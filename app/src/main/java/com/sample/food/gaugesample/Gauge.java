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

import java.util.Locale;

public abstract class Gauge extends View {

    /**
     * the max range in speedometer, {@code default = 100}
     */
    private int maxRiskPercentage = 100;
    /**
     * the min range in speedometer, {@code default = 0}
     */
    private int minRiskPercentage = 0;
    /**
     * the last speed which you set by {@link #speedTo(float)}
     * or if you stop speedometer By {@link #stop()} method.
     */
    private float speed = minRiskPercentage;
    /**
     * what is speed now in <b>int</b>
     */
    private int currentIntSpeed = 0;
    /**
     * what is speed now in <b>float</b>
     */
    private float currentSpeed = 0f;

    private ValueAnimator speedAnimator;
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
     * low speed area
     */
    private int lowSpeedPercent = 20;
    /**
     * low speed area
     */
    private int lowMidSpeedPercent = 40;
    /**
     * medium speed area
     */
    private int mediumSpeedPercent = 60;
    /**
     * medium speed area
     */
    private int mediumHighSpeedPercent = 80;

    private boolean attachedToWindow = false;

    protected float translatedDx = 0;
    protected float translatedDy = 0;

    /**
     * object to set text digits locale
     */
    private Locale locale = Locale.getDefault();

    /**
     * Number expresses the Acceleration, between (0, 1]
     */
    private float accelerate = .1f;
    /**
     * Number expresses the Deceleration, between (0, 1]
     */
    private float decelerate = .1f;

    //private Position speedTextPosition = Position.BOTTOM_CENTER;
    /**
     * space between unitText and speedText
     */
    private float unitSpeedInterval = dpTOpx(1);
    private float speedTextPadding = dpTOpx(20f);
    private boolean unitUnderSpeedText = false;
    private Bitmap speedUnitTextBitmap;

    /**
     * draw speed text as <b>integer</b> .
     */
    public static final byte INTEGER_FORMAT = 0;
    /**
     * draw speed text as <b>float</b>.
     */
    public static final byte FLOAT_FORMAT = 1;
    private byte speedTextFormat = FLOAT_FORMAT;

    public Gauge(Context context) {
        this(context, null);
        init();
    }

