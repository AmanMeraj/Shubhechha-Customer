package com.subh.shubhechha;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CurvedBottomDrawable extends Drawable {

    private Paint paint;
    private Path path;
    private int color;
    private float curveAmount = 0f; // 0 = flat, 0.5 = half-moon, 1 = full circle

    public CurvedBottomDrawable(int color) {
        this.color = color;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        path = new Path();
    }

    public void setCurveAmount(float amount) {
        // Clamp between 0 and 1
        this.curveAmount = Math.max(0f, Math.min(1f, amount));
        invalidateSelf();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int width = getBounds().width();
        int height = getBounds().height();

        path.reset();

        if (curveAmount == 0f) {
            // Draw a simple rectangle when fully expanded
            path.addRect(0, 0, width, height, Path.Direction.CW);
        } else {
            // Calculate the curve height based on curveAmount
            // At 0.5 (half-moon), the curve should be about 1/4 of the width
            float maxCurveHeight = width / 4f;
            float currentCurveHeight = maxCurveHeight * (curveAmount / 0.5f);

            // Clamp curve height
            currentCurveHeight = Math.min(currentCurveHeight, maxCurveHeight);

            // Start from top-left
            path.moveTo(0, 0);

            // Draw top edge
            path.lineTo(width, 0);

            // Draw right edge
            path.lineTo(width, height);

            // Draw bottom curved edge using quadratic bezier curve
            // Control point is at the center bottom, pulled down by currentCurveHeight
            float controlX = width / 2f;
            float controlY = height + currentCurveHeight;

            // End point is at bottom-left
            path.quadTo(controlX, controlY, 0, height);

            // Draw left edge back to top-left
            path.lineTo(0, 0);

            path.close();
        }

        canvas.drawPath(path, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}