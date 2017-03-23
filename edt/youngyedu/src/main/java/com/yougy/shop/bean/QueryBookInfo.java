package com.yougy.shop.bean;

import com.yougy.init.bean.BookInfo;

import java.util.List;

/**
 * Created by jiangliang on 2017/2/18.
 */

public class QueryBookInfo extends BaseData {

    private List<BookInfo> bookList;

    public List<BookInfo> getBookList() {
        return bookList;
    }

    public void setBookList(List<BookInfo> bookList) {
        this.bookList = bookList;
    }
}
