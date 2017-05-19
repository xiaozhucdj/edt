package com.yougy.common.protocol.response;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/5/15.
 */

public class NewBaseRep   extends DataSupport {
    protected int  code ;
    protected String msg ;
    protected int count;
    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    public int getCount() {
        return count;
    }
}
