package com.yougy.common.media.file;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.yougy.common.dialog.BaseDialog;
import com.yougy.ui.activity.R;

public class DownFileDialog extends BaseDialog implements View.OnClickListener {
    private TextView mTvTitle;
    private Button mBtnCancel;
    private Button mBtnConfirm;

    private ClickListener mListener;

    public DownFileDialog(Context context) {
        super(context);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public DownFileDialog(Context context, int themeResId) {
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

        setContentView(R.layout.dialog_down_file);
        mTvTitle = (TextView) this.findViewById(R.id.tv_title);
        mBtnCancel = (Button) this.findViewById(R.id.btn_cancel);
        mBtnCancel.setOnClickListener(this);
        mBtnConfirm = (Button) this.findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        if (mListener == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.btn_cancel:
                mListener.onBtnCancelListener();
                break;
            case R.id.btn_confirm:
                mListener.onBtnConfirmListener();
                break;
        }

    }


    public void setTitle(String tite) {
        mTvTitle.setText(tite);
    }

    public void setBtConfirmCallBack() {
        mBtnConfirm.callOnClick();
    }

    public void setBtnConfirmVisibility(int visibility) {
        mBtnConfirm.setVisibility(visibility);
    }

    public void setBtnCancelVisibility(int visibility) {
        mBtnCancel.setVisibility(visibility);
    }

    public void setListener(ClickListener mListener) {
        this.mListener = mListener;
    }

    public interface ClickListener {


        void onBtnConfirmListener();

        void onBtnCancelListener();
    }
}
