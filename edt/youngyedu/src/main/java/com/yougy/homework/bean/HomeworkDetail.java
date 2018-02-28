package com.yougy.homework.bean;

import com.yougy.anwser.OriginQuestionItem;
import com.yougy.anwser.ParsedQuestionItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/20.
 */

public class HomeworkDetail {
    /**
     * examCreateTime : 2017-11-13 16:59:19
     * examReceiver : 3940121
     * examPaper : {"paperScope":"","paperStatus":"","paperOwner":10000001,"paperContent":[{"paperItemWeight":1,"paperItem":59,"paperItemContent":[{"questionType":"选择","itemStatusCode":"IB01","notation":[],"itemScope":"私人","notationType":null,"question":[{"questionTypeCode":"IC02","questionId":54944,"questionItem":59,"questionCreateTime":"2017-08-15 11:34:33","questionContent":[{"value":"1+2+3","version":0.1,"format":"TEXT"}],"questionType":"选择","questionCreator":10000001}],"answerType":"参考","questionTypeCode":"IC02","itemId":59,"answerTypeCode":"ID02","notationTypeCode":null,"itemScopeCode":"IA01","answer":[{"answerContent":[{"value":6,"version":0.1,"format":"TEXT"}],"answerItem":59,"answerTypeCode":"ID02","answerCreator":10000001,"answerType":"参考","answerCreateTime":"2017-08-15 11:34:33","answerId":39}],"itemOwner":10000001,"itemStatus":"编辑"}],"paperItemRank":null,"paperId":499},{"paperItemWeight":1,"paperItem":60,"paperItemContent":[{"questionType":"填空","itemStatusCode":"IB01","notation":[],"itemScope":"私人","notationType":null,"question":[{"questionTypeCode":"IC01","questionId":54945,"questionItem":60,"questionCreateTime":"2017-08-15 11:35:21","questionContent":[{"value":"3+2+1","version":0.1,"format":"TEXT"}],"questionType":"填空","questionCreator":10000001}],"answerType":null,"questionTypeCode":"IC01","itemId":60,"answerTypeCode":null,"notationTypeCode":null,"itemScopeCode":"IA01","answer":[],"itemOwner":10000001,"itemStatus":"编辑"}],"paperItemRank":null,"paperId":499}],"paperId":499}
     * examScope :
     * examSponsor : 10000001
     * examStartTime : 2017-10-01 00:00:00
     * examStatus : 未批改
     * examName : 作业一
     * examEndTime : 2017-10-08 00:00:00
     * examStatusCode : IH03
     * examId : 529
     */

    private String examCreateTime;
    private int examReceiver;
    private ExamPaper examPaper;
    private String examScope;
    private int examSponsor;
    private String examStartTime;
    private String examStatus;
    private String examName;
    private String examEndTime;
    private String examStatusCode;
    private int examId;

    public String getExamCreateTime() {
        return examCreateTime;
    }

    public void setExamCreateTime(String examCreateTime) {
        this.examCreateTime = examCreateTime;
    }

    public int getExamReceiver() {
        return examReceiver;
    }

    public void setExamReceiver(int examReceiver) {
        this.examReceiver = examReceiver;
    }

    public ExamPaper getExamPaper() {
        return examPaper;
    }

    public void setExamPaper(ExamPaper examPaper) {
        this.examPaper = examPaper;
    }

    public String getExamScope() {
        return examScope;
    }

    public void setExamScope(String examScope) {
        this.examScope = examScope;
    }

    public int getExamSponsor() {
        return examSponsor;
    }

    public void setExamSponsor(int examSponsor) {
        this.examSponsor = examSponsor;
    }

    public String getExamStartTime() {
        return examStartTime;
    }

    public void setExamStartTime(String examStartTime) {
        this.examStartTime = examStartTime;
    }

    public String getExamStatus() {
        return examStatus;
    }

