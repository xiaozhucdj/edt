package com.yougy.view.dialog;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.yougy.common.dialog.BaseDialog;
import com.yougy.common.utils.StringUtils;
import com.yougy.ui.activity.R;


/**
 * Created by Administrator on 2018/8/24.
 */

public class AppUpdateDialog extends BaseDialog implements View.OnClickListener {

    private Button mBtnCancel;
    private TextView mTvContent;
    private Button mBtnConfirm;

    private AppUpdateClicklListener mListener;

    public void setCliclListener(AppUpdateClicklListener listener) {
        mListener = listener;
    }

    public void isShowcCancel(boolean isShow){
        mBtnCancel.setVisibility(isShow == true ?View.VISIBLE :View.GONE);
    }


    public AppUpdateDialog(Context context) {
        super(context);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public AppUpdateDialog(Context context, int themeResId) {
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
        setContentView(R.layout.dialog_app_update);
        mBtnCancel = (Button) this.findViewById(R.id.cancle_btn);
        mBtnCancel.setOnClickListener(this);
        mBtnConfirm = (Button) this.findViewById(R.id.confirm_btn);
        mBtnConfirm.setOnClickListener(this);
        mTvContent = (TextView) this.findViewById(R.id.content_tv);
    }

    public void setContent(String str) {
        if (!StringUtils.isEmpty(str)) {
            mTvContent.setText(str);
        }
    }

    @Override
    public void onClick(View view) {
        if (mListener == null) {
            return;
        }

        switch (view.getId()) {
            case R.id.cancle_btn:
                mListener.cancelListener();
                break;

            case R.id.confirm_btn:
                mListener.confirmListener();
                break;
        }
    }

    public interface AppUpdateClicklListener {
        void cancelListener();
        void confirmListener();
    }
}
