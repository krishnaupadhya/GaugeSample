package com.sample.food.gaugesample;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.Locale;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
@SuppressWarnings("unused")
public abstract class Gauge extends View {

    private TextPaint speedTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG),
            unitTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    /**
     * the text after speedText
     */
    private String unit = "Km/h";

    /**
     * the max range in speedometer, {@code default = 100}
     */
    private int maxSpeed = 100;
    /**
     * the min range in speedometer, {@code default = 0}
     */
    private int minSpeed = 0;
    /**
     * the last speed which you set by {@link #speedTo(float)}
     * or {@link #speedTo(float, long)} or {@link #speedPercentTo(int)},
     * or if you stop speedometer By {@link #stop()} method.
     */
    private float speed = minSpeed;
    /**
     * what is speed now in <b>int</b>
     */
    private int currentIntSpeed = 0;
    /**
     * what is speed now in <b>float</b>
     */
    private float currentSpeed = 0f;

    private ValueAnimator speedAnimator;
    private boolean canceled = false;
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
     * View height without padding
     */
    private int heightPa = 0;

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


    public static final byte LOW_SECTION = 1;
    public static final byte LOW_MID_SECTION = 5;
    public static final byte MEDIUM_SECTION = 2;
    public static final byte MID_HIGH_SECTION = 4;
    public static final byte HIGH_SECTION = 3;


    private byte section = LOW_SECTION;

    private boolean speedometerTextRightToLeft = false;

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
    }

    public Gauge(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Gauge(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
        initAttributeValue();
    }

    private void init() {

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
        defaultGaugeValues();
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null)
            return;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Gauge, 0, 0);

        maxSpeed = a.getInt(R.styleable.Gauge_sv_maxSpeed, maxSpeed);
        minSpeed = a.getInt(R.styleable.Gauge_sv_minSpeed, minSpeed);
        speedTextPaint.setColor(a.getColor(R.styleable.Gauge_sv_speedTextColor, speedTextPaint.getColor()));
        speedTextPaint.setTextSize(a.getDimension(R.styleable.Gauge_sv_speedTextSize, speedTextPaint.getTextSize()));
        unitTextPaint.setColor(a.getColor(R.styleable.Gauge_sv_unitTextColor, unitTextPaint.getColor()));
        unitTextPaint.setTextSize(a.getDimension(R.styleable.Gauge_sv_unitTextSize, unitTextPaint.getTextSize()));
        String unit = a.getString(R.styleable.Gauge_sv_unit);
        this.unit = (unit != null) ? unit : this.unit;
        lowSpeedPercent = a.getInt(R.styleable.Gauge_sv_lowSpeedPercent, lowSpeedPercent);
        mediumSpeedPercent = a.getInt(R.styleable.Gauge_sv_mediumSpeedPercent, mediumSpeedPercent);
        speedometerTextRightToLeft = a.getBoolean(R.styleable.Gauge_sv_textRightToLeft, speedometerTextRightToLeft);
        accelerate = a.getFloat(R.styleable.Gauge_sv_accelerate, accelerate);
        decelerate = a.getFloat(R.styleable.Gauge_sv_decelerate, decelerate);
        unitUnderSpeedText = a.getBoolean(R.styleable.Gauge_sv_unitUnderSpeedText, unitUnderSpeedText);
        unitSpeedInterval = a.getDimension(R.styleable.Gauge_sv_unitSpeedInterval, unitSpeedInterval);
        speedTextPadding = a.getDimension(R.styleable.Gauge_sv_speedTextPadding, speedTextPadding);
        int position = a.getInt(R.styleable.Gauge_sv_speedTextPosition, -1);
        byte format = (byte) a.getInt(R.styleable.Gauge_sv_speedTextFormat, -1);
        if (format != -1)
            setSpeedTextFormat(format);
        a.recycle();
        checkSpeedometerPercent();
        checkAccelerate();
        checkDecelerate();
    }

    private void initAttributeValue() {
        if (unitUnderSpeedText) {
            speedTextPaint.setTextAlign(Paint.Align.CENTER);
            unitTextPaint.setTextAlign(Paint.Align.CENTER);
        } else {
            speedTextPaint.setTextAlign(Paint.Align.LEFT);
            unitTextPaint.setTextAlign(Paint.Align.LEFT);
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

    private void checkAccelerate() {
        if (accelerate > 1f || accelerate <= 0)
            throw new IllegalArgumentException("accelerate must be between (0, 1]");
    }

    private void checkDecelerate() {
        if (decelerate > 1f || decelerate <= 0)
            throw new IllegalArgumentException("decelerate must be between (0, 1]");
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
     * convert pixel to <b>dp</b>.
     *
     * @param px to convert.
     * @return Dimension in dp.
     */
    public float pxTOdp(float px) {
        return px / getContext().getResources().getDisplayMetrics().density;
    }

    /**
     * add default values for Gauge inside this method,
     * call super setting method to set default value,
     * Ex :
     * <pre>
     *     super.setBackgroundCircleColor(Color.TRANSPARENT);
     * </pre>
     */
    abstract protected void defaultGaugeValues();

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
        heightPa = getHeight() - padding * 2;
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
        canceled = true;
        speedAnimator.cancel();
        canceled = false;
    }

    /**
     * rotate indicator to correct speed without animation.
     *
     * @param speed correct speed to move.
     */
    public void setSpeedAt(float speed) {
        speed = (speed > maxSpeed) ? maxSpeed : (speed < minSpeed) ? minSpeed : speed;
        this.speed = speed;
        this.currentSpeed = speed;
        cancelSpeedAnimator();
        invalidate();
    }

    /**
     * move speed to percent value.
     *
     * @param percent percent value to move, must be between [0,100].
     * @see #speedTo(float)
     * @see #speedTo(float, long)
     * @see #speedPercentTo(int, long)
     * @see #realSpeedTo(float)
     */
    public void speedPercentTo(int percent) {
        speedPercentTo(percent, 2000);
    }

    /**
     * move speed to percent value.
     *
     * @param percent      percent value to move, must be between [0,100].
     * @param moveDuration The length of the animation, in milliseconds.
     *                     This value cannot be negative.
     * @see #speedTo(float)
     * @see #speedTo(float, long)
     * @see #speedPercentTo(int)
     * @see #realSpeedTo(float)
     */
    public void speedPercentTo(int percent, long moveDuration) {
        speedTo(getSpeedValue(percent), moveDuration);
    }

    /**
     * move speed to correct {@code int},
     * it should be between [{@link #minSpeed}, {@link #maxSpeed}].<br>
     * <br>
     * if {@code speed > maxSpeed} speed will change to {@link #maxSpeed},<br>
     * if {@code speed < minSpeed} speed will change to {@link #minSpeed}.<br>
     * <p>
     * it is the same {@link #speedTo(float, long)}
     * with default {@code moveDuration = 2000}.
     *
     * @param speed correct speed to move.
     * @see #speedTo(float, long)
     * @see #speedPercentTo(int)
     * @see #realSpeedTo(float)
     */
    public void speedTo(float speed) {
        speedTo(speed, 2000);
    }

    /**
     * move speed to correct {@code int},
     * it should be between [{@link #minSpeed}, {@link #maxSpeed}].<br>
     * <br>
     * if {@code speed > maxSpeed} speed will change to {@link #maxSpeed},<br>
     * if {@code speed < minSpeed} speed will change to {@link #minSpeed}.
     *
     * @param speed        correct speed to move.
     * @param moveDuration The length of the animation, in milliseconds.
     *                     This value cannot be negative.
     * @see #speedTo(float)
     * @see #speedPercentTo(int)
     * @see #realSpeedTo(float)
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void speedTo(float speed, long moveDuration) {
        speed = (speed > maxSpeed) ? maxSpeed : (speed < minSpeed) ? minSpeed : speed;
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
     * this method use {@code realSpeedTo()} to speed up
     * the speedometer to {@link #maxSpeed}.
     *
     * @see #realSpeedTo(float)
     * @see #slowDown()
     */
    public void speedUp() {
        realSpeedTo(getMaxSpeed());
    }

    /**
     * this method use {@code #realSpeedTo()} to slow down
     * the speedometer to {@link #minSpeed}.
     *
     * @see #realSpeedTo(float)
     * @see #speedUp()
     */
    public void slowDown() {
        realSpeedTo(0);
    }

    /**
     * move speed to percent value by using {@link #realSpeedTo(float)} method.
     *
     * @param percent percent value to move, must be between [0,100].
     */
    public void realSpeedPercentTo(float percent) {
        realSpeedTo(getSpeedValue(percent));
    }

    /**
     * to make speedometer some real.
     * <br>
     * when <b>speed up</b> : speed value well increase <i>slowly</i> by {@link #accelerate}.
     * <br>
     * when <b>slow down</b> : speed value will decrease <i>rapidly</i> by {@link #decelerate}.
     *
     * @param speed correct speed to move.
     * @see #speedTo(float)
     * @see #speedTo(float, long)
     * @see #speedPercentTo(int)
     * @see #speedUp()
     * @see #slowDown()
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void realSpeedTo(float speed) {
        boolean oldIsSpeedUp = this.speed > currentSpeed;
        speed = (speed > maxSpeed) ? maxSpeed : (speed < minSpeed) ? minSpeed : speed;
        if (speed == this.speed)
            return;
        this.speed = speed;

        if (Build.VERSION.SDK_INT < 11) {
            setSpeedAt(speed);
            return;
        }

        cancelSpeedAnimator();
    }

    /**
     * @param percentSpeed between [0, 100].
     * @return speed value at correct percentSpeed.
     */
    private float getSpeedValue(float percentSpeed) {
        percentSpeed = (percentSpeed > 100) ? 100 : (percentSpeed < 0) ? 0 : percentSpeed;
        return percentSpeed * (maxSpeed - minSpeed) * .01f + minSpeed;
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
     * @return speed text's format, [{@link #INTEGER_FORMAT} or {@link #FLOAT_FORMAT}].
     */
    public byte getSpeedTextFormat() {
        return speedTextFormat;
    }

    /**
     * change speed text's format [{@link #INTEGER_FORMAT} or {@link #FLOAT_FORMAT}].
     *
     * @param speedTextFormat new format.
     */
    public void setSpeedTextFormat(byte speedTextFormat) {
        this.speedTextFormat = speedTextFormat;
        //recreateSpeedUnitTextBitmap();
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * get correct speed as string to <b>Draw</b>.
     *
     * @return correct speed to draw.
     */
    protected String getSpeedText() {
        return speedTextFormat == FLOAT_FORMAT ? String.format(locale, "%.1f", currentSpeed)
                : String.format(locale, "%d", currentIntSpeed);
    }

    /**
     * get Max speed as string to <b>Draw</b>.
     *
     * @return Max speed to draw.
     */
    protected String getMaxSpeedText() {
        return String.format(locale, "%d", maxSpeed);
    }

    /**
     * get Min speed as string to <b>Draw</b>.
     *
     * @return Min speed to draw.
     */
    protected String getMinSpeedText() {
        return String.format(locale, "%d", minSpeed);
    }

    /**
     * @return the last speed which you set by {@link #speedTo(float)}
     * or {@link #speedTo(float, long)} or {@link #speedPercentTo(int)},
     * or if you stop speedometer By {@link #stop()} method.
     * @see #getCurrentSpeed()
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * what is correct speed now.
     *
     * @return correct speed now.
     * @see #getSpeed()
     */
    public float getCurrentSpeed() {
        return currentSpeed;
    }

    /**
     * get max speed in speedometer, default max speed is 100.
     *
     * @return max speed.
     * @see #getMinSpeed()
     */
    public int getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * get min speed in speedometer, default min speed is 0.
     *
     * @return min speed.
     * @see #getMaxSpeed()
     */
    public int getMinSpeed() {
        return minSpeed;
    }

  /**
     * @return unit text.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * unit text, the text after speed text.
     *
     * @param unit unit text.
     */
    public void setUnit(String unit) {
        this.unit = unit;
        if (!attachedToWindow)
            return;
        invalidate();
    }



    /**
     * @return the long of low speed area (low section) as percent.
     */
    public int getLowSpeedPercent() {
        return lowSpeedPercent;
    }

    /**
     * @return the long of low speed area (low section) as Offset [0, 1].
     */
    public float getLowSpeedOffset() {
        return lowSpeedPercent * .01f;
    }

    /**
     * to change low speed area (low section).
     *
     * @param lowSpeedPercent the long of low speed area as percent,
     *                        must be between {@code [0,100]}.
     * @throws IllegalArgumentException if {@code lowSpeedPercent} out of range.
     * @throws IllegalArgumentException if {@code lowSpeedPercent > mediumSpeedPercent}.
     */
    public void setLowSpeedPercent(int lowSpeedPercent) {
        this.lowSpeedPercent = lowSpeedPercent;
        checkSpeedometerPercent();
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * @return the long of low speed area (low section) as percent.
     */
    public int getLowMidSpeedPercent() {
        return lowMidSpeedPercent;
    }

    /**
     * @return the long of low speed area (low section) as Offset [0, 1].
     */
    public float getLowMidSpeedOffset() {
        return lowMidSpeedPercent * .01f;
    }

    /**
     * to change low speed area (low section).
     *
     * @param lowMidSpeedPercent the long of low speed area as percent,
     *                           must be between {@code [0,100]}.
     * @throws IllegalArgumentException if {@code lowSpeedPercent} out of range.
     * @throws IllegalArgumentException if {@code lowSpeedPercent > mediumSpeedPercent}.
     */
    public void setLowMidSpeedPercent(int lowMidSpeedPercent) {
        this.lowMidSpeedPercent = lowMidSpeedPercent;
        checkSpeedometerPercent();
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
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
     * to change medium speed area (medium section).
     *
     * @param mediumSpeedPercent the long of medium speed area as percent,
     *                           must be between {@code [0,100]}.
     * @throws IllegalArgumentException if {@code mediumSpeedPercent} out of range.
     * @throws IllegalArgumentException if {@code mediumSpeedPercent < lowSpeedPercent}.
     */
    public void setMediumSpeedPercent(int mediumSpeedPercent) {
        this.mediumSpeedPercent = mediumSpeedPercent;
        checkSpeedometerPercent();
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * @return the long of Medium speed area (Medium section) as percent.
     */
    public int getMediumHighSpeedPercent() {
        return mediumHighSpeedPercent;
    }

    /**
     * @return the long of Medium speed area (Medium section) as Offset [0, 1].
     */
    public float getMediumHighSpeedOffset() {
        return mediumHighSpeedPercent * .01f;
    }

    /**
     * to change medium speed area (medium section).
     *
     * @param mediumHighSpeedPercent the long of medium speed area as percent,
     *                               must be between {@code [0,100]}.
     * @throws IllegalArgumentException if {@code mediumSpeedPercent} out of range.
     * @throws IllegalArgumentException if {@code mediumSpeedPercent < lowSpeedPercent}.
     */
    public void setMediumHighSpeedPercent(int mediumHighSpeedPercent) {
        this.mediumHighSpeedPercent = mediumHighSpeedPercent;
        checkSpeedometerPercent();
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * @return whether drawing unit text to left of speed text.
     */
    public boolean isSpeedometerTextRightToLeft() {
        return speedometerTextRightToLeft;
    }

    /**
     * to support Right To Left Text.
     *
     * @param speedometerTextRightToLeft true to flip text right to left.
     */
    public void setSpeedometerTextRightToLeft(boolean speedometerTextRightToLeft) {
        this.speedometerTextRightToLeft = speedometerTextRightToLeft;
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * @return View width without padding.
     */
    public int getWidthPa() {
        return widthPa;
    }

    /**
     * @return View height without padding.
     */
    public int getHeightPa() {
        return heightPa;
    }

    public int getViewSize() {
        return Math.max(getWidth(), getHeight());
    }

    public int getViewSizePa() {
        return Math.max(widthPa, heightPa);
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

    /**
     * @return digit's Locale.
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * set Locale to localizing digits to the given locale,
     * for speed Text and speedometer Text.
     *
     * @param locale the locale to apply, {@code null} value means no localization.
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
        if (!attachedToWindow)
            return;
        invalidate();
    }

    /**
     * check if correct speed in <b>Low Speed Section</b>.
     *
     * @return true if correct speed in Low Speed Section.
     * @see #setLowSpeedPercent(int)
     */
    public boolean isInLowSection() {
        return (maxSpeed - minSpeed) * getLowSpeedOffset() + minSpeed >= currentSpeed;
    }

    /**
     * check if correct speed in <b>Low Speed Section</b>.
     *
     * @return true if correct speed in Low Speed Section.
     * @see #setLowSpeedPercent(int)
     */
    public boolean isInLowMidSection() {
        return (maxSpeed - minSpeed) * getLowMidSpeedOffset() + minSpeed >= currentSpeed && !isInLowSection();
    }

    /**
     * check if correct speed in <b>Medium Speed Section</b>.
     *
     * @return true if correct speed in Medium Speed Section
     * , and it is not in Low Speed Section.
     * @see #setMediumSpeedPercent(int)
     */
    public boolean isInMediumSection() {
        return (maxSpeed - minSpeed) * getMediumSpeedOffset() + minSpeed >= currentSpeed && !isInLowMidSection();
    }


    /**
     * check if correct speed in <b>Medium Speed Section</b>.
     *
     * @return true if correct speed in Medium Speed Section
     * , and it is not in Low Speed Section.
     * @see #setMediumSpeedPercent(int)
     */
    public boolean isInMediumHighSection() {
        return (maxSpeed - minSpeed) * getMediumHighSpeedOffset() + minSpeed >= currentSpeed && !isInMediumSection();
    }

    /**
     * check if correct speed in <b>High Speed Section</b>.
     *
     * @return true if correct speed in High Speed Section
     * , and it is not in Low Speed Section or Medium Speed Section.
     */
    public boolean isInHighSection() {
        return currentSpeed > (maxSpeed - minSpeed) * getMediumHighSpeedOffset() + minSpeed;
    }

    /**
     * @return correct section,
     * used in condition : {@code if (speedometer.getSection() == speedometer.LOW_SECTION)}.
     */
    public byte getSection() {
        if (isInLowSection())
            return LOW_SECTION;
        else if (isInLowMidSection())
            return LOW_MID_SECTION;
        else if (isInMediumSection())
            return MEDIUM_SECTION;
        else if (isInMediumHighSection())
            return MID_HIGH_SECTION;
        else
            return HIGH_SECTION;
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

    /**
     * @return typeface for <b>speed and unit</b> text.
     */
    public Typeface getSpeedTextTypeface() {
        return speedTextPaint.getTypeface();
    }

    /**
     * change typeface for <b>speed and unit</b> text.
     *
     * @param typeface Maybe null. The typeface to be installed.
     */
    public void setSpeedTextTypeface(Typeface typeface) {
        speedTextPaint.setTypeface(typeface);
        unitTextPaint.setTypeface(typeface);
        if (!attachedToWindow)
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    /**
     * @return {@link #accelerate} used in {@link #realSpeedTo(float)}.
     * @see #setAccelerate(float)
     */
    public float getAccelerate() {
        return accelerate;
    }

    /**
     * change accelerate, used by {@link #realSpeedTo(float)} {@link #speedUp()}
     * and {@link #slowDown()} methods.<br>
     * must be between {@code (0, 1]}, default value 0.1f.
     *
     * @param accelerate new accelerate.
     * @throws IllegalArgumentException if {@code accelerate} out of range.
     */
    public void setAccelerate(float accelerate) {
        this.accelerate = accelerate;
        checkAccelerate();
    }

    /**
     * @return {@link #decelerate} used in {@link #realSpeedTo(float)}.
     * @see #setDecelerate(float)
     */
    public float getDecelerate() {
        return decelerate;
    }

    /**
     * change decelerate, used by {@link #realSpeedTo(float)} {@link #speedUp()}
     * and {@link #slowDown()} methods.<br>
     * must be between {@code (0, 1]}, default value 0.1f.
     *
     * @param decelerate new decelerate.
     * @throws IllegalArgumentException if {@code decelerate} out of range.
     */
    public void setDecelerate(float decelerate) {
        this.decelerate = decelerate;
    }
}
