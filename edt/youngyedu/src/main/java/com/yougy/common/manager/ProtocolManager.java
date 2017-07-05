package com.yougy.common.manager;


import android.widget.Toast;

import com.yougy.common.global.Commons;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.request.AppendBookCartRequest;
import com.yougy.common.protocol.request.AppendBookFavorRequest;
import com.yougy.common.protocol.request.AppendNotesRequest;
import com.yougy.common.protocol.request.PromoteBookRequest;
import com.yougy.common.protocol.request.QueryQRStrRequest;
import com.yougy.common.protocol.request.RemoveBookCartRequest;
import com.yougy.common.protocol.request.RemoveBookFavorRequest;
import com.yougy.common.protocol.request.RemoveNotesRequest;
import com.yougy.common.protocol.request.RequirePayOrderRequest;
import com.yougy.common.protocol.request.UpdateNotesRequest;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.init.bean.BookInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostStringBuilder;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

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
        RequestCall call = builder.build();
        // 执行请求，
        call.execute(callbac);
    }

    private static Response setCommon(String json,int id){
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


    /////////////////////////////////协议接口 用户////////////////////////////////////////////////////////////////////

    /***
     * 1.地区查询
     *
     * @param areaId              地区编码           不需要参数时候，参数值=-1
     * @param areaName:地区名称       不需要参数时候，参数值=""
     * @param pageCur:分页页码(>=1)   不需要参数时候，参数值=-1
     * @param pageSize:每页记录数(>=1) 不需要参数时候，参数值=-1
     * @param protocol_id:        协议ID
     * @param callbac:            协议回调函数
     */
    public static void queryAreaProtocol(int areaId, String areaName, int pageCur, int pageSize, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol............. 1.地区查询.");
        JSONObject obj = new JSONObject();
        try {
            obj.put("areaId", areaId);
            obj.put("areaName", areaName);
            obj.put("pageCur", pageCur);
            obj.put("pageSize", pageSize);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCommon(Commons.URL_QUERY_AREA, obj.toString(), protocol_id, callbac);
    }

    /**
     * 2.学校查询
     *
     * @param areaId:地区编码   不需要参数时候，参数值=-1
     * @param areaName:地区名称 不需要参数时候，参数值=""
     * @param protocol_id:  协议ID
     * @param callbac:      协议回调函数
     */
    public static void querySchoolProtocol(String areaId, String areaName, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol............. 2.学校查询.");
        JSONObject obj = new JSONObject();
        try {
            obj.put("areaId", areaId);
            obj.put("areaName", areaName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCommon(Commons.URL_QUERY_SCHOOL, obj.toString(), protocol_id, callbac);
    }


    /**
     * 3.校内组织查询
     *
     * @param orgId:组织编码   不需要参数时候，参数值=-1
     * @param orgName:组织名称 不需要参数时候，参数值=""
     * @param protocol_id: 协议ID
     * @param callbac:     协议回调函数
     */
    public static void queryClassProtocol(String orgId, String orgName, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol............. 3.校内组织查询");
        JSONObject obj = new JSONObject();
        try {
            obj.put("orgId", orgId);
            obj.put("orgName", orgName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCommon(Commons.URL_QUERY_CLASS, obj.toString(), protocol_id, callbac);
    }

    /**
     * 4.用户查询
     *
     * @param orgId:组织编码   不需要参数时候，参数值=-1
     * @param orgName:组织名称 不需要参数时候，参数值=""
     * @param protocol_id: 协议ID
     * @param callbac:     协议回调函数
     */
    public static void queryUserProtocol(String orgId, String orgName, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol............. 4.用户查询");
        JSONObject obj = new JSONObject();
        try {
            obj.put("orgId", orgId);
            obj.put("orgName", orgName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCommon(Commons.URL_QUERY_USER, obj.toString(), protocol_id, callbac);
    }


    /**
     * 7. 用户登录
     *
     * @param deviceId:设备编码
     * @param protocol_id:  协议ID
     * @param callbac:      协议回调函数
     */
    public static void loginProtocol(String deviceId, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol............. 7. 用户登录");
        JSONObject obj = new JSONObject();
        try {
            obj.put("deviceId", deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCommon(Commons.URL_LOGIN, obj.toString(), protocol_id, callbac);
    }

    /**
     * 8. 用户退出用。
     *
     * @param deviceId:设备编码
     * @param protocol_id:  协议ID
     * @param callbac:      协议回调函数
     */
    public static void logoutProtocol(String deviceId, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol............. 8. 用户退出");
        JSONObject obj = new JSONObject();
        try {
            obj.put("deviceId", deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCommon(Commons.URL_LOGOUT, obj.toString(), protocol_id, callbac);
    }


    /////////////////////////////////协议接口 笔记////////////////////////////////////////////////////////////////////


    /**
     * 10. 用户笔记追加(创建笔记)
     *
     * @param request
     * @param callbac:     协议回调函数
     * @param protocol_id: 协议ID
     */
    public static void appendNotesProtocol(AppendNotesRequest request, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol............. 10. 用户笔记追加");
        setCommon(Commons.URL_APPEND_NOTES, GsonUtil.toJson(request) , protocol_id, callbac);
    }

    /**
     * 11. 用户笔记删除
     */
    public static void removeNotesProtocol(RemoveNotesRequest request, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol............. 11. 用户笔记删除");
        setCommon(Commons.URL_REMOVE_NOTES, GsonUtil.toJson(request) , protocol_id, callbac);

    }

    /**
     * 12.  用户笔记更新
     */
    public static void updateNotesProtocol(UpdateNotesRequest request, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol............. 12.  用户笔记更新");
        setCommon(Commons.URL_UPDATE_NOTES,GsonUtil.toJson(request), protocol_id, callbac);
    }

    /***
     * 13.  用户笔记查询
     *
     * @param userId       用户id
     * @param termIndex    termIndex参数为0,将追加用户本学期笔记。目前仅支持参数0。
     * @param callbac:     协议回调函数
     * @param noteType:
     * @param protocol_id: 协议ID
     */
    public static void queryNotesProtocol(int userId, int termIndex, int noteType,int protocol_id, Callback callbac) {
        LogUtils.i("Protocol............. 13.  用户笔记查询");
        JSONObject obj = new JSONObject();
        try {
            obj.put("userId", userId);
            obj.put("termIndex", termIndex);
            obj.put("noteType", noteType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCommon(Commons.URL_QUERY_NOTES, obj.toString(), protocol_id, callbac);
    }

    /////////////////////////////////协议接口 书城////////////////////////////////////////////////////////////////////


    /***
     * 9. 用户书架
     *
     * @param userId       用户编码
     * @param categoryId   分类编码  否 -1
     * @param categoryName 分类名称  否 ""
     * @param termIndex    学期偏移
     * @param protocol_id
     * @param callback
     */
    public static void bookShelfProtocol(int userId, int termIndex, int categoryId, String categoryName, int protocol_id, Callback callback) {
        LogUtils.i("Protocol............. 9. 用户书架");
        JSONObject obj = new JSONObject();
        try {
            obj.put("userId", userId);
            obj.put("termIndex", termIndex);
            obj.put("categoryId", categoryId);
            obj.put("categoryName", categoryName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCommon(Commons.URL_BOOK_SHELF, obj.toString(), protocol_id, callback);
    }


    /**
     * 14. 书城分类查询
     *
     * @param userId        ：用户ID
     * @param categoryDepth :图书分类级别深度 , 不需要参数时候，参数值=-1
     * @param protocol_id   :协议id
     * @param callbac       :协议回调函数
     */
    public static void queryBookCategoryProtocol(int userId, int categoryDepth, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol............. 14. 书城分类查询.");
        JSONObject obj = new JSONObject();
        try {
            obj.put("userId", userId);
            obj.put("categoryDepth", categoryDepth);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCommon(Commons.URL_BOOK_CATEGORY_QUERY, obj.toString(), protocol_id, callbac);
    }

    public static Response queryBookCategoryProtocol(int userId,int categoryDepth){
        JSONObject obj = new JSONObject();
        try {
            obj.put("m","queryBookCategory");
            obj.put("userId", userId);
            obj.put("categoryDepth", categoryDepth);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return setCommon(obj.toString(),ProtocolId.PROTOCOL_ID_QUERY_BOOK_CATEGORY);
    }

    /***
     * 15. 书城图书查询
     *
     * @param userId      : 用户ID
     * @param queryReg    :正则查询条件 ，不需要参数时候，参数值=""
     * @param categoryId  :分类编码     ，不需要参数时候，参数值=-1
     * @param pageCur     :分页页码     ，不需要参数时候，参数值=-1
     * @param pageSize    :每页记录数   ，不需要参数时候，参数值=-1
     * @param protocol_id :协议id
     * @param callbac     :协议回调函数
     */
    public static void queryBookProtocol(int userId, String queryReg, int categoryId, int pageCur, int pageSize, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol.............  书城图书查询");

        JSONObject obj = new JSONObject();
        try {
            obj.put("m","queryBook");
            obj.put("userId", userId);
            obj.put("queryReg", queryReg);
            obj.put("categoryId", categoryId);
            obj.put("pageCur", pageCur);
            obj.put("pageSize", pageSize);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCommon(Commons.URL_BOOK_INFO_QUERY, obj.toString(), protocol_id, callbac);
    }

    public static Response queryBookProtocol(int userId, String queryReg, int categoryId, int pageCur, int pageSize) {
        LogUtils.i("Protocol.............  书城图书查询");

        JSONObject obj = new JSONObject();
        try {
            obj.put("m","queryBook");
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
        setCommon(Commons.SHOP_URL,GsonUtil.toJson(request) , protocol_id, callbac);
    }

    /***
     * 17. 书城图书首页
     *
     * @param userId        : 用户ID
     * @param queryReg      :正则查询条件         ，不需要参数时候，参数值=""
     * @param categoryId    :分类编码             ，不需要参数时候，参数值=-1
     * @param categoryDepth :图书分类级别深度,    ，不需要参数时候，参数值=-1
     * @param pageCur       :分页页码             ，不需要参数时候，参数值=-1
     * @param pageSize      :每页记录数           ，不需要参数时候，参数值=-1
     * @param protocol_id   :协议id
     * @param callbac       :协议回调函数
     */
    public static void requireBookMainProtocol(int userId, String queryReg, int categoryId, int categoryDepth, int pageCur, int pageSize, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol............. 17. 书城图书首页");
        JSONObject obj = new JSONObject();
        try {
            obj.put("userId", userId);
            obj.put("queryReg", queryReg);
            obj.put("categoryId", categoryId);
            obj.put("categoryDepth", categoryDepth);
            obj.put("pageCur", pageCur);
            obj.put("pageSize", pageSize);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCommon(Commons.URL_BOOK_MAIN, obj.toString(), protocol_id, callbac);
    }

    public static Response requireBookMainProtocol(int userId, String queryReg, int categoryId, int categoryDepth, int pageCur, int pageSize){
        JSONObject obj = new JSONObject();
        try {
            obj.put("m","requireBookMain");
            obj.put("userId", userId);
            obj.put("queryReg", queryReg);
            obj.put("categoryId", categoryId);
            obj.put("categoryDepth", categoryDepth);
            obj.put("pageCur", pageCur);
            obj.put("pageSize", pageSize);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return setCommon(obj.toString(),ProtocolId.PROTOCOL_ID_REQUIRE_BOOK_MAIN);
    }

    /***
     * 18. 书城购物车追加
     *
     * @param protocol_id 协议id
     * @param callbac     协议回调函数
     */
    public static void appendBookCartProtocol(AppendBookCartRequest request , int protocol_id, Callback callbac) {
        LogUtils.i("Protocol.............18书城购物车追加");
        setCommon(Commons.SHOP_URL, GsonUtil.toJson(request), protocol_id, callbac);
    }

    /***
     * 19. 书城购物车移除
     *
     * @param protocol_id 协议id
     * @param callbac     协议回调函数
     */
    public static void removeBookCartProtocol(RemoveBookCartRequest request , int protocol_id, Callback callbac) {
        LogUtils.i("Protocol.............19. 书城购物车移除");
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
            obj.put("m" , "queryCart");
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
        setCommon(Commons.SHOP_URL , GsonUtil.toJson(request) , protocol_id , callbac);
    }

    /**
     * 22. 书城订单取消
     */
    public static void cancelPayOrderProtocol(String orderId , int orderOwner , int protocal_id, Callback callback) {
        LogUtils.i("Protocol.............22. 书城订单取消");
        JSONObject obj = new JSONObject();
        try {
            obj.put("m" , "cancelOrder");
            obj.put("orderId" , orderId);
            obj.put("orderOwner" , orderOwner);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCommon(Commons.SHOP_URL , obj.toString() , protocal_id , callback);
    }

    /**
     * 23. 书城订单结单
     */
    public static void finishPayOrderProtocol() {
        LogUtils.i("Protocol.............  23. 书城订单结单");
    }

    /**
     * 24. 书城订单查询
     */
    public static void queryBookOrderProtocol(String orderOwner, String orderStatus , int protocol_id, Callback callbac) {
        LogUtils.i("Protocol.............  24. 书城订单查询");
        JSONObject obj = new JSONObject();
        try {
            obj.put("m" , "queryOrder");
            obj.put("orderOwner", orderOwner);
            if (orderStatus != null){
                obj.put("orderStatus" , orderStatus);
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


    /**
     *26.. 书城收藏移除.
     * @param protocol_id 协议id
     * @param callbac     协议回调函数
     */
    public static void bookFavorRemoveProtocol(RemoveBookFavorRequest request, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol.............26. 书城收藏移除.");
        setCommon(Commons.SHOP_URL, GsonUtil.toJson(request), protocol_id, callbac);
    }
    /**
     * 27. 书城收藏查询
     *
     * @param :userId
     */
    public static void queryBookFavorProtocol(int userId, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol............27. 书城收藏查询");
        JSONObject obj = new JSONObject();
        try {
            obj.put("m" , "queryFavor");
            obj.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCommon(Commons.SHOP_URL, obj.toString(), protocol_id, callbac);
    }

    /**
     * 29、更新笔记
     */
    public static void updateNoteProtocol(Map<String,String> params,int protocol_id, Callback callback){
        JSONObject obj = new JSONObject();
        for (String key:params.keySet()){
            try {
                obj.put(key,params.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setCommon(Commons.URL_UPDATE_NOTES,obj.toString(),protocol_id,callback);
    }


    /***
     * 29查询作业
     *
     * @param userId       用户id
     * @param callbac:     协议回调函数
     * @param protocol_id: 协议ID
     */
    public static void queryHomeWrokProtocol(int userId, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol.............  29查询作业");
        JSONObject obj = new JSONObject();
        try {
            obj.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCommon(Commons.URL_QUERY_HOMEWORK, obj.toString(), protocol_id, callbac);
    }

    /***
     * 根据图书id和用户id以用户视角查询商城图书详情
     *
     */
    public static void queryShopBookDetailByIdProtocol(int userId, int bookId , int protocol_id, Callback callbac) {
        LogUtils.i("Protocol.............  根据图书id和用户id以用户视角查询商城图书详情");
        JSONObject obj = new JSONObject();
        try {
            obj.put("m","queryBook");
            obj.put("userId", userId);
            obj.put("bookId", bookId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCommon(Commons.SHOP_URL, obj.toString(), protocol_id, callbac);
    }

    public static void qureyQRStrProtocol(final QueryQRStrRequest request , final int protocol_id, final Callback callbac) {
        LogUtils.i("Protocol............. 31 获取支付二维码字符串");
        setCommon(Commons.SHOP_URL, GsonUtil.toJson(request), protocol_id, callbac);
    }

    /**
     * 1获取作业总数
     * @param :userId
     */
    public static void getHomework_todo_count(int userId, int protocol_id, Callback callbac) {
        LogUtils.i("Protocol............1..获取作业总数 ");
        JSONObject obj = new JSONObject();
        try {
            obj.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCommon(Commons.HOMEWORK_TODO_COUNT, obj.toString(), protocol_id, callbac);
    }




    //模拟数据,后期接口通了可删除
    static ArrayList<BookInfo> simulateData = new ArrayList<BookInfo>();

    /**
     * 初始化模拟数据
     */
    public static void initSimulateData(){
        simulateData.clear();
        for (int i = 0; i < 64; i++) {
            BookInfo bookInfo = new BookInfo();
            bookInfo.setBookId(i);
            bookInfo.setBookTitle(i + "轻巧夺冠-优化训练:三年级数学(上)(人教版)(北师大版)");
            bookInfo.setBookAuthor(i + "作者:洛夫斯基");
            bookInfo.setBookSalePrice(8888.88f);
            bookInfo.setBookCover("http://img3.imgtn.bdimg.com/it/u=4204844413,210602442&fm=11&gp=0.jpg");
            simulateData.add(bookInfo);
        }
    }

    public static void isOrderPaySuccessProtocol(String orderId, int orderOwner , int protocol_id, Callback callback) {
        LogUtils.i("Protocol............. 32 查询订单支付是否成功");
        JSONObject obj = new JSONObject();
        try {
            obj.put("m" , "closeOrder");
            obj.put("orderId" , orderId);
            obj.put("orderOwner" , orderOwner);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCommon(Commons.SHOP_URL , obj.toString() , protocol_id , callback);
    }


    /**
     * 操作BookInfo列表的方法,在指定的BookInfo列表中删除拥有给定的bookInfo列表中bookInfo的项
     * @param fromBookInfoList 在其中删除的BookInfo列表
     * @param toRemoveBookInfoList 指定的bookInfo列表
     * @return 返回删除后的BookInfo列表
     */
    public static void removeBooksByID(ArrayList<BookInfo> fromBookInfoList, ArrayList<BookInfo> toRemoveBookInfoList) {
        for (BookInfo toRemoveBookInfo : toRemoveBookInfoList) {
            BookInfo bookInfo = findBookByID(fromBookInfoList, toRemoveBookInfo.getBookId());
            if (bookInfo != null) {
                fromBookInfoList.remove(bookInfo);
            }
        }
    }


    /**
     * 操作BookInfo列表的方法,在列表中查找拥有指定bookID的BookInfo
     *
     * @param bookInfoList 要查找的BookInfo列表
     * @param bookID       指定的bookID
     * @return 如果找到, 则返回该BookInfo, 如果有多个, 返回第一个, 如果没有, 返回null.
     */
    public static BookInfo findBookByID(ArrayList<BookInfo> bookInfoList, int bookID) {
        for (BookInfo bookInfo : bookInfoList) {
            if (bookInfo.getBookId() == bookID) {
                return bookInfo;
            }
        }
        return null;
    }


    /**
     * 1.8
     * 获取app版本信息
     * @param callbac
     */
    public static void getAppVersion(int protocol_id, Callback callbac) {

        LogUtils.i("Protocol............. 获取app版本信息");
        JSONObject obj = new JSONObject();
        try {
            obj.put("m", "getAppVersion");
            obj.put("os", "android");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setCommon(Commons.VERSION_URL, obj.toString(), protocol_id, callbac);
    }

}
