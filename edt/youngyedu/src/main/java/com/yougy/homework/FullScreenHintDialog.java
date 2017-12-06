package com.yougy.homework;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.ToastUtil;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.FullScreenHintDialogBinding;

/**
 * Created by FH on 2017/11/27.
 * 全屏的提示Dialog
 */

public class FullScreenHintDialog extends Dialog {
    private FullScreenHintDialogBinding binding;
    @DrawableRes
    private int iconResId;
    private String contentText;
    private String btn1Text;
    private String btn2Text;
    private OnClickListener btn1OnClickListener , btn2OnClickListener;
    private OnClickListener nextStepOnclickListener;
    private boolean showNoMoreAgainHint = true;
    private String tag;


    public FullScreenHintDialog(Context context , String tag) {
        super(context);
        this.tag = tag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();
    }

    protected void init() {
    }

    protected void initLayout() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()) , R.layout.full_screen_hint_dialog , new FrameLayout(getContext()), false);
        //去除标题栏
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //去除标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        //以下两句必须在setContentView之后调用
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        if (iconResId > 0){
            binding.icon.setBackgroundResource(iconResId);
        }
        if (!TextUtils.isEmpty(contentText)){
            binding.contentTv.setText(contentText);
        }
        if (!TextUtils.isEmpty(btn1Text)){
            binding.btn1.setText(btn1Text);
        }
        binding.btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn1OnClickListener == null){
                    dismiss();
                }
                else {
                    btn1OnClickListener.onClick(FullScreenHintDialog.this , 0);
                }
            }
        });
        if (!TextUtils.isEmpty(btn2Text)){
            binding.btn2.setVisibility(View.VISIBLE);
            binding.btn2.setText(btn2Text);
            binding.btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btn2OnClickListener == null){
                        dismiss();
                    }
                    else {
                        btn2OnClickListener.onClick(FullScreenHintDialog.this , 0);
                    }
                }
            });
        }
        binding.checkbox.setSelected(SpUtil.isThisDialogNotShowAgain(tag));
        binding.notAgainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.checkbox.setSelected(!binding.checkbox.isSelected());
                SpUtil.setNotSHowAgainDialogTag(tag , binding.checkbox.isSelected());
            }
        });
        if (!showNoMoreAgainHint){
            binding.notAgainLayout.setVisibility(View.GONE);
        }
    }

    public FullScreenHintDialog setContentText(String contentText) {
        this.contentText = contentText;
        return this;
    }

    public String getContentText() {
        return contentText;
    }

    public String getBtn1Text() {
        return btn1Text;
    }

    public FullScreenHintDialog setBtn1(String btn1Text , OnClickListener onClickListener , boolean isNextStep) {
        this.btn1Text = btn1Text;
        this.btn1OnClickListener = onClickListener;
        if (isNextStep){
            nextStepOnclickListener = onClickListener;
        }
        return this;
    }

    public String getBtn2Text() {
        return btn2Text;
    }

    public FullScreenHintDialog setBtn2(String btn2Text , OnClickListener onClickListener , boolean isNextStep) {
        this.btn2Text = btn2Text;
        this.btn2OnClickListener = onClickListener;
        if (isNextStep){
            nextStepOnclickListener = onClickListener;
        }
        return this;
    }

    public boolean isShowNoMoreAgainHint() {
        return showNoMoreAgainHint;
    }

    public FullScreenHintDialog setShowNoMoreAgainHint(boolean showNoMoreAgainHint) {
        this.showNoMoreAgainHint = showNoMoreAgainHint;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public FullScreenHintDialog setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public int getIconResId() {
        return iconResId;
    }

    public FullScreenHintDialog setIconResId(@DrawableRes int iconResId) {
        this.iconResId = iconResId;
        return this;
    }

    @Override
    public void show() {
        if (SpUtil.isThisDialogNotShowAgain(tag)){
            if (nextStepOnclickListener != null){
                nextStepOnclickListener.onClick(this  , 0);
            }
            else {
                ToastUtil.showToast(getContext() , "本Dialog被设定成不再提示,但是没有设置下一步的操作");
            }
        }
        else {
            super.show();
        }
    }
}
