package com.yougy.anwser;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FH on 2017/8/18.
 */

public class ParsedQuestionItem implements Parcelable {
    public String itemId;
    public List<Question> questionList = new ArrayList<Question>();
    public List<Answer> answerList = new ArrayList<Answer>();
    public List<Analysis> analysisList = new ArrayList<Analysis>();

    public ArrayList<Content_new> questionContentList = new ArrayList<Content_new>();
    public ArrayList<Content_new> answerContentList = new ArrayList<Content_new>();
    public ArrayList<Content_new> analysisContentList = new ArrayList<Content_new>();

    public String knowledgePoint;
    public String difficulty;


    public static class Question implements Serializable {
        public String questionType;
    }

    public static class HtmlQuestion extends Question implements Serializable {
        public String htmlUrl;
        public HtmlQuestion(String questionType , String htmlUrl) {
            this.htmlUrl = htmlUrl;
            this.questionType = questionType;
        }
    }

    public static class TextQuestion extends Question implements Serializable {
        public String text;
        public TextQuestion(String questionType , String text) {
            this.text = text;
            this.questionType = questionType;
        }
    }

    public static class ImgQuestion extends Question implements Serializable {
        public String imgUrl;
        public ImgQuestion(String questionType , String imgUrl) {
            this.imgUrl = imgUrl;
            this.questionType = questionType;
        }
    }

    public static class Answer implements Serializable {
        public String answerType;
    }
    public static class HtmlAnswer extends Answer implements Serializable {
        public String answerUrl;
        public HtmlAnswer(String answerType , String answerUrl) {
            this.answerUrl = answerUrl;
            this.answerType = answerType;
        }
    }

    public static class TextAnswer extends Answer implements Serializable {
        public String text;
        public TextAnswer(String answerType , String text) {
            this.text = text;
            this.answerType = answerType;
        }
    }

    public static class ImgAnswer extends Answer implements Serializable {
        public String imgUrl;
        public ImgAnswer(String answerType , String imgUrl) {
            this.imgUrl = imgUrl;
            this.answerType = answerType;
        }
    }

    public static class Analysis implements Serializable {
    }
    public static class HtmlAnalysis extends Analysis implements Serializable {
        public String analysisUrl;
        public HtmlAnalysis(String analysisUrl) {
            this.analysisUrl = analysisUrl;
        }
    }

    public static class TextAnalysis extends Analysis implements Serializable {
        public String text;
        public TextAnalysis(String text) {
            this.text = text;
        }
    }

    public static class ImgAnalysis extends Analysis implements Serializable {
        public String imgUrl;
        public ImgAnalysis(String imgUrl) {
            this.imgUrl = imgUrl;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.itemId);
        dest.writeList(this.questionList);
        dest.writeList(this.answerList);
        dest.writeList(this.analysisList);
        dest.writeTypedList(this.questionContentList);
        dest.writeTypedList(this.answerContentList);
        dest.writeTypedList(this.analysisContentList);
        dest.writeString(this.knowledgePoint);
        dest.writeString(this.difficulty);
    }

    public ParsedQuestionItem() {
    }

    protected ParsedQuestionItem(Parcel in) {
        this.itemId = in.readString();
        this.questionList = new ArrayList<Question>();
        in.readList(this.questionList, Question.class.getClassLoader());
        this.answerList = new ArrayList<Answer>();
        in.readList(this.answerList, Answer.class.getClassLoader());
        this.analysisList = new ArrayList<Analysis>();
        in.readList(this.analysisList, Analysis.class.getClassLoader());
        this.questionContentList = in.createTypedArrayList(Content_new.CREATOR);
        this.answerContentList = in.createTypedArrayList(Content_new.CREATOR);
        this.analysisContentList = in.createTypedArrayList(Content_new.CREATOR);
        this.knowledgePoint = in.readString();
        this.difficulty = in.readString();
    }

    public static final Parcelable.Creator<ParsedQuestionItem> CREATOR = new Parcelable.Creator<ParsedQuestionItem>() {
        @Override
        public ParsedQuestionItem createFromParcel(Parcel source) {
            return new ParsedQuestionItem(source);
        }

        @Override
        public ParsedQuestionItem[] newArray(int size) {
            return new ParsedQuestionItem[size];
        }
    };
}
