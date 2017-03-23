package com.artifex.mupdfdemo.pdf.bean;

/**
 * Created by Administrator on 2016/6/29.
 */
public class LinkInfoRemote extends LinkInfo {
    final public String fileSpec;
    final public int pageNumber;
    final public boolean newWindow;

    public LinkInfoRemote(float l, float t, float r, float b, String f, int p, boolean n) {
        super(l, t, r, b);
        fileSpec = f;
        pageNumber = p;
        newWindow = n;
    }

    public void acceptVisitor(LinkInfoVisitor visitor) {
        visitor.visitRemote(this);
    }
}
