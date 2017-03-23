package com.artifex.mupdfdemo.pdf.bean;

/**
 * Created by Administrator on 2016/6/29.
 */
public class LinkInfoInternal extends LinkInfo {
    final public int pageNumber;

    public LinkInfoInternal(float l, float t, float r, float b, int p) {
        super(l, t, r, b);
        pageNumber = p;
    }

    public void acceptVisitor(LinkInfoVisitor visitor) {
        visitor.visitInternal(this);
    }
}
