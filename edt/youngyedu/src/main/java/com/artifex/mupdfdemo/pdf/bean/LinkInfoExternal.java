package com.artifex.mupdfdemo.pdf.bean;

/**
 * Created by Administrator on 2016/6/29.
 */
public class LinkInfoExternal extends LinkInfo {
    final public String url;

    public LinkInfoExternal(float l, float t, float r, float b, String u) {
        super(l, t, r, b);
        url = u;
    }

    public void acceptVisitor(LinkInfoVisitor visitor) {
        visitor.visitExternal(this);
    }
}
