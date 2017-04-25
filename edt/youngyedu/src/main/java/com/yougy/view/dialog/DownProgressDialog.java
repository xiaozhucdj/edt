package com.yougy.view.dialog;

import android.content.Context;
import android.widget.TextView;

import com.yougy.common.dialog.BaseDialog;
import com.yougy.ui.activity.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by FH on 2017/02/17.
 */
public class DownProgressDialog extends BaseDialog {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_content)
    TextView tvContent;


    public DownProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void init() {

        // 用户不可以点击外部消失对话框
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.dialog_update);
        ButterKnife.bind(this);
    }

    public void setDownProgress(String progress) {
        tvContent.setText(progress);
    }

}
