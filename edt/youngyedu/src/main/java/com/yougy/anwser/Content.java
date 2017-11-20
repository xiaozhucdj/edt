package com.yougy.anwser;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by FH on 2017/8/30.
 */

public class Content implements Parcelable {
    public enum Type{
        HTML_URL ,
        IMG_URL ,
        TEXT
    }
    private double version;
    private Type type;
    private String content;

    private Content(Type type , double version , String content){
        this.type = type;
        this.version = version;
        this.content = content;
    }

    public static Content newHtmlContent(double version , String url){
        return new Content(Type.HTML_URL , version , url);
    }

    public static Content newImgContent(double version , String imgUrl){
        return new Content(Type.IMG_URL , version , imgUrl);
    }

    public static Content newTextContent(double version , String text){
        return new Content(Type.TEXT , version , text);
    }


    public String getValue(){
        return content;
    }

    public Type getType(){
        return type;
    }

    public double getVersion(){
        return version;
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
    }

    protected Content(Parcel in) {
        this.version = in.readDouble();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : Type.values()[tmpType];
        this.content = in.readString();
    }

    public static final Parcelable.Creator<Content> CREATOR = new Parcelable.Creator<Content>() {
        @Override
        public Content createFromParcel(Parcel source) {
            return new Content(source);
        }

        @Override
        public Content[] newArray(int size) {
            return new Content[size];
        }
    };
}
