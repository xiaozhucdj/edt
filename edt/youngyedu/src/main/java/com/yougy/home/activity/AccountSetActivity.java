package com.yougy.home.activity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yougy.common.activity.BaseActivity;
import com.yougy.common.global.Commons;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.callback.UnBindCallback;
import com.yougy.common.protocol.request.NewUnBindDeviceReq;
import com.yougy.common.protocol.response.NewUnBindDeviceRep;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.init.activity.InitInfoActivity;
import com.yougy.ui.activity.R;

import rx.functions.Action1;
import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2016/10/20.
 * 账号设置 ，注销
 */
public class AccountSetActivity extends BaseActivity implements View.OnClickListener {
    private TextView mTvTitle;
    private ImageButton mBtnleft;
    private ImageButton mBtnRight;
    private TextView mTvSchool;
    private TextView mTvClass;
    private TextView mTvName;
    private TextView mTvNumber;
    private Button mUnBindBtn;

    private CompositeSubscription subscription;
    private ConnectableObservable<Object> tapEventEmitter;

    @Override
    protected void init() {
        subscription = new CompositeSubscription();
        tapEventEmitter = YougyApplicationManager.getRxBus(this).toObserverable().publish();
        handleUnBindEvent();
    }

    private void handleUnBindEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof NewUnBindDeviceRep) {
                    BaseActivity.finishAll();
                    loadIntent(InitInfoActivity.class);
                }
            }
        }));
        subscription.add(tapEventEmitter.connect());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e(tag,"onDestroy......");
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_account);
        mBtnleft = (ImageButton) this.findViewById(R.id.btn_left);
        mBtnRight = (ImageButton) this.findViewById(R.id.img_btn_right);
        mBtnRight.setVisibility(View.GONE);
        mTvTitle = (TextView) this.findViewById(R.id.tv_title);
        mTvTitle.setText("账号设置");
        mBtnleft.setOnClickListener(this);

        mTvSchool = (TextView) this.findViewById(R.id.tv_school);
        mTvClass = (TextView) this.findViewById(R.id.tv_class);
        mTvName = (TextView) this.findViewById(R.id.tv_name);
        mTvNumber = (TextView) this.findViewById(R.id.tv_number);
        mUnBindBtn = (Button) this.findViewById(R.id.btn_unwrap);
        mUnBindBtn.setOnClickListener(this);

    }

    @Override
    protected void loadData() {
        String school = getResources().getString(R.string.info_school);
        String classgroud = getResources().getString(R.string.info_class);
        String name = getResources().getString(R.string.info_name);
        String number = getResources().getString(R.string.info_number);

        LogUtils.i(SpUtil.getAccountInfo().toString());
        mTvSchool.setText(String.format(school, SpUtil.getAccountSchool()));
        mTvClass.setText(String.format(classgroud, SpUtil.getAccountClass()));
        mTvName.setText(String.format(name, SpUtil.getAccountName()));
        mTvNumber.setText(String.format(number, SpUtil.getAccountNumber()));
    }

    @Override
    protected void refreshView() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                this.finish();
                break;
            case R.id.btn_unwrap:
//                ProtocolManager.deviceUnBindProtocol(Commons.UUID, ProtocolId.PROTOCOL_ID_UNBIND_DEVICE, new UnBindCallback(this));
                NewUnBindDeviceReq unBindDeviceReq = new NewUnBindDeviceReq();
                unBindDeviceReq.setDeviceId(Commons.UUID);
                unBindDeviceReq.setUserId(SpUtil.getAccountId());
                NewProtocolManager.unbindDevice(unBindDeviceReq,new UnBindCallback(this));
                break;
        }
    }

}
