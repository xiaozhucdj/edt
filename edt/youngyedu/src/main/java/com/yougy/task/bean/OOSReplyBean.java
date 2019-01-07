package com.yougy.task.bean;

/*
* "accessKeySecret": "HACsCWpG3DaGzD1eQH2xSZBcR3FBXwmsmXJ5mhV8KqFV",
    "accessKeyId": "STS.NHK8vrWrt6WkkrTfLdYc2n1nn",
    "path": "139701/1000002541/2018",
    "bucketName": "global-task",
    "expiration": "2018-11-29T09:48:21Z",
    "securityToken":
* */
public class OOSReplyBean {

    private String accessKeySecret;
    private String accessKeyId;
    private String path;
    private String bucketName;
    private String expiration;
    private String securityToken;

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }
}
