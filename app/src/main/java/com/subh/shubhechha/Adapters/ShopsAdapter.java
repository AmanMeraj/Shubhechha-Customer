package com.subh.shubhechha.Adapters;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.subh.shubhechha.Model.Shop;
import com.subh.shubhechha.databinding.ItemCategoryGridBinding;
import com.subh.shubhechha.utils.TimeUtil;

import java.util.List;

public class ShopsAdapter extends RecyclerView.Adapter<ShopsAdapter.ViewHolder> {

    private final List<Shop> shopsList;
    private OnShopClickListener listener;
    TimeUtil timeUtil = new TimeUtil();


    // Constructor
    public ShopsAdapter(List<Shop> shopsList) {
        this.shopsList = shopsList;
    }

    // Interface for click listener
    public interface OnShopClickListener {
        void onShopClick(Shop shop, int position);
    }

    // Setter for listener
    public void setOnShopClickListener(OnShopClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryGridBinding binding = ItemCategoryGridBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Shop shop = shopsList.get(position);
        holder.bind(shop, position);
    }

    @Override
    public int getItemCount() {
        return shopsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryGridBinding binding;

        public ViewHolder(@NonNull ItemCategoryGridBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Shop shop, int position) {
            binding.shopName.setText(shop.getName());
            binding.orderTime.setText(shop.getTime());
            binding.shopOpenDuration.setText(shop.getDistance());
            binding.shopImage.setImageResource(shop.getImageResId());

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onShopClick(shop, position);
                }
            });
        }
    }
}
