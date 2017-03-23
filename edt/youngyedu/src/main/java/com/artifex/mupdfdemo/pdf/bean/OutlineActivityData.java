package com.artifex.mupdfdemo.pdf.bean;

import com.artifex.mupdfdemo.OutlineItem;

/**
 * Created by Administrator on 2016/7/1.
 */
public class OutlineActivityData {
    public OutlineItem items[];
    public int         position;
    static private OutlineActivityData singleton;

    static public void set(OutlineActivityData d) {
        singleton = d;
    }

    static public OutlineActivityData get() {
        if (singleton == null)
            singleton = new OutlineActivityData();
        return singleton;
    }
}
