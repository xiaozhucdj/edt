package com.yougy.common.protocol.request;
/**
 * Created by Administrator on 2017/5/16.
 * 学校组织查询
 */
public class NewQuerySchoolOrgReq extends  NewBaseReq {
    /**组织编码*/
    private int   orgId  = -1 ;
    /**归属组织编码*/
    private int   orgParent = -1 ;
    /** 学校编码*/
    private int   schoolId = -1 ;

    public NewQuerySchoolOrgReq() {
        m = "querySchoolOrg" ;
        address = "classRoom" ;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public void setOrgParent(int orgParent) {
        this.orgParent = orgParent;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }
}
