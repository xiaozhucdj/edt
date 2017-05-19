package com.yougy.home.fragment.mainFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yougy.common.fragment.BFragment;
import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.callback.NewNoteBookCallBack;
import com.yougy.common.protocol.request.NewQueryNoteReq;
import com.yougy.common.protocol.response.NewQueryNoteRep;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.Observable.Observer;
import com.yougy.home.activity.ControlFragmentActivity;
import com.yougy.home.activity.MainActivity;
import com.yougy.home.adapter.AllNotesAdapter;
import com.yougy.home.adapter.FitGradeAdapter;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.home.adapter.SubjectAdapter;
import com.yougy.home.bean.BookCategory;
import com.yougy.home.bean.CacheJsonInfo;
import com.yougy.home.bean.NoteInfo;
import com.yougy.home.imple.RefreshBooksListener;
import com.yougy.ui.activity.R;
import com.yougy.view.CustomGridLayoutManager;
import com.yougy.view.DividerGridItemDecoration;
import com.yougy.view.dialog.LoadingProgressDialog;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/7/12.
 * 笔记
 */
public class AllNotesFragment extends BFragment implements View.OnClickListener, Observer {//, BookMarksDialog.DialogClickFinsihListener {
    private static final String TAG = "AllNotesFragment";
    //////////////////////////////////////集合数据/////////////////////////////////////////////////////
    /**
     * 适配器 数据
     */
    private List<NoteInfo> mInfos = new ArrayList<>();
    /**
     * 当前按照（年级，和课本类别分类后的数据总集合）
     */
    private List<NoteInfo> mCountInfos = new ArrayList<>();

    /**
     * 服务器返回数据
     */
    private List<NoteInfo> mServerInfos = new ArrayList<>();

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
    private static final int COUNT_PER_PAGE = 12;
    /***
     * 当前翻页的角标
     */
    private int mPagerIndex;

    /***
     * 切换年级 切换角标
     */
    private int mFitGradeIndex = -1;
    //////////////////////////////////////View/////////////////////////////////////////////////////

    private ViewGroup mRootView;
    private boolean mIsFist;
    private TextView mBookItemTile;
    private RecyclerView mFitGradeView;
    private RecyclerView mSubjectView;
    private RecyclerView mNoteView;
    private SubjectAdapter mSubjectAdapter;
    private FitGradeAdapter mFitGradeAdapter;
    private LinearLayout mLlPager;
    private AllNotesAdapter mNotesAdapter;
    private TextView mSubMore;
    private TextView mGradeMore;

