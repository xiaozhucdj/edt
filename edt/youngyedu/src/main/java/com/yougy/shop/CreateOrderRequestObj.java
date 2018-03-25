package com.yougy.shop;

import com.yougy.shop.bean.BookIdObj;

import java.util.List;

/**
 * Created by FH on 2017/6/6.
 */

public class CreateOrderRequestObj {
    private Integer orderOwner;
    private List<BookIdObj> data;

    public CreateOrderRequestObj(Integer orderOwner, List<BookIdObj> data) {
        this.orderOwner = orderOwner;
        this.data = data;
    }
}
