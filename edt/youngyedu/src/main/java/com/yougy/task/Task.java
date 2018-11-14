package com.yougy.task;

public class Task {

    private String taskName;

    private String chapter;

    private String type;

    private String groupName;

    private int dataCount;

    private int exerciseCount;

    private boolean needSignature;

    private String completeTime;

    private boolean isComplete;


    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getDataCount() {
        return dataCount;
    }

    public void setDataCount(int dataCount) {
        this.dataCount = dataCount;
    }

    public int getExerciseCount() {
        return exerciseCount;
    }

    public void setExerciseCount(int exerciseCount) {
        this.exerciseCount = exerciseCount;
    }

    public boolean isNeedSignature() {
        return needSignature;
    }

    public void setNeedSignature(boolean needSignature) {
        this.needSignature = needSignature;
    }

    public String getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }
}
