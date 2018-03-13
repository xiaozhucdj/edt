package com.yougy.shop.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by FH on 2017/6/1.
 */

public class CartItem implements Parcelable {
    /**
     * bookId : 7146306
     * userAge : 18
     * userId : 1000000001
     * userName : student1
     * bookTitle : 语文
     * userStatus : 启用
     * bookCount : 1
     * cartId : 1
     * userGender :
     * bookSalePrice : 1.11
     * userRole : 学生
     * bookDiscount : 100
     * bookAuthor : 无
     * bookSubtitle : 无
     * bookStatus : 审查通过
     * bookPrice : 1
     * bookCoverS : http://espo.oss-cn-shanghai.aliyuncs.com/leke/book/cover/img20161102052955.png
     * userRealName : 袁野
     */

    private int bookId;
    private int userAge;
    private int userId;
    private String userName;
    private String bookTitle;
    private String userStatus;
    private int bookCount;
    private int cartId;
    private String userGender;
    private double bookSalePrice;
    private String userRole;
    private int bookDiscount;
    private String bookAuthor;
    private String bookSubtitle;
    private String bookStatus;
    private double bookPrice;
    private String bookCoverS;
    private String userRealName;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

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

    public int getBookCount() {
        return bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public double getBookSalePrice() {
        return bookSalePrice;
    }

    public void setBookSalePrice(double bookSalePrice) {
        this.bookSalePrice = bookSalePrice;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public int getBookDiscount() {
        return bookDiscount;
    }

    public void setBookDiscount(int bookDiscount) {
        this.bookDiscount = bookDiscount;
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

    public String getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(String bookStatus) {
        this.bookStatus = bookStatus;
    }

    public double getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(double bookPrice) {
        this.bookPrice = bookPrice;
    }

    public String getBookCoverS() {
        return bookCoverS;
    }

    public void setBookCoverS(String bookCoverS) {
        this.bookCoverS = bookCoverS;
    }

    public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.bookId);
        dest.writeInt(this.userAge);
        dest.writeInt(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.bookTitle);
        dest.writeString(this.userStatus);
        dest.writeInt(this.bookCount);
        dest.writeInt(this.cartId);
        dest.writeString(this.userGender);
        dest.writeDouble(this.bookSalePrice);
        dest.writeString(this.userRole);
        dest.writeInt(this.bookDiscount);
        dest.writeString(this.bookAuthor);
        dest.writeString(this.bookSubtitle);
        dest.writeString(this.bookStatus);
        dest.writeDouble(this.bookPrice);
        dest.writeString(this.bookCoverS);
        dest.writeString(this.userRealName);
    }

    public CartItem() {
    }

    protected CartItem(Parcel in) {
        this.bookId = in.readInt();
        this.userAge = in.readInt();
        this.userId = in.readInt();
        this.userName = in.readString();
        this.bookTitle = in.readString();
        this.userStatus = in.readString();
        this.bookCount = in.readInt();
        this.cartId = in.readInt();
        this.userGender = in.readString();
        this.bookSalePrice = in.readDouble();
        this.userRole = in.readString();
        this.bookDiscount = in.readInt();
        this.bookAuthor = in.readString();
        this.bookSubtitle = in.readString();
        this.bookStatus = in.readString();
        this.bookPrice = in.readDouble();
        this.bookCoverS = in.readString();
        this.userRealName = in.readString();
    }

    public static final Parcelable.Creator<CartItem> CREATOR = new Parcelable.Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel source) {
            return new CartItem(source);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };
}
