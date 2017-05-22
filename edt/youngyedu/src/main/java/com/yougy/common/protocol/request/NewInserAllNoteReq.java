package com.yougy.common.protocol.request;

import com.yougy.home.bean.NoteInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/5/22.
 * 添加全部笔记
 */

public class NewInserAllNoteReq extends NewBaseReq {

    public NewInserAllNoteReq() {
        m = "inserNote";
        address = "classRoom";
    }

    private int userId ;

    private List<NoteInfo> data ;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setData(List<NoteInfo> data) {
        this.data = data;
    }
}
