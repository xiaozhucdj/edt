package com.yougy.common.protocol.response;

import com.yougy.shop.bean.BaseData;

/**
 * Created by FH on 2017/2/17.
 * 获取生成支付二维码的str的response
 */

public class QueryQRStrProtocol extends BaseData{
    String qrStr;

    public String getQrStr() {
        return qrStr;
    }

    public void setQrStr(String qrStr) {
        this.qrStr = qrStr;
    }
}
