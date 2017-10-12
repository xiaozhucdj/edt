package com.yougy.view.dialog;

import android.content.Context;
import android.view.View;

import com.yougy.common.dialog.BaseDialog;
import com.yougy.ui.activity.R;

/**
 * Created by Administrator on 2017/9/29.
 */

public abstract class OpenBookErrorDialog  extends BaseDialog {

    public OpenBookErrorDialog(Context context) {
        super(context);
//        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public OpenBookErrorDialog(Context context, int themeResId) {
        super(context, themeResId);
//        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }


    @Override
    protected void init() {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.dialog_error);
        this.findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deltedeBook();
            }
        });
    }

    public abstract void deltedeBook();

}

