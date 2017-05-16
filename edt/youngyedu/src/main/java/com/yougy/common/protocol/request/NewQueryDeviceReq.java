package com.yougy.common.protocol.request;

/**
 * Created by Administrator on 2017/5/15.
 * 设备查询
 */

public class NewQueryDeviceReq extends NewBaseReq {
    /**用户编码,是否必须,否*/
    private int userId ;
    /**设备编码,是否必须,否*/
    private String deviceId ;

    public NewQueryDeviceReq() {
        m = "queryDevice" ;
        address = "device";
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
