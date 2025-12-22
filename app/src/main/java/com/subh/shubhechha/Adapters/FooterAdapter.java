package com.subh.shubhechha.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.subh.shubhechha.Model.HomeResponse;
import com.subh.shubhechha.R;

public class FooterAdapter extends RecyclerView.Adapter<FooterAdapter.FooterViewHolder> {

    private HomeResponse.FooterBanners footerBanner;
    private OnFooterClickListener onFooterClickListener;

    public interface OnFooterClickListener {
        void onFooterClick(HomeResponse.FooterBanners footer);
    }

    public FooterAdapter(HomeResponse.FooterBanners footerBanner) {
        this.footerBanner = footerBanner;
    }

    public void setOnFooterClickListener(OnFooterClickListener listener) {
        this.onFooterClickListener = listener;
    }

    @NonNull
    @Override
    public FooterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FooterViewHolder holder, int position) {
        if (footerBanner == null) {
            return;
        }

        // Load remote image using Glide
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.banner1) // Placeholder while loading
                .error(R.drawable.banner1) // Error image if loading fails
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(holder.itemView.getContext())
                .load(footerBanner.getImage_path())
                .apply(options)
                .into(holder.footerImage);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (onFooterClickListener != null) {
                onFooterClickListener.onFooterClick(footerBanner);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Since footer is a single object, return 1 if it exists, 0 otherwise
        return footerBanner != null ? 1 : 0;
    }

    public void updateFooter(HomeResponse.FooterBanners newFooter) {
        this.footerBanner = newFooter;
        notifyDataSetChanged();
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {
        ImageView footerImage;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            footerImage = itemView.findViewById(R.id.iv_banner);
        }
    }
}