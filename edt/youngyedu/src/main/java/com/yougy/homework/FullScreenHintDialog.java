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

    /**
     * 本dialog是全屏的dialog,主要用于弹出确认提示.
     * 支持最多两个按钮,一个icon,一行提示文字,和"下次不再弹出"的checkbox
     * 下次不再弹出的功能已经集成,只用在构造函数中传入tag,并且在setBtn的时候设定nextStep,就可在自动完成下次不再弹出的功能.
     * @param context
     * @param tag 设定一个tag,tag相同的FullScreenHintDialog视为同一类Dialog,同一类的dialog再勾选下次不再显示只有则不再弹出,直接执行isNextStep被设为true的OnClickListener.
     */
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

    /**
     * 设定btn1(左按钮)的文字,点击逻辑,并且规定是否在勾选了不在弹出本dialog后下次自动执行btn1的onClick逻辑
     * 如果btn1和btn2的isNextStep都被置为true,则以后置的btn的onClick为准
     *
     * @param btn1Text
     * @param onClickListener
     * @param isNextStep 如果为true,并且勾选了下次不再提示勾选框,下次就不再弹出dialog,而是直接执行btn1的onClick逻辑
     * @return
     */
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

    /**
     * 设定btn2(右按钮)的文字,点击逻辑,并且规定是否在勾选了不在弹出本dialog后下次自动执行btn2的onClick逻辑
     * 如果btn1和btn2的isNextStep都被置为true,则以后置的btn的onClick为准
     * 如果setBtn2没有被调用,则dialog上只会存在一个btn1按钮
     *
     * @param btn2Text
     * @param onClickListener
     * @param isNextStep 如果为true,并且勾选了下次不再提示勾选框,下次就不再弹出dialog,而是直接执行btn2的onClick逻辑
     * @return
     */
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
