package com.subh.shubhechha.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.search.SearchView;
import com.google.android.material.snackbar.Snackbar;
import com.subh.shubhechha.Adapters.ShopGridAdapter;
import com.subh.shubhechha.Model.ShopResponse;
import com.subh.shubhechha.R;
import com.subh.shubhechha.ViewModel.ViewModel;
import com.subh.shubhechha.databinding.ActivityShopsBinding;
import com.subh.shubhechha.utils.SharedPref;
import java.util.ArrayList;
import java.util.List;

public class ShopsActivity extends AppCompatActivity {

    private static final String TAG = "ShopsActivity";
    private static final int MAX_RETRY_COUNT = 3;
    private static final long RETRY_DELAY_MS = 2000;

    private ActivityShopsBinding binding;
    private ShopGridAdapter adapter;
    private ShopGridAdapter searchAdapter;
    private List<ShopResponse.Shop> shopList;
    private ViewModel viewModel;
    private SharedPref pref = new SharedPref();

    private String categoryName = "Category Name";
    private int moduleId = 0;
    private String authToken;
    private String latitude;
    private String longitude;

    private int retryCount = 0;
    private boolean isLoading = false;
    private Handler retryHandler = new Handler();

    private List<ShopResponse.Shop> filteredShopList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityShopsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        if (!extractIntentData()) {
            showErrorAndFinish("Missing required data");
            return;
        }

