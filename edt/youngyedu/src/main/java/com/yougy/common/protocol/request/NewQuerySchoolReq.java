package com.yougy.common.protocol.request;

/**
 * Created by Administrator on 2017/5/16.
 * 学校查询
 */

public class NewQuerySchoolReq extends NewBaseReq {

    /**学校编码*/
    private int schoolId = -1 ;
    /**学校名称*/
    private String schoolName ;
    /**学校管理员*/
    private String schoolAdmin ;
    /**学校所属地区*/
    private int schoolArea = -1;



    public NewQuerySchoolReq() {
        m = "querySchool" ;
        address = "classRoom" ;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setSchoolAdmin(String schoolAdmin) {
        this.schoolAdmin = schoolAdmin;
    }

    public void setSchoolArea(int schoolArea) {
        this.schoolArea = schoolArea;
    }
}
