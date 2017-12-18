package com.sample.food.gaugesample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.sample.food.gaugesample.Indicators.ImageIndicator;
import com.sample.food.gaugesample.Indicators.Indicator;
import com.sample.food.gaugesample.Indicators.NormalIndicator;

public class SpeedView extends Gauge {

    private Paint outerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            innerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            speedometerPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Indicator indicator;
    private Paint circleBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float speedometerWidth = dpTOpx(30f);

    private int markColor = Color.WHITE
            , lowSpeedColor = getResources().getColor(R.color.low_color)
            ,lowMidSpeedColor = getResources().getColor(R.color.low_mid_color)
            ,mediumSpeedColor = getResources().getColor(R.color.mid_color)
            ,highMidSpeedColor = getResources().getColor(R.color.mid_high_color)
            ,highSpeedColor = getResources().getColor(R.color.high_color)
            ,backgroundCircleColor = Color.WHITE;

    private int startDegree = 180, endDegree = 180 + 180;
    /**
     * to rotate indicator
     */
    private float degree = startDegree;

    private RectF speedometerRect = new RectF();
    private float ARC_PADDING = 5f;
    private float riskPosition;
    private float riskPercentage = 0f;
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

    private int highSpeedPercent = 100;

    public SpeedView(Context context) {
        this(context, null);
        init();
    }

    public SpeedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public SpeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        indicator = new NormalIndicator(getContext());
        setIndicatorColor(getResources().getColor(R.color.risko_meter_indicator_color));
        degree = startDegree;
        checkStartAndEndDegree();
        setBackgroundCircleColor(Color.TRANSPARENT);
        speedometerPaint.setStyle(Paint.Style.STROKE);
        markPaint.setStyle(Paint.Style.STROKE);
        outerCirclePaint.setColor(getResources().getColor(R.color.center_outer_circle));
        innerCirclePaint.setColor(Color.WHITE);

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

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        updateBackgroundBitmap();
        indicator.onSizeChange(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        int newW = size;
        int newH = size / 2 + (int) (size * 0.2);
        setMeasuredDimension(newW, newH);
    }


