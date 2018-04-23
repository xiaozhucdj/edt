package com.yougy.common.new_network;

import android.util.Log;

import com.yougy.anwser.CourseInfo;
import com.yougy.anwser.ParsedQuestionItem;
import com.yougy.anwser.STSbean;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.global.Commons;
import com.yougy.homework.bean.HomeworkBookDetail;
import com.yougy.homework.bean.HomeworkBookSummary;
import com.yougy.homework.bean.HomeworkDetail;
import com.yougy.homework.bean.QuestionReplyDetail;
import com.yougy.homework.bean.QuestionReplySummary;
import com.yougy.shop.CreateOrderRequestObj;
import com.yougy.shop.QueryQRStrObj;
import com.yougy.shop.bean.BookInfo;
import com.yougy.shop.bean.CartItem;
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

    private static final int HTTP_CONNECTION_TIMEOUT = 60 * 1000;

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

        if (flag && BaseActivity.getForegroundActivity() != null) {
            if (loadingProgressDialog == null || !loadingProgressDialog.isShowing()){
                loadingProgressDialog = new LoadingProgressDialog(BaseActivity.getForegroundActivity());
                loadingProgressDialog.show();
            }
        }
        return mInstance;
    }

    public static NetWorkManager getInstance() {
        return getInstance(true);
    }


    public static Observable<List<ParsedQuestionItem>> queryQuestionItemList(String userId, String bookId
            , String itemId, Integer cursor) {
        Log.v("FH", "!!!!!调用ServerApi查询问答题目列表:queryQuestionItemList");
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
        Log.v("FH", "!!!!!调用ServerApi查询云信对应token:queryToken");
        return getServerApi().queryToken(userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<DownloadInfo>> downloadBook(String userId, String bookId) {
        Log.v("FH", "!!!!!调用ServerApi下载图书:downloadBook");
        return getInstance().getServerApi().downloadBook(userId, bookId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<HomeworkBookSummary>> queryHomeworkBookList(String userId, String homeworkFitGradeName) {
        Log.v("FH", "!!!!!调用ServerApi获取作业本列表:queryHomeworkBookList");
        return getInstance().getServerApi().queryHomeworkBookList(userId, homeworkFitGradeName)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<HomeworkBookDetail>> queryHomeworkBookDetail(Integer homeworkId) {
        Log.v("FH", "!!!!!调用ServerApi获取作业本内作业(考试)列表:queryHomeworkBookDetail");
        return getInstance().getServerApi().queryHomeworkBookDetail(homeworkId, "II02")
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<HomeworkBookDetail>> queryHomeworkBookDetail_Anwser(Integer homeworkId) {
        Log.v("FH", "!!!!!调用ServerApi获取作业本内问答列表:queryHomeworkBookDetail");
        return getInstance().getServerApi().queryHomeworkBookDetail(homeworkId, "II01")
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<BookInfo>> queryBook(String bookId, String userId) {
        Log.v("FH", "!!!!!调用ServerApi获取图书信息:queryBook");
        return getInstance().getServerApi().queryBook(bookId, userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> refreshHomeworkBook(Integer homeworkId) {
        Log.v("FH", "!!!!!调用ServerApi刷新作业本:refreshHomeworkBook");
        return getInstance().getServerApi().refreshHomeworkBook(homeworkId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<QuestionReplyDetail>> queryReplyDetail(Integer examId, Integer itemId, String userId) {
        Log.v("FH", "!!!!!调用ServerApi查询学生解答详情:queryReplyDetail");
        return getInstance().getServerApi().reviewComment(examId, itemId, userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog))
                .compose(RxResultHelper.parseReplyDetail());
    }

    public static Observable<List<HomeworkDetail>> queryHomeworkDetail(Integer examId) {
        Log.v("FH", "!!!!!调用ServerApi查询作业详情:queryHomeworkDetail");
        return getInstance().getServerApi().queryHomeworkDetail(examId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog))
                .compose(RxResultHelper.parseHomeworkQuestion());
    }

    public static Observable<List<HomeworkDetail>> queryHomeworkDetailList(String examIds) {
        Log.v("FH", "!!!!!调用ServerApi查询多个作业详情:queryHomeworkDetailList");
        return getInstance().getServerApi().queryHomeworkDetailList(examIds)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog))
                .compose(RxResultHelper.parseHomeworkQuestion());
    }

    public static Observable<List<QuestionReplySummary>> queryReplySummary(Integer examId, Integer userId) {
        Log.v("FH", "!!!!!调用ServerApi查询学生解答摘要:queryReplySummary");
        return getInstance().getServerApi().queryReply(examId, userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> deleteMistake_thorough(Integer homeworkId, String itemId) {
        Log.v("FH", "!!!!!调用ServerApi移除错题(彻底删除):deleteMistake_thorough");
        return getInstance().getServerApi().removeHomeworkExcerpt(homeworkId, "{\"item\":" + itemId + "}")
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> deleteMistake(Integer homeworkId, String itemId) {
        Log.v("FH", "!!!!!调用ServerApi移除错题(标记为删除):deleteMistake");
        return getInstance().getServerApi().modifyHomeworkExcerpt(homeworkId
                , "{\"item\":" + itemId + ",\"extra\":{\"deleted\":true}}")
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> setMistakeLastScore(Integer homeworkId, String itemId, int score) {
        Log.v("FH", "!!!!!调用ServerApi设置错题上次自评结果:setMistakeLastScore");
        return getInstance().getServerApi().modifyHomeworkExcerpt(homeworkId
                , "{\"item\":" + itemId + ",\"extra\":{\"lastScore\":" + score + "}}")
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<CourseInfo>> queryCourse(Integer userId) {
        Log.v("FH", "!!!!!调用ServerApi查询课程:queryCourse");
        return getInstance().getServerApi().queryCourse(userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<HomeworkDetail>> queryAnswer(String classId, String bookId, Integer cursor) {
        Log.v("FH", "!!!!!调用ServerApi查询问答:queryAnswer");
        return getInstance().getServerApi().queryAnswer(classId, bookId, cursor)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog))
                .compose(RxResultHelper.parseHomeworkQuestion());
    }

    public static Observable<List<QuestionReplySummary>> queryReply(Integer examId, Integer userId) {
        Log.v("FH", "!!!!!调用ServerApi查询考试回答情况:queryReply");
        return getInstance().getServerApi().queryReply(examId, userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<CartItem>> queryCart(String userId) {
        Log.v("FH", "!!!!!调用ServerApi查询用户购物车:queryCart");
        return getInstance(false).getServerApi().queryCart(userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> removeCart(RemoveRequestObj removeRequestObj) {
        Log.v("FH", "!!!!!调用ServerApi删除多项收藏夹:removeCart");
        return getInstance().getServerApi().removeCart(removeRequestObj)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<OrderDetailBean>> queryOrderTree(String orderId) {
        Log.v("FH", "!!!!!调用ServerApi查询订单树,包含分拆的子订单");
        return getInstance().getServerApi().queryOrderTree(orderId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<OrderSummary>> queryMyOrderList(String orderOwner) {
        Log.v("FH", "!!!!!调用ServerApi查询我的订单");
        return getInstance().getServerApi().queryOrderSole(orderOwner)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<QueryQRStrObj>> checkOrder(String orderId, int orderOwner, double orderPrice, int payMethod) {
        Log.v("FH", "!!!!!调用ServerApi进行订单结算获取二维码:checkOrder");
        return getInstance().getServerApi().checkOrder(orderId, orderOwner, orderPrice, payMethod)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> isOrderPaySuccess(String orderId, int orderOwner) {
        Log.v("FH", "!!!!!调用ServerApi查询订单支付情况:isOrderPaySuccess");
        return getInstance().getServerApi().isOrderPaySuccess(orderId, orderOwner)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> cancelOrder(String orderId, Integer orderOwner) {
        Log.v("FH", "!!!!!调用ServerApi取消订单:cancelOrder");
        return getInstance().getServerApi().cancelOrder(orderId, orderOwner)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<OrderInfo>> queryOrder(String orderOwner, String orderStatus) {
        Log.v("FH", "!!!!!调用ServerApi查询我的订单列表:queryOrder");
        return getInstance().getServerApi().queryMyOrderList(orderOwner, orderStatus)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<OrderIdObj>> createOrder(CreateOrderRequestObj createOrderRequestObj) {
        Log.v("FH", "!!!!!调用ServerApi创建订单:createOrder");
        return getInstance().getServerApi().createOrder(createOrderRequestObj)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> removeFavor(Integer userId, Integer bookId) {
        Log.v("FH", "!!!!!调用ServerApi删除单项收藏夹:removeFavor");
        return getInstance().getServerApi().removeFavor(userId, bookId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> removeFavor(RemoveRequestObj removeRequestObj) {
        Log.v("FH", "!!!!!调用ServerApi删除多项收藏夹:removeFavor");
        return getInstance().getServerApi().removeFavor(removeRequestObj)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<Favor>> queryFavor(Integer userId) {
        Log.v("FH", "!!!!!调用ServerApi查询收藏夹:queryFavor");
        return getInstance().getServerApi().queryFavor(userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }


}
