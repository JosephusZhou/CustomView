package com.josephus.customview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自定义盘口 View
 */
public class DepthView extends View {

    public DepthView(Context context) {
        this(context, null);
    }

    public DepthView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DepthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
