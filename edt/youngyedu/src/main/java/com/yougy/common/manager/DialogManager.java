package com.yougy.common.manager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.ui.activity.R;
import com.yougy.view.dialog.UiPromptDialog;

import de.greenrobot.event.EventBus;

/**
 * @author: zhang yc
 * @create date: 2018/8/29 11:06
 * @class desc:  Dialog管理  相同gon
 * @modifier: 
 * @modify date: 2018/8/29 11:06
 * @modify desc: 
 */
public class DialogManager {
    private static DialogManager mDialogManager;
    private DialogManager () {}
    public static DialogManager newInstance () {
        if (mDialogManager ==  null) {
            synchronized (DialogManager.class) {
                if (mDialogManager == null) {
                    mDialogManager = new DialogManager();
                }
            }
        }
        return mDialogManager;
    }

    /*************************************网络连接对话框*******************************************/
    private UiPromptDialog mNetConnStatusDialog;
    public void showNetConnDialog (Context context) {
        if (context == null) {
            LogUtils.e("showNetConnDialog context error. return.");
            return;
        }
        YoungyApplicationManager applicationManager = (YoungyApplicationManager) context.getApplicationContext();
        if (!applicationManager.isForegroundApp()){
            LogUtils.d("isForegroudApp false, return.");
            return;
        }

        if (mNetConnStatusDialog == null) {
            mNetConnStatusDialog = new UiPromptDialog(context);
        }
        if (NetUtils.isNetConnected()) {
            if (mNetConnStatusDialog.isShowing()) {
                mNetConnStatusDialog.dismiss();
            }
            return;
        } else {
            if (mNetConnStatusDialog.isShowing()) {
                return;
            }
        }
        mNetConnStatusDialog.setListener(new UiPromptDialog.Listener() {
            @Override
            public void onUiCancelListener() {
                dissMissUiPromptDialog();
            }

            @Override
            public void onUiDetermineListener() {
                dissMissUiPromptDialog();
                jumpTonet(context);
            }

            @Override
            public void onUiCenterDetermineListener() {
                dissMissUiPromptDialog();
            }
        });

        mNetConnStatusDialog.setOnDismissListener(dialog -> {
            BaseEvent baseEvent = new BaseEvent(EventBusConstant.EVENT_NETDIALOG_DISMISS, "");
            EventBus.getDefault().post(baseEvent);
        });

        if (!mNetConnStatusDialog.isShowing()) {
            mNetConnStatusDialog.show();
            mNetConnStatusDialog.setTag(0);
            mNetConnStatusDialog.setTitle(R.string.jump_to_net);
            mNetConnStatusDialog.setDialogStyle(false);
        }
    }

    private void dissMissUiPromptDialog () {
        if (mNetConnStatusDialog != null && mNetConnStatusDialog.isShowing()) {
            mNetConnStatusDialog.dismiss();
            mNetConnStatusDialog = null;
        }
    }

    private void jumpTonet(Context context) {
        Intent intent = new Intent("android.intent.action.WIFI_ENABLE");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    /*************************************网络连接对话框*******************************************/

}