        // Load user data - doesn't require login, just gets available data
        loadUserData();

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupSearchBar();
        setupCollapsingToolbar();
        setupRecyclerView();
        setupSearchRecyclerView();
        setupObservers();
        loadShops();
    }

    private boolean extractIntentData() {
        Intent intent = getIntent();
        if (intent == null) {
            return false;
        }

        categoryName = intent.getStringExtra("CATEGORY_NAME");
        moduleId = intent.getIntExtra("MODULE_ID", 0);

        Log.d(TAG, "Category: " + categoryName + ", Module ID: " + moduleId);
        return categoryName != null && !categoryName.isEmpty();
    }

    private void loadUserData() {
        try {
            // Get token - use empty Bearer for guest users
            String token = pref.getPrefString(this, pref.user_token);
            if (token != null && !token.isEmpty()) {
                authToken = token.startsWith("Bearer ") ? token : "Bearer " + token;
                Log.d(TAG, "Loading as logged-in user");
            } else {
                authToken = "Bearer "; // Empty bearer for guest access
                Log.d(TAG, "Loading as guest user");
            }

            // Get location
            latitude = pref.getPrefString(this, pref.user_lat);
            longitude = pref.getPrefString(this, pref.user_long);

            // Use default values if location not available
            if (latitude == null || latitude.isEmpty()) {
                latitude = "0.0";
                Log.w(TAG, "Latitude not available, using default");
            }
            if (longitude == null || longitude.isEmpty()) {
                longitude = "0.0";
                Log.w(TAG, "Longitude not available, using default");
            }

            Log.d(TAG, "Location: " + latitude + ", " + longitude);

        } catch (Exception e) {
            Log.e(TAG, "Error loading user data: " + e.getMessage());
            // Set defaults on error
            authToken = "Bearer ";
            latitude = "0.0";
            longitude = "0.0";
        }
    }

    private void initViews() {
        binding.tvNotificationExpanded.setText(categoryName);
        binding.tvToolbarTitle.setText(categoryName);
        binding.backBtn.setOnClickListener(v -> onBackPressed());

        // Set click listeners for search buttons to open search
        binding.searchBtn.setOnClickListener(v -> openSearch());
        binding.searchBtnToolBar.setOnClickListener(v -> openSearch());
    }

    private void setupSearchBar() {
        // Setup SearchBar hint

        // Setup SearchView text listener
        binding.searchView.getEditText().addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();

                // Filter immediately as user types
                if (query.length() > 0) {
                    filterShops(query);
                    binding.searchResultsRecycler.setVisibility(View.VISIBLE);
                } else {
                    // Show all shops when search is cleared
                    resetSearchFilter();
                    binding.searchResultsRecycler.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // Not needed
            }
        });

        // Handle SearchView editor action (keyboard search button)
        binding.searchView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                String query = binding.searchView.getText().toString().trim();
                if (query.length() > 0) {
                    filterShops(query);
                }
                // Hide keyboard
                android.view.inputmethod.InputMethodManager imm =
                        (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });

        // Listen for SearchView visibility changes
        binding.searchView.addTransitionListener((searchView, previousState, newState) -> {
            if (newState == SearchView.TransitionState.HIDDEN) {
                // Hide SearchView and reset filter when closed
                binding.searchView.setVisibility(View.GONE);
                binding.searchView.setText("");
                resetSearchFilter();
                binding.searchResultsRecycler.setVisibility(View.GONE);
            } else if (newState == SearchView.TransitionState.SHOWN) {
                // Show all shops initially when search opens
                resetSearchFilter();
            }
        });
    }

    private void openSearch() {
        // Make SearchView visible and show it
        binding.searchView.setVisibility(View.VISIBLE);
        binding.searchView.show();

        // Optional: Request focus on search input
        binding.searchView.requestFocusAndShowKeyboard();
    }

    private void closeSearch() {
        // Hide the SearchView
        binding.searchView.hide();

        // Clear search text
        binding.searchView.setText("");

        // Reset filter
        resetSearchFilter();

        // Hide results
        binding.searchResultsRecycler.setVisibility(View.GONE);
    }

    private void setupCollapsingToolbar() {
        binding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                float percentage = Math.abs(verticalOffset) / (float) scrollRange;

                // Handle toolbar title and search button visibility (fade in/out)
                if (percentage > 0.6f) {
                    float alpha = (percentage - 0.6f) * 2.5f;
                    binding.tvToolbarTitle.setAlpha(Math.min(alpha, 1.0f));
                } else {
                    binding.tvToolbarTitle.setAlpha(0f);
                }

                // Handle expanded header visibility
                binding.tvNotificationExpanded.setAlpha(1 - percentage);

                if (scrollRange + verticalOffset == 0) {
                    binding.tvToolbarTitle.setVisibility(View.VISIBLE);
                    isShow = true;
                } else if (isShow) {
                    binding.tvToolbarTitle.setVisibility(View.VISIBLE);
                    isShow = false;
                }
            }
        });
    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.rcNotification.setLayoutManager(layoutManager);
        binding.rcNotification.setHasFixedSize(true);

        shopList = new ArrayList<>();
        adapter = new ShopGridAdapter(shopList);
        binding.rcNotification.setAdapter(adapter);

        adapter.setOnShopClickListener(this::onShopItemClick);
    }

    private void setupSearchRecyclerView() {
        GridLayoutManager searchLayoutManager = new GridLayoutManager(this, 2);
        binding.searchResultsRecycler.setLayoutManager(searchLayoutManager);
        binding.searchResultsRecycler.setHasFixedSize(true);

        filteredShopList = new ArrayList<>();
        searchAdapter = new ShopGridAdapter(filteredShopList);
        binding.searchResultsRecycler.setAdapter(searchAdapter);

        searchAdapter.setOnShopClickListener((shop, position) -> {
            // Close search and open shop
            closeSearch();
            onShopItemClick(shop, position);
        });

        binding.searchResultsRecycler.setVisibility(View.GONE);
    }

    private void setupObservers() {
        viewModel.shops(authToken, longitude, latitude, moduleId).observe(this, response -> {
            Log.d(TAG, "Response received: " + (response != null));

            if (response == null) {
                handleError("No response from server", null);
                return;
            }

            Log.d(TAG, "Response status: " + response.data.status);

            if (response.data.status == 1) {
                handleSuccess(response.data.data);
            } else {
                String errorMsg = response.message != null ? response.message : "Failed to load shops";
                handleError(errorMsg, null);
            }
        });
    }

    private void loadShops() {
        if (isLoading) {
            Log.d(TAG, "Already loading shops");
            return;
        }

        isLoading = true;
        showLoading();
        retryCount = 0;

        Log.d(TAG, "Loading shops with moduleId: " + moduleId);
    }

    private void handleSuccess(ShopResponse.Data data) {
        isLoading = false;
        hideLoading();
        retryCount = 0;

        if (data == null || data.getShops() == null) {
            Log.w(TAG, "No shop data in response");
            showEmptyState("No shops available");
            return;
        }

        ArrayList<ShopResponse.Shop> shops = data.getShops();
        Log.d(TAG, "Received " + shops.size() + " shops");

        shopList.clear();

        if (shops.isEmpty()) {
            showEmptyState("No shops found in this category");
        } else {
            shopList.addAll(shops);
            showData();
            adapter.notifyDataSetChanged();
            Log.d(TAG, "Successfully loaded " + shopList.size() + " shops");
        }
    }

    private void handleError(String message, Throwable throwable) {
        isLoading = false;
        hideLoading();

        Log.e(TAG, "Error loading shops: " + message, throwable);

        if (shouldRetry(throwable)) {
            retryLoadShops();
        } else {
            if (shopList.isEmpty()) {
                showErrorState(message != null ? message : "Failed to load shops");
            } else {
                showSnackBar(message != null ? message : "Failed to refresh shops", false);
            }
        }
    }

    private boolean shouldRetry(Throwable throwable) {
        if (retryCount >= MAX_RETRY_COUNT) {
            return false;
        }

        if (throwable != null) {
            String errorMsg = throwable.getMessage();
            return errorMsg != null && (
                    errorMsg.contains("timeout") ||
                            errorMsg.contains("network") ||
                            errorMsg.contains("connection")
            );
        }

        return false;
    }

    private void retryLoadShops() {
        retryCount++;
        showSnackBar("Retrying... (" + retryCount + "/" + MAX_RETRY_COUNT + ")", false);

        retryHandler.postDelayed(() -> {
            if (!isFinishing() && !isDestroyed()) {
                loadShops();
            }
        }, RETRY_DELAY_MS);
    }

    private void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.rcNotification.setVisibility(View.GONE);
        binding.emptyStateLayout.setVisibility(View.GONE);
    }

    private void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
    }

    private void showData() {
        binding.progressBar.setVisibility(View.GONE);
        binding.emptyStateLayout.setVisibility(View.GONE);
        binding.rcNotification.setVisibility(View.VISIBLE);
    }

    private void showEmptyState(String message) {
        binding.progressBar.setVisibility(View.GONE);
        binding.rcNotification.setVisibility(View.GONE);
        binding.emptyStateLayout.setVisibility(View.VISIBLE);
        Log.d(TAG, "Showing empty state: " + message);
    }

    private void showErrorState(String message) {
        binding.progressBar.setVisibility(View.GONE);
        binding.rcNotification.setVisibility(View.GONE);
        binding.emptyStateLayout.setVisibility(View.VISIBLE);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Error")
                .setMessage(message + "\n\nWould you like to retry?")
                .setPositiveButton("Retry", (dialog, which) -> {
                    retryCount = 0;
                    loadShops();
                })
                .setNegativeButton("Cancel", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void onShopItemClick(ShopResponse.Shop shop, int position) {
        if (shop == null) {
            showSnackBar("Invalid shop selected", false);
            return;
        }

        Log.d(TAG, "Shop clicked: " + shop.getName() + " (ID: " + shop.getId() + ")");
        Toast.makeText(this, "Opening: " + shop.getName(), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> {
            try {
                Intent intent = new Intent(ShopsActivity.this, ItemsActivity.class);
                intent.putExtra("SHOP_NAME", shop.getName());
                intent.putExtra("SHOP_ID", String.valueOf(shop.getId()));
                intent.putExtra("SHOP_IMAGE", shop.getImage_path());
                intent.putExtra("SHOP_STATUS", shop.getActive_status());
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } catch (Exception e) {
                Log.e(TAG, "Error opening ItemsActivity: " + e.getMessage());
                showSnackBar("Error opening shop", false);
            }
        }, 400);
    }

    public void refreshShops() {
        if (!isLoading) {
            shopList.clear();
            adapter.notifyDataSetChanged();
            retryCount = 0;
            loadShops();
        }
    }

    private void filterShops(String query) {
        String searchQuery = query.toLowerCase().trim();
        Log.d(TAG, "Filtering shops with query: " + searchQuery);

        filteredShopList.clear();

        for (ShopResponse.Shop shop : shopList) {
            if (shop.getName() != null &&
                    shop.getName().toLowerCase().contains(searchQuery)) {
                filteredShopList.add(shop);
            }
        }

        searchAdapter.notifyDataSetChanged();
        Log.d(TAG, "Filtered results: " + filteredShopList.size() + " shops");

        // Show/hide RecyclerView based on results
        if (filteredShopList.isEmpty()) {
            binding.searchResultsRecycler.setVisibility(View.GONE);
            // You could show a "No results" message here if needed
        } else {
            binding.searchResultsRecycler.setVisibility(View.VISIBLE);
        }
    }

    private void resetSearchFilter() {
        Log.d(TAG, "Resetting search filter - showing all shops");
        filteredShopList.clear();
        filteredShopList.addAll(shopList);
        searchAdapter.notifyDataSetChanged();

        if (!filteredShopList.isEmpty()) {
            binding.searchResultsRecycler.setVisibility(View.VISIBLE);
        }
    }

    private void showSnackBar(String message, boolean success) {
        if (binding == null || binding.getRoot() == null) {
            return;
        }

        try {
            Snackbar snackbar = Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT);
            View snackView = snackbar.getView();
            int bgColor = success ? getColor(R.color.success_green) : getColor(R.color.error_red);
            snackView.setBackgroundColor(bgColor);
            snackbar.setTextColor(getColor(R.color.white));
            snackbar.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing snackbar: " + e.getMessage());
        }
    }

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (retryHandler != null) {
            retryHandler.removeCallbacksAndMessages(null);
        }

        if (viewModel != null) {
            viewModel.shops(authToken, longitude, latitude, moduleId).removeObservers(this);
        }

        binding = null;
    }

    @Override
    public void onBackPressed() {
        // Close search if it's open
        if (binding.searchView.getVisibility() == View.VISIBLE) {
            closeSearch();
            return;
        }

        if (isLoading) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Loading")
                    .setMessage("Data is still loading. Do you want to go back?")
                    .setPositiveButton("Yes", (dialog, which) -> super.onBackPressed())
                    .setNegativeButton("Wait", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }
}