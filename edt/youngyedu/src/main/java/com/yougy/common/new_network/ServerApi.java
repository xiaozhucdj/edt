package com.yougy.common.new_network;

import com.yougy.anwser.BaseResult;
import com.yougy.anwser.OriginQuestionItem;
import com.yougy.anwser.STSbean;

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
            , @Field("examId") String examId, @Field("content") String content);

    /**
     *  按userId查询云信token
     */
    @FormUrlEncoded
    @POST("netease")
    @DefaultField(keys = {"m"} , values = {"queryToken"})
    Observable<BaseResult<Object>> queryToken(@Field("userId") String userId);

}
