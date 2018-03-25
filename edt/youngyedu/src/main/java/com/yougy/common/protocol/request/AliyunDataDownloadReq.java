package com.yougy.common.protocol.request;

import com.yougy.common.utils.SpUtils;

/**
 * Created by jiangliang on 2017/10/23.
 */

public class AliyunDataDownloadReq extends NewBaseReq {

    private int userId;

    public AliyunDataDownloadReq(){
        m = "loadDeviceDB";
        userId = SpUtils.getUserId();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
