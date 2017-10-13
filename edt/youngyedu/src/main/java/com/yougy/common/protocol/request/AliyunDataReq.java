package com.yougy.common.protocol.request;

import com.yougy.common.global.Commons;

/**
 * Created by jiangliang on 2017/10/13.
 */

public class AliyunDataReq extends NewBaseReq {

    private String deviceId;

    public AliyunDataReq(){
        m = "getUploadToken";
        deviceId = Commons.UUID;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
