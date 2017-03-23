package com.inkscreen.ui;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yougy.ui.activity.R;


/**
 * Created by xcz on 2016/11/29.
 */
public class LeSubTitleLayout extends LinearLayout {

    View container;
    ImageView backIv;
    ImageView bookIv;
    ImageView workIv;
    ImageView panIv;

    private void inject(){
        container = findViewById(R.id.container);
        backIv= (ImageView)findViewById(R.id.backIv);
        bookIv = (ImageView)findViewById(R.id.bookIv);
        workIv = (ImageView)findViewById(R.id.workIv);
        panIv = (ImageView)findViewById(R.id.panIv);
    }

    public LeSubTitleLayout(Context context) {
        super(context);
        init();
    }

    public LeSubTitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LeSubTitleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LeSubTitleLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        inflate(getContext(), R.layout.leke_title_actionbar, this);
        setOrientation(VERTICAL);
        inject();
    }


    public void setTitle(String title, OnClickListener clickBackListener) {
        if (clickBackListener != null){
            backIv.setOnClickListener(clickBackListener);
        }
    }
    public void setOncBook(String title, OnClickListener clickBackListener) {
        if (clickBackListener != null){
            bookIv.setOnClickListener(clickBackListener);
        }
    }
    public void setOncPan(String title, OnClickListener clickBackListener) {
        if (clickBackListener != null){
            panIv.setOnClickListener(clickBackListener);
        }
    }
    public void setOncWork(String title, OnClickListener clickBackListener) {
        if (clickBackListener != null){
            workIv.setOnClickListener(clickBackListener);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }


}
