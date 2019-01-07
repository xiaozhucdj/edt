package com.yougy.task.bean;

import com.yougy.anwser.STSResultbean;

import java.util.ArrayList;

/*
* [
        { "performId":3165, // 任务ID
          "stageId":2001, // 任务练习ID
          "picContent":[{"bucket":"global-replies","format":"ATCH/PNG","remote":"198201/1000002558/2018/origin/3165_0_0.png","size":16453,"version":0.1}],
          "sceneCreateTime":"2018-11-26 09:35:04",
          "txtContent":[]}
    ]

 {
    "error_code": 0,
    "data": {
      "uid": "1",
      "username": "12154545",
      "name": "吴系挂",
      "groupid": 2 ,
      "reg_time": "1436864169",
      "last_login_time": "0",
    }
  }


* */
public class SubmitTaskBean {
    private int performId;
    private int stageId;
    private ArrayList<STSResultbean> picContent;
    private String sceneCreateTime;
    private String[] textContent;

    public ArrayList<STSResultbean> getPicContent() {
        return picContent;
    }

    public void setPicContent(ArrayList<STSResultbean> picContent) {
        this.picContent = picContent;
    }


    public int getPerformId() {
        return performId;
    }

    public void setPerformId(int performId) {
        this.performId = performId;
    }

    public int getStageId() {
        return stageId;
    }

    public void setStageId(int stageId) {
        this.stageId = stageId;
    }

    public String getSceneCreateTime() {
        return sceneCreateTime;
    }

    public void setSceneCreateTime(String sceneCreateTime) {
        this.sceneCreateTime = sceneCreateTime;
    }

    public String[] getTextContent() {
        return textContent;
    }

    public void setTextContent(String[] textContent) {
        this.textContent = textContent;
    }
}
