package com.yougy.shop.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.yougy.message.SizeUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FH on 2017/3/20.
 */

public class BookInfo implements Parcelable{

    private String bookAuthor;
    private int bookVersion;
    private String bookISBN;
    private String bookPublisherName;
    private String bookStatus;
    private String bookSummary;
    private String bookDownloads;
    private int bookSupplier;
    private String bookPreview;
    private String bookStatusCode;
    private double bookSalePrice;
    private String bookVol;
    private double bookOriginalPrice;
    private String bookCreateTime;
    private int bookVersionTime;
    private String bookSaleTime;
    private String bookSubtitle;
    private String bookAwards;
    private String bookPublishTime;
    private String bookCoverL;
    private String bookCoverS;
    private BookContentsBean bookContents;
    private int bookCreator;
    private String bookCategoryFamilyName;
    private String bookTitle;
    private int bookDiscount;
    private int bookCategoryFamily;
    private int bookId;
    private String bookKeyWord;
    private String bookModifyTime;
    private String bookVersionName;
    private int bookPublisher;
    private int bookCategory;
    private String bookCategoryName;
    private List<BookCouponBean> bookCoupon;
    private List<BookAtchBean> bookAtch;
    private boolean bookInShelf;
    private boolean bookInCart;
    private boolean bookInFavor;
    private int bookFitNoteId;
    private int noteStyle;
    private int bookFitSubjectId;
    private String bookFitSubjectName;
    private int bookFitHomeworkId;
    private String bookFitNoteTitle;
    private long bookDownloadSize;
    private double bookSpotPrice;



    public double getBookSpotPrice() {
        return SizeUtil.doScale_double(bookSpotPrice , 2 , BigDecimal.ROUND_UP);
    }

    public void setBookSpotPrice(double bookSpotPrice) {
        this.bookSpotPrice = bookSpotPrice;
    }

    public long getBookDownloadSize() {
        return bookDownloadSize;
    }

    public void setBookDownloadSize(long bookDownloadSize) {
        this.bookDownloadSize = bookDownloadSize;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public int getBookVersion() {
        return bookVersion;
    }

    public void setBookVersion(int bookVersion) {
        this.bookVersion = bookVersion;
    }

    public String getBookISBN() {
        return bookISBN;
    }

    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }

    public String getBookPublisherName() {
        return bookPublisherName;
    }

    public void setBookPublisherName(String bookPublisherName) {
        this.bookPublisherName = bookPublisherName;
    }

    public String getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(String bookStatus) {
        this.bookStatus = bookStatus;
    }

    public String getBookSummary() {
        return bookSummary;
    }

    public void setBookSummary(String bookSummary) {
        this.bookSummary = bookSummary;
    }

    public String getBookDownloads() {
        return bookDownloads;
    }

    public void setBookDownloads(String bookDownloads) {
        this.bookDownloads = bookDownloads;
    }

    public int getBookSupplier() {
        return bookSupplier;
    }

    public void setBookSupplier(int bookSupplier) {
        this.bookSupplier = bookSupplier;
    }

    public String getBookPreview() {
        return bookPreview;
    }

    public void setBookPreview(String bookPreview) {
        this.bookPreview = bookPreview;
    }

    public String getBookStatusCode() {
        return bookStatusCode;
    }

    public void setBookStatusCode(String bookStatusCode) {
        this.bookStatusCode = bookStatusCode;
    }

    public double getBookSalePrice() {
        return bookSalePrice;
    }

    public void setBookSalePrice(double bookSalePrice) {
        this.bookSalePrice = bookSalePrice;
    }

    public String getBookVol() {
        return bookVol;
    }

    public void setBookVol(String bookVol) {
        this.bookVol = bookVol;
    }

    public double getBookOriginalPrice() {
        return bookOriginalPrice;
    }

    public void setBookOriginalPrice(double bookOriginalPrice) {
        this.bookOriginalPrice = bookOriginalPrice;
    }

