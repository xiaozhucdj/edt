package com.yougy.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yougy.common.dialog.BaseDialog;
import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.ImageLoaderManager;
import com.yougy.ui.activity.R;

/**
 * Created by Administrator on 2018/5/17.
 */

public class PaySuccessDialog extends BaseDialog implements View.OnClickListener {


    private ImageView mImgClosae;
    private Button mBtnCancel;
    private Button mBtnConfirm;
    private TextView mTvOrderNumber;
    private TextView mTvOrderTime;
    private TextView mTvOrderDetail;
    private TextView mTvSalesDetail;
    private ImageView mImgSmallIcon;
    private TextView mTvBookName;
    private TextView mTvBookAuther;
    private TextView mTvFactoryPrice;
    private TextView mTvMarketPrice;

    public PaySuccessDialog(Context context) {
        super(context);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public PaySuccessDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    protected void init() {
        // 用户不可以点击外部消失对话框
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.dialog_pay_success);
        mImgClosae = this.findViewById(R.id.img_close);
        mImgClosae.setOnClickListener(this);

        mBtnCancel = this.findViewById(R.id.btn_cancel);
        mBtnCancel.setOnClickListener(this);

        mBtnConfirm = this.findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);


        mTvOrderNumber = this.findViewById(R.id.tv_order_number);
        mTvOrderTime = this.findViewById(R.id.tv_order_time);
        mTvOrderDetail = this.findViewById(R.id.tv_order_detail);
        mTvSalesDetail = this.findViewById(R.id.tv_sales_detail);
        mImgSmallIcon = this.findViewById(R.id.img_book_icon);
        mTvBookName = this.findViewById(R.id.tv_book_name);
        mTvBookAuther = this.findViewById(R.id.tv_book_auther);
        mTvFactoryPrice = this.findViewById(R.id.tv_factory_price);
        mTvMarketPrice = this.findViewById(R.id.tv_market_price);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                dismiss();
                break;
            case R.id.btn_cancel:
                if (mPaySuccessListener != null) {
                    mPaySuccessListener.onCancelListener();
                }
                break;
            case R.id.btn_confirm:
                if (mPaySuccessListener != null) {
                    mPaySuccessListener.onConfirmListener();
                }
                break;
        }
    }

    private PaySuccessListener mPaySuccessListener;

    public void setBookDetailsListener(PaySuccessListener paySuccessListener) {
        mPaySuccessListener = paySuccessListener;
    }

    public interface PaySuccessListener {
        void onCancelListener();

        void onConfirmListener();
    }

    public void setOrderNumber(String str) {
        mTvOrderNumber.setText(str);
    }

    public void setOrderTime(String str) {
        mTvOrderTime.setText(str);
    }

    public void setOrderDetail(String str) {
        mTvOrderDetail.setText(str);
    }

    public void setSalesDetail(String str) {
        mTvSalesDetail.setText(str);
        if ("限免".equals(str) || "折扣".equals(str) || "满减".equals(str)){
            mTvSalesDetail.setBackgroundResource(R.drawable.img_bg_manjian);
        }
        else if ("不参加任何满减".equals(str)){
            mTvSalesDetail.setBackgroundResource(R.drawable.img_bucanjiamanjian);
        }
    }

    public void setSmallIcon(String url, Activity activity) {
        refreshImg(mImgSmallIcon, url, activity);
    }

    public void setBookName(String str) {
        mTvBookName.setText(str);
    }

    public void setBookAuther(String str) {
        mTvBookAuther.setText(str);
    }

    public void setFactoryPrice(String str) {
        mTvFactoryPrice.setText(str);
    }

    public void setMarketPrice(String str) {
        mTvMarketPrice.setText(str);
    }

    private void refreshImg(ImageView view, String url, Activity activity) {
        ImageLoaderManager.getInstance().loadImageActivity(activity,
                url,
                R.drawable.img_book_cover,
                R.drawable.img_book_cover,
                FileContonst.withS ,
                FileContonst.heightS,
                view);
    }
}