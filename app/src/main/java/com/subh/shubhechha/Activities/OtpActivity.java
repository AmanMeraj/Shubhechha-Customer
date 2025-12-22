package com.subh.shubhechha.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.subh.shubhechha.Model.User;
import com.subh.shubhechha.Model.VerifyOtpResponse;
import com.subh.shubhechha.R;
import com.subh.shubhechha.Repository.Repository;
import com.subh.shubhechha.ViewModel.ViewModel;
import com.subh.shubhechha.databinding.ActivityOtpBinding;
import com.subh.shubhechha.utils.Utility;

public class OtpActivity extends Utility {

    private ActivityOtpBinding binding;
    private ViewModel viewModel;
    private String phoneNumber;
    private boolean isVerifying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Initialize binding
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
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

        // Setup OTP inputs and listeners
        setupOtpInputs();
        setupClickListeners();
        int receivedOtp = getIntent().getIntExtra("otp",0);
        String otp = String.valueOf(receivedOtp);

        Toast.makeText(this, "Your OTP is "+receivedOtp, Toast.LENGTH_LONG).show();
        binding.otp1.setText(String.valueOf(otp.charAt(0)));
        binding.otp1.setSelection(binding.otp1.getText().length());

        binding.otp2.setText(String.valueOf(otp.charAt(1)));
        binding.otp2.setSelection(binding.otp2.getText().length());

        binding.otp3.setText(String.valueOf(otp.charAt(2)));
        binding.otp3.setSelection(binding.otp3.getText().length());

        binding.otp4.setText(String.valueOf(otp.charAt(3)));
        binding.otp4.setSelection(binding.otp4.getText().length());



