package com.subh.shubhechha.Adapters;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.subh.shubhechha.Model.ShopResponse;
import com.subh.shubhechha.R;
import com.subh.shubhechha.databinding.ItemCategoryGridBinding;

import java.util.List;

public class ShopGridAdapter extends RecyclerView.Adapter<ShopGridAdapter.ViewHolder> {

    private final List<ShopResponse.Shop> shopList;
    private OnShopClickListener listener;

    // Constructor
    public ShopGridAdapter(List<ShopResponse.Shop> shopList) {
        this.shopList = shopList;
    }

    // Interface for click listener
    public interface OnShopClickListener {
        void onShopClick(ShopResponse.Shop shop, int position);
    }

    // Setter for listener
    public void setOnShopClickListener(OnShopClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShopGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryGridBinding binding = ItemCategoryGridBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopGridAdapter.ViewHolder holder, int position) {
        ShopResponse.Shop shop = shopList.get(position);
        holder.bind(shop, position);
    }

    @Override
    public int getItemCount() {
        return shopList != null ? shopList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryGridBinding binding;

        public ViewHolder(@NonNull ItemCategoryGridBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ShopResponse.Shop shop, int position) {
            // Set shop name
            binding.shopName.setText(shop.getName());

            // Load shop image using Glide
            String imageUrl = shop.getImage_path();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.subh_logo2)
                        .error(R.drawable.subh_logo2)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(binding.shopImage);
            } else {
                binding.shopImage.setImageResource(R.drawable.subh_logo2);
            }

            // Set click listener
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onShopClick(shop, position);
                }
            });
        }
    }
}