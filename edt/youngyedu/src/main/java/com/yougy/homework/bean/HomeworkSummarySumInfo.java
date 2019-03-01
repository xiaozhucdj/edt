package com.yougy.homework.bean;

public class HomeworkSummarySumInfo {

    private double scoreAvg;
    private int correctCount;
    private double score;

    public double getScoreAvg() {
        return scoreAvg;
    }

    public HomeworkSummarySumInfo setScoreAvg(double scoreAvg) {
        this.scoreAvg = scoreAvg;
        return this;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public HomeworkSummarySumInfo setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
        return this;
    }

    public double getScore() {
        return score;
    }

    public HomeworkSummarySumInfo setScore(double score) {
        this.score = score;
        return this;
    }
}
