package com.yougy.common.protocol.response;

import com.yougy.shop.bean.BaseData;
import com.yougy.shop.bean.CartItem;

import java.util.List;

/**
 * Created by FH on 2016/10/13.
 * 14. 书城购物车查询
 */
public class QueryBookCartRep extends BaseData {
    List<CartItem> data;
    public List<CartItem> getData() {
        return data;
    }
    public QueryBookCartRep setData(List<CartItem> data) {
        this.data = data;
        return this;
    }
}

