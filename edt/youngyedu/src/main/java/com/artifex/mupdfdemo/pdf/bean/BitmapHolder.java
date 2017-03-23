package com.artifex.mupdfdemo.pdf.bean;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2016/6/29.
 */
public class BitmapHolder {

    private Bitmap bm;

    public BitmapHolder() {
        bm = null;
    }

    public synchronized void setBm(Bitmap abm) {
        if (bm != null && bm != abm)
            bm.recycle();
        bm = abm;
    }

    public synchronized void drop() {
        bm = null;
    }

    public synchronized Bitmap getBm() {
        return bm;
    }

}
