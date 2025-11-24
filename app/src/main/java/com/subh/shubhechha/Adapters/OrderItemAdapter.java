package com.subh.shubhechha.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.subh.shubhechha.Model.OrderItem;
import com.subh.shubhechha.databinding.ItemOrderRowBinding;

import java.util.List;

public class OrderItemAdapter extends BaseAdapter {
    private Context context;
    private List<OrderItem> orderItems;

    public OrderItemAdapter(Context context, List<OrderItem> orderItems) {
        this.context = context;
        this.orderItems = orderItems;
    }

    @Override
    public int getCount() {
        return orderItems.size();
    }

    @Override
    public Object getItem(int position) {
        return orderItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemOrderRowBinding binding;

        if (convertView == null) {
            binding = ItemOrderRowBinding.inflate(LayoutInflater.from(context), parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ItemOrderRowBinding) convertView.getTag();
        }

        OrderItem item = orderItems.get(position);

        binding.tvItemQuantity.setText(item.getQuantity() + " x");
        binding.tvItemName.setText(item.getItemName());
        binding.tvItemPrice.setText("₹ " + String.format("%.0f", item.getItemPrice()));

        return convertView;
    }
}