package com.artifex.mupdfdemo.pdf.bean;

/**
 * Created by Administrator on 2016/6/29.
 */
public abstract class PassClickResultVisitor {
    public abstract void visitText(PassClickResultText result);
    public abstract void visitChoice(PassClickResultChoice result);
}
