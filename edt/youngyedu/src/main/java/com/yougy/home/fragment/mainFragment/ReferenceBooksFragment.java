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
import com.yougy.home.activity.ControlFragmentActivity;
import com.yougy.home.activity.MainActivity;
import com.yougy.home.adapter.BookAdapter;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.home.bean.CacheJsonInfo;
import com.yougy.home.imple.RefreshBooksListener;
import com.yougy.home.imple.SearchReferenceBooksListener;
import com.yougy.init.bean.BookInfo;
import com.yougy.ui.activity.R;
import com.yougy.view.CustomGridLayoutManager;
import com.yougy.view.DividerGridItemDecoration;
import com.yougy.view.dialog.DownBookDialog;
import com.yougy.view.dialog.LoadingProgressDialog;
import com.yougy.view.dialog.SearchBookDialog;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2016/7/12.
 * 课外书
 */
public class ReferenceBooksFragment extends BFragment implements View.OnClickListener, DownBookDialog.DownBookListener {
    /**
     * 适配器 数据
     */
    private List<BookInfo> mBooks = new ArrayList<>();
    /***
     * 书的总共页数
     */
    private List<BookInfo> mCountBooks = new ArrayList<>();

    /***
     * 书的总共页数
     */
    private List<BookInfo> mSerachBooks = new ArrayList<>();
    /**
     * 服务器返回数据
     */
    private List<BookInfo> mServerBooks = new ArrayList<>();
    /***
     * 一页数据个数
     */
    private static final int COUNT_PER_PAGE = 16;

    /***
     * 搜索结果 显示的页数
     */
    private static final int COUNT_SEARCH_PAGE = 12;
    /***
     * 当前翻页的角标
     */
    private int mPagerIndex;

    /***
     * 搜索的 key
     */
    private String mSearchKey;


