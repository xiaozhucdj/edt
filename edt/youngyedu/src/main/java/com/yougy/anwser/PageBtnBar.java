package com.yougy.anwser;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.yougy.ui.activity.R;

/**
 * Created by FH on 2018/1/8.
 */

public class PageBtnBar extends LinearLayout {
    private PageBarAdapter mPageBarAdapter;
    private OnPageBtnClickListener mOnPageBtnClickListener;
    private int currentSelectPageIndex = -1;

    public PageBtnBar(Context context) {
        this(context , null);
    }

    public PageBtnBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public PageBtnBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Button makePageBtn() {
        Button button = (Button) LayoutInflater.from(getContext())
                .inflate(R.layout.item_page_btn, this, false);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = (int) v.getTag();
                if (mOnPageBtnClickListener != null){
                    mOnPageBtnClickListener.onPageBtnClick(v , index , (String)((Button) v).getText());
                }
            }
        });
        return button;
    }

    public void refreshPageBar() {
        if (mPageBarAdapter == null || mPageBarAdapter.getPageBtnCount() == 0){
            removeAllViews();
            currentSelectPageIndex = -1;
            return;
        }
        while (getChildCount() > mPageBarAdapter.getPageBtnCount()) {
            removeViewAt(getChildCount()- 1);
        }
        while (getChildCount() < mPageBarAdapter.getPageBtnCount()) {
            addView(makePageBtn());
        }
        boolean needPerformClick = false;
        if (currentSelectPageIndex == -1 || currentSelectPageIndex >= getChildCount()){
            currentSelectPageIndex = 0;
            needPerformClick = true;
        }
        for (int i = 0; i < getChildCount(); i++) {
            Button button = (Button) getChildAt(i);
            button.setTag(i);
            button.setText(mPageBarAdapter.getPageText(i));
            if (i == currentSelectPageIndex) {
                button.setSelected(true);
            } else {
                button.setSelected(false);
            }
        }
        if (needPerformClick){
            getChildAt(0).performClick();
        }
    }

    public PageBarAdapter getPageBarAdapter() {
        return mPageBarAdapter;
    }

    public PageBtnBar setPageBarAdapter(PageBarAdapter pageBarAdapter) {
        this.mPageBarAdapter = pageBarAdapter;
        return this;
    }

    public OnPageBtnClickListener getOnPageBtnClickListener() {
        return mOnPageBtnClickListener;
    }

    public PageBtnBar setOnPageBtnClickListener(OnPageBtnClickListener onPageBtnClickListener) {
        this.mOnPageBtnClickListener = onPageBtnClickListener;
        return this;
    }

    public int getCurrentSelectPageIndex() {
        return currentSelectPageIndex;
    }

    public interface OnPageBtnClickListener{
        void onPageBtnClick(View btn, int btnIndex, String textInBtn);
    }

    public interface PageBarAdapter{
        int getPageBtnCount();
        String getPageText(int index);
    }


}
