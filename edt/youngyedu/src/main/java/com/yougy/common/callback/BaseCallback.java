package com.yougy.common.callback;


import com.yougy.common.utils.StringUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/10/14.
 * <p>
 *      协议回调的基类，主要解决问题 ，根据服务器返回的错误统一跳转
 */
public abstract class BaseCallback extends Callback {
    @Override
    public String parseNetworkResponse(Response response, int id) throws Exception {
//        System.out.println("parseNetworkResponse ..............id ..." + id);
        onNetworkResponse(response, id);
        String res = "";
        if (response.isSuccessful()) {
            res = response.body().string();
        }
        return res;
    }


    @Override
    public void onError(Call call, Exception e, int id) {
//        System.out.println("onError ----start ");
//        System.out.println("onError ----id ");
        e.printStackTrace();
//        System.out.println("onError ----end ");
        pareError(call, e, id);
    }

    @Override
    public void onResponse(Object response, int id) {
//        System.out.println("onResponse ..............id ..." + id);
        String json = response.toString();
//        System.out.println("onResponse 后台JSON ............"+json);
        if (!StringUtils.isEmpty(json)) {
            //解析JSON
            try {
                JSONObject obj = new JSONObject(json);
                int ret = obj.isNull("ret") ? -1 : obj.getInt("ret");
                //TODO:处理公共的请求头出错问题，这里需求未知

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        pareResponse(json, id);
    }

    public void onNetworkResponse(Response response, int id){

    }

    public abstract void pareResponse(Object response, int id);

    public abstract void pareError(Call call, Exception e, int id);
}