package com.yougy.common.protocol.request;

/**
 * Created by Administrator on 2017/5/16.
 * 笔记查询
 */

public class NewQueryNoteReq extends  NewBaseReq {

    /**用户编码*/
    private   int userId  = -1 ;
    /**笔记编码*/
    private int  noteId  = -1 ;
    /**图书编码*/
    private  int bookId   = -1 ;
    /**课程编码*/
    private int courseId  = -1 ;
    /***年级名称*/
    private  String noteFitGradeName ;


    public NewQueryNoteReq() {
        m = "queryNote" ;
        address = "classRoom" ;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setNoteFitGradeName(String noteFitGradeName) {
        this.noteFitGradeName = noteFitGradeName;
    }
}
