package com.yougy.shop;

import com.yougy.common.model.BaseData;

/**
 * Created by FH on 2017/2/17.
 */

public class QueryQRStrObj extends BaseData {
    int payMethod;
    String qrcode;

    public int getPayMethod() {
        return payMethod;
    }

    public QueryQRStrObj setPayMethod(int payMethod) {
        this.payMethod = payMethod;
        return this;
    }

    public String getQrcode() {
        return qrcode;
    }

    public QueryQRStrObj setQrcode(String qrcode) {
        this.qrcode = qrcode;
        return this;
    }
}
