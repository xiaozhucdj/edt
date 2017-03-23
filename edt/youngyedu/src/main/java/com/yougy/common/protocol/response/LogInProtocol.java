package com.yougy.common.protocol.response;

import com.yougy.init.bean.UserInfo;
import com.yougy.shop.bean.BaseData;

import java.util.List;

/**
 * Created by Administrator on 2016/10/26.
 * 登录协议返回
 */
public class LogInProtocol extends BaseData {

    private int count;
    private List<UserInfo.User> userList;

    public List<UserInfo.User>      getUserList() {
        return userList;
    }

    public void setUserList(List<UserInfo.User> userList) {
        this.userList = userList;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
