package com.yougy.common.protocol.response;

import com.yougy.home.bean.HomeWorkDataBean;
import com.yougy.shop.bean.BaseData;

import java.util.List;

/**
 * Created by Administrator on 2016/11/15.
 * 查询笔记
 */

public class QueryHomewrokProtocol extends BaseData {

    private int count;
    private List<HomeWorkDataBean> data;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<HomeWorkDataBean> getData() {
        return data;
    }

    public void setData(List<HomeWorkDataBean> data) {
        this.data = data;
    }
}
