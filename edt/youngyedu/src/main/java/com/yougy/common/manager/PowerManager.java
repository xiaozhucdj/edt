package com.yougy.common.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;

import de.greenrobot.event.EventBus;

import static android.content.Intent.ACTION_BATTERY_CHANGED;


/**
 * Created by Administrator on 2017/4/26.
 */

public class PowerManager {
    private static PowerManager sInstance;
    private final BatteryReceiver mBatteryReceiver;
    private int mLvelPercent;
    private int mStatus;

    private PowerManager() {
        mBatteryReceiver = new BatteryReceiver();
    }

    public static synchronized PowerManager getInstance() {
        if (sInstance == null) {
            sInstance = new PowerManager();
        }
        return sInstance;
    }


    /**
     * 注册广播O(∩_∩)O~~
     * @param context
     */
    public void registerReceiver(Context context){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_BATTERY_CHANGED);
        context.registerReceiver(mBatteryReceiver, filter);
    }

    /** 反注册广播 */
    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(mBatteryReceiver);
    }

    /**
     * 获取电量百分比
     * @return
     */
    public int getlevelPercent(){
        return mLvelPercent ;
    }
    public int getBatteryStatus(){
        return mStatus ;
    }
    private final class BatteryReceiver extends BroadcastReceiver {



        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_BATTERY_CHANGED)) {

               int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                //电量
                 mLvelPercent = (int)(((float)level / scale) * 100);
                //获取电池状态
                mStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);

                BaseEvent baseEvent = new BaseEvent(EventBusConstant.EVENTBUS_POWER, "");
                EventBus.getDefault().post(baseEvent);
            }
        }
    }
}
