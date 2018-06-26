package com.yougy.task.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.common.fragment.BFragment;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.FragmentTabTaskExerciseBinding;

/**
 * Created by lenovo on 2018/6/21.
 */

public class FragmentTaskExercise extends BFragment {
    FragmentTabTaskExerciseBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tab_task_exercise, container, false);
        return binding.getRoot();
    }
}
