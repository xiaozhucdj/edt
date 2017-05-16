package com.yougy.common.protocol.request;

/**
 * Created by Administrator on 2017/5/16.
 * 日志删除
 */

public class NewDeleteUserLogReq extends NewBaseReq {

    private int userId = -1 ;
    private String deviceId ;

    public NewDeleteUserLogReq() {
        m = "deleteUserLog";
        address = "users";
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
