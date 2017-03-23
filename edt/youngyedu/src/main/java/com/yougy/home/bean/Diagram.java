package com.yougy.home.bean;


import org.litepal.crud.DataSupport;

/**
 * Created by jiangliang on 2016/8/17.
 */
public class Diagram extends DataSupport {

    private int id;
    private int leftMargin;
    private int topMargin;
    private String diagramPath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLeftMargin() {
        return leftMargin;
    }

    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    }

    public int getTopMargin() {
        return topMargin;
    }

    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
    }

    public String getDiagramPath() {
        return diagramPath;
    }

    public void setDiagramPath(String diagramPath) {
        this.diagramPath = diagramPath;
    }

    @Override
    public String toString() {
        return "Diagram{" +
                "id=" + id +
                ", leftMargin=" + leftMargin +
                ", topMargin=" + topMargin +
                ", diagramPath='" + diagramPath + '\'' +
                '}';
    }
}
