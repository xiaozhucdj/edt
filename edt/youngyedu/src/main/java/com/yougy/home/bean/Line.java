package com.yougy.home.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangliang on 2016/10/27.
 */

public class Line implements Serializable{
    private static final long serialVersionUID = 1507480388948413724L;
    private float width;

    private Point start;

    private List<Point> points = new ArrayList<>();

    private Point end;

    private int color = 0xff000000;

    private int type = 0;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Line{" +
                "width=" + width +
                ", start=" + start +
                ", points=" + points +
                ", end=" + end +
                '}';
    }
}
