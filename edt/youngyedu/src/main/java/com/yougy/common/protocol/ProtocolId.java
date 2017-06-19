package com.yougy.common.protocol;

/**
 * Created by Administrator on 2016/10/12.
 * * 可以自定义请求 协议id, 定义ID 要与回调其他协议不要相同
 */
public class ProtocolId {

    ////////////////////////////////////请求协议 用户相关 1000ID//////////////////////////////////
    /**
     * 1.地区查询
     */
    public static final int PROTOCOL_ID_QUERYAREA = 1001;
    /**
     * 2.学校查询
     */
    public static final int PROTOCOL_ID_QUERYSCHOOL = 1002;
    /**
     * 3.校内组织查询
     */
    public static final int PROTOCOL_ID_QUERYCLASS = 1003;
    /**
     * 4.用户查询
     */
    public static final int PROTOCOL_ID_QUERYUSER = 1004;
    /**
     * 5.设备绑定
     */
    public static final int PROTOCOL_ID_DEVICEBIND = 1005;
    /**
     * 6.设备解绑
     */
    public static final int PROTOCOL_ID_UNBIND_DEVICE = 1006;
    /**
     * 7. 用户登录
     */
    public static final int PROTOCOL_ID_LOGIN = 1007;
    /**
     * 8. 用户退出
     */
    public static final int PROTOCOL_ID_LOGOUT = 1008;
    ////////////////////////////////////请求协议 书城 2000ID//////////////////////////////////

    /**
     * 9. 用户书架
     */
    public static final int PROTOCOL_ID_BOOK_SHELF = 2009;
    /**
     * 14. 书城分类查询
     */
    public static final int PROTOCOL_ID_QUERY_BOOK_CATEGORY = 20014;
    /**
     * 15. 书城图书查询
     */
    public static final int PROTOCOL_ID_QUERY_BOOK = 20015;
    /**
     * 16.书城图书推荐
     */
    public static final int PROTOCOL_ID_PROMOTE_BOOK = 2016;
    /**
     * 17. 书城图书首页
     */
    public static final int PROTOCOL_ID_REQUIRE_BOOK_MAIN = 2017;
    /**
     * 18. 书城购物车追加
     */
    public static final int PROTOCOL_ID_APPEND_BOOK_CART = 1018;
    /**
     * 19. 书城购物车移除
     */
    public static final int PROTOCOL_ID_REMOVE_BOOK_CART = 2019;
    /**
     * 20. 书城购物车查询
     */
    public static final int PROTOCOL_ID_QUERY_BOOK_CART = 2020;
    /**
     * 21. 书城订单下单
     */
    public static final int PROTOCOL_ID_REQUIRE_PAY_ORDER = 2021;
    /**
     * 22. 书城订单取消
     */
    public static final int PROTOCOL_ID_CANCEL_PAY_ORDER = 2022;
    /**
     * 23. 书城订单结单
     */
    public static final int PROTOCOL_ID_FINISH_PAY_ORDER = 2023;
    /**
     * 24. 书城订单查询
     */
    public static final int PROTOCOL_ID_QUERY_BOOK_ORDER = 2024;
    /**
     * 25. 书城收藏追加
     */
    public static final int PROTOCOL_ID_APPEND_BOOK_FAVOR = 2025;
    /**
     * 26. 书城收藏移除
     */
    public static final int PROTOCOL_ID_REMOVE_BOOK_FAVOR = 2026;
    /**
     * 27. 书城收藏查询
     */
    public static final int PROTOCOL_ID_QUERY_BOOK_FAVOR = 2027;

    /**
     * 29. 查询 作业列表
     */
    public static final int PROTOCOL_ID_QUERY_HOME_WROK = 2029;

    /**
     * 30. 查询 商城图书详情
     */
    public static final int PROTOCOL_ID_QUERY_SHOP_BOOK_DETAIL = 2030;

    /**
     * 31. 获取订单支付二维码
     */
    public static final int PROTOCOL_ID_QUERY_QR_CODE = 2031;

    /**
     * 32. 查询订单是否支付成功
     */
    public static final int PROTOCOL_ID_IS_ORDER_PAY_SUCCESS = 2032;


    ////////////////////////////////////请求协议 笔记相关 3000ID//////////////////////////////////
    /***
     * 10.用户笔记追加
     */
    public static final int PROTOCOL_ID_APPEND_NOTES = 3010;
    /***
     * 11. 用户笔记删除
     */
    public static final int PROTOCOL_ID_REMOVE_NOTES = 3011;
    /**
     * 12. 用户笔记更新
     */
    public static final int PROTOCOL_ID_UPDATE_NOTES = 3012;
    /**
     * 13. 用户笔记查询
     */
    public static final int PROTOCOL_ID_QUERY_NOTES = 3013;

    ////////////////////////////////////协议返回 ret//////////////////////////////////
    public static final int RET_SUCCESS = 200;

    public static final int UPDATE_NOTE_ID = 4000;

    ////////////////////////////////////缓存key的id 5000 开始//////////////////////////////////
    /**
     * 本学期用书
     */
    public static final int  PROTOCOL_ID_TEXT_BOOK = 5000;
    /**
     * 全部课本
     */
    public static final int PROTOCOL_ID_All_TEXT_BOOK = 5001;
    /**
     * 辅导书
     */
    public static final int CPROTOCOL_ID_COACH_BOOK = 5002;
    /**
     * 全部辅导书
     */
    public static final int PROTOCOL_ID_ALL_COACHBOOK = 5003;
    /**
     * 全部课外书
     */
    public static final int ROTOCOL_ID_ALL_REFERENCE_BOOK = 5004;
    /**
     * 当前笔记
     */
    public static final int PROTOCOL_ID_NOTE =5005;
    /**
     * 全部笔记
     */
    public static final int PROTOCOL_ID_ALL_NOTE =5006 ;
    /**
     * 当前作业
     */
    public static final int PROTOCOL_ID_HOME_WORK =5008;
    /**
     * 全部作业
     */
    public static final int ROTOCOL_ID_ALL_HOME_WORK = 5009;


}
