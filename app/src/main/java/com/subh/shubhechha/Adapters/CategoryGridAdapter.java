package com.subh.shubhechha.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.subh.shubhechha.Model.Category;
import com.subh.shubhechha.databinding.ItemCategoryGridBinding;

import java.util.List;

public class CategoryGridAdapter extends RecyclerView.Adapter<CategoryGridAdapter.ViewHolder> {

    private final List<Category> categoryList;
    private OnCategoryClickListener listener;

    // Constructor
    public CategoryGridAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    // Interface for click listener
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category, int position);
    }

    // Setter for listener
    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryGridBinding binding = ItemCategoryGridBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryGridAdapter.ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.bind(category, position);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryGridBinding binding;

        public ViewHolder(@NonNull ItemCategoryGridBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Category category, int position) {
            binding.shopName.setText(category.getName());
            binding.shopImage.setImageResource(category.getIconResId());
//            binding.imageContainer.setBackgroundResource(category.getbackgroundColor());

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category, position);
                }
            });
        }
    }
}