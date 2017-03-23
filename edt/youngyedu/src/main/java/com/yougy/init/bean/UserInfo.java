package com.yougy.init.bean;

import com.google.gson.annotations.SerializedName;
import com.yougy.shop.bean.BaseData;

import java.util.List;

/**
 * Created by jiangliang on 2016/10/20.
 */

public class UserInfo extends BaseData {

    private int count;
    private List<User> userList;

    public static class User{
        private String userId;
        private String userNumber;
        private String userName ;
        private String className ;
        private String schoolName ;
        private String userRealName;


        private String userToken;
        @SerializedName("userBinded")
        private boolean isBind;
        //年级
        private String gradeName;
        /**学科：返回内容 语文 ，数学，英语，历史等 以","分开 在创建笔记使用*/
        private String subjectNames ;

        public String getGradeName() {
            return gradeName;
        }

        public void setGradeName(String gradeName) {
            this.gradeName = gradeName;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public String getUserRealName() {
            return userRealName;
        }

        public void setUserRealName(String userRealName) {
            this.userRealName = userRealName;
        }

        public boolean isBind() {
            return isBind;
        }

        public void setBind(boolean bind) {
            isBind = bind;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserNumber() {
            return userNumber;
        }

        public void setUserNumber(String userNumber) {
            this.userNumber = userNumber;
        }

        public String getSubjectNames() {
            return subjectNames;
        }

        public void setSubjectNames(String subjectNames) {
            this.subjectNames = subjectNames;
        }

        public String getUserToken() {
            return userToken;
        }

        public void setUserToken(String userToken) {
            this.userToken = userToken;
        }

        @Override
        public String toString() {
            return "User{" +
                    "userId='" + userId + '\'' +
                    ", userName='" + userName + '\'' +
                    ", userNumber='" + userNumber + '\'' +
                    ", isBind=" + isBind +
                    '}';
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<User> getUsers() {
        return userList;
    }

    public void setUsers(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "users=" + userList +
                '}';
    }


}
