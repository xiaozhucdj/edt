package com.yougy.common.protocol.request;

/**
 * Created by Administrator on 2017/5/15.
 * 设备绑定
 */

public class NewBindDeviceReq extends NewBaseReq {
    /**用户编码 ,是否必须 ,s*/
    private int userId;
    /**设备编码 ,是否必须 ,s*/
    private String deviceId ;

    public NewBindDeviceReq() {
        m = "bindDevice" ;
        address = "device" ;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
