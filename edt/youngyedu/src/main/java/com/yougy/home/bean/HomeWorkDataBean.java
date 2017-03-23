package com.yougy.home.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/1/16.
 */

public class HomeWorkDataBean {
    private int count;
    private List<HomeWorkBean> homeworkList;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<HomeWorkBean> getHomeworkList() {
        return homeworkList;
    }

    public void setHomeworkList(List<HomeWorkBean> homeworkList) {
        this.homeworkList = homeworkList;
    }
}
