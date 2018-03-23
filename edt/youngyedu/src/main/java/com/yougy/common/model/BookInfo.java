package com.yougy.common.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangliang on 2017/3/20.
 */

public class BookInfo implements Parcelable {

    public String bookAuthor;
    public int bookVersion;
    public String bookISBN;
    public String bookPublisherName;
    public String bookDownload;
    public String bookSummary;
    public String bookCoverS;
    public int bookSupplier;
    public String bookPreview;
    public String bookStatusCode;
    public double bookSalePrice;
    public String bookVol;
    public double bookOriginalPrice;
    public String bookCreateTime;
    public int bookVersionTime;
    public String bookCategoryName;
    public String bookSubtitle;
    public String bookAwards;
    public String bookPublishTime;
    public String bookCoverL;
    public int bookCategory;
    public int bookId;
    public int bookCreator;
    public String bookCategoryFamilyName;
    public BookContentsBean bookContents;
    public int bookCategoryFamily;
    public String bookKeyWord;
    public int bookPublisher;
    public String bookTitle;
    public String bookModifyTime;
    public String bookVersionName;
    public String bookStatus;
    public String bookDownloadKey;
    public List<BookAtchBean> bookAtch;


    public static class BookContentsBean implements Parcelable {
        /**
         * nodes : [{"name":"Unit1 My name is Gina.","id":1,"level":1},{"name":"Unit2 This is my sister.","id":2,"level":1},{"name":"Unit3 Is this your pencil?","id":3,"level":1},{"name":"Unit4 Where is my schoolbag?","id":4,"level":1},{"name":"Unit5 Do you have a soccer ball?","id":5,"level":1},{"name":"Unit6 Do you like bananas?","id":6,"level":1},{"name":"Unit7 How much are these socks?","id":7,"level":1},{"name":"Unit8 When is your birthday?","id":8,"level":1},{"name":"Unit9 My favoride subject is science.","id":9,"level":1}]
         * version : 0.1
         */

        public double version;
        public List<NodesBean> nodes;



        public static class NodesBean implements Parcelable {
            /**
             * name : Unit1 My name is Gina.
             * id : 1
             * level : 1
             */

            public String name;
            public int id;
            public int level;

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.name);
                dest.writeInt(this.id);
                dest.writeInt(this.level);
            }

            public NodesBean() {
            }

            protected NodesBean(Parcel in) {
                this.name = in.readString();
                this.id = in.readInt();
                this.level = in.readInt();
            }

            public static final Creator<NodesBean> CREATOR = new Creator<NodesBean>() {
                @Override
                public NodesBean createFromParcel(Parcel source) {
                    return new NodesBean(source);
                }

                @Override
                public NodesBean[] newArray(int size) {
                    return new NodesBean[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeDouble(this.version);
            dest.writeList(this.nodes);
        }

        public BookContentsBean() {
        }

        protected BookContentsBean(Parcel in) {
            this.version = in.readDouble();
            this.nodes = new ArrayList<NodesBean>();
            in.readList(this.nodes, NodesBean.class.getClassLoader());
        }

        public static final Creator<BookContentsBean> CREATOR = new Creator<BookContentsBean>() {
            @Override
            public BookContentsBean createFromParcel(Parcel source) {
                return new BookContentsBean(source);
            }

            @Override
            public BookContentsBean[] newArray(int size) {
                return new BookContentsBean[size];
            }
        };
    }

    public static class BookAtchBean implements Parcelable {
        public int userId;
        public String atchType;
        public String atchTypeCode;
        public String userStatus;
        public String atchCreateTime;
        public String atchEncryptMode;
        public int atchId;
        public String atchSupportCode;
        public String atchOriginPath;
        public int bookId;
        public String atchEncryptKey;
        public int atchSize;
        public String atchBucket;
        public String userRealName;
        public String userName;
        public String atchFormat;
        public String atchSupport;
        public String atchRemotePath;
        public String userRole;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.userId);
            dest.writeString(this.atchType);
            dest.writeString(this.atchTypeCode);
            dest.writeString(this.userStatus);
            dest.writeString(this.atchCreateTime);
            dest.writeString(this.atchEncryptMode);
            dest.writeInt(this.atchId);
            dest.writeString(this.atchSupportCode);
            dest.writeString(this.atchOriginPath);
            dest.writeInt(this.bookId);
            dest.writeString(this.atchEncryptKey);
            dest.writeInt(this.atchSize);
            dest.writeString(this.atchBucket);
            dest.writeString(this.userRealName);
            dest.writeString(this.userName);
            dest.writeString(this.atchFormat);
            dest.writeString(this.atchSupport);
            dest.writeString(this.atchRemotePath);
            dest.writeString(this.userRole);
        }

