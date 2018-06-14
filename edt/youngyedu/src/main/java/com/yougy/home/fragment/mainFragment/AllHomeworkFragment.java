package com.yougy.home.fragment.mainFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.frank.etude.pageBtnBar.PageBtnBar;
import com.frank.etude.pageBtnBar.PageBtnBarAdapter;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.fragment.BFragment;
import com.yougy.common.global.FileContonst;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.activity.ControlFragmentActivity;
import com.yougy.home.adapter.AllHomeworkAdapter;
import com.yougy.home.adapter.FitGradeAdapter;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.home.adapter.SubjectAdapter;
import com.yougy.home.bean.BookCategory;
import com.yougy.homework.bean.HomeworkBookSummary;
import com.yougy.ui.activity.R;
import com.yougy.view.CustomGridLayoutManager;
import com.yougy.view.DividerGridItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import rx.functions.Action1;


/**
 * Created by Administrator on 2016/7/12.
 * 全部作业 fragment_homework
 */
public class AllHomeworkFragment extends BFragment implements View.OnClickListener {

    //////////////////////////////////////集合数据/////////////////////////////////////////////////////
    /**
     * 适配器 数据
     */
    private List<HomeworkBookSummary> mBooks = new ArrayList<>();
    /**
     * 当前按照（年级，和课本类别分类后的数据总集合）
     */
    private List<HomeworkBookSummary> mCountBooks = new ArrayList<>();

    /**
     * 服务器返回数据
     */
    private List<HomeworkBookSummary> mServerBooks = new ArrayList<>();

//
    /**
     * 年级分类码 notifyDataSetChanged之前 Collections.sort(mBookFitGrade); 进行排序
     */
    private List<BookCategory> mBookFitGrade = new ArrayList<>();
    /**
     * 学科分类码 notifyDataSetChanged 之前 Collections.sort(mBookSubject); 进行排序
     */
    private List<BookCategory> mBookSubject = new ArrayList<>();

    /***
     * 年级分类码  遍历作用
     */
    private TreeSet<BookCategory> mTreeFitGrade = new TreeSet<>();
    /***
     * 学科分类码 遍历作用
     */
    private TreeSet<BookCategory> mTreeSubject = new TreeSet<>();


    /***
     * 切换学科的 切换角标
     */
    private int mSubjectIndex = -1;
    /***
     * 一页数据个数
     */
    private static final int COUNT_PER_PAGE = FileContonst.SMALL_PAGE_COUNTS;

    //////////////////////////////////////View/////////////////////////////////////////////////////

    /***
     * 切换年级 切换角标
     */
    private int mFitGradeIndex = -1;

    private ViewGroup mRootView;
    private boolean mIsFist;
    private TextView mBookItemTile;
    private RecyclerView mFitGradeView;
    private RecyclerView mSubjectView;
    private RecyclerView mBookView;
    private SubjectAdapter mSubjectAdapter;
    private FitGradeAdapter mFitGradeAdapter;
    private TextView mSubMore;
    private TextView mGradeMore;

    private ViewGroup mGroupSub;
    private ViewGroup mGroupGrade;
    //    private Subscription mSub;
    private ViewGroup mLoadingNull;
    private boolean mIsPackUp;
    private LinearLayout llTerm;
    private AllHomeworkAdapter mHomeworkAdapter;
    private PageBtnBar mPageBtnBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_all_book, null);
        mBookItemTile = (TextView) mRootView.findViewById(R.id.tv_bookItemTile);
        initFitGradeAdapter();
        initSubjectAdapter();
        initBookAdapter();

        mSubMore = (TextView) mRootView.findViewById(R.id.tv_subjectMore);

        mGradeMore = (TextView) mRootView.findViewById(R.id.tv_gradeMore);
