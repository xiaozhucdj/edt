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

    private List<ReplyCommentedBean> replyCommented = new ArrayList<ReplyCommentedBean>();
    private List<Object> replyComment;

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

    public List<ReplyCommentedBean> getReplyCommented() {
        return replyCommented;
    }

    public QuestionReplySummary setReplyCommented(List<ReplyCommentedBean> replyCommented) {
        this.replyCommented = replyCommented;
        return this;
    }

    public List<Object> getReplyComment() {
        return replyComment;
    }

    public QuestionReplySummary setReplyComment(List<Object> replyComment) {
        this.replyComment = replyComment;
        return this;
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

    public static class ReplyCommentedBean implements Parcelable {
        private int replyScore;
        private int replyCommentator;

        public int getReplyScore() {
            return replyScore;
        }

        public ReplyCommentedBean setReplyScore(int replyScore) {
            this.replyScore = replyScore;
            return this;
        }

        public int getReplyCommentator() {
            return replyCommentator;
        }

        public ReplyCommentedBean setReplyCommentator(int replyCommentator) {
            this.replyCommentator = replyCommentator;
            return this;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.replyScore);
            dest.writeInt(this.replyCommentator);
        }

        public ReplyCommentedBean() {
        }

        protected ReplyCommentedBean(Parcel in) {
            this.replyScore = in.readInt();
            this.replyCommentator = in.readInt();
        }

        public static final Parcelable.Creator<ReplyCommentedBean> CREATOR = new Parcelable.Creator<ReplyCommentedBean>() {
            @Override
            public ReplyCommentedBean createFromParcel(Parcel source) {
                return new ReplyCommentedBean(source);
            }

            @Override
            public ReplyCommentedBean[] newArray(int size) {
                return new ReplyCommentedBean[size];
            }
        };
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
        dest.writeTypedList(this.replyCommented);
        dest.writeList(this.replyComment);
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
        this.replyCommented = in.createTypedArrayList(ReplyCommentedBean.CREATOR);
        in.readList(this.replyComment , Object.class.getClassLoader());

    }

    public static final Parcelable.Creator<QuestionReplySummary> CREATOR = new Parcelable.Creator<QuestionReplySummary>() {
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
