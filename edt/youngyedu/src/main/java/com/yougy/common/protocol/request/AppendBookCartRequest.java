package com.yougy.common.protocol.request;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FH on 2017/2/16.
 * 添加购物车
 */

public class AppendBookCartRequest{
    String m = "appendCart";
    int userId;
    List<BookIdObj> data = new ArrayList<BookIdObj>();

    public int getUserId() {
        return userId;
    }

    public AppendBookCartRequest setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public List<BookIdObj> getData() {
        return data;
    }

    public AppendBookCartRequest setData(List<BookIdObj> data) {
        this.data = data;
        return this;
    }

    public static class BookIdObj{
        int bookId;

        public BookIdObj(int bookId) {
            this.bookId = bookId;
        }
    }
}
