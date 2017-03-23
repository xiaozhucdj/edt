package com.yougy.init.bean;


import com.yougy.shop.bean.BaseData;

import java.util.List;

/**
 * Created by jiangliang on 2016/10/18.
 */

public class SchoolInfo extends BaseData {

    private int count;

    private List<School> schoolList;

    public static class School{
        private String schoolId;
        private String schoolName;

        public String getSchoolId() {
            return schoolId;
        }

        public void setSchoolId(String schoolId) {
            this.schoolId = schoolId;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        @Override
        public String toString() {
            return schoolName;
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<School> getSchoolList() {
        return schoolList;
    }

    public void setSchoolList(List<School> schoolList) {
        this.schoolList = schoolList;
    }

    @Override
    public String toString() {
        return "SchoolInfo{" +
                "schoolList=" + schoolList +
                '}';
    }
}
