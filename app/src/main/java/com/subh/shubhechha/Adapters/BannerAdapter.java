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

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<HomeResponse.Banner> bannerItems;
    private OnBannerClickListener onBannerClickListener;

    public interface OnBannerClickListener {
        void onBannerClick(HomeResponse.Banner banner, int position);
    }

    public BannerAdapter(List<HomeResponse.Banner> bannerItems) {
        this.bannerItems = bannerItems;
    }

    public void setOnBannerClickListener(OnBannerClickListener listener) {
        this.onBannerClickListener = listener;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        HomeResponse.Banner banner = bannerItems.get(position);

        // Load remote image using Glide
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.banner1) // Placeholder while loading
                .error(R.drawable.banner1) // Error image if loading fails
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(holder.itemView.getContext())
                .load(banner.getImage_path())
                .apply(options)
                .into(holder.bannerImage);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (onBannerClickListener != null) {
                onBannerClickListener.onBannerClick(banner, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return bannerItems != null ? bannerItems.size() : 0;
    }

    public void updateBanners(List<HomeResponse.Banner> newBanners) {
        this.bannerItems = newBanners;
        notifyDataSetChanged();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImage;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.iv_banner);
        }
    }
}