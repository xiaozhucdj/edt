package com.yougy.home.fragment.mainFragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.fragment.BFragment;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.homework.PageableRecyclerView;
import com.yougy.homework.bean.HomeworkBookSummary;
import com.yougy.task.activity.TaskListActivity;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.AdapterFragmentHomeworkBinding;
import com.yougy.ui.activity.databinding.FragmentTaskBinding;

import java.util.ArrayList;
import java.util.List;

public class TaskFragment extends BFragment {


    private FragmentTaskBinding binding;
    private PageableRecyclerView.Adapter<TaskHolder> adapter;

    private List<HomeworkBookSummary> bookList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_task, container, false);
        initView();
        return binding.getRoot();
    }

    private void loadData() {
        LogUtils.e(tag,"TaskFragment load data ........ ");
        if (NetUtils.isNetConnected()) {
            NetWorkManager.queryHomeworkBookList(SpUtils.getUserId() + "", SpUtils.getGradeName())
                    .subscribe(homeworkBookInfos -> freshUI(homeworkBookInfos), throwable -> {
                        ToastUtil.showCustomToast(getContext(), "获取任务数据失败,请点击刷新重新获取");
                        throwable.printStackTrace();
                    });
        } else {
//          showCancelAndDetermineDialog(R.string.jump_to_net);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void freshUI(List<HomeworkBookSummary> beans) {
        if (beans != null && beans.size() > 0) {
            bookList = beans;
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
                holder.setData(bookList.get(position));
            }

            @Override
            public int getItemCount() {
                return bookList.size();
            }
        });
    }


    private class TaskHolder extends RecyclerView.ViewHolder {

        AdapterFragmentHomeworkBinding binding;

        public TaskHolder(AdapterFragmentHomeworkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(HomeworkBookSummary book) {
            LogUtils.e(tag,"task is : " + book);
            binding.tvMyTitle.setText(book.getHomeworkFitSubjectName());
            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(context, TaskListActivity.class);
                intent.putExtra("contentBookLink",book.getCourseBookId());
                intent.putExtra("courseBookTitle",book.getCourseBookTitle());
                intent.putExtra("homeworkId",book.getHomeworkId());
                context.startActivity(intent);
            });
        }

    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        if (event.getType().equalsIgnoreCase(EventBusConstant.task_event)) {
            LogUtils.i("type .." + EventBusConstant.task_event);
            loadData();
        }
    }

}
