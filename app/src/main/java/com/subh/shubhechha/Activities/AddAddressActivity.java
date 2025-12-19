package com.subh.shubhechha.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.subh.shubhechha.ViewModel.ViewModel;
import com.subh.shubhechha.databinding.ActivityAddAddressBinding;
import com.subh.shubhechha.utils.Utility;

public class AddAddressActivity extends Utility {

    private ActivityAddAddressBinding binding;
    private ViewModel viewModel;
    private String selectedAddressType = "Home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeViewModel();
        setupWindowInsets();
        setupToolbar();
        setupAddressTypeSpinner();
        setupSubmitButton();
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.scrollView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupToolbar() {
        binding.toolbar.backBtn.setOnClickListener(v -> finish());
    }

    private void setupAddressTypeSpinner() {
        String[] addressTypes = {"Home", "Office", "Other"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, addressTypes) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                android.widget.TextView textView = (android.widget.TextView) view;
                textView.setTextColor(getResources().getColor(android.R.color.black));
                textView.setTextSize(14);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                android.widget.TextView textView = (android.widget.TextView) view;
                textView.setTextColor(getResources().getColor(android.R.color.black));
                textView.setPadding(16, 16, 16, 16);
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerAddressType.setAdapter(adapter);

        binding.spinnerAddressType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAddressType = addressTypes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedAddressType = "Home";
            }
        });
    }

    private void setupSubmitButton() {
        binding.btnSubmit.setOnClickListener(v -> validateAndSubmitForm());
    }

    private void validateAndSubmitForm() {
        String apartment = binding.etApartment.getText().toString().trim();
        String street = binding.etStreet.getText().toString().trim();
        String pincode = binding.etPincode.getText().toString().trim();
        String city = binding.etCity.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();

        // Validate apartment
        if (TextUtils.isEmpty(apartment)) {
            showFieldError(binding.etApartment, "Please enter apartment no.");
            return;
        }

        // Validate street
        if (TextUtils.isEmpty(street)) {
            showFieldError(binding.etStreet, "Please enter street address");
            return;
        }

        // Validate pincode
        if (TextUtils.isEmpty(pincode)) {
            showFieldError(binding.etPincode, "Please enter pincode");
            return;
        }
        if (pincode.length() != 6) {
            showFieldError(binding.etPincode, "Pincode must be 6 digits");
            return;
        }

        // Validate city
        if (TextUtils.isEmpty(city)) {
            showFieldError(binding.etCity, "Please enter city");
            return;
        }

        // Validate phone
        if (!validatePhoneNumber(phone)) {
            return;
        }

        // Check internet connection
        if (!isInternetConnected(this)) {
            showErrorSnackbar("No internet connection");
            return;
        }

        submitAddress(apartment, street, pincode, city, phone);
    }

    private boolean validatePhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone)) {
            showFieldError(binding.etPhone, "Please enter phone number");
            return false;
        }

        String cleanPhone = phone.replaceAll("[^0-9]", "");

        if (cleanPhone.length() != 10) {
            showFieldError(binding.etPhone, "Phone number must be 10 digits");
            return false;
        }

        if (!cleanPhone.matches("^[6-9]\\d{9}$")) {
            showFieldError(binding.etPhone, "Enter valid Indian mobile number");
            return false;
        }

        return true;
    }

    private void showFieldError(android.widget.EditText field, String message) {
        field.setError(message);
        field.requestFocus();
    }

    private void submitAddress(String apartment, String street, String pincode, String city, String phone) {
        showLoading();

        PostAddress postAddress = new PostAddress();
        postAddress.setFlat_number(apartment);
        postAddress.setAddress(street);
        postAddress.setPincode(pincode);
        postAddress.setMobile(phone);
        postAddress.setType(selectedAddressType.toLowerCase());
        postAddress.setCountry("India");

        // Add city if your PostAddress model has a city field
         postAddress.setCity(city);

        String authToken = "Bearer " + getAuthToken();

        viewModel.postAddress(authToken, postAddress).observe(this, apiResponse -> {
            hideLoading();

            if (apiResponse == null) {
                showErrorSnackbar("Something went wrong. Please try again.");
                return;
            }

            if (apiResponse.data != null && apiResponse.data.status == 1) {
                PostAddressResponse resp = apiResponse.data;
                handleSuccessResponse(resp);
            } else {
                String errorMsg = apiResponse.message != null ?
                        apiResponse.message : "Failed to add address";
                showErrorSnackbar(errorMsg);
            }
        });
    }

    private void handleSuccessResponse(PostAddressResponse response) {
        showSuccessSnackbar(response.getMessage());

        binding.btnSubmit.postDelayed(() -> {
            Intent intent = new Intent(AddAddressActivity.this, AddressBookActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, 1200);
    }

    private void showSuccessSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();

        snackbarView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.setMargins(32, 32, 32, 32);
        snackbarView.setLayoutParams(params);
        snackbarView.setBackground(getDrawable(R.drawable.snackbar_success_bg));

        snackbar.show();
    }

    private void showErrorSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();

        snackbarView.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.setMargins(32, 32, 32, 32);
        snackbarView.setLayoutParams(params);
        snackbarView.setBackground(getDrawable(R.drawable.snackbar_error_bg));

        snackbar.show();
    }

    private String getAuthToken() {
        return pref.getPrefString(this, pref.user_token);
    }

    private void showLoading() {
        binding.btnSubmit.setEnabled(false);
        binding.btnSubmit.setAlpha(0.6f);
    }

    private void hideLoading() {
        binding.btnSubmit.setEnabled(true);
        binding.btnSubmit.setAlpha(1.0f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}