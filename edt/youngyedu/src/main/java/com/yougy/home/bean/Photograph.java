package com.yougy.home.bean;


import org.litepal.crud.DataSupport;

/**
 * Created by jiangliang on 2016/8/12.
 */
public class Photograph extends DataSupport {
    private int id;
    private int leftMargin;
    private int topMargin;
    private byte [] bytes;

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLeftMargin() {
        return leftMargin;
    }

    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    }

    public int getTopMargin() {
        return topMargin;
    }

    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
    }

    @Override
    public String toString() {
        return "Photograph{" +
                "id=" + id +
                ", leftMargin=" + leftMargin +
                ", topMargin=" + topMargin +
                '}';
    }
}
