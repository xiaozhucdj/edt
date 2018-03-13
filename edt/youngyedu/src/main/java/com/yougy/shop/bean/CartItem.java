package com.yougy.shop.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FH on 2017/6/1.
 */

public class CartItem implements Parcelable {


    /**
     * userId : 1000001820
     * bookVersion : 301
     * userAge : null
     * bookISBN : 1234567890564
     * bookPublisherName : 北京师范大学出版社
     * bookCoupon : [{"couponCreateTime":"2017-11-23 16:15:33","couponContentExplain":"限时满减-个人 满30元减5元","couponContent":[{"cut":"5","over":"30"}],"couponName":"测试满减个人1","couponMemo":null,"couponType":"限时满减-个人","couponEndTime":"2018-04-07 23:55:18","couponRemain":null,"couponTotal":null,"couponTypeCode":"BO03","couponId":7,"couponCreator":1,"couponStartTime":"2017-11-23 16:15:18","couponBook":["7146309","7244707","7244559","7244706"],"couponTarget":null},{"couponCreateTime":"2018-03-13 15:00:13","couponContentExplain":"限时折扣 8.0折","couponContent":[{"off":"80"}],"couponName":"打折活动01","couponMemo":null,"couponType":"限时折扣","couponEndTime":"2018-03-31 15:00:59","couponRemain":null,"couponTotal":null,"couponTypeCode":"BO01","couponId":22,"couponCreator":1,"couponStartTime":"2018-03-13 15:00:00","couponBook":["7146309","7244707"],"couponTarget":null}]
     * bookStatus : 已上架
     * bookSummary : null
     * bookDownloads : null
     * bookSupplier : 1
     * bookPreview :
     * bookStatusCode : BA11
     * bookSalePrice : 12
     * bookVol : 上册
     * bookOriginalPrice : 12
     * bookCreateTime : 2017-11-06 11:36:10
     * bookVersionTime : 2017
     * bookSaleTime : null
     * bookCategoryName : 教材初一语文
     * bookCategory : 10701
     * userStatus : 启用
     * userRole :
     * bookAtch : [{"userId":1,"atchType":"封面(大)","atchTypeCode":"BM01","userStatus":"启用","atchCreateTime":"2017-11-06 11:36:10","atchEncryptMode":null,"atchId":3286,"atchSupportCode":"BN01","atchOriginPath":"C:/fakepath/Cover.jpg","bookId":7146309,"atchEncryptKey":null,"atchSize":8061,"atchBucket":"espo","userRealName":"root-update","userName":"root","atchFormat":"jpg","atchSupport":"默认","atchRemotePath":"leke/book/cover/bNHTkJPjew3NiShEF5EpDKmCA6CahzCp.jpg","userRole":""},{"userId":1,"atchType":"封面(小)","atchTypeCode":"BM02","userStatus":"启用","atchCreateTime":"2017-11-06 11:36:10","atchEncryptMode":null,"atchId":3287,"atchSupportCode":"BN01","atchOriginPath":"C:/fakepath/Cover.jpg","bookId":7146309,"atchEncryptKey":null,"atchSize":8061,"atchBucket":"espo","userRealName":"root-update","userName":"root","atchFormat":"jpg","atchSupport":"默认","atchRemotePath":"leke/book/cover/kZHjP4HFebYJAKbAeKFZs7hK6stDjDKd.jpg","userRole":""},{"userId":1,"atchType":"封面(大)","atchTypeCode":"BM01","userStatus":"启用","atchCreateTime":"2017-11-06 11:36:10","atchEncryptMode":null,"atchId":3289,"atchSupportCode":"BN02","atchOriginPath":"C:/fakepath/Cover.jpg","bookId":7146309,"atchEncryptKey":null,"atchSize":8061,"atchBucket":"espo","userRealName":"root-update","userName":"root","atchFormat":"jpg","atchSupport":"N96","atchRemotePath":"leke/book/cover/s86KzKGaRPRj7csSzaFrZAHbmJtsQmwS.jpg","userRole":""},{"userId":1,"atchType":"封面(小)","atchTypeCode":"BM02","userStatus":"启用","atchCreateTime":"2017-11-06 11:36:10","atchEncryptMode":null,"atchId":3290,"atchSupportCode":"BN02","atchOriginPath":"C:/fakepath/Cover.jpg","bookId":7146309,"atchEncryptKey":null,"atchSize":8061,"atchBucket":"espo","userRealName":"root-update","userName":"root","atchFormat":"jpg","atchSupport":"N96","atchRemotePath":"leke/book/cover/Ps7rXyJstbBDki25wQSb4dJsTacEM2p5.jpg","userRole":""}]
     * bookSubtitle : null
     * bookAwards :
     * bookPublisher : 3
     * bookVersionName : 北师大版
     * bookModifyTime : 2017-11-06 11:37:32
     * bookKeyWord : null
     * bookPublishTime : null
     * bookCategoryFamily : 10000
     * bookTitle : 初一语文上-北师大版
     * bookCategoryFamilyName : 教材
     * bookCreator : 1
     * cartId : 23
     * bookId : 7146309
     * bookContents : {"nodes":[{"id":1,"name":"第一单元","nodes":[{"name":"1*在山的那边","id":2,"level":2},{"name":"2 走一步，再走一步","id":3,"level":2},{"id":4,"name":"3*短文两篇","nodes":[{"name":"蝉","id":5,"level":3},{"name":"贝壳","id":6,"level":3}],"level":2},{"name":"4 紫藤萝瀑布","id":7,"level":2},{"name":"5 童趣","id":8,"level":2}],"level":1},{"id":9,"name":"第二单元","nodes":[{"name":"6 理想","id":10,"level":2},{"id":11,"name":"7* 短文两篇","nodes":[{"name":"行道树","id":12,"level":3},{"name":"第一次真好","id":13,"level":3}],"level":2},{"name":"8*人生寓言","id":14,"level":2},{"name":"9*我的信念","id":15,"level":2},{"name":"10 《论语》十则","id":16,"level":2}],"level":1},{"id":17,"name":"第三单元","nodes":[{"name":"11 春","id":18,"level":2},{"name":"12 济南的冬天","id":19,"level":2},{"name":"13*夏感","id":20,"level":2},{"name":"14*秋天","id":21,"level":2},{"id":22,"name":"15 古代诗歌四首","nodes":[{"name":"观沧海","id":23,"level":3},{"name":"次北固山下","id":24,"level":3},{"name":"钱塘湖春行","id":25,"level":3},{"name":"天净沙 秋思","id":26,"level":3}],"level":2}],"level":1},{"id":27,"name":"第四单元","nodes":[{"name":"16 化石吟","id":28,"level":2},{"name":"17 看云识天气","id":29,"level":2},{"name":"18*绿色蝈蝈","id":30,"level":2},{"name":"19*月亮上的足迹","id":31,"level":2},{"name":"20*山市","id":32,"level":2}],"level":1},{"id":33,"name":"第五单元","nodes":[{"name":"21 风筝","id":34,"level":2},{"name":"22 羚羊木雕","id":35,"level":2},{"name":"23*散步","id":36,"level":2}],"level":1}],"version":0.1}
     * bookCoverS : http://espo.oss-cn-shanghai.aliyuncs.com/leke/book/cover/kZHjP4HFebYJAKbAeKFZs7hK6stDjDKd.jpg
     * bookCoverL : http://espo.oss-cn-shanghai.aliyuncs.com/leke/book/cover/bNHTkJPjew3NiShEF5EpDKmCA6CahzCp.jpg
     * userRealName : 李云峰
     * userName : 1000001820
     * bookDiscount : 100
     * bookCount : 1
     * bookAuthor : 不详
     * userGender : 男
     */

