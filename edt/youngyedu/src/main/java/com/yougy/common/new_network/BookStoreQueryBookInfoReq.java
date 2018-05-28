package com.yougy.common.new_network;

/**
 * Created by jiangliang on 2017/6/2.
 */

public class BookStoreQueryBookInfoReq extends NewBaseReq{

    private int bookId = -1;
    private int bookCategory = -1;
    private int bookCategoryMatch = -1;
    private int userId = -1;
    private int bookVersion = -1;
    private String bookTitle = "";
    private String bookTitleMatch = "";
    private int ps = 50;
    private int pn = 1;


    public BookStoreQueryBookInfoReq(){
        m = "queryBook";
        address = "bookStore";
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public void setBookCategory(int bookCategory) {
        this.bookCategory = bookCategory;
    }

    public void setBookCategoryMatch(int bookCategoryMatch) {
        this.bookCategoryMatch = bookCategoryMatch;
    }

    public void setBookVersion(int bookVersion) {
        this.bookVersion = bookVersion;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookTitleMatch() {
        return bookTitleMatch;
    }

    public void setBookTitleMatch(String bookTitleMatch) {
        this.bookTitleMatch = bookTitleMatch;
    }

    public void setPs(int ps) {
        this.ps = ps;
    }

    public void setPn(int pn) {
        this.pn = pn;
    }
}
