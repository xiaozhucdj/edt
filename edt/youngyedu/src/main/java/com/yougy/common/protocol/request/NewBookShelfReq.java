package com.yougy.common.protocol.request;

/**
 * Created by Administrator on 2017/5/16.
 * 书架查询
 */

public class NewBookShelfReq extends NewBaseReq{
    /**用户编码*/
    private int userId = -1 ;
    /**课程编码*/
    private int courseId= -1 ;
    /**对应年级名称*/
    private String bookFitGradeName ;
    /**模糊分类编码*/
    private int bookCategoryMatch = -1 ;
    /**缓存图书的ID*/
    private int cacheId ;

    public NewBookShelfReq() {
        m = "bookShelf" ;
        address = "classRoom" ;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setBookFitGradeName(String bookFitGradeName) {
        this.bookFitGradeName = bookFitGradeName;
    }

    public void setBookCategoryMatch(int bookCategoryMatch) {
        this.bookCategoryMatch = bookCategoryMatch;
    }

    public int getCacheId() {
        return cacheId;
    }

    public void setCacheId(int cacheId) {
        this.cacheId = cacheId;
    }
}
