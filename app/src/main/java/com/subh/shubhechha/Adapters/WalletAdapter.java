package com.subh.shubhechha.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.subh.shubhechha.Model.WalletResponse;
import com.subh.shubhechha.R;
import com.subh.shubhechha.databinding.RowTransactionBinding;
import com.subh.shubhechha.utils.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.WalletViewHolder> {

    private Context context;
    private ArrayList<WalletResponse.TransactionItem> transactionList;
    TimeUtil time = new TimeUtil();

    public WalletAdapter(Context context, ArrayList<WalletResponse.TransactionItem> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
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
        WalletResponse.TransactionItem transaction = transactionList.get(position);

        // Set title based on transaction type
        String title = getTransactionTitle(transaction.getType(), transaction.getOrder_id());
        holder.binding.tvTitle.setText(title);

        // Set subtitle (notes)
        String notes = transaction.getNotes() != null ? transaction.getNotes() : "Transaction";
        holder.binding.tvSubtitle.setText(notes);

        // Format and set date
        String formattedDate = formatDate(transaction.getCreated_at());
        holder.binding.tvDate.setText(formattedDate);

        // Set icon (you can customize based on transaction type)
        holder.binding.iconImage.setImageResource(R.drawable.subh_logo2);

        // Determine if credit or debit based on type
        boolean isCredit = isTransactionCredit(transaction.getType());

        // Set amount with + or - prefix and color
        if (isCredit) {
            holder.binding.tvAmount.setText("+ ₹ " + transaction.getAmount());
            holder.binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else {
            holder.binding.tvAmount.setText("- ₹ " + transaction.getAmount());
            holder.binding.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.red));
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    /**
     * Determine if transaction is credit based on type
     */
    private boolean isTransactionCredit(String type) {
        if (type == null) return false;

        // Adjust these based on your actual transaction types
        return type.equalsIgnoreCase("credit") ||
                type.equalsIgnoreCase("refund") ||
                type.equalsIgnoreCase("recharge") ||
                type.equalsIgnoreCase("bonus") ||
                type.equalsIgnoreCase("cashback");
    }

    /**
     * Get appropriate title based on transaction type
     */
    private String getTransactionTitle(String type, Integer orderId) {
        if (type == null) return "Transaction";

        switch (type.toLowerCase()) {
            case "credit":
                return "Wallet Recharge";
            case "debit":
                return orderId != null ? "Order Payment #" + orderId : "Payment";
            case "refund":
                return "Refund Received";
            case "bonus":
                return "Bonus Credited";
            case "cashback":
                return "Cashback Received";
            default:
                return type.substring(0, 1).toUpperCase() + type.substring(1);
        }
    }

    /**
     * Format date from API format to display format
     */
    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "N/A";
        }

        try {
            // Input format from API (adjust if different)
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            // Output format for display
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());

            Date date = inputFormat.parse(dateString);
            return date != null ? outputFormat.format(date) : dateString;
        } catch (ParseException e) {
            e.printStackTrace();
            // Return original string if parsing fails
            return dateString;
        }
    }

    /**
     * Update the list with new data
     */
    public void updateData(ArrayList<WalletResponse.TransactionItem> newList) {
        this.transactionList = newList;
        notifyDataSetChanged();
    }

    public static class WalletViewHolder extends RecyclerView.ViewHolder {
        RowTransactionBinding binding;

        public WalletViewHolder(@NonNull RowTransactionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}