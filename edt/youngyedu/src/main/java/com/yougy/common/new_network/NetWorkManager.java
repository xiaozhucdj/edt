package com.yougy.common.new_network;

import android.text.TextUtils;
import android.util.Pair;
import android.view.WindowManager;

import com.yougy.anwser.BaseResult;
import com.yougy.anwser.CourseInfo;
import com.yougy.anwser.ParsedQuestionItem;
import com.yougy.anwser.STSbean;
import com.yougy.common.bean.AliyunData;
import com.yougy.common.global.Commons;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.media.file.OssInfoBean;
import com.yougy.common.model.Version;
import com.yougy.common.protocol.request.BookStoreHomeReq;
import com.yougy.common.protocol.request.NewBookShelfReq;
import com.yougy.common.protocol.request.NewDeleteNoteReq;
import com.yougy.common.protocol.request.NewInsertAllNoteReq;
import com.yougy.common.protocol.request.NewLoginReq;
import com.yougy.common.protocol.request.NewQueryNoteReq;
import com.yougy.common.protocol.request.NewUnBindDeviceReq;
import com.yougy.common.protocol.request.NewUpdateNoteReq;
import com.yougy.common.protocol.request.PromotionReq;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.SystemUtils;
import com.yougy.home.bean.DataCountInBookNode;
import com.yougy.home.bean.InsertNoteId;
import com.yougy.home.bean.NoteInfo;
import com.yougy.homework.bean.HomeworkBookDetail;
import com.yougy.homework.bean.HomeworkBookSummary;
import com.yougy.homework.bean.HomeworkDetail;
import com.yougy.homework.bean.HomeworkSummarySumInfo;
import com.yougy.homework.bean.QuestionReplyDetail;
import com.yougy.homework.bean.QuestionReplySummary;
import com.yougy.homework.bean.TeamBean;
import com.yougy.init.bean.Student;
import com.yougy.shop.AllowOrderRequestObj;
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
import com.yougy.shop.bean.PromotionResult;
import com.yougy.shop.bean.RemoveRequestObj;
import com.yougy.task.bean.StageTaskBean;
import com.yougy.task.bean.SubmitReplyBean;
import com.yougy.task.bean.Task;
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

import static com.yougy.common.global.Constant.IICODE_01;
import static com.yougy.common.global.Constant.IICODE_02;
import static com.yougy.common.global.Constant.IICODE_03;

/**
 */
public final class NetWorkManager {

    private static final int HTTP_CONNECTION_TIMEOUT = 15 * 1000;

    private ServerApi mServerApi;

    public ServerApi getServerApi() {
        return mServerApi;
    }

    MyHttpLoggingInterceptor interceptor;

