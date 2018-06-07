package com.yougy.common.protocol.request;

/**
 * Created by jiangliang on 2018-6-7.
 */

public class BookStoreHomeReq extends NewBaseReq {
    private int ps;
    public BookStoreHomeReq(){
        m = "requireBookMain";
        address = "bookStore";
        ps = 15;
    }

    public int getPs() {
        return ps;
    }

    public void setPs(int ps) {
        this.ps = ps;
    }
}
