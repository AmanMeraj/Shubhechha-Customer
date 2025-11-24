package com.subh.shubhechha.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.subh.shubhechha.Model.ProfileResponse;
import com.subh.shubhechha.R;
import com.subh.shubhechha.Repository.Repository;
import com.subh.shubhechha.ViewModel.ViewModel;
import com.subh.shubhechha.databinding.ActivityMyProfileBinding;
import com.subh.shubhechha.utils.Utility;

public class MyProfileActivity extends Utility {

    private static final String TAG = "MyProfileActivity";
    private ActivityMyProfileBinding binding;
    private ViewModel viewModel;
    private Uri selectedImageUri;
    private boolean isLoadingProfile = false;

    private static final String PREF_NAME = "UserData";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "mobile";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_IMAGE_URL = "imageUrl";

    // Activity Result Launchers
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String[]> storagePermissionLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViewModel();
        initializeActivityResultLaunchers();
        setupClickListeners();

        // Load profile data from API on create
        loadProfileData();
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
    }

    private void initializeActivityResultLaunchers() {
        // Gallery Launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        loadImageIntoView(selectedImageUri);
                        Log.d(TAG, "Image selected from gallery: " + selectedImageUri);
                    }
                }
        );

        // Camera Launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        loadImageIntoView(selectedImageUri);
                        Log.d(TAG, "Image captured from camera: " + selectedImageUri);
                    }
                }
        );

        // Storage Permission Launcher (for Android 13+)
        storagePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean allGranted = true;
                    for (Boolean granted : result.values()) {
                        if (!granted) {
                            allGranted = false;
                            break;
                        }
                    }

                    if (allGranted) {
                        openGallery();
                    } else {
                        showSnackBar("Storage permission is required to select images", false);
                    }
                }
        );

        // Camera Permission Launcher
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        openCamera();
                    } else {
                        showSnackBar("Camera permission is required", false);
                    }
                }
        );
    }

    private void setupClickListeners() {
        // Change Photo Button
        binding.btnChangePhoto.setOnClickListener(v -> showImagePickerDialog());

        // Update Button
        binding.btnUpdate.setOnClickListener(v -> validateAndUpdateProfile());

        // Back button
        binding.toolbar.backBtn.setOnClickListener(v -> finish());
    }

    /**
     * Load profile data from API
     */
    private void loadProfileData() {
        // Get token from SharedPreferences
        String token = pref.getPrefString(this, pref.user_token);

        if (token == null || token.trim().isEmpty()) {
            // Token not found, redirect to login
            redirectToLogin();
            return;
        }

        // Check if already loading
        if (isLoadingProfile) return;

        isLoadingProfile = true;

        // Show loading state
        showLoadingState();

        // Format authorization header
        String authHeader = "Bearer " + token;

        // Call API
        viewModel.profile(authHeader).observe(this, response -> {
            isLoadingProfile = false;

            // Hide loading state
            hideLoadingState();

            if (response != null) {
                handleProfileResponse(response);
            } else {
                // Handle unexpected error
                showSnackBar("Unable to load profile",false);
                loadCachedUserData();
            }
        });
    }

    /**
     * Handle profile API response
     */
    private void handleProfileResponse(@NonNull Repository.ApiResponse<ProfileResponse> response) {
        if (binding == null) return;

        if (response.isSuccess() && response.data != null) {
            ProfileResponse profileResponse = response.data;

            if (profileResponse.getStatus() == 1) {
                // Success - Update UI with profile data
                if (profileResponse.getData() != null && profileResponse.getData().getUser() != null) {
                    updateProfileUI(profileResponse.getData().getUser());

                    // Save to SharedPreferences
                    ProfileResponse.User user = profileResponse.getData().getUser();
                    saveUserDataToPrefs(user);
                } else {
                    showSnackBar("Profile data not available",false);
                }
            } else {
                String message = profileResponse.getMessage() != null ?
                        profileResponse.getMessage() : "Failed to load profile";
                showSnackBar(message,false);

            }
        } else {
            // Handle error response
            handleErrorResponse(response);
        }
    }

    /**
     * Handle error responses
     */
    private void handleErrorResponse(@NonNull Repository.ApiResponse<ProfileResponse> response) {
        if (response.code == Repository.ERROR_SESSION_EXPIRED) {
            // Session expired, redirect to login
            showSessionExpiredDialog();
        } else {
            // Load cached data for other errors
            String errorMsg = response.message != null ? response.message : "Failed to load profile";
            showSnackBar(errorMsg,false);
            loadCachedUserData();
        }
    }

    /**
     * Update UI with user profile data
     */
    private void updateProfileUI(@NonNull ProfileResponse.User user) {
        if (binding == null) return;

        // Update name
        if (user.getName() != null && !user.getName().trim().isEmpty()) {
            binding.etName.setText(user.getName());
        } else {
            binding.etName.setText("");
        }

        // Update email
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            binding.etEmail.setText(user.getEmail());
        } else {
            binding.etEmail.setText("");
        }

        // Update mobile
        if (user.getMobile() != null && !user.getMobile().trim().isEmpty()) {
            binding.etPhone.setText(user.getMobile());
        } else {
            binding.etPhone.setText("");
        }

        // Update profile image
        if (user.getImage_path() != null && !user.getImage_path().trim().isEmpty()) {
            loadProfileImage(user.getImage_path());
            // Save image URL to preferences
            saveProfileImageUrl(user.getImage_path());
        } else {
            // Set default avatar
            setDefaultAvatar();
        }

        Log.d(TAG, "Profile UI updated successfully");
    }

    /**
     * Load profile image using Glide
     */
    private void loadProfileImage(@NonNull String imagePath) {
        if (binding == null) return;

        try {
            Glide.with(this)
                    .load(imagePath)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.no_profile)
                    .error(R.drawable.no_profile)
                    .into(binding.profileImage);
        } catch (Exception e) {
            Log.e(TAG, "Error loading profile image: " + e.getMessage());
            setDefaultAvatar();
        }
    }

    /**
     * Set default avatar image
     */
    private void setDefaultAvatar() {
        if (binding == null) return;

        try {
            binding.profileImage.setImageResource(R.drawable.no_profile);
        } catch (Exception ignored) {}
    }

    /**
     * Load cached user data from SharedPreferences
     */
    private void loadCachedUserData() {
        String name = pref.getPrefString(this,pref.user_name);
        String email = pref.getPrefString(this,pref.user_email);
        String phone = pref.getPrefString(this,pref.user_mobile);
        String imageUrl = pref.getPrefString(this,pref.user_image);

        binding.etName.setText(name);
        binding.etEmail.setText(email);
        binding.etPhone.setText(phone);

        if (!TextUtils.isEmpty(imageUrl)) {
            loadProfileImage(imageUrl);
        } else {
            setDefaultAvatar();
        }

        Log.d(TAG, "Loaded cached user data");
    }

    /**
     * Save user data to SharedPreferences
     */
    private void saveUserDataToPrefs(ProfileResponse.User user) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (user.getName() != null) {
            editor.putString(KEY_NAME, user.getName());
            pref.setPrefString(this, pref.user_name, user.getName());
        }
        if (user.getEmail() != null) {
            editor.putString(KEY_EMAIL, user.getEmail());
            pref.setPrefString(this, pref.user_email, user.getEmail());
        }
        if (user.getMobile() != null) {
            editor.putString(KEY_PHONE, user.getMobile());
            pref.setPrefString(this, pref.user_mobile, user.getMobile());
        }
        if (user.getImage_path() != null) {
            editor.putString(KEY_IMAGE_URL, user.getImage_path());
        }

        editor.apply();
        Log.d(TAG, "User data saved to SharedPreferences");
    }

    /**
     * Save profile image URL to SharedPreferences
     */
    private void saveProfileImageUrl(String imageUrl) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        prefs.edit().putString(KEY_IMAGE_URL, imageUrl).apply();
    }

    /**
     * Show loading state
     */
    private void showLoadingState() {
        if (binding == null) return;

        if (binding.progressBar != null) {
            binding.progressBar.setVisibility(View.VISIBLE);  // ✅ Already implemented!
        }

        // Disable form fields
        binding.etName.setEnabled(false);
        binding.etEmail.setEnabled(false);
        binding.etPhone.setEnabled(false);
        binding.btnUpdate.setEnabled(false);
        binding.btnChangePhoto.setEnabled(false);
    }

    /**
     * Hide loading state
     */
    private void hideLoadingState() {
        if (binding == null) return;

        if (binding.progressBar != null) {
            binding.progressBar.setVisibility(View.GONE);  // ✅ Already implemented!
        }

        // Enable form fields
        binding.etName.setEnabled(true);
        binding.etEmail.setEnabled(true);
        binding.etPhone.setEnabled(true);
        binding.btnUpdate.setEnabled(true);
        binding.btnChangePhoto.setEnabled(true);
    }

