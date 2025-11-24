package com.subh.shubhechha.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import com.subh.shubhechha.databinding.ActivitySplashBinding;
import com.subh.shubhechha.utils.Utility;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends Utility {
ActivitySplashBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Navigate to next screen after 3 seconds
        new Handler(Looper.getMainLooper()).postDelayed(this::proceed, 3000);
    }

    private void proceed(){
        if(pref.getPrefBoolean(this,pref.is_intro_shown)){
            if (pref.getPrefBoolean(this, pref.login_status)) {
                Intent intent = new Intent(SplashActivity.this, ContainerActivity.class);
                startActivity(intent);
                finish();

            } else {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }else {
            Intent intent = new Intent(SplashActivity.this, IntroductionActivity.class);
            startActivity(intent);
            finish();
        }
        }
    }
