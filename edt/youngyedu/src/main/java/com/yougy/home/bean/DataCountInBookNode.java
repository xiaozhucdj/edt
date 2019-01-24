package com.yougy.home.bean;

import java.util.List;

public class DataCountInBookNode {
    /**
     * nodeId : 1
     * count : 0
     * nodeName : 第一单元
     */

    private int nodeId;
    private int count;
    private String nodeName;
    private List<Integer> exams;

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public List<Integer> getExams() {
        return exams;
    }

    public DataCountInBookNode setExams(List<Integer> exams) {
        this.exams = exams;
        return this;
    }
}
