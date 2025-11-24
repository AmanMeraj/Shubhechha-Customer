package com.subh.shubhechha.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.subh.shubhechha.Adapters.NotificationAdapter;
import com.subh.shubhechha.Model.NotificationModel;
import com.subh.shubhechha.R;
import com.subh.shubhechha.databinding.ActivityNotificationBinding;
import com.subh.shubhechha.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends Utility {

    private ActivityNotificationBinding binding;
    private NotificationAdapter notificationAdapter;

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

            initializeViews();
            setupCollapsingToolbar();
            setupRecyclerView();
            loadNotifications();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to initialize notifications");
        }
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
        // Add any additional animations or state changes here
    }

    private void onToolbarExpanded() {
        // Called when toolbar is fully expanded
        // Add any additional animations or state changes here
    }

    private void setupRecyclerView() {
        try {
            // Setup RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            binding.rcNotification.setLayoutManager(layoutManager);
            binding.rcNotification.setHasFixedSize(true);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to setup RecyclerView", e);
        }
    }

    private void loadNotifications() {
        try {
            // Show loading state
            showLoading(true);

            // Sample data - Replace with your actual data source
            List<NotificationModel> notifications = getSampleNotifications();

            // Initialize adapter with data
            notificationAdapter = new NotificationAdapter(notifications);
            binding.rcNotification.setAdapter(notificationAdapter);

            // Update empty state
            updateEmptyState(notifications.isEmpty());

            // Hide loading
            showLoading(false);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load notifications");
            showLoading(false);
        }
    }

    private List<NotificationModel> getSampleNotifications() {
        List<NotificationModel> notifications = new ArrayList<>();

        notifications.add(new NotificationModel(
                "Order Confirmed",
                "Your order #12345 has been confirmed",
                "2 hours ago",
                R.drawable.subh_logo2
        ));

        notifications.add(new NotificationModel(
                "Out for Delivery",
                "Your order is out for delivery",
                "5 hours ago",
                R.drawable.subh_logo2
        ));

        notifications.add(new NotificationModel(
                "Special Offer",
                "Get 20% off on your next order",
                "1 day ago",
                R.drawable.subh_logo2
        ));

        notifications.add(new NotificationModel(
                "Payment Successful",
                "Payment of ₹850 received successfully",
                "2 days ago",
                R.drawable.subh_logo2
        ));

        notifications.add(new NotificationModel(
                "New Products Added",
                "Check out our new collection",
                "3 days ago",
                R.drawable.subh_logo2
        ));

        notifications.add(new NotificationModel(
                "Order Delivered",
                "Your order #12344 has been delivered",
                "5 days ago",
                R.drawable.subh_logo2
        ));

        return notifications;
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
}