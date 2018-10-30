package com.yougy.home.fragment.mainFragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.common.fragment.BFragment;
import com.yougy.common.utils.LogUtils;
import com.yougy.homework.PageableRecyclerView;
import com.yougy.task.Task;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.AdapterFragmentHomeworkBinding;
import com.yougy.ui.activity.databinding.FragmentTaskBinding;

import org.litepal.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class TaskFragment extends BFragment {


    private FragmentTaskBinding binding;
    private PageableRecyclerView.Adapter<TaskHolder> adapter;

    private List<Task> tasks = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_task,container,false);
        loadData();
        initView();
        return binding.getRoot();
    }

    private void loadData(){
        for(int i =0;i<50;i++){
            Task task = new Task();
            task.setTaskName("语文 "+i);
            tasks.add(task);
        }
    }

    private void initView(){
        LogUtils.e(tag,"init view ... ");
        binding.pageableRecycler.setMaxItemNumInOnePage(9);
        binding.pageableRecycler.setLayoutManager(new GridLayoutManager(getContext(),3));
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.adaper_divider_img_normal));
        binding.pageableRecycler.addItemDecoration(divider);
        binding.pageableRecycler.setAdapter(adapter = new PageableRecyclerView.Adapter<TaskHolder>() {
            @Override
            public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new TaskHolder(DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.adapter_fragment_homework,parent,false));
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


    private class TaskHolder extends RecyclerView.ViewHolder{

        AdapterFragmentHomeworkBinding binding;

        public TaskHolder(AdapterFragmentHomeworkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(Task task){
            binding.tvMyTitle.setText(task.getTaskName());
            LogUtils.e(tag,"task name is : " + task.getTaskName());
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }


}
