package com.sample.food.gaugesample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.sample.food.gaugesample.Indicators.Indicator;
import com.sample.food.gaugesample.Indicators.NormalIndicator;

public class RiskView extends RiskoMeterView {

    private Paint outerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            innerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            riskometerPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Indicator indicator;
    private Paint circleBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float rsikometerWidth = dpTOpx(30f);

    private int markColor = Color.WHITE, lowRiskColor = getResources().getColor(R.color.low_color), ModerateLowRiskColor = getResources().getColor(R.color.low_mid_color), mediumRiskColor = getResources().getColor(R.color.mid_color), ModeratelyHighRiskColor = getResources().getColor(R.color.mid_high_color), highRiskColor = getResources().getColor(R.color.high_color), backgroundCircleColor = Color.WHITE;

    private int startDegree = 180, endDegree = 180 + 180;
    /**
     * to rotate indicator
     */
    private float degree = startDegree;

    private RectF riskometerRect = new RectF();
    private float ARC_PADDING = 5f;
    private float riskPosition;
    private float riskPercentage = 0f;
    /**
     * low risk area
     */
    private int lowRiskPercent = 20;
    /**
     * low risk area
     */
    private int moderateLowRiskPercent = 40;
    /**
     * medium risk area
     */
    private int mediumRiskPercent = 60;
    /**
     * medium risk area
     */
    private int moderatelyHighRiskPercent = 80;

    private int highRiskPercent = 100;

    public RiskView(Context context) {
        this(context, null);
        init();
    }

    public RiskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public RiskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        indicator = new NormalIndicator(getContext());
        setIndicatorColor(getResources().getColor(R.color.risko_meter_indicator_color));
        degree = startDegree;
        checkStartAndEndDegree();
        setBackgroundCircleColor(Color.TRANSPARENT);
        riskometerPaint.setStyle(Paint.Style.STROKE);
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
        riskometerPaint.setStrokeWidth(getRsikometerWidth());
        markPaint.setColor(getMarkColor());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        degree = getDegreeAtRisk(getCurrentRiskPercentage());
        initDraw();

        //draw indicator line
        drawIndicator(canvas);

