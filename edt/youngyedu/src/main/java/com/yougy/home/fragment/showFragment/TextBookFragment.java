package com.yougy.home.fragment.showFragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.OutlineItem;
import com.artifex.mupdfdemo.pdf.View.MuPDFPageView;
import com.artifex.mupdfdemo.pdf.bean.OutlineActivityData;
import com.jakewharton.rxbinding.view.RxView;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.BookMarkAdapter;
import com.yougy.home.adapter.OutlineAdapter;
import com.yougy.home.bean.Book;
import com.yougy.home.bean.BookMarkInfo;
import com.yougy.home.bean.Note;
import com.yougy.home.imple.PageListener;
import com.yougy.rx_subscriber.BaseSubscriber;
import com.yougy.ui.activity.R;
import com.yougy.view.controlView.ControlView;
import com.yougy.view.dialog.BookMarksDialog;
import com.yougy.view.showView.MyFrameLayout;
import com.yougy.view.showView.TextThumbSeekBar;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Administrator on 2016/12/23.
 * TextBookFragment 查询数据放入子线程 ,翻页labl放子线程
 */
public class TextBookFragment extends BaseFragment implements AdapterView.OnItemClickListener, ControlView.PagerChangerListener, BookMarksDialog.DialogClickFinsihListener {

    private static final String TAG = "TextBookFragment";
    private MuPDFCore mCore;
    private ViewGroup mRlDirectory;
    private ListView mLvBookDirectory;
    private OutlineItem mOutlineItems[];
    private OutlineAdapter mBookAdapter;
    private LinearLayout bookNeedLayout;
    private ImageButton mBackPageBtn;
    private Button mBookMarkBtn;
    private Button mItemDirectory;
    private ImageView mBookMarkImg;
    private ImageView mDirectoryImg;
    private int mPageSliderRes;
    private BookMarkAdapter mBookMarkAdapter;
    private String mPdfFile;
    private TextThumbSeekBar mSeekbarPage;
    protected TextView mTvPageNumber;
    //
    private Map<Integer, BookMarkInfo> mBookMarks = new HashMap<>();
    //书签页码
    private int mCurrentMarksPage;
    private List<BookMarkInfo> mInfos = new ArrayList<>();
    /***
     * seekbar 前进后退
     */
    private Map<String, Integer> mProgresInfos = new HashMap<>();
    private String START = "START";
    private String END = "END";
    private boolean isBackPage = false;
    private static SparseArray<Note> noteSparse;
    /**
     * 是否隐藏seekbar整体
     */
    private boolean mRlPageVisible = true;
    private View viewItemDirectory;
    private View viewItemBookmarks;

    /**
     * PDF 总页数
     */
    private int mPageCounts;
    /**
     * 用来显示PDF 内容
     */
    private MuPDFPageView mMupdfView;
    private ImageView mBackPageNext;
    private ImageView mBackPageBack;
    private boolean mHide;

    private long start, end;
    private Subscription mSubDb;
    private PageListenerImple mPageListenerImple;


    private void printTakeTimes(String job) {
        LogUtils.e(TAG, job + " takes " + (end - start));
    }

    private void startTime() {
        start = System.currentTimeMillis();
    }

    private void endTime() {
        end = System.currentTimeMillis();
    }

    /**
     * by @onCreate
     */
    @Override
    protected void initDatas() {
        super.initDatas();
//      mPdfFile = FileUtils.getTextBookFilesDir() + mControlActivity.mBookId + ".pdf";
        mPdfFile = FileUtils.getBookFileName( mControlActivity.mBookId , FileUtils.bookDir) ;
        fileName = mControlActivity.mBookId + "";
    }

