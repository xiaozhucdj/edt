package com.yougy.home.bean;

import com.yougy.init.bean.BookInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/2/16.
 * 解析图书
 */

public class DataBookBean {
    private int count;
    private List<BookInfo> bookList;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<BookInfo> getBookList() {
        return bookList;
    }

    public void setBookList(List<BookInfo> bookList) {
        this.bookList = bookList;
    }
}