    private int userId;
    private int bookVersion;
    private String userAge;
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
    private String bookCategoryName;
    private int bookCategory;
    private String userStatus;
    private String userRole;
    private String bookSubtitle;
    private String bookAwards;
    private int bookPublisher;
    private String bookVersionName;
    private String bookModifyTime;
    private String bookKeyWord;
    private String bookPublishTime;
    private int bookCategoryFamily;
    private String bookTitle;
    private String bookCategoryFamilyName;
    private int bookCreator;
    private int cartId;
    private int bookId;
    private BookContentsBean bookContents;
    private String bookCoverS;
    private String bookCoverL;
    private String userRealName;
    private String userName;
    private int bookDiscount;
    private int bookCount;
    private String bookAuthor;
    private String userGender;
    private List<BookCouponBean> bookCoupon;
    private List<BookAtchBean> bookAtch;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookVersion() {
        return bookVersion;
    }

    public void setBookVersion(int bookVersion) {
        this.bookVersion = bookVersion;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
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

    public String getBookCategoryName() {
        return bookCategoryName;
    }

    public void setBookCategoryName(String bookCategoryName) {
        this.bookCategoryName = bookCategoryName;
    }

    public int getBookCategory() {
        return bookCategory;
    }

    public void setBookCategory(int bookCategory) {
        this.bookCategory = bookCategory;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
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

    public int getBookPublisher() {
        return bookPublisher;
    }

    public void setBookPublisher(int bookPublisher) {
        this.bookPublisher = bookPublisher;
    }

    public String getBookVersionName() {
        return bookVersionName;
    }

    public void setBookVersionName(String bookVersionName) {
        this.bookVersionName = bookVersionName;
    }

    public String getBookModifyTime() {
        return bookModifyTime;
    }

    public void setBookModifyTime(String bookModifyTime) {
        this.bookModifyTime = bookModifyTime;
    }

    public String getBookKeyWord() {
        return bookKeyWord;
    }

    public void setBookKeyWord(String bookKeyWord) {
        this.bookKeyWord = bookKeyWord;
    }

    public String getBookPublishTime() {
        return bookPublishTime;
    }

    public void setBookPublishTime(String bookPublishTime) {
        this.bookPublishTime = bookPublishTime;
    }

    public int getBookCategoryFamily() {
        return bookCategoryFamily;
    }

    public void setBookCategoryFamily(int bookCategoryFamily) {
        this.bookCategoryFamily = bookCategoryFamily;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookCategoryFamilyName() {
        return bookCategoryFamilyName;
    }

    public void setBookCategoryFamilyName(String bookCategoryFamilyName) {
        this.bookCategoryFamilyName = bookCategoryFamilyName;
    }

    public int getBookCreator() {
        return bookCreator;
    }

    public void setBookCreator(int bookCreator) {
        this.bookCreator = bookCreator;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public BookContentsBean getBookContents() {
        return bookContents;
    }

    public void setBookContents(BookContentsBean bookContents) {
        this.bookContents = bookContents;
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

    public int getBookDiscount() {
        return bookDiscount;
    }

    public void setBookDiscount(int bookDiscount) {
        this.bookDiscount = bookDiscount;
    }

    public int getBookCount() {
        return bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
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

    public static class BookContentsBean implements Parcelable {

        /**
         * nodes : [{"id":1,"name":"第一单元","nodes":[{"name":"1*在山的那边","id":2,"level":2},{"name":"2 走一步，再走一步","id":3,"level":2},{"id":4,"name":"3*短文两篇","nodes":[{"name":"蝉","id":5,"level":3},{"name":"贝壳","id":6,"level":3}],"level":2},{"name":"4 紫藤萝瀑布","id":7,"level":2},{"name":"5 童趣","id":8,"level":2}],"level":1},{"id":9,"name":"第二单元","nodes":[{"name":"6 理想","id":10,"level":2},{"id":11,"name":"7* 短文两篇","nodes":[{"name":"行道树","id":12,"level":3},{"name":"第一次真好","id":13,"level":3}],"level":2},{"name":"8*人生寓言","id":14,"level":2},{"name":"9*我的信念","id":15,"level":2},{"name":"10 《论语》十则","id":16,"level":2}],"level":1},{"id":17,"name":"第三单元","nodes":[{"name":"11 春","id":18,"level":2},{"name":"12 济南的冬天","id":19,"level":2},{"name":"13*夏感","id":20,"level":2},{"name":"14*秋天","id":21,"level":2},{"id":22,"name":"15 古代诗歌四首","nodes":[{"name":"观沧海","id":23,"level":3},{"name":"次北固山下","id":24,"level":3},{"name":"钱塘湖春行","id":25,"level":3},{"name":"天净沙 秋思","id":26,"level":3}],"level":2}],"level":1},{"id":27,"name":"第四单元","nodes":[{"name":"16 化石吟","id":28,"level":2},{"name":"17 看云识天气","id":29,"level":2},{"name":"18*绿色蝈蝈","id":30,"level":2},{"name":"19*月亮上的足迹","id":31,"level":2},{"name":"20*山市","id":32,"level":2}],"level":1},{"id":33,"name":"第五单元","nodes":[{"name":"21 风筝","id":34,"level":2},{"name":"22 羚羊木雕","id":35,"level":2},{"name":"23*散步","id":36,"level":2}],"level":1}]
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
             * name : 第一单元
             * nodes : [{"name":"1*在山的那边","id":2,"level":2},{"name":"2 走一步，再走一步","id":3,"level":2},{"id":4,"name":"3*短文两篇","nodes":[{"name":"蝉","id":5,"level":3},{"name":"贝壳","id":6,"level":3}],"level":2},{"name":"4 紫藤萝瀑布","id":7,"level":2},{"name":"5 童趣","id":8,"level":2}]
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
            dest.writeTypedList(this.nodes);
        }

        public BookContentsBean() {
        }

        protected BookContentsBean(Parcel in) {
            this.version = in.readDouble();
            this.nodes = in.createTypedArrayList(NodesBean.CREATOR);
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
         * couponContent : [{"cut":"5","over":"30"}]
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
        private List<CouponContentBean> couponContent;
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

        public List<CouponContentBean> getCouponContent() {
            return couponContent;
        }

        public void setCouponContent(List<CouponContentBean> couponContent) {
            this.couponContent = couponContent;
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

            public CouponContentBean() {
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.couponCreateTime);
            dest.writeString(this.couponContentExplain);
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
            dest.writeTypedList(this.couponContent);
            dest.writeStringList(this.couponBook);
        }

        public BookCouponBean() {
        }

        protected BookCouponBean(Parcel in) {
            this.couponCreateTime = in.readString();
            this.couponContentExplain = in.readString();
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
            this.couponContent = in.createTypedArrayList(CouponContentBean.CREATOR);
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
         * atchCreateTime : 2017-11-06 11:36:10
         * atchEncryptMode : null
         * atchId : 3286
         * atchSupportCode : BN01
         * atchOriginPath : C:/fakepath/Cover.jpg
         * bookId : 7146309
         * atchEncryptKey : null
         * atchSize : 8061
         * atchBucket : espo
         * userRealName : root-update
         * userName : root
         * atchFormat : jpg
         * atchSupport : 默认
         * atchRemotePath : leke/book/cover/bNHTkJPjew3NiShEF5EpDKmCA6CahzCp.jpg
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

    public CartItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.userId);
        dest.writeInt(this.bookVersion);
        dest.writeString(this.userAge);
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
        dest.writeString(this.bookCategoryName);
        dest.writeInt(this.bookCategory);
        dest.writeString(this.userStatus);
        dest.writeString(this.userRole);
        dest.writeString(this.bookSubtitle);
        dest.writeString(this.bookAwards);
        dest.writeInt(this.bookPublisher);
        dest.writeString(this.bookVersionName);
        dest.writeString(this.bookModifyTime);
        dest.writeString(this.bookKeyWord);
        dest.writeString(this.bookPublishTime);
        dest.writeInt(this.bookCategoryFamily);
        dest.writeString(this.bookTitle);
        dest.writeString(this.bookCategoryFamilyName);
        dest.writeInt(this.bookCreator);
        dest.writeInt(this.cartId);
        dest.writeInt(this.bookId);
        dest.writeParcelable(this.bookContents, flags);
        dest.writeString(this.bookCoverS);
        dest.writeString(this.bookCoverL);
        dest.writeString(this.userRealName);
        dest.writeString(this.userName);
        dest.writeInt(this.bookDiscount);
        dest.writeInt(this.bookCount);
        dest.writeString(this.bookAuthor);
        dest.writeString(this.userGender);
        dest.writeTypedList(this.bookCoupon);
        dest.writeTypedList(this.bookAtch);
    }

    protected CartItem(Parcel in) {
        this.userId = in.readInt();
        this.bookVersion = in.readInt();
        this.userAge = in.readString();
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
        this.bookCategoryName = in.readString();
        this.bookCategory = in.readInt();
        this.userStatus = in.readString();
        this.userRole = in.readString();
        this.bookSubtitle = in.readString();
        this.bookAwards = in.readString();
        this.bookPublisher = in.readInt();
        this.bookVersionName = in.readString();
        this.bookModifyTime = in.readString();
        this.bookKeyWord = in.readString();
        this.bookPublishTime = in.readString();
        this.bookCategoryFamily = in.readInt();
        this.bookTitle = in.readString();
        this.bookCategoryFamilyName = in.readString();
        this.bookCreator = in.readInt();
        this.cartId = in.readInt();
        this.bookId = in.readInt();
        this.bookContents = in.readParcelable(BookContentsBean.class.getClassLoader());
        this.bookCoverS = in.readString();
        this.bookCoverL = in.readString();
        this.userRealName = in.readString();
        this.userName = in.readString();
        this.bookDiscount = in.readInt();
        this.bookCount = in.readInt();
        this.bookAuthor = in.readString();
        this.userGender = in.readString();
        this.bookCoupon = in.createTypedArrayList(BookCouponBean.CREATOR);
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
