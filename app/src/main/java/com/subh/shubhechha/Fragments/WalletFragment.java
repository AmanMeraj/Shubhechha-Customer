package com.subh.shubhechha.Fragments;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.subh.shubhechha.Adapters.WalletAdapter;
import com.subh.shubhechha.CurvedBottomDrawable;
import com.subh.shubhechha.Model.Wallet;
import com.subh.shubhechha.R;
import com.subh.shubhechha.databinding.FragmentWalletBinding;

import java.util.ArrayList;

public class WalletFragment extends Fragment {
    FragmentWalletBinding binding;
    private WalletAdapter walletAdapter;
    private ArrayList<Wallet> walletList;
    private CurvedBottomDrawable curvedDrawable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWalletBinding.inflate(inflater, container, false);
        setupCurvedBackground();
        initViews();
        loadTransactions();

        return binding.getRoot();
    }

    private void setupCurvedBackground() {
        // Create curved background drawable
        int peachColor = ContextCompat.getColor(requireContext(), R.color.peach);
        curvedDrawable = new CurvedBottomDrawable(peachColor);
        binding.curvedBackground.setBackground(curvedDrawable);
    }

    private void initViews() {
        walletList = new ArrayList<>();
        walletAdapter = new WalletAdapter(getContext(), walletList);

        binding.rcTransactionHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcTransactionHistory.setAdapter(walletAdapter);
        binding.rcTransactionHistory.setHasFixedSize(true);
    }

    private void loadTransactions() {
        // Add sample transactions - Replace with your actual data
        walletList.add(new Wallet(
                "Mustard Oil",
                "Contrary to popular belief.",
                "500",
                "20 Jul, 2025",
                R.drawable.subh_logo2,
                false // debit
        ));

        walletList.add(new Wallet(
                "Salary Credited",
                "Monthly salary deposit",
                "50000",
                "01 Jul, 2025",
                R.drawable.subh_logo2,
                true // credit
        ));

        walletList.add(new Wallet(
                "Grocery Shopping",
                "Supermarket purchase",
                "1250",
                "18 Jul, 2025",
                R.drawable.subh_logo2,
                false // debit
        ));

        walletList.add(new Wallet(
                "Refund Received",
                "Product return refund",
                "899",
                "15 Jul, 2025",
                R.drawable.subh_logo2,
                true // credit
        ));

        walletList.add(new Wallet(
                "Electric Bill",
                "Monthly electricity payment",
                "2300",
                "10 Jul, 2025",
                R.drawable.subh_logo2,
                false // debit
        ));

        walletList.add(new Wallet(
                "Bonus Credited",
                "Performance bonus",
                "15000",
                "05 Jul, 2025",
                R.drawable.subh_logo2,
                true // credit
        ));

        walletAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}