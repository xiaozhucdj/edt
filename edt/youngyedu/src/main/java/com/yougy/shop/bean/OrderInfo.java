package com.yougy.shop.bean;

import com.yougy.init.bean.BookInfo;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by FH on 2017/2/17.
 */

public class OrderInfo extends DataSupport {
    String orderId;
    float orderPrice;
    String orderStatus;
    int count;
    List<BookInfo> bookList;

    public List<BookInfo> getBookList() {
        return bookList;
    }

    public OrderInfo setBookList(List<BookInfo> bookList) {
        this.bookList = bookList;
        return this;
    }

    public int getCount() {
        return count;
    }

    public OrderInfo setCount(int count) {
        this.count = count;
        return this;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public OrderInfo setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public float getOrderPrice() {
        return orderPrice;
    }

    public OrderInfo setOrderPrice(float orderPrice) {
        this.orderPrice = orderPrice;
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderInfo setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

}
