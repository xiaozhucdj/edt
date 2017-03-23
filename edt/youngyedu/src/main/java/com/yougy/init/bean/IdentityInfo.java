package com.yougy.init.bean;

/**
 * Created by jiangliang on 2016/10/14.
 */

public class IdentityInfo {

    private String name;
    private String number;
    private boolean isBind;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isBind() {
        return isBind;
    }

    public void setBind(boolean bind) {
        isBind = bind;
    }

    @Override
    public String toString() {
        return "IdentityInfo{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", isBind=" + isBind +
                '}';
    }
}
