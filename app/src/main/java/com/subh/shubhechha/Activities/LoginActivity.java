package com.subh.shubhechha.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.subh.shubhechha.Model.LoginResponse;
import com.subh.shubhechha.Model.User;
import com.subh.shubhechha.R;
import com.subh.shubhechha.Repository.Repository;
import com.subh.shubhechha.ViewModel.ViewModel;
import com.subh.shubhechha.databinding.ActivityLoginBinding;
import com.subh.shubhechha.utils.Utility;

public class LoginActivity extends Utility {

    private ActivityLoginBinding binding;
    private ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Initialize binding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        // Setup UI
        setupWindowInsets();
        setupClickListeners();
    }

    /**
     * Setup edge-to-edge window insets
     */
    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Setup click listeners
     */
    private void setupClickListeners() {
        binding.btnContinue.setOnClickListener(v ->
                validateAndProceed());
    }

    /**
     * Validate input and proceed with login
     */
    private void validateAndProceed() {
        String phone = binding.etPhone.getText().toString().trim();

        // Check Internet connection
        if (!isInternetConnected(this)) {
            showSnackBar("No Internet connection!", false);
            return;
        }

        // Validate phone number
        if (phone.isEmpty()) {
            showSnackBar("Please enter your phone number!", false);
            binding.etPhone.requestFocus();
            return;
        }

        if (phone.length() != 10 || !phone.matches("[0-9]+")) {
            showSnackBar("Enter a valid 10-digit phone number!", false);
            binding.etPhone.requestFocus();
            return;
        }

        // Proceed with login API call
        performLogin(phone);
    }

    /**
     * Call login API through ViewModel
     */
    private void performLogin(String phone) {
        // Disable button to prevent multiple clicks
        binding.btnContinue.setEnabled(false);

        // Show loading state (optional - you can add a progress bar)
        binding.tvContinue.setText("Please wait...");

        // Create user object
        User user = new User();
        user.setPhone(phone);

        // Call API
        viewModel.login(user).observe(this, response -> {
            // Re-enable button
            binding.btnContinue.setEnabled(true);
            binding.tvContinue.setText("Continue");

            if (response != null) {
                handleLoginResponse(response, phone);
            } else {
                showSnackBar("Unexpected error occurred!", false);
            }
        });
    }

    /**
     * Handle login API response
     */
    private void handleLoginResponse(Repository.ApiResponse<LoginResponse> response, String phone) {
        if (response.isSuccess() && response.data != null) {
            LoginResponse loginResponse = response.data;

            if (loginResponse.getStatus() == 200 || loginResponse.getStatus() == 1) {
                // Success
                showSnackBar(loginResponse.getMessage() != null ?
                        loginResponse.getMessage() : "OTP sent successfully!", true);

                // Navigate to OTP screen
                navigateToOtpScreen(phone, loginResponse);
            } else {
                // API returned error status
                showSnackBar(loginResponse.getMessage() != null ?
                        loginResponse.getMessage() : "Failed to send OTP!", false);
            }
        } else {
            // Handle error response
            handleErrorResponse(response);
        }
    }

    /**
     * Handle error responses
     */
    private void handleErrorResponse(Repository.ApiResponse<LoginResponse> response) {
        if (response.code == Repository.ERROR_SESSION_EXPIRED) {
            showSnackBar("Session expired. Please try again!", false);
        } else if (response.message != null) {
            showSnackBar(response.message, false);
        } else {
            showSnackBar("Failed to send OTP. Please try again!", false);
        }
    }

    /**
     * Navigate to OTP verification screen
     */
    private void navigateToOtpScreen(String phone, LoginResponse loginResponse) {
        binding.getRoot().postDelayed(() -> {
            Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
            intent.putExtra("phone", phone);

            // Pass OTP if available (for testing/development)
            if (loginResponse.getData() != null) {
                intent.putExtra("otp", loginResponse.getData().getOtp());
            }

            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }, 500);
    }

    /**
     * Show a custom colored Snackbar
     */
    private void showSnackBar(String message, boolean success) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT);
        View snackView = snackbar.getView();
        int bgColor = success ? getColor(R.color.success_green) : getColor(R.color.error_red);
        snackView.setBackgroundColor(bgColor);
        snackbar.setTextColor(getColor(R.color.white));
        snackbar.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up binding to prevent memory leaks
        binding = null;
    }
}