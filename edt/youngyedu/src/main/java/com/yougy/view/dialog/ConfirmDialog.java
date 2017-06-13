package com.yougy.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yougy.common.dialog.BaseDialog;
import com.yougy.ui.activity.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by FH on 2017/02/17.
 */
public class ConfirmDialog extends BaseDialog {
    String msg;
    @BindView(R.id.confirm_dialog_tv)
    TextView titleTv;
    @BindView(R.id.confirm_dialog_confirm_btn)
    Button confirmBtn;
    @BindView(R.id.confirm_dialog_cancle_btn)
    Button cancleBtn;
    String cancleBtnText;
    String confirmBtnText;

    Dialog.OnClickListener confirmBtnListener;

    public ConfirmDialog(Context context, String msg , Dialog.OnClickListener confirmBtnListener) {
        super(context);
        this.msg = msg;
        this.confirmBtnListener = confirmBtnListener;
    }

    @Override
    protected void init() {
    }

    public ConfirmDialog setCancleBtnText(String cancleBtnText) {
        this.cancleBtnText = cancleBtnText;
        return this;
    }

    public ConfirmDialog setConfirmBtnText(String confirmBtnText) {
        this.confirmBtnText = confirmBtnText;
        return this;
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.confirm_dialog_layout);
        ButterKnife.bind(this);
        if (!TextUtils.isEmpty(msg))titleTv.setText(msg);
        if (!TextUtils.isEmpty(cancleBtnText))cancleBtn.setText(cancleBtnText);
        if (!TextUtils.isEmpty(confirmBtnText))confirmBtn.setText(confirmBtnText);
    }

    @OnClick({R.id.confirm_dialog_confirm_btn, R.id.confirm_dialog_cancle_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm_dialog_confirm_btn:
                if (confirmBtnListener != null){
                    confirmBtnListener.onClick(this , 0);
                }
                break;
            case R.id.confirm_dialog_cancle_btn:
                dismiss();
                break;
        }
    }
}
