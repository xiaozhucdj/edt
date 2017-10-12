package com.yougy.shop.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jiangliang on 2017/3/20.
 */

public class BookInfo implements Parcelable{

    private String bookId;
    private String bookAwards;
    private String bookISBN;
    private String bookSupplier;
    private String bookCreateTime;
    private String bookSubtitle;
    private String bookTitle;
    private String bookSummary;
    private String bookPublisher;
    private String bookSalePrice;
    private String bookVol;
    private String bookPreview;
    private String bookCover;
    private String bookCategory;
    private String bookStatus;
    private String bookVersion;
    private String bookAuthor;
    private String bookDownload;
    private String bookPublishTime;
    private String bookPrice;
    private String bookCreator;
    private String bookKeyWord;
    private String bookCategoryFamilyName;
    private String bookCategoryName;
    private String bookStatusCode;
    private String bookPublisherName;
    private  int bookCategoryFamily;
    private String bookVersionName;
    private String bookCoverS ;
    private String bookCoverL;

    public BookInfo(){

    }
    protected BookInfo(Parcel in) {
        bookId = in.readString();
        bookAwards = in.readString();
        bookISBN = in.readString();
        bookSupplier = in.readString();
        bookCreateTime = in.readString();
        bookSubtitle = in.readString();
        bookTitle = in.readString();
        bookSummary = in.readString();
        bookPublisher = in.readString();
        bookSalePrice = in.readString();
        bookVol = in.readString();
        bookPreview = in.readString();
        bookCover = in.readString();
        bookCategory = in.readString();
        bookStatus = in.readString();
        bookVersion = in.readString();
        bookAuthor = in.readString();
        bookDownload = in.readString();
        bookPublishTime = in.readString();
        bookPrice = in.readString();
        bookCreator = in.readString();
        bookKeyWord = in.readString();
        bookCategoryFamilyName = in.readString();
        bookCategoryName = in.readString();
        bookStatusCode = in.readString();
        bookPublisherName = in.readString();
        bookCategoryFamily = in.readInt();
        bookVersionName = in.readString();
        bookCoverS = in.readString();
        bookCoverL = in.readString();
    }

    public static final Creator<BookInfo> CREATOR = new Creator<BookInfo>() {
        @Override
        public BookInfo createFromParcel(Parcel in) {
            return new BookInfo(in);
        }

        @Override
        public BookInfo[] newArray(int size) {
            return new BookInfo[size];
        }
    };

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookAwards() {
        return bookAwards;
    }

    public void setBookAwards(String bookAwards) {
        this.bookAwards = bookAwards;
    }