    /**
     * 解析PDF
     *
     * @param path ：文件路径
     * @return
     */
    private MuPDFCore openFile(String path) {
        MuPDFCore core = null;
        try {
            // 解析PDF 核心类
            core = new MuPDFCore(mContext, path);
            //删除 PDF 目录 ，需要回复数据
            OutlineActivityData.set(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return core;
    }

    /**
     * byo @nActivityCreated
     */
    @Override
    protected void initOtherView() {
        super.initOtherView();
        fileName = mControlActivity.mBookId + "";
        initView();
    }

    /**
     * 初始化布局
     **/
    private void initView() {
        //替换布局
        mStub.setLayoutResource(R.layout.pdf_layout2);
        mStub.inflate();
        mTextbookIv.setEnabled(false);
        //读取 xml文件 和初始化 mupdfView
        SharedPreferences prefs = ((Activity) mContext).getPreferences(Context.MODE_PRIVATE);
        mCurrentMarksPage = prefs.getInt("page" + fileName, 0);

        bookNeedLayout = (LinearLayout) mRoot.findViewById(R.id.book_need_layout);
        bookNeedLayout.setVisibility(View.VISIBLE);

        viewItemDirectory = mRoot.findViewById(R.id.view_item_directory);
        viewItemBookmarks = mRoot.findViewById(R.id.view_item_bookmarks);
        mRlDirectory = (ViewGroup) mRoot.findViewById(R.id.rl_directory);
        mLvBookDirectory = (ListView) mRoot.findViewById(R.id.lv_item_book_directory);
        mLvBookDirectory.setDividerHeight(0);
        mBookMarkBtn = (Button) mRoot.findViewById(R.id.btn_item_bookmarks);
        mItemDirectory = (Button) mRoot.findViewById(R.id.btn_item_directory);
        //seek
        mRl_page = (RelativeLayout) mRoot.findViewById(R.id.rl_page);
        mSeekbarPage = (TextThumbSeekBar) mRoot.findViewById(R.id.seekbar_page);
        mSeekbarPage.setVisibility(View.VISIBLE);

        mTvPageNumber = (TextView) mRoot.findViewById(R.id.tv_page_number);
        mTvPageNumber.setVisibility(View.INVISIBLE);

        mBookMarkImg = (ImageView) mRoot.findViewById(R.id.bookmark);
        mDirectoryImg = (ImageView) mRoot.findViewById(R.id.directory);
        mBookMarkerIv.setSelected(mBookMarks.containsKey(mCurrentMarksPage));
        mBackPageBtn = (ImageButton) mRoot.findViewById(R.id.btn_back_page);
        mBackPageBtn.setEnabled(false);

        ////////////////////////add layer  params//////////////////////////////
//        addLayerLayout();

        /**
         * 20160926添加next fram按钮
         */
        mBackPageBack = (ImageView) mRoot.findViewById(R.id.img_pageBack);
        backScription = RxView.clicks(mBackPageBack).throttleFirst(DURATION, TimeUnit.SECONDS).subscribe(getBackSubscriber());
        mBackPageNext = (ImageView) mRoot.findViewById(R.id.img_pageNext);
        nextScription = RxView.clicks(mBackPageNext).throttleFirst(DURATION, TimeUnit.SECONDS).subscribe(getNextSubscriber());

        //解析PDF
        mCore = openFile(mPdfFile);
        if (mCore != null) {
            initPDF();
            initDB();
        }
    }


    /**
     * 初始化 pdf
     */
    private void initPDF() {
        mPageCounts = mCore.countPages();
        mSeekbarPage.setPdfCounts(mPageCounts);
        Point point = new Point(UIUtils.getScreenWidth(), UIUtils.getScreenHeight());
        mMupdfView = new MuPDFPageView(getActivity(), mCore, point);
        mPageListenerImple = new PageListenerImple();
        mMupdfView.setPageListener(mPageListenerImple);
        mControlView = (ControlView) mRoot.findViewById(R.id.rl_pdf);
        mControlView.addView(mMupdfView, 0);
    }

    private class PageListenerImple implements PageListener {

        @Override
        public void onPangeFinishListener() {
            endTime();
            LogUtils.i("pdf......time ==" + (end - start));
            mBookMarkerIv.setSelected(mBookMarks.containsKey(mCurrentMarksPage));
            restViewState();
        }
    }

    /**
     * 初始化 数据库信息
     */
    private void initDB() {

        mSubDb = getObserver().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(getDBSubscriber());
    }

    /**
     * by @onActivityCreated
     */
    @Override
    protected void addListener() {
        super.addListener();
        mBookMarkBtn.setOnClickListener(this);
        mItemDirectory.setOnClickListener(this);
        mBackPageBtn.setOnClickListener(this);
        mRlDirectory.setOnClickListener(this);
        mBookMarkImg.setOnClickListener(this);
        mDirectoryImg.setOnClickListener(this);
        mLvBookDirectory.setOnItemClickListener(this);
        int smax = Math.max(mCore.countPages() - 1, 1);
        mPageSliderRes = ((10 + smax - 1) / smax) * 2;
        mSeekbarPage.setPageSliderRes(mPageSliderRes);
        mSeekbarPage.setMax((mPageCounts - 1) * mPageSliderRes);

        mSeekbarPage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                //设置进度
                mProgresInfos.put(END, seekBar.getProgress());
                LogUtils.i("yuanye seekbar progress =="+seekBar.getProgress());
                //设置按钮可以点击
                mBackPageBtn.setEnabled(true);

                requestPageTask((seekBar.getProgress() + mPageSliderRes / 2) / mPageSliderRes);

                if (mProgresInfos.get(START) < seekBar.getProgress()) {
                    isBackPage = true;
                    mBackPageBtn.setImageDrawable(UIUtils.getDrawable(R.drawable.img_btn_qianjin_select));
                } else {
                    isBackPage = false;
                    mBackPageBtn.setImageDrawable(UIUtils.getDrawable(R.drawable.img_btn_houtui_select));
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                mProgresInfos.put(START, seekBar.getProgress());
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                leaveScribbleMode(true);
                updatePageNumView((progress + mPageSliderRes / 2) / mPageSliderRes);
                mUndoIv.setEnabled(false);
                mRedoIv.setEnabled(false);
            }
        });
        mControlView.setPagerListener(this);
    }


