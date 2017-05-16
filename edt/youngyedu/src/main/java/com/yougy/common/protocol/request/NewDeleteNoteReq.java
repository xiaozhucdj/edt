package com.yougy.common.protocol.request;

/**
 * Created by Administrator on 2017/5/16.
 * 笔记删除
 */

public class NewDeleteNoteReq extends NewBaseReq {
    /** 用户编码*/
    private int userId = -1;
    /**图书编码*/
    private int noteId = -1;
    /**课程编码*/
    private int courseId = -1;
    public NewDeleteNoteReq() {
        m = "deleteNote";
        address = "classRoom";
    }

    public int getUserId() {
        return userId;
    }

    public int getNoteId() {
        return noteId;
    }

    public int getCourseId() {
        return courseId;
    }
}
