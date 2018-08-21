package com.yougy.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import com.yougy.common.dialog.BaseDialog;
import com.yougy.common.utils.LogUtils;
import com.yougy.ui.activity.R;

/**
 * Created by Administrator on 2016/10/10.
 */
public class LoadingProgressDialog extends BaseDialog {
    private TextView mTvMsg;

    public LoadingProgressDialog(Context context) {
        super(context);
//        setOwnerActivity((Activity) context);
    }

    public LoadingProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
//        setOwnerActivity((Activity) context);
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
        mTvMsg = this.findViewById(R.id.tv_loadingMsg);
    }

    public void setTitle(String title) {
        mTvMsg.setText(title);
    }

    public void setTitle(int resId) {
        mTvMsg.setText(resId);
    }


/*    @Override
    public void show() {
        super.show();
        LogUtils.e(getClass().getName(),"show loading progress dialog............");
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.TOP;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(layoutParams);
    }*/
}
