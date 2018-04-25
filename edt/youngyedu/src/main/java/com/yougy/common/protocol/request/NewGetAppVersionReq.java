package com.yougy.common.protocol.request;

/**
 * Created by Administrator on 2017/5/15.
 * 功能：版本获取
 */
public class NewGetAppVersionReq extends NewBaseReq {

    /**
     * 移动操作系统,是否必须,是
     */
    private final String os = "student";

    public NewGetAppVersionReq() {
        m = "getAppVersion";
        address = "version";
    }
}