    public String getBookCreateTime() {
        return bookCreateTime;
    }

    public void setBookCreateTime(String bookCreateTime) {
        this.bookCreateTime = bookCreateTime;
    }

    public int getBookVersionTime() {
        return bookVersionTime;
    }

    public void setBookVersionTime(int bookVersionTime) {
        this.bookVersionTime = bookVersionTime;
    }

    public String getBookSaleTime() {
        return bookSaleTime;
    }

    public void setBookSaleTime(String bookSaleTime) {
        this.bookSaleTime = bookSaleTime;
    }

    public String getBookSubtitle() {
        return bookSubtitle;
    }

    public void setBookSubtitle(String bookSubtitle) {
        this.bookSubtitle = bookSubtitle;
    }

    public String getBookAwards() {
        return bookAwards;
    }

    public void setBookAwards(String bookAwards) {
        this.bookAwards = bookAwards;
    }

    public String getBookPublishTime() {
        return bookPublishTime;
    }

    public void setBookPublishTime(String bookPublishTime) {
        this.bookPublishTime = bookPublishTime;
    }

    public String getBookCoverL() {
        return bookCoverL;
    }

    public void setBookCoverL(String bookCoverL) {
        this.bookCoverL = bookCoverL;
    }

    public String getBookCoverS() {
        return bookCoverS;
    }

    public void setBookCoverS(String bookCoverS) {
        this.bookCoverS = bookCoverS;
    }

    public BookContentsBean getBookContents() {
        return bookContents;
    }

    public void setBookContents(BookContentsBean bookContents) {
        this.bookContents = bookContents;
    }

    public int getBookCreator() {
        return bookCreator;
    }

    public void setBookCreator(int bookCreator) {
        this.bookCreator = bookCreator;
    }

    public String getBookCategoryFamilyName() {
        return bookCategoryFamilyName;
    }

