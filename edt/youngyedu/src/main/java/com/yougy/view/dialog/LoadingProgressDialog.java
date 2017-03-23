package com.yougy.view.dialog;

import android.content.Context;
import android.widget.TextView;

import com.yougy.common.dialog.BaseDialog;
import com.yougy.ui.activity.R;

/**
 * Created by Administrator on 2016/10/10.
 */
public class LoadingProgressDialog extends BaseDialog {
    private TextView mTvMsg;

    public LoadingProgressDialog(Context context) {
        super(context);
    }

    public LoadingProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void init() {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void initLayout() {
//        setContentView(R.layout.dialog_loading_progress);
        setContentView(R.layout.load_layout);
        mTvMsg = (TextView) this.findViewById(R.id.tv_loadingMsg);
    }

    public void setTitle(String title) {
        mTvMsg.setText(title);
    }

    public void setTitle(int resId) {
        mTvMsg.setText(resId);
    }


}
