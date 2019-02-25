package com.yougy.task.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.frank.etude.pageable.PageBtnBarAdapterV2;
import com.frank.etude.pageable.PageBtnBarV2;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.AliyunUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.task.ContentDisPlayer;
import com.yougy.task.ContentDisPlayerAdapter;
import com.yougy.task.LoadAnswer;
import com.yougy.task.bean.ReadTimeBean;
import com.yougy.task.bean.StageTaskBean;
import com.yougy.task.fragment.MaterialsBaseFragment;
import com.yougy.ui.activity.R;
import com.yougy.view.NoteBookView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

public class MaterialActivity2 extends BaseActivity {

    @BindView(R.id.image_back)
    ImageView mImageBack;
    @BindView(R.id.material_content_display)
    ContentDisPlayer mMaterialContentDisplay;
    @BindView(R.id.material_pageBar)
    PageBtnBarV2 mMaterialPageBar;
    @BindView(R.id.material_content_display_layout)
    FrameLayout mLayoutNoteView;

    @BindView(R.id.paint_draw)
    ImageView mImagePaint;
    @BindView(R.id.eraser)
    ImageView mImageEraser;
    @BindView(R.id.undo)
    ImageView mImageUndo;
    @BindView(R.id.redo)
    ImageView mImageRedo;

    @BindView(R.id.pen_eraser_layout)
    RelativeLayout mRelativeLayout;
    @BindView(R.id.pen_eraser_seek)
    SeekBar mSeekBar;


    private int mMeterialCount = 1;
    private int mPageCount = 0;//资料总页
    private int mPageIndex = 0;//资料当前页
//    private List<StageTaskBean.StageContent> testUrl = new ArrayList<>();

    private NoteBookView mNoteBookView;
//    private ArrayList<String> pathLists = new ArrayList<>();
//    private int mMaterialId  = 10000;
    public static final String CACHE_KEY = "_material_task_";
    public static final String BITMAP_KEY = "_bitmap_material_task_";
    public static final String PAGE_TYPE_KEY = "MaterialDesc";


    private boolean isHadComplete = false;
    private StageTaskBean mStageTaskBean;
    private StageTaskBean.StageContent stageContent;
    private LoadAnswer mLoadAnswer;

    private int taskID;
    private int stageId;

    private int mCurrentPosition;

    private long readTimeStart = 0;
    private int readTime = 0;
    private String saveTimeKey;

    @Override
    public void loadIntent(Context packageContext, Class<?> cls) {
        super.loadIntent(packageContext, cls);
    }

    private String mCurrentUrl = "";

