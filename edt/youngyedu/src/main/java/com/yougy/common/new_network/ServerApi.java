package com.yougy.common.new_network;

import com.yougy.anwser.BaseResult;
import com.yougy.anwser.CourseInfo;
import com.yougy.anwser.OriginQuestionItem;
import com.yougy.anwser.STSbean;
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
     *  查询题目整体(列表)
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"} , values = {"queryItem"})
    Observable<BaseResult<List<OriginQuestionItem>>> queryTotalQuestionList(@Field("userId") String userId
            , @Field("bookId") String bookId , @Field("itemId") String itemId, @Field("cursor") Integer cursor);
/**
     *  问题解答上传oss
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"} , values = {"postReplyRequest"})
    Observable<BaseResult<STSbean>> queryReplyRequest(@Field("userId") String userId);


    /**
     *  解答上传（单题）
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"} , values = {"postReply"})
    Observable<BaseResult<Object>> postReply(@Field("userId") String userId, @Field("itemId") String itemId
            , @Field("examId") String examId, @Field("picContent") String picContent, @Field("txtContent") String txtContent , @Field("replyUseTime") String replyUseTime);
/**
     *  解答上传（多题，作业）
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"} , values = {"postReply"})
    Observable<BaseResult<Object>> postReply(@Field("userId") String userId,  @Field("data") String data);

    /**
     *  按userId查询云信token
     */
    @FormUrlEncoded
    @POST("netease")
    @DefaultField(keys = {"m"} , values = {"queryToken"})
    Observable<BaseResult<Object>> queryToken(@Field("userId") String userId);

    /**
     * 图书下载
     */
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"downloadBook"})
    Observable<BaseResult<List<DownloadInfo>>> downloadBook(@Field("userId") String userId , @Field("bookId") String bookId);

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
    Observable<BaseResult<List<HomeworkBookDetail>>> queryHomeworkBookDetail(
            @Field("homeworkId") Integer homeworkId , @Field("examTypeCode") String type , @Field("needRefresh") Boolean needRefresh);

    /**
     * 获取图书信息
     */
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"queryBook"})
    Observable<BaseResult<List<BookInfo>>> queryBook(@Field("bookId") String bookId ,@Field("userId") String userId);

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
            , @Field("itemId") Integer itemId , @Field("userId") String userId);

    /**
     * 查询作业详情
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"queryExam"})
    Observable<BaseResult<List<HomeworkDetail>>> queryExam(@Field("examId") String examIds
            , @Field("examStartTime") String examStartTime);

    /**
     * 查询问答
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m" , "examTypeCode"}, values = {"queryExam" , "II01"})
    Observable<BaseResult<List<HomeworkDetail>>> queryAnswer(@Field("classId") String classId
            , @Field("book") String bookId , @Field("cursor") Integer cursor);

    /**
     * 查询解答摘要
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"queryReply"})
    Observable<BaseResult<List<QuestionReplySummary>>> queryReply(@Field("examId") Integer examId
            , @Field("userId") Integer userId , @Field("replyId") String replyId);

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
     * 获取订单树,包括订单的拆分的子订单信息
     */
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"queryOrderTree"})
    Observable<BaseResult<List<OrderDetailBean>>> queryOrderTree(@Field("orderId") String orderId);

    /**
     * 获取我的订单列表,不包含子订单信息
     */
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m" , "orderParent"}, values = {"queryOrderSole" , "0"})
    Observable<BaseResult<List<OrderSummary>>> queryOrderSole(@Field("orderOwner") String orderOwner);

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

    @POST("bookStore")
    Observable<BaseResult<List<BookInfo>>> queryBookInfo(@Body BookStoreQueryBookInfoReq req);

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
    Observable<BaseResult<STSbean>> postCommentRequest(@Field("replyId") String replyId);

    /**
     * 作业评定(结束某个学生的作业,其实就是调了这个接口就会将这个学生这次考试的错题写到错题本里去,不调就不写)
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"closeHomework"})
    Observable<BaseResult<Object>> closeHomework(@Field("examId") Integer examId
            , @Field("courseId") Integer courseId , @Field("userId") String userId);

    /**
     * 教师批改上传（单题）
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"postComment"})
    Observable<BaseResult<Object>> postComment(@Field("replyId") String replyId, @Field("score") String score
            , @Field("content") String content, @Field("replyCommentator") String replyCommentator);


}
