package com.yougy.common.protocol.response;

import com.yougy.home.bean.NoteInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/5/16.
 * 笔记查询
 */

public class NewQueryNoteRep extends NewBaseRep {
    private List<NoteInfo> data ;
    public List<NoteInfo> getData() {
        return data;
    }
}
