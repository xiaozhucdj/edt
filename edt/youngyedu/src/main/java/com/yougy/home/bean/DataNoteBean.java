package com.yougy.home.bean;


import java.util.List;

/**
 * Created by Administrator on 2017/1/4.
 * JSON 解析笔记
 */

public class DataNoteBean {
    private int count;
    private List<NoteInfo> noteList;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<NoteInfo> getNoteList() {
        return noteList;
    }

    public void setNoteList(List<NoteInfo> noteList) {
        this.noteList = noteList;
    }
}
