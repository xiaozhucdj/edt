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
    }
}
