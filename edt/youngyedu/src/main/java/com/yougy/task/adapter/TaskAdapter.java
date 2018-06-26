package com.yougy.task.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.common.utils.LogUtils;
import com.yougy.task.bean.TaskBean;
import com.yougy.ui.activity.BR;
import com.yougy.ui.activity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhang yc
 * @create date: 2018/6/20 17:46
 * @class desc: 任务各种状态适配器
 * @modifier: 
 * @modify date: 2018/6/20 17:46
 * @modify desc: 
 */
public class TaskAdapter extends RecyclerView.Adapter{
    private List<TaskBean> mTaskBeanList = new ArrayList<>();

    private Context mContext;

    public TaskAdapter (Context context) {
        mContext = context;
    }

    public void setData (List<TaskBean> taskBeanList) {
        mTaskBeanList.clear();
        mTaskBeanList.addAll(taskBeanList);
        LogUtils.d("zhangyc setData mTaskBeanList size :"  + mTaskBeanList.size());
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.task_item, parent, false);
        return new TaskViewHolder(binding.getRoot());

//        View view = UIUtils.inflate(R.layout.activity_task_item);
//        return new TaskAdapter.TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LogUtils.d("zhangyc onBindViewHolder position = " + position);
        ViewDataBinding binding = DataBindingUtil.getBinding(holder.itemView);
        //set data
        binding.setVariable(BR.task,mTaskBeanList.get(position));
        binding.executePendingBindings();
    }

//    @Override
//    public void onBindViewHolder(TaskViewHolder holder, int position) {
//        LogUtils.d("zhangyc onBindViewHolder position = " + position);
//        ViewDataBinding binding = DataBindingUtil.getBinding(holder.itemView);
//        //set data
//        binding.setVariable(BR.task,mTaskBeanList.get(position));
//        binding.executePendingBindings();
//    }

    @Override
    public int getItemCount() {
        LogUtils.d("zhangyc getItemCount mTaskBeanList = " + mTaskBeanList.size());
        return mTaskBeanList.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {

        public TaskViewHolder(View itemView) {
            super(itemView);
        }
    }
}