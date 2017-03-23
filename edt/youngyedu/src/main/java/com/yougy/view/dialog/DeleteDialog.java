package com.yougy.view.dialog;

import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yougy.common.dialog.BaseDialog;
import com.yougy.ui.activity.R;

/**
 * Created by Administrator on 2016/11/30.
 * 删除 笔记
 */

public class DeleteDialog extends BaseDialog implements View.OnClickListener {
    private Context mContex;
    private Button mBtnSure;
    private Button mBtnCancel;
    private TextView mTvTitle;

    public DeleteDialog(Context context) {
        super(context);
        mContex = context;
    }

    public DeleteDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContex = context;
    }

    @Override
    protected void init() {
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.dialog_delete);
        mBtnSure = (Button) this.findViewById(R.id.btn_sure);
        mBtnSure.setOnClickListener(this);

        mBtnCancel = (Button) this.findViewById(R.id.btn_cancel);
        mBtnCancel.setOnClickListener(this);
        mTvTitle = (TextView) this.findViewById(R.id.tv_title);

    }


    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        Display display = window.getWindowManager().getDefaultDisplay();
        int width = (int) (display.getWidth() * getWidthScale());
        window.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        // window.setGravity(Gravity.CENTER_VERTICAL);
    }

    @Override
    public void onClick(View v) {
        if (isShowing()) {
            dismiss();
        }
        switch (v.getId()) {
            case R.id.btn_sure:
                if (mListener != null) {
                    mListener.onSureClick();
                }
                break;

            case R.id.btn_cancel:
        }
    }

    private SureListener mListener;

    public interface SureListener {
        void onSureClick();
    }

    public void setSureListener(SureListener listener) {
        mListener = listener;
    }

    public void setTitle(String str) {
        mTvTitle.setText(str);
    }

    public void setTitle(int resId) {
        mTvTitle.setText(resId);
    }
}