    public void setBookCategoryFamilyName(String bookCategoryFamilyName) {
        this.bookCategoryFamilyName = bookCategoryFamilyName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getBookDiscount() {
        return bookDiscount;
    }

    public void setBookDiscount(int bookDiscount) {
        this.bookDiscount = bookDiscount;
    }

    public int getBookCategoryFamily() {
        return bookCategoryFamily;
    }

    public void setBookCategoryFamily(int bookCategoryFamily) {
        this.bookCategoryFamily = bookCategoryFamily;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookKeyWord() {
        return bookKeyWord;
    }

    public void setBookKeyWord(String bookKeyWord) {
        this.bookKeyWord = bookKeyWord;
    }

    public String getBookModifyTime() {
        return bookModifyTime;
    }

    public void setBookModifyTime(String bookModifyTime) {
        this.bookModifyTime = bookModifyTime;
    }

    public String getBookVersionName() {
        return bookVersionName;
    }

    public void setBookVersionName(String bookVersionName) {
        this.bookVersionName = bookVersionName;
    }

    public int getBookPublisher() {
        return bookPublisher;
    }

    public void setBookPublisher(int bookPublisher) {
        this.bookPublisher = bookPublisher;
    }

    public int getBookCategory() {
        return bookCategory;
    }

    public void setBookCategory(int bookCategory) {
        this.bookCategory = bookCategory;
    }

    public boolean isBookInShelf() {
        return bookInShelf;
    }

    public void setBookInShelf(boolean bookInShelf) {
        this.bookInShelf = bookInShelf;
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

    public int getBookFitNoteId() {
        return bookFitNoteId;
    }

    public void setBookFitNoteId(int bookFitNoteId) {
        this.bookFitNoteId = bookFitNoteId;
    }

    public int getNoteStyle() {
        return noteStyle;
    }

    public void setNoteStyle(int noteStyle) {
        this.noteStyle = noteStyle;
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

    public int getBookFitHomeworkId() {
        return bookFitHomeworkId;
    }

    public void setBookFitHomeworkId(int bookFitHomeworkId) {
        this.bookFitHomeworkId = bookFitHomeworkId;
    }

    public String getBookFitNoteTitle() {
        return bookFitNoteTitle;
    }

    public void setBookFitNoteTitle(String bookFitNoteTitle) {
        this.bookFitNoteTitle = bookFitNoteTitle;
    }

    public String getBookCategoryName() {
        return bookCategoryName;
    }

    public void setBookCategoryName(String bookCategoryName) {
        this.bookCategoryName = bookCategoryName;
    }

    public List<BookCouponBean> getBookCoupon() {
        return bookCoupon;
    }

    public void setBookCoupon(List<BookCouponBean> bookCoupon) {
        this.bookCoupon = bookCoupon;
    }

    public List<BookAtchBean> getBookAtch() {
        return bookAtch;
    }

    public void setBookAtch(List<BookAtchBean> bookAtch) {
        this.bookAtch = bookAtch;
    }

    @Override
    public String toString() {
        return "BookInfo{" +
                "bookSalePrice=" + bookSalePrice +
                ", bookOriginalPrice=" + bookOriginalPrice +
                ", bookSpotPrice=" + bookSpotPrice +
                '}';
    }

    public static class BookContentsBean implements Parcelable {

        /**
         * nodes : [{"id":1,"name":"Get Ready A You","nodes":[{"name":"Part I","id":2,"level":2},{"name":"Part II","id":3,"level":2}],"level":1},{"id":4,"name":"Get Ready B Your Friends","nodes":[{"name":"Part I","id":5,"level":2},{"name":"Part II","id":6,"level":2}],"level":1},{"id":7,"name":"Get Ready C Your Family","nodes":[{"name":"Part I","id":8,"level":2},{"name":"Part II","id":9,"level":2}],"level":1},{"id":10,"name":"Get Ready D Your Classroom","nodes":[{"name":"Part I","id":11,"level":2},{"name":"Part II","id":12,"level":2}],"level":1},{"id":13,"name":"Get Ready E Your Room","nodes":[{"name":"Part I","id":14,"level":2},{"name":"Part II","id":15,"level":2}],"level":1},{"id":16,"name":"Unit 1 Family","nodes":[{"name":"Lesson 1 Photos of Us","id":17,"level":2},{"name":"Lesson 2 What Do They Look Like?","id":18,"level":2},{"name":"Lesson 3 Happy Birthday!","id":19,"level":2},{"name":"Communication Workshop","id":20,"level":2}],"level":1},{"id":21,"name":"Unit 2 School Life","nodes":[{"name":"Lesson 4 School Things","id":22,"level":2},{"name":"Lesson 5 Before Class","id":23,"level":2},{"name":"Lesson 6 A School Day","id":24,"level":2},{"name":"Communication Workshop","id":25,"level":2}],"level":1},{"id":26,"name":"Unit 3 Home","nodes":[{"name":"Lesson 7 Time to Tidy","id":27,"level":2},{"name":"Lesson 8 WhoseBall Is This?","id":28,"level":2},{"name":"Lesson 9 Near My Home","id":29,"level":2},{"name":"Communication Workshop","id":30,"level":2}],"level":1},{"id":31,"name":"Unit 4 Interests and Skills","nodes":[{"name":"Lesson 10 My Interests","id":32,"level":2},{"name":"Lesson 11 A Skills Survey","id":33,"level":2},{"name":"Lesson 12 China\u2019s Got Talent","id":34,"level":2},{"name":"Communication Workshop","id":35,"level":2}],"level":1}]
         * version : 0.1
         */

        private double version;
        private List<NodesBean> nodes;

        public double getVersion() {
            return version;
        }

        public void setVersion(double version) {
            this.version = version;
        }

        public List<NodesBean> getNodes() {
            return nodes;
        }

        public void setNodes(List<NodesBean> nodes) {
            this.nodes = nodes;
        }

        public static class NodesBean implements Parcelable {

            /**
             * id : 1
             * name : Get Ready A You
             * nodes : [{"name":"Part I","id":2,"level":2},{"name":"Part II","id":3,"level":2}]
             * level : 1
             */

            private int id;
            private String name;
            private int level;
            private List<NodesBean> nodes;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getLevel() {
                return level;
            }

            public void setLevel(int level) {
                this.level = level;
            }

            public List<NodesBean> getNodes() {
                return nodes;
            }

            public void setNodes(List<NodesBean> nodes) {
                this.nodes = nodes;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.id);
                dest.writeString(this.name);
                dest.writeInt(this.level);
                dest.writeList(this.nodes);
            }

            public NodesBean() {
            }

            protected NodesBean(Parcel in) {
                this.id = in.readInt();
                this.name = in.readString();
                this.level = in.readInt();
                this.nodes = new ArrayList<NodesBean>();
                in.readList(this.nodes, NodesBean.class.getClassLoader());
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

    public static class BookCouponBean implements Parcelable {
        /**
         * couponCreateTime : 2017-11-23 16:15:33
         * couponContentExplain : 限时满减-个人 满30元减5元
         * couponContent : {"cut":"5","over":"30"}
         * couponName : 测试满减个人1
         * couponMemo : null
         * couponType : 限时满减-个人
         * couponEndTime : 2018-04-07 23:55:18
         * couponRemain : null
         * couponTotal : null
         * couponTypeCode : BO03
         * couponId : 7
         * couponCreator : 1
         * couponStartTime : 2017-11-23 16:15:18
         * couponBook : ["7146309","7244707","7244559","7244706"]
         * couponTarget : null
         */

        private String couponCreateTime;
        private String couponContentExplain;
        private List<CouponContentBean> couponContent;
        private String couponName;
        private String couponMemo;
        private String couponType;
        private String couponEndTime;
        private String couponRemain;
        private String couponTotal;
        private String couponTypeCode;
        private int couponId;
        private int couponCreator;
        private String couponStartTime;
        private String couponTarget;
        private List<String> couponBook;

        public String getCouponCreateTime() {
            return couponCreateTime;
        }

        public void setCouponCreateTime(String couponCreateTime) {
            this.couponCreateTime = couponCreateTime;
        }

        public String getCouponContentExplain() {
            return couponContentExplain;
        }

        public void setCouponContentExplain(String couponContentExplain) {
            this.couponContentExplain = couponContentExplain;
        }

        public List<CouponContentBean> getCouponContent() {
            return couponContent;
        }

        public void setCouponContent(List<CouponContentBean> couponContent) {
            this.couponContent = couponContent;
        }

        public String getCouponName() {
            return couponName;
        }

        public void setCouponName(String couponName) {
            this.couponName = couponName;
        }

        public String getCouponMemo() {
            return couponMemo;
        }

        public void setCouponMemo(String couponMemo) {
            this.couponMemo = couponMemo;
        }

        public String getCouponType() {
            return couponType;
        }

        public void setCouponType(String couponType) {
            this.couponType = couponType;
        }

        public String getCouponEndTime() {
            return couponEndTime;
        }

        public void setCouponEndTime(String couponEndTime) {
            this.couponEndTime = couponEndTime;
        }

        public String getCouponRemain() {
            return couponRemain;
        }

        public void setCouponRemain(String couponRemain) {
            this.couponRemain = couponRemain;
        }

        public String getCouponTotal() {
            return couponTotal;
        }

        public void setCouponTotal(String couponTotal) {
            this.couponTotal = couponTotal;
        }

        public String getCouponTypeCode() {
            return couponTypeCode;
        }

        public void setCouponTypeCode(String couponTypeCode) {
            this.couponTypeCode = couponTypeCode;
        }

        public int getCouponId() {
            return couponId;
        }

        public void setCouponId(int couponId) {
            this.couponId = couponId;
        }

        public int getCouponCreator() {
            return couponCreator;
        }

        public void setCouponCreator(int couponCreator) {
            this.couponCreator = couponCreator;
        }

        public String getCouponStartTime() {
            return couponStartTime;
        }

        public void setCouponStartTime(String couponStartTime) {
            this.couponStartTime = couponStartTime;
        }

        public String getCouponTarget() {
            return couponTarget;
        }

        public void setCouponTarget(String couponTarget) {
            this.couponTarget = couponTarget;
        }

        public List<String> getCouponBook() {
            return couponBook;
        }

        public void setCouponBook(List<String> couponBook) {
            this.couponBook = couponBook;
        }

        public static class CouponContentBean implements Parcelable {
            /**
             * cut : 5
             * over : 30
             */

            private String cut;
            private String over;
            private String free;
            private String off;

            public String getCut() {
                return cut;
            }

            public void setCut(String cut) {
                this.cut = cut;
            }

            public String getOver() {
                return over;
            }

            public void setOver(String over) {
                this.over = over;
            }

            public String getFree() {
                return free;
            }

            public void setFree(String free) {
                this.free = free;
            }

            public String getOff() {
                return off;
            }

            public void setOff(String off) {
                this.off = off;
            }

            public CouponContentBean() {
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.cut);
                dest.writeString(this.over);
                dest.writeString(this.free);
                dest.writeString(this.off);
            }

            protected CouponContentBean(Parcel in) {
                this.cut = in.readString();
                this.over = in.readString();
                this.free = in.readString();
                this.off = in.readString();
            }

            public static final Creator<CouponContentBean> CREATOR = new Creator<CouponContentBean>() {
                @Override
                public CouponContentBean createFromParcel(Parcel source) {
                    return new CouponContentBean(source);
                }

                @Override
                public CouponContentBean[] newArray(int size) {
                    return new CouponContentBean[size];
                }
            };
        }

        public BookCouponBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.couponCreateTime);
            dest.writeString(this.couponContentExplain);
            dest.writeTypedList(this.couponContent);
            dest.writeString(this.couponName);
            dest.writeString(this.couponMemo);
            dest.writeString(this.couponType);
            dest.writeString(this.couponEndTime);
            dest.writeString(this.couponRemain);
            dest.writeString(this.couponTotal);
            dest.writeString(this.couponTypeCode);
            dest.writeInt(this.couponId);
            dest.writeInt(this.couponCreator);
            dest.writeString(this.couponStartTime);
            dest.writeString(this.couponTarget);
            dest.writeStringList(this.couponBook);
        }

        protected BookCouponBean(Parcel in) {
            this.couponCreateTime = in.readString();
            this.couponContentExplain = in.readString();
            this.couponContent = in.createTypedArrayList(CouponContentBean.CREATOR);
            this.couponName = in.readString();
            this.couponMemo = in.readString();
            this.couponType = in.readString();
            this.couponEndTime = in.readString();
            this.couponRemain = in.readString();
            this.couponTotal = in.readString();
            this.couponTypeCode = in.readString();
            this.couponId = in.readInt();
            this.couponCreator = in.readInt();
            this.couponStartTime = in.readString();
            this.couponTarget = in.readString();
            this.couponBook = in.createStringArrayList();
        }

        public static final Creator<BookCouponBean> CREATOR = new Creator<BookCouponBean>() {
            @Override
            public BookCouponBean createFromParcel(Parcel source) {
                return new BookCouponBean(source);
            }

            @Override
            public BookCouponBean[] newArray(int size) {
                return new BookCouponBean[size];
            }
        };
    }

    public static class BookAtchBean implements Parcelable {
        /**
         * userId : 1
         * atchType : 封面(大)
         * atchTypeCode : BM01
         * userStatus : 启用
         * atchCreateTime : 2017-09-01 14:02:31
         * atchEncryptMode : null
         * atchId : 2082
         * atchSupportCode : BN01
         * atchOriginPath : C:/fakepath/Cover.jpg
         * bookId : 7244706
         * atchEncryptKey : null
         * atchSize : 5421
         * atchBucket : espo
         * userRealName : root-update
         * userName : root
         * atchFormat : jpg
         * atchSupport : 默认
         * atchRemotePath : leke/book/cover/YEMiMpse6xZyRexG8tPYCN7sdPtRmieK.jpg
         * userRole :
         */

        private int userId;
        private String atchType;
        private String atchTypeCode;
        private String userStatus;
        private String atchCreateTime;
        private String atchEncryptMode;
        private int atchId;
        private String atchSupportCode;
        private String atchOriginPath;
        private int bookId;
        private String atchEncryptKey;
        private int atchSize;
        private String atchBucket;
        private String userRealName;
        private String userName;
        private String atchFormat;
        private String atchSupport;
        private String atchRemotePath;
        private String userRole;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getAtchType() {
            return atchType;
        }

        public void setAtchType(String atchType) {
            this.atchType = atchType;
        }

        public String getAtchTypeCode() {
            return atchTypeCode;
        }

        public void setAtchTypeCode(String atchTypeCode) {
            this.atchTypeCode = atchTypeCode;
        }

        public String getUserStatus() {
            return userStatus;
        }

        public void setUserStatus(String userStatus) {
            this.userStatus = userStatus;
        }

        public String getAtchCreateTime() {
            return atchCreateTime;
        }

        public void setAtchCreateTime(String atchCreateTime) {
            this.atchCreateTime = atchCreateTime;
        }

        public String getAtchEncryptMode() {
            return atchEncryptMode;
        }

        public void setAtchEncryptMode(String atchEncryptMode) {
            this.atchEncryptMode = atchEncryptMode;
        }

        public int getAtchId() {
            return atchId;
        }

        public void setAtchId(int atchId) {
            this.atchId = atchId;
        }

        public String getAtchSupportCode() {
            return atchSupportCode;
        }

        public void setAtchSupportCode(String atchSupportCode) {
            this.atchSupportCode = atchSupportCode;
        }

        public String getAtchOriginPath() {
            return atchOriginPath;
        }

        public void setAtchOriginPath(String atchOriginPath) {
            this.atchOriginPath = atchOriginPath;
        }

        public int getBookId() {
            return bookId;
        }

        public void setBookId(int bookId) {
            this.bookId = bookId;
        }

        public String getAtchEncryptKey() {
            return atchEncryptKey;
        }

        public void setAtchEncryptKey(String atchEncryptKey) {
            this.atchEncryptKey = atchEncryptKey;
        }

        public int getAtchSize() {
            return atchSize;
        }

        public void setAtchSize(int atchSize) {
            this.atchSize = atchSize;
        }

        public String getAtchBucket() {
            return atchBucket;
        }

        public void setAtchBucket(String atchBucket) {
            this.atchBucket = atchBucket;
        }

        public String getUserRealName() {
            return userRealName;
        }

        public void setUserRealName(String userRealName) {
            this.userRealName = userRealName;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getAtchFormat() {
            return atchFormat;
        }

        public void setAtchFormat(String atchFormat) {
            this.atchFormat = atchFormat;
        }

        public String getAtchSupport() {
            return atchSupport;
        }

        public void setAtchSupport(String atchSupport) {
            this.atchSupport = atchSupport;
        }

        public String getAtchRemotePath() {
            return atchRemotePath;
        }

        public void setAtchRemotePath(String atchRemotePath) {
            this.atchRemotePath = atchRemotePath;
        }

        public String getUserRole() {
            return userRole;
        }

        public void setUserRole(String userRole) {
            this.userRole = userRole;
        }

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
        dest.writeString(this.bookStatus);
        dest.writeString(this.bookSummary);
        dest.writeString(this.bookDownloads);
        dest.writeInt(this.bookSupplier);
        dest.writeString(this.bookPreview);
        dest.writeString(this.bookStatusCode);
        dest.writeDouble(this.bookSalePrice);
        dest.writeString(this.bookVol);
        dest.writeDouble(this.bookOriginalPrice);
        dest.writeString(this.bookCreateTime);
        dest.writeInt(this.bookVersionTime);
        dest.writeString(this.bookSaleTime);
        dest.writeString(this.bookSubtitle);
        dest.writeString(this.bookAwards);
        dest.writeString(this.bookPublishTime);
        dest.writeString(this.bookCoverL);
        dest.writeString(this.bookCoverS);
        dest.writeParcelable(this.bookContents, flags);
        dest.writeInt(this.bookCreator);
        dest.writeString(this.bookCategoryFamilyName);
        dest.writeString(this.bookTitle);
        dest.writeInt(this.bookDiscount);
        dest.writeInt(this.bookCategoryFamily);
        dest.writeInt(this.bookId);
        dest.writeString(this.bookKeyWord);
        dest.writeString(this.bookModifyTime);
        dest.writeString(this.bookVersionName);
        dest.writeInt(this.bookPublisher);
        dest.writeInt(this.bookCategory);
        dest.writeString(this.bookCategoryName);
        dest.writeTypedList(this.bookCoupon);
        dest.writeTypedList(this.bookAtch);
        dest.writeByte(this.bookInShelf ? (byte) 1 : (byte) 0);
        dest.writeByte(this.bookInCart ? (byte) 1 : (byte) 0);
        dest.writeByte(this.bookInFavor ? (byte) 1 : (byte) 0);
        dest.writeInt(this.bookFitNoteId);
        dest.writeInt(this.noteStyle);
        dest.writeInt(this.bookFitSubjectId);
        dest.writeString(this.bookFitSubjectName);
        dest.writeInt(this.bookFitHomeworkId);
        dest.writeString(this.bookFitNoteTitle);
        dest.writeLong(this.bookDownloadSize);
        dest.writeDouble(this.bookSpotPrice);
    }

    protected BookInfo(Parcel in) {
        this.bookAuthor = in.readString();
        this.bookVersion = in.readInt();
        this.bookISBN = in.readString();
        this.bookPublisherName = in.readString();
        this.bookStatus = in.readString();
        this.bookSummary = in.readString();
        this.bookDownloads = in.readString();
        this.bookSupplier = in.readInt();
        this.bookPreview = in.readString();
        this.bookStatusCode = in.readString();
        this.bookSalePrice = in.readDouble();
        this.bookVol = in.readString();
        this.bookOriginalPrice = in.readDouble();
        this.bookCreateTime = in.readString();
        this.bookVersionTime = in.readInt();
        this.bookSaleTime = in.readString();
        this.bookSubtitle = in.readString();
        this.bookAwards = in.readString();
        this.bookPublishTime = in.readString();
        this.bookCoverL = in.readString();
        this.bookCoverS = in.readString();
        this.bookContents = in.readParcelable(BookContentsBean.class.getClassLoader());
        this.bookCreator = in.readInt();
        this.bookCategoryFamilyName = in.readString();
        this.bookTitle = in.readString();
        this.bookDiscount = in.readInt();
        this.bookCategoryFamily = in.readInt();
        this.bookId = in.readInt();
        this.bookKeyWord = in.readString();
        this.bookModifyTime = in.readString();
        this.bookVersionName = in.readString();
        this.bookPublisher = in.readInt();
        this.bookCategory = in.readInt();
        this.bookCategoryName = in.readString();
        this.bookCoupon = in.createTypedArrayList(BookCouponBean.CREATOR);
        this.bookAtch = in.createTypedArrayList(BookAtchBean.CREATOR);
        this.bookInShelf = in.readByte() != 0;
        this.bookInCart = in.readByte() != 0;
        this.bookInFavor = in.readByte() != 0;
        this.bookFitNoteId = in.readInt();
        this.noteStyle = in.readInt();
        this.bookFitSubjectId = in.readInt();
        this.bookFitSubjectName = in.readString();
        this.bookFitHomeworkId = in.readInt();
        this.bookFitNoteTitle = in.readString();
        this.bookDownloadSize = in.readLong();
        this.bookSpotPrice = in.readDouble();
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