    public Gauge(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public Gauge(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        checkSpeedometerPercent();
        if (Build.VERSION.SDK_INT >= 11) {
            speedAnimator = ValueAnimator.ofFloat(0f, 1f);
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

    private void checkSpeedometerPercent() {
        if (lowSpeedPercent > mediumSpeedPercent)
            throw new IllegalArgumentException("lowSpeedPercent must be smaller than mediumSpeedPercent");
        if (lowSpeedPercent > 100 || lowSpeedPercent < 0)
            throw new IllegalArgumentException("lowSpeedPercent must be between [0, 100]");
        if (mediumSpeedPercent > 100 || mediumSpeedPercent < 0)
            throw new IllegalArgumentException("mediumSpeedPercent must be between [0, 100]");
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
     * use this method just when you wont to stop {@code speedTo and realSpeedTo}.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void stop() {
        if (Build.VERSION.SDK_INT < 11)
            return;
        if (!speedAnimator.isRunning())
            return;
        speed = currentSpeed;
        cancelSpeedAnimator();
    }

    /**
     * cancel all animators without call
     */
    protected void cancelSpeedAnimator() {
        cancelSpeedMove();

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void cancelSpeedMove() {
        if (Build.VERSION.SDK_INT < 11)
            return;
        speedAnimator.cancel();
    }

    /**
     * rotate indicator to correct speed without animation.
     *
     * @param speed correct speed to move.
     */
    public void setSpeedAt(float speed) {
        speed = (speed > maxRiskPercentage) ? maxRiskPercentage : (speed < minRiskPercentage) ? minRiskPercentage : speed;
        this.speed = speed;
        this.currentSpeed = speed;
        cancelSpeedAnimator();
        invalidate();
    }

    /**
     * move speed to correct {@code int},
     * it should be between [{@link #minRiskPercentage}, {@link #maxRiskPercentage}].<br>
     * <br>
     * if {@code speed > maxRiskPercentage} speed will change to {@link #maxRiskPercentage},<br>
     * if {@code speed < minRiskPercentage} speed will change to {@link #minRiskPercentage}.<br>
     * <p>
     * it is the same {@link #speedTo(float, long)}
     * with default {@code moveDuration = 2000}.
     *
     * @param speed correct speed to move.
     * @see #speedTo(float, long)
     */
    public void speedTo(float speed) {
        speedTo(speed, 2000);
    }

    /**
     * move speed to correct {@code int},
     * it should be between [{@link #minRiskPercentage}, {@link #maxRiskPercentage}].<br>
     * <br>
     * if {@code speed > maxRiskPercentage} speed will change to {@link #maxRiskPercentage},<br>
     * if {@code speed < minRiskPercentage} speed will change to {@link #minRiskPercentage}.
     *
     * @param speed        correct speed to move.
     * @param moveDuration The length of the animation, in milliseconds.
     *                     This value cannot be negative.
     * @see #speedTo(float)
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void speedTo(float speed, long moveDuration) {
        speed = (speed > maxRiskPercentage) ? maxRiskPercentage : (speed < minRiskPercentage) ? minRiskPercentage : speed;
        if (speed == this.speed)
            return;
        this.speed = speed;

        if (Build.VERSION.SDK_INT < 11) {
            setSpeedAt(speed);
            return;
        }

        cancelSpeedAnimator();
        speedAnimator = ValueAnimator.ofFloat(currentSpeed, speed);
        speedAnimator.setInterpolator(new DecelerateInterpolator());
        speedAnimator.setDuration(moveDuration);
        speedAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentSpeed = (float) speedAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        speedAnimator.addListener(animatorListener);
        speedAnimator.start();
    }

    /**
     * @param percentSpeed between [0, 100].
     * @return speed value at correct percentSpeed.
     */
    private float getSpeedValue(float percentSpeed) {
        percentSpeed = (percentSpeed > 100) ? 100 : (percentSpeed < 0) ? 0 : percentSpeed;
        return percentSpeed * (maxRiskPercentage - minRiskPercentage) * .01f + minRiskPercentage;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelSpeedAnimator();
        attachedToWindow = false;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putFloat("speed", speed);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        speed = bundle.getFloat("speed");
        state = bundle.getParcelable("superState");
        super.onRestoreInstanceState(state);
        setSpeedAt(speed);
    }

    /**
     * what is correct speed now.
     *
     * @return correct speed now.
     */
    public float getCurrentSpeed() {
        return currentSpeed;
    }

    /**
     * get max speed in speedometer, default max speed is 100.
     *
     * @return max speed.
     * @see #getMinRiskPercentage()
     */
    public int getMaxRiskPercentage() {
        return maxRiskPercentage;
    }

    /**
     * get min speed in speedometer, default min speed is 0.
     *
     * @return min speed.
     * @see #getMaxRiskPercentage()
     */
    public int getMinRiskPercentage() {
        return minRiskPercentage;
    }

    /**
     * @return the long of low speed area (low section) as Offset [0, 1].
     */
    public float getLowSpeedOffset() {
        return lowSpeedPercent * .01f;
    }

    /**
     * @return the long of low speed area (low section) as Offset [0, 1].
     */
    public float getLowMidSpeedOffset() {
        return lowMidSpeedPercent * .01f;
    }

    /**
     * @return the long of Medium speed area (Medium section) as percent.
     */
    public int getMediumSpeedPercent() {
        return mediumSpeedPercent;
    }

    /**
     * @return the long of Medium speed area (Medium section) as Offset [0, 1].
     */
    public float getMediumSpeedOffset() {
        return mediumSpeedPercent * .01f;
    }

    /**
     * @return the long of Medium speed area (Medium section) as Offset [0, 1].
     */
    public float getMediumHighSpeedOffset() {
        return mediumHighSpeedPercent * .01f;
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
