package com.yougy.common.protocol.request;

/**
 * Created by jiangliang on 2017/5/19.
 */

public class NewBookStoreHomeReq extends NewBaseReq {
    private int ps;
    public NewBookStoreHomeReq() {
        m = "requireBookMain";
        ps = 15;
    }

    public int getPs() {
        return ps;
    }

    public void setPs(int ps) {
        this.ps = ps;
    }
}
