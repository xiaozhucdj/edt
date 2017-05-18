package com.yougy.home.fragment.mainFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.download.DownloadListener;
import com.yougy.common.fragment.BFragment;
import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.DownloadManager;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.nohttp.DownInfo;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.TextBookCallBack;
import com.yougy.common.protocol.response.BookShelfProtocol;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.Observable.Observer;
import com.yougy.home.activity.ControlFragmentActivity;
import com.yougy.home.activity.MainActivity;
import com.yougy.home.adapter.FitGradeAdapter;
import com.yougy.home.adapter.SubjectAdapter;
import com.yougy.home.adapter.AllBookAdapter;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.home.bean.BookCategory;
import com.yougy.home.bean.CacheJsonInfo;
import com.yougy.home.imple.RefreshBooksListener;
import com.yougy.init.bean.BookInfo;
import com.yougy.ui.activity.R;
import com.yougy.view.CustomGridLayoutManager;
import com.yougy.view.DividerGridItemDecoration;
import com.yougy.view.dialog.DownBookDialog;
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

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static android.view.View.inflate;

/**
 * Created by yuanye on 2016/11/2.
 * 全部课本本
 * 注意 adapter 对应的 list  ，并且集合赋值 “= ”切记不要使用
 */
public class AllTextBookFragment extends BFragment implements OnClickListener, DownBookDialog.DownBookListener,Observer {
    //////////////////////////////////////集合数据/////////////////////////////////////////////////////
    /**
     * 适配器 数据
     */
    private List<BookInfo> mBooks = new ArrayList<>();
    /**
     * 当前按照（年级，和课本类别分类后的数据总集合）
     */
    private List<BookInfo> mCountBooks = new ArrayList<>();

    /**
     * 服务器返回数据
     */
    private List<BookInfo> mServerBooks = new ArrayList<>();

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
    private AllBookAdapter mAdaptetFragmentAllTextBook;
    private SubjectAdapter mSubjectAdapter;
    private FitGradeAdapter mFitGradeAdapter;
    private LinearLayout mLlPager;
    private DownBookDialog mDialog;
    private BookInfo mDownInfo;
    private TextView mSubMore;
    private TextView mGradeMore;

    private ViewGroup mGroupSub;
    private ViewGroup mGroupGrade;
    private TextBookCallBack mTextBookCall;
    private Subscription mSub;
    private ViewGroup mLoadingNull;

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

