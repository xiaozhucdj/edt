package com.yougy.task.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Task {

    private int homeworkId;
    private int contentElement;
    private int contentBookLink;
    private int contentCourseLink;
    private String  contentStatusCode;
    private int contentDrama;
    private String contentTitle;
    private int homeworkOwner;
    private int contentPerform;
    private String contentCreateTime;
    private String performStartTime;
    private String performEndTime;
    private String contentStatus;
    private String contentCourseLinkName;
    private boolean isComplete = true;
    @SerializedName("SR03")
    private int exerciseCount;
    @SerializedName("SR02")
    private int dataCount;
    @SerializedName("SR04")
    private int signCount;


    public int getSignCount() {
        return signCount;
    }

    public void setSignCount(int signCount) {
        this.signCount = signCount;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getPerformStartTime() {
        return performStartTime;
    }

    public void setPerformStartTime(String performStartTime) {
        this.performStartTime = performStartTime;
    }

    public String getPerformEndTime() {
        return performEndTime;
    }

    public void setPerformEndTime(String performEndTime) {
        this.performEndTime = performEndTime;
    }

    public String getContentCourseLinkName() {
        return contentCourseLinkName;
    }

    public int getExerciseCount() {
        return exerciseCount;
    }

    public void setExerciseCount(int exerciseCount) {
        this.exerciseCount = exerciseCount;
    }

    public int getDataCount() {
        return dataCount;
    }

    public void setDataCount(int dataCount) {
        this.dataCount = dataCount;
    }

    public boolean isNeedSignature() {
        return signCount == 1;
    }

    public void setContentCourseLinkName(String contentCourseLinkName) {
        this.contentCourseLinkName = contentCourseLinkName;
    }

    public int getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(int homeworkId) {
        this.homeworkId = homeworkId;
    }

    public int getContentElement() {
        return contentElement;
    }

    public void setContentElement(int contentElement) {
        this.contentElement = contentElement;
    }

    public int getContentBookLink() {
        return contentBookLink;
    }

    public void setContentBookLink(int contentBookLink) {
        this.contentBookLink = contentBookLink;
    }

    public int getContentCourseLink() {
        return contentCourseLink;
    }

    public void setContentCourseLink(int contentCourseLink) {
        this.contentCourseLink = contentCourseLink;
    }

    public String getContentStatusCode() {
        return contentStatusCode;
    }

    public void setContentStatusCode(String contentStatusCode) {
        this.contentStatusCode = contentStatusCode;
    }

    public int getContentDrama() {
        return contentDrama;
    }

    public void setContentDrama(int contentDrama) {
        this.contentDrama = contentDrama;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public int getHomeworkOwner() {
        return homeworkOwner;
    }

    public void setHomeworkOwner(int homeworkOwner) {
        this.homeworkOwner = homeworkOwner;
    }

    public int getContentPerform() {
        return contentPerform;
    }

    public void setContentPerform(int contentPerform) {
        this.contentPerform = contentPerform;
    }

    public String getContentCreateTime() {
        return contentCreateTime;
    }

    public void setContentCreateTime(String contentCreateTime) {
        this.contentCreateTime = contentCreateTime;
    }

    public String getContentStatus() {
        return contentStatus;
    }

    public void setContentStatus(String contentStatus) {
        this.contentStatus = contentStatus;
    }

    @Override
    public String toString() {
        return "Task{" +
                "homeworkId=" + homeworkId +
                ", contentElement=" + contentElement +
                ", contentBookLink=" + contentBookLink +
                ", contentCourseLink=" + contentCourseLink +
                ", contentStatusCode='" + contentStatusCode + '\'' +
                ", contentDrama=" + contentDrama +
                ", contentTitle='" + contentTitle + '\'' +
                ", homeworkOwner=" + homeworkOwner +
                ", contentPerform=" + contentPerform +
                ", contentCreateTime='" + contentCreateTime + '\'' +
                ", performStartTime='" + performStartTime + '\'' +
                ", performEndTime='" + performEndTime + '\'' +
                ", contentStatus='" + contentStatus + '\'' +
                ", contentCourseLinkName='" + contentCourseLinkName + '\'' +
                ", isComplete=" + isComplete +
                ", exerciseCount=" + exerciseCount +
                ", dataCount=" + dataCount +
                ", signCount=" + signCount +
                '}';
    }
}