    public void setExamStatus(String examStatus) {
        this.examStatus = examStatus;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getExamEndTime() {
        return examEndTime;
    }

    public void setExamEndTime(String examEndTime) {
        this.examEndTime = examEndTime;
    }

    public String getExamStatusCode() {
        return examStatusCode;
    }

    public void setExamStatusCode(String examStatusCode) {
        this.examStatusCode = examStatusCode;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public static class ExamPaper {
        /**
         * paperScope :
         * paperStatus :
         * paperOwner : 10000001
         * paperContent : [{"paperItemWeight":1,"paperItem":59,"paperItemContent":[{"questionType":"选择","itemStatusCode":"IB01","notation":[],"itemScope":"私人","notationType":null,"question":[{"questionTypeCode":"IC02","questionId":54944,"questionItem":59,"questionCreateTime":"2017-08-15 11:34:33","questionContent":[{"value":"1+2+3","version":0.1,"format":"TEXT"}],"questionType":"选择","questionCreator":10000001}],"answerType":"参考","questionTypeCode":"IC02","itemId":59,"answerTypeCode":"ID02","notationTypeCode":null,"itemScopeCode":"IA01","answer":[{"answerContent":[{"value":6,"version":0.1,"format":"TEXT"}],"answerItem":59,"answerTypeCode":"ID02","answerCreator":10000001,"answerType":"参考","answerCreateTime":"2017-08-15 11:34:33","answerId":39}],"itemOwner":10000001,"itemStatus":"编辑"}],"paperItemRank":null,"paperId":499},{"paperItemWeight":1,"paperItem":60,"paperItemContent":[{"questionType":"填空","itemStatusCode":"IB01","notation":[],"itemScope":"私人","notationType":null,"question":[{"questionTypeCode":"IC01","questionId":54945,"questionItem":60,"questionCreateTime":"2017-08-15 11:35:21","questionContent":[{"value":"3+2+1","version":0.1,"format":"TEXT"}],"questionType":"填空","questionCreator":10000001}],"answerType":null,"questionTypeCode":"IC01","itemId":60,"answerTypeCode":null,"notationTypeCode":null,"itemScopeCode":"IA01","answer":[],"itemOwner":10000001,"itemStatus":"编辑"}],"paperItemRank":null,"paperId":499}]
         * paperId : 499
         */

        private String paperScope;
        private String paperStatus;
        private int paperOwner;
        private int paperId;
        private List<ExamPaperContent> paperContent;

        public String getPaperScope() {
            return paperScope;
        }

        public void setPaperScope(String paperScope) {
            this.paperScope = paperScope;
        }

        public String getPaperStatus() {
            return paperStatus;
        }

        public void setPaperStatus(String paperStatus) {
            this.paperStatus = paperStatus;
        }

        public int getPaperOwner() {
            return paperOwner;
        }

        public void setPaperOwner(int paperOwner) {
            this.paperOwner = paperOwner;
        }

        public int getPaperId() {
            return paperId;
        }

        public void setPaperId(int paperId) {
            this.paperId = paperId;
        }

        public List<ExamPaperContent> getPaperContent() {
            return paperContent;
        }

        public void setPaperContent(List<ExamPaperContent> paperContent) {
            this.paperContent = paperContent;
        }

        public static class ExamPaperContent {
            /**
             * paperItemWeight : 1
             * paperItem : 59
             * paperItemContent : [{"questionType":"选择","itemStatusCode":"IB01","notation":[],"itemScope":"私人","notationType":null,"question":[{"questionTypeCode":"IC02","questionId":54944,"questionItem":59,"questionCreateTime":"2017-08-15 11:34:33","questionContent":[{"value":"1+2+3","version":0.1,"format":"TEXT"}],"questionType":"选择","questionCreator":10000001}],"answerType":"参考","questionTypeCode":"IC02","itemId":59,"answerTypeCode":"ID02","notationTypeCode":null,"itemScopeCode":"IA01","answer":[{"answerContent":[{"value":6,"version":0.1,"format":"TEXT"}],"answerItem":59,"answerTypeCode":"ID02","answerCreator":10000001,"answerType":"参考","answerCreateTime":"2017-08-15 11:34:33","answerId":39}],"itemOwner":10000001,"itemStatus":"编辑"}]
             * paperItemRank : null
             * paperId : 499
             */

            private int paperItemWeight;
            private int paperItem;
            private Object paperItemRank;
            private int paperId;
            private List<OriginQuestionItem> paperItemContent;
            private List<ParsedQuestionItem> parsedQuestionItemList = new ArrayList<ParsedQuestionItem>();

            public int getPaperItemWeight() {
                return paperItemWeight;
            }

            public void setPaperItemWeight(int paperItemWeight) {
                this.paperItemWeight = paperItemWeight;
            }

            public int getPaperItem() {
                return paperItem;
            }

            public void setPaperItem(int paperItem) {
                this.paperItem = paperItem;
            }

            public Object getPaperItemRank() {
                return paperItemRank;
            }

            public void setPaperItemRank(Object paperItemRank) {
                this.paperItemRank = paperItemRank;
            }

            public int getPaperId() {
                return paperId;
            }

            public void setPaperId(int paperId) {
                this.paperId = paperId;
            }

            public List<OriginQuestionItem> getPaperItemContent() {
                return paperItemContent;
            }

            public void setPaperItemContent(List<OriginQuestionItem> paperItemContent) {
                this.paperItemContent = paperItemContent;
            }

            public List<ParsedQuestionItem> getParsedQuestionItemList() {
                return parsedQuestionItemList;
            }

            public ExamPaperContent setParsedQuestionItemList(List<ParsedQuestionItem> parsedQuestionItemList) {
                this.parsedQuestionItemList = parsedQuestionItemList;
                return this;
            }
        }
    }
}
