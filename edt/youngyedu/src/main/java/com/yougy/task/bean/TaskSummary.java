package com.yougy.task.bean;

import com.google.gson.annotations.SerializedName;
import com.yougy.common.model.BaseData;

import java.util.List;

public class TaskSummary extends BaseData {
    @SerializedName("count")
    private int taskCount;
    @SerializedName("data")
    private List<Task> tasks;

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
