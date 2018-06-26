package com.yougy.task.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lenovo on 2018/6/22.
 */

public class MaterialBean implements Parcelable {

    private int materialId;

    private String materialName;

    public MaterialBean(int materialId, String materialName) {
        this.materialId = materialId;
        this.materialName = materialName;
    }

    protected MaterialBean(Parcel in) {
        materialId = in.readInt();
        materialName = in.readString();
    }

    public static final Creator<MaterialBean> CREATOR = new Creator<MaterialBean>() {
        @Override
        public MaterialBean createFromParcel(Parcel in) {
            return new MaterialBean(in);
        }

        @Override
        public MaterialBean[] newArray(int size) {
            return new MaterialBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(materialId);
        dest.writeString(materialName);
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }
}