    private void fitItemClick(int position){
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

    private void subjectItemClick(int position){
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
     * 初始化课本
     */
    private void initBookAdapter() {
        mBookView = (RecyclerView) mRootView.findViewById(R.id.recycler_books);
        mBookView.addItemDecoration(new DividerGridItemDecoration(UIUtils.getContext()));
        CustomGridLayoutManager layout = new CustomGridLayoutManager(getActivity(), 4);
        layout.setScrollEnabled(false);
        mBookView.setLayoutManager(layout);
        mAdaptetFragmentAllTextBook = new AllBookAdapter(getActivity(), mBooks, this);
        mBookView.setAdapter(mAdaptetFragmentAllTextBook);
        mBookView.addOnItemTouchListener(new OnRecyclerItemClickListener(mBookView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                itemClick(vh.getAdapterPosition());
            }
        });
        mAdaptetFragmentAllTextBook.notifyDataSetChanged();
    }

    private void itemClick(int position) {
        BookInfo info = mBooks.get(position);
        mDownInfo = info;
        LogUtils.i("book id ....." + info.toString());
        String filePath = FileUtils.getTextBookFilesDir() + info.getBookId() + ".pdf";
        if (FileUtils.exists(filePath)) {
            Bundle extras = new Bundle();
            //课本进入
            extras.putString(FileContonst.JUMP_FRAGMENT, FileContonst.JUMP_TEXT_BOOK);
            //笔记创建者
            extras.putInt(FileContonst.NOTE_CREATOR, -1);
            //笔记id
            extras.putInt(FileContonst.NOTE_ID, info.getBookFitNoteId());
            //图书id
            extras.putInt(FileContonst.BOOK_ID, info.getBookId());
            //分类码
            extras.putInt(FileContonst.CATEGORY_ID, info.getBookCategory());
            //笔记类型
            extras.putInt(FileContonst.NOTE_Style, info.getNoteStyle());
            extras.putInt(FileContonst.HOME_WROK_ID, info.getBookFitHomeworkId()) ;
            loadIntentWithExtras(ControlFragmentActivity.class, extras);
        } else {

            if (NetUtils.isNetConnected()) {
                if (mDialog == null) {
                    mDialog = new DownBookDialog(getActivity());
                    mDialog.setListener(this);
                }
                mDialog.show();
                mDialog.getBtnConfirm().setVisibility(View.VISIBLE);
                mDialog.setTitle(UIUtils.getString(R.string.down_book_defult));
            } else {
                UIUtils.showToastSafe(R.string.net_not_connection, Toast.LENGTH_SHORT);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mIsFist = true;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (mIsFist && !hidden && mServerBooks.size() == 0) {
            loadData();
        }
        if (!hidden) {
            LogUtils.i("当前--全部课本");
            setRefreshListener();
        }
    }

    private void setRefreshListener() {
     SearchImple imple =  new SearchImple() ;
        ((MainActivity) getActivity()).setRefreshListener(imple);
    }

    class  SearchImple implements RefreshBooksListener {
        @Override
        public void onRefreshClickListener() {
            loadData();
        }
    }

    private void loadData() {
        if (YougyApplicationManager.isWifiAvailable()) {
            mTextBookCall = new TextBookCallBack(getActivity(),ProtocolId.PROTOCOL_ID_All_TEXT_BOOK);
            mTextBookCall.setTermIndex(-1);
            mTextBookCall.setCategoryId(10000);
            Log.e(TAG, "query book from server...");
            ProtocolManager.bookShelfProtocol(SpUtil.getAccountId(), -1, 10000, "", ProtocolId.PROTOCOL_ID_All_TEXT_BOOK, mTextBookCall);
        } else {
            Log.e(TAG, "query book from database...");
            mSub =  getObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(getSubscriber());
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSub != null) {
            mSub.unsubscribe();
        }
    }

    public void loadIntentWithExtras(Class<? extends Activity> cls, Bundle extras) {
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtras(extras);
        startActivity(intent);
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
            for (BookInfo book : mServerBooks) {

                //设置当前年级锁需要的书 ，并且做分页显示
                if (StringUtils.isEquals(key, book.getBookFitGradeName())) {
                    mCountBooks.add(book);
                }
            }
        } else {
            for (BookInfo book : mServerBooks) {

                //设置当前年级锁需要的书 ，并且做分页显示
                if (StringUtils.isEquals(key, book.getBookFitSubjectName())) {
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
                mBooks.clear();

                if ((mPagerIndex - 1) * COUNT_PER_PAGE + COUNT_PER_PAGE > mCountBooks.size()) { // 不是 正数被
                    mBooks.addAll(mCountBooks.subList((mPagerIndex - 1) * COUNT_PER_PAGE, mCountBooks.size()));
                } else {
                    mBooks.addAll(mCountBooks.subList((mPagerIndex - 1) * COUNT_PER_PAGE, (mPagerIndex - 1) * COUNT_PER_PAGE + COUNT_PER_PAGE)); //正数被
                }
                mAdaptetFragmentAllTextBook.notifyDataSetChanged();
                break;

            case R.id.tv_subjectMore:

                int  tagSub = (int) mSubMore.getTag();
                if (tagSub==0){
                    setMoreSize(mGroupSub);
                    mSubMore.setTag(1);
                    mSubMore.setSelected(true);
                }else if (tagSub ==1){
                    setLineSize(mGroupSub);
                    mSubMore.setTag(0);
                    mSubMore.setSelected(false);
                }

                break;

            case R.id.tv_gradeMore:
                int  tagGrade = (int) mGradeMore.getTag();
                if (tagGrade==0){
                    setMoreSize(mGroupGrade);
                    mGradeMore.setTag(1);
                    mGradeMore.setSelected(true);

                }else if (tagGrade ==1){
                    setLineSize(mGroupGrade);
                    mGradeMore.setTag(0);
                    mGradeMore.setSelected(false);
                }
                break;
        }

    }

    @Override
    public void onCancelListener() {
        //判断是否下载
        DownloadManager.cancel();
        mDialog.dismiss();
    }

    @Override
    public void onConfirmListener() {
        mDialog.getBtnConfirm().setVisibility(GONE);
        List<DownInfo> mFiles = new ArrayList<>();
        DownInfo info = new DownInfo(mDownInfo.getBookDownload(), FileUtils.getTextBookFilesDir(), mDownInfo.getBookId() + ".pdf", true, false, mDownInfo.getBookId());
        info.setBookName(mDownInfo.getBookTitle());
        mFiles.add(info);
        downBook(mFiles);
    }

    /***
     * 文件下载，下载位置 ，FileUtils.getTextBookFilesDir()
     */
    private void downBook(List<DownInfo> mFiles) {
        DownloadManager.downloadFile(mFiles, new DownloadListener() {
            @Override
            public void onDownloadError(int what, Exception exception) {
                LogUtils.i("  onDownloadError     what ........" + what);
                DownloadManager.cancel();
                mDialog.setTitle(UIUtils.getString(R.string.down_book_defult));
                mDialog.getBtnConfirm().setVisibility(VISIBLE);
            }

            @Override
            public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
                LogUtils.i("  onStart     what ........" + what);
            }

            @Override
            public void onProgress(int what, int progress, long fileCount) {
                mDialog.setTitle(String.format(getString(R.string.down_book_loading), progress +"%" ));
            }

            @Override
            public void onFinish(int what, String filePath) {
                if (DownloadManager.isFinish()) {
                    mDialog.dismiss();
                }
            }

            @Override
            public void onCancel(int what) {

            }
        });
    }



/*    *//***
     * 监听回调
     *//*
    private class AllTextBookCallBack extends BaseCallBack<BookShelfProtocol> {
        public AllTextBookCallBack(Context context) {
            super(context);
        }

        @Override
        public BookShelfProtocol parseNetworkResponse(Response response, int id) throws Exception {
            String str = response.body().string();
            LogUtils.i("response json ...." + str);
            return GsonUtil.fromJson(str, BookShelfProtocol.class);
        }

        @Override
        public void onResponse(BookShelfProtocol response, int id) {

            if (response.getCode() == ProtocolId.RET_SUCCESS) {
                if (response.getBookList() != null && response.getBookList().size() > 0) {
                    // 总数据赋值
                    mServerBooks.clear();
                    mServerBooks.addAll(response.getBookList());
                    refreshFirstAdapterData();
                } else {
                    //数据为空
                }
            } else {
                //协议失败
            }
        }

        @Override
        public void onClick() {
            super.onClick();
            ProtocolManager.bookShelfProtocol(Integer.parseInt(SpUtil.getAccountId()), -1, 10000, "", ProtocolId.PROTOCOL_ID_BOOK_SHELF, this);
        }
    }*/

    /***
     * 刷新适配器数据
     */
    private void refreshFirstAdapterData() {

        //删除上次数据
        mTreeFitGrade.clear();
        mTreeSubject.clear();
        mBookFitGrade.clear();
        mBookSubject.clear();
        mBooks.clear();
        mCountBooks.clear();

        if (mServerBooks.size() > 0) {
            for (BookInfo book : mServerBooks) {
                //设置当前年级锁需要的书 ，并且做分页显示
                if (StringUtils.isEquals(SpUtil.getGradeName(), book.getBookFitGradeName())) {
                    mCountBooks.add(book);
                }
                //设置 年级分类
                if (!StringUtils.isEmpty(book.getBookFitGradeName())) {
                    BookCategory info = new BookCategory();
                    info.setCategoryName(book.getBookFitGradeName());
                    info.setCategoryId(book.getBookFitGradeId());

                    mTreeFitGrade.add(info);
                }
                // 设置学科分类
                if (!StringUtils.isEmpty(book.getBookFitSubjectName())) {
                    BookCategory info = new BookCategory();
                    info.setCategoryName(book.getBookFitSubjectName());
                    info.setCategoryId(book.getBookFitSubjectId());
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
            LogUtils.i("mBooks===" + mBooks.size());
            LogUtils.i("mCountBooks===" + mCountBooks.size());

            //更新分类适配器
            mFitGradeAdapter.notifyDataSetChanged();
            mSubjectAdapter.notifyDataSetChanged();
            initPages();
            mBookItemTile.setText(SpUtil.getGradeName() + "课本");
            //显示隐藏更多
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
        //删除之前的按钮
        mLlPager.removeAllViews();
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
        mAdaptetFragmentAllTextBook.notifyDataSetChanged();
    }


    /***
     * 添加按钮
     *
     * @param counts
     */
    private void addBtnCounts(int counts) {
        for (int index = 1; index <= counts; index++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 20;
            View pageLayout = inflate(getActivity(), R.layout.page_item, null);
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


    ///////////////////////////////测试假数据///////////////////////////////////////

    /***
     * 5个年级
     * 2个学科 ：
     */
    private void testData() {
        BookInfo bookInfo1 = new BookInfo();
        //设置图片
        bookInfo1.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo1.setBookFitGradeName("小学一年级");
        bookInfo1.setBookFitGradeId(1);
        bookInfo1.setBookFitSubjectName("语文");
        bookInfo1.setBookFitSubjectId(1);

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo2.setBookFitGradeName("小学二年级");
        bookInfo2.setBookFitGradeId(2);
        bookInfo2.setBookFitSubjectName("语文");
        bookInfo2.setBookFitSubjectId(1);

        BookInfo bookInfo3 = new BookInfo();
        //设置图片
        bookInfo3.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo3.setBookFitGradeName("小学三年级");
        bookInfo3.setBookFitGradeId(3);
        bookInfo3.setBookFitSubjectName("数学");
        bookInfo3.setBookFitSubjectId(2);

        BookInfo bookInfo4 = new BookInfo();
        //设置图片
        bookInfo4.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo4.setBookFitGradeName("小学四年级");
        bookInfo4.setBookFitGradeId(4);
        bookInfo4.setBookFitSubjectName("数学");
        bookInfo4.setBookFitSubjectId(2);


        BookInfo bookInfo5 = new BookInfo();
        bookInfo5.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo5.setBookFitGradeName("小学四年级");
        bookInfo5.setBookFitGradeId(4);
        bookInfo5.setBookFitSubjectName("数学");
        bookInfo5.setBookFitSubjectId(2);

        BookInfo bookInfo6 = new BookInfo();
        bookInfo6.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo6.setBookFitGradeName("小学六年级");
        bookInfo6.setBookFitGradeId(6);
        bookInfo6.setBookFitSubjectName("数学");
        bookInfo6.setBookFitSubjectId(2);


        BookInfo bookInfo7 = new BookInfo();
        bookInfo7.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo7.setBookFitGradeName("小学六年级");
        bookInfo7.setBookFitGradeId(6);
        bookInfo7.setBookFitSubjectName("数学");
        bookInfo7.setBookFitSubjectId(2);


        BookInfo bookInfo8 = new BookInfo();
        bookInfo8.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo8.setBookFitGradeName("小学六年级");
        bookInfo8.setBookFitGradeId(6);
        bookInfo8.setBookFitSubjectName("数学");
        bookInfo8.setBookFitSubjectId(2);


        BookInfo bookInfo9 = new BookInfo();
        bookInfo9.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo9.setBookFitGradeName("小学六年级");
        bookInfo9.setBookFitGradeId(6);
        bookInfo9.setBookFitSubjectName("数学");
        bookInfo9.setBookFitSubjectId(2);


        BookInfo bookInfo10 = new BookInfo();
        bookInfo10.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo10.setBookFitGradeName("小学六年级");
        bookInfo10.setBookFitGradeId(6);
        bookInfo10.setBookFitSubjectName("数学");
        bookInfo10.setBookFitSubjectId(2);

        BookInfo bookInfo11 = new BookInfo();
        bookInfo11.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo11.setBookFitGradeName("小学六年级");
        bookInfo11.setBookFitGradeId(6);
        bookInfo11.setBookFitSubjectName("数学");
        bookInfo11.setBookFitSubjectId(2);


        BookInfo bookInfo12 = new BookInfo();
        bookInfo12.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo12.setBookFitGradeName("小学六年级");
        bookInfo12.setBookFitGradeId(6);
        bookInfo12.setBookFitSubjectName("数学");
        bookInfo12.setBookFitSubjectId(2);


        BookInfo bookInfo13 = new BookInfo();
        bookInfo13.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo13.setBookFitGradeName("小学六年级");
        bookInfo13.setBookFitGradeId(6);
        bookInfo13.setBookFitSubjectName("数学");
        bookInfo13.setBookFitSubjectId(2);


        BookInfo bookInfo14 = new BookInfo();
        bookInfo14.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo14.setBookFitGradeName("小学六年级");
        bookInfo14.setBookFitGradeId(6);
        bookInfo14.setBookFitSubjectName("数学");
        bookInfo14.setBookFitSubjectId(2);


        BookInfo bookInfo15 = new BookInfo();
        bookInfo15.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo15.setBookFitGradeName("小学六年级");
        bookInfo15.setBookFitGradeId(6);
        bookInfo15.setBookFitSubjectName("数学");
        bookInfo15.setBookFitSubjectId(2);


        BookInfo bookInfo16 = new BookInfo();
        bookInfo16.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo16.setBookFitGradeName("小学六年级");
        bookInfo16.setBookFitGradeId(6);
        bookInfo16.setBookFitSubjectName("数学");
        bookInfo16.setBookFitSubjectId(2);


        mServerBooks.add(bookInfo1);
        mServerBooks.add(bookInfo2);
        mServerBooks.add(bookInfo3);
        mServerBooks.add(bookInfo4);
        mServerBooks.add(bookInfo5);
        mServerBooks.add(bookInfo6);
        mServerBooks.add(bookInfo7);
        mServerBooks.add(bookInfo8);
        mServerBooks.add(bookInfo9);
        mServerBooks.add(bookInfo10);
        mServerBooks.add(bookInfo11);
        mServerBooks.add(bookInfo12);
        mServerBooks.add(bookInfo13);
        mServerBooks.add(bookInfo14);
        mServerBooks.add(bookInfo15);
        mServerBooks.add(bookInfo16);
//-----------------------------------------------
        mServerBooks.add(bookInfo1);
        mServerBooks.add(bookInfo2);
        mServerBooks.add(bookInfo3);
        mServerBooks.add(bookInfo4);
        mServerBooks.add(bookInfo5);
        mServerBooks.add(bookInfo6);
        mServerBooks.add(bookInfo7);
        mServerBooks.add(bookInfo8);
        mServerBooks.add(bookInfo9);
        mServerBooks.add(bookInfo10);
        mServerBooks.add(bookInfo11);
        mServerBooks.add(bookInfo12);
        mServerBooks.add(bookInfo13);
        mServerBooks.add(bookInfo14);
        mServerBooks.add(bookInfo15);
        mServerBooks.add(bookInfo16);
//-----------------------------------------------
        mServerBooks.add(bookInfo1);
        mServerBooks.add(bookInfo2);
        mServerBooks.add(bookInfo3);
        mServerBooks.add(bookInfo4);
        mServerBooks.add(bookInfo5);
        mServerBooks.add(bookInfo6);
        mServerBooks.add(bookInfo7);
        mServerBooks.add(bookInfo8);
        mServerBooks.add(bookInfo9);
        mServerBooks.add(bookInfo10);
        mServerBooks.add(bookInfo11);
        mServerBooks.add(bookInfo12);
        mServerBooks.add(bookInfo13);
        mServerBooks.add(bookInfo14);
        mServerBooks.add(bookInfo15);
        mServerBooks.add(bookInfo16);


        refreshFirstAdapterData();
    }


    /**
     * 设置一行item 显示大小
     * @param view
     */
    private void setLineSize(ViewGroup view ){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 60);
        if (view == mGroupSub){
            params.setMargins(0,15,0,0);
        }
        view.setLayoutParams(params);
    }

    /**
     * 设置全部大小
     */
    private void  setMoreSize(ViewGroup view  ){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (view == mGroupSub){
            params.setMargins(0,15,0,0);
        }
        view.setLayoutParams(params);
    }


    @Override
    public void updataNote(long noteId, int noteStyle, String subject, String noteTile) {

        LogUtils.i("更新笔记");
        if (mServerBooks == null || mServerBooks.size()<0){
            return;
        }
        for (BookInfo info : mServerBooks) {
            if (info.getBookFitNoteId() == noteId) {
                info.setNoteStyle(noteStyle);
                break;
            }
        }
    }



    @Override
    public void removeNote(int noteId) {

    }


    private Observable<List<BookInfo>> getObservable() {
        return Observable.create(new Observable.OnSubscribe<List<BookInfo>>() {
            @Override
            public void call(Subscriber<? super List<BookInfo>> subscriber) {

                List<CacheJsonInfo> infos = DataSupport.where("cacheID = ? ", ProtocolId.PROTOCOL_ID_All_TEXT_BOOK+"").find(CacheJsonInfo.class);
                if (infos != null && infos.size() > 0) {
                    subscriber.onNext(GsonUtil.fromJson(infos.get(0).getCacheJSON(), BookShelfProtocol.class).getBookList());
                }else{
                    mLoadingNull.setVisibility(View.VISIBLE);
                }
                subscriber.onCompleted();
            }
        });
    }

    private Subscriber<List<BookInfo>> getSubscriber() {
        return new Subscriber<List<BookInfo>>() {
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
                Log.e(TAG, "onCompleted...");
                dialog.dismiss();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<BookInfo> bookInfos) {
                freshUI(bookInfos);
            }
        };
    }

    private void freshUI(List<BookInfo> bookInfos) {
        if (bookInfos!=null && bookInfos.size()>0){
            mLoadingNull.setVisibility(View.GONE);
            mServerBooks.clear();
            mServerBooks.addAll(bookInfos);
            refreshFirstAdapterData();
        }
        else {
            mLoadingNull.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void handleEvent() {
        handleTextBookEvent();
        super.handleEvent();
    }

    private void handleTextBookEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof BookShelfProtocol && !mHide &&  mTextBookCall!=null) { //网数据库存储 协议返回的JSON
                    BookShelfProtocol shelfProtocol = (BookShelfProtocol) o;
                    List<BookInfo> bookInfos = shelfProtocol.getBookList();
                    freshUI(bookInfos);
                }else if (o instanceof String && !mHide && StringUtils.isEquals((String) o,ProtocolId.PROTOCOL_ID_All_TEXT_BOOK+"")){
                    LogUtils.i("yuanye...请求服务器 加载出错 ---AllTextBookFragment");
                    mSub= getObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(getSubscriber());
                }
            }
        }));
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFitGradeView =null;
        mBookView =null;
        mSubjectView =null;
        if (mAdaptetFragmentAllTextBook!=null){
            mAdaptetFragmentAllTextBook = null ;
        }

        if (mSubjectAdapter !=null){
            mSubjectAdapter = null ;
        }
        if (mFitGradeAdapter !=null){
            mFitGradeAdapter = null ;
        }
    }
}