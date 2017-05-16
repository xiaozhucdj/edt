package com.yougy.common.protocol.request;

/**
 * Created by Administrator on 2017/5/16.
 */

public class NewInserNoteReq extends NewBaseReq {
    /** 用户编码*/
    private int userId = -1;
    /**图书编码*/
    private int bookId = -1;
    /**课程编码*/
    private int courseId = -1;

    public NewInserNoteReq() {
        m = "inserNote";
        address = "classRoom";
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
