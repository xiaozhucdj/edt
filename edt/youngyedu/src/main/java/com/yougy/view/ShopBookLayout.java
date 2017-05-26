package com.yougy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.yougy.common.utils.LogUtils;
import com.yougy.shop.bean.BookInfo;
import com.yougy.ui.activity.R;

import java.util.List;

/**
 * Created by jiangliang on 2016/9/21.
 */
public class ShopBookLayout extends LinearLayout {

    private ShopBookView[] shopBookViews = new ShopBookView[5];
    private LinearLayout mLinearLayout;
    public ShopBookLayout(Context context) {
        super(context);
        init(context);
    }

    public ShopBookLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mLinearLayout = (LinearLayout) View.inflate(context, R.layout.shop_book_layout, this);
        shopBookViews[0] = (ShopBookView) findViewById(R.id.shop_book_first);
        shopBookViews[1] = (ShopBookView) findViewById(R.id.shop_book_second);
        shopBookViews[2] = (ShopBookView) findViewById(R.id.shop_book_third);
        shopBookViews[3] = (ShopBookView) findViewById(R.id.shop_book_fourth);
        shopBookViews[4] = (ShopBookView) findViewById(R.id.shop_book_five);
    }

    public void updateView(List<BookInfo> infos) {
        LogUtils.e("ShopBookLayout", "updateView.................info's size : " + infos.size());
        for (int i = 0; i < infos.size(); i++) {
            shopBookViews[i].updateView(infos.get(i));
            shopBookViews[i].setVisibility(View.VISIBLE);
        }
    }
}
