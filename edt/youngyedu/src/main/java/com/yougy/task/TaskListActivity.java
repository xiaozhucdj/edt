package com.yougy.task;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.common.activity.BaseActivity;
import com.yougy.homework.PageableRecyclerView;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.TaskItemBinding;
import com.yougy.ui.activity.databinding.TaskListLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends BaseActivity {
    private TaskListLayoutBinding binding;
    private List<Task> tasks = new ArrayList<>();
    private PageableRecyclerView.Adapter<TaskHolder> adapter;


    @Override
    protected void setContentView() {
        binding = DataBindingUtil.setContentView(this,R.layout.task_list_layout);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initLayout() {
        binding.uncompleteTv.setSelected(true);
        binding.tasksRecycler.setMaxItemNumInOnePage(4);
        binding.tasksRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        binding.tasksRecycler.setAdapter(adapter = new PageableRecyclerView.Adapter<TaskHolder>() {
            @Override
            public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new TaskHolder(DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()),R.layout.task_item,parent,false));
            }

            @Override
            public void onBindViewHolder(TaskHolder holder, int position) {
                holder.setData(tasks.get(position));
            }

            @Override
            public int getItemCount() {
                return tasks.size();
            }
        });
    }

    @Override
    protected void loadData() {
        for (int i = 0;i<50;i++){
            Task task = new Task();
            task.setTaskName("背诵并默写伤仲永："+i);
            tasks.add(task);
        }
        binding.tasksRecycler.notifyDataSetChanged();
    }

    @Override
    protected void refreshView() {

    }


    private class TaskHolder extends RecyclerView.ViewHolder{
        TaskItemBinding binding;
        public TaskHolder(TaskItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(Task task){
            binding.taskTitle.setText(task.getTaskName());
        }
    }

    public void uncomplete(View view){
        binding.uncompleteTv.setSelected(true);
        binding.completedTv.setSelected(false);
    }

    public void complete(View view){
        binding.uncompleteTv.setSelected(false);
        binding.completedTv.setSelected(true);
    }

}
