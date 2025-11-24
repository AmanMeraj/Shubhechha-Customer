package com.subh.shubhechha.Activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.subh.shubhechha.R;
import com.subh.shubhechha.databinding.ActivityCheckoutBinding;
import com.subh.shubhechha.databinding.ActivityContainerBinding;
import com.subh.shubhechha.databinding.ItemRadioButtonBinding;
import com.subh.shubhechha.utils.Utility;

import java.util.Arrays;
import java.util.List;

public class CheckoutActivity extends Utility {
    ActivityCheckoutBinding binding;

    String selectedPaymentMethod="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupCollapsingToolbar();
        binding.backBtn.setOnClickListener(v->finish());
        List<String> paymentMethods = Arrays.asList("Cash on Delivery", "UPI", "Pay Later", "Credit Card");
        populatePaymentMethods(paymentMethods);

    }

    private void populatePaymentMethods(List<String> paymentMethods) {
        binding.flexPaymentMethods.removeAllViews();
        RadioGroup hiddenGroup = new RadioGroup(this);
        for (String paymentMethod : paymentMethods) {
            ItemRadioButtonBinding radioButtonBinding = ItemRadioButtonBinding.inflate(LayoutInflater.from(this),binding.flexPaymentMethods,false);

            RadioButton radioButton = radioButtonBinding.radioButton;
            radioButton.setText(paymentMethod);

            hiddenGroup.addView(radioButton);
            radioButton.setOnClickListener(v -> {
                for (int i = 0; i < hiddenGroup.getChildCount(); i++) {
                    RadioButton rb = (RadioButton) hiddenGroup.getChildAt(i);
                    rb.setChecked(rb == radioButton);
                }

                selectedPaymentMethod = paymentMethod;
                Toast.makeText(this, "Selected: " + selectedPaymentMethod, Toast.LENGTH_SHORT).show();
            });

            binding.flexPaymentMethods.addView(radioButtonBinding.getRoot());

        }
    }
    private void setupCollapsingToolbar() {
        try {
            binding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isCollapsed = false;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    int scrollRange = appBarLayout.getTotalScrollRange();
                    if (scrollRange == 0) return;

                    float percentage = Math.abs(verticalOffset / (float) scrollRange);
                    if (Float.isNaN(percentage) || Float.isInfinite(percentage)) return;

                    // Fade in/out toolbar title
                    binding.tvToolbarTitle.setAlpha(percentage);

                    // Fade in/out expanded title
                    binding.tvCartExpanded.setAlpha(1 - percentage);

                    // Scale the background curve
                    float scale = 1 - (percentage * 0.2f);
                    scale = Math.max(0.8f, Math.min(1f, scale));
                    binding.peachCurveBg.setScaleY(scale);

                    // Check if fully collapsed or expanded
                    if (Math.abs(verticalOffset) >= scrollRange) {
                        if (!isCollapsed) {
                            isCollapsed = true;
                            onToolbarCollapsed();
                        }
                    } else {
                        if (isCollapsed) {
                            isCollapsed = false;
                            onToolbarExpanded();
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onToolbarCollapsed() {
        // Called when toolbar is fully collapsed
        // Add any additional animations or state changes here
    }

    private void onToolbarExpanded() {
        // Called when toolbar is fully expanded
        // Add any additional animations or state changes here
    }
}