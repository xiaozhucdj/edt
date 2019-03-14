package com.yougy.home.fragment.showFragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.yougy.common.fragment.BFragment;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.ImageLoader;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.activity.ControlFragmentActivity;
import com.yougy.home.adapter.ColorAdapter;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.home.bean.Book;
import com.yougy.home.bean.Diagram;
import com.yougy.home.bean.Label;
import com.yougy.home.bean.Line;
import com.yougy.home.bean.Note;
import com.yougy.home.bean.PaintDrawStateInfo;
import com.yougy.home.bean.Photograph;
import com.yougy.home.bean.Position;
import com.yougy.home.svgparser.SVG;
import com.yougy.home.svgparser.SVGParser;
import com.yougy.rx_subscriber.BaseSubscriber;
import com.yougy.shop.bean.BaseData;
import com.yougy.ui.activity.R;
import com.yougy.view.NoteBookView;
import com.yougy.view.controlView.ControlView;
import com.yougy.view.decoration.DividerGridItemDecoration;
import com.yougy.view.showView.MoveImageView;
import com.yougy.view.showView.MoveRelativeLayout;
import com.yougy.view.showView.MoveRelativeLayout1;
import com.yougy.view.showView.MoveView;
import com.yougy.view.showView.MyFrameLayout;
import com.yougy.view.showView.ScreenShotView;

import org.litepal.crud.DataSupport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.os.Environment.getExternalStorageDirectory;
import static com.yougy.ui.activity.R.id.img_deleteNote;

/**
 * Created by jiangliang on 2016/7/8.
 */
public class BaseFragment extends BFragment implements View.OnClickListener, NoteBookView.OnUndoOptListener, NoteBookView.OnDoOptListener, ShortNoteFragment.DeleteLabelListener, ShortNoteFragment.AddLabelListener, View.OnTouchListener, MoveImageView.UpdateImageViewMapListener, SeekBar.OnSeekBarChangeListener, DrawFragment.OnDrawListener {
    protected ImageButton mPen;
    protected ImageButton mPencil;
    protected ImageButton mOliBlackPen;
    protected ImageButton mMakerPen;
    protected ImageView mEraser;
    protected ImageButton mColor;
    protected SeekBar mPenSizePg;
    protected ImageView mPenSizeIv;
    protected SeekBar mPenAlphaPg;
    protected ImageView mPenAlphaIv;
    protected ImageView mBookPackageIv;
    protected ImageView mGestureIv;
    protected ImageView mPaintDrawIv;
    protected ImageView mDrawPicIv;
    protected ImageView mLabelIv;
    protected ImageView mImageIv;
    protected ImageView mScreenshotIv;
    protected ImageView mSendIv;
    protected ImageView mUndoIv;
    protected ImageView mRedoIv;
    protected ImageView mTextbookIv;
    protected ImageView mNotebookIv;
    protected ImageView mExerciseBookIv;
    protected ImageView mBookMarkerIv;
    protected ImageView mDirectoryIv;
    protected NoteBookView mNoteBookView;
    protected RelativeLayout mPaintChoose;
    protected LinearLayout mBookNeedLayout;
    protected LinearLayout mOptionLayout;
    protected ScreenShotView mScreenShotView;
    protected MyFrameLayout mFrameLayout;
    protected View mRoot;
    protected Context mContext;
    protected ViewStub mStub;

    protected RelativeLayout mRl_page;


    protected FrameLayout mParentLayout;
    protected int x;//绘画开始的横坐标
    protected int y;//绘画开始的纵坐标
    protected int right;//绘画结束的横坐标
    protected int bottom;//绘画结束的纵坐标
    protected int width;//绘画的宽度
    protected int height;//绘画的高度
    private static final String PROPERTY_NAME = "translationY";
    private static final String TAG = "BaseFragment";
    private Map<Position, MoveImageView> imageViews;//用来存放位置和图标的对应关系

    protected boolean isNeedCutScreen;

    private boolean contentChanged;

    protected Book book;
    protected Note mNote;
    protected List<Note> mNotes = new ArrayList<>();
    protected Map<Integer, Note> mMapNotes;

    protected String fileName;
    protected int labelCount;
    protected int position;
    protected String labelPath;

    protected int pictureCount;
    protected String picturePath;

    protected String contentPath;
    protected LinearLayout mLlOtherIcon;
    protected ImageView mImgShowOtherIcon;

    private static PaintDrawStateInfo mPaintDrawState;
    /**
     * 判断是否显示 画笔工具栏目
     */
    protected boolean mIsShowPaintChoose = false;
    /***
     * 用来判断 点击 左右 是否 拦截事件
     */
    protected boolean mIsIntercept = true;
    /**
     * 拷贝
     */
    private ImageView mImgPaste;

    /**
     * 根View 左右切换滑动
     */
    protected ControlView mControlView;
    protected ImageView mImgupdataNote;
    private ImageView mImgDeleteNote;
    protected static final int DURATION = 1;
    private TextView mTvPenOrEraser;
    private SeekBar mSeekPenOrEraser;
    private int mSeekPenOrEraserSize;
    private int mCutterPenSize = 2;
    private int mCutterEraserSize = 2;

    private boolean mIsUserPen = true;
    protected FrameLayout base_opt_layout;
//    protected TextView tv_media;

