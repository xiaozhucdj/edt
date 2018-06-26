package com.yougy.task.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.common.fragment.BFragment;
import com.yougy.common.utils.LogUtils;
import com.yougy.task.TaskDetailActivity;
import com.yougy.task.bean.TaskContentBean;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.FragmentTabTaskContentBinding;

/**
 * Created by lenovo on 2018/6/21.
 */

public class FragmentTaskContent extends BFragment {
    FragmentTabTaskContentBinding binding;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tab_task_content, container, false);
        loadData();
        return binding.getRoot();
    }

    private void loadData () {
        TaskContentBean taskContentBean = ((TaskDetailActivity)getActivity()).getTaskContentBean();
        if (taskContentBean != null) {
            LogUtils.d("taskContentBean getTaskContentTitle : " + taskContentBean.getTaskContentTitle());
            binding.setContent(taskContentBean);
        } else {
            LogUtils.d("taskContentBean null.");
        }
    }
}
