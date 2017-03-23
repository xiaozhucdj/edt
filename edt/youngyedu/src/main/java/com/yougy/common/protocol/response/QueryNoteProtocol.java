package com.yougy.common.protocol.response;

import com.yougy.home.bean.DataNoteBean;
import com.yougy.shop.bean.BaseData;

import java.util.List;

/**
 * Created by Administrator on 2016/11/15.
 * 查询笔记
 */

public class QueryNoteProtocol extends BaseData {

    private int count;
    private List<DataNoteBean> data;

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<DataNoteBean> getData() {
        return data;
    }

    public void setData(List<DataNoteBean> data) {
        this.data = data;
    }
}
