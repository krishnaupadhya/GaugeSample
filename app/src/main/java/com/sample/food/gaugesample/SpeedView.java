package com.sample.food.gaugesample;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.sample.food.gaugesample.Indicators.NormalIndicator;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class SpeedView extends Speedometer {

    private Paint outerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            innerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            speedometerPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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
    }

    public SpeedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public SpeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);

    }

    @Override
    protected void defaultGaugeValues() {
    }

    @Override
    protected void defaultSpeedometerValues() {
        super.setIndicator(new NormalIndicator(getContext()));
        super.setBackgroundCircleColor(Color.TRANSPARENT);
    }

    private void init() {
        speedometerPaint.setStyle(Paint.Style.STROKE);
        markPaint.setStyle(Paint.Style.STROKE);
        outerCirclePaint.setColor(getResources().getColor(R.color.center_outer_circle));
        innerCirclePaint.setColor(Color.WHITE);
        setIndicatorColor(getResources().getColor(R.color.risko_meter_indicator_color));
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SpeedView, 0, 0);

        outerCirclePaint.setColor(a.getColor(R.styleable.SpeedView_sv_centerCircleColor, outerCirclePaint.getColor()));
        innerCirclePaint.setColor(a.getColor(R.styleable.SpeedView_sv_centerInnerCircleColor, innerCirclePaint.getColor()));
        a.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        updateBackgroundBitmap();
    }

    private void initDraw() {
        speedometerPaint.setStrokeWidth(getSpeedometerWidth());
        markPaint.setColor(getMarkColor());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDraw();

        //drawSpeedUnitText(canvas);

        drawIndicator(canvas);


//        canvas.drawBitmap(bitmap, (this.getWidth() / 2) - riskPosition, riskPosition, null);
        canvas.drawCircle(getSize() * .5f, getSize() * .5f, getWidthPa() / 18f, outerCirclePaint);
        canvas.drawCircle(getSize() * .5f, getSize() * .5f, getWidthPa() / 36f, innerCirclePaint);
        // canvas.drawArc(speedometerRect,180,180+360,true,outerCirclePaint);

        //drawNotes(canvas);
    }

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
        //updateIndicator(c);
        c.save();

    }

    private void updateIndicator(Canvas canvas) {
//        if (canvas.getWidth() > 0 && canvas.getHeight() > 0) {
//            ImageIndicator imageIndicator = new ImageIndicator(getContext(), R.drawable.group_2
//                    , (int) dpTOpx(getWidth()), (int) dpTOpx(getHeight()));
//            setIndicator(imageIndicator);
//        }

       /* Bitmap srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.group_2);
        riskPosition = getSpeedometerWidth() * .5f + getWidth() / 12;

        // Initialize a new Matrix instance
        matrix = new Matrix();

        matrix.setRotate(
                -45, // degrees
                srcBitmap.getWidth() / 2, // px
                srcBitmap.getHeight() / 2 // py
        );

        // Draw the bitmap at the center position of the canvas both vertically and horizontally
        matrix.postTranslate(
                canvas.getWidth() / 2 - srcBitmap.getWidth() / 2,
                canvas.getHeight() / 2 - srcBitmap.getHeight() / 2
        );

        canvas.drawBitmap(
                srcBitmap, // Bitmap
                matrix, // Matrix
                outerCirclePaint // Paint
        );*/
    }

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
     * change the color of the center circle (if exist),
     * <b>this option is not available for all Speedometers</b>.
     *
     * @param centerCircleColor new color.
     */
    public void setCenterCircleColor(int centerCircleColor) {
        outerCirclePaint.setColor(centerCircleColor);
        if (!isAttachedToWindow())
            return;
        invalidate();
    }

    @Override
    public void setIndicatorColor(int indicatorColor) {
        super.setIndicatorColor(indicatorColor);

    }
}
