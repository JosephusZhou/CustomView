package com.josephus.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.josephus.customview.DensityUtils;
import com.josephus.customview.R;

/**
 * 自定义盘口 View
 */
public class DepthView extends View {

    private static final int RED = 0xFFD8545E;
    private static final int GREEN = 0xFF4FA29A;
    private static final int RED_BG = 0xFF562226;
    private static final int GREEN_BG = 0xFF31524E;
    private static final int WHITE = 0xFFFFFFFF;

    private Context context;

    private int paddingWidth;
    private int paddingHeight;
    private int bidBgColor;
    private int askBgColor;
    private int upColor;
    private int downColor;
    private int textColor;
    private int amountSize;
    private int coinSize;
    private int currencySize;

    private float itemMaxWidth;
    private float itemHeight;
    private int amountMarginLeft;
    private int coinPriceMarginRight;

    private Paint itemBgPaint;
    private Paint itemAmountPaint;
    private Paint itemCoinPricePaint;
    private Paint itemCurrencyPaint;
    private RectF rectF;
    private Rect textBounds;
    private int subSize;

    private DepthViewData data;

    public DepthView(Context context) {
        this(context, null);
    }

    public DepthView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DepthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DepthView, defStyleAttr, 0);
        paddingWidth = a.getDimensionPixelSize(R.styleable.DepthView_dv_padding_width, DensityUtils.dp2px(context, 1));
        paddingHeight = a.getDimensionPixelSize(R.styleable.DepthView_dv_padding_height, DensityUtils.dp2px(context, 4));
        bidBgColor = a.getColor(R.styleable.DepthView_dv_bid_bg_color, RED_BG);
        askBgColor = a.getColor(R.styleable.DepthView_dv_ask_bg_color, GREEN_BG);
        upColor = a.getColor(R.styleable.DepthView_dv_up_color, RED);
        downColor = a.getColor(R.styleable.DepthView_dv_down_color, GREEN);
        textColor = a.getColor(R.styleable.DepthView_dv_text_color, WHITE);
        amountSize = a.getDimensionPixelSize(R.styleable.DepthView_dv_amount_size, DensityUtils.dp2px(context, 13));
        coinSize = a.getDimensionPixelSize(R.styleable.DepthView_dv_coin_size, DensityUtils.dp2px(context, 13));
        currencySize = a.getDimensionPixelSize(R.styleable.DepthView_dv_currency_size, DensityUtils.dp2px(context, 11));
        a.recycle();

        init();
    }

    private void init() {
        itemBgPaint = new Paint();
        itemBgPaint.setStyle(Paint.Style.FILL);

        itemAmountPaint = new Paint();
        itemAmountPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        itemAmountPaint.setStyle(Paint.Style.FILL);
        itemAmountPaint.setTextAlign(Paint.Align.LEFT);
        itemAmountPaint.setTextSize(amountSize);
        itemAmountPaint.setColor(textColor);

        itemCoinPricePaint = new Paint();
        itemCoinPricePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        itemCoinPricePaint.setStyle(Paint.Style.FILL);
        itemCoinPricePaint.setTextSize(coinSize);

        itemCurrencyPaint = new Paint();
        itemCurrencyPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        itemCurrencyPaint.setStyle(Paint.Style.FILL);
        itemCurrencyPaint.setTextSize(currencySize);
        itemCurrencyPaint.setColor(textColor);

        amountMarginLeft = DensityUtils.dp2px(context, 13);
        coinPriceMarginRight = DensityUtils.dp2px(context, 6);

        rectF = new RectF(0, 0, 0, 0);
        textBounds = new Rect();

        data = new DepthViewData();
        data.bidAmount = "0";
        data.bidCoinPrice = "-";
        data.bidCurrencyPrice = "-";
        data.bidUpOrDown = 1;
        data.bidProgress = 0;
        data.askAmount = "0";
        data.askCoinPrice = "-";
        data.askCurrencyPrice = "-";
        data.askUpOrDown = 1;
        data.askProgress = 0;
    }

    public void setData(DepthViewData data) {
        this.data = data;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        itemMaxWidth = (getMeasuredWidth() - paddingWidth * 3) / 2.0f;
        itemHeight = getMeasuredHeight() - paddingHeight * 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制买卖一档背景
        itemBgPaint.setColor(bidBgColor);
        rectF.top = paddingHeight;
        rectF.bottom = paddingHeight + itemHeight;
        rectF.right = paddingWidth + itemMaxWidth;
        rectF.left = paddingWidth + (1 - data.bidProgress / 100.0f) * itemMaxWidth;
        canvas.drawRect(rectF, itemBgPaint);

        itemBgPaint.setColor(askBgColor);
        rectF.left = paddingWidth * 2 + itemMaxWidth;
        rectF.right = rectF.left + data.askProgress / 100.0f * itemMaxWidth;
        canvas.drawRect(rectF, itemBgPaint);

        // 绘制数量
        Paint.FontMetricsInt fontMetrics = itemAmountPaint.getFontMetricsInt();
        float baselineY = getMeasuredHeight() / 2.0f + ((fontMetrics.descent - fontMetrics.ascent) / 2.0f - fontMetrics.descent);

        scaleTextSize(itemAmountPaint, itemMaxWidth, amountMarginLeft, amountSize, data.bidAmount);
        canvas.drawText(data.bidAmount, paddingWidth + amountMarginLeft,
                baselineY, itemAmountPaint);

        scaleTextSize(itemAmountPaint, itemMaxWidth, amountMarginLeft, amountSize, data.askAmount);
        canvas.drawText(data.askAmount, paddingWidth * 2 + itemMaxWidth + amountMarginLeft,
                baselineY, itemAmountPaint);

        // 绘制价格
        fontMetrics = itemCoinPricePaint.getFontMetricsInt();
        baselineY = (paddingHeight + getMeasuredHeight() / 2.0f) / 2.0f + ((fontMetrics.descent - fontMetrics.ascent) / 2.0f - fontMetrics.descent);

        scaleTextSize(itemCoinPricePaint, itemMaxWidth, coinPriceMarginRight, coinSize, data.bidCoinPrice);
        itemCoinPricePaint.getTextBounds(data.bidCoinPrice, 0, data.bidCoinPrice.length(), textBounds);
        if (data.bidUpOrDown == 1) {
            itemCoinPricePaint.setColor(upColor);
        } else {
            itemCoinPricePaint.setColor(downColor);
        }
        canvas.drawText(data.bidCoinPrice,
                paddingWidth + itemMaxWidth - coinPriceMarginRight - (textBounds.right - textBounds.left),
                baselineY,
                itemCoinPricePaint);

        scaleTextSize(itemCoinPricePaint, itemMaxWidth, coinPriceMarginRight, coinSize, data.askCoinPrice);
        itemCoinPricePaint.getTextBounds(data.askCoinPrice, 0, data.askCoinPrice.length(), textBounds);
        if (data.askUpOrDown == 1) {
            itemCoinPricePaint.setColor(upColor);
        } else {
            itemCoinPricePaint.setColor(downColor);
        }
        canvas.drawText(data.askCoinPrice,
                getMeasuredWidth() - paddingWidth - textBounds.right - coinPriceMarginRight,
                baselineY,
                itemCoinPricePaint);

        // 绘制法币价格
        fontMetrics = itemCurrencyPaint.getFontMetricsInt();
        baselineY = (getMeasuredHeight() / 2.0f + (getMeasuredHeight() - paddingHeight)) / 2.0f + ((fontMetrics.descent - fontMetrics.ascent) / 2.0f - fontMetrics.descent);

        scaleTextSize(itemCurrencyPaint, itemMaxWidth, coinPriceMarginRight, currencySize, data.bidCurrencyPrice);
        itemCurrencyPaint.getTextBounds(data.bidCurrencyPrice, 0, data.bidCurrencyPrice.length(), textBounds);
        canvas.drawText(data.bidCurrencyPrice,
                paddingWidth + itemMaxWidth - coinPriceMarginRight - (textBounds.right - textBounds.left),
                baselineY,
                itemCurrencyPaint);

        scaleTextSize(itemCurrencyPaint, itemMaxWidth, coinPriceMarginRight, currencySize, data.askCurrencyPrice);
        itemCurrencyPaint.getTextBounds(data.askCurrencyPrice, 0, data.askCurrencyPrice.length(), textBounds);
        canvas.drawText(data.askCurrencyPrice,
                getMeasuredWidth() - paddingWidth - textBounds.right - coinPriceMarginRight,
                baselineY,
                itemCurrencyPaint);
    }

    private void scaleTextSize(Paint paint, float maxWidth, int otherWidth, int maxSize, String text) {
        subSize = 1;
        paint.setTextSize(maxSize);
        paint.getTextBounds(text, 0, text.length(), textBounds);
        int width = textBounds.width() + otherWidth;
        while (width > maxWidth / 2.0f) {
            paint.setTextSize(maxSize - subSize);
            paint.getTextBounds(text, 0, text.length(), textBounds);
            width = textBounds.width() + coinPriceMarginRight;
            subSize ++;
        }
    }

    public static class DepthViewData {
        public String bidAmount;
        public String bidCoinPrice;
        public String bidCurrencyPrice;
        public int bidUpOrDown;
        public int bidProgress;

        public String askAmount;
        public String askCoinPrice;
        public String askCurrencyPrice;
        public int askUpOrDown;
        public int askProgress;
    }
}
