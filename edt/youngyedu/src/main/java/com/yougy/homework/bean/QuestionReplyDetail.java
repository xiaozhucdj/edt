package com.yougy.homework.bean;

import android.text.TextUtils;

import com.google.gson.internal.LinkedTreeMap;
import com.yougy.anwser.Content_new;
import com.yougy.anwser.OriginQuestionItem;
import com.yougy.anwser.ParsedQuestionItem;
import com.yougy.common.global.Commons;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/17.
 */

public class QuestionReplyDetail {

    /**
     * replyStatus : 已评完
     * replyItem : 59
     * replyStatusCode : IG04
     * replyContent : []
     * replyCreator : 1000001795
     * replyExam : 536
     * replyUseTime : 00:00:51
     * replyId : 272
     * replyCommentator : 10000001
     * replyComment : []
     * replyScore : 0
     * replyCreatorName : 李媛媛
     * replyCommentTime : 0000-00-00 00:00:00
     * replyCreateTime : 2017-11-16 13:41:15
     */

    private String replyStatus;
    private int replyItem;
    private String replyStatusCode;
    private int replyCreator;
    private int replyExam;
    private String replyUseTime;
    private int replyId;
    private int replyCommentator;
    private int replyScore;
    private String replyCreatorName;
    private String replyCommentTime;
    private String replyCreateTime;
    private List<LinkedTreeMap> replyContent;
    private List<LinkedTreeMap> replyComment;
    private OriginQuestionItem replyItemContent;
    private Integer replyItemWeight;


    private List<Content_new> parsedReplyContentList = new ArrayList<Content_new>();
    private List<Content_new> parsedReplyCommentList = new ArrayList<Content_new>();
    private ParsedQuestionItem parsedQuestionItem;
    public Integer getReplyItemWeight() {
        return replyItemWeight;
    }

    public void setReplyItemWeight(Integer replyItemWeight) {
        this.replyItemWeight = replyItemWeight;
    }

    public String getReplyStatus() {
        return replyStatus;
    }

    public void setReplyStatus(String replyStatus) {
        this.replyStatus = replyStatus;
    }

    public int getReplyItem() {
        return replyItem;
    }

    public void setReplyItem(int replyItem) {
        this.replyItem = replyItem;
    }

    public String getReplyStatusCode() {
        return replyStatusCode;
    }

    public void setReplyStatusCode(String replyStatusCode) {
        this.replyStatusCode = replyStatusCode;
    }

    public int getReplyCreator() {
        return replyCreator;
    }

    public void setReplyCreator(int replyCreator) {
        this.replyCreator = replyCreator;
    }

    public int getReplyExam() {
        return replyExam;
    }

    public void setReplyExam(int replyExam) {
        this.replyExam = replyExam;
    }

    public String getReplyUseTime() {
        return replyUseTime;
    }

    public void setReplyUseTime(String replyUseTime) {
        this.replyUseTime = replyUseTime;
    }

    public int getReplyId() {
        return replyId;
    }

    public void setReplyId(int replyId) {
        this.replyId = replyId;
    }

    public int getReplyCommentator() {
        return replyCommentator;
    }

    public void setReplyCommentator(int replyCommentator) {
        this.replyCommentator = replyCommentator;
    }

    public int getReplyScore() {
        return replyScore;
    }

    public void setReplyScore(int replyScore) {
        this.replyScore = replyScore;
    }

    public String getReplyCreatorName() {
        return replyCreatorName;
    }

    public void setReplyCreatorName(String replyCreatorName) {
        this.replyCreatorName = replyCreatorName;
    }

    public String getReplyCommentTime() {
        return replyCommentTime;
    }

    public void setReplyCommentTime(String replyCommentTime) {
        this.replyCommentTime = replyCommentTime;
    }

    public String getReplyCreateTime() {
        return replyCreateTime;
    }

    public void setReplyCreateTime(String replyCreateTime) {
        this.replyCreateTime = replyCreateTime;
    }

    public List<LinkedTreeMap> getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(List<LinkedTreeMap> replyContent) {
        this.replyContent = replyContent;
    }

    public List<LinkedTreeMap> getReplyComment() {
        return replyComment;
    }

    public void setReplyComment(List<LinkedTreeMap> replyComment) {
        this.replyComment = replyComment;
    }

    public List<Content_new> getParsedReplyContentList() {
        return parsedReplyContentList;
    }

    public QuestionReplyDetail setParsedReplyContentList(List<Content_new> parsedReplyContentList) {
        this.parsedReplyContentList = parsedReplyContentList;
        return this;
    }