    public String getBookISBN() {
        return bookISBN;
    }

    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }

    public String getBookSupplier() {
        return bookSupplier;
    }

    public void setBookSupplier(String bookSupplier) {
        this.bookSupplier = bookSupplier;
    }

    public String getBookSubtitle() {
        return bookSubtitle;
    }

    public void setBookSubtitle(String bookSubtitle) {
        this.bookSubtitle = bookSubtitle;
    }

    public String getBookCreateTime() {
        return bookCreateTime;
    }

    public void setBookCreateTime(String bookCreateTime) {
        this.bookCreateTime = bookCreateTime;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookSummary() {
        return bookSummary;
    }

    public void setBookSummary(String bookSummary) {
        this.bookSummary = bookSummary;
    }

    public String getBookPublisher() {
        return bookPublisher;
    }

    public void setBookPublisher(String bookPublisher) {
        this.bookPublisher = bookPublisher;
    }

    public String getBookSalePrice() {
        return bookSalePrice;
    }

    public void setBookSalePrice(String bookSalePrice) {
        this.bookSalePrice = bookSalePrice;
    }

    public String getBookVol() {
        return bookVol;
    }

    public void setBookVol(String bookVol) {
        this.bookVol = bookVol;
    }

    public String getBookPreview() {
        return bookPreview;
    }

    public void setBookPreview(String bookPreview) {
        this.bookPreview = bookPreview;
    }

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public String getBookCategory() {
        return bookCategory;
    }

    public void setBookCategory(String bookCategory) {
        this.bookCategory = bookCategory;
    }

    public String getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(String bookStatus) {
        this.bookStatus = bookStatus;
    }

    public String getBookVersion() {
        return bookVersion;
    }

    public void setBookVersion(String bookVersion) {
        this.bookVersion = bookVersion;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookDownload() {
        return bookDownload;
    }

    public void setBookDownload(String bookDownload) {
        this.bookDownload = bookDownload;
    }

    public String getBookPublishTime() {
        return bookPublishTime;
    }

    public void setBookPublishTime(String bookPublishTime) {
        this.bookPublishTime = bookPublishTime;
    }

    public String getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(String bookPrice) {
        this.bookPrice = bookPrice;
    }

    public String getBookCreator() {
        return bookCreator;
    }

    public void setBookCreator(String bookCreator) {
        this.bookCreator = bookCreator;
    }

    public String getBookCategoryFamilyName() {
        return bookCategoryFamilyName;
    }

    public void setBookCategoryFamilyName(String bookCategoryFamilyName) {
        this.bookCategoryFamilyName = bookCategoryFamilyName;
    }

    public String getBookKeyWord() {
        return bookKeyWord;
    }

    public void setBookKeyWord(String bookKeyWord) {
        this.bookKeyWord = bookKeyWord;
    }

    public String getBookCategoryName() {
        return bookCategoryName;
    }

    public void setBookCategoryName(String bookCategoryName) {
        this.bookCategoryName = bookCategoryName;
    }

    public String getBookStatusCode() {
        return bookStatusCode;
    }

    public void setBookStatusCode(String bookStatusCode) {
        this.bookStatusCode = bookStatusCode;
    }

    public String getBookPublisherName() {
        return bookPublisherName;
    }

    public void setBookPublisherName(String bookPublisherName) {
        this.bookPublisherName = bookPublisherName;
    }

    public int getBookCategoryFamily() {
        return bookCategoryFamily;
    }

    public void setBookCategoryFamily(int bookCategoryFamily) {
        this.bookCategoryFamily = bookCategoryFamily;
    }

    public String getBookVersionName() {
        return bookVersionName;
    }

    public void setBookVersionName(String bookVersionName) {
        this.bookVersionName = bookVersionName;
    }

    public String getBookCoverS() {
        return bookCoverS;
    }

    public void setBookCoverS(String bookCoverS) {
        this.bookCoverS = bookCoverS;
    }

    public String getBookCoverL() {
        return bookCoverL;
    }

    public void setBookCoverL(String bookCoverL) {
        this.bookCoverL = bookCoverL;
    }

    @Override
    public String toString() {
        return "BookInfo{" +
                "bookTitle='" + bookTitle + '\'' +
                ", bookAuthor='" + bookAuthor + '\'' +
                ", bookCategoryFamilyName='" + bookCategoryFamilyName + '\'' +
                ", bookCoverS='" + bookCoverS + '\'' +
                ", bookCoverL='" + bookCoverL + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bookId);
        dest.writeString(this.bookAwards);
        dest.writeString(this.bookISBN);
        dest.writeString(this.bookSupplier);
        dest.writeString(this.bookCreateTime);
        dest.writeString(this.bookSubtitle);
        dest.writeString(this.bookTitle);
        dest.writeString(this.bookSummary);
        dest.writeString(this.bookPublisher);
        dest.writeString(this.bookSalePrice);
        dest.writeString(this.bookPreview);
        dest.writeString(this.bookCover);
        dest.writeString(this.bookCategory);
        dest.writeString(this.bookStatus);
        dest.writeString(this.bookVersion);
        dest.writeString(this.bookAuthor);
        dest.writeString(this.bookDownload);
        dest.writeString(this.bookPublishTime);
        dest.writeString(this.bookPrice);
        dest.writeString(this.bookCreator);
        dest.writeString(this.bookKeyWord);
        dest.writeString(this.bookCategoryFamilyName);
        dest.writeString(this.bookCategoryName);
        dest.writeString(this.bookStatusCode);
        dest.writeString(this.bookPublisherName);
        dest.writeInt(this.bookCategoryFamily);
        dest.writeString(this.bookVersionName);
        dest.writeString(this.bookCoverS);
        dest.writeString(this.bookCoverL);
    }
}
