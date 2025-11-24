package com.subh.shubhechha.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.subh.shubhechha.Model.Wallet;
import com.subh.shubhechha.R;
import com.subh.shubhechha.databinding.RowTransactionBinding;

import java.util.ArrayList;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.WalletViewHolder> {

    private Context context;
    private ArrayList<Wallet> walletList;

    public WalletAdapter(Context context, ArrayList<Wallet> walletList) {
        this.context = context;
        this.walletList = walletList;
    }

    @NonNull
    @Override
    public WalletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowTransactionBinding binding = RowTransactionBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new WalletViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletViewHolder holder, int position) {
        Wallet wallet = walletList.get(position);

        holder.binding.tvTitle.setText(wallet.getTitle());
        holder.binding.tvSubtitle.setText(wallet.getSubtitle());
        holder.binding.tvDate.setText(wallet.getDate());
        holder.binding.iconImage.setImageResource(wallet.getIconResId());

        // Set amount with + or - prefix and color
        if (wallet.isCredit()) {
            holder.binding.tvAmount.setText("+ ₹ " + wallet.getAmount());
            holder.binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else {
            holder.binding.tvAmount.setText("- ₹ " + wallet.getAmount());
            holder.binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.red));
        }
    }

    @Override
    public int getItemCount() {
        return walletList.size();
    }

    public static class WalletViewHolder extends RecyclerView.ViewHolder {
        RowTransactionBinding binding;

        public WalletViewHolder(@NonNull RowTransactionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}