package com.artifex.mupdfdemo.pdf.bean;

/**
 * Created by Administrator on 2016/7/20.
 * 课本的 fragment info
 */
@Deprecated
public class TextBookInfo {
    private String fileName ;
    private String fileAbs;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileAbs() {
        return fileAbs;
    }

    public void setFileAbs(String fileAbs) {
        this.fileAbs = fileAbs;
    }
}
