package com.yougy.common.protocol.request;


import com.yougy.common.utils.SpUtils;

/**
 *  图书推荐 请求协议
 */
public class PromoteBookRequest {
    private String m = "promoteBook";
    private int userId;
    private int bookId = -1;

    public PromoteBookRequest(int bookId) {
        this.userId = SpUtils.getAccountId();
        this.bookId = bookId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
}
