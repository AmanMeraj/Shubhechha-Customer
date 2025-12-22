package com.subh.shubhechha.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.subh.shubhechha.Adapters.CartAdapter;
import com.subh.shubhechha.Model.AddToCartModel;
import com.subh.shubhechha.Model.CartResponse;
import com.subh.shubhechha.ViewModel.ViewModel;
import com.subh.shubhechha.databinding.ActivityCartBinding;
import com.subh.shubhechha.utils.Utility;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartActivity extends Utility implements CartAdapter.OnCartItemListener {

    private ActivityCartBinding binding;
    private CartAdapter cartAdapter;
    private DecimalFormat priceFormat;
    private ViewModel viewModel;
    private CartResponse.Data cartData;

    private String authorization;
    private ArrayList<CartResponse.CartItem> cartItems = new ArrayList<>();
    private boolean isCartOperationInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        try {
            binding = ActivityCartBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            initializeViewModel();
            initializeViews();
            setupCollapsingToolbar();
            setupRecyclerView();
            setupClickListeners();
            loadCartData();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to initialize cart");
        }
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
    }

    private void initializeViews() {
        try {
            priceFormat = new DecimalFormat("#,##0.00");
            authorization = getAuthToken();

            // Setup toolbar back button
            if (binding.backBtn != null) {
                binding.backBtn.setOnClickListener(v -> onBackPressed());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupCollapsingToolbar() {
        try {
            binding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isCollapsed = false;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    int scrollRange = appBarLayout.getTotalScrollRange();
                    if (scrollRange == 0) return;

                    float percentage = Math.abs(verticalOffset / (float) scrollRange);
                    if (Float.isNaN(percentage) || Float.isInfinite(percentage)) return;

                    if (binding.tvToolbarTitle != null) {
                        binding.tvToolbarTitle.setAlpha(percentage);
                    }

                    if (binding.tvOrderDetails != null) {
                        binding.tvOrderDetails.setAlpha(1 - percentage);
                    }

                    if (binding.peachCurveBg != null) {
                        float scale = 1 - (percentage * 0.2f);
                        scale = Math.max(0.8f, Math.min(1f, scale));
                        binding.peachCurveBg.setScaleY(scale);
                    }

                    if (Math.abs(verticalOffset) >= scrollRange) {
                        if (!isCollapsed) {
                            isCollapsed = true;
                            onToolbarCollapsed();
                        }
                    } else {
                        if (isCollapsed) {
                            isCollapsed = false;
                            onToolbarExpanded();
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onToolbarCollapsed() {
        // Called when toolbar is fully collapsed
    }

    private void onToolbarExpanded() {
        // Called when toolbar is fully expanded
    }

    private void setupRecyclerView() {
        try {
            cartAdapter = new CartAdapter(this);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            binding.cartRecyclerView.setLayoutManager(layoutManager);
            binding.cartRecyclerView.setAdapter(cartAdapter);
            binding.cartRecyclerView.setHasFixedSize(true);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to setup RecyclerView", e);
        }
    }

    private void setupClickListeners() {
        try {
            // Checkout button
            if (binding.checkoutButton != null) {
                binding.checkoutButton.setOnClickListener(v -> {
                    if (cartAdapter != null && cartAdapter.getItemCount() > 0) {
                        proceedToCheckout();
                    } else {
                        Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // Shop now button (for empty state)
            if (binding.shopNowButton != null) {
                binding.shopNowButton.setOnClickListener(v -> {
                    onBackPressed();
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadCartData() {
        showLoading(true);

        viewModel.getCart(authorization).observe(this, response -> {
            showLoading(false);

            if (response != null && response.data != null && response.data.getStatus() == 1) {
                cartData = response.data.getData();

                if (cartData != null && cartData.getCart() != null) {
                    CartResponse.Cart cart = cartData.getCart();

                    // ✅ SAVE CART ITEM COUNT FROM API RESPONSE
                    pref.setPrefInteger(this, pref.cart_count, cartData.getCart_item_count());

                    if (cart.getCart_items() != null && !cart.getCart_items().isEmpty()) {
                        cartItems = cart.getCart_items();
                        cartAdapter.setCartItems(cartItems);
                        updateOrderSummary(cart);
                        updateEmptyState();
                    } else {
                        cartItems.clear();
                        cartAdapter.setCartItems(cartItems);
                        pref.setPrefInteger(this, pref.cart_count, 0);
                        updateEmptyState();
                    }
                } else {
                    cartItems.clear();
                    cartAdapter.setCartItems(cartItems);
                    pref.setPrefInteger(this, pref.cart_count, 0);
                    updateEmptyState();
                }
            } else {
                cartItems.clear();
                cartAdapter.setCartItems(cartItems);
                pref.setPrefInteger(this, pref.cart_count, 0);
                updateEmptyState();
            }
        });
    }

    private void updateOrderSummary(CartResponse.Cart cart) {
        try {
            // You can add order summary UI updates here if needed
            // For example: subtotal, tax, delivery fee, total

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onQuantityChanged(CartResponse.CartItem item, int position, int newQuantity, boolean isIncreasing) {
        if (isCartOperationInProgress) {
            Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (item == null) {
            showError("Invalid item");
            return;
        }

        // Check if this is the last item being removed
        int currentQuantity = 0;
        try {
            currentQuantity = Integer.parseInt(item.getQuantity());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        boolean isLastItem = (currentQuantity == 1 && !isIncreasing && cartItems.size() == 1);

        if (isLastItem) {
            // Clear cart completely for last item
            clearCartCompletely(item, position);
        } else {
            // Normal update
            updateCartItem(item, position, isIncreasing);
        }
    }

    @Override
    public void onDeleteItem(CartResponse.CartItem item, int position) {

        new AlertDialog.Builder(this)
                .setTitle("Remove Item")
                .setMessage("Are you sure you want to remove this item?")
                .setPositiveButton("Remove", (d, w) -> {
                    removeCartItem(item.getId(), position);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void updateCartItem(CartResponse.CartItem item, int position, boolean isIncreasing) {
        isCartOperationInProgress = true;
        showLoading(true);

        AddToCartModel model = new AddToCartModel();
        model.setItem_id(String.valueOf(item.getItem_id()));
        model.setQuantity("1");
        model.setType(isIncreasing ? "add" : "remove");
        model.setAdded_modifier_id(new ArrayList<>());

        viewModel.updateCart(authorization, model).observe(this, response -> {
            isCartOperationInProgress = false;
            showLoading(false);

            if (response != null && response.data != null && response.data.getStatus() == 1) {
                // ✅ SAVE UPDATED CART COUNT FROM API
                CartResponse.Data updatedCartData = response.data.getData();
                if (updatedCartData != null) {
                    pref.setPrefInteger(this, pref.cart_count, updatedCartData.getCart_item_count());
                }

                loadCartData();

                String itemName = item.getItem() != null ? item.getItem().getName() : "Item";
                Toast.makeText(this, itemName + " updated", Toast.LENGTH_SHORT).show();
            } else {
                String message = response != null && response.message != null ?
                        response.message : "Failed to update item";
                showError(message);
                loadCartData();
            }
        });
    }
    private void removeCartItem(int itemId, int position) {
        isCartOperationInProgress = true;
        showLoading(true);

        viewModel.deleteCartItem(authorization, itemId)
                .observe(this, response -> {
                    isCartOperationInProgress = false;
                    showLoading(false);

                    boolean success = false;
                    String serverMsg = null;

                    try {
                        if (response != null) {
                            serverMsg = response.message;

                            if (response.data != null) {
                                try {
                                    if (response.data.getStatus() == 1) {
                                        success = true;
                                        // ✅ SAVE UPDATED CART COUNT FROM API
                                        loadCartData();

                                    }
                                } catch (Exception ignored) { }
                            }

                            if (!success && serverMsg != null) {
                                String lower = serverMsg.toLowerCase();
                                if (lower.contains("success") || lower.contains("removed")
                                        || lower.contains("deleted") || lower.contains("cart cleared")
                                        || lower.contains("item removed")) {
                                    success = true;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (success) {
                        if (position >= 0 && position < cartAdapter.getItemCount()) {
                            cartAdapter.removeItem(position);
                        }
                        updateEmptyState();

                        Toast.makeText(this,
                                serverMsg != null && !serverMsg.isEmpty() ? serverMsg : "Item removed",
                                Toast.LENGTH_SHORT).show();

                        loadCartData();
                    } else {
                        loadCartData();
                    }
                });
    }

    private void clearCartCompletely(CartResponse.CartItem item, int position) {
        isCartOperationInProgress = true;
        showLoading(true);

        AddToCartModel model = new AddToCartModel();
        model.setItem_id(String.valueOf(item.getItem_id()));
        model.setQuantity(item.getQuantity());
        model.setType("remove");
        model.setAdded_modifier_id(new ArrayList<>());

        viewModel.updateCart(authorization, model).observe(this, response -> {
            isCartOperationInProgress = false;
            showLoading(false);

            // Clear local cart regardless of response
            cartItems.clear();
            cartAdapter.setCartItems(cartItems);
            updateEmptyState();

            pref.setPrefInteger(this, pref.cart_count, 0); // ADD THIS LINE

            Toast.makeText(this, "Cart cleared", Toast.LENGTH_SHORT).show();
        });
    }
    private void updateEmptyState() {
        try {
            if (cartAdapter != null) {
                boolean isEmpty = cartAdapter.getItemCount() == 0;

                if (binding.emptyStateLayout != null) {
                    binding.emptyStateLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                }

                if (binding.cartRecyclerView != null) {
                    binding.cartRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                }

                if (binding.checkoutButton != null) {
                    binding.checkoutButton.setEnabled(!isEmpty);
                    binding.checkoutButton.setAlpha(isEmpty ? 0.5f : 1.0f);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLoading(boolean show) {
        try {
            if (binding.progressBar != null) {
                binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void proceedToCheckout() {
        try {
            if (cartData == null) {
                Toast.makeText(this, "Cart data not available", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, CheckoutActivity.class);

            // Pass all cart summary data
            intent.putExtra("cart_item_count", cartData.getCart_item_count());
            intent.putExtra("sub_total", cartData.getSub_total());
            intent.putExtra("delivery_charge", cartData.getDelivery_charge());
            intent.putExtra("discount_amount", cartData.getDiscount_amount());
            intent.putExtra("packaging_charge", cartData.getPackaging_charge());
            intent.putExtra("total_tax", cartData.getTotal_tax());
            intent.putExtra("total", cartData.getTotal());
            intent.putExtra("gst_on_item_total", cartData.getGst_on_item_total());
            intent.putExtra("gst_on_packaging_charge", cartData.getGst_on_packaging_charge());
            intent.putExtra("gst_on_delivery_charge", cartData.getGst_on_delivery_charge());

            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to proceed to checkout");
        }
    }


    private void showError(String message) {
        try {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getAuthToken() {
        return "Bearer " + pref.getPrefString(this, pref.user_token);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binding != null) {
            binding = null;
        }
        cartAdapter = null;
    }
}