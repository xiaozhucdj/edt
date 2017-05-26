package com.yougy.common.protocol.request;

import com.yougy.home.bean.NoteInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/5/22.
 * 更新笔记
 */

public class NewUpdateNoteReq extends NewBaseReq {

    public NewUpdateNoteReq() {
        m = "updateNote";
        address = "classRoom";
    }

    private List<NoteInfo> data ;
    private int userId ;
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public void setData(List<NoteInfo> data) {
        this.data = data;
    }
}
