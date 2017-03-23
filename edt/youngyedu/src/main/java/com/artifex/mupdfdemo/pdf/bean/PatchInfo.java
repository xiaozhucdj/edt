package com.artifex.mupdfdemo.pdf.bean;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by Administrator on 2016/6/29.
 * 内部类 PatchInfo 作用是在bitmp 上绘制
 */
public class PatchInfo {
    public BitmapHolder bmh;
    public Bitmap bm;
    public Point patchViewSize;
    public Rect patchArea;
    public boolean completeRedraw;

    public PatchInfo(Point aPatchViewSize, Rect aPatchArea, BitmapHolder aBmh, boolean aCompleteRedraw) {
        bmh = aBmh;
        bm = null;
        patchViewSize = aPatchViewSize;
        patchArea = aPatchArea;
        completeRedraw = aCompleteRedraw;
    }
}