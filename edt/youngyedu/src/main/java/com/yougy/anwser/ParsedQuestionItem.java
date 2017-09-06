package com.yougy.anwser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FH on 2017/8/18.
 */

public class ParsedQuestionItem implements Serializable {
    public String itemId;
    public List<Question> questionList = new ArrayList<Question>();
    public List<Answer> answerList = new ArrayList<Answer>();
    public List<Analysis> analysisList = new ArrayList<Analysis>();
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
}
