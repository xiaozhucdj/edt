package com.yougy.homework.bean;

import java.util.List;

/**
 * Created by FH on 2017/11/13.
 */

public class HomeworkBookSummary {

    /**
     * homeworkStatus : 已开通
     * userId : 1000000001
     * homeworkType : 1
     * homeworkCreateTime : 2017-05-12 12:46:36
     * homeworkCreator : 100003
     * homeworkFitSubjectName : 数学
     * courseId : 2
     * homeworkFitGradeId : 3880302
     * homeworkStatusCode : SK01
     * courseBookTitle : 数学
     * homeworkFitGradeName : 初一
     * homeworkContent : []
     * homeworkFitNoteTitle : 数学笔记(绑定)
     * homeworkFitSubjectId : 20200
     * homeworkFitNoteId : 2
     * courseBookId : 7244557
     * homeworkId : 2
     * homeworkTitle : 数学作业
     */

    private String homeworkStatus;
    private int userId;
    private int homeworkType;
    private String homeworkCreateTime;
    private int homeworkCreator;
    private String homeworkFitSubjectName;
    private int courseId;
    private int homeworkFitGradeId;
    private String homeworkStatusCode;
    private String courseBookTitle;
    private String homeworkFitGradeName;
    private String homeworkFitNoteTitle;
    private int homeworkFitNoteStyle;
    private int homeworkFitSubjectId;
    private int homeworkFitNoteId;
    private int courseBookId;
    private int homeworkId;
    private String homeworkTitle;
    private List<?> homeworkContent;

    public String getHomeworkStatus() {
        return homeworkStatus;
    }

    public void setHomeworkStatus(String homeworkStatus) {
        this.homeworkStatus = homeworkStatus;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public int getHomeworkFitNoteStyle() {
        return homeworkFitNoteStyle;
    }

    public HomeworkBookSummary setHomeworkFitNoteStyle(int homeworkFitNoteStyle) {
        this.homeworkFitNoteStyle = homeworkFitNoteStyle;
        return this;
    }

    public int getHomeworkCreator() {
        return homeworkCreator;
    }

    public void setHomeworkCreator(int homeworkCreator) {
        this.homeworkCreator = homeworkCreator;
    }

    public String getHomeworkFitSubjectName() {
        return homeworkFitSubjectName;
    }

    public void setHomeworkFitSubjectName(String homeworkFitSubjectName) {
        this.homeworkFitSubjectName = homeworkFitSubjectName;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getHomeworkFitGradeId() {
        return homeworkFitGradeId;
    }

    public void setHomeworkFitGradeId(int homeworkFitGradeId) {
        this.homeworkFitGradeId = homeworkFitGradeId;
    }

    public String getHomeworkStatusCode() {
        return homeworkStatusCode;
    }

    public void setHomeworkStatusCode(String homeworkStatusCode) {
        this.homeworkStatusCode = homeworkStatusCode;
    }

    public String getCourseBookTitle() {
        return courseBookTitle;
    }

    public void setCourseBookTitle(String courseBookTitle) {
        this.courseBookTitle = courseBookTitle;
    }

    public String getHomeworkFitGradeName() {
        return homeworkFitGradeName;
    }

    public void setHomeworkFitGradeName(String homeworkFitGradeName) {
        this.homeworkFitGradeName = homeworkFitGradeName;
    }

    public String getHomeworkFitNoteTitle() {
        return homeworkFitNoteTitle;
    }

    public void setHomeworkFitNoteTitle(String homeworkFitNoteTitle) {
        this.homeworkFitNoteTitle = homeworkFitNoteTitle;
    }

    public int getHomeworkFitSubjectId() {
        return homeworkFitSubjectId;
    }

    public void setHomeworkFitSubjectId(int homeworkFitSubjectId) {
        this.homeworkFitSubjectId = homeworkFitSubjectId;
    }

    public int getHomeworkFitNoteId() {
        return homeworkFitNoteId;
    }

    public void setHomeworkFitNoteId(int homeworkFitNoteId) {
        this.homeworkFitNoteId = homeworkFitNoteId;
    }

    public int getCourseBookId() {
        return courseBookId;
    }

    public void setCourseBookId(int courseBookId) {
        this.courseBookId = courseBookId;
    }

    public int getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(int homeworkId) {
        this.homeworkId = homeworkId;
    }

    public String getHomeworkTitle() {
        return homeworkTitle;
    }

    public void setHomeworkTitle(String homeworkTitle) {
        this.homeworkTitle = homeworkTitle;
    }

    public List<?> getHomeworkContent() {
        return homeworkContent;
    }

    public void setHomeworkContent(List<?> homeworkContent) {
        this.homeworkContent = homeworkContent;
    }
}
