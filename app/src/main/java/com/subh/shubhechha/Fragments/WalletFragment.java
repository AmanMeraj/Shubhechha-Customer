package com.subh.shubhechha.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.subh.shubhechha.Activities.LoginActivity;
import com.subh.shubhechha.Activities.RechargeBottomSheet;
import com.subh.shubhechha.Adapters.WalletAdapter;
import com.subh.shubhechha.CurvedBottomDrawable;
import com.subh.shubhechha.Model.WalletResponse;
import com.subh.shubhechha.R;
import com.subh.shubhechha.Repository.Repository;
import com.subh.shubhechha.ViewModel.ViewModel;
import com.subh.shubhechha.databinding.FragmentWalletBinding;
import com.subh.shubhechha.utils.TimeUtil;
import com.subh.shubhechha.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class WalletFragment extends Fragment {
    private static final String TAG = "WalletFragment";

    private FragmentWalletBinding binding;

    private WalletAdapter walletAdapter;
    private ArrayList<WalletResponse.TransactionItem> transactionList;
    private CurvedBottomDrawable curvedDrawable;
    private ViewModel viewModel;
    private Utility sharedPrefManager= new Utility();

    // Pagination variables
    private int currentPage = 1;
    private int lastPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWalletBinding.inflate(inflater, container, false);

        initializeComponents();
        setupCurvedBackground();
        initViews();
        setupClickListeners();
        setupPagination();
        loadWalletData();

        return binding.getRoot();
    }

    private void initializeComponents() {
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        transactionList = new ArrayList<>();
    }

    private void setupCurvedBackground() {
        int peachColor = ContextCompat.getColor(requireContext(), R.color.peach);
        curvedDrawable = new CurvedBottomDrawable(peachColor);
        binding.curvedBackground.setBackground(curvedDrawable);
    }

    private void initViews() {
        walletAdapter = new WalletAdapter(getContext(), transactionList);

        binding.rcTransactionHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rcTransactionHistory.setAdapter(walletAdapter);
        binding.rcTransactionHistory.setHasFixedSize(true);
    }

    private void setupClickListeners() {
        binding.tvAdd.setOnClickListener(v -> {
            RechargeBottomSheet bottomSheet = RechargeBottomSheet.newInstance();
            bottomSheet.show(getChildFragmentManager(), "RechargeBottomSheet");
        });
    }

    private void setupPagination() {
        binding.rcTransactionHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0) {
                            loadMoreTransactions();
                        }
                    }
                }
            }
        });
    }

    private void loadWalletData() {
        if (isLoading) return;

        String token = "Bearer " + sharedPrefManager.pref.getPrefString(requireActivity(),sharedPrefManager.pref.user_token);

        showLoading(true);
        isLoading = true;

        viewModel.getWallet(token).observe(getViewLifecycleOwner(), response -> {
            showLoading(false);
            isLoading = false;

            if (response != null && response.isSuccess() && response.data != null) {
                WalletResponse walletResponse = response.data;

                // Update wallet amount
                if (walletResponse.getData() != null) {
                    int walletAmount = walletResponse.getData().getWallet_amount();
                    binding.tvAmount.setText("₹ " + walletAmount);

                    // Handle transactions
                    WalletResponse.Transactions transactions = walletResponse.getData().getTransactions();
                    if (transactions != null) {
                        List<WalletResponse.TransactionItem> items = transactions.getData();

                        if (items != null && !items.isEmpty()) {
                            transactionList.clear();
                            transactionList.addAll(items);
                            walletAdapter.notifyDataSetChanged();

                            // Update pagination info
                            currentPage = transactions.getCurrent_page();
                            lastPage = transactions.getLast_page();
                            isLastPage = currentPage >= lastPage;

                            // Hide empty state
                            binding.rcTransactionHistory.setVisibility(View.VISIBLE);
                            if (binding.tvEmptyState != null) {
                                binding.tvEmptyState.setVisibility(View.GONE);
                            }
                        } else {
                            showEmptyState();
                        }
                    } else {
                        showEmptyState();
                    }
                }
            } else {
                handleError(response);
            }
        });
    }

    private void loadMoreTransactions() {
        if (isLoading || isLastPage) return;

        String token = "Bearer " + sharedPrefManager.pref.getPrefString(requireActivity(),sharedPrefManager.pref.user_token);

        isLoading = true;
        // Show loading indicator at bottom if you have one
        // binding.progressBarBottom.setVisibility(View.VISIBLE);

        viewModel.getWallet(token).observe(getViewLifecycleOwner(), response -> {
            isLoading = false;
            // binding.progressBarBottom.setVisibility(View.GONE);

            if (response != null && response.isSuccess() && response.data != null) {
                WalletResponse walletResponse = response.data;

                if (walletResponse.getData() != null) {
                    WalletResponse.Transactions transactions = walletResponse.getData().getTransactions();

                    if (transactions != null) {
                        List<WalletResponse.TransactionItem> items = transactions.getData();

                        if (items != null && !items.isEmpty()) {
                            int oldSize = transactionList.size();
                            transactionList.addAll(items);
                            walletAdapter.notifyItemRangeInserted(oldSize, items.size());

                            // Update pagination info
                            currentPage = transactions.getCurrent_page();
                            lastPage = transactions.getLast_page();
                            isLastPage = currentPage >= lastPage;
                        } else {
                            isLastPage = true;
                        }
                    }
                }
            } else {
                Toast.makeText(getContext(), "Failed to load more transactions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        if (binding.progressBar != null) {
            binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        binding.rcTransactionHistory.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState() {
        binding.rcTransactionHistory.setVisibility(View.GONE);
        if (binding.tvEmptyState != null) {
            binding.tvEmptyState.setVisibility(View.VISIBLE);
            binding.tvEmptyState.setText("No transactions yet");
        }
    }

    private void handleError(Repository.ApiResponse<?> response) {
        String errorMessage = "Failed to load wallet data";

        if (response != null && response.message != null) {
            errorMessage = response.message;

            if (response.code == Repository.ERROR_SESSION_EXPIRED) {
                // Handle session expired - navigate to login
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                // Navigate to login screen
                 getActivity().finish();
                 startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        }

        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Error: " + errorMessage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}