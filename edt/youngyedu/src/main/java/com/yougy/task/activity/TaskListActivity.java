package com.yougy.task.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frank.etude.pageable.PageBtnBarAdapterV2;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.message.attachment.TaskRemindAttachment;
import com.yougy.task.bean.Task;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.TaskItemBinding;
import com.yougy.ui.activity.databinding.TaskListLayoutBinding;

import org.litepal.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends BaseActivity {
    private TaskListLayoutBinding binding;
    private TaskAdapter unCompleteAdapter;
    private TaskAdapter completedAdapter;
    private List<Task> currentCompletedTasks = new ArrayList<>();
    private List<Task> currentUnCompleteTasks = new ArrayList<>();
    private int currentCompletedPage;
    private int currentUnCompletePage;
    private int completedTasksCount;
    private int uncompleteTasksCount;

    private List<Task> completedTasks = new ArrayList<>();
    private List<Task> unCompleteTasks = new ArrayList<>();
    private boolean isComplete = false;
    private int contentBookLink;
    private String courseBookTitle;
    private int homeworkId;
    private final int MAX_PAGE_COUNT = 5;
    public static final int REQUEST_CODE = 1000;


    @Override
    protected void setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.task_list_layout);
    }

    @Override
    protected void init() {
        Intent intent = getIntent();
        contentBookLink = intent.getIntExtra("contentBookLink", -1);
        courseBookTitle = intent.getStringExtra("courseBookTitle");
        homeworkId = intent.getIntExtra("homeworkId", -1);
    }

    @Override
    protected void initLayout() {
        binding.uncompleteTv.setSelected(true);
        binding.uncompleteTasksRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.uncompleteTasksRecycler.setAdapter(unCompleteAdapter = new TaskAdapter(currentUnCompleteTasks));

        binding.completedTasksRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.completedTasksRecycler.setAdapter(completedAdapter = new TaskAdapter(currentCompletedTasks));
        binding.topLayout.titleTv.setText(courseBookTitle);
        initPageBar();
    }

    private void initPageBar() {
        binding.uncompletePageBarTask.setPageBarAdapter(new PageBtnBarAdapterV2(this) {
            @Override
            public int getPageBtnCount() {
                return (uncompleteTasksCount + MAX_PAGE_COUNT - 1) / MAX_PAGE_COUNT;
            }

            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn, int lastSelectPageBtnIndex) {
                LogUtils.e(tag, "onPageBtnClick........." + btnIndex);
                refreshUnCompleteTask(btnIndex);
            }

            @Override
            public void onNoPageToShow() {

            }
        });
        binding.uncompletePageBarTask.selectPageBtn(0, false);

        binding.completedPageBarTask.setPageBarAdapter(new PageBtnBarAdapterV2(this) {
            @Override
            public int getPageBtnCount() {
                return (completedTasksCount + MAX_PAGE_COUNT - 1) / MAX_PAGE_COUNT;
            }

            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn, int lastSelectPageBtnIndex) {
                LogUtils.e(tag, "onPageBtnClick........." + btnIndex);
                refreshCompletedTask(btnIndex);
            }

            @Override
            public void onNoPageToShow() {

            }
        });
        binding.completedPageBarTask.selectPageBtn(0, false);
    }

    @Override
    protected void loadData() {
        generateData(currentUnCompletePage);
    }

    @Override
    public void refresh(View view) {
        super.refresh(view);
        if (isComplete) {
            completedTasks.clear();
            completeSparseArray.clear();
        } else {
            unCompleteTasks.clear();
        }
        generateData(isComplete ? currentCompletedPage : currentUnCompletePage);
    }

