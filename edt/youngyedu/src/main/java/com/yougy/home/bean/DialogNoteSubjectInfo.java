package com.yougy.home.bean;

/**
 * Created by Administrator on 2016/11/17.
 * 创建笔记的学科
 */

public class DialogNoteSubjectInfo {

    private  String subject ;
    private  boolean  isSelect  = false;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
