package com.yougy.home.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.badoo.mobile.util.WeakHandler;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.global.Commons;
import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.LoginCallBack;
import com.yougy.common.protocol.callback.NewUpdateCallBack;
import com.yougy.common.protocol.request.NewGetAppVersionReq;
import com.yougy.common.protocol.request.NewLoginReq;
import com.yougy.common.protocol.response.NewGetAppVersionRep;
import com.yougy.common.protocol.response.NewLoginRep;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.init.activity.LocalLockActivity;
import com.yougy.init.activity.LoginActivity;
import com.yougy.init.bean.Student;
import com.yougy.message.YXClient;
import com.yougy.ui.activity.R;
import com.yougy.update.DownloadManager;
import com.yougy.update.VersionUtils;
import com.yougy.view.dialog.ConfirmDialog;
import com.yougy.view.dialog.DownProgressDialog;
import com.yougy.view.dialog.HintDialog;

import org.litepal.LitePal;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Request;
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
    private int lastProgress;

    private WeakHandler mHandler = new WeakHandler();
//    private PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LitePal.getDatabase();
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
        subscription.add(tapEventEmitter.connect());
    }


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void init() {


        //打开背光 一共18等级  减到0，打开默认为7级，直接关闭，打开默认为之前关闭是级数。

/*
        //保存打开app前的背光情况。
        boolean isLightOn = FrontLightController.isLightOn(this);
        int nowBrightness = FrontLightController.getBrightness(this);
        SharedPreferencesUtil.getSpUtil().putBoolean("OLD_ISLIGHTON", isLightOn);
        SharedPreferencesUtil.getSpUtil().putInt("OLD_BRIGHTNESS", nowBrightness);

        FrontLightController.turnOn(this);

        //展示设备亮度为10/16
        FrontLightController.setBrightness(this, 160*10/16);*/


        /*//获取当前亮级
        int now = FrontLightController.getBrightness(this);
        //获取可展示级别  0-160
        List<Integer> list = FrontLightController.getFrontLightValueList(this);
        Collections.reverse(list);

        for (Integer target : list) {
            if (target < now) {
                //设置背光亮度级别
                FrontLightController.setBrightness(this, target);
                return;
            }
        }*/
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
        onCheckLogIn();
    }

    private void onCheckLogIn() {
      LogUtils.e("FH", "开始检测版本更新...");
        if (NetUtils.isNetConnected()) {
          LogUtils.e("FH", "有网络,更新UUID");
            Commons.UUID = NetworkUtil.getMacAddress(this).replaceAll(":" , "") ;
            SpUtils.saveUUID(Commons.UUID);
            getServerVersion();
        } else {
            if (-1 == SpUtils.getAccountId()) {
              LogUtils.e("FH", "没有网络,没有之前的登录信息,跳转到wifi设置");
                jumpWifiActivity();
            } else {
              LogUtils.e("FH", "没有网络,有之前的登录信息");
                checkLocalLockAndJump();
            }
        }
    }


    private void checkLocalLockAndJump() {
        if (TextUtils.isEmpty(SpUtils.getLocalLockPwd())) {
          LogUtils.e("FH", "没有发现localLock的本地储存密码,重置密码并通知用户");
            new HintDialog(SplashActivity.this
                    , "我们已为您设置了本机的开机密码为:123456,\n您可以随后在账号设置下进行修改"
                    , "确定"
                    , new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    SpUtils.setLocalLockPwd("123456");
                    loadIntent(LocalLockActivity.class);
                    finish();
                }
            }).show();
        } else {
          LogUtils.e("FH", "发现存在localLock的本地储存密码,跳转到LocalLockActivity");
            jumpActivity(LocalLockActivity.class);
        }
    }

    private void login() {
        NewLoginReq loginReq = new NewLoginReq();
        loginReq.setDeviceId(Commons.UUID);
        NewProtocolManager.login(loginReq, new LoginCallBack(this,loginReq) {
            @Override
            public void onBefore(Request request, int id) {
            }

            @Override
            public void onAfter(int id) {
            }

            @Override
            public void onResponse(NewLoginRep response, int id) {
                if (response.getCode() == ProtocolId.RET_SUCCESS && response.getCount() > 0) {
                    Student student = response.getData().get(0);
                    if (!student.getUserRole().equals("学生")) {
                        new HintDialog(getThisActivity(), "权限错误:本设备已被其他账号绑定过,请先解绑后重新登录", "退出程序", new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                finishAll();
                            }
                        }).show();
                    } else {
                      LogUtils.e("FH", "自动登录成功");
                        SpUtils.saveStudent(response.getData().get(0));
                        YXClient.getInstance().getTokenAndLogin(String.valueOf(SpUtils.getUserId()), null);
                        checkLocalLockAndJump();
                    }
                } else {
                  LogUtils.e("FH", "自动登录失败 , 失败原因:本设备没有被绑定过,跳转到用户名密码登录界面");
                    FileUtils.writeProperties(FileUtils.getSDCardPath() + "leke_init", FileContonst.LOAD_APP_RESET);
                    jumpActivity(LoginActivity.class);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
              LogUtils.e("FH", "自动登录失败原因:其他错误:" + e.getMessage());
                if (-1 == SpUtils.getAccountId()) {
                  LogUtils.e("FH", "自动登录失败,没有之前的登录信息,跳转到登录");
                    jumpActivity(LoginActivity.class);
                } else {
                  LogUtils.e("FH", "自动登录失败,有之前的登录信息");
                    checkLocalLockAndJump();
                }
            }
        });
    }

    //升级接口 m=getAppVersion&id=student   用id来判断学生端、教师端 http://ocghxr9lf.bkt.clouddn.com/sample-debug.apk
    private void getServerVersion() {
        NewProtocolManager.getAppVersion(new NewGetAppVersionReq(), new NewUpdateCallBack(SplashActivity.this) {
            @Override
            public void onResponse(NewGetAppVersionRep response, int id) {
                LogUtils.e("FH" , "getVersion : " + response.toString());
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
                            LogUtils.e("FH" , "检测到有更新的版本,当前版本vCode=" + localVersion + " 服务器版本vCode=" + serverVersion + "弹出升级提示框");
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    new ConfirmDialog(getThisActivity(), null, "检测到有更新版本的程序,是否升级?"
                                            , "现在升级", "暂缓升级", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //现在升级
                                            LogUtils.e("FH" , "用户点击现在升级");
                                            dialog.dismiss();
                                            doDownLoad(SplashActivity.this, url);
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //暂缓升级
                                            LogUtils.e("FH" , "用户点击暂缓升级,直接登录");
                                            dialog.dismiss();
                                            login();
                                        }
                                    }).show();
                                }
                            });
                        } else {
                          LogUtils.e("FH", "检测版本成功,没有更新的版本,开始登录...");
                            login();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                      LogUtils.e("FH", "检测版本失败:" + e.getMessage());
                        login();
                    }
                } else {
                  LogUtils.e("FH", "检测版本失败.");
                    login();
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                login();
            }
        });
    }

    private void jumpWifiActivity() {
        UIUtils.showToastSafe("当前没有网络，请设置网络");
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
                LogUtils.e("FH" , "下载完成,开始安装");
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
                LogUtils.e("FH" , "更新失败: errorCode=" + errorCode + " errorMsg=" + errorMessage);
                // TODO: 2017/4/25
                downProgressDialog.dismiss();
                doDownLoad(mContext, downloadUrl);
            }

            @Override
            public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                if (lastProgress != progress) {
                    LogUtils.e("FH" , "下载进度变化------" + progress + "%");
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

    @Override
    protected void onPause() {
        super.onPause();
//        if (mWakeLock != null) {
//            mWakeLock.release();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mWakeLock = ((PowerManager) getSystemService(POWER_SERVICE))
//                .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
//                        | PowerManager.ON_AFTER_RELEASE, this.getClass().getName());
//        mWakeLock.acquire();
    }
}
