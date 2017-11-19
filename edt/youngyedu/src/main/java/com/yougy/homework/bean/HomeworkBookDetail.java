package com.yougy.homework.bean;

import java.util.List;

/**
 * Created by FH on 2017/11/14.
 */

public class HomeworkBookDetail {

    /**
     * homeworkStatus : 未开通
     * homeworkId : 569
     * homeworkContent : []
     * homeworkType : 1
     * homeworkCreateTime : 2017-11-13 16:50:30
     * homeworkCreator : 100049
     * homeworkTitle : 数学作业本(绑定)
     * homeworkOwner : 1000001790
     * homeworkStatusCode : SK01
     * homeworkExcerpt : []
     */

    private String homeworkStatus;
    private int homeworkId;
    private int homeworkType;
    private String homeworkCreateTime;
    private int homeworkCreator;
    private String homeworkTitle;
    private int homeworkOwner;
    private String homeworkStatusCode;
    private List<HomeworkSummary> homeworkContent;
    private List<MistakeSummary> homeworkExcerpt;

    public String getHomeworkStatus() {
        return homeworkStatus;
    }

    public void setHomeworkStatus(String homeworkStatus) {
        this.homeworkStatus = homeworkStatus;
    }

    public int getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(int homeworkId) {
        this.homeworkId = homeworkId;
    }

    public int getHomeworkType() {
        return homeworkType;
    }

    public void setHomeworkType(int homeworkType) {
        this.homeworkType = homeworkType;
    }

    public String getHomeworkCreateTime() {
        return homeworkCreateTime;
    }

    public void setHomeworkCreateTime(String homeworkCreateTime) {
        this.homeworkCreateTime = homeworkCreateTime;
    }

    public int getHomeworkCreator() {
        return homeworkCreator;
    }

    public void setHomeworkCreator(int homeworkCreator) {
        this.homeworkCreator = homeworkCreator;
    }

    public String getHomeworkTitle() {
        return homeworkTitle;
    }

    public void setHomeworkTitle(String homeworkTitle) {
        this.homeworkTitle = homeworkTitle;
    }

    public int getHomeworkOwner() {
        return homeworkOwner;
    }

    public void setHomeworkOwner(int homeworkOwner) {
        this.homeworkOwner = homeworkOwner;
    }

    public String getHomeworkStatusCode() {
        return homeworkStatusCode;
    }

    public void setHomeworkStatusCode(String homeworkStatusCode) {
        this.homeworkStatusCode = homeworkStatusCode;
    }

    public List<HomeworkSummary> getHomeworkContent() {
        return homeworkContent;
    }

    public void setHomeworkContent(List<HomeworkSummary> homeworkContent) {
        this.homeworkContent = homeworkContent;
    }

    public List<MistakeSummary> getHomeworkExcerpt() {
        return homeworkExcerpt;
    }

    public void setHomeworkExcerpt(List<MistakeSummary> homeworkExcerpt) {
        this.homeworkExcerpt = homeworkExcerpt;
    }
}
