package com.subh.shubhechha.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.subh.shubhechha.Model.HomeResponse;
import com.subh.shubhechha.R;

import java.util.List;

public class CategoryHorizontalAdapter extends RecyclerView.Adapter<CategoryHorizontalAdapter.CategoryViewHolder> {

    private List<HomeResponse.Module> modules;
    private OnCategoryClickListener onCategoryClickListener;

    // Background color array for cycling through backgrounds
    private final int[] backgrounds = {
            R.drawable.bg_category,
            R.drawable.bg_category_green,
            R.drawable.bg_category_mint,
            R.drawable.bg_category_blue
    };

    public interface OnCategoryClickListener {
        void onCategoryClick(HomeResponse.Module module, int position);
    }

    public CategoryHorizontalAdapter(List<HomeResponse.Module> modules) {
        this.modules = modules;
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.onCategoryClickListener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_horizontal, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        HomeResponse.Module module = modules.get(position);

        // Set module name
        holder.categoryName.setText(module.getName());

        // Set background drawable on FrameLayout (cycle through backgrounds)
        int backgroundIndex = position % backgrounds.length;
        holder.imageContainer.setBackground(
                ContextCompat.getDrawable(holder.itemView.getContext(), backgrounds[backgroundIndex])
        );

        // Load remote image using Glide
        RequestOptions options = new RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.ic_launcher_foreground) // Placeholder while loading
                .error(R.drawable.ic_launcher_foreground) // Error image if loading fails
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(holder.itemView.getContext())
                .load(module.getImage_path())
                .apply(options)
                .into(holder.categoryIcon);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (onCategoryClickListener != null) {
                onCategoryClickListener.onCategoryClick(module, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return modules != null ? modules.size() : 0;
    }

    public void updateModules(List<HomeResponse.Module> newModules) {
        this.modules = newModules;
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        FrameLayout imageContainer;
        ImageView categoryIcon;
        TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageContainer = itemView.findViewById(R.id.imageContainer);
            categoryIcon = itemView.findViewById(R.id.categoryIcon);
            categoryName = itemView.findViewById(R.id.categoryName);
        }
    }
}