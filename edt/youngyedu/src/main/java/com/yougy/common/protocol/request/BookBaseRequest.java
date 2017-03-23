package com.yougy.common.protocol.request;

import com.yougy.home.bean.DataBookBean;

import java.util.List;

/**
 * Created by Administrator on 2017/2/16.
 */

public class BookBaseRequest  {
    private int userId;
    private List<DataBookBean> data;
    private int count =1;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<DataBookBean> getData() {
        return data;
    }

    public void setData(List<DataBookBean> data) {
        this.data = data;
    }
}