    public List<Content_new> getParsedReplyCommentList() {
        return parsedReplyCommentList;
    }

    public QuestionReplyDetail setParsedReplyCommentList(List<Content_new> parsedReplyCommentList) {
        this.parsedReplyCommentList = parsedReplyCommentList;
        return this;
    }

    public OriginQuestionItem getReplyItemContent() {
        return replyItemContent;
    }

    public QuestionReplyDetail setReplyItemContent(OriginQuestionItem replyItemContent) {
        this.replyItemContent = replyItemContent;
        return this;
    }

    public ParsedQuestionItem getParsedQuestionItem() {
        return parsedQuestionItem;
    }

    public QuestionReplyDetail setParsedQuestionItem(ParsedQuestionItem parsedQuestionItem) {
        this.parsedQuestionItem = parsedQuestionItem;
        return this;
    }

    public void parse(){
        for (LinkedTreeMap linkedTreeMap : replyContent) {
            String format = (String) linkedTreeMap.get("format");
            if (format.startsWith("ATCH/")){
                if (linkedTreeMap.get("remote") != null
                        && !TextUtils.isEmpty((String)linkedTreeMap.get("remote"))){
                    String url = "http://" + linkedTreeMap.get("bucket") + Commons.ANSWER_PIC_HOST + linkedTreeMap.get("remote");
                    if (url.endsWith(".gif")
                            || url.endsWith(".jpg")
                            || url.endsWith(".png")
                            ){
                        parsedReplyContentList.add(
                                new Content_new(Content_new.Type.IMG_URL
                                        , ((Double) linkedTreeMap.get("version"))
                                        , url
                                        , null)
                        );
                    }
                    else if (url.endsWith(".htm")){
                        parsedReplyContentList.add(
                                new Content_new(Content_new.Type.HTML_URL
                                        , ((Double) linkedTreeMap.get("version"))
                                        , url
                                        , null)
                        );
                    }
                    else if (url.endsWith(".pdf")){
                        if ("ATCH/PDF_COLOR".equals(format)) {
                            parsedReplyContentList.add(
                                    new Content_new(Content_new.Type.PDF
                                            , ((Double) linkedTreeMap.get("version"))
                                            , url
                                            , null)
                            );
                        }
                    }
                }else{
                    parsedReplyContentList.add(null);
                }
            }
            else if (format.equals("TEXT")){
                parsedReplyContentList.add(
                        new Content_new(Content_new.Type.TEXT
                                , ((Double) linkedTreeMap.get("version"))
                                , ((String) linkedTreeMap.get("value"))
                                , null));
            }
        }

       /* for (LinkedTreeMap linkedTreeMap : replyComment) {
            String commentStr = (String) linkedTreeMap.get("value");
            if (!TextUtils.isEmpty(commentStr)){
                parsedReplyCommentList.add(commentStr);
            }
        }*/

        for (LinkedTreeMap linkedTreeMap : replyComment) {
            String format = (String) linkedTreeMap.get("format");
            if (format.startsWith("ATCH/")) {
                if (linkedTreeMap.get("remote") != null
                        && !TextUtils.isEmpty((String) linkedTreeMap.get("remote"))) {
                    String url = "http://" + linkedTreeMap.get("bucket") + Commons.ANSWER_PIC_HOST + linkedTreeMap.get("remote");
                    if (url.endsWith(".gif")
                            || url.endsWith(".jpg")
                            || url.endsWith(".png")
                            ) {
                        parsedReplyCommentList.add(
                                new Content_new(Content_new.Type.IMG_URL
                                        , ((Double) linkedTreeMap.get("version"))
                                        , url
                                        , null)
                        );
                    } else if (url.endsWith(".htm")) {
                        parsedReplyCommentList.add(
                                new Content_new(Content_new.Type.HTML_URL
                                        , ((Double) linkedTreeMap.get("version"))
                                        , url
                                        , null)
                        );
                    } else if (url.endsWith(".pdf")) {
                        if ("ATCH/PDF_COLOR".equals(format)) {
                            parsedReplyCommentList.add(
                                    new Content_new(Content_new.Type.PDF
                                            , ((Double) linkedTreeMap.get("version"))
                                            , url
                                            , null)
                            );
                        }
                    }
                }else{
                    parsedReplyCommentList.add(null);
                }
            } else if (format.equals("TEXT")) {
                parsedReplyCommentList.add(
                        new Content_new(Content_new.Type.TEXT
                                , ((Double) linkedTreeMap.get("version"))
                                , ((String) linkedTreeMap.get("value"))
                                , null));
            }
        }


        parsedQuestionItem = replyItemContent.parseQuestion();
    }


}
