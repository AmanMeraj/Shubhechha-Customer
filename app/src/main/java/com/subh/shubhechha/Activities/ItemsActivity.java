package com.subh.shubhechha.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.search.SearchView;
import com.subh.shubhechha.Adapters.ShopHorizontalCategoryAdapter;
import com.subh.shubhechha.Adapters.ShopItemAdapter;
import com.subh.shubhechha.Model.AddToCartModel;
import com.subh.shubhechha.Model.CartResponse;
import com.subh.shubhechha.Model.ShopItemResponse;
import com.subh.shubhechha.ViewModel.ViewModel;
import com.subh.shubhechha.databinding.ActivityItemsBinding;
import com.subh.shubhechha.utils.AuthHelper;
import com.subh.shubhechha.utils.SharedPref;
import com.subh.shubhechha.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemsActivity extends Utility {
    private ActivityItemsBinding binding;
    private ShopHorizontalCategoryAdapter horizontalAdapter;
    private ShopItemAdapter shopItemAdapter;
    private AuthHelper authHelper;
    private double minimumOrderAmount = 0.0;
    private String currencySymbol = "₹";
    private ShopItemAdapter searchAdapter;
    private ViewModel viewModel;
    private int apiCartItemCount = 0;
    private double apiCartSubtotal = 0.0;

    private String currentCategoryName = "All Shops";
    private String shopName = "Shops";
    private String shopId;
    private String currentMenuId = "";
    private String authorization;
    private String selectedFilterBy = null;  // Will be "0" for Veg, "1" for Non-Veg, or null
    private String selectedSortBy = null;
    private String longitude;
    private String latitude;

    private List<ShopItemResponse.Datum> filteredItemList;

    // Cart management
    private Map<String, CartItem> cartItems = new HashMap<>();
    private boolean isCartVisible = false;
    private String currentCartShopId = null;
    private boolean isCartOperationInProgress = false;

    // Track if menus are loaded
    private boolean menusLoaded = false;

    // Cart item class
    private static class CartItem {
        String itemId;
        String itemName;
        int quantity;
        double price;
        String shopId;

        CartItem(String itemId, String itemName, int quantity, double price, String shopId) {
            this.itemId = itemId;
            this.itemName = itemName;
            this.quantity = quantity;
            this.price = price;
            this.shopId = shopId;
        }
    }

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
        setupBottomCart();
        fetchCartData();
        loadInitialData(); // This will now wait for menus to load first
        fetchCartData();
        loadInitialData();
        updateCartBadge();
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        authHelper = new AuthHelper();
    }

    private void initializeViews() {
        shopName = getIntent().getStringExtra("SHOP_NAME");
        shopId = getIntent().getStringExtra("SHOP_ID");

        if (currentCategoryName == null) {
            currentCategoryName = "All Category";
        }

        authorization = getAuthToken();
        longitude = getLongitude();
        latitude = getLatitude();

        binding.tvNotificationExpanded.setText(shopName);
        binding.tvToolbarTitle.setText(shopName);
        binding.tvCategory.setText(currentCategoryName);
    }

    private void setupBottomCart() {
        binding.bottomCartCard.setVisibility(View.GONE);
        binding.bottomCartCard.setTranslationY(200f);

        binding.checkoutBtn.setOnClickListener(v -> {
            // Check login first
            if (!authHelper.checkLoginAndExecute(this, () -> {
                // Check minimum order amount
                if (minimumOrderAmount > 0 && apiCartSubtotal < minimumOrderAmount) {
                    double remaining = minimumOrderAmount - apiCartSubtotal;
                    String message = String.format("Add items worth %s %.2f more to place order",
                            currencySymbol, remaining);
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    return;
                }

                // Proceed to checkout
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
            })) {
                // User not logged in, dialog shown
            }
        });
    }

    private void setupSearchBar() {
        binding.searchView.getEditText().addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();

                if (query.length() > 0) {
                    filterItems(query);
                    binding.searchResultsRecycler.setVisibility(View.VISIBLE);
                } else {
                    resetSearchFilter();
                    binding.searchResultsRecycler.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        binding.searchView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                String query = binding.searchView.getText().toString().trim();
                if (query.length() > 0) {
                    filterItems(query);
                }
                android.view.inputmethod.InputMethodManager imm =
                        (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });

        binding.searchView.addTransitionListener((searchView, previousState, newState) -> {
            if (newState == SearchView.TransitionState.HIDDEN) {
                binding.searchView.setVisibility(View.GONE);
                binding.searchView.setText("");
                resetSearchFilter();
                binding.searchResultsRecycler.setVisibility(View.GONE);
            } else if (newState == SearchView.TransitionState.SHOWN) {
                resetSearchFilter();
            }
        });
    }

    private void openSearch() {
        binding.searchView.setVisibility(View.VISIBLE);
        binding.searchView.show();
        binding.searchView.requestFocusAndShowKeyboard();
    }

    private void closeSearch() {
        binding.searchView.hide();
        binding.searchView.setText("");
        resetSearchFilter();
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

                binding.tvNotificationExpanded.setAlpha(1 - percentage);
                binding.searchBtn.setAlpha(1 - percentage);

                if (scrollRange + verticalOffset == 0) {
                    binding.tvToolbarTitle.setVisibility(View.VISIBLE);
                    binding.searchBtnToolBar.setVisibility(View.GONE);
                    binding.filterBtnToolBar.setVisibility(View.VISIBLE);
                    isShow = true;
                } else if (isShow) {
                    binding.tvToolbarTitle.setVisibility(View.VISIBLE);
                    binding.searchBtnToolBar.setVisibility(View.GONE);
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
            handleQuantityChange(item, newQuantity);
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
            closeSearch();
            Toast.makeText(this, "Clicked: " + item.getName(), Toast.LENGTH_SHORT).show();
        });

        searchAdapter.setOnQuantityChangeListener((item, newQuantity, position) -> {
            handleQuantityChange(item, newQuantity);
        });

        binding.searchResultsRecycler.setVisibility(View.GONE);
    }


    private void fetchCartData() {
        viewModel.getCart(authorization).observe(this, response -> {
            if (response != null && response.data != null && response.data.getStatus() == 1) {
                CartResponse.Data cartData = response.data.getData();

                if (cartData != null && cartData.getCart() != null) {
                    CartResponse.Cart cart = cartData.getCart();

                    if (cart.getShop_id() != 0) {
                        currentCartShopId = String.valueOf(cart.getShop_id());
                    }

                    // ✅ STORE cart count and subtotal from API
                    apiCartItemCount = cartData.getCart_item_count();
                    apiCartSubtotal = Double.parseDouble(String.valueOf(cartData.getSub_total()));

                    pref.setPrefInteger(this, pref.cart_count, apiCartItemCount);

                    if (cart.getCart_items() != null && !cart.getCart_items().isEmpty()) {
                        loadCartItemsFromResponse(cart.getCart_items());
                    } else {
                        // Cart is empty
                        cartItems.clear();
                        currentCartShopId = null;
                        apiCartItemCount = 0;
                        apiCartSubtotal = 0.0;
                        updateCartUI();
                        syncAdapterWithCart(); // ✅ ADD THIS - Sync when cart is empty
                    }

                    updateCartBadge();
                }
            } else {
                // Clear cart if API returns error or empty cart
                cartItems.clear();
                currentCartShopId = null;
                apiCartItemCount = 0;
                apiCartSubtotal = 0.0;
                pref.setPrefInteger(this, pref.cart_count, 0);
                updateCartUI();
                syncAdapterWithCart(); // ✅ ADD THIS - Sync when clearing cart
                updateCartBadge();
            }
        });
    }

    private void loadCartItemsFromResponse(ArrayList<CartResponse.CartItem> apiCartItems) {
        cartItems.clear();

        for (CartResponse.CartItem apiItem : apiCartItems) {
            if (apiItem.getItem() != null) {
                String itemId = String.valueOf(apiItem.getItem_id());
                String itemName = apiItem.getItem().getName();
                int quantity = 0;

                try {
                    quantity = Integer.parseInt(apiItem.getQuantity());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                double price = 0.0;
                try {
                    String offerPrice = apiItem.getItem().getOffer_price();
                    String originalPrice = apiItem.getItem().getAmount();

                    if (offerPrice != null && !offerPrice.isEmpty()
                            && !offerPrice.equals("0") && !offerPrice.equals("0.00")) {
                        price = Double.parseDouble(offerPrice);
                    } else {
                        price = Double.parseDouble(originalPrice);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                String itemShopId = String.valueOf(apiItem.getShop_id());

                cartItems.put(itemId, new CartItem(itemId, itemName, quantity, price, itemShopId));
            }
        }

        updateCartUI();
        syncAdapterWithCart(); // ✅ This was already here, which is correct
    }
    private void syncAdapterWithCart() {
        Map<String, Integer> quantities = new HashMap<>();
        for (Map.Entry<String, CartItem> entry : cartItems.entrySet()) {
            quantities.put(entry.getKey(), entry.getValue().quantity);
        }

        shopItemAdapter.setItemQuantities(quantities);
        searchAdapter.setItemQuantities(quantities);
    }

    private void clearCartCompletely(ShopItemResponse.Datum item) {
        isCartOperationInProgress = true;
        showProgress();

        AddToCartModel model = new AddToCartModel();
        model.setItem_id(String.valueOf(item.getId()));

        CartItem cartItem = cartItems.get(String.valueOf(item.getId()));
        if (cartItem != null) {
            model.setQuantity(String.valueOf(cartItem.quantity));
        } else {
            model.setQuantity("1");
        }

        model.setType("remove");
        model.setAdded_modifier_id(new ArrayList<>());

        viewModel.updateCart(authorization, model).observe(this, response -> {
            isCartOperationInProgress = false;
            hideProgress();

            // ✅ CLEAR CART REGARDLESS OF RESPONSE
            cartItems.clear();
            currentCartShopId = null;
            pref.setPrefInteger(this, pref.cart_count, 0);

            hideCartWithAnimation();
            updateCartUI();
            syncAdapterWithCart();
            refreshCurrentCategory();

            Toast.makeText(this, "Cart cleared", Toast.LENGTH_SHORT).show();
        });
    }
    private void handleQuantityChange(ShopItemResponse.Datum item, int newQuantity) {
        // ✅ CHECK LOGIN FIRST before any cart operation
        if (!authHelper.isUserLoggedIn(this)) {
            // Show login dialog - callback will be executed after user returns from LoginActivity
            authHelper.showLoginRequiredDialog(this, null);
            // Reset the UI to previous state
            syncAdapterWithCart();
            return;
        }

        if (isCartOperationInProgress) {
            Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT).show();
            return;
        }

        String itemId = String.valueOf(item.getId());

        // Check if this is a new shop
        if (currentCartShopId != null && !currentCartShopId.equals(shopId) && newQuantity > 0) {
            showDifferentShopDialog(item, newQuantity);
            return;
        }

        double price = getItemPrice(item);

        // Get current quantity from cart
        int currentQuantity = 0;
        if (cartItems.containsKey(itemId)) {
            currentQuantity = cartItems.get(itemId).quantity;
        }

        // Determine if increasing or decreasing
        boolean isIncreasing = newQuantity > currentQuantity;
        boolean itemExistsInCart = cartItems.containsKey(itemId);

        if (newQuantity > 0) {
            if (itemExistsInCart) {
                updateCartItem(item, 1, price, false, isIncreasing);
            } else {
                addCartItem(item, newQuantity, price, false);
            }
        } else {
            // Check if this is the last item in cart
            if (itemExistsInCart) {
                CartItem cartItem = cartItems.get(itemId);
                boolean isLastItem = (cartItem.quantity == 1 && cartItems.size() == 1);
                if (isLastItem) {
                    clearCartCompletely(item);
                } else {
                    removeCartItem(item);
                }
            }
        }
    }

    // Also add login check to the cart button click listener
// Modify setupListeners() method - update the cart button click listener
    private void setupListeners() {
        binding.backBtn.setOnClickListener(v -> onBackPressed());

        // ✅ MODIFIED: Check login before navigating to cart
        binding.cart.setOnClickListener(v -> {
            if (authHelper.checkLoginAndExecute(this, () -> {
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
            })) {
                // User is logged in, action already executed
            }
        });

        binding.searchBtn.setOnClickListener(v -> openSearch());
        binding.searchBtnToolBar.setOnClickListener(v -> openSearch());
        binding.filterBtn.setOnClickListener(v -> handleFilterClick());
        binding.filterBtnToolBar.setOnClickListener(v -> handleFilterClick());
        setupCollapsingToolbar();
    }

    private void showDifferentShopDialog(ShopItemResponse.Datum item, int newQuantity) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Replace cart item?")
                .setMessage("Your cart contains items from different shop. Do you want to discard the selection and add this item?")
                .setPositiveButton("Yes, Replace", (dialog, which) -> {
                    double price = getItemPrice(item);
                    addCartItem(item, newQuantity, price, true);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    syncAdapterWithCart();
                })
                .setCancelable(false)
                .show();
    }

    private void addCartItem(ShopItemResponse.Datum item, int quantity, double price, boolean forceAdd) {
        isCartOperationInProgress = true;
        showProgress();

        AddToCartModel model = new AddToCartModel();
        model.setItem_id(String.valueOf(item.getId()));
        model.setQuantity(String.valueOf(quantity));
        model.setForce_add(forceAdd ? "1" : "0");
        model.setAdded_modifier_id(new ArrayList<>());

        viewModel.addToCart(authorization, model).observe(this, response -> {
            isCartOperationInProgress = false;
            hideProgress();

            if (response != null && response.data != null && response.data.getStatus() == 1) {

               fetchCartData();

                String itemId = String.valueOf(item.getId());

                if (forceAdd) {
                    cartItems.clear();
                }

                cartItems.put(itemId, new CartItem(itemId, item.getName(), quantity, price, shopId));
                currentCartShopId = shopId;

                updateCartUI();
                syncAdapterWithCart();
                Toast.makeText(this, item.getName() + " added to cart", Toast.LENGTH_SHORT).show();

                refreshCurrentCategory();
            } else {
                String message = response != null && response.message != null ?
                        response.message : "Failed to add item";
                showError(message);
                syncAdapterWithCart();
            }
        });
    }
    // FIXED: Update cart item with hardcoded quantity of 1

    private void updateCartItem(ShopItemResponse.Datum item, int hardcodedQuantity, double price, boolean isRemove, boolean isIncreasing) {
        isCartOperationInProgress = true;
        showProgress();

        AddToCartModel model = new AddToCartModel();
        model.setItem_id(String.valueOf(item.getId()));
        model.setQuantity("1");

        if (isRemove) {
            model.setType("remove");
        } else {
            model.setType(isIncreasing ? "add" : "remove");
        }

        model.setAdded_modifier_id(new ArrayList<>());

        viewModel.updateCart(authorization, model).observe(this, response -> {
            isCartOperationInProgress = false;
            hideProgress();

            if (response != null && response.data != null && response.data.getStatus() == 1) {
                CartResponse.Data cartData = response.data.getData();

                if (cartData != null) {
                    // ✅ UPDATE cart count and subtotal from API response
                    apiCartItemCount = cartData.getCart_item_count();
                    if (cartData.getCart() != null) {
                        apiCartSubtotal = Double.parseDouble(String.valueOf(cartData.getSub_total()));
                    }
                    pref.setPrefInteger(this, pref.cart_count, apiCartItemCount);
                }

                String itemId = String.valueOf(item.getId());

                if (isRemove) {
                    cartItems.remove(itemId);
                    if (cartItems.isEmpty()) {
                        currentCartShopId = null;
                        apiCartItemCount = 0;
                        apiCartSubtotal = 0.0;
                        hideCartWithAnimation();
                    }
                    Toast.makeText(this, item.getName() + " removed from cart", Toast.LENGTH_SHORT).show();
                } else {
                    CartItem existingItem = cartItems.get(itemId);
                    if (existingItem != null) {
                        int newQuantity = isIncreasing ? existingItem.quantity + 1 : existingItem.quantity - 1;

                        if (newQuantity > 0) {
                            cartItems.put(itemId, new CartItem(itemId, item.getName(), newQuantity, price, shopId));
                            Toast.makeText(this, item.getName() + " updated", Toast.LENGTH_SHORT).show();
                        } else {
                            cartItems.remove(itemId);
                            if (cartItems.isEmpty()) {
                                currentCartShopId = null;
                                apiCartItemCount = 0;
                                apiCartSubtotal = 0.0;
                                hideCartWithAnimation();
                            }
                            Toast.makeText(this, item.getName() + " removed from cart", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                updateCartUI();
                syncAdapterWithCart();
                refreshCurrentCategory();
            } else {
                String message = response != null && response.message != null ?
                        response.message : "Failed to update item";
                showError(message);
                syncAdapterWithCart();
            }
        });
    }

      private void removeCartItem(ShopItemResponse.Datum item) {
        // Pass true for isRemove parameter to set type="remove"
        updateCartItem(item, 1, 0, true, false);
      }

    private double getItemPrice(ShopItemResponse.Datum item) {
        try {
            String offerPrice = item.getOffer_price();
            String originalPrice = item.getAmount();

            if (offerPrice != null && !offerPrice.isEmpty()
                    && !offerPrice.equals("0") && !offerPrice.equals("0.00")) {
                return Double.parseDouble(offerPrice);
            } else {
                return Double.parseDouble(originalPrice);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
    private void showCartWithAnimation() {
        binding.bottomCartCard.setVisibility(View.VISIBLE);

        binding.bottomCartCard.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        isCartVisible = true;
                    }
                })
                .start();
    }

    private void hideCartWithAnimation() {
        binding.bottomCartCard.animate()
                .translationY(200f)
                .alpha(0f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        binding.bottomCartCard.setVisibility(View.GONE);
                        isCartVisible = false;

                        // ADD THIS: Clear cart count when cart is hidden
                        if (cartItems.isEmpty()) {
                            pref.setPrefInteger(ItemsActivity.this, pref.cart_count, 0);
                        }
                    }
                })
                .start();
    }

//    private void loadShopItems(String shopId, String menuId, List<String> filterBy, String sortBy) {
//        List<String> finalFilterBy = (filterBy != null && !filterBy.isEmpty()) ? filterBy : new ArrayList<>();
//        String finalSortBy = (sortBy != null && !sortBy.isEmpty()) ? sortBy : "";
//
//        viewModel.getShopItems(
//                authorization,
//                longitude,
//                latitude,
//                shopId,
//                menuId,
//                finalFilterBy,
//                finalSortBy
//        ).observe(this, response -> {
//            hideProgress();
//
//            if (response != null) {
//                if (response.data.status == 1) {
//                    handleSuccessResponse(response.data);
//                } else {
//                    showError(response.message != null ? response.message : "Error loading items");
//                    updateEmptyState();
//                }
//            }
//        });
//    }

    private void handleSuccessResponse(ShopItemResponse response) {
        // ✅ CAPTURE minimum_order_amount from shop items response
        if (response.data != null && response.data.shop.getMin_cart_val() != null) {
            try {
                minimumOrderAmount = Double.parseDouble(response.data.shop.getMin_cart_val());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                minimumOrderAmount = 0.0;
            }
        }

        // FIXED: Handle menu loading properly
        if (response.data.getMenus() != null && !response.data.getMenus().isEmpty()) {
            if (!menusLoaded) {
                // First time loading menus
                horizontalAdapter.updateMenus(response.data.getMenus());
                horizontalAdapter.setSelectedPosition(0);

                // FIXED: Set the first menu as current and load its items
                ShopItemResponse.Menu firstMenu = response.data.getMenus().get(0);
                currentCategoryName = firstMenu.getName();
                currentMenuId = String.valueOf(firstMenu.getId());
                binding.tvCategory.setText(currentCategoryName);

                menusLoaded = true;

                // Load items for the first category
                loadShopItems(shopId, currentMenuId, selectedFilterBy, selectedSortBy);
                return; // Exit here, wait for next response with filtered items
            }

            binding.rcCategory.setVisibility(View.VISIBLE);
        } else {
            binding.rcCategory.setVisibility(View.GONE);
        }

        if (response.data.getItems() != null && !response.data.getItems().data.isEmpty()) {
            shopItemAdapter.setShopItems(response.data.getItems().data);
        } else {
            shopItemAdapter.setShopItems(new ArrayList<>());
        }

        updateEmptyState();
        updateCartBadge();

        // ✅ Update cart UI after getting minimum order amount
        updateCartUI();
    }

    // Complete updateCartUI() method with minimum order logic
    private void updateCartUI() {
        // Check if cart is empty first
        if (cartItems.isEmpty()) {
            hideCartWithAnimation();
            currentCartShopId = null;
            apiCartItemCount = 0;
            apiCartSubtotal = 0.0;
            updateCartBadge();
            return;
        }

        boolean cartBelongsToCurrentShop = currentCartShopId != null && currentCartShopId.equals(shopId);

        if (!cartBelongsToCurrentShop) {
            hideCartWithAnimation();
            return;
        }

        // ✅ USE ONLY API VALUES from cart response (no local calculation)
        int totalItems = apiCartItemCount;
        double totalPrice = apiCartSubtotal;

        // Double check if items are actually present
        if (totalItems == 0) {
            hideCartWithAnimation();
            cartItems.clear();
            currentCartShopId = null;
            apiCartItemCount = 0;
            apiCartSubtotal = 0.0;
            updateCartBadge();
            return;
        }

        String itemText = totalItems + (totalItems == 1 ? " Item" : " Items");
        String priceText = String.format("%s %.2f", currencySymbol, totalPrice);

        binding.cartItemsCount.setText(itemText);
        binding.cartTotalPrice.setText(priceText);
        binding.cartTotalPriceBottom.setText(priceText);

        // ✅ SET MINIMUM ORDER AMOUNT
        if (minimumOrderAmount > 0) {
            String minimumOrderText = String.format("Minimum Order amount: %s %.2f", currencySymbol, minimumOrderAmount);
            binding.tvMinOrderAmount.setText(minimumOrderText);
            binding.tvMinOrderAmount.setVisibility(View.VISIBLE);

            // Check if cart total meets minimum order requirement
            if (totalPrice < minimumOrderAmount) {
                // Cart doesn't meet minimum - show warning color
                binding.tvMinOrderAmount.setTextColor(getResources().getColor(android.R.color.white));

                // Optional: Disable checkout button
                binding.checkoutBtn.setEnabled(false);
                binding.checkoutBtn.setAlpha(0.5f);
            } else {
                // Cart meets minimum - show normal color
                binding.tvMinOrderAmount.setTextColor(getResources().getColor(android.R.color.white));

                // Enable checkout button
                binding.checkoutBtn.setEnabled(true);
                binding.checkoutBtn.setAlpha(1.0f);
            }
        } else {
            // No minimum order requirement
            binding.tvMinOrderAmount.setVisibility(View.GONE);
            binding.checkoutBtn.setEnabled(true);
            binding.checkoutBtn.setAlpha(1.0f);
        }

        updateCartBadge();

        if (!isCartVisible) {
            showCartWithAnimation();
        }
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

    public String getSelectedSortBy() {
        return selectedSortBy;
    }




    public double getTotalCartPrice() {
        double total = 0.0;
        for (CartItem item : cartItems.values()) {
            total += (item.price * item.quantity);
        }
        return total;
    }

    private void updateCartBadge() {
        int count = pref.getPrefInteger(this, pref.cart_count); // your shared pref key

        if (binding.cartBadge == null) return;

        if (count > 0) {
            binding.cartBadge.setVisibility(View.VISIBLE);

            if (count > 99)
                binding.cartBadge.setText("99+");
            else
                binding.cartBadge.setText(String.valueOf(count));

        } else {
            binding.cartBadge.setVisibility(View.GONE);
        }
    }


    @Override
    public void onBackPressed() {
        if (binding.searchView.getVisibility() == View.VISIBLE) {
            closeSearch();
            return;
        }

        super.onBackPressed();
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
        fetchCartData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }


    private void showFilterBottomSheet() {
        FilterBottomSheet bottomSheet = new FilterBottomSheet();

        bottomSheet.setCurrentFilters(selectedFilterBy, selectedSortBy);

        bottomSheet.setFilterListener((filterBy, sortBy) -> {
            selectedFilterBy = filterBy;
            selectedSortBy = sortBy;

            // Display friendly names in toast
            String filterString = "None";
            if (filterBy != null) {
                filterString = filterBy.equals("0") ? "Veg" : "Non-Veg";
            }

            String sortString = "None";
            if (sortBy != null) {
                sortString = sortBy.equals("pl2h") ? "Price Low to High" : "Price High to Low";
            }

            Toast.makeText(this,
                    "Filter: " + filterString + "\nSort: " + sortString,
                    Toast.LENGTH_SHORT).show();

            applyFiltersAndSort();
        });

        bottomSheet.show(getSupportFragmentManager(), "FilterBottomSheet");
    }

    // 3. CRITICAL FIX: Update loadShopItems to send filter and sort as STRINGS
    private void loadShopItems(String shopId, String menuId, String filterBy, String sortBy) {
        // Send filterBy and sortBy as strings (not lists)
        // filterBy will be "0", "1", or empty string
        // sortBy will be "pl2h", "ph2l", or empty string

        String finalFilterBy = (filterBy != null && !filterBy.isEmpty()) ? filterBy : "";
        String finalSortBy = (sortBy != null && !sortBy.isEmpty()) ? sortBy : "";

        viewModel.getShopItems(
                authorization,
                longitude,
                latitude,
                shopId,
                menuId,
                finalFilterBy,  // Send as STRING (not List)
                finalSortBy     // Send as STRING
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

    // 4. Update clearFilters
    public void clearFilters() {
        selectedFilterBy = null;
        selectedSortBy = null;

        Toast.makeText(this, "Filters cleared", Toast.LENGTH_SHORT).show();
        applyFiltersAndSort();
    }

    // 5. Update hasActiveFilters
    public boolean hasActiveFilters() {
        return (selectedFilterBy != null && !selectedFilterBy.isEmpty())
                || (selectedSortBy != null && !selectedSortBy.isEmpty());
    }

    // 6. Update getSelectedFilterBy
    public String getSelectedFilterBy() {
        return selectedFilterBy;
    }

    // 7. All method calls remain the same
    private void onCategorySelected(ShopItemResponse.Menu menu) {
        currentCategoryName = menu.getName();
        currentMenuId = String.valueOf(menu.getId());
        binding.tvCategory.setText(currentCategoryName);
        binding.appBarLayout.setExpanded(true, true);
        showProgress();
        loadShopItems(shopId, currentMenuId, selectedFilterBy, selectedSortBy);
    }

    private void refreshCurrentCategory() {
        loadShopItems(shopId, currentMenuId, selectedFilterBy, selectedSortBy);
    }

    private void loadInitialData() {
        showProgress();
        loadShopItems(shopId, "", selectedFilterBy, selectedSortBy);
    }

    private void applyFiltersAndSort() {
        showProgress();
        loadShopItems(shopId, currentMenuId, selectedFilterBy, selectedSortBy);
    }
}