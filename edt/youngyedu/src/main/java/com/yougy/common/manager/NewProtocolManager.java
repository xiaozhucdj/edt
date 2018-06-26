package com.yougy.common.manager;

import com.yougy.common.global.Commons;
import com.yougy.common.protocol.request.AliyunDataDownloadReq;
import com.yougy.common.protocol.request.AliyunDataUploadReq;
import com.yougy.common.protocol.request.NewBindDeviceReq;
import com.yougy.common.protocol.request.NewBookShelfReq;
import com.yougy.common.protocol.request.NewBookStoreBookReq;
import com.yougy.common.protocol.request.NewBookStoreCategoryReq;
import com.yougy.common.protocol.request.NewBookStoreHomeReq;
import com.yougy.common.protocol.request.NewDeleteNoteReq;
import com.yougy.common.protocol.request.NewDeleteUserLogReq;
import com.yougy.common.protocol.request.NewGetAppVersionReq;
import com.yougy.common.protocol.request.NewInsertAllNoteReq;
import com.yougy.common.protocol.request.NewInserNoteReq;
import com.yougy.common.protocol.request.NewLoginReq;
import com.yougy.common.protocol.request.NewLogoutReq;
import com.yougy.common.protocol.request.NewQueryAreaReq;
import com.yougy.common.protocol.request.NewQueryDeviceReq;
import com.yougy.common.protocol.request.NewQueryNoteReq;
import com.yougy.common.protocol.request.NewQuerySchoolOrgReq;
import com.yougy.common.protocol.request.NewQuerySchoolReq;
import com.yougy.common.protocol.request.NewQueryStudentReq;
import com.yougy.common.protocol.request.NewQueryTeachertReq;
import com.yougy.common.protocol.request.NewQueryUserLogReq;
import com.yougy.common.protocol.request.NewQueryUserReq;
import com.yougy.common.protocol.request.NewUnBindDeviceReq;
import com.yougy.common.protocol.request.NewUpdateNoteReq;
import com.yougy.common.protocol.request.NewUpdateUserReq;
import com.yougy.common.protocol.request.PromotionReq;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SystemUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostStringBuilder;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Response;


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
