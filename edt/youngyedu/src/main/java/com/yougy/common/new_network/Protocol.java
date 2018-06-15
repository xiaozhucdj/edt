package com.yougy.common.new_network;

/**
 * Created by jiangliang on 2018-6-14.
 */

public class Protocol {
    /**
     * 缓存的 网络请求中的KEY
     */
    public static class CacheId {
        /**
         * 当前学期课本
         */
        public static String CODE_CURRENT_BOOK = "5000";
        public static String ALL_CODE_CURRENT_BOOK = "5001";
        /**
         * 课外书
         */
        public static String CODE_REFERENCE_BOOK = "5004";
        /**
         * 辅导书
         */
        public static String CODE_COACH_BOOK = "5002";
        public static String ALL_CODE_COACH_BOOK = "5003";
        /**
         * 笔记
         */
        public static String CODE_CURRENT_NOTE = "5005";
        public static String ALL_CODE_NOTE = "5006";
    }

    /**
     * 离线修改笔记 ，添加笔记 修改笔记
     */
    public static class OffLineId {
        public static String OFF_LINE_ADD = "6001";
        public static String OFF_LINE_UPDATA = "6002";
    }

    /**
     * 后台返回状态码
     */
    public static class ResultId {

        public static String RESULT_SUCCESS = "200";
        /**
         * 服务器没有找到数据 ，例如课本为null 时候 会返回404 not find book log
         */
        public static String RESULT_NOT_FIND = "404";
    }
}

