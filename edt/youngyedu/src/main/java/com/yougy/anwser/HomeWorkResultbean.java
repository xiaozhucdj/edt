package com.yougy.anwser;

/**
 * Created by Administrator on 2017/9/12.
 */

public class HomeWorkResultbean {

    private int examId;
    private int itemId;
    private String txtcontent;
    private String piccontent;
    private String useTime;

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

    public String getTxtcontent() {
        return txtcontent;
    }

    public void setTxtcontent(String txtcontent) {
        this.txtcontent = txtcontent;
    }

    public String getPiccontent() {
        return piccontent;
    }

    public void setPiccontent(String piccontent) {
        this.piccontent = piccontent;
    }

    public String getUseTime() {
        return useTime;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }
}
