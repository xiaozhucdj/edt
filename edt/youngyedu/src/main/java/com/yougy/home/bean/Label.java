package com.yougy.home.bean;


import org.litepal.crud.DataSupport;

/**
 * Created by jiangliang on 2016/8/12.
 */
public class Label extends DataSupport {

    private int id;
    private int leftMargin;
    private int topMargin;
//    private String bitmapPath;
    private String text;
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

//    public String getBitmapPath() {
//        return bitmapPath;
//    }
//
//    public void setBitmapPath(String bitmapPath) {
//        this.bitmapPath = bitmapPath;
//    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Label label = (Label) o;

        if (leftMargin != label.leftMargin) return false;
        return topMargin == label.topMargin;

    }

    @Override
    public int hashCode() {
        int result = leftMargin;
        result = 31 * result + topMargin;
        return result;
    }

    @Override
    public String toString() {
        return "Label{" +
                "id=" + id +
                ", leftMargin=" + leftMargin +
                ", topMargin=" + topMargin +
                ", text='" + text + '\'' +
                '}';
    }
}
