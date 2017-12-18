package com.sample.food.gaugesample;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public abstract class RiskoMeterView extends View {

    /**
     * the max range in Riskometer, {@code default = 100}
     */
    private int maxRiskPercentage = 100;
    /**
     * the min range in Riskometer, {@code default = 0}
     */
    private int minRiskPercentage = 0;
    /**
     * the last riskPercentage which you set by {@link #RiskTo(float)}
     * or if you stop Riskometer By {@link #stop()} method.
     */
    private float riskPercentage = minRiskPercentage;
    /**
     * what is riskPercentage now in <b>float</b>
     */
    private float currentRiskPercentage = 0f;

    private ValueAnimator riskAnimator;
    private Animator.AnimatorListener animatorListener;

    /**
     * to contain all drawing that doesn't change
     */
    protected Bitmap backgroundBitmap;
    private Paint backgroundBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int padding = 0;
    /**
     * view width without padding
     */
    private int widthPa = 0;

    /**
     * low riskPercentage area
     */
    private int lowRiskPercent = 20;
    /**
     * low riskPercentage area
     */
    private int moderateLowRiskPercent = 40;
    /**
     * medium riskPercentage area
     */
    private int mediumRiskPercent = 60;
    /**
     * medium riskPercentage area
     */
    private int moderatelyHighRiskPercent = 80;

    private boolean attachedToWindow = false;

    protected float translatedDx = 0;
    protected float translatedDy = 0;

    public RiskoMeterView(Context context) {
        this(context, null);
        init();
    }

    public RiskoMeterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public RiskoMeterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        checkRiskMeterPercent();
        if (Build.VERSION.SDK_INT >= 11) {
            riskAnimator = ValueAnimator.ofFloat(0f, 1f);
            animatorListener = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            };
        }
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }

    private void checkRiskMeterPercent() {
        if (lowRiskPercent > moderateLowRiskPercent)
            throw new IllegalArgumentException("lowRiskPercent must be smaller than moderateLowRiskPercent");
        if (moderateLowRiskPercent > mediumRiskPercent)
            throw new IllegalArgumentException("moderateLowRiskPercent must be smaller than mediumRiskPercent");
        if (mediumRiskPercent > moderatelyHighRiskPercent)
            throw new IllegalArgumentException("mediumRiskPercent must be smaller than moderatelyHighRiskPercent");
        if (lowRiskPercent > 100 || lowRiskPercent < 0)
            throw new IllegalArgumentException("lowRiskPercent must be between [0, 100]");
        if (mediumRiskPercent > 100 || mediumRiskPercent < 0)
            throw new IllegalArgumentException("mediumRiskPercent must be between [0, 100]");
    }

    /**
     * convert dp to <b>pixel</b>.
     *
     * @param dp to convert.
     * @return Dimension in pixel.
     */
    public float dpTOpx(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }

    /**
     * notice that {@link #backgroundBitmap} must recreate.
     */
    abstract protected void updateBackgroundBitmap();

    /**
     * notice that padding or size have changed.
     */
    private void updatePadding(int left, int top, int right, int bottom) {
        padding = Math.max(Math.max(left, right), Math.max(top, bottom));
        widthPa = getWidth() - padding * 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(translatedDx, translatedDy);
        if (backgroundBitmap != null)
            canvas.drawBitmap(backgroundBitmap, 0f, 0f, backgroundBitmapPaint);
    }


    /**
     * create canvas to draw {@link #backgroundBitmap}.
     *
     * @return {@link #backgroundBitmap}'s canvas.
     */
    protected Canvas createBackgroundBitmapCanvas() {
        if (getWidth() == 0 || getHeight() == 0)
            return new Canvas();
        backgroundBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        return new Canvas(backgroundBitmap);
    }

    /**
     * use this method just when you wont to stop {@code RiskTo and realRiskTo}.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void stop() {
        if (Build.VERSION.SDK_INT < 11)
            return;
        if (!riskAnimator.isRunning())
            return;
        riskPercentage = currentRiskPercentage;
        cancelRiskAnimator();
    }

    /**
     * cancel all animators without call
     */
    protected void cancelRiskAnimator() {
        cancelRiskMove();

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void cancelRiskMove() {
        if (Build.VERSION.SDK_INT < 11)
            return;
        riskAnimator.cancel();
    }

    /**
     * rotate indicator to correct riskPercentage without animation.
     *
     * @param risk correct riskPercentage to move.
     */
    public void setRiskAt(float risk) {
        risk = (risk > maxRiskPercentage) ? maxRiskPercentage : (risk < minRiskPercentage) ? minRiskPercentage : risk;
        this.riskPercentage = risk;
        this.currentRiskPercentage = risk;
        cancelRiskAnimator();
        invalidate();
    }

    /**
     * move riskPercentage to correct {@code int},
     * it should be between [{@link #minRiskPercentage}, {@link #maxRiskPercentage}].<br>
     * <br>
     * if {@code riskPercentage > maxRiskPercentage} riskPercentage will change to {@link #maxRiskPercentage},<br>
     * if {@code riskPercentage < minRiskPercentage} riskPercentage will change to {@link #minRiskPercentage}.<br>
     * <p>
     * it is the same {@link #RiskTo(float, long)}
     * with default {@code moveDuration = 2000}.
     *
     * @param risk correct riskPercentage to move.
     * @see #RiskTo(float, long)
     */
    public void RiskTo(float risk) {
        RiskTo(risk, 2000);
    }

    /**
     * move riskPercentage to correct {@code int},
     * it should be between [{@link #minRiskPercentage}, {@link #maxRiskPercentage}].<br>
     * <br>
     * if {@code riskPercentage > maxRiskPercentage} riskPercentage will change to {@link #maxRiskPercentage},<br>
     * if {@code riskPercentage < minRiskPercentage} riskPercentage will change to {@link #minRiskPercentage}.
     *
     * @param risk        correct riskPercentage to move.
     * @param moveDuration The length of the animation, in milliseconds.
     *                     This value cannot be negative.
     * @see #RiskTo(float)
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void RiskTo(float risk, long moveDuration) {
        risk = (risk > maxRiskPercentage) ? maxRiskPercentage : (risk < minRiskPercentage) ? minRiskPercentage : risk;
        if (risk == this.riskPercentage)
            return;
        this.riskPercentage = risk;

        if (Build.VERSION.SDK_INT < 11) {
            setRiskAt(risk);
            return;
        }

        cancelRiskAnimator();
        riskAnimator = ValueAnimator.ofFloat(currentRiskPercentage, risk);
        riskAnimator.setInterpolator(new DecelerateInterpolator());
        riskAnimator.setDuration(moveDuration);
        riskAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentRiskPercentage = (float) riskAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        riskAnimator.addListener(animatorListener);
        riskAnimator.start();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelRiskAnimator();
        attachedToWindow = false;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putFloat("riskPercentage", riskPercentage);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        riskPercentage = bundle.getFloat("riskPercentage");
        state = bundle.getParcelable("superState");
        super.onRestoreInstanceState(state);
        setRiskAt(riskPercentage);
    }

    /**
     * what is correct riskPercentage now.
     *
     * @return correct riskPercentage now.
     */
    public float getCurrentRiskPercentage() {
        return currentRiskPercentage;
    }

    /**
     * get max riskPercentage in riskometer, default max riskPercentage is 100.
     *
     * @return max riskPercentage.
     * @see #getMinRiskPercentage()
     */
    public int getMaxRiskPercentage() {
        return maxRiskPercentage;
    }

    /**
     * get min riskPercentage in riskometer, default min riskPercentage is 0.
     *
     * @return min riskPercentage.
     * @see #getMaxRiskPercentage()
     */
    public int getMinRiskPercentage() {
        return minRiskPercentage;
    }

    /**
     * @return the long of low riskPercentage area (low section) as Offset [0, 1].
     */
    public float getLowRiskOffset() {
        return lowRiskPercent * .01f;
    }

    /**
     * @return the long of low riskPercentage area (low section) as Offset [0, 1].
     */
    public float getModerateLowRiskOffset() {
        return moderateLowRiskPercent * .01f;
    }

    /**
     * @return the long of Medium riskPercentage area (Medium section) as percent.
     */
    public int getMediumRiskPercent() {
        return mediumRiskPercent;
    }

    /**
     * @return the long of Medium riskPercentage area (Medium section) as Offset [0, 1].
     */
    public float getMediumRiskOffset() {
        return mediumRiskPercent * .01f;
    }

    /**
     * @return the long of Medium riskPercentage area (Medium section) as Offset [0, 1].
     */
    public float getModeratelyOffset() {
        return moderatelyHighRiskPercent * .01f;
    }

    /**
     * @return View width without padding.
     */
    public int getWidthPa() {
        return widthPa;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        updatePadding(left, top, right, bottom);
        super.setPadding(padding, padding, padding, padding);
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        updatePadding(start, top, end, bottom);
        super.setPaddingRelative(padding, padding, padding, padding);
    }

  public int getPadding() {
        return padding;
    }

    /**
     * @return whether this view attached to Layout or not.
     */
    public boolean isAttachedToWindow() {
        return attachedToWindow;
    }

}
