package com.yougy.common.protocol.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.yougy.init.bean.BookInfo;
import com.yougy.shop.bean.BaseData;

import java.util.List;

/**
 * Created by FH on 2017/2/16.
 * 下单结果
 */

public class RequirePayOrderRep extends BaseData {
    List<OrderObj> data;

    public List<OrderObj> getData() {
        return data;
    }

    public RequirePayOrderRep setData(List<OrderObj> data) {
        this.data = data;
        return this;
    }

    public static class OrderObj implements Parcelable {
        double orderPrice;
        String orderId;
        List<BookInfo> bookList;
        public double getOrderPrice() {
            return orderPrice;
        }

        public OrderObj setOrderPrice(double orderPrice) {
            this.orderPrice = orderPrice;
            return this;
        }

        public String getOrderId() {
            return orderId;
        }

        public OrderObj setOrderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public List<BookInfo> getBookList() {
            return bookList;
        }

        public OrderObj setBookList(List<BookInfo> bookList) {
            this.bookList = bookList;
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
        }

        public OrderObj() {
        }

        protected OrderObj(Parcel in) {
            this.orderPrice = in.readDouble();
            this.orderId = in.readString();
            this.bookList = in.createTypedArrayList(BookInfo.CREATOR);
        }

        public static final Creator<OrderObj> CREATOR = new Creator<OrderObj>() {
            @Override
            public OrderObj createFromParcel(Parcel source) {
                return new OrderObj(source);
            }

            @Override
            public OrderObj[] newArray(int size) {
                return new OrderObj[size];
            }
        };
    }
}
