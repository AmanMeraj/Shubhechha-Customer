package com.subh.shubhechha.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.subh.shubhechha.Model.AddressModel;
import com.subh.shubhechha.databinding.ItemAddressBinding;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private final List<AddressModel> addressList;
    private OnDeleteClickListener listener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public AddressAdapter(List<AddressModel> addressList) {
        this.addressList = addressList;
    }

    // Setter for delete listener
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAddressBinding binding = ItemAddressBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new AddressViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        AddressModel model = addressList.get(position);

        holder.binding.tvAddress.setText(model.getAddress());
        holder.binding.tvAddressTag.setText(model.getTag());

        holder.binding.dustbin.setOnClickListener(view -> {
            if (listener != null) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList != null ? addressList.size() : 0;
    }

    // Method to delete an item
    public void deleteItem(int position) {
        if (position >= 0 && position < addressList.size()) {
            addressList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, addressList.size());
        }
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        ItemAddressBinding binding;

        public AddressViewHolder(@NonNull ItemAddressBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}