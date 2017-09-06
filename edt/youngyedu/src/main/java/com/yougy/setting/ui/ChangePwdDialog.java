package com.yougy.setting.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.yougy.common.dialog.BaseDialog;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.DialogChangePwdBinding;
import com.yougy.view.dialog.HintDialog;

/**
 * Created by FH on 2017/6/27.
 * 修改密码对话框
 */

public class ChangePwdDialog extends BaseDialog {
    Context mContext;
    DialogChangePwdBinding binding;
    public ChangePwdDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initLayout() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext) , R.layout.dialog_change_pwd , null , false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
        binding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePwd();
            }
        });
        binding.cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void changePwd(){
        if (TextUtils.isEmpty(binding.oldPwdEdtv.getText().toString())){
            new HintDialog(mContext , "旧密码不能为空").show();
        }
        else if(!isLengthLegal(binding.oldPwdEdtv.getText().toString().length())){
            new HintDialog(mContext , "旧密码长度不正确").show();
        }
        else if (TextUtils.isEmpty(binding.newPwdEdtv.getText().toString())){
            new HintDialog(mContext , "新密码不能为空").show();
        }
        else if(!isLengthLegal(binding.newPwdEdtv.getText().toString().length())){
            new HintDialog(mContext , "新密码长度不正确").show();
        }
        else if (TextUtils.isEmpty(binding.againNewPwdEdtv.getText().toString())){
            new HintDialog(mContext , "确认新密码不能为空").show();
        }
        else if (!binding.newPwdEdtv.getText().toString().equals(binding.againNewPwdEdtv.getText().toString())){
            new HintDialog(mContext , "您两次输入的新密码不一致,请输入一致的新密码").show();
        }
        else if (!SpUtil.getLocalLockPwd().equals(binding.oldPwdEdtv.getText().toString())){
            new HintDialog(mContext , "您输入的旧密码不正确").show();
        }
        else {
            SpUtil.setLocalLockPwd(binding.newPwdEdtv.getText().toString());
            dismiss();
            new HintDialog(mContext , "修改成功!").show();
        }
    }

    public boolean isLengthLegal(int length){
        if (length > 16 || length < 6){
            return false;
        }
        return true;
    }


}
