package com.subh.shubhechha.utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

    /**
     * Format timestamp to relative time (e.g., "2 min ago", "5 hours ago")
     * @param timestamp The timestamp string from API (e.g., "2024-12-16 10:30:00")
     * @return Formatted relative time string
     */
    public static String getRelativeTime(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return "N/A";
        }

        try {
            // Parse the timestamp (adjust format based on your API response)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(timestamp);

            if (date == null) {
                return "N/A";
            }

            long timeInMillis = date.getTime();
            long currentTimeInMillis = System.currentTimeMillis();
            long diffInMillis = currentTimeInMillis - timeInMillis;

            // Calculate time difference
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
            long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
            long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            if (minutes < 1) {
                return "Just now";
            } else if (minutes < 60) {
                return minutes + " min ago";
            } else if (hours < 24) {
                return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
            } else if (days < 7) {
                return days + " day" + (days > 1 ? "s" : "") + " ago";
            } else {
                // Return formatted date for older items
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                return dateFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "N/A";
        }
    }

    /**
     * Format timestamp to simple time display (e.g., "10:30 AM")
     * @param timestamp The timestamp string from API
     * @return Formatted time string
     */
    public static String formatTime(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return "N/A";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

            Date date = inputFormat.parse(timestamp);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "N/A";
    }

    /**
     * Format timestamp to date display (e.g., "16 Dec 2024")
     * @param timestamp The timestamp string from API
     * @return Formatted date string
     */
    public static String formatDate(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return "N/A";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

            Date date = inputFormat.parse(timestamp);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "N/A";
    }
}
