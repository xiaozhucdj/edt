package com.yougy.common.protocol.request;

/**
 * Created by FH on 2017/2/17.
 * 获取支付二维码字符串的Request
 */

public class QueryQRStrRequest {
    String m = "checkOrder";
    String orderId;
    int orderOwner;
    double orderPrice;
    int payMethod;

    public String getOrderId() {
        return orderId;
    }

    public QueryQRStrRequest setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public int getOrderOwner() {
        return orderOwner;
    }

    public QueryQRStrRequest setOrderOwner(int orderOwner) {
        this.orderOwner = orderOwner;
        return this;
    }

    public double getOrderPrice() {
        return orderPrice;
    }

    public QueryQRStrRequest setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
        return this;
    }

    public int getPayMethod() {
        return payMethod;
    }

    public QueryQRStrRequest setPayMethod(int payMethod) {
        this.payMethod = payMethod;
        return this;
    }
}
