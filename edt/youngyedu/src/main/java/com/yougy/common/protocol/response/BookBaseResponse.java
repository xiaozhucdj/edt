package com.yougy.common.protocol.response;

import com.yougy.home.bean.DataBookBean;
import com.yougy.shop.bean.BaseData;

import java.util.List;

/**
 * Created by Administrator on 2017/2/16.
 */

public class BookBaseResponse extends BaseData {

    private int count;
    private List<DataBookBean> data;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<DataBookBean> getData() {
        return data;
    }

    public void setData(List<DataBookBean> data) {
        this.data = data;
    }
}
