package com.sample.food.gaugesample.Indicators;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Path;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class NormalIndicator extends Indicator<NormalIndicator> {

    private Path indicatorPath = new Path();

    public NormalIndicator(Context context) {
        super(context);
        updateIndicator();
    }

    @Override
    protected float getDefaultIndicatorWidth() {
        return dpTOpx(12f);
    }

    @Override
    public void draw(Canvas canvas, float degree) {
        canvas.save();
        canvas.rotate(90f + degree, getCenterX(), getCenterY());
        canvas.drawPath(indicatorPath, indicatorPaint);
        canvas.restore();
    }

    @Override
    protected void updateIndicator() {
        indicatorPath.reset();
        indicatorPath.moveTo(getCenterX(), getViewSize()/14);
        float indicatorBottom = getViewSize()*2f/3f - getViewSize()/16;
        indicatorPath.lineTo(getCenterX() - getIndicatorWidth(), indicatorBottom);
        indicatorPath.lineTo(getCenterX() + getIndicatorWidth(), indicatorBottom);
//        RectF rectF = new RectF(getCenterX() - getIndicatorWidth(), indicatorBottom - getIndicatorWidth()
//                , getCenterX() + getIndicatorWidth(), indicatorBottom + getIndicatorWidth());
//        indicatorPath.addArc(rectF, 0f, 30f);

        indicatorPaint.setColor(getIndicatorColor());
    }

    @Override
    protected void setWithEffects(boolean withEffects) {
        if (withEffects && !isInEditMode()) {
            indicatorPaint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.SOLID));
        }
        else {
            indicatorPaint.setMaskFilter(null);
        }
    }
}
