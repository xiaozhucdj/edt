package com.yougy.home.fragment.showFragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
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

import com.jakewharton.rxbinding.view.RxView;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContentEntry;
import com.onyx.reader.ReaderContract;
import com.onyx.reader.ReaderPresenter;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.global.FileContonst;
import com.yougy.common.media.AudioHelper;
import com.yougy.common.media.BookVoiceBean;
import com.yougy.common.media.MediaHelper;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.BookMarkAdapter;
import com.yougy.home.adapter.HandlerDirAdapter;
import com.yougy.home.bean.Book;
import com.yougy.home.bean.BookMarkInfo;
import com.yougy.home.bean.DirectoryModel;
import com.yougy.home.bean.Note;
import com.yougy.rx_subscriber.BaseSubscriber;
import com.yougy.ui.activity.R;
import com.yougy.view.dialog.BookMarksDialog;
import com.yougy.view.dialog.LoadingProgressDialog;
import com.yougy.view.dialog.OpenBookErrorDialog;
import com.yougy.view.showView.MyFrameLayout;
import com.yougy.view.showView.TextThumbSeekBar;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Administrator on 2016/12/23.
 * TextBookFragment 查询数据放入子线程 ,翻页labl放子线程
 */
public class HandleOnyxReaderFragment extends BaseFragment implements AdapterView.OnItemClickListener, BookMarksDialog.DialogClickFinsihListener, ReaderContract.ReaderView, MediaHelper.CompletionPlayerListener {

    private static final String TAG = "TextBookFragment";
    private ViewGroup mRlDirectory;
    private ListView mLvBookDirectory;
    private LinearLayout bookNeedLayout;
    private ImageButton mBackPageBtn;
    private Button mBookMarkBtn;
    private Button mItemDirectory;
    private int mPageSliderRes;
    private ImageView mDirectoryImg;
    private BookMarkAdapter mBookMarkAdapter;
    private String mPdfFile;
    private TextThumbSeekBar mSeekbarPage;
    //
    private Map<Integer, BookMarkInfo> mBookMarks = new HashMap<>();
    //书签页码
    private int mCurrentMarksPage;
    private List<BookMarkInfo> mInfos = new ArrayList<>();
    private static SparseArray<Note> noteSparse;
    /**
     * 是否隐藏seekbar整体
     */
    private View viewItemDirectory;
    private View viewItemBookmarks;

    /**
     * PDF 总页数
     */
    private int mPageCounts;
    /**
     * 用来显示PDF 内容
     */
    private ImageView mBackPageNext;
    private ImageView mBackPageBack;
    private boolean mHide;
    private Subscription mSubDb;
    private ImageView mOnyxImgView;
    private ReaderPresenter mReaderPresenter;
    private HandlerDirAdapter mHandlerDirAdapter;
    private LoadingProgressDialog mloadingDialog;
    private boolean mIsReferenceBook;
    private ImageView img_page_next;
    private ImageView img_page_back;
    private Button img_btn_hide;
    private Subscription backScription2;
    private Subscription nextScription2;
    private MediaHelper mMediaHelper;
    private boolean mIsOpenSelfAdapter;
    private boolean mIsOpenVoice;
    private FrameLayout fm_voice;
    private RelativeLayout rl_reader;
    private ImageButton btn_reader_page;
    private ImageButton btn_reader_pause;

