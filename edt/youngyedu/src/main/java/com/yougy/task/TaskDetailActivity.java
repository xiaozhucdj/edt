package com.yougy.task;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;

import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.task.bean.ExercisesBean;
import com.yougy.task.bean.StudyDataBean;
import com.yougy.task.bean.TaskBean;
import com.yougy.task.bean.TaskContentBean;
import com.yougy.task.fragment.FragmentTaskContent;
import com.yougy.task.fragment.FragmentTaskExercise;
import com.yougy.task.fragment.FragmentTaskMaterials;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityTaskDetailBinding;

import java.util.List;

import butterknife.OnClick;


/**
 * @author: zhang yc
 * @create date: 2018/6/20 18:00
 * @class desc: 任务详情  打开任务已经提交  底部按钮提交家长签字  已经完成签字暂不显示
 * @modifier: 
 * @modify date: 2018/6/20 18:00
 * @modify desc: 
 */
public class TaskDetailActivity extends TaskBaseActivity{
    private ActivityTaskDetailBinding binding;

    private int currentSelectTab = 0;
    private final int TAB_CONTENT = 0;
    private final int TAB_MATERIALS = 1;
    private final int TAB_EXERCISES = 2;

    private FragmentTaskContent fragmentTaskContent ;
    private FragmentTaskMaterials fragmentTaskMaterials ;
    private FragmentTaskExercise fragmentTaskExercise ;

    private TaskBean taskBean;
    private TaskContentBean taskContentBean;
    private List<StudyDataBean> studyDataBeanList;
    private List<ExercisesBean> exercisesBeanList;

    @Override
    protected void yxMsgObserverCall(Object o) {

    }

    @Override
    protected void init() {
        super.init();
        Intent intent = getIntent();
        taskBean = intent.getParcelableExtra("taskBean");
        taskContentBean = intent.getParcelableExtra("taskContentBean");
        studyDataBeanList = intent.getParcelableArrayListExtra("studyDataList");
        exercisesBeanList = intent.getParcelableArrayListExtra("exercisesList");
    }

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_task_detail, null, false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    @Override
    protected void initLayout() {
        initFragments();
        changeTabContentFragment();
    }

    @Override
    protected void loadData() {
        super.loadData();
    }

    @OnClick ({R.id.tab_task_content ,R.id.tab_task_material, R.id.tab_task_exercise
            , R.id.imageView_back, R.id.bottom_confirm_btn})
    public void onClick (View view) {
        switch (view.getId()) {
            case R.id.tab_task_content:
                currentSelectTab = TAB_CONTENT;
                break;
            case R.id.tab_task_material:
                currentSelectTab = TAB_MATERIALS;
                break;
            case R.id.tab_task_exercise:
                currentSelectTab = TAB_EXERCISES;
                break;
            case R.id.imageView_back:
                finish();
                return;
            case R.id.bottom_confirm_btn:
                //提交任务
                commitTask();
                return;
        }
        changeTabContentFragment();
    }

    @Override
    protected void refreshView() {

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fragmentTaskContent = null;
        fragmentTaskMaterials = null;
        fragmentTaskExercise = null;
    }

    private void initFragments () {
        fragmentTaskContent = new FragmentTaskContent();
        fragmentTaskMaterials = new FragmentTaskMaterials();
        fragmentTaskExercise = new FragmentTaskExercise();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.task_detail_container, fragmentTaskContent);
        fragmentTransaction.add(R.id.task_detail_container, fragmentTaskMaterials);
        fragmentTransaction.add(R.id.task_detail_container, fragmentTaskExercise);
        fragmentTransaction.commit();
    }

    private void changeTabContentFragment () {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (currentSelectTab) {
            case TAB_CONTENT:
                fragmentTransaction.hide(fragmentTaskExercise);
                fragmentTransaction.hide(fragmentTaskMaterials);
                fragmentTransaction.show(fragmentTaskContent);
//                binding.lineContent.setBackgroundColor(Color.GREEN);
//                binding.lineMaterial.setBackgroundColor(Color.TRANSPARENT);
//                binding.lineExercise.setBackgroundColor(Color.TRANSPARENT);
                binding.lineContent.setVisibility(View.VISIBLE);
                binding.lineMaterial.setVisibility(View.GONE);
                binding.lineExercise.setVisibility(View.GONE);
                break;
            case TAB_MATERIALS:
                fragmentTransaction.hide(fragmentTaskContent);
                fragmentTransaction.hide(fragmentTaskExercise);
                fragmentTransaction.show(fragmentTaskMaterials);
//                binding.lineContent.setBackgroundColor(Color.TRANSPARENT);
//                binding.lineMaterial.setBackgroundColor(Color.GREEN);
//                binding.lineExercise.setBackgroundColor(Color.TRANSPARENT);
                binding.lineContent.setVisibility(View.GONE);
                binding.lineMaterial.setVisibility(View.VISIBLE);
                binding.lineExercise.setVisibility(View.GONE);
                break;
            case TAB_EXERCISES:
                fragmentTransaction.hide(fragmentTaskContent);
                fragmentTransaction.hide(fragmentTaskMaterials);
                fragmentTransaction.show(fragmentTaskExercise);
//                binding.lineContent.setBackgroundColor(Color.TRANSPARENT);
//                binding.lineMaterial.setBackgroundColor(Color.TRANSPARENT);
//                binding.lineExercise.setBackgroundColor(Color.GREEN);
                binding.lineContent.setVisibility(View.GONE);
                binding.lineMaterial.setVisibility(View.GONE);
                binding.lineExercise.setVisibility(View.VISIBLE);
                break;
        }
        fragmentTransaction.commit();
    }

    /**
     * 提交任务  提交家长签字
     */
    private void commitTask() {
        loadIntent(SignActivity.class);
    }

    public TaskContentBean getTaskContentBean () {
        return taskContentBean;
    }

    public List<StudyDataBean> getStudyDataBeanList () {
        return studyDataBeanList;
    }

    public List<ExercisesBean> getExercisesBeanList() {
        return exercisesBeanList;
    }
}
