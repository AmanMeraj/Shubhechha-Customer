package com.subh.shubhechha.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.subh.shubhechha.Adapters.OrderAdapter;
import com.subh.shubhechha.Model.OrderModel;
import com.subh.shubhechha.R;
import com.subh.shubhechha.databinding.ActivityMyOrdersBinding;
import com.subh.shubhechha.utils.Utility;

import java.util.ArrayList;

public class MyOrdersActivity extends Utility {

    private ActivityMyOrdersBinding binding;
    private OrderAdapter orderAdapter;
    private ArrayList<OrderModel> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        try {
            binding = ActivityMyOrdersBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            initializeViews();
            setupCollapsingToolbar();
            setupRecyclerView();
            loadOrders();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to initialize orders");
        }
    }

    private void initializeViews() {
        try {
            // Setup toolbar back button
            if (binding.backBtn != null) {
                binding.backBtn.setOnClickListener(v -> onBackPressed());
            }

            // Setup shop now button
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

                    // Fade in/out toolbar title
                    if (binding.tvToolbarTitle != null) {
                        binding.tvToolbarTitle.setAlpha(percentage);
                    }

                    // Fade in/out expanded title
                    if (binding.tvOrdersExpanded != null) {
                        binding.tvOrdersExpanded.setAlpha(1 - percentage);
                    }

                    // Scale the background curve
                    if (binding.peachCurveBg != null) {
                        float scale = 1 - (percentage * 0.2f);
                        scale = Math.max(0.8f, Math.min(1f, scale));
                        binding.peachCurveBg.setScaleY(scale);
                    }

                    // Check if fully collapsed or expanded
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
            orderList = new ArrayList<>();
            orderAdapter = new OrderAdapter(this, orderList);

            binding.rcAddressBook.setLayoutManager(new LinearLayoutManager(this));
            binding.rcAddressBook.setAdapter(orderAdapter);
            binding.rcAddressBook.setHasFixedSize(true);

            // Set click listener
            orderAdapter.setOnOrderClickListener((order, position) -> {
                // Add a slight delay for better UX
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(this, OrderDetailsActivity.class);
                    intent.putExtra("order_id", order.getOrderId());
                    intent.putExtra("store_name", order.getStoreName());
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }, 400);
            });

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to setup RecyclerView", e);
        }
    }

    private void loadOrders() {
        try {
            // Show loading state
            showLoading(true);

            // Sample data - Replace with your actual data loading logic (API call, database, etc.)
            orderList.add(new OrderModel(
                    "Fresh Mart Store",
                    "#254586",
                    "1000",
                    "20 Jul, 2025",
                    "Delivered",
                    R.drawable.subh_img1
            ));

            orderList.add(new OrderModel(
                    "Electronics Hub",
                    "#254587",
                    "2500",
                    "18 Jul, 2025",
                    "Processing",
                    R.drawable.subh_img1
            ));

            orderList.add(new OrderModel(
                    "Fashion Boutique",
                    "#254588",
                    "1500",
                    "15 Jul, 2025",
                    "Delivered",
                    R.drawable.subh_img1
            ));

            orderList.add(new OrderModel(
                    "Grocery Corner",
                    "#254589",
                    "800",
                    "12 Jul, 2025",
                    "Cancelled",
                    R.drawable.subh_img1
            ));

            orderList.add(new OrderModel(
                    "Book Store",
                    "#254590",
                    "600",
                    "10 Jul, 2025",
                    "Pending",
                    R.drawable.subh_img1
            ));

            orderList.add(new OrderModel(
                    "Tech Store",
                    "#254591",
                    "3200",
                    "08 Jul, 2025",
                    "Delivered",
                    R.drawable.subh_img1
            ));

            orderList.add(new OrderModel(
                    "Food Market",
                    "#254592",
                    "450",
                    "05 Jul, 2025",
                    "Delivered",
                    R.drawable.subh_img1
            ));

            // Notify adapter
            orderAdapter.notifyDataSetChanged();

            // Update empty state
            updateEmptyState();

            // Hide loading
            showLoading(false);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load orders");
            showLoading(false);
        }
    }

    // Method to refresh orders from server/database
    private void refreshOrders() {
        try {
            // Show loading
            showLoading(true);

            // Clear existing list
            orderList.clear();

            // Load new data
            loadOrders();

            // Notify adapter
            orderAdapter.notifyDataSetChanged();

            // Update empty state
            updateEmptyState();

            // Hide loading
            showLoading(false);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to refresh orders");
            showLoading(false);
        }
    }

    private void updateEmptyState() {
        try {
            boolean isEmpty = orderList == null || orderList.isEmpty();

            if (binding.emptyStateLayout != null) {
                binding.emptyStateLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            }

            if (binding.rcAddressBook != null) {
                binding.rcAddressBook.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
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
        orderAdapter = null;
        orderList = null;
    }
}