package com.subh.shubhechha.Activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.subh.shubhechha.R;

public class FilterBottomSheet extends BottomSheetDialogFragment {

    private TextView btnVeg, btnNonVeg;
    private TextView btnLowToHigh, btnHighToLow;
    private TextView btnSubmit;
    private FrameLayout btnClose;

    private String selectedFilter = "Veg";
    private String selectedSort = "pl2h"; // Changed to short key

    public interface FilterListener {
        void onFilterApplied(String filter, String sort);
    }

    private FilterListener listener;

    public void setFilterListener(FilterListener listener) {
        this.listener = listener;
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
        return inflater.inflate(R.layout.bottom_sheet_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        btnVeg = view.findViewById(R.id.btnVeg);
        btnNonVeg = view.findViewById(R.id.btnNonVeg);
        btnLowToHigh = view.findViewById(R.id.btnLowToHigh);
        btnHighToLow = view.findViewById(R.id.btnHighToLow);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnClose = view.findViewById(R.id.btnClose);

        // Set click listeners for Filter buttons
        btnVeg.setOnClickListener(v -> selectFilter("Veg"));
        btnNonVeg.setOnClickListener(v -> selectFilter("Non-Veg"));

        // Set click listeners for Sort buttons with short keys
        btnLowToHigh.setOnClickListener(v -> selectSort("pl2h"));
        btnHighToLow.setOnClickListener(v -> selectSort("ph2l"));

        // Submit button
        btnSubmit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFilterApplied(selectedFilter, selectedSort);
            }
            dismiss();
        });

        // Close button
        btnClose.setOnClickListener(v -> dismiss());

        // Set initial selection
        updateFilterUI();
        updateSortUI();
    }

    private void selectFilter(String filter) {
        selectedFilter = filter;
        updateFilterUI();
    }

    private void selectSort(String sort) {
        selectedSort = sort;
        updateSortUI();
    }

    private void updateFilterUI() {
        if (selectedFilter.equals("Veg")) {
            btnVeg.setBackgroundResource(R.drawable.btn_selected);
            btnVeg.setTextColor(getResources().getColor(android.R.color.white));
            btnNonVeg.setBackgroundResource(R.drawable.btn_unselected);
            btnNonVeg.setTextColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            btnNonVeg.setBackgroundResource(R.drawable.btn_selected);
            btnNonVeg.setTextColor(getResources().getColor(android.R.color.white));
            btnVeg.setBackgroundResource(R.drawable.btn_unselected);
            btnVeg.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);

            // Disable pull/drag
            behavior.setDraggable(false);
        }
    }

    private void updateSortUI() {
        if (selectedSort.equals("pl2h")) { // Price Low to High
            btnLowToHigh.setBackgroundResource(R.drawable.btn_selected);
            btnLowToHigh.setTextColor(getResources().getColor(android.R.color.white));
            btnHighToLow.setBackgroundResource(R.drawable.btn_unselected);
            btnHighToLow.setTextColor(getResources().getColor(android.R.color.darker_gray));
        } else { // ph2l - Price High to Low
            btnHighToLow.setBackgroundResource(R.drawable.btn_selected);
            btnHighToLow.setTextColor(getResources().getColor(android.R.color.white));
            btnLowToHigh.setBackgroundResource(R.drawable.btn_unselected);
            btnLowToHigh.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }
}