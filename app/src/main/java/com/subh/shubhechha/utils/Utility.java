package com.subh.shubhechha.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility extends AppCompatActivity {

    public SharedPref pref = new SharedPref();
    public String device_type = "Android";


//    @SuppressLint("HardwareIds")
//    public int getDeviceId(){
//        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//    }

    public static boolean isNumberOnly(String val) {
        return TextUtils.isDigitsOnly(val);
    }

    public String getVersionName(Context c) {
        String version = "";
        try {
            PackageInfo pInfo = c.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public boolean isInternetConnected(Context con) {
        boolean result = false;
        if (con != null) {
            ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                result = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
            }
            if (!result) {
                showToastGrey(con, "No Internet Connection");
            }
        }
        return result;
    }


    public void showToastGrey(Context c, String msg) {
        Toast toast = Toast.makeText(c, msg, Toast.LENGTH_SHORT);
        View view = toast.getView();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            //view.setBackgroundResource(R.drawable.bg_toast_grey);
            TextView text = view.findViewById(android.R.id.message);
            //text.setTextColor(getResources().getColor(R.color.white));
        }
        toast.show();
    }

    public void showAlert(Context c, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    //do things
                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showAlertFinish(Context c, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    //do things
// Finish the activity
                    if (c instanceof Activity) {
                        ((Activity) c).finish();
                    }
                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public String getOnlyDigits(String s) {
        Pattern pattern = Pattern.compile("[^0-9]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        return number;
    }

    public String capsFirstCharacter(String s) {
        StringBuilder sb = new StringBuilder(s);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }
}
