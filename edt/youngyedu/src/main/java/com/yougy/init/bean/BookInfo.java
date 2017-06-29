package com.yougy.init.bean;


import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;


/**
 * Created by jiangliang on 2016/9/18.
 */
public class BookInfo extends DataSupport implements Parcelable {
/**
 * bookId : 7244704
 * bookTitle : 七年级英语上册
 * bookAuthor : 无
 * bookCategory : 100000
 * bookISBN : 9787107244704
 * bookPublisher : 19
 * bookPublishTime : 2012-05-01
 * bookSalePrice : 8.8
 * bookSummary : <p><a title="Link: http://192.168.12.2:8080/leke_platform/BookAdd.html" href="http://192.168.12.2:8080/leke_platform/BookAdd.html">人教版七年级上</a>册英文教材</p>
 * bookCover : img20160930113829.png
 * bookPreview : preview20160930113821.pdf
 * bookDownload :
 */
    /**
     * 图书编码
     */
    private int bookId = -1;
    /**
     * 图书名称
     */
    private String bookTitle;

    /**
     * 图书子标题
     */
    private String bookSubtitle;
    /**
     * 图书作者
     */
    private String bookAuthor;

    /**
     * 图书卷册
     */
    private String bookVol;
    /**
     * 图书类别
     */
    private int bookCategory = -1;
    /**
     * 图书的ISBN号
     **/
    private String bookISBN;
    /**
     * 图书出版社
     */
    private int bookPublisher = -1;

    /**
     * 图书出版时间
     */
    private String bookPublishTime;
    /**
     * 图书价格
     */
    private double bookSalePrice = -1;
    /**
     * 图书简介
     */
    private String bookSummary;
    /**
     * 图书封面
     */
    private String bookCover;
    /**
     * 图书封面文件大小
     */
    private String bookCoverSize;
    /**
     * 图书试读
     */
    private String bookPreview;
    /**
     * 图书试读文件大小
     */
    private String bookPreviewSize;
    /**
     * 图书资源
     */
    private String bookDownload;
    /**
     * 图书资源文件大小
     */
    private String bookDownloadSize;

    /**
     * 图书在购物车内
     */
    private boolean bookInCart;

    private boolean bookInShelf;
    /**
     * 图书在收藏内
     */
    private boolean bookInFavor;

    /***
     *图书匹配年级索引
     */
    private int bookFitGradeId = -1;
    /***
     *图书匹配年级名称
     */
    private String bookFitGradeName;
    /***
     *图书匹配学科索引
     */
    private int bookFitSubjectId = -1;
    /***
     *图书匹配学科名称
     */
    private String bookFitSubjectName;

    /**
     * 笔记ID
     */
    private int bookFitNoteId =-1;
    /**
     * 笔记标题
     */
    private String bookFitNoteTitle;

    /***
     * 笔记类型
     */
    private int bookFitNoteStyle = 0;


    /**
     * 图书分类名称
     */
    private String bookCategoryName;

    /**
     * 图书出版社名称
     */
    private String bookPublisherName;

    /**
     * 图书版本
     */
    private int bookVersion;

    /**
     * 图书版本名称
     */
    private String bookVersionName;

    /**
     * 图书状态
     */
    private String bookStatus;

    /**
     * 图书状态码
     */
    private String bookStatusCode ;


    /**
     * 对应课程编码
     */
    private int  courseId ;


    /**图书密码*/
    private  String bookDownloadKey  = "";

    public String getDownloadkey() {
        return bookDownloadKey;
    }

    public int getNoteStyle() {
        return bookFitNoteStyle;
    }

    public void setNoteStyle(int bookFitNoteStyle) {
        this.bookFitNoteStyle = bookFitNoteStyle;
    }

    public int getBookFitGradeId() {
        return bookFitGradeId;
    }

    public void setBookFitGradeId(int bookFitGradeId) {
        this.bookFitGradeId = bookFitGradeId;
    }

    public String getBookFitGradeName() {
        return bookFitGradeName;
    }

    public void setBookFitGradeName(String bookFitGradeName) {
        this.bookFitGradeName = bookFitGradeName;
    }

    public int getBookFitSubjectId() {
        return bookFitSubjectId;
    }

