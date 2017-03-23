package com.yougy.common.protocol.response;

import com.yougy.shop.bean.BaseData;
import com.yougy.shop.bean.DataOrderBean;

import java.util.List;

/**
 * Created by FH on 2017/2/16.
 */

public class OrderBaseResponse extends BaseData {

    private int count;
    private List<DataOrderBean> data;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<DataOrderBean> getData() {
        return data;
    }

    public void setData(List<DataOrderBean> data) {
        this.data = data;
    }
}
