package com.yougy.order;


import android.view.View;
import android.view.ViewGroup;

import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.RefreshUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.activity.MainActivity;
import com.yougy.message.YXClient;
import com.yougy.message.ui.ChattingActivity;
import com.yougy.ui.activity.R;

import de.greenrobot.event.EventBus;


/**
 * Created by Administrator on 2018/5/8.
 * 锁UI
 * <p>
 * 1.正常上课
 * 2.用户息屏幕，在开机器
 * 3.设备重启
 * 4.手写各个状态
 */

public class LockerActivity extends BaseActivity {
    private boolean mIsBack = false;
    private boolean mEventResult;

    @Override
    protected void setContentView() {
        setContentView(inflate(R.layout.activity_locker));
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initLayout() {

        this.findViewById(R.id.btn_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNetAndRefreshLogin();
            }
        });
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void refreshView() {

    }


    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        if (event.getType().equalsIgnoreCase(EventBusConstant.EVENT_CLEAR_ACTIIVTY_ORDER)) {
            mIsBack = true;
            this.finish();
        } else if (event.getType().equalsIgnoreCase(EventBusConstant.EVENT_START_ACTIIVTY_ORDER_RESULT) && !mEventResult) {
            mEventResult = true;
            UIUtils.postDelayed(new Runnable() {
                @Override
                public void run() {
                    RefreshUtil.invalidate(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0));
                }
            }, 3000);
        }
    }

    @Override
    public void onBackPressed() {
        if (mIsBack) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        BaseEvent baseEvent = new BaseEvent(EventBusConstant.EVENT_START_ACTIIVTY_ORDER, "");
        EventBus.getDefault().post(baseEvent);
        checkNetAndRefreshLogin();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseEvent baseEvent = new BaseEvent(EventBusConstant.EVENT_LOCKER_ACTIVITY_PUSE, "");
        EventBus.getDefault().post(baseEvent);
    }

    private void checkNetAndRefreshLogin() {
        if (!NetUtils.isNetConnected()){
            jumpTonet();
            return;
        }
        YXClient.checkNetAndRefreshLogin(this, new Runnable() {
            @Override
            public void run() {
            }
        });
    }
}
