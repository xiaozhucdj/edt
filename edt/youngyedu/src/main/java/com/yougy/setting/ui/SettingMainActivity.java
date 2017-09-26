package com.yougy.setting.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.global.Commons;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.UnBindCallback;
import com.yougy.common.protocol.request.NewUnBindDeviceReq;
import com.yougy.common.protocol.response.NewUnBindDeviceRep;
import com.yougy.common.service.UploadService;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.init.activity.LoginActivity;
import com.yougy.init.bean.Student;
import com.yougy.message.GlideCircleTransform;
import com.yougy.message.YXClient;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivitySettingBinding;
import com.yougy.view.dialog.ConfirmDialog;

import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;

/**
 * 账号设置界面
 * Created by FH on 2017/6/1.
 */

public class SettingMainActivity extends BaseActivity {
    protected CompositeSubscription subscription;
    protected ConnectableObservable<Object> tapEventEmitter;

    ActivitySettingBinding binding;
    private int mTagNoNet = 1;
    private int mTagUnbindFail = 2;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_setting, null, false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscription = new CompositeSubscription();
        tapEventEmitter = YougyApplicationManager.getRxBus(this).toObserverable().publish();
        handleEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null) {
            subscription.clear();
            subscription = null;
        }
        tapEventEmitter = null;
    }

    protected void handleEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof NewUnBindDeviceRep) {
                    if (((NewUnBindDeviceRep) o).getCode() == ProtocolId.RET_SUCCESS) {
                        Intent intent = new Intent(getApplicationContext(), UploadService.class);
                        startService(intent);
                        SpUtil.clearSP();
                        showCenterDetermineDialog(R.string.unbind_success);
                        YXClient.getInstance().logout();
                    } else {
                        LogUtils.i("unbind fail ..." + getString(R.string.unbind_fail) + ((NewUnBindDeviceRep) o).getMsg());
                        showTagCancelAndDetermineDialog(R.string.unbind_fail, mTagUnbindFail);
                    }
                }
            }
        }));
        subscription.add(tapEventEmitter.connect());
    }

    @Override
    public void init() {
    }

    @Override
    protected void initLayout() {

    }

    @Override
    public void loadData() {
        Student student = SpUtil.getStudent();
        binding.nameTv.setText("姓名 : " + student.getUserRealName());
        binding.schoolTv.setText("学校 : " + student.getSchoolName());
        binding.classTv.setText("班级 : " + student.getClassName());
        binding.numTv.setText("编号 : " + student.getUserNum());
    }

    @Override
    protected void refreshView() {

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                this.finish();
                break;
        }
    }

    public void unBind(View view) {

        new ConfirmDialog(this, "确定解绑该账号吗?", "账号解绑后,存储在本设备上的资料都将遗失,\n请慎重操作"
                , "解绑", "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                unBindRequest();
                dialog.dismiss();
            }
        }).show();
    }

    private void unBindRequest() {
        if (!NetUtils.isNetConnected()) {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagNoNet);
            return;
        }

        NewUnBindDeviceReq unBindDeviceReq = new NewUnBindDeviceReq();
        unBindDeviceReq.setDeviceId(Commons.UUID);
        NewProtocolManager.unbindDevice(unBindDeviceReq, new UnBindCallback(SettingMainActivity.this));
    }

    public void changePwd(View view) {
        new ChangePwdDialog(this).show();
    }


    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        if (mTagNoNet == mUiPromptDialog.getTag()) {
            jumpTonet();

        } else if (mTagUnbindFail == mUiPromptDialog.getTag()) {
            unBindRequest();
        }
    }

    @Override
    public void onUiCenterDetermineListener() {
        super.onUiCenterDetermineListener();
        finishAll();
        loadIntent(LoginActivity.class);
    }
}
