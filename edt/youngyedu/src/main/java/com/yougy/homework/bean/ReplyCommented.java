package com.yougy.homework.bean;

import android.text.TextUtils;

import com.google.gson.internal.LinkedTreeMap;
import com.yougy.anwser.Content_new;
import com.yougy.common.global.Commons;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/17.
 */

public class ReplyCommented {


    private String replyStatus;
    private int replyItem;
    private String replyStatusCode;
    private int replyExam;
    private int replyId;
    private int replyCommentator;
    private int replyScore;
    private String replyCommentatorName;
    private String replyCommentTime;

    private List<LinkedTreeMap> replyComment;

    private List<Content_new> parsedReplyCommentList = new ArrayList<Content_new>();

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

    public int getReplyExam() {
        return replyExam;
    }

    public void setReplyExam(int replyExam) {
        this.replyExam = replyExam;
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

    public String getReplyCommentatorName() {
        return replyCommentatorName;
    }

    public void setReplyCommentatorName(String replyCommentatorName) {
        this.replyCommentatorName = replyCommentatorName;
    }

    public String getReplyCommentTime() {
        return replyCommentTime;
    }

    public void setReplyCommentTime(String replyCommentTime) {
        this.replyCommentTime = replyCommentTime;
    }

    public List<LinkedTreeMap> getReplyComment() {
        return replyComment;
    }

    public void setReplyComment(List<LinkedTreeMap> replyComment) {
        this.replyComment = replyComment;
    }

    public List<Content_new> getParsedReplyCommentList() {
        return parsedReplyCommentList;
    }

    public void setParsedReplyCommentList(List<Content_new> parsedReplyCommentList) {
        this.parsedReplyCommentList = parsedReplyCommentList;
    }

    public ReplyCommented parse(){


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
        return this;


    }


}
