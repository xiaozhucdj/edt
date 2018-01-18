package com.yougy.anwser;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by FH on 2018/1/5.
 */

public class Content_new<E> implements Parcelable {
    public enum Type{
        HTML_URL ,
        IMG_URL ,
        TEXT ,
        PDF
    }
    private double version;
    private Content_new.Type type;
    private String content;
    private E extraData;

    public Content_new(Content_new.Type type , double version , String content , E extraData){
        this.type = type;
        this.version = version;
        this.content = content;
        this.extraData = extraData;
    }


    public String getValue(){
        return content;
    }

    public Content_new.Type getType(){
        return type;
    }

    public double getVersion(){
        return version;
    }

    public E getExtraData(){
        return extraData;
    }

    public void setValue(String value){
        this.content = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.version);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeString(this.content);
        dest.writeValue(this.extraData);
    }

    protected Content_new(Parcel in) {
        this.version = in.readDouble();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : Type.values()[tmpType];
        this.content = in.readString();
        this.extraData = (E) in.readValue(null);
    }

    public static final Parcelable.Creator<Content_new> CREATOR = new Parcelable.Creator<Content_new>() {
        @Override
        public Content_new createFromParcel(Parcel source) {
            return new Content_new(source);
        }

        @Override
        public Content_new[] newArray(int size) {
            return new Content_new[size];
        }
    };
}