    private ViewGroup mRootView;
    private RecyclerView mRecyclerView;
    private BookAdapter mBookAdapter;
    private boolean mIsFist;
    private LinearLayout mLlPager;
    /***
     * 搜索顶部显示
     */
    private LinearLayout mLlSearchKeyTitle;
    /**
     * 搜索结果为空
     */
    private LinearLayout mLlSearchKeyResut;
    /**
     * 返回全部课外书
     */
    private TextView mTvAllBooks;
    /***
     * 搜索结果显示的关键字
     */
    private TextView mTvSerachKeyContext;
    /***
     * 搜索内容为空时候提示 title 包含key
     */
    private TextView mTvSerachErrorTitle;
    private SearchBookDialog mSearchDialog;
    private DownBookDialog mDialog;
    private BookInfo mDownInfo;
    private TextBookCallBack mTextBookCall;
    private Subscription mSub;
    private ViewGroup mLoadingNull;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_book, null);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_View);
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(UIUtils.getContext()));
        CustomGridLayoutManager layout = new CustomGridLayoutManager(getActivity(), 4);
        layout.setScrollEnabled(false);
        mRecyclerView.setLayoutManager(layout);

        mBookAdapter = new BookAdapter(getActivity(), mBooks, this);
        mRecyclerView.setAdapter(mBookAdapter);
        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                itemClick(vh.getAdapterPosition());
            }
        });
        mBookAdapter.notifyDataSetChanged();

        mLlPager = (LinearLayout) mRootView.findViewById(R.id.ll_page);

        mLlSearchKeyTitle = (LinearLayout) mRootView.findViewById(R.id.ll_referenceKey);

        mLlSearchKeyResut = (LinearLayout) mRootView.findViewById(R.id.ll_referenceResult);


        mTvAllBooks = (TextView) mRootView.findViewById(R.id.tv_referenceBooks);
        mTvAllBooks.setOnClickListener(this);

        /**
         * 搜索结果错误提示关键字
         */
        mTvSerachKeyContext = (TextView) mRootView.findViewById(R.id.tv_referenceKeyContext);
        mTvSerachErrorTitle = (TextView) mRootView.findViewById(R.id.tv_referenceResultTitle);
        /**设置搜索的点击事件*/
        setSearchListener();
        mLoadingNull = (ViewGroup) mRootView.findViewById(R.id.loading_null);
        return mRootView;
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

        if (mIsFist && !hidden && mCountBooks.size() == 0) {
            loadData();
        }
        if (!hidden) {
            LogUtils.i("当前--课外书");
            /** 刷新列表*/
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
        if (YougyApplicationManager.isWifiAvailable()) {
            mTextBookCall = new TextBookCallBack(getActivity(),ProtocolId.ROTOCOL_ID_ALL_REFERENCE_BOOK);
            mTextBookCall.setTermIndex(-1);
            mTextBookCall.setCategoryId(30000);
            Log.e(TAG, "query book from server...");
            ProtocolManager.bookShelfProtocol(Integer.parseInt(SpUtil.getAccountId()), -1, 30000, "", ProtocolId.ROTOCOL_ID_ALL_REFERENCE_BOOK, mTextBookCall);
        } else {
            Log.e(TAG, "query book from database...");
            mSub = getObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(getSubscriber());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSub != null) {
            mSub.unsubscribe();
        }
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
                mDialog.getBtnConfirm().setVisibility(View.VISIBLE);
            }

            @Override
            public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
                LogUtils.i("  onStart     what ........" + what);

            }

            @Override
            public void onProgress(int what, int progress, long fileCount) {
                mDialog.setTitle(String.format(getString(R.string.down_book_loading), progress + "%"));
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


    public void loadIntentWithExtras(Class<? extends Activity> cls, Bundle extras) {
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtras(extras);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_referenceBooks: //返回全部课课外书
                mLlSearchKeyTitle.setVisibility(View.GONE);
                mLlSearchKeyResut.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                initPages(mServerBooks, COUNT_PER_PAGE);
                break;
            case R.id.page_btn:
                refreshAdapterData(v);
                break;
        }

    }

    //取消下载
    @Override
    public void onCancelListener() {
        //判断是否下载
        DownloadManager.cancel();
        mDialog.dismiss();
    }

    //确定下载
    @Override
    public void onConfirmListener() {
        mDialog.getBtnConfirm().setVisibility(View.GONE);
        List<DownInfo> mFiles = new ArrayList<>();
        DownInfo info = new DownInfo(mDownInfo.getBookDownload(), FileUtils.getTextBookFilesDir(), mDownInfo.getBookId() + ".pdf", true, false, mDownInfo.getBookId());
        info.setBookName(mDownInfo.getBookTitle());
        mFiles.add(info);
        downBook(mFiles);
    }


    private void freshUI(List<BookInfo> bookInfos) {
        if (bookInfos!=null && bookInfos.size()>0){
            mServerBooks.clear();
            mServerBooks.addAll(bookInfos);
            initPages(mServerBooks, COUNT_PER_PAGE);
        }
        else{
            // 数据返回为null
            mLoadingNull.setVisibility(View.VISIBLE);
        }
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
        mBooks.clear();

        if ((mPagerIndex - 1) * COUNT_PER_PAGE + COUNT_PER_PAGE > mCountBooks.size()) { // 不是 正数被
            mBooks.addAll(mCountBooks.subList((mPagerIndex - 1) * COUNT_PER_PAGE, mCountBooks.size()));
        } else {
            mBooks.addAll(mCountBooks.subList((mPagerIndex - 1) * COUNT_PER_PAGE, (mPagerIndex - 1) * COUNT_PER_PAGE + COUNT_PER_PAGE)); //正数被
        }
        mBookAdapter.notifyDataSetChanged();

    }

    /**
     * 初始化翻页角标
     */
    private void initPages(List infos, int count_page) {
        mCountBooks.clear();
        mCountBooks.addAll(infos);
        int counts = 0;
        int quotient = mCountBooks.size() / count_page;
        int remainder = mCountBooks.size() % count_page;
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
        if (mCountBooks.size() > count_page) { // 大于1页
            mBooks.addAll(mCountBooks.subList(0, count_page));
        } else {
            mBooks.addAll(mCountBooks.subList(0, mCountBooks.size()));
        }
        mBookAdapter.notifyDataSetChanged();
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

    /**
     * 监听搜索
     */
    private void setSearchListener() {
        SearchReferenceBooksListenerImple imple = new SearchReferenceBooksListenerImple();
        ((MainActivity) getActivity()).setSearchListener(imple);
    }


    private class SearchReferenceBooksListenerImple implements SearchReferenceBooksListener {

        @Override
        public void onSearchClickListener() {

            if (mServerBooks.size() < 0) {
                UIUtils.showToastSafe("你还没有购买课外书", Toast.LENGTH_SHORT);
                return;
            }

            if (mSearchDialog == null) {
                mSearchDialog = new SearchBookDialog(getActivity());
                mSearchDialog.setSearchListener(new SearchBookDialog.SearchListener() {
                    @Override
                    public void searClick() {
                        if (!StringUtils.isEmpty(mSearchDialog.getSearchKey())) {
                            mSearchKey = mSearchDialog.getSearchKey();

                            setSearchView(mSearchKey);
                            mSearchDialog.dismiss();
                        } else {
                            UIUtils.showToastSafe("请输入搜索内容", Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
            if (!mSearchDialog.isShowing()) {
                mSearchDialog.show();
            }
        }
    }

    /***
     * 设置搜索
     *
     * @param key
     */
    private void setSearchView(String key) {
        //删除之前的按钮
        mLlPager.removeAllViews();
        //清空上次搜索结果数据
        mSerachBooks.clear();
        //查询搜索数据
        for (BookInfo info : mServerBooks) {
            if (info.getBookTitle().contains(key)) {
                mSerachBooks.add(info);
            }
        }

        //根据查询结果 显示结果
        if (mSerachBooks != null && mSerachBooks.size() > 0) {
            mLlSearchKeyTitle.setVisibility(View.VISIBLE);
            mLlSearchKeyResut.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            //判断搜索是否有内容
            initPages(mSerachBooks, COUNT_SEARCH_PAGE);
        } else {
            mLlSearchKeyTitle.setVisibility(View.VISIBLE);
            mLlSearchKeyResut.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        //您搜索的关键词:"大"的结果
        String formatC = getResources().getString(R.string.search_result_context);
        String resultC = String.format(formatC, key);
        mTvSerachKeyContext.setText(resultC);

        //很抱歉,没有找到与"大"相关的课外书
        String formatT = getResources().getString(R.string.search_error_title);
        String resultT = String.format(formatT, key);
        mTvSerachErrorTitle.setText(resultT);
    }

    //////////////////////////RX////////////////////////////////////////////
    @Override
    protected void handleEvent() {
        handleTextBookEvent();
        super.handleEvent();
    }


    private void handleTextBookEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof BookShelfProtocol && !mHide && mTextBookCall != null) { //网数据库存储 协议返回的JSON
                    BookShelfProtocol shelfProtocol = (BookShelfProtocol) o;
                    List<BookInfo> bookInfos = shelfProtocol.getBookList();
                    freshUI(bookInfos);
                }else if (o instanceof String && !mHide && StringUtils.isEquals((String) o,ProtocolId.ROTOCOL_ID_ALL_REFERENCE_BOOK+"")){
                    LogUtils.i("yuanye...请求服务器 加载出错 ---ReferenceBooksFragment");
                    mSub = getObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(getSubscriber());
                }
            }
        }));
    }

    private Observable<List<BookInfo>> getObservable() {
        return Observable.create(new Observable.OnSubscribe<List<BookInfo>>() {
            @Override
            public void call(Subscriber<? super List<BookInfo>> subscriber) {

                List<CacheJsonInfo> infos = DataSupport.where("cacheID = ? ", ProtocolId.ROTOCOL_ID_ALL_REFERENCE_BOOK+"").find(CacheJsonInfo.class);
                if (infos != null && infos.size() > 0) {
                    subscriber.onNext(GsonUtil.fromJson(infos.get(0).getCacheJSON(), BookShelfProtocol.class).getBookList());
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
        bookInfo1.setBookTitle("老人与海");

        BookInfo bookInfo2 = new BookInfo();
        bookInfo2.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo2.setBookFitGradeName("小学二年级");
        bookInfo2.setBookFitGradeId(2);
        bookInfo2.setBookFitSubjectName("语文");
        bookInfo2.setBookFitSubjectId(1);
        bookInfo2.setBookTitle("老人与海2");

        BookInfo bookInfo3 = new BookInfo();
        //设置图片
        bookInfo3.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo3.setBookFitGradeName("小学三年级");
        bookInfo3.setBookFitGradeId(3);
        bookInfo3.setBookFitSubjectName("数学");
        bookInfo3.setBookFitSubjectId(2);
        bookInfo3.setBookTitle("老人与海3");

        BookInfo bookInfo4 = new BookInfo();
        //设置图片
        bookInfo4.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo4.setBookFitGradeName("小学四年级");
        bookInfo4.setBookFitGradeId(4);
        bookInfo4.setBookFitSubjectName("数学");
        bookInfo4.setBookFitSubjectId(2);
        bookInfo4.setBookTitle("老人与海4");

        BookInfo bookInfo5 = new BookInfo();
        bookInfo5.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo5.setBookFitGradeName("小学四年级");
        bookInfo5.setBookFitGradeId(4);
        bookInfo5.setBookFitSubjectName("数学");
        bookInfo5.setBookFitSubjectId(2);
        bookInfo5.setBookTitle("老人与海5");

        BookInfo bookInfo6 = new BookInfo();
        bookInfo6.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo6.setBookFitGradeName("小学六年级");
        bookInfo6.setBookFitGradeId(6);
        bookInfo6.setBookFitSubjectName("数学");
        bookInfo6.setBookFitSubjectId(2);
        bookInfo6.setBookTitle("老人与海6");

        BookInfo bookInfo7 = new BookInfo();
        bookInfo7.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo7.setBookFitGradeName("小学六年级");
        bookInfo7.setBookFitGradeId(6);
        bookInfo7.setBookFitSubjectName("数学");
        bookInfo7.setBookFitSubjectId(2);
        bookInfo7.setBookTitle("老人与海7");

        BookInfo bookInfo8 = new BookInfo();
        bookInfo8.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo8.setBookFitGradeName("小学六年级");
        bookInfo8.setBookFitGradeId(6);
        bookInfo8.setBookFitSubjectName("数学");
        bookInfo8.setBookFitSubjectId(2);
        bookInfo8.setBookTitle("老人与海8");

        BookInfo bookInfo9 = new BookInfo();
        bookInfo9.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo9.setBookFitGradeName("小学六年级");
        bookInfo9.setBookFitGradeId(6);
        bookInfo9.setBookFitSubjectName("数学");
        bookInfo9.setBookFitSubjectId(2);
        bookInfo9.setBookTitle("老人与海9");

        BookInfo bookInfo10 = new BookInfo();
        bookInfo10.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo10.setBookFitGradeName("小学六年级");
        bookInfo10.setBookFitGradeId(6);
        bookInfo10.setBookFitSubjectName("数学");
        bookInfo10.setBookFitSubjectId(2);
        bookInfo10.setBookTitle("老人与海10");


        BookInfo bookInfo11 = new BookInfo();
        bookInfo11.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo11.setBookFitGradeName("小学六年级");
        bookInfo11.setBookFitGradeId(6);
        bookInfo11.setBookFitSubjectName("数学");
        bookInfo11.setBookFitSubjectId(2);
        bookInfo11.setBookTitle("老人与海11");

        BookInfo bookInfo12 = new BookInfo();
        bookInfo12.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo12.setBookFitGradeName("小学六年级");
        bookInfo12.setBookFitGradeId(6);
        bookInfo12.setBookFitSubjectName("数学");
        bookInfo12.setBookFitSubjectId(2);
        bookInfo12.setBookTitle("老人与海12");

        BookInfo bookInfo13 = new BookInfo();
        bookInfo13.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo13.setBookFitGradeName("小学六年级");
        bookInfo13.setBookFitGradeId(6);
        bookInfo13.setBookFitSubjectName("数学");
        bookInfo13.setBookFitSubjectId(2);
        bookInfo13.setBookTitle("老人与海13");

        BookInfo bookInfo14 = new BookInfo();
        bookInfo14.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo14.setBookFitGradeName("小学六年级");
        bookInfo14.setBookFitGradeId(6);
        bookInfo14.setBookFitSubjectName("数学");
        bookInfo14.setBookFitSubjectId(2);
        bookInfo14.setBookTitle("袁野日记14");

        BookInfo bookInfo15 = new BookInfo();
        bookInfo15.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo15.setBookFitGradeName("小学六年级");
        bookInfo15.setBookFitGradeId(6);
        bookInfo15.setBookFitSubjectName("数学");
        bookInfo15.setBookFitSubjectId(2);
        bookInfo15.setBookTitle("袁野日记15");

        BookInfo bookInfo16 = new BookInfo();
        bookInfo16.setBookCover("http://192.168.12.2:8080/leke_platform/bookimgs/img20161031114500.png");
        bookInfo16.setBookFitGradeName("小学六年级");
        bookInfo16.setBookFitGradeId(6);
        bookInfo16.setBookFitSubjectName("数学");
        bookInfo16.setBookFitSubjectId(2);
        bookInfo16.setBookTitle("袁野日记16");

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
        initPages(mServerBooks, COUNT_PER_PAGE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerView =null;
        if (mBookAdapter !=null){
            mBookAdapter = null ;
        }
        if (mSearchDialog!=null){
            mSearchDialog = null ;
        }
    }
}


