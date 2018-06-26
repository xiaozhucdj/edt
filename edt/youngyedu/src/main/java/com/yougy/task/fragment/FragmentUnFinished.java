package com.yougy.task.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.common.fragment.BFragment;
import com.yougy.common.utils.LogUtils;
import com.yougy.task.TaskActivity;
import com.yougy.task.adapter.TaskAdapter;
import com.yougy.task.bean.TaskBean;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.FragmentTasklistBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2018/6/22.
 */

public class FragmentUnFinished  extends BFragment{

    FragmentTasklistBinding binding;

    private List<TaskBean> taskBeanLists = new ArrayList<>();
    private List<TaskBean> currentTaskList = new ArrayList<>();

    private TaskActivity taskActivity;

    private TaskAdapter mTaskAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tasklist, container, false);
        taskActivity = (TaskActivity)getActivity();
        return binding.getRoot();
    }

    private void loadData () {
        taskBeanLists.clear();
        taskBeanLists.addAll(taskActivity.getUnFinTaskBeanList());
        mTaskAdapter = new TaskAdapter(taskActivity);
        initBtnBarPages();
    }

    /**
     *  数据请求成功
     */
    private void initBtnBarPages () {
        int count = taskActivity.getBtnBarCounts(taskBeanLists.size(), TaskActivity.COUNT_PER_PAGE);
        LogUtils.d("zhangyc count" + count);
        taskActivity.addBtnBarCounts(binding.btnBarTaskAct, count);
        LogUtils.d("zhangyc getCurrentSelectPageIndex:" + binding.btnBarTaskAct.getCurrentSelectPageIndex());

        currentTaskList.clear();
        if (taskBeanLists.size() > TaskActivity.COUNT_PER_PAGE) { // 大于1页
            currentTaskList.addAll(taskBeanLists.subList(0, TaskActivity.COUNT_PER_PAGE));
        } else {
            //小于1页
            currentTaskList.addAll(taskBeanLists.subList(0, taskBeanLists.size()));
        }
        mTaskAdapter.setData(currentTaskList);
    }
}
