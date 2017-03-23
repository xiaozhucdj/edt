package com.inkscreen.utils;

import android.net.Uri;

/**
 * Created by xcz on 2016/11/24.
 */
public class LeApiApp  {

    private static Uri.Builder getBuilder(String module){
        return  Uri.parse(module).buildUpon();
    }

    private static String finalUrl(Uri.Builder uriBuilder) {
        return LeApiUtils.finalUrl(LeAppApi.HOST_NAME, uriBuilder);
    }

    private static String finalUrlk(Uri.Builder uriBuilder) {
        return LeApiUtils.finalUrl(LeAppApi.HOST_NAME, uriBuilder);
    }

    public static String getLoginlUrl(String username,String password){
        Uri.Builder builder = getBuilder("/zy/m/account/login");

        return finalUrl(builder);

    }

    public static String getWorkUrl(int page,int size){
        Uri.Builder builder = getBuilder("/router/youngyedu/ym/s/homework/todo");
        return finalUrl(builder);

    }


    public static String getRecordUrl(int page,int size){
        Uri.Builder builder = getBuilder("/router/youngyedu/ym/s/homework/history");
        return finalUrl(builder);

    }
    public static String getWorngUrl(String type,int page,int size){
        Uri.Builder builder = getBuilder("/router/youngyedu/ym/s/fallible/query");
        return finalUrl(builder);

    }

    public static String getMySortUrl(){
        Uri.Builder builder = getBuilder("/router/youngyedu/ym/s/fallible/filterConditions");
        return finalUrl(builder);

    }


    public static String getHomeWorkUrl(){
        Uri.Builder builder = getBuilder("/zy/m/s/hk/2/view");///router/youngyedu/ym/s/homework/view
        return finalUrl(builder);

    }

    public static String getPostWorkUrl(){
        Uri.Builder builder = getBuilder("/router/youngyedu/ym/s/homework/do");
        return finalUrl(builder);

    }


    public static String getPostImgUrl(){
        Uri.Builder builder = getBuilder("/f/up/answer-img");
        return finalUrl(builder);

    }
    public static String getCommitUrl(){
        Uri.Builder builder = getBuilder("/router/youngyedu/ym/s/homework/commit");
        return finalUrl(builder);

    }



    //融捷登录接口
//    public static String getRjLoginlUrl(){
//        Uri.Builder builder = getBuilder("/user/login.json");
//
//        return finalRjUrl(builder);
//
//    }
//    private static String finalRjUrl(Uri.Builder uriBuilder) {
//        return LeApiUtils.finalUrl(LeAppApi.HOSTRJ_NAME, uriBuilder);
//    }


    //蓝舰登录接口

    public static String getLJLoginlUrl(){
        Uri.Builder builder = getBuilder("/router/youngyedu/ym/login");

        return finalUrl(builder);

    }

//  http://zuoye.web.dev.uxuebao.com/router/youngyedu/ym/s/fallible/query?type=ALL&page=1&size=8

//  http://zuoye.web.dev.uxuebao.com/router/youngyedu/ym/s/fallible/query?type=ALL&page=1&size=8


}
