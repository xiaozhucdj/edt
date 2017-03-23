package com.yougy.common.protocol.response;

import com.yougy.init.bean.BookInfo;
import com.yougy.shop.bean.BaseData;

import java.util.List;

/**
 * Created by Administrator on 2016/10/26.
 * 用户书架
 */
public class BookShelfProtocol extends BaseData {
    private int count ;
    private List<BookInfo> bookList ;

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
