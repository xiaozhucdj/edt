package com.yougy.common.protocol.request;

/**
 * Created by Administrator on 2017/5/15.
 * 设备解绑
 */

public class NewUnBindDeviceReq  extends NewBaseReq{

    /**用户编码 ,是否必须 ,s*/
    private int userId= -1 ;
    /**设备编码 ,是否必须 ,s*/
    private String deviceId ;

    public NewUnBindDeviceReq() {
        m = "unbindDevice" ;
        address = "device" ;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
