package com.yougy.view.dialog;

import android.content.Context;
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
public class HintDialog extends BaseDialog {
    String msg;
    @BindView(R.id.hint_dialog_tv)
    TextView titleTv;
    @BindView(R.id.hint_dialog_confirm_btn)
    Button confirmBtn;

    String confirmBtnText;

    public HintDialog(Context context , String msg){
        this(context , msg , "确定" , null);
    }

    public HintDialog(Context context, String msg , String confirmBtnText , OnDismissListener onDismissListener) {
        super(context);
        this.msg = msg;
        this.confirmBtnText = confirmBtnText;
        if (onDismissListener != null){
            setOnDismissListener(onDismissListener);
        }
    }

    @Override
    protected void init() {}

    @Override
    protected void initLayout() {
        setContentView(R.layout.hint_dialog_layout);
        ButterKnife.bind(this);
        titleTv.setText(msg);
        confirmBtn.setText(confirmBtnText);
    }

    @OnClick({R.id.hint_dialog_confirm_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.hint_dialog_confirm_btn:
                dismiss();
                break;
        }
    }

    public void changeMsg(String msg){
        this.msg = msg;
        titleTv.setText(msg);
    }

    public String getMsg(){
        return msg;
    }

    public HintDialog setMsg(String msg) {
        this.msg = msg;
        titleTv.setText(msg);
        return this;
    }
}
