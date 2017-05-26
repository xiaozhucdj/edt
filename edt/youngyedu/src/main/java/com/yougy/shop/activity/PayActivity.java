package com.yougy.shop.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yougy.init.bean.BookInfo;
import com.yougy.common.activity.BaseActivity;
import com.yougy.ui.activity.R;
import com.yougy.shop.adapter.AdapterActivityPayBooks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/20.
 * <p/>
 * 支付
 */
public class PayActivity extends BaseActivity implements View.OnClickListener {
    private Button mBtnBack;
    private TextView mTvPayMsg;
    private Button mBtnPayQQ;
    private Button mBtnPayALi;
    private Button mBtnUnionPay;
    private RecyclerView mReclyView;
    private List<BookInfo> mInfos ;
    private AdapterActivityPayBooks mAdapter;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_pay);
    }

    @Override
    protected void init() {
        initInfos();
    }


    /**
     * 初始化数据
     */
    private void initInfos() {
        mInfos = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            BookInfo info = new BookInfo();
            info.setBookCover("xxxxxxxxxxxxxxx");
            info.setBookTitle("Android群英传" + i);
            info.setBookAuthor("蒋亮" + i);
            info.setBookSalePrice(30.00f);
            mInfos.add(info);
        }
    }

    @Override
    protected void initLayout() {

        mBtnBack = (Button) this.findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(this);

        mTvPayMsg = (TextView) this.findViewById(R.id.tv_BookPayMsg);


        mBtnPayQQ = (Button) this.findViewById(R.id.btn_PayQQ);
        mBtnPayQQ.setOnClickListener(this);

        mBtnPayALi = (Button) this.findViewById(R.id.btn_PayALi);
        mBtnPayALi.setOnClickListener(this);

        mBtnUnionPay = (Button) this.findViewById(R.id.btn_UnionPay);
        mBtnUnionPay.setOnClickListener(this);

        initReclyView();

    }

    /**初始化RecyclerView */
    private  void  initReclyView(){
        mReclyView = (RecyclerView) this.findViewById(R.id.recycler_view);
        GridLayoutManager layout = new GridLayoutManager(PayActivity.this, 4);
        mReclyView.setLayoutManager(layout);
        mAdapter = new AdapterActivityPayBooks(PayActivity.this, mInfos);
        mReclyView.setAdapter(mAdapter);
        mReclyView.setHasFixedSize(true);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void refreshView() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_back:
                PayActivity.this.finish();
                break;

            case R.id.btn_PayQQ:
                System.out.println("微信支付");
                break;
            case R.id.btn_PayALi:
                System.out.println("支付宝支付");
                break;
            case R.id.btn_UnionPay:
                System.out.println("银联支付支付");
                break;
        }
    }
}
