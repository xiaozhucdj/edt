package com.yougy.home.bean;

/**
 * Created by Administrator on 2017/4/19.
 * 图书目录
 */

public class DirectoryModel {
    private String title ;
    private String position ;
    private boolean isHead  = false;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isHead() {
        return isHead;
    }

    public void setHead(boolean head) {
        isHead = head;
    }

    @Override
    public String toString() {
        return "DirectoryModel{" +
                "title='" + title + '\'' +
                ", position='" + position + '\'' +
                ", isHead=" + isHead +
                '}';
    }
}
