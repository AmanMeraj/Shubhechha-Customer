package com.subh.shubhechha.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.subh.shubhechha.Activities.AddressBookActivity;
import com.subh.shubhechha.Activities.ContainerActivity;
import com.subh.shubhechha.Activities.IntroductionActivity;
import com.subh.shubhechha.Activities.LoginActivity;
import com.subh.shubhechha.Activities.MyOrdersActivity;
import com.subh.shubhechha.Activities.MyProfileActivity;
import com.subh.shubhechha.CustomBottomNavigation;
import com.subh.shubhechha.Model.ProfileResponse;
import com.subh.shubhechha.R;
import com.subh.shubhechha.Repository.Repository;
import com.subh.shubhechha.ViewModel.ViewModel;
import com.subh.shubhechha.databinding.FragmentProfileBinding;
import com.subh.shubhechha.utils.SharedPref;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ViewModel viewModel;
    private SharedPref pref;
    private boolean isLoadingProfile = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewModel and SharedPref
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        pref = new SharedPref();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup UI
        setupClickListeners();

        // Load profile data
        loadProfileData();
    }

    /**
     * Load user profile data from API
     */
    private void loadProfileData() {
        if (!isAdded() || getContext() == null) return;

        // Get token from SharedPreferences
        String token = pref.getPrefString(requireContext(), pref.user_token);

        if (token == null || token.trim().isEmpty()) {
            // Token not found, redirect to login
            redirectToLogin();
            return;
        }

        // Check if already loading
        if (isLoadingProfile) return;

        isLoadingProfile = true;

        // Show loading state (optional)
        showLoadingState();

        // Format authorization header
        String authHeader = "Bearer " + token;

        // Call API
        viewModel.profile(authHeader).observe(getViewLifecycleOwner(), response -> {
            isLoadingProfile = false;

            if (!isAdded() || binding == null) return;

            // Hide loading state
            hideLoadingState();

            if (response != null) {
                handleProfileResponse(response);
            } else {
                // Handle unexpected error silently
                loadCachedUserData();
            }
        });
    }

    /**
     * Handle profile API response
     */
    private void handleProfileResponse(@NonNull Repository.ApiResponse<ProfileResponse> response) {
        if (!isAdded() || binding == null) return;

        if (response.isSuccess() && response.data != null) {
            ProfileResponse profileResponse = response.data;

            if (profileResponse.getStatus() == 1) {
                // Success - Update UI with profile data
                if (profileResponse.getData() != null && profileResponse.getData().getUser() != null) {
                    updateProfileUI(profileResponse.getData().getUser());
                    saveUserDataToPrefs(profileResponse.getData().getUser());
                } else {
                    // Fallback to cached data
                    loadCachedUserData();
                }
            } else {
                // API returned error status
                loadCachedUserData();
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
        if (!isAdded() || getContext() == null) return;

        if (response.code == Repository.ERROR_SESSION_EXPIRED) {
            // Session expired, redirect to login
            showSessionExpiredDialog();
        } else {
            // Load cached data for other errors
            loadCachedUserData();
        }
    }

    /**
     * Update UI with user profile data
     */
    private void updateProfileUI(@NonNull ProfileResponse.User user) {
        if (!isAdded() || binding == null) return;

        // Update name
        if (user.getName() != null && !user.getName().trim().isEmpty()) {
            binding.tvName.setText(user.getName());
        } else {
            binding.tvName.setText("Guest User");
        }

        // Update mobile
        if (user.getMobile() != null && !user.getMobile().trim().isEmpty()) {
            binding.tvNumber.setText(user.getMobile());
        } else {
            binding.tvNumber.setText("");
        }

        // Update email (if you have a TextView for it)
        // binding.tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "");

        // Update profile image
        if (user.getImage_path() != null && !user.getImage_path().trim().isEmpty()) {
            loadProfileImage(user.getImage_path());
        } else {
            // Set default avatar
            setDefaultAvatar();
        }
    }

    /**
     * Load profile image using Glide
     */
    private void loadProfileImage(@NonNull String imagePath) {
        if (!isAdded() || getContext() == null || binding == null) return;

        try {
            Glide.with(this)
                    .load(imagePath)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.no_profile)
                    .error(R.drawable.no_profile)
                    .into(binding.profileImage);
        } catch (Exception e) {
            setDefaultAvatar();
        }
    }

    /**
     * Set default avatar image
     */
    private void setDefaultAvatar() {
        if (!isAdded() || binding == null) return;

        try {
            binding.profileImage.setImageResource(R.drawable.no_profile);
        } catch (Exception ignored) {}
    }

    /**
     * Load cached user data from SharedPreferences
     */
    private void loadCachedUserData() {
        if (!isAdded() || getContext() == null || binding == null) return;

        String cachedName = pref.getPrefString(requireActivity(), pref.user_name);
        String cachedMobile = pref.getPrefString(requireContext(), pref.user_mobile);

        binding.tvName.setText(cachedName != null ? cachedName : "Guest User");
        binding.tvNumber.setText(cachedMobile != null ? cachedMobile : "");

        // Set default avatar for cached data
        setDefaultAvatar();
    }

    /**
     * Save user data to SharedPreferences
     */
    private void saveUserDataToPrefs(@NonNull ProfileResponse.User user) {
        if (!isAdded() || getContext() == null) return;

        if (user.getName() != null && !user.getName().trim().isEmpty()) {
            pref.setPrefString(requireContext(), pref.user_name, user.getName());
        }

        if (user.getMobile() != null && !user.getMobile().trim().isEmpty()) {
            pref.setPrefString(requireContext(), pref.user_mobile, user.getMobile());
        }

        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            pref.setPrefString(requireContext(), pref.user_email, user.getEmail());
        }
    }

    /**
     * Show loading state
     */
    private void showLoadingState() {
        if (!isAdded() || binding == null) return;

        // Show progress bar
        if (binding.progressBar != null) {
            binding.progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hide loading state
     */
    private void hideLoadingState() {
        if (!isAdded() || binding == null) return;

        // Hide progress bar
        if (binding.progressBar != null) {
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Setup all click listeners
     */
    private void setupClickListeners() {
        if (binding == null) return;

        // My Profile
        binding.linearMyProfile.setOnClickListener(v -> {
            binding.linearMyProfile.postDelayed(() -> {
                if (!isAdded() || getActivity() == null) return;

                Intent intent = new Intent(getActivity(), MyProfileActivity.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }, 300);
        });

        // My Address
        binding.linearMyAddress.setOnClickListener(v -> {
            binding.linearMyAddress.postDelayed(() -> {
                if (!isAdded() || getActivity() == null) return;

                Intent intent = new Intent(getActivity(), AddressBookActivity.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }, 300);
        });

        // My Orders
        binding.linearMyOrders.setOnClickListener(v -> {
            binding.linearMyOrders.postDelayed(() -> {
                if (!isAdded() || getActivity() == null) return;

                Intent intent = new Intent(getActivity(), MyOrdersActivity.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }, 300);
        });

        // My Wallet
        binding.linearMyWallet.setOnClickListener(v -> {
            binding.linearMyWallet.postDelayed(() -> {
                if (!isAdded() || getActivity() == null) return;

            }, 300);
        });

        // Logout
        binding.linearLogout.setOnClickListener(v -> {
            binding.linearLogout.postDelayed(() -> {
                if (!isAdded() || getActivity() == null) return;

                showLogoutDialog();
            }, 300);
        });
    }


    /**
     * Show logout confirmation dialog
     */
    private void showLogoutDialog() {
        if (!isAdded() || getContext() == null) return;

        try {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
            builder.setTitle("Logout");
            builder.setMessage("Are you sure you want to logout?");

            builder.setPositiveButton("Yes", (dialog, which) -> {
                performLogout();
            });

            builder.setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            });

            builder.show();
        } catch (Exception e) {
            // Handle dialog creation errors
            e.printStackTrace();
        }
    }

    /**
     * Show session expired dialog
     */
    private void showSessionExpiredDialog() {
        if (!isAdded() || getContext() == null) return;

        try {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
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
        if (!isAdded() || getContext() == null) return;

        try {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                getActivity().finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh profile data when fragment is resumed
        if (!isLoadingProfile) {
            loadProfileData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Remove all pending callbacks to prevent crashes
        if (binding != null) {
            binding.linearMyProfile.removeCallbacks(null);
            binding.linearMyAddress.removeCallbacks(null);
            binding.linearMyOrders.removeCallbacks(null);
            binding.linearMyWallet.removeCallbacks(null);
            binding.linearLogout.removeCallbacks(null);
        }

        // Nullify binding to prevent memory leaks
        binding = null;
    }
    /**
     * Perform logout operation
     */

    private void performLogout() {
        if (!isAdded() || getContext() == null) return;

        // Clear all user data from SharedPreferences
        pref.setPrefBoolean(requireContext(), pref.login_status, false);
        pref.setPrefString(requireContext(), pref.user_token, "");
        pref.setPrefString(requireContext(), pref.user_name, "");
        pref.setPrefString(requireContext(), pref.user_mobile, "");
        pref.setPrefString(requireContext(), pref.user_email, "");
        pref.setPrefString(requireContext(), pref.user_type, "");
        pref.setPrefInteger(requireContext(), pref.cart_item, 0);
        pref.setPrefInteger(requireContext(), pref.cart_count, 0);

        // Reset intro flag to show intro screen again
        pref.setPrefBoolean(requireContext(), pref.is_intro_shown, false);

        // Redirect to intro activity
        redirectToIntro();
    }

    /**
     * Redirect to intro activity after logout
     */
    private void redirectToIntro() {
        if (!isAdded() || getContext() == null) return;

        try {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                getActivity().finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}