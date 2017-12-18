package com.sample.food.gaugesample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.sample.food.gaugesample.Indicators.ImageIndicator;
import com.sample.food.gaugesample.Indicators.Indicator;
import com.sample.food.gaugesample.Indicators.NormalIndicator;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
@SuppressWarnings("unused")
public abstract class Speedometer extends Gauge {

    private Indicator indicator;
    private Paint circleBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float speedometerWidth = dpTOpx(30f);

    private int markColor = Color.WHITE, lowSpeedColor = getResources().getColor(R.color.low_color), lowMidSpeedColor = getResources().getColor(R.color.low_mid_color), mediumSpeedColor = getResources().getColor(R.color.mid_color), highMidSpeedColor = getResources().getColor(R.color.mid_high_color), highSpeedColor = getResources().getColor(R.color.high_color), backgroundCircleColor = Color.WHITE;

    private int startDegree = 180, endDegree = 180 + 180;
    /**
     * to rotate indicator
     */
    private float degree = startDegree;


    public Speedometer(Context context) {
        this(context, null);
    }

    public Speedometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Speedometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
        initAttributeValue();
    }

    private void init() {
        indicator = new NormalIndicator(getContext());
        defaultSpeedometerValues();
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        degree = startDegree;
        checkStartAndEndDegree();
    }

    private void initAttributeValue() {
        circleBackPaint.setColor(backgroundCircleColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        int newW = size;
        int newH = size / 2 + (int) (size * 0.2);
        setMeasuredDimension(newW, newH);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        indicator.onSizeChange(this);
    }

    private void checkStartAndEndDegree() {
        if (startDegree < 0)
            throw new IllegalArgumentException("StartDegree can\'t be Negative");
        if (endDegree < 0)
            throw new IllegalArgumentException("EndDegree can\'t be Negative");
        if (startDegree >= endDegree)
            throw new IllegalArgumentException("EndDegree must be bigger than StartDegree !");
        if (endDegree - startDegree > 360)
            throw new IllegalArgumentException("(EndDegree - StartDegree) must be smaller than 360 !");
    }

    /**
     * add default values for Speedometer inside this method,
     * call super setting method to set default value,
     * Ex :
     * <pre>
     *     super.setBackgroundCircleColor(Color.TRANSPARENT);
     * </pre>
     */
    abstract protected void defaultSpeedometerValues();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        degree = getDegreeAtSpeed(getCurrentSpeed());
    }

    /**
     * draw indicator at correct {@link #degree},
     * this method must call in subSpeedometer's {@code onDraw} method.
     *
     * @param canvas view canvas to draw.
     */
    protected void drawIndicator(Canvas canvas) {
        indicator.draw(canvas, degree);
    }

    /**
     * create canvas to draw {@link #backgroundBitmap}.
     *
     * @return {@link #backgroundBitmap}'s canvas.
     */
    @Override
    protected final Canvas createBackgroundBitmapCanvas() {
        if (getWidth() == 0 || getHeight() == 0)
            return new Canvas();
        backgroundBitmap = Bitmap.createBitmap(getSize(), getSize(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(backgroundBitmap);
        canvas.drawCircle(getSize() * .5f, getSize() * .5f, getSize() * .5f - getPadding(), circleBackPaint);
        return canvas;
    }

    /**
     * @return correct degree where indicator must be.
     */
    protected float getDegree() {
        return degree;
    }

    /**
     * @param speed to know the degree at it.
     * @return correct Degree at that speed.
     */
    protected float getDegreeAtSpeed(float speed) {
        return (speed - getMinSpeed()) * (endDegree - startDegree) / (getMaxSpeed() - getMinSpeed()) + startDegree;
    }

    public int getIndicatorColor() {
        return indicator.getIndicatorColor();
    }

    /**
     * change indicator's color,
     * this option will ignore when using {@link ImageIndicator}.
     *
     * @param indicatorColor new color.
     */
    public void setIndicatorColor(int indicatorColor) {
        indicator.noticeIndicatorColorChange(indicatorColor);
        if (!isAttachedToWindow())
            return;
        invalidate();
    }

    public int getMarkColor() {
        return markColor;
    }

    /**
     * change the color of all marks (if exist),
     * <b>this option is not available for all Speedometers</b>.
     *
     * @param markColor new color.
     */
    public void setMarkColor(int markColor) {
        this.markColor = markColor;
        if (!isAttachedToWindow())
            return;
        invalidate();
    }

    public int getLowSpeedColor() {
        return lowSpeedColor;
    }

    /**
     * change the color of Low Section.
     *
     * @param lowSpeedColor new color.
     */
    public void setLowSpeedColor(int lowSpeedColor) {
        this.lowSpeedColor = lowSpeedColor;
        if (!isAttachedToWindow())
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public int getLowMidSpeedColor() {
        return lowMidSpeedColor;
    }

    /**
     * change the color of Low Section.
     *
     * @param lowMidSpeedColor new color.
     */
    public void setLowMidSpeedColor(int lowMidSpeedColor) {
        this.lowMidSpeedColor = lowMidSpeedColor;
        if (!isAttachedToWindow())
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public int getMediumSpeedColor() {
        return mediumSpeedColor;
    }

    /**
     * change the color of Medium Section.
     *
     * @param mediumSpeedColor new color.
     */
    public void setMediumSpeedColor(int mediumSpeedColor) {
        this.mediumSpeedColor = mediumSpeedColor;
        if (!isAttachedToWindow())
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public int getMediumHighSpeedColor() {
        return highMidSpeedColor;
    }

    /**
     * change the color of Medium Section.
     *
     * @param highMidSpeedColor new color.
     */
    public void setMediumHighSpeedColor(int highMidSpeedColor) {
        this.highMidSpeedColor = highMidSpeedColor;
        if (!isAttachedToWindow())
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public int getHighSpeedColor() {
        return highSpeedColor;
    }

    /**
     * change the color of High Section.
     *
     * @param highSpeedColor new color.
     */
    public void setHighSpeedColor(int highSpeedColor) {
        this.highSpeedColor = highSpeedColor;
        if (!isAttachedToWindow())
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public int getBackgroundCircleColor() {
        return backgroundCircleColor;
    }

    /**
     * Circle Background Color,
     * you can set it {@code Color.TRANSPARENT}
     * to remove circle background.
     *
     * @param backgroundCircleColor new Circle Background Color.
     */
    public void setBackgroundCircleColor(int backgroundCircleColor) {
        this.backgroundCircleColor = backgroundCircleColor;
        circleBackPaint.setColor(backgroundCircleColor);
        if (!isAttachedToWindow())
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public float getSpeedometerWidth() {
        return speedometerWidth;
    }

    protected int getStartDegree() {
        return startDegree;
    }

    protected int getEndDegree() {
        return endDegree;
    }

    /**
     * @return size of speedometer.
     */
    public int getSize() {
            return Math.max(getWidth(), getHeight());
    }

    public float getIndicatorWidth() {
        return indicator.getIndicatorWidth();
    }

    /**
     * change indicator width, this value have several meaning
     * between {@link Indicator.Indicators}, it will be ignore
     * if using {@link ImageIndicator}.
     *
     * @param indicatorWidth new width in pixel.
     */
    public void setIndicatorWidth(float indicatorWidth) {
        indicator.noticeIndicatorWidthChange(indicatorWidth);
        if (!isAttachedToWindow())
            return;
        invalidate();
    }

    /**
     * call this method to apply/remove blur effect for indicator.
     *
     * @param withEffects effect.
     */
    protected void indicatorEffects(boolean withEffects) {
        indicator.withEffects(withEffects);
    }

    /**
     * change <a href="https://github.com/anastr/SpeedView/wiki/Indicators">indicator shape</a>.<br>
     * this method will get bach indicatorColor and indicatorWidth to default.
     *
     * @param indicator new indicator (Enum value).
     */
    public void setIndicator(Indicator.Indicators indicator) {
        this.indicator = Indicator.createIndicator(getContext(), indicator);
        if (!isAttachedToWindow())
            return;
        this.indicator.setTargetSpeedometer(this);
        invalidate();
    }

    /**
     * add custom <a href="https://github.com/anastr/SpeedView/wiki/Indicators">indicator</a>.
     *
     * @param indicator new indicator.
     */
    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
        if (!isAttachedToWindow())
            return;
        this.indicator.setTargetSpeedometer(this);
        invalidate();
    }

}

