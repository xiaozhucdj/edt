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
import com.yougy.home.activity.ControlFragmentActivity;
import com.yougy.home.adapter.BookAdapter;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.init.bean.BookInfo;
import com.yougy.shop.activity.BookShopActivityDB;
import com.yougy.ui.activity.R;
import com.yougy.view.CustomGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2016/7/12.
 * 辅导书
 */
public class CoachBookFragment extends BFragment {
    /**
     * 适配器 数据
     */
    private List<BookInfo> mBooks = new ArrayList<>();
    /**
     * 当前按照（年级，和课本类别分类后的数据总集合）
     */
    private List<BookInfo> mCountBooks = new ArrayList<>();
    /***
     * 一页数据个数
     */
    private static final int COUNT_PER_PAGE = FileContonst.PAGE_COUNTS;
    private RecyclerView mRecyclerView;
    private BookAdapter mBookAdapter;
    private boolean mIsFist;

    private int mDownPosition;
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
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.adaper_divider_img_normal));
        mRecyclerView.addItemDecoration(divider);

        CustomGridLayoutManager layout = new CustomGridLayoutManager(getActivity(), FileContonst.PAGE_LINES);
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
        mPageBtnBar = mRootView.findViewById(R.id.btn_bar);
        return mRootView;
    }


    private void itemClick(int position) {
        mDownPosition = position;
        BookInfo info = mBooks.get(position);

        if (info.getBookId() == -1) {
            if (NetUtils.isNetConnected()) {
                Intent intent = new Intent(getActivity(), BookShopActivityDB.class);
                intent.putExtra(BookShopActivityDB.CLASSIFY_POSITION, BookShopActivityDB.CLASSIFY_POSITION_GUID);
                startActivity(intent);
            } else {
                showCancelAndDetermineDialog(R.string.jump_to_net);
            }
        } else {
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
        LogUtils.i("yuanye ....coach");
//        if (!hidden) {
//            if (mCountBooks.size() > 0) {
//                mBookAdapter.notifyDataSetChanged();
//            }
//            if ((mIsFist && mCountBooks.size() == 0) || mIsRefresh) {
//                loadData();
//            }
//        }
    }

    private void loadData() {
        LogUtils.e("loadData ..." + tag);
        if (YoungyApplicationManager.isWifiAvailable()) {
            NewBookShelfReq req = new NewBookShelfReq();
            //设置学生ID
            req.setUserId(SpUtils.getAccountId());
            //设置缓存数据ID的key
            req.setCacheId(Integer.parseInt(NewProtocolManager.NewCacheId.CODE_COACH_BOOK));
            //设置年级
            req.setBookFitGradeName();
            req.setBookCategoryMatch(20000);
            NetWorkManager.getBookShelf(req).compose(((BaseActivity) context).bindToLifecycle())
                    .subscribe(new Action1<List<BookInfo>>() {
                        @Override
                        public void call(List<BookInfo> bookInfos) {
                            CoachBookFragment.this.freshUI(bookInfos);
                            if (bookInfos != null && bookInfos.size() > 0) {
                                DataCacheUtils.putString(getActivity(), NewProtocolManager.NewCacheId.CODE_COACH_BOOK, GsonUtil.toJson(bookInfos));
                            } else {
                                DataCacheUtils.putString(getActivity(), NewProtocolManager.NewCacheId.CODE_COACH_BOOK, "");
                            }

                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            CoachBookFragment.this.freshUI(CoachBookFragment.this.getCacheBooks(NewProtocolManager.NewCacheId.CODE_COACH_BOOK));
                        }
                    });
        } else {
            LogUtils.e(TAG, "query book from database...");
            freshUI(getCacheBooks(NewProtocolManager.NewCacheId.CODE_COACH_BOOK));
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
        mBooks.clear();
        if ((pagerIndex - 1) * COUNT_PER_PAGE + COUNT_PER_PAGE > mCountBooks.size()) { // 不是 正数被
            mBooks.addAll(mCountBooks.subList((pagerIndex - 1) * COUNT_PER_PAGE, mCountBooks.size()));
        } else {
            mBooks.addAll(mCountBooks.subList((pagerIndex - 1) * COUNT_PER_PAGE, (pagerIndex - 1) * COUNT_PER_PAGE + COUNT_PER_PAGE)); //正数被
        }
        notifyDataSetChanged();

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
        notifyDataSetChanged();
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
                refreshAdapterData(btnIndex + 1);
            }
        });
        mPageBtnBar.setCurrentSelectPageIndex(0);
        mPageBtnBar.refreshPageBar();
    }


    private void freshUI(List<BookInfo> bookInfos) {
        mIsRefresh = false;
        mCountBooks.clear();
        mCountBooks.add(0, getAddBook());
        if (bookInfos != null && bookInfos.size() > 0) {
            mCountBooks.addAll(bookInfos);
        }
        initPages();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerView = null;
        if (mBookAdapter != null) {
            mBookAdapter = null;
        }
    }

    private void notifyDataSetChanged() {
        mBookAdapter.notifyDataSetChanged();
        RefreshUtil.invalidate(mRootView);
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        if (event.getType().equalsIgnoreCase(EventBusConstant.current_coach_book)) {
            LogUtils.i("type .." + EventBusConstant.current_coach_book);
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

    @Override
    protected void onDownBookFinish() {
        super.onDownBookFinish();
        mBookAdapter.notifyItemChanged(mDownPosition);
        itemClick(mDownPosition);
    }
}