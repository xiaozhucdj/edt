package com.yougy.common.manager;


import com.yougy.common.global.Commons;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.request.AppendBookCartRequest;
import com.yougy.common.protocol.request.AppendBookFavorRequest;
import com.yougy.common.protocol.request.PromoteBookRequest;
import com.yougy.common.protocol.request.RequirePayOrderRequest;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostStringBuilder;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/8/4.
 */
public class ProtocolManager {

/////////////////////////////////请求协议公共信息////////////////////////////////////////////////////////////////////

    /**
     * 设置统一的公共头信息
     *
     * @param json    ：请求内容JSON
     * @param url     ：服务器地址
     * @param id      ：协议ID
     * @param callbac : 协议返回后的监听
     */
    private static void setCommon(String url, String json, int id, Callback callbac) {

        LogUtils.i("请求地址.......url..." + url);
        LogUtils.i("请求数据.......json..." + json);
        LogUtils.i("请求id.........id..." + id);

        //防止用户的多次请求
        // 由于新接口的url很多都是一样的,只是以m做功能区分,因此这个防止请求不能用了
//        OkHttpUtils.getInstance().cancelTag(url);
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
        if (Commons.isRelase) {
            builder.addHeader("X-Auth-Options", "1e7904f32c4fcfd59b8a524d1bad1d8a.qg0J9zG*FIkBk^vo");
        }
        RequestCall call = builder.build();
        // 执行请求，
        call.execute(callbac);
    }

    private static Response setCommon(String json, int id) {
        String url = Commons.SHOP_URL;
        //防止用户的多次请求
//        OkHttpUtils.getInstance().cancelTag(url);
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
        if (Commons.isRelase) {
            builder.addHeader("X-Auth-Options", "1e7904f32c4fcfd59b8a524d1bad1d8a.qg0J9zG*FIkBk^vo");
        }
        RequestCall call = builder.build();
        // 执行请求，
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static Response queryBookProtocol(int userId, String queryReg, int categoryId, int pageCur, int pageSize) {
        LogUtils.i("Protocol.............  书城图书查询");

        JSONObject obj = new JSONObject();
        try {
            obj.put("m", "queryBook");
            obj.put("userId", userId);
            obj.put("queryReg", queryReg);
            obj.put("categoryId", categoryId);
            obj.put("pageCur", pageCur);
            obj.put("pageSize", pageSize);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return setCommon(obj.toString(), ProtocolId.PROTOCOL_ID_QUERY_BOOK);
    }

    /**
     * 16. 书城图书推荐
     *
     * @param protocol_id ：协议id
     * @param callbac     ：协议回调函数
     */
    public static void promoteBookProtocol(PromoteBookRequest request, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol.............16.书城图书推荐.");
        setCommon(Commons.SHOP_URL, GsonUtil.toJson(request), protocol_id, callbac);
    }


    /***
     * 18. 书城购物车追加
     *
     * @param protocol_id 协议id
     * @param callbac     协议回调函数
     */
    public static void appendBookCartProtocol(AppendBookCartRequest request, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol.............18书城购物车追加");
        setCommon(Commons.SHOP_URL, GsonUtil.toJson(request), protocol_id, callbac);
    }


    /**
     * 20. 书城购物车查询
     *
     * @param userId      :用户ID
     * @param protocol_id :协议ID
     * @param callbac     ：协议回调
     */
    public static void queryBookCartProtocol(int userId, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol.............20. 书城购物车查询");
        JSONObject obj = new JSONObject();
        try {
            obj.put("m", "queryCart");
            obj.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCommon(Commons.SHOP_URL, obj.toString(), protocol_id, callbac);
    }

    /**
     * 21. 书城订单下单
     */
    public static void requirePayOrderProtocol(RequirePayOrderRequest request, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol............. 21. 书城订单下单.");
        setCommon(Commons.SHOP_URL, GsonUtil.toJson(request), protocol_id, callbac);
    }


    /**
     * 24. 书城订单查询
     */
    public static void queryBookOrderProtocol(String orderOwner, String orderStatus, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol.............  24. 书城订单查询");
        JSONObject obj = new JSONObject();
        try {
            obj.put("m", "queryOrder");
            obj.put("orderOwner", orderOwner);
            if (orderStatus != null) {
                obj.put("orderStatus", orderStatus);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCommon(Commons.SHOP_URL, obj.toString(), protocol_id, callbac);
    }


    /**
     * 25. 书城收藏追加
     *
     * @param protocol_id 协议id
     * @param callbac     协议回调函数
     */
    public static void appendBookFavorProtocol(AppendBookFavorRequest request, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol.............25. 书城收藏追加 ");
        setCommon(Commons.SHOP_URL, GsonUtil.toJson(request), protocol_id, callbac);
    }


    /***
     * 根据图书id和用户id以用户视角查询商城图书详情
     *
     */
    public static void queryShopBookDetailByIdProtocol(int userId, int bookId, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol.............  根据图书id和用户id以用户视角查询商城图书详情");
        JSONObject obj = new JSONObject();
        try {
            obj.put("m", "queryBook");
            obj.put("userId", userId);
            obj.put("bookId", bookId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCommon(Commons.SHOP_URL, obj.toString(), protocol_id, callbac);
    }

}
