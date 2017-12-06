package com.yougy.homework.bean;

import android.text.TextUtils;

import com.google.gson.internal.LinkedTreeMap;
import com.yougy.anwser.Content;
import com.yougy.anwser.OriginQuestionItem;
import com.yougy.anwser.ParsedQuestionItem;

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


    private String textContent;
    private List<Content> parsedReplyContentList = new ArrayList<Content>();
    private List<String> parsedReplyCommentList = new ArrayList<String>();
    private ParsedQuestionItem parsedQuestionItem;

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

    public String getTextContent() {
        return textContent;
    }

    public QuestionReplyDetail setTextContent(String textContent) {
        this.textContent = textContent;
        return this;
    }

    public List<Content> getParsedReplyContentList() {
        return parsedReplyContentList;
    }

    public QuestionReplyDetail setParsedReplyContentList(List<Content> parsedReplyContentList) {
        this.parsedReplyContentList = parsedReplyContentList;
        return this;
    }

    public List<String> getParsedReplyCommentList() {
        return parsedReplyCommentList;
    }

    public QuestionReplyDetail setParsedReplyCommentList(List<String> parsedReplyCommentList) {
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
                    String url = "http://" + linkedTreeMap.get("bucket") + ".oss-cn-shanghai.aliyuncs.com/" + linkedTreeMap.get("remote");
                    if (url.endsWith(".gif")
                            || url.endsWith(".jpg")
                            || url.endsWith(".png")
                            ){
                        parsedReplyContentList.add(Content.newImgContent((Double) linkedTreeMap.get("version"), url));
                    }
                    else if (url.endsWith(".htm")){
                        parsedReplyContentList.add(Content.newHtmlContent((Double) linkedTreeMap.get("version"), url));
                    }
                }
            }
            else if (format.equals("TEXT")){
                textContent = (String) linkedTreeMap.get("value");
            }
        }

        for (LinkedTreeMap linkedTreeMap : replyComment) {
            parsedReplyCommentList.add((String) linkedTreeMap.get("value"));
        }

        parsedQuestionItem = replyItemContent.parseQuestion();
    }


}
