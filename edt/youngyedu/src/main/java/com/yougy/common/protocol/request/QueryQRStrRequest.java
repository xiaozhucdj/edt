package com.yougy.common.protocol.request;

/**
 * Created by FH on 2017/2/17.
 * 获取支付二维码字符串的Request
 */

public class QueryQRStrRequest {
    //要付款的订单ID
    int orderID;

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

}
