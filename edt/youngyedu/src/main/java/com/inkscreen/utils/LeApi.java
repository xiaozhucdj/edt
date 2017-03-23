package com.inkscreen.utils;

import org.apache.http.protocol.HTTP;

/**
 * Created by xcz on 2016/11/24.
 */
public class LeApi {
    //http://dev.elanking.com:8088/wiki/pages         http://zuoye.web.dev.uxuebao.com/zy/m/account http://zuoye.web.dev.uxuebao.com/
    public static final String RELEASE_HOST_NAME = "http://m.web.test.uxuebao.com:8082";
    public static String HOST_NAME = RELEASE_HOST_NAME;

    //public static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";
    public static final String FORM_CONTENT_TYPE = String.format("application/x-www-form-urlencoded; charset=%s",HTTP.UTF_8);
    public static final String MULTIPART_CONTENT_TYPE = "multipart/form-data";

    //http://m.web.test.uxuebao.com:8082/



//    String.format("application/x-www-form-urlencoded; charset=%s",
//                       HTTP.UTF_8);
}
//http://yoomath.web.dev.uxuebao.com  http://192.168.66.134:8080  application/json; charset=utf-8"
