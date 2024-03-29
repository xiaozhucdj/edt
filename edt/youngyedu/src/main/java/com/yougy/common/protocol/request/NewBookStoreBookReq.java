package com.yougy.common.protocol.request;

/**
 * Created by jiangliang on 2017/5/25.
 */

public class NewBookStoreBookReq extends NewBaseReq {


    private int bookId = -1;
    private int bookCategory = -1;
    private int bookCategoryMatch = -1;
    private int userId = -1;
    private int bookVersion = -1;
    private String bookTitle = "";
    private String bookTitleMatch = "";

    public NewBookStoreBookReq(){
        m = "queryBook";
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getBookCategory() {
        return bookCategory;
    }

    public void setBookCategory(int bookCategory) {
        this.bookCategory = bookCategory;
    }

    public int getBookCategoryMatch() {
        return bookCategoryMatch;
    }

    public void setBookCategoryMatch(int bookCategoryMatch) {
        this.bookCategoryMatch = bookCategoryMatch;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookVersion() {
        return bookVersion;
    }

    public void setBookVersion(int bookVersion) {
        this.bookVersion = bookVersion;
    }

    public String getBookTitleMatch() {
        return bookTitleMatch;
    }

    public void setBookTitleMatch(String bookTitleMatch) {
        this.bookTitleMatch = bookTitleMatch;
    }
}
