package com.yougy.shop.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by FH on 2018/3/20.
 */

public class Coupon implements Parcelable {
    /**
     * couponCreateTime : 2017-11-23 16:15:33
     * couponContentExplain : 限时满减-个人 满30元减5元
     * couponContent : [{"cut":"5","over":"30"}]
     * couponName : 测试满减个人1
     * couponMemo : null
     * couponType : 限时满减-个人
     * couponEndTime : 2018-04-07 23:55:18
     * couponRemain : null
     * couponTotal : null
     * couponTypeCode : BO03
     * couponId : 7
     * couponCreator : 1
     * couponStartTime : 2017-11-23 16:15:18
     * couponBook : ["7146309","7244707","7244559","7244706"]
     * couponTarget : null
     */

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
    private List<CouponContentBean> couponContent;
    private List<String> couponBook;

    public String getCouponCreateTime() {
        return couponCreateTime;
    }

    public void setCouponCreateTime(String couponCreateTime) {
        this.couponCreateTime = couponCreateTime;
    }

    public String getCouponContentExplain() {
        return couponContentExplain;
    }

    public void setCouponContentExplain(String couponContentExplain) {
        this.couponContentExplain = couponContentExplain;
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

    public Coupon setCouponId(int couponId) {
        this.couponId = couponId;
        return this;
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

    public List<CouponContentBean> getCouponContent() {
        return couponContent;
    }

    public void setCouponContent(List<CouponContentBean> couponContent) {
        this.couponContent = couponContent;
    }

    public List<String> getCouponBook() {
        return couponBook;
    }

    public void setCouponBook(List<String> couponBook) {
        this.couponBook = couponBook;
    }

    public static class CouponContentBean implements Parcelable {
        /**
         * cut : 5
         * over : 30
         */


        private String cut;
        private String over;
        private String free;
        private String off;

        public String getFree() {
            return free;
        }

        public void setFree(String free) {
            this.free = free;
        }

        public String getOff() {
            return off;
        }

        public void setOff(String off) {
            this.off = off;
        }

        public String getCut() {
            return cut;
        }

        public void setCut(String cut) {
            this.cut = cut;
        }

        public String getOver() {
            return over;
        }

        public void setOver(String over) {
            this.over = over;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.cut);
            dest.writeString(this.over);
            dest.writeString(this.free);
            dest.writeString(this.off);
        }

        public CouponContentBean() {
        }

        protected CouponContentBean(Parcel in) {
            this.cut = in.readString();
            this.over = in.readString();
            this.free = in.readString();
            this.off = in.readString();
        }

        public static final Creator<CouponContentBean> CREATOR = new Creator<CouponContentBean>() {
            @Override
            public CouponContentBean createFromParcel(Parcel source) {
                return new CouponContentBean(source);
            }

            @Override
            public CouponContentBean[] newArray(int size) {
                return new CouponContentBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.couponCreateTime);
        dest.writeString(this.couponContentExplain);
        dest.writeString(this.couponName);
        dest.writeString(this.couponMemo);
        dest.writeString(this.couponType);
        dest.writeString(this.couponEndTime);
        dest.writeString(this.couponRemain);
        dest.writeString(this.couponTotal);
        dest.writeString(this.couponTypeCode);
        dest.writeInt(this.couponId);
        dest.writeInt(this.couponCreator);
        dest.writeString(this.couponStartTime);
        dest.writeString(this.couponTarget);
        dest.writeTypedList(this.couponContent);
        dest.writeStringList(this.couponBook);
    }

    public Coupon() {
    }

    protected Coupon(Parcel in) {
        this.couponCreateTime = in.readString();
        this.couponContentExplain = in.readString();
        this.couponName = in.readString();
        this.couponMemo = in.readString();
        this.couponType = in.readString();
        this.couponEndTime = in.readString();
        this.couponRemain = in.readString();
        this.couponTotal = in.readString();
        this.couponTypeCode = in.readString();
        this.couponId = in.readInt();
        this.couponCreator = in.readInt();
        this.couponStartTime = in.readString();
        this.couponTarget = in.readString();
        this.couponContent = in.createTypedArrayList(CouponContentBean.CREATOR);
        this.couponBook = in.createStringArrayList();
    }

    public static final Creator<Coupon> CREATOR = new Creator<Coupon>() {
        @Override
        public Coupon createFromParcel(Parcel source) {
            return new Coupon(source);
        }

        @Override
        public Coupon[] newArray(int size) {
            return new Coupon[size];
        }
    };
}
