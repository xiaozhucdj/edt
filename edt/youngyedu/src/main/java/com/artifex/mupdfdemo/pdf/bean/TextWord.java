package com.artifex.mupdfdemo.pdf.bean;

import android.graphics.RectF;

/**
 * Created by Administrator on 2016/6/29.
 */
public class TextWord extends RectF {
    public String w;

    public TextWord() {
        super();
        w = new String();
    }

    public void Add(TextChar tc) {
        super.union(tc);
        w = w.concat(new String(new char[]{tc.c}));
    }

}
