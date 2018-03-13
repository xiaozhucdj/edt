package com.yougy.common.protocol.response;

import com.yougy.shop.bean.BaseData;
import com.yougy.shop.bean.BookInfo;

import java.util.List;

/**
 * Created by FH on 2017/6/9.
 * 查询商城图书详情
 */
public class QueryShopBookDetailRep extends BaseData {
    List<BookInfo> data;
    public List<BookInfo> getData() {
        return data;
    }
    public QueryShopBookDetailRep setData(List<BookInfo> data) {
        this.data = data;
        return this;
    }
}
