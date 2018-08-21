package com.yougy.view.dialog;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.yougy.common.dialog.BaseDialog;
import com.yougy.ui.activity.R;

/**
 * Created by Administrator on 2018/3/27.
 */

public class CancelAndConfirmDialog extends BaseDialog implements View.OnClickListener {


    private TextView mTvTitle;
    private Button mBtnCancel;
    private Button mBtnConfirm;

    public CancelAndConfirmDialog(Context context) {
        super(context);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public CancelAndConfirmDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    protected void init() {
        // 用户不可以点击外部消失对话框
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    protected void initLayout() {
        setContentView(R.layout.dialog_cancle_confirm);
        mTvTitle = this.findViewById(R.id.title_tv);
        mBtnCancel = this.findViewById(R.id.cancle_btn);
        mBtnCancel.setOnClickListener(this);
        mBtnConfirm = this.findViewById(R.id.confirm_btn);
        mBtnConfirm.setOnClickListener(this);
    }


/*    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        Display display = window.getWindowManager().getDefaultDisplay();
        int width = (int) (display.getWidth() * getWidthScale());
        window.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancle_btn:
                dismiss();
                break;
            case R.id.confirm_btn:
                if (mListener != null) {
                    mListener.clickListener();
                }
                dismiss();
                break;
        }
    }


    public void setTitle(String str) {
        mTvTitle.setText(str);
    }

    public void setTitle(int resID) {
        mTvTitle.setText(resID);
    }

    private ConfirmClickListener mListener;

    public interface ConfirmClickListener {
        void clickListener();
    }

    public void setListener(ConfirmClickListener listener) {
        mListener = listener;
    }

}