    @Override
    protected void setContentView() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_materials_desc2, null);
        setContentView(view);
    }

    @Override
    public void init() {
        mLoadAnswer = new LoadAnswer(this);
        mStageTaskBean = MaterialsBaseFragment.sStageTaskBean;
        isHadComplete = getIntent().getBooleanExtra("isHadComplete", false);
        mCurrentPosition = getIntent().getIntExtra("mCurrentPosition", 0);
        if (mStageTaskBean == null) {
            ToastUtil.showCustomToast(getApplicationContext(),"任务资料打开失败！");
            finish();
            return;
        }
        stageId = mStageTaskBean.getStageId();
        taskID = getIntent().getIntExtra("mTaskId", 0);
        LogUtils.i("TaskTest material : taskID =  " + taskID + "" + "  stageId = " + stageId);
        List<StageTaskBean.StageContent> stageContents = mStageTaskBean.getStageContent();
        LogUtils.i("TaskTest material size :" + stageContents.size() + "  mCurrentPosition =" + mCurrentPosition);
        if (stageContents.size() > 0) {
            this.stageContent = stageContents.get(0);
        } else {
            ToastUtil.showCustomToast(getApplicationContext(), "position error!");
            finish();
            return;
        }
        String bucket = this.stageContent.getBucket();
        String remote = this.stageContent.getRemote();
        mCurrentUrl = "http://" + bucket + AliyunUtil.ANSWER_PIC_HOST + remote;
        LogUtils.d("TaskTest bucket = " + bucket + "  remote = " + remote + "  mCurrentUrl = " + mCurrentUrl);
        saveTimeKey = taskID + "_" + stageId +"_readTime_" + mCurrentPosition;
        initContentDisPlayer ();
        initPageBar();
    }

    @Override
    protected void initLayout() {
        initNoteView ();
        initSeekBar();
        mLoadAnswer.loadAnswer(mNoteBookView, mStageTaskBean, 0, mPageIndex);
    }


    @Override
    public void loadData() {
        mPageIndex = 0;
        ContentDisPlayerAdapter contentAdapter = mMaterialContentDisplay.getContentAdapter();
//        contentAdapter.updateDataList(PAGE_TYPE_KEY, "http://la.lovewanwan.top/1.png", "IMG");
        if (mStageTaskBean.getStageContent().size() > 0 ) {
            String format = mStageTaskBean.getStageContent().get(0).getFormat();
            if (format.contains("pdf") || format.contains("PDF") || format.contains("txt")) {
                format = "PDF";
            } else {
                format = "IMG";
            }
            LogUtils.d("TaskTest format = " + format  + "  mCurrentUrl = " + mCurrentUrl);
            contentAdapter.updateDataList(PAGE_TYPE_KEY, mCurrentUrl, format);
            mMaterialContentDisplay.toPage(PAGE_TYPE_KEY, mPageIndex, true, mStatusChangeListener);
            mMaterialPageBar.selectPageBtn(mPageIndex, false);
        }
        readTime = 0;
        readTimeStart = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d("NoteView onResume.");
        if (readTime > 0) {
            readTime = readTime / 1000;
            NetWorkManager.readMaterialTime(String.valueOf(SpUtils.getUserId()),String.valueOf(stageId),
                    null, String.valueOf(readTime)).subscribe(readTimeBean -> LogUtils.i("TaskTest task material time update success!" + readTime),
                    throwable -> LogUtils.d("TaskTest interface studentReply return error!"));
        } else {
            LogUtils.e("TaskTest system time is error.");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.d("NoteView onOnPause.");
        String[] cacheBitmapKey = getCacheBitmapKey(mCurrentPosition, mPageIndex);
        SaveNoteUtils.getInstance(getApplicationContext()).saveNoteViewData(mNoteBookView, SaveNoteUtils.getInstance(getApplicationContext()).getTaskFileDir(),
                cacheBitmapKey[0], cacheBitmapKey[1], String.valueOf(taskID), stageId);
        if (NetUtils.isNetConnected()) {
            readTime = (int) ((System.currentTimeMillis() - readTimeStart) / 1000);
            if (readTime > 0) {
                NetWorkManager.readMaterialTime(String.valueOf(SpUtils.getUserId()),String.valueOf(stageId),
                        null, String.valueOf(readTime)).subscribe(readTimeBean -> {
                            LogUtils.i("TaskTest task material time update success!" + readTime);
                            SpUtils.putInt(saveTimeKey, readTime + SpUtils.getInt(saveTimeKey));
                        },
                        throwable -> LogUtils.d("TaskTest interface studentReply return error!"));
            } else {
                LogUtils.e("TaskTest system time is error.");
            }
        } else {
            readTime = (int) ((System.currentTimeMillis() - readTimeStart) / 1000);
        }
    }


    private String[] getCacheBitmapKey (int position, int page) {
        String cacheKey = taskID + "_" + stageId + CACHE_KEY + position + "_" + page;
        String bitmapKey = taskID + "_" + stageId + BITMAP_KEY + position + "_" + page;
        LogUtils.w("TaskTest cacheKey is Null, position IndexOfArray Exception.");
        return new String[]{cacheKey, bitmapKey};
    }

    @Override
    protected void refreshView() {

    }

    @OnClick({R.id.image_back, R.id.paint_draw, R.id.eraser, R.id.undo, R.id.redo})
    public void onClick (View view) {
        leaveScribbleMode (true);
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.paint_draw:
                clickPen();
                break;
            case R.id.eraser:
                clickEraser();
                break;
            case R.id.undo:

                break;
            case R.id.redo:

                break;
        }
    }

    public void leaveScribbleMode(boolean needFreshUI) {
        if (null != mNoteBookView) {
            EpdController.leaveScribbleMode(mNoteBookView);
        }

        if (needFreshUI && null != mNoteBookView && mNoteBookView.isContentChanged()) {
            mNoteBookView.invalidate();
        }
    }

    private boolean mCurrentIsPen = true;

    private void clickPen () {
        mNoteBookView.outSetPenSize(mSeekBarPen);
        if (mRelativeLayout.getVisibility() == View.GONE) {
            mRelativeLayout.setVisibility(View.VISIBLE);
            mSeekBar.setProgress(mSeekBarPen);
        } else {
            if (mCurrentIsPen) {
                mRelativeLayout.setVisibility(View.GONE);
            } else {
                mSeekBar.setProgress(mSeekBarPen);
            }
        }
        mCurrentIsPen = true;
    }

    private void clickEraser () {
        mNoteBookView.outSetEraserSize(mSeekBarEraser);
        if (mRelativeLayout.getVisibility() == View.GONE) {
            mRelativeLayout.setVisibility(View.VISIBLE);
            mSeekBar.setProgress(mSeekBarEraser);
        } else {
            if (mCurrentIsPen) {
                mSeekBar.setProgress(mSeekBarEraser);
            } else {
                mRelativeLayout.setVisibility(View.GONE);
            }
        }
        mCurrentIsPen = false;
    }


    private int mSeekBarEraser = 2;
    private int mSeekBarPen = 2;
    private void initSeekBar () {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (mCurrentIsPen) {
                    mSeekBarPen = progress;
                } else {
                    mSeekBarEraser = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mCurrentIsPen) {
                    mNoteBookView.outSetPenSize(mSeekBarPen);
                } else {
                    mNoteBookView.outSetEraserSize(mSeekBarEraser);
                }
                leaveScribbleMode(true);
            }
        });
    }



    private void initContentDisPlayer() {
        LogUtils.d("TaskTest initContentDisPlayer.");
        mMaterialContentDisplay.setContentAdapter(new ContentDisPlayerAdapter() {
            @Override
            public void afterPageCountChanged(String typeKey) {
                mPageCount = getPageCount(typeKey);
            }
        });
    }

    private void initPageBar() {
        mMaterialPageBar.setPageBarAdapter(new PageBtnBarAdapterV2(getApplicationContext()) {
            @Override
            public int getPageBtnCount() {
                return mPageCount;
            }

            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn, int lastSelectPageBtnIndex) {
                LogUtils.d("NoteView page Index = " + btnIndex + "   mPageIndex = " + mPageIndex);
                mNoteBookView.leaveScribbleMode();
                if (btnIndex != mPageIndex){
                    String[] cacheBitmapKey = getCacheBitmapKey(mCurrentPosition, mPageIndex);
                    SaveNoteUtils.getInstance(getApplicationContext()).saveNoteViewData(mNoteBookView, SaveNoteUtils.getInstance(getApplicationContext()).getTaskFileDir(),
                            cacheBitmapKey[0], cacheBitmapKey[1], String.valueOf(taskID),stageId);
                }
                mPageIndex = btnIndex;
                mMaterialContentDisplay.toPage(PAGE_TYPE_KEY, btnIndex, false, mStatusChangeListener);
                mNoteBookView.clear();
                mNoteBookView.leaveScribbleMode();
            }

            @Override
            public void onNoPageToShow() {

            }
        });
    }

    private ContentDisPlayer.StatusChangeListener mStatusChangeListener = (newStatus, typeKey, url, errorType, errorMsg) -> {
        switch (newStatus) {
            case DOWNLOADING:
            case LOADING:
                mMaterialContentDisplay.setHintText("加载中...");
                break;
            case ERROR:
                LogUtils.e("NoteView mStatusChangeListener 错误："  + errorMsg);
                mMaterialContentDisplay.setHintText("加载失败,点击刷新重试！");
                mMaterialContentDisplay.setOnClickListener(view -> loadData());
                break;
            case SUCCESS:
                LogUtils.d("NoteView mStatusChangeListener SUCCESS.");
                mMaterialContentDisplay.setHintText(null);//隐藏
                mMaterialPageBar.refreshPageBar();
                if (isHadComplete) {
                    mLoadAnswer.loadAnswer(mNoteBookView, mStageTaskBean, 0 , mPageIndex);
                } else {
                    UIUtils.postDelayed(() -> mNoteBookView.setIntercept(false), 20);
                    String[] cacheBitmapKey = getCacheBitmapKey(mCurrentPosition, mPageIndex);
                    SaveNoteUtils.getInstance(getApplicationContext()).resetNoteView(mNoteBookView, cacheBitmapKey[0], cacheBitmapKey[1],
                        SaveNoteUtils.getInstance(getApplicationContext()).getTaskFileDir());
//                    SaveNoteUtils.getInstance(MaterialActivity2.this).resetNoteView(mNoteBookView, taskID + CACHE_KEY + mPageIndex,
//                            mMaterialId + BITMAP_KEY + mPageIndex, SaveNoteUtils.getInstance(getApplicationContext()).getTaskFileDir());
                }
                break;

        }
    };

    private void initNoteView () {
        mNoteBookView = new NoteBookView(this);
        mNoteBookView.leaveScribbleMode(true);
        mNoteBookView.setIntercept(true);
        mLayoutNoteView.addView(mNoteBookView);
        mNoteBookView.outSetPenSize(mSeekBarPen);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        leaveScribbleMode(false);
    }
}
