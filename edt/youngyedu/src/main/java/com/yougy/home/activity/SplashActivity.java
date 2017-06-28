package com.yougy.home.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.badoo.mobile.util.WeakHandler;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.global.Commons;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.LoginCallBack;
import com.yougy.common.protocol.callback.NewUpdateCallBack;
import com.yougy.common.protocol.request.NewGetAppVersionReq;
import com.yougy.common.protocol.request.NewLoginReq;
import com.yougy.common.protocol.response.NewGetAppVersionRep;
import com.yougy.common.protocol.response.NewLoginRep;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.SystemUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.init.activity.LocalLockActivity;
import com.yougy.init.activity.LoginActivity;
import com.yougy.ui.activity.R;
import com.yougy.update.DownloadManager;
import com.yougy.update.VersionUtils;
import com.yougy.view.Toaster;
import com.yougy.view.dialog.DownProgressDialog;
import com.yougy.view.dialog.HintDialog;
import com.zhy.autolayout.utils.AutoUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by FH on 2016/8/25.
 * <p>
 * APP 第一个页面，欢迎
 */
public class SplashActivity extends BaseActivity implements LoginCallBack.OnJumpListener {
    protected CompositeSubscription subscription;
    protected ConnectableObservable<Object> tapEventEmitter;

    private ImageView mImgLogo;
    private LoginCallBack callBack;
    private int lastProgress;



    private WeakHandler mHandler = new WeakHandler();

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
                    if (((NewLoginRep) o).getCode()== ProtocolId.RET_SUCCESS && ((NewLoginRep) o).getCount()>0) {
                        Log.v("FH", "自动登录成功");
                        SpUtil.saveStudent(((NewLoginRep) o).getData().get(0));
                        if (TextUtils.isEmpty(SpUtil.getLocalLockPwd())){
                            Log.v("FH" , "没有发现localLock的本地储存密码,重置密码并通知用户");
                            new HintDialog(SplashActivity.this
                                    , "我们已为您设置了本机的开机密码为:123456,\n您可以随后在账号设置下进行修改"
                                    , "确定"
                                    , new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    SpUtil.setLocalLockPwd("123456");
                                    loadIntent(LocalLockActivity.class);
                                    finish();
                                }
                            }).show();
                        }
                        else {
                            Log.v("FH" , "发现存在localLock的本地储存密码,跳转到LocalLockActivity");
                            jumpActivity(LocalLockActivity.class);
                        }
                    }
                    else {
                        Log.v("FH", "自动登录失败 , 失败原因:本设备没有被绑定过,跳转到用户名密码登录界面");
                        jumpActivity(LoginActivity.class);
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
                    Log.v("FH" , "自动登录失败原因:网络错误" + ((LoginCallBack.Error) o).e.getMessage());
                    if (!TextUtils.isEmpty(SpUtil.getLocalLockPwd())){
                        Log.v("FH" , "网络错误导致登录失败:本机有之前的本机锁密码,跳转到本地锁界面");
                        jumpActivity(LocalLockActivity.class);
                    }
                    else {
                        Log.v("FH" , "网络错误导致登录失败:本机没有之前的本机锁密码,跳转到用户名密码登录界面");
                        jumpActivity(LoginActivity.class);
                    }
                }
            }
        }));
        subscription.add(tapEventEmitter.connect());
    }


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void init() {
    }

    @Override
    protected void initLayout() {
        mImgLogo = (ImageView) this.findViewById(R.id.img_logo);
    }

    @Override
    protected void loadData() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Commons.UUID == null){
            Log.v("FH", "UUID 为空,重新获取UUID");
            Commons.UUID = SystemUtils.getMacAddress().replaceAll(":" , "");
            SpUtil.saveUUID(Commons.UUID);
        }
        if (Commons.UUID == null){
            ToastUtil.showToast(getApplicationContext() , "获取UUID失败,程序退出");
            finishAll();
        }
        else {
            onCheckLogIn();
        }
    }

    private void onCheckLogIn(){
        if (NetUtils.isNetConnected()) {
            Log.v("FH", "有网络,开始检测版本更新...");
            getServerVersion();
        } else {
            Log.v("FH", "没有网络,跳转到wifi设置");
            jumpWifiActivity();
        }
    }


    private void login() {
        callBack = new LoginCallBack(SplashActivity.this);
        NewLoginReq loginReq = new NewLoginReq();
        loginReq.setDeviceId(Commons.UUID);
        NewProtocolManager.login(loginReq,callBack);
    }

    private void getServerVersion(){
        NewProtocolManager.getAppVersion(new NewGetAppVersionReq(), new NewUpdateCallBack(SplashActivity.this) {
            @Override
            public void onResponse(NewGetAppVersionRep response, int id) {
                super.onResponse(response, id);
                if (response != null && response.getCode() == NewProtocolManager.NewCodeResult.CODE_SUCCESS && response.getData() != null) {
                    try {
                        LogUtils.i(SplashActivity.class.getName() + ":" + response.getData().toString());
                        NewGetAppVersionRep.Data data = response.getData();
                        int serverVersion = Integer.parseInt(data.getVer());
                        int localVersion = VersionUtils.getVersionCode(SplashActivity.this);
                        LogUtils.i("袁野 localVersion ==" + localVersion);
                        final String url = data.getUrl();
                        if (serverVersion > localVersion && !TextUtils.isEmpty(url)) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    doDownLoad(SplashActivity.this, url);
                                }
                            });
                        } else {
                            Log.v("FH", "检测版本成功,没有更新的版本,开始登录...");
                            login();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Log.v("FH", "检测版本失败:" + e.getMessage());
                        login();
                    }
                } else {
                    Log.v("FH", "检测版本失败.");
                    login();
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                login();
            }
        });
    }

    private void jumpWifiActivity() {
        Toaster.showDefaultToast(getApplication(), "当前没有网络请，请设置网络", Toast.LENGTH_LONG);
        Intent intent = new Intent("android.intent.action.WIFI_ENABLE");
        startActivity(intent);
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


    /**
     * 开始执行下载动作
     */
    private void doDownLoad(final Context mContext, final String downloadUrl) {


        final DownProgressDialog downProgressDialog = new DownProgressDialog(mContext);

//        downProgressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        downProgressDialog.show();
        downProgressDialog.setDownProgress("0%");


        // 删除下载的apk文件
        doDeleteDownApk();
        DownloadManager.getInstance().cancelAll();
        DownloadManager.downloadId = DownloadManager.getInstance().add(DownloadManager.getDownLoadRequest(mContext, downloadUrl, new DownloadStatusListenerV1() {
            @Override
            public void onDownloadComplete(DownloadRequest downloadRequest) {

                // 更新进度条显示
                downProgressDialog.setDownProgress("100%");
                downProgressDialog.dismiss();

                // 下载完成，执行安装逻辑
                doInstallApk(mContext);
                // 退出App
                finishAll();
            }

            @Override
            public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                downProgressDialog.setDownProgress("更新失败，重新更新下载");
                // TODO: 2017/4/25
                downProgressDialog.dismiss();
                doDownLoad(mContext, downloadUrl);
            }

            @Override
            public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                if (lastProgress != progress) {
                    lastProgress = progress;
                    String content = downloadedBytes * 100 / totalBytes + "%";
                    downProgressDialog.setDownProgress(content);
                }
            }
        }));
    }


    /**
     * 删除下载的apk文件
     */
    private static void doDeleteDownApk() {
        File file = new File(DownloadManager.getApkPath());
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 执行安装apk文件
     */
    private static void doInstallApk(Context mContext) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(DownloadManager.getApkPath())),
                "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

}
