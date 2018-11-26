package com.yougy.task;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.frank.etude.pageable.PageBtnBarAdapterV2;
import com.frank.etude.pageable.PageBtnBarV2;
import com.yougy.anwser.ContentDisplayerAdapterV2;
import com.yougy.anwser.ContentDisplayerV2;
import com.yougy.anwser.Content_new;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.ui.activity.R;
import com.yougy.view.NoteBookView2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author: zhang yc
 * @create date: 2018/10/31 9:35
 * @class desc: 资料详情显示界面
 * @modifier:
 * @modify date: 2018/10/31 9:35
 * @modify desc:
 */
public class MaterialActivity extends BaseActivity {

    @BindView(R.id.image_back)
    ImageView mImageBack;
    @BindView(R.id.image_refresh)
    ImageView mImageRefresh;
    @BindView(R.id.top_title)
    TextView mTopTitle;
    @BindView(R.id.material_content_display)
    ContentDisplayerV2 mMaterialContentDisplay;
    @BindView(R.id.material_pageBar)
    PageBtnBarV2 mMaterialPageBar;
    @BindView(R.id.material_content_display_layout)
    FrameLayout mLayoutNoteView;

    private int mMeterialCount = 1;
    private int mCurrentPosition = 0;//资料中这个变量没用
    private int mPageCount = 0;//资料总页
    private int mPageIndex = 0;//资料当前页
    private List<Content_new> testUrl = new ArrayList<>();

    private NoteBookView2 mNoteBookView2;
    private ArrayList<String> pathLists = new ArrayList<>();
    private int mMaterialId  = 10000;
    public static final String CACHE_KEY = "_material_task_";
    public static final String BITMAP_KEY = "_bitmap_material_task_";
    public static final String PAGE_TYPE_KEY = "MaterialDesc";
    private boolean isMultiPage = false;

    @Override
    protected void setContentView() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_materials_desc, null);
        setContentView(view);
    }

    @Override
    public void init() {
        testUrl.add(new Content_new(Content_new.Type.PDF, 1, "http://pre-global-questions.oss-cn-beijing.aliyuncs.com/2018/107020002/ae6a8ad6-529d-450f-909e-5e840ae13be6/sqs.pdf", null));
        initContentDisPlayer ();
        initPageBar();
    }

    @Override
    protected void initLayout() {
        initNoteView ();
    }


    @Override
    public void loadData() {
        mPageIndex = 0;
        mMaterialContentDisplay.getContentAdapter().updateDataList(PAGE_TYPE_KEY, testUrl);
        mMaterialContentDisplay.toPage(PAGE_TYPE_KEY, mPageIndex, true, mStatusChangeListener);
        mMaterialPageBar.selectPageBtn(mPageIndex, false);
        for (int i = 0; i < mMeterialCount; i++) {
            pathLists.add(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d("NoteView onResume.");
        UIUtils.postDelayed(() -> mNoteBookView2.setIntercept(false), 300);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.d("NoteView onOnPause.");
        SaveNoteUtils.getInstance(this).saveNoteViewData(mNoteBookView2,SaveNoteUtils.TASK_FILE_DIR,mMaterialId + CACHE_KEY + mPageIndex
            , mMaterialId + BITMAP_KEY + mPageIndex, true, mCurrentPosition, pathLists , isMultiPage);
    }

    @Override
    protected void refreshView() {

    }

    @OnClick({R.id.image_back, R.id.image_refresh})
    public void onClick (View view) {
        mNoteBookView2.leaveScribbleMode();
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.image_refresh:
                mNoteBookView2.setVisibility(View.GONE);
                loadData();
                break;
        }
    }

    private void initContentDisPlayer() {
        mMaterialContentDisplay.setContentAdapter(new ContentDisplayerAdapterV2() {
            @Override
            public void afterPageCountChanged(String typeKey) {
                mPageCount = getPageCount(typeKey);
                pathLists.clear();
            }
        });
    }

    private void initPageBar() {
        mMaterialPageBar.setPageBarAdapter(new PageBtnBarAdapterV2(getApplicationContext()) {
            @Override
            public int getPageBtnCount() {
                if (mPageCount > 1) {
                    isMultiPage = true;
                }  else {
                    isMultiPage = false;
                }
                return mPageCount;
            }

            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn, int lastSelectPageBtnIndex) {
                LogUtils.d("NoteView page Index = " + btnIndex + "   mPageIndex = " + mPageIndex);
                mNoteBookView2.leaveScribbleMode();
                if (btnIndex != mPageIndex)
                    SaveNoteUtils.getInstance(MaterialActivity.this).saveNoteViewData(mNoteBookView2, SaveNoteUtils.TASK_FILE_DIR,mMaterialId + CACHE_KEY + mPageIndex
                            , mMaterialId + BITMAP_KEY + mPageIndex, true,mCurrentPosition, pathLists , isMultiPage);
                mPageIndex = btnIndex;
                mMaterialContentDisplay.toPage(PAGE_TYPE_KEY, btnIndex, true, mStatusChangeListener);
                mNoteBookView2.clearAll();
                mNoteBookView2.leaveScribbleMode(true);
            }

            @Override
            public void onNoPageToShow() {

            }
        });
    }

    private ContentDisplayerV2.StatusChangeListener mStatusChangeListener = (newStatus, typeKey, pageIndex, url, errorType, errorMsg) -> {
        switch (newStatus) {
            case DOWNLOADING:
            case LOADING:
                mMaterialContentDisplay.setHintText("加载中...");
                break;
            case ERROR:
                mMaterialContentDisplay.setHintText("错误：" + errorMsg);
                break;
            case SUCCESS:
                LogUtils.d("NoteView mStatusChangeListener SUCCESS.");
                mMaterialContentDisplay.setHintText(null);//隐藏
                mMaterialPageBar.refreshPageBar();
                if (mNoteBookView2.getVisibility() ==View.GONE) {}
                SaveNoteUtils.getInstance(MaterialActivity.this).resetNoteView(mNoteBookView2, mMaterialId + CACHE_KEY + mPageIndex,
                        mMaterialId + BITMAP_KEY + mPageIndex, SaveNoteUtils.TASK_FILE_DIR);
                break;

        }
    };

    private void initNoteView () {
        mNoteBookView2 = new NoteBookView2(this);
        mNoteBookView2.leaveScribbleMode(true);
        mNoteBookView2.setIntercept(true);
        mLayoutNoteView.addView(mNoteBookView2);
    }

}
