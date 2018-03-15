package com.yougy.common.protocol.response;

import com.yougy.shop.bean.BaseData;
import com.yougy.shop.bean.BookInfo;

import java.util.List;

/**
 * Created by FH on 2017/6/13.
 * 书城推荐返回 协议
 */
public class PromoteBookRep extends BaseData{
    List<BookInfo> data;

    public List<BookInfo> getData() {
        return data;
    }

    public PromoteBookRep setData(List<BookInfo> data) {
        this.data = data;
        return this;
    }
}