//    private SparseArray<List<Task>> unCompleteSparseArray = new SparseArray<>();
    private SparseArray<List<Task>> completeSparseArray = new SparseArray<>();
    private String contentStatusCode = "SV01";

    private void generateData(int currentPage) {
        LogUtils.e(tag, "homeworkId is : " + homeworkId + ",contentBookLink is : " + contentBookLink);
//        int pageCount = MAX_PAGE_COUNT;
//        int pageBtnCount = isComplete ? binding.completedPageBarTask.getPageBarAdapter().getPageBtnCount() : binding.uncompletePageBarTask.getPageBarAdapter().getPageBtnCount();
//        if (currentPage == pageBtnCount - 1) {
//            int tasksCount = isComplete ? completedTasksCount : uncompleteTasksCount;
//            pageCount = tasksCount % MAX_PAGE_COUNT == 0 ? MAX_PAGE_COUNT : tasksCount % MAX_PAGE_COUNT;
//        }
        NetWorkManager.queryTasks(homeworkId, contentBookLink, currentPage + 1, MAX_PAGE_COUNT, contentStatusCode)
                .compose(bindToLifecycle())
                .subscribe(taskSummary -> {
                    List<Task> taskList = taskSummary.getData();
                    if (isComplete) {
                        currentCompletedTasks.clear();
                        currentCompletedTasks.addAll(taskList);
                        completedTasksCount = taskSummary.getCount();
                        completeSparseArray.put(currentPage, taskList);
                        completedTasks.addAll(taskList);
                        completedAdapter.notifyDataSetChanged();
                    } else {
                        currentUnCompleteTasks.clear();
                        currentUnCompleteTasks.addAll(taskList);
                        uncompleteTasksCount = taskSummary.getCount();
                        unCompleteTasks.addAll(taskList);
                        unCompleteAdapter.notifyDataSetChanged();
                        binding.uncompletePageBarTask.refreshPageBar();
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    ToastUtil.showCustomToast(TaskListActivity.this, "获取任务失败");
                });
    }

    private void refreshUnCompleteTask(int index) {
        currentUnCompleteTasks.clear();
        currentUnCompletePage = index;
//        if (unCompleteSparseArray.get(index) != null) {
//            int start = index * MAX_PAGE_COUNT;
//            int end = start + MAX_PAGE_COUNT;
//            if (index == binding.uncompletePageBarTask.getPageBarAdapter().getPageBtnCount() - 1) {
//                end = unCompleteTasks.size();
//            }
//            currentUnCompleteTasks.addAll(unCompleteTasks.subList(start, end));
//            unCompleteAdapter.notifyDataSetChanged();
//        } else {
            generateData(index);
//        }
    }

    private void refreshCompletedTask(int index) {
        currentCompletedTasks.clear();
        currentCompletedPage = index;
//        if (completeSparseArray.get(index) != null) {
//            int start = index * MAX_PAGE_COUNT;
//            int end = start + MAX_PAGE_COUNT;
//            if (index == binding.completedPageBarTask.getPageBarAdapter().getPageBtnCount() - 1) {
//                end = completedTasks.size();
//            }
//            currentCompletedTasks.addAll(completedTasks.subList(start, end));
//            completedAdapter.notifyDataSetChanged();
//        } else {
            generateData(index);
//        }
    }


    @Override
    protected void refreshView() {

    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {
        private List<Task> tasks;

        public TaskAdapter(List<Task> tasks) {
            this.tasks = tasks;
        }

        @NonNull
        @Override
        public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TaskHolder(DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.task_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
            holder.setData(tasks.get(position));
        }

        @Override
        public int getItemCount() {
            return tasks.size();
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
                clickedTask = task;
                Intent intent = new Intent(TaskListActivity.this, TaskDetailStudentActivity.class);
                intent.putExtra(TaskRemindAttachment.KEY_TASK_ID, task.getContentElement());
                intent.putExtra(TaskRemindAttachment.KEY_TASK_ID_DEST, task.getContentDrama());
                intent.putExtra(TaskRemindAttachment.KEY_TASK_NAME, task.getContentTitle());
                intent.putExtra("isSign", task.isNeedSignature());
                intent.putExtra("ContentStatusCode", task.getContentStatusCode());
                startActivityForResult(intent, REQUEST_CODE);
            });
        }
    }

    private Task clickedTask;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 10000) {
            LogUtils.e(tag, "onActivityResult............");
            unCompleteTasks.remove(clickedTask);
            currentUnCompleteTasks.clear();
            if (unCompleteTasks.size() > 0) {
                int start = currentUnCompletePage * MAX_PAGE_COUNT;
                int end = start + MAX_PAGE_COUNT;
                if (end > unCompleteTasks.size() - 1) {
                    end = unCompleteTasks.size();
                }
                currentUnCompleteTasks.addAll(unCompleteTasks.subList(start, end));
            }
            unCompleteAdapter.notifyDataSetChanged();
            uncompleteTasksCount--;
            binding.uncompletePageBarTask.refreshPageBar();
        }
    }

    public void uncomplete(View view) {
        binding.uncompleteTv.setSelected(true);
        binding.completedTv.setSelected(false);
        isComplete = false;
        contentStatusCode = "SV01";
        binding.completedPageBarTask.setVisibility(View.GONE);
        binding.completedTasksRecycler.setVisibility(View.GONE);
        binding.uncompletePageBarTask.setVisibility(View.VISIBLE);
        binding.uncompleteTasksRecycler.setVisibility(View.VISIBLE);
    }

    public void complete(View view) {
        binding.uncompleteTv.setSelected(false);
        binding.completedTv.setSelected(true);
        isComplete = true;
        contentStatusCode = "SV02";
        binding.uncompletePageBarTask.setVisibility(View.GONE);
        binding.uncompleteTasksRecycler.setVisibility(View.GONE);
        binding.completedPageBarTask.setVisibility(View.VISIBLE);
        binding.completedTasksRecycler.setVisibility(View.VISIBLE);
        if (completeSparseArray.get(currentCompletedPage) == null) {
            generateData(currentCompletedPage);
        }
    }

}
