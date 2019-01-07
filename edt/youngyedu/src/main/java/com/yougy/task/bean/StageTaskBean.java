package com.yougy.task.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/*
*
* {
    "msg": "success",
    "data": [
        {
            "stageCreator": 10000347,
            "stageMission": null,
            "stageAttach": 160,
            "stageId": 898,
            "stageRank": 11, // 任务项在drama下的次序。
            "stageContent": [
                {
                    "value": "12612",
                    "size": 16985,
                    "format": "ATCH/PNG",
                    "bucket": "global-replies",
                    "remote": "198201/1000002560/2018/origin/1543054849022.png",
                    "origin": null,
                    "version": 0.1
                }
            ],
            "stageType": "签字",
            "stageTypeCode": "SR04",
            "stageCreateTime": "2018-11-22 11:33:35"
        }
    ],
    "code": 200
}
* */
public class StageTaskBean implements Parcelable{

    private int stageCreator;
    private String stageMission;
    private int stageAttach;
    private int stageId;
    private int stageRank;
    private List<StageContent> stageContent;
    private String stageType;
    private String stageTypeCode;
    private String stageCreateTime;

    public int getStageCreator() {
        return stageCreator;
    }

    public void setStageCreator(int stageCreator) {
        this.stageCreator = stageCreator;
    }

    public String getStageMission() {
        return stageMission;
    }

    public void setStageMission(String stageMission) {
        this.stageMission = stageMission;
    }

    public int getStageAttach() {
        return stageAttach;
    }

    public void setStageAttach(int stageAttach) {
        this.stageAttach = stageAttach;
    }

    public int getStageId() {
        return stageId;
    }

    public void setStageId(int stageId) {
        this.stageId = stageId;
    }

    public int getStageRank() {
        return stageRank;
    }

    public void setStageRank(int stageRank) {
        this.stageRank = stageRank;
    }

    public List<StageContent> getStageContent() {
        return stageContent;
    }

    public void setStageContent(List<StageContent> stageContent) {
        this.stageContent = stageContent;
    }

    public String getStageType() {
        return stageType;
    }

    public void setStageType(String stageType) {
        this.stageType = stageType;
    }

    public String getStageTypeCode() {
        return stageTypeCode;
    }

    public void setStageTypeCode(String stageTypeCode) {
        this.stageTypeCode = stageTypeCode;
    }

    public String getStageCreateTime() {
        return stageCreateTime;
    }

    public void setStageCreateTime(String stageCreateTime) {
        this.stageCreateTime = stageCreateTime;
    }

    public StageTaskBean(int stageCreator, String stageMission, int stageAttach, int stageId, int stageRank,
                         List<StageContent> stageContent, String stageType, String stageTypeCode, String stageCreateTime) {
        this.stageCreator = stageCreator;
        this.stageMission = stageMission;
        this.stageAttach = stageAttach;
        this.stageId = stageId;
        this.stageRank = stageRank;
        this.stageContent = stageContent;
        this.stageType = stageType;
        this.stageTypeCode = stageTypeCode;
        this.stageCreateTime = stageCreateTime;
    }

    protected StageTaskBean(Parcel in) {
        stageCreator = in.readInt();
        stageMission = in.readString();
        stageAttach = in.readInt();
        stageId = in.readInt();
        stageRank = in.readInt();
        stageType = in.readString();
        stageTypeCode = in.readString();
        stageCreateTime = in.readString();
    }

    public static final Creator<StageTaskBean> CREATOR = new Creator<StageTaskBean>() {
        @Override
        public StageTaskBean createFromParcel(Parcel in) {
            return new StageTaskBean(in);
        }

        @Override
        public StageTaskBean[] newArray(int size) {
            return new StageTaskBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(stageCreator);
        dest.writeString(stageMission);
        dest.writeInt(stageAttach);
        dest.writeInt(stageId);
        dest.writeInt(stageRank);
        dest.writeString(stageType);
        dest.writeString(stageTypeCode);
        dest.writeString(stageCreateTime);
    }

    public static final String FORMAT_TYPE_TEXT = "TEXT";
    public static final String FORMAT_TYPE_PDF = "PDF";
    public static final String FORMAT_TYPE_IMG = "PNG";
    public static final String FORMAT_TYPE_HTML = "HTML";
    /**
     * 任务详情内容
     */
    public static class StageContent implements Parcelable {
        private String value;
        private int size;
        private String format;
        private String bucket;
        private String remote;
        private String origin;
        private float version;

        public StageContent(String value, int size, String format, String bucket, String remote, String origin, float version) {
            this.value = value;
            this.size = size;
            this.format = format;
            this.bucket = bucket;
            this.remote = remote;
            this.origin = origin;
            this.version = version;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
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

        public String getOrigin() {
            return origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }

        public float getVersion() {
            return version;
        }

        public void setVersion(float version) {
            this.version = version;
        }

        protected StageContent(Parcel in) {
            value = in.readString();
            size = in.readInt();
            format = in.readString();
            bucket = in.readString();
            remote = in.readString();
            origin = in.readString();
            version = in.readFloat();
        }

        public static final Creator<StageContent> CREATOR = new Creator<StageContent>() {
            @Override
            public StageContent createFromParcel(Parcel in) {
                return new StageContent(in);
            }

            @Override
            public StageContent[] newArray(int size) {
                return new StageContent[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(value);
            dest.writeInt(size);
            dest.writeString(format);
            dest.writeString(bucket);
            dest.writeString(remote);
            dest.writeString(origin);
            dest.writeFloat(version);
        }
    }

}
