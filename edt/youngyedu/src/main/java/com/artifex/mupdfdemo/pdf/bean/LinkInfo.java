package com.artifex.mupdfdemo.pdf.bean;

import android.graphics.RectF;

/**
 * Created by Administrator on 2016/6/29.
 */
public class LinkInfo {

    final public RectF rect;

    public LinkInfo(float l, float t, float r, float b) {
        rect = new RectF(l, t, r, b);
    }

    public void acceptVisitor(LinkInfoVisitor visitor) {
    }

}
