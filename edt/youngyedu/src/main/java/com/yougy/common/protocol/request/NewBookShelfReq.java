package com.yougy.common.protocol.request;

import com.yougy.common.utils.DateUtils;

/**
 * Created by Administrator on 2017/5/16.
 * 书架查询
 */

public class NewBookShelfReq extends NewBaseReq {
    /**
     * 用户编码
     */
    private int userId = -1;

    private int courseId = -1;
    /**对应年级名称*/
    /**
     * 模糊分类编码 ,10000(课本)，30000(课外书) ,(辅导书20000)
     */
    private int bookCategoryMatch = -1;
    /**
     * 缓存图书的ID
     */
    private int cacheId;

    private String bookFitCourseTime;

    public NewBookShelfReq() {
        m = "bookShelf";
        address = "classRoom";
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    /***
     * 传时间
     */
    public void setBookFitGradeName() {
        bookFitCourseTime = DateUtils.getCalendarString();
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
