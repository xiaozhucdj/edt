package com.yougy.common.protocol.response;

import java.util.List;

/**
 * Created by Administrator on 2017/5/16.
 * 学校组织查询
 */

public class NewQuerySchoolOrgRep extends NewBaseRep {
    private List<SchoolOrg> data;

    public List<SchoolOrg> getData() {
        return data;
    }

    public void setData(List<SchoolOrg> data) {
        this.data = data;
    }

    public static class SchoolOrg{
        private int orgId;
        private int orgAdmin;
        private int orgLevel;
        private int orgParent;
        private int schoolId;
        private String orgDisplay;
        private String orgName;
        private String orgCreateTime;
        private List<SchoolOrg> orgList;

        public int getOrgId() {
            return orgId;
        }

        public void setOrgId(int orgId) {
            this.orgId = orgId;
        }

        public int getOrgAdmin() {
            return orgAdmin;
        }

        public void setOrgAdmin(int orgAdmin) {
            this.orgAdmin = orgAdmin;
        }

        public int getOrgLevel() {
            return orgLevel;
        }

        public void setOrgLevel(int orgLevel) {
            this.orgLevel = orgLevel;
        }

        public int getOrgParent() {
            return orgParent;
        }

        public void setOrgParent(int orgParent) {
            this.orgParent = orgParent;
        }

        public int getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(int schoolId) {
            this.schoolId = schoolId;
        }

        public String getOrgDisplay() {
            return orgDisplay;
        }

        public void setOrgDisplay(String orgDisplay) {
            this.orgDisplay = orgDisplay;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public String getOrgCreateTime() {
            return orgCreateTime;
        }

        public void setOrgCreateTime(String orgCreateTime) {
            this.orgCreateTime = orgCreateTime;
        }

        public List<SchoolOrg> getOrgList() {
            return orgList;
        }

        public void setOrgList(List<SchoolOrg> orgList) {
            this.orgList = orgList;
        }

        @Override
        public String toString() {
            return orgName;
        }
    }
}
