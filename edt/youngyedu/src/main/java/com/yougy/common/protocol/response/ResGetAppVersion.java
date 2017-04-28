package com.yougy.common.protocol.response;

/**
 * Created by Administrator on 2017/3/3.
 */

public class ResGetAppVersion {
    private String appVersion;
    private String appUrl;

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
}

