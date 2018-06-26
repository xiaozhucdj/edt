package com.yougy.task.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author: zhang yc
 * @create date: 2018/6/22 14:20
 * @class desc: 任务学习资料
 * @modifier: 
 * @modify date: 2018/6/22 14:20
 * @modify desc: 
 */
public class StudyDataBean implements Parcelable{
    private String dataTitle;
    private int dataType;
    private String dataUrl;
    private int studyDataId;

    public StudyDataBean(String dataTitle, int dataType, String dataUrl, int studyDataId) {
        this.dataTitle = dataTitle;
        this.dataType = dataType;
        this.dataUrl = dataUrl;
        this.studyDataId = studyDataId;
    }

    protected StudyDataBean(Parcel in) {
        dataTitle = in.readString();
        dataType = in.readInt();
        dataUrl = in.readString();
        studyDataId = in.readInt();
    }

    public static final Creator<StudyDataBean> CREATOR = new Creator<StudyDataBean>() {
        @Override
        public StudyDataBean createFromParcel(Parcel in) {
            return new StudyDataBean(in);
        }

        @Override
        public StudyDataBean[] newArray(int size) {
            return new StudyDataBean[size];
        }
    };

    public String getDataTitle() {
        return dataTitle;
    }

    public void setDataTitle(String dataTitle) {
        this.dataTitle = dataTitle;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public int getStudyDataId() {
        return studyDataId;
    }

    public void setStudyDataId(int studyDataId) {
        this.studyDataId = studyDataId;
    }

    @Override
    public String toString() {
        return "StudyDataBean{" +
                "dataTitle='" + dataTitle + '\'' +
                ", dataType=" + dataType +
                ", dataUrl='" + dataUrl + '\'' +
                ", studyDataId=" + studyDataId +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dataTitle);
        dest.writeInt(dataType);
        dest.writeString(dataUrl);
        dest.writeInt(studyDataId);
    }
}
