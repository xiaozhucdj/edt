package com.yougy.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.yougy.common.dialog.BaseDialog;
import com.yougy.common.utils.UIUtils;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ConfirmDialogLayoutBinding;

/**
 * Created by FH on 2017/02/17.
 */
public class ConfirmDialog extends BaseDialog {
    ConfirmDialogLayoutBinding binding;
    String titleText;
    String contentText;
    String cancleBtnText;
    String confirmBtnText;
    Context mContext;
    Dialog.OnClickListener confirmBtnListener;
    Dialog.OnClickListener cancleBtnListener;

    public ConfirmDialog(Context context, String titleText , String contentText , String confirmBtnText
            , String cancleBtnText , Dialog.OnClickListener confirmBtnListener , Dialog.OnClickListener cancleBtnListener) {
        super(context);
        mContext = context;
        this.titleText = titleText;
        this.contentText = contentText;
        this.confirmBtnText = confirmBtnText;
        this.cancleBtnText = cancleBtnText;
        this.confirmBtnListener = confirmBtnListener;
        this.cancleBtnListener = cancleBtnListener;
    }
    public ConfirmDialog(Context context, String titleText , String contentText , String confirmBtnText
            , String cancleBtnText , Dialog.OnClickListener confirmBtnListener) {
        this(context, titleText, contentText, confirmBtnText, cancleBtnText , confirmBtnListener , null);
    }

    public ConfirmDialog(Context context, String contentText , Dialog.OnClickListener confirmBtnListener) {
        this(context , null , contentText , null , null , confirmBtnListener , null);
    }

    public ConfirmDialog(Context context, String titleText , String contentText , Dialog.OnClickListener confirmBtnListener) {
        this(context , titleText , contentText , null , null , confirmBtnListener , null);
    }

    public ConfirmDialog(Context context, String contentText , Dialog.OnClickListener confirmBtnListener , String confirmBtnText) {
        this(context , null , contentText , confirmBtnText , null , confirmBtnListener);
    }

    public ConfirmDialog(Context context, String contentText , String confirmBtnText
            , Dialog.OnClickListener confirmBtnListener , String cancleBtnText , OnClickListener cancleBtnListener) {
        this(context , null , contentText , confirmBtnText , cancleBtnText , confirmBtnListener , cancleBtnListener);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initLayout() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext) , R.layout.confirm_dialog_layout , null , false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
        if (TextUtils.isEmpty(titleText)){
            binding.titleTv.setVisibility(View.GONE);
        }
        else {
            binding.titleTv.setText(titleText);
        }
        if (TextUtils.isEmpty(contentText)){
            binding.contentTv.setText("提示");
        }
        else {
            binding.contentTv.setText(contentText);
        }
        if (TextUtils.isEmpty(confirmBtnText)){
            binding.confirmBtn.setText("确定");
        }
        else {
            binding.confirmBtn.setText(confirmBtnText);
        }
        if (TextUtils.isEmpty(cancleBtnText)){
            binding.cancleBtn.setText("取消");
        }
        else {
            binding.cancleBtn.setText(cancleBtnText);
        }
        binding.cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancleBtnListener != null){
                    cancleBtnListener.onClick(ConfirmDialog.this , 0);
                }
                else {
                    dismiss();
                }
            }
        });
        binding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmBtnListener != null) {
                    confirmBtnListener.onClick(ConfirmDialog.this, 0);
                } else {
                    dismiss();
                }
            }
        });
    }
}