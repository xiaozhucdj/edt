package com.yougy.view.showView;

/**
 * Created by jiangliang on 2017/2/9.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.ui.activity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动换行的LinearLayout
 */
public class AutoWrapLineLayout extends ViewGroup {

    public static final int MODE_FILL_PARENT = 0;
    public static final int MODE_WRAP_CONTENT = 1;

    private int mVerticalMargin = 0;
    private int mHorizontalMargin = 0;

    private int mFillMode = MODE_FILL_PARENT;

    private List<Integer> childOfLine; //Save the count of child views of each line;
    private List<Integer> mOriginWidth;

    public AutoWrapLineLayout(Context context) {
        super(context);
        mOriginWidth = new ArrayList<>();
    }

    public AutoWrapLineLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mOriginWidth = new ArrayList<>();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.autoWrapLineLayout);
        mHorizontalMargin = ta.getDimensionPixelSize(R.styleable.autoWrapLineLayout_horizontalMargin, 0);
        mVerticalMargin = ta.getDimensionPixelSize(R.styleable.autoWrapLineLayout_verticalMargin, 0);
        mFillMode = ta.getInteger(R.styleable.autoWrapLineLayout_fillMode, 1);
        ta.recycle();
    }

    public AutoWrapLineLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mFillMode == MODE_FILL_PARENT) {
            layoutModeFillParent();
        } else {
            layoutWrapContent();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        childOfLine = new ArrayList<>();
        int childCount = getChildCount();
        int totalHeight = getPaddingTop() + getPaddingBottom();
        int totalWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int curLineChildCount = 0;
        int curLineWidth = 0;
        int maxHeight = 0;
        for (int i = 0; i < childCount; i++) {
            View childItem = getChildAt(i);
            if (mFillMode == MODE_FILL_PARENT) {
                if (mOriginWidth.size() <= i) {
                    measureChild(childItem, widthMeasureSpec, heightMeasureSpec);
                    mOriginWidth.add(childItem.getMeasuredWidth());
                } else {
                    childItem.measure(MeasureSpec.makeMeasureSpec(mOriginWidth.get(i), MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(childItem.getMeasuredHeight(), MeasureSpec.EXACTLY));
                }
            } else {
                measureChild(childItem, widthMeasureSpec, heightMeasureSpec);
            }
            int childHeight = childItem.getMeasuredHeight();
            int childWidth = childItem.getMeasuredWidth() + (curLineWidth > 0 ? mHorizontalMargin : 0);//每一行的第一个不计算marginleft
            if (curLineWidth + childWidth <= totalWidth) {
                curLineWidth += childWidth;
                maxHeight = Math.max(childHeight, maxHeight);
                curLineChildCount++;
            } else {
                childOfLine.add(curLineChildCount);
                curLineWidth = childWidth;
                curLineChildCount = 1;
                totalHeight += maxHeight;
                maxHeight = childHeight;
            }

        }
        childOfLine.add(curLineChildCount);
        for (int i = 0; i < childOfLine.size(); i++) {
            if (childOfLine.get(i) == 0) {
                childOfLine.remove(i);
            }
        }
        totalHeight += (mVerticalMargin * (childOfLine.size() - 1) + maxHeight);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), totalHeight);
    }

    private void layoutModeFillParent() {
        int index = 0;
        int width = getMeasuredWidth();
        int curHeight = 0;
        for (int i = 0; i < childOfLine.size(); i++) {
            int childCount = childOfLine.get(i);
            int maxHeight = 0;
            int lineWidth = 0;
            for (int j = 0; j < childCount; j++) {
                lineWidth += getChildAt(j + index).getMeasuredWidth();
            }
            int padding = (width - lineWidth - mHorizontalMargin * (childCount - 1)) / childCount / 2;
            lineWidth = 0;
            int target = index + childCount;
            for (; index < target; index++) {
                View item = getChildAt(index);
                maxHeight = Math.max(maxHeight, item.getMeasuredHeight());
                item.setPadding(padding, item.getPaddingTop(),
                        padding, item.getPaddingBottom());
                item.measure(MeasureSpec.makeMeasureSpec(item.getMeasuredWidth() + padding * 2, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(item.getMeasuredHeight(), MeasureSpec.EXACTLY));
                item.layout(lineWidth, curHeight, lineWidth + item.getMeasuredWidth(), curHeight + item.getMeasuredHeight());
                lineWidth += item.getMeasuredWidth() + mHorizontalMargin;
            }
            curHeight += maxHeight + mVerticalMargin;
        }
    }

    private void layoutWrapContent() {
        int index = 0;
        int curHeight = getPaddingTop();
        for (int i = 0; i < childOfLine.size(); i++) {
            int childCount = childOfLine.get(i);
            int maxHeight = getPaddingTop();
            int lineWidth = getPaddingLeft();
            int target = index + childCount;
            for (; index < target; index++) {
                View item = getChildAt(index);
                maxHeight = Math.max(maxHeight, item.getMeasuredHeight());
                item.layout(lineWidth, curHeight, lineWidth + item.getMeasuredWidth(), curHeight + item.getMeasuredHeight());
                lineWidth += item.getMeasuredWidth() + mHorizontalMargin;
            }
            curHeight += maxHeight + mVerticalMargin;
        }
    }

    @Override
    public void addView(View child) {
        super.addView(child);
    }

    public void setFillMode(int fillMode) {
        this.mFillMode = fillMode;
    }

    public void setHorizontalMargin(int horizontalMargin) {
        this.mHorizontalMargin = horizontalMargin;
    }

    public void setVerticalMargin(int verticalMargin) {
        this.mVerticalMargin = verticalMargin;
    }

}