    private void updatePageNumView(int index) {
        if (mCore == null)
            return;
        mTvPageNumber.setText(String.format(Locale.CHINA, "%d / %d", index + 1, mCore.countPages()));
    }

    @NonNull
    private BaseSubscriber<Void> getNextSubscriber() {
        return new BaseSubscriber<Void>() {
            @Override
            public void onNext(Void aVoid) {
                requestPageTask(mMupdfView.getPage() + 1);
            }
        };
    }

    @NonNull
    private BaseSubscriber<Void> getBackSubscriber() {
        return new BaseSubscriber<Void>() {
            @Override
            public void onNext(Void aVoid) {
                requestPageTask(mMupdfView.getPage() - 1);
            }
        };
    }

    private Observable getObserver() {
        return Observable.create(new Observable.OnSubscribe<Object>() {

            @Override
            public void call(Subscriber<? super Object> subscriber) {
                startTime();
                queryDB();
                endTime();
                LogUtils.i("db......time ==" + (end - start));
                subscriber.onCompleted();
            }
        });
    }

    /**
     * 查询数据库
     */
    private void queryDB() {
        List<Book> books = DataSupport.where("name = ? ", fileName).find(Book.class);
        LogUtils.i("books is : " + books);
        if (!books.isEmpty()) {
            book = books.get(0);
        } else {
            book = new Book();
            book.setName(fileName);
        }
        noteSparse = book.getNotesArray();
        mBookMarks.clear();
        List<BookMarkInfo> bookMarkInfos = DataSupport.where("fileName = ? ", fileName).find(BookMarkInfo.class);
        if (bookMarkInfos != null && bookMarkInfos.size() > 0) {
            // 查询数据List转成锁需要的map 方便 使用对应的是哪个标签
            mBookMarks = convert_list_to_map_with_java(bookMarkInfos);
        }
    }


