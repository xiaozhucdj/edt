package com.yougy.common.protocol.request;

/**
 * Created by Administrator on 2017/5/15.
 * 教师查询
 */

public class NewQueryTeachertReq extends NewBaseReq {
    /**用户编码*/
    private  int userId = -1;
    /**教师工号*/
    private int userNum = -1;
    /**教师帐号*/
    private  String userName  ;
    /**教师姓名*/
    private  String userRealName ;
    /**学校编码*/
    private  int schoolId = -1 ;
    /**设备编码*/
    private  String deviceId;


    public NewQueryTeachertReq() {
        m = "queryTeacher" ;
        address = "users";
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserNum(int userNum) {
        this.userNum = userNum;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
