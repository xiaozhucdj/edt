package com.yougy.common.protocol.response;

import com.yougy.shop.bean.BaseData;

import java.util.List;

/**
 * Created by FH on 2017/2/17.
 * 获取生成支付二维码的str的response
 */

public class QueryQRStrRep extends BaseData{
    List<QrObj> data;

    public List<QrObj> getData() {
        return data;
    }

    public QueryQRStrRep setData(List<QrObj> data) {
        this.data = data;
        return this;
    }

    public static class QrObj{
        int payMethod;
        String qrcode;

        public String getQrcode() {
            return qrcode;
        }

        public QrObj setQrcode(String qrcode) {
            this.qrcode = qrcode;
            return this;
        }
    }
}
