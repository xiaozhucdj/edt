package com.yougy.common.protocol.callback;

/**
 * Created by jiangliang on 2016/11/17.
 */

import android.content.Context;

import com.yougy.common.global.Commons;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.response.LogInProtocol;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.StringUtils;
import com.yougy.home.activity.MainActivity;
import com.yougy.init.activity.InitInfoActivity;
import com.yougy.init.bean.UserInfo;

import okhttp3.Response;

/***
 * 登录接口回调
 */
public class LoginCallBack extends BaseCallBack<LogInProtocol> {
    private OnJumpListener listener;

    public LoginCallBack(Context context) {
        super(context);
    }

    public void setOnJumpListener(OnJumpListener listener) {
        this.listener = listener;
    }

    @Override
    public LogInProtocol parseNetworkResponse(Response response, int id) throws Exception {
        String str = response.body().string();
        LogUtils.i("response json ...." + str);
        LogUtils.e("parseNetwork",Thread.currentThread().getName());
        return GsonUtil.fromJson(str, LogInProtocol.class);
    }

    @Override
    public void onResponse(LogInProtocol response, int id) {
        LogUtils.e("onResponse",Thread.currentThread().getName());
        if (response.getCode() == ProtocolId.RET_SUCCESS) {
            //设置数据 ,
            if (response.getUserList() != null && response.getUserList().get(0) != null) {
                UserInfo.User user = response.getUserList().get(0);
                SpUtil.saveAccountId(user.getUserId());
                SpUtil.saveAccountSchool(user.getSchoolName());
                SpUtil.saveAccountClass(user.getClassName());
                SpUtil.saveAccountNumber(user.getUserNumber());
                SpUtil.saveGradeName(user.getGradeName());
                SpUtil.saveAccountName(user.getUserRealName());
                if (StringUtils.isEmpty(user.getSubjectNames())){
                    user.setSubjectNames("语文,数学,外语");
                }
                SpUtil.saveSubjectNames(user.getSubjectNames());
            }
            if (listener != null) {
                listener.jumpActivity(MainActivity.class);
            }
        } else {
            SpUtil.clearAccount();
            if (listener != null) {
                listener.jumpActivity(InitInfoActivity.class);
            }
        }
    }

    @Override
    public void onClick() {
        ProtocolManager.loginProtocol(Commons.UUID, ProtocolId.PROTOCOL_ID_LOGIN, this);
    }

    public interface OnJumpListener {
        void jumpActivity(Class clazz);
    }
}