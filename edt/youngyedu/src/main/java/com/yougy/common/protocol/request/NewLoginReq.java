package com.yougy.common.protocol.request;

/**
 * Created by Administrator on 2017/5/15.
 * 用户登录
 * 需传入userName+userPassword，userToken或deviceId。
 */

public class NewLoginReq  extends  NewBaseReq{
    /***用户帐号*/
    private  String userName;
    /**用户密码*/
    private  String userPassword;
    /**用户令牌*/
    private  String userToken;
    /***设备编码*/
    private  String deviceId;


    public NewLoginReq() {
        m = "login";
        address = "users";
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
