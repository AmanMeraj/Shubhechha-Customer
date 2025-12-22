package com.subh.shubhechha.Activities;

import static android.app.ProgressDialog.show;

import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.subh.shubhechha.Adapters.BillSummaryAdapter;
import com.subh.shubhechha.Adapters.OrderItemAdapter;
import com.subh.shubhechha.Model.BillSummary;
import com.subh.shubhechha.Model.OrderDetails;
import com.subh.shubhechha.Model.OrderItem;
import com.subh.shubhechha.R;
import com.subh.shubhechha.ViewModel.ViewModel;
import com.subh.shubhechha.databinding.ActivityOrderDetailsBinding;
import com.subh.shubhechha.utils.Utility;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderDetailsActivity extends Utility {
    private ActivityOrderDetailsBinding binding;
    private OrderItemAdapter orderItemAdapter;
    private BillSummaryAdapter billSummaryAdapter;
    private List<OrderItem> orderItems;
    private List<BillSummary> billSummaries;
    private ViewModel orderViewModel;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityOrderDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get order ID from intent
        orderId = getIntent().getIntExtra("order_id", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Invalid order ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViewModel();
        setupAdapters();
        fetchOrderDetails();
    }

    private void initializeViewModel() {
        orderViewModel = new ViewModelProvider(this).get(ViewModel.class);
    }

    private void fetchOrderDetails() {
        String auth = "Bearer " + getAuthToken(); // Get your auth token

        orderViewModel.getOrderDetails(auth, orderId).observe(this, response -> {
            if (response != null) {
                if (response.isSuccess() && response.data.getData() != null) {
                    OrderDetails orderDetails = response.data;
                    populateOrderDetails(orderDetails);
                } else {
                    Toast.makeText(this, response.data.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void populateOrderDetails(OrderDetails orderDetails) {
        if (orderDetails.getData() == null || orderDetails.getData().getOrder() == null) {
            return;
        }

        OrderDetails.Order order = orderDetails.getData().getOrder();

        // Set order status banner
        updateStatusBanner(order.getStatus());

        // Set store information
        binding.tvStoreName.setText(order.getShop_name() != null ? order.getShop_name() : "N/A");
        binding.tvStoreAddress.setText(order.getAddress() != null ? order.getAddress() : "N/A");

        if (order.getShop_image() != null && !order.getShop_image().isEmpty()) {
            Glide.with(this)
                    .load(order.getShop_image())
                    .placeholder(R.drawable.subh_img2)
                    .into(binding.ivStoreImage);
        }

        // Set order ID
        binding.tvOrderId.setText("Order Id : " + (order.getOrderno() != null ? order.getOrderno() : "N/A"));

        // Populate order items
        populateOrderItems(order.getOrdritems());

        // Populate bill summary
        populateBillSummary(order);
        if(order.status.matches("completed")){
            binding.llSuccessBanner.setVisibility(View.VISIBLE);
        }else{
            binding.llSuccessBanner.setVisibility(View.GONE);
        }

        // Set user information
        binding.tvUserName.setText(order.getReceipent_name() != null ? order.getReceipent_name() : "N/A");
        binding.tvUserPhone.setText(order.getUser_mobile() != null ? order.getUser_mobile() : "N/A");

        if (order.getUser_image() != null && !order.getUser_image().isEmpty()) {
            Glide.with(this)
                    .load(order.getUser_image())
                    .placeholder(R.drawable.cart_profile)
                    .into(binding.ivUserAvatar);
        }

        // Set payment method
        String paymentMethod = "Cash";
        if (order.getPay_wallet() != null && !order.getPay_wallet().isEmpty()) {
            paymentMethod = order.getPay_wallet();
        }
        binding.tvPaymentMethod.setText("Paid via - " + paymentMethod);

        // Set payment date
        binding.tvPaymentDate.setText(formatDate(order.getCreated_at()));

        // Set delivery address
        String fullAddress = buildFullAddress(order);
        binding.tvDeliveryAddress.setText(fullAddress);

        // Post the height calculation
        binding.lvOrderItems.post(this::setListViewHeights);
    }

    private void updateStatusBanner(String status) {
        // You can customize this based on different status values
        if (status != null) {
            switch (status.toLowerCase()) {
                case "delivered":
                    binding.llSuccessBanner.setVisibility(View.VISIBLE);
                    // Update text and icon as needed
                    break;
                case "pending":
                case "processing":
                    binding.llSuccessBanner.setVisibility(View.VISIBLE);
                    // Update to show different status
                    break;
                default:
                    binding.llSuccessBanner.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void populateOrderItems(ArrayList<OrderDetails.Ordritem> ordritems) {
        orderItems = new ArrayList<>();

        if (ordritems != null && !ordritems.isEmpty()) {
            for (OrderDetails.Ordritem item : ordritems) {
                double amount = 0;
                try {
                    amount = Double.parseDouble(item.getAmount());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                OrderItem orderItem = new OrderItem(
                        item.getQuantity(),
                        item.getName() != null ? item.getName() : "Unknown Item",
                        amount
                );
                orderItems.add(orderItem);
            }
        }

        orderItemAdapter.updateData(orderItems);
    }

    private void populateBillSummary(OrderDetails.Order order) {
        billSummaries = new ArrayList<>();

        // Item total (subtotal)
        double subtotal = parseDouble(order.getSubtotal());
        billSummaries.add(new BillSummary("Item total", subtotal));

        // Delivery charge
        double deliveryCharge = parseDouble(order.getDelivery_charge());
        if (deliveryCharge > 0) {
            billSummaries.add(new BillSummary("Delivery charge", deliveryCharge));
        }

        // Packaging charge
        double packagingCharge = parseDouble(order.getPackaging_charge());
        if (packagingCharge > 0) {
            billSummaries.add(new BillSummary("Packaging charge", packagingCharge));
        }

        // Taxes and charges
        double gstItemTotal = parseDouble(order.getGst_on_item_total());
        double gstPackaging = parseDouble(order.getGst_on_packaging_charge());
        double gstDelivery = parseDouble(order.getGst_on_delivery_charge());
        double tax = parseDouble(order.getTax());

        double totalTax = gstItemTotal + gstPackaging + gstDelivery + tax;
        if (totalTax > 0) {
            billSummaries.add(new BillSummary("Taxes and charges", totalTax));
        }

        // Discount
        double discount = parseDouble(order.getDiscount_amount());
        if (discount > 0) {
            billSummaries.add(new BillSummary("Discount", -discount));
        }

        // Cashback
        double cashback = parseDouble(order.getCashback_amount());
        if (cashback > 0) {
            billSummaries.add(new BillSummary("Cashback", -cashback));
        }

        billSummaryAdapter.updateData(billSummaries);

        // Calculate and set grand total
        double grandTotal = parseDouble(order.getTotal());
        binding.tvGrandTotal.setText("₹ " + String.format("%.0f", grandTotal));
    }

    private double parseDouble(String value) {
        if (value == null || value.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return "N/A";
        }

        try {
            // Adjust the input format based on your API response format
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault());

            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr;
        }
    }

    private String buildFullAddress(OrderDetails.Order order) {
        StringBuilder address = new StringBuilder();

        if (order.getAddress() != null && !order.getAddress().isEmpty()) {
            address.append(order.getAddress());
        }

        if (order.getPincode() != null && !order.getPincode().isEmpty()) {
            if (address.length() > 0) {
                address.append(", ");
            }
            address.append(order.getPincode());
        }

        return address.length() > 0 ? address.toString() : "N/A";
    }

    private void setupAdapters() {
        // Initialize with empty lists
        orderItems = new ArrayList<>();
        billSummaries = new ArrayList<>();

        // Setup Order Items Adapter
        orderItemAdapter = new OrderItemAdapter(this, orderItems);
        binding.lvOrderItems.setAdapter(orderItemAdapter);

        // Setup Bill Summary Adapter
        billSummaryAdapter = new BillSummaryAdapter(this, billSummaries);
        binding.lvBillSummary.setAdapter(billSummaryAdapter);
    }

    private void setListViewHeights() {
        // Set dynamic height for Order Items ListView
        setListViewHeight(binding.lvOrderItems);

        // Set dynamic height for Bill Summary ListView
        setListViewHeight(binding.lvBillSummary);
    }

    private void setListViewHeight(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null) {
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.EXACTLY);

        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        android.view.ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private String getAuthToken() {
        return pref.getPrefString(this,pref.user_token);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}