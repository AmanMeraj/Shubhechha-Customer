package com.subh.shubhechha.Fragments;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.subh.shubhechha.Activities.ContainerActivity;
import com.subh.shubhechha.Activities.ShopsActivity;
import com.subh.shubhechha.Adapters.BannerAdapter;
import com.subh.shubhechha.Adapters.CategoryHorizontalAdapter;
import com.subh.shubhechha.Adapters.FooterAdapter;
import com.subh.shubhechha.CurvedBottomDrawable;
import com.subh.shubhechha.Model.HomeResponse;
import com.subh.shubhechha.Model.UpdateFcm;
import com.subh.shubhechha.R;
import com.subh.shubhechha.ViewModel.ViewModel;
import com.subh.shubhechha.databinding.FragmentHomeBinding;
import com.subh.shubhechha.utils.SharedPref;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private ViewModel viewModel;
    private CategoryHorizontalAdapter horizontalAdapter;
    SharedPref pref = new SharedPref();

    // Banner related
    private BannerAdapter bannerAdapter;
    private FooterAdapter footerAdapter;
    private Handler bannerHandler;
    private Runnable bannerRunnable;
    private ImageView[] indicators;

    // Curved background
    private CurvedBottomDrawable curvedDrawable;

    // GPS related
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private ActivityResultLauncher<String[]> locationPermissionLauncher;
    private ActivityResultLauncher<IntentSenderRequest> gpsSettingsLauncher;
    private boolean isLocationObtained = false;
    private Geocoder geocoder;

    private static final int GRID_SPAN_COUNT = 3;
    private static final long AUTO_SCROLL_DELAY = 3000;
    private static final float MAX_CURVE_AMOUNT = 0.5f;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLocationPermissionLauncher();
        setupGpsSettingsLauncher();
        geocoder = new Geocoder(requireContext(), Locale.getDefault());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.d("TAG", "onComplete: "+token);
                        updateFCM(token);
                    }
                });
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar();
        setupCollapsingBehavior();
        setupFeaturedCategoriesRecyclerView();

        // Check and request location before loading data
        checkAndRequestLocation();
    }

    private void setupLocationPermissionLauncher() {
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fineLocationGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                    Boolean coarseLocationGranted = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);

                    if (fineLocationGranted != null && fineLocationGranted) {
                        checkGpsSettings();
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                        checkGpsSettings();
                    } else {
                        showPermissionDeniedDialog();
                    }
                }
        );
    }

    private void setupGpsSettingsLauncher() {
        gpsSettingsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                        getCurrentLocation();
                    } else {
                        showGpsRequiredDialog();
                    }
                }
        );
    }

    private void checkAndRequestLocation() {
        if (hasLocationPermission()) {
            checkGpsSettings();
        } else {
            requestLocationPermission();
        }


    }
    private void updateFCM(String fcm) {
        UpdateFcm updateFcm = new UpdateFcm();
        updateFcm.setFcm_token(fcm);

        String auth="Bearer "+pref.getPrefString(requireActivity(),pref.user_token);
        Log.d("TAG", "getLoggedIn: "+auth);
        viewModel.addFcm(auth,updateFcm).observe(requireActivity(), response -> {

        });
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        locationPermissionLauncher.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private void checkGpsSettings() {
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .build();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(requireActivity())
                .checkLocationSettings(builder.build());

        task.addOnSuccessListener(locationSettingsResponse -> {
            getCurrentLocation();
        });

        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(
                            resolvable.getResolution()).build();
                    gpsSettingsLauncher.launch(intentSenderRequest);
                } catch (Exception sendEx) {
                    Log.e(TAG, "Error requesting GPS settings", sendEx);
                    showGpsRequiredDialog();
                }
            } else {
                showGpsRequiredDialog();
            }
        });
    }

    private void getCurrentLocation() {
        if (!hasLocationPermission()) {
            requestLocationPermission();
            return;
        }

        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            saveLocationAndAddress(location);
                            loadHomeData();
                        } else {
                            requestFreshLocation();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to get location", e);
                        requestFreshLocation();
                    });
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception when getting location", e);
            requestLocationPermission();
        }
    }

    private void requestFreshLocation() {
        if (!hasLocationPermission()) {
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .setMaxUpdates(1)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    saveLocationAndAddress(location);
                    fusedLocationClient.removeLocationUpdates(this);
                    if (!isLocationObtained) {
                        isLocationObtained = true;
                        loadHomeData();
                    }
                }
            }
        };

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback, Looper.getMainLooper());
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception when requesting location updates", e);
        }
    }

    private void saveLocationAndAddress(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Save coordinates
        pref.setPrefString(requireActivity(), pref.user_lat, String.valueOf(latitude));
        pref.setPrefString(requireActivity(), pref.user_long, String.valueOf(longitude));

        Log.d(TAG, "Location saved - Lat: " + latitude + ", Long: " + longitude);

        // Get and save address
        getAddressFromLocation(latitude, longitude);
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        new Thread(() -> {
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);

                    // Build formatted address
                    StringBuilder addressBuilder = new StringBuilder();

                    // Get specific parts
                    String subLocality = address.getSubLocality(); // Neighborhood/Area
                    String locality = address.getLocality(); // City
                    String subAdminArea = address.getSubAdminArea(); // District
                    String adminArea = address.getAdminArea(); // State
                    String postalCode = address.getPostalCode();

                    // Build short address (Area, City)
                    if (subLocality != null && !subLocality.isEmpty()) {
                        addressBuilder.append(subLocality);
                    }
                    if (locality != null && !locality.isEmpty()) {
                        if (addressBuilder.length() > 0) addressBuilder.append(", ");
                        addressBuilder.append(locality);
                    }

                    String shortAddress = addressBuilder.toString();

                    // Build full address
                    addressBuilder = new StringBuilder();
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        if (i > 0) addressBuilder.append(", ");
                        addressBuilder.append(address.getAddressLine(i));
                    }
                    String fullAddress = addressBuilder.toString();

                    // Save to preferences
                    requireActivity().runOnUiThread(() -> {
                        pref.setPrefString(requireActivity(), pref.user_address,
                                fullAddress.isEmpty() ? "Location obtained" : fullAddress);
                        pref.setPrefString(requireActivity(), pref.user_short_address,
                                shortAddress.isEmpty() ? locality : shortAddress);
                        pref.setPrefString(requireActivity(), pref.user_city,
                                locality != null ? locality : "");
                        pref.setPrefString(requireActivity(), pref.user_state,
                                adminArea != null ? adminArea : "");
                        pref.setPrefString(requireActivity(), pref.user_postal_code,
                                postalCode != null ? postalCode : "");

                        Log.d(TAG, "Address saved: " + fullAddress);

                        // Update address in ContainerActivity
                        updateAddressInContainer(shortAddress.isEmpty() ? locality : shortAddress);

                        Toast.makeText(getContext(), "Location updated", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        pref.setPrefString(requireActivity(), pref.user_address, "Location obtained");
                        pref.setPrefString(requireActivity(), pref.user_short_address, "Current Location");
                        Log.w(TAG, "No address found for location");
                        updateAddressInContainer("Current Location");
                    });
                }
            } catch (IOException e) {
                Log.e(TAG, "Geocoder error", e);
                requireActivity().runOnUiThread(() -> {
                    pref.setPrefString(requireActivity(), pref.user_address, "Location obtained");
                    pref.setPrefString(requireActivity(), pref.user_short_address, "Current Location");
                    updateAddressInContainer("Current Location");
                });
            }
        }).start();
    }

    private void updateAddressInContainer(String address) {
        if (getActivity() instanceof ContainerActivity) {
            ((ContainerActivity) getActivity()).updateAddress(address);
        }

    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Location Permission Required")
                .setMessage("This app needs location permission to show nearby shops and services. Please grant location permission.")
                .setCancelable(false)
                .setPositiveButton("Grant Permission", (dialog, which) -> {
                    requestLocationPermission();
                })
                .setNegativeButton("Exit", (dialog, which) -> {
                    requireActivity().finish();
                })
                .show();
    }

    private void showGpsRequiredDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("GPS Required")
                .setMessage("Please enable GPS to use this app and find nearby shops and services.")
                .setCancelable(false)
                .setPositiveButton("Enable GPS", (dialog, which) -> {
                    checkGpsSettings();
                })
                .setNegativeButton("Exit", (dialog, which) -> {
                    requireActivity().finish();
                })
                .show();
    }

    private void loadHomeData() {
        showLoading(true);
        String token = pref.getPrefString(requireActivity(), pref.user_token);

        if (token.isEmpty()) {
            Log.e(TAG, "Auth token not found");
            showLoading(false);
            Toast.makeText(getContext(), "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;

        viewModel.home(authHeader).observe(getViewLifecycleOwner(), response -> {
            showLoading(false);

            if (response != null) {
                if (response.isSuccess() && response.data != null) {
                    handleSuccessResponse(response.data);
                } else {
                    String errorMsg = response.message != null ?
                            response.message : "Failed to load data";
                    Log.e(TAG, "API Error: " + errorMsg);
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Response is null");
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSuccessResponse(HomeResponse homeResponse) {
        HomeResponse.Data data = homeResponse.getData();

        if (data != null) {
            try {
                int cartCount = data.getCart_count();
                pref.setPrefInteger(requireActivity(), pref.cart_count, cartCount);
                Log.d(TAG, "Cart count saved: " + cartCount);
                notifyCartBadgeUpdate();
            } catch (Exception e) {
                Log.e(TAG, "Error saving cart count", e);
            }
            if(pref.getPrefString(requireActivity(),pref.user_name).isEmpty()||pref.getPrefString(requireActivity(),pref.user_name).matches("")){
                binding.greetingText.setText("Hii! User");
            }else{
                binding.greetingText.setText("Hii! " + pref.getPrefString(requireActivity(),pref.user_name));
            }

            if (data.getBanners() != null && !data.getBanners().isEmpty()) {
                setupBannerFromApi(data.getBanners());
            } else {
                Log.e(TAG, "Banner data is empty");
            }

            if(data.footer_banners != null ){
                setupFooterBannerFromApi(data.getFooter_banners());
            } else {
                Log.e(TAG, "Banner data is empty");
            }

            if (data.getModules() != null && !data.getModules().isEmpty()) {
                updateCategoriesFromApi(data.getModules());
            }
        } else {
            Log.e(TAG, "Data is null");
        }
    }

    private void notifyCartBadgeUpdate() {
        if (getActivity() instanceof ContainerActivity) {
            int cartCount = pref.getPrefInteger(requireActivity(), pref.cart_count);
            ((ContainerActivity) getActivity()).updateCartBadge(cartCount);

            Intent intent = new Intent("UPDATE_CART_BADGE");
            intent.putExtra("cart_count", cartCount);
            requireActivity().sendBroadcast(intent);
        }
    }

    private void setupBannerFromApi(ArrayList<HomeResponse.Banner> apiBanners) {
        bannerAdapter = new BannerAdapter(apiBanners);
        bannerAdapter.setOnBannerClickListener((banner, position) -> {
            Toast.makeText(getContext(),
                    "Banner: " + banner.getModule_service() + " clicked",
                    Toast.LENGTH_SHORT).show();
        });

        binding.bannerViewPager.setAdapter(bannerAdapter);
        binding.bannerViewPager.setClipToPadding(false);
        binding.bannerViewPager.setClipChildren(false);
        binding.bannerViewPager.setOffscreenPageLimit(3);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int cardWidth = (int) (screenWidth * 0.82f);
        int padding = (screenWidth - cardWidth) / 2;

        binding.bannerViewPager.setPadding(padding, 0, padding, 0);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(16));
        transformer.addTransformer((page, position) -> {
            float absPosition = Math.abs(position);
            if (absPosition >= 1) {
                page.setScaleY(0.82f);
            } else {
                float scale = 0.85f + (1 - absPosition) * 0.15f;
                page.setScaleY(scale);
            }
        });
        binding.bannerViewPager.setPageTransformer(transformer);

        setupIndicators(apiBanners.size());
        setupAutoScroll(apiBanners.size());
    }
    private void setupFooterBannerFromApi(HomeResponse.FooterBanners apiBanners) {
        footerAdapter = new FooterAdapter(apiBanners);
        footerAdapter.setOnFooterClickListener((banner) -> {
            Toast.makeText(getContext(),
                    "Banner: " + banner.getModule_service() + " clicked",
                    Toast.LENGTH_SHORT).show();
        });

        binding.footerBannerViewPager.setAdapter(footerAdapter);
        binding.footerBannerViewPager.setClipToPadding(false);
        binding.footerBannerViewPager.setClipChildren(false);
        binding.footerBannerViewPager.setOffscreenPageLimit(3);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int cardWidth = (int) (screenWidth * 0.95f);
        int padding = (screenWidth - cardWidth) / 2;

        binding.footerBannerViewPager.setPadding(padding, 0, padding, 0);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(6 ));
        transformer.addTransformer((page, position) -> {
            float absPosition = Math.abs(position);
            if (absPosition >= 1) {
                page.setScaleY(0.82f);
            } else {
                float scale = 0.85f + (1 - absPosition) * 0.05f;
                page.setScaleY(scale);
            }
        });
        binding.footerBannerViewPager.setPageTransformer(transformer);
    }

    private void updateCategoriesFromApi(ArrayList<HomeResponse.Module> apiModules) {
        if (horizontalAdapter != null) {
            horizontalAdapter.updateModules(apiModules);
        }
    }

    private void showLoading(boolean show) {
        if (binding != null && binding.progressBar != null) {
            binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void setupToolbar() {
        binding.collapsingToolbar.setTitleEnabled(false);
    }

    private void setupCollapsingBehavior() {
        binding.appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int scrollRange = appBarLayout.getTotalScrollRange();

            if (scrollRange == 0) return;

            float collapsePercentage = Math.abs(verticalOffset) / (float) scrollRange;
            float contentAlpha = 1 - collapsePercentage;

            binding.greetingText.setAlpha(contentAlpha);
            binding.taglineText.setAlpha(contentAlpha);
            binding.bannerFrameLayout.setAlpha(contentAlpha);

            if (curvedDrawable != null) {
                float adjustedCurve = collapsePercentage * MAX_CURVE_AMOUNT;
                curvedDrawable.setCurveAmount(adjustedCurve);
            }
        });
    }

    private void collapseAppBar() {
        binding.appBarLayout.setExpanded(false, true);
    }

    private void onCategorySelected(HomeResponse.Module module) {
        collapseAppBar();

        Intent intent = new Intent(getContext(), ShopsActivity.class);
        intent.putExtra("CATEGORY_NAME", module.getName());
        intent.putExtra("MODULE_ID", module.getId());
        startActivity(intent);
    }

    private void setupIndicators(int bannerCount) {
        if (bannerCount == 0) {
            return;
        }

        binding.indicatorContainer.removeAllViews();

        indicators = new ImageView[bannerCount];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                dpToPx(8), dpToPx(8)
        );
        layoutParams.setMargins(dpToPx(4), 0, dpToPx(4), 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getContext());
            indicators[i].setLayoutParams(layoutParams);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    requireContext(), R.drawable.indicator_inactive
            ));
            binding.indicatorContainer.addView(indicators[i]);
        }

        if (indicators.length > 0) {
            indicators[0].setImageDrawable(ContextCompat.getDrawable(
                    requireContext(), R.drawable.indicator_active
            ));
        }

        binding.bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateIndicators(position);
            }
        });
    }

    private void updateIndicators(int position) {
        if (indicators == null || indicators.length == 0) {
            return;
        }

        for (int i = 0; i < indicators.length; i++) {
            if (i == position) {
                indicators[i].setImageDrawable(ContextCompat.getDrawable(
                        requireContext(), R.drawable.indicator_active
                ));
            } else {
                indicators[i].setImageDrawable(ContextCompat.getDrawable(
                        requireContext(), R.drawable.indicator_inactive
                ));
            }
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void setupAutoScroll(int bannerCount) {
        bannerHandler = new Handler(Looper.getMainLooper());
        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                if (binding != null && bannerCount > 0) {
                    int currentItem = binding.bannerViewPager.getCurrentItem();
                    int nextItem = (currentItem + 1) % bannerCount;
                    binding.bannerViewPager.setCurrentItem(nextItem, true);
                    bannerHandler.postDelayed(this, AUTO_SCROLL_DELAY);
                }
            }
        };

        bannerHandler.postDelayed(bannerRunnable, AUTO_SCROLL_DELAY);
    }

    private void setupFeaturedCategoriesRecyclerView() {
        horizontalAdapter = new CategoryHorizontalAdapter(new ArrayList<>());
        horizontalAdapter.setOnCategoryClickListener((module, position) -> {
            onCategorySelected(module);
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                getContext(), GRID_SPAN_COUNT
        );

        binding.featuredRecyclerView.setLayoutManager(gridLayoutManager);
        binding.featuredRecyclerView.setAdapter(horizontalAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (bannerHandler != null && bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bannerHandler != null && bannerRunnable != null) {
            bannerHandler.postDelayed(bannerRunnable, AUTO_SCROLL_DELAY);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bannerHandler != null && bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }
        if (locationCallback != null && fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        binding = null;
        horizontalAdapter = null;
        bannerAdapter = null;
        bannerHandler = null;
        bannerRunnable = null;
        curvedDrawable = null;
        indicators = null;
        fusedLocationClient = null;
        locationCallback = null;
        geocoder = null;
    }
}