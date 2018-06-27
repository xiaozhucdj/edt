package com.yougy.home.fragment.mainFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frank.etude.pageBtnBar.PageBtnBar;
import com.frank.etude.pageBtnBar.PageBtnBarAdapter;
import com.yougy.anwser.AnswerBookStructureActivity;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.fragment.BFragment;
import com.yougy.common.global.FileContonst;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.home.adapter.HomeworkAdapter;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.homework.bean.HomeworkBookSummary;
import com.yougy.ui.activity.R;
import com.yougy.view.CustomGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;


/**
 * Created by Administrator on 2016/7/12.
 * 文件夹
 */
public class FolderFragment extends BFragment {
    /**
     * 适配器 数据
     */
    private List<HomeworkBookSummary> mCourseInfos = new ArrayList<>();
    private List<HomeworkBookSummary> mCountCourses = new ArrayList<>();
    /***
     * 一页数据个数
     */
    private static final int COUNT_PER_PAGE = FileContonst.PAGE_COUNTS;
    private ViewGroup mRootView;
    private RecyclerView mRecyclerView;
    private HomeworkAdapter mCourseAdapter;
    private boolean mIsFist;
    private ViewGroup mLoadingNull;
    private TextView tvErrMsg;
    private PageBtnBar mPageBtnBar;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_book, null);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_View);
//        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(UIUtils.getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.adaper_divider_img_normal));
        mRecyclerView.addItemDecoration(divider);


        CustomGridLayoutManager layout = new CustomGridLayoutManager(getActivity(), FileContonst.PAGE_LINES);
        layout.setScrollEnabled(false);
        mRecyclerView.setLayoutManager(layout);

        mCourseAdapter = new HomeworkAdapter(mCourseInfos);
        mRecyclerView.setAdapter(mCourseAdapter);
        mCourseAdapter.notifyDataSetChanged();

        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                itemClick(vh.getAdapterPosition());
            }
        });
        mCourseAdapter.notifyDataSetChanged();
        mLoadingNull = (ViewGroup) mRootView.findViewById(R.id.loading_null);
        tvErrMsg = (TextView) mRootView.findViewById(R.id.tv_errMsg);
        mPageBtnBar = (PageBtnBar) mRootView.findViewById(R.id.btn_bar);
        return mRootView;
    }

    /**
     * 点击事件
     */
    private void itemClick(int position) {
        HomeworkBookSummary info = mCourseInfos.get(position);

        if ( info.getCourseBookId() == -1 || info.getCourseBookId() == 0){
            ToastUtil.showCustomToast(getActivity() , "该学科还没有教材");
            return;
        }
        Intent intent = new Intent(getActivity(), AnswerBookStructureActivity.class);
        intent.putExtra("bookName", info.getCourseBookTitle());
        intent.putExtra("bookId", info.getCourseBookId());
        intent.putExtra("homeworkId", info.getHomeworkId());
        startActivity(intent);
    }

    @Override
    protected void handleEvent() {
        super.handleEvent();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mIsFist = true;
    }

    private void freshUI(List<HomeworkBookSummary> beans) {
        if (beans != null && beans.size() > 0) {
            mLoadingNull.setVisibility(View.GONE);
            mCountCourses.clear();
            mCountCourses.addAll(beans);
            initPages();
        } else {
            // 数据返回为null
            mLoadingNull.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (mIsFist && !hidden && mCountCourses.size() == 0) {
            loadData();
        }
    }

    private void loadData() {
        if (NetUtils.isNetConnected()) {
            NetWorkManager.queryHomeworkBookList(SpUtils.getUserId() + "", SpUtils.getGradeName())
                    .subscribe(new Action1<List<HomeworkBookSummary>>() {
                        @Override
                        public void call(List<HomeworkBookSummary> homeworkBookSummaries) {
                            freshUI(homeworkBookSummaries);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            ToastUtil.showCustomToast(getContext(), "获取作业本数据失败,请点击刷新重新获取");
                            throwable.printStackTrace();
                        }
                    });
        } else {
            showCancelAndDetermineDialog(R.string.jump_to_net);
        }
    }

    public void loadIntentWithExtras(Class<? extends Activity> cls, Bundle extras) {
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtras(extras);
        startActivity(intent);
    }


    /***
     * 刷新适配器数据
     */
    private void refreshAdapterData(int pagerIndex) {
        //设置page页数数据
        mCourseInfos.clear();
        if ((pagerIndex - 1) * COUNT_PER_PAGE + COUNT_PER_PAGE > mCountCourses.size()) { // 不是 正数被
            mCourseInfos.addAll(mCountCourses.subList((pagerIndex - 1) * COUNT_PER_PAGE, mCountCourses.size()));
        } else {
            mCourseInfos.addAll(mCountCourses.subList((pagerIndex - 1) * COUNT_PER_PAGE, (pagerIndex - 1) * COUNT_PER_PAGE + COUNT_PER_PAGE)); //正数被
        }
        mCourseAdapter.notifyDataSetChanged();
    }


    /**
     * 初始化翻页角标
     */
    private void initPages() {
        int counts = 0;
        int quotient = mCountCourses.size() / COUNT_PER_PAGE;
        int remainder = mCountCourses.size() % COUNT_PER_PAGE;
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
        addBtnCounts(counts);
        mCourseInfos.clear();
        if (mCountCourses.size() > COUNT_PER_PAGE) { // 大于1页
            mCourseInfos.addAll(mCountCourses.subList(0, COUNT_PER_PAGE));
        } else {
            LogUtils.i("initPages2.."); //小于1页
            mCourseInfos.addAll(mCountCourses.subList(0, mCountCourses.size()));
        }
        mCourseAdapter.notifyDataSetChanged();
    }

    /***
     * 添加按钮
     *
     * @param counts
     */
    private void addBtnCounts(int counts) {

        mPageBtnBar.setPageBarAdapter(new PageBtnBarAdapter(getContext()) {
            @Override
            public int getPageBtnCount() {
                return counts;
            }

            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn) {
/*                contentDisplayer.getContentAdaper().setSubText(parseSubText(questionItemList.get(btnIndex)));
                contentDisplayer.getContentAdaper().toPage("question" , btnIndex , true);*/

                refreshAdapterData(btnIndex + 1);
            }
        });
        mPageBtnBar.setCurrentSelectPageIndex(0);
        mPageBtnBar.refreshPageBar();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerView = null;
        if (mCourseAdapter != null) {
            mCourseAdapter = null;
        }
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        if (event.getType().equalsIgnoreCase(EventBusConstant.answer_event)) {
            LogUtils.i("type .." + EventBusConstant.answer_event);
            loadData();
        }
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
}
