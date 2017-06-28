package com.yougy.init.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;


import com.yougy.common.activity.BaseActivity;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.LoginCallBack;
import com.yougy.common.protocol.request.NewLoginReq;
import com.yougy.common.protocol.response.NewLoginRep;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.init.dialog.ConfirmUserInfoDialog;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityLoginBinding;
import com.yougy.view.dialog.HintDialog;

import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by FH on 2017/6/22.
 */

public class LoginActivity extends BaseActivity {
    protected CompositeSubscription subscription;
    protected ConnectableObservable<Object> tapEventEmitter;

    ActivityLoginBinding binding;
    ConfirmUserInfoDialog confirmUserInfoDialog;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this) , R.layout.activity_login , null , false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    @Override
    public void init() {

    }

    @Override
    protected void initLayout() {

    }

    @Override
    public void loadData() {
    }

    @Override
    protected void refreshView() {

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
                if (o instanceof NewLoginRep){
                    NewLoginRep response = (NewLoginRep) o;
                    if (response.getCode() == ProtocolId.RET_SUCCESS && response.getCount()>0) {
                        Log.v("FH", "登录成功,弹出信息确认dialog");
                        confirmUserInfoDialog = new ConfirmUserInfoDialog(LoginActivity.this , response.getData().get(0));
                        confirmUserInfoDialog.show();
                    }
                    else if (response.getCode() == 401){
                        ToastUtil.showToast(getApplicationContext() , "登录失败:用户名密码错误");
                    }
                }
                else if (o instanceof LoginCallBack.Error){
                    if (!((LoginCallBack.Error) o).mUiPromptDialog.isShowing()) {
                        ((LoginCallBack.Error) o).mUiPromptDialog.show();
                        ((LoginCallBack.Error) o).mUiPromptDialog.setDialogStyle(false);
                        ((LoginCallBack.Error) o).mUiPromptDialog.setCancel(R.string.cancel);
                        ((LoginCallBack.Error) o).mUiPromptDialog.setConfirm(R.string.retry);
                        ((LoginCallBack.Error) o).mUiPromptDialog.setTitle(R.string.text_connect_timeout);
                    }
                }
            }
        }));
        subscription.add(tapEventEmitter.connect());
    }

    public void login(View view){
        if (TextUtils.isEmpty(binding.accountEdittext.getText())){
            ToastUtil.showToast(getApplicationContext() , "乐课账号不能为空");
            return;
        }
        if (TextUtils.isEmpty(binding.pwdEdittext.getText())){
            ToastUtil.showToast(getApplicationContext() , "乐课密码不能为空");
            return;
        }
        NewLoginReq loginReq = new NewLoginReq();
        loginReq.setUserName(binding.accountEdittext.getText().toString());
        loginReq.setUserPassword(binding.pwdEdittext.getText().toString());
        NewProtocolManager.login(loginReq,new LoginCallBack(this));
    }

    public void forgetPwd(View view){
        new HintDialog(this, "请联系管理员请求重置你的乐课账户密码,重置成功后请重新登录即可").show();
    }
}
