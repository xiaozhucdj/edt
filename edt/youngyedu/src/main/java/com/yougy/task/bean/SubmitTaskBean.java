package com.yougy.task.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.yougy.anwser.STSResultbean;

import java.util.ArrayList;
import java.util.List;

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
public class SubmitTaskBean implements Parcelable {

    private ArrayList<SubmitTask> mSubmitTasks;

    public ArrayList<SubmitTask> getSubmitTasks() {
        return mSubmitTasks;
    }

    public void setSubmitTasks(ArrayList<SubmitTask> submitTasks) {
        mSubmitTasks = submitTasks;
    }

    public static class SubmitTask implements Parcelable {
        private int performId;
        private int stageId;
        private ArrayList<STSResultbean> picContent;
        private String sceneCreateTime;
        private String[] textContent = new String[0];

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


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.performId);
            dest.writeInt(this.stageId);
            dest.writeList(this.picContent);
            dest.writeString(this.sceneCreateTime);
            dest.writeStringArray(this.textContent);
        }

        public SubmitTask() {
        }

        protected SubmitTask(Parcel in) {
            this.performId = in.readInt();
            this.stageId = in.readInt();
            this.picContent = new ArrayList<STSResultbean>();
            in.readList(this.picContent, STSResultbean.class.getClassLoader());
            this.sceneCreateTime = in.readString();
            this.textContent = in.createStringArray();
        }

        public static final Creator<SubmitTask> CREATOR = new Creator<SubmitTask>() {
            @Override
            public SubmitTask createFromParcel(Parcel source) {
                return new SubmitTask(source);
            }

            @Override
            public SubmitTask[] newArray(int size) {
                return new SubmitTask[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mSubmitTasks);
    }

    public SubmitTaskBean() {
    }

    protected SubmitTaskBean(Parcel in) {
        this.mSubmitTasks = in.createTypedArrayList(SubmitTask.CREATOR);
    }

    public static final Creator<SubmitTaskBean> CREATOR = new Creator<SubmitTaskBean>() {
        @Override
        public SubmitTaskBean createFromParcel(Parcel source) {
            return new SubmitTaskBean(source);
        }

        @Override
        public SubmitTaskBean[] newArray(int size) {
            return new SubmitTaskBean[size];
        }
    };
}
