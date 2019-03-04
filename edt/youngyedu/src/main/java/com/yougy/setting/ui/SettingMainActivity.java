package com.yougy.setting.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.global.Commons;
import com.yougy.common.global.FileContonst;
import com.yougy.common.jd.BroadcastHelper;
import com.yougy.common.manager.NetManager;
import com.yougy.common.manager.PowerManager;
import com.yougy.common.manager.ThreadManager;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.protocol.request.NewUnBindDeviceReq;
import com.yougy.common.utils.AliyunUtil;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.DeviceScreensaverUtils;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.PackageUtils;
import com.yougy.common.utils.RefreshUtil;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.SystemUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.init.activity.LoginActivity;
import com.yougy.init.bean.Student;
import com.yougy.message.YXClient;
import com.yougy.ui.activity.BuildConfig;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivitySettingBinding;
import com.yougy.view.dialog.ConfirmDialog;
import com.yougy.view.dialog.LoadingProgressDialog;

import org.litepal.tablemanager.Connector;

import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.yougy.common.utils.AliyunUtil.DATABASE_NAME;
import static com.yougy.common.utils.AliyunUtil.JOURNAL_NAME;

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

    private void initSysIcon() {
        setSysWifi();
        setSysTime();
        setSysPower(PowerManager.getInstance().getlevelPercent(), PowerManager.getInstance().getBatteryStatus());

        SharedPreferences sharedPreferences3 = YoungyApplicationManager.getApp().getSharedPreferences("date", YoungyApplicationManager.MODE_PRIVATE);
        String str = "";

        switch (sharedPreferences3.getInt("ulr", -1)) {
            case 0:
                str = "生产" ;
                break;
            case 1:
                str = "开发" ;
                break;
            case 2:
                str = "测试" ;
                break;
        }
        binding.tvUrlState.setText("当前URL地址为"+str);
        binding.tvVersion.setText(PackageUtils.getVersionCode()+":"+PackageUtils.getVersionName());
    }

    private void setSysTime() {
        binding.tvTime.setText(DateUtils.getTimeHHMMString());
    }

    private void setSysWifi() {
        if (NetManager.getInstance().isWifiConnected(this)) {
            int level = NetManager.getInstance().getConnectionInfoRssi(this);
            // 这个方法。得到的值是一个0到-100的区间值，是一个int型数据，其中0到-50表示信号最好，-50到-70表示信号偏差，
            //小于-70表示最差，有可能连接不上或者掉线，一般Wifi已断则值为-200。
            if (level <= 0 && level >= -50) {
                binding.imgWifi.setImageDrawable(UIUtils.getDrawable(R.drawable.img_wifi_0));
            } else if (level < -50 && level >= -70) {
                binding.imgWifi.setImageDrawable(UIUtils.getDrawable(R.drawable.img_wifi_4));
            } else if (level < -70 && level >= -80) {
                binding.imgWifi.setImageDrawable(UIUtils.getDrawable(R.drawable.img_wifi_3));
            } else if (level < -80 && level >= -100) {
                binding.imgWifi.setImageDrawable(UIUtils.getDrawable(R.drawable.img_wifi_2));
            }
        } else {
            binding.imgWifi.setImageDrawable(UIUtils.getDrawable(R.drawable.img_wifi_1));
        }
    }


    private void setSysPower(int level, int state) {

        binding.tvPower.setText(level + "%");
        if (level == 0) {
            binding.imgElectricity.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING ? R.drawable.ic_battery_charge_0_black_03 : R.drawable.ic_battery_0_black_03));

        } else if (level > 0 && level <= 10) {
            binding.imgElectricity.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING ? R.drawable.ic_battery_charge_10_black_03 : R.drawable.ic_battery_10_black_03));
        } else if (level > 10 && level <= 20) {
            binding.imgElectricity.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING ? R.drawable.ic_battery_charge_20_black_03 : R.drawable.ic_battery_20_black_03));
        } else if (level > 20 && level <= 30) {
            binding.imgElectricity.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING ? R.drawable.ic_battery_charge_30_black_03 : R.drawable.ic_battery_30_black_03));
        } else if (level > 30 && level <= 40) {
            binding.imgElectricity.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING ? R.drawable.ic_battery_charge_40_black_03 : R.drawable.ic_battery_40_black_03));
        } else if (level > 40 && level <= 50) {
            binding.imgElectricity.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING ? R.drawable.ic_battery_charge_50_black_03 : R.drawable.ic_battery_50_black_03));
        } else if (level > 50 && level <= 60) {
            binding.imgElectricity.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING ? R.drawable.ic_battery_charge_60_black_03 : R.drawable.ic_battery_60_black_03));
        } else if (level > 60 && level <= 70) {
            binding.imgElectricity.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING ? R.drawable.ic_battery_charge_70_black_03 : R.drawable.ic_battery_70_black_03));
        } else if (level > 70 && level <= 80) {
            binding.imgElectricity.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING ? R.drawable.ic_battery_charge_80_black_03 : R.drawable.ic_battery_80_black_03));
        } else if (level > 80 && level < 100) {
            binding.imgElectricity.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING ? R.drawable.ic_battery_charge_90_black_03 : R.drawable.ic_battery_90_black_03));
        } else if (level == 100) {
            binding.imgElectricity.setImageDrawable(UIUtils.getDrawable(R.drawable.ic_battery_100_black_03));
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscription = new CompositeSubscription();
        tapEventEmitter = YoungyApplicationManager.getRxBus(this).toObserverable().publish();
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

    @Override
    public void init() {


    }

    @Override
    protected void initLayout() {

        if (BuildConfig.DEBUG) {
            binding.unbindBtn.setVisibility(View.VISIBLE);
            binding.llMsg.setVisibility(View.VISIBLE);
        } else {
            binding.unbindBtn.setVisibility(View.GONE);
            binding.llMsg.setVisibility(View.GONE);
        }
    }

    @Override
    public void loadData() {
        Student student = SpUtils.getStudent();
        binding.nameTv.setText("姓名 : " + student.getUserRealName());
        binding.schoolTv.setText("学校 : " + student.getSchoolName());
        binding.classTv.setText("班级 : " + student.getClassName());
        binding.numTv.setText("编号 : " + student.getUserNum());
        String sex = SpUtils.getSex();
        if ("男".equalsIgnoreCase(sex)) {
            binding.avatarImv.setImageDrawable(UIUtils.getDrawable(R.drawable.icon_avatar_student_male_160px));
        } else {
            binding.avatarImv.setImageDrawable(UIUtils.getDrawable(R.drawable.icon_avatar_student_famale_162px));
        }
        initSysIcon();
        binding.avatarImv.setOnLongClickListener(v -> {
            LogUtils.setOpenLog(true);
            NetWorkManager.getInstance(false).openBODY();
            return true;
        });
    }

    @Override
    protected void refreshView() {

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                this.finish();
                break;
            case R.id.img_wifi:
//                boolean isConnected = NetManager.getInstance().isWifiConnected(this);
                boolean isConnected = false;
                NetManager.getInstance().changeWiFi(this, !isConnected);
                binding.imgWifi.setImageDrawable(UIUtils.getDrawable(isConnected ? R.drawable.img_wifi_1 : R.drawable.img_wifi_0));
                break;

            case R.id.btn_chckUrl0:
                SharedPreferences sharedPreferences1 = YoungyApplicationManager.getApp().getSharedPreferences("date", YoungyApplicationManager.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                editor1.putInt("ulr", 0);
                editor1.commit();
                binding.tvUrlState.setText("当前URL地址为 生产环境");
                restartApplication(this);
                break;
            case R.id.btn_chckUrl1:
                SharedPreferences sharedPreferences2 = YoungyApplicationManager.getApp().getSharedPreferences("date", YoungyApplicationManager.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                editor2.putInt("ulr", 1);
                editor2.commit();
                binding.tvUrlState.setText("当前URL地址为 开发环境");
                restartApplication(this);
                break;
            case R.id.btn_chckUrl2:
                SharedPreferences sharedPreferences3 = YoungyApplicationManager.getApp().getSharedPreferences("date", YoungyApplicationManager.MODE_PRIVATE);
                SharedPreferences.Editor editor3 = sharedPreferences3.edit();
                editor3.putInt("ulr", 2);
                editor3.commit();
                binding.tvUrlState.setText("当前URL地址为 测试环境");
                restartApplication(this);
                break;
        }
    }

    public void unBind(View view) {

        new ConfirmDialog(this, "确定解绑该账号吗?", "账号解绑后,存储在本设备上的资料都将遗失,\n请慎重操作"
                , "解绑", "取消", (dialog, which) -> {
            unBindRequest();
            dialog.dismiss();
            invalidateDelayed();
        }, (dialogInterface, i) -> {
            dialogInterface.dismiss();
            invalidateDelayed();
        }).show();
    }

    private LoadingProgressDialog loadingProgressDialog;

    private void unBindRequest() {
        if (!NetUtils.isNetConnected()) {
            showTagCancelAndDetermineDialog(R.string.jump_to_net, mTagNoNet);
            return;
        }

        if (SpUtils.isContentChanged()) {
            if (null == loadingProgressDialog) {
                loadingProgressDialog = new LoadingProgressDialog(this);
                loadingProgressDialog.show();
            }

            NetWorkManager.queryUploadAliyunData().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(aliyunData -> {
                try {
                    AliyunUtil aliyunUtil = new AliyunUtil(aliyunData);
                    PutObjectResult result = aliyunUtil.upload();
                    if (result.getStatusCode() == 200) {
                        unbindDevice();
                    }
                } catch (Exception e) {
                    showTagCancelAndDetermineDialog(R.string.unbind_fail, mTagUnbindFail);
                } finally {
                    loadingProgressDialog.dismiss();
                }
            }, Throwable::printStackTrace);
        } else {
            unbindDevice();
        }
    }

    private void unbindDevice() {
        NewUnBindDeviceReq unBindDeviceReq = new NewUnBindDeviceReq();
        unBindDeviceReq.setDeviceId(Commons.UUID);
        unBindDeviceReq.setUserId(SpUtils.getUserId());
        NetWorkManager.unbindDevice(unBindDeviceReq)
                .compose(bindToLifecycle())
                .subscribe(o -> {
                    SpUtils.clearSP();
                    SpUtils.changeInitFlag(false);
                    Connector.resetHelper();
                    deleteDatabase(DATABASE_NAME);
                    deleteDatabase(JOURNAL_NAME);
                    FileUtils.writeProperties(FileUtils.getSDCardPath() + "leke_init", FileContonst.LOAD_APP_RESET + "," + SpUtils.getVersion());
                    showCenterDetermineDialog(R.string.unbind_success);
                    BroadcastHelper.unBindJdReader(SettingMainActivity.this);
                    YXClient.getInstance().logout();
                    ThreadManager.getSinglePool().execute(new Runnable() {
                        @Override
                        public void run() {
                            DeviceScreensaverUtils.setScreensaver();
                        }
                    });
                }, throwable -> {
                    showTagCancelAndDetermineDialog(R.string.unbind_fail, mTagUnbindFail);
                });
    }

    public void changePwd(View view) {
        new ChangePwdDialog(this).setPwdListener(this::invalidateDelayed).setOnDismissListener_return(dialog -> RefreshUtil.invalidate(binding.getRoot())).show();
    }


    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        if (mTagUnbindFail == mUiPromptDialog.getTag()) {
            unBindRequest();
        }
    }

    @Override
    public void onUiCenterDetermineListener() {
        super.onUiCenterDetermineListener();
        finishAll();
        loadIntent(LoginActivity.class);
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        if (event == null)
            return;

        setSysTime();
        if (EventBusConstant.EVENT_WIIF.equals(event.getType())) {
            setSysWifi();
        } else if (EventBusConstant.EVENTBUS_POWER.equals(event.getType())) {
            setSysPower(PowerManager.getInstance().getlevelPercent(), PowerManager.getInstance().getBatteryStatus());
        }
    }

    public void restartApplication(Context context) {
        UIUtils.postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }, 3000) ;
    }

}
