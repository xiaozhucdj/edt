package com.yougy.init.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.BatteryManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.yougy.common.activity.BaseActivity;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.global.Commons;
import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.DialogManager;
import com.yougy.common.manager.NetManager;
import com.yougy.common.manager.PowerManager;
import com.yougy.common.manager.ThreadManager;
import com.yougy.common.new_network.ApiException;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.protocol.request.NewLoginReq;
import com.yougy.common.service.DownloadService;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.DeviceScreensaverUtils;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.activity.MainActivity;
import com.yougy.home.activity.SplashActivity;
import com.yougy.init.bean.Student;
import com.yougy.message.YXClient;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityLocalLockBinding;
import com.yougy.view.dialog.ConfirmDialog;
import com.yougy.view.dialog.HintDialog;

import org.litepal.tablemanager.Connector;

import rx.functions.Action1;

import static com.yougy.common.utils.AliyunUtil.DATABASE_NAME;
import static com.yougy.common.utils.AliyunUtil.JOURNAL_NAME;

/**
 * Created by FH on 2017/6/22.
 */

public class LocalLockActivity extends BaseActivity {
    static public final String NOT_GOTO_HOMEPAGE_ON_ENTER = "not_goto_homepage";
    ActivityLocalLockBinding binding;

    @Override
    protected void onStart() {
        super.onStart();
        DialogManager.newInstance().dissMissUiPromptDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsCheckStartNet = false ;
    }

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_local_lock, null, false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    private void initSysIcon() {
        setSysWifi();
        setSysTime();
        setSysPower(PowerManager.getInstance().getlevelPercent(), PowerManager.getInstance().getBatteryStatus());
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
    public void init() {
    }

    @Override
    protected void initLayout() {
    }

    @Override
    public void loadData() {
        Student student = SpUtils.getStudent();
        binding.nameTv.setText("姓名 : " + student.getUserRealName());
        binding.schoolTv.setText("学校 : " + student.getSchoolName());
        binding.classTv.setText("班级 : " + student.getClassName());
        binding.numTv.setText("编号 : " + student.getUserNum());
        if(!Commons.isRelase){
            binding.localLockEdittext.setText("123456");
        }

        String sex = SpUtils.getSex();
        if ("男".equalsIgnoreCase(sex)) {
            binding.avatarImv.setImageDrawable(UIUtils.getDrawable(R.drawable.icon_avatar_student_male_160px));
        } else {
            binding.avatarImv.setImageDrawable(UIUtils.getDrawable(R.drawable.icon_avatar_student_famale_162px));
        }


        initSysIcon();
    }

    @Override
    protected void refreshView() {

    }

    public void enter(View view) {
        if (TextUtils.isEmpty(binding.localLockEdittext.getText()) || binding.localLockEdittext.getText().length() < 6) {
            new HintDialog(getThisActivity(), "密码长度太短").show();
            return;
        }
        LogUtils.e("FH", "edittext : " + binding.localLockEdittext.getText().toString() + " local : " + SpUtils.getLocalLockPwd());
        if (binding.localLockEdittext.getText().toString().equals(SpUtils.getLocalLockPwd())) {
            long currentTimeMillis = System.currentTimeMillis();
            LogUtils.e("FH", "本地锁输入密码正确! 上一次验证解绑时间 : " +  + SplashActivity.lastLogInSuccessedTimeStamp + "当前时间 : " + currentTimeMillis);
            if (System.currentTimeMillis() - SplashActivity.lastLogInSuccessedTimeStamp > 3600*1000){
                LogUtils.e("FH", "本地锁进入主界面前验证是否被解绑");
                NewLoginReq loginReq = new NewLoginReq();
                loginReq.setDeviceId(Commons.UUID);
                NetWorkManager.getInstance(true).login(loginReq)
                        .compose(bindToLifecycle())
                        .subscribe(students -> {
                            LogUtils.e("FH", "本地锁进入主界面前验证是否被解绑-------结果:没有被解绑,走正常流程");
                            finish();
                            if (!getIntent().getBooleanExtra(NOT_GOTO_HOMEPAGE_ON_ENTER, false)) {
                                startService(new Intent(this, DownloadService.class));
                                loadIntent(MainActivity.class);
                            }
                            SplashActivity.lastLogInSuccessedTimeStamp = currentTimeMillis;
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                                if (throwable instanceof ApiException
                                        && ("401".equals(((ApiException) throwable).getCode()))) {
                                    LogUtils.e("FH", "本地锁进入主界面前验证是否被解绑-------结果:可能被解绑了,先logout后跳转到login界面");
                                    ToastUtil.showCustomToast(getApplicationContext() , "您的设备可能已经被解绑了,请重新登录绑定设备!");
                                    //登录失败,可能是在pc端被解绑了.
                                    //删除本端的缓存数据并且跳转到login界面
                                    SpUtils.clearSP();
                                    SpUtils.changeInitFlag(false);
                                    Connector.resetHelper();
                                    deleteDatabase(DATABASE_NAME);
                                    deleteDatabase(JOURNAL_NAME);
                                    FileUtils.writeProperties(FileUtils.getSDCardPath() + "leke_init", FileContonst.LOAD_APP_RESET + "," + SpUtils.getVersion());
                                    YXClient.getInstance().logout();
                                    ThreadManager.getSinglePool().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            DeviceScreensaverUtils.setScreensaver();
                                        }
                                    });
                                    startActivity(new Intent(getThisActivity() , LoginActivity.class));
                                    finish();
                                }
                                else {
                                    LogUtils.e("FH", "本地锁进入主界面前验证是否被解绑-------结果:未知,可能是网络不通畅,验证勉强算成功,走正常逻辑");
                                    finish();
                                    if (!getIntent().getBooleanExtra(NOT_GOTO_HOMEPAGE_ON_ENTER, false)) {
                                        startService(new Intent(LocalLockActivity.this, DownloadService.class));
                                        loadIntent(MainActivity.class);
                                    }
                                }
                            }
                        });
            }
            else {
                LogUtils.e("FH", "由于距离上一次验证是否被解绑的时间过于近,本次不验证,直接走正常流程");
                finish();
                if (!getIntent().getBooleanExtra(NOT_GOTO_HOMEPAGE_ON_ENTER, false)) {
                    startService(new Intent(this, DownloadService.class));
                    loadIntent(MainActivity.class);
                }
            }
        } else {
            new HintDialog(getThisActivity(), "密码不正确").show();
        }
    }

    public void forgetPwd(View view) {
        new ConfirmDialog(this, "忘记本机锁密码需要使用乐课账户密码重新登录来重置本机锁密码,是否确定要重置?"
                , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadIntent(LoginActivity.class);
                dialog.dismiss();
                finish();
            }
        }).show();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void wifi(View view){
//        boolean isConnected = NetManager.getInstance().isWifiConnected(this);
        boolean isConnected = false ;
        NetManager.getInstance().changeWiFi(this, !isConnected);
        binding.imgWifi.setImageDrawable(UIUtils.getDrawable(isConnected ? R.drawable.img_wifi_1 : R.drawable.img_wifi_0));
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
//        System.out.println("onEventMainThread  lockac");
        if (event == null)
            return;
        setSysTime();
        if (EventBusConstant.EVENT_WIIF.equals(event.getType())) {
            setSysWifi();
        } else if (EventBusConstant.EVENTBUS_POWER.equals(event.getType())) {
            setSysPower(PowerManager.getInstance().getlevelPercent(), PowerManager.getInstance().getBatteryStatus());
        }
    }
}
