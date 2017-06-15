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
import android.widget.Toast;

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
import com.yougy.common.manager.YougyApplicationManager;
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
import com.yougy.init.bean.BookInfo;
import com.yougy.ui.activity.R;
import com.yougy.view.CustomGridLayoutManager;
import com.yougy.view.DividerGridItemDecoration;
import com.yougy.view.dialog.DownBookDialog;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2016/7/12.
 * 辅导书
 */
public class CoachBookFragment extends BFragment implements View.OnClickListener, DownBookDialog.DownBookListener {
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
    private static final int COUNT_PER_PAGE =FileContonst.PAGE_COUNTS;
    /***
     * 当前翻页的角标
     */
    private int mPagerIndex;
    private ViewGroup mRootView;
    private RecyclerView mRecyclerView;
    private BookAdapter mBookAdapter;
    private boolean mIsFist;
    private LinearLayout mLlPager;
    private BookInfo mDownInfo;
    private DownBookDialog mDialog;

//    private Subscription mSub;
    private ViewGroup mLoadingNull;
    private NewTextBookCallBack mNewTextBookCallBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_book, null);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_View);
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(UIUtils.getContext()));
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
//        mBookAdapter.notifyDataSetChanged();
        mLlPager = (LinearLayout) mRootView.findViewById(R.id.ll_page);
        mLoadingNull = (ViewGroup) mRootView.findViewById(R.id.loading_null);
        return mRootView;
    }

    private void itemClick(int position){
        BookInfo info = mBooks.get(position);
        mDownInfo = info;
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
        if ((mIsFist && !hidden && mCountBooks.size() == 0)|| mIsRefresh ) {
            loadData();
        }
    }

    private void loadData() {
        if (YougyApplicationManager.isWifiAvailable()) {
            NewBookShelfReq req = new NewBookShelfReq();
            //设置学生ID
            req.setUserId(SpUtil.getAccountId());
            //设置缓存数据ID的key
            req.setCacheId(Integer.parseInt(NewProtocolManager.NewCacheId.CODE_COACH_BOOK));
            //设置年级
            req.setBookFitGradeName(SpUtil.getGradeName());
            req.setBookCategoryMatch(20000);
            mNewTextBookCallBack = new NewTextBookCallBack(getActivity() ,req) ;
            NewProtocolManager.bookShelf(req,mNewTextBookCallBack);

        } else {
            Log.e(TAG, "query book from database...");
            freshUI(getCacheBooks(NewProtocolManager.NewCacheId.CODE_COACH_BOOK));
//            mSub= getObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(getSubscriber());
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (mSub != null) {
//            mSub.unsubscribe();
//        }
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
        DownInfo info = new DownInfo(mDownInfo.getBookDownload(), FileUtils.getTextBookFilesDir(), mDownInfo.getBookId() + ".pdf", true, false, mDownInfo.getBookId());
        info.setBookName(mDownInfo.getBookTitle());
        mFiles.add(info);
        downBook(mFiles);
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
//                mBookAdapter.notifyDataSetChanged();
                notifyDataSetChanged();
                break;
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
//        mBookAdapter.notifyDataSetChanged();
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
                if (o instanceof NewBookShelfRep && !mHide &&  mNewTextBookCallBack!=null) { //网数据库存储 协议返回的JSON
                    NewBookShelfRep shelfProtocol = (NewBookShelfRep) o;
                    List<BookInfo> bookInfos = shelfProtocol.getData();
                    freshUI(bookInfos);
                }else if (o instanceof String && !mHide && StringUtils.isEquals((String) o,NewProtocolManager.NewCacheId.CODE_COACH_BOOK+"")){
                    LogUtils.i("yuanye...请求服务器 加载出错 ---CoachBookFragment");
//                    mSub= getObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(getSubscriber());
                    freshUI(getCacheBooks( NewProtocolManager.NewCacheId.CODE_COACH_BOOK));
                }
            }
        }));
    }

   /* private Observable<List<BookInfo>> getObservable() {
        return Observable.create(new Observable.OnSubscribe<List<BookInfo>>() {
            @Override
            public void call(Subscriber<? super List<BookInfo>> subscriber) {

                List<CacheJsonInfo> infos = DataSupport.where("cacheID = ? ", NewProtocolManager.NewCacheId.CODE_COACH_BOOK+"").find(CacheJsonInfo.class);
                if (infos != null && infos.size() > 0) {
                    subscriber.onNext(GsonUtil.fromJson(infos.get(0).getCacheJSON(), NewBookShelfRep.class).getData());
                }else{
                    UIUtils.post(new Runnable() {
                        @Override
                        public void run() {
                            mLoadingNull.setVisibility(View.VISIBLE);
                        }
                    }) ;
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
    }*/

    private void freshUI(List<BookInfo> bookInfos) {
        mIsRefresh =false ;
        if (bookInfos!=null && bookInfos.size()>0){
            mLoadingNull.setVisibility(View.GONE);
            mCountBooks.clear();
            mCountBooks.addAll(bookInfos);
            initPages();
        }else {
            mLoadingNull.setVisibility(View.VISIBLE);
        }

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerView =null;
        if (mBookAdapter !=null){
            mBookAdapter = null ;
        }
    }
    private void notifyDataSetChanged(){
        mBookAdapter.notifyDataSetChanged();
        EpdController.invalidate(mRootView, UpdateMode.GC);
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
    public void onResume() {
        super.onResume();
        if (mIsRefresh){
            loadData();
        }
    }
}