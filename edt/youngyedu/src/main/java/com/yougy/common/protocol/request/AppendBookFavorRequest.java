package com.yougy.common.protocol.request;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FH on 2017/6/12.
 * 收藏追加
 */

public class AppendBookFavorRequest{
    String m = "appendFavor";
    int userId;
    List<BookIdObj> data = new ArrayList<BookIdObj>();

    public int getUserId() {
        return userId;
    }

    public AppendBookFavorRequest setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public List<BookIdObj> getData() {
        return data;
    }

    public AppendBookFavorRequest setData(List<BookIdObj> data) {
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
