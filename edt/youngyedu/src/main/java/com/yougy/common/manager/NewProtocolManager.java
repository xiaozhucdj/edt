package com.yougy.common.manager;

import android.widget.Toast;

import com.yougy.common.global.Commons;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.request.NewBindDeviceReq;
import com.yougy.common.protocol.request.NewGetAppVersionReq;
import com.yougy.common.protocol.request.NewLoginReq;
import com.yougy.common.protocol.request.NewQueryAreaReq;
import com.yougy.common.protocol.request.NewQueryDeviceReq;
import com.yougy.common.protocol.request.NewQueryStudentReq;
import com.yougy.common.protocol.request.NewQueryTeachertReq;
import com.yougy.common.protocol.request.NewQueryUserReq;
import com.yougy.common.protocol.request.NewUnBindDeviceReq;
import com.yougy.common.protocol.request.NewUpdateUserReq;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostStringBuilder;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;


/**
 * Created by Administrator on 2017/5/15.
 */

public class NewProtocolManager {

    private static void setCommon(String url, String json, int id, Callback callbac) {

        LogUtils.i("请求地址.......url..." + url);
        LogUtils.i("请求数据.......json..." + json);
        LogUtils.i("请求id.........id..." + id);

        //防止用户的多次请求
        OkHttpUtils.getInstance().cancelTag(url);
        //设置请求String
        PostStringBuilder builder = OkHttpUtils.postString();
        //设置地址
        builder.url(url);
        //设置请求内容
        builder.content(json);
        //设置类型
        builder.mediaType(MediaType.parse("application/json; charset=utf-8"));
        //设置tag,为了 打标志 取消请求
        builder.tag(url);
        //根据协议设置ID在回调函数 统一处理
        builder.id(id);
        RequestCall call = builder.build();
        // 执行请求，
        call.execute(callbac);
    }

    /***
     * 解析 仅有公共头的JSON
     *
     * @param json    str
     * @param success 成功提示
     * @param error   失败提示
     */
    public static boolean pareOnlyHeadJson(String json, String success, String error) {
        boolean result = false;
        if (!StringUtils.isEmpty(json)) {
            //解析JSON
            try {
                JSONObject obj = new JSONObject(json);
                int ret = obj.isNull("ret") ? -1 : obj.getInt("ret");
                if (ret == ProtocolId.RET_SUCCESS) {
                    UIUtils.showToastSafe(success, Toast.LENGTH_SHORT);
                    result = true;
                } else {
                    UIUtils.showToastSafe(error, Toast.LENGTH_SHORT);
                    result = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            UIUtils.showToastSafe(error, Toast.LENGTH_SHORT);
            result = false;
        }
        return result;
    }

    /////////////////////////////////版本接口,web/version ////////////////////////////////////////

    /**
     * 1获取APP 版本
     *
     * @param req
     * @param callbac
     */
    public static void getAppVersion(NewGetAppVersionReq req, Callback callbac) {
        setCommon(Commons.NEW_URL + req.getAddress(), GsonUtil.toJson(req), NewProtocolId.ID_VERSION, callbac);
    }


    /////////////////////////////////设备接口web/device////////////////////////////////////////

    /**
     * 2设备查询
     *
     * @param req
     * @param callbac
     */
    public static void queryDevice(NewQueryDeviceReq req, Callback callbac) {
        setCommon(Commons.NEW_URL + req.getAddress(), GsonUtil.toJson(req), NewProtocolId.ID_QUERY_DEVICE, callbac);
    }

    /**
     * 3设备绑定
     *
     * @param req
     * @param callbac
     */
    public static void bindDevice(NewBindDeviceReq req, Callback callbac) {
        setCommon(Commons.NEW_URL + req.getAddress(), GsonUtil.toJson(req), NewProtocolId.ID_BIND_DEVICE, callbac);
    }

    /**
     * 4设备解绑
     *
     * @param req
     * @param callbac
     */
    public static void unbindDevice(NewUnBindDeviceReq req, Callback callbac) {
        setCommon(Commons.NEW_URL + req.getAddress(), GsonUtil.toJson(req), NewProtocolId.ID_UN_BIND_DEVICE, callbac);
    }

    /////////////////////////////////用户接口web/users////////////////////////////////////////

    /**
     * 5用户查询
     *
     * @param req
     * @param callbac
     */
    public static void queryUser(NewQueryUserReq req, Callback callbac) {
        setCommon(Commons.NEW_URL + req.getAddress(), GsonUtil.toJson(req), NewProtocolId.ID_QUERY_USER, callbac);
    }

    /**
     * 6用户修改
     *
     * @param req
     * @param callbac
     */
    public static void updateUser(NewUpdateUserReq req, Callback callbac) {
        setCommon(Commons.NEW_URL + req.getAddress(), GsonUtil.toJson(req), NewProtocolId.ID_UPDATE_USER, callbac);
    }

    /**
     * 7用户登录
     *
     * @param req
     * @param callbac
     */
    public static void login(NewLoginReq req, Callback callbac) {
        setCommon(Commons.NEW_URL + req.getAddress(), GsonUtil.toJson(req), NewProtocolId.ID_LOGIN, callbac);
    }

    /**
     * 8学生查询
     *
     * @param req
     * @param callbac
     */
    public static void queryStudent(NewQueryStudentReq req, Callback callbac) {
        setCommon(Commons.NEW_URL + req.getAddress(), GsonUtil.toJson(req), NewProtocolId.ID_QUERY_STUDENT, callbac);
    }

    /**
     * 9教师查询
     *
     * @param req
     * @param callbac
     */
    public static void queryTeacher(NewQueryTeachertReq req, Callback callbac) {
        setCommon(Commons.NEW_URL + req.getAddress(), GsonUtil.toJson(req), NewProtocolId.ID_QUERY_TEACHER, callbac);
    }

    /////////////////////////////////课堂接口web/classRoom////////////////////////////////////////

    /**
     * 10地区查询
     *
     * @param req
     * @param callbac
     */
    public static void queryArea(NewQueryAreaReq req, Callback callbac) {
        setCommon(Commons.NEW_URL + req.getAddress(), GsonUtil.toJson(req), NewProtocolId.ID_QUERY_AREA, callbac);
    }











    public static class NewProtocolId {
        public static final int ID_VERSION = 1;

        public static final int ID_QUERY_DEVICE = 2;

        public static final int ID_BIND_DEVICE = 3;

        public static final int ID_UN_BIND_DEVICE = 4;

        public static final int ID_QUERY_USER = 5;

        public static final int ID_UPDATE_USER = 6;

        public static final int ID_LOGIN = 7;

        public static final int ID_QUERY_STUDENT = 8;

        public static final int ID_QUERY_TEACHER = 9;

        public static final int ID_QUERY_AREA= 10 ;
    }
}
