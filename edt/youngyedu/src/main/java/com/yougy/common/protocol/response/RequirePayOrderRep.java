package com.yougy.common.protocol.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.yougy.init.bean.BookInfo;
import com.yougy.shop.bean.BaseData;
import com.yougy.shop.bean.BriefOrder;

import java.util.List;

/**
 * Created by FH on 2017/2/16.
 * 下单结果
 */

public class RequirePayOrderRep extends BaseData {
    List<BriefOrder> data;

    public List<BriefOrder> getData() {
        return data;
    }

    public RequirePayOrderRep setData(List<BriefOrder> data) {
        this.data = data;
        return this;
    }

}
