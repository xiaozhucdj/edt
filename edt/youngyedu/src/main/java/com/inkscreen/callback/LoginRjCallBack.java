package com.inkscreen.callback;

/**
 * Created by jiangliang on 2016/11/17.
 */

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.inkscreen.model.Event;
import com.inkscreen.utils.LeApiApp;
import com.inkscreen.utils.LeApiResult;
import com.inkscreen.utils.LeApiUtils;
import com.yougy.common.global.Commons;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.BaseCallBack;
import com.yougy.common.protocol.response.LogInProtocol;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.init.bean.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import okhttp3.Request;
import okhttp3.Response;

/***
 * 登录接口回调
 */
public class LoginRjCallBack extends BaseCallBack<LogInProtocol> {
    private OnJumpListener listener;
    private Context mContext;
    @Override
    public void onAfter(int id) {
    }

    @Override
    public void onBefore(Request request, int id) {
    }

    public LoginRjCallBack(Context context) {
        super(context);
        mContext = context;
    }

    public void setOnJumpListener(OnJumpListener listener) {
        this.listener = listener;
    }

    @Override
    public LogInProtocol parseNetworkResponse(Response response, int id) throws Exception {
        String str = response.body().string();
        LogUtils.i("response json ...." + str);
        return GsonUtil.fromJson(str, LogInProtocol.class);
    }

    @Override
    public void onResponse(LogInProtocol response, int id) {
//        if (response.getCode() == ProtocolId.RET_SUCCESS) {
//            //设置数据 ,
//            if (response.getUserList() != null && response.getUserList().get(0) != null) {
//                UserInfo.User user = response.getUserList().get(0);
//                SpUtil.saveAccountId(user.getUserId());
//                SpUtil.saveAccountSchool(user.getSchoolName());
//                SpUtil.saveAccountClass(user.getClassName());
//                SpUtil.saveAccountNumber(user.getUserNumber());
//                SpUtil.saveGradeName(user.getGradeName());
//                SpUtil.saveAccountName(user.getUserName());
//                if (StringUtils.isEmpty(user.getSubjectNames())){
//                    user.setSubjectNames("语文,数学,外语");
//                }
//                SpUtil.saveSubjectNames(user.getSubjectNames());
//            }
//            if (listener != null) {
//                listener.jumpActivity(MainActivity.class);
//            }
//        } else {
//            SpUtil.clearAccount();
//            if (listener != null) {
//                listener.jumpActivity(InitInfoActivity.class);
//            }
//        }
        Log.i("xcz","response:==================================");
        Toast.makeText(mContext, "网络ok", Toast.LENGTH_LONG).show();
        if (response.getCode() == ProtocolId.RET_SUCCESS){


            if (!StringUtils.isEmpty(response.getUserList().get(0).getUserToken())){
                Log.i("xcz", "jsonObject" + response.getUserList().get(0).getUserToken());
                Map<String, String> map = new HashMap<>();
                map.put("token", response.getUserList().get(0).getUserToken());
                LeApiUtils.postString(LeApiApp.getLJLoginlUrl(), map, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        LeApiResult<UserInfo.User> result = new LeApiResult<UserInfo.User>(jsonObject, new TypeToken<UserInfo.User>() {
                        });

                        try {
                            if (jsonObject.getInt("ret_code") == 0){
                                EventBus.getDefault().post(new Event<>(200));

                            }else {
                            Toast.makeText(mContext,""+jsonObject.getString("ret_msg"),Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("xcz", "jsonObject" + jsonObject.toString());
                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }, this);


            }

        }



    }


    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        ProtocolManager.loginProtocol(Commons.UUID, ProtocolId.PROTOCOL_ID_LOGIN, this);
    }

    public interface OnJumpListener {
        void jumpActivity(Class clazz);
    }
}