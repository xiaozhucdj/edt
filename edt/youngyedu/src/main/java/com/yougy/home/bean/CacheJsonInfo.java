package com.yougy.home.bean;


import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2016/12/19.
 * <p>
 * 存储 服务返回的JSON
 */

public class CacheJsonInfo extends DataSupport {
    private String cacheID;
    private String cacheJSON;

    public String getCacheID() {
        return cacheID;
    }

    public void setCacheID(String cacheID) {
        this.cacheID = cacheID;
    }

    public String getCacheJSON() {
        return cacheJSON;
    }

    public void setCacheJSON(String cacheJSON) {
        this.cacheJSON = cacheJSON;
    }
}
