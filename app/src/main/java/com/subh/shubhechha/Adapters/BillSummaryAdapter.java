package com.subh.shubhechha.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.subh.shubhechha.Model.BillSummary;
import com.subh.shubhechha.databinding.ItemBillSummaryBinding;

import java.util.List;

public class BillSummaryAdapter extends BaseAdapter {
    private Context context;
    private List<BillSummary> billSummaries;

    public BillSummaryAdapter(Context context, List<BillSummary> billSummaries) {
        this.context = context;
        this.billSummaries = billSummaries;
    }

    @Override
    public int getCount() {
        return billSummaries.size();
    }

    @Override
    public Object getItem(int position) {
        return billSummaries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemBillSummaryBinding binding;

        if (convertView == null) {
            binding = ItemBillSummaryBinding.inflate(LayoutInflater.from(context), parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ItemBillSummaryBinding) convertView.getTag();
        }

        BillSummary summary = billSummaries.get(position);

        binding.tvBillLabel.setText(summary.getLabel());
        binding.tvBillAmount.setText("₹ " + String.format("%.0f", summary.getAmount()));

        return convertView;
    }
}