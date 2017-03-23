package com.yougy.home.bean;


import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2016/7/11.
 * //书签
 */
public class BookMarkInfo extends DataSupport {
    /** 书签 别名*/
    private String markName;
    /** 书签对应的页码*/
    private int number;
    /** 书签创建时间*/
    private String creatTime;
    /** 对应的PDF 文件名*/
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMarkName() {
        return markName;
    }

    public void setMarkName(String markName) {
        this.markName = markName;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }
}
