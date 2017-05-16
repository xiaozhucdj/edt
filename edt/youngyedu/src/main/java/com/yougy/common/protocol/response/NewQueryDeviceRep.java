package com.yougy.common.protocol.response;

import java.util.List;

/**
 * Created by Administrator on 2017/5/15.
 * 设备查询
 */

public class NewQueryDeviceRep extends NewBaseRep {
    private int count ;
    private List<Data> data;

    public int getCount() {
        return count;
    }

    public List<Data> getData() {
        return data;
    }

    public static class Data {
        /**
         * userId : 1000000001
         * userName : student1
         * userRole : 学生
         * deviceId : 3f8ccb515d05b572
         */
        private int userId;
        private String userName;
        private String userRole;
        private String deviceId;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public String getUserRole() {
            return userRole;
        }

        public String getDeviceId() {
            return deviceId;
        }
    }
}
