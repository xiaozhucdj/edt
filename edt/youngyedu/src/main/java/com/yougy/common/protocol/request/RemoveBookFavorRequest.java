package com.yougy.common.protocol.request;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FH on 2017/6/8.
 * 移除收藏
 */

public class RemoveBookFavorRequest{
    private String m = "removeFavor";
    private int userId;
    private List<BookIdObj> data = new ArrayList<BookIdObj>();

    public String getM() {
        return m;
    }

    public RemoveBookFavorRequest setM(String m) {
        this.m = m;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public RemoveBookFavorRequest setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public List<BookIdObj> getData() {
        return data;
    }

    public RemoveBookFavorRequest setData(List<BookIdObj> data) {
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
