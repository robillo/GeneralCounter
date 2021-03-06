package com.robillo.generalcounterlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Locale;

/**
 * Created by robinkamboj on 26/08/17.
 */

public class GeneralCounter extends View implements CounterInterface{

    private static final int MAX_COUNT = 9999;
    private static final String MAX_COUNT_STRING = String.valueOf(MAX_COUNT);

    // State variables
    private int count;
    private String displayedCount;

    //Drawing variables
    Paint mBackgroundPaint, mLinePaint;
    TextPaint mNumberPaint;
    RectF mBackgroundRect;
    float mCornerRadius;
    float mTextSize = 40f;

    public GeneralCounter(Context context) {
        super(context);
    }

    public GeneralCounter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // Set up paints for canvas drawing.
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNumberPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        if(attrs == null){
            mBackgroundPaint.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
            mLinePaint.setColor(ContextCompat.getColor(context, R.color.colorAccent));
            mNumberPaint.setColor(ContextCompat.getColor(context, android.R.color.white));
        }
        else {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.GeneralCounter);
            mTextSize = array.getFloat(R.styleable.GeneralCounter_text_size, 40f);
            mBackgroundPaint.setColor(array.getColor(R.styleable.GeneralCounter_background_color, ContextCompat.getColor(context, R.color.colorPrimary)));
            mLinePaint.setColor(array.getColor(R.styleable.GeneralCounter_highlight_color, ContextCompat.getColor(context, R.color.colorAccent)));
            mNumberPaint.setColor(array.getColor(R.styleable.GeneralCounter_text_color, ContextCompat.getColor(context, android.R.color.white)));
            array.recycle();
        }
        mLinePaint.setStrokeWidth(5f);
        // Set the number text size to be 64sp.
        // Translate 64sp
        mNumberPaint.setTextSize(Math.round(mTextSize * getResources().getDisplayMetrics().scaledDensity));

        // Allocate objects needed for canvas drawing here.
        mBackgroundRect = new RectF();

        // Initialize drawing measurements.
        mCornerRadius = Math.round(1f * getResources().getDisplayMetrics().density);

        // Do initial count setup.
        setCount(0);
    }

    //UNUSED FOR THE PROJECT
    public GeneralCounter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //UNUSED FOR THE PROJECT
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GeneralCounter(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final Paint.FontMetrics fontMetrics = mNumberPaint.getFontMetrics();

        // Measure maximum possible width of text.
        final float maxTextWidth = mNumberPaint.measureText(MAX_COUNT_STRING);
        // Estimate maximum possible height of text.
        final float maxTextHeight = -fontMetrics.top + fontMetrics.bottom;

        // Add padding to maximum width calculation.
        final int desiredWidth = Math.round(maxTextWidth + getPaddingLeft() + getPaddingRight());

        // Add padding to maximum height calculation.
        final int desiredHeight = Math.round(maxTextHeight * 2f + getPaddingTop() +
                getPaddingBottom());

        // Reconcile size that this view wants to be with the size the parent will let it be.
        final int measuredWidth = reconcileSize(desiredWidth, widthMeasureSpec);
        final int measuredHeight = reconcileSize(desiredHeight, heightMeasureSpec);

        // Store the final measured dimensions.
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //GRAB CANVAS DIMENSIONS
        final int canvasWidth = canvas.getWidth();
        final int canvasHeight = canvas.getHeight();

        //CALCULATE HORIZONTAL CENTER
        final float centerX = canvasWidth * 0.5f; //HALF OF WIDTH OF canvasWidth

        //DRAW THE BACKGROUND
        mBackgroundRect.set(0f, 0f, canvasWidth, canvasHeight);
        canvas.drawRoundRect(mBackgroundRect, mCornerRadius, mCornerRadius, mBackgroundPaint);

        //DRAW THE BASELINE
        final float baselineY = canvasHeight * 0.65f;
        final Paint.FontMetrics fontMetrics = mNumberPaint.getFontMetrics();
        final int topY = Math.round(baselineY + fontMetrics.top);
        final int bottomY = Math.round(baselineY + fontMetrics.bottom);
        canvas.drawLine(0f, topY, canvasWidth, topY, mLinePaint);
        canvas.drawLine(0f, bottomY, canvasWidth, bottomY, mLinePaint);

        //Measure textwidth, text x-cood that will center the text, draw text
        final float textWidth = mNumberPaint.measureText(displayedCount);
        final float textX = Math.round(centerX - textWidth * 0.5f);
        canvas.drawText(displayedCount, textX, baselineY, mNumberPaint);
    }

    /**
     * Reconcile a desired size for the view contents with a {@link android.view.View.MeasureSpec}
     * constraint passed by the parent.
     *
     * This is a simplified version of {@link View#resolveSize(int, int)}
     *
     * @param contentSize Size of the view's contents.
     * @param measureSpec A {@link android.view.View.MeasureSpec} passed by the parent.
     * @return A size that best fits {@code contentSize} while respecting the parent's constraints.
     */
    private int reconcileSize(int contentSize, int measureSpec) {
        final int mode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.EXACTLY:
                return specSize;
            case MeasureSpec.AT_MOST:
                if (contentSize < specSize) {
                    return contentSize;
                } else {
                    return specSize;
                }
            case MeasureSpec.UNSPECIFIED:
            default:
                return contentSize;
        }
    }

    @Override
    public void reset() {
        if(getCount()!=0){
            setCount(0);
            postInvalidate();
        }
    }

    @Override
    public void increment() {
        setCount(count+1);
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void setCount(int count) {
        count = Math.min(count, MAX_COUNT);
        this.count = count;
        //The string to be displayed is formatted here
        this.displayedCount = String.format(Locale.getDefault(), "%04d", count);
        postInvalidate();
    }

    @Override
    public void decrement() {
        setCount(count-1);
    }

    public void setMaxCount(int count){
        count = Math.min(count, MAX_COUNT);
        this.count = count;
        //The string to be displayed is formatted here
        this.displayedCount = String.format(Locale.getDefault(), "%04d", count);
        postInvalidate();
    }
}
