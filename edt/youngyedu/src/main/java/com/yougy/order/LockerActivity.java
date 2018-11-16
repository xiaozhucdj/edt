package com.yougy.order;


import android.content.Intent;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netease.nimlib.sdk.RequestCallback;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.RefreshUtil;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.activity.MainActivity;
import com.yougy.message.YXClient;
import com.yougy.ui.activity.R;

import de.greenrobot.event.EventBus;

import static com.yougy.common.global.FileContonst.NO_LOCK_SCREEN;


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

        this.findViewById(R.id.ll_check_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNetAndRefreshLogin();
            }
        });
        TextView tv = (TextView) this.findViewById(R.id.tv_line_text);
        tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tv.getPaint().setAntiAlias(true);//抗锯齿
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

            if (!BaseActivity.isContainsActivity(MainActivity.class.getName())){
                Intent newIntent = new Intent(getApplicationContext(), MainActivity.class);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(newIntent);
            }
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
        if (!NetUtils.isNetConnected()) {
            jumpTonet();
            return;
        }
        YXClient.getInstance().checkIfNotLoginThenDoIt(this, new RequestCallback() {
            @Override
            public void onSuccess(Object param) {
                if (!SpUtils.getOrder().contains(DateUtils.getCalendarString())){
                    SpUtils.setOrder(NO_LOCK_SCREEN);
                    BaseEvent baseEvent = new BaseEvent(EventBusConstant.EVENT_CLEAR_ACTIIVTY_ORDER, "");
                    onEventMainThread(baseEvent);
                }
            }

            @Override
            public void onFailed(int code) {
                ToastUtil.showCustomToast(getApplicationContext() , "连接消息服务器失败!");
            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }
}
