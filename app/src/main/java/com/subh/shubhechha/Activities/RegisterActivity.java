package com.subh.shubhechha.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.subh.shubhechha.Model.RegisterUserResponse;
import com.subh.shubhechha.Model.User;
import com.subh.shubhechha.R;
import com.subh.shubhechha.Repository.Repository;
import com.subh.shubhechha.ViewModel.ViewModel;
import com.subh.shubhechha.databinding.ActivityRegisterBinding;
import com.subh.shubhechha.utils.Utility;

public class RegisterActivity extends Utility {
    private ActivityRegisterBinding binding;
    private ViewModel viewModel;
    private String phoneNumber;
    private boolean isRegistering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        // Setup UI
        setupWindowInsets();

        // Retrieve and validate phone number
        if (!retrieveAndValidatePhone()) {
            finish();
            return;
        }

        // Setup listeners
        setupTextWatchers();
        setupClickListeners();

        // Initial button state
        updateButtonState(false);
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
     * Retrieve and validate phone number from intent
     */
    private boolean retrieveAndValidatePhone() {
        phoneNumber = getIntent().getStringExtra("phone");

        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            showSnackBar(binding.getRoot(), "Phone number not provided!", false);
            return false;
        }

        phoneNumber = phoneNumber.trim();

        if (phoneNumber.length() != 10 || !phoneNumber.matches("[0-9]+")) {
            showSnackBar(binding.getRoot(), "Invalid phone number received!", false);
            return false;
        }

        return true;
    }

    /**
     * Setup text watchers for input validation
     */
    private void setupTextWatchers() {
        binding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAndUpdateButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Setup click listeners
     */
    private void setupClickListeners() {
        binding.btnContinue.setOnClickListener(v -> validateAndRegister());
    }

    /**
     * Validate inputs and proceed with registration
     */
    private void validateAndRegister() {
        // Prevent multiple clicks
        if (isRegistering) {
            return;
        }

        // Check Internet connection
        if (!isInternetConnected(this)) {
            showSnackBar(binding.getRoot(), "No Internet connection!", false);
            return;
        }

        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();

        // Validate name
        if (name.isEmpty()) {
            showSnackBar(binding.getRoot(), "Please enter your name!", false);
            binding.etName.requestFocus();
            return;
        }

        if (name.length() < 3) {
            showSnackBar(binding.getRoot(), "Name must be at least 3 characters!", false);
            binding.etName.requestFocus();
            return;
        }

        // Validate email if provided
        if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showSnackBar(binding.getRoot(), "Please enter a valid email address!", false);
            binding.etEmail.requestFocus();
            return;
        }

        // Proceed with registration
        performRegistration(name, email);
    }

    /**
     * Call registration API through ViewModel
     */
    private void performRegistration(String name, String email) {
        isRegistering = true;

        // Update UI to loading state
        updateButtonState(false);
        // You can add a progress indicator here if needed

        // Create user object
        User user = new User();
        user.setName(name);
        user.setPhone(phoneNumber);
        user.setType("customer");

        // Only set email if provided
        if (!email.isEmpty()) {
            user.setEmail(email);
        }

        // Call API
        viewModel.register(user).observe(this, response -> {
            isRegistering = false;

            // Restore button state
            updateButtonState(true);

            if (response != null) {
                handleRegistrationResponse(response, name);
            } else {
                showSnackBar(binding.getRoot(), "Unexpected error occurred!", false);
            }
        });
    }

    /**
     * Handle registration API response
     */
    private void handleRegistrationResponse(@NonNull Repository.ApiResponse<RegisterUserResponse> response, String name) {
        if (response.isSuccess() && response.data != null) {
            RegisterUserResponse registerResponse = response.data;

            if (registerResponse.getStatus() == 1) {
                // Success - Registration completed
                String message = registerResponse.getMessage() != null ?
                        registerResponse.getMessage() : "Registration successful!";

                showSnackBar(binding.getRoot(), "Welcome " + name + "!", true);

                // Save user data if available
                if (registerResponse.getData() != null) {
                    saveUserData(registerResponse.getData());
                }

                // Navigate to Container Activity
                navigateToHome(registerResponse);
            } else {
                // API returned error status
                String errorMsg = registerResponse.getMessage() != null ?
                        registerResponse.getMessage() : "Registration failed. Please try again!";
                showSnackBar(binding.getRoot(), errorMsg, false);
            }
        } else {
            // Handle error response
            handleErrorResponse(response);
        }
    }

    /**
     * Handle error responses
     */
    private void handleErrorResponse(@NonNull Repository.ApiResponse<RegisterUserResponse> response) {
        if (response.code == Repository.ERROR_SESSION_EXPIRED) {
            showSnackBar(binding.getRoot(), "Session expired. Please login again!", false);
            navigateToLogin();
        } else if (response.message != null) {
            showSnackBar(binding.getRoot(), response.message, false);
        } else {
            showSnackBar(binding.getRoot(), "Registration failed. Please try again!", false);
        }
    }

    /**
     * Save user data to SharedPreferences
     */
    private void saveUserData(RegisterUserResponse.Data data) {
        if (data == null) return;

        // Save token
        if (data.getToken() != null && !data.getToken().isEmpty()) {
            pref.setPrefString(this, pref.user_token, data.getToken());
        }

        // Save user details
        if (data.getName() != null) {
            pref.setPrefString(this, pref.user_name, data.getName());
        }

        if (data.getMobile() != null) {
            pref.setPrefString(this, pref.user_mobile, data.getMobile());
        }

        if (data.getType() != null) {
            pref.setPrefString(this, pref.user_type, data.getType());
        }

        // Save cart count if available
        pref.setPrefInteger(this, pref.cart_item, data.getCart_count());
    }

    /**
     * Navigate to Home/Container Activity after successful registration
     */
    private void navigateToHome(RegisterUserResponse registerResponse) {
        binding.getRoot().postDelayed(() -> {
            Intent intent = new Intent(RegisterActivity.this, ContainerActivity.class);
            pref.setPrefBoolean(this,pref.login_status,true);


            // Pass user data
            if (registerResponse.getData() != null) {
                intent.putExtra("user_name", registerResponse.getData().getName());
                intent.putExtra("cart_count", registerResponse.getData().getCart_count());
            }

            // Clear back stack for new users
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, 600);
    }

    /**
     * Navigate back to login screen
     */
    private void navigateToLogin() {
        binding.getRoot().postDelayed(() -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, 1000);
    }

    /**
     * Check if name is valid and update button state
     */
    private void checkAndUpdateButtonState() {
        String name = binding.etName.getText().toString().trim();
        boolean shouldEnable = !name.isEmpty() && name.length() >= 3 && !isRegistering;
        updateButtonState(shouldEnable);
    }

    /**
     * Update button enabled/disabled state
     */
    private void updateButtonState(boolean enabled) {
        binding.btnContinue.setEnabled(enabled);
        binding.btnContinue.setAlpha(enabled ? 1f : 0.5f);
    }

    /**
     * Displays a floating rounded Snackbar
     */
    private void showSnackBar(View view, String message, boolean success) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        View snackView = snackbar.getView();

        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(25f);
        bg.setColor(success ? Color.parseColor("#4CAF50") : Color.parseColor("#F44336"));

        snackView.setBackground(bg);
        float translationY = -50 * getResources().getDisplayMetrics().density;
        snackView.setTranslationY(translationY);
        snackbar.setTextColor(Color.WHITE);
        snackbar.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up binding to prevent memory leaks
        binding = null;
    }
}