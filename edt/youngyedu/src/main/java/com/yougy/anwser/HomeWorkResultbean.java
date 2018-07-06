package com.yougy.anwser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/12.
 */

public class HomeWorkResultbean {

    private int examId;
    private int itemId;
    private List<String> txtContent;
    private ArrayList<STSResultbean> picContent;
    private String useTime;
    private String replyCreateTime;

    public String getReplyCreateTime() {
        return replyCreateTime;
    }

    public void setReplyCreateTime(String replyCreateTime) {
        this.replyCreateTime = replyCreateTime;
    }

    public List<String> getTxtContent() {
        return txtContent;
    }

    public void setTxtContent(List<String> txtContent) {
        this.txtContent = txtContent;
    }

    public ArrayList<STSResultbean> getPicContent() {
        return picContent;
    }

    public void setPicContent(ArrayList<STSResultbean> picContent) {
        this.picContent = picContent;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }


    public String getUseTime() {
        return useTime;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }
}
