package com.yougy.common.protocol.request;

import com.yougy.home.bean.DataNoteBean;

import java.util.List;

/**
 * Created by Administrator on 2017/2/16.
 */

public class NoteBaseRequest {
    private int userId;
    private List<DataNoteBean> data;
    private int count ;

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

    public List<DataNoteBean> getData() {
        return data;
    }

    public void setData(List<DataNoteBean> data) {
        this.data = data;
    }
}