    private ViewGroup mGroupSub;
    private ViewGroup mGroupGrade;
    private Subscription mSub;
    private ViewGroup mLoadingNull;
    private NewNoteBookCallBack mNewNoteBookCallBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_all_book, null);
        mBookItemTile = (TextView) mRootView.findViewById(R.id.tv_bookItemTile);
        initFitGradeAdapter();
        initSubjectAdapter();
        initBookAdapter();
        mLlPager = (LinearLayout) mRootView.findViewById(R.id.ll_page);


        mSubMore = (TextView) mRootView.findViewById(R.id.tv_subjectMore);
        mSubMore.setEnabled(false);
        mSubMore.setTag(0);
        mSubMore.setOnClickListener(this);

        mGradeMore = (TextView) mRootView.findViewById(R.id.tv_gradeMore);
        mGradeMore.setEnabled(false);
        mGradeMore.setTag(0);
        mGradeMore.setOnClickListener(this);


        mGroupSub = (ViewGroup) mRootView.findViewById(R.id.rl_subject);
        mGroupGrade = (ViewGroup) mRootView.findViewById(R.id.rl_grade);
        mLoadingNull = (ViewGroup)mRootView.findViewById(R.id.loading_null) ;
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
        mBookItemTile.setText(mBookFitGrade.get(position).getCategoryName() + "课本");
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
        }
        //替换position
        mSubjectIndex = position;
        //替换位置
        mBookSubject.get(position).setSelect(true);
        mBookItemTile.setText(mBookSubject.get(position).getCategoryName() + "课本");
        mSubjectAdapter.notifyDataSetChanged();
        //根据条件设置显示数据
        refreshAdapterData(mBookSubject.get(position).getCategoryName(), false);
        LogUtils.i("position.....onClickSubListener...last");
    }

    /***
     * 初始化笔记
     */
    private void initBookAdapter() {
        mNoteView = (RecyclerView) mRootView.findViewById(R.id.recycler_books);
        mNoteView.addItemDecoration(new DividerGridItemDecoration(UIUtils.getContext()));
        CustomGridLayoutManager layout = new CustomGridLayoutManager(getActivity(), 4);
        layout.setScrollEnabled(false);
        mNoteView.setLayoutManager(layout);
        mNotesAdapter = new AllNotesAdapter(getActivity(), mInfos);
        mNoteView.setAdapter(mNotesAdapter);
        mNoteView.addOnItemTouchListener(new OnRecyclerItemClickListener(mNoteView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                noteItemClick(vh.getAdapterPosition());
            }
        });
        mNotesAdapter.notifyDataSetChanged();
    }

    private void noteItemClick(int position){
        NoteInfo info = mInfos.get(position);
        LogUtils.i("noteinfo ....." + info.toString());
        Bundle extras = new Bundle();
        //课本进入
        extras.putString(FileContonst.JUMP_FRAGMENT, FileContonst.JUMP_NOTE);
        //笔记创建者
        extras.putInt(FileContonst.NOTE_CREATOR, info.getNoteCreator());
        //笔记id
        extras.putInt(FileContonst.NOTE_ID, info.getNoteId());
        //图书id
        extras.putInt(FileContonst.BOOK_ID, info.getBookId());
        //分类码
        extras.putInt(FileContonst.CATEGORY_ID, info.getBookCategory());
        //笔记标题
        extras.putString(FileContonst.NOTE_TITLE, info.getNoteTitle());
        //笔记学科
        extras.putString(FileContonst.NOTE_SUBJECT_NAME, info.getNoteFitSubjectName());
        //笔记类型
        extras.putInt(FileContonst.NOTE_MARK, info.getNoteStyle());
        //内部ID
        extras.putLong(FileContonst.NOTE_MARK, info.getNoteMark());
        //作业ID
        extras.putInt(FileContonst.HOME_WROK_ID, info.getNoteFitHomeworkId()) ;
        loadIntentWithExtras(ControlFragmentActivity.class, extras);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mIsFist = true;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (mIsFist && !hidden && mServerInfos.size() == 0 || FileContonst.globeIsAdd) {
            loadData();
        }
        if (!hidden) {
            LogUtils.i("当前--全部笔记");
            setRefreshListener();
        }
    }

    private void setRefreshListener() {
        SearchImple imple = new SearchImple();
        ((MainActivity) getActivity()).setRefreshListener(imple);
    }

    class SearchImple implements RefreshBooksListener {
        @Override
        public void onRefreshClickListener() {
            loadData();
        }
    }


    private void loadData() {
//        initTestData();
        //设置数据
        getNotes();
    }

    /***
     * 获取服务器笔记列表
     */
    private void getNotes() {
        if (YougyApplicationManager.isWifiAvailable()) {
           /* mNoteCallBack = new NoteBookCallBack(getActivity(),ProtocolId.PROTOCOL_ID_ALL_NOTE);
            mNoteCallBack.setTermIndex(-1);
            ProtocolManager.queryNotesProtocol(SpUtil.getAccountId(), 0, 2, ProtocolId.PROTOCOL_ID_ALL_NOTE, mNoteCallBack);*/
            NewQueryNoteReq req =  new NewQueryNoteReq() ;
            //设置学生ID
            req.setUserId(SpUtil.getAccountId());
            //设置缓存数据ID的key
            req.setCacheId(NewProtocolManager.NewCacheId.ALL_CODE_NOTE);
            //设置年级
            req.setNoteFitGradeName("");
            mNewNoteBookCallBack  = new NewNoteBookCallBack(getActivity() ,req) ;
            NewProtocolManager.queryNote(req ,mNewNoteBookCallBack);
            LogUtils.e(TAG, "query notes from server...");
        } else {
            LogUtils.e(TAG, "query notes from database...");
            mSub = getNotesObserver().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(getSubscriber());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSub != null) {
            mSub.unsubscribe();
        }
    }

    private Observable<List<NoteInfo>> getNotesObserver() {
        return Observable.create(new Observable.OnSubscribe<List<NoteInfo>>() {
            @Override
            public void call(Subscriber<? super List<NoteInfo>> subscriber) {

                List<CacheJsonInfo> infos = DataSupport.where("cacheID = ? ",NewProtocolManager.NewCacheId.ALL_CODE_NOTE+"").find(CacheJsonInfo.class);
                if (infos != null && infos.size() > 0) {
                    NewQueryNoteRep protocol = GsonUtil.fromJson(infos.get(0).getCacheJSON(), NewQueryNoteRep.class);
                    if (protocol!=null && protocol.getData() != null && protocol.getData().size()>0)
                        subscriber.onNext(protocol.getData());
                }
                subscriber.onCompleted();
            }
        });
    }

    private Subscriber<List<NoteInfo>> getSubscriber() {
        return new Subscriber<List<NoteInfo>>() {
            LoadingProgressDialog dialog;

            @Override
            public void onStart() {
                super.onStart();
                dialog = new LoadingProgressDialog(getActivity());
                dialog.show();
                dialog.setTitle("数据加载中...");
            }

            @Override
            public void onCompleted() {
                LogUtils.e(TAG, "onCompleted...");
                dialog.dismiss();
                if (mServerInfos!=null && mServerInfos.size()<0){
                    setLoading(View.VISIBLE);
                }
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.e(TAG, "onError...");
                dialog.dismiss();
            }

            @Override
            public void onNext(List<NoteInfo> noteInfos) {
                if (noteInfos!=null && noteInfos.size()>0){
                  setLoading(View.GONE);
                    refresh(noteInfos);
                }else{
                    setLoading(View.VISIBLE);
                }
            }
        };
    }

    private void refresh(List<NoteInfo> infos) {
        mServerInfos.clear();
        mServerInfos.addAll(infos);
        refreshFirstAdapterData();
    }


    @Override
    protected void handleEvent() {

        handleNoteBookEvent();
        super.handleEvent();
    }


    private void handleNoteBookEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof NewQueryNoteRep && !mHide && mNewNoteBookCallBack != null) {
                    NewQueryNoteRep data = (NewQueryNoteRep) o;
                    if (data.getCode() == NewProtocolManager.NewCodeResult.CODE_SUCCESS && data!=null && data.getData()!=null && data.getData().size()>0){
                        setLoading(View.GONE);
                        refresh(data.getData());
                    }else{
                        setLoading(View.VISIBLE);
                    }
                }else if (o instanceof String && !mHide && StringUtils.isEquals((String) o,NewProtocolManager.NewCacheId.ALL_CODE_NOTE+"")){
                    LogUtils.i(" onerror 处理 ...........");
                    mSub = getNotesObserver().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(getSubscriber());
                }
            }
        }));
    }


    private  void  setLoading( final int visibility){
        UIUtils.post(new Runnable() {
            @Override
            public void run() {
                mLoadingNull.setVisibility(visibility);
            }
        }) ;
    }

    ///////////////////////////////////点击事件//////////////////////////////////////


    public void loadIntentWithExtras(Class<? extends Activity> cls, Bundle extras) {
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtras(extras);
        startActivity(intent);
    }


    /***
     * 刷新适配器数据
     */
    private void refreshFirstAdapterData() {
        //删除上次数据
        mTreeFitGrade.clear();
        mTreeSubject.clear();
        mBookFitGrade.clear();
        mBookSubject.clear();
        mInfos.clear();
        mCountInfos.clear();

        if (mServerInfos.size() > 0) {
            for (NoteInfo noteInfo : mServerInfos) {
                //设置当前年级锁需要的书 ，并且做分页显示
                if (StringUtils.isEquals(SpUtil.getGradeName(), noteInfo.getNoteFitGradeName())) {
                    mCountInfos.add(noteInfo);
                }
                //设置 年级分类
                if (!StringUtils.isEmpty(noteInfo.getNoteFitGradeName())) {
                    BookCategory info = new BookCategory();
                    info.setCategoryName(noteInfo.getNoteFitGradeName());
                    info.setCategoryId(noteInfo.getNoteFitGradeId());
                    mTreeFitGrade.add(info);
                }
                // 设置学科分类
                if (!StringUtils.isEmpty(noteInfo.getNoteFitSubjectName())) {
                    BookCategory info = new BookCategory();
                    info.setCategoryName(noteInfo.getNoteFitSubjectName());
                    info.setCategoryId(noteInfo.getNoteFitSubjectId());
                    mTreeSubject.add(info);
                }
            }

            // 设置 年级分类 集合
            if (mTreeFitGrade.size() > 0) {
                for (BookCategory infoGrade : mTreeFitGrade) {
                    //设置年级选中状态
                    if (StringUtils.isEquals(SpUtil.getGradeName(), infoGrade.getCategoryName())) {
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
            LogUtils.i("mInfos===" + mInfos.size());
            LogUtils.i("mCountInfos===" + mCountInfos.size());

            //更新分类适配器
            mFitGradeAdapter.notifyDataSetChanged();
            mSubjectAdapter.notifyDataSetChanged();
            initPages();
            mBookItemTile.setText(SpUtil.getGradeName() + "课本");


            if (mBookFitGrade.size() > 4) {
                mGradeMore.setVisibility(View.VISIBLE);
                mGradeMore.setEnabled(true);
            } else {
                mGradeMore.setVisibility(View.INVISIBLE);
                mGradeMore.setEnabled(false);
            }
            if (mBookSubject.size() > 4) {
                mSubMore.setVisibility(View.VISIBLE);
                mSubMore.setEnabled(true);
            } else {
                mSubMore.setVisibility(View.INVISIBLE);
                mSubMore.setEnabled(false);
            }
            //设置最小值大小
            setLineSize(mGroupSub);
            setLineSize(mGroupGrade);
        } else {
            LogUtils.i("当前还没有书");
        }
    }


    /**
     * 初始化翻页角标
     */
    private void initPages() {
        int counts = 0;
        int quotient = mCountInfos.size() / COUNT_PER_PAGE;
        int remainder = mCountInfos.size() % COUNT_PER_PAGE;
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
        mInfos.clear();
        if (mCountInfos.size() > COUNT_PER_PAGE) { // 大于1页
            LogUtils.i("initPages1..");
            mInfos.addAll(mCountInfos.subList(0, COUNT_PER_PAGE));
        } else {
            LogUtils.i("initPages2.."); //小于1页
            mInfos.addAll(mCountInfos.subList(0, mCountInfos.size()));
        }
        mNotesAdapter.notifyDataSetChanged();
    }

    /***
     * 添加按钮
     *
     * @param counts
     */
    private void addBtnCounts(int counts) {
        //删除之前的按钮
        mLlPager.removeAllViews();

        for (int index = 1; index <= counts; index++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 20;
            View pageLayout = View.inflate(getActivity(), R.layout.page_item, null);
            final Button pageBtn = (Button) pageLayout.findViewById(R.id.page_btn);
            if (index == 1) {
                mPagerIndex = 1;
                pageBtn.setSelected(true);
            }
            pageBtn.setTag(index);
            pageBtn.setText(Integer.toString(index));
            pageBtn.setOnClickListener(this);
            mLlPager.addView(pageBtn, params);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.page_btn:
                if ((int) v.getTag() == mPagerIndex) {
                    return;
                }

                //还原上个按钮状态
                mLlPager.getChildAt(mPagerIndex - 1).setSelected(false);
                mPagerIndex = (int) v.getTag();
                //设置当前按钮状态
                mLlPager.getChildAt(mPagerIndex - 1).setSelected(true);

                //设置page页数数据
                mInfos.clear();

                if ((mPagerIndex - 1) * COUNT_PER_PAGE + COUNT_PER_PAGE > mCountInfos.size()) { // 不是 正数被
                    mInfos.addAll(mCountInfos.subList((mPagerIndex - 1) * COUNT_PER_PAGE, mCountInfos.size()));
                } else {
                    mInfos.addAll(mCountInfos.subList((mPagerIndex - 1) * COUNT_PER_PAGE, (mPagerIndex - 1) * COUNT_PER_PAGE + COUNT_PER_PAGE)); //正数被
                }
                mNotesAdapter.notifyDataSetChanged();
                break;


            case R.id.tv_subjectMore:

                int tagSub = (int) mSubMore.getTag();
                if (tagSub == 0) {
                    setMoreSize(mGroupSub);
                    mSubMore.setTag(1);
                    mSubMore.setSelected(true);
                } else if (tagSub == 1) {
                    setLineSize(mGroupSub);
                    mSubMore.setTag(0);
                    mSubMore.setSelected(false);
                }

                break;

            case R.id.tv_gradeMore:
                int tagGrade = (int) mGradeMore.getTag();
                if (tagGrade == 0) {
                    setMoreSize(mGroupGrade);
                    mGradeMore.setTag(1);
                    mGradeMore.setSelected(true);

                } else if (tagGrade == 1) {
                    setLineSize(mGroupGrade);
                    mGradeMore.setTag(0);
                    mGradeMore.setSelected(false);
                }
                break;
        }
    }


    /**
     * 刷新适配器的数据 ，暂时缺少分页
     */
    private void refreshAdapterData(String key, boolean isGrade) {
        //情况数据
        mCountInfos.clear();
        mInfos.clear();
        // 根据 是否是学科筛选数据
        if (isGrade) {
            for (NoteInfo noteInfo : mServerInfos) {

                //设置当前年级锁需要的书 ，并且做分页显示
                if (StringUtils.isEquals(key, noteInfo.getNoteFitGradeName())) {
                    mCountInfos.add(noteInfo);
                }
            }
        } else {
            for (NoteInfo noteInfo : mServerInfos) {
                //设置当前年级锁需要的书 ，并且做分页显示
                if (StringUtils.isEquals(key, noteInfo.getNoteFitSubjectName())) {
                    mCountInfos.add(noteInfo);
                }
            }
        }

        LogUtils.i("po ....mCountInfos" + mCountInfos.size());
        initPages();

    }


    //////////////////////////////////测试数据//////////////////////////////////////

    /***
     * 初始化 测试数据
     */
    private void initTestData() {

        NoteInfo NoteInfo1 = new NoteInfo();
        //设置图片
        NoteInfo1.setNoteFitGradeName("小学一年级");
        NoteInfo1.setNoteFitGradeId(1);
        NoteInfo1.setNoteFitSubjectName("语文");
        NoteInfo1.setNoteFitSubjectId(1);
        //设置笔记创建者
        NoteInfo1.setNoteAuthor(SpUtil.getAccountId());
        //设置笔记对应书的id
        NoteInfo1.setBookId(-1);
        //设置是否和书绑定
        NoteInfo1.setBookCategory(-1);
////////////////////////////////////////////////////////////////////////////////////
        NoteInfo NoteInfo2 = new NoteInfo();
        NoteInfo2.setNoteFitGradeName("小学二年级");
        NoteInfo2.setNoteFitGradeId(2);
        NoteInfo2.setNoteFitSubjectName("语文");
        NoteInfo2.setNoteFitSubjectId(1);
        //设置笔记创建者
        NoteInfo2.setNoteAuthor(SpUtil.getAccountId());
        //设置笔记对应书的id
        NoteInfo2.setBookId(-1);
        //设置是否和书绑定
        NoteInfo2.setBookCategory(-1);

        NoteInfo NoteInfo3 = new NoteInfo();
        //设置图片
        NoteInfo3.setNoteFitGradeName("小学三年级");
        NoteInfo3.setNoteFitGradeId(3);
        NoteInfo3.setNoteFitSubjectName("数学");
        NoteInfo3.setNoteFitSubjectId(2);

        NoteInfo NoteInfo4 = new NoteInfo();
        //设置图片
        NoteInfo4.setNoteFitGradeName("小学四年级");
        NoteInfo4.setNoteFitGradeId(4);
        NoteInfo4.setNoteFitSubjectName("数学");
        NoteInfo4.setNoteFitSubjectId(2);
        //设置笔记创建者
        NoteInfo4.setNoteAuthor(11111);
        //设置笔记对应书的id
        NoteInfo4.setBookId(1111111);
        //设置是否和书绑定
        NoteInfo4.setBookCategory(111111111);


        NoteInfo NoteInfo5 = new NoteInfo();
        NoteInfo5.setNoteFitGradeName("小学四年级");
        NoteInfo5.setNoteFitGradeId(4);
        NoteInfo5.setNoteFitSubjectName("数学");
        NoteInfo5.setNoteFitSubjectId(2);

        NoteInfo NoteInfo6 = new NoteInfo();
        NoteInfo6.setNoteFitGradeName("小学六年级");
        NoteInfo6.setNoteFitGradeId(6);
        NoteInfo6.setNoteFitSubjectName("数学");
        NoteInfo6.setNoteFitSubjectId(2);


        NoteInfo NoteInfo7 = new NoteInfo();
        NoteInfo7.setNoteFitGradeName("小学六年级");
        NoteInfo7.setNoteFitGradeId(6);
        NoteInfo7.setNoteFitSubjectName("数学");
        NoteInfo7.setNoteFitSubjectId(2);


        NoteInfo NoteInfo8 = new NoteInfo();
        NoteInfo8.setNoteFitGradeName("小学六年级");
        NoteInfo8.setNoteFitGradeId(6);
        NoteInfo8.setNoteFitSubjectName("数学");
        NoteInfo8.setNoteFitSubjectId(2);


        NoteInfo NoteInfo9 = new NoteInfo();
        NoteInfo9.setNoteFitGradeName("小学六年级");
        NoteInfo9.setNoteFitGradeId(6);
        NoteInfo9.setNoteFitSubjectName("数学");
        NoteInfo9.setNoteFitSubjectId(2);


        NoteInfo NoteInfo10 = new NoteInfo();
        NoteInfo10.setNoteFitGradeName("小学六年级");
        NoteInfo10.setNoteFitGradeId(6);
        NoteInfo10.setNoteFitSubjectName("数学");
        NoteInfo10.setNoteFitSubjectId(2);

        NoteInfo NoteInfo11 = new NoteInfo();
        NoteInfo11.setNoteFitGradeName("小学六年级");
        NoteInfo11.setNoteFitGradeId(6);
        NoteInfo11.setNoteFitSubjectName("数学");
        NoteInfo11.setNoteFitSubjectId(2);


        NoteInfo NoteInfo12 = new NoteInfo();
        NoteInfo12.setNoteFitGradeName("小学六年级");
        NoteInfo12.setNoteFitGradeId(6);
        NoteInfo12.setNoteFitSubjectName("数学");
        NoteInfo12.setNoteFitSubjectId(2);


        NoteInfo NoteInfo13 = new NoteInfo();
        NoteInfo13.setNoteFitGradeName("小学六年级");
        NoteInfo13.setNoteFitGradeId(6);
        NoteInfo13.setNoteFitSubjectName("数学");
        NoteInfo13.setNoteFitSubjectId(2);


        NoteInfo NoteInfo14 = new NoteInfo();
        NoteInfo14.setNoteFitGradeName("小学六年级");
        NoteInfo14.setNoteFitGradeId(6);
        NoteInfo14.setNoteFitSubjectName("数学");
        NoteInfo14.setNoteFitSubjectId(2);


        NoteInfo NoteInfo15 = new NoteInfo();
        NoteInfo15.setNoteFitGradeName("小学六年级");
        NoteInfo15.setNoteFitGradeId(6);
        NoteInfo15.setNoteFitSubjectName("数学");
        NoteInfo15.setNoteFitSubjectId(2);


        NoteInfo NoteInfo16 = new NoteInfo();
        NoteInfo16.setNoteFitGradeName("小学六年级");
        NoteInfo16.setNoteFitGradeId(6);
        NoteInfo16.setNoteFitSubjectName("数学");
        NoteInfo16.setNoteFitSubjectId(2);


        mServerInfos.add(NoteInfo1);
        mServerInfos.add(NoteInfo2);
        mServerInfos.add(NoteInfo3);
        mServerInfos.add(NoteInfo4);
        mServerInfos.add(NoteInfo5);
        mServerInfos.add(NoteInfo6);
        mServerInfos.add(NoteInfo7);
        mServerInfos.add(NoteInfo8);
        mServerInfos.add(NoteInfo9);
        mServerInfos.add(NoteInfo10);
        mServerInfos.add(NoteInfo11);
        mServerInfos.add(NoteInfo12);
        mServerInfos.add(NoteInfo13);
        mServerInfos.add(NoteInfo14);
        mServerInfos.add(NoteInfo15);
        mServerInfos.add(NoteInfo16);
//-----------------------------------------------
        mServerInfos.add(NoteInfo1);
        mServerInfos.add(NoteInfo2);
        mServerInfos.add(NoteInfo3);
        mServerInfos.add(NoteInfo4);
        mServerInfos.add(NoteInfo5);
        mServerInfos.add(NoteInfo6);
        mServerInfos.add(NoteInfo7);
        mServerInfos.add(NoteInfo8);
        mServerInfos.add(NoteInfo9);
        mServerInfos.add(NoteInfo10);
        mServerInfos.add(NoteInfo11);
        mServerInfos.add(NoteInfo12);
        mServerInfos.add(NoteInfo13);
        mServerInfos.add(NoteInfo14);
        mServerInfos.add(NoteInfo15);
        mServerInfos.add(NoteInfo16);
//-----------------------------------------------
        mServerInfos.add(NoteInfo1);
        mServerInfos.add(NoteInfo2);
        mServerInfos.add(NoteInfo3);
        mServerInfos.add(NoteInfo4);
        mServerInfos.add(NoteInfo5);
        mServerInfos.add(NoteInfo6);
        mServerInfos.add(NoteInfo7);
        mServerInfos.add(NoteInfo8);
        mServerInfos.add(NoteInfo9);
        mServerInfos.add(NoteInfo10);
        mServerInfos.add(NoteInfo11);
        mServerInfos.add(NoteInfo12);
        mServerInfos.add(NoteInfo13);
        mServerInfos.add(NoteInfo14);
        mServerInfos.add(NoteInfo15);
        mServerInfos.add(NoteInfo16);

        refreshFirstAdapterData();
    }

    @Override
    public void updataNote(long noteId, int noteStyle, String subject, String noteTile) {
        LogUtils.i("修改全部笔记");

        LogUtils.i("更新笔记");
        if (mServerInfos == null || mServerInfos.size() < 0) {
            return;
        }

        for (NoteInfo info : mServerInfos) {
            if (info.getNoteId() == noteId || info.getNoteMark() == noteId) {
                if (!StringUtils.isEmpty(noteTile)) {
                    info.setNoteTitle(noteTile);
                }
                if (!StringUtils.isEmpty(subject)) {
                    info.setNoteFitSubjectName(subject);
                }

                info.setNoteStyle(noteStyle);
                mNotesAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void removeNote(int noteId) {
        LogUtils.i("删除全部笔记");
        if (mServerInfos == null || mServerInfos.size() < 0) {
            return;
        }
        for (NoteInfo info : mServerInfos) {
            if (info.getNoteId() == noteId) {
                mServerInfos.remove(info);
                refreshFirstAdapterData();
                break;
            }
        }
    }


    /**
     * 设置一行item 显示大小
     *
     * @param view
     */
    private void setLineSize(ViewGroup view) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 60);
        if (view == mGroupSub) {
            params.setMargins(0, 15, 0, 0);
        }
        view.setLayoutParams(params);
    }

    /**
     * 设置全部大小
     */
    private void setMoreSize(ViewGroup view) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (view == mGroupSub) {
            params.setMargins(0, 15, 0, 0);
        }
        view.setLayoutParams(params);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFitGradeView = null;
        mSubjectView = null;
        mNoteView = null;
        if (mSubjectAdapter != null) {
            mSubjectAdapter = null;
        }
        if (mFitGradeAdapter != null) {
            mFitGradeAdapter = null;
        }

        if (mNotesAdapter != null) {
            mNotesAdapter = null;
        }
    }
}
