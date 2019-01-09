package com.yougy.homework.bean;

/**
 * Created by FH on 2017/11/14.
 */

public class HomeworkSummary {


    /**
     * exam : 529
     * extra : {"endTime":"2017-10-08","name":"作业一","startTime":"2017-10-01","statusCode":"IH03"}
     * version : 0.1
     * format : UNKNOWN
     */

    private int exam;
    private ExtraBean extra;
    private double version;
    private String format;

    public int getExam() {
        return exam;
    }

    public void setExam(int exam) {
        this.exam = exam;
    }

    public ExtraBean getExtra() {
        return extra;
    }

    public void setExtra(ExtraBean extra) {
        this.extra = extra;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public static class ExtraBean {
        /**
         * endTime : 2017-10-08
         * name : 作业一
         * startTime : 2017-10-01
         * statusCode : IH03
         */

        private String endTime;
        private String name;
        private String startTime;
        private String statusCode;
        private String book;
        private String cursor;
        private String useTime;
        private String lifeTime; //定时作业
        private float accuracy;//正确率
        private int totalPoints;//学生总得分
        private int examTotalPoints;//作业总分值
        private String typeCode;// 自评 互评等作业状态
        private int itemCount;//考试题总数
        private int correctCount;//正确的题目
        private int examSponsor;//teacher id
        private int exam;//id
        private String eval;//作业批改类型(IKXX)
        private String teamName;//如果是分组作业,这里是组名
        private int team;//如果是分组作业,这里是小组id
        private long replyCreator;

        public long getReplyCreator() {
            return replyCreator;
        }

        public void setReplyCreator(long replyCreator) {
            this.replyCreator = replyCreator;
        }

        public int getExam() {
            return exam;
        }

        public void setExam(int exam) {
            this.exam = exam;
        }

        public String getBook() {
            return book;
        }

        public void setBook(String book) {
            this.book = book;
        }

        public String getCursor() {
            return cursor;
        }

        public void setCursor(String cursor) {
            this.cursor = cursor;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(String statusCode) {
            this.statusCode = statusCode;
        }

        public String getUseTime() {
            return useTime;
        }

        public void setUseTime(String useTime) {
            this.useTime = useTime;
        }

        public String getLifeTime() {
            return lifeTime;
        }

        public void setLifeTime(String lifeTime) {
            this.lifeTime = lifeTime;
        }

        public float getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(float accuracy) {
            this.accuracy = accuracy;
        }

        public int getTotalPoints() {
            return totalPoints;
        }

        public void setTotalPoints(int totalPoints) {
            this.totalPoints = totalPoints;
        }

        public int getExamTotalPoints() {
            return examTotalPoints;
        }

        public void setExamTotalPoints(int examTotalPoints) {
            this.examTotalPoints = examTotalPoints;
        }

        public String getTypeCode() {
            return typeCode;
        }

        public void setTypeCode(String typeCode) {
            this.typeCode = typeCode;
        }

        public int getItemCount() {
            return itemCount;
        }

        public void setItemCount(int itemCount) {
            this.itemCount = itemCount;
        }

        public int getCorrectCount() {
            return correctCount;
        }

        public void setCorrectCount(int correctCount) {
            this.correctCount = correctCount;
        }

        public int getExamSponsor() {
            return examSponsor;
        }

        public void setExamSponsor(int exmaSponsor) {
            this.examSponsor = exmaSponsor;
        }

        public String getEval() {
            return eval;
        }
        public ExtraBean setEval(String eval) {
            this.eval = eval;
            return this;
        }

        public String getTeamName() {
            return teamName;
        }

        public ExtraBean setTeamName(String teamName) {
            this.teamName = teamName;
            return this;
        }

        public int getTeam() {
            return team;
        }

        public ExtraBean setTeam(int team) {
            this.team = team;
            return this;
        }
    }
}
