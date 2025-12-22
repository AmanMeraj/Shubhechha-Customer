package com.subh.shubhechha.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.subh.shubhechha.Fragments.HomeFragment;
import com.subh.shubhechha.Fragments.ProfileFragment;
import com.subh.shubhechha.Fragments.WalletFragment;
import com.subh.shubhechha.R;
import com.subh.shubhechha.databinding.ActivityContainerBinding;
import com.subh.shubhechha.utils.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContainerActivity extends Utility {

    ActivityContainerBinding binding;
    private Fragment activeFragment = null;
    private BroadcastReceiver cartBadgeUpdateReceiver;
    int cartCount = 0;

    // Permission request launcher
    private ActivityResultLauncher<String[]> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityContainerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Handle edge insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize permission launcher
        initializePermissionLauncher();
        setupCartBadgeReceiver();

        // Request permissions
        requestAllPermissions();

        // Initialize address from SharedPreferences
        updateAddressUI();

        binding.notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.notification.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ContainerActivity.this, NotificationActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }, 300);
            }
        });

        binding.cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.cart.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ContainerActivity.this, CartActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }, 300);
            }
        });

        // Set default tab and fragment on first launch
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            if (binding.bottomNavigation != null) {
                binding.bottomNavigation.selectTab(0);
            }
        }

        // Handle tab selection events
        if (binding.bottomNavigation != null) {
            binding.bottomNavigation.setOnTabSelectedListener(position -> {
                switch (position) {
                    case 0:
                        loadFragment(new HomeFragment());
                        break;
                    case 1:
                        loadFragment(new WalletFragment());
                        break;
                    case 2:
                        loadFragment(new ProfileFragment());
                        break;
                }
            });
        }

        updateCartBadge(cartCount);
    }

    private void setupCartBadgeReceiver() {
        cartBadgeUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("UPDATE_CART_BADGE".equals(intent.getAction())) {
                    cartCount = intent.getIntExtra("cart_count", 0);
                    updateCartBadge(cartCount);
                }
            }
        };

        // Register the receiver
        IntentFilter filter = new IntentFilter("UPDATE_CART_BADGE");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(cartBadgeUpdateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(cartBadgeUpdateReceiver, filter);
        }
    }

    public void updateCartBadge(int count) {
        if (binding.cartBadge != null) {
            if (count > 0) {
                binding.cartBadge.setVisibility(View.VISIBLE);
                if (count > 99) {
                    binding.cartBadge.setText("99+");
                } else {
                    binding.cartBadge.setText(String.valueOf(count));
                }
            } else {
                binding.cartBadge.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Update address display in the UI
     * Call this method when address changes
     */
    public void updateAddress(String address) {
        // Save to preferences
        pref.setPrefString(this, pref.user_short_address, address);

        // Update UI
        updateAddressUI();

        // Optional: Send broadcast to notify other components
        Intent intent = new Intent("UPDATE_ADDRESS");
        intent.putExtra("address", address);
        sendBroadcast(intent);
    }

    /**
     * Update the address TextView in the UI
     */
    private void updateAddressUI() {
        if (binding.address != null) {
            String shortAddress = pref.getPrefString(this, pref.user_address);
            if (shortAddress.isEmpty()) {
                binding.address.setText("Getting location...");
            } else {
                binding.address.setText(shortAddress);
            }
        }
    }

    /**
     * Initialize the permission launcher
     */
    private void initializePermissionLauncher() {
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    List<String> deniedPermissions = new ArrayList<>();
                    List<String> permanentlyDeniedPermissions = new ArrayList<>();

                    for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                        if (!entry.getValue()) {
                            deniedPermissions.add(entry.getKey());

                            if (!shouldShowRequestPermissionRationale(entry.getKey())) {
                                permanentlyDeniedPermissions.add(entry.getKey());
                            }
                        }
                    }

                    if (deniedPermissions.isEmpty()) {
                        Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!permanentlyDeniedPermissions.isEmpty()) {
                            showPermissionSettingsDialog();
                        } else {
                            showPermissionRationaleDialog(deniedPermissions);
                        }
                    }
                }
        );
    }

    /**
     * Request all necessary permissions
     */
    private void requestAllPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();

        // Camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA);
        }

        // Location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        // Notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        // Storage permissions (varies by Android version)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_VIDEO);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toArray(new String[0]));
        }
    }

    /**
     * Show dialog explaining why permissions are needed
     */
    private void showPermissionRationaleDialog(List<String> deniedPermissions) {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage("This app needs the following permissions to function properly:\n\n" +
                        "• Camera: To capture photos\n" +
                        "• Location: To show nearby stores\n" +
                        "• Storage: To save and access images\n" +
                        "• Notifications: To keep you updated\n\n" +
                        "Please grant these permissions to continue.")
                .setPositiveButton("Grant Permissions", (dialog, which) -> {
                    permissionLauncher.launch(deniedPermissions.toArray(new String[0]));
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Toast.makeText(this, "Some features may not work without permissions",
                            Toast.LENGTH_LONG).show();
                })
                .setCancelable(false)
                .show();
    }

    /**
     * Show dialog to navigate to app settings
     */
    private void showPermissionSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage("Some permissions were permanently denied. Please enable them in app settings to use all features.\n\n" +
                        "Go to: Settings > Apps > " + getString(R.string.app_name) + " > Permissions")
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Toast.makeText(this, "Some features may not work without permissions",
                            Toast.LENGTH_LONG).show();
                })
                .setCancelable(false)
                .show();
    }

    private void loadFragment(Fragment fragment) {
        updateCartBadge(cartCount);
        if (activeFragment != null && activeFragment.getClass() == fragment.getClass()) return;

        activeFragment = fragment;
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                )
                .replace(R.id.contentContainer, fragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh cart badge from SharedPreferences
        cartCount = pref.getPrefInteger(this, pref.cart_count);
        updateCartBadge(cartCount);

        // Refresh address display
        updateAddressUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the broadcast receiver
        if (cartBadgeUpdateReceiver != null) {
            try {
                unregisterReceiver(cartBadgeUpdateReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
}