package com.subh.shubhechha.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.subh.shubhechha.Model.ShopItemResponse;
import com.subh.shubhechha.R;
import com.subh.shubhechha.databinding.ItemCategoryHorizontalBinding;

import java.util.ArrayList;
import java.util.List;

public class ShopHorizontalCategoryAdapter extends RecyclerView.Adapter<ShopHorizontalCategoryAdapter.CategoryViewHolder> {

    private List<ShopItemResponse.Menu> menus = new ArrayList<>();
    private OnCategoryClickListener onCategoryClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    // Background color array for cycling through backgrounds
    private final int[] backgrounds = {
            R.drawable.bg_category,
            R.drawable.bg_category_green,
            R.drawable.bg_category_mint,
            R.drawable.bg_category_blue
    };

    public interface OnCategoryClickListener {
        void onCategoryClick(ShopItemResponse.Menu menu, int position);
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.onCategoryClickListener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryHorizontalBinding binding = ItemCategoryHorizontalBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        ShopItemResponse.Menu menu = menus.get(position);
        holder.bind(menu, position, position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return menus != null ? menus.size() : 0;
    }

    public void updateMenus(List<ShopItemResponse.Menu> newMenus) {
        this.menus = newMenus != null ? newMenus : new ArrayList<>();
        this.selectedPosition = RecyclerView.NO_POSITION;
        notifyDataSetChanged();
    }

    // Method to programmatically select a category if needed
    public void setSelectedPosition(int position) {
        int oldPosition = selectedPosition;
        selectedPosition = position;
        if (oldPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(oldPosition);
        }
        if (selectedPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(selectedPosition);
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryHorizontalBinding binding;

        public CategoryViewHolder(@NonNull ItemCategoryHorizontalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ShopItemResponse.Menu menu, int position, boolean isSelected) {
            // Set menu name
            binding.categoryName.setText(menu.getName());

            // Set background drawable on FrameLayout (cycle through backgrounds)
            int backgroundIndex = position % backgrounds.length;
            binding.imageContainer.setBackground(
                    ContextCompat.getDrawable(binding.getRoot().getContext(), backgrounds[backgroundIndex])
            );

            // Load remote image using Glide
            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(binding.getRoot().getContext())
                    .load(menu.getImage_path())
                    .apply(options)
                    .into(binding.categoryIcon);

            // Apply shadow/elevation and text color for selected item
            if (isSelected) {
                // Selected state - add elevation for shadow effect
                binding.getRoot().setElevation(12f); // CardView-like shadow
                binding.getRoot().setTranslationZ(4f); // Additional lift

                // Set text color to orange
                binding.categoryName.setTextColor(
                        ContextCompat.getColor(binding.getRoot().getContext(), R.color.orange)
                );
            } else {
                // Unselected state - no shadow
                binding.getRoot().setElevation(0f);
                binding.getRoot().setTranslationZ(0f);

                // Set text color to default (black or your default color)
                binding.categoryName.setTextColor(
                        ContextCompat.getColor(binding.getRoot().getContext(), R.color.black)
                );
            }

            // Set click listener
            binding.getRoot().setOnClickListener(v -> {
                int currentPosition = getAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) return;

                int oldPosition = selectedPosition;
                selectedPosition = currentPosition;

                // Notify only the changed items for efficiency
                if (oldPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(oldPosition);
                }
                notifyItemChanged(selectedPosition);

                if (onCategoryClickListener != null) {
                    onCategoryClickListener.onCategoryClick(menu, currentPosition);
                }
            });
        }
    }
}