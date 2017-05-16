package com.yougy.common.protocol.request;

/**
 * Created by Administrator on 2017/5/16.
 * 日志查询
 */

public class NewQueryUserLogReq extends  NewBaseReq{
    /**用户编码*/
    private int userId =-1 ;
    /**设备编码*/
    private int deviceId ;

    public NewQueryUserLogReq() {
        m = "queryUserLog" ;
        address = "users";
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
}