    public void setBookFitSubjectId(int bookFitSubjectId) {
        this.bookFitSubjectId = bookFitSubjectId;
    }

    public String getBookFitSubjectName() {
        return bookFitSubjectName;
    }

    public void setBookFitSubjectName(String bookFitSubjectName) {
        this.bookFitSubjectName = bookFitSubjectName;
    }

    public boolean isBookInCart() {
        return bookInCart;
    }

    public void setBookInCart(boolean bookInCart) {
        this.bookInCart = bookInCart;
    }

    public boolean isBookInFavor() {
        return bookInFavor;
    }

    public void setBookInFavor(boolean bookInFavor) {
        this.bookInFavor = bookInFavor;
    }

    public boolean isBookInShelf() {
        return bookInShelf;
    }

    public BookInfo setBookInShelf(boolean bookInShelf) {
        this.bookInShelf = bookInShelf;
        return this;
    }

    public int getBookFitNoteId() {
        return bookFitNoteId;
    }

    public void setBookFitNoteId(int bookFitNoteId) {
        this.bookFitNoteId = bookFitNoteId;
    }

    public String getBookFitNoteTitle() {
        return bookFitNoteTitle;
    }

    public void setBookFitNoteTitle(String bookFitNoteTitle) {
        this.bookFitNoteTitle = bookFitNoteTitle;
    }

    public String getBookPublisherName() {
        return bookPublisherName;
    }

    public BookInfo setBookPublisherName(String bookPublisherName) {
        this.bookPublisherName = bookPublisherName;
        return this;
    }

    ///////////////////////////////////////////////////////////
    /**
     * 判断是否是选中的
     */
    private boolean isCheck = false;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public int getBookCategory() {
        return bookCategory;
    }

    public void setBookCategory(int bookCategory) {
        this.bookCategory = bookCategory;
    }

