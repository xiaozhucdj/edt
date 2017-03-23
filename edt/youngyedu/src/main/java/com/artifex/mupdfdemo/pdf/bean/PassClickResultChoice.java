package com.artifex.mupdfdemo.pdf.bean;

/**
 * Created by Administrator on 2016/6/29.
 */
public class PassClickResultChoice extends PassClickResult{

    public final String [] options;
    public final String [] selected;

    public PassClickResultChoice(boolean _changed, String [] _options, String [] _selected) {
        super(_changed);
        options = _options;
        selected = _selected;
    }
    public void acceptVisitor(PassClickResultVisitor visitor) {
        visitor.visitChoice(this);
    }
}
