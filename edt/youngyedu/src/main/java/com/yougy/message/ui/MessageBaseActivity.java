package com.yougy.message.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yougy.common.activity.BaseActivity;
import com.yougy.common.utils.NetUtils;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityMessageBaseBinding;
import com.yougy.view.dialog.ConfirmDialog;
import com.zhy.autolayout.utils.AutoUtils;

/**
 * Created by FH on 2017/3/21.
 */

public abstract class MessageBaseActivity extends BaseActivity {
    ActivityMessageBaseBinding baseBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitleBar(baseBinding.titleBarLayout , baseBinding.backBtn , baseBinding.titleTextview , baseBinding.rightBtn);
        AutoUtils.auto(baseBinding.rootLayout);
    }


    @Override
    protected void initLayout() {
        //装入页面上方titleBar
        baseBinding = DataBindingUtil.inflate(LayoutInflater.from(this) , R.layout.activity_message_base , null , false);
        setContentView(baseBinding.rootLayout);
    }

    /**
     * 根据layoutID 对应的xml生成view并添加到当前界面的titleBar下方.
     * @param layoutID 要生成的layout的layout的xml的ID
     * @return 生成的view,可用于dataBinding
     */
    public View setLayoutRes(@LayoutRes int layoutID){
        View view = LayoutInflater.from(this).inflate(layoutID , baseBinding.rootLayout , false);
        AutoUtils.auto(view);
        baseBinding.rootLayout.addView(view);
        return view;
    }

    @Override
    protected void setContentView() {

    }

    @Override
    protected void refreshView() {

    }

    /**
     * don't use this method to set layout ,use {@link #setLayoutRes} instead.
     *
     * @param layoutResID
     */
    @Override
    @Deprecated
    public void setContentView(@LayoutRes int layoutResID) {
    }

    protected abstract void initTitleBar(RelativeLayout titleBarLayout , Button leftBtn , TextView titleTv , Button rightBtn);

    public void onTitleBarLeftBtnClick(View view){
        onBackPressed();
    }

    public void onTitleBarRightBtnClick(View view){}

    /**
     * 通过Activity类启动activity,并且传递多个String extra参数
     *
     * @param pClass 要启动的activity的class
     * @param args   启动后要传递的参数,必须都为String类型,而且要以key1,value1,key2,value2...这样的顺序依次传递
     */
    protected void openActivity(Class<?> pClass, String... args) {
        Intent intent = new Intent(this, pClass);
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 < args.length) {
                intent.putExtra(args[i], args[i + 1]);
            }
        }
        startActivity(intent);
    }

    /**
     * 检查网络,如果wifi没有打开,则提示.
     * @return
     */
    protected boolean isWifiOn(){
        if (!NetUtils.isNetConnected()) {
            new ConfirmDialog(getThisActivity(), "当前的wifi没有打开,无法接收新的消息,是否打开wifi?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent("android.intent.action.WIFI_ENABLE");
                    startActivity(intent);
                    dialog.dismiss();
                }
            } , "打开").show();
            return false;
        }
        return true;
    }

}
