package com.yougy.home.bean;

/**
 * Created by Administrator on 2016/9/1.
 *  保存 画笔状态的bean
 */
public class PaintDrawStateInfo {
    /**画笔大小*/
    private  String PAN_SIZE = "PAN_SIZE" ;
    /**画笔颜色*/
    private  String PAN_COLOR = "PAN_COLOR" ;
    /**画笔  大小进度条*/
    private  String PAN_SIZE_PROGRESS = "PAN_SIZE_PROGRESS" ;
    /**画笔  透明度 进度*/
    private  String PAN_ALPH_PROGRESS ="PAN_ALPH_PROGRESS" ;

    ///////////////////////PaintValue/////////////////////////////
    private float panSize  =-1;
    private int   panColor = -1;
    private int   panSizeProgress =-1 ;
    private int   panAlphProgress = -1 ;


    public String getPAN_SIZE() {
        return PAN_SIZE;
    }

    public String getPAN_COLOR() {
        return PAN_COLOR;
    }



    public String getPAN_ALPH_PROGRESS() {
        return PAN_ALPH_PROGRESS;
    }

    public String getPAN_SIZE_PROGRESS() {
        return PAN_SIZE_PROGRESS;
    }

    public float getPanSize() {
        return panSize;
    }

    public int getPanColor() {
        return panColor;
    }



    public int getPanSizeProgress() {
        return panSizeProgress;
    }

    public int getPanAlphProgress() {
        return panAlphProgress;
    }


    public void setPanSize(float panSize) {
        this.panSize = panSize;
    }

    public void setPanColor(int panColor) {
        this.panColor = panColor;
    }
    public void setPanAlphProgress(int panAlphProgress) {
        this.panAlphProgress = panAlphProgress;
    }
    public void setPanSizeProgress(int panSizeProgress) {
        this.panSizeProgress = panSizeProgress;
    }


}
