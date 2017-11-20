package com.yougy.homework.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/11/17.
 */

public class QuestionReply {

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
    private List<?> replyContent;
    private List<?> replyComment;

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

    public List<?> getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(List<?> replyContent) {
        this.replyContent = replyContent;
    }

    public List<?> getReplyComment() {
        return replyComment;
    }

    public void setReplyComment(List<?> replyComment) {
        this.replyComment = replyComment;
    }
}