    private String generatePicturePath() {
        picturePath = fileName + "-" + "picture" + "-" + position + "-" + pictureCount;
        return getExternalStorageDirectory() + "/android/data/" + fileName + File.separator + picturePath;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
        // 创建 画笔状态对象
        if (mPaintDrawState == null) {
            mPaintDrawState = new PaintDrawStateInfo();
            //从XML 文件中读取数据
            SpUtils.readPaintDrawStates(mPaintDrawState);
            if (mPaintDrawState.getPanSize() == -1 && mPaintDrawState.getPanColor() == -1) {
                mPaintDrawState.setPanSize(2.0f);
                mPaintDrawState.setPanColor(Color.BLACK);
                mPaintDrawState.setPanSizeProgress(2);
                mPaintDrawState.setPanAlphProgress(255);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDatas();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_base_layout, container, false);
        return mRoot;
    }

    protected void initDatas() {
        imageViews = new HashMap<>();
        position = restorePage();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initBaseView();
        initOtherView();
        addListener();
    }

    @Override
    protected void handleEvent() {
        handleUploadEvent();
        handleDeleteEvent();
        super.handleEvent();
    }

    private void handleDeleteEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof Photograph) {
                    final Photograph photo = (Photograph) o;
                    mFrameLayout.removeView(currentClickedImg);
                    Observable.create(new Observable.OnSubscribe<Object>() {
                        @Override
                        public void call(Subscriber<? super Object> subscriber) {
                            int count = photo.delete();
                            LogUtils.e(TAG, "count is : " + count);
                        }
                    }).subscribeOn(Schedulers.io()).subscribe();
                }
            }
        }));
    }

    private void handleUploadEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof BaseData) {
                    //TODO:更新数据库的笔记
                    LogUtils.e(TAG, "笔记上传成功。。。");
                }
            }
        }));
    }

    private void initBaseView() {
        /***baseFragment 根View*/
        mParentLayout = mRoot.findViewById(R.id.parent_layout);
        /**
         *         opt_container .xml
         *         显示笔工具栏
         */

        /***
         * 修改  mPaintChoose 布局 位于顶部60dp 位了减去 动画控件
         */


        // 显示笔工具栏  LinearLayout
        mPaintChoose = mRoot.findViewById(R.id.paint_choose);
        //钢笔
        mPen = mRoot.findViewById(R.id.pen);
        //铅笔
        mPencil = mRoot.findViewById(R.id.pencil);
        //油性黑笔
        mOliBlackPen = mRoot.findViewById(R.id.oli_black_pen);
        //马克笔
        mMakerPen = mRoot.findViewById(R.id.maker_pen);
        //橡皮
        mEraser = mRoot.findViewById(R.id.eraser);
        // 颜色 ImageButton
        mColor = mRoot.findViewById(R.id.color);
        // 笔大小 SeekBar
        mPenSizePg = mRoot.findViewById(R.id.pen_size_pg);
        //笔大小 ImageView
        mPenSizeIv = mRoot.findViewById(R.id.pen_size);
        // 透明度 SeekBar
        mPenAlphaPg = mRoot.findViewById(R.id.pen_alpha_pg);
        //透明度  ImageView
        mPenAlphaIv = mRoot.findViewById(R.id.pen_alpha);
/**********************************************************************/
        /***
         *  按钮 工具栏
         */

        // 按钮 工具栏 总控件
        mOptionLayout = mRoot.findViewById(R.id.opt_layout);

        //书包
        mBookPackageIv = mRoot.findViewById(R.id.book_package);
        //手势
        mGestureIv = mRoot.findViewById(R.id.gesture);
        // 画笔 并且弹出 笔栏目
        mPaintDrawIv = mRoot.findViewById(R.id.paint_draw);
        // 画图 显示一个webViwe 添加刘阳HTML
        mDrawPicIv = mRoot.findViewById(R.id.draw_pic);
        // 添加标签
        mLabelIv = mRoot.findViewById(R.id.label);
        // 插入图片 调用系统 相册
        mImageIv = mRoot.findViewById(R.id.image);
        // 截图
        mScreenshotIv = mRoot.findViewById(R.id.screenshot);
        // 发送
        mSendIv = mRoot.findViewById(R.id.send);
        // 后退
        mUndoIv = mRoot.findViewById(R.id.undo);
        RxView.clicks(mUndoIv).throttleFirst(DURATION, TimeUnit.SECONDS).subscribe(getUndoSubscriber());
        //前进
        mRedoIv = mRoot.findViewById(R.id.redo);
        RxView.clicks(mRedoIv).throttleFirst(DURATION, TimeUnit.SECONDS).subscribe(getRedoSubscriber());
        // 添加书签
        mBookMarkerIv = mRoot.findViewById(R.id.bookmark);
        //查看目录
        mDirectoryIv = mRoot.findViewById(R.id.directory);

        //切换Fragment 课本
        mTextbookIv = mRoot.findViewById(R.id.textbook);

        //切换Fragment 作业
        mExerciseBookIv = mRoot.findViewById(R.id.exercise_book);



        //修改笔记
        mImgupdataNote = mRoot.findViewById(R.id.img_updataNote);

//        if (mControlActivity.mBookId > 0 && !StringUtils.isEmpty(FileUtils.getBookFileName(mControlActivity.mBookId, FileUtils.bookDir))) {

        if (mControlActivity.mBookId > 0) {
            mTextbookIv.setEnabled(true);
        } else {
            mTextbookIv.setEnabled(false);
        }

        mImgupdataNote.setOnClickListener(this);
        //删除笔记
        mImgDeleteNote = mRoot.findViewById(img_deleteNote);
        mImgDeleteNote.setOnClickListener(this);

        //切换Fragment 笔记
        mNotebookIv = mRoot.findViewById(R.id.notebook);
        mNotebookIv.setEnabled(mControlActivity.mNoteId > 0);

        // 判断作业状体按钮
        mExerciseBookIv.setEnabled(mControlActivity.mHomewrokId > 0);


        if (mControlActivity.mNoteCreator == SpUtils.getAccountId()) {
            mNotebookIv.setVisibility(View.GONE);
            mTextbookIv.setVisibility(View.GONE);
            mExerciseBookIv.setVisibility(View.GONE);
            mRoot.findViewById(R.id.to_homewrok_line).setVisibility(View.INVISIBLE);

            mImgDeleteNote.setVisibility(View.VISIBLE);
            mImgupdataNote.setVisibility(View.VISIBLE);
        }


        // 显示 书签和 目录内容的 控件
        mBookNeedLayout = mRoot.findViewById(R.id.book_need_layout);

        /**
         *  Seekbar 底部进度条
         */

        // Seekbar 根VIEW
        mRl_page = mRoot.findViewById(R.id.rl_page);
        base_opt_layout = mRoot.findViewById(R.id.base_opt_layout);

        //替换布局的  ViewStub
        mStub = mRoot.findViewById(R.id.view_stub);

        /***
         *  初始化 按钮状态
         */
        mBookNeedLayout.setVisibility(View.GONE);
        mUndoIv.setEnabled(false);
        mRedoIv.setEnabled(false);

       /* mPenSizePg.setProgress(100);
        mPenAlphaPg.setProgress(100);*/
        mGestureIv.setSelected(true);
        // 绘制控件
        mFrameLayout = mRoot.findViewById(R.id.note_parent);
        // 截图 View
        mScreenShotView = new ScreenShotView(mContext);

        //其他按钮根布局
        mLlOtherIcon = mRoot.findViewById(R.id.ll_OtherIcon);
        mLlOtherIcon.setVisibility(View.GONE);
        //控制是否显示其它按钮
        mImgShowOtherIcon = mRoot.findViewById(R.id.img_showOhterIcon);
        mImgPaste = mRoot.findViewById(R.id.paste);
        setPasteEnable();


        mTvPenOrEraser = mRoot.findViewById(R.id.tv_pen_or_eraser);
        mSeekPenOrEraser = mRoot.findViewById(R.id.seek_pen_or_eraser);
        mSeekPenOrEraser.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSeekPenOrEraserSize = progress + 2;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                leaveScribbleMode(true);
                if (mNoteBookView != null) {
                    if (mIsUserPen) {
                        mCutterPenSize = mSeekPenOrEraserSize;
                        mNoteBookView.outSetPenSize(mCutterPenSize);
                    } else {
                        mCutterEraserSize = mSeekPenOrEraserSize;
                        mNoteBookView.outSetEraserSize(mCutterEraserSize);
                    }
                    setThum(mSeekPenOrEraserSize);
                }
            }
        });

