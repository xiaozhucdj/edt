package com.yougy.init.dialog;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.yougy.common.dialog.BaseDialog;
import com.yougy.common.global.Commons;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.BindCallBack;
import com.yougy.common.protocol.request.NewBindDeviceReq;
import com.yougy.common.protocol.response.NewBindDeviceRep;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.activity.MainActivity;
import com.yougy.init.bean.Student;
import com.yougy.message.YXClient;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ConfirmUserinfoDialogLayoutBinding;
import com.yougy.view.dialog.HintDialog;

import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by FH on 2017/6/26.
 */

public class ConfirmUserInfoDialog extends BaseDialog {
    protected CompositeSubscription subscription;
    protected ConnectableObservable<Object> tapEventEmitter;

    Activity mActivity;
    Student student;
    ConfirmUserinfoDialogLayoutBinding binding;
    public ConfirmUserInfoDialog(Activity activity, Student student) {
        super(activity);
        mActivity = activity;
        this.student = student;
    }

    @Override
    protected void init() {

    }
    @Override
    protected void initLayout() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mActivity)
                , R.layout.confirm_userinfo_dialog_layout , null , false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
        binding.nameTv.setText("姓名 : " + student.getUserRealName());
        binding.schoolTv.setText("学校 : " + student.getSchoolName());
        binding.classTv.setText("班级 : " + student.getClassName());
        binding.numTv.setText("编号 : " + student.getUserNum());
        SpannableString spannableString = new SpannableString("我们为您设置了本机的开机密码为:123456,\n您可以随后在账号设置下进行修改");
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD_ITALIC);
        spannableString.setSpan(styleSpan , 16 , 22 , Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        binding.localPwdHintTv.setText(spannableString);
        binding.cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        binding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
        binding.startUseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUse();
            }
        });
    }

    protected void handleEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof NewBindDeviceRep) {
                    if (((NewBindDeviceRep) o).getCode() == ProtocolId.RET_SUCCESS){
                        Log.v("FH" , "绑定成功,开始登录云信SDK");
                        YXClient.getInstance().getTokenAndLogin(String.valueOf(SpUtil.getUserId()), new RequestCallbackWrapper() {
                            @Override
                            public void onResult(int code, Object result, Throwable exception) {
                                if (code != ResponseCode.RES_SUCCESS){
                                    Log.v("FH" , "云信SDK登录失败 : code : " + code);
                                    new HintDialog(mActivity , "云信SDK登录失败 : code : " + code).show();
                                }
                                else {
                                    Log.v("FH" , "云信SDK登录成功 , 重置本机锁密码并提示");
                                    binding.confirmBtn.setVisibility(View.GONE);
                                    binding.cancleBtn.setVisibility(View.GONE);
                                    binding.localPwdHintTv.setVisibility(View.VISIBLE);
                                    binding.startUseBtn.setVisibility(View.VISIBLE);
                                    binding.titleTv.setText("恭喜,用户与设备绑定成功");
                                    SpUtil.setLocalLockPwd("123456");
                                    SpUtil.saveStudent(student);
                                }
                            }
                        });
                    }
                    else {
                        Log.v("FH" , "绑定失败 : " + ((NewBindDeviceRep) o).getMsg());
                        new HintDialog(mActivity , "绑定失败 : " + ((NewBindDeviceRep) o).getMsg()).show();
                    }
                }
            }
        }));
        subscription.add(tapEventEmitter.connect());
    }

    public void confirm(){
        NewBindDeviceReq deviceReq = new NewBindDeviceReq();
        deviceReq.setDeviceId(Commons.UUID);
        deviceReq.setUserId(student.getUserId());
        NewProtocolManager.bindDevice(deviceReq,new BindCallBack(mActivity));
    }
    public void startUse (){
        dismiss();
        mActivity.startActivity(new Intent(mActivity , MainActivity.class));
        mActivity.finish();
    }
    @Override
    public void show() {
        super.show();
        subscription = new CompositeSubscription();
        tapEventEmitter = YougyApplicationManager.getRxBus(mActivity).toObserverable().publish();
        handleEvent();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (subscription != null) {
            subscription.clear();
            subscription = null;
        }
        tapEventEmitter = null;
    }
}
