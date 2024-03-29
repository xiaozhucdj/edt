package com.yougy.shop.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yougy.common.utils.DateUtils;
import com.yougy.home.activity.MainActivity;
import com.yougy.ui.activity.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by FH on 2017/3/7.
 */

public class PaySuccessActivity extends ShopAutoLayoutBaseActivity {

    @BindView(R.id.pay_succcess_actvt_money_sum_tv)
    TextView moneySumTv;
    @BindView(R.id.pay_succcess_actvt_pay_time_tv)
    TextView payTimeTv;
    @BindView(R.id.pay_succcess_actvt_to_schoolbag_btn)
    Button toSchoolbagBtn;
    @BindView(R.id.pay_succcess_actvt_to_shop_frontpage_btn)
    Button toShopFrontpageBtn;

    double orderPrice;
    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_pay_success);
    }

    @Override
    protected void init() {
        orderPrice = getIntent().getDoubleExtra("price" , -1);
    }

    @Override
    protected void initLayout() {

    }

    @Override
    protected void loadData() {
        moneySumTv.setText("支付金额 : ￥" + orderPrice);
        payTimeTv.setText("支付时间 :　" + DateUtils.getCalendarAndTimeString());

    }

    @Override
    protected void refreshView() {

    }

    @OnClick({R.id.pay_succcess_actvt_to_schoolbag_btn, R.id.pay_succcess_actvt_to_shop_frontpage_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pay_succcess_actvt_to_schoolbag_btn:
                loadIntentWithSpecificFlag(MainActivity.class , Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            case R.id.pay_succcess_actvt_to_shop_frontpage_btn:
                loadIntentWithSpecificFlag(BookShopActivityDB.class , Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                finish();
                break;
        }
    }
//
//    @Override
//    public void onBackPressed() {
//
//    }
}