        public BookAtchBean() {
        }

        protected BookAtchBean(Parcel in) {
            this.userId = in.readInt();
            this.atchType = in.readString();
            this.atchTypeCode = in.readString();
            this.userStatus = in.readString();
            this.atchCreateTime = in.readString();
            this.atchEncryptMode = in.readString();
            this.atchId = in.readInt();
            this.atchSupportCode = in.readString();
            this.atchOriginPath = in.readString();
            this.bookId = in.readInt();
            this.atchEncryptKey = in.readString();
            this.atchSize = in.readInt();
            this.atchBucket = in.readString();
            this.userRealName = in.readString();
            this.userName = in.readString();
            this.atchFormat = in.readString();
            this.atchSupport = in.readString();
            this.atchRemotePath = in.readString();
            this.userRole = in.readString();
        }

        public static final Creator<BookAtchBean> CREATOR = new Creator<BookAtchBean>() {
            @Override
            public BookAtchBean createFromParcel(Parcel source) {
                return new BookAtchBean(source);
            }

            @Override
            public BookAtchBean[] newArray(int size) {
                return new BookAtchBean[size];
            }
        };
    }

    public BookInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bookAuthor);
        dest.writeInt(this.bookVersion);
        dest.writeString(this.bookISBN);
        dest.writeString(this.bookPublisherName);
        dest.writeString(this.bookDownload);
        dest.writeString(this.bookSummary);
        dest.writeString(this.bookCoverS);
        dest.writeInt(this.bookSupplier);
        dest.writeString(this.bookPreview);
        dest.writeString(this.bookStatusCode);
        dest.writeDouble(this.bookSalePrice);
        dest.writeString(this.bookVol);
        dest.writeDouble(this.bookOriginalPrice);
        dest.writeString(this.bookCreateTime);
        dest.writeInt(this.bookVersionTime);
        dest.writeString(this.bookCategoryName);
        dest.writeString(this.bookSubtitle);
        dest.writeString(this.bookAwards);
        dest.writeString(this.bookPublishTime);
        dest.writeString(this.bookCoverL);
        dest.writeInt(this.bookCategory);
        dest.writeInt(this.bookId);
        dest.writeInt(this.bookCreator);
        dest.writeString(this.bookCategoryFamilyName);
        dest.writeParcelable(this.bookContents, flags);
        dest.writeInt(this.bookCategoryFamily);
        dest.writeString(this.bookKeyWord);
        dest.writeInt(this.bookPublisher);
        dest.writeString(this.bookTitle);
        dest.writeString(this.bookModifyTime);
        dest.writeString(this.bookVersionName);
        dest.writeString(this.bookStatus);
        dest.writeString(this.bookDownloadKey);
        dest.writeTypedList(this.bookAtch);
    }

    protected BookInfo(Parcel in) {
        this.bookAuthor = in.readString();
        this.bookVersion = in.readInt();
        this.bookISBN = in.readString();
        this.bookPublisherName = in.readString();
        this.bookDownload = in.readString();
        this.bookSummary = in.readString();
        this.bookCoverS = in.readString();
        this.bookSupplier = in.readInt();
        this.bookPreview = in.readString();
        this.bookStatusCode = in.readString();
        this.bookSalePrice = in.readDouble();
        this.bookVol = in.readString();
        this.bookOriginalPrice = in.readDouble();
        this.bookCreateTime = in.readString();
        this.bookVersionTime = in.readInt();
        this.bookCategoryName = in.readString();
        this.bookSubtitle = in.readString();
        this.bookAwards = in.readString();
        this.bookPublishTime = in.readString();
        this.bookCoverL = in.readString();
        this.bookCategory = in.readInt();
        this.bookId = in.readInt();
        this.bookCreator = in.readInt();
        this.bookCategoryFamilyName = in.readString();
        this.bookContents = in.readParcelable(BookContentsBean.class.getClassLoader());
        this.bookCategoryFamily = in.readInt();
        this.bookKeyWord = in.readString();
        this.bookPublisher = in.readInt();
        this.bookTitle = in.readString();
        this.bookModifyTime = in.readString();
        this.bookVersionName = in.readString();
        this.bookStatus = in.readString();
        this.bookDownloadKey = in.readString();
        this.bookAtch = in.createTypedArrayList(BookAtchBean.CREATOR);
    }

    public static final Creator<BookInfo> CREATOR = new Creator<BookInfo>() {
        @Override
        public BookInfo createFromParcel(Parcel source) {
            return new BookInfo(source);
        }

        @Override
        public BookInfo[] newArray(int size) {
            return new BookInfo[size];
        }
    };
}