        // Initial button state
        updateButtonState(true);
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
            showSnackBar("Phone number not provided!", false);
            return false;
        }

        phoneNumber = phoneNumber.trim();

        if (phoneNumber.length() != 10 || !phoneNumber.matches("[0-9]+")) {
            showSnackBar("Invalid phone number!", false);
            return false;
        }

        return true;
    }

    /**
     * Setup OTP input fields with auto-focus behavior
     */
    private void setupOtpInputs() {

        setOtpBehaviour(binding.otp1, null, binding.otp2);
        setOtpBehaviour(binding.otp2, binding.otp1, binding.otp3);
        setOtpBehaviour(binding.otp3, binding.otp2, binding.otp4);
        setOtpBehaviour(binding.otp4, binding.otp3, null);

        binding.otp1.requestFocus();
    }

    private void setOtpBehaviour(EditText current, EditText previous, EditText next) {

        // TextWatcher to move forward
        current.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 1 && next != null) {
                    next.requestFocus();
                }

                checkAndUpdateButtonState();
            }
        });

        // Backspace handler
        current.setOnKeyListener((v, keyCode, event) -> {

            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {

                if (current.getText().toString().isEmpty() && previous != null) {
                    previous.setText("");
                    previous.requestFocus();
                    return true;
                }
            }
            return false;
        });
    }
    /**
     * Setup click listeners
     */
    private void setupClickListeners() {
        binding.btnContinue.setOnClickListener(v -> validateAndVerifyOtp());
    }

    /**
     * Validate OTP input and proceed with verification
     */
    private void validateAndVerifyOtp() {
        // Prevent multiple clicks
        if (isVerifying) {
            return;
        }

        // Check Internet connection
        if (!isInternetConnected(this)) {
            showSnackBar("No Internet connection!", false);
            return;
        }

        // Get OTP
        String otp = getOtp();

        // Validate OTP
        if (otp.length() != 4) {
            showSnackBar("Please enter complete 4-digit OTP!", false);
            return;
        }

        if (!otp.matches("[0-9]+")) {
            showSnackBar("OTP must contain only numbers!", false);
            return;
        }

        // Proceed with OTP verification
        performOtpVerification(otp);
    }

    /**
     * Call OTP verification API through ViewModel
     */
    private void performOtpVerification(String otp) {
        isVerifying = true;

        // Update UI to loading state
        updateButtonState(false);
        binding.tvContinue.setText("Verifying...");

        // Create user object with phone, OTP, and type
        User user = new User();
        user.setPhone(phoneNumber);
        user.setOtp(otp);
        user.setType("customer");  // Set user type as customer

        // Call API
        viewModel.otp(user).observe(this, response -> {
            isVerifying = false;

            // Restore button state
            updateButtonState(true);
            binding.tvContinue.setText("Continue");

            if (response != null) {
                handleOtpResponse(response);
            } else {
                showSnackBar("Unexpected error occurred!", false);
            }
        });
    }

    /**
     * Handle OTP verification API response
     */
    private void handleOtpResponse(@NonNull Repository.ApiResponse<VerifyOtpResponse> response) {
        if (response.isSuccess() && response.data != null) {
            VerifyOtpResponse otpResponse = response.data;

            if ( otpResponse.getStatus() == 1) {
                // Success - OTP verified
                String message = otpResponse.getMessage() != null ?
                        otpResponse.getMessage() : "OTP verified successfully!";
                showSnackBar(message, true);

                // Save token and user data if available
                if (otpResponse.getData() != null) {
                    saveUserData(otpResponse.getData());
                }

                // Navigate based on is_new flag
                navigateBasedOnUserStatus(otpResponse);
            } else {
                // API returned error status
                String errorMsg = otpResponse.getMessage() != null ?
                        otpResponse.getMessage() : "Invalid OTP. Please try again!";
                showSnackBar(errorMsg, false);
                clearOtpFields();
            }
        } else {
            // Handle error response
            handleErrorResponse(response);
        }
    }

    /**
     * Handle error responses
     */
    private void handleErrorResponse(@NonNull Repository.ApiResponse<VerifyOtpResponse> response) {
        if (response.code == Repository.ERROR_SESSION_EXPIRED) {
            showSnackBar("Session expired. Please login again!", false);
            navigateToLogin();
        } else if (response.message != null) {
            showSnackBar(response.message, false);
        } else {
            showSnackBar("OTP verification failed. Please try again!", false);
        }
        clearOtpFields();
    }

    /**
     * Save user data to SharedPreferences or local storage
     */
    private void saveUserData(VerifyOtpResponse.Data data) {
        if (data == null) return;

        // Save token
        if (data.getToken() != null && !data.getToken().isEmpty()) {

            pref.setPrefString(this,pref.user_token,data.getToken());
            pref.setPrefString(this,pref.user_name,data.getName());
            pref.setPrefString(this,pref.user_mobile,data.getMobile());
            pref.setPrefString(this,pref.user_type,data.getType());
            pref.setPrefInteger(this,pref.cart_item,data.getCart_count());
        }
    }

    /**
     * Navigate based on is_new flag from API response
     * is_new = 1: New user -> Register Activity
     * is_new = 0: Existing user -> Container Activity
     */
    private void navigateBasedOnUserStatus(VerifyOtpResponse otpResponse) {
        binding.getRoot().postDelayed(() -> {
            Intent intent;

            if (otpResponse.getData() != null && otpResponse.getData().getIs_new() == 1) {
                // New user - navigate to Register Activity
                intent = new Intent(OtpActivity.this, RegisterActivity.class);
                intent.putExtra("phone", phoneNumber);

                // Pass token if available
                if (otpResponse.getData().getToken() != null) {
                    intent.putExtra("token", otpResponse.getData().getToken());
                }
            } else {
                // Existing user - navigate to Container Activity (Home)
                intent = new Intent(OtpActivity.this, ContainerActivity.class);

                pref.setPrefBoolean(this,pref.login_status,true);
                pref.setPrefInteger(this,pref.cart_count,otpResponse.getData().getCart_count());

                // Pass user data
                if (otpResponse.getData() != null) {
                    intent.putExtra("user_name", otpResponse.getData().getName());
                }

                // Clear back stack for existing users
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }

            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, 500);
    }

    /**
     * Navigate back to login screen
     */
    private void navigateToLogin() {
        binding.getRoot().postDelayed(() -> {
            Intent intent = new Intent(OtpActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, 1000);
    }

    /**
     * Get complete OTP from all fields
     */
    @NonNull
    private String getOtp() {
        String otp1 = binding.otp1.getText() != null ? binding.otp1.getText().toString().trim() : "";
        String otp2 = binding.otp2.getText() != null ? binding.otp2.getText().toString().trim() : "";
        String otp3 = binding.otp3.getText() != null ? binding.otp3.getText().toString().trim() : "";
        String otp4 = binding.otp4.getText() != null ? binding.otp4.getText().toString().trim() : "";
        return otp1 + otp2 + otp3 + otp4;
    }

    /**
     * Clear all OTP fields
     */
    private void clearOtpFields() {
        binding.otp1.setText("");
        binding.otp2.setText("");
        binding.otp3.setText("");
        binding.otp4.setText("");
        binding.otp1.requestFocus();
    }

    /**
     * Update button enabled/disabled state
     */
    private void updateButtonState(boolean enabled) {
        binding.btnContinue.setEnabled(enabled);
        binding.btnContinue.setAlpha(enabled ? 1f : 0.5f);
    }

    /**
     * Check if all OTP fields are filled and update button state
     */
    private void checkAndUpdateButtonState() {
        String otp = getOtp();
        boolean shouldEnable = otp.length() == 4 && !isVerifying;
        updateButtonState(shouldEnable);
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

    /**
     * Custom TextWatcher for OTP fields with auto-focus navigation
     */
    private class OtpTextWatcher implements TextWatcher {
        private final View currentView;
        private final View previousView;
        private final View nextView;

        OtpTextWatcher(View currentView, View previousView, View nextView) {
            this.currentView = currentView;
            this.previousView = previousView;
            this.nextView = nextView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Not needed
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Not needed
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 1) {
                // Move to next field
                if (nextView != null) {
                    nextView.requestFocus();
                }
            } else if (s.length() == 0) {
                // Move to previous field
                if (previousView != null) {
                    previousView.requestFocus();
                }
            }

            // Update button state after any change
            checkAndUpdateButtonState();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up binding to prevent memory leaks
        binding = null;
    }
}