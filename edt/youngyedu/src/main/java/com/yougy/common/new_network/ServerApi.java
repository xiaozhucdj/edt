package com.yougy.common.new_network;

import com.yougy.anwser.BaseResult;
import com.yougy.anwser.OriginQuestionItem;
import com.yougy.anwser.STSbean;
import com.yougy.homework.bean.HomeworkBookDetail;
import com.yougy.homework.bean.HomeworkBookSummary;
import com.yougy.homework.bean.QuestionReply;
import com.yougy.shop.bean.DownloadInfo;

import java.util.List;

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
     *  问答解答
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"} , values = {"postReply"})
    Observable<BaseResult<Object>> postReply(@Field("userId") String userId, @Field("itemId") String itemId
            , @Field("examId") String examId, @Field("content") String content , @Field("replyUseTime") String replyUseTime);

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
    @DefaultField(keys = {"m"}, values = {"queryHomeworkDetail"})
    Observable<BaseResult<List<HomeworkBookDetail>>> queryHomeworkBookDetail(@Field("homeworkId") Integer homeworkId);

    /**
     * 获取图书信息
     */
    @FormUrlEncoded
    @POST("bookStore")
    @DefaultField(keys = {"m"}, values = {"queryBook"})
    Observable<BaseResult<List<Object>>> queryBook(@Field("bookId") Integer bookId);

    /**
     * 刷新作业本中所有作业的状态
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"refreshHomework"})
    Observable<BaseResult<Object>> refreshHomeworkBook(@Field("homeworkId") Integer homeworkId);

    /**
     * 查询解答
     */
    @FormUrlEncoded
    @POST("classRoom")
    @DefaultField(keys = {"m"}, values = {"queryReply"})
    Observable<BaseResult<List<QuestionReply>>> queryReply(@Field("examId") Integer examId);
}
