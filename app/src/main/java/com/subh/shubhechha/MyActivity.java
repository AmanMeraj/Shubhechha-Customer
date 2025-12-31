package com.subh.shubhechha;

import android.app.Activity;
import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class MyActivity extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
