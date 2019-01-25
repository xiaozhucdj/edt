package com.yougy.common.new_network;

import com.yougy.anwser.BaseResult;
import com.yougy.anwser.CourseInfo;
import com.yougy.anwser.OriginQuestionItem;
import com.yougy.anwser.STSbean;
import com.yougy.common.bean.AliyunData;
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

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DefaultField;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 游戏用的请求管理器
 */
public interface ServerApi {

    /**
     * 查询题目整体(列表)
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"queryItem"})
    Observable<BaseResult<List<OriginQuestionItem>>> queryTotalQuestionList(@Field("userId") String userId
            , @Field("bookId") String bookId, @Field("itemId") String itemId, @Field("cursor") Integer cursor);

    /**
     * 问题解答上传oss
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"postReplyRequest"})
    Observable<BaseResult<STSbean>> queryReplyRequest(@Field("userId") String userId);


    /**
     * 解答上传（单题）
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"postReply"})
    Observable<BaseResult<Object>> postReply(@Field("userId") String userId, @Field("itemId") String itemId
            , @Field("examId") String examId, @Field("picContent") String picContent, @Field("txtContent") String txtContent, @Field("replyUseTime") String replyUseTime, @Field("replyCreateTime") String replyCreateTime);

    /**
     * 解答上传（多题，作业）
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"postReply"})
    Observable<BaseResult<Object>> postReply(@Field("userId") String userId, @Field("data") String data);


    /**
     * 互评作业分配接口
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"allocationMutualHomework"})
    Observable<BaseResult<Object>> allocationMutualHomework(@Field("examId") String examId);

    /**
     * 按userId查询云信token
     */
    @FormUrlEncoded
    @POST("netease")
    @DefaultField(keys = {"m"}, values = {"queryToken"})
    Observable<BaseResult<Object>> queryToken(@Field("userId") String userId);

    /**
     * 按userId更新云信token,并返回更新后的token
     */
    @FormUrlEncoded
    @POST("netease")
    @DefaultField(keys = {"m"}, values = {"updateToken"})
    Observable<BaseResult<Object>> updateToken(@Field("userId") String userId);

