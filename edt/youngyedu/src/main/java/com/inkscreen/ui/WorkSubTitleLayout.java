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
public class WorkSubTitleLayout extends LinearLayout {

    View container;
    ImageView backIv;
    ImageView bookIv;
    ImageView workIv;
    ImageView panIv;
    ImageView undoImg;
    ImageView redoImg;
    ImageView imgBi;
    ImageView imgXp;

    private void inject(){
        container = findViewById(R.id.container);
        backIv= (ImageView)findViewById(R.id.backIv);
        bookIv = (ImageView)findViewById(R.id.bookIv);
        workIv = (ImageView)findViewById(R.id.workIv);
        panIv = (ImageView)findViewById(R.id.panIv);
        undoImg = (ImageView)findViewById(R.id.nudo1);
        redoImg = (ImageView)findViewById(R.id.redo);
        imgBi = (ImageView)findViewById(R.id.bi);
        imgXp = (ImageView)findViewById(R.id.xp);

    }

    public WorkSubTitleLayout(Context context) {
        super(context);
        init();
    }

    public WorkSubTitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WorkSubTitleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public WorkSubTitleLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        inflate(getContext(), R.layout.leke_work_title_actionbar, this);
        setOrientation(VERTICAL);
        inject();
    }


    public void setTitle(String title, OnClickListener clickBackListener) {
        if (clickBackListener != null){
            backIv.setOnClickListener(clickBackListener);
        }
    }
    public void setOndo( OnClickListener clickBackListener) {
        if (clickBackListener != null){
            undoImg.setOnClickListener(clickBackListener);
        }
    }
    public void setOnRedo(OnClickListener clickBackListener) {
        if (clickBackListener != null){
            redoImg.setOnClickListener(clickBackListener);
        }
    }
    public void setOncWork(String title, OnClickListener clickBackListener) {
        if (clickBackListener != null){
            workIv.setOnClickListener(clickBackListener);
        }
    }

    public void setOnBi(String title, OnClickListener clickBackListener) {
        if (clickBackListener != null){
            imgBi.setOnClickListener(clickBackListener);
        }
    }
    public void setOnXp(String title, OnClickListener clickBackListener) {
        if (clickBackListener != null){
            imgXp.setOnClickListener(clickBackListener);
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