    private String mVoiceBaseURL;
    private BookVoiceBean mVoiceBean;
    private Map<Integer, ImageView> mImgVoices = new HashMap<>();
    private boolean mIsPageReader;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            mIsReferenceBook = getArguments().getBoolean(FileContonst.IS_REFERENCE_BOOK);
            mIsOpenSelfAdapter = getArguments().getBoolean(FileContonst.IS_OPEN_SELF_ADAPTER);
            mIsOpenVoice = getArguments().getBoolean(FileContonst.IS_OPEN_VOICE);

//            mIsOpenSelfAdapter = true;
//            mIsOpenVoice = true;

        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * by @onCreate
     */
    @Override
    protected void initDatas() {
        super.initDatas();
//        mPdfFile = FileUtils.getTextBookFilesDir() + mControlActivity.mBookId + ".pdf";
        mPdfFile = FileUtils.getBookFileName(mControlActivity.mBookId, FileUtils.bookDir);
        fileName = mControlActivity.mBookId + "";
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
        mloadingDialog = new LoadingProgressDialog(getActivity());
        mloadingDialog.show();
        mloadingDialog.setTitle("请稍后...");
        mStub.setLayoutResource(R.layout.pdf_layout2);
        mStub.inflate();
        mTextbookIv.setEnabled(false);
        //读取 xml文件 和初始化 mupdfView
        SharedPreferences prefs = ((Activity) mContext).getPreferences(Context.MODE_PRIVATE);
        mCurrentMarksPage = prefs.getInt("page" + fileName, 0);

        bookNeedLayout = mRoot.findViewById(R.id.book_need_layout);
        bookNeedLayout.setVisibility(View.VISIBLE);

        viewItemDirectory = mRoot.findViewById(R.id.view_item_directory);
        viewItemBookmarks = mRoot.findViewById(R.id.view_item_bookmarks);
        mRlDirectory = mRoot.findViewById(R.id.rl_directory);
        mLvBookDirectory = mRoot.findViewById(R.id.lv_item_book_directory);
        mLvBookDirectory.setDividerHeight(0);
        mBookMarkBtn = mRoot.findViewById(R.id.btn_item_bookmarks);
        mItemDirectory = mRoot.findViewById(R.id.btn_item_directory);
        //seek
        mRl_page = mRoot.findViewById(R.id.rl_page);
        mSeekbarPage = mRoot.findViewById(R.id.seekbar_page);
        mRoot.findViewById(R.id.tv_page_number).setVisibility(View.GONE);
        mDirectoryImg = mRoot.findViewById(R.id.directory);
        mBookMarkerIv.setSelected(mBookMarks.containsKey(mCurrentMarksPage));
        mBackPageBtn = mRoot.findViewById(R.id.btn_back_page);
        mBackPageBtn.setEnabled(false);
        /**
         * 20160926添加next fram按钮
         */
        mBackPageBack = mRoot.findViewById(R.id.img_pageBack);
        backScription = RxView.clicks(mBackPageBack).throttleFirst(DURATION, TimeUnit.SECONDS).subscribe(getBackSubscriber());

        mBackPageNext = mRoot.findViewById(R.id.img_pageNext);
        nextScription = RxView.clicks(mBackPageNext).throttleFirst(DURATION, TimeUnit.SECONDS).subscribe(getNextSubscriber());


        img_page_next = mRoot.findViewById(R.id.img_page_next);
        img_page_back = mRoot.findViewById(R.id.img_page_back);
        img_btn_hide = mRoot.findViewById(R.id.img_btn_hide);
        img_btn_hide.setOnClickListener(this);
        backScription2 = RxView.clicks(img_page_back).throttleFirst(DURATION, TimeUnit.SECONDS).subscribe(getBackSubscriber());
        nextScription2 = RxView.clicks(img_page_next).throttleFirst(DURATION, TimeUnit.SECONDS).subscribe(getNextSubscriber());


        btn_reader_page = (ImageButton) mRoot.findViewById(R.id.btn_reader_page);
        btn_reader_page.setOnClickListener(this);
        btn_reader_page.setVisibility(View.GONE);


        btn_reader_pause = (ImageButton) mRoot.findViewById(R.id.btn_reader_pause);
        btn_reader_pause.setOnClickListener(this);
        btn_reader_pause.setVisibility(View.GONE);

        fm_voice = (FrameLayout) mRoot.findViewById(R.id.fm_voice);
        fm_voice.setVisibility(View.GONE);
        rl_reader = (RelativeLayout) mRoot.findViewById(R.id.rl_reader);
        rl_reader.setVisibility(View.GONE);


        btn_reader_page.setTag("连读");
        btn_reader_page.setSelected(false);

        btn_reader_pause.setTag("是否暂停");
        btn_reader_pause.setSelected(false);
        //解析PDF
        initPDF();
    }


