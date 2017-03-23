package com.yougy.common.protocol.response;

import com.yougy.init.bean.BookInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/10/9.
 * 10. 书城图书推荐
 * 废弃
 */
@Deprecated
public class PromoteBookInfoProtocol {

    private int ret = -1 ;
    private String  msg ;
    private int count ;
    private List<BookInfo> bookList ;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

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
