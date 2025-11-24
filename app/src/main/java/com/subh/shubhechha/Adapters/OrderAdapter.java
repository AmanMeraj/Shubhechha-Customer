package com.subh.shubhechha.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.subh.shubhechha.Model.OrderModel;
import com.subh.shubhechha.R;
import com.subh.shubhechha.databinding.ItemMyOrdersBinding;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private ArrayList<OrderModel> orderList;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(OrderModel order, int position);
    }

    public OrderAdapter(Context context, ArrayList<OrderModel> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMyOrdersBinding binding = ItemMyOrdersBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orderList.get(position);
        holder.bind(order, position);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        ItemMyOrdersBinding binding;

        public OrderViewHolder(@NonNull ItemMyOrdersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OrderModel order, int position) {
            binding.storeName.setText(order.getStoreName());
            binding.orderId.setText("Order Id : " + order.getOrderId());
            binding.orderAmount.setText("Order Amount : ₹ " + order.getOrderAmount());
            binding.orderDate.setText(order.getOrderDate());
            binding.statusLabel.setText(order.getStatus());
            binding.storeImage.setImageResource(order.getStoreImage());

            // Set status color based on order status
            switch (order.getStatus().toLowerCase()) {
                case "delivered":
                    binding.statusLabel.setTextColor(context.getResources().getColor(R.color.green));
                    break;
                case "pending":
                    binding.statusLabel.setTextColor(Color.parseColor("#FFA500")); // Orange
                    break;
                case "cancelled":
                    binding.statusLabel.setTextColor(Color.parseColor("#FF0000")); // Red
                    break;
                case "processing":
                    binding.statusLabel.setTextColor(Color.parseColor("#2196F3")); // Blue
                    break;
                default:
                    binding.statusLabel.setTextColor(context.getResources().getColor(R.color.green));
                    break;
            }

            // Click listener
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderClick(order, position);
                }
            });
        }
    }

    public void updateList(ArrayList<OrderModel> newList) {
        this.orderList = newList;
        notifyDataSetChanged();
    }

    public void addOrder(OrderModel order) {
        orderList.add(order);
        notifyItemInserted(orderList.size() - 1);
    }

    public void removeOrder(int position) {
        orderList.remove(position);
        notifyItemRemoved(position);
    }
}