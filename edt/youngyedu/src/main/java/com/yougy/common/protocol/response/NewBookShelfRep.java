package com.yougy.common.protocol.response;

import com.yougy.init.bean.BookInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/5/16.
 * 书架查询
 */

public class NewBookShelfRep extends  NewBaseRep {
    private List<BookInfo> data ;
    public List<BookInfo> getData() {
        return data;
    }
}
