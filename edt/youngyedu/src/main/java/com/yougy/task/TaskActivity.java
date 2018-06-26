package com.yougy.task;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.alibaba.sdk.android.oss.OSS;
import com.yougy.anwser.STSbean;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.task.adapter.TaskAdapter;
import com.yougy.task.bean.ExercisesBean;
import com.yougy.task.bean.StudyDataBean;
import com.yougy.task.bean.TaskBean;
import com.yougy.task.bean.TaskContentBean;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityTaskBinding;
import com.yougy.view.CustomLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author: zhang yc
 * @create date: 2018/6/19 10:24
 * @class desc:
 * @modifier: 
 * @modify date: 2018/6/19 10:24
 * @modify desc: 
 */
public class TaskActivity  extends TaskBaseActivity{

    public ActivityTaskBinding binding;

    private int currentSelectTab = 0;  // 0 未完成  1 已完成
    public static final int UNFINISHED_TASK = 0;
    public static final int FINISHED_TASK = 1;

    public static final int COUNT_PER_PAGE = 5;
    private ArrayList<TaskBean> unFinishedList = new ArrayList<>();
    private ArrayList<TaskBean> currentUnFinishedList = new ArrayList<>();
    private ArrayList<TaskBean> finishedList = new ArrayList<>();
    private ArrayList<TaskBean> currentFinishedList = new ArrayList<>();

    private TaskAdapter mTaskAdapter;

    @Override
    protected void onHandleMessage(Message msg) {
        super.onHandleMessage(msg);
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
    }

    @Override
    protected void yxMsgObserverCall(Object o) {

    }

    @OnClick({R.id.text_unfinished_task, R.id.text_finished_task, R.id.imageView_back})
    public void onClick (View view) {
        if (Utils.isFastClick()){
            return;
        }
        switch (view.getId()) {
            case R.id.text_unfinished_task:
                currentSelectTab = UNFINISHED_TASK;
                changeTabState();
                changeDataType(currentSelectTab);
                break;
            case R.id.text_finished_task:
                currentSelectTab = FINISHED_TASK;
                changeTabState();
                changeDataType(currentSelectTab);
                break;
            case R.id.imageView_back:
                finish();
                break;
        }
    }

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_task, null, false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initLayout() {
        initRecyclerView();
        changeTabState();
    }

    @Override
    protected void loadData() {
        super.loadData();
//        changeDataType(currentSelectTab);
        initBtnBarPages(tempAddTestData());
    }

    private List<TaskBean>  tempAddTestData() {
        List<TaskBean> tmpList = new ArrayList<>();
        List<StudyDataBean> studyDataBeanList = new ArrayList<>();
        StudyDataBean studyDataBean = new StudyDataBean("学习资料1",1,"http://www.baidu.com", 100001);
        studyDataBeanList.add(studyDataBean);
        List<ExercisesBean> exercisesBeanList = new ArrayList<>();
        List<String> anwers = new ArrayList<>();
        ExercisesBean exercisesBean = new ExercisesBean(anwers,101, 30, 2, 10, 10, 10, 10);
        exercisesBeanList.add(exercisesBean);
        TaskContentBean taskContentBean = new TaskContentBean(1001,10,10,2, 10,"task 内容测试");
        int max ;
        if (currentSelectTab == UNFINISHED_TASK) {
            max = 33;
        } else {
            max = 69;
        }
        for (int i = 0 ; i< max ; i++) {
            if (currentSelectTab == UNFINISHED_TASK) {
                tmpList.add(new TaskBean("第十章","2018年5月20日", "2018年6月25日", 0, 1, 3,
                        studyDataBeanList, 1, taskContentBean, 3, exercisesBeanList, 2001, "task title 课后题练习 :" + i));
            } else {
                tmpList.add(new TaskBean("第三章","2018年6月20日", "2018年6月25日", 0, 1, 3,
                        studyDataBeanList, 1, taskContentBean, 3, exercisesBeanList, 2001, "task title 课前预习题 :" + i));
            }
        }
        return tmpList;
    }

