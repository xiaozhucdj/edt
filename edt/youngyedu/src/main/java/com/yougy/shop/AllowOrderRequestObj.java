package com.yougy.shop;

import com.yougy.shop.bean.BookIdObj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FH on 2018/8/2.
 */

public class AllowOrderRequestObj {
    private Integer orderOwner;
    private List<BookIdObj> data;
    private List<String> orderStatusCode = new ArrayList<String>(){{
        add("BH01");//待支付
        add("BH02");//已支付
        add("BH03");//交易成功
        add("BH05");//免支付
    }};

    public AllowOrderRequestObj(Integer orderOwner, List<BookIdObj> data) {
        this.orderOwner = orderOwner;
        this.data = data;
    }
}
