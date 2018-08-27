package com.yougy.common.model;

import com.yougy.anwser.BaseResult;

/**
 * Created by jiangliang on 2018-5-7.
 */


public class Version extends BaseResult<Version> {

    private String appVersion;
    private String appUrl;
    private String forceVersion;
    private String updaMsg;

    public String getForceVersion() {
        return forceVersion;
    }


    public String getUpdaMsg() {
        return updaMsg;
    }


    public String getAppVersion() {
        return appVersion;
    }


    public String getAppUrl() {
        return appUrl;
    }


    @Override
    public String toString() {
        return "Version{" +
                "appVersion='" + appVersion + '\'' +
                ", appUrl='" + appUrl + '\'' +
                '}';
    }
}
