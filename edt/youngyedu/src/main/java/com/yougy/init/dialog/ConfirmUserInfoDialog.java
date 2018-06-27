package com.yougy.init.dialog;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;

import com.yougy.common.dialog.BaseDialog;
import com.yougy.common.global.Commons;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.protocol.request.NewBindDeviceReq;
import com.yougy.common.service.DownloadService;
import com.yougy.common.utils.AliyunUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.activity.MainActivity;
import com.yougy.init.bean.Student;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ConfirmUserinfoDialogLayoutBinding;
import com.yougy.view.dialog.HintDialog;

import java.io.File;

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
                , R.layout.confirm_userinfo_dialog_layout, null, false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
        AliyunUtil.DATABASE_NAME = student.getUserId() + ".db";
        binding.nameTv.setText("姓名 : " + student.getUserRealName());
        binding.schoolTv.setText("学校 : " + student.getSchoolName());
        binding.classTv.setText("班级 : " + student.getClassName());
        binding.numTv.setText("编号 : " + student.getUserNum());
        SpannableString spannableString = new SpannableString("我们为您设置了本机的开机密码为:123456,\n您可以随后在账号设置下进行修改");
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD_ITALIC);
        spannableString.setSpan(styleSpan, 16, 22, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
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
            }
        }));
        subscription.add(tapEventEmitter.connect());
    }

    public void confirm() {
        NewBindDeviceReq deviceReq = new NewBindDeviceReq();
        deviceReq.setDeviceId(Commons.UUID);
        deviceReq.setUserId(student.getUserId());
        NetWorkManager.bindDevice(student.getUserId(), Commons.UUID)
                .subscribe(o -> {
                    LogUtils.e("FH", "绑定成功");
                    SpUtils.saveStudent(student);
                    File file = new File(ConfirmUserInfoDialog.this.getContext().getDatabasePath(student.getUserId() + ".db").getAbsolutePath());
                    if (!file.exists()) {
                        ConfirmUserInfoDialog.this.getContext().startService(new Intent(ConfirmUserInfoDialog.this.getContext(), DownloadService.class));
                    }
                    LogUtils.e("FH", "云信SDK登录成功 , 重置本机锁密码并提示");
                    binding.confirmBtn.setVisibility(View.GONE);
                    binding.cancleBtn.setVisibility(View.GONE);
                    binding.localPwdHintTv.setVisibility(View.VISIBLE);
                    binding.startUseBtn.setVisibility(View.VISIBLE);
                    binding.titleTv.setText("恭喜,用户与设备绑定成功");
                    SpUtils.setLocalLockPwd("123456");
                }, throwable -> {
                    ConfirmUserInfoDialog.this.dismiss();
                    new HintDialog(mActivity, "绑定失败 : 可能是设备已经被绑定过").show();
                });
    }


    public void startUse() {
        dismiss();
        mActivity.startActivity(new Intent(mActivity, MainActivity.class));
        mActivity.finish();
    }

    @Override
    public void show() {
        super.show();
        subscription = new CompositeSubscription();
        tapEventEmitter = YoungyApplicationManager.getRxBus(mActivity).toObserverable().publish();
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
