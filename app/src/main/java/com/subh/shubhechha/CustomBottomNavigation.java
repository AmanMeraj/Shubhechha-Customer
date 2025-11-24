package com.subh.shubhechha;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomBottomNavigation extends FrameLayout {

    private Paint curvePaint;
    private Paint pillPaint;
    private Path curvePath;
    private int selectedPosition = 1; // 0=Home, 1=Wallet, 2=Profile
    private int curveRadius = 140;
    private int curveDepth = 150;
    private ValueAnimator curveAnimator;
    private float animatedPosition = 0;

    private LinearLayout homeContainer, walletContainer, profileContainer;
    private ImageView homeIcon, walletIcon, profileIcon;
    private TextView homeText, walletText, profileText;

    private int iconSizeNormal;
    private int iconSizeSelected;
    private float pillWidth;
    private float pillHeight;
    private float pillVerticalOffset; // controls space below icons
    private boolean isSmallScreen = false;

    public CustomBottomNavigation(Context context) {
        super(context);
        init(context);
    }

    public CustomBottomNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomBottomNavigation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);

        // Calculate responsive sizing
        calculateSizeBasedOnScreen(context);

        curvePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        curvePaint.setColor(0xFFFF8C42); // Orange color
        curvePaint.setStyle(Paint.Style.FILL);

        pillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pillPaint.setColor(0xFF2C2C2C); // Dark pill background
        pillPaint.setStyle(Paint.Style.FILL);

        curvePath = new Path();

        // Inflate layout
        LayoutInflater.from(context).inflate(R.layout.custom_bottom_nav, this, true);

        // Initialize after layout inflation
        post(() -> {
            initializeViews();
            setupClickListeners();
            updateIconStates();
        });
    }

//    private void calculateSizeBasedOnScreen(Context context) {
//
//        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
//        int dpi = metrics.densityDpi;
//
//        // -----------------------------------------
//        // 🔥 DPI BASED SIZING — PURE, SIMPLE, ACCURATE
//        // -----------------------------------------
//
//        if (dpi <= 250) {   // very low DPI (Poco C61, cheap phones)
//            iconSizeNormal = 18;     // realistic sizes, not tiny!
//            iconSizeSelected = 22;
//
//            pillWidth = 80;
//            pillHeight = 95;
//
//            pillVerticalOffset = 1.40f;   // 🔥 THE FIX — pushes pill downward
//            return;
//        }
//
//
//        if (dpi <= 320) {    // low–mid DPI
//            iconSizeNormal = 18;
//            iconSizeSelected = 20;
//
//            pillWidth = 100;
//            pillHeight = 115;
//            pillVerticalOffset = 1.28f;
//            return;
//        }
//
//        if (dpi <= 440) {    // MOST PHONES – 1080p category
//            iconSizeNormal = 22;
//            iconSizeSelected = 26;
//
//            pillWidth = 125;
//            pillHeight = 145;
//            pillVerticalOffset = 1.45f;
//            return;
//        }
//
//        if (dpi <= 560) {    // high DPI (QHD phones, flagship AMOLED)
//            iconSizeNormal = 22;
//            iconSizeSelected = 26;
//
//            pillWidth = 125;
//            pillHeight = 170;
//            pillVerticalOffset = 1.42f;
//            return;
//        }
//
//        // Ultra-high DPI (foldables, tablets, modern QHD+)
//        iconSizeNormal = 28;
//        iconSizeSelected = 32;
//
//        pillWidth = 150;
//        pillHeight = 175;
//        pillVerticalOffset = 1.55f;
//    }


    private void calculateSizeBasedOnScreen(Context context) {

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        float scale = metrics.densityDpi / 420f;
        // 420 is the reference for a perfectly normal 1080p screen

        // ICON SIZE
        iconSizeNormal  = (int) (20 * scale);
        iconSizeSelected = (int) (22 * scale);

        // PILL SIZE
        pillWidth  = 120 * scale;   // consistent width everywhere
        pillHeight = 155 * scale;   // consistent height everywhere

        // How low the pill sits
        pillVerticalOffset = 1.42f;   // fixed ratio → consistent look on all screens

        // CURVE SIZE
        curveRadius = (int) (140 * scale);
        curveDepth  = (int) (150 * scale);
    }


    private void initializeViews() {
        homeContainer = findViewById(R.id.homeContainer1);
        walletContainer = findViewById(R.id.walletContainer1);
        profileContainer = findViewById(R.id.profileContainer1);

        homeIcon = findViewById(R.id.homeIcon);
        walletIcon = findViewById(R.id.walletIcon);
        profileIcon = findViewById(R.id.profileIcon);

        homeText = findViewById(R.id.homeText);
        walletText = findViewById(R.id.walletText);
        profileText = findViewById(R.id.profileText);

        // Adjust spacing and translation if smaller screen
        if (isSmallScreen) {
            reduceTextTopMargin(homeText);
            reduceTextTopMargin(walletText);
            reduceTextTopMargin(profileText);

            float translateY = dpToPx(15); // shift both icon & text down
            applyDownwardTranslation(homeIcon, homeText, translateY);
            applyDownwardTranslation(walletIcon, walletText, translateY);
            applyDownwardTranslation(profileIcon, profileText, translateY);
        }
    }

    private void applyDownwardTranslation(ImageView icon, TextView text, float translateY) {
        if (icon != null) icon.setTranslationY(translateY);
        if (text != null) text.setTranslationY(translateY);
    }

    private void reduceTextTopMargin(TextView textView) {
        if (textView != null && textView.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
            params.topMargin = dpToPx(1); // tighter vertical spacing
            textView.setLayoutParams(params);
        }
    }

    private void setupClickListeners() {
        if (homeContainer != null) {
            homeContainer.setOnClickListener(v -> selectTab(0));
        }

        if (walletContainer != null) {
            walletContainer.setOnClickListener(v -> selectTab(1));
        }

        if (profileContainer != null) {
            profileContainer.setOnClickListener(v -> selectTab(2));
        }
    }

    public void selectTab(int position) {
        if (selectedPosition != position) {
            selectedPosition = position;
            animateCurve(position);
            updateIconStates();
            if (listener != null) {
                listener.onTabSelected(position);
            }
        }
    }

    private void animateCurve(int toPosition) {
        if (curveAnimator != null && curveAnimator.isRunning()) {
            curveAnimator.cancel();
        }

        curveAnimator = ValueAnimator.ofFloat(animatedPosition, toPosition);
        curveAnimator.setDuration(400);
        curveAnimator.setInterpolator(new DecelerateInterpolator());
        curveAnimator.addUpdateListener(animation -> {
            animatedPosition = (float) animation.getAnimatedValue();
            invalidate();
        });
        curveAnimator.start();
    }

    private void updateIconStates() {
        if (homeIcon == null || walletIcon == null || profileIcon == null) return;

        animateIconAndText(homeIcon, homeText, selectedPosition == 0);
        animateIconAndText(walletIcon, walletText, selectedPosition == 1);
        animateIconAndText(profileIcon, profileText, selectedPosition == 2);
    }

    private void animateIconAndText(final ImageView icon, final TextView text, boolean isSelected) {
        float targetAlpha = isSelected ? 1.0f : 0.6f;
        icon.animate().alpha(targetAlpha).setDuration(300).start();
        text.animate().alpha(targetAlpha).setDuration(300).start();

        int targetSize = isSelected ? iconSizeSelected : iconSizeNormal;
        int targetSizePx = dpToPx(targetSize);
        int currentSize = icon.getLayoutParams().width;

        ValueAnimator sizeAnimator = ValueAnimator.ofInt(currentSize, targetSizePx);
        sizeAnimator.setDuration(300);
        sizeAnimator.setInterpolator(new DecelerateInterpolator());
        sizeAnimator.addUpdateListener(animation -> {
            int size = (int) animation.getAnimatedValue();
            android.view.ViewGroup.LayoutParams params = icon.getLayoutParams();
            params.width = size;
            params.height = size;
            icon.setLayoutParams(params);
        });
        sizeAnimator.start();

        int targetColor = isSelected ? 0xFFFFFFFF : 0xFF808080;
        ValueAnimator colorAnimator = ValueAnimator.ofArgb(text.getCurrentTextColor(), targetColor);
        colorAnimator.setDuration(300);
        colorAnimator.addUpdateListener(animation ->
                text.setTextColor((int) animation.getAnimatedValue()));
        colorAnimator.start();

        ValueAnimator iconColorAnimator = ValueAnimator.ofArgb(
                isSelected ? 0xFF808080 : 0xFFFFFFFF,
                targetColor
        );
        iconColorAnimator.setDuration(200);
        iconColorAnimator.addUpdateListener(animation ->
                icon.setColorFilter((int) animation.getAnimatedValue()));
        iconColorAnimator.start();
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getContext().getResources().getDisplayMetrics()
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        if (width == 0 || height == 0) return;

        float tabWidth = width / 3f;
        float centerX = tabWidth * animatedPosition + tabWidth / 2f;

        curvePath.reset();
        curvePath.moveTo(0, height);
        curvePath.lineTo(0, curveDepth);

        curvePath.quadTo(centerX, 0, width, curveDepth);

        curvePath.lineTo(width, height);
        curvePath.close();

        canvas.drawPath(curvePath, curvePaint);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        if (width > 0 && height > 0) {
            float tabWidth = width / 3f;
            float centerX = tabWidth * animatedPosition + tabWidth / 2f;

            // Responsive vertical positioning of pill
            float pillY = (height - pillHeight) / pillVerticalOffset;

            RectF pillRect = new RectF(centerX - pillWidth / 2, pillY,
                    centerX + pillWidth / 2, pillY + pillHeight);
            canvas.drawRoundRect(pillRect, pillHeight / 2, pillHeight / 2, pillPaint);
        }

        super.dispatchDraw(canvas);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    // Listener
    public interface OnTabSelectedListener {
        void onTabSelected(int position);
    }

    private OnTabSelectedListener listener;

    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        this.listener = listener;
    }
}