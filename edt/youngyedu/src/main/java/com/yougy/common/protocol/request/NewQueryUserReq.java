package com.yougy.common.protocol.request;

/**
 * Created by Administrator on 2017/5/15.
 * 用户查询
 */

public class NewQueryUserReq extends NewBaseReq {
    private int userId = -1 ;
    /**用户帐号*/
    private String userName;
    /**用户姓名*/
    private String userRealName;
    /**设备编码*/
    private String deviceId;

    public NewQueryUserReq() {
        m = "queryUser";
        address = "users";
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
