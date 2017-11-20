package com.yougy.homework.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/11/17.
 */

public class MistakeSummary implements Parcelable {

    /**
     * extra : {"exam":542,"book":7244559,"useTime":"00:00:51","score":0,"cursor":11,"display":true}
     * item : 189
     * version : 0.1
     * format : UNKNOWN
     */

    private ExtraBean extra;
    private int item;
    private double version;
    private String format;

    public ExtraBean getExtra() {
        return extra;
    }

    public void setExtra(ExtraBean extra) {
        this.extra = extra;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public static class ExtraBean implements Parcelable {
        /**
         * exam : 542
         * book : 7244559
         * useTime : 00:00:51
         * score : 0
         * cursor : 11
         * display : true
         */

        private int exam;
        private int book;
        private String useTime;
        private int score;
        private int cursor;
        private boolean display;
        private String name;

        public int getExam() {
            return exam;
        }

        public void setExam(int exam) {
            this.exam = exam;
        }

        public int getBook() {
            return book;
        }

        public void setBook(int book) {
            this.book = book;
        }

        public String getUseTime() {
            return useTime;
        }

        public void setUseTime(String useTime) {
            this.useTime = useTime;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getCursor() {
            return cursor;
        }

        public void setCursor(int cursor) {
            this.cursor = cursor;
        }

        public boolean isDisplay() {
            return display;
        }

        public void setDisplay(boolean display) {
            this.display = display;
        }

        public String getName() {
            return name;
        }

        public ExtraBean setName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.exam);
            dest.writeInt(this.book);
            dest.writeString(this.useTime);
            dest.writeInt(this.score);
            dest.writeInt(this.cursor);
            dest.writeByte(this.display ? (byte) 1 : (byte) 0);
            dest.writeString(this.name);
        }

        public ExtraBean() {
        }

        protected ExtraBean(Parcel in) {
            this.exam = in.readInt();
            this.book = in.readInt();
            this.useTime = in.readString();
            this.score = in.readInt();
            this.cursor = in.readInt();
            this.display = in.readByte() != 0;
            this.name = in.readString();
        }

        public static final Parcelable.Creator<ExtraBean> CREATOR = new Parcelable.Creator<ExtraBean>() {
            @Override
            public ExtraBean createFromParcel(Parcel source) {
                return new ExtraBean(source);
            }

            @Override
            public ExtraBean[] newArray(int size) {
                return new ExtraBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.extra, flags);
        dest.writeInt(this.item);
        dest.writeDouble(this.version);
        dest.writeString(this.format);
    }

    public MistakeSummary() {
    }

    protected MistakeSummary(Parcel in) {
        this.extra = in.readParcelable(ExtraBean.class.getClassLoader());
        this.item = in.readInt();
        this.version = in.readDouble();
        this.format = in.readString();
    }

    public static final Parcelable.Creator<MistakeSummary> CREATOR = new Parcelable.Creator<MistakeSummary>() {
        @Override
        public MistakeSummary createFromParcel(Parcel source) {
            return new MistakeSummary(source);
        }

        @Override
        public MistakeSummary[] newArray(int size) {
            return new MistakeSummary[size];
        }
    };
}
