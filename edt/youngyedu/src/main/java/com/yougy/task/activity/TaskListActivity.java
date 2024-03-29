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
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.manager.YoungyApplicationManager;
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

import de.greenrobot.event.EventBus;

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
    protected void onDestroy() {
        super.onDestroy();
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
        generateData(isComplete ? currentCompletedPage : currentUnCompletePage);
    }

    private String contentStatusCode = "SV01";

    private void generateData(int currentPage) {
        NetWorkManager.queryTasks(homeworkId, contentBookLink, currentPage + 1, MAX_PAGE_COUNT, contentStatusCode)
                .compose(bindToLifecycle())
                .subscribe(taskSummary -> {
                    List<Task> taskList = taskSummary.getData();
                    if (isComplete) {
                        currentCompletedTasks.clear();
                        currentCompletedTasks.addAll(taskList);
                        completedTasksCount = taskSummary.getCount();

                        if (currentCompletedTasks.size() == 0) {
                            binding.textDataEmpty.setText(R.string.no_completed_task);
                            binding.textDataEmpty.setVisibility(View.VISIBLE);
                            binding.completedTasksRecycler.setVisibility(View.GONE);
                        } else {
                            binding.textDataEmpty.setVisibility(View.GONE);
                            completedAdapter.notifyDataSetChanged();
                            binding.completedPageBarTask.refreshPageBar();
                            binding.completedTasksRecycler.setVisibility(View.VISIBLE);
                        }
                    } else {
                        currentUnCompleteTasks.clear();
                        currentUnCompleteTasks.addAll(taskList);
                        uncompleteTasksCount = taskSummary.getCount();
                        if (currentUnCompleteTasks.size() == 0) {
                            binding.textDataEmpty.setText(R.string.no_uncomplete_task);
                            binding.textDataEmpty.setVisibility(View.VISIBLE);
                            binding.uncompleteTasksRecycler.setVisibility(View.GONE);
                        } else {
                            binding.textDataEmpty.setVisibility(View.GONE);
                            unCompleteAdapter.notifyDataSetChanged();
                            binding.uncompletePageBarTask.refreshPageBar();
                            binding.uncompleteTasksRecycler.setVisibility(View.VISIBLE);
                        }
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    ToastUtil.showCustomToast(TaskListActivity.this, R.string.get_task_failed);
                });
    }

    private void refreshUnCompleteTask(int index) {
        currentUnCompleteTasks.clear();
        currentUnCompletePage = index;
        generateData(index);
    }

    private void refreshCompletedTask(int index) {
        currentCompletedTasks.clear();
        currentCompletedPage = index;
        generateData(index);
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
            if (task.isNeedSignature()) {
                binding.signature.setText(task.isSigned());
                binding.signature.setVisibility(isComplete ? View.VISIBLE : View.GONE);
                binding.signatureText.setVisibility(isComplete ? View.VISIBLE : View.GONE);
            }else{
                binding.signature.setVisibility(View.GONE);
                binding.signatureText.setVisibility(View.GONE);
            }
            binding.taskChapter.setText(getString(R.string.task_chapter, task.getContentCourseLinkName()));
            binding.taskDataCount.setText(String.valueOf(task.getDataCount()));
            binding.taskExerciseCount.setText(String.valueOf(task.getExerciseCount()));
            binding.taskCompleteTime.setText(getString(R.string.task_complete_time, task.getPerformStartTime(), task.getPerformEndTime()));
            itemView.setOnClickListener(v -> {
                clickedTask = task;
                Intent intent = new Intent(TaskListActivity.this, TaskDetailStudentActivity.class);
                intent.putExtra(TaskRemindAttachment.KEY_TASK_ID, task.getContentElement());
                intent.putExtra(TaskRemindAttachment.KEY_DRAMA_ID, task.getContentDrama());
                intent.putExtra(TaskRemindAttachment.KEY_TASK_NAME, task.getContentTitle());
                intent.putExtra(TaskRemindAttachment.IS_SIGN, task.isNeedSignature());
                intent.putExtra(TaskRemindAttachment.SCENE_STATUS_CODE, task.getContentStatusCode());
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
            generateData(currentUnCompletePage);
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
        if (currentUnCompleteTasks.size() > 0) {
            binding.textDataEmpty.setVisibility(View.GONE);
        } else {
            binding.textDataEmpty.setText(R.string.no_uncomplete_task);
            binding.textDataEmpty.setVisibility(View.VISIBLE);
        }
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
        if (currentCompletedTasks.size() > 0) {
            binding.textDataEmpty.setVisibility(View.GONE);
        } else {
            binding.textDataEmpty.setText(R.string.no_completed_task);
            binding.textDataEmpty.setVisibility(View.VISIBLE);
        }
        generateData(currentCompletedPage);
    }

}
