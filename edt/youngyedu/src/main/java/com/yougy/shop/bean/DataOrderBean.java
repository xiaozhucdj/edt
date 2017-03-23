package com.yougy.shop.bean;

import com.yougy.init.bean.BookInfo;

import java.util.List;

/**
 * Created by FH on 2017/2/16.
 * 解析订单
 */

public class DataOrderBean {
    private int count;
    private List<OrderInfo> orderList;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<OrderInfo> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<OrderInfo> bookList) {
        this.orderList = bookList;
    }
}
