package com.yougy.shop.activity;

import android.os.Bundle;
import android.view.WindowManager;

import com.yougy.common.activity.AutoLayoutBaseActivity;

import butterknife.ButterKnife;

/**
 * Created by FH on 2017/3/6.
 */

public abstract class ShopAutoLayoutBaseActivity extends AutoLayoutBaseActivity {
    protected String tag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView();
        ButterKnife.bind(this);
        tag = this.getClass().getName();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    /**
     * 设置界面布局文件
     */
    protected abstract void setContentView();
}
