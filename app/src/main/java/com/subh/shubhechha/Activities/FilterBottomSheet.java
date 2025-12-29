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
    private TextView btnSubmit, btnClearFilters;
    private FrameLayout btnClose;

    // Store as "0" for Veg, "1" for Non-Veg, null for none
    private String selectedFilter = null;
    private String selectedSort = null;

    public interface FilterListener {
        void onFilterApplied(String filter, String sort);
    }

    private FilterListener listener;

    public void setFilterListener(FilterListener listener) {
        this.listener = listener;
    }

    public void setCurrentFilters(String currentFilter, String currentSort) {
        this.selectedFilter = currentFilter;
        this.selectedSort = currentSort;
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

        btnVeg = view.findViewById(R.id.btnVeg);
        btnNonVeg = view.findViewById(R.id.btnNonVeg);
        btnLowToHigh = view.findViewById(R.id.btnLowToHigh);
        btnHighToLow = view.findViewById(R.id.btnHighToLow);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnClose = view.findViewById(R.id.btnClose);

        // Filter buttons - send "1" for Veg, "0" for Non-Veg
        btnVeg.setOnClickListener(v -> selectFilter("1"));
        btnNonVeg.setOnClickListener(v -> selectFilter("0"));

        // Sort buttons
        btnLowToHigh.setOnClickListener(v -> selectSort("pl2h"));
        btnHighToLow.setOnClickListener(v -> selectSort("ph2l"));

        btnSubmit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFilterApplied(selectedFilter, selectedSort);
            }
            dismiss();
        });

        if (btnClearFilters != null) {
            btnClearFilters.setOnClickListener(v -> {
                selectedFilter = null;
                selectedSort = null;
                updateFilterUI();
                updateSortUI();
                if (listener != null) {
                    listener.onFilterApplied(null, null);
                }
                dismiss();
            });
        }

        btnClose.setOnClickListener(v -> dismiss());

        updateFilterUI();
        updateSortUI();
    }

    private void selectFilter(String filter) {
        // Toggle: click again to deselect
        if (selectedFilter != null && selectedFilter.equals(filter)) {
            selectedFilter = null;
        } else {
            selectedFilter = filter;
        }
        updateFilterUI();
    }

    private void selectSort(String sort) {
        if (selectedSort != null && selectedSort.equals(sort)) {
            selectedSort = null;
        } else {
            selectedSort = sort;
        }
        updateSortUI();
    }

    private void updateFilterUI() {
        // Reset both
        btnVeg.setBackgroundResource(R.drawable.btn_unselected);
        btnVeg.setTextColor(getResources().getColor(android.R.color.darker_gray));
        btnNonVeg.setBackgroundResource(R.drawable.btn_unselected);
        btnNonVeg.setTextColor(getResources().getColor(android.R.color.darker_gray));

        // Apply selection - "0" is Veg, "1" is Non-Veg
        if (selectedFilter != null) {
            if (selectedFilter.equals("1")) {
                btnVeg.setBackgroundResource(R.drawable.btn_selected);
                btnVeg.setTextColor(getResources().getColor(android.R.color.white));
            } else if (selectedFilter.equals("0")) {
                btnNonVeg.setBackgroundResource(R.drawable.btn_selected);
                btnNonVeg.setTextColor(getResources().getColor(android.R.color.white));
            }
        }
    }

    private void updateSortUI() {
        btnLowToHigh.setBackgroundResource(R.drawable.btn_unselected);
        btnLowToHigh.setTextColor(getResources().getColor(android.R.color.darker_gray));
        btnHighToLow.setBackgroundResource(R.drawable.btn_unselected);
        btnHighToLow.setTextColor(getResources().getColor(android.R.color.darker_gray));

        if (selectedSort != null) {
            if (selectedSort.equals("pl2h")) {
                btnLowToHigh.setBackgroundResource(R.drawable.btn_selected);
                btnLowToHigh.setTextColor(getResources().getColor(android.R.color.white));
            } else if (selectedSort.equals("ph2l")) {
                btnHighToLow.setBackgroundResource(R.drawable.btn_selected);
                btnHighToLow.setTextColor(getResources().getColor(android.R.color.white));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setDraggable(false);
        }
    }
}