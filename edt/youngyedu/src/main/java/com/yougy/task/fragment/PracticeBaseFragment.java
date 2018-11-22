package com.yougy.task.fragment;


import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yougy.anwser.ContentDisplayerAdapterV2;
import com.yougy.anwser.ContentDisplayerV2;
import com.yougy.anwser.Content_new;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.RefreshUtil;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.task.SaveNoteUtils;
import com.yougy.task.TaskDetailStudentActivity;
import com.yougy.task.bean.TaskPracticeBean;
import com.yougy.ui.activity.R;
import com.yougy.view.ContentPdfImageView;
import com.yougy.view.NoteBookView2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.greenrobot.event.EventBus;

public class PracticeBaseFragment extends TaskBaseFragment {

    @BindView(R.id.practice_constrain_layout)
    ConstraintLayout mConstraintLayout;
    @BindView(R.id.task_practice_viewStub)
    ViewStub mTaskPracticeViewStub;
    @BindView(R.id.prev_practice)
    TextView mPrevPractice;
    @BindView(R.id.next_practice)
    TextView mNextPractice;
    @BindView(R.id.view_line)
    View mViewLine;
    @BindView(R.id.task_practice_pageBar)
    RecyclerView mTaskPracticePageBar;
    @BindView(R.id.content_disPlayer)
    ContentDisplayerV2 mContentDisplayer;
    @BindView(R.id.select_practice)
    TextView mSelectPractice;
    @BindView(R.id.image_bg_practice)
    ImageView mImageBg;

    @BindView(R.id.layout_caoGao)
    LinearLayout mLayoutCaoGao;
    @BindView(R.id.text_practice_caoGao)
    TextView mTextCaoGao;
    @BindView(R.id.practice_noteView)
    NoteBookView2 mNoteBookView;
    @BindView(R.id.noteView_caoGao)
    NoteBookView2 mCaoGaoNoteView;
    @BindView(R.id.view_line2)
    View mViewLine2;

    public static final String CACEH_KEY = "_task_practice_cache_";
    public static final String BITMAP_KEY = "_task_practice_bitmap_";

    private int prevSavePosition = 0;
    private int currentSelectPosition = 0;
    private int practiceTotalCount = 10;//总共的练习数量
    private int singlePracticePage = 1;//单个练习有几页
    private int mAddPageCaoGao = 0;//添加页数
    private int mPageCount = 0;//练习页数加上添加的页数
    private int prevPage = 0;
    private int currentPage = 0;

