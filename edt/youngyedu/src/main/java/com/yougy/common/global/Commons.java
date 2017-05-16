package com.yougy.common.global;


/**
 * Created by jiangliang on 2016/10/8.
 */

public class Commons {

    /**
     * 融捷 内部API 接口
     */
//    private static final String URL = " http://192.168.10.16:8080/leke_api/android";


    /**
     * 外网接口
     */
//    private static final String URL = "http://api.learningpad.cn:8080/leke_api/android";
    public static final String URL = "http://106.15.45.14:8080/leke_api/android";
    public static final String VERSION_URL = "http://106.15.45.14:10002/version";

    public static final String SHOP_URL = "http://192.168.12.7:80/api/bookStore";
    public static final String NEW_URL = "http://106.15.45.14:10002/web/";

    /**
     * 花生壳 映射外网 -->每个月限制流量1G
     */
    // private static final String URL = "http://161p25u374.iok.la:13867/leke_api/android" ;

    ///////////////////////////////////用户//////////////////////////////////////////////////////
    /**
     * 1.地区查询
     */
    public static final String URL_QUERY_AREA = URL + "/user/queryArea.json";
    /**
     * 2.学校查询
     */
    public static final String URL_QUERY_SCHOOL = URL + "/user/querySchool.json";
    /**
     * 3.校内组织查询
     */
    public static final String URL_QUERY_CLASS = URL + "/user/querySchoolOrg.json";
    /**
     * 4.用户查询
     */
    public static final String URL_QUERY_USER = URL + "/user/queryUser.json";
    /**
     * 5.设备绑定
     */
    public static final String URL_DEVICE_BIND = URL + "/user/bindDevice.json";

    /**
     * 6.设备解绑
     */
    public static final String URL_UNBIND_DEVICE = URL + "/user/unbindDevice.json";

    /**
     * 7. 用户登录
     */
    public static final String URL_LOGIN = URL + "/user/login.json";
    /**
     * 8. 用户退出
     */
    public static final String URL_LOGOUT = URL + "/user/logout.json";

    ////////////////////////////////////笔记/////////////////////////////////////////////////////////
    /**
     * 10. 用户笔记追加
     */
    public static final String URL_APPEND_NOTES = URL + "/user/appendNotes.json";

    /**
     * 11. 用户笔记删除
     */
    public static final String URL_REMOVE_NOTES = URL + "/user/removeNotes.json";

    /**
     * 12. 用户笔记更新
     */
    public static final String URL_UPDATE_NOTES = URL + "/user/updateNotes.json";

    /**
     * 13. 用户笔记查询
     */
    public static final String URL_QUERY_NOTES = URL + "/user/queryNotes.json";


///////////////////////////////////书城//////////////////////////////////////////////////////
    /**
     * 9. 用户书架
     */
    public static final String URL_BOOK_SHELF = URL + "/user/bookShelf.json";


    /**
     * 14. 书城分类查询
     */
    public static final String URL_BOOK_CATEGORY_QUERY = SHOP_URL;

    /**
     * 15. 书城图书查询
     */
    public static final String URL_BOOK_INFO_QUERY = SHOP_URL;

    /**
     * 16. 书城图书推荐
     */
    public static final String URL_BOOK_PROMOTE = SHOP_URL + "/promoteBook.json";

    /**
     * 17. 书城图书首页
     */
    public static final String URL_BOOK_MAIN = SHOP_URL + "/requireBookMain.json";
//    public static final String URL_BOOK_MAIN = "http://192.168.10.16:8080/leke_api/android/bookstore/queryBookCategory.json";

    /**
     * 18. 书城购物车追加
     */
    public static final String URL_BOOK_CART_APPEND = URL + "/bookstore/appendBookCart.json";
    /**
     * 19. 书城购物车移除
     */
    public static final String URL_BOOK_CART_REMOVE = URL + "/bookstore/removeBookCart.json";
    /**
     * 20. 书城购物车查询
     */
    public static final String URL_BOOK_CART_QUERY = URL + "bookstore/queryBookCart.json";

    /**
     * 21. 书城订单下单
     */
    public static final String URL_BOOK_ORDER_REQUIRE = URL + "/bookstore/requirePayOrder.json";
    /**
     * 22.书城订单取消
     */
    public static final String URL_BOOK_ORDER_CANCEL = URL + "/bookstore/cancelPayOrder.json";
    /**
     * 23. 书城订单结单
     */
    public static final String URL_BOOK_ORDER_FINISH = URL + "/bookstore/finishPayOrder.json";
    /**
     * 24. 书城订单查询
     */
    public static final String URL_BOOK_ORDER_QUERY = URL + "/bookstore/queryBookOrder.json";

    /**
     * 25. 书城收藏追加
     */
    public static final String URL_BOOK_COLLECT_APPEND = URL + "/bookstore/appendBookFavor.json";
    /**
     * 26. 书城收藏移除
     */
    public static final String URL_BOOK_COLLECT_REMOVE = URL + "/bookstore/removeBookFavor.json";
    /**
     * 27. 书城收藏查询
     */
    public static final String URL_BOOK_COLLECT_QUERY = URL + "/bookstore/queryBookFavor.json";
    /**
     * 28查询作业
     */
    public static final String URL_QUERY_HOMEWORK = URL + "/user/queryHomework.json";


/////////////////////////////////////////////其他///////////////////////////////////////////////////////////

    //常量 ,设备ID
    public static String UUID;
    /////////////////////////////////////////////优数学接口///////////////////////////////////////////////////
    /**
     * 获取作业个数
     */
    public static final String HOMEWORK_TODO_COUNT = "http://m.web.test.uxuebao.com:8082/router/youngyedu/ym/s/homework/getTodoCount";
}

