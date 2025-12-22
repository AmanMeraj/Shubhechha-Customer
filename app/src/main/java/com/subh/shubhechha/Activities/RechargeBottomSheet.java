package com.subh.shubhechha.Activities;

import static android.widget.LinearLayout.HORIZONTAL;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.subh.shubhechha.Adapters.AmountButtonAdapter;
import com.subh.shubhechha.R;
import java.util.Arrays;
import java.util.List;

public class RechargeBottomSheet extends BottomSheetDialogFragment {

    private RecyclerView rvAmountButtons;
    private EditText etAmount;
    private TextView btnProceed;
    private View btnClose;

    private List<Integer> amounts = Arrays.asList(100, 200, 500, 1000);

    public static RechargeBottomSheet newInstance() {
        return new RechargeBottomSheet();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottomsheet_recharge_wallet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        rvAmountButtons = view.findViewById(R.id.rvAmountButtons);
        etAmount = view.findViewById(R.id.etAmount);
        btnProceed = view.findViewById(R.id.btnProceed);
        btnClose = view.findViewById(R.id.btnClose);

        // Setup RecyclerView with GridLayoutManager (2 columns)
        rvAmountButtons.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));

        // Setup adapter
        AmountButtonAdapter adapter = new AmountButtonAdapter(amounts, amount -> {
            // When an amount button is clicked, update the EditText
            etAmount.setText(String.valueOf(amount));
        });
        rvAmountButtons.setAdapter(adapter);

        // Close button click
        btnClose.setOnClickListener(v -> dismiss());

        // Proceed button click
        btnProceed.setOnClickListener(v -> {
            String amountText = etAmount.getText().toString();
            if (!amountText.isEmpty()) {
                int amount = Integer.parseInt(amountText);
                proceedWithRecharge(amount);
            } else {
                Toast.makeText(requireContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void proceedWithRecharge(int amount) {
        // Implement your recharge logic here
        // For example: navigate to payment gateway or API call
        Toast.makeText(requireContext(), "Proceeding with ₹" + amount, Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
