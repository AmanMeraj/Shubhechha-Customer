package com.subh.shubhechha.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.subh.shubhechha.R;
import com.subh.shubhechha.databinding.ActivityWebviewBinding;

public class ActivityWebview extends AppCompatActivity {
    ActivityWebviewBinding binding;

    // URLs for different pages
    private static final String TERMS_URL = "https://codebuzzers.net/Shubhechha-landing/termsand_condition_mob.html";
    private static final String PRIVACY_URL = "https://codebuzzers.net/Shubhechha-landing/privacy_policy_mob.html";
    private static final String FAQ_URL = "https://codebuzzers.net/Shubhechha-landing/faq_mob.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.toolbar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Setup WebView
        setupWebView();

        // Setup click listeners for tabs
        setupTabListeners();

        // Load Terms & Conditions by default
        loadUrl(TERMS_URL);
    }

    private void setupWebView() {
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setLoadWithOverviewMode(true);
        binding.webView.getSettings().setUseWideViewPort(true);
        binding.webView.getSettings().setBuiltInZoomControls(false);
        binding.webView.getSettings().setSupportZoom(false);

        // Set WebViewClient to handle page loading within the WebView
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    private void setupTabListeners() {
        binding.tvTermsConditions.setOnClickListener(v -> {
            selectTab(binding.tvTermsConditions);
            loadUrl(TERMS_URL);
        });

        binding.tvPrivacyPolicy.setOnClickListener(v -> {
            selectTab(binding.tvPrivacyPolicy);
            loadUrl(PRIVACY_URL);
        });

        binding.tvFaq.setOnClickListener(v -> {
            selectTab(binding.tvFaq);
            loadUrl(FAQ_URL);
        });
    }

    private void selectTab(TextView selectedTab) {
        // Reset all tabs
        resetTab(binding.tvTermsConditions);
        resetTab(binding.tvPrivacyPolicy);
        resetTab(binding.tvFaq);

        // Highlight selected tab
        selectedTab.setBackgroundResource(R.drawable.orange_btn);
    }

    private void resetTab(TextView tab) {
        tab.setBackground(null);
    }

    private void loadUrl(String url) {
        binding.webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        // Handle WebView back navigation
        if (binding.webView.canGoBack()) {
            binding.webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}