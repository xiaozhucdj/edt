package com.yougy.home.fragment.mainFragment;

import android.app.Activity;
import android.content.Context;
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

import com.frank.etude.pageable.PageBtnBar;
import com.frank.etude.pageable.PageBtnBarAdapter;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.fragment.BFragment;
import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.media.file.DownFileListener;
import com.yougy.common.media.file.DownFileManager;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.protocol.request.NewBookShelfReq;
import com.yougy.common.utils.DataCacheUtils;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.RefreshUtil;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.activity.ControlFragmentActivity;
import com.yougy.home.adapter.BookAdapter;
import com.yougy.init.bean.BookInfo;
import com.yougy.shop.activity.BookShopActivityDB;
import com.yougy.ui.activity.R;
import com.yougy.view.CustomGridLayoutManager;
import com.yougy.view.dialog.SearchBookDialog;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2016/7/12.
 * 课外书
 */
public class ReferenceBooksFragment extends BFragment implements View.OnClickListener {
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
    private static final int COUNT_PER_PAGE = FileContonst.PAGE_COUNTS;


    /***
     * 搜索结果 显示的页数
     */
    private static final int COUNT_SEARCH_PAGE = FileContonst.SEARCH_PAGE_COUNTS;
    /***
     * 当前翻页的角标
     */

    /***
     * 搜索的 key
     */
    private String mSearchKey;
    private RecyclerView mRecyclerView;
    private BookAdapter mBookAdapter;
    private boolean mIsFist;
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
    private ViewGroup mLoadingNull;
    private int mDownPosition;
    private DividerItemDecoration divider;
    private PageBtnBar mPageBtnBar;
    private BookInfo mAddBook;
    private DownFileManager mDownFileManager;

    private synchronized BookInfo getAddBook() {
        if (mAddBook == null) {
            mAddBook = new BookInfo();
            mAddBook.setBookId(-1);
        }
        return mAddBook;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_book, null);
        mRecyclerView = mRootView.findViewById(R.id.recycler_View);

        divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.adaper_divider_img_normal));
        mRecyclerView.addItemDecoration(divider);

        CustomGridLayoutManager layout = new CustomGridLayoutManager(getActivity(), FileContonst.PAGE_LINES);
        layout.setScrollEnabled(false);
        mRecyclerView.setLayoutManager(layout);
        mBookAdapter = new BookAdapter(getActivity(), mBooks, this);
        mBookAdapter.setReference(true);
        mBookAdapter.setOnItemClickListener(new BookAdapter.OnItemDeteteListener() {
            @Override
            public void onItemDeteteClickL(int position) {
                LogUtils.i("onItemDeteteClickL");
                //请求服务器后 ，删除当前的集合重新刷新下内存 计算角标
                removeBookInBookcase();
            }

            @Override
            public void onItemDeteteClickS(int position) {
                LogUtils.i("onItemDeteteClickS");
                //请求服务器后 ，删除当前的集合重新刷新下内存 计算角标
                removeBookInBookcase();
            }

            @Override
            public void onItemDownClickL(int position) {
                LogUtils.i("onItemDownClickL");
                if (mBooks.get(position).getBookId() == -1) {
                    if (NetUtils.isNetConnected()) {
                        Intent intent = new Intent(getActivity(),BookShopActivityDB.class);
                        intent.putExtra(BookShopActivityDB.CLASSIFY_POSITION,BookShopActivityDB.CLASSIFY_POSITION_EXTRA);
                        startActivity(intent);

                    } else {
                        showCancelAndDetermineDialog(R.string.jump_to_net);
                    }
                } else {
                    itemClick(position);
                }
            }

            @Override
            public void onItemDownClickS(int position) {
                LogUtils.i("onItemDownClickS");
                itemClick(position);
            }
        });

        mRecyclerView.setAdapter(mBookAdapter);

        mLlSearchKeyTitle = mRootView.findViewById(R.id.ll_referenceKey);
        mLlSearchKeyResut = mRootView.findViewById(R.id.ll_referenceResult);
        mTvAllBooks = mRootView.findViewById(R.id.tv_referenceBooks);
        mTvAllBooks.setOnClickListener(this);
        /**
         * 搜索结果错误提示关键字
         */
        mTvSerachKeyContext = mRootView.findViewById(R.id.tv_referenceKeyContext);
        mTvSerachErrorTitle = mRootView.findViewById(R.id.tv_referenceResultTitle);
        mLoadingNull = mRootView.findViewById(R.id.loading_null);

        mPageBtnBar = mRootView.findViewById(R.id.btn_bar);

        return mRootView;
    }

    private void itemClick(int position) {





        mDownPosition = position;
        BookInfo info = mBooks.get(position);
        if (NetUtils.isNetConnected()) {
            if (mDownFileManager == null) {
                mDownFileManager = new DownFileManager(getActivity(), new DownFileListener() {
                    @Override
                    public void onDownFileListenerCallBack(int state) {
                        LogUtils.e("text book state.." + state);
                        if (state != STATE_NO_SUPPORT_BOOK && state != STATE_SERVER_NO_BOOK_SOURCE) {
                            mBookAdapter.notifyItemChanged(mDownPosition);
                            jumpBundle();
                        }
                    }
                });
            }
            mDownFileManager.requestDownFile(info);
        } else {
            if (!StringUtils.isEmpty(FileUtils.getBookFileName(info.getBookId(), FileUtils.bookDir))) {
                jumpBundle();
            } else {
                showCancelAndDetermineDialog(R.string.jump_to_net);
            }
        }

    }


    private void jumpBundle() {
        BookInfo info = mBooks.get(mDownPosition);
        Bundle extras = new Bundle();
        //课本进入
        extras.putString(FileContonst.JUMP_FRAGMENT, FileContonst.JUMP_TEXT_BOOK);
        //笔记创建者
        extras.putInt(FileContonst.NOTE_CREATOR, -1);
        //分类码
        extras.putInt(FileContonst.CATEGORY_ID, info.getBookCategory());
        //笔记类型
        extras.putInt(FileContonst.NOTE_Style, info.getNoteStyle());
        extras.putInt(FileContonst.NOTE_SUBJECT_ID, info.getBookFitSubjectId());
        extras.putString(FileContonst.NOTE_SUBJECT_NAME, info.getBookFitSubjectName());
        //作业ID
        extras.putInt(FileContonst.HOME_WROK_ID, info.getBookFitHomeworkId());
        //笔记id
        extras.putInt(FileContonst.NOTE_ID, info.getBookFitNoteId());
        //图书id
        extras.putInt(FileContonst.BOOK_ID, info.getBookId());
        extras.putString(FileContonst.NOTE_TITLE, info.getBookFitNoteTitle());
        extras.putString(FileContonst.NOTE_TITLE, info.getBookFitNoteTitle());

        extras.putString(FileContonst.LOACL_BOOK_STATU_SCODE, info.getBookAudioStatusCode());
        extras.putString(FileContonst.LOACL_BOOK_BOOK_AUDIO, info.getBookAudio());
        extras.putString(FileContonst.LOACL_BOOK_BOOK_AUDIO_CONFIG, info.getBookAudioConfig());

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
        if (!hidden) {
            if ((mIsFist && mCountBooks.size() == 0) || mIsRefresh) {
                loadData();
            }
        }
    }


    private void loadData() {
        LogUtils.e("loadData ..."+tag);
        if (YoungyApplicationManager.isWifiAvailable()) {
            mLoadingNull.setVisibility(View.GONE);
            NewBookShelfReq req = new NewBookShelfReq();
            //设置学生ID
            req.setUserId(SpUtils.getAccountId());
            //设置缓存数据ID的key
            req.setCacheId(Integer.parseInt(NewProtocolManager.NewCacheId.CODE_REFERENCE_BOOK));
            //设置年级
            req.setBookCategoryMatch(30000);
            NetWorkManager.getBookShelf(req).compose(((BaseActivity)context).bindToLifecycle())
                    .subscribe(new Action1<List<BookInfo>>() {
                        @Override
                        public void call(List<BookInfo> bookInfos) {
                            ReferenceBooksFragment.this.freshUI(bookInfos);

                            if (bookInfos!=null && bookInfos.size()>0){
                                DataCacheUtils.putString(getActivity(),NewProtocolManager.NewCacheId.CODE_REFERENCE_BOOK,  GsonUtil.toJson(bookInfos));
                            }else{
                                DataCacheUtils.putString(getActivity(),NewProtocolManager.NewCacheId.CODE_REFERENCE_BOOK, "");
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            ReferenceBooksFragment.this.freshUI(ReferenceBooksFragment.this.getCacheBooks(NewProtocolManager.NewCacheId.CODE_REFERENCE_BOOK));
                        }
                    });
        } else {
            LogUtils.e(TAG, "query book from database...");
            freshUI(getCacheBooks(NewProtocolManager.NewCacheId.CODE_REFERENCE_BOOK));
        }
    }

    public void loadIntentWithExtras(Class<? extends Activity> cls, Bundle extras) {
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtras(extras);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_referenceBooks) {
            mLlSearchKeyTitle.setVisibility(View.GONE);
            mLlSearchKeyResut.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

            //TODO:设置 recyleview ,和adapter大小
            mRecyclerView.addItemDecoration(divider);
            CustomGridLayoutManager layout = new CustomGridLayoutManager(getActivity(), FileContonst.PAGE_LINES);
            layout.setScrollEnabled(false);
            mRecyclerView.setLayoutManager(layout);
            mBookAdapter.setPicL(true);
            initPages(mServerBooks, COUNT_PER_PAGE);
        }
    }

    private void freshUI(List<BookInfo> bookInfos) {
        mIsRefresh = false;
        mLoadingNull.setVisibility(View.GONE);
        LogUtils.i("freshUI.....freshUI");
        if (bookInfos != null && bookInfos.size() > 0) {
            mServerBooks.clear();
            mServerBooks.addAll(bookInfos);
            mServerBooks.add(0, getAddBook());
            initPages(mServerBooks, COUNT_PER_PAGE);
        } else {
            mServerBooks.clear();
            mServerBooks.add(getAddBook());
            initPages(mServerBooks, COUNT_PER_PAGE);
        }
    }

    /***
     * 刷新适配器数据
     */
    private void refreshAdapterData(int pagerIndex) {
        mBooks.clear();
        if ((pagerIndex - 1) * mCountsPage + mCountsPage > mCountBooks.size()) { // 不是 正数被
            mBooks.addAll(mCountBooks.subList((pagerIndex - 1) * mCountsPage, mCountBooks.size()));
        } else {
            mBooks.addAll(mCountBooks.subList((pagerIndex - 1) * mCountsPage, (pagerIndex - 1) * mCountsPage + mCountsPage)); //正数被
        }
        notifyDataSetChanged();
    }

    private int mCountsPage;

    /**
     * 初始化翻页角标
     */
    private void initPages(List infos, int count_page) {
        mCountsPage = count_page;
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
        notifyDataSetChanged();
    }


    private MyPageBtnBarAdapter mPageBtnBarAdapter;

    private class MyPageBtnBarAdapter extends PageBtnBarAdapter {
        public int count = 0;

        public void setCount(int count) {
            this.count = count;
        }

        public MyPageBtnBarAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        public int getPageBtnCount() {
            return count;
        }

        @Override
        public void onPageBtnClick(View btn, int btnIndex, String textInBtn) {
            refreshAdapterData(btnIndex + 1);
        }
    }

    /***
     * 添加按钮
     *
     * @param counts
     */
    private void addBtnCounts(int counts) {
        LogUtils.i("counts==" + counts);
        if (mPageBtnBarAdapter == null) {
            mPageBtnBarAdapter = new MyPageBtnBarAdapter(getContext());
            mPageBtnBar.setPageBarAdapter(mPageBtnBarAdapter);
        }
        mPageBtnBarAdapter.setCount(counts);
        mPageBtnBar.removeAllViews();
        mPageBtnBar.setCurrentSelectPageIndex(0);
        mPageBtnBar.refreshPageBar();

    }

    private String mKey;

    /***
     * 设置搜索
     *
     * @param key
     */
    private void setSearchView(String key) {
        mKey = key;
        //删除之前的按钮
        //清空上次搜索结果数据
        mSerachBooks.clear();
        //查询搜索数据
        for (BookInfo info : mServerBooks) {
            if (!StringUtils.isEmpty(info.getBookTitle()) && info.getBookTitle().contains(key)) {
                mSerachBooks.add(info);
            }
        }

        searchResult();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerView = null;
        if (mBookAdapter != null) {
            mBookAdapter = null;
        }
        if (mSearchDialog != null) {
            mSearchDialog = null;
        }
    }

    private void notifyDataSetChanged() {
        mBookAdapter.notifyDataSetChanged();
        RefreshUtil.invalidate(mRootView);
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        if (event.getType().equalsIgnoreCase(EventBusConstant.current_reference_book)) {
            LogUtils.i("type .." + EventBusConstant.current_reference_book);
            if (mLlSearchKeyTitle.getVisibility() == View.GONE) {
                loadData();
            }
        } else if (event.getType().equalsIgnoreCase(EventBusConstant.serch_reference)) {
            LogUtils.i("type .." + EventBusConstant.serch_reference);
            if (mServerBooks.size() < 0) {
                showCenterDetermineDialog(R.string.no_buy_book);
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
                            showCenterDetermineDialog(R.string.input_seartch_context);
                        }
                    }
                });
            }
            if (!mSearchDialog.isShowing()) {
                mSearchDialog.show();
            }
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

    @Override
    protected void onDownBookFinish() {
        super.onDownBookFinish();
        // TODO: 2018/5/16   更新下载完成 图片
        mBookAdapter.notifyItemChanged(mDownPosition);
        itemClick(mDownPosition);
    }


    private void localRemoveBook() {
        BookInfo removeBook = mBooks.remove(mBookAdapter.getDeletePs());
        String path = FileUtils.getBookFileName(removeBook.getBookId(), FileUtils.bookDir);
        if (!StringUtils.isEmpty(path)) {
            FileUtils.deleteFile(path);
        }
        mServerBooks.remove(removeBook);
        mCountBooks.remove(removeBook);
        if (mBookAdapter.isPicL()) {
            initPages(mServerBooks, COUNT_PER_PAGE);
        } else {
            mSerachBooks.remove(removeBook);
            searchResult();
        }
    }

    private void searchResult() {
        //根据查询结果 显示结果
        if (mSerachBooks != null && mSerachBooks.size() > 0) {
            mLlSearchKeyTitle.setVisibility(View.VISIBLE);
            mLlSearchKeyResut.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            //判断搜索是否有内容
            //TODO:设置 recyleview ,和adapter大小
            mRecyclerView.removeItemDecoration(divider);

            CustomGridLayoutManager layout = new CustomGridLayoutManager(getActivity(), FileContonst.SMALL_PAGE_LINES);
            layout.setScrollEnabled(false);
            mRecyclerView.setLayoutManager(layout);
            mBookAdapter.setPicL(false);
            initPages(mSerachBooks, COUNT_SEARCH_PAGE);
        } else {
            mLlSearchKeyTitle.setVisibility(View.VISIBLE);
            mLlSearchKeyResut.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        //您搜索的关键词:"大"的结果
        String formatC = getResources().getString(R.string.search_result_context);
        String resultC = String.format(formatC, mKey);
        mTvSerachKeyContext.setText(resultC);

        //很抱歉,没有找到与"大"相关的课外书
        String formatT = getResources().getString(R.string.search_error_title);
        String resultT = String.format(formatT, mKey);
        mTvSerachErrorTitle.setText(resultT);
    }


    private void removeBookInBookcase() {

        if (!NetUtils.isNetConnected()) {
            showCancelAndDetermineDialog(R.string.jump_to_net);
            return;
        }

        NetWorkManager.removeBookInBookcase(mBooks.get(mBookAdapter.getDeletePs()).getBookId(), SpUtils.getUserId()).subscribe(o -> {
            UIUtils.showToastSafe("移除图书成功");
            localRemoveBook();
        }, throwable -> {
            throwable.printStackTrace();
            UIUtils.showToastSafe("移除图书失败,请稍候再试");
        });

    }
}


