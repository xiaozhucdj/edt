package com.yougy.shop.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by FH on 2018/3/20.
 */

public class OrderDetailBean implements Parcelable {
    public List<OrderDetailBean> orderChild;
    public String orderType;
    public String orderId;
    public String orderStatus;
    public String orderCreateTime;
    public double orderDeduction;
    public double orderAmount;
    public List<Coupon> orderCoupon;
    public List<OrderInfo> orderInfo;

    public static class OrderInfo implements Parcelable {
        public double bookSalePrice;
        public String orderId;
        public int bookCount;
        public int bookId;
        public double bookFinalPrice;
        public List<BookInfo> bookInfo;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeDouble(this.bookSalePrice);
            dest.writeString(this.orderId);
            dest.writeInt(this.bookCount);
            dest.writeInt(this.bookId);
            dest.writeDouble(this.bookFinalPrice);
            dest.writeTypedList(this.bookInfo);
        }

        public OrderInfo() {
        }

        protected OrderInfo(Parcel in) {
            this.bookSalePrice = in.readDouble();
            this.orderId = in.readString();
            this.bookCount = in.readInt();
            this.bookId = in.readInt();
            this.bookFinalPrice = in.readDouble();
            this.bookInfo = in.createTypedArrayList(BookInfo.CREATOR);
        }

        public static final Creator<OrderInfo> CREATOR = new Creator<OrderInfo>() {
            @Override
            public OrderInfo createFromParcel(Parcel source) {
                return new OrderInfo(source);
            }

            @Override
            public OrderInfo[] newArray(int size) {
                return new OrderInfo[size];
            }
        };
    }

    public OrderDetailBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.orderChild);
        dest.writeString(this.orderType);
        dest.writeString(this.orderId);
        dest.writeString(this.orderStatus);
        dest.writeString(this.orderCreateTime);
        dest.writeDouble(this.orderDeduction);
        dest.writeDouble(this.orderAmount);
        dest.writeTypedList(this.orderCoupon);
        dest.writeTypedList(this.orderInfo);
    }

    protected OrderDetailBean(Parcel in) {
        this.orderChild = in.createTypedArrayList(OrderDetailBean.CREATOR);
        this.orderType = in.readString();
        this.orderId = in.readString();
        this.orderStatus = in.readString();
        this.orderCreateTime = in.readString();
        this.orderDeduction = in.readDouble();
        this.orderAmount = in.readDouble();
        this.orderCoupon = in.createTypedArrayList(Coupon.CREATOR);
        this.orderInfo = in.createTypedArrayList(OrderInfo.CREATOR);
    }

    public static final Creator<OrderDetailBean> CREATOR = new Creator<OrderDetailBean>() {
        @Override
        public OrderDetailBean createFromParcel(Parcel source) {
            return new OrderDetailBean(source);
        }

        @Override
        public OrderDetailBean[] newArray(int size) {
            return new OrderDetailBean[size];
        }
    };
}
