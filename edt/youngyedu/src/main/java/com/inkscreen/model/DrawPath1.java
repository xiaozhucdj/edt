package com.inkscreen.model;

import android.graphics.Paint;
import android.graphics.Path;

import java.io.Serializable;

/**
 * Created by xcz on 2016/12/20.
 */
public class DrawPath1 implements Serializable{

    public Path path;// 路径  

    public Paint paint;// 画笔  

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}
