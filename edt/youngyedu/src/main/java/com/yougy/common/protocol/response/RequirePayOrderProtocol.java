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


    int orderId;
    float orderPrice;

    public float getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(float orderPrice) {
        this.orderPrice = orderPrice;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
