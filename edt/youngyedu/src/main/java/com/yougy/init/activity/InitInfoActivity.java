package com.yougy.init.activity;

import android.content.Intent;
import android.os.BatteryManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yougy.common.activity.BaseActivity;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.manager.NetManager;
import com.yougy.common.manager.PowerManager;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.init.fragment.SelectSchoolFragment;
import com.yougy.init.manager.InitManager;
import com.yougy.ui.activity.R;

import de.greenrobot.event.EventBus;

/**
 * Created by jiangliang on 2016/10/13.
 */

public class InitInfoActivity extends BaseActivity {
    private ImageView mImgWSysWifi;
    private ImageView mImgWSysPower;
    private TextView mTvSysPower;
    private TextView mTvSysTime;

    @Override
    protected void init() {

    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.init_layout);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.container, new SelectSchoolFragment(), InitManager.TAG_SELECT_SCHOOL);
        transaction.commit();
        setPressTwiceToExit(true);
        initId();
    }

    private void initId() {

        mImgWSysWifi = (ImageView) this.findViewById(R.id.img_wifi);
        mImgWSysWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isConnected = NetManager.getInstance().isWifiConnected(InitInfoActivity.this);
                NetManager.getInstance().changeWiFi(InitInfoActivity.this, !isConnected);
                mImgWSysWifi.setImageDrawable(UIUtils.getDrawable(isConnected == true? R.drawable.img_wifi_1:R.drawable.img_wifi_0));
            }
        });
        mImgWSysPower = (ImageView) this.findViewById(R.id.img_electricity);
        mTvSysPower = (TextView) this.findViewById(R.id.tv_power);
        mTvSysTime = (TextView) this.findViewById(R.id.tv_time);
        EventBus.getDefault().register(this);
        NetManager.getInstance().registerReceiver(this);
        PowerManager.getInstance().registerReceiver(this);

    }


    @Override
    protected void loadData() {

    }

    @Override
    protected void refreshView() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        NetManager.getInstance().unregisterReceiver(this);
        PowerManager.getInstance().unregisterReceiver(this);

    }

    private void setSysTime() {
        mTvSysTime.setText(DateUtils.getTimeHHMMString());

    }

    private void setSysWifi() {
        if (NetManager.getInstance().isWifiConnected(this)) {
            int level = NetManager.getInstance().getConnectionInfoRssi(this);
            // 这个方法。得到的值是一个0到-100的区间值，是一个int型数据，其中0到-50表示信号最好，-50到-70表示信号偏差，
            //小于-70表示最差，有可能连接不上或者掉线，一般Wifi已断则值为-200。
            if (level <= 0 && level >= -50) {
                mImgWSysWifi.setImageDrawable(UIUtils.getDrawable(R.drawable.img_wifi_0));
            } else if (level < -50 && level >= -70) {
                mImgWSysWifi.setImageDrawable(UIUtils.getDrawable(R.drawable.img_wifi_4));
            } else if (level < -70 && level >= -80) {
                mImgWSysWifi.setImageDrawable(UIUtils.getDrawable(R.drawable.img_wifi_3));
            } else if (level < -80 && level >= -100) {
                mImgWSysWifi.setImageDrawable(UIUtils.getDrawable(R.drawable.img_wifi_2));
            }
        } else {
            mImgWSysWifi.setImageDrawable(UIUtils.getDrawable(R.drawable.img_wifi_1));
            //跳转到WIFI
            Intent intent = new Intent("android.intent.action.WIFI_ENABLE");
            startActivity(intent);
        }
    }


    private void setSysPower(int level,int state) {

        mTvSysPower.setText(level + "%");
        if (level== 0){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING?R.drawable.ic_battery_charge_0_black_03:R.drawable.ic_battery_0_black_03  ));

        }else if(level>0 && level<=10 ){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING?R.drawable.ic_battery_charge_10_black_03:R.drawable.ic_battery_10_black_03  ));
        }
        else if(level>10 && level<=20 ){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING?R.drawable.ic_battery_charge_20_black_03:R.drawable.ic_battery_20_black_03  ));
        }  else if(level>20 && level<=30 ){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING?R.drawable.ic_battery_charge_30_black_03:R.drawable.ic_battery_30_black_03  ));
        }
        else if(level>30 && level<=40 ){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING?R.drawable.ic_battery_charge_40_black_03:R.drawable.ic_battery_40_black_03  ));
        }

        else if(level>40 && level<=50 ){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING?R.drawable.ic_battery_charge_50_black_03:R.drawable.ic_battery_50_black_03  ));
        }
        else if(level>50 && level<=60 ){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING?R.drawable.ic_battery_charge_60_black_03:R.drawable.ic_battery_60_black_03  ));
        }
        else if(level>60 && level<=70){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING?R.drawable.ic_battery_charge_70_black_03:R.drawable.ic_battery_70_black_03  ));
        }

        else if(level>70 && level<=80 ){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING?R.drawable.ic_battery_charge_80_black_03:R.drawable.ic_battery_80_black_03  ));
        }

        else if(level>80&& level<100 ){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING?R.drawable.ic_battery_charge_90_black_03:R.drawable.ic_battery_90_black_03  ));
        }

        else if(level==100){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(R.drawable.ic_battery_100_black_03 ));
        }

    }

    public void onEventMainThread(BaseEvent event) {
        if (event == null)
            return;

        setSysTime();
        if (EventBusConstant.EVENT_WIIF.equals(event.getType())) {
            setSysWifi();
        } else if (EventBusConstant.EVENTBUS_POWER.equals(event.getType())) {
            LogUtils.i("event ...power");
            LogUtils.i("event...lever..." + PowerManager.getInstance().getlevelPercent());
            LogUtils.i("event...status..." + PowerManager.getInstance().getBatteryStatus());
            setSysPower(PowerManager.getInstance().getlevelPercent(), PowerManager.getInstance().getBatteryStatus());
        }
    }
}
