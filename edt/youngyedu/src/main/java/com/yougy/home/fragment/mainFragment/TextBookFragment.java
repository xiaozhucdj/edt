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

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.download.DownloadListener;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.fragment.BFragment;
import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.DownloadManager;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.nohttp.DownInfo;
import com.yougy.common.protocol.callback.NewTextBookCallBack;
import com.yougy.common.protocol.request.NewBookShelfReq;
import com.yougy.common.protocol.response.NewBookShelfRep;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.activity.ControlFragmentActivity;
import com.yougy.home.adapter.BookAdapter;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.home.bean.NoteInfo;
import com.yougy.init.bean.BookInfo;
import com.yougy.ui.activity.R;
import com.yougy.view.CustomGridLayoutManager;
import com.yougy.view.DividerGridItemDecoration;
import com.yougy.view.dialog.DownBookDialog;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

import static android.content.ContentValues.TAG;
import static com.yougy.common.global.FileContonst.PAGE_COUNTS;
import static com.yougy.common.global.FileContonst.PAGE_LINES;

/**
 * Created by Administrator on 2016/7/12.
 * 课本
 */
public class TextBookFragment extends BFragment implements View.OnClickListener, DownBookDialog.DownBookListener {
    /**
     * 适配器 数据
     */
    private List<BookInfo> mBooks = new ArrayList<>();
    private List<BookInfo> mCountBooks = new ArrayList<>();

