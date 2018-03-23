package com.yougy.common.protocol.request;

import com.yougy.common.utils.SpUtils;

/**
 * Created by jiangliang on 2017/10/13.
 */

public class AliyunDataUploadReq extends NewBaseReq {

    private int userId;

    public AliyunDataUploadReq(){
        m = "saveDeviceDB";
        userId = SpUtils.getUserId();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