// And the setUpdateLoadingState() method also controls the progress bar:

    private void setUpdateLoadingState(boolean isLoading) {
        binding.btnUpdate.setEnabled(!isLoading);

        if (isLoading) {
            binding.btnUpdate.setText("Updating...");

            // Disable form fields during update
            binding.etName.setEnabled(false);
            binding.etEmail.setEnabled(false);
            binding.etPhone.setEnabled(false);
            binding.btnChangePhoto.setEnabled(false);

            // Show progress bar if available
            if (binding.progressBar != null) {
                binding.progressBar.setVisibility(View.VISIBLE);  // ✅ Already implemented!
            }
        } else {
            binding.btnUpdate.setText("Update Profile");

            // Enable form fields after update
            binding.etName.setEnabled(true);
            binding.etEmail.setEnabled(true);
            binding.etPhone.setEnabled(true);
            binding.btnChangePhoto.setEnabled(true);

            // Hide progress bar if available
            if (binding.progressBar != null) {
                binding.progressBar.setVisibility(View.GONE);  // ✅ Already implemented!
            }
        }
    }


    private void showImagePickerDialog() {
        String[] options = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Profile Photo");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    checkCameraPermissionAndOpen();
                    break;
                case 1:
                    checkStoragePermissionAndOpen();
                    break;
                case 2:
                    dialog.dismiss();
                    break;
            }
        });
        builder.show();
    }

    /**
     * Check camera permission and open camera
     */
    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            openCamera();
        }
    }

    /**
     * Check storage permission based on Android version and open gallery
     */
    private void checkStoragePermissionAndOpen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ - Check for READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request the new granular media permissions
                storagePermissionLauncher.launch(new String[]{
                        Manifest.permission.READ_MEDIA_IMAGES
                });
            } else {
                openGallery();
            }
        } else {
            // Android 12 and below - Check for READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                storagePermissionLauncher.launch(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                });
            } else {
                openGallery();
            }
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(cameraIntent);
        } else {
            showSnackBar("No camera app found",false);
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    private void loadImageIntoView(Uri imageUri) {
        if (imageUri != null) {
            Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.dummy_profile)
                    .error(R.drawable.dummy_profile)
                    .circleCrop()
                    .into(binding.profileImage);
        }
    }

    private void validateAndUpdateProfile() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();

        // Reset errors
        binding.etName.setError(null);
        binding.etEmail.setError(null);
        binding.etPhone.setError(null);

        boolean isValid = true;

        // Validate Name
        if (TextUtils.isEmpty(name)) {
            binding.etName.setError("Name is required");
            binding.etName.requestFocus();
            isValid = false;
        } else if (name.length() < 3) {
            binding.etName.setError("Name must be at least 3 characters");
            binding.etName.requestFocus();
            isValid = false;
        } else if (!name.matches("^[a-zA-Z\\s]+$")) {
            binding.etName.setError("Name should contain only letters");
            binding.etName.requestFocus();
            isValid = false;
        }

        // Validate Email
        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError("Email is required");
            if (isValid) binding.etEmail.requestFocus();
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("Enter a valid email address");
            if (isValid) binding.etEmail.requestFocus();
            isValid = false;
        }

        // Validate Phone
        if (TextUtils.isEmpty(phone)) {
            binding.etPhone.setError("Phone number is required");
            if (isValid) binding.etPhone.requestFocus();
            isValid = false;
        } else {
            // Remove any spaces, dashes, or other formatting
            String cleanPhone = phone.replaceAll("[\\s\\-()]", "");

            // Check if it starts with country code or not
            if (cleanPhone.startsWith("+91")) {
                cleanPhone = cleanPhone.substring(3);
            } else if (cleanPhone.startsWith("91")) {
                cleanPhone = cleanPhone.substring(2);
            }

            // Validate 10 digit phone number
            if (!cleanPhone.matches("^[6-9]\\d{9}$")) {
                binding.etPhone.setError("Enter a valid 10-digit phone number");
                if (isValid) binding.etPhone.requestFocus();
                isValid = false;
            }
        }

        if (isValid) {
            updateProfile(name, email, phone);
        }
    }

    private void updateProfile(String name, String email, String phone) {
        String token = "Bearer " + pref.getPrefString(this, pref.user_token);

        if (TextUtils.isEmpty(token) || token.equals("Bearer ")) {
            showSnackBar("Session expired. Please login again",true);
            redirectToLogin();
            return;
        }

        // Show loading state
        setUpdateLoadingState(true);

        // Call ViewModel to update profile
        viewModel.updateProfile(token, name, email, phone, selectedImageUri, this)
                .observe(this, response -> {
                    setUpdateLoadingState(false);

                    if (response != null) {
                        if (response.isSuccess() && response.data != null) {
                            // Update successful
                            String message = response.data.getMessage() != null ?
                                    response.data.getMessage() : "Profile updated successfully";
                            showSnackBar(message,true);
                            Log.d(TAG, "Profile update successful, reloading data...");

                            // Clear selected image as it's now uploaded
                            selectedImageUri = null;

                            // Reload profile data to get fresh data from server
                            loadProfileData();

                        } else {
                            // Update failed
                            String errorMsg = response.message != null ?
                                    response.message : "Failed to update profile";
                            showSnackBar(errorMsg,false);
                            Log.e(TAG, "Profile update failed: " + errorMsg);
                        }
                    } else {
                        showSnackBar("Something went wrong. Please try again",false);
                        Log.e(TAG, "Update response is null");
                    }
                });
    }
    private void showSessionExpiredDialog() {
        try {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Session Expired");
            builder.setMessage("Your session has expired. Please login again.");
            builder.setCancelable(false);

            builder.setPositiveButton("Login", (dialog, which) -> {
                redirectToLogin();
            });

            builder.show();
        } catch (Exception e) {
            // Fallback to direct redirect
            redirectToLogin();
        }
    }

    /**
     * Redirect to login activity
     */
    private void redirectToLogin() {
        try {
            // Clear user session
            pref.setPrefBoolean(this, pref.login_status, false);
            pref.setPrefString(this, pref.user_token, "");
            pref.setPrefString(this, pref.user_name, "");
            pref.setPrefString(this, pref.user_mobile, "");
            pref.setPrefString(this, pref.user_email, "");
            pref.setPrefString(this, pref.user_type, "");
            pref.setPrefInteger(this, pref.cart_item, 0);

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error redirecting to login: " + e.getMessage());
            e.printStackTrace();
        }
    }

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
        binding = null;
    }
}