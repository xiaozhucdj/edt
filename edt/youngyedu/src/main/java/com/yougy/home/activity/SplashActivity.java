package com.yougy.home.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.badoo.mobile.util.WeakHandler;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.global.Commons;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.protocol.callback.LoginCallBack;
import com.yougy.common.protocol.callback.NewUpdateCallBack;
import com.yougy.common.protocol.request.NewGetAppVersionReq;
import com.yougy.common.protocol.request.NewLoginReq;
import com.yougy.common.protocol.response.NewGetAppVersionRep;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.SystemUtils;
import com.yougy.ui.activity.R;
import com.yougy.update.DownloadManager;
import com.yougy.update.VersionUtils;
import com.yougy.view.Toaster;
import com.yougy.view.dialog.DownProgressDialog;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
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
    private int lastProgress;

    private WeakHandler mHandler = new WeakHandler();

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
        onCheckLogIn();
    }

    private void onCheckLogIn(){
        if (NetUtils.isNetConnected()) {
            //设置UUID 为MAC 地址
            Commons.UUID = SystemUtils.getMacAddress();
            SpUtil.saveUUID(Commons.UUID);
            getServerVersion();
        } else {
            if (-1==SpUtil.getAccountId()) {
                jumpWifiActivity();
            } else {
                jumpActivity(MainActivity.class);
            }
        }
    }


    private void login() {
        callBack = new LoginCallBack(SplashActivity.this);
        callBack.setOnJumpListener(SplashActivity.this);
        NewLoginReq loginReq = new NewLoginReq();
        loginReq.setDeviceId(Commons.UUID);
//        ProtocolManager.loginProtocol(Commons.UUID, ProtocolId.PROTOCOL_ID_LOGIN, callBack);
        NewProtocolManager.login(loginReq,callBack);
    }

    private void getServerVersion(){

        if (!NetUtils.isNetConnected()){
            jumpWifiActivity();
        }else{
            NewProtocolManager.getAppVersion(new NewGetAppVersionReq(),new NewUpdateCallBack(SplashActivity.this){
                @Override
                public void onResponse(NewGetAppVersionRep response, int id) {
                    super.onResponse(response, id);
                    if (response!=null && response.getCode() ==NewProtocolManager.NewCodeResult.CODE_SUCCESS && response.getData()!=null){
                        try {
                            LogUtils.i(SplashActivity.class.getName()+":"+response.getData().toString());
                            NewGetAppVersionRep.Data data =  response.getData() ;
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
                                login();
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            login();
                        }
                    }else{
                        login();
                    }
                }
                @Override
                public void onError(Call call, Exception e, int id) {
                    login();
                }
            });
        }
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
