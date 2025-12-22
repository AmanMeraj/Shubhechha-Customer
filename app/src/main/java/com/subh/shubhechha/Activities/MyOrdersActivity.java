package com.subh.shubhechha.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.subh.shubhechha.Adapters.OrderAdapter;
import com.subh.shubhechha.Model.OrderModel;
import com.subh.shubhechha.R;
import com.subh.shubhechha.ViewModel.ViewModel;
import com.subh.shubhechha.databinding.ActivityMyOrdersBinding;
import com.subh.shubhechha.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersActivity extends Utility {

    private ActivityMyOrdersBinding binding;
    private OrderAdapter orderAdapter;
    private ArrayList<OrderModel.Order> orderList;
    private ViewModel orderViewModel;

    // Pagination variables
    private int currentPage = 1;
    private int lastPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;

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
            setupViewModel();
            loadOrders(currentPage);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to initialize orders");
        }
    }

    private void initializeViews() {
        try {
            if (binding.backBtn != null) {
                binding.backBtn.setOnClickListener(v -> navigateToContainer());
            }

            if (binding.shopNowButton != null) {
                binding.shopNowButton.setOnClickListener(v -> navigateToContainer());
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

                    if (binding.tvOrdersExpanded != null) {
                        binding.tvOrdersExpanded.setAlpha(1 - percentage);
                    }

                    if (binding.peachCurveBg != null) {
                        float scale = 1 - (percentage * 0.2f);
                        scale = Math.max(0.8f, Math.min(1f, scale));
                        binding.peachCurveBg.setScaleY(scale);
                    }

                    if (Math.abs(verticalOffset) >= scrollRange) {
                        if (!isCollapsed) {
                            isCollapsed = true;
                        }
                    } else {
                        if (isCollapsed) {
                            isCollapsed = false;
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupRecyclerView() {
        try {
            orderList = new ArrayList<>();
            orderAdapter = new OrderAdapter(this, orderList);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            binding.rcAddressBook.setLayoutManager(layoutManager);
            binding.rcAddressBook.setAdapter(orderAdapter);
            binding.rcAddressBook.setHasFixedSize(true);

            // Pagination scroll listener
            binding.rcAddressBook.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0) {
                            loadMoreOrders();
                        }
                    }
                }
            });

            orderAdapter.setOnOrderClickListener((order, position) -> {
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(this, OrderDetailsActivity.class);
                    intent.putExtra("order_id", order.getId());
                    intent.putExtra("order_no", order.getOrderno());
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }, 400);
            });

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to setup RecyclerView", e);
        }
    }

    private void setupViewModel() {
        try {
            orderViewModel = new ViewModelProvider(this).get(ViewModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadOrders(int page) {
        if (isLoading) return;

        try {
            isLoading = true;
            showLoading(true);

            String authToken = "Bearer " + getAuthToken(); // Get your auth token from SharedPreferences

            orderViewModel.getOrders(authToken).observe(this, response -> {
                isLoading = false;
                showLoading(false);

                if (response != null) {
                    if (response.data.getStatus() ==1) {
                        handleOrdersResponse(response.data);
                    } else {
                        showError(response.data.getMessage() != null ? response.data.getMessage() : "Failed to load orders");
                    }
                } else {
                    showError("Failed to load orders");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            isLoading = false;
            showLoading(false);
            showError("Failed to load orders");
        }
    }

    private void handleOrdersResponse(OrderModel orderModel) {
        try {
            if (orderModel.getData() != null && orderModel.getData().getOrders() != null) {
                OrderModel.Orders ordersData = orderModel.getData().getOrders();

                currentPage = ordersData.getCurrent_page();
                lastPage = ordersData.getLast_page();
                isLastPage = currentPage >= lastPage;

                List<OrderModel.Order> newOrders = ordersData.getData();

                if (newOrders != null && !newOrders.isEmpty()) {
                    if (currentPage == 1) {
                        orderList.clear();
                    }

                    int startPosition = orderList.size();
                    orderList.addAll(newOrders);

                    if (currentPage == 1) {
                        orderAdapter.notifyDataSetChanged();
                    } else {
                        orderAdapter.notifyItemRangeInserted(startPosition, newOrders.size());
                    }
                }
            }

            updateEmptyState();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to process orders");
        }
    }

    private void loadMoreOrders() {
        if (!isLastPage && !isLoading) {
            currentPage++;
            loadOrders(currentPage);
        }
    }

    private void refreshOrders() {
        try {
            currentPage = 1;
            isLastPage = false;
            orderList.clear();
            orderAdapter.notifyDataSetChanged();
            loadOrders(currentPage);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to refresh orders");
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
                // Only show progress bar for first page
                if (currentPage == 1) {
                    binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
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

    private String getAuthToken() {
        return pref.getPrefString(this,pref.user_token);
    }

    /**
     * Navigate to ContainerActivity and clear back stack
     */
    private void navigateToContainer() {
        try {
            Intent intent = new Intent(this, ContainerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to normal back press if navigation fails
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            getOnBackPressedDispatcher().onBackPressed();
        } else {
            navigateToContainer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register back press callback for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    navigateToContainer();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binding != null) {
            binding = null;
        }
        orderAdapter = null;
        orderList = null;
    }
}