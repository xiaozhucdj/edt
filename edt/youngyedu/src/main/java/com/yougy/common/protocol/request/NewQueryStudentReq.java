package com.yougy.common.protocol.request;

/**
 * Created by Administrator on 2017/5/15.
 * 学生查询
 * 仅传m参数，将返回全部学生信息。
 */

public class NewQueryStudentReq extends NewBaseReq {
    /**用户编码*/
//    private  int userId=-1;
//    /**学生学号*/
//    private  int userNum=-1 ;
//    /**学生帐号*/
//    private  String userName ;
//    /**学生姓名*/
//    private  String userRealName;
//    /**学校编码*/
//    private  int schoolId=-1  ;
//    /**年级编码*/
//    private   int gradeId=-1  ;
    /***班级编码*/
    private   int classId=-1  ;
    /**设备编码*/
    private   String deviceId ;

    public NewQueryStudentReq() {
        m = "queryStudent" ;
        address = "users";
    }

//    public void setUserId(int userId) {
//        this.userId = userId;
//    }
//
//    public void setUserNum(int userNum) {
//        this.userNum = userNum;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    public void setUserRealName(String userRealName) {
//        this.userRealName = userRealName;
//    }
//
//    public void setSchoolId(int schoolId) {
//        this.schoolId = schoolId;
//    }
//
//    public void setGradeId(int gradeId) {
//        this.gradeId = gradeId;
//    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
