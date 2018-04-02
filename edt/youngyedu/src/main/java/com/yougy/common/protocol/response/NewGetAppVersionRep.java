package com.yougy.common.protocol.response;

/**
 * Created by Administrator on 2017/5/15.
 * 功能：版本获取
 */

public class NewGetAppVersionRep extends NewBaseRep {

    private Data data ;

    public Data getData() {
        return data;
    }

    public static class Data{
        /**应用程序版本*/
        private String appVersion ;
        /**应用程序下载地址*/
        private String appUrl ;

        public String getVer() {
            return appVersion;
        }

        public String getUrl() {
            return appUrl;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "ver='" + appVersion + '\'' +
                    ", url='" + appUrl + '\'' +
                    '}';
        }
    }
}
