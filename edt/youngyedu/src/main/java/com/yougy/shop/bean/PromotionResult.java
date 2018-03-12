package com.yougy.shop.bean;

import java.util.List;

/**
 * Created by jiangliang on 2018-3-8.
 */

public class PromotionResult {

    private String couponCreateTime;

    private String couponContentExplain;

    private String couponName;

    private String couponMemo;

    private String couponType;

    private String couponEndTime;

    private String couponRemain;

    private String couponTotal;

    private String couponTypeCode;

    private int couponId;

    private int couponCreator;

    private String couponStartTime;

    private String couponTarget;

    private List<BookInfo> couponBook;

    public String getCouponCreateTime() {
        return couponCreateTime;
    }

    public void setCouponCreateTime(String couponCreateTime) {
        this.couponCreateTime = couponCreateTime;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public String getCouponMemo() {
        return couponMemo;
    }

    public void setCouponMemo(String couponMemo) {
        this.couponMemo = couponMemo;
    }

    public String getCouponType() {
        return couponType;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    public String getCouponEndTime() {
        return couponEndTime;
    }

    public void setCouponEndTime(String couponEndTime) {
        this.couponEndTime = couponEndTime;
    }

    public String getCouponRemain() {
        return couponRemain;
    }

    public void setCouponRemain(String couponRemain) {
        this.couponRemain = couponRemain;
    }

    public String getCouponTotal() {
        return couponTotal;
    }

    public void setCouponTotal(String couponTotal) {
        this.couponTotal = couponTotal;
    }

    public String getCouponTypeCode() {
        return couponTypeCode;
    }

    public void setCouponTypeCode(String couponTypeCode) {
        this.couponTypeCode = couponTypeCode;
    }

    public int getCouponId() {
        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }

    public int getCouponCreator() {
        return couponCreator;
    }

    public void setCouponCreator(int couponCreator) {
        this.couponCreator = couponCreator;
    }

    public String getCouponStartTime() {
        return couponStartTime;
    }

    public void setCouponStartTime(String couponStartTime) {
        this.couponStartTime = couponStartTime;
    }

    public String getCouponTarget() {
        return couponTarget;
    }

    public void setCouponTarget(String couponTarget) {
        this.couponTarget = couponTarget;
    }

    public List<BookInfo> getCouponBook() {
        return couponBook;
    }

    public void setCouponBook(List<BookInfo> couponBook) {
        this.couponBook = couponBook;
    }

    public String getCouponContentExplain() {
        return couponContentExplain;
    }

    public void setCouponContentExplain(String couponContentExplain) {
        this.couponContentExplain = couponContentExplain;
    }
}
