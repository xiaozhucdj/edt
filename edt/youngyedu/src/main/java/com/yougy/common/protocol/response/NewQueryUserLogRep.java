package com.yougy.common.protocol.response;


import java.util.List;

/**
 * Created by Administrator on 2017/5/16.
 * 日志查询
 */

public class NewQueryUserLogRep extends NewBaseRep {

    private int count;
    private List<DataBean> data;

    public static class DataBean {
        private int userId;
        private String deviceId ;
        /**日志内容*/
        private String userLogInfo;
        /**日志记录时间*/
        private String userLogTime;

        public int getUserId() {
            return userId;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public String getUserLogInfo() {
            return userLogInfo;
        }

        public String getUserLogTime() {
            return userLogTime;
        }
    }

    public int getCount() {
        return count;
    }

    public List<DataBean> getData() {
        return data;
    }
}
