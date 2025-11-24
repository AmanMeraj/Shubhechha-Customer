package com.subh.shubhechha.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.search.SearchView;
import com.subh.shubhechha.Adapters.ShopHorizontalCategoryAdapter;
import com.subh.shubhechha.Adapters.ShopItemAdapter;
import com.subh.shubhechha.Model.ShopItemResponse;
import com.subh.shubhechha.ViewModel.ViewModel;
import com.subh.shubhechha.databinding.ActivityItemsBinding;
import com.subh.shubhechha.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class ItemsActivity extends Utility {
    private ActivityItemsBinding binding;
    private ShopHorizontalCategoryAdapter horizontalAdapter;
    private ShopItemAdapter shopItemAdapter;
    private ShopItemAdapter searchAdapter;
    private ViewModel viewModel;

    private String currentCategoryName = "All Shops";
    private String shopName = "Shops";
    private String shopId;
    private String currentMenuId = "";
    private String authorization;
    private String longitude;
    private String latitude;
    private boolean isInitialLoad = true;

    private List<ShopItemResponse.Datum> filteredItemList;

    // Filter and Sort parameters
    private List<String> selectedFilterBy = new ArrayList<>();
    private String selectedSortBy = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViewModel();
        initializeViews();
        setupListeners();
        setupSearchBar();
        setupRecyclerViews();
        setupSearchRecyclerView();
        loadInitialData();
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
    }

    private void initializeViews() {
        // Get data from intent
        shopName = getIntent().getStringExtra("SHOP_NAME");
        shopId = getIntent().getStringExtra("SHOP_ID");

        if (currentCategoryName == null) {
            currentCategoryName = "All Category";
        }

        // Get stored auth token and location
        authorization = getAuthToken();
        longitude = getLongitude();
        latitude = getLatitude();

        // Set the shop name (not category name) to both titles
        binding.tvNotificationExpanded.setText(shopName);
        binding.tvToolbarTitle.setText(shopName);
        binding.tvCategory.setText(currentCategoryName);
    }

    private void setupListeners() {
        // Back button listener
        binding.backBtn.setOnClickListener(v -> onBackPressed());

        // Cart button listener
        binding.cart.setOnClickListener(v -> {
            Toast.makeText(this, "Cart clicked", Toast.LENGTH_SHORT).show();
        });

        // Search button listeners - open search
        binding.searchBtn.setOnClickListener(v -> openSearch());
        binding.searchBtnToolBar.setOnClickListener(v -> openSearch());

        // Filter button listeners
        binding.filterBtn.setOnClickListener(v -> handleFilterClick());
        binding.filterBtnToolBar.setOnClickListener(v -> handleFilterClick());

        // AppBar collapse listener for toolbar animations
        setupCollapsingToolbar();
    }

    private void setupSearchBar() {
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
                    filterItems(query);
                    binding.searchResultsRecycler.setVisibility(View.VISIBLE);
                } else {
                    // Show all items when search is cleared
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
                    filterItems(query);
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
                // Show all items initially when search opens
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

                // Calculate the percentage of collapse
                float percentage = Math.abs(verticalOffset) / (float) scrollRange;

                // Fade in toolbar title when collapsed (starts fading at 60% collapse)
                if (percentage > 0.6f) {
                    float alpha = (percentage - 0.6f) * 2.5f;
                    binding.tvToolbarTitle.setAlpha(alpha);
                    binding.searchBtnToolBar.setAlpha(alpha);
                    binding.filterBtnToolBar.setAlpha(alpha);
                } else {
                    binding.tvToolbarTitle.setAlpha(0f);
                    binding.searchBtnToolBar.setAlpha(0f);
                    binding.filterBtnToolBar.setAlpha(0f);
                }

                // Fade out expanded title
                binding.tvNotificationExpanded.setAlpha(1 - percentage);
                binding.searchBtn.setAlpha(1 - percentage);

                // Show/hide based on collapse state
                if (scrollRange + verticalOffset == 0) {
                    binding.tvToolbarTitle.setVisibility(View.VISIBLE);
                    binding.searchBtnToolBar.setVisibility(View.VISIBLE);
                    binding.filterBtnToolBar.setVisibility(View.VISIBLE);
                    isShow = true;
                } else if (isShow) {
                    binding.tvToolbarTitle.setVisibility(View.VISIBLE);
                    binding.searchBtnToolBar.setVisibility(View.VISIBLE);
                    binding.filterBtnToolBar.setVisibility(View.VISIBLE);
                    isShow = false;
                }
            }
        });
    }

    private void setupRecyclerViews() {
        setupFeaturedCategoriesRecyclerView();
        setupShopItemsRecyclerView();
    }

    private void setupFeaturedCategoriesRecyclerView() {
        horizontalAdapter = new ShopHorizontalCategoryAdapter();
        horizontalAdapter.setOnCategoryClickListener((menu, position) -> {
            onCategorySelected(menu);
        });

        binding.rcCategory.setAdapter(horizontalAdapter);
        binding.rcCategory.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        binding.rcCategory.setNestedScrollingEnabled(false);
    }

    private void setupShopItemsRecyclerView() {
        shopItemAdapter = new ShopItemAdapter();

        shopItemAdapter.setOnItemClickListener((item, position) -> {
            Toast.makeText(this, "Clicked: " + item.getName(), Toast.LENGTH_SHORT).show();
        });

        shopItemAdapter.setOnQuantityChangeListener((item, newQuantity, position) -> {
            Toast.makeText(this,
                    item.getName() + " quantity: " + newQuantity,
                    Toast.LENGTH_SHORT).show();
        });

        binding.rcNotification.setAdapter(shopItemAdapter);
        binding.rcNotification.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupSearchRecyclerView() {
        LinearLayoutManager searchLayoutManager = new LinearLayoutManager(this);
        binding.searchResultsRecycler.setLayoutManager(searchLayoutManager);
        binding.searchResultsRecycler.setHasFixedSize(true);

        filteredItemList = new ArrayList<>();
        searchAdapter = new ShopItemAdapter();
        binding.searchResultsRecycler.setAdapter(searchAdapter);

        searchAdapter.setOnItemClickListener((item, position) -> {
            // Close search and handle item click
            closeSearch();
            Toast.makeText(this, "Clicked: " + item.getName(), Toast.LENGTH_SHORT).show();
        });

        searchAdapter.setOnQuantityChangeListener((item, newQuantity, position) -> {
            Toast.makeText(this,
                    item.getName() + " quantity: " + newQuantity,
                    Toast.LENGTH_SHORT).show();
        });

        binding.searchResultsRecycler.setVisibility(View.GONE);
    }

    private void loadInitialData() {
        showProgress();
        loadShopItems(shopId, currentMenuId, selectedFilterBy, selectedSortBy);
    }

    private void loadShopItems(String shopId, String menuId, List<String> filterBy, String sortBy) {
        // Ensure filterBy and sortBy are not null
        List<String> finalFilterBy = (filterBy != null && !filterBy.isEmpty()) ? filterBy : new ArrayList<>();
        String finalSortBy = (sortBy != null && !sortBy.isEmpty()) ? sortBy : "";

        viewModel.getShopItems(
                authorization,
                longitude,
                latitude,
                shopId,
                menuId,
                finalFilterBy,
                finalSortBy
        ).observe(this, response -> {
            hideProgress();

            if (response != null) {
                if (response.data.status == 1) {
                    handleSuccessResponse(response.data);
                } else {
                    showError(response.message != null ? response.message : "Error loading items");
                    updateEmptyState();
                }
            }
        });
    }

    private void handleSuccessResponse(ShopItemResponse response) {
        // Update menus/categories
        if (response.data.getMenus() != null && !response.data.getMenus().isEmpty()) {
            boolean menusAlreadyLoaded = horizontalAdapter.getItemCount() > 0;

            if (!menusAlreadyLoaded) {
                horizontalAdapter.updateMenus(response.data.getMenus());
                horizontalAdapter.setSelectedPosition(0);
                currentCategoryName = response.data.getMenus().get(0).getName();
                currentMenuId = String.valueOf(response.data.getMenus().get(0).getId());
                binding.tvCategory.setText(currentCategoryName);
            }

            binding.rcCategory.setVisibility(View.VISIBLE);
        } else {
            binding.rcCategory.setVisibility(View.GONE);
        }

        // Update shop items
        if (response.data.getItems() != null && !response.data.getItems().data.isEmpty()) {
            shopItemAdapter.setShopItems(response.data.getItems().data);
        } else {
            shopItemAdapter.setShopItems(new ArrayList<>());
        }

        updateEmptyState();
    }

    private void onCategorySelected(ShopItemResponse.Menu menu) {
        currentCategoryName = menu.getName();
        currentMenuId = String.valueOf(menu.getId());

        // Update only the category text, NOT the toolbar title
        binding.tvCategory.setText(currentCategoryName);

        // Expand the AppBarLayout to show category change
        binding.appBarLayout.setExpanded(true, true);

        // Load items for selected menu with current filters
        showProgress();
        loadShopItems(shopId, currentMenuId, selectedFilterBy, selectedSortBy);
    }

    private void filterItems(String query) {
        String searchQuery = query.toLowerCase().trim();

        filteredItemList.clear();
        List<ShopItemResponse.Datum> currentItems = shopItemAdapter.getShopItems();

        if (currentItems != null) {
            for (ShopItemResponse.Datum item : currentItems) {
                if (item.getName() != null &&
                        item.getName().toLowerCase().contains(searchQuery)) {
                    filteredItemList.add(item);
                }
            }
        }

        searchAdapter.setShopItems(filteredItemList);

        // Show/hide RecyclerView based on results
        if (filteredItemList.isEmpty()) {
            binding.searchResultsRecycler.setVisibility(View.GONE);
        } else {
            binding.searchResultsRecycler.setVisibility(View.VISIBLE);
        }
    }

    private void resetSearchFilter() {
        filteredItemList.clear();
        List<ShopItemResponse.Datum> currentItems = shopItemAdapter.getShopItems();

        if (currentItems != null) {
            filteredItemList.addAll(currentItems);
        }

        searchAdapter.setShopItems(filteredItemList);

        if (!filteredItemList.isEmpty()) {
            binding.searchResultsRecycler.setVisibility(View.VISIBLE);
        }
    }

    private void showFilterBottomSheet() {
        FilterBottomSheet bottomSheet = new FilterBottomSheet();

        // Pass current selections to the bottom sheet if needed
        // You can add methods to FilterBottomSheet to set initial values

        bottomSheet.setFilterListener((filterBy, sortBy) -> {
            // Update the filter and sort parameters
            if(filterBy != null){
                selectedFilterBy.add(filterBy);
            }else{
                selectedFilterBy.clear();
            }
            selectedSortBy = sortBy != null ? sortBy : "";

            // Log the selections
            String filterString = selectedFilterBy.isEmpty() ? "None" : selectedFilterBy.toString();
            String sortString = selectedSortBy.isEmpty() ? "None" : selectedSortBy;

            Toast.makeText(this,
                    "Filter: " + filterString + "\nSort: " + sortString,
                    Toast.LENGTH_SHORT).show();

            // Apply the filters
            applyFiltersAndSort();
        });

        bottomSheet.show(getSupportFragmentManager(), "FilterBottomSheet");
    }

    private void applyFiltersAndSort() {
        // Reload items with new filter/sort parameters
        showProgress();
        loadShopItems(shopId, currentMenuId, selectedFilterBy, selectedSortBy);
    }

    public void clearFilters() {
        selectedFilterBy.clear();
        selectedSortBy = "";

        Toast.makeText(this, "Filters cleared", Toast.LENGTH_SHORT).show();

        // Reload items without filters
        applyFiltersAndSort();
    }

    private void handleFilterClick() {
        showFilterBottomSheet();
    }

    private void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.rcNotification.setVisibility(View.GONE);
        binding.emptyStateLayout.setVisibility(View.GONE);
    }

    private void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
    }

    private void updateEmptyState() {
        List<ShopItemResponse.Datum> items = shopItemAdapter.getShopItems();

        if (items == null || items.isEmpty()) {
            binding.emptyStateLayout.setVisibility(View.VISIBLE);
            binding.rcNotification.setVisibility(View.GONE);
        } else {
            binding.emptyStateLayout.setVisibility(View.GONE);
            binding.rcNotification.setVisibility(View.VISIBLE);
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String getAuthToken() {
        return "Bearer " + pref.getPrefString(this, pref.user_token);
    }

    private String getLongitude() {
        return pref.getPrefString(this, pref.user_long);
    }

    private String getLatitude() {
        return pref.getPrefString(this, pref.user_lat);
    }

    // Getters for current filter/sort state
    public List<String> getSelectedFilterBy() {
        return selectedFilterBy;
    }

    public String getSelectedSortBy() {
        return selectedSortBy;
    }

    public boolean hasActiveFilters() {
        return !selectedFilterBy.isEmpty() || !selectedSortBy.isEmpty();
    }

    @Override
    public void onBackPressed() {
        // Close search if it's open
        if (binding.searchView.getVisibility() == View.VISIBLE) {
            closeSearch();
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}