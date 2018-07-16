package com.yougy.shop.bean;

import java.util.List;

/**
 * Created by FH on 2018/3/23.
 */

public class OrderSummary {
    /**
     * orderAmount : 0
     * orderCreateTime : 2018-05-18 17:13:49
     * orderInvoice : null
     * orderDeduction : 0
     * orderCoupon : 0
     * orderOwner : 1000002099
     * orderId : 2018051800000005
     * orderType : 个人订单
     * orderStatusCode : BH03
     * orderMemo : null
     * orderStatus : 交易成功
     * orderPayer : null
     * orderInfo : [{"bookInfo":{"bookCoverL":"http://espo.oss-cn-shanghai.aliyuncs.com/leke/book/cover/hiGxcfPBhnmcQEseByDQSQFznX5746AS.png","bookTitle":"父与子","bookCoverS":"http://espo.oss-cn-shanghai.aliyuncs.com/leke/book/cover/abEhDxWWjyRhxmiZp2QjF58eTQ5C8Gz8.png"},"bookSalePrice":2.5,"orderId":"2018051800000005","bookCount":1,"bookId":304000001,"bookFinalPrice":0}]
     * orderTypeCode : BG01
     * orderParent : 0
     * orderReceiver : 1000002099
     */
    private double orderAmount;
    private double orderDeduction;
    private String orderCreateTime;
    private String orderId;
    private String orderStatusCode;
    private String orderStatus;
    private List<OrderInfoBean> orderInfo;

    public double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public double getOrderDeduction() {
        return orderDeduction;
    }

    public void setOrderDeduction(double orderDeduction) {
        this.orderDeduction = orderDeduction;
    }

    public String getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(String orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatusCode() {
        return orderStatusCode;
    }

    public void setOrderStatusCode(String orderStatusCode) {
        this.orderStatusCode = orderStatusCode;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<OrderInfoBean> getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(List<OrderInfoBean> orderInfo) {
        this.orderInfo = orderInfo;
    }

    public static class OrderInfoBean {
        /**
         * bookInfo : {"bookCoverL":"http://espo.oss-cn-shanghai.aliyuncs.com/leke/book/cover/hiGxcfPBhnmcQEseByDQSQFznX5746AS.png","bookTitle":"父与子","bookCoverS":"http://espo.oss-cn-shanghai.aliyuncs.com/leke/book/cover/abEhDxWWjyRhxmiZp2QjF58eTQ5C8Gz8.png"}
         * bookSalePrice : 2.5
         * orderId : 2018051800000005
         * bookCount : 1
         * bookId : 304000001
         * bookFinalPrice : 0
         */

        private BookInfoBean bookInfo;
        private double bookSalePrice;
        private double bookFinalPrice;
        private int bookCount;

        public int getBookCount() {
            return bookCount;
        }

        public void setBookCount(int bookCount) {
            this.bookCount = bookCount;
        }

        public BookInfoBean getBookInfo() {
            return bookInfo;
        }

        public void setBookInfo(BookInfoBean bookInfo) {
            this.bookInfo = bookInfo;
        }

        public double getBookSalePrice() {
            return bookSalePrice;
        }

        public void setBookSalePrice(double bookSalePrice) {
            this.bookSalePrice = bookSalePrice;
        }


        public double getBookFinalPrice() {
            return bookFinalPrice;
        }

        public void setBookFinalPrice(double bookFinalPrice) {
            this.bookFinalPrice = bookFinalPrice;
        }

        public static class BookInfoBean {
            /**
             * bookCoverL : http://espo.oss-cn-shanghai.aliyuncs.com/leke/book/cover/hiGxcfPBhnmcQEseByDQSQFznX5746AS.png
             * bookTitle : 父与子
             * bookCoverS : http://espo.oss-cn-shanghai.aliyuncs.com/leke/book/cover/abEhDxWWjyRhxmiZp2QjF58eTQ5C8Gz8.png
             */

            private String bookCoverL;
            private String bookTitle;
            private String bookCoverS;

            public String getBookCoverL() {
                return bookCoverL;
            }

            public void setBookCoverL(String bookCoverL) {
                this.bookCoverL = bookCoverL;
            }

            public String getBookTitle() {
                return bookTitle;
            }

            public void setBookTitle(String bookTitle) {
                this.bookTitle = bookTitle;
            }

            public String getBookCoverS() {
                return bookCoverS;
            }

            public void setBookCoverS(String bookCoverS) {
                this.bookCoverS = bookCoverS;
            }
        }
    }
}
