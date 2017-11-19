package com.yougy.homework.bean;

/**
 * Created by Administrator on 2017/11/17.
 */

public class MistakeSummary {

    /**
     * extra : {"exam":542,"book":7244559,"useTime":"00:00:51","score":0,"cursor":11,"display":true}
     * item : 189
     * version : 0.1
     * format : UNKNOWN
     */

    private ExtraBean extra;
    private int item;
    private double version;
    private String format;

    public ExtraBean getExtra() {
        return extra;
    }

    public void setExtra(ExtraBean extra) {
        this.extra = extra;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
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
         * exam : 542
         * book : 7244559
         * useTime : 00:00:51
         * score : 0
         * cursor : 11
         * display : true
         */

        private int exam;
        private int book;
        private String useTime;
        private int score;
        private int cursor;
        private boolean display;

        public int getExam() {
            return exam;
        }

        public void setExam(int exam) {
            this.exam = exam;
        }

        public int getBook() {
            return book;
        }

        public void setBook(int book) {
            this.book = book;
        }

        public String getUseTime() {
            return useTime;
        }

        public void setUseTime(String useTime) {
            this.useTime = useTime;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getCursor() {
            return cursor;
        }

        public void setCursor(int cursor) {
            this.cursor = cursor;
        }

        public boolean isDisplay() {
            return display;
        }

        public void setDisplay(boolean display) {
            this.display = display;
        }
    }
}