        // draw indicator circles at center
        canvas.drawCircle(getSize() * .5f, getSize() * .5f, getWidthPa() / 16f, outerCirclePaint);
        canvas.drawCircle(getSize() * .5f, getSize() * .5f, getWidthPa() / 40f, innerCirclePaint);

    }

    /**
     * draw indicator at correct {@link #degree},
     * this method must call in subRiskometer's {@code onDraw} method.
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

        riskPosition = getRsikometerWidth() * .5f + getWidth() / 8;
        riskometerRect.set(riskPosition, riskPosition, getSize() - riskPosition, getSize() - riskPosition);

        riskometerPaint.setColor(getHighRiskColor());
        c.drawArc(riskometerRect, getStartDegree(), getEndDegree() - getStartDegree(), false, riskometerPaint);

        riskometerPaint.setColor(Color.WHITE);
        c.drawArc(riskometerRect, getStartDegree()
                , (getEndDegree() - getStartDegree()) * getModeratelyOffset() + ARC_PADDING, false, riskometerPaint);

        riskometerPaint.setColor(getModeratelyHighRiskColor());
        c.drawArc(riskometerRect, getStartDegree()
                , (getEndDegree() - getStartDegree()) * getModeratelyOffset(), false, riskometerPaint);

        riskometerPaint.setColor(Color.WHITE);
        c.drawArc(riskometerRect, getStartDegree()
                , (getEndDegree() - getStartDegree()) * getMediumRiskOffset() + ARC_PADDING, false, riskometerPaint);

        riskometerPaint.setColor(getMediumRiskColor());
        c.drawArc(riskometerRect, getStartDegree()
                , (getEndDegree() - getStartDegree()) * getMediumRiskOffset(), false, riskometerPaint);

        riskometerPaint.setColor(Color.WHITE);
        c.drawArc(riskometerRect, getStartDegree()
                , (getEndDegree() - getStartDegree()) * getModerateLowRiskOffset() + ARC_PADDING, false, riskometerPaint);

        riskometerPaint.setColor(getModerateLowRiskColor());
        c.drawArc(riskometerRect, getStartDegree()
                , (getEndDegree() - getStartDegree()) * getModerateLowRiskOffset(), false, riskometerPaint);

        riskometerPaint.setColor(Color.WHITE);
        c.drawArc(riskometerRect, getStartDegree()
                , (getEndDegree() - getStartDegree()) * getLowRiskOffset() + ARC_PADDING, false, riskometerPaint);


        riskometerPaint.setColor(getLowRiskColor());
        c.drawArc(riskometerRect, getStartDegree()
                , (getEndDegree() - getStartDegree()) * getLowRiskOffset(), false, riskometerPaint);

        updateTextView(c, riskPosition);
        c.save();

    }

    /**
     * draw risk type text views at respective positions
     *
     * @param c
     * @param risk
     */
    private void updateTextView(Canvas c, float risk) {
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        c.drawPaint(paint);

        paint.setColor(getResources().getColor(R.color.risko_meter_text_color));
        int spSize = 12;
        float scaledSizeInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spSize, getResources().getDisplayMetrics());
        paint.setTextSize(scaledSizeInPixels);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        c.drawText(getContext().getString(R.string.low_risk), risk - (0.7f * risk), risk * 2, paint);
        c.drawText(getContext().getString(R.string.low_mid_risk), risk - (0.7f * risk), risk - (0.1f * risk), paint);
        c.drawText(getContext().getString(R.string.mid_risk), risk * 2.3f, risk - (0.5f * risk), paint);
        c.drawText(getContext().getString(R.string.mid_high_risk), risk * 4f, risk, paint);
        c.drawText(getContext().getString(R.string.high_risk), risk * 4.9f, risk * 2, paint);
        if (riskPercentage < lowRiskPercent) {
            paint.setColor(getResources().getColor(R.color.low_color));
            c.drawText(getContext().getString(R.string.low_risk), risk - (0.7f * risk), risk * 2, paint);
        } else if (riskPercentage < moderateLowRiskPercent) {
            paint.setColor(getResources().getColor(R.color.low_mid_color));
            c.drawText(getContext().getString(R.string.low_mid_risk), risk - (0.7f * risk), risk - (0.1f * risk), paint);
        } else if (riskPercentage < mediumRiskPercent) {
            paint.setColor(getResources().getColor(R.color.mid_color));
            c.drawText(getContext().getString(R.string.mid_risk), risk * 2.3f, risk - (0.5f * risk), paint);
        } else if (riskPercentage < moderatelyHighRiskPercent) {
            paint.setColor(getResources().getColor(R.color.mid_high_color));
            c.drawText(getContext().getString(R.string.mid_high_risk), risk * 4f, risk, paint);
        } else {
            paint.setColor(getResources().getColor(R.color.high_color));
            c.drawText(getContext().getString(R.string.high_risk), risk * 4.9f, risk * 2, paint);
        }
    }

    public void RiskTo(RISKMODE riskMode) {
        float riskPercentage = getRiskPercentage(riskMode);
        setRiskPercentage(riskPercentage);
        updateBackgroundBitmap();
        super.RiskTo(riskPercentage);
    }

    private float getRiskPercentage(RISKMODE riskMode) {
        if (riskMode == RISKMODE.LOW_RISK_MODE)
            return 10f;
        else if (riskMode == RISKMODE.LOW_MODERATE_RISK_MODE)
            return 30f;
        else if (riskMode == RISKMODE.MEDIUM_RISK_MODE)
            return 50f;
        else if (riskMode == RISKMODE.MODERATELY_HIGH_RISK_MODE)
            return 70f;
        else if (riskMode == RISKMODE.HIGH_RISK_MODE)
            return 90f;
        else
            return 100f;
    }

    private void setRiskPercentage(float risk) {
        if (risk < lowRiskPercent)
            this.riskPercentage = 10f;
        else if (risk < moderateLowRiskPercent)
            this.riskPercentage = 30f;
        else if (risk < mediumRiskPercent)
            this.riskPercentage = 50f;
        else if (risk < moderatelyHighRiskPercent)
            this.riskPercentage = 70f;
        else if (risk < highRiskPercent)
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
     * @param risk to know the degree at it.
     * @return correct Degree at that risk.
     */
    protected float getDegreeAtRisk(float risk) {
        return (risk - getMinRiskPercentage()) * (endDegree - startDegree) / (getMaxRiskPercentage() - getMinRiskPercentage()) + startDegree;
    }

    public int getIndicatorColor() {
        return indicator.getIndicatorColor();
    }

    /**
     * change indicator's color,
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
     * <b>this option is not available for all Riskometers</b>.
     *
     * @param markColor new color.
     */
    public void setMarkColor(int markColor) {
        this.markColor = markColor;
        if (!isAttachedToWindow())
            return;
        invalidate();
    }

    public int getLowRiskColor() {
        return lowRiskColor;
    }

    /**
     * change the color of Low Section.
     *
     * @param lowRiskColor new color.
     */
    public void setLowRiskColor(int lowRiskColor) {
        this.lowRiskColor = lowRiskColor;
        if (!isAttachedToWindow())
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public int getModerateLowRiskColor() {
        return ModerateLowRiskColor;
    }

    /**
     * change the color of Low Section.
     *
     * @param moderateLowRiskColor new color.
     */
    public void setModerateLowRiskColor(int moderateLowRiskColor) {
        this.ModerateLowRiskColor = moderateLowRiskColor;
        if (!isAttachedToWindow())
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public int getMediumRiskColor() {
        return mediumRiskColor;
    }

    /**
     * change the color of Medium Section.
     *
     * @param mediumRiskColor new color.
     */
    public void setMediumRiskColor(int mediumRiskColor) {
        this.mediumRiskColor = mediumRiskColor;
        if (!isAttachedToWindow())
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public int getModeratelyHighRiskColor() {
        return ModeratelyHighRiskColor;
    }

    /**
     * change the color of Medium Section.
     *
     * @param highMidRiskColor new color.
     */
    public void setMediumHighRiskColor(int highMidRiskColor) {
        this.ModeratelyHighRiskColor = highMidRiskColor;
        if (!isAttachedToWindow())
            return;
        updateBackgroundBitmap();
        invalidate();
    }

    public int getHighRiskColor() {
        return highRiskColor;
    }

    /**
     * change the color of High Section.
     *
     * @param highRiskColor new color.
     */
    public void setHighRiskColor(int highRiskColor) {
        this.highRiskColor = highRiskColor;
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

    public float getRsikometerWidth() {
        return rsikometerWidth;
    }

    protected int getStartDegree() {
        return startDegree;
    }

    protected int getEndDegree() {
        return endDegree;
    }

    /**
     * @return size of Riskometer.
     */
    public int getSize() {
        return Math.max(getWidth(), getHeight());
    }

    public enum RISKMODE {
        LOW_RISK_MODE,
        LOW_MODERATE_RISK_MODE,
        MEDIUM_RISK_MODE,
        MODERATELY_HIGH_RISK_MODE,
        HIGH_RISK_MODE

    }

}
