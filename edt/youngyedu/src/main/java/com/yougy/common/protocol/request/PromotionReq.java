package com.yougy.common.protocol.request;

/**
 * Created by jiangliang on 2018-3-8.
 */

public class PromotionReq extends NewBaseReq {

    private int couponId;

    private String couponTypeCode;

    private String couponTime;

    private String book;


    public PromotionReq(){
        m = "queryCoupon";
        address = "bookStore";
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }

    public void setCouponTypeCode(String couponTypeCode) {
        this.couponTypeCode = couponTypeCode;
    }

    public void setCouponTime(String couponTime) {
        this.couponTime = couponTime;
    }

    public void setBook(String book) {
        this.book = book;
    }
}