//       tv_media = mRoot.findViewById(R.id.tv_media);
//        tv_media.setOnClickListener(this);

    }

    @NonNull
    private BaseSubscriber<Void> getUndoSubscriber() {
        return new BaseSubscriber<Void>() {
            @Override
            public void onNext(Void aVoid) {
                leaveScribbleMode(true);
                mNoteBookView.undo();
            }
        };
    }

    @NonNull
    private BaseSubscriber<Void> getRedoSubscriber() {
        return new BaseSubscriber<Void>() {
            @Override
            public void onNext(Void aVoid) {
                leaveScribbleMode(true);
                mNoteBookView.redo();
            }
        };
    }

    /**
     * 设置 粘贴按钮 的状态
     */
    protected void setPasteEnable() {
        mImgPaste.setEnabled(getItem() != null || flag);

    }

    /**
     * 获取拷贝对象的Item 间接的获取地址
     */
    private ClipData.Item getItem() {
        ClipData.Item item = null;
        if (clipboardManager == null) {
            clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        if (clipboardManager.getPrimaryClip() != null) {
            item = clipboardManager.getPrimaryClip().getItemAt(0);
        }

        return item;
    }

    protected void initOtherView() {

    }


    public void initNoteView() {
//        updatePaintColor(Color.BLACK);
        if (mNoteBookView!=null){
            mNoteBookView.recycle();

        }

        mNoteBookView = new NoteBookView(mContext);
        if (mIsUserPen) {
            mNoteBookView.outSetPenSize(mCutterPenSize);
        } else {
            mNoteBookView.outSetEraserSize(mCutterEraserSize);
        }
        mNoteBookView.setReDoOptListener(this);
        mNoteBookView.setUndoOptListener(this);
        mFrameLayout.setOnTouchListener(this);
    }

    protected void addListener() {
        mPen.setOnClickListener(this);
        mPencil.setOnClickListener(this);
        mOliBlackPen.setOnClickListener(this);
        mMakerPen.setOnClickListener(this);
        mEraser.setOnClickListener(this);
        mColor.setOnClickListener(this);
        mBookPackageIv.setOnClickListener(this);
        mGestureIv.setOnClickListener(this);
        mPaintDrawIv.setOnClickListener(this);
        mDrawPicIv.setOnClickListener(this);
        mLabelIv.setOnClickListener(this);
        mImageIv.setOnClickListener(this);
        mScreenshotIv.setOnClickListener(this);
        mSendIv.setOnClickListener(this);
        mBookMarkerIv.setOnClickListener(this);
        mDirectoryIv.setOnClickListener(this);
        mTextbookIv.setOnClickListener(this);
        mNotebookIv.setOnClickListener(this);
        mExerciseBookIv.setOnClickListener(this);
        mPenSizePg.setOnSeekBarChangeListener(this);
        mPenAlphaPg.setOnSeekBarChangeListener(this);
        mImgShowOtherIcon.setOnClickListener(this);
        mImgPaste.setOnClickListener(this);
    }

    private boolean isClear = false;

    protected void saveContent(boolean inUIThread) {
        if (isClear) {
            mNote.delete();
            return;
        }
        if (mNoteBookView != null) {
            LogUtils.e(TAG, "save content.................");
            if (mNoteBookView.isContentChanged()) {
                SpUtils.changeContent(true);
                if (inUIThread) {
                    save();
                } else {
                    getSaveObservable().subscribeOn(Schedulers.io()).subscribe();
                }
            }
        }
    }

    @NonNull
    protected Observable<Void> getSaveObservable() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                save();
            }
        });
    }

    Map<String, String> params = new HashMap<>();

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    protected void save() {
        /**
         * 20161213 添加 笔记样式
         */
        if (mNote.getNoteStyle() == -1) {
            mNote.setNoteStyle(mControlActivity.mNoteStyle);
        }


        long start = System.currentTimeMillis();
        LogUtils.i("yuanye save ...type ==" + mNote.getNoteStyle());
        mNoteBookView.save(mNote);
        long end = System.currentTimeMillis();
        LogUtils.e(TAG, "save note take times is : " + (end - start));
        if (mMapNotes != null) {
            mMapNotes.put(mNote.getPageNum(), mNote);
        }
        mNoteBookView.setContentChanged(false);
        start = System.currentTimeMillis();
        saveBook();
        end = System.currentTimeMillis();
        LogUtils.e(TAG, "save book take times is : " + (end - start));
    }

    private void saveBook() {
        if (null != book) {
            book.getNoteList().add(mNote);
            book.getNotesArray().put(mNote.getBookPageNum(), mNote);
            book.save();
            LogUtils.e(TAG, "save book is : " + book);
        }
    }

    private int chooserHeight;
    private boolean flag = false;//用来标识便签显示是以便签按钮为入口的
    protected View view;
    private boolean isNeedHide;

    @Override
    public void onClick(View v) {
        leaveScribbleMode(true);
        if (mFrameLayout != null) {
            mFrameLayout.setInterceptable(false);
        }
        if (screenCutOptLayout != null) {
            screenCutOptLayout.setVisibility(View.GONE);
        }
        /**
         * 恢复默认值
         */
        isNeedCutScreen = false;
        isNeedHide = true;
        view = null;
        if (v.getId() != R.id.eraser && v.getId() != R.id.paint_draw) {
            mPaintChoose.setVisibility(View.GONE);
        }

        switch (v.getId()) {
            /************************************************设置 笔栏目 点击事件*************************************/
          /*  case R.id.pen: //笔 ，显示 笔栏目
                view = mPen;
                isNeedHide = true;
                //设置笔属性
                setPanDrawStates(2.0f, Color.BLACK, 2, 255);
                updateStatus(Color.BLACK, 2, 255);
                mNoteBookView.setPen();
                mNoteBookView.setEraserFlag(false);
                break;*/
           /* case R.id.pencil:
                //TODO:设置画笔为铅笔
                view = mPencil;
                isNeedHide = true;

                //设置为油性画笔属性
                setPanDrawStates(3.0f, Color.BLACK, 3, 255);
                updateStatus(Color.BLACK, 3, 255);
                mNoteBookView.setOilBlackPen();
                break;*/
         /*   case R.id.oli_black_pen:
                view = mOliBlackPen;
                isNeedHide = true;

                //设置为油性画笔属性
                setPanDrawStates(5.0f, Color.BLACK, 5, 255);
                updateStatus(Color.BLACK, 5, 255);
                mNoteBookView.setOilBlackPen();
                break;*/
     /*       case R.id.maker_pen:
                view = mMakerPen;
                isNeedHide = true;
                //设置马克笔属性
                setPanDrawStates(5.0f, Color.BLACK, 15, 100);
                updateStatus(Color.GREEN, 15, 100);
                mNoteBookView.setMakerPen();
                break;*/
            case R.id.eraser:
                view = mEraser;
                isNeedHide = true;
                //设置橡皮属性
        /*        mNoteBookView.useEraser();
                mNoteBookView.setEraserFlag(true);*/

                if (mPaintChoose.getVisibility() == View.VISIBLE) {
                    mPaintChoose.setVisibility(View.GONE);
                } else {
                    mIsUserPen = false;
                    mSeekPenOrEraser.setProgress(mCutterEraserSize);
                    mPaintChoose.setVisibility(View.VISIBLE);
                }
                mNoteBookView.outSetEraserSize(mCutterEraserSize);
                mTvPenOrEraser.setText("橡皮大小：");
                break;
//            case R.id.color:
//                view = mColor;
//                /**打开 调色板  选择颜色*/
//                showColorSelector();
//                isNeedHide = false;
//                break;

            /************************************************设置 按钮栏目点击事件*************************************/
//            case R.id.gesture:
//                //TODO:切换为手势控制
//                view = mGestureIv;
//                // 点击手势时候 对数据进行保存操作
//                saveContent(false);
//                mIsShowPaintChoose = false;
//                mIsIntercept = true;
//                break;
            case R.id.paint_draw:
                //TODO：切换为绘制输入
                view = mPaintDrawIv;
//                if (mIsShowPaintChoose) {
//                    if (mPaintChoose.getVisibility() == View.VISIBLE) {
//                        outAnimator();
//                    } else {
//                        inAnimatior();
//                    }
//                } else {
//                    mIsShowPaintChoose = true;
//                }
//                mIsIntercept = false;

                if (mPaintChoose.getVisibility() == View.VISIBLE) {
                    mPaintChoose.setVisibility(View.GONE);
                } else {
                    mIsUserPen = true;
                    mSeekPenOrEraser.setProgress(mCutterPenSize);
                    mPaintChoose.setVisibility(View.VISIBLE);
                }
                mNoteBookView.outSetPenSize(mCutterPenSize);
                mTvPenOrEraser.setText("画笔大小：");
                break;
            case R.id.draw_pic:
                //TODO:使用绘图工具
//                view = mDrawPicIv;
//                toDrawFragment();
//                addMoveView();
                isClear = true;
                mNoteBookView.clear();
                break;
            case R.id.label:
                mLlOtherIcon.setVisibility(View.GONE);
                view = mLabelIv;
                flag = true;
                Label label = new Label();
                useNote(label);
                break;
            case R.id.image:
                /**
                 彩色设备上没有相册 PL107
                 mLlOtherIcon.setVisibility(View.GONE);
                 view = mImageIv;
                 useLocalPhoto();*/
                break;
            case R.id.screenshot:
                if (mFrameLayout != null) {
                    mLlOtherIcon.setVisibility(View.GONE);
                    isNeedCutScreen = true;
                    view = mScreenshotIv;
                    mScreenShotView.setSign(true);
                    mFrameLayout.setInterceptable(true);
                    mScreenShotView.postInvalidate();
                    mIsIntercept = false;
                }

                break;
            case R.id.send:
                mLlOtherIcon.setVisibility(View.GONE);
                //TODO:发送至服务器
                view = mSendIv;
                send();
                break;
            case R.id.textbook:
                if (!StringUtils.isEmpty(FileUtils.getBookFileName(mControlActivity.mBookId, FileUtils.bookDir))) {
                    toTextBookFragment();
                } else {
                    if (NetUtils.isNetConnected()) {
                        downBookTask(mControlActivity.mBookId);
                    } else {
                        showCancelAndDetermineDialog(R.string.jump_to_net);
                    }
                }
                break;
            case R.id.notebook:
                toNoteBookFragment();
                break;
            case R.id.exercise_book:
//                Bundle extras = new Bundle();
//                //图书ID
//                extras.putInt(FileContonst.BOOK_ID, mControlActivity.mBookId);
//                //笔记ID
//                extras.putInt(FileContonst.NOTE_ID, mControlActivity.mNoteId);
//                //作业ID
//                extras.putInt(FileContonst.HOME_WROK_ID, mControlActivity.mHomewrokId);
//                //笔记名字
//                extras.putString(FileContonst.NOTE_TITLE, mControlActivity.mNotetitle);
//                //笔记样式
//                extras.putInt(FileContonst.NOTE_Style, mControlActivity.mNoteStyle);
//
//                Intent intent = new Intent(getActivity(), MainActivityScreen.class);
//                intent.putExtras(extras);
//                startActivity(intent);
                toExerciseBookFragment();
                break;
            case R.id.cancel_cut:
                isNeedCutScreen = true;
                view = mScreenshotIv;
                cancelCutOpt();
                break;
            case R.id.copy_cut:
                isNeedCutScreen = true;
                view = mScreenshotIv;
                copyCutOpt();
                break;
            case R.id.send_cut:
                isNeedCutScreen = true;
                view = mScreenshotIv;
                sendCutOpt();
                break;
            case R.id.save_cut:
                isNeedCutScreen = true;
                view = mScreenshotIv;
                saveCutOpt();
                break;

            case R.id.img_showOhterIcon:
                isNeedHide = true;
                mLlOtherIcon.setVisibility(mLlOtherIcon.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;

            case R.id.paste:
                mLlOtherIcon.setVisibility(View.GONE);
                onLoadPaseImg();
                break;
            case R.id.book_package:
                saveContent(false);
                getActivity().onBackPressed();
                break;
//
//            case  R.id.tv_media:
//                cliclMedia();
//                break;

        }
//        if (null != view && isNeedHide && view != mPaintDrawIv && mPaintChoose.getVisibility() == View.VISIBLE) {
//            outAnimator();
//        }


        /**
         * 根据状态 切换 手势 笔 截图 三个按钮的选择状态
         */
//        if (null != view && view == mScreenshotIv) {
//            mScreenshotIv.setSelected(true);
//            mGestureIv.setSelected(false);
//            mPaintDrawIv.setSelected(false);
//        } else {
//            mScreenshotIv.setSelected(false);
//            mGestureIv.setSelected(mIsIntercept);
//            mPaintDrawIv.setSelected(!mIsIntercept);
//        }
    }




    public void leaveScribbleMode(boolean needFreshUI) {
        if (null != mNoteBookView) {
            EpdController.leaveScribbleMode(mNoteBookView);
        }

        if (needFreshUI && null != mNoteBookView && mNoteBookView.isContentChanged()) {
            LogUtils.e(TAG, "leaveScribbleMode................");
            mNoteBookView.invalidate();
        }
    }

    /**
     * 跳转至绘图界面是
     */
    protected DrawFragment mDrawFragment;

    protected void toDrawFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (null == mDrawFragment) {
            mDrawFragment = new DrawFragment();
            mDrawFragment.setOnDrawListener(this);
            ft.add(R.id.container, mDrawFragment);
        } else {
            ft.show(mDrawFragment);
        }
        ft.commit();
    }

    private void toTextBookFragment() {
        if (null != switcherListener) {
            switcherListener.switch2TextBookFragment();
        }
    }

    private void toNoteBookFragment() {
        if (null != switcherListener) {
            switcherListener.switch2NoteBookFragment();
        }
    }

    private void toExerciseBookFragment() {
        if (null != switcherListener) {
            switcherListener.switch2ExerciseFragment();
        }
    }

    /***
     * 更新 画笔大小和 透明度 的seek值
     */
    private void updateStatus(int color, int progress, int alpha) {
  /*      GradientDrawable drawable = (GradientDrawable) mPenSizeIv.getBackground();
        drawable.setColor(color);
        GradientDrawable alphaDrawable = (GradientDrawable) mPenAlphaIv.getBackground();
        alphaDrawable.setColor(color);*/
        mPenSizePg.setProgress(progress);
        mPenAlphaPg.setProgress(alpha);
        updatePaintColor(color);
    }

    /**
     * 显示颜色选择窗口
     */
    private PopupWindow popWindow;

    private void showColorSelector() {
        if (popWindow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.color_layout, null);
            popWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            RecyclerView recyclerView = view.findViewById(R.id.color_selector);
            final int[] colors = getResources().getIntArray(R.array.colors);
            ColorAdapter adapter = new ColorAdapter(colors, mContext);
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 6));
            recyclerView.addItemDecoration(new DividerGridItemDecoration(mContext));
            recyclerView.setAdapter(adapter);
            recyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(recyclerView) {
                @Override
                public void onItemClick(RecyclerView.ViewHolder vh) {
                    int position = vh.getAdapterPosition();
                    popWindow.dismiss();
                    updatePaintColor(colors[position]);
                }
            });
        }
        // 使其能获得焦点 ，要想监听菜单里控件的事件就必须要调用此方法
        popWindow.setFocusable(true);
        // 设置允许在外点击消失
        popWindow.setOutsideTouchable(true);
        // 设置背景，这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        //软键盘不会挡着popupwindow
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //设置菜单显示的位置
        popWindow.showAtLocation(mPaintChoose, Gravity.TOP, 0, 2 * mPaintChoose.getHeight() + UIUtils.getStatusBarHeight());
        //监听触屏事件
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                return false;
            }
        });
    }

    /**
     * 改变画笔颜色
     */
    private void updatePaintColor(int color) {
        GradientDrawable drawable = (GradientDrawable) mPenSizeIv.getBackground();
        drawable.setColor(color);
        GradientDrawable alphaDrawable = (GradientDrawable) mPenAlphaIv.getBackground();
        alphaDrawable.setColor(color);
        GradientDrawable colorDrawable = (GradientDrawable) mColor.getBackground();
        colorDrawable.setColor(color);
        setPanDrawStates(mPaintDrawState.getPanSize(), color, mPaintDrawState.getPanSizeProgress(), mPaintDrawState.getPanAlphProgress());
    }


    /**
     * 画笔选择界面进入动画
     */
    private void inAnimatior() {
       /* ObjectAnimator animator = ObjectAnimator.ofFloat(mPaintChoose, PROPERTY_NAME, 0, chooserHeight);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mPaintChoose.setVisibility(View.VISIBLE);
            }
        });
        animator.setDuration(500);
        animator.start();*/
        updateStatus(mPaintDrawState.getPanColor(), mPaintDrawState.getPanSizeProgress(), mPaintDrawState.getPanAlphProgress());
        //  mNoteBookView.setDrawState(mPaintDrawState.getPanSize() ,mPaintDrawState.getPanColor(), mPaintDrawState.getPanAlphProgress());
        mPaintChoose.setVisibility(View.VISIBLE);
    }

    /**
     * 画笔选择界面退出动画
     */
    private void outAnimator() {
    /*    ObjectAnimator animator = ObjectAnimator.ofFloat(mPaintChoose, PROPERTY_NAME, chooserHeight, 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPaintChoose.setVisibility(View.INVISIBLE);
            }
        });
        animator.setDuration(500);
        animator.start();*/
        mPaintChoose.setVisibility(View.GONE);
    }

    /**
     * 从本地相册获取图片
     */
    private void useLocalPhoto() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public void disenableReDoView() {
        mRedoIv.setEnabled(false);
    }

    @Override
    public void enableReDoView() {
        mRedoIv.setEnabled(true);
    }

    @Override
    public boolean isEnableRedo() {
        LogUtils.e(TAG, "redo enabled : " + mRedoIv.isEnabled());
        return mRedoIv.isEnabled();
    }

    @Override
    public void disenableUndoView() {
        mUndoIv.setEnabled(false);
    }

    @Override
    public void enableUndoView() {
        mUndoIv.setEnabled(true);
    }

    @Override
    public boolean isEnableUndo() {
        LogUtils.e(TAG, "undo enabled : " + mUndoIv.isEnabled());
        return mUndoIv.isEnabled();
    }

    /**
     * 打开便签界面
     */

    protected void useNote(Label label) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ShortNoteFragment noteFragment = new ShortNoteFragment();
        noteFragment.setDeleteLabelListener(this);
        noteFragment.setAddLabelListener(this);
        if (null != label) {
            noteFragment.setLabel(label);
        }
        ft.add(R.id.container, noteFragment);
        ft.commit();
    }

    protected void usePhoto(Photograph photo) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        PicFragment picFragment = new PicFragment();
        picFragment.setPhotoGraph(photo);
        ft.add(R.id.container, picFragment);
        ft.commit();
    }

    /**
     * 根据请求代码，得到不同情况下的图片路径，进而进行图片解析和显示
     *
     * @param requestCode 0代表使用系统相机拍照，1代表选择系统相册照片
     */
    private String path;

    @Override
    public void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            final FrameLayout.LayoutParams params = createLayoutParams();
            final Photograph photo = new Photograph();
            photo.setLeftMargin(params.leftMargin);
            photo.setTopMargin(params.topMargin);
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Observable.create(new Observable.OnSubscribe<Bitmap>() {
                @Override
                public void call(Subscriber<? super Bitmap> subscriber) {
                    if (requestCode == 1) {
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = mContext.getContentResolver().query(data.getData(), filePathColumn, null, null, null);
                        if (null != cursor) {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            path = cursor.getString(columnIndex);
                            cursor.close();
                        }
                    }
                    if (!subscriber.isUnsubscribed()) {
                        Bitmap bitmap = ImageLoader.loadBmpFromFile(path);
                        subscriber.onNext(bitmap);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                        photo.setBytes(bos.toByteArray());
                        boolean result = photo.save();
                        LogUtils.e(TAG, "result is : " + result);
                        mNote.getPhotographList().add(photo);
                        mNote.save();
                    }
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Bitmap>() {
                        @Override
                        public void call(Bitmap bitmap) {
                            addPic(photo, bitmap, params);
                        }
                    });
        }
    }

    protected void addPic(final Photograph photo, Bitmap bitmap, FrameLayout.LayoutParams params) {
//        final MoveImageView imageView = new MoveImageView(mContext);
//        imageView.setImageResource(R.drawable.img_pic_normal);
//        imageView.setPhotograph(photo);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LogUtils.e(TAG, "photo is clicked................");
//                currentClickedImg = imageView;
//                imageView.setImageResource(R.drawable.img_pic_normal);
//                EpdController.leaveScribbleMode(mNoteBookView);
//                flag = false;
//                usePhoto(photo);
//                FrameLayout.LayoutParams tmp = (FrameLayout.LayoutParams) imageView.getLayoutParams();
//                curImgViewPosition = new Position(tmp.leftMargin, tmp.topMargin);
//            }
//        });
//        imageView.setUpdateImageViewMapListener(this);
//        mFrameLayout.addView(imageView, params);
//        imageViews.put(new Position(params.leftMargin, params.topMargin), imageView);
        MoveRelativeLayout1 view = (MoveRelativeLayout1) LayoutInflater.from(mContext).inflate(R.layout.insert_pic_layout, null);
        view.setPhotoGraph(photo);
        final Button deleteBtn = view.findViewById(R.id.delete_pic);
        ImageView imagebtn = view.findViewById(R.id.insert_pic);
        imagebtn.setImageBitmap(bitmap);
        params.width = bitmap.getWidth();
        params.height = bitmap.getHeight();
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MoveRelativeLayout1 layout = (MoveRelativeLayout1) v.getParent();
                mFrameLayout.removeView(layout);
                Observable.create(new Observable.OnSubscribe<Object>() {
                    @Override
                    public void call(Subscriber<? super Object> subscriber) {
                        Photograph photograph = layout.getPhotoGraph();
                        if (null != photograph) {
                            mNote.getPhotographList().remove(photograph);
                            int count = photograph.delete();
                            LogUtils.e(TAG, "count is : " + count);
                        }
                    }
                }).subscribeOn(Schedulers.io()).subscribe();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBtn.setVisibility(View.VISIBLE);
            }
        });
        view.setOnHideDeleteBtnListener(new MoveRelativeLayout1.OnHideDeleteBtnListener() {
            @Override
            public void hideDeleteBtn() {
                deleteBtn.setVisibility(View.GONE);
            }
        });
        mFrameLayout.addView(view, params);
    }

    private ImageView currentClickedImg;

    protected FrameLayout.LayoutParams createLayoutParams() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.START | Gravity.TOP;
        params.leftMargin = 100;
        params.topMargin = 100;
        return params;
    }

    @Override
    public void deleteLabel(Label label) {
        mFrameLayout.removeView(imageViews.remove(curImgViewPosition));
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                if (curImgViewPosition != null) {
                    labelCount--;
//                    int leftmargin = curImgViewPosition.getLeftMargin();
//                    int topmargin = curImgViewPosition.getTopMargin();
//                    Label tmp = getCurrentLabel(leftmargin, topmargin);
                    if (null != label) {
                        mNote.getLabelList().remove(label);
                        int rowsNum = label.delete();
                        LogUtils.e(TAG, "rowsNum is : " + rowsNum);
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Nullable
    private Label getCurrentLabel(int leftmargin, int topmargin) {
        Label tmp = null;
        for (Label label : mNote.getLabelList()) {
            if (label.getLeftMargin() == leftmargin && label.getTopMargin() == topmargin) {
                tmp = label;
            }
        }
        return tmp;
    }

    @Override
    public void addLabel(Label label) {
        if (flag) {
            addLabel2UI(label);
        } else {
            LogUtils.e(TAG, "addLabel.....................");
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 防止切换PDF的时候 用户会有触摸事件，会出现截图
        if (isNeedCutScreen) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = 0;
                    y = 0;
                    width = 0;
                    height = 0;
                    x = (int) event.getX();
                    //纵坐标应减去顶部操作栏的高度
                    y = (int) event.getY();
//                popX = x;
//                popY = y;
                    if (mScreenShotView.getParent() != null) {
                        ((ViewGroup) mScreenShotView.getParent()).removeView(mScreenShotView);
                    }

                    if (mScreenShotView.getParent() == null) {
                        mFrameLayout.addView(mScreenShotView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    right = (int) event.getX();
                    bottom = (int) event.getY();
                    mScreenShotView.setSeat(x, y, right, bottom);
//                mScreenShotView.postInvalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    mScreenShotView.postInvalidate();
                    width = (int) Math.abs(x - event.getX());
                    height = (int) Math.abs(y - event.getY());
                    if (event.getX() <= x) {
                        x = (int) event.getX();
                    }
                    if (event.getY() <= y) {
                        y = (int) event.getY();
                    }
                    bitmap = getBitmap();
                    if (null != bitmap) {
//                    showScreenCutOptWindow();
                        showScreenCutOptLayout();
                    } else {
                        ToastUtil.showCustomToast(mContext, "请正确截图。。。");
                        resetScreenView();
                    }
                    break;
            }
            return true;
        } else {
            return false;
        }
    }

    private Bitmap bitmap;
    /**
     * 显示截图操作窗口
     */
    private PopupWindow cutPopWindow;
    //    private int popX;
//    private int popY;
    private Button cancelCut;
    private Button copyCut;
    private Button sendCut;
    private Button saveCut;
    protected LinearLayout screenCutOptLayout;

    private void showScreenCutOptLayout() {
        if (screenCutOptLayout == null) {
            screenCutOptLayout = mRoot.findViewById(R.id.screen_cut_layout);
        }
        if (cancelCut == null) {
            cancelCut = mRoot.findViewById(R.id.cancel_cut);
            cancelCut.setOnClickListener(this);
        }
        if (copyCut == null) {
            copyCut = mRoot.findViewById(R.id.copy_cut);
            copyCut.setOnClickListener(this);
        }
        if (sendCut == null) {
            sendCut = mRoot.findViewById(R.id.send_cut);
            sendCut.setOnClickListener(this);
        }
        if (saveCut == null) {
            saveCut = mRoot.findViewById(R.id.save_cut);
            saveCut.setOnClickListener(this);
        }
        screenCutOptLayout.setVisibility(View.VISIBLE);
    }


    private void showScreenCutOptWindow() {
        View view = View.inflate(mContext, R.layout.screencut_opt_layout, null);
        Button cancelCut = view.findViewById(R.id.cancel_cut);
        Button copyCut = view.findViewById(R.id.copy_cut);
        Button sendCut = view.findViewById(R.id.send_cut);
        Button saveCut = view.findViewById(R.id.save_cut);
        cancelCut.setOnClickListener(this);
        copyCut.setOnClickListener(this);
        sendCut.setOnClickListener(this);
        saveCut.setOnClickListener(this);

        cutPopWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // 使其能获得焦点 ，要想监听菜单里控件的事件就必须要调用此方法
        cutPopWindow.setFocusable(true);
        // 设置允许在外点击消失
        cutPopWindow.setOutsideTouchable(false);
        // 设置背景，这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        cutPopWindow.setBackgroundDrawable(new BitmapDrawable());
        //软键盘不会挡着popupwindow
        cutPopWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //设置菜单显示的位置
        int[] locations = new int[2];
        mScreenShotView.getLocationInWindow(locations);
//        cutPopWindow.showAtLocation(mParentLayout, Gravity.NO_GRAVITY, popX, popY + 20);
        int y = UIUtils.getScreenHeight() - 40;
        LogUtils.e(TAG, " y is : " + y);
        cutPopWindow.showAtLocation(mParentLayout, Gravity.BOTTOM, 0, 0);
        //监听触屏事件
        cutPopWindow.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                return false;
            }
        });
    }

    /**
     * 保存截图到SD卡
     */
    private void saveCutOpt() {
        cutCommenOpt();
        saveCutPic();
        ToastUtil.showCustomToast(mContext, R.string.save_cut_success);
    }

    private void saveCutPic() {
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                try {
                    FileOutputStream fout = new FileOutputStream("mnt/sdcard/img.png");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private void cutCommenOpt() {
//        cutPopWindow.dismiss();
        screenCutOptLayout.setVisibility(View.GONE);
        mScreenShotView.setSign(true);
        mFrameLayout.setInterceptable(true);
        resetScreenView();
    }

    private void resetScreenView() {
        mScreenShotView.setSeat(0, 0, 0, 0);
        mScreenShotView.postInvalidate();
    }

    /**
     * 发送截图
     */
    private void sendCutOpt() {
        ToastUtil.showCustomToast(mContext, R.string.cut_pic_sent);
        cutCommenOpt();
    }

    /**
     * 拷贝截图
     */
    private ClipboardManager clipboardManager;
    private ClipData clipData;

    private void copyCutOpt() {
        if (clipboardManager == null) {
            clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        saveCutPic();
        cutCommenOpt();
        clipData = ClipData.newUri(mContext.getContentResolver(), "uri", Uri.fromFile(new File("mnt/sdcard/img.png")));
        clipboardManager.setPrimaryClip(clipData);
        mImgPaste.setEnabled(true);
    }

    private void send() {
        try {
            FileInputStream fis = new FileInputStream(new File(getExternalStorageDirectory(), "picture.png"));
            Picture picture = Picture.createFromStream(fis);
            MoveImageView moveImageView = new MoveImageView(mContext);
            moveImageView.setFlag();
            moveImageView.setImageDrawable(new PictureDrawable(picture));
            FrameLayout.LayoutParams params = createLayoutParams();
            mFrameLayout.addView(moveImageView, params);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消截图操作
     */
    private void cancelCutOpt() {
        cutCommenOpt();
    }


    /**
     * 添加便签到界面中
     */
    private Position curImgViewPosition;//当前控件的位置

    private void addLabel2UI(final Label label) {
        final FrameLayout.LayoutParams params = createLayoutParams();
        Observable.create(new Observable.OnSubscribe<Label>() {
            @Override
            public void call(Subscriber<? super Label> subscriber) {
                Label tmp = DataSupport.findLast(Label.class);
                if (tmp != null) {
                    label.setId(tmp.getId() + 1);
                }
                labelCount++;
                label.setLeftMargin(params.leftMargin);
                label.setTopMargin(params.topMargin);
                LogUtils.e(TAG, "save label : " + label);
                label.save();
                mNote.getLabelList().add(label);
                mNote.save();
                LogUtils.e(TAG, "save note is : " + mNote);
                saveBook();
                subscriber.onNext(label);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Label>() {
                    @Override
                    public void call(Label label) {
                        addLabel(params, label);
                    }
                });
    }

    protected void addMoveView() {
        if (mFrameLayout != null) {
            FrameLayout.LayoutParams params = createLayoutParams();
            MoveView moveView = new MoveView(mContext);
            moveView.setImageResource(R.drawable.icon_label);
            mFrameLayout.addView(moveView, params);
        }
    }

    /**
     * 添加便签
     */
    protected void addLabel(FrameLayout.LayoutParams params, final Label label) {
        final MoveImageView imageView = new MoveImageView(mContext);
        imageView.setImageResource(R.drawable.icon_label);
        imageView.setLabel(label);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e(TAG, "label is clicked................");
                imageView.setImageResource(R.drawable.icon_label);
                EpdController.leaveScribbleMode(mNoteBookView);
                flag = false;
                useNote(label);
                curImgViewPosition = imageView.getOldPosition();

            }
        });
        imageView.setUpdateImageViewMapListener(this);

        mFrameLayout.addView(imageView, params);
//        imageViews.put(new Position(params.leftMargin, params.topMargin), imageView);
        Position position = new Position(params.leftMargin, params.topMargin);
        imageView.setOldPosition(position);
        imageViews.put(position, imageView);
    }

    /**
     * 获取截取区域内的图片
     */
    private Bitmap getBitmap() {
        View view = mControlView;
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        Rect frame = new Rect();
        view.getWindowVisibleDisplayFrame(frame);
        Bitmap bmp = null;
        LogUtils.e(TAG, "width is : " + width + ",height is : " + height + ",x is : " + x + ",y is : " + y + ",bitmap's width is : " + bitmap.getWidth() + ",bitmap's height is : " + bitmap.getHeight());
        if (width > 1 && height > 1 && x + width <= bitmap.getWidth() && y > 0 && (y <= bitmap.getHeight())) {
            bmp = Bitmap.createBitmap(bitmap, x + 1, y + 1, width - 1, height - 1);
        }
        view.setDrawingCacheEnabled(false);
        return bmp;
    }

    @Override
    public void updateImageViewMap(final Position position, int type, MoveImageView imageView) {
        leaveScribbleMode(true);

        if (imageView.getOldPosition() != null) {
            imageViews.remove(imageView.getOldPosition());
        }
        imageView.setOldPosition(position);
        imageViews.put(position, imageView);

        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                Label tmp = getCurrentLabel(position.getLeftMargin(), position.getTopMargin());
                if (null != tmp) {
                    tmp.update(tmp.getId());
                }

                Diagram diagram = getCurrentDiagram(position.getLeftMargin(), position.getTopMargin());
                if (null != diagram) {
                    diagram.update(diagram.getId());
                }

                Photograph photo = getCurrentPhotograph(position.getLeftMargin(), position.getTopMargin());
                if (null != photo) {
                    photo.update(photo.getId());
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();

        switch (type) {
            case MoveImageView.TYPE_LABEL:
                imageView.setImageResource(R.drawable.icon_label);
                break;
            case MoveImageView.TYPE_PHOTO:
                imageView.setImageResource(R.drawable.img_pic_normal);
                break;
        }
    }

    private Photograph getCurrentPhotograph(int leftmargin, int topmargin) {
        Photograph tmp = null;
        for (Photograph photo : mNote.getPhotographList()) {
            if (photo.getLeftMargin() == leftmargin && photo.getTopMargin() == topmargin) {
                tmp = photo;
            }
        }
        return tmp;
    }

    private Diagram getCurrentDiagram(int leftmargin, int topmargin) {
        Diagram tmp = null;
        for (Diagram diagram : mNote.getDiagramList()) {
            if (diagram.getLeftMargin() == leftmargin && diagram.getTopMargin() == topmargin) {
                tmp = diagram;
            }
        }
        return tmp;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float temp = progress / 100.0f;
        if (seekBar == mPenSizePg) {
            if (temp < 0.1) {
                temp = 0.1f;
            }
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(mPenSizeIv, "scaleX", temp);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(mPenSizeIv, "scaleY", temp);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animatorX, animatorY);
            animatorSet.setDuration(100);
            animatorSet.start();
            if (mNoteBookView != null) {
//                mNoteBookView.setPaintSize(temp * 30);
            }
        } else {
            mPenAlphaIv.setAlpha(temp);
            setPanDrawStates(temp * 30, mPaintDrawState.getPanColor(), seekBar.getProgress(), mPaintDrawState.getPanAlphProgress());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        leaveScribbleMode(true);
    }

    private OnSwitcherListener switcherListener;

    public void setOnSwitcherListener(OnSwitcherListener switcherListener) {
        this.switcherListener = switcherListener;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.e(TAG, "hidden is : " + hidden);
        saveContent(false);
        savePage();
        setPasteEnable();
    }

    @Override
    public void onPause() {
        super.onPause();
        savePage();
        // 失去焦点的时候存储数据
        saveContent(false);
        SpUtils.putPaintDrawStates(mPaintDrawState);
        if (null != mNoteBookView) {
            EpdController.leaveScribbleMode(mNoteBookView);
        }
    }

    /**
     * 保存页码
     */
    protected void savePage() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences("biji_page", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(fileName, position);
        editor.apply();
    }

    protected int restorePage() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences("biji_page", Context.MODE_PRIVATE);
        return sp.getInt(fileName, 0);
    }


    private boolean drawFlag;

    @Override
    public void onDraw2UI(final String points) {
        LogUtils.e(TAG, "points is : " + points);
        drawFlag = true;
        pictureCount++;
        final String path = generatePicturePath();
        final FrameLayout.LayoutParams params = createLayoutParams();
        final Diagram diagram = new Diagram();
        diagram.setDiagramPath(path);
        diagram.setLeftMargin(params.leftMargin);
        diagram.setTopMargin(params.topMargin);
        Observable.create(new Observable.OnSubscribe<Picture>() {
            @Override
            public void call(Subscriber<? super Picture> subscriber) {
                SVG svg = SVGParser.getSVGFromInputStream(new ByteArrayInputStream(points.getBytes()));
                Picture picture = svg.getPicture();
                try {
                    FileOutputStream fos = new FileOutputStream(new File(path));
                    picture.writeToStream(fos);
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                diagram.save();
                LogUtils.e(TAG, "save diagram is : " + diagram);
                mNote.getDiagramList().add(diagram);
                mNote.save();
                LogUtils.e(TAG, "save note is : " + mNote);
                saveBook();
                subscriber.onNext(picture);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Picture>() {
                    @Override
                    public void call(Picture picture) {
                        addDiagram(params, picture, diagram);
                    }
                });
    }

    protected void addDiagram(final FrameLayout.LayoutParams params, Picture picture, final Diagram diagram) {
        PictureDrawable drawable = new PictureDrawable(picture);
        final MoveRelativeLayout view = (MoveRelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.insert_pic_layout, null);
        view.setDiagram(diagram);
        final Button deleteBtn = view.findViewById(R.id.delete_pic);
        ImageView imagebtn = view.findViewById(R.id.insert_pic);
        imagebtn.setImageDrawable(drawable);
        params.width = 400;
        params.height = 400;
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveRelativeLayout layout = (MoveRelativeLayout) v.getParent();
                mFrameLayout.removeView(layout);
                final Diagram diag = layout.getDiagram();
                Observable.create(new Observable.OnSubscribe<Object>() {
                    @Override
                    public void call(Subscriber<? super Object> subscriber) {
                        if (null != diag) {
                            diag.delete();
                        }
                    }
                }).subscribeOn(Schedulers.io()).subscribe();

            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBtn.setVisibility(View.VISIBLE);
            }
        });
        view.setOnHideDeleteBtnListener(new MoveRelativeLayout.OnHideDeleteBtnListener() {
            @Override
            public void hideDeleteBtn() {
                deleteBtn.setVisibility(View.GONE);
            }
        });

        if (drawFlag) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mFrameLayout.addView(view, params);
                    drawFlag = false;
                }
            });
        } else {
            mFrameLayout.addView(view, params);
        }
    }


    public interface OnSwitcherListener {
        void switch2TextBookFragment();

        void switch2NoteBookFragment();

        void switch2ExerciseFragment();

    }

    /**
     * 绘制便签
     */
    private void drawLabel(final Note note) {
        Observable.create(new Observable.OnSubscribe<List<Label>>() {
            @Override
            public void call(Subscriber<? super List<Label>> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(note.getLabelList());
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Label>>() {
                    @Override
                    public void call(List<Label> labels) {
                        if (labels.size() > 0) {
                            labelCount = labels.size();
                            for (Label label : labels) {
                                FrameLayout.LayoutParams layoutParams = createLayoutParams();
                                layoutParams.leftMargin = label.getLeftMargin();
                                layoutParams.topMargin = label.getTopMargin();
                                LogUtils.e(TAG, "label is : " + label);
                                addLabel(layoutParams, label);
                            }
                        }
                    }
                });
    }

    /**
     * 绘制图片
     */
    private void drawPhoto(final Note note) {
        Observable.create(new Observable.OnSubscribe<List<Photograph>>() {
            @Override
            public void call(Subscriber<? super List<Photograph>> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(note.getPhotographList());
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Photograph>>() {
                    @Override
                    public void call(List<Photograph> photographs) {
                        if (photographs.size() > 0) {
                            for (Photograph photo : photographs) {
                                LogUtils.e(TAG, "photo is : " + photo);
                                FrameLayout.LayoutParams layoutParams = createLayoutParams();
                                layoutParams.leftMargin = photo.getLeftMargin();
                                layoutParams.topMargin = photo.getTopMargin();
                                Bitmap bitmap = ImageLoader.loadBmpFromBytes(photo.getBytes());
                                addPic(photo, bitmap, layoutParams);
                            }
                        }
                    }
                });
    }

    private void drawDiagram(final Note note) {
        Observable.create(new Observable.OnSubscribe<List<Diagram>>() {
            @Override
            public void call(Subscriber<? super List<Diagram>> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(note.getDiagramList());
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Diagram>>() {
                    @Override
                    public void call(List<Diagram> diagrams) {
                        if (diagrams.size() > 0) {
                            pictureCount = diagrams.size();
                            for (Diagram diagram : diagrams) {
                                LogUtils.e(TAG, "diagram is : " + diagram);
                                FrameLayout.LayoutParams params = createLayoutParams();
                                params.leftMargin = diagram.getLeftMargin();
                                params.topMargin = diagram.getTopMargin();
                                try {
                                    Picture picture = Picture.createFromStream(new FileInputStream(diagram.getDiagramPath()));
                                    addDiagram(params, picture, diagram);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
    }

    private void drawLines(final Note note) {
        Observable.create(new Observable.OnSubscribe<List<Line>>() {
            @Override
            public void call(Subscriber<? super List<Line>> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(note.getLines());
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Line>>() {
                    @Override
                    public void call(List<Line> lines) {
                        if (lines.size() > 0) {
                            mNoteBookView.drawLines(lines, true);
                        } else {
                            mNoteBookView.clear();
                        }
                    }
                });
    }

    protected void initNoteItem(final Note note) {
        if (note == null) {
            return;
        }
        drawLabel(note);
        drawPhoto(note);
        drawDiagram(note);
        drawLines(note);
        setNoteType();

    }

    protected void setNoteType() {

    }

    /**
     * 设置 全局的画笔对象 数据
     */
    private void setPanDrawStates(float panSize, int color, int sizePro, int alphPro) {
        mPaintDrawState.setPanSize(panSize);
        mPaintDrawState.setPanColor(color);
        mPaintDrawState.setPanSizeProgress(sizePro);
        mPaintDrawState.setPanAlphProgress(alphPro);
    }

    /**
     * 加载拷贝的截图
     */
    private void onLoadPaseImg() {
        final Uri uriPase = getItem().getUri();
        final FrameLayout.LayoutParams params = createLayoutParams();
        final Photograph photo = new Photograph();
        photo.setLeftMargin(params.leftMargin);
        photo.setTopMargin(params.topMargin);
        Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                Bitmap bitmap = BitmapFactory.decodeFile(uriPase.getPath());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                photo.setBytes(bos.toByteArray());
                boolean result = photo.save();
                LogUtils.e(TAG, "result is : " + result);
                mNote.getPhotographList().add(photo);
                mNote.save();
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(ImageLoader.loadBitmap(uriPase.getPath()));
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        addPic(photo, ImageLoader.loadBitmap(uriPase.getPath()), params);
                    }
                });
    }

    protected Subscription backScription;
    protected Subscription nextScription;
    protected Subscription initScription;

    @Override
    public void onDestroy() {
        super.onDestroy();
        leaveScribbleMode(false);
        if (backScription != null) {
            backScription.unsubscribe();
        }
        if (nextScription != null) {
            nextScription.unsubscribe();
        }
        if (initScription != null) {
            initScription.unsubscribe();
        }
    }

    //TODO:袁野
    public ControlFragmentActivity mControlActivity;

    public void setActivity(ControlFragmentActivity activity) {
        mControlActivity = activity;
    }


    private int[] thums = {R.drawable.img_2px_jindukuai, R.drawable.img_3px_jindukuai, R.drawable.img_4px_jindukuai, R.drawable.img_5px_jindukuai
            , R.drawable.img_6px_jindukua, R.drawable.img_7px_jindukuai, R.drawable.img_8px_jindukuai, R.drawable.img_9px_jindukuai, R.drawable.img_10px_jindukuai
            , R.drawable.img_11px_jindukuai, R.drawable.img_12px_jindukuai, R.drawable.img_13px_jindukuai, R.drawable.img_14px_jindukuai, R.drawable.img_15px_jindukuai
    };

    private void setThum(float progress) {
        int index = ((int) progress) - 2;
        Drawable thumD = getActivity().getResources().getDrawable(thums[index]);
        mSeekPenOrEraser.setThumb(thumD);//设置新的图片
        if (index < 3) {
            mSeekPenOrEraser.setThumbOffset(0);
        } else {
            mSeekPenOrEraser.setThumbOffset(30);
        }

    }

    @Override
    protected void onDownBookFinish() {
        super.onDownBookFinish();
        toTextBookFragment();
    }


}