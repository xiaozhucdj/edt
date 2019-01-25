package com.yougy.task.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class SubmitReplyBean implements Parcelable {

    private int sceneId;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.sceneId);
    }

    public SubmitReplyBean() {
    }

    protected SubmitReplyBean(Parcel in) {
        this.sceneId = in.readInt();
    }

    public static final Creator<SubmitReplyBean> CREATOR = new Creator<SubmitReplyBean>() {
        @Override
        public SubmitReplyBean createFromParcel(Parcel source) {
            return new SubmitReplyBean(source);
        }

        @Override
        public SubmitReplyBean[] newArray(int size) {
            return new SubmitReplyBean[size];
        }
    };
}
