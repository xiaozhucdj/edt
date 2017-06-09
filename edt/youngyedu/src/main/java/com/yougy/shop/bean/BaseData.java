package com.yougy.shop.bean;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;


/**
 * Created by jiangliang on 2016/10/8.
 */

public class BaseData extends DataSupport {
    protected int code;
    protected String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
