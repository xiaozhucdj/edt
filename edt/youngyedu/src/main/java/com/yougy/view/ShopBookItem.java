package com.yougy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yougy.common.utils.LogUtils;
import com.yougy.shop.bean.BookInfo;
import com.yougy.shop.adapter.BookShopAdapter;
import com.yougy.ui.activity.R;

import java.util.List;

/**
 * Created by jiangliang on 2016/9/21.
 */
public class ShopBookItem extends RelativeLayout implements View.OnClickListener {
    private TextView mBookClassifyTv;
    private TextView mBookMoreTv;
    private ShopBookLayout mShopBookLayout;
    private String mClassify;
    private int position;
    public static final String CLASSIFY = "classify";

    public ShopBookItem(Context context) {
        super(context);
        init(context);
    }

    public ShopBookItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.book_shop_classify_layout, this);
        mBookClassifyTv = findViewById(R.id.book_classify);
        mBookMoreTv = findViewById(R.id.book_more);
        mShopBookLayout = findViewById(R.id.book_list);
        mBookMoreTv.setOnClickListener(this);
    }

    public void updateView(String classify, List<BookInfo> infos,int position) {
        LogUtils.e("ShopBookItem","updateView.................");
        mClassify = classify;
        mBookClassifyTv.setText(classify);
        mShopBookLayout.updateView(infos);
        this.position = position;
    }

    private BookShopAdapter.OnMoreClickListener listener;

    public void setOnMoreClickListener(BookShopAdapter.OnMoreClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        LogUtils.e("ShopBookItem", " classify is : " + mClassify);
//        Intent intent = new Intent(getContext(), SearchResultActivity.class);
//        intent.putExtra(CLASSIFY,mClassify);
//        getContext().startActivity(intent);
        if (listener != null) {
            listener.onMoreClick(position);
        }
    }
}
