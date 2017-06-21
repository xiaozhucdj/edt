package com.yougy.view.dialog;

import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
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
        mTvTitle = (TextView) this.findViewById(R.id.tv_title);

        mBtnCancel = (Button) this.findViewById(R.id.btn_cancel);
        mBtnCancel.setOnClickListener(this);

        mBtnConfirm = (Button) this.findViewById(R.id.btn_confirm);
        mBtnConfirm.setOnClickListener(this);
    }

    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        Display display = window.getWindowManager().getDefaultDisplay();
        int width = (int) (display.getWidth() * getWidthScale());
        window.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
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
        }
    }

    private Listener mListener;

    public interface Listener {
        void onUiCancelListener();

        void onUiDetermineListener();
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
}
