package com.yougy.shop.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.yougy.common.model.*;
import com.yougy.message.SizeUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by FH on 2017/6/1.
 */

public class CartItem extends com.yougy.common.model.BookInfo implements Parcelable{
    public List<Coupon> bookCoupon;
    private double bookSpotPrice;

    public double getBookSpotPrice() {
        return SizeUtil.doScale_double(bookSpotPrice , 2 , BigDecimal.ROUND_UP);
    }

    public void setBookSpotPrice(double bookSpotPrice) {
        this.bookSpotPrice = bookSpotPrice;
    }

    public CartItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.bookCoupon);
        dest.writeDouble(this.bookSpotPrice);
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

    protected CartItem(Parcel in) {
        super(in);
        this.bookCoupon = in.createTypedArrayList(Coupon.CREATOR);
        this.bookSpotPrice = in.readDouble();
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

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel source) {
            return new CartItem(source);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };
}
