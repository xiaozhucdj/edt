package com.yougy.common.protocol.response;

/**
 * Created by Administrator on 2017/2/13.
 * 添加购物车
 */

public class VersioinProtocol {
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

    private ResGetAppVersion data;

    public ResGetAppVersion getData() {
        return data;
    }

    public void setData(ResGetAppVersion data) {
        this.data = data;
    }
}