    @Override
    protected void refreshView() {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d("zhangyc TaskActivity onResume...");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void itemClick (RecyclerView.ViewHolder vh) {
        TaskBean taskBean = unFinishedList.get(vh.getAdapterPosition());
        Intent intent = new Intent(this, TaskDetailActivity.class) ;
        intent.putExtra("taskBean", taskBean);
        intent.putExtra("taskContentBean", taskBean.getTaskContentBean());
        intent.putExtra("taskBean", taskBean);
        intent.putParcelableArrayListExtra("studyDataList", (ArrayList<? extends Parcelable>) taskBean.getStudyDataList());
        intent.putParcelableArrayListExtra("exercisesList", (ArrayList<? extends Parcelable>) taskBean.getTaskExercisesList());

        List<StudyDataBean> studyDataBeanList = intent.getParcelableArrayListExtra("studyDataList");
        List<ExercisesBean> exercisesBeanList = intent.getParcelableArrayListExtra("exercisesList");
        LogUtils.d("taskContentBean studyDataBeanList:" + studyDataBeanList.size());
        LogUtils.d("taskContentBean exercisesBeanList:" + exercisesBeanList.size());

        startActivity(intent);
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.adaper_divider_img_normal));
        binding.recyclerViewTaskAct.addItemDecoration(divider);
        CustomLinearLayoutManager layout = new CustomLinearLayoutManager(this);
        layout.setScrollHorizontalEnabled(false);
        binding.recyclerViewTaskAct.setLayoutManager(layout);
        mTaskAdapter = new TaskAdapter(this);
        binding.recyclerViewTaskAct.setAdapter(mTaskAdapter);
        binding.recyclerViewTaskAct.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.recyclerViewTaskAct) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                itemClick(vh);
            }
        });
    }

    /**
     *  数据请求成功
     * @param taskBeanList
     */
    private void initBtnBarPages (List<TaskBean> taskBeanList) {
        int count = getBtnBarCounts(taskBeanList.size(), COUNT_PER_PAGE);
        LogUtils.d("zhangyc count" + count);
        addBtnBarCounts(binding.btnBarTaskAct, count);
        LogUtils.d("zhangyc getCurrentSelectPageIndex:" + binding.btnBarTaskAct.getCurrentSelectPageIndex());

        switch (currentSelectTab) {
            case UNFINISHED_TASK:
                currentUnFinishedList.clear();
                unFinishedList.clear();
                unFinishedList.addAll(taskBeanList);
                if (taskBeanList.size() > COUNT_PER_PAGE) { // 大于1页
                    currentUnFinishedList.addAll(taskBeanList.subList(0, COUNT_PER_PAGE));
                } else {
                   //小于1页
                    currentUnFinishedList.addAll(taskBeanList.subList(0, taskBeanList.size()));
                }
                mTaskAdapter.setData(currentUnFinishedList);
                break;
            case FINISHED_TASK:
                currentFinishedList.clear();
                finishedList.clear();
                finishedList.addAll(taskBeanList);
                if (taskBeanList.size() > COUNT_PER_PAGE) { // 大于1页
                    currentFinishedList.addAll(taskBeanList.subList(0, COUNT_PER_PAGE));
                } else {
                    //小于1页
                    currentFinishedList.addAll(taskBeanList.subList(0, taskBeanList.size()));
                }
                mTaskAdapter.setData(currentFinishedList);
                break;
        }
    }

    @Override
    protected void refreshAdapterData(int pagerIndex) {
        super.refreshAdapterData(pagerIndex);
        LogUtils.d("zhangyc pagerIndex: " + pagerIndex);
        if (currentSelectTab == UNFINISHED_TASK) {
            currentUnFinishedList.clear();
            if (COUNT_PER_PAGE * pagerIndex > unFinishedList.size()) {
                currentUnFinishedList.addAll(unFinishedList.subList((pagerIndex-1) * COUNT_PER_PAGE, unFinishedList.size()));
            } else {
                currentUnFinishedList.addAll(unFinishedList.subList((pagerIndex-1) * COUNT_PER_PAGE, COUNT_PER_PAGE * pagerIndex));
            }
            mTaskAdapter.setData(currentUnFinishedList);
        } else {
            currentFinishedList.clear();
            if (COUNT_PER_PAGE * pagerIndex > finishedList.size()) {
                currentFinishedList.addAll(finishedList.subList((pagerIndex-1) * COUNT_PER_PAGE, finishedList.size()));
            } else {
                currentFinishedList.addAll(finishedList.subList((pagerIndex-1) * COUNT_PER_PAGE, COUNT_PER_PAGE * pagerIndex));
            }
            mTaskAdapter.setData(currentFinishedList);
        }
    }

    private void changeTabState () {
        switch (currentSelectTab) {
            case UNFINISHED_TASK:
                binding.textUnfinishedTask.setTextColor(Color.WHITE);
                binding.textUnfinishedTask.setBackgroundColor(Color.BLACK);
                binding.textFinishedTask.setTextColor(Color.BLACK);
                binding.textFinishedTask.setBackgroundColor(Color.WHITE);
                break;
            case FINISHED_TASK:
                binding.textUnfinishedTask.setTextColor(Color.BLACK);
                binding.textUnfinishedTask.setBackgroundColor(Color.WHITE);
                binding.textFinishedTask.setTextColor(Color.WHITE);
                binding.textFinishedTask.setBackgroundColor(Color.BLACK);
                break;
        }
    }

    private void changeDataType (int selectTab) {
        switch (selectTab) {
            case UNFINISHED_TASK:
                if (unFinishedList.size() <= 0) {
                    //网络请求数据
                    unFinishedList.addAll(tempAddTestData());
                }
                List<TaskBean> tmpList = new ArrayList<>();
                tmpList.addAll(unFinishedList);
                initBtnBarPages(tmpList);
                break;
            case FINISHED_TASK:
                if (finishedList.size() <= 0) {
                    //网络请求数据
                    finishedList.addAll(tempAddTestData());
                }
                List<TaskBean> tmpList2= new ArrayList<>();
                tmpList2.addAll(finishedList);
                initBtnBarPages(tmpList2);
                break;
        }
    }

    private  void uploading (STSbean stSbean) {
        OSS aliOOS = Utils.getAliOOS(stSbean);

        rx.Observable.create(new rx.Observable.OnSubscribe<Object>() {

            @Override
            public void call(Subscriber<? super Object> subscriber) {

            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        LogUtils.e(tag + "--uploading--onCompleted." );
                        sendUploadInfoToServer();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(tag + "--uploading--"  + e.getMessage().toString());
                    }

                    @Override
                    public void onNext(Object o) {
                        LogUtils.e(tag + "--uploading--onNext." );
                    }
                });
    }

    private void sendUploadInfoToServer () {

    }

    public List<TaskBean> getUnFinTaskBeanList () {
        return unFinishedList;
    }

    public List<TaskBean> getFinTaskBeanList() {
        return finishedList;
    }

}
