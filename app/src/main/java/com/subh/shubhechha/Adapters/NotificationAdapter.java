package com.subh.shubhechha.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.subh.shubhechha.Model.NotificationResponse;
import com.subh.shubhechha.databinding.ItemNotificationBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<NotificationResponse.NotificationItem> notificationList;
    private SimpleDateFormat inputFormat;
    private SimpleDateFormat outputFormat;

    public NotificationAdapter() {
        this.notificationList = new ArrayList<>();
        // Adjust these formats based on your API date format
        this.inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        this.outputFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new NotificationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        try {
            NotificationResponse.NotificationItem notification = notificationList.get(position);
            android.util.Log.d("NotificationAdapter", "Binding position: " + position +
                    ", Title: " + (notification != null ? notification.getTitle() : "null"));
            holder.bind(notification, inputFormat, outputFormat);
        } catch (Exception e) {
            android.util.Log.e("NotificationAdapter", "Error binding position: " + position, e);
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return notificationList != null ? notificationList.size() : 0;
    }

    public void setNotifications(List<NotificationResponse.NotificationItem> notifications) {
        this.notificationList = notifications != null ? notifications : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addNotifications(List<NotificationResponse.NotificationItem> notifications) {
        if (notifications != null && !notifications.isEmpty()) {
            int startPosition = this.notificationList.size();
            this.notificationList.addAll(notifications);
            android.util.Log.d("NotificationAdapter", "Added " + notifications.size() +
                    " items, total now: " + this.notificationList.size());
            notifyItemRangeInserted(startPosition, notifications.size());
        }
    }

    public void clearNotifications() {
        this.notificationList.clear();
        notifyDataSetChanged();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private ItemNotificationBinding binding;

        public NotificationViewHolder(@NonNull ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(NotificationResponse.NotificationItem notification,
                         SimpleDateFormat inputFormat,
                         SimpleDateFormat outputFormat) {
            try {
                // Set title
                binding.tvTitle.setText(notification.getTitle() != null ?
                        notification.getTitle() : "Notification");

                // Set description
                binding.tvSubtitle.setText(notification.getDescription() != null ?
                        notification.getDescription() : "");

                // Format and set date
                String formattedDate = formatDate(notification.getCreated_at(),
                        inputFormat, outputFormat);
                binding.tvDate.setText(formattedDate);

                // Set icon (you can customize based on notification type if needed)
                // binding.iconImage.setImageResource(R.drawable.subh_logo2);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String formatDate(String dateString, SimpleDateFormat inputFormat,
                                  SimpleDateFormat outputFormat) {
            if (dateString == null || dateString.isEmpty()) {
                return "";
            }

            try {
                Date date = inputFormat.parse(dateString);
                if (date != null) {
                    return outputFormat.format(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Return original string if parsing fails
            return dateString;
        }
    }
}