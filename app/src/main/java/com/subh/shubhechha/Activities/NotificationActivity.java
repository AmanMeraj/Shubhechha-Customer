package com.subh.shubhechha.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.subh.shubhechha.Adapters.NotificationAdapter;
import com.subh.shubhechha.Model.NotificationResponse;
import com.subh.shubhechha.Repository.Repository;
import com.subh.shubhechha.ViewModel.ViewModel;
import com.subh.shubhechha.databinding.ActivityNotificationBinding;
import com.subh.shubhechha.utils.Utility;

public class NotificationActivity extends Utility {

    private ActivityNotificationBinding binding;
    private NotificationAdapter notificationAdapter;
    private ViewModel viewModel;

    private int currentPage = 1;
    private int lastPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        try {
            binding = ActivityNotificationBinding.inflate(getLayoutInflater());
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
            loadNotifications(currentPage);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to initialize notifications");
        }
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
    }

    private void initializeViews() {
        try {
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

                    // Fade in/out toolbar title
                    if (binding.tvToolbarTitle != null) {
                        binding.tvToolbarTitle.setAlpha(percentage);
                    }

                    // Fade in/out expanded title
                    if (binding.tvNotificationExpanded != null) {
                        binding.tvNotificationExpanded.setAlpha(1 - percentage);
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
    }

    private void onToolbarExpanded() {
        // Called when toolbar is fully expanded
    }

    private void setupRecyclerView() {
        try {
            // Initialize adapter
            notificationAdapter = new NotificationAdapter();

            // Setup RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            binding.rcNotification.setLayoutManager(layoutManager);
            binding.rcNotification.setHasFixedSize(true);
            binding.rcNotification.setAdapter(notificationAdapter);

            // Add padding to prevent first item from being hidden
            binding.rcNotification.setClipToPadding(false);
            int topPadding = (int) (16 * getResources().getDisplayMetrics().density); // 16dp
            binding.rcNotification.setPadding(0, topPadding, 0, 0);

            // Add scroll listener for pagination
            binding.rcNotification.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (dy > 0) { // Scrolling down
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                        if (!isLoading && !isLastPage) {
                            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                    && firstVisibleItemPosition >= 0) {
                                // Load next page
                                loadNotifications(currentPage + 1);
                            }
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to setup RecyclerView", e);
        }
    }

    private void loadNotifications(int page) {
        try {
            if (isLoading || isLastPage) return;

            isLoading = true;
            showLoading(page == 1); // Show main loading only for first page

            String token = "Bearer "+getToken();
            if (token == null || token.isEmpty()) {
                showError("Authentication required");
                showLoading(false);
                isLoading = false;
                return;
            }

            viewModel.getNotification(token).observe(this, apiResponse -> {
                isLoading = false;
                showLoading(false);

                if (apiResponse != null && apiResponse.isSuccess()) {
                    handleSuccessResponse(apiResponse.data, page);
                } else {
                    handleErrorResponse(apiResponse);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load notifications");
            showLoading(false);
            isLoading = false;
        }
    }

    private void handleSuccessResponse(NotificationResponse response, int page) {
        try {
            if (response != null && response.getData() != null) {
                NotificationResponse.Data data = response.getData();
                if (data.getNotifications() != null) {
                    NotificationResponse.Notifications notifications = data.getNotifications();

                    // Update pagination info
                    currentPage = notifications.getCurrent_page();
                    lastPage = notifications.getLast_page();
                    isLastPage = currentPage >= lastPage;

                    // Get notification items
                    if (notifications.getData() != null && !notifications.getData().isEmpty()) {
                        android.util.Log.d("NotificationActivity", "Total items: " + notifications.getData().size());
                        android.util.Log.d("NotificationActivity", "First item title: " +
                                (notifications.getData().get(0) != null ? notifications.getData().get(0).getTitle() : "null"));

                        if (page == 1) {
                            // First page - replace all data
                            notificationAdapter.clearNotifications();
                            notificationAdapter.addNotifications(notifications.getData());
                        } else {
                            // Next pages - append data
                            notificationAdapter.addNotifications(notifications.getData());
                        }

                        updateEmptyState(false);

                        // Force RecyclerView to update
                        binding.rcNotification.post(() -> {
                            if (page == 1 && binding.rcNotification.getLayoutManager() != null) {
                                binding.rcNotification.scrollToPosition(0);
                            }
                        });
                    } else {
                        if (page == 1) {
                            // No notifications at all
                            updateEmptyState(true);
                        }
                    }
                } else {
                    if (page == 1) {
                        updateEmptyState(true);
                    }
                }
            } else {
                if (page == 1) {
                    updateEmptyState(true);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.e("NotificationActivity", "Error processing notifications", e);
            showError("Error processing notifications");
        }
    }

    private void handleErrorResponse(Repository.ApiResponse<NotificationResponse> apiResponse) {
        try {
            if (apiResponse != null) {
                if (apiResponse.code == Repository.ERROR_SESSION_EXPIRED) {
                    showError("Session expired. Please login again.");
                    // Handle session expiration (e.g., navigate to login)
//                    handleSessionExpired();
                } else if (apiResponse.message != null && !apiResponse.message.isEmpty()) {
                    showError(apiResponse.message);
                } else {
                    showError("Failed to load notifications");
                }
            } else {
                showError("Failed to load notifications");
            }

            if (currentPage == 1) {
                updateEmptyState(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateEmptyState(boolean isEmpty) {
        try {
            if (binding.emptyStateLayout != null) {
                binding.emptyStateLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            }

            if (binding.rcNotification != null) {
                binding.rcNotification.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
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
        notificationAdapter = null;
    }
    private  String getToken(){
        return pref.getPrefString(this, pref.user_token);
    }
}