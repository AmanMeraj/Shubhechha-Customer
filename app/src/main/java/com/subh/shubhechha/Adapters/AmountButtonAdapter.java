package com.subh.shubhechha.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.subh.shubhechha.R;

import java.util.List;

public class AmountButtonAdapter extends RecyclerView.Adapter<AmountButtonAdapter.AmountViewHolder> {

    private List<Integer> amounts;
    private OnAmountClickListener onAmountClickListener;

    public interface OnAmountClickListener {
        void onAmountClick(int amount);
    }

    public AmountButtonAdapter(List<Integer> amounts, OnAmountClickListener listener) {
        this.amounts = amounts;
        this.onAmountClickListener = listener;
    }

    @NonNull
    @Override
    public AmountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_amount_button, parent, false);
        return new AmountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AmountViewHolder holder, int position) {
        int amount = amounts.get(position);
        holder.tvAmount.setText("+ " + amount);

        holder.tvAmount.setOnClickListener(v -> {
            if (onAmountClickListener != null) {
                onAmountClickListener.onAmountClick(amount);
            }
        });
    }

    @Override
    public int getItemCount() {
        return amounts.size();
    }

    public static class AmountViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount;

        public AmountViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}