package com.artifex.mupdfdemo.pdf.bean;

/**
 * Created by Administrator on 2016/6/29.
 */
public class PassClickResult {
    public final boolean changed;

    public PassClickResult(boolean _changed) {
        changed = _changed;
    }

    public void acceptVisitor(PassClickResultVisitor visitor) {
    }
}
