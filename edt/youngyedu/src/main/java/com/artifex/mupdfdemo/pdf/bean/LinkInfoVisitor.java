package com.artifex.mupdfdemo.pdf.bean;

/**
 * Created by Administrator on 2016/6/29.
 */
abstract public class LinkInfoVisitor {
    /**内部链接*/
    public abstract void visitInternal(LinkInfoInternal li);
    /**外部链接*/
    public abstract void visitExternal(LinkInfoExternal li);
    /**什么的远程链接*/
    public abstract void visitRemote(LinkInfoRemote li);
}
