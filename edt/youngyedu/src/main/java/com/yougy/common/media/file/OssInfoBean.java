package com.yougy.common.media.file;

public class OssInfoBean {
    //    音频配置
    private String atchType;
    private String accessKeySecret;
    private String expiration;
    private String atchBucket;
    private String securityToken;
    private String atchEncryptKey;
    private String accessKeyId;
    private String atchRemotePath;
    //pl107
    private String atchSupport;


    public String getAtchType() {
        return atchType;
    }

    public void setAtchType(String atchType) {
        this.atchType = atchType;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getAtchBucket() {
        return atchBucket;
    }

    public void setAtchBucket(String atchBucket) {
        this.atchBucket = atchBucket;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public String getAtchEncryptKey() {
        return atchEncryptKey;
    }

    public void setAtchEncryptKey(String atchEncryptKey) {
        this.atchEncryptKey = atchEncryptKey;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAtchRemotePath() {
        return atchRemotePath;
    }

    public void setAtchRemotePath(String atchRemotePath) {
        this.atchRemotePath = atchRemotePath;
    }

    public String getAtchSupport() {
        return atchSupport;
    }

    public void setAtchSupport(String atchSupport) {
        this.atchSupport = atchSupport;
    }
}
