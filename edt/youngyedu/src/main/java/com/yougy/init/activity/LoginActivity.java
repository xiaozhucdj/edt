package com.yougy.init.activity;

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
import com.yougy.common.manager.NetManager;
import com.yougy.common.manager.PowerManager;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.protocol.request.NewLoginReq;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.init.bean.Student;
import com.yougy.init.dialog.ConfirmUserInfoDialog;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityLoginBinding;
import com.yougy.view.dialog.HintDialog;

/**
 * Created by FH on 2017/6/22.
 */

public class LoginActivity extends BaseActivity {

    ActivityLoginBinding binding;
    ConfirmUserInfoDialog confirmUserInfoDialog;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_login, null, false);
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
        binding.tvGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.finish();
            }
        });

        binding.tvNetSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.WIFI_ENABLE");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initLayout() {

    }

    @Override
    public void loadData() {
        initSysIcon();
    }

    @Override
    protected void refreshView() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private boolean isLogining = false;

    public void login(View view) {
        if (isLogining) {
            return;
        }
        isLogining = true;
        if (TextUtils.isEmpty(binding.accountEdittext.getText())) {
            new HintDialog(getThisActivity(), "账号不能为空").show();
            return;
        }
        if (TextUtils.isEmpty(binding.pwdEdittext.getText()) || binding.pwdEdittext.getText().length() < 6) {
            new HintDialog(getThisActivity(), "密码长度太短").show();
            return;
        }
        NewLoginReq loginReq = new NewLoginReq();
        loginReq.setUserName(binding.accountEdittext.getText().toString());
        loginReq.setUserPassword(binding.pwdEdittext.getText().toString());
        NetWorkManager.login(loginReq)
                .compose(bindToLifecycle())
                .subscribe(students -> {
                    Student student = students.get(0);
                    if (!student.getUserRole().equals(getString(R.string.student))) {
                        isLogining = false;
                        new HintDialog(getThisActivity(), "权限错误:账号类型错误,请使用学生账号登录").show();
                    } else {
                        LogUtils.e("FH", "登录成功,弹出信息确认dialog");
                        confirmUserInfoDialog = new ConfirmUserInfoDialog(LoginActivity.this, student);
                        confirmUserInfoDialog.show();
                    }
                }, throwable -> {
                    isLogining = false;
                    new HintDialog(getThisActivity(), "登录失败:用户名密码错误").show();
                });
    }


    public void forgetPwd(View view) {
        new HintDialog(this, "请联系管理员请求重置你的乐课账户密码,重置成功后请重新登录即可").show();
    }

    public void wifi(View view) {
        boolean isConnected = false;
        NetManager.getInstance().changeWiFi(this, !isConnected);
        binding.imgWifi.setImageDrawable(UIUtils.getDrawable(isConnected ? R.drawable.img_wifi_1 : R.drawable.img_wifi_0));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /* *//**
     * 检查系统应用程序，并打开
     *//*
    public void openApp(String appPag, String cls) {
        PackageManager packageManager = this.getPackageManager();
        try {
            PackageInfo pi = packageManager.getPackageInfo(appPag, 0);
            if (null != pi) {
                Intent intent = new Intent();
                intent.setClassName(appPag, cls);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplication(), "当前没有安装APP 。。。", Toast.LENGTH_LONG).show();
        }
    }*/
}
