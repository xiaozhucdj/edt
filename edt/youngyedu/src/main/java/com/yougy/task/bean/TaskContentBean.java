package com.yougy.task.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author: zhang yc
 * @create date: 2018/6/22 14:24
 * @class desc: 任务内容
 * @modifier: 
 * @modify date: 2018/6/22 14:24
 * @modify desc: 
 */
public class TaskContentBean implements Parcelable{
    private int contentNumber;
    private int contentPadingRight;
    private int contentSize;
    private int contentlineSpacing;
    private int ontentPadingleft;
    private String taskContentTitle;

    public TaskContentBean(int contentNumber, int contentPadingRight, int contentSize, int contentlineSpacing, int ontentPadingleft, String taskContentTitle) {
        this.contentNumber = contentNumber;
        this.contentPadingRight = contentPadingRight;
        this.contentSize = contentSize;
        this.contentlineSpacing = contentlineSpacing;
        this.ontentPadingleft = ontentPadingleft;
        this.taskContentTitle = taskContentTitle;
    }

    protected TaskContentBean(Parcel in) {
        contentNumber = in.readInt();
        contentPadingRight = in.readInt();
        contentSize = in.readInt();
        contentlineSpacing = in.readInt();
        ontentPadingleft = in.readInt();
        taskContentTitle = in.readString();
    }

    public static final Creator<TaskContentBean> CREATOR = new Creator<TaskContentBean>() {
        @Override
        public TaskContentBean createFromParcel(Parcel in) {
            return new TaskContentBean(in);
        }

        @Override
        public TaskContentBean[] newArray(int size) {
            return new TaskContentBean[size];
        }
    };

    public int getContentNumber() {
        return contentNumber;
    }

    public void setContentNumber(int contentNumber) {
        this.contentNumber = contentNumber;
    }

    public int getContentPadingRight() {
        return contentPadingRight;
    }

    public void setContentPadingRight(int contentPadingRight) {
        this.contentPadingRight = contentPadingRight;
    }

    public int getContentSize() {
        return contentSize;
    }

    public void setContentSize(int contentSize) {
        this.contentSize = contentSize;
    }

    public int getContentlineSpacing() {
        return contentlineSpacing;
    }

    public void setContentlineSpacing(int contentlineSpacing) {
        this.contentlineSpacing = contentlineSpacing;
    }

    public int getOntentPadingleft() {
        return ontentPadingleft;
    }

    public void setOntentPadingleft(int ontentPadingleft) {
        this.ontentPadingleft = ontentPadingleft;
    }

    public String getTaskContentTitle() {
        return taskContentTitle;
    }

    public void setTaskContentTitle(String taskContentTitle) {
        this.taskContentTitle = taskContentTitle;
    }

    @Override
    public String toString() {
        return "TaskContentBean{" +
                "contentNumber=" + contentNumber +
                ", contentPadingRight=" + contentPadingRight +
                ", contentSize=" + contentSize +
                ", contentlineSpacing=" + contentlineSpacing +
                ", ontentPadingleft=" + ontentPadingleft +
                ", taskContentTitle='" + taskContentTitle + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(contentNumber);
        dest.writeInt(contentPadingRight);
        dest.writeInt(contentSize);
        dest.writeInt(contentlineSpacing);
        dest.writeInt(ontentPadingleft);
        dest.writeString(taskContentTitle);
    }
}
