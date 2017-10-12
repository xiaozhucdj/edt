package com.yougy.common.protocol.response;

import com.yougy.shop.bean.BaseData;
import com.yougy.shop.bean.CartItem;
import com.yougy.shop.bean.OrderInfo;

import java.util.List;

/**
 * Created by FH on 2016/10/13.
 * 订单列表查询响应
 */
public class QueryBookOrderListRep extends BaseData {
    List<OrderInfo> data;
    public List<OrderInfo> getData() {
        return data;
    }
    public QueryBookOrderListRep setData(List<OrderInfo> data) {
        this.data = data;
        return this;
    }
}

