package com.yougy.shop.bean;

/**
 * Created by FH on 2017/6/29.
 */

public class OrderInfo {

    /**
     * orderType : 集体订单
     * bookId : 22222222
     * orderOwner : 100003
     * bookISBN : 2222222222222
     * bookStatusCode : BA02
     * orderStatus : 交易成功
     * bookVersion : 818
     * bookSubtitle :
     * orderPrice : 1.11
     * orderId : 2017011200000001
     * publisherName : 江苏教育出版社
     * versionName : 苏教版
     * orderCreateTime : 2017-05-12 12:48:47
     * bookCount : 11
     * orderPayer : ""
     * categoryName : 教材初一生物
     * orderParent : 0
     * orderStatusCode : BH03
     * bookPublisher : 8
     * bookStatus : 审查通过
     * bookCategory : 10706
     * bookAuthor :
     * bookVol : 上册
     * bookCover : http://espo.oss-cn-shanghai.aliyuncs.com/leke/book/cover/img20161102051752.png
     * bookTitle : 生物
     * orderReceiver : 0
     * bookPreview : http://espo.oss-cn-shanghai.aliyuncs.com/leke/book/preview/preview20161102051756.pdf
     */

    private String orderType;
    private int bookId;
    private int orderOwner;
    private String bookISBN;
    private String bookStatusCode;
    private String orderStatus;
    private int bookVersion;
    private String bookSubtitle;
    private double orderPrice;
    private String orderId;
    private String publisherName;
    private String versionName;
    private String orderCreateTime;
    private int bookCount;
    private String orderPayer;
    private String categoryName;
    private String orderParent;
    private String orderStatusCode;
    private int bookPublisher;
    private String bookStatus;
    private int bookCategory;
    private String bookAuthor;
    private String bookVol;
    private String bookCover;
    private String bookTitle;
    private int orderReceiver;
    private String bookPreview;
    private double bookPrice;

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getOrderOwner() {
        return orderOwner;
    }

    public void setOrderOwner(int orderOwner) {
        this.orderOwner = orderOwner;
    }

    public String getBookISBN() {
        return bookISBN;
    }

    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }

    public String getBookStatusCode() {
        return bookStatusCode;
    }

    public void setBookStatusCode(String bookStatusCode) {
        this.bookStatusCode = bookStatusCode;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getBookVersion() {
        return bookVersion;
    }

    public void setBookVersion(int bookVersion) {
        this.bookVersion = bookVersion;
    }

    public String getBookSubtitle() {
        return bookSubtitle;
    }

    public void setBookSubtitle(String bookSubtitle) {
        this.bookSubtitle = bookSubtitle;
    }

    public double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(String orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    public int getBookCount() {
        return bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }

    public Object getOrderPayer() {
        return orderPayer;
    }

    public void setOrderPayer(String orderPayer) {
        this.orderPayer = orderPayer;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getOrderParent() {
        return orderParent;
    }

    public void setOrderParent(String orderParent) {
        this.orderParent = orderParent;
    }

    public String getOrderStatusCode() {
        return orderStatusCode;
    }

    public void setOrderStatusCode(String orderStatusCode) {
        this.orderStatusCode = orderStatusCode;
    }

    public int getBookPublisher() {
        return bookPublisher;
    }

    public void setBookPublisher(int bookPublisher) {
        this.bookPublisher = bookPublisher;
    }

    public String getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(String bookStatus) {
        this.bookStatus = bookStatus;
    }

    public int getBookCategory() {
        return bookCategory;
    }

    public void setBookCategory(int bookCategory) {
        this.bookCategory = bookCategory;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookVol() {
        return bookVol;
    }

    public void setBookVol(String bookVol) {
        this.bookVol = bookVol;
    }

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getOrderReceiver() {
        return orderReceiver;
    }

    public void setOrderReceiver(int orderReceiver) {
        this.orderReceiver = orderReceiver;
    }

    public String getBookPreview() {
        return bookPreview;
    }

    public void setBookPreview(String bookPreview) {
        this.bookPreview = bookPreview;
    }

    public double getBookPrice() {
        return bookPrice;
    }

    public OrderInfo setBookPrice(double bookPrice) {
        this.bookPrice = bookPrice;
        return this;
    }
}