    public String getBookISBN() {
        return bookISBN;
    }

    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }

    public int getBookPublisher() {
        return bookPublisher;
    }

    public void setBookPublisher(int bookPublisher) {
        this.bookPublisher = bookPublisher;
    }

    public String getBookPublishTime() {
        return bookPublishTime;
    }

    public void setBookPublishTime(String bookPublishTime) {
        this.bookPublishTime = bookPublishTime;
    }

    public double getBookSalePrice() {
        return bookSalePrice;
    }

    public void setBookSalePrice(double bookSalePrice) {
        this.bookSalePrice = bookSalePrice;
    }

    public String getBookSummary() {
        return bookSummary;
    }

    public void setBookSummary(String bookSummary) {
        this.bookSummary = bookSummary;
    }

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public String getBookPreview() {
        return bookPreview;
    }

    public void setBookPreview(String bookPreview) {
        this.bookPreview = bookPreview;
    }

    public String getBookDownload() {
        return bookDownload;
    }

    public void setBookDownload(String bookDownload) {
        this.bookDownload = bookDownload;
    }


    public String getBookCoverSize() {
        return bookCoverSize;
    }

    public void setBookCoverSize(String bookCoverSize) {
        this.bookCoverSize = bookCoverSize;
    }

    public String getBookPreviewSize() {
        return bookPreviewSize;
    }

    public void setBookPreviewSize(String bookPreviewSize) {
        this.bookPreviewSize = bookPreviewSize;
    }

    public String getBookDownloadSize() {
        return bookDownloadSize;
    }

    public void setBookDownloadSize(String bookDownloadSize) {
        this.bookDownloadSize = bookDownloadSize;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public BookInfo() {
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookInfo info = (BookInfo) o;

        if (bookId != info.bookId) return false;
        if (bookCategory != info.bookCategory) return false;
        if (bookPublisher != info.bookPublisher) return false;
        if (Double.compare(info.bookSalePrice, bookSalePrice) != 0) return false;
        if (bookInCart != info.bookInCart) return false;
        if (bookInFavor != info.bookInFavor) return false;
        if (bookFitGradeId != info.bookFitGradeId) return false;
        if (bookFitSubjectId != info.bookFitSubjectId) return false;
        if (bookFitNoteId != info.bookFitNoteId) return false;
        if (bookFitNoteStyle != info.bookFitNoteStyle) return false;
        if (isCheck != info.isCheck) return false;
        if (bookTitle != null ? !bookTitle.equals(info.bookTitle) : info.bookTitle != null)
            return false;
        if (bookAuthor != null ? !bookAuthor.equals(info.bookAuthor) : info.bookAuthor != null)
            return false;
        if (bookISBN != null ? !bookISBN.equals(info.bookISBN) : info.bookISBN != null)
            return false;
        if (bookPublishTime != null ? !bookPublishTime.equals(info.bookPublishTime) : info.bookPublishTime != null)
            return false;
        if (bookSummary != null ? !bookSummary.equals(info.bookSummary) : info.bookSummary != null)
            return false;
        if (bookCover != null ? !bookCover.equals(info.bookCover) : info.bookCover != null)
            return false;
        if (bookCoverSize != null ? !bookCoverSize.equals(info.bookCoverSize) : info.bookCoverSize != null)
            return false;
        if (bookPreview != null ? !bookPreview.equals(info.bookPreview) : info.bookPreview != null)
            return false;
        if (bookPreviewSize != null ? !bookPreviewSize.equals(info.bookPreviewSize) : info.bookPreviewSize != null)
            return false;
        if (bookDownload != null ? !bookDownload.equals(info.bookDownload) : info.bookDownload != null)
            return false;
        if (bookDownloadSize != null ? !bookDownloadSize.equals(info.bookDownloadSize) : info.bookDownloadSize != null)
            return false;
        if (bookFitGradeName != null ? !bookFitGradeName.equals(info.bookFitGradeName) : info.bookFitGradeName != null)
            return false;
        if (bookFitSubjectName != null ? !bookFitSubjectName.equals(info.bookFitSubjectName) : info.bookFitSubjectName != null)
            return false;
        return bookFitNoteTitle != null ? bookFitNoteTitle.equals(info.bookFitNoteTitle) : info.bookFitNoteTitle == null;

    }

    @Override
    public int hashCode() {
        int result = bookId;
        result = 31 * result + (bookTitle != null ? bookTitle.hashCode() : 0);
        result = 31 * result + (bookAuthor != null ? bookAuthor.hashCode() : 0);
        result = 31 * result + bookCategory;
        result = 31 * result + (bookISBN != null ? bookISBN.hashCode() : 0);
        result = 31 * result + bookPublisher;
        result = 31 * result + (bookPublishTime != null ? bookPublishTime.hashCode() : 0);
        result = (int) (31 * result + (bookSalePrice != +0.0f ? Double.doubleToLongBits(bookSalePrice) : 0));
        result = 31 * result + (bookSummary != null ? bookSummary.hashCode() : 0);
        result = 31 * result + (bookCover != null ? bookCover.hashCode() : 0);
        result = 31 * result + (bookCoverSize != null ? bookCoverSize.hashCode() : 0);
        result = 31 * result + (bookPreview != null ? bookPreview.hashCode() : 0);
        result = 31 * result + (bookPreviewSize != null ? bookPreviewSize.hashCode() : 0);
        result = 31 * result + (bookDownload != null ? bookDownload.hashCode() : 0);
        result = 31 * result + (bookDownloadSize != null ? bookDownloadSize.hashCode() : 0);
        result = 31 * result + (bookInCart ? 1 : 0);
        result = 31 * result + (bookInFavor ? 1 : 0);
        result = 31 * result + bookFitGradeId;
        result = 31 * result + (bookFitGradeName != null ? bookFitGradeName.hashCode() : 0);
        result = 31 * result + bookFitSubjectId;
        result = 31 * result + (bookFitSubjectName != null ? bookFitSubjectName.hashCode() : 0);
        result = 31 * result + bookFitNoteId;
        result = 31 * result + (bookFitNoteTitle != null ? bookFitNoteTitle.hashCode() : 0);
        result = 31 * result + bookFitNoteStyle;
        result = 31 * result + (isCheck ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BookInfo{" +
                "bookTitle='" + bookTitle + '\'' +
                '}';
    }

    /**
     * 作业ID
     */
    private int bookFitHomeworkId;

    public int getBookFitHomeworkId() {
        return bookFitHomeworkId;
    }

    public void setBookFitHomeworkId(int bookFitHomeworkId) {
        this.bookFitHomeworkId = bookFitHomeworkId;
    }

    /**
     * 图书匹配作业名称
     */
    private String bookFitHomeworkTitle;

    public String getBookFitHomeworkTitle() {
        return bookFitHomeworkTitle;
    }

    public void setBookFitHomeworkTitle(String bookFitHomeworkTitle) {
        this.bookFitHomeworkTitle = bookFitHomeworkTitle;
    }


    /////////////////////////////////序列化 start ////////////////////////////


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.bookId);
        dest.writeString(this.bookTitle);
        dest.writeString(this.bookSubtitle);
        dest.writeString(this.bookAuthor);
        dest.writeString(this.bookVol);
        dest.writeInt(this.bookCategory);
        dest.writeString(this.bookISBN);
        dest.writeInt(this.bookPublisher);
        dest.writeString(this.bookPublishTime);
        dest.writeDouble(this.bookSalePrice);
        dest.writeString(this.bookSummary);
        dest.writeString(this.bookCover);
        dest.writeString(this.bookCoverSize);
        dest.writeString(this.bookPreview);
        dest.writeString(this.bookPreviewSize);
        dest.writeString(this.bookDownload);
        dest.writeString(this.bookDownloadSize);
        dest.writeByte(this.bookInCart ? (byte) 1 : (byte) 0);
        dest.writeByte(this.bookInShelf ? (byte) 1 : (byte) 0);
        dest.writeByte(this.bookInFavor ? (byte) 1 : (byte) 0);
        dest.writeInt(this.bookFitGradeId);
        dest.writeString(this.bookFitGradeName);
        dest.writeInt(this.bookFitSubjectId);
        dest.writeString(this.bookFitSubjectName);
        dest.writeInt(this.bookFitNoteId);
        dest.writeString(this.bookFitNoteTitle);
        dest.writeInt(this.bookFitNoteStyle);
        dest.writeString(this.bookCategoryName);
        dest.writeString(this.bookPublisherName);
        dest.writeInt(this.bookVersion);
        dest.writeString(this.bookVersionName);
        dest.writeString(this.bookStatus);
        dest.writeString(this.bookStatusCode);
        dest.writeInt(this.courseId);
        dest.writeString(this.bookDownloadKey);
        dest.writeByte(this.isCheck ? (byte) 1 : (byte) 0);
        dest.writeInt(this.bookFitHomeworkId);
        dest.writeString(this.bookFitHomeworkTitle);
    }

    protected BookInfo(Parcel in) {
        this.bookId = in.readInt();
        this.bookTitle = in.readString();
        this.bookSubtitle = in.readString();
        this.bookAuthor = in.readString();
        this.bookVol = in.readString();
        this.bookCategory = in.readInt();
        this.bookISBN = in.readString();
        this.bookPublisher = in.readInt();
        this.bookPublishTime = in.readString();
        this.bookSalePrice = in.readDouble();
        this.bookSummary = in.readString();
        this.bookCover = in.readString();
        this.bookCoverSize = in.readString();
        this.bookPreview = in.readString();
        this.bookPreviewSize = in.readString();
        this.bookDownload = in.readString();
        this.bookDownloadSize = in.readString();
        this.bookInCart = in.readByte() != 0;
        this.bookInShelf = in.readByte() != 0;
        this.bookInFavor = in.readByte() != 0;
        this.bookFitGradeId = in.readInt();
        this.bookFitGradeName = in.readString();
        this.bookFitSubjectId = in.readInt();
        this.bookFitSubjectName = in.readString();
        this.bookFitNoteId = in.readInt();
        this.bookFitNoteTitle = in.readString();
        this.bookFitNoteStyle = in.readInt();
        this.bookCategoryName = in.readString();
        this.bookPublisherName = in.readString();
        this.bookVersion = in.readInt();
        this.bookVersionName = in.readString();
        this.bookStatus = in.readString();
        this.bookStatusCode = in.readString();
        this.courseId = in.readInt();
        this.bookDownloadKey = in.readString();
        this.isCheck = in.readByte() != 0;
        this.bookFitHomeworkId = in.readInt();
        this.bookFitHomeworkTitle = in.readString();
    }

    public static final Parcelable.Creator<BookInfo> CREATOR = new Parcelable.Creator<BookInfo>() {
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


/////////////////////////////////序列化 end ////////////////////////////