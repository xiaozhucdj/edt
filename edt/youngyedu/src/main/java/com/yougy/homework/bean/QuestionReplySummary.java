package com.yougy.homework.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.internal.LinkedTreeMap;
import com.yougy.anwser.Content_new;
import com.yougy.common.utils.AliyunUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/17.
 */

public class QuestionReplySummary implements Parcelable {

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
    private int replyItemWeight;
    private String replyCreatorName;
    private String replyCommentTime;
    private String replyCreateTime;
    //学生回答原始数据
    private List<Object> replyContent;
    //解析后的回答数据
    private List<Content_new> parsedContentList = new ArrayList<Content_new>();

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

    public List<Object> getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(List<Object> replyContent) {
        this.replyContent = replyContent;
    }

    public List<Content_new> getParsedContentList() {
        return parsedContentList;
    }

    public void setParsedContentList(List<Content_new> parsedContentList) {
        this.parsedContentList = parsedContentList;
    }

    public int getReplyItemWeight() {
        return replyItemWeight;
    }

    public void setReplyItemWeight(int replyItemWeight) {
        this.replyItemWeight = replyItemWeight;
    }

    public QuestionReplySummary parsedContent(){
        parsedContentList.clear();
        for (Object obj : replyContent) {
            Content_new content = null;
            LinkedTreeMap contentTreeMap = (LinkedTreeMap) obj;
            String format = (String) contentTreeMap.get("format");
            String bucked = (String) contentTreeMap.get("bucket");
            double version = (double) contentTreeMap.get("version");
            if (format.startsWith("ATCH/")) {
                if (contentTreeMap.get("remote") != null
                        && !TextUtils.isEmpty((String)contentTreeMap.get("remote"))){
                    String url = "http://" + bucked + AliyunUtil.ANSWER_PIC_HOST + contentTreeMap.get("remote");
                    if (url.endsWith(".gif")
                            || url.endsWith(".jpg")
                            || url.endsWith(".png")
                            ){
                        content = new Content_new(Content_new.Type.IMG_URL , version , url , null);
                    }
                    else if (url.endsWith(".htm")){
                        content = new Content_new(Content_new.Type.HTML_URL , version , url , null);
                    }
                }
            }
            else if (format.equals("TEXT")){
                String contentText = "" + contentTreeMap.get("value");
                content = new Content_new(Content_new.Type.TEXT , version , contentText , null);
            }
//            if (content != null){
                parsedContentList.add(content);
//            }
        }
        return this;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.replyStatus);
        dest.writeInt(this.replyItem);
        dest.writeString(this.replyStatusCode);
        dest.writeInt(this.replyCreator);
        dest.writeInt(this.replyExam);
        dest.writeString(this.replyUseTime);
        dest.writeInt(this.replyId);
        dest.writeInt(this.replyCommentator);
        dest.writeInt(this.replyScore);
        dest.writeInt(this.replyItemWeight);
        dest.writeString(this.replyCreatorName);
        dest.writeString(this.replyCommentTime);
        dest.writeString(this.replyCreateTime);
        dest.writeList(this.replyContent);
        dest.writeTypedList(this.parsedContentList);
    }

    public QuestionReplySummary() {
    }

    protected QuestionReplySummary(Parcel in) {
        this.replyStatus = in.readString();
        this.replyItem = in.readInt();
        this.replyStatusCode = in.readString();
        this.replyCreator = in.readInt();
        this.replyExam = in.readInt();
        this.replyUseTime = in.readString();
        this.replyId = in.readInt();
        this.replyCommentator = in.readInt();
        this.replyScore = in.readInt();
        this.replyItemWeight = in.readInt();
        this.replyCreatorName = in.readString();
        this.replyCommentTime = in.readString();
        this.replyCreateTime = in.readString();
        this.replyContent = new ArrayList<Object>();
        in.readList(this.replyContent, Object.class.getClassLoader());
        this.parsedContentList = in.createTypedArrayList(Content_new.CREATOR);
    }

    public static final Creator<QuestionReplySummary> CREATOR = new Creator<QuestionReplySummary>() {
        @Override
        public QuestionReplySummary createFromParcel(Parcel source) {
            return new QuestionReplySummary(source);
        }

        @Override
        public QuestionReplySummary[] newArray(int size) {
            return new QuestionReplySummary[size];
        }
    };
}
