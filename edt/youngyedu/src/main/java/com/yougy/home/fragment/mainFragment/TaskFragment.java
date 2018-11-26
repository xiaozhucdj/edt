package com.yougy.home.fragment.mainFragment;

import android.content.Intent;
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
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.homework.PageableRecyclerView;
import com.yougy.homework.bean.HomeworkBookSummary;
import com.yougy.task.Task;
import com.yougy.task.TaskListActivity;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.AdapterFragmentHomeworkBinding;
import com.yougy.ui.activity.databinding.FragmentTaskBinding;

import org.litepal.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class TaskFragment extends BFragment {


    private FragmentTaskBinding binding;
    private PageableRecyclerView.Adapter<TaskHolder> adapter;

    private List<Task> tasks = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_task, container, false);
        initView();
        return binding.getRoot();
    }

    private void loadData() {
//        for(int i =0;i<50;i++){
//            Task task = new Task();
//            task.setTaskName("语文 "+i);
//            tasks.add(task);
//        }
//        binding.pageableRecycler.notifyDataSetChanged();

        if (NetUtils.isNetConnected()) {
            NetWorkManager.queryHomeworkBookList(SpUtils.getUserId() + "", SpUtils.getGradeName())
                    .subscribe(homeworkBookInfos -> freshUI(homeworkBookInfos), throwable -> {
                        ToastUtil.showCustomToast(getContext(), "获取作业本数据失败,请点击刷新重新获取");
                        throwable.printStackTrace();
                    });
        } else {
            showCancelAndDetermineDialog(R.string.jump_to_net);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void freshUI(List<HomeworkBookSummary> beans) {
        if (beans != null && beans.size() > 0) {
            for (HomeworkBookSummary bean : beans) {
                Task task = new Task();
                task.setTaskName(bean.getHomeworkFitSubjectName());
                tasks.add(task);
            }
            binding.pageableRecycler.notifyDataSetChanged();
            binding.resultEmpty.setVisibility(View.GONE);
        } else {
            binding.resultEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        LogUtils.e(tag, "init view ... ");
        binding.pageableRecycler.setMaxItemNumInOnePage(9);
        binding.pageableRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
        DividerItemDecoration divider = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(context, R.drawable.adaper_divider_img_normal));
        binding.pageableRecycler.addItemDecoration(divider);
        binding.pageableRecycler.setAdapter(adapter = new PageableRecyclerView.Adapter<TaskHolder>() {
            @Override
            public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new TaskHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_fragment_homework, null, false));
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


    private class TaskHolder extends RecyclerView.ViewHolder {

        AdapterFragmentHomeworkBinding binding;

        public TaskHolder(AdapterFragmentHomeworkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(Task task) {
            binding.tvMyTitle.setText(task.getTaskName());
            binding.getRoot().setOnClickListener(v -> context.startActivity(new Intent(context, TaskListActivity.class)));
        }

    }


}