//        mGradeMore.setTag(0);
        mGradeMore.setOnClickListener(this);

        mGroupSub = (ViewGroup) mRootView.findViewById(R.id.rl_subject);
        mGroupGrade = (ViewGroup) mRootView.findViewById(R.id.rl_grade);
        mLoadingNull = (ViewGroup) mRootView.findViewById(R.id.loading_null);
        llTerm = (LinearLayout) mRootView.findViewById(R.id.ll_term);
        mPageBtnBar = (PageBtnBar) mRootView.findViewById(R.id.btn_bar);
        return mRootView;
    }


    /***
     * 初始化年级
     */
    private void initFitGradeAdapter() {
        mFitGradeView = (RecyclerView) mRootView.findViewById(R.id.recycler_fitGrade);
        mFitGradeView.addItemDecoration(new DividerGridItemDecoration(UIUtils.getContext()));
        CustomGridLayoutManager layout = new CustomGridLayoutManager(getActivity(), 4);
        layout.setScrollEnabled(false);
        mFitGradeView.setLayoutManager(layout);
        mFitGradeAdapter = new FitGradeAdapter(mBookFitGrade);
        mFitGradeView.setAdapter(mFitGradeAdapter);
        mFitGradeView.addOnItemTouchListener(new OnRecyclerItemClickListener(mFitGradeView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                fitItemClick(vh.getAdapterPosition());
            }
        });
        mFitGradeAdapter.notifyDataSetChanged();
    }

    private void fitItemClick(int position) {
        mIsPackUp = true;
        setLlTermSize();
        LogUtils.i("position.....onClickGradeListener..." + position);
        //多次重复点击按钮
        if (position == mFitGradeIndex) {
            return;
        }
        if (mFitGradeIndex == -1) { //第一次
            for (BookCategory bookCategory : mBookFitGrade) {
                bookCategory.setSelect(false);
            }
        } else { //把之前选择的item状态恢复
            mBookFitGrade.get(mFitGradeIndex).setSelect(false);
        }

        //点击，年级后要把学科状态切换

        if (mSubjectIndex != -1 && mBookSubject.get(mSubjectIndex).isSelect()) {
            mBookSubject.get(mSubjectIndex).setSelect(false);
            //重新设置角标
            mSubjectIndex = -1;
            mSubjectAdapter.notifyDataSetChanged();
        }
        //替换position
        mFitGradeIndex = position;

        //替换位置
        mBookFitGrade.get(position).setSelect(true);
        mBookItemTile.setText(mBookFitGrade.get(position).getCategoryName() + "作业");
        mFitGradeAdapter.notifyDataSetChanged();
        //根据条件设置显示数据
        refreshAdapterData(mBookFitGrade.get(position).getCategoryName(), true);
        LogUtils.i("position....onClickGradeListener....last");
    }

    /**
     * 初始化科目
     */
    private void initSubjectAdapter() {

        mSubjectView = (RecyclerView) mRootView.findViewById(R.id.recycler_subject);
        mSubjectView.addItemDecoration(new DividerGridItemDecoration(UIUtils.getContext()));
        CustomGridLayoutManager layout = new CustomGridLayoutManager(getActivity(), 4);
        layout.setScrollEnabled(false);
        mSubjectView.setLayoutManager(layout);

        mSubjectAdapter = new SubjectAdapter(mBookSubject);
        mSubjectView.setAdapter(mSubjectAdapter);
        mSubjectView.addOnItemTouchListener(new OnRecyclerItemClickListener(mSubjectView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                subjectItemClick(vh.getAdapterPosition());
            }
        });
        mSubjectAdapter.notifyDataSetChanged();
    }

    private void subjectItemClick(int position) {
        mIsPackUp = true;
        setLlTermSize();
        LogUtils.i("position.....onClickSubListener..." + position);
        //多次重复点击按钮
        if (position == mSubjectIndex) {
            return;
        }
        if (mSubjectIndex == -1) { //第一次
            for (BookCategory bookCategory : mBookSubject) {
                bookCategory.setSelect(false);
            }
        } else { //把之前选择的item状态恢复
            mBookSubject.get(mSubjectIndex).setSelect(false);
        }


        //点击，年级后要把学科状态切换
        if (mFitGradeIndex != -1 && mBookFitGrade.get(mFitGradeIndex).isSelect()) {
            mBookFitGrade.get(mFitGradeIndex).setSelect(false);
            mFitGradeIndex = -1;
            mFitGradeAdapter.notifyDataSetChanged();
        } else {
            for (BookCategory bookCategory : mBookFitGrade) {
                bookCategory.setSelect(false);
            }
            mFitGradeAdapter.notifyDataSetChanged();
        }
        //替换position
        mSubjectIndex = position;
        //替换位置
        mBookSubject.get(position).setSelect(true);
        mBookItemTile.setText(mBookSubject.get(position).getCategoryName() + "作业");
        mSubjectAdapter.notifyDataSetChanged();
        //根据条件设置显示数据
        refreshAdapterData(mBookSubject.get(position).getCategoryName(), false);
        LogUtils.i("position.....onClickSubListener...last");
    }

    /***
     * 初始化课本
     */
    private void initBookAdapter() {
        mBookView = (RecyclerView) mRootView.findViewById(R.id.recycler_books);
        mBookView.addItemDecoration(new DividerGridItemDecoration(UIUtils.getContext()));
        CustomGridLayoutManager layout = new CustomGridLayoutManager(getActivity(),FileContonst.SMALL_PAGE_LINES);
        layout.setScrollEnabled(false);
        mBookView.setLayoutManager(layout);

        mHomeworkAdapter = new AllHomeworkAdapter(mBooks);
        mBookView.setAdapter(mHomeworkAdapter);
        mBookView.addOnItemTouchListener(new OnRecyclerItemClickListener(mBookView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                itemClick(vh.getAdapterPosition());
            }
        });
        mHomeworkAdapter.notifyDataSetChanged();
    }

    private void itemClick(int position) {
        HomeworkBookSummary info = mBooks.get(position);
        if (info.getCourseBookId() == 0 || info.getCourseBookId() == -1){
            ToastUtil.showCustomToast(getActivity() , "该学科还没有教材");
            return;
        }
        Bundle extras = new Bundle();
        //图书ID
        extras.putInt(FileContonst.BOOK_ID, info.getCourseBookId());
        //笔记ID
        extras.putInt(FileContonst.NOTE_ID, info.getHomeworkFitNoteId());
        //作业ID
        extras.putInt(FileContonst.HOME_WROK_ID, info.getHomeworkId());
        //笔记名字
        extras.putString(FileContonst.NOTE_TITLE, info.getHomeworkFitNoteTitle());
        //笔记样式
        extras.putInt(FileContonst.NOTE_Style, info.getHomeworkFitNoteStyle());
        //课本进入
        extras.putString(FileContonst.JUMP_FRAGMENT, FileContonst.JUMP_HOMEWROK);
        loadIntentWithExtras(ControlFragmentActivity.class, extras);
    }
    public void loadIntentWithExtras(Class<? extends Activity> cls, Bundle extras) {
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtras(extras);
        startActivity(intent);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mIsFist = true;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if ((mIsFist && mCountBooks.size() == 0) || mIsRefresh) {
                loadData();
            }
        }
    }

    private void loadData() {
        if (NetUtils.isNetConnected()) {
            mLoadingNull.setVisibility(View.GONE);
            NetWorkManager.queryHomeworkBookList(SpUtils.getUserId()+"",null)
                    .subscribe(new Action1<List<HomeworkBookSummary>>() {
                        @Override
                        public void call(List<HomeworkBookSummary> homeworkBookInfos) {
                            freshUI(homeworkBookInfos);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            ToastUtil.showCustomToast(getContext() , "获取作业本数据失败,请点击刷新重新获取");
                            throwable.printStackTrace();
                        }
                    });
        } else {
            showCancelAndDetermineDialog(R.string.jump_to_net);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 刷新适配器的数据 ，暂时缺少分页
     */
    private void refreshAdapterData(String key, boolean isGrade) {
        //情况数据
        mCountBooks.clear();
        mBooks.clear();
        // 根据 是否是学科筛选数据
        if (isGrade) {
            for (HomeworkBookSummary book : mServerBooks) {

                //设置当前年级锁需要的书 ，并且做分页显示
                if (StringUtils.isEquals(key, book.getHomeworkFitGradeName())) {
                    mCountBooks.add(book);
                }
            }
        } else {
            for (HomeworkBookSummary book : mServerBooks) {
                //设置当前年级锁需要的书 ，并且做分页显示
                if (StringUtils.isEquals(key, book.getHomeworkFitSubjectName())) {
                    mCountBooks.add(book);
                }
            }
        }
        LogUtils.i("po ....mCountBooks" + mCountBooks.size());
        initPages();
    }


    /**
     * 跳转页数的 事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_gradeMore:
                mIsPackUp = !mIsPackUp;
                setLlTermSize();
                break;
        }

    }

    private void setLlTermSize() {

        // 延迟2S 解决 硬件残影问题
        llTerm.postDelayed(new Runnable() {
            @Override
            public void run() {
                llTerm.setVisibility(View.GONE);
                llTerm.setVisibility(View.VISIBLE);
            }
        }, 200);

        RelativeLayout.LayoutParams params;
        if (mIsPackUp) {
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, FileContonst.MIN_ALL_ITEM_SUBJECT);
        } else {
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }
        llTerm.setLayoutParams(params);
        mGradeMore.setSelected(mIsPackUp);
    }


    /***
     * 刷新适配器数据
     */
    private void refreshFirstAdapterData() {
        mFitGradeIndex = -1;
        mSubjectIndex  =- 1;
        //删除上次数据
        mTreeFitGrade.clear();
        mTreeSubject.clear();
        mBookFitGrade.clear();
        mBookSubject.clear();
        mBooks.clear();
        mCountBooks.clear();

        if (mServerBooks.size() > 0) {
            for (HomeworkBookSummary book : mServerBooks) {
                //设置当前年级锁需要的书 ，并且做分页显示
                if (StringUtils.isEquals(SpUtils.getGradeName(), book.getHomeworkFitGradeName())) {
                    mCountBooks.add(book);
                }
                //设置 年级分类
                if (!StringUtils.isEmpty(book.getHomeworkFitGradeName())) {
                    BookCategory info = new BookCategory();
                    info.setCategoryName(book.getHomeworkFitGradeName());
                    info.setCategoryId(book.getHomeworkFitGradeId());
                    mTreeFitGrade.add(info);
                }
                // 设置学科分类
                if (!StringUtils.isEmpty(book.getHomeworkFitSubjectName())) {
                    BookCategory info = new BookCategory();
                    info.setCategoryName(book.getHomeworkFitSubjectName());
                    info.setCategoryId(book.getHomeworkFitSubjectId());
                    mTreeSubject.add(info);
                }
            }

            // 设置 年级分类 集合
            if (mTreeFitGrade.size() > 0) {
                for (BookCategory infoGrade : mTreeFitGrade) {
                    //设置年级选中状态
                    if (StringUtils.isEquals(SpUtils.getGradeName(), infoGrade.getCategoryName())) {
                        infoGrade.setSelect(true);
                    }
                    mBookFitGrade.add(infoGrade);
                }
            }
            // 设置 学科分类 集合
            if (mTreeSubject.size() > 0) {
                for (BookCategory infoGrade : mTreeSubject)
                    mBookSubject.add(infoGrade);
            }
            //设置 显示书条目集合

            Collections.sort(mBookFitGrade);
            Collections.sort(mBookSubject);
            LogUtils.i("mBookFitGrade===" + mBookFitGrade.size());
            LogUtils.i("mBookSubject===" + mBookSubject.size());
            LogUtils.i("mBooks===" + mBooks.size());
            LogUtils.i("mCountBooks===" + mCountBooks.size());

            //更新分类适配器
            mFitGradeAdapter.notifyDataSetChanged();
            mSubjectAdapter.notifyDataSetChanged();
            initPages();
            mBookItemTile.setText(SpUtils.getGradeName() + "作业");
            //显示隐藏更多
        } else {
            LogUtils.i("当前还没有书");
        }
    }

    /**
     * 初始化翻页角标
     */
    private void initPages() {
        int counts = 0;
        int quotient = mCountBooks.size() / COUNT_PER_PAGE;
        int remainder = mCountBooks.size() % COUNT_PER_PAGE;
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
        //设置显示按钮
        addBtnCounts(counts);
        mBooks.clear();
        if (mCountBooks.size() > COUNT_PER_PAGE) { // 大于1页
            LogUtils.i("initPages1..");
            mBooks.addAll(mCountBooks.subList(0, COUNT_PER_PAGE));
        } else {
            LogUtils.i("initPages2.."); //小于1页
            mBooks.addAll(mCountBooks.subList(0, mCountBooks.size()));
        }
        mHomeworkAdapter.notifyDataSetChanged();
    }

    /***
     * 添加按钮
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

                refreshAdapterData(btnIndex+1);
            }
        });
        mPageBtnBar.setCurrentSelectPageIndex(0);
        mPageBtnBar.refreshPageBar();
    }

    private void refreshAdapterData(int pagerIndex) {
        //设置page页数数据
        mBooks.clear();

        if ((pagerIndex - 1) * COUNT_PER_PAGE + COUNT_PER_PAGE > mCountBooks.size()) { // 不是 正数被
            mBooks.addAll(mCountBooks.subList((pagerIndex - 1) * COUNT_PER_PAGE, mCountBooks.size()));
        } else {
            mBooks.addAll(mCountBooks.subList((pagerIndex - 1) * COUNT_PER_PAGE, (pagerIndex - 1) * COUNT_PER_PAGE + COUNT_PER_PAGE)); //正数被
        }
        mHomeworkAdapter.notifyDataSetChanged();
    }

    private void freshUI(List<HomeworkBookSummary> homeworkBookInfos) {
        if (homeworkBookInfos != null && homeworkBookInfos.size() > 0) {
            mLoadingNull.setVisibility(View.GONE);
            mServerBooks.clear();
            mServerBooks.addAll(homeworkBookInfos);
            refreshFirstAdapterData();
        } else {
            mLoadingNull.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFitGradeView = null;
        mBookView = null;
        mSubjectView = null;
        if (mHomeworkAdapter != null) {
            mHomeworkAdapter = null;
        }

        if (mSubjectAdapter != null) {
            mSubjectAdapter = null;
        }
        if (mFitGradeAdapter != null) {
            mFitGradeAdapter = null;
        }
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        if (event.getType().equalsIgnoreCase(EventBusConstant.all_home_work)) {
            LogUtils.i("type .." + EventBusConstant.current_home_work);
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
