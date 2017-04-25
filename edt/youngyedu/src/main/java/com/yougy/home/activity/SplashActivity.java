package com.yougy.home.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.badoo.mobile.util.WeakHandler;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.global.Commons;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.LoginCallBack;
import com.yougy.common.protocol.callback.UpdateCallBack;
import com.yougy.common.protocol.response.ResGetAppVersion;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.ui.activity.R;
import com.yougy.update.DownLoadService;
import com.yougy.update.VersionUtils;
import com.yougy.view.Toaster;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
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

    private WeakHandler mHandler = new WeakHandler();

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
        if (NetUtils.isNetConnected()) {
            getServerVersion();
            /*//重复绑定会失败，用户可以清空APP数据 ，所以每次都进来登录
            callBack = new LoginCallBack(this);
            callBack.setOnJumpListener(this);
            ProtocolManager.loginProtocol(Commons.UUID, ProtocolId.PROTOCOL_ID_LOGIN, callBack);*/
        } else {
            if ("-1".equalsIgnoreCase(SpUtil.getAccountId())) {
                Toaster.showDefaultToast(getApplication(), "当前没有网络请，请设置网络", Toast.LENGTH_LONG);
            } else {
                jumpActivity(MainActivity.class);
            }
        }
    }

    private void login(){
        callBack = new LoginCallBack(SplashActivity.this);
        callBack.setOnJumpListener(SplashActivity.this);
        ProtocolManager.loginProtocol(Commons.UUID, ProtocolId.PROTOCOL_ID_LOGIN, callBack);
    }

    private void getServerVersion(){
        ProtocolManager.getAppVersion(ProtocolId.PROTOCOL_ID_LOGIN, new UpdateCallBack(SplashActivity.this){
            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
            }

            @Override
            public ResGetAppVersion parseNetworkResponse(Response response, int id) throws Exception {
                String backJson = response.body().string();
                LogUtils.i("袁野..升级" + backJson);
                return GsonUtil.fromJson(backJson, ResGetAppVersion.class);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.i("袁野backJson error 升级==" + e.toString());
                //升级接口请求失败时对页面进行跳转。
                login();
            }

            @Override
            public void onResponse(ResGetAppVersion response, int id) {
                if (response != null) {
                    int serverVersion = Integer.parseInt(response.getServerVersion());
                    int localVersion = VersionUtils.getVersionCode(SplashActivity.this);
                    LogUtils.i("袁野 localVersion ==" + localVersion);
                    final String url = response.getAppUrl();
                    if (serverVersion > localVersion && !TextUtils.isEmpty(url)) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SplashActivity.this, DownLoadService.class);
                                intent.putExtra(DownLoadService.DOWNLOAD_URL, url);
                                intent.putExtra(DownLoadService.ISFOCUS_UPDATE, true);
                                SplashActivity.this.startService(intent);
                            }
                        });
                    } else {
                        login();
                    }
                } else {
                    UIUtils.showToastSafe("服务器异常，请退出重试", Toast.LENGTH_SHORT);
                }
            }
        });
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
