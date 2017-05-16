package com.yougy.common.protocol.response;

import java.util.List;

/**
 * Created by Administrator on 2017/5/15.
 * 用户查询
 */

public class NewQueryUserRep extends NewBaseRep {

    private int count;
    private List<DataBean> data;

    public int getCount() {
        return count;
    }

    public List<DataBean> getData() {
        return data;
    }


    public static class DataBean {
        /**
         * userId : 1000000001
         * userRole : 学生
         * userName : student1
         * userRealName : 袁野
         * userGender :
         * userAge : 18
         * userPhoto : photo1
         * userBirthday : null
         * userCitizenId : null
         * userQQ : null
         * userEmail : mail@www.domain.com
         * userMobile : 13800138000
         * userAddress : 北京市海淀区
         * userMemo : null
         * userStatus : 启用
         * userExpire : 2099-12-31 00:00:00
         */

        private int userId;
        private String userRole;
        private String userName;
        private String userRealName;
        private String userGender;
        private int userAge;
        private String userPhoto;
        private String userBirthday;
        private String userCitizenId;
        private String userQQ;
        private String userEmail;
        private String userMobile;
        private String userAddress;
        private String userMemo;
        private String userStatus;
        private String userExpire;

        public int getUserId() {
            return userId;
        }

        public String getUserRole() {
            return userRole;
        }

        public String getUserName() {
            return userName;
        }

        public String getUserRealName() {
            return userRealName;
        }

        public String getUserGender() {
            return userGender;
        }

        public int getUserAge() {
            return userAge;
        }


        public String getUserPhoto() {
            return userPhoto;
        }

        public String getUserBirthday() {
            return userBirthday;
        }

        public String getUserCitizenId() {
            return userCitizenId;
        }

        public String getUserQQ() {
            return userQQ;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public String getUserMobile() {
            return userMobile;
        }

        public String getUserAddress() {
            return userAddress;
        }

        public String getUserMemo() {
            return userMemo;
        }

        public String getUserStatus() {
            return userStatus;
        }

        public String getUserExpire() {
            return userExpire;
        }

    }
}
