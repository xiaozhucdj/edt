package com.yougy.task.activity;

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
import com.yougy.common.utils.ToastUtil;
import com.yougy.message.attachment.TaskRemindAttachment;
import com.yougy.task.bean.Task;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.TaskItemBinding;
import com.yougy.ui.activity.databinding.TaskListLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends BaseActivity {
    private TaskListLayoutBinding binding;
    private List<Task> tasks = new ArrayList<>();
    private TaskAdapter adapter;
    private List<Task> currentTasks = new ArrayList<>();
    private int currentPage;
    private final int MAX_PAGE_COUNT = 5;
    private final int MAX_SIZE_PER_PAGE = 20;
    private int tasksCount;
    private List<Task> completedTasks = new ArrayList<>();
    private List<Task> unCompleteTasks = new ArrayList<>();
    private boolean isComplete = true;
    private int contentCourseLink;
    private String courseBookTitle;


    @Override
    protected void setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.task_list_layout);
    }

    @Override
    protected void init() {
        Intent intent = getIntent();
        contentCourseLink = intent.getIntExtra("contentBookLink", -1);
        courseBookTitle = intent.getStringExtra("courseBookTitle");
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
        binding.topLayout.titleTv.setText(courseBookTitle);
        initPageBar();
    }

    private void initPageBar() {
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

    @Override
    protected void loadData() {
        generateData();
    }

    private void generateData() {
        NetWorkManager.queryTasks(13253, 8292, tasks.size(), MAX_SIZE_PER_PAGE)
                .compose(bindToLifecycle())
                .subscribe(taskSummary -> {
                    tasksCount = taskSummary.getCount();
                    List<Task> taskList = taskSummary.getData();
                    if (null != taskList && taskList.size() > 0) {
                        for (Task task : taskList) {
                            if (task.isComplete()) {
                                completedTasks.add(task);
                            } else {
                                unCompleteTasks.add(task);
                            }
                        }
                        tasks.clear();
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
//                        initPageBar();
                        binding.pageBarTask.refreshPageBar();
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    ToastUtil.showCustomToast(TaskListActivity.this, "获取任务失败");
                });


//        int size = tasks.size();
//        for (int i = size; i < size + 20; i++) {
//            Task task = new Task();
//            task.setContentTitle(courseBookTitle + i);
//            task.setContentCourseLinkName("背诵并默写伤仲永：" + i);
//            task.setComplete(i % 2 == 0);
//            task.setSignCount(i % 3 == 0 ? 1 : 0);
//            task.setDataCount(i + 3);
//            task.setExerciseCount(i + 5);
//            if (task.isComplete()) {
//                completedTasks.add(task);
//            } else {
//                unCompleteTasks.add(task);
//            }
//        }
//
//        if (isComplete) {
//            tasks.addAll(completedTasks);
//        } else {
//            tasks.addAll(unCompleteTasks);
//        }
//
//        int start = currentPage * MAX_PAGE_COUNT;
//        int end = start + MAX_PAGE_COUNT;
//        if (end > tasks.size() - 1) {
//            end = tasks.size();
//        }
//        currentTasks.addAll(tasks.subList(start, end));
//        adapter.notifyDataSetChanged();
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
            binding.taskTitle.setText(task.getContentTitle());
            binding.taskSignature.setVisibility(task.isNeedSignature() ? View.VISIBLE : View.GONE);
            binding.taskChapter.setText(task.getContentCourseLinkName());
            binding.taskDataCount.setText(String.valueOf(task.getDataCount()));
            binding.taskExerciseCount.setText(String.valueOf(task.getExerciseCount()));
            binding.taskCompleteTime.setText(getString(R.string.task_complete_time, "2018-11-28 17:48", "2018-11-29 17:00"));
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(TaskListActivity.this, TaskDetailStudentActivity.class);
                intent.putExtra(TaskRemindAttachment.KEY_TASK_ID,task.getContentElement());
                intent.putExtra(TaskRemindAttachment.KEY_TASK_NAME,task.getContentTitle());
                startActivity(intent);
            });
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
