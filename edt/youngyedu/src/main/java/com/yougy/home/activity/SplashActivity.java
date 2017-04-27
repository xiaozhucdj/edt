package com.yougy.home.activity;

import android.widget.ImageView;
import android.widget.Toast;

import com.yougy.common.activity.BaseActivity;
import com.yougy.common.global.Commons;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.LoginCallBack;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.ui.activity.R;
import com.yougy.view.Toaster;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Administrator on 2016/8/25.
 * <p>
 * APP 第一个页面，欢迎
 */
public class SplashActivity extends BaseActivity implements LoginCallBack.OnJumpListener {
    private ImageView mImgLogo;
    private LoginCallBack callBack;

    @Override
    protected void init() {
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_splash);
        mImgLogo = (ImageView) this.findViewById(R.id.img_logo);
    }

    @Override
    protected void loadData() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        onCheckLogIn();
    }

    private void onCheckLogIn(){
        if (NetUtils.isNetConnected()) {
            //重复绑定会失败，用户可以清空APP数据 ，所以每次都进来登录
            callBack = new LoginCallBack(this);
            callBack.setOnJumpListener(this);
            ProtocolManager.loginProtocol(Commons.UUID, ProtocolId.PROTOCOL_ID_LOGIN, callBack);
        } else {
            if ("-1".equalsIgnoreCase(SpUtil.getAccountId())) {
                Toaster.showDefaultToast(getApplication(), "当前没有网络请，请设置网络", Toast.LENGTH_LONG);
                //跳转到设置页面
            } else {
                jumpActivity(MainActivity.class);
            }
        }

    }

    @Override
    protected void refreshView() {
    }

    @Override
    public void jumpActivity(final Class clazz) {
        Observable.timer(2000, TimeUnit.MILLISECONDS).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                if (callBack != null) {
                    callBack.hideLoadingDialog();
                }
                loadIntent(SplashActivity.this, clazz);
                SplashActivity.this.finish();
            }
        });
    }
}