    public Map<Integer, BookMarkInfo> convert_list_to_map_with_java(List<BookMarkInfo> bookMarkInfos) {

        Map<Integer, BookMarkInfo> bookMarks = new LinkedHashMap<>();
        for (BookMarkInfo info : bookMarkInfos) {
            bookMarks.put(info.getNumber(), info);
        }
        return bookMarks;
    }

    private Subscriber<Object> getDBSubscriber() {
        return new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                //请求当前页面PDF 图片
                requestPageTask(mCurrentMarksPage);
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Object o) {
            }
        };
    }


    private void requestPageTask(final int position) {
        LogUtils.i("requestPageTask");
        isNeedCutScreen = false;
        if (screenCutOptLayout != null) {
            screenCutOptLayout.setVisibility(View.GONE);
        }
        //在翻页的时候 由于速度很快 可能多次 调用数据库 那么我们采取
        //取消rxjava中 上一次的订阅
        if (initScription != null && initScription.isUnsubscribed()) {
            initScription.unsubscribe();
        }

        if (mNoteBookView != null) {
            EpdController.leaveScribbleMode(mNoteBookView);
        }
        mBackPageBack.setEnabled(false);
        mBackPageNext.setEnabled(false);
        mSeekbarPage.setClickable(false);

        if (mNoteBookView != null && mNoteBookView.isContentChanged()) {
            LogUtils.e(TAG, "save content.................");
            getSaveObservable().subscribeOn(Schedulers.io()).subscribe();
        }
        judgeFlipPage(position);
    }

    private void judgeFlipPage(int position) {
        if (position >= mPageCounts) {
            UIUtils.showToastSafe("当前是最后一页");
            mBackPageBack.setEnabled(true);
            mBackPageNext.setEnabled(true);
            mSeekbarPage.setClickable(true);
            return;
        }
        if (position < 0) {
            UIUtils.showToastSafe("当前是第一页");
            mBackPageBack.setEnabled(true);
            mBackPageNext.setEnabled(true);
            mSeekbarPage.setClickable(true);
            return;
        }
        mCurrentMarksPage = position;
        startTime();
        mMupdfView.setPage(position, mCore);
    }

    @Override
    public void smartMoveBackwards() {
        requestPageTask(mMupdfView.getPage() - 1);
    }

    @Override
    public void smartMoveForwards() {
        requestPageTask(mMupdfView.getPage() + 1);
    }

    @Override
    public void onTapMainDocArea() {
        if (mRlPageVisible) {
            hideSeekbarLayout();
        } else {
            showSeekbarLayout();
        }
        mRlPageVisible = !mRlPageVisible;
    }

    /////////////////////////////////////////////


    /**
     * 设置seekbar点击 前进后退
     */
    private void seekbarGoAndForward() {
        int progress;
        if (!isBackPage) {
            isBackPage = true;
            progress = mProgresInfos.get(END);
            mBackPageBtn.setImageDrawable(UIUtils.getDrawable(R.drawable.img_btn_qianjin_select));
        } else {
            isBackPage = false;
            progress = mProgresInfos.get(START);
            mBackPageBtn.setImageDrawable(UIUtils.getDrawable(R.drawable.img_btn_houtui_select));
        }
        requestPageTask((progress + mPageSliderRes / 2) / mPageSliderRes);
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_item_directory:
                showDirectoryAdapter();
                break;
            case R.id.btn_item_bookmarks:
                showPdfMarkAdapter();
                break;
            case R.id.rl_directory:
                mRlDirectory.setVisibility(View.GONE);
                break;
            case R.id.btn_back_page:
                seekbarGoAndForward();
                break;
            case R.id.bookmark:
                showMarkDialog();
                break;
            case R.id.directory:
                showDirectory();
                break;
        }
    }

    private void showPdfMarkAdapter() {
        setMarkAndDirectoryClickTextChange(false);
        //刷新数据
        mInfos.clear();
        if (mBookMarks.size() > 0) {
            mInfos.addAll(new ArrayList<>(mBookMarks.values()));
        }
        if (mBookMarkAdapter == null) {
            mBookMarkAdapter = new BookMarkAdapter(mInfos, mContext);
        }
        mLvBookDirectory.setAdapter(mBookMarkAdapter);
        mBookMarkAdapter.notifyDataSetChanged();
    }

    private void showDirectoryAdapter() {
        setMarkAndDirectoryClickTextChange(true);
        // 增加判断 当获取目录为空
        if (mOutlineItems != null && mOutlineItems.length > 0 && mBookAdapter != null) {
            mLvBookDirectory.setAdapter(mBookAdapter);
            mBookAdapter.notifyDataSetChanged();
            mLvBookDirectory.setSelection(OutlineActivityData.get().position);
        } else {
            mBookAdapter = new OutlineAdapter(((Activity) mContext).getLayoutInflater(), mOutlineItems);
            mLvBookDirectory.setAdapter(mBookAdapter);
            mBookAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置 目录 书签 下划线 和文本颜色变化
     */
    private void setMarkAndDirectoryClickTextChange(boolean isDirectory) {


        viewItemDirectory.setVisibility(isDirectory ? View.VISIBLE : View.INVISIBLE);
        viewItemBookmarks.setVisibility(!isDirectory ? View.VISIBLE : View.INVISIBLE);

        mBookMarkBtn.setSelected(!isDirectory);
        mItemDirectory.setSelected(isDirectory);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        mRlDirectory.setVisibility(View.GONE);
        if (parent.getAdapter() instanceof OutlineAdapter) {
            // 保持用户可以看见的 位置 ，因为一个目录会有可能出现很长
            OutlineActivityData.get().position = mLvBookDirectory.getFirstVisiblePosition();
            if (position >= 0) {
                requestPageTask(mOutlineItems[position].page);
            }

        } else if (parent.getAdapter() instanceof BookMarkAdapter) {
            requestPageTask(mInfos.get(position).getNumber() - 1);
        }
    }


    private BookMarksDialog mBookMarkDialog;

    public void showBookMarksDialog(String tag, BookMarksDialog.DialogMode dialogMode, String msg) {

        if (null == mBookMarkDialog) {
            mBookMarkDialog = new BookMarksDialog(mContext);
            mBookMarkDialog.setListener(this);
        }

        if (!mBookMarkDialog.isShowing()) {
            mBookMarkDialog.show();
            mBookMarkDialog.setTag(tag);
            mBookMarkDialog.setDialogMode(dialogMode);
            mBookMarkDialog.setEditBookMarksContent(msg);
        }
    }

    //dialog点击按钮
    @Override
    public void onDialogConfirmCancleClick(BookMarksDialog dialog, BookMarksDialog.ClickState clickState) {

        if (clickState == BookMarksDialog.ClickState.IMG_BTN_CLOSE_CLICKED) {

        } else if (clickState == BookMarksDialog.ClickState.BTN_CHANGE_CLICKED) {  // 修改标签

            String text = "";
            if (TextUtils.isEmpty(dialog.getEditBookMarksContent())) {
                text = "书签" + (mCurrentMarksPage + 1);
            } else {
                text = dialog.getEditBookMarksContent();
            }
            BookMarkInfo info = mBookMarks.get(mCurrentMarksPage);
            info.setMarkName(text);
            info.setCreatTime(DateUtils.getCalendarAndTimeString());


            ContentValues values = new ContentValues();
            values.put("markname", info.getMarkName());

            DataSupport.updateAll(BookMarkInfo.class, values, "filename = ? and number = ?", fileName, "" + info.getNumber());

        } else if (clickState == BookMarksDialog.ClickState.BTN_DELETE_CLICKED) { //删除标签
            BookMarkInfo info = mBookMarks.remove(mCurrentMarksPage);
            info.delete();

        } else if (clickState == BookMarksDialog.ClickState.BTN_ADD_CLICKED) { //添加标签

            String text = "";
            if (TextUtils.isEmpty(dialog.getEditBookMarksContent())) {
                text = "书签" + (mCurrentMarksPage + 1);
            } else {
                text = dialog.getEditBookMarksContent();
            }

            BookMarkInfo bookMarkInfo = new BookMarkInfo();
            bookMarkInfo.setMarkName(text);
            bookMarkInfo.setNumber(mCurrentMarksPage + 1);
            bookMarkInfo.setCreatTime(DateUtils.getCalendarAndTimeString());
            bookMarkInfo.setFileName(fileName);
            mBookMarks.put(mCurrentMarksPage, bookMarkInfo);
            bookMarkInfo.save();
        }

        /**
         * 更新 修改 删除 标签后  需要更新 UI
         */
        mBookMarkerIv.setSelected(mBookMarks.containsKey(mCurrentMarksPage));

        dialog.dismiss();
    }


    /**
     * 设置对话框 添加和修改标签
     */
    public void showMarkDialog() {
        if (mBookMarks.containsKey(mCurrentMarksPage)) {
            showBookMarksDialog("btn_bookmarks", BookMarksDialog.DialogMode.CHANGE_OR_DELETE, mBookMarks.get(mCurrentMarksPage).getMarkName());
        } else {
            showBookMarksDialog("btn_bookmarks", BookMarksDialog.DialogMode.ADD, "添加标签" + (mCurrentMarksPage + 1));
        }
    }


    /***
     * 点击目录
     */
    public void showDirectory() {
        {
            // 第一次进来 先获取 目录 ，以后就不需执行
            if (mOutlineItems == null || mOutlineItems.length == 0) {
                // 获取书的目录
                OutlineItem outline[] = mCore.getOutline();
                if (outline != null) {
                    // 设置 书的目录数据
                    OutlineActivityData.get().items = outline;
                    mOutlineItems = OutlineActivityData.get().items;
                    if (mBookAdapter == null) {
                        mBookAdapter = new OutlineAdapter(((Activity) mContext).getLayoutInflater(), mOutlineItems);
                    }
                }
            }
            // TODO: 增加判断 当获取目录为空
            if (mOutlineItems != null && mOutlineItems.length > 0 && mBookAdapter != null) {
                mLvBookDirectory.setAdapter(mBookAdapter);
                mBookAdapter.notifyDataSetChanged();
            }
            mRlDirectory.setVisibility(View.VISIBLE);
            setMarkAndDirectoryClickTextChange(true);


            if (mRlPageVisible) {
                mRlPageVisible = false;
                hideSeekbarLayout();
            }
        }
    }

    //TODO:增加 seeker 动画
    private void hideSeekbarLayout() {
/*        mRl_page.setVisibility(View.INVISIBLE);
        mSeekbarPage.setEnabled(false);
        mBackPageBtn.setEnabled(false);*/
    }

    //TODO:增加 seeker 动画
    private void showSeekbarLayout() {
/*        mRl_page.setVisibility(View.VISIBLE);
        mSeekbarPage.setEnabled(true);
        if (isBackPage) {
            mBackPageBtn.setEnabled(true);
        }*/
    }


    //
    //////////////////////////生命周期//////////////////////////////////////////////

    public void onDestroyView() {
        super.onDestroyView();
        if (mCore != null) {
            mCore.onDestroy();
        }
        mCore = null;

        if (!mSubDb.isUnsubscribed()) {
            mSubDb.unsubscribe();
        }
        mSubDb = null;

        if (mPageListenerImple != null) {
            mPageListenerImple = null;
        }
    }

    @Override
    public void onDestroy() {
        if (mCore != null) {
            mCore.onDestroy();
        }
        mCore = null;
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
        mHide = hidden;
        if (hidden) {
            saveFileToXML();
        }
        savePage();
        setPasteEnable();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveFileToXML();
    }


    /***
     * 保存 当前PDF 离开的页数 到XML文件
     */
    private void saveFileToXML() {
        if (fileName != null && mCore != null) {
            SharedPreferences prefs = ((Activity) mContext).getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putInt("page" + fileName, mCurrentMarksPage);
            edit.apply();
        }
    }


    //-------------------图层 操作 和数据----------------------------//


    /**
     * 添加图层
     */

    private int mOldPage;

    private void addLayerLayout() {
        mOldPage = mCurrentMarksPage;

        mFrameLayout = (MyFrameLayout) UIUtils.inflate(R.layout.notebook_item);
        final Note note = noteSparse.get(mCurrentMarksPage);
        LogUtils.e(TAG, " Note is : " + note);
        initNoteView();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 60, 0, 111);
        mFrameLayout.addView(mNoteBookView, params);
        mControlView.addView(mFrameLayout, 1);
        initScription = getLinesObservable(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber());
    }

    @NonNull
    private Subscriber<Note> getSubscriber() {
        return new BaseSubscriber<Note>() {
            @Override
            public void onNext(Note note) {
                initNoteItem(note);
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                LogUtils.i(".....initNoteItem");
            }
        };
    }

    @NonNull
    private Observable<Note> getLinesObservable(final Note note) {
        return Observable.create(new Observable.OnSubscribe<Note>() {
            @Override
            public void call(Subscriber<? super Note> subscriber) {
                if (note != null) {
                    note.getLines();
                    note.getLabelList().size();
                    note.getLines().size();
                    note.getPhotographList().size();
                    note.getDiagramList().size();
                    subscriber.onNext(note);
                }
                subscriber.onCompleted();
            }
        });
    }


    /**
     * 移除图层
     */
    private void removerLayerLayout() {
        if (null != mFrameLayout) {
//            saveContent(false);
            mControlView.removeView(mFrameLayout);
        }
    }

    private void moveLayerLayout() {
        position = mCurrentMarksPage;
        mNote = noteSparse.get(mCurrentMarksPage);
        if (mNote == null) {
            mNote = new Note();
        }
        mNote.setBookPageNum(mCurrentMarksPage);
    }


    @Override
    public void prevPageForKey() {
        super.prevPageForKey();
        if (!mHide) {
            if (mBackPageBack.isEnabled() && mBackPageNext.isEnabled() && mSeekbarPage.isClickable() && mBackPageBack!=null) {
                mBackPageBack.callOnClick();
            }
        }
    }

    @Override
    public void nextPageForKey() {
        super.nextPageForKey();
        if (!mHide) {
            if (mBackPageBack.isEnabled() && mBackPageNext.isEnabled() && mSeekbarPage.isClickable() && mBackPageNext!=null) {
                mBackPageNext.callOnClick();
            }
        }
    }


    /**
     * 重新UI 状态
     */
    private void restViewState() {
        if (mNoteBookView != null) {
            EpdController.leaveScribbleMode(mNoteBookView);
        }
        removerLayerLayout();
        addLayerLayout();
        moveLayerLayout();
        initSeekbarAndTextNumber();
        mBackPageBack.setEnabled(true);
        mBackPageNext.setEnabled(true);
        mSeekbarPage.setClickable(true);
    }

    private void initSeekbarAndTextNumber() {
        if (mCore == null)
            return;
        // 获取 当前显示的 PDF 角标
        final int index = mCurrentMarksPage;
        // 更新显示
        updatePageNumView(index);
        mSeekbarPage.setProgress(index * mPageSliderRes);
    }
}