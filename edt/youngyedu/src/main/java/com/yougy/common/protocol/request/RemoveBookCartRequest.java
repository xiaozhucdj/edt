package com.yougy.common.protocol.request;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/16.
 * 移除购物车商品
 */

public class RemoveBookCartRequest{
    private String m = "removeCart";
    private int userId;
    private List<RemoveBookCartRequest.BookIdObj> data = new ArrayList<RemoveBookCartRequest.BookIdObj>();

    public String getM() {
        return m;
    }

    public RemoveBookCartRequest setM(String m) {
        this.m = m;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public RemoveBookCartRequest setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public List<RemoveBookCartRequest.BookIdObj> getData() {
        return data;
    }

    public RemoveBookCartRequest setData(List<RemoveBookCartRequest.BookIdObj> data) {
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
