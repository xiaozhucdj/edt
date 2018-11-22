package com.yougy.task;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frank.etude.pageable.PageBtnBarAdapterV2;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.homework.PageableRecyclerView;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.TaskItemBinding;
import com.yougy.ui.activity.databinding.TaskListLayoutBinding;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class TaskListActivity extends BaseActivity {
    private TaskListLayoutBinding binding;
    private List<Task> tasks = new ArrayList<>();
    private TaskAdapter adapter;
    private List<Task> currentTasks = new ArrayList<>();
    private int currentPage;
    private final int MAX_PAGE_COUNT = 5;
    private int tasksCount = 50;
    private List<Task> completedTasks = new ArrayList<>();
    private List<Task> unCompleteTasks = new ArrayList<>();
    private boolean isComplete = true;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.task_list_layout);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initLayout() {
        binding.completedTv.setSelected(true);
        binding.tasksRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.tasksRecycler.setAdapter(adapter = new TaskAdapter());
    }

    @Override
    protected void loadData() {
        generateData();
        binding.pageBarTask.setPageBarAdapter(new PageBtnBarAdapterV2(this) {
            @Override
            public int getPageBtnCount() {
                return (tasksCount + MAX_PAGE_COUNT - 1) / MAX_PAGE_COUNT;
            }

            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn, int lastSelectPageBtnIndex) {
                refreshTask(btnIndex);
            }

            @Override
            public void onNoPageToShow() {

            }
        });
        binding.pageBarTask.selectPageBtn(0, false);
    }

    private void generateData() {

//        NetWorkManager.queryTasks(SpUtils.getUserId(), currentPage)
//                .compose(bindToLifecycle())
//                .subscribe(taskSummary -> {
//                    List<Task> taskList = taskSummary.getTasks();
//                    if (null != taskList && taskList.size() > 0) {
//                        for (Task task : taskList) {
//                            if (task.isComplete()) {
//                                completedTasks.add(task);
//                            } else {
//                                unCompleteTasks.add(task);
//                            }
//                        }
//                        tasks.clear();
//                        if (isComplete) {
//                            tasks.addAll(completedTasks);
//                        } else {
//                            tasks.addAll(unCompleteTasks);
//                        }
//                        int start = currentPage * MAX_PAGE_COUNT;
//                        int end = start + MAX_PAGE_COUNT;
//                        if (end > tasks.size() - 1) {
//                            end = tasks.size();
//                        }
//                        currentTasks.addAll(tasks.subList(start, end));
//                        adapter.notifyDataSetChanged();
//                    }
//                }, throwable -> ToastUtil.showCustomToast(TaskListActivity.this, "获取任务失败"));
        tasks.clear();

        int size = tasks.size();
        for (int i = size; i < size + 20; i++) {
            Task task = new Task();
            task.setTaskName("背诵并默写伤仲永：" + i);
            task.setComplete(i % 2 == 0);
            task.setNeedSignature(i % 3 == 0);
            task.setDataCount(i + 3);
            task.setExerciseCount(i + 5);
//            tasks.add(task);
            if (task.isComplete()) {
                completedTasks.add(task);
            } else {
                unCompleteTasks.add(task);
            }
        }

        if (isComplete) {
            tasks.addAll(completedTasks);
        } else {
            tasks.addAll(unCompleteTasks);
        }

        int start = currentPage * MAX_PAGE_COUNT;
        int end = start + MAX_PAGE_COUNT;
        if (end > tasks.size() - 1) {
            end = tasks.size();
        }
        currentTasks.addAll(tasks.subList(start, end));
        adapter.notifyDataSetChanged();
    }

    private void refreshTask(int index) {
        currentTasks.clear();
        currentPage = index;
        if ((index + 1) * MAX_PAGE_COUNT > tasks.size()) {
            generateData();
        } else {
            tasks.clear();
            if (isComplete) {
                tasks.addAll(completedTasks);
            } else {
                tasks.addAll(unCompleteTasks);
            }
            int start = index * MAX_PAGE_COUNT;
            int end = start + MAX_PAGE_COUNT;
            if (index == binding.pageBarTask.getPageBarAdapter().getPageBtnCount() - 1) {
                end = tasks.size();
            }
            currentTasks.addAll(tasks.subList(start, end));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void refreshView() {

    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {

        @NonNull
        @Override
        public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TaskHolder(DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.task_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
            holder.setData(currentTasks.get(position));
        }

        @Override
        public int getItemCount() {
            return currentTasks.size();
        }
    }

    private class TaskHolder extends RecyclerView.ViewHolder {
        TaskItemBinding binding;
        View itemView;

        public TaskHolder(TaskItemBinding binding) {
            super(binding.getRoot());
            itemView = binding.getRoot();
            this.binding = binding;
        }

        public void setData(Task task) {
            binding.taskTitle.setText(task.getTaskName());
            binding.taskSignature.setVisibility(task.isNeedSignature() ? View.VISIBLE : View.GONE);
            binding.taskChapter.setText(task.getChapter());
            binding.taskDataCount.setText(String.valueOf(task.getDataCount()));
            binding.taskExerciseCount.setText(String.valueOf(task.getExerciseCount()));
            itemView.setOnClickListener(v -> startActivity(new Intent(TaskListActivity.this, TaskDetailStudentActivity.class)));
        }
    }

    public void uncomplete(View view) {
        binding.uncompleteTv.setSelected(true);
        binding.completedTv.setSelected(false);
        isComplete = false;
    }

    public void complete(View view) {
        binding.uncompleteTv.setSelected(false);
        binding.completedTv.setSelected(true);
        isComplete = true;
    }

}
