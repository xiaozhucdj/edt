package com.yougy.view.dialog;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yougy.common.dialog.BaseDialog;
import com.yougy.ui.activity.R;

/**
 * Created by Administrator on 2017/6/21.
 * 界面提示的dialog
 */

public class UiPromptDialog extends BaseDialog implements View.OnClickListener {
    private TextView mTvTitle;
    private Button mBtnCancel;
    private Button mBtnConfirm;
    private Button mBtnCenterConfirm;
    private LinearLayout mRlCancelAndConfirm;
    private RelativeLayout mRlCenter;

    public UiPromptDialog(Context context) {
        super(context);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public UiPromptDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    protected void init() {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.dialog_down_book);
        mTvTitle = this.findViewById(R.id.tv_title);

        mBtnCancel = this.findViewById(R.id.btn_cancel);
        mBtnCancel.setOnClickListener(this);

        mBtnConfirm = this.findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);


        mRlCancelAndConfirm = this.findViewById(R.id.rl_cancelAndConfirm);
        mRlCenter = this.findViewById(R.id.rl_center);
        mBtnCenterConfirm = this.findViewById(R.id.btn_centerConfirm);
        mBtnCenterConfirm.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_cancel:

                if (mListener != null) {
                    mListener.onUiCancelListener();
                }
                break;
            case R.id.btn_confirm:
                if (mListener != null) {
                    mListener.onUiDetermineListener();
                }
                break;

            case R.id.btn_centerConfirm:
                if (mListener != null) {
                    mListener.onUiCenterDetermineListener();
                }
                break;
        }
    }

    private Listener mListener;

    public interface Listener {
        void onUiCancelListener();

        void onUiDetermineListener();

        void onUiCenterDetermineListener();
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void setTitle(String str) {
        mTvTitle.setText(str);
    }

    public void setTitle(int resID) {
        mTvTitle.setText(resID);
    }


    public void setDialogStyle(boolean isCenter) {
        mRlCancelAndConfirm.setVisibility(isCenter == true ? View.GONE : View.VISIBLE);
        mRlCenter.setVisibility(isCenter == false ? View.GONE : View.VISIBLE);
    }

    private int tag = 0;

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public void set(int resID) {
        mTvTitle.setText(resID);
    }


    public void setCancel(int resId) {
        mBtnCancel.setText(resId);
    }

    public void setConfirm(int resId) {
        mBtnConfirm.setText(resId);
    }

    public void setCenterConfirm(int resId) {
        mBtnCenterConfirm.setText(resId);
    }
}
