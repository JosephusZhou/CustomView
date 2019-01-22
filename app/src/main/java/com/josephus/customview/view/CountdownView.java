package com.josephus.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;

import com.josephus.customview.DensityUtils;
import com.josephus.customview.R;

/**
 * 自定义倒计时 View
 */
public class CountdownView extends View {

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    private int timeWidth;
    private int timeHeight;
    private int timeSize;
    private int timeColor;
    private int timeBgColor;
    private int timeBgCornerRadius;
    private int timeBg;
    private int divWidth;
    private int divMarginX;
    private int divMarginY;
    private int divColor;

    private Paint timeTextPaint;
    private Paint timeBgPaint;
    private Paint timeDivPaint;

    private RectF rectF;
    private float divRadius;
    private float divFirstY;
    private float divSecondY;
    private Rect textBounds;
    private Bitmap bitmap;

    private String[] times;
    private CountDownTimer countDownTimer;

    public CountdownView(Context context) {
        this(context, null);
    }

    public CountdownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountdownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CountdownView, defStyleAttr, 0);
        timeWidth = a.getDimensionPixelSize(R.styleable.CountdownView_cdv_time_width, DensityUtils.dp2px(context, 40));
        timeHeight = a.getDimensionPixelSize(R.styleable.CountdownView_cdv_time_height, DensityUtils.dp2px(context, 30));
        timeSize = a.getDimensionPixelSize(R.styleable.CountdownView_cdv_time_size, DensityUtils.dp2px(context, 26));
        timeColor = a.getColor(R.styleable.CountdownView_cdv_time_color, BLACK);
        timeBgColor = a.getColor(R.styleable.CountdownView_cdv_time_bg_color, WHITE);
        timeBgCornerRadius = a.getDimensionPixelSize(R.styleable.CountdownView_cdv_time_bg_corner_radius, DensityUtils.dp2px(context, 3));
        timeBg = a.getResourceId(R.styleable.CountdownView_cdv_time_bg, 0);
        divWidth = a.getDimensionPixelSize(R.styleable.CountdownView_cdv_div_width, DensityUtils.dp2px(context, 2));
        divMarginX = a.getDimensionPixelSize(R.styleable.CountdownView_cdv_div_margin_x, DensityUtils.dp2px(context, 3));
        divMarginY = a.getDimensionPixelSize(R.styleable.CountdownView_cdv_div_margin_y, DensityUtils.dp2px(context, 10));
        divColor = a.getColor(R.styleable.CountdownView_cdv_div_color, WHITE);
        a.recycle();

        init();
    }

    private void init() {
        timeTextPaint = new Paint();
        timeTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        timeTextPaint.setTextAlign(Paint.Align.CENTER);
        timeTextPaint.setStyle(Paint.Style.FILL);
        timeTextPaint.setTextSize(timeSize);
        timeTextPaint.setStrokeWidth(3);
        timeTextPaint.setColor(timeColor);

        timeBgPaint = new Paint();
        timeBgPaint.setStyle(Paint.Style.FILL);
        if (timeBg > 0) {
            bitmap = BitmapFactory.decodeResource(getResources(), timeBg);
        } else {
            timeBgPaint.setColor(timeBgColor);
        }

        timeDivPaint = new Paint();
        timeDivPaint.setStyle(Paint.Style.FILL);
        timeDivPaint.setColor(divColor);

        rectF = new RectF(0, 0, timeWidth, timeHeight);
        divRadius = divWidth / 2.0f;
        divFirstY = (timeHeight - divMarginY - divWidth * 2.0f) / 2.0f + divRadius;
        divSecondY = timeHeight - (timeHeight - divMarginY - divWidth * 2.0f) / 2.0f - divRadius;
        textBounds = new Rect();

        times = new String[4];
        for (int i = 0; i < times.length; i ++) {
            times[i] = "00";
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = timeWidth * 4 + divWidth * 4 + divMarginX * 6;
        int measuredHeight = timeHeight;
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < 4; i ++) {
            // draw time bg
            rectF.left = timeWidth * i + divMarginX * 2 * i + divWidth * i;
            rectF.right = timeWidth * (i + 1) + divMarginX * 2 * i + divWidth * i;
            if (timeBg > 0) {
                canvas.drawBitmap(bitmap, null, rectF, timeBgPaint);
            } else {
                canvas.drawRoundRect(rectF, timeBgCornerRadius, timeBgCornerRadius, timeBgPaint);
            }

            // draw time div
            if (i < 3) {
                canvas.drawCircle(rectF.right + divMarginX + divRadius,  divFirstY, divRadius, timeDivPaint);
                canvas.drawCircle(rectF.right + divMarginX + divRadius,  divSecondY, divRadius, timeDivPaint);
            }

            // draw time text
            timeTextPaint.getTextBounds(times[i], 0, times[i].length(), textBounds);
            Paint.FontMetricsInt fontMetrics = timeTextPaint.getFontMetricsInt();
            canvas.drawText(times[i],
                    timeWidth * i + divMarginX * 2 * i + divWidth * i + timeWidth / 2,
                    (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2.0f - fontMetrics.top,
                    timeTextPaint);
        }
    }

    public void setTime(long time) {
        if (time <= 0) {
            return;
        }

        cancelCountdown();

        countDownTimer = new CountDownTimer(time * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                long remindSeconds = millisUntilFinished / 1000;
                times[0] = format(remindSeconds / 86400);
                long last = remindSeconds % 86400;
                times[1] = format(last / 3600);
                last = last % 3600;
                times[2] = format(last / 60);
                times[3] = format(last % 60);
                invalidate();
            }

            public void onFinish() {

            }
        }.start();
    }

    public void cancelCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private String format(long time) {
        if (time < 10) {
            return String.format("0%s", time);
        } else {
            return String.format("%s", time);
        }
    }
}
