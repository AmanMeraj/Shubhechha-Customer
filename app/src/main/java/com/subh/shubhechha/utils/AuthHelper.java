package com.subh.shubhechha.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.subh.shubhechha.Activities.LoginActivity;

public class AuthHelper {

    private static final String TAG = "AuthHelper";
    private SharedPref pref;

    public AuthHelper() {
        this.pref = new SharedPref();
    }

    /**
     * Check if user is logged in
     */
    public boolean isUserLoggedIn(Context context) {
        if (context == null) return false;

        boolean loginStatus = pref.getPrefBoolean(context, pref.login_status);
        String token = pref.getPrefString(context, pref.user_token);

        return loginStatus && token != null && !token.trim().isEmpty();
    }

    /**
     * Show login required dialog
     * @param context Activity context
     * @param onLoginClick Optional callback when login is clicked (can be null)
     */
    public void showLoginRequiredDialog(Context context, OnLoginClickListener onLoginClick) {
        if (context == null || !(context instanceof Activity)) return;

        Activity activity = (Activity) context;

        try {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            builder.setTitle("Login Required");
            builder.setMessage("Please log in to access the full features of Shubhechha!");
            builder.setCancelable(true);

            builder.setPositiveButton("Login", (dialog, which) -> {
                dialog.dismiss();
                if (onLoginClick != null) {
                    onLoginClick.onLoginClick();
                } else {
                    navigateToLogin(context);
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });

            if (!activity.isFinishing() && !activity.isDestroyed()) {
                builder.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigate to login screen
     */
    public void navigateToLogin(Context context) {
        if (context == null) return;

        try {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);

            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check login and execute action
     * @return true if user is logged in, false otherwise
     */
    public boolean checkLoginAndExecute(Context context, Runnable action) {
        if (isUserLoggedIn(context)) {
            if (action != null) {
                action.run();
            }
            return true;
        } else {
            showLoginRequiredDialog(context, null);
            return false;
        }
    }

    /**
     * Interface for login click callback
     */
    public interface OnLoginClickListener {
        void onLoginClick();
    }
}