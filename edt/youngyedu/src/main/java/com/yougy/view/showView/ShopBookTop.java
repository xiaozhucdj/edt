package com.yougy.view.showView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yougy.ui.activity.R;

/**
 * Created by jiangliang on 2016/9/18.
 */
public class ShopBookTop extends RelativeLayout {
    private TextView mBackBtn;
    private TextView mTitle;

    public ShopBookTop(Context context) {
        super(context);
        init(context);
    }

    public ShopBookTop(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.shop_cart_top, this);
        mBackBtn = (TextView) findViewById(R.id.book_shop_back);
        mTitle = (TextView) findViewById(R.id.book_shop_title);
        mBackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener!=null){
                    mListener.onBack();
                }
            }
        });
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    private OnBackListener mListener;

    public void setOnBackListener(OnBackListener listener) {
        mListener = listener;
    }

    public interface OnBackListener {
        void onBack();
    }
}
