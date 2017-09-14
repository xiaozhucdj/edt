package com.yougy.anwser;

/**
 * Created by Administrator on 2017/9/12.
 */

public class STSResultbean {

    private double version = 0.1;
    private long size;
    private String bucket;
    private String format = "ATCH/PNG";
    private String origin;
    private String remote;

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getRemote() {
        return remote;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }
}