    /***
     * 一页数据个数
     */
    private static final int COUNT_PER_PAGE = PAGE_COUNTS;
    /***
     * 当前翻页的角标
     */
    private int mPagerIndex;
    /***
     * 准备下载的图书
     */
    private BookInfo mDownInfo;
    private ViewGroup mRootView;
    private RecyclerView mRecyclerView;
    private BookAdapter mBookAdapter;
    private boolean mIsFist;
    private LinearLayout mLlPager;
    private DownBookDialog mDialog;
    //    private Subscription msb;
    private ViewGroup mLoadingNull;
    private NewTextBookCallBack mNewTextBookCallBack;
    private int mDownPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_book, null);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_View);
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(UIUtils.getContext()));
        CustomGridLayoutManager layout = new CustomGridLayoutManager(getActivity(), PAGE_LINES);
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
        notifyDataSetChanged();
        mLlPager = (LinearLayout) mRootView.findViewById(R.id.ll_page);
        mLoadingNull = (ViewGroup) mRootView.findViewById(R.id.loading_null);
        return mRootView;
    }

    private void itemClick(int position) {
        mDownPosition = position ;
        BookInfo info = mBooks.get(position);
        mDownInfo = info;
//        String filePath = FileUtils.getTextBookFilesDir() + info.getBookId() + ".pdf";
        if (!StringUtils.isEmpty( FileUtils.getBookFileName( info.getBookId() ,FileUtils.bookDir))) {
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
            extras.putInt(FileContonst.NOTE_SUBJECT_ID, info.getBookFitSubjectId());
            extras.putString(FileContonst.NOTE_SUBJECT_NAME, info.getBookFitSubjectName());
            //作业ID
            extras.putInt(FileContonst.HOME_WROK_ID, info.getBookFitHomeworkId());
            extras.putString(FileContonst.NOTE_TITLE, info.getBookFitNoteTitle());
            loadIntentWithExtras(ControlFragmentActivity.class, extras);
        } else {
            if (NetUtils.isNetConnected()) {
                if (mDialog == null) {
                    mDialog = new DownBookDialog(getActivity());
                    mDialog.setListener(TextBookFragment.this);
                }
                mDialog.show();
                mDialog.getBtnConfirm().setVisibility(View.VISIBLE);
                mDialog.setTitle(UIUtils.getString(R.string.down_book_defult));
            } else {
                showCancelAndDetermineDialog(R.string.jump_to_net);
            }
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
                //展示数据,服务器返回
                if (o instanceof NewBookShelfRep && !mHide && mNewTextBookCallBack != null) {
                    NewBookShelfRep shelfProtocol = (NewBookShelfRep) o;
                    List<BookInfo> bookInfos = shelfProtocol.getData();
                    freshUI(bookInfos);
                } else if (o instanceof String && !mHide && StringUtils.isEquals((String) o, NewProtocolManager.NewCacheId.CODE_CURRENT_BOOK + "")) {
                    //服务请求错误
                    freshUI(getCacheBooks(NewProtocolManager.NewCacheId.CODE_CURRENT_BOOK));
                }
            }
        }));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mIsFist = true;
    }

    private void freshUI(List<BookInfo> bookInfos) {
        mIsRefresh = false;
        mNewTextBookCallBack = null;
        if (bookInfos != null && bookInfos.size() > 0) {
            mLoadingNull.setVisibility(View.GONE);
            mCountBooks.clear();
            mCountBooks.addAll(bookInfos);
            initPages();
        } else {
            // 数据返回为null
            mLoadingNull.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.e(TAG, "onHiddenChanged......");
        LogUtils.i("yuanye ...text");
        if (!hidden) {
            if ((mIsFist && mCountBooks.size() == 0) || mIsRefresh) {
                loadData();
            }
        }
    }

    private void loadData() {
        LogUtils.i("yuanye////////load");
        if (NetUtils.isNetConnected()) {
            NewBookShelfReq req = new NewBookShelfReq();
            //设置学生ID
            req.setUserId(SpUtil.getAccountId());
            //设置缓存数据ID的key
            req.setCacheId(Integer.parseInt(NewProtocolManager.NewCacheId.CODE_CURRENT_BOOK));
            //设置年级
            req.setBookFitGradeName(SpUtil.getGradeName());
            req.setBookCategoryMatch(10000);
            mNewTextBookCallBack = new NewTextBookCallBack(getActivity(), req);
            NewProtocolManager.bookShelf(req, mNewTextBookCallBack);
        } else {
            Log.e(TAG, "query book from database...");
            freshUI(getCacheBooks(NewProtocolManager.NewCacheId.CODE_CURRENT_BOOK));
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
        DownInfo info = new DownInfo(mDownInfo.getBookDownload(), FileUtils.getTextBookFilesDir(), mDownInfo.getBookId() + "", true, false, mDownInfo.getBookId());
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
                mDialog.setTitle(UIUtils.getString(R.string.down_book_error));
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
                    //直接进入下载的图书
                    itemClick(mDownPosition);
                }
            }

            @Override
            public void onCancel(int what) {

            }
        });
    }

    /***
     * 刷新适配器数据
     */
    private void refreshAdapterData(View v) {
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
                notifyDataSetChanged();

                break;
        }

    }

    private void notifyDataSetChanged() {
        mBookAdapter.notifyDataSetChanged();
        EpdController.invalidate(mRootView, UpdateMode.GC);
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
        for (int index = 1; index <= counts; index++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 20;
            LogUtils.e(TAG, "getActivity is null ? " + (getActivity() == null));
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
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerView = null;
        if (mBookAdapter != null) {
            mBookAdapter = null;
        }
    }


    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        if (event.getType().equalsIgnoreCase(EventBusConstant.current_text_book)) {
            LogUtils.i("type .." + EventBusConstant.current_text_book);
            loadData();
        } else if (event.getType().equalsIgnoreCase(EventBusConstant.alter_note)) {
            LogUtils.i("type .." + EventBusConstant.alter_note);
            // 用户自己创建的笔记 不需要做处理
            NoteInfo noteInfo = (NoteInfo) event.getExtraData();
            if (noteInfo.getNoteId() < 0 || noteInfo.getNoteMark() > 0) {
                return;
            }
            // 后台对应的笔记只会修改样式
            if (mBooks != null && mBooks.size() > 0) {
                for (BookInfo info : mBooks) {
                    if (info.getBookFitNoteId() == noteInfo.getNoteId()) {
                        info.setNoteStyle(noteInfo.getNoteStyle());
                        break;
                    }
                }
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
}