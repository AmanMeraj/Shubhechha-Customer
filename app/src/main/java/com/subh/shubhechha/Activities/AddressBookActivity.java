package com.subh.shubhechha.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.subh.shubhechha.Adapters.AddressAdapter;
import com.subh.shubhechha.Model.AddressModel;
import com.subh.shubhechha.Model.GetAddressResponse;
import com.subh.shubhechha.R;
import com.subh.shubhechha.ViewModel.ViewModel;
import com.subh.shubhechha.databinding.ActivityAddressBookBinding;
import com.subh.shubhechha.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class AddressBookActivity extends Utility {

    ActivityAddressBookBinding binding;
    AddressAdapter adapter;
    List<AddressModel> addressList = new ArrayList<>();
    ViewModel viewModel;
    SharedPreferences sharedPreferences;
    String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityAddressBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViewModel();
        initSharedPreferences();
        initViews();
        setupCollapsingToolbar();
        setupRecyclerView();
        loadAddresses();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
    }

    private void initSharedPreferences() {
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        authToken = "Bearer " + pref.getPrefString(this,pref.user_token);
    }

    private void initViews() {
        binding.tvNotificationExpanded.setText("Address Book");
        binding.tvToolbarTitle.setText("Address Book");

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        binding.btnSubmit.setOnClickListener(view -> {
            binding.btnSubmit.postDelayed(() -> {
                Intent intent = new Intent(AddressBookActivity.this, AddAddressActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }, 600);
        });
    }

    private void setupCollapsingToolbar() {
        binding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                float percentage = Math.abs(verticalOffset) / (float) scrollRange;

                if (percentage > 0.6f) {
                    float alpha = (percentage - 0.6f) * 2.5f;
                    binding.tvToolbarTitle.setAlpha(alpha);
                } else {
                    binding.tvToolbarTitle.setAlpha(0f);
                }

                binding.tvNotificationExpanded.setAlpha(1 - percentage);

                if (scrollRange + verticalOffset == 0) {
                    binding.tvToolbarTitle.setVisibility(View.VISIBLE);
                    isShow = true;
                } else if (isShow) {
                    binding.tvToolbarTitle.setVisibility(View.VISIBLE);
                    isShow = false;
                }
            }
        });
    }

    private void setupRecyclerView() {
        binding.rcAddressBook.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddressAdapter(addressList);
        binding.rcAddressBook.setAdapter(adapter);

        // Set delete click listener
        adapter.setOnDeleteClickListener(position -> {
            if (position >= 0 && position < addressList.size()) {
                AddressModel addressModel = addressList.get(position);
                showDeleteConfirmationDialog(position, addressModel.getId());
            }
        });
    }

    private void loadAddresses() {
        showLoading(true);

        viewModel.addresses(authToken).observe(this, response -> {
            showLoading(false);

            if (response != null && response.data != null) {
                if (response.data.getStatus() == 1) {
                    GetAddressResponse.Data data = response.data.getData();
                    if (data != null && data.getAddresses() != null && !data.getAddresses().isEmpty()) {
                        updateAddressList(data.getAddresses());
                    } else {
                        showEmptyState();
                    }
                } else {
                    String message = response.data.getMessage() != null ? response.data.getMessage() : "Failed to load addresses";
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error loading addresses", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAddressList(ArrayList<GetAddressResponse.Address> addresses) {
        addressList.clear();

        for (GetAddressResponse.Address addr : addresses) {
            // Build complete address string
            StringBuilder fullAddress = new StringBuilder();

            if (addr.getFlat_number() != null && !addr.getFlat_number().isEmpty()) {
                fullAddress.append(addr.getFlat_number()).append(", ");
            }
            if (addr.getFloor() != null && !addr.getFloor().isEmpty()) {
                fullAddress.append("Floor ").append(addr.getFloor()).append(", ");
            }
            if (addr.getBuilding() != null && !addr.getBuilding().isEmpty()) {
                fullAddress.append(addr.getBuilding()).append(", ");
            }
            if (addr.getAddress() != null && !addr.getAddress().isEmpty()) {
                fullAddress.append(addr.getAddress()).append(", ");
            }
            if (addr.getState() != null && !addr.getState().isEmpty()) {
                fullAddress.append(addr.getState()).append(", ");
            }
            if (addr.getCountry() != null && !addr.getCountry().isEmpty()) {
                fullAddress.append(addr.getCountry()).append(" - ");
            }
            if (addr.getPincode() != null && !addr.getPincode().isEmpty()) {
                fullAddress.append(addr.getPincode());
            }

            // Remove trailing comma and space if present
            String addressStr = fullAddress.toString().trim();
            if (addressStr.endsWith(",")) {
                addressStr = addressStr.substring(0, addressStr.length() - 1);
            }

            String tag = addr.getName() != null ? addr.getName() : "Address " + addr.getId();

            // Create AddressModel with id
            AddressModel model = new AddressModel(addressStr, tag);
            model.setId(addr.getId()); // Make sure AddressModel has setId() method
            addressList.add(model);
        }

        adapter.notifyDataSetChanged();

        if (addressList.isEmpty()) {
            showEmptyState();
        } else {
            hideEmptyState();
        }
    }

    private void showDeleteConfirmationDialog(int position, int addressId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Address")
                .setMessage("Are you sure you want to delete this address?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteAddress(position, addressId);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAddress(int position, int addressId) {
        showLoading(true);

        viewModel.deleteAddress(authToken, addressId).observe(this, response -> {
            showLoading(false);

            if (response != null && response.data != null) {
                if (response.data.getStatus() == 1) {
                    adapter.deleteItem(position);
                    if (addressList.isEmpty()) {
                        showEmptyState();
                    }
                } else {
                    String message = response.data.getMessage() != null ? response.data.getMessage() : "Failed to delete address";
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error deleting address", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showLoading(boolean show) {
        if (binding.progressBar != null) {
            binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void showEmptyState() {
        // Add empty state view if you have one in your layout
        if (binding.tvEmptyState != null) {
            binding.tvEmptyState.setVisibility(View.VISIBLE);
            binding.tvEmptyState.setText("No addresses found.\nAdd a new address to get started.");
        }
        binding.rcAddressBook.setVisibility(View.GONE);
    }

    private void hideEmptyState() {
        if (binding.tvEmptyState != null) {
            binding.tvEmptyState.setVisibility(View.GONE);
        }
        binding.rcAddressBook.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload addresses when returning to this activity
        loadAddresses();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}