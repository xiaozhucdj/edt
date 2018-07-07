package com.yougy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2018/3/26.
 */

public class ContentPdfImageView extends android.support.v7.widget.AppCompatImageView {


    public ContentPdfImageView(Context context) {
        super(context);
    }

    public ContentPdfImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