    /**
     * 初始化 pdf
     */
    private void initPDF() {
        mControlView = mRoot.findViewById(R.id.rl_pdf);
        mOnyxImgView = new ImageView(getContext());
        //设置显示PDF 大小
        mOnyxImgView.setLayoutParams(new FrameLayout.LayoutParams(UIUtils.getScreenWidth(), UIUtils.getScreenHeight()));
        mControlView.addView(mOnyxImgView, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.e("HandleOnyxReaderFragment onStart------" + mControlActivity.mBookId);
        mVoiceBaseURL = FileUtils.getMediaMp3Path() + mControlActivity.mBookId + "/";
        getReaderPresenter().openDocument(mPdfFile, mControlActivity.mBookId + "");
    }


    /////////////////////////////////////////pdf sdk start////////////////////////////////////////////

    private ReaderContract.ReaderPresenter getReaderPresenter() {
        if (mReaderPresenter == null) {
            mReaderPresenter = new ReaderPresenter(this);
            //TODO: 设置  语音朗读 和 自身适应 大小 104010002
            mReaderPresenter.setmIsUserVoice(mIsOpenVoice);
            mReaderPresenter.setSelfAdapter(mIsOpenSelfAdapter);

        }
        return mReaderPresenter;
    }

    @Override
    public Context getViewContext() {
        return getActivity();
    }

    @Override
    public void updatePage(int page, Bitmap bitmap, BookVoiceBean bean) {
        if (mloadingDialog != null && mloadingDialog.isShowing()) {
            mloadingDialog.dismiss();
        }

        LogUtils.i("updatePage");
        position = page;
        mBookMarkerIv.setSelected(mBookMarks.containsKey(mCurrentMarksPage));
        EpdController.invalidate(mRoot, UpdateMode.GC);
        mOnyxImgView.setImageBitmap(bitmap);
        restViewState();

        if (mIsOpenVoice) {
            mVoiceBean = bean;
            fm_voice.removeAllViews();
            if (bean == null) {
                fm_voice.setVisibility(View.GONE);
                rl_reader.setVisibility(View.GONE);
                return;
            }
            fm_voice.setVisibility(View.VISIBLE);
            rl_reader.setVisibility(View.VISIBLE);
            btn_reader_page.setVisibility(View.VISIBLE);
            addVoiceItem();
        }
        if (mRunThread == null) {
            mRunThread = new NoteBookDelayedRun();
        }
        UIUtils.getMainThreadHandler().postDelayed(mRunThread, 500);
    }

    private void addVoiceItem() {
        FrameLayout.LayoutParams params = null;
        if (mVoiceBean == null || mVoiceBean.getVoiceInfos() == null || mVoiceBean.getVoiceInfos().size() == 0) {
            return;
        } else {
            for (final BookVoiceBean.VoiceBean bean : mVoiceBean.getVoiceInfos()) {
                params = createLayoutParams();
                params.topMargin = bean.getViewTop();
                params.leftMargin = bean.getViewLeft();
                final ImageView imageView = new ImageView(getActivity());
                mImgVoices.put(bean.getPostion(), imageView);
                imageView.setImageResource(R.drawable.img_sound_select);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mImgVoices.size() != 0) {
                            for (Map.Entry<Integer, ImageView> entry : mImgVoices.entrySet()) {
                                entry.getKey();
                                entry.getValue().setSelected(false);
                            }
                        }
                        imageView.setSelected(true);
                        LogUtils.e("bean.toString() ===" + bean.toString());
                        btn_reader_pause.setTag("是否暂停");
                        btn_reader_pause.setSelected(false);
//                        getMediaHelper().player_continue();
                        getMediaHelper().player_reset();
                        getMediaHelper().player_start(mVoiceBaseURL + bean.getUrl(), bean.getStart(), bean.getEnd(), bean.getPostion());
                    }
                });
                fm_voice.addView(imageView, params);
            }
        }
    }

    private NoteBookDelayedRun mRunThread;

    @Override
    public void onCompletionPlayerListener(int voicePs) {
        mImgVoices.get(voicePs).setSelected(false);
        if (mIsPageReader) {
            voicePs = voicePs + 1;
            if (mVoiceBean.getVoiceInfos().size() < voicePs) {
                UIUtils.showToastSafe("本页音频文件全部播放完成");
                getMediaHelper().player_reset();
                mIsPageReader = false;
                btn_reader_page.setTag("连读");
                btn_reader_page.setSelected(false);
                btn_reader_pause.setVisibility(View.GONE);
                btn_reader_pause.setTag("是否暂停");
                btn_reader_pause.setSelected(false);
            } else {

                btn_reader_pause.setTag("是否暂停");
                btn_reader_pause.setSelected(false);
                getMediaHelper().player_continue();

                mImgVoices.get(voicePs).setSelected(true);
                BookVoiceBean.VoiceBean bean = mVoiceBean.getVoiceInfos().get(voicePs - 1);
                getMediaHelper().player_start(mVoiceBaseURL + bean.getUrl(), bean.getStart(), bean.getEnd(), bean.getPostion());
            }
        }
    }


    private class NoteBookDelayedRun implements Runnable {

        @Override
        public void run() {
            if (mNoteBookView != null) {
                mNoteBookView.setIntercept(false);
            }
        }
    }


    @Override
    public View getContentView() {
        return mOnyxImgView;
    }

    @Override
    public void showThrowable(Throwable throwable) {
        throwable.printStackTrace();
        mloadingDialog.dismiss();
        OpenBookErrorDialog mErrorDialog = new OpenBookErrorDialog(getActivity()) {
            @Override
            public void deltedeBook() {
                FileUtils.deleteFile((FileUtils.getTextBookFilesDir() + mControlActivity.mBookId + ".pdf"));
                this.dismiss();
                getActivity().finish();
            }
        };
        mErrorDialog.show();
    }


    private boolean isSuccesspdf = false;

    @Override
    public void openDocumentFinsh() {

        if (!isSuccesspdf) {
            mPageCounts = mReaderPresenter.getPages();
            initSeekBar();
            initDB();
        }

    }

    /**
     * 设置 图书章节
     */
    private List<DirectoryModel> mDirectoryList = new ArrayList<>();

    @Override
    public void updateDirectory(ReaderDocumentTableOfContent content) {
        if (content != null && !content.isEmpty() && content.getRootEntry() != null) {
            ReaderDocumentTableOfContentEntry entry = content.getRootEntry();
            if (entry.getChildren() != null && entry.getChildren().size() > 0) {
                setDirectoryList2(entry, mDirectoryList, 0);
            }
            setShowDirectory();
        } else {
            setMarkAndDirectoryClickTextChange(true);
        }
    }

    private void setDirectoryList2(ReaderDocumentTableOfContentEntry entry, List<DirectoryModel> modelList, int level) {
        DirectoryModel model = new DirectoryModel();
        LogUtils.i("pageName ==" + entry.getPageName());
        LogUtils.i("getPosition ==" + entry.getPosition());
        LogUtils.i("/////////////////////////////////////////////");
        if (entry.getTitle() != null && entry.getPageName() != null) {
            model.setTitle(entry.getTitle());
            model.setPosition(entry.getPageName());
            model.setHead(level == 1);
            modelList.add(model);
        }

        List<ReaderDocumentTableOfContentEntry> entries = entry.getChildren();
        if (entries != null && entries.size() > 0) {
            for (ReaderDocumentTableOfContentEntry childs : entries) {
                setDirectoryList2(childs, modelList, level + 1);
            }
        }
    }

    private void setShowDirectory() {
        setMarkAndDirectoryClickTextChange(true);
        // 增加判断 当获取目录为空
        if (mDirectoryList != null && mDirectoryList.size() > 0 && mHandlerDirAdapter != null) {
            mLvBookDirectory.setAdapter(mHandlerDirAdapter);
            mHandlerDirAdapter.notifyDataSetChanged();
//            mLvBookDirectory.setSelection(OutlineActivityData.get().position);
        } else {
            mHandlerDirAdapter = new HandlerDirAdapter(((Activity) mContext).getLayoutInflater(), mDirectoryList);
            mLvBookDirectory.setAdapter(mHandlerDirAdapter);
            mHandlerDirAdapter.notifyDataSetChanged();
        }
    }
    /////////////////////////////////////////pdf sdk end////////////////////////////////////////////


    private void initDB() {
        mSubDb = getObserver().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(getDBSubscriber());
    }

    @Override
    protected void addListener() {
        super.addListener();
        mBookMarkBtn.setOnClickListener(this);
        mItemDirectory.setOnClickListener(this);
        mBackPageBtn.setOnClickListener(this);
        mRlDirectory.setOnClickListener(this);
        mDirectoryImg.setOnClickListener(this);
        mLvBookDirectory.setOnItemClickListener(this);
    }

    private void initSeekBar() {
        LogUtils.i("mPageCounts ==" + mPageCounts);
        mSeekbarPage.setPdfCounts(mPageCounts);
        mSeekbarPage.setVisibility(View.VISIBLE);
        int smax = Math.max(mPageCounts - 1, 1);
        mPageSliderRes = ((10 + smax - 1) / smax) * 2;
        mSeekbarPage.setPageSliderRes(mPageSliderRes);
        mSeekbarPage.setMax((mPageCounts - 1) * mPageSliderRes);


        mSeekbarPage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                requestPageTask((seekBar.getProgress() + mPageSliderRes / 2) / mPageSliderRes);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                leaveScribbleMode(true);
                mUndoIv.setEnabled(false);
                mRedoIv.setEnabled(false);
            }
        });
    }


    @NonNull
    private BaseSubscriber<Void> getNextSubscriber() {
        return new BaseSubscriber<Void>() {
            @Override
            public void onNext(Void aVoid) {
                requestPageTask(mCurrentMarksPage + 1);
            }
        };
    }

    @NonNull
    private BaseSubscriber<Void> getBackSubscriber() {
        return new BaseSubscriber<Void>() {
            @Override
            public void onNext(Void aVoid) {
                requestPageTask(mCurrentMarksPage - 1);
            }
        };
    }

    private Observable getObserver() {
        return Observable.create(new Observable.OnSubscribe<Object>() {

            @Override
            public void call(Subscriber<? super Object> subscriber) {
                queryDB();
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
            bookMarks.put(info.getNumber() - 1, info);
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
        isSuccesspdf = true;
        mSeekbarPage.setClickable(false);
        mSeekbarPage.setEnabled(false);
        mSeekbarPage.setFocusable(false);
        mBackPageBack.setEnabled(false);
        mBackPageNext.setEnabled(false);

        if (mIsOpenVoice) {
            mIsPageReader = false;
            mImgVoices.clear();

            btn_reader_page.setTag("连读");
            btn_reader_page.setSelected(false);

            btn_reader_pause.setTag("是否暂停");
            btn_reader_pause.setSelected(false);
            getMediaHelper().player_reset();
        }

        getReaderPresenter().gotoPage(position);
    }


    /////////////////////////////////////////////
    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_item_directory:
                setShowDirectory();
                break;
            case R.id.btn_item_bookmarks:
                showPdfMarkAdapter();
                break;
            case R.id.rl_directory:
                mRlDirectory.setVisibility(View.GONE);
                break;

            case R.id.bookmark:
                showMarkDialog();
                break;
            case R.id.directory:
                showDirectory();
                break;
            case R.id.img_btn_hide:
                onBackListener();
                break;

            case R.id.btn_reader_page:
                if (mVoiceBean == null || mVoiceBean.getVoiceInfos() == null || mVoiceBean.getVoiceInfos().size() == 0) {
                    UIUtils.showToastSafe("没有播放文件");
                    break;
                }

                mIsPageReader = !mIsPageReader;
                if (mIsPageReader) {
                    btn_reader_page.setTag("停止");
                    btn_reader_page.setSelected(true);
                    btn_reader_pause.setVisibility(View.VISIBLE);
                    btn_reader_pause.setTag("是否暂停");
                    btn_reader_pause.setSelected(false);
                    if (mImgVoices.size() != 0) {
                        for (Map.Entry<Integer, ImageView> entry : mImgVoices.entrySet()) {
                            entry.getKey();
                            entry.getValue().setSelected(false);
                        }
                    }

                    BookVoiceBean.VoiceBean bean = mVoiceBean.getVoiceInfos().get(0);
                    if (mImgVoices.size() > 0) {
                        mImgVoices.get(bean.getPostion()).setSelected(true);
                    }

                    btn_reader_pause.setTag("是否暂停");
                    btn_reader_pause.setSelected(false);
//                    getMediaHelper().player_continue();
                    getMediaHelper().player_start(mVoiceBaseURL + bean.getUrl(), bean.getStart(), bean.getEnd(), bean.getPostion());
                } else {

                    if (mImgVoices.size() != 0) {
                        for (Map.Entry<Integer, ImageView> entry : mImgVoices.entrySet()) {
                            entry.getKey();
                            entry.getValue().setSelected(false);
                        }
                    }
                    btn_reader_page.setTag("连读");
                    btn_reader_page.setSelected(false);
                    btn_reader_pause.setVisibility(View.GONE);
                    getMediaHelper().player_reset();
                    btn_reader_pause.setTag("是否暂停");
                    btn_reader_pause.setSelected(false);
                }
                break;

            case R.id.btn_reader_pause:
                if (btn_reader_pause.getTag().toString().trim().equalsIgnoreCase("是否暂停")) {
                    btn_reader_pause.setTag("已暂停");
                    btn_reader_pause.setSelected(true);
                    getMediaHelper().player_pause();
                } else {
                    btn_reader_pause.setTag("是否暂停");
                    btn_reader_pause.setSelected(false);
                    getMediaHelper().player_continue();
                }
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

        if (parent.getAdapter() instanceof HandlerDirAdapter) {
            // 保持用户可以看见的 位置 ，因为一个目录会有可能出现很长
            if (position >= 0) {
                LogUtils.i("yuanye pos =" + (mDirectoryList.get(position).getPosition()));
                requestPageTask(Integer.parseInt(mDirectoryList.get(position).getPosition()));
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
        if (mRlDirectory.getVisibility() == View.VISIBLE) {
            mRlDirectory.setVisibility(View.GONE);
            return;
        }

        mRlDirectory.setVisibility(View.VISIBLE);
        if (mBookMarkBtn.isSelected() && mBookMarkAdapter != null) {
            mInfos.clear();
            if (mBookMarks.size() > 0) {
                mInfos.addAll(new ArrayList<>(mBookMarks.values()));
            }
            mBookMarkAdapter.notifyDataSetChanged();
        } else if (mDirectoryList.size() > 0) {
            setShowDirectory();
        } else {
            getReaderPresenter().getDirectory();
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (backScription2 != null) {
            backScription2.unsubscribe();
        }
        if (nextScription2 != null) {
            nextScription2.unsubscribe();
        }

        if (mSubDb != null && !mSubDb.isUnsubscribed()) {
            mSubDb.unsubscribe();
        }
        mSubDb = null;
        if (mNoteBookView != null) {
            mNoteBookView.recycle();
        }
        getReaderPresenter().close();

        if (mRunThread != null) {
            UIUtils.getMainThreadHandler().removeCallbacks(mRunThread);
        }
        mRunThread = null;
        getMediaHelper().player_release();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
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
        if (fileName != null) {
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
    private void addLayerLayout() {
        mFrameLayout = (MyFrameLayout) UIUtils.inflate(R.layout.notebook_item);
        final Note note = noteSparse.get(mCurrentMarksPage);
        LogUtils.e(TAG, " Note is : " + note);
        initNoteView();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 60, 0, 62);
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
            if (mBackPageBack.isEnabled() && mBackPageNext.isEnabled() && mSeekbarPage.isClickable() && mBackPageBack != null) {
                mBackPageBack.callOnClick();
            }
        }
    }

    @Override
    public void nextPageForKey() {
        super.nextPageForKey();
        if (!mHide) {
            if (mBackPageBack.isEnabled() && mBackPageNext.isEnabled() && mSeekbarPage.isClickable() && mBackPageNext != null) {
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
        mSeekbarPage.setClickable(true);
        mSeekbarPage.setEnabled(true);
        mSeekbarPage.setFocusable(true);
        mBackPageBack.setEnabled(true);
        mBackPageNext.setEnabled(true);
    }

    private void initSeekbarAndTextNumber() {
        final int index = mCurrentMarksPage;
        mSeekbarPage.setProgress(index * mPageSliderRes);
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        if (event.getType().equalsIgnoreCase(EventBusConstant.EVENT_ANSWERING_SHOW)) {
            LogUtils.i("type .." + event.getType());
            if (mNoteBookView != null) {
                mNoteBookView.leaveScribbleMode();
                mNoteBookView.setIntercept(true);
            }
            BaseEvent baseEvent = new BaseEvent(EventBusConstant.EVENT_ANSWERING_RESULT, "");
            EventBus.getDefault().post(baseEvent);
        } else if (event.getType().equalsIgnoreCase(EventBusConstant.EVENT_ANSWERING_PUASE) || (event.getType().equalsIgnoreCase(EventBusConstant.EVENT_LOCKER_ACTIVITY_PUSE))) {
            LogUtils.i("type .." + event.getType());
            if (mNoteBookView != null) {
                mNoteBookView.setIntercept(false);
            }
        } else if (event.getType().equalsIgnoreCase(EventBusConstant.EVENT_START_ACTIIVTY_ORDER)) {
            LogUtils.i("type .." + event.getType());
            if (mNoteBookView != null) {
                mNoteBookView.leaveScribbleMode();
                mNoteBookView.setIntercept(true);
            }
            BaseEvent baseEvent = new BaseEvent(EventBusConstant.EVENT_START_ACTIIVTY_ORDER_RESULT, "");
            EventBus.getDefault().post(baseEvent);
        }
    }

    public void onBackListener() {

        mNoteBookView.leaveScribbleMode();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) img_btn_hide.getLayoutParams();
        if (mRl_page.getVisibility() == View.VISIBLE) {
            mRl_page.setVisibility(View.GONE);
            base_opt_layout.setVisibility(View.GONE);
            img_page_next.setVisibility(View.VISIBLE);
            img_page_back.setVisibility(View.VISIBLE);
            img_btn_hide.setText("显示菜单栏");
            params.setMargins(0, 0, 0, 10);
        } else {
            mRl_page.setVisibility(View.VISIBLE);
            base_opt_layout.setVisibility(View.VISIBLE);
            img_page_next.setVisibility(View.GONE);
            img_page_back.setVisibility(View.GONE);
            img_btn_hide.setText("隐藏菜单栏");
            params.setMargins(0, 0, 0, 88);
        }
        img_btn_hide.setLayoutParams(params);
    }

    ///////////////////////////////////voice///////////////////////////////////////////////////////////
    private MediaHelper getMediaHelper() {
        if (mMediaHelper == null) {
            new AudioHelper(getActivity()).setMaxVolume();
            mMediaHelper = new MediaHelper();
            mMediaHelper.setCompletionListener(this);
            mMediaHelper.init(getActivity());
        }
        return mMediaHelper;
    }
}