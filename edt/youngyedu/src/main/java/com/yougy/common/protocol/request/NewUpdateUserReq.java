package com.yougy.common.protocol.request;

/**
 * Created by Administrator on 2017/5/15.
 * 用户修改
 */

public class NewUpdateUserReq extends NewBaseReq {
    /**用户编码*/
    private    int userId ;
    /**用户年龄*/
    private int userAge ;
    /**用户照片*/
    private String userPhoto;
    /**用户电话*/
    private String userMobile  ;
    /**用户姓名*/
    private  String userRealName ;
    /**用户密码*/
    private String userPassword ;
    /**用户邮箱*/
    private  String userEmail ;
    /**用户地址*/
    private String userAddress;


    public NewUpdateUserReq() {
        m = "updateUser";
        address = "users";
    }
}
