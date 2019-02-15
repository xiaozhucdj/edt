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
    private List<Task> tasks = new ArrayList<>();
    private TaskAdapter adapter;
    private List<Task> currentTasks = new ArrayList<>();
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
                int tasksCount = isComplete?completedTasksCount:uncompleteTasksCount;
                return (tasksCount + MAX_PAGE_COUNT - 1) / MAX_PAGE_COUNT;
            }

            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn, int lastSelectPageBtnIndex) {
                LogUtils.e(tag, "onPageBtnClick........." + btnIndex);
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
        generateData(currentUnCompletePage);
    }

    @Override
    public void refresh(View view) {
        super.refresh(view);
        tasks.clear();
        completedTasks.clear();
        unCompleteTasks.clear();
        currentTasks.clear();
        unCompleteSparseArray.clear();
        completeSparseArray.clear();
        generateData(isComplete?currentCompletedPage:currentUnCompletePage);
    }

    private SparseArray<List<Task>> unCompleteSparseArray = new SparseArray<>();
    private SparseArray<List<Task>> completeSparseArray = new SparseArray<>();
    private String contentStatusCode = "SV01";

    private void generateData(int currentPage) {
        LogUtils.e(tag, "homeworkId is : " + homeworkId + ",contentBookLink is : " + contentBookLink);
        int pageCount = MAX_PAGE_COUNT;
        if (currentPage == binding.pageBarTask.getPageBarAdapter().getPageBtnCount() - 1) {
            int tasksCount = isComplete?completedTasksCount:uncompleteTasksCount;
            pageCount = tasksCount % MAX_PAGE_COUNT == 0 ? MAX_PAGE_COUNT : tasksCount % MAX_PAGE_COUNT;
        }
        NetWorkManager.queryTasks(homeworkId, contentBookLink, currentPage + 1, pageCount, contentStatusCode)
                .compose(bindToLifecycle())
                .subscribe(taskSummary -> {
                    List<Task> taskList = taskSummary.getData();
                    if (isComplete) {
                        completedTasksCount = taskSummary.getCount();
                        completeSparseArray.put(currentPage, taskList);
                        completedTasks.addAll(taskList);
                    } else {
                        uncompleteTasksCount = taskSummary.getCount();
                        unCompleteSparseArray.put(currentPage, taskList);
                        unCompleteTasks.addAll(taskList);
                    }
                    currentTasks.addAll(taskList);
                    adapter.notifyDataSetChanged();
                    binding.pageBarTask.refreshPageBar();
//                    if (null != taskList && taskList.size() > 0) {
//                        for (Task task : taskList) {
//                            LogUtils.e(tag, "task's sceneStatusCode is : " + task.getSceneStatusCode());
//                            if (task.isComplete()) {
//                                completedTasks.add(task);
//                            } else {
//                                unCompleteTasks.add(task);
//                            }
//                        }
//
//                        tasks.clear();
//                        tasksCount = taskSummary.getCount() - completedTasks.size();
//                        if (isComplete) {
//                            tasks.addAll(completedTasks);
//                        } else {
//                            tasks.addAll(unCompleteTasks);
//                        }
//                        if (tasks.size() > 0) {
//                            int start = currentPage * MAX_PAGE_COUNT;
//                            int end = start + MAX_PAGE_COUNT;
//                            if (end > tasks.size() - 1) {
//                                end = tasks.size();
//                            }
//                            currentTasks.addAll(tasks.subList(start, end));
//                        }
//                        adapter.notifyDataSetChanged();
//                        binding.pageBarTask.refreshPageBar();
//                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    ToastUtil.showCustomToast(TaskListActivity.this, "获取任务失败");
                });
    }

    private void refreshTask(int index) {
        currentTasks.clear();
        if (isComplete) {
            currentCompletedPage = index;
            if (completeSparseArray.get(index) != null) {
//                currentTasks.addAll(completeSparseArray.get(index));
                int start = index * MAX_PAGE_COUNT;
                int end = start + MAX_PAGE_COUNT;
                if (index == binding.pageBarTask.getPageBarAdapter().getPageBtnCount() - 1) {
                    end = completedTasks.size();
                }
                currentTasks.addAll(completedTasks.subList(start, end));
                adapter.notifyDataSetChanged();
            } else {
                generateData(index);
            }
        } else {
            currentUnCompletePage = index;
            if (unCompleteSparseArray.get(index) != null) {
//                currentTasks.addAll(unCompleteSparseArray.get(index));
                int start = index * MAX_PAGE_COUNT;
                int end = start + MAX_PAGE_COUNT;
                if (index == binding.pageBarTask.getPageBarAdapter().getPageBtnCount() - 1) {
                    end = unCompleteTasks.size();
                }
                currentTasks.addAll(unCompleteTasks.subList(start, end));
                adapter.notifyDataSetChanged();
            } else {
                generateData(index);
            }
        }


//        if (index != 0 && (index + 1) * MAX_PAGE_COUNT > tasks.size() && tasksCount > tasks.size()) {
//            generateData();
//        } else {
//            tasks.clear();
//            LogUtils.e(tag, "uncomplete tasks' size is : " + unCompleteTasks.size() + ",complete tasks'size is : " + completedTasks.size());
//            if (isComplete) {
//                tasks.addAll(completedTasks);
//            } else {
//                tasks.addAll(unCompleteTasks);
//            }
//            if (tasks.size() > 0) {
//                int start = index * MAX_PAGE_COUNT;
//                int end = start + MAX_PAGE_COUNT;
//                if (index == binding.pageBarTask.getPageBarAdapter().getPageBtnCount() - 1) {
//                    end = tasks.size();
//                }
//                currentTasks.addAll(tasks.subList(start, end));
//            }
//            adapter.notifyDataSetChanged();
//        }
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
                clickedTask = task;
                Intent intent = new Intent(TaskListActivity.this, TaskDetailStudentActivity.class);
                intent.putExtra(TaskRemindAttachment.KEY_TASK_ID, task.getContentElement());
                intent.putExtra(TaskRemindAttachment.KEY_TASK_ID_DEST, task.getContentDrama());
                intent.putExtra(TaskRemindAttachment.KEY_TASK_NAME, task.getContentTitle());
                intent.putExtra("isSign", task.isNeedSignature());
                intent.putExtra("SceneStatusCode", task.getSceneStatusCode());
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
            tasks.clear();
            currentTasks.clear();
            if (unCompleteTasks.size() > 0) {
//                tasks.addAll(unCompleteTasks);
                int start = currentUnCompletePage * MAX_PAGE_COUNT;
                int end = start + MAX_PAGE_COUNT;
                if (end > unCompleteTasks.size() - 1) {
                    end = unCompleteTasks.size();
                }
                currentTasks.addAll(unCompleteTasks.subList(start, end));
            }
//            List<Task> tasks = unCompleteSparseArray.get(currentUnCompletePage);
//            tasks.remove(clickedTask);
//            currentTasks.addAll(tasks);
            adapter.notifyDataSetChanged();
            uncompleteTasksCount--;
            binding.pageBarTask.refreshPageBar();
        }
    }

    public void uncomplete(View view) {
        binding.uncompleteTv.setSelected(true);
        binding.completedTv.setSelected(false);
        isComplete = false;
        contentStatusCode = "SV01";
        tasks.clear();
        currentTasks.clear();
//        if (unCompleteTasks.size() > 0) {
//            tasks.addAll(unCompleteTasks);
//            int start = currentPage * MAX_PAGE_COUNT;
//            int end = start + MAX_PAGE_COUNT;
//            if (end > tasks.size() - 1) {
//                end = tasks.size();
//            }
//            currentTasks.addAll(tasks.subList(start, end));
//        }
        if (unCompleteSparseArray.get(currentUnCompletePage) != null) {
//            currentTasks.addAll(completeSparseArray.get(currentCompletedPage));
            int start = currentUnCompletePage * MAX_PAGE_COUNT;
            int end = start + MAX_PAGE_COUNT;
            if (end > unCompleteTasks.size() - 1) {
                end = unCompleteTasks.size();
            }
            currentTasks.addAll(unCompleteTasks.subList(start, end));
        } else {
            generateData(currentUnCompletePage);
        }
        adapter.notifyDataSetChanged();
        binding.pageBarTask.selectPageBtn(currentUnCompletePage,true);
        binding.pageBarTask.refreshPageBar();
    }

    public void complete(View view) {
        binding.uncompleteTv.setSelected(false);
        binding.completedTv.setSelected(true);
        isComplete = true;
        contentStatusCode = "SV02";
        tasks.clear();
        currentTasks.clear();
//        if (completedTasks.size() > 0) {
//            tasks.addAll(completedTasks);
//            int start = currentPage * MAX_PAGE_COUNT;
//            int end = start + MAX_PAGE_COUNT;
//            if (end > tasks.size() - 1) {
//                end = tasks.size();
//            }
//            currentTasks.addAll(tasks.subList(start, end));
//        }
        if (completeSparseArray.get(currentCompletedPage) != null) {
//            currentTasks.addAll(completeSparseArray.get(currentUnCompletePage));
            int start = currentCompletedPage * MAX_PAGE_COUNT;
            int end = start + MAX_PAGE_COUNT;
            if (end > completedTasks.size() - 1) {
                end = completedTasks.size();
            }
            currentTasks.addAll(completedTasks.subList(start, end));
        } else {
            generateData(currentCompletedPage);
        }
        adapter.notifyDataSetChanged();
//        binding.pageBarTask.refreshPageBar();
        binding.pageBarTask.selectPageBtn(currentCompletedPage,true);
    }

}
