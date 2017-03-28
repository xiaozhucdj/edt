package com.yougy.common.protocol.response;

import com.yougy.shop.bean.BaseData;

/**
 * Created by FH on 2017/2/16.
 * 下单结果
 */

public class RequirePayOrderProtocol extends BaseData {
    /**
     * 返回结果
     * {
     * "ret": 0,
     * "msg": "success",
     * "orderId": xxx,
     * "orderPrice": xxx
     * }
     */


    String orderId;
    float orderPrice;
    //临时
    public String qrCodeStr;

    public float getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(float orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
