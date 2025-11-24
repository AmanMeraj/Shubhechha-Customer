package com.subh.shubhechha.Adapters;

import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.subh.shubhechha.Model.ShopItemResponse;
import com.subh.shubhechha.databinding.ItemShopItemRowBinding;

import java.util.ArrayList;
import java.util.List;

public class ShopItemAdapter extends RecyclerView.Adapter<ShopItemAdapter.ShopItemViewHolder> {

    private List<ShopItemResponse.Datum> shopItems = new ArrayList<>();
    private OnItemClickListener listener;
    private OnQuantityChangeListener quantityListener;

    // Click callback
    public interface OnItemClickListener {
        void onItemClick(ShopItemResponse.Datum item, int position);
    }

    // Quantity change callback
    public interface OnQuantityChangeListener {
        void onQuantityChanged(ShopItemResponse.Datum item, int newQuantity, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.quantityListener = listener;
    }

    public void setShopItems(List<ShopItemResponse.Datum> newItems) {
        this.shopItems = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShopItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShopItemViewHolder(
                ItemShopItemRowBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ShopItemViewHolder holder, int position) {
        holder.bind(shopItems.get(position), position);
    }

    @Override
    public int getItemCount() {
        return shopItems.size();
    }

    public List<ShopItemResponse.Datum> getShopItems() {
        return new ArrayList<>(shopItems);
    }

    class ShopItemViewHolder extends RecyclerView.ViewHolder {

        ItemShopItemRowBinding binding;
        int currentQuantity = 0;

        public ShopItemViewHolder(@NonNull ItemShopItemRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ShopItemResponse.Datum item, int position) {

            // Reset quantity
            currentQuantity = 0;
            binding.quantityText.setText("0");
            updateQuantityVisibility();

            // Load image
            Glide.with(binding.getRoot().getContext())
                    .load(item.getImage_path())
                    .into(binding.productImage);

            // Name & description
            binding.productName.setText(item.getName());
            binding.productDescription.setText(item.getDescription());

            // Prices
            String offerPrice = item.getOffer_price();
            String originalPrice = item.getAmount();

            // Clear flags (important for RecyclerView)
            binding.originalPrice.setPaintFlags(
                    binding.originalPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)
            );
            binding.currentPrice.setPaintFlags(
                    binding.currentPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)
            );

            if (!TextUtils.isEmpty(offerPrice)
                    && !offerPrice.equals("0")
                    && !offerPrice.equals("0.00")
                    && !offerPrice.equals(originalPrice)) {

                // When offer exists
                binding.originalPrice.setVisibility(View.VISIBLE);
                binding.currentPrice.setVisibility(View.VISIBLE);

                binding.originalPrice.setText("₹" + originalPrice);
                binding.currentPrice.setText("₹" + offerPrice);

                binding.originalPrice.setPaintFlags(
                        binding.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                );

            } else {
                // No offer → show only original price
                binding.originalPrice.setVisibility(View.GONE);
                binding.currentPrice.setVisibility(View.VISIBLE);

                binding.currentPrice.setText("₹" + originalPrice);

                binding.currentPrice.setPaintFlags(
                        binding.currentPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)
                );
            }

            // Increment
            binding.incrementButton.setOnClickListener(v -> {
                currentQuantity++;
                binding.quantityText.setText(String.valueOf(currentQuantity));
                updateQuantityVisibility();

                if (quantityListener != null)
                    quantityListener.onQuantityChanged(item, currentQuantity, position);
            });

            // Decrement
            binding.decrementButton.setOnClickListener(v -> {
                if (currentQuantity > 0) {
                    currentQuantity--;
                    binding.quantityText.setText(String.valueOf(currentQuantity));
                    updateQuantityVisibility();

                    if (quantityListener != null)
                        quantityListener.onQuantityChanged(item, currentQuantity, position);
                }
            });

            // Item click
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(item, position);
            });
        }

        private void updateQuantityVisibility() {
            if (currentQuantity == 0) {
                binding.decrementButton.setVisibility(View.GONE);
                binding.quantityText.setVisibility(View.GONE);
                binding.incrementButton.setVisibility(View.VISIBLE);
            } else {
                binding.decrementButton.setVisibility(View.VISIBLE);
                binding.quantityText.setVisibility(View.VISIBLE);
                binding.incrementButton.setVisibility(View.VISIBLE);
            }
        }
    }
}