    private List<TaskPracticeBean> mTaskPracticeBeans = new ArrayList<>();
    public static final String TAG_KEY = "taskPractice";

//    private List<String> mPracticeNames = new ArrayList<>();//练习题名
    private boolean mCurrentShowItems = false;


    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_task_practice, container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);
        initContentDisPlayer();
        initPageBar();
        initNoteView(mNoteBookView);
        return mRootView;
    }

    @Override
    protected void initNoteView(NoteBookView2 noteBookView2) {
        super.initNoteView(noteBookView2);
        mNoteBookView.setIntercept(true);
        mCurrentCacheName = "_practice_stu_";
    }

    @Override
    public void loadData() {
        super.loadData();
        dismissPopupWindow();
        for (int i = 0; i < practiceTotalCount; i++) {
            List<Content_new> mPracticeLists = new ArrayList<>();
            if (i % 4 == 0) {
                mPracticeLists.add (new Content_new(Content_new.Type.PDF, 1 , "http://pre-global-questions.oss-cn-beijing.aliyuncs.com/2018/107020002/2bef61b7-6bae-443d-ac00-890713ecb723/abc.pdf" , null));
            } else if (i % 4 == 1) {
                mPracticeLists.add (new Content_new(Content_new.Type.PDF, 1 , "http://pre-global-questions.oss-cn-beijing.aliyuncs.com/2018/107020002/45581abc-46dc-4e2d-8f72-c7992de7fde5/abc.pdf" , null));
            } else if (i % 4 == 2) {
                mPracticeLists.add (new Content_new(Content_new.Type.PDF, 1 , "http://pre-global-questions.oss-cn-beijing.aliyuncs.com/2018/107020002/ae6a8ad6-529d-450f-909e-5e840ae13be6/sqs.pdf" , null));
            } else {
                mPracticeLists.add (new Content_new(Content_new.Type.PDF, 1 , "http://pre-global-questions.oss-cn-beijing.aliyuncs.com/2018/107020002/ae6a8ad6-529d-450f-909e-5e840ae13be6/sqs.pdf" , null));
            }
            TaskPracticeBean taskPracticeBean = new TaskPracticeBean("No"+(i + 1)+"题",mPracticeLists);
            mTaskPracticeBeans.add(taskPracticeBean);
            pathLists.add(null);
        }
        change(currentSelectPosition);
        if (!isHadCommit)
            UIUtils.postDelayed(() -> PracticeBaseFragment.this.leaveScribbleMode(true, false), 500);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        LogUtils.d("Practice  onPause...");


        showOrHideCaoGaoLayout(false, false);
        if(!isHadCommit) {
            //未提交完，保存\
            int currentId = mTaskPracticeBeans.get(currentSelectPosition).getPracticeId();
            String cacheKey = currentId + CACEH_KEY + currentSelectPosition + "_" + currentPage;
            String bitmapKey = currentId + BITMAP_KEY + currentSelectPosition + "_" + currentPage;
            boolean isMultiPage = currentPage > 0 ;
            SaveNoteUtils.getInstance(mContext).saveNoteViewData(mNoteBookView2, SaveNoteUtils.TASK_FILE_DIR,
                    cacheKey, bitmapKey, true, currentSelectPosition, pathLists, isMultiPage);

//            saveNoteViewData(mPracticeNames.get(currentSelectPosition), currentSelectPosition, currentPage);
            leaveScribbleMode(false, true);
        }

    }

    @Override
    protected void handlerRequestSuccess() {
        super.handlerRequestSuccess();
    }

    @Override
    protected void handlerRequestFail() {
        super.handlerRequestFail();
        showDataEmpty(View.VISIBLE);
    }

    @Override
    protected void showDataEmpty(int visibility) {
        super.showDataEmpty(visibility);
        if (mDataEmptyView == null) {
            mDataEmptyView = mTaskPracticeViewStub.inflate();
            TextView text = mDataEmptyView.findViewById(R.id.text_empty);
            text.setText(mContext.getString(R.string.str_task_practice_empty));
        } else {
            mDataEmptyView.setVisibility(visibility);
        }
    }

    @Override
    protected void unInit() {
        super.unInit();
        if (mPageBind != null) mPageBind.unbind();
    }


    private void initPageBar() {
        if (!mTaskDetailStudentActivity.isBottomBtnShow()) {
            ConstraintSet practiceConstrainSet = new ConstraintSet();
            practiceConstrainSet.clone(mConstraintLayout);
            practiceConstrainSet.setMargin(R.id.view_line2, ConstraintSet.TOP, 34);
            practiceConstrainSet.setMargin(R.id.task_practice_pageBar, ConstraintSet.TOP, 24);
            practiceConstrainSet.setMargin(R.id.text_practice_clear, ConstraintSet.TOP, 20);
            practiceConstrainSet.setMargin(R.id.text_practice_caoGao, ConstraintSet.TOP, 20);
            practiceConstrainSet.setMargin(R.id.img_add_page, ConstraintSet.TOP, 24);
            practiceConstrainSet.applyTo(mConstraintLayout);

            ViewGroup.LayoutParams layoutParams = mImageBg.getLayoutParams();
            layoutParams.width = 908;
            layoutParams.height = 944;
            mImageBg.setLayoutParams(layoutParams);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mTaskPracticePageBar.setLayoutManager(linearLayoutManager);
        mTaskPracticePageBar.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.right = 10;
                outRect.left = 10;
            }
        });
        mTaskPageAdapter = new TaskPageAdapter();
        mTaskPracticePageBar.setAdapter(mTaskPageAdapter);
    }

    private TaskPageAdapter mTaskPageAdapter;
    class TaskPageAdapter extends RecyclerView.Adapter<PageViewHolder> {


        @NonNull
        @Override
        public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PageViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_task_page_index, null));
        }

        @Override
        public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
            holder.mTextView.setText(String.valueOf(position + 1));
            boolean selected = position == currentPage;
            holder.mTextView.setSelected(selected);
            holder.mTextView.setOnClickListener(v -> {
                currentPage = position;
                autoScrollRecyclerView((LinearLayoutManager) mTaskPracticePageBar.getLayoutManager(), false);
                mTaskPageAdapter.notifyDataSetChanged();
                clickPageIndex ();
            });
        }

        @Override
        public int getItemCount() {
            if (singlePracticePage + mAddPageCaoGao > 1) {
                isMultiPage = true;
            } else {
                isMultiPage = false;
            }
            return singlePracticePage + mAddPageCaoGao;
        }

    }

    /**
     *  保存上一页轨迹
     */
    private void clickPageIndex () {
        String cacheKey = "addPage" + CACEH_KEY + prevSavePosition + "_" + prevPage;
        String bitmapKey = "addPage" + BITMAP_KEY + prevSavePosition + "_" + prevPage;
        int currentId = 0;
        if (prevSavePosition >= 0 && prevSavePosition < mTaskPracticeBeans.size()) {
            currentId = mTaskPracticeBeans.get(prevSavePosition).getPracticeId();
            cacheKey = currentId + CACEH_KEY + prevSavePosition + "_" + prevPage;
            bitmapKey = currentId + BITMAP_KEY + prevSavePosition + "_" + prevPage;
        } else {
            LogUtils.w(" prev page is error. prevSavePosition = " + prevSavePosition);
        }
        if (prevSavePosition != currentSelectPosition || prevPage != currentPage) {
            boolean isMultiPage = mPageCount > 0;
            SaveNoteUtils.getInstance(mContext).saveNoteViewData(mNoteBookView2, SaveNoteUtils.TASK_FILE_DIR,
                    cacheKey, bitmapKey, true, prevSavePosition, pathLists, isMultiPage);
        }
        LogUtils.d("cacheKey " + cacheKey + "\n bitmapKey = " + bitmapKey);
        leaveScribbleMode(false, true);
        if (currentPage < singlePracticePage) {
            mContentDisplayer.setVisibility(View.VISIBLE);
            mContentDisplayer.toPage(TAG_KEY, currentPage, true, mStatusChangeListener);
        } else {
            mContentDisplayer.setVisibility(View.GONE);
        }
        cacheKey = currentId + CACEH_KEY + prevSavePosition + "_" + currentPage;
        bitmapKey = currentId + BITMAP_KEY + prevSavePosition + "_" + currentPage;
        SaveNoteUtils.getInstance(mContext).resetNoteView(mNoteBookView2, cacheKey, bitmapKey,  SaveNoteUtils.TASK_FILE_DIR);
        leaveScribbleMode(true, false);
        prevPage = currentPage;
    }

    private Unbinder mPageBind;
    public class PageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_practice_page_index)
        TextView mTextView;
        public PageViewHolder(View itemView) {
            super(itemView);
            mPageBind = ButterKnife.bind(this, itemView);
        }
    }

    private void initContentDisPlayer() {
        int childCount = mContentDisplayer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (mContentDisplayer.getChildAt(i) instanceof  ImageView) {
                ((ImageView)mContentDisplayer.getChildAt(i)).setScaleType(ImageView.ScaleType.FIT_XY);
            }
            if (mContentDisplayer.getChildAt(i) instanceof ContentPdfImageView) {
                ((ContentPdfImageView)mContentDisplayer.getChildAt(i)).setScaleType(ImageView.ScaleType.FIT_XY);
            }
        }
        mMyContentDisPlayerAdapter = new MyContentDisPlayerAdapter();
        mContentDisplayer.setContentAdapter(mMyContentDisPlayerAdapter);
        mContentDisplayer.setLoadingStatusListener(mStatusChangeListener);
    }

    private MyContentDisPlayerAdapter mMyContentDisPlayerAdapter;
    public class MyContentDisPlayerAdapter extends ContentDisplayerAdapterV2 {

        @Override
        public void afterPageCountChanged(String typeKey) {
            singlePracticePage = getPageCount(typeKey);
            mPageCount = mAddPageCaoGao + singlePracticePage;
            setRecyclerViewWidth();
        }
    }

    private void setRecyclerViewWidth (){
        ViewGroup.LayoutParams layoutParams = mTaskPracticePageBar.getLayoutParams();
        if (singlePracticePage + mAddPageCaoGao > 5) {
            layoutParams.width = 300;
        } else {
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mTaskPracticePageBar.setLayoutParams(layoutParams);
    }

    private ContentDisplayerV2.StatusChangeListener mStatusChangeListener = (newStatus, typeKey, pageIndex, url, errorType, errorMsg) -> {
        if (mContentDisplayer == null) return;
        switch (newStatus) {
            case DOWNLOADING:
            case LOADING:
                leaveScribbleMode(false, false);
                mContentDisplayer.setHintText("加载中...");
                break;
            case ERROR:
                leaveScribbleMode(false, false);
                mContentDisplayer.setHintText("错误：" + errorMsg);
                break;
            case SUCCESS:
                leaveScribbleMode(true, false);
                mContentDisplayer.setHintText(null);//隐藏
                mTaskPageAdapter.notifyDataSetChanged();

                int currentId = mTaskPracticeBeans.get(currentSelectPosition).getPracticeId();
                String cacheKey = currentId + CACEH_KEY + currentSelectPosition + "_" + currentPage;
                String bitmapKey = currentId + BITMAP_KEY + currentSelectPosition + "_" + currentPage;
                SaveNoteUtils.getInstance(mContext).resetNoteView(mNoteBookView2,cacheKey, bitmapKey, SaveNoteUtils.TASK_FILE_DIR);
                prevSavePosition = currentSelectPosition;
                prevPage = currentPage;
                break;
        }
    };


    @OnClick({R.id.prev_practice, R.id.next_practice , R.id.select_practice,R.id.text_practice_caoGao, R.id.text_practice_clear
                , R.id.img_add_page, R.id.text_hide_caoGao})
    public void onClick(View view) {
        if (mCurrentShowItems && view.getId() != R.id.select_practice) {
            showSelectItem(false);
        }
        switch (view.getId()) {
            case R.id.prev_practice:
                changePractice(false);
                break;
            case R.id.next_practice:
                changePractice(true);
                break;
            case R.id.select_practice:
                leaveScribbleMode(false, false);
                showSelectItem(!mCurrentShowItems);
                break;
            case R.id.text_practice_caoGao:
                isCaoGaoShow = !isCaoGaoShow;
                showOrHideCaoGaoLayout(isCaoGaoShow, true);
                break;
            case R.id.text_hide_caoGao:
                showOrHideCaoGaoLayout(false, false);
                break;
            case R.id.text_practice_clear:
                if (isCaoGaoShow) {
                    mCaoGaoNoteView.leaveScribbleMode();
                    mCaoGaoNoteView.clearAll();
                    mCaoGaoNoteView.leaveScribbleMode(true);
                } else {
                    leaveScribbleMode(false, true);
                    leaveScribbleMode(true, false);
                }
                break;
            case R.id.img_add_page:
                addPage();
                break;
        }
    }

    private synchronized void addPage () {
        if (mAddPageCaoGao == 5) {
            ToastUtil.showCustomToast(mContext, "最多添加5页！");
            return;
        }
        prevPage = currentPage;
        currentPage = mAddPageCaoGao + singlePracticePage;
        mAddPageCaoGao ++;
        pathLists.add(null);
        mPageCount = mAddPageCaoGao + singlePracticePage;
        clickPageIndex();
        autoScrollRecyclerView ((LinearLayoutManager) mTaskPracticePageBar.getLayoutManager(), true);
        setRecyclerViewWidth();
        LogUtils.d("singlePracticePage = " + singlePracticePage);
        mTaskPageAdapter.notifyDataSetChanged();
    }

    private int minIndex, maxIndex;
    private void autoScrollRecyclerView (LinearLayoutManager linearLayoutManager, boolean isAdd) {
        minIndex = linearLayoutManager.findFirstVisibleItemPosition();
        maxIndex = linearLayoutManager.findLastVisibleItemPosition();
        int pageTotalCount = singlePracticePage + mAddPageCaoGao;
        LogUtils.d("currentPage = " + currentPage + "  minIndex = " + minIndex + "   maxIndex = " + maxIndex);
        if (isAdd) {
            if ( pageTotalCount > 5)
                linearLayoutManager.scrollToPosition(currentPage);
        } else {
            if (currentPage == minIndex && currentPage > 0 ) {
                int temp = currentPage - 4;
                if (temp < 0 ) temp = 0;
                linearLayoutManager.scrollToPosition(temp);
            } else if (currentPage == maxIndex && currentPage < pageTotalCount - 1) {
                int temp = currentPage + 4 ;
                if (temp >= pageTotalCount - 1)  temp = pageTotalCount - 1;
                linearLayoutManager.scrollToPosition(temp);
            }
        }
    }

    private boolean isCaoGaoShow  = false;
    /**
     * 显示隐藏草稿纸
     */
    private void showOrHideCaoGaoLayout (boolean isVisibility, boolean isClearCaoGao) {
        if (isHadCommit) return;
        if (isVisibility) {
            mTextCaoGao.setText("扔掉草稿纸");
            mTextCaoGao.setTextSize(20);
            mLayoutCaoGao.setVisibility(View.VISIBLE);
            leaveScribbleMode(false, false);
            mCaoGaoNoteView.setIntercept(false);
            mCaoGaoNoteView.leaveScribbleMode(true);
        } else {
            mTextCaoGao.setText("草稿纸");
            mTextCaoGao.setTextSize(24);
            leaveScribbleMode(true, false);
            mCaoGaoNoteView.setIntercept(true);
            if (isClearCaoGao)
                mCaoGaoNoteView.clearAll();
            mCaoGaoNoteView.leaveScribbleMode();
            mLayoutCaoGao.setVisibility(View.GONE);
        }
        RefreshUtil.invalidate(mTextCaoGao);
        isCaoGaoShow = isVisibility;
    }

    /**
     * 切换上下题
     *
     * @param isNext
     */
    private void changePractice(boolean isNext) {
        if (isNext) {
            if (currentSelectPosition < practiceTotalCount - 1) {
                currentSelectPosition++;
            } else {
                LogUtils.w("practice currentSelectPosition is max value, return");
                return;
            }
        } else {
            if (currentSelectPosition > 0) {
                currentSelectPosition--;
            } else {
                LogUtils.w("practice currentSelectPosition is 0, return.");
                return;
            }
        }
        change(currentSelectPosition);
    }

    /**
     * 切换到指定的位置
     * @param position
     */
    private synchronized void change(int position) {
        dismissPopupWindow();
        currentPage = 0;
        mAddPageCaoGao = 0;
        currentSelectPosition = position;
        showOrHideCaoGaoLayout(false, false);
        setCurrentPracticeInfo();
    }

    private void setCurrentPracticeInfo() {
        if (prevSavePosition != currentSelectPosition) {
            int currentId = mTaskPracticeBeans.get(prevSavePosition).getPracticeId();
            String cacheKey = currentId + CACEH_KEY + prevSavePosition + "_" + prevPage;
            String bitmapKey = currentId + BITMAP_KEY + prevSavePosition + "_" + prevPage;
            boolean isMultiPage = prevPage > 0 ? true : false;
            SaveNoteUtils.getInstance(mContext).saveNoteViewData(mNoteBookView2, SaveNoteUtils.TASK_FILE_DIR,
                    cacheKey, bitmapKey, true, prevSavePosition, pathLists, isMultiPage);
        }
        leaveScribbleMode(false, true);
        mSelectPractice.setText(String.format(mContext.getString(R.string.str_select_practice) + "(" + (currentSelectPosition + 1) + "/" + practiceTotalCount + ")" ));
        mContentDisplayer.getContentAdapter().deleteDataList(TAG_KEY);
        mContentDisplayer.getContentAdapter().updateDataList(TAG_KEY, mTaskPracticeBeans.get(currentSelectPosition).getContent_news());
        mContentDisplayer.setVisibility(View.VISIBLE);
        mContentDisplayer.toPage(TAG_KEY, currentPage, true, mStatusChangeListener);

        mTaskPageAdapter.notifyDataSetChanged();
        leaveScribbleMode(true, false);
    }

    private void showSelectItem (boolean isShow) {
        LogUtils.d("show select item popup window : " + isShow);
        mSelectPractice.setSelected(isShow);
        if (isShow) {
            showPopupWindow();
        } else {
            dismissPopupWindow();
        }
        mCurrentShowItems = isShow;
        LogUtils.d("showSelectItem mCurrentShowItems  = " + mCurrentShowItems);
    }

    private PopupWindow mPopupWindow;
    private void showPopupWindow () {
        View view = LayoutInflater.from(mContext).inflate(R.layout.select_practice_popupwindow, null);
        mPopupWindow = new PopupWindow(mContext);
        mPopupWindow.setWidth(960);
        mPopupWindow.setContentView(view);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setTouchable(true);
        RecyclerView recyclerView = view.findViewById(R.id.item_practice_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 8, LinearLayoutManager.VERTICAL, false){
            @Override
            public boolean canScrollVertically() {
                if (mPageCount > 104) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int childAdapterPosition = parent.getChildAdapterPosition(view);
                int index = childAdapterPosition % 8;
                if (index == 0 || index == 2 || index == 5 || index == 7) {
                    outRect.left = 31;
                    outRect.right = 30;
                } else if (index == 1 || index == 3) {
                    outRect.left = 30;
                    outRect.right = 30;
                } else {
                    outRect.left = 30;
                    outRect.right = 31;
                }
                outRect.top = 10;
                outRect.bottom = 10;
            }
        });

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(new RecyclerView.Adapter<ViewHolder>() {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_spinner_select_practice, null, false);
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                holder.mTextView.setText(String.valueOf(position + 1));
                if (currentSelectPosition == position) {
                    holder.mTextView.setSelected(true);
                    holder.mImageView.setSelected(true);
                } else {
                    holder.mTextView.setSelected(false);
                    holder.mImageView.setSelected(false);
                }
                holder.mTextView.setOnClickListener(v -> change(position));
            }

            @Override
            public int getItemCount() {
                return practiceTotalCount;
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mPopupWindow.showAsDropDown(mViewLine, 0 , 2);
            mPopupWindow.setOnDismissListener(() -> {
                mCurrentShowItems = false;
                mSelectPractice.setSelected(mCurrentShowItems);
                LogUtils.d("mCurrentShowItems = " + mCurrentShowItems + "    " + mPopupWindow.isShowing());
                leaveScribbleMode(true, false);
            });
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.item_spinner_text);
            mImageView = itemView.findViewById(R.id.item_bg);
        }
    }

    public void dismissPopupWindow () {
        if (mPopupWindow != null) mPopupWindow.dismiss();
        mPopupWindow = null;
    }

}
