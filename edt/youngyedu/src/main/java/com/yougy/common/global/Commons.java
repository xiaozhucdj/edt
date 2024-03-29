package com.yougy.common.global;

import android.app.Application;
import android.content.SharedPreferences;

import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.ui.activity.BuildConfig;

/**
 * Created by jiangliang on 2016/10/8.
 */

public class Commons {
    /**
     * 如果isRelase 为true 测试人员 无法切地址 为了防止 打包出错
     */
    public static boolean isRelase = true;

    //全部接口
    public static String NEW_URL;
    //书城接口
    public static String SHOP_URL;
    //阿里云OSS
    public static String ALIYUNDATE_URL;

    public static String ENDPOINT;
    public static String ANSWER_PIC_HOST;
    public static String BUCKET_NAME;

    //云信app_key
    public static String YUNXING_APP_KEY;


    private static int testCase = 2;

    static {
        if (isRelase) {
            NEW_URL = "http://api.edu-pad.com.cn/";

            ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";
            ANSWER_PIC_HOST = ".oss-cn-beijing.aliyuncs.com/";
            BUCKET_NAME = "bj-b00k";
            YUNXING_APP_KEY = "6ba4e97ff40a1720bb4c193bfd6580ba";
        } else {
            switch (testCase) {
                case 1://测试环境
                    NEW_URL = "https://api.learningpad.cn/";
                    ENDPOINT = "http://oss-cn-shanghai.aliyuncs.com";
                    ANSWER_PIC_HOST = ".oss-cn-shanghai.aliyuncs.com/";
                    BUCKET_NAME = "b00k";
                    YUNXING_APP_KEY = "88d269fbc1d49219764c826de8e54d91";
                    break;

                case 2:// 预发布环境
                    NEW_URL = "https://api.schoolpad.cn/";
                    ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";
                    ANSWER_PIC_HOST = ".oss-cn-beijing.aliyuncs.com/";
                    BUCKET_NAME = "pre-b00k";
                    YUNXING_APP_KEY = "dfc509a65dca2b658b7cf8e825df9cd6";
                    break;
                default:
                    //默认走测试 环境
                    NEW_URL = "https://api.learningpad.cn/";
                    ENDPOINT = "http://oss-cn-shanghai.aliyuncs.com";
                    ANSWER_PIC_HOST = ".oss-cn-shanghai.aliyuncs.com/";
                    BUCKET_NAME = "b00k";
                    YUNXING_APP_KEY = "88d269fbc1d49219764c826de8e54d91";
                    break;

            }

        }
        SHOP_URL = NEW_URL + "bookStore";
        ALIYUNDATE_URL = NEW_URL + "device";
    }

    //常量 ,设备ID
    public static String UUID;
    public final static String LOAD_APP_STUDENT = "2";

    public final static String LOAD_APP_RESET = "0";


    public static void seetingUrl(Application application) {

        if (BuildConfig.DEBUG || !Commons.isRelase) {
            System.out.println("seetingUrlseetingUrlseetingUrlseetingUrlseetingUrlseetingUrl");
            SharedPreferences sharedPreferences = application.getSharedPreferences("date", YoungyApplicationManager.MODE_PRIVATE);
            int urlKey = sharedPreferences.getInt("ulr", 2);
            switch (urlKey) {
                case 0:
                    NEW_URL = "http://api.edu-pad.com.cn/";
                    ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";
                    ANSWER_PIC_HOST = ".oss-cn-beijing.aliyuncs.com/";
                    BUCKET_NAME = "bj-b00k";
                    YUNXING_APP_KEY = "6ba4e97ff40a1720bb4c193bfd6580ba";
                    break;

                case 1://开发环境
                    NEW_URL = "https://api.learningpad.cn/";
                    ENDPOINT = "http://oss-cn-shanghai.aliyuncs.com";
                    ANSWER_PIC_HOST = ".oss-cn-shanghai.aliyuncs.com/";
                    BUCKET_NAME = "b00k";
                    YUNXING_APP_KEY = "88d269fbc1d49219764c826de8e54d91";
                    break;

                case 2:// 预发布环境
                    NEW_URL = "https://api.schoolpad.cn/";
                    ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";
                    ANSWER_PIC_HOST = ".oss-cn-beijing.aliyuncs.com/";
                    BUCKET_NAME = "pre-b00k";
                    YUNXING_APP_KEY = "dfc509a65dca2b658b7cf8e825df9cd6";
                    break;
            }
            SHOP_URL = NEW_URL + "bookStore";
            ALIYUNDATE_URL = NEW_URL + "device";
        }
    }
}

