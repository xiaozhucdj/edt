package com.yougy.common.protocol.response;

import com.yougy.shop.bean.BaseData;
import com.yougy.shop.bean.Favor;

import java.util.List;

/**
 * Created by FH on 2017/6/8.
 * 查询收藏的书
 */
public class QueryBookFavorRep extends BaseData {
    List<Favor> data;
    public List<Favor> getData() {
        return data;
    }
    public QueryBookFavorRep setData(List<Favor> data) {
        this.data = data;
        return this;
    }
}
