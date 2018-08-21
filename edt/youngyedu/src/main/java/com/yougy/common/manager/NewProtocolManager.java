package com.yougy.common.manager;

/**
 * Created by Administrator on 2017/5/15.
 */

public class NewProtocolManager {

    /***缓存id*/
    public static class NewCacheId {

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

}