    private void initDraw() {
        speedometerPaint.setStrokeWidth(getSpeedometerWidth());
        markPaint.setColor(getMarkColor());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        degree = getDegreeAtSpeed(getCurrentSpeed());
        initDraw();

        //draw indicator line
        drawIndicator(canvas);

        // draw indicator circles at center
        canvas.drawCircle(getSize() * .5f, getSize() * .5f, getWidthPa() / 16f, outerCirclePaint);
        canvas.drawCircle(getSize() * .5f, getSize() * .5f, getWidthPa() / 40f, innerCirclePaint);

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
     * draw arcs
     */
    @Override
    protected void updateBackgroundBitmap() {
        Canvas c = createBackgroundBitmapCanvas();
        initDraw();

        riskPosition = getSpeedometerWidth() * .5f + getWidth() / 8;
        speedometerRect.set(riskPosition, riskPosition, getSize() - riskPosition, getSize() - riskPosition);

        speedometerPaint.setColor(getHighSpeedColor());
        c.drawArc(speedometerRect, getStartDegree(), getEndDegree() - getStartDegree(), false, speedometerPaint);

        speedometerPaint.setColor(Color.WHITE);
        c.drawArc(speedometerRect, getStartDegree()
                , (getEndDegree() - getStartDegree()) * getMediumHighSpeedOffset() + ARC_PADDING, false, speedometerPaint);

        speedometerPaint.setColor(getMediumHighSpeedColor());
        c.drawArc(speedometerRect, getStartDegree()
                , (getEndDegree() - getStartDegree()) * getMediumHighSpeedOffset(), false, speedometerPaint);

        speedometerPaint.setColor(Color.WHITE);
        c.drawArc(speedometerRect, getStartDegree()
                , (getEndDegree() - getStartDegree()) * getMediumSpeedOffset() + ARC_PADDING, false, speedometerPaint);

        speedometerPaint.setColor(getMediumSpeedColor());
        c.drawArc(speedometerRect, getStartDegree()
                , (getEndDegree() - getStartDegree()) * getMediumSpeedOffset(), false, speedometerPaint);

        speedometerPaint.setColor(Color.WHITE);
        c.drawArc(speedometerRect, getStartDegree()
                , (getEndDegree() - getStartDegree()) * getLowMidSpeedOffset() + ARC_PADDING, false, speedometerPaint);

        speedometerPaint.setColor(getLowMidSpeedColor());
        c.drawArc(speedometerRect, getStartDegree()
                , (getEndDegree() - getStartDegree()) * getLowMidSpeedOffset(), false, speedometerPaint);

        speedometerPaint.setColor(Color.WHITE);
        c.drawArc(speedometerRect, getStartDegree()
                , (getEndDegree() - getStartDegree()) * getLowSpeedOffset() + ARC_PADDING, false, speedometerPaint);


        speedometerPaint.setColor(getLowSpeedColor());
        c.drawArc(speedometerRect, getStartDegree()
                , (getEndDegree() - getStartDegree()) * getLowSpeedOffset(), false, speedometerPaint);

        updateTextView(c, riskPosition);
        c.save();

    }

    /**
     * draw risk type text views at respective positions
     * @param c
     * @param risk
     */
    private void updateTextView(Canvas c, float risk) {
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        c.drawPaint(paint);

        paint.setColor(getResources().getColor(R.color.risko_meter_text_color));
        paint.setTextSize(getContext().getResources().getDimensionPixelSize(R.dimen.custom_text_size));
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        c.drawText(getContext().getString(R.string.low_risk), risk - (0.7f * risk), risk * 2, paint);
        c.drawText(getContext().getString(R.string.low_mid_risk), risk - (0.7f * risk), risk - (0.1f * risk), paint);
        c.drawText(getContext().getString(R.string.mid_risk), risk * 2.3f, risk - (0.5f * risk), paint);
        c.drawText(getContext().getString(R.string.mid_high_risk), risk * 4f, risk, paint);
        c.drawText(getContext().getString(R.string.high_risk), risk * 4.9f, risk * 2, paint);
        if (riskPercentage < lowSpeedPercent) {
            paint.setColor(getResources().getColor(R.color.low_color));
            c.drawText(getContext().getString(R.string.low_risk), risk - (0.7f * risk), risk * 2, paint);
        } else if (riskPercentage < lowMidSpeedPercent) {
            paint.setColor(getResources().getColor(R.color.low_mid_color));
            c.drawText(getContext().getString(R.string.low_mid_risk), risk - (0.7f * risk), risk - (0.1f * risk), paint);
        } else if (riskPercentage < mediumSpeedPercent) {
            paint.setColor(getResources().getColor(R.color.mid_color));
            c.drawText(getContext().getString(R.string.mid_risk), risk * 2.3f, risk - (0.5f * risk), paint);
        } else if (riskPercentage < mediumHighSpeedPercent) {
            paint.setColor(getResources().getColor(R.color.mid_high_color));
            c.drawText(getContext().getString(R.string.mid_high_risk), risk * 4f, risk, paint);
        } else {
            paint.setColor(getResources().getColor(R.color.high_color));
            c.drawText(getContext().getString(R.string.high_risk), risk * 4.9f, risk * 2, paint);
        }
    }

    @Override
    public void speedTo(float speed) {
        setRiskPercentage(speed);
        updateBackgroundBitmap();
        super.speedTo(riskPercentage);


    }

    private void setRiskPercentage(float speed) {
        if (speed < lowSpeedPercent)
            this.riskPercentage = 10f;
        else if (speed < lowMidSpeedPercent)
            this.riskPercentage = 30f;
        else if (speed < mediumSpeedPercent)
            this.riskPercentage = 50f;
        else if (speed < mediumHighSpeedPercent)
            this.riskPercentage = 70f;
        else if (speed < highSpeedPercent)
            this.riskPercentage = 90f;
        else
            this.riskPercentage = 100f;
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
     * @param speed to know the degree at it.
     * @return correct Degree at that speed.
     */
    protected float getDegreeAtSpeed(float speed) {
        return (speed - getMinRiskPercentage()) * (endDegree - startDegree) / (getMaxRiskPercentage() - getMinRiskPercentage()) + startDegree;
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