    /**
     * 图书下载
     */
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"downloadBook"})
    Observable<BaseResult<List<DownloadInfo>>> downloadBook(@Field("userId") String userId, @Field("bookId") String bookId);

    /**
     * 作业本列表接口
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"queryHomework"})
    Observable<BaseResult<List<HomeworkBookSummary>>> queryHomeworkBookList(@Field("userId") String userId
            , @Field("homeworkFitGradeName") String homeworkFitGradeName);

    /**
     * 作业本内容作业(考试)列表接口
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"queryHomeworkSole"})
    Observable<BaseResult<List<HomeworkBookDetail>>> queryHomeworkBookDetail_New(
            @Field("homeworkId") Integer homeworkId, @Field("examTypeCode") String type, @Field("needRefresh") Boolean needRefresh
            , @Field("examStatusCode") String examStatusCode);

    /**
     * 作业本内容作业(考试)列表接口
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"queryHomeworkSole"})
    Observable<BaseResult<List<HomeworkBookDetail>>> queryHomeworkBookDetail(
            @Field("homeworkId") Integer homeworkId, @Field("examTypeCode") String type, @Field("needRefresh") Boolean needRefresh);

    /**
     * 获取图书信息
     */
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"queryBook"})
    Observable<BaseResult<List<BookInfo>>> queryBook(@Field("bookId") String bookId, @Field("userId") String userId);

    /**
     * 刷新作业本中所有作业的状态
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"refreshHomework"})
    Observable<BaseResult<Object>> refreshHomeworkBook(@Field("homeworkId") Integer homeworkId);

    /**
     * 查询解答详情
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"reviewComment"})
    Observable<BaseResult<List<QuestionReplyDetail>>> reviewComment(@Field("examId") Integer examId
            , @Field("itemId") Integer itemId, @Field("userId") String userId);

    /**
     * 查询学生互评解答详情
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"reviewComment"})
    Observable<BaseResult<List<QuestionReplyDetail>>> reviewComment2(@Field("examId") Integer examId
            , @Field("itemId") Integer itemId, @Field("replyCommentator") String replyCommentator, @Field("replyCreator") long replyCreator);

    /**
     * 查询错题列表
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"queryHomeworkExcerptWithReply"})
    Observable<BaseResult<List<QuestionReplyDetail>>> queryHomeworkExcerptWithReply(@Field("homeworkId") Integer homeworkId
            , @Field("examFitCourseBookCursor") Integer examFitCourseBookCursor);

    /**
     * 查询作业详情
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"queryExam"})
    Observable<BaseResult<List<HomeworkDetail>>> queryExam(@Field("examId") String examIds
            , @Field("examStartTime") String examStartTime);

    /**
     * 查询解答摘要
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"queryReply"})
    Observable<BaseResult<List<QuestionReplySummary>>> queryReply(@Field("examId") Integer examId
            , @Field("userId") Integer userId, @Field("replyId") String replyId);


    /**
     * 学生查询待批改的问答
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"queryQuestions2BeMarked"})
    Observable<BaseResult<List<QuestionReplyDetail>>> queryQuestions2BeMarked(@Field("userId") String userId);

    /**
     * 查询某次考试总分情况
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"sumReplyStudent"})
    Observable<BaseResult<List<HomeworkSummarySumInfo>>> sumReplyStudent(@Field("examId") Integer examId
            , @Field("studentId") Integer studentId);


    /**
     * 作业本错题移除
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"removeHomeworkExcerpt"})
    Observable<BaseResult<Object>> removeHomeworkExcerpt(@Field("homeworkId") Integer homeworkId
            , @Field("homeworkExcerpt") String homeworkExcerpt);

    /**
     * 作业本错题修改
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"modifyHomeworkExcerpt"})
    Observable<BaseResult<Object>> modifyHomeworkExcerpt(@Field("homeworkId") Integer homeworkId
            , @Field("homeworkExcerpt") String homeworkExcerpt);

    /**
     * 查询学生课程
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"queryCourse"})
    Observable<BaseResult<List<CourseInfo>>> queryCourse(@Field("userId") Integer userId);

    /**
     * 查询学生课程
     */
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"queryCart"})
    Observable<BaseResult<List<CartItem>>> queryCart(@Field("userId") String userId);

    /**
     * 批量删除购物车
     *
     * @param removeRequestObj
     * @return
     */
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"removeCart"})
    Observable<BaseResult<Object>> removeCart(@Body RemoveRequestObj removeRequestObj);

    /**
     * 删除订单(可批量)
     */
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"removeOrder"})
    Observable<BaseResult<Object>> removeOrder(@Field("orderId") String orderId, @Field("orderOwner") String orderOwner);

    /**
     * 获取订单树,包括订单的拆分的子订单信息
     */
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"queryOrderTree"})
    Observable<BaseResult<List<OrderDetailBean>>> queryOrderTree(@Field("orderId") String orderId);

    /**
     * 获取我的订单列表
     */
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m", "orderParent"}, values = {"queryOrderAbbr", "0"})
    Observable<BaseResult<List<OrderSummary>>> queryOrderAbbr(@Field("orderOwner") String orderOwner
            , @Field("ps") Integer ps, @Field("pn") Integer pn, @Field("orderCreateTime") String orderCreateTime);

    /**
     * 订单结算,获取支付二维码
     */
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"checkOrder"})
    Observable<BaseResult<List<QueryQRStrObj>>> checkOrder(@Field("orderId") String orderId, @Field("orderOwner") int orderOwner
            , @Field("orderPrice") double orderPrice, @Field("payMethod") int payMethod);

    /**
     * 查询支付状态
     */
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"closeOrder"})
    Observable<BaseResult<Object>> isOrderPaySuccess(@Field("orderId") String orderId, @Field("orderOwner") int orderOwner);

    /**
     * 取消订单
     *
     * @param orderId
     * @param orderOwner
     * @return
     */
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"cancelOrder"})
    Observable<BaseResult<Object>> cancelOrder(@Field("orderId") String orderId, @Field("orderOwner") Integer orderOwner);

    /**
     * 查询我的订单列表
     */
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"queryOrder"})
    Observable<BaseResult<List<OrderInfo>>> queryMyOrderList(@Field("orderOwner") String orderOwner, @Field("orderStatus") String orderStatus);

    /**
     * 创建个人订单
     */
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"createOrder"})
    Observable<BaseResult<List<OrderIdObj>>> createOrder(@Body CreateOrderRequestObj createOrderRequestObj);

    /**
     * 订单查重
     */
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"allowOrder"})
    Observable<BaseResult<Object>> allowOrder(@Body AllowOrderRequestObj allowOrderRequestObj);

    /**
     * 删除单个收藏夹
     *
     * @param userId
     * @param bookId
     * @return
     */
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"removeFavor"})
    Observable<BaseResult<Object>> removeFavor(@Field("userId") Integer userId, @Field("bookId") Integer bookId);

    /**
     * 批量删除收藏夹
     *
     * @param removeRequestObj
     * @return
     */
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"removeFavor"})
    Observable<BaseResult<Object>> removeFavor(@Body RemoveRequestObj removeRequestObj);

    /**
     * 查询收藏夹
     *
     * @param userId
     * @return
     */
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"queryFavor"})
    Observable<BaseResult<List<Favor>>> queryFavor(@Field("userId") Integer userId);

    /**
     * 登录接口
     */
    @FormUrlEncoded
    @POST("users")
    @DefaultField(keys = {"m"}, values = {"login"})
    Observable<BaseResult<List<Student>>> login(@Field("userName") String userName
            , @Field("userPassword") String userPassword, @Field("userToken") String userToken
            , @Field("deviceId") String deviceId, @Field("userId") String userId);

    @POST("users")
    Observable<BaseResult<List<Student>>> login(@Body NewLoginReq req);

    /**
     * 获取版本
     */
    @FormUrlEncoded
    @POST("version")
    @DefaultField(keys = {"m", "os"}, values = {"getAppVersion", "student"})
    Observable<BaseResult<Version>> getVersion();

    /**
     * 设备绑定
     *
     * @param userId
     * @param deviceId
     * @return
     */
    @FormUrlEncoded
    @POST("device")
    @DefaultField(keys = {"m"}, values = {"bindDevice"})
    Observable<BaseResult<Object>> bindDevice(@Field("userId") Integer userId
            , @Field("deviceId") String deviceId, @Field("deviceModel") String deviceModel);

    /**
     * 设备解绑
     */
    @POST("device")
    Observable<BaseResult<Object>> unbindDevice(@Body NewUnBindDeviceReq unBindDeviceReq);


    @FormUrlEncoded
    @POST("device")
    @DefaultField(keys = {"m"}, values = {"loadDeviceDB"})
    Observable<BaseResult<AliyunData>> queryDownloadAliyunData(@Field("userId") int userId);

    @FormUrlEncoded
    @POST("device")
    @DefaultField(keys = {"m"}, values = {"saveDeviceDB"})
    Observable<BaseResult<AliyunData>> queryUploadAliyunData(@Field("userId") int userId);


    ///////////////////////////////书城//////////////////////////////////////
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"queryBookCategoryPlus"})
    Observable<BaseResult<List<CategoryInfo>>> queryBookCategory();

    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"queryBook"})
    Observable<BaseResult<List<BookInfo>>> queryBookInfo(@Field("bookId") Integer bookId
            , @Field("bookCategory") Integer bookCategory, @Field("bookCategoryMatch") Integer bookCategoryMatch
            , @Field("userId") Integer userId, @Field("bookVersion") Integer bookVersion
            , @Field("bookTitle") String bookTitle, @Field("bookTitleMatch") String bookTitleMatch
            , @Field("ps") Integer ps, @Field("pn") Integer pn);

    @POST("bookStore")
    Observable<BaseResult<List<BookInfo>>> queryBookShopHomeInfo(@Body BookStoreHomeReq req);

    /**
     * 书架图书移除
     */
    @FormUrlEncoded
    @POST("common/v1")
    @DefaultField(keys = {"m"}, values = {"common_removeBookcaseBooks"})
    Observable<BaseResult<Object>> removeBookInBookcase(@Field("bookId") Integer bookId, @Field("userId") Integer userId);

    /**
     * 添加上架图书
     */
    @FormUrlEncoded
    @POST("common/v1")
    @DefaultField(keys = {"m"}, values = {"common_addBookcaseBooks"})
    Observable<BaseResult<Object>> addBookToBookcase(@Field("bookId") Integer bookId, @Field("userId") Integer userId);

    /**
     * 问题解答上传oss
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"postCommentRequest"})
    Observable<BaseResult<STSbean>> postCommentRequest(@Field("replyId") String replyId, @Field("userId") String userId);

    /**
     * 作业评定(结束某个学生的作业,其实就是调了这个接口就会将这个学生这次考试的错题写到错题本里去,不调就不写)
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"closeHomework"})
    Observable<BaseResult<Object>> closeHomework(@Field("examId") Integer examId
            , @Field("courseId") Integer courseId, @Field("userId") String userId);

    /**
     * 教师批改上传（单题）
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"postComment"})
    Observable<BaseResult<Object>> postComment(@Field("replyId") String replyId, @Field("score") String score
            , @Field("comment") String content, @Field("replyCommentator") String replyCommentator, @Field("originalReplyCommentator") String originalReplyCommentator);

    /**
     * 添加单个收藏夹
     *
     * @param userId
     * @param bookId
     * @return
     */
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"appendFavor"})
    Observable<BaseResult<Object>> appendFavor(@Field("userId") Integer userId, @Field("bookId") Integer bookId);

    /**
     * 添加单个购物车
     *
     * @param userId
     * @param bookId
     * @return
     */
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"appendCart"})
    Observable<BaseResult<Object>> appendCart(@Field("userId") Integer userId, @Field("bookId") Integer bookId);

    /**
     * 查询商城图书详情
     *
     * @param userId
     * @param bookId
     * @param bookTitle
     * @param bookVersion
     * @param bookCategory
     * @param bookCategoryMatch
     * @return
     */
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"queryBook"})
    Observable<BaseResult<List<BookInfo>>> queryShopBook(@Field("userId") Integer userId
            , @Field("bookId") Integer bookId, @Field("bookTitle") String bookTitle, @Field("bookVersion") Integer bookVersion
            , @Field("bookCategory") Integer bookCategory, @Field("bookCategoryMatch") Integer bookCategoryMatch);

    /**
     * 获取推荐图书列表
     *
     * @param userId
     * @param bookId
     * @return
     */
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"promoteBook"})
    Observable<BaseResult<List<BookInfo>>> promoteBook(@Field("userId") int userId, @Field("bookId") int bookId);

    /**
     * 笔记查询
     */
    @POST("classRoom")
    Observable<BaseResult<List<NoteInfo>>> queryNote(@Body NewQueryNoteReq req);

    /**
     * 笔记删除
     */
    @POST("classRoom")
    Observable<BaseResult<Object>> deleteNote(@Body NewDeleteNoteReq req);


    /**
     * 促销活动查询
     */
    @POST("bookStore")
    Observable<BaseResult<List<PromotionResult>>> queryPromotion(@Body PromotionReq req);

    /**
     * 添加笔记
     */
    @POST("classRoom")
    Observable<BaseResult<List<InsertNoteId>>> insertAllNoteApi(@Body NewInsertAllNoteReq req);


    /**
     * 更新笔记
     */
    @POST("classRoom")
    Observable<BaseResult<Object>> updateNote(@Body NewUpdateNoteReq req);

    @POST("classRoom")
    Observable<BaseResult<List<com.yougy.init.bean.BookInfo>>> getBookShelf(@Body NewBookShelfReq req);

    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"queryTaskContent"})
    Observable<BaseResult<List<Task>>> queryTasks(@Field("homeworkId") int homeworkId, @Field("contentBookLink") int contentBookLink, @Field("pn") int pn, @Field("ps") int ps);

    /**
     * 获取组信息
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"querySchoolTeamByStudentAndExam"})
    Observable<BaseResult<TeamBean>> querySchoolTeamByStudentAndExam(@Field("studentId") String studentId, @Field("examId") String examId);


    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"queryStage"})
    Observable<BaseResult<List<StageTaskBean>>> queryStageTask(@Field("dramaId") String dramaId, @Field("stageTypeCode") String stageTypeCode);

    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"postSceneRequest"})
    Observable<BaseResult<STSbean>> uploadTaskPracticeOOS(@Field("userId") Integer userId);

    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"insertScene"})
    Observable<BaseResult<SubmitReplyBean>> submitTaskPracticeServer(@Field("userId") Integer userId, @Field("data") String data);

    /**
     * 统计作业本中作业和问答的数量并且按章节统计返回
     * homeworkId 作业本Id
     * examTypeCode 作业类型（可以传数组）　II0x
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"getExamCount4CursorByHomework"})
    Observable<BaseResult<List<DataCountInBookNode>>> countQuestionCount4Cursor (@Field("homeworkId") Integer homeworkId
            , @Field("examTypeCode") String examTypeCode);
}
