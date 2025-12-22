package com.subh.shubhechha.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.subh.shubhechha.Model.AddressModel;
import com.subh.shubhechha.databinding.ItemAddressSelectableBinding;

import java.util.List;

public class AddressSelectionAdapter extends RecyclerView.Adapter<AddressSelectionAdapter.AddressViewHolder> {

    private final List<AddressModel> addressList;
    private OnAddressSelectListener listener;

    public interface OnAddressSelectListener {
        void onAddressSelected(AddressModel address, int position);
    }

    public AddressSelectionAdapter(List<AddressModel> addressList) {
        this.addressList = addressList;
    }

    public void setOnAddressSelectListener(OnAddressSelectListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAddressSelectableBinding binding = ItemAddressSelectableBinding.inflate(
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

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onAddressSelected(model, adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList != null ? addressList.size() : 0;
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        ItemAddressSelectableBinding binding;

        public AddressViewHolder(@NonNull ItemAddressSelectableBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}