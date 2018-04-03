package com.yougy.common.global;


/**
 * Created by jiangliang on 2016/10/8.
 */

public class Commons {

    public static boolean isRelase = false;

    //全部接口
    public static String NEW_URL;
    //书城接口
    public static String SHOP_URL;
    //阿里云OSS
    public static String ALIYUNDATE_URL;

    public static String ENDPOINT;
    public static String ANSWER_PIC_HOST;
    public static String BUCKET_NAME;





    static {
        if (isRelase) {
            NEW_URL = "http://api.edu-pad.com.cn/";

            ENDPOINT ="http://oss-cn-beijing.aliyuncs.com" ;
            ANSWER_PIC_HOST =".oss-cn-beijing.aliyuncs.com/";
            BUCKET_NAME ="bj-b00k";

        } else {
            NEW_URL = "http://api.learningpad.cn:10002/";

            ENDPOINT ="http://oss-cn-shanghai.aliyuncs.com";
            ANSWER_PIC_HOST =".oss-cn-shanghai.aliyuncs.com/";
            BUCKET_NAME ="b00k" ;
        }
        SHOP_URL = NEW_URL + "bookStore";
        ALIYUNDATE_URL = NEW_URL + "device";
    }

    //常量 ,设备ID
    public static String UUID;
}

