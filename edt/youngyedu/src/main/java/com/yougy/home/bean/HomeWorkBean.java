package com.yougy.home.bean;

/**
 * Created by Administrator on 2017/1/16.
 * 作业列表
 */

public class HomeWorkBean {
    /**
     * 作业编码
     */
    private int homeworkId ;
    /**
     *作业类型
     */
    private int homeworkType ;
    /**
     *作业名称
     */
    private String homeworkTitle ;
    /**
     *作业内容
     */
    private String homeworkContent;
    /**
     *作业所有者(用户编码
     */
    private  int homeworkOwner ;
    /**
     *作业创建者(用户编码)
     */
    private int homeworkCreator;
    /**
     *关联课程编码
     */
    private  int homeworkFitCourseId ;
    /**
     *关联图书编码
     */
    private int homeworkFitBookId ;
    /**
     *关联图书名称
     */
    private String homeworkFitBookTitle ;
    /**
     *关联笔记编码
     */
    private int homeworkFitNoteId ;
    /**
     *关联笔记名称
     */
    private String homeworkFitNoteTitle ;
    /**关联笔记样式
     *
     */
    private int homeworkFitNoteStyle ;
    /**
     * 0代表未开通，1代表已开通
     */
    private int  homeworkStatus ;

    public int getHomeworkStatus() {
        return homeworkStatus;
    }

    public void setHomeworkStatus(int homeworkStatus) {
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

    public String getHomeworkTitle() {
        return homeworkTitle;
    }

    public void setHomeworkTitle(String homeworkTitle) {
        this.homeworkTitle = homeworkTitle;
    }

    public String getHomeworkContent() {
        return homeworkContent;
    }

    public void setHomeworkContent(String homeworkContent) {
        this.homeworkContent = homeworkContent;
    }

    public int getHomeworkOwner() {
        return homeworkOwner;
    }

    public void setHomeworkOwner(int homeworkOwner) {
        this.homeworkOwner = homeworkOwner;
    }

    public int getHomeworkCreator() {
        return homeworkCreator;
    }

    public void setHomeworkCreator(int homeworkCreator) {
        this.homeworkCreator = homeworkCreator;
    }

    public int getHomeworkFitCourseId() {
        return homeworkFitCourseId;
    }

    public void setHomeworkFitCourseId(int homeworkFitCourseId) {
        this.homeworkFitCourseId = homeworkFitCourseId;
    }

    public int getHomeworkFitBookId() {
        return homeworkFitBookId;
    }

    public void setHomeworkFitBookId(int homeworkFitBookId) {
        this.homeworkFitBookId = homeworkFitBookId;
    }

    public String getHomeworkFitBookTitle() {
        return homeworkFitBookTitle;
    }

    public void setHomeworkFitBookTitle(String homeworkFitBookTitle) {
        this.homeworkFitBookTitle = homeworkFitBookTitle;
    }

    public int getHomeworkFitNoteId() {
        return homeworkFitNoteId;
    }

    public void setHomeworkFitNoteId(int homeworkFitNoteId) {
        this.homeworkFitNoteId = homeworkFitNoteId;
    }

    public String getHomeworkFitNoteTitle() {
        return homeworkFitNoteTitle;
    }

    public void setHomeworkFitNoteTitle(String homeworkFitNoteTitle) {
        this.homeworkFitNoteTitle = homeworkFitNoteTitle;
    }

    public int getHomeworkFitNoteStyle() {
        return homeworkFitNoteStyle;
    }

    public void setHomeworkFitNoteStyle(int homeworkFitNoteStyle) {
        this.homeworkFitNoteStyle = homeworkFitNoteStyle;
    }
}
