package com.subh.shubhechha.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.subh.shubhechha.Adapters.CartAdapter;
import com.subh.shubhechha.Model.CartItem;
import com.subh.shubhechha.databinding.ActivityCartBinding;
import com.subh.shubhechha.utils.Utility;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends Utility implements CartAdapter.OnCartItemListener {

    private ActivityCartBinding binding;
    private CartAdapter cartAdapter;
    private DecimalFormat priceFormat;

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

            initializeViews();
            setupCollapsingToolbar();
            setupRecyclerView();
            loadCartData();
            setupClickListeners();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to initialize cart");
        }
    }

    private void initializeViews() {
        try {
            priceFormat = new DecimalFormat("#,##0.00");

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
                    if (scrollRange == 0) return; // avoid division by zero

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
                        // clamp value between 0.8f and 1f for safety
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
        // Add any additional animations or state changes here
    }

    private void onToolbarExpanded() {
        // Called when toolbar is fully expanded
        // Add any additional animations or state changes here
    }

    private void setupRecyclerView() {
        try {
            // Initialize adapter
            cartAdapter = new CartAdapter(this);

            // Setup RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            binding.cartRecyclerView.setLayoutManager(layoutManager);
            binding.cartRecyclerView.setAdapter(cartAdapter);
            binding.cartRecyclerView.setHasFixedSize(true);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to setup RecyclerView", e);
        }
    }

    private void loadCartData() {
        try {
            // Show loading state
            showLoading(true);

            // Sample data - Replace with your actual data source
            List<CartItem> cartItems = getSampleCartItems();

            if (cartAdapter != null) {
                cartAdapter.setCartItems(cartItems);
                updateEmptyState();
            }

            // Hide loading
            showLoading(false);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load cart items");
            showLoading(false);
        }
    }

    private List<CartItem> getSampleCartItems() {
        List<CartItem> items = new ArrayList<>();

        items.add(new CartItem(
                "1",
                "Mosur Dal",
                "Contrary to popular belief, Lorem Ipsum is not simply random text.",
                "https://example.com/mosur_dal.jpg",
                130.00,
                95.00,
                3,
                10
        ));

        items.add(new CartItem(
                "2",
                "Mustard Oil",
                "Contrary to popular belief, Lorem Ipsum is not simply random text.",
                "https://example.com/mustard_oil.jpg",
                180.00,
                150.00,
                1,
                5
        ));

        items.add(new CartItem(
                "3",
                "Soybean Oil",
                "Contrary to popular belief, Lorem Ipsum is not simply random text.",
                "https://example.com/soybean_oil.jpg",
                230.00,
                195.00,
                1,
                8
        ));

        items.add(new CartItem(
                "4",
                "Rice",
                "Premium quality basmati rice",
                "https://example.com/rice.jpg",
                250.00,
                220.00,
                2,
                15
        ));
 items.add(new CartItem(
                "1",
                "Mosur Dal",
                "Contrary to popular belief, Lorem Ipsum is not simply random text.",
                "https://example.com/mosur_dal.jpg",
                130.00,
                95.00,
                3,
                10
        ));

        items.add(new CartItem(
                "2",
                "Mustard Oil",
                "Contrary to popular belief, Lorem Ipsum is not simply random text.",
                "https://example.com/mustard_oil.jpg",
                180.00,
                150.00,
                1,
                5
        ));

        items.add(new CartItem(
                "3",
                "Soybean Oil",
                "Contrary to popular belief, Lorem Ipsum is not simply random text.",
                "https://example.com/soybean_oil.jpg",
                230.00,
                195.00,
                1,
                8
        ));

        items.add(new CartItem(
                "4",
                "Rice",
                "Premium quality basmati rice",
                "https://example.com/rice.jpg",
                250.00,
                220.00,
                2,
                15
        ));

        return items;
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
                    // Navigate to shop/home
                    onBackPressed();
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onQuantityChanged(CartItem item, int position, int newQuantity) {
        try {
            if (item == null) {
                showError("Invalid item");
                return;
            }

            // TODO: Update the quantity in your backend/database here
            // updateQuantityInBackend(item.getProductId(), newQuantity);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to update quantity");

            // Revert the change if update fails
            if (cartAdapter != null) {
                cartAdapter.notifyItemChanged(position);
            }
        }
    }

    @Override
    public void onDeleteItem(CartItem item, int position) {
        try {
            if (item == null) {
                showError("Invalid item");
                return;
            }

            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Remove Item")
                    .setMessage("Are you sure you want to remove " + item.getProductName() + " from cart?")
                    .setPositiveButton("Remove", (dialog, which) -> {
                        try {
                            // TODO: Delete from backend/database here
                            // deleteItemFromBackend(item.getProductId());

                            // Remove from adapter
                            if (cartAdapter != null) {
                                cartAdapter.removeItem(position);
                                updateEmptyState();
                                Toast.makeText(this, "Item removed from cart", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showError("Failed to remove item");
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to delete item");
        }
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
            // TODO: Implement checkout logic
            // Intent intent = new Intent(this, CheckoutActivity.class);
            // startActivity(intent);

            Toast.makeText(this, "Proceeding to checkout...", Toast.LENGTH_SHORT).show();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up resources
        if (binding != null) {
            binding = null;
        }
        cartAdapter = null;
    }
}