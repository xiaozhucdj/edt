package com.yougy.shop.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by FH on 2017/7/3.
 */

public class BriefOrder implements Parcelable {
    private double orderPrice;
    private String orderId;
    private List<BookInfo> bookList;
    private String orderTime;
    private String orderStatus;


    public double getOrderPrice() {
        return orderPrice;
    }

    public BriefOrder setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public BriefOrder setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public List<BookInfo> getBookList() {
        return bookList;
    }

    public BriefOrder setBookList(List<BookInfo> bookList) {
        this.bookList = bookList;
        return this;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public BriefOrder setOrderTime(String orderTime) {
        this.orderTime = orderTime;
        return this;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public BriefOrder setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.orderPrice);
        dest.writeString(this.orderId);
        dest.writeTypedList(this.bookList);
        dest.writeString(this.orderTime);
        dest.writeString(this.orderStatus);
    }

    public BriefOrder() {
    }

    protected BriefOrder(Parcel in) {
        this.orderPrice = in.readDouble();
        this.orderId = in.readString();
        this.bookList = in.createTypedArrayList(BookInfo.CREATOR);
        this.orderTime = in.readString();
        this.orderStatus = in.readString();
    }

    public static final Creator<BriefOrder> CREATOR = new Creator<BriefOrder>() {
        @Override
        public BriefOrder createFromParcel(Parcel source) {
            return new BriefOrder(source);
        }

        @Override
        public BriefOrder[] newArray(int size) {
            return new BriefOrder[size];
        }
    };
}

