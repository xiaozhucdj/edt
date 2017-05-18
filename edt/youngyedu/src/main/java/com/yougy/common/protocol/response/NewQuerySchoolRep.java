package com.yougy.common.protocol.response;

import java.util.List;

/**
 * Created by Administrator on 2017/5/16.
 * 学校查询
 */

public class NewQuerySchoolRep extends NewBaseRep {
    private List<School> data;

    public List<School> getData() {
        return data;
    }

    public void setData(List<School> data) {
        this.data = data;
    }

    public static class School{
        private int schoolId;
        private String schoolName;
        private String schoolType;
        private String schoolAdmin;
        private String schoolPhone;
        private String schoolAddress;
        private String schoolCreateTime;

        public int getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(int schoolId) {
            this.schoolId = schoolId;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public String getSchoolType() {
            return schoolType;
        }

        public void setSchoolType(String schoolType) {
            this.schoolType = schoolType;
        }

        public String getSchoolAdmin() {
            return schoolAdmin;
        }

        public void setSchoolAdmin(String schoolAdmin) {
            this.schoolAdmin = schoolAdmin;
        }

        public String getSchoolPhone() {
            return schoolPhone;
        }

        public void setSchoolPhone(String schoolPhone) {
            this.schoolPhone = schoolPhone;
        }

        public String getSchoolAddress() {
            return schoolAddress;
        }

        public void setSchoolAddress(String schoolAddress) {
            this.schoolAddress = schoolAddress;
        }

        public String getSchoolCreateTime() {
            return schoolCreateTime;
        }

        public void setSchoolCreateTime(String schoolCreateTime) {
            this.schoolCreateTime = schoolCreateTime;
        }

        @Override
        public String toString() {
            return schoolName;
        }
    }

}
