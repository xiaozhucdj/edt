package com.yougy.common.new_network;

import android.util.Pair;
import android.view.WindowManager;

import com.yougy.anwser.BaseResult;
import com.yougy.anwser.CourseInfo;
import com.yougy.anwser.ParsedQuestionItem;
import com.yougy.anwser.STSbean;
import com.yougy.common.bean.AliyunData;
import com.yougy.common.global.Commons;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.model.Version;
import com.yougy.common.protocol.request.BookStoreCategoryReq;
import com.yougy.common.protocol.request.BookStoreHomeReq;
import com.yougy.common.protocol.request.NewLoginReq;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.SystemUtils;
import com.yougy.homework.bean.HomeworkBookDetail;
import com.yougy.homework.bean.HomeworkBookSummary;
import com.yougy.homework.bean.HomeworkDetail;
import com.yougy.homework.bean.QuestionReplyDetail;
import com.yougy.homework.bean.QuestionReplySummary;
import com.yougy.init.bean.Student;
import com.yougy.shop.CreateOrderRequestObj;
import com.yougy.shop.QueryQRStrObj;
import com.yougy.shop.bean.BookInfo;
import com.yougy.shop.bean.CartItem;
import com.yougy.shop.bean.CategoryInfo;
import com.yougy.shop.bean.DownloadInfo;
import com.yougy.shop.bean.Favor;
import com.yougy.shop.bean.OrderDetailBean;
import com.yougy.shop.bean.OrderIdObj;
import com.yougy.shop.bean.OrderInfo;
import com.yougy.shop.bean.OrderSummary;
import com.yougy.shop.bean.RemoveRequestObj;
import com.yougy.ui.activity.BuildConfig;
import com.yougy.view.dialog.LoadingProgressDialog;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 */
public final class NetWorkManager {

    private static final int HTTP_CONNECTION_TIMEOUT =15 * 1000;

    private ServerApi mServerApi;

    public ServerApi getServerApi() {
        return mServerApi;
    }

    private NetWorkManager() {
        // 配置日志输出，因为Retrofit2不支持输出日志，只能用OkHttp来输出
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        /*OkHttpClient mClient = new OkHttpClient.Builder()
                .readTimeout(HTTP_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(HTTP_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(HTTP_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true).build();*/

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        Interceptor headerInterceptor = new Interceptor() {

            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request orignaRequest = chain.request();
                Request.Builder newBuilder = orignaRequest.newBuilder();
                newBuilder.header("Content-Type", "application/json");
                newBuilder.header("Accept", "application/json");
                newBuilder.method(orignaRequest.method(), orignaRequest.body());
                newBuilder.addHeader("X-Device-Model", SystemUtils.getDeviceModel());
                if (Commons.isRelase) {
                    newBuilder.addHeader("X-Auth-Options", "1e7904f32c4fcfd59b8a524d1bad1d8a.qg0J9zG*FIkBk^vo");
                }

                Request request = newBuilder.build();
                return chain.proceed(request);
            }
        };

        builder.readTimeout(HTTP_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(HTTP_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(HTTP_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(interceptor).addInterceptor(headerInterceptor)
                .retryOnConnectionFailure(true);

        OkHttpClient mClient = builder.build();


        Retrofit retrofit = new Retrofit.Builder()
                .client(mClient)
                .baseUrl(Commons.NEW_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        mServerApi = retrofit.create(ServerApi.class);
    }

    private static NetWorkManager mInstance; // 单例

    private static LoadingProgressDialog loadingProgressDialog;

    /**
     * 获取游戏API请求实例
     *
     * @return 单例对象
     */
    public static NetWorkManager getInstance(boolean flag) {
        if (mInstance == null) {
            synchronized (NetWorkManager.class) {
                if (mInstance == null) {
                    mInstance = new NetWorkManager();
                }
            }
        }

        synchronized (NetWorkManager.class) {
            if (flag) {
                if (loadingProgressDialog == null) {
                    loadingProgressDialog = new LoadingProgressDialog(YougyApplicationManager.getInstance().getApplicationContext());
                    loadingProgressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    loadingProgressDialog.show();
                } else {
                    if (!loadingProgressDialog.isShowing()) {
                        loadingProgressDialog.show();
                    }
                }
            }
        }


        return mInstance;
    }

    public static NetWorkManager getInstance() {
        return getInstance(true);
    }


    public static Observable<List<ParsedQuestionItem>> queryQuestionItemList(String userId, String bookId
            , String itemId, Integer cursor) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询问答题目列表:queryQuestionItemList");
        return getInstance().getServerApi().queryTotalQuestionList(userId, bookId, itemId, cursor)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog))
                .compose(RxResultHelper.parseQuestion());
    }


