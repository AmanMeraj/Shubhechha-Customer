package com.subh.shubhechha.Activities;

import android.os.Bundle;
import android.util.Log;
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

    private static final String TAG = "NotificationActivity";

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
            Log.e(TAG, "onCreate error", e);
            showError("Failed to initialize notifications");
        }
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
    }

    private void initializeViews() {
        try {
            if (binding.backBtn != null) {
                binding.backBtn.setOnClickListener(v -> onBackPressed());
            }
        } catch (Exception e) {
            Log.e(TAG, "initializeViews error", e);
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

                    if (binding.tvNotificationExpanded != null) {
                        binding.tvNotificationExpanded.setAlpha(1 - percentage);
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
            Log.e(TAG, "setupCollapsingToolbar error", e);
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
            notificationAdapter = new NotificationAdapter();

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            binding.rcNotification.setLayoutManager(layoutManager);
            binding.rcNotification.setHasFixedSize(true);
            binding.rcNotification.setAdapter(notificationAdapter);

            binding.rcNotification.setClipToPadding(false);
            int topPadding = (int) (16 * getResources().getDisplayMetrics().density);
            binding.rcNotification.setPadding(0, topPadding, 0, 0);

            binding.rcNotification.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (dy > 0) { // Scrolling down
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                        Log.d(TAG, "Scroll - visible: " + visibleItemCount +
                                ", total: " + totalItemCount +
                                ", first: " + firstVisibleItemPosition +
                                ", isLoading: " + isLoading +
                                ", isLastPage: " + isLastPage +
                                ", currentPage: " + currentPage +
                                ", lastPage: " + lastPage);

                        if (!isLoading && !isLastPage) {
                            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 3
                                    && firstVisibleItemPosition >= 0
                                    && totalItemCount >= visibleItemCount) {
                                Log.d(TAG, "Triggering page load: " + (currentPage + 1));
                                loadNotifications(currentPage + 1);
                            }
                        }
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "setupRecyclerView error", e);
            throw new RuntimeException("Failed to setup RecyclerView", e);
        }
    }

    private void loadNotifications(int page) {
        try {
            if (isLoading) {
                Log.d(TAG, "Already loading, skipping page: " + page);
                return;
            }

            if (isLastPage) {
                Log.d(TAG, "Last page reached, skipping page: " + page);
                return;
            }

            isLoading = true;
            Log.d(TAG, "Starting to load page: " + page);

            showLoading(page == 1);
            if (page > 1) {
                showPaginationLoading(true);
            }

            String token = "Bearer " + getToken();
            if (token == null || token.isEmpty() || token.equals("Bearer null")) {
                showError("Authentication required");
                showLoading(false);
                showPaginationLoading(false);
                isLoading = false;
                return;
            }

            Log.d(TAG, "Making API call for page: " + page);

            viewModel.getNotification(token, page).observe(this, apiResponse -> {
                isLoading = false;
                showLoading(false);
                showPaginationLoading(false);

                Log.d(TAG, "API response received for page: " + page);

                if (apiResponse != null && apiResponse.isSuccess()) {
                    Log.d(TAG, "Success response for page: " + page);
                    handleSuccessResponse(apiResponse.data, page);
                } else {
                    Log.e(TAG, "Error response for page: " + page +
                            ", message: " + (apiResponse != null ? apiResponse.message : "null"));
                    handleErrorResponse(apiResponse);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "loadNotifications error for page: " + page, e);
            showError("Failed to load notifications");
            showLoading(false);
            showPaginationLoading(false);
            isLoading = false;
        }
    }

    private void handleSuccessResponse(NotificationResponse response, int page) {
        try {
            if (response != null && response.getData() != null) {
                NotificationResponse.Data data = response.getData();
                if (data.getNotifications() != null) {
                    NotificationResponse.Notifications notifications = data.getNotifications();

                    // Log pagination info
                    Log.d(TAG, "═══════════════════════════════════════");
                    Log.d(TAG, "Pagination Info for page " + page + ":");
                    Log.d(TAG, "Current Page: " + notifications.getCurrent_page());
                    Log.d(TAG, "Last Page: " + notifications.getLast_page());
                    Log.d(TAG, "Per Page: " + notifications.getPer_page());
                    Log.d(TAG, "Total Items: " + notifications.getTotal());
                    Log.d(TAG, "Items in Response: " + (notifications.getData() != null ? notifications.getData().size() : 0));
                    Log.d(TAG, "From: " + notifications.getFrom());
                    Log.d(TAG, "To: " + notifications.getTo());
                    Log.d(TAG, "Next Page URL: " + notifications.getNext_page_url());
                    Log.d(TAG, "═══════════════════════════════════════");

                    // Update pagination info
                    currentPage = notifications.getCurrent_page();
                    lastPage = notifications.getLast_page();
                    isLastPage = currentPage >= lastPage;

                    Log.d(TAG, "Updated - currentPage: " + currentPage +
                            ", lastPage: " + lastPage +
                            ", isLastPage: " + isLastPage);

                    if (notifications.getData() != null && !notifications.getData().isEmpty()) {
                        Log.d(TAG, "Processing " + notifications.getData().size() + " items for page " + page);

                        if (page == 1) {
                            notificationAdapter.clearNotifications();
                            notificationAdapter.addNotifications(notifications.getData());
                            Log.d(TAG, "Replaced all data with page 1");
                        } else {
                            int positionStart = notificationAdapter.getItemCount();
                            notificationAdapter.addNotifications(notifications.getData());
                            Log.d(TAG, "Appended " + notifications.getData().size() +
                                    " items at position: " + positionStart);
                        }

                        updateEmptyState(false);

                        binding.rcNotification.post(() -> {
                            if (page == 1 && binding.rcNotification.getLayoutManager() != null) {
                                binding.rcNotification.scrollToPosition(0);
                            }
                        });

                        Log.d(TAG, "Total items in adapter: " + notificationAdapter.getItemCount());
                    } else {
                        Log.w(TAG, "No data in response for page: " + page);
                        if (page == 1) {
                            updateEmptyState(true);
                        }
                    }
                } else {
                    Log.w(TAG, "Notifications object is null for page: " + page);
                    if (page == 1) {
                        updateEmptyState(true);
                    }
                }
            } else {
                Log.w(TAG, "Response or data is null for page: " + page);
                if (page == 1) {
                    updateEmptyState(true);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "handleSuccessResponse error for page: " + page, e);
            showError("Error processing notifications");
        }
    }

    private void handleErrorResponse(Repository.ApiResponse<NotificationResponse> apiResponse) {
        try {
            if (apiResponse != null) {
                if (apiResponse.code == Repository.ERROR_SESSION_EXPIRED) {
                    showError("Session expired. Please login again.");
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
            Log.e(TAG, "handleErrorResponse error", e);
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
            Log.e(TAG, "updateEmptyState error", e);
        }
    }

    private void showLoading(boolean show) {
        try {
            if (binding.progressBar != null) {
                binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "showLoading error", e);
        }
    }

    private void showPaginationLoading(boolean show) {
        try {
            // If you have a separate loading indicator for pagination
            // binding.paginationProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            Log.e(TAG, "showPaginationLoading error", e);
        }
    }

    private void showError(String message) {
        try {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "showError error", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binding != null) {
            binding = null;
        }
        notificationAdapter = null;
    }

    private String getToken() {
        return pref.getPrefString(this, pref.user_token);
    }
}