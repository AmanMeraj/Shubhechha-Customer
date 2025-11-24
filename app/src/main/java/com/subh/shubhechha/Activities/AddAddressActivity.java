package com.subh.shubhechha.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.subh.shubhechha.Model.PostAddress;
import com.subh.shubhechha.Model.PostAddressResponse;
import com.subh.shubhechha.R;
import com.subh.shubhechha.Repository.Repository;
import com.subh.shubhechha.ViewModel.ViewModel;
import com.subh.shubhechha.databinding.ActivityAddAddressBinding;
import com.subh.shubhechha.utils.Utility;

public class AddAddressActivity extends Utility {

    ActivityAddAddressBinding binding;
    ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button
        binding.toolbar.backBtn.setOnClickListener(v -> finish());

        // Submit button click validation
        binding.btnSubmit.setOnClickListener(v -> validateForm());
    }

    private void validateForm() {
        String apartment = binding.etApartment.getText().toString().trim();
        String street = binding.etStreet.getText().toString().trim();
        String pincode = binding.etPincode.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();

        // Apartment validation
        if (TextUtils.isEmpty(apartment)) {
            binding.etApartment.setError("Please enter apartment no.");
            binding.etApartment.requestFocus();
            return;
        }

        // Street validation
        if (TextUtils.isEmpty(street)) {
            binding.etStreet.setError("Please enter street address");
            binding.etStreet.requestFocus();
            return;
        }

        // Pincode validation
        if (TextUtils.isEmpty(pincode)) {
            binding.etPincode.setError("Please enter pincode");
            binding.etPincode.requestFocus();
            return;
        } else if (pincode.length() != 6) {
            binding.etPincode.setError("Pincode must be 6 digits");
            binding.etPincode.requestFocus();
            return;
        }

        // Phone validation - Indian numbers only (10 digits starting with 6-9)
        if (TextUtils.isEmpty(phone)) {
            binding.etPhone.setError("Please enter phone number");
            binding.etPhone.requestFocus();
            return;
        }

        // Remove spaces and special characters
        String cleanPhone = phone.replaceAll("[^0-9]", "");

        if (cleanPhone.length() != 10) {
            binding.etPhone.setError("Phone number must be 10 digits");
            binding.etPhone.requestFocus();
            return;
        }

        if (!cleanPhone.matches("^[6-9]\\d{9}$")) {
            binding.etPhone.setError("Enter valid Indian mobile number");
            binding.etPhone.requestFocus();
            return;
        }

        if (isInternetConnected(this)){
            submitAddress(apartment,street,pincode,phone);
        }else {
            showErrorSnackbar("No internet connection");
        }
        // If all fields are valid, submit address
    }

    private void submitAddress(String apartment, String street, String pincode, String phone) {

        showLoading();

        // Create PostAddress object
        PostAddress postAddress = new PostAddress();
        postAddress.setFlat_number(apartment);
        postAddress.setAddress(street);
        postAddress.setPincode(pincode);
        postAddress.setMobile(phone);
        postAddress.setType("home");
        postAddress.setCountry("India");

        String authToken = "Bearer " + getAuthToken();

        viewModel.postAddress(authToken, postAddress).observe(this, apiResponse -> {

            if (apiResponse == null) {
                hideLoading();
                showErrorSnackbar("Something went wrong. Please try again.");
                return;
            }
            if (apiResponse.data.status == 1) {

                hideLoading();

                PostAddressResponse resp = apiResponse.data;

                // Backend status check
                if (resp != null && resp.getStatus() == 1) {

                    showSuccessSnackbar(resp.getMessage());

                    // Navigate
                    binding.btnSubmit.postDelayed(() -> {
                        Intent intent = new Intent(AddAddressActivity.this, AddressBookActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }, 1200);

                } else {
                        hideLoading();
                        String msg = apiResponse.message != null ? apiResponse.message : "Failed to add address";
                        showErrorSnackbar(msg);
                }
            }
        });
    }


    private void showSuccessSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();

        // Set background color to green
        snackbarView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));

        // Set rounded corners
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.setMargins(32, 32, 32, 32);
        snackbarView.setLayoutParams(params);
        snackbarView.setBackground(getDrawable(R.drawable.snackbar_success_bg));

        snackbar.show();
    }

    private void showErrorSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();

        // Set background color to red
        snackbarView.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));

        // Set rounded corners
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.setMargins(32, 32, 32, 32);
        snackbarView.setLayoutParams(params);
        snackbarView.setBackground(getDrawable(R.drawable.snackbar_error_bg));

        snackbar.show();
    }

    private String getAuthToken() {
        return pref.getPrefString(this,pref.user_token);
    }

    private void showLoading() {
        binding.btnSubmit.setEnabled(false);
        binding.btnSubmit.setAlpha(0.6f);
    }

    private void hideLoading() {
        binding.btnSubmit.setEnabled(true);
        binding.btnSubmit.setAlpha(1.0f);
    }
}