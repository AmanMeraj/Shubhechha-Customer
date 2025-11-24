package com.subh.shubhechha.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.subh.shubhechha.R;
import com.subh.shubhechha.databinding.ActivityIntroductionBinding;
import com.subh.shubhechha.utils.Utility;

public class IntroductionActivity extends Utility {
    ActivityIntroductionBinding binding;
    private int currentPage = 0;
    private boolean isAnimating = false;

    // Content data for each page
    private int[] images = {R.drawable.delivery_person, R.drawable.delivery2, R.drawable.delivery3};
    private int[] titles = {R.string.intro_title_1, R.string.intro_title_2, R.string.intro_title_3};
    private int[] descriptions = {R.string.intro_description_1, R.string.intro_description_2, R.string.intro_description_3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityIntroductionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        // Set initial content
        binding.titleText.setText(titles[0]);
        binding.descriptionText.setText(descriptions[0]);
        updateIndicators();
    }

    private void setupClickListeners() {
        binding.nextButton.setOnClickListener(v -> {
            if (!isAnimating) {
                if (currentPage < 2) {
                    currentPage++;
                    animateToNextPage();
                } else {
                    // Navigate to next activity (e.g., MainActivity or LoginActivity)
                    navigateToNextActivity();
                }
            }
        });
    }

    private void animateToNextPage() {
        isAnimating = true;

        // Get current and next image views
        ImageView currentImageView = getCurrentImageView();
        ImageView nextImageView = getNextImageView();

        // Make next image visible and position it off-screen to the right
        nextImageView.setVisibility(View.VISIBLE);
        nextImageView.setTranslationX(binding.imageContainer.getWidth());
        nextImageView.setTranslationY(0);

        // Animation for current image (sunset effect - moves down-left)
        float currentImageWidth = currentImageView.getWidth();
        ObjectAnimator currentMoveX = ObjectAnimator.ofFloat(currentImageView, "translationX", 0, -currentImageWidth * 0.3f);
        ObjectAnimator currentMoveY = ObjectAnimator.ofFloat(currentImageView, "translationY", 0, binding.imageContainer.getHeight() * 0.5f);
        ObjectAnimator currentFade = ObjectAnimator.ofFloat(currentImageView, "alpha", 1f, 0f);

        AnimatorSet currentAnimatorSet = new AnimatorSet();
        currentAnimatorSet.playTogether(currentMoveX, currentMoveY, currentFade);
        currentAnimatorSet.setDuration(600);
        currentAnimatorSet.setInterpolator(new AccelerateInterpolator());

        // Animation for next image (sunrise from right)
        ObjectAnimator nextMoveX = ObjectAnimator.ofFloat(nextImageView, "translationX", binding.imageContainer.getWidth(), 0);
        ObjectAnimator nextFade = ObjectAnimator.ofFloat(nextImageView, "alpha", 0.5f, 1f);

        AnimatorSet nextAnimatorSet = new AnimatorSet();
        nextAnimatorSet.playTogether(nextMoveX, nextFade);
        nextAnimatorSet.setDuration(600);
        nextAnimatorSet.setInterpolator(new DecelerateInterpolator());

        // Animate content (title and description)
        animateContentChange();

        // Play both animations together
        AnimatorSet mainAnimatorSet = new AnimatorSet();
        mainAnimatorSet.playTogether(currentAnimatorSet, nextAnimatorSet);

        mainAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Reset current image view
                currentImageView.setVisibility(View.GONE);
                currentImageView.setTranslationX(0);
                currentImageView.setTranslationY(0);
                currentImageView.setAlpha(1f);

                // Update indicators
                updateIndicators();

                isAnimating = false;
            }
        });

        mainAnimatorSet.start();
    }

    private void animateContentChange() {
        // Fade out current content
        binding.titleText.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> {
                    // Update text
                    binding.titleText.setText(titles[currentPage]);
                    binding.descriptionText.setText(descriptions[currentPage]);

                    // Fade in new content
                    binding.titleText.animate().alpha(1f).setDuration(300).start();
                    binding.descriptionText.animate().alpha(1f).setDuration(300).start();
                }).start();

        binding.descriptionText.animate()
                .alpha(0f)
                .setDuration(200)
                .start();
    }

    private ImageView getCurrentImageView() {
        switch (currentPage - 1) {
            case 0:
                return binding.introImage1;
            case 1:
                return binding.introImage2;
            default:
                return binding.introImage1;
        }
    }

    private ImageView getNextImageView() {
        switch (currentPage) {
            case 1:
                return binding.introImage2;
            case 2:
                return binding.introImage3;
            default:
                return binding.introImage1;
        }
    }

    private void updateIndicators() {
        // Reset all indicators
        binding.indicator1.setBackgroundResource(R.drawable.indicator_inactive);
        binding.indicator2.setBackgroundResource(R.drawable.indicator_inactive);
        binding.indicator3.setBackgroundResource(R.drawable.indicator_inactive);

        // Update widths
        binding.indicator1.getLayoutParams().width = dpToPx(8);
        binding.indicator2.getLayoutParams().width = dpToPx(8);
        binding.indicator3.getLayoutParams().width = dpToPx(8);

        // Set active indicator
        switch (currentPage) {
            case 0:
                binding.indicator1.setBackgroundResource(R.drawable.indicator_active);
                binding.indicator1.getLayoutParams().width = dpToPx(32);
                break;
            case 1:
                binding.indicator2.setBackgroundResource(R.drawable.indicator_active);
                binding.indicator2.getLayoutParams().width = dpToPx(32);
                break;
            case 2:
                binding.indicator3.setBackgroundResource(R.drawable.indicator_active);
                binding.indicator3.getLayoutParams().width = dpToPx(32);
                break;
        }

        // Request layout update
        binding.indicator1.requestLayout();
        binding.indicator2.requestLayout();
        binding.indicator3.requestLayout();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void navigateToNextActivity() {
        // Navigate to your main activity or login activity
        // Example:
         Intent intent = new Intent(IntroductionActivity.this, LoginActivity.class);
         pref.setPrefBoolean(this, pref.is_intro_shown, true);
         startActivity(intent);
         finish();
    }
}