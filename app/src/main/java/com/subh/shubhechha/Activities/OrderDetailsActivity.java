package com.subh.shubhechha.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.subh.shubhechha.Adapters.BillSummaryAdapter;
import com.subh.shubhechha.Adapters.OrderItemAdapter;
import com.subh.shubhechha.Model.BillSummary;
import com.subh.shubhechha.Model.OrderItem;
import com.subh.shubhechha.R;
import com.subh.shubhechha.databinding.ActivityOrderDetailsBinding;
import com.subh.shubhechha.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsActivity extends Utility {
    private ActivityOrderDetailsBinding binding;
    private OrderItemAdapter orderItemAdapter;
    private BillSummaryAdapter billSummaryAdapter;
    private List<OrderItem> orderItems;
    private List<BillSummary> billSummaries;

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

        initializeData();
        setupAdapters();

        // Post the height calculation to ensure views are laid out
        binding.lvOrderItems.post(() -> {
            setListViewHeights();
        });
    }

    private void initializeData() {
        // Initialize Order Items
        orderItems = new ArrayList<>();
        orderItems.add(new OrderItem(2, "Salmon Rice (Rgl)", 600));
        orderItems.add(new OrderItem(1, "Chicken Biryani", 250));
        orderItems.add(new OrderItem(3, "Veg Momos", 150));

        // Initialize Bill Summary
        billSummaries = new ArrayList<>();
        billSummaries.add(new BillSummary("Item total", 1020));
        billSummaries.add(new BillSummary("Delivery charge", 30));
        billSummaries.add(new BillSummary("Taxes and charges", 50));
        billSummaries.add(new BillSummary("Discount", -200));

        // Calculate and set Grand Total
        double grandTotal = 0;
        for (BillSummary summary : billSummaries) {
            grandTotal += summary.getAmount();
        }
        binding.tvGrandTotal.setText("₹ " + String.format("%.0f", grandTotal));
    }

    private void setupAdapters() {
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

    /**
     * Helper method to set ListView height dynamically based on its children
     * This is necessary when ListView is inside ScrollView
     */
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}