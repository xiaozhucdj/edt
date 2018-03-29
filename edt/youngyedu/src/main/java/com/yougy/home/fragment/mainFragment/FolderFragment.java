package com.yougy.home.fragment.mainFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yougy.anwser.AnswerBookStructureActivity;
import com.yougy.anwser.CourseInfo;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.fragment.BFragment;
import com.yougy.common.global.FileContonst;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.home.adapter.CoursekAdapter;
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
public class FolderFragment extends BFragment implements View.OnClickListener {
    /**
     * 适配器 数据
     */
    private List<HomeworkBookSummary> mCourseInfos = new ArrayList<>();
    private List<HomeworkBookSummary> mCountCourses = new ArrayList<>();
    /***
     * 一页数据个数
     */
    private static final int COUNT_PER_PAGE = FileContonst.PAGE_COUNTS;
    /***
     * 当前翻页的角标
     */
    private int mPagerIndex;
    private ViewGroup mRootView;
    private RecyclerView mRecyclerView;
    private HomeworkAdapter mCourseAdapter;
    private boolean mIsFist;
    private LinearLayout mLlPager;
    private ViewGroup mLoadingNull;
    private TextView tvErrMsg;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_book, null);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_View);
//        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(UIUtils.getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.adaper_divider_img_normal));
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
        mLlPager = (LinearLayout) mRootView.findViewById(R.id.ll_page);
        mLoadingNull = (ViewGroup) mRootView.findViewById(R.id.loading_null);

        tvErrMsg = (TextView) mRootView.findViewById(R.id.tv_errMsg);

        return mRootView;
    }

    /**
     * 点击事件
     */
    private void itemClick(int position) {
        HomeworkBookSummary info = mCourseInfos.get(position);
        Intent intent = new Intent(getActivity() , AnswerBookStructureActivity.class);
        intent.putExtra("bookName" , info.getCourseBookTitle());
        intent.putExtra("bookId" , info.getCourseBookId());
        intent.putExtra("homeworkId" , info.getHomeworkId());
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

    @Override
    public void onClick(View v) {
        refreshAdapterData(v);
    }

    /***
     * 刷新适配器数据
     */
    private void refreshAdapterData(View v) {

        if ((int) v.getTag() == mPagerIndex) {
            return;
        }

        //还原上个按钮状态
        mLlPager.getChildAt(mPagerIndex - 1).setSelected(false);
        mPagerIndex = (int) v.getTag();
        //设置当前按钮状态
        mLlPager.getChildAt(mPagerIndex - 1).setSelected(true);

        //设置page页数数据
        mCourseInfos.clear();

        if ((mPagerIndex - 1) * COUNT_PER_PAGE + COUNT_PER_PAGE > mCountCourses.size()) { // 不是 正数被
            mCourseInfos.addAll(mCountCourses.subList((mPagerIndex - 1) * COUNT_PER_PAGE, mCountCourses.size()));
        } else {
            mCourseInfos.addAll(mCountCourses.subList((mPagerIndex - 1) * COUNT_PER_PAGE, (mPagerIndex - 1) * COUNT_PER_PAGE + COUNT_PER_PAGE)); //正数被
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
        mLlPager.removeAllViews();
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
        for (int index = 1; index <= counts; index++) {
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.leftMargin = 20;
//            LogUtils.e(TAG, "getActivity is null ? " + (getActivity() == null));
//            View pageLayout = View.inflate(getActivity(), R.layout.page_item, null);
//            final Button pageBtn = (Button) pageLayout.findViewById(R.id.page_btn);
            TextView pageBtn = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.new_page_item, mLlPager, false);
            if (index == 1) {
                mPagerIndex = 1;
                pageBtn.setSelected(true);
            }
            pageBtn.setTag(index);
            pageBtn.setText(Integer.toString(index));
            pageBtn.setOnClickListener(this);
            mLlPager.addView(pageBtn);
        }
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
