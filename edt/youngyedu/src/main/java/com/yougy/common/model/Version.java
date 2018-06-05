package com.yougy.common.model;

import com.yougy.anwser.BaseResult;

/**
 * Created by jiangliang on 2018-5-7.
 */

public class Version extends BaseResult<Version> {

    private String appVersion;
    private String appUrl;

    public String getAppVersion() {
        return appVersion;
    }

    public Version setAppVersion(String appVersion) {
        this.appVersion = appVersion;
        return this;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public Version setAppUrl(String appUrl) {
        this.appUrl = appUrl;
        return this;
    }

    @Override
    public String toString() {
        return "Version{" +
                "appVersion='" + appVersion + '\'' +
                ", appUrl='" + appUrl + '\'' +
                '}';
    }
}