    public static Observable<STSbean> queryReplyRequest(String userId) {
        return getInstance().getServerApi().queryReplyRequest(userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> postReply(String userId, String itemId, String examId, String picContent, String txtContent, String replyUseTime) {
        return getInstance().getServerApi().postReply(userId, itemId, examId, picContent, txtContent, replyUseTime)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> postReply(String userId, String data) {
        return getInstance().getServerApi().postReply(userId, data)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public Observable<Object> queryToken(String userId) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询云信对应token:queryToken");
        return getServerApi().queryToken(userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<DownloadInfo>> downloadBook(String userId, String bookId) {
        LogUtils.e("FH", "!!!!!调用ServerApi下载图书:downloadBook");
        return getInstance().getServerApi().downloadBook(userId, bookId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<HomeworkBookSummary>> queryHomeworkBookList(String userId, String homeworkFitGradeName) {
        LogUtils.e("FH", "!!!!!调用ServerApi获取作业本列表:queryHomeworkBookList");
        return getInstance().getServerApi().queryHomeworkBookList(userId, homeworkFitGradeName)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<HomeworkBookDetail>> queryHomeworkBookDetail(Integer homeworkId) {
        LogUtils.e("FH", "!!!!!调用ServerApi获取作业本内作业(考试)列表:queryHomeworkBookDetail");
        return getInstance().getServerApi().queryHomeworkBookDetail(homeworkId, "II02", true)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<HomeworkBookDetail>> queryHomeworkBookDetail_Anwser(Integer homeworkId) {
        LogUtils.e("FH", "!!!!!调用ServerApi获取作业本内问答列表:queryHomeworkBookDetail");
        return getInstance().getServerApi().queryHomeworkBookDetail(homeworkId, "II01", true)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<BookInfo>> queryBook(String bookId, String userId) {
        LogUtils.e("FH", "!!!!!调用ServerApi获取图书信息:queryBook");
        return getInstance().getServerApi().queryBook(bookId, userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> refreshHomeworkBook(Integer homeworkId) {
        LogUtils.e("FH", "!!!!!调用ServerApi刷新作业本:refreshHomeworkBook");
        return getInstance().getServerApi().refreshHomeworkBook(homeworkId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<QuestionReplyDetail>> queryReplyDetail(Integer examId, Integer itemId, String userId) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询学生解答详情:queryReplyDetail");
        return getInstance().getServerApi().reviewComment(examId, itemId, userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog))
                .compose(RxResultHelper.parseReplyDetail());
    }

    public static Observable<List<HomeworkDetail>> queryExam(String examId, String examStartTime) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询考试:queryExam");
        return getInstance().getServerApi().queryExam(examId, examStartTime)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog))
                .compose(RxResultHelper.parseHomeworkQuestion());
    }

    public static Observable<List<QuestionReplySummary>> queryReplySummary(Integer examId, Integer userId) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询学生解答摘要:queryReplySummary");
        return getInstance().getServerApi().queryReply(examId, userId, null)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> deleteMistake_thorough(Integer homeworkId, String itemId) {
        LogUtils.e("FH", "!!!!!调用ServerApi移除错题(彻底删除):deleteMistake_thorough");
        return getInstance().getServerApi().removeHomeworkExcerpt(homeworkId, "{\"item\":" + itemId + "}")
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> deleteMistake(Integer homeworkId, String itemId) {
        LogUtils.e("FH", "!!!!!调用ServerApi移除错题(标记为删除):deleteMistake");
        return getInstance().getServerApi().modifyHomeworkExcerpt(homeworkId
                , "{\"item\":" + itemId + ",\"extra\":{\"deleted\":true}}")
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> setMistakeLastScore(Integer homeworkId, String itemId, int score) {
        LogUtils.e("FH", "!!!!!调用ServerApi设置错题上次自评结果:setMistakeLastScore");
        return getInstance().getServerApi().modifyHomeworkExcerpt(homeworkId
                , "{\"item\":" + itemId + ",\"extra\":{\"lastScore\":" + score + "}}")
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<CourseInfo>> queryCourse(Integer userId) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询课程:queryCourse");
        return getInstance().getServerApi().queryCourse(userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<HomeworkDetail>> queryAnswer(String classId, String bookId, Integer cursor) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询问答:queryAnswer");
        return getInstance().getServerApi().queryAnswer(classId, bookId, cursor)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog))
                .compose(RxResultHelper.parseHomeworkQuestion());
    }

    public static Observable<List<QuestionReplySummary>> queryReply(Integer examId, Integer userId, String replyId) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询考试回答情况:queryReply");
        return getInstance().getServerApi().queryReply(examId, userId, replyId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog))
                .compose(RxResultHelper.parseReply());
    }

    public static Observable<List<CartItem>> queryCart(String userId) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询用户购物车:queryCart");
        return getInstance(false).getServerApi().queryCart(userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> removeCart(RemoveRequestObj removeRequestObj) {
        LogUtils.e("FH", "!!!!!调用ServerApi删除多项收藏夹:removeCart");
        return getInstance().getServerApi().removeCart(removeRequestObj)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<OrderDetailBean>> queryOrderTree(String orderId) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询订单树,包含分拆的子订单");
        return getInstance().getServerApi().queryOrderTree(orderId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Pair<Integer , List<OrderSummary>>> queryMyOrderList(String orderOwner , Integer ps ,Integer pn) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询我的订单");
        return getInstance().getServerApi().queryOrderSole(orderOwner , ps , pn)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult_new(loadingProgressDialog));
    }

    public static Observable<List<QueryQRStrObj>> checkOrder(String orderId, int orderOwner, double orderPrice, int payMethod) {
        LogUtils.e("FH", "!!!!!调用ServerApi进行订单结算获取二维码:checkOrder");
        return getInstance().getServerApi().checkOrder(orderId, orderOwner, orderPrice, payMethod)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> isOrderPaySuccess(String orderId, int orderOwner) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询订单支付情况:isOrderPaySuccess");
        return getInstance().getServerApi().isOrderPaySuccess(orderId, orderOwner)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> cancelOrder(String orderId, Integer orderOwner) {
        LogUtils.e("FH", "!!!!!调用ServerApi取消订单:cancelOrder");
        return getInstance().getServerApi().cancelOrder(orderId, orderOwner)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<OrderInfo>> queryOrder(String orderOwner, String orderStatus) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询我的订单列表:queryOrder");
        return getInstance().getServerApi().queryMyOrderList(orderOwner, orderStatus)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<OrderIdObj>> createOrder(CreateOrderRequestObj createOrderRequestObj) {
        LogUtils.e("FH", "!!!!!调用ServerApi创建订单:createOrder");
        return getInstance().getServerApi().createOrder(createOrderRequestObj)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> removeFavor(Integer userId, Integer bookId) {
        LogUtils.e("FH", "!!!!!调用ServerApi删除单项收藏夹:removeFavor");
        return getInstance().getServerApi().removeFavor(userId, bookId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> removeFavor(RemoveRequestObj removeRequestObj) {
        LogUtils.e("FH", "!!!!!调用ServerApi删除多项收藏夹:removeFavor");
        return getInstance().getServerApi().removeFavor(removeRequestObj)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<Favor>> queryFavor(Integer userId) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询收藏夹:queryFavor");
        return getInstance().getServerApi().queryFavor(userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<Student>> login(NewLoginReq req) {
        return getInstance().getServerApi().login(req)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    /**
     * 根据类别获取图书信息
     */
    public static Observable<BaseResult<List<BookInfo>>> queryBookInfo(BookStoreQueryBookInfoReq req) {
        return getInstance().getServerApi().queryBookInfo(req)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.dismissDialog(loadingProgressDialog))
                ;
    }

    /**
     * 移除架上图书
     */
    public static Observable<Object> removeBookInBookcase(Integer bookId, Integer userId) {
        return getInstance().getServerApi().removeBookInBookcase(bookId, userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    /**
     * 获取版本号
     */
    public static Observable<Version> getVersion() {
        LogUtils.e("FH", "!!!!!调用ServerApi获取版本号:getVersion");
        return getInstance().getServerApi().getVersion()
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    /**
     * 添加架上图书
     */
    public static Observable<Object> addBookToBookcase(Integer bookId, Integer userId) {
        return getInstance().getServerApi().addBookToBookcase(bookId, userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> bindDevice(Integer userId, String deviceId) {
        LogUtils.e("FH", "!!!!!调用ServerApi绑定设备:bindDevice");
        return getInstance().getServerApi().bindDevice(userId, deviceId, SystemUtils.getDeviceModel())
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> closeHomework(Integer examId, Integer courseId, String userId) {
        LogUtils.e("FH", "!!!!!调用ServerApi进行作业评定,关闭单学生单次作业并写入错题本");
        return getInstance().getServerApi().closeHomework(examId, courseId, userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<AliyunData> queryDownloadAliyunData() {
        return getInstance(false).getServerApi().queryDownloadAliyunData(SpUtils.getUserId())
                .compose(RxResultHelper.handleResult(null));
    }

    public static Observable<AliyunData> queryUploadAliyunData() {
        return getInstance(false).getServerApi().queryUploadAliyunData(SpUtils.getUserId())
                .compose(RxResultHelper.handleResult(null));
    }

    public static Observable<Object> postComment(String replyId, String score, String content, String replyCommentator) {
        return getInstance().getServerApi().postComment(replyId, score, content, replyCommentator)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<STSbean> postCommentRequest(String replyId) {
        return getInstance().getServerApi().postCommentRequest(replyId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    /**
     * 图书分类查询
     */
    public static Observable<List<CategoryInfo>> queryBookCategoryInfo(BookStoreCategoryReq req) {
        return getInstance().getServerApi().queryBookCategory(req)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }


    /**
     * 书城首页信息
     */
    public static Observable<List<BookInfo>> queryBookShopHomeInfo(BookStoreHomeReq req) {
        return getInstance().getServerApi().queryBookShopHomeInfo(req)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> appendFavor(Integer userId, Integer bookId) {
        LogUtils.e("FH", "!!!!!调用ServerApi添加单项收藏夹:appendFavor");
        return getInstance().getServerApi().appendFavor(userId, bookId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> appendCart(Integer userId, Integer bookId) {
        LogUtils.e("FH", "!!!!!调用ServerApi添加单项购物车:appendCart");
        return getInstance().getServerApi().appendCart(userId, bookId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<BookInfo>> queryShopBook(Integer userId, Integer bookId
            , String bookTitle, Integer bookVersion, Integer bookCategory, Integer bookCategoryMatch) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询商城图书:queryShopBook");
        return getInstance().getServerApi().queryShopBook(userId, bookId, bookTitle, bookVersion, bookCategory, bookCategoryMatch)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }
    public static Observable<List<BookInfo>> promoteBook(Integer userId, int bookId) {
        LogUtils.e("FH", "!!!!!调用ServerApi获取推荐书列表:promoteBook");
        return getInstance().getServerApi().promoteBook(userId, bookId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }



}
