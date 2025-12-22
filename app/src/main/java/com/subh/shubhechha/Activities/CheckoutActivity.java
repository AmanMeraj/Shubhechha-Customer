package com.subh.shubhechha.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.subh.shubhechha.Adapters.AddressSelectionAdapter;
import com.subh.shubhechha.Model.AddressModel;
import com.subh.shubhechha.Model.CheckoutModel;
import com.subh.shubhechha.Model.GetAddressResponse;
import com.subh.shubhechha.R;
import com.subh.shubhechha.ViewModel.ViewModel;
import com.subh.shubhechha.databinding.ActivityCheckoutBinding;
import com.subh.shubhechha.databinding.BottomSheetAddressXmlBinding;
import com.subh.shubhechha.databinding.ItemRadioButtonBinding;
import com.subh.shubhechha.utils.Utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CheckoutActivity extends Utility {
    ActivityCheckoutBinding binding;
    ViewModel viewModel;
    String authToken;

    private int cartItemCount;
    private int subTotal;
    private int deliveryCharge;
    private int discountAmount;
    private int packagingCharge;
    private int totalTax;
    private int total;
    private int gstOnItemTotal;
    private int gstOnPackagingCharge;
    private int gstOnDeliveryCharge;

    // Address data
    private List<AddressModel> addressList = new ArrayList<>();
    private AddressModel selectedAddress;

    // Payment data
    private List<GetAddressResponse.PaymentMethod> paymentMethods = new ArrayList<>();
    private Set<String> selectedPaymentSlugs = new HashSet<>();
    private Set<RadioButton> selectedRadioButtons = new HashSet<>();
    private int walletAmount = 0;

    // Constants for payment method validation
    private static final String WALLET = "wallet";
    private static final String COD = "cod";
    private static final String UPI = "upi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViewModel();
        initSharedPreferences();
        getCartDataFromIntent();
        setupCollapsingToolbar();
        setupClickListeners();
        loadCheckoutData();
        updateOrderSummaryUI();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
    }

    private void initSharedPreferences() {
        authToken = "Bearer " + pref.getPrefString(this, pref.user_token);
    }

    private void setupClickListeners() {
        binding.backBtn.setOnClickListener(v -> finish());
        binding.tvChangeAddress.setOnClickListener(v -> showAddressBottomSheet());
        binding.btnPlaceOrder.setOnClickListener(v -> handlePlaceOrder());
        binding.addAddressButton.setOnClickListener(v -> {
            startActivity(new Intent(this, AddAddressActivity.class));
        });
    }

    private void loadCheckoutData() {
        showLoading(true);

        viewModel.addresses(authToken).observe(this, response -> {
            showLoading(false);

            if (response != null && response.data != null) {
                if (response.data.getStatus() == 1) {
                    GetAddressResponse.Data data = response.data.getData();
                    if (data != null) {
                        // Load addresses
                        if (data.getAddresses() != null && !data.getAddresses().isEmpty()) {
                            updateAddressList(data.getAddresses());
                            if (!addressList.isEmpty()) {
                                selectedAddress = addressList.get(0);
                                updateAddressUI(selectedAddress);
                                showAddressCard(true);
                            } else {
                                showAddressCard(false);
                            }
                        } else {
                            showAddressCard(false);
                        }

                        // Load payment methods
                        if (data.getPayment_methods() != null && !data.getPayment_methods().isEmpty()) {
                            paymentMethods = data.getPayment_methods();
                            populatePaymentMethods(paymentMethods);
                        }

                        // Store wallet amount
                        walletAmount = data.getWallet_amount();
                    }
                } else {
                    String message = response.data.getMessage() != null ?
                            response.data.getMessage() : "Failed to load data";
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    showAddressCard(false);
                }
            } else {
                Toast.makeText(this, "Error loading checkout data", Toast.LENGTH_SHORT).show();
                showAddressCard(false);
            }
        });
    }

    private void showAddressCard(boolean hasAddress) {
        if (hasAddress) {
            binding.cardAddress.setVisibility(View.VISIBLE);
            binding.tvChangeAddress.setVisibility(View.VISIBLE);
            binding.addAddressButton.setVisibility(View.GONE);
        } else {
            binding.cardAddress.setVisibility(View.GONE);
            binding.tvChangeAddress.setVisibility(View.GONE);
            binding.addAddressButton.setVisibility(View.VISIBLE);
        }
    }

    private void updateAddressList(ArrayList<GetAddressResponse.Address> addresses) {
        addressList.clear();

        for (GetAddressResponse.Address addr : addresses) {
            StringBuilder fullAddress = new StringBuilder();

            if (addr.getFlat_number() != null && !addr.getFlat_number().isEmpty()) {
                fullAddress.append("Flat ").append(addr.getFlat_number()).append(", ");
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

            String addressStr = fullAddress.toString().trim();
            if (addressStr.endsWith(",")) {
                addressStr = addressStr.substring(0, addressStr.length() - 1);
            }

            String tag = addr.getName() != null ? addr.getName() : "Address " + addr.getId();

            AddressModel model = new AddressModel(addressStr, tag);
            model.setId(addr.getId());
            addressList.add(model);
        }
    }

    private void showAddressBottomSheet() {
        if (addressList.isEmpty()) {
            Toast.makeText(this, "No addresses available", Toast.LENGTH_SHORT).show();
            return;
        }

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        BottomSheetAddressXmlBinding sheetBinding = BottomSheetAddressXmlBinding.inflate(
                LayoutInflater.from(this)
        );
        bottomSheetDialog.setContentView(sheetBinding.getRoot());

        sheetBinding.rvAddresses.setLayoutManager(new LinearLayoutManager(this));
        AddressSelectionAdapter adapter = new AddressSelectionAdapter(addressList);
        sheetBinding.rvAddresses.setAdapter(adapter);

        adapter.setOnAddressSelectListener((address, position) -> {
            selectedAddress = address;
            updateAddressUI(address);
            bottomSheetDialog.dismiss();
            Toast.makeText(this, "Address selected", Toast.LENGTH_SHORT).show();
        });

        if (addressList.isEmpty()) {
            sheetBinding.rvAddresses.setVisibility(View.GONE);
            sheetBinding.tvNoAddress.setVisibility(View.VISIBLE);
        } else {
            sheetBinding.rvAddresses.setVisibility(View.VISIBLE);
            sheetBinding.tvNoAddress.setVisibility(View.GONE);
        }

        bottomSheetDialog.show();
    }

    private void updateAddressUI(AddressModel address) {
        if (address != null) {
            binding.tvAddress.setText(address.getAddress());
            binding.tvAddressTag.setText(address.getTag());
        }
    }



    private void proceedWithOrder() {
        // Determine which API to call based on payment method combination
        String paymentMethodString = determinePaymentMethodString();

        // Create checkout model
        CheckoutModel checkoutModel = new CheckoutModel();
        checkoutModel.setAddress_id(String.valueOf(selectedAddress.getId()));
        checkoutModel.setPayment_method(paymentMethodString);

        // Determine which API endpoint to use
        boolean hasWallet = selectedPaymentSlugs.contains(WALLET);
        boolean hasCod = selectedPaymentSlugs.contains(COD);
        boolean hasUpi = selectedPaymentSlugs.contains(UPI);

        if (hasWallet && !hasCod && !hasUpi) {
            // Only Wallet - Use checkout API
            placeOrderWithCheckoutAPI(checkoutModel);
        } else if (hasWallet && hasCod) {
            // Wallet + COD - Use checkout API
            placeOrderWithCheckoutAPI(checkoutModel);
        } else if (hasCod && !hasWallet && !hasUpi) {
            // Only COD - Use checkout API
            placeOrderWithCheckoutAPI(checkoutModel);
        } else if (hasUpi || (hasWallet && hasUpi)) {
            // UPI or Wallet + UPI - Use different API
            // TODO: Implement UPI payment gateway integration
            placeOrderWithUpiAPI(checkoutModel);
        } else {
            Toast.makeText(this, "Invalid payment method combination", Toast.LENGTH_SHORT).show();
        }
    }

    private String determinePaymentMethodString() {
        // Create comma-separated string of payment methods
        StringBuilder paymentString = new StringBuilder();
        List<String> methods = new ArrayList<>(selectedPaymentSlugs);

        for (int i = 0; i < methods.size(); i++) {
            paymentString.append(methods.get(i));
            if (i < methods.size() - 1) {
                paymentString.append(",");
            }
        }

        return paymentString.toString();
    }

    private void placeOrderWithCheckoutAPI(CheckoutModel checkoutModel) {
        showLoading(true);
        binding.btnPlaceOrder.setEnabled(false);

        viewModel.checkout(authToken, checkoutModel).observe(this, response -> {
            showLoading(false);
            binding.btnPlaceOrder.setEnabled(true);

            if (response != null && response.data != null) {
                if (response.data.getStatus() == 1) {
                    // ✅ Clear cart count as soon as order is placed successfully
                    clearCartCount();

                    // Show order success dialog
                    showOrderSuccessDialog(response.data.getMessage());
                } else {
                    // Order failed
                    String message = response.data.getMessage() != null ?
                            response.data.getMessage() : "Failed to place order";
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Error placing order. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void clearCartCount() {
        // Clear cart count in SharedPreferences
        pref.setPrefInteger(this, pref.cart_count, 0);

        // Send broadcast to update cart badge in ContainerActivity
        Intent broadcastIntent = new Intent("UPDATE_CART_BADGE");
        broadcastIntent.putExtra("cart_count", 0);
        sendBroadcast(broadcastIntent);
    }

    private void placeOrderWithUpiAPI(CheckoutModel checkoutModel) {
        // TODO: Implement UPI payment gateway integration

        showLoading(true);
        binding.btnPlaceOrder.setEnabled(false);

        // Placeholder for UPI integration
        Toast.makeText(this, "UPI Payment Gateway Integration Pending", Toast.LENGTH_LONG).show();

    /* Example structure with cart clearing:
    initiateUpiPayment(total, (paymentSuccess) -> {
        if (paymentSuccess) {
            viewModel.checkout(authToken, checkoutModel).observe(this, response -> {
                showLoading(false);
                binding.btnPlaceOrder.setEnabled(true);

                if (response != null && response.data != null && response.data.getStatus() == 1) {
                    // ✅ Clear cart count as soon as order is placed successfully
                    clearCartCount();

                    showOrderSuccessDialog(response.data.getMessage());
                } else {
                    Toast.makeText(this, "Order placement failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            showLoading(false);
            binding.btnPlaceOrder.setEnabled(true);
            Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show();
        }
    });
    */

        showLoading(false);
        binding.btnPlaceOrder.setEnabled(true);
    }
    private void showOrderSuccessDialog(String message) {
        String displayMessage = message != null ? message : "Your order has been placed successfully!";

        new MaterialAlertDialogBuilder(this)
                .setTitle("Order Placed Successfully")
                .setMessage(displayMessage)
                .setPositiveButton("View Orders", (dialog, which) -> {
                    dialog.dismiss();
                    navigateToMyOrders();
                })
                .setCancelable(false)
                .show();
    }

    private void navigateToMyOrders() {
        // Ensure cart count is cleared before navigation
        clearCartCount();

        Intent intent = new Intent(this, MyOrdersActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void setupCollapsingToolbar() {
        try {
            binding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isCollapsed = false;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    int scrollRange = appBarLayout.getTotalScrollRange();
                    if (scrollRange == 0) return;

                    float percentage = Math.abs(verticalOffset / (float) scrollRange);
                    if (Float.isNaN(percentage) || Float.isInfinite(percentage)) return;

                    binding.tvToolbarTitle.setAlpha(percentage);
                    binding.tvCartExpanded.setAlpha(1 - percentage);

                    float scale = 1 - (percentage * 0.2f);
                    scale = Math.max(0.8f, Math.min(1f, scale));
                    binding.peachCurveBg.setScaleY(scale);

                    if (Math.abs(verticalOffset) >= scrollRange) {
                        if (!isCollapsed) {
                            isCollapsed = true;
                            onToolbarCollapsed();
                        }
                    } else {
                        if (isCollapsed) {
                            isCollapsed = false;
                            onToolbarExpanded();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onToolbarCollapsed() {
        // Called when toolbar is fully collapsed
    }

    private void onToolbarExpanded() {
        // Called when toolbar is fully expanded
    }

    private void showLoading(boolean show) {
        if (binding.progressBar != null) {
            binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public Set<String> getSelectedPaymentSlugs() {
        return new HashSet<>(selectedPaymentSlugs);
    }

    public AddressModel getSelectedAddress() {
        return selectedAddress;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void getCartDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            cartItemCount = intent.getIntExtra("cart_item_count", 0);
            subTotal = intent.getIntExtra("sub_total", 0);
            deliveryCharge = intent.getIntExtra("delivery_charge", 0);
            discountAmount = intent.getIntExtra("discount_amount", 0);
            packagingCharge = intent.getIntExtra("packaging_charge", 0);
            totalTax = intent.getIntExtra("total_tax", 0);
            total = intent.getIntExtra("total", 0);
            gstOnItemTotal = intent.getIntExtra("gst_on_item_total", 0);
            gstOnPackagingCharge = intent.getIntExtra("gst_on_packaging_charge", 0);
            gstOnDeliveryCharge = intent.getIntExtra("gst_on_delivery_charge", 0);
        }
    }

    private void updateOrderSummaryUI() {
        try {
            binding.tvItemsLabel.setText(cartItemCount + " Items");
            binding.tvItemsValue.setText("₹" + subTotal);
            binding.tvDeliveryValue.setText("₹" + deliveryCharge);
            binding.tvDiscountValue.setText("₹" + discountAmount);
            binding.tvTotalValue.setText("₹" + total);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// Replace the payment method related methods in CheckoutActivity.java

    // Replace the payment method related methods in CheckoutActivity.java

    private void populatePaymentMethods(List<GetAddressResponse.PaymentMethod> methods) {
        selectedPaymentSlugs.clear();
        selectedRadioButtons.clear();

        // Update wallet balance text
        binding.tvWalletBalance.setText("You have ₹" + walletAmount + " wallet balance available");

        // Setup click listeners for each payment method
        for (GetAddressResponse.PaymentMethod method : methods) {
            String slug = method.getSlug();

            if (slug != null) {
                if (slug.equalsIgnoreCase(WALLET)) {
                    // Setup Wallet CheckBox
                    binding.checkboxWallet.setTag(slug);
                    binding.checkboxWallet.setOnClickListener(v -> {
                        handleWalletSelection();
                    });

                } else if (slug.equalsIgnoreCase(UPI)) {
                    // Setup UPI RadioButton
                    binding.radioUpi.setTag(slug);
                    binding.radioUpi.setOnClickListener(v -> {
                        handlePaymentMethodSelection(binding.radioUpi, UPI);
                    });

                } else if (slug.equalsIgnoreCase(COD)) {
                    // Setup COD RadioButton
                    binding.radioCod.setTag(slug);
                    binding.radioCod.setOnClickListener(v -> {
                        handlePaymentMethodSelection(binding.radioCod, COD);
                    });
                }
            }
        }
    }

    private void handleWalletSelection() {
        boolean wasSelected = selectedPaymentSlugs.contains(WALLET);

        if (wasSelected) {
            // Deselect wallet
            binding.checkboxWallet.setChecked(false);
            selectedPaymentSlugs.remove(WALLET);
        } else {
            // Validate if wallet can be selected
            if (isValidPaymentCombination(WALLET)) {
                binding.checkboxWallet.setChecked(true);
                selectedPaymentSlugs.add(WALLET);
            } else {
                binding.checkboxWallet.setChecked(false);
                showInvalidCombinationMessage(WALLET);
            }
        }
    }

    private void handlePaymentMethodSelection(RadioButton clickedButton, String clickedSlug) {
        boolean wasSelected = selectedPaymentSlugs.contains(clickedSlug);

        if (wasSelected) {
            // Deselect the clicked payment method
            clickedButton.setChecked(false);
            selectedPaymentSlugs.remove(clickedSlug);
            selectedRadioButtons.remove(clickedButton);
        } else {
            // Check if wallet is already selected with another payment method
            if (selectedPaymentSlugs.contains(WALLET) && selectedPaymentSlugs.size() == 2) {
                // Wallet + one other method is already selected
                // Uncheck the other radio button and select this one
                if (clickedSlug.equals(UPI) && selectedPaymentSlugs.contains(COD)) {
                    // User wants UPI, so uncheck COD
                    binding.radioCod.setChecked(false);
                    selectedPaymentSlugs.remove(COD);
                    selectedRadioButtons.remove(binding.radioCod);

                    // Now select UPI
                    binding.radioUpi.setChecked(true);
                    selectedPaymentSlugs.add(UPI);
                    selectedRadioButtons.add(binding.radioUpi);

                    Toast.makeText(this, "Switched from COD to UPI", Toast.LENGTH_SHORT).show();
                    return;

                } else if (clickedSlug.equals(COD) && selectedPaymentSlugs.contains(UPI)) {
                    // User wants COD, so uncheck UPI
                    binding.radioUpi.setChecked(false);
                    selectedPaymentSlugs.remove(UPI);
                    selectedRadioButtons.remove(binding.radioUpi);

                    // Now select COD
                    binding.radioCod.setChecked(true);
                    selectedPaymentSlugs.add(COD);
                    selectedRadioButtons.add(binding.radioCod);

                    Toast.makeText(this, "Switched from UPI to COD", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Validate if this selection is allowed
            if (isValidPaymentCombination(clickedSlug)) {
                clickedButton.setChecked(true);
                selectedPaymentSlugs.add(clickedSlug);
                selectedRadioButtons.add(clickedButton);

                // If selecting UPI or COD without wallet, uncheck the other one
                if (!selectedPaymentSlugs.contains(WALLET)) {
                    if (clickedSlug.equals(UPI) && binding.radioCod.isChecked()) {
                        binding.radioCod.setChecked(false);
                        selectedPaymentSlugs.remove(COD);
                        selectedRadioButtons.remove(binding.radioCod);
                    } else if (clickedSlug.equals(COD) && binding.radioUpi.isChecked()) {
                        binding.radioUpi.setChecked(false);
                        selectedPaymentSlugs.remove(UPI);
                        selectedRadioButtons.remove(binding.radioUpi);
                    }
                }
            } else {
                clickedButton.setChecked(false);
                showInvalidCombinationMessage(clickedSlug);
            }
        }
    }

    private boolean isValidPaymentCombination(String newSlug) {
        // If nothing is selected yet, allow any payment method
        if (selectedPaymentSlugs.isEmpty()) {
            return true;
        }

        // Check if wallet has sufficient balance
        boolean walletHasSufficientBalance = walletAmount >= total;

        // If wallet is being selected
        if (newSlug.equals(WALLET)) {
            // If wallet has sufficient balance
            if (walletHasSufficientBalance) {
                // Don't allow combining with other payment methods
                if (!selectedPaymentSlugs.isEmpty()) {
                    Toast.makeText(this, "Wallet has sufficient balance. No need to combine with other payment methods.", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            } else {
                // Wallet has insufficient balance - can be combined with COD or UPI (not both)
                // Check if already selected payment is valid (only one of COD or UPI)
                if (selectedPaymentSlugs.size() == 1) {
                    if (selectedPaymentSlugs.contains(COD) || selectedPaymentSlugs.contains(UPI)) {
                        return true;
                    }
                }
                // If both COD and UPI are selected (shouldn't happen but safety check)
                if (selectedPaymentSlugs.contains(COD) && selectedPaymentSlugs.contains(UPI)) {
                    Toast.makeText(this, "Cannot select Wallet when both COD and UPI are selected.", Toast.LENGTH_LONG).show();
                    return false;
                }
                // If nothing selected or valid single selection
                return selectedPaymentSlugs.isEmpty() || selectedPaymentSlugs.size() == 1;
            }
        }

        // If COD or UPI is being selected
        if (newSlug.equals(COD) || newSlug.equals(UPI)) {
            // Check if we already have 2 payment methods selected
            if (selectedPaymentSlugs.size() >= 2) {
                Toast.makeText(this, "You can only select maximum 2 payment methods.", Toast.LENGTH_LONG).show();
                return false;
            }

            // Check if wallet is already selected
            if (selectedPaymentSlugs.contains(WALLET)) {
                // Wallet is selected - check if it has sufficient balance
                if (walletHasSufficientBalance) {
                    // Wallet has sufficient balance - uncheck wallet first
                    Toast.makeText(this, "Wallet has sufficient balance. Please uncheck Wallet to select " +
                            (newSlug.equals(COD) ? "COD" : "UPI"), Toast.LENGTH_LONG).show();
                    return false;
                } else {
                    // Wallet has insufficient balance
                    // Check if we're trying to select the third option
                    if (selectedPaymentSlugs.size() == 2) {
                        // Already have Wallet + one other method, can't add more
                        Toast.makeText(this, "You can only combine Wallet with one payment method (COD or UPI).", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    // Allow selecting COD or UPI with Wallet
                    return true;
                }
            }

            // Check if the other radio button is already selected
            if (selectedPaymentSlugs.contains(COD) && newSlug.equals(UPI)) {
                Toast.makeText(this, "Cannot combine COD with UPI.", Toast.LENGTH_LONG).show();
                return false;
            }
            if (selectedPaymentSlugs.contains(UPI) && newSlug.equals(COD)) {
                Toast.makeText(this, "Cannot combine UPI with COD.", Toast.LENGTH_LONG).show();
                return false;
            }

            return true;
        }

        return true;
    }

    private void showInvalidCombinationMessage(String attemptedSlug) {
        String message;
        boolean walletHasSufficientBalance = walletAmount >= total;

        if (attemptedSlug.equals(WALLET) && !selectedPaymentSlugs.isEmpty() && walletHasSufficientBalance) {
            message = "Wallet has sufficient balance (₹" + walletAmount + ") to pay the total amount (₹" + total + "). " +
                    "No need to combine with other payment methods.";
        } else if (selectedPaymentSlugs.contains(WALLET) && walletHasSufficientBalance &&
                (attemptedSlug.equals(COD) || attemptedSlug.equals(UPI))) {
            message = "Wallet has sufficient balance (₹" + walletAmount + ") to pay the total amount (₹" + total + "). " +
                    "Please uncheck Wallet to select other payment methods.";
        } else if (selectedPaymentSlugs.contains(COD) && attemptedSlug.equals(UPI)) {
            message = "Cannot combine COD with UPI.";
        } else if (selectedPaymentSlugs.contains(UPI) && attemptedSlug.equals(COD)) {
            message = "Cannot combine UPI with COD.";
        } else if (!walletHasSufficientBalance && attemptedSlug.equals(WALLET) && selectedPaymentSlugs.size() >= 2) {
            message = "Wallet balance is insufficient (₹" + walletAmount + "). You can only combine Wallet with one other payment method (COD or UPI).";
        } else {
            message = "Invalid payment method combination.";
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void handlePlaceOrder() {
        // Validate address
        if (selectedAddress == null) {
            Toast.makeText(this, "Please select a delivery address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate payment method
        if (selectedPaymentSlugs.isEmpty()) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check wallet scenarios
        if (selectedPaymentSlugs.contains(WALLET)) {
            boolean walletHasSufficientBalance = walletAmount >= total;
            boolean hasOtherPaymentMethod = selectedPaymentSlugs.contains(COD) || selectedPaymentSlugs.contains(UPI);

            if (walletHasSufficientBalance) {
                // Wallet has sufficient balance
                if (hasOtherPaymentMethod) {
                    // User tried to combine wallet with other method when wallet has sufficient balance
                    Toast.makeText(this, "Wallet has sufficient balance. Remove other payment methods.", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    // Only wallet selected and has sufficient balance - proceed
                    showWalletFullPaymentConfirmation();
                }
            } else {
                // Wallet has insufficient balance
                if (hasOtherPaymentMethod) {
                    // Wallet + COD or Wallet + UPI - show partial payment dialog
                    checkWalletBalanceAndProceed();
                } else {
                    // Only wallet selected but insufficient balance
                    int remainingAmount = total - walletAmount;
                    showInsufficientWalletDialog(remainingAmount);
                }
            }
        } else {
            // No wallet involved, proceed directly
            proceedWithOrder();
        }
    }

    private void showWalletFullPaymentConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirm Payment")
                .setMessage("Order Amount: ₹" + total +
                        "\nWallet Balance: ₹" + walletAmount +
                        "\n\nFull amount will be deducted from your wallet." +
                        "\n\nDo you want to proceed?")
                .setPositiveButton("Proceed", (dialog, which) -> {
                    proceedWithOrder();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(true)
                .show();
    }

    private void checkWalletBalanceAndProceed() {
        if (walletAmount < total) {
            // Wallet balance is insufficient
            int remainingAmount = total - walletAmount;

            // Check if COD or UPI is also selected
            boolean hasCodOrUpi = selectedPaymentSlugs.contains(COD) || selectedPaymentSlugs.contains(UPI);

            if (hasCodOrUpi) {
                // Show confirmation dialog for partial wallet payment
                showPartialWalletPaymentDialog(remainingAmount);
            } else {
                // Only wallet selected but insufficient balance
                showInsufficientWalletDialog(remainingAmount);
            }
        } else {
            // This case should not occur based on the new logic, but keeping as safeguard
            proceedWithOrder();
        }
    }

    private void showPartialWalletPaymentDialog(int remainingAmount) {
        String paymentMethodName = selectedPaymentSlugs.contains(COD) ? "COD" : "UPI";

        new MaterialAlertDialogBuilder(this)
                .setTitle("Partial Payment Confirmation")
                .setMessage("Order Amount: ₹" + total +
                        "\nWallet Balance: ₹" + walletAmount +
                        "\n\n• ₹" + walletAmount + " will be deducted from wallet" +
                        "\n• ₹" + remainingAmount + " will be paid via " + paymentMethodName +
                        "\n\nDo you want to proceed?")
                .setPositiveButton("Proceed", (dialog, which) -> {
                    proceedWithOrder();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(true)
                .show();
    }

    private void showInsufficientWalletDialog(int remainingAmount) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Insufficient Wallet Balance")
                .setMessage("Order Amount: ₹" + total +
                        "\nWallet Balance: ₹" + walletAmount +
                        "\nShortfall: ₹" + remainingAmount +
                        "\n\nPlease select COD or UPI along with Wallet to complete this order.")
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(true)
                .show();
    }
}