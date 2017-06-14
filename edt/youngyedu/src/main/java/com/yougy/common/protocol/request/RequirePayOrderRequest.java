package com.yougy.common.protocol.request;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by FH on 2017/2/16.
 * 下单
 */

public class RequirePayOrderRequest{
    String m = "createOrder";
    int orderOwner;
    List<BookIdObj> data = new ArrayList<BookIdObj>();

    public int getOrderOwner() {
        return orderOwner;
    }

    public RequirePayOrderRequest setOrderOwner(int orderOwner) {
        this.orderOwner = orderOwner;
        return this;
    }

    public List<BookIdObj> getData() {
        return data;
    }

    public RequirePayOrderRequest setData(List<BookIdObj> data) {
        this.data = data;
        return this;
    }

    public static class BookIdObj{
        int bookId;

        public BookIdObj(int bookId) {
            this.bookId = bookId;
        }
    }
}
