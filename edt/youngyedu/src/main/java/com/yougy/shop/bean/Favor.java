package com.yougy.shop.bean;

/**
 * Created by FH on 2017/6/8.
 */

public class Favor {

    /**
     * bookTitle : 语文
     * userStatus : 启用
     * bookId : 7146306
     * bookCover : http://espo.oss-cn-shanghai.aliyuncs.com/leke/book/cover/img20161102052955.png
     * favorId : 1
     * userGender :
     * userId : 1000000001
     * userAge : 18
     * bookStatus : 审查通过
     * bookAuthor : 无
     * bookSubtitle : 无
     * userName : student1
     * userRole : 学生
     * bookCollectTime : 2017-06-01 11:39:18
     * userRealName : 袁野
     */

    private String bookTitle;
    private String userStatus;
    private int bookId;
    private String bookCover;
    private int favorId;
    private String userGender;
    private int userId;
    private int userAge;
    private String bookStatus;
    private String bookAuthor;
    private String bookSubtitle;
    private String userName;
    private String userRole;
    private String bookCollectTime;
    private String userRealName;

    public double getBookSalePrice() {
        return bookSalePrice;
    }

    public void setBookSalePrice(double bookSalePrice) {
        this.bookSalePrice = bookSalePrice;
    }

    private double bookSalePrice;


    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public int getFavorId() {
        return favorId;
    }

    public void setFavorId(int favorId) {
        this.favorId = favorId;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public String getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(String bookStatus) {
        this.bookStatus = bookStatus;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookSubtitle() {
        return bookSubtitle;
    }

    public void setBookSubtitle(String bookSubtitle) {
        this.bookSubtitle = bookSubtitle;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getBookCollectTime() {
        return bookCollectTime;
    }

    public void setBookCollectTime(String bookCollectTime) {
        this.bookCollectTime = bookCollectTime;
    }

    public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }
}
