package com.yougy.task.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author: zhang yc
 * @create date: 2018/6/20 18:00
 * @class desc: 任务首页适配器
 * @modifier: 
 * @modify date: 2018/6/20 18:00
 * @modify desc: 
 */
public class TaskSubjectAdapter extends RecyclerView.Adapter<TaskSubjectAdapter.TaskViewHolder>{

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {

        public TaskViewHolder(View itemView) {
            super(itemView);
        }
    }
}
