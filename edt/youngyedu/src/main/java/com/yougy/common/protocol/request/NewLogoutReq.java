package com.yougy.common.protocol.request;

/**
 * Created by Administrator on 2017/5/16.
 * 用户退出
 */

public class NewLogoutReq extends NewBaseReq {
    private int userId ;
    public NewLogoutReq() {
        m = "logout";
        address = "users";
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
