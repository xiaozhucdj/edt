package com.yougy.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.yougy.common.dialog.BaseDialog;
import com.yougy.ui.activity.R;

/**
 * Created by jiangliang on 2016/10/24.
 */

public class LoadingErrorDialog extends BaseDialog implements View.OnClickListener {
    private FrameLayout mFrameLayout;
    private OnScreenClickListenr listenr;

    public LoadingErrorDialog(Context context) {
        super(context);
    }

    public LoadingErrorDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void init() {
//        setCancelable(false);
//        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.load_error_layout);
        mFrameLayout = findViewById(R.id.load_error);
        mFrameLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listenr != null) {
            listenr.onClick();
        }

    }

    public void setOnScreenClickListener(OnScreenClickListenr listener) {
        this.listenr = listener;
    }

    public interface OnScreenClickListenr {
        void onClick();
    }


}
