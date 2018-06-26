package com.yougy.home.fragment.mainFragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frank.etude.pageBtnBar.PageBtnBarAdapter;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.fragment.BFragment;
import com.yougy.common.global.FileContonst;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.homework.bean.HomeworkBookSummary;
import com.yougy.task.TaskActivity;
import com.yougy.task.bean.TaskBean;
import com.yougy.task.UIState;
import com.yougy.task.adapter.TaskSubjectAdapter;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.FragmentTaskBinding;
import com.yougy.view.CustomGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by lenovo on 2018/6/20.
 */

public class TaskFragment extends BFragment{

    private static final int COUNT_PER_PAGE = 9;
    FragmentTaskBinding binding;

    private TaskSubjectAdapter taskAdapter;

    private ArrayList<TaskBean> taskBeanList = new ArrayList<>();
    private ArrayList<TaskBean> currentTaskBeanList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_task, null, false);
        UIUtils.recursiveAuto(binding.getRoot());
        initRecyclerView();

        return binding.getRoot();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && taskBeanList.size() == 0) {
            loadData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);

    }

    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        Intent intent = new Intent("android.intent.action.WIFI_ENABLE");
        startActivity(intent);
        dissMissUiPromptDialog();
    }

    @Override
    public void onUiCancelListener() {
        super.onUiCancelListener();
        dissMissUiPromptDialog();
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.adaper_divider_img_normal));
        binding.recyclerViewTask.addItemDecoration(divider);
        CustomGridLayoutManager layout = new CustomGridLayoutManager(getActivity(), FileContonst.PAGE_LINES);
        layout.setScrollEnabled(false);
        binding.recyclerViewTask.setLayoutManager(layout);
        taskAdapter = new TaskSubjectAdapter();
        binding.recyclerViewTask.setAdapter(taskAdapter);
        binding.recyclerViewTask.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.recyclerViewTask) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                itemClick(vh);
            }
        });
    }

    /**
     *  RecyclerView 点击事件
     * @param vh
     */
    private void itemClick (RecyclerView.ViewHolder vh) {
        //跳转任务列表界面
        Intent intent = new Intent(getActivity(), TaskActivity.class);
        startActivity(intent);
    }

    /**
     *   加载任务数据
     */
    private void loadData () {
        taskBeanList.clear();
        judgeCurrentShowState(UIState.UI_LOADING);
        if (NetUtils.isNetConnected()) {
            NetWorkManager.queryHomeworkBookList(SpUtils.getUserId() + "", SpUtils.getGradeName())
                    .subscribe(new Action1<List<HomeworkBookSummary>>() {
                        @Override
                        public void call(List<HomeworkBookSummary> homeworkBookInfos) {
//                            freshUI(homeworkBookInfos);
                            initPages();
                            judgeCurrentShowState(UIState.UI_SHOW);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            ToastUtil.showCustomToast(getContext(), "获取任务数据失败,请点击刷新重新获取");
                            throwable.printStackTrace();
                        }
                    });
        } else {
            showCancelAndDetermineDialog(R.string.jump_to_net);
        }
    }

    /**
     * 根据页面当前状态，判断显示隐藏UI
     * @param state
     */
    private void judgeCurrentShowState (UIState state) {
        LogUtils.d("taskLog judgeCurrentShowState state: " + state);
        switch (state) {
            case UI_EMPTY:
                currentUiState(View.GONE,View.GONE,View.VISIBLE);
                break;
            case UI_LOADING:
                currentUiState(View.GONE,View.GONE,View.GONE);
                break;
            case UI_SHOW:
                currentUiState(View.VISIBLE,View.VISIBLE,View.GONE);
                break;
        }
    }

    /**
     *  设置当前显示UI
     * @param btnBarState
     * @param recyclerState
     * @param viewStubState
     */
    private void currentUiState (int btnBarState, int recyclerState, int viewStubState) {
        binding.btnBarTask.setVisibility(btnBarState);
        binding.recyclerViewTask.setVisibility(recyclerState);
        binding.viewStubTaskFrag.getViewStub().setVisibility(viewStubState);
    }



    /**
     * 初始化翻页角标
     */
    private void initPages() {
        int counts = 0;
        int quotient = taskBeanList.size() / COUNT_PER_PAGE;
        int remainder = taskBeanList.size() % COUNT_PER_PAGE;
        if (quotient == 0) {
            if (remainder == 0) {
                //没有数据
                counts = 0;
            } else {
                //不足16个item
                counts = 1;
            }
        }
        if (quotient != 0) {
            if (remainder == 0) {
                //没有数据
                counts = quotient; //.正好是16的倍数
            } else {
                //不足16个item
                counts = quotient + 1; // 不足16个 +1
            }
        }
        //删除之前的按钮
        //设置显示按钮
        addBtnBarCounts(counts);
        currentTaskBeanList.clear();
        if (taskBeanList.size() > COUNT_PER_PAGE) { // 大于1页
            currentTaskBeanList.addAll(taskBeanList.subList(0, COUNT_PER_PAGE));
        } else {
            LogUtils.i("initPages2.."); //小于1页
            currentTaskBeanList.addAll(taskBeanList.subList(0, taskBeanList.size()));
        }
        taskAdapter.notifyDataSetChanged();
    }

    /**
     * @param counts  下面的Btn  page
     */
    private void addBtnBarCounts (int counts) {
        binding.btnBarTask.setPageBarAdapter(new PageBtnBarAdapter(getContext()) {
            @Override
            public int getPageBtnCount() {
                return counts;
            }

            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn) {
                refreshAdapterData(btnIndex+1);
            }
        });
        binding.btnBarTask.setCurrentSelectPageIndex(0);
        binding.btnBarTask.refreshPageBar();
    }


    /***
     * 刷新适配器数据
     */
    private void refreshAdapterData(int pagerIndex) {
        //设置page页数数据
        currentTaskBeanList.clear();
        if ((pagerIndex - 1) * COUNT_PER_PAGE + COUNT_PER_PAGE > taskBeanList.size()) { // 不是 正数倍
            currentTaskBeanList.addAll(taskBeanList.subList((pagerIndex - 1) * COUNT_PER_PAGE, taskBeanList.size()));
        } else {
            currentTaskBeanList.addAll(taskBeanList.subList((pagerIndex - 1) * COUNT_PER_PAGE, (pagerIndex - 1) * COUNT_PER_PAGE + COUNT_PER_PAGE)); //正数被
        }
        taskAdapter.notifyDataSetChanged();
    }
}
