package com.subh.shubhechha.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.subh.shubhechha.Model.OrderModel;
import com.subh.shubhechha.R;
import com.subh.shubhechha.databinding.ItemMyOrdersBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private ArrayList<OrderModel.Order> orderList;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(OrderModel.Order order, int position);
    }

    public OrderAdapter(Context context, ArrayList<OrderModel.Order> orderList) {
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
        OrderModel.Order order = orderList.get(position);
        holder.bind(order, position);
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        ItemMyOrdersBinding binding;

        public OrderViewHolder(@NonNull ItemMyOrdersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(OrderModel.Order order, int position) {
            try {
                // Set store name
                if (order.getShop_name() != null && !order.getShop_name().isEmpty()) {
                    binding.storeName.setText(order.getShop_name());
                } else {
                    binding.storeName.setText("N/A");
                }

                // Set order ID
                if (order.getOrderno() != null && !order.getOrderno().isEmpty()) {
                    binding.orderId.setText("Order Id : #" + order.getOrderno());
                } else {
                    binding.orderId.setText("Order Id : N/A");
                }

                // Set order amount
                if (order.getTotal() != null && !order.getTotal().isEmpty()) {
                    binding.orderAmount.setText("Order Amount : ₹ " + order.getTotal());
                } else {
                    binding.orderAmount.setText("Order Amount : ₹ 0");
                }

                // Set order date (format the date)
                if (order.getCreated_at() != null && !order.getCreated_at().isEmpty()) {
                    binding.orderDate.setText(formatDate(order.getCreated_at()));
                } else {
                    binding.orderDate.setText("N/A");
                }

                // Set status
                if (order.getStatus() != null && !order.getStatus().isEmpty()) {
                    binding.statusLabel.setText(capitalizeFirst(order.getStatus()));
                    setStatusColor(order.getStatus());
                } else {
                    binding.statusLabel.setText("Unknown");
                    binding.statusLabel.setTextColor(context.getResources().getColor(R.color.black));
                }

                // Load store image using Glide
                if (order.getShop_image() != null && !order.getShop_image().isEmpty()) {
                    Glide.with(context)
                            .load(order.getShop_image())
                            .placeholder(R.drawable.subh_img1)
                            .error(R.drawable.subh_img1)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.storeImage);
                } else {
                    binding.storeImage.setImageResource(R.drawable.subh_img1);
                }

                // Click listener
                binding.getRoot().setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onOrderClick(order, position);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void setStatusColor(String status) {
            try {
                String statusLower = status.toLowerCase();

                switch (statusLower) {
                    case "delivered":
                    case "completed":
                        binding.statusLabel.setTextColor(context.getResources().getColor(R.color.green));
                        break;
                    case "pending":
                    case "placed":
                        binding.statusLabel.setTextColor(Color.parseColor("#FFA500")); // Orange
                        break;
                    case "cancelled":
                    case "rejected":
                        binding.statusLabel.setTextColor(Color.parseColor("#FF0000")); // Red
                        break;
                    case "processing":
                    case "confirmed":
                        binding.statusLabel.setTextColor(Color.parseColor("#2196F3")); // Blue
                        break;
                    case "shipped":
                    case "out for delivery":
                        binding.statusLabel.setTextColor(Color.parseColor("#9C27B0")); // Purple
                        break;
                    default:
                        binding.statusLabel.setTextColor(context.getResources().getColor(R.color.black));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String formatDate(String dateString) {
            try {
                // Parse the date from API format (e.g., "2024-12-09 14:30:00" or "2024-12-09T14:30:00Z")
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());

                // Try parsing with space separator
                Date date = inputFormat.parse(dateString);
                if (date != null) {
                    return outputFormat.format(date);
                }

            } catch (ParseException e) {
                try {
                    // Try parsing with T separator (ISO format)
                    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
                    Date date = isoFormat.parse(dateString);
                    if (date != null) {
                        return outputFormat.format(date);
                    }
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }

            // If parsing fails, return the original string
            return dateString;
        }

        private String capitalizeFirst(String text) {
            if (text == null || text.isEmpty()) {
                return text;
            }
            return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
        }
    }

    public void updateList(ArrayList<OrderModel.Order> newList) {
        if (newList != null) {
            this.orderList = newList;
            notifyDataSetChanged();
        }
    }

    public void addOrder(OrderModel.Order order) {
        if (order != null) {
            orderList.add(order);
            notifyItemInserted(orderList.size() - 1);
        }
    }

    public void addOrders(ArrayList<OrderModel.Order> orders) {
        if (orders != null && !orders.isEmpty()) {
            int startPosition = orderList.size();
            orderList.addAll(orders);
            notifyItemRangeInserted(startPosition, orders.size());
        }
    }

    public void removeOrder(int position) {
        if (position >= 0 && position < orderList.size()) {
            orderList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clearOrders() {
        orderList.clear();
        notifyDataSetChanged();
    }
}