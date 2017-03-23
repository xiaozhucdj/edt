package com.yougy.common.nohttp;

import java.io.File;

/**
 * Created by Administrator on 2016/10/20.
 */
public class DownInfo {


    /***
     * 下载地址。
     */
    private String url;
    /***
     * 保存的文件夹。
     */
    private String fileFolder;
    /***
     * 文件名。
     */
    private String filename;
    /***
     * 是否断点续传下载。
     */
    private boolean isRange;
    /**
     * 如果发现存在同名文件，是否删除后重新下载，如果不删除，则直接下载成功。
     */
    private boolean isDeleteOld;
    /**
     * 请求标识
     */
    private int what;
    private String bookName;
    private int index;

    /**
     * @param url         下载地址。
     * @param fileFolder  保存的文件夹。
     * @param filename    文件名。
     * @param isRange     是否断点续传下载。
     * @param isDeleteOld 如果发现存在同名文件，是否删除后重新下载，如果不删除，则直接下载成功
     * @param what        请求标识
     */
    public DownInfo(String url, String fileFolder, String filename, boolean isRange, boolean isDeleteOld, int what) {
        this.url = url;
        this.fileFolder = fileFolder;
        this.filename = filename;
        this.isRange = isRange;
        this.isDeleteOld = isDeleteOld;
        this.what = what;
    }

    public String getUrl() {
        return url;
    }

    public String getFileFolder() {
        return fileFolder;
    }

    public String getFilename() {
        return filename;
    }

    public boolean isRange() {
        return isRange;
    }

    public boolean isDeleteOld() {
        return isDeleteOld;
    }

    public int getWhat() {
        return what;
    }

    public boolean isexists() {
        File file = new File(fileFolder, filename);
        return file.exists();
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