    public void openBODY() {
        if (interceptor != null) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }
    }

    private NetWorkManager() {
        // 配置日志输出，因为Retrofit2不支持输出日志，只能用OkHttp来输出
        interceptor = new MyHttpLoggingInterceptor();
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
//                if (Commons.isRelase) {
                newBuilder.addHeader("X-Auth-Options", "1e7904f32c4fcfd59b8a524d1bad1d8a.qg0J9zG*FIkBk^vo");
//                }

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
                    loadingProgressDialog = new LoadingProgressDialog(YoungyApplicationManager.getInstance().getApplicationContext());
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

    public static Observable<Object> postReply(String userId, String itemId, String examId, String picContent, String txtContent, String replyUseTime, String replyCreateTime) {
        return getInstance().getServerApi().postReply(userId, itemId, examId, picContent, txtContent, replyUseTime, replyCreateTime)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> postReply(String userId, String data) {
        return getInstance().getServerApi().postReply(userId, data)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> allocationMutualHomework(String examId) {
        return getInstance().getServerApi().allocationMutualHomework(examId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public Observable<Object> queryToken(String userId) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询云信token:queryToken");
        return getServerApi().queryToken(userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public Observable<Object> updateToken(String userId) {
        LogUtils.e("FH", "!!!!!调用ServerApi更新云信token:updateToken");
        return getServerApi().updateToken(userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<DownloadInfo>> downloadBook(String userId, String bookId) {
        LogUtils.e("FH", "!!!!!调用ServerApi下载图书:downloadBook");
        return getInstance().getServerApi().downloadBook(userId, bookId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<OssInfoBean>> downloadFile(String bookId, String atchTypeCode) {
        LogUtils.e("FH", "!!!!!调用ServerApi下载图书:downloadBook2");
        return getInstance().getServerApi().downloadFile(bookId, atchTypeCode)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }


    public static Observable<List<HomeworkBookSummary>> queryHomeworkBookList(String userId, String homeworkFitGradeName) {
        LogUtils.e("FH", "!!!!!调用ServerApi获取作业本列表:queryHomeworkBookList");
        return getInstance().getServerApi().queryHomeworkBookList(userId, homeworkFitGradeName)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<HomeworkBookDetail>> queryHomeworkBookDetail_New(Integer homeworkId, String examTypeCode, String statusCode) {
        LogUtils.e("FH", "!!!!!调用ServerApi获取作业本内作业(考试)列表:queryHomeworkBookDetail_New");
        return getInstance().getServerApi().queryHomeworkBookDetail_New(homeworkId, examTypeCode, true, statusCode)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<HomeworkBookDetail>> queryHomeworkBookDetail(Integer homeworkId) {
        LogUtils.e("FH", "!!!!!调用ServerApi获取作业本内作业(考试)列表:queryHomeworkBookDetail");
        return getInstance().getServerApi().queryHomeworkBookDetail(homeworkId, StringUtils.smartCombineStrings("[", "]", "\"", "\"", ",", IICODE_02, IICODE_03), true)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }


    public static Observable<List<QuestionReplyDetail>> queryHomeworkExcerptWithReply(Integer homeworkId, Integer examFitCourseBookCursor) {
        LogUtils.e("FH", "!!!!!调用ServerApi获取作业本内作业(考试)列表:queryHomeworkBookDetail");
        return getInstance().getServerApi().queryHomeworkExcerptWithReply(homeworkId, examFitCourseBookCursor)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog))
                .compose(RxResultHelper.parseReplyDetail());
    }


    public static Observable<List<HomeworkBookDetail>> queryHomeworkBookDetail_Anwser(Integer homeworkId) {
        LogUtils.e("FH", "!!!!!调用ServerApi获取作业本内问答列表:queryHomeworkBookDetail");
        return getInstance().getServerApi().queryHomeworkBookDetail(homeworkId, StringUtils.smartCombineStrings("[", "]", "\"", "\"", ",", IICODE_01), true)
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

    public static Observable<List<QuestionReplyDetail>> queryReplyDetail2(Integer examId, Integer itemId, String replyCommentator, long replyCreator) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询学生互评解答详情:queryReplyDetail");
        return getInstance().getServerApi().reviewComment2(examId, itemId, replyCommentator, replyCreator)
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

    public static Observable<List<HomeworkSummarySumInfo>> sumReplyStudent(Integer examId, Integer studentId) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询学生考试分数总和:queryReplySummary");
        return getInstance().getServerApi().sumReplyStudent(examId, studentId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> deleteMistake_thorough(Integer homeworkId, String itemId) {
        LogUtils.e("FH", "!!!!!调用ServerApi移除错题(彻底删除):deleteMistake_thorough");
        return getInstance().getServerApi().removeHomeworkExcerpt(homeworkId, "{\"item\":" + itemId + "}")
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> deleteMistake(Integer homeworkId, String replyId) {
        LogUtils.e("FH", "!!!!!调用ServerApi移除错题(标记为删除):deleteMistake");
        return getInstance().getServerApi().modifyHomeworkExcerpt(homeworkId
                , "{\"reply\":" + replyId + ",\"extra\":{\"deleted\":true}}")
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> setMistakeLastScore(Integer homeworkId, String replyId, int score) {
        LogUtils.e("FH", "!!!!!调用ServerApi设置错题上次自评结果:setMistakeLastScore");
        return getInstance().getServerApi().modifyHomeworkExcerpt(homeworkId
                , "{\"reply\":" + replyId + ",\"extra\":{\"lastScore\":" + score + "}}")
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> setMistakeExcerpt(Integer homeworkId, String replyId, String key, int value) {
        LogUtils.e("FH", "!!!!!调用ServerApi设置错题新增参数key"+key);
        return getInstance().getServerApi().modifyHomeworkExcerpt(homeworkId
                , "{\"reply\":" + replyId + ",\"extra\":{\"" + key + "\":" + value + "}}")
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }


    public static Observable<List<CourseInfo>> queryCourse(Integer userId) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询课程:queryCourse");
        return getInstance().getServerApi().queryCourse(userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<QuestionReplySummary>> queryReply(Integer examId, Integer userId, String replyId) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询考试回答情况:queryReply");
        return getInstance().getServerApi().queryReply(examId, userId, replyId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog))
                .compose(RxResultHelper.parseReply());
    }

    public static Observable<List<QuestionReplyDetail>> queryQuestions2BeMarked(String userId) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询考试回答情况:queryReply");
        return getInstance().getServerApi().queryQuestions2BeMarked(userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog))
                .compose(RxResultHelper.parseReplyDetail());
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

    public static Observable<Object> removeOrder(String orderId, String orderOwner) {
        LogUtils.e("FH", "!!!!!调用ServerApi删除订单:removeOrder");
        return getInstance().getServerApi().removeOrder(orderId, orderOwner)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<OrderDetailBean>> queryOrderTree(String orderId) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询订单树,包含分拆的子订单");
        return getInstance().getServerApi().queryOrderTree(orderId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Pair<Integer, List<OrderSummary>>> queryMyOrderList(String orderOwner
            , Integer ps, Integer pn) {
        LogUtils.e("FH", "!!!!!调用ServerApi查询我的订单");
        return getInstance().getServerApi().queryOrderAbbr(orderOwner, ps, pn, "{\"order\":\"DESC\"}")
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

    public static Observable<Object> allowOrder(AllowOrderRequestObj allowOrderRequestObj) {
        LogUtils.e("FH", "!!!!!调用ServerApi做订单查重:allowOrder");
        return getInstance().getServerApi().allowOrder(allowOrderRequestObj)
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

    public Observable<List<Student>> login(NewLoginReq req) {
        return getServerApi().login(req)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<Student>> login(String userName, String userPassword, String userToken, String deviceId, String userId) {
        return getInstance(false).getServerApi().login(userName, userPassword, userToken, deviceId, userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(null));
    }

    /**
     * 根据类别获取图书信息
     * add by jiangliang
     * <p>
     * modified by FH
     * 此处原先的用法是req里如果有不使用的字段,则默认传-1或者"",但是这会导致服务器返回的数据为null
     * 因此改成不使用的字段,直接不传这个字段至服务器
     */
    public static Observable<BaseResult<List<BookInfo>>> queryBookInfo(BookStoreQueryBookInfoReq req) {
        return getInstance().getServerApi().queryBookInfo(req.getBookId() == -1 ? null : req.getBookId()
                , req.getBookCategory() == -1 ? null : req.getBookCategory()
                , req.getBookCategoryMatch() == -1 ? null : req.getBookCategoryMatch()
                , req.getUserId() == -1 ? null : req.getUserId()
                , req.getBookVersion() == -1 ? null : req.getBookVersion()
                , TextUtils.isEmpty(req.getBookTitle()) ? null : req.getBookTitle()
                , TextUtils.isEmpty(req.getBookTitleMatch()) ? null : req.getBookTitleMatch()
                , req.getPs() == -1 ? null : req.getPs()
                , req.getPn() == -1 ? null : req.getPn())
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

    public static Observable<Object> unbindDevice(NewUnBindDeviceReq unBindDeviceReq) {
        LogUtils.e("FH", "!!!!!调用ServerApi解绑设备:unbindDevice");
        return getInstance().getServerApi().unbindDevice(unBindDeviceReq)
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

    public static Observable<Object> postComment(String replyId, String score, String content, String replyCommentator, String originalReplyCommentator) {
        return getInstance().getServerApi().postComment(replyId, score, content, replyCommentator, originalReplyCommentator)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<STSbean> postCommentRequest(String replyId, String userId) {
        return getInstance().getServerApi().postCommentRequest(replyId, userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }


    public static Observable<List<NoteInfo>> queryNote(NewQueryNoteReq req) {
        return getInstance().getServerApi().queryNote(req)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }


    public static Observable<Object> deleteNote(NewDeleteNoteReq req) {
        return getInstance().getServerApi().deleteNote(req)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<PromotionResult>> queryPromotion(PromotionReq req) {
        return getInstance().getServerApi().queryPromotion(req)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<InsertNoteId>> insertAllNote(NewInsertAllNoteReq req) {
        return getInstance().getServerApi().insertAllNoteApi(req)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<Object> updateNote(NewUpdateNoteReq req) {
        return getInstance().getServerApi().updateNote(req)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<com.yougy.init.bean.BookInfo>> getBookShelf(NewBookShelfReq req) {
        return getInstance().getServerApi().getBookShelf(req)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }


    /**
     * 图书分类查询
     */
    public static Observable<List<CategoryInfo>> queryBookCategoryInfo() {
        return getInstance().getServerApi().queryBookCategory()
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

    public static Observable<BaseResult<List<Task>>> queryTasks(int homeworkId, int contentBookLink, int pn, int ps,String contentStatusCode) {
        return getInstance().getServerApi().queryTasks(homeworkId, contentBookLink, pn, ps,contentStatusCode)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.dismissDialog(loadingProgressDialog));
    }

    public static Observable<TeamBean> querySchoolTeamByStudentAndExam(String studentId, String examId) {
        return getInstance().getServerApi().querySchoolTeamByStudentAndExam(studentId, examId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    /**
     * 查询任务内容  练习  资料 签字  详情
     *
     * @param dramaId
     * @param stageTypeCode SR01 内容
     *                      SR02 资料
     *                      SR03 练习
     *                      SR04 签字
     * @return
     */
    public static Observable<List<StageTaskBean>> queryStageTask(String dramaId, String stageTypeCode, int userId) {
        return getInstance().getServerApi().queryStageTask(dramaId, stageTypeCode, userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    /**
     * OOS 上传
     *
     * @param userId
     * @return
     */
    public static Observable<STSbean> uploadTaskPracticeOOS(Integer userId) {
        return getInstance().getServerApi().uploadTaskPracticeOOS(userId)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    public static Observable<List<SubmitReplyBean>> submitTaskPracticeServer(Integer userId, String data) {
        return getInstance().getServerApi().submitTaskPracticeServer(userId, data)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

    /**
     * 统计作业本中作业和问答的数量并且按章节统计返回
     * homeworkId 作业本Id
     * examTypeCode 作业类型（可以传数组）　II0x
     */
    public static Observable<List<DataCountInBookNode>> getItemCountBaseOnBookNode(Integer homeworkId, String examTypeCode) {
        return getInstance().getServerApi().countQuestionCount4Cursor(homeworkId, examTypeCode)
                .compose(RxSchedulersHelper.io_main())
                .compose(RxResultHelper.handleResult(loadingProgressDialog));
    }

}
