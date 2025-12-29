package com.subh.shubhechha.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.subh.shubhechha.R;
import com.subh.shubhechha.utils.AuthHelper;
import com.subh.shubhechha.utils.SharedPref;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000; // 2 seconds
    private SharedPref pref;
    private AuthHelper authHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pref = new SharedPref();
        authHelper = new AuthHelper();

        // Navigate after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            navigateToNextScreen();
        }, SPLASH_DELAY);
    }

    private void navigateToNextScreen() {
        boolean isIntroShown = pref.getPrefBoolean(this, pref.is_intro_shown);

        Intent intent;

        if (!isIntroShown) {
            // First time user - show intro
            intent = new Intent(this, IntroductionActivity.class);
        } else {
            // User has seen intro - go directly to home (logged in or not)
            intent = new Intent(this, ContainerActivity.class);
        }

        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        // Prevent back press during splash
    }
}