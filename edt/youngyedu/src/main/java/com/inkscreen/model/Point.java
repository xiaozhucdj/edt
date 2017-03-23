package com.inkscreen.model;

import java.io.Serializable;

/**
 * Created by xcz on 2016/12/20.
 */
public class Point implements Serializable{
    public float x;

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float y;

}
