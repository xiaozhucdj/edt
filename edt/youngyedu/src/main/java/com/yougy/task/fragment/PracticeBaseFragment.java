package com.yougy.task.fragment;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frank.etude.pageable.PageBtnBarAdapterV2;
import com.frank.etude.pageable.PageBtnBarV2;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.RefreshUtil;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.task.ContentDisPlayer;
import com.yougy.task.ContentDisPlayerAdapter;
import com.yougy.task.LoadAnswer;
import com.yougy.task.activity.SaveNoteUtils;
import com.yougy.task.activity.TaskDetailStudentActivity;
import com.yougy.task.bean.StageTaskBean;
import com.yougy.ui.activity.R;
import com.yougy.view.ContentPdfImageView;
import com.yougy.view.CustomGridLayoutManager;
import com.yougy.view.CustomItemDecoration;
import com.yougy.view.CustomLinearLayoutManager;
import com.yougy.view.NoteBookView2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.lang.String.format;

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
    ContentDisPlayer mContentDisplayer;
    @BindView(R.id.select_practice)
    TextView mSelectPractice;
    @BindView(R.id.image_bg_practice)
    ImageView mImageBg;
    @BindView(R.id.layout_caoGao)
    LinearLayout mLayoutCaoGao;
    @BindView(R.id.text_practice_caoGao)
    TextView mTextCaoGao;
    @BindView(R.id.text_practice_clear)
    TextView mTextClear;
    @BindView(R.id.practice_noteView)
    NoteBookView2 mNoteBookView;
    @BindView(R.id.noteView_caoGao)
    NoteBookView2 mCaoGaoNoteView;
    @BindView(R.id.view_line2)
    View mViewLine2;
    @BindView(R.id.img_add_page)
    ImageView mImageAddPage;
    @BindView(R.id.task_practice_pageBar2)
    PageBtnBarV2 mPageBtnBarV2;
    @BindView(R.id.select_recycler_view)
    RecyclerView mSelectRecyclerView;
    @BindView(R.id.layout_select_question)
    LinearLayout layoutSelect;


//    private PopupWindow mPopupWindow;

    public static final String CACHE_KEY = "_task_practice_cache_";
    public static final String BITMAP_KEY = "_task_practice_bitmap_";

    private int prevSavePosition = 0;
    private int currentSelectPosition = 0;
    /*总共的练习数量*/
    private int practiceTotalCount = 0;
    /*单个练习有几页*/
    private int singlePracticePage = 1;
    /*添加页数*/
    private int mAddPageCaoGao = 0;
    /*练习页数加上添加的页数*/
//    private int mPageCount = 0;
    private int prevPage = 0;
    private int currentPage = 0;

    public static final String TAG_KEY = "taskPractice";
    /*选择题目选项是否显示*/
    private boolean mCurrentShowItems = false;
    /*草稿纸是否显示*/
    private boolean isCaoGaoShow  = false;

    private LoadAnswer mLoadAnswer;


    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        LogUtils.d("TaskTest onEventMainThread :" + event.getType());
        String type = event.getType();
        if (type.equals(TaskDetailStudentActivity.EVENT_TYPE_LOAD_DATA)) {
            mIsServerFail = false;
            mStageTaskBeans.clear();
            mStageTaskBeans.addAll(mTaskDetailStudentActivity.getStageTaskBeans());
            loadData();
        } else if (type.equals(TaskDetailStudentActivity.EVENT_TYPE_LOAD_DATA_FAIL)){
            mIsServerFail = true;
//            mServerFailMsg = (String) event.getExtraData();
            mServerFailMsg = "服务器请求失败！";
            loadData();
        } else if (type.equals(TaskDetailStudentActivity.EVENT_TYPE_SCRIBBLE_MODE)) {
            if (mNoteBookView2 != null && !mTaskDetailStudentActivity.isHadCommit()){
                boolean scribbleMode = (boolean) event.getExtraData();
                leaveScribbleMode(scribbleMode, false);
            }
        } else if (event.getType().equals(TaskDetailStudentActivity.EVENT_TYPE_COMMIT_STATE)) {
            boolean extraData = (boolean) event.getExtraData();
            if (extraData) {
                mImageAddPage.setVisibility(View.GONE);
                mTextCaoGao.setVisibility(View.GONE);
                ((LinearLayoutManager) mTaskPracticePageBar.getLayoutManager()).scrollToPosition(0);
            }
        }
    }


    @Override
    protected void init() {
        super.init();
        mLoadAnswer = new LoadAnswer(mTaskDetailStudentActivity);
    }

    @Override
    protected View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d("TaskTest initView :");
        mRootView = inflater.inflate(R.layout.fragment_task_practice, container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);
        initContentDisPlayer();
        initPageBar();
        initNoteView(mNoteBookView);
        initSelectRecycView();
        return mRootView;
    }

    @Override
    protected void initNoteView(NoteBookView2 noteBookView2) {
        super.initNoteView(noteBookView2);
        mNoteBookView.setIntercept(true);
    }

    @Override
    public void loadData() {
        super.loadData();
        dismissPopupWindow();
        LogUtils.d("TaskTest Practice loadData.");
        if (checkCurrentPosition(currentSelectPosition, mStageTaskBeans)){
            handlerRequestSuccess();
        } else {
            if (mIsServerFail) {
                handlerRequestFail();
            } else {
                handlerRequestSuccess();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTaskDetailStudentActivity.isHadCommit()) {
            mImageAddPage.setVisibility(View.GONE);
            mTextCaoGao.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.d("Practice  onPause...");
        showOrHideCaoGaoLayout(false, false);

        if (isCommited()) return; //未提交，保存
        saveCurrentPractice();
        leaveScribbleMode(false, true);
    }

    @Override
    protected void handlerRequestSuccess() {
        super.handlerRequestSuccess();
        LogUtils.d("TaskTest handlerRequestSuccess...");
        if (mStageTaskBeans.size() == 0) {
            showDataEmpty(View.VISIBLE);
        } else {
            showDataEmpty(View.GONE);
            practiceTotalCount = mStageTaskBeans.size();
//        if (practiceTotalCount > 0 ) {
//            List<StageTaskBean.StageContent> stageContents = new ArrayList<>();
////            stageContents.add(new StageTaskBean.StageContent("http://pre-global-questions.oss-cn-beijing.aliyuncs.com/2018/107020002/2bef61b7-6bae-443d-ac00-890713ecb723/abc.pdf", 1000, "PDF", null,
////                        "http://pre-global-questions.oss-cn-beijing.aliyuncs.com/2018/107020002/2bef61b7-6bae-443d-ac00-890713ecb723/abc.pdf", null, 0.1f));
//            stageContents.add(new StageTaskBean.StageContent("http://pre-global-questions.oss-cn-beijing.aliyuncs.com/2018/107020002/ae6a8ad6-529d-450f-909e-5e840ae13be6/sqs.pdf", 1000, "PDF", null,
//                        "http://pre-global-questions.oss-cn-beijing.aliyuncs.com/2018/107020002/ae6a8ad6-529d-450f-909e-5e840ae13be6/sqs.pdf", null, 0.1f));
//            StageTaskBean stageTaskBean = new StageTaskBean(0, null, 0, 0,
//                    0, stageContents, null, null, null);
//            mStageTaskBeans.add(stageTaskBean);
//            practiceTotalCount ++;
//        }
            change(currentSelectPosition);
            if (!isCommited()) UIUtils.postDelayed(() -> PracticeBaseFragment.this.leaveScribbleMode(true, false), 500);
        }
    }

    @Override
    protected void handlerRequestFail() {
        super.handlerRequestFail();
        LogUtils.d("TaskTest handlerRequestFail...");
        showDataEmpty(View.VISIBLE);
    }

    @Override
    protected void showDataEmpty(int visibility) {
        super.showDataEmpty(visibility);
        String textStr = mContext.getString(R.string.str_task_practice_empty);
        if (mIsServerFail) {
            if (!TextUtils.isEmpty(mServerFailMsg))
                textStr = mServerFailMsg;
        }
        if (mDataEmptyView == null) {
            mDataEmptyView = mTaskPracticeViewStub.inflate();
        }
        TextView text = mDataEmptyView.findViewById(R.id.text_empty);
        text.setText(textStr);
        mDataEmptyView.setVisibility(visibility);
    }

    @Override
    protected void unInit() {
        super.unInit();
    }


    private void initPageBar() {
        mTaskPageAdapter = new TaskPageAdapter();
        mTaskPracticePageBar.setAdapter(mTaskPageAdapter);

        // 如果Bottom  Button  没显示  PageBarV2;   显示 recyclerView, 布局改变影响
        if (!judgeBottomBtnShow()) return;

        CustomLinearLayoutManager customLinearLayoutManager = new CustomLinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        customLinearLayoutManager.setScrollHorizontalEnabled(false);
        customLinearLayoutManager.setScrollVerticalEnabled(false);
        mTaskPracticePageBar.setLayoutManager(customLinearLayoutManager);

        CustomItemDecoration.Builder builder = new CustomItemDecoration.Builder();
        CustomItemDecoration itemDecoration = builder.isOffsets(true).setItemMargin(10, 0, 10, 0).build();
        mTaskPracticePageBar.addItemDecoration(itemDecoration);
    }

    /**
     *  底部Button是否显示 布局调整
     */
    private boolean judgeBottomBtnShow () {
        if (isBottomBtnShow()) return true;
        //下面Button未显示时  布局调整
        mImageAddPage.setVisibility(View.GONE);
        mTextCaoGao.setVisibility(View.GONE);
        mTextClear.setVisibility(View.GONE);
        mTaskPracticePageBar.setVisibility(View.GONE);
        mPageBtnBarV2.setVisibility(View.VISIBLE);
        mPageBtnBarV2.setPageBarAdapter(new PageBtnBarAdapterV2(mContext) {
            @Override
            public int getPageBtnCount() {
                return singlePracticePage + mAddPageCaoGao;
            }

            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn, int lastSelectPageBtnIndex) {
                dismissPopupWindow();
                currentPage = btnIndex;
                mNoteBookView2.clearAll();
                mContentDisplayer.toPage(TAG_KEY, btnIndex, false, mStatusChangeListener);
            }

            @Override
            public void onNoPageToShow() {

            }
        });
        mPageBtnBarV2.selectPageBtn(currentPage, false);

        setLayoutParams(mImageBg, 908, 944);
        return false;
    }

    /**
     * recyclerView  Page Adapter
     */
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
            return singlePracticePage + mAddPageCaoGao;
        }

    }

    /**
     *  保存上一页轨迹
     */
    private void clickPageIndex () {
        String[] cacheBitmapKey = getCacheBitmapKey(prevSavePosition, prevPage);
        SaveNoteUtils.getInstance(mContext).saveNoteViewData(mNoteBookView2, SaveNoteUtils.getInstance(mContext).getTaskFileDir(),
                cacheBitmapKey[0], cacheBitmapKey[1], String.valueOf(mTaskDetailStudentActivity.mTaskId),mStageTaskBeans.get(prevSavePosition).getStageId());
        LogUtils.d("TaskLog cacheKey " + cacheBitmapKey[0] + "\n bitmapKey = " + cacheBitmapKey[1]);
        leaveScribbleMode(false, true);
        showPracticeOrAddPage (currentPage);
        cacheBitmapKey = getCacheBitmapKey(prevSavePosition, currentPage);
        SaveNoteUtils.getInstance(mContext).resetNoteView(mNoteBookView2, cacheBitmapKey[0], cacheBitmapKey[1], SaveNoteUtils.getInstance(mContext).getTaskFileDir());
        leaveScribbleMode(true, false);
        prevPage = currentPage;
    }


    private void showPracticeOrAddPage (int page) {
        if (isAddPage(page)) {
            mContentDisplayer.setVisibility(View.GONE);
        } else {
            mContentDisplayer.setVisibility(View.VISIBLE);
            mContentDisplayer.toPage(TAG_KEY, page, false, mStatusChangeListener);
        }
    }

    public class PageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_practice_page_index)
        TextView mTextView;
        public PageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void initContentDisPlayer() {
        initDisplayChildScaleType(ImageView.ScaleType.FIT_XY);
        mMyContentDisPlayerAdapter = new MyContentDisPlayerAdapter();
        mContentDisplayer.setContentAdapter(mMyContentDisPlayerAdapter);
//        mContentDisplayer.setOnClickListener(v -> loadData());
    }

    private MyContentDisPlayerAdapter mMyContentDisPlayerAdapter;
    public class MyContentDisPlayerAdapter extends ContentDisPlayerAdapter {

        @Override
        public void afterPageCountChanged(String typeKey) {
            singlePracticePage = getPageCount(typeKey);
//            mPageCount = mAddPageCaoGao + singlePracticePage;
            if (isBottomBtnShow()) setRecyclerViewWidth();
            else mPageBtnBarV2.refreshPageBar();
        }
    }


    private ContentDisPlayer.StatusChangeListener mStatusChangeListener = (newStatus, typeKey, url, errorType, errorMsg) -> {
        if (mContentDisplayer == null) return;
        switch (newStatus) {
            case DOWNLOADING:
            case LOADING:
                isLoadSuccess = false;
                mContentDisplayer.setHintText("加载中...");
                break;
            case ERROR:
                isLoadSuccess = false;
                mContentDisplayer.setHintText("错误：" + errorMsg);
                break;
            case SUCCESS:
                LogUtils.i("TaskTest state changed success.");
                isLoadSuccess = true;
                mContentDisplayer.setHintText(null);//隐藏
                mTaskPageAdapter.notifyDataSetChanged();
                //"http://" + bucket + AliyunUtil.ANSWER_PIC_HOST + answerContentTreeMap.get("remote");
                if (mTaskDetailStudentActivity.isHadCommit()) {
                    StageTaskBean stageTaskBean = mStageTaskBeans.get(currentSelectPosition);
                    LogUtils.i("TaskTest state stageTaskBean = " + stageTaskBean.toString());
                    mLoadAnswer.loadAnswer(mNoteBookView, stageTaskBean, currentSelectPosition, currentPage);
                } else {
                    String[] cacheBitmapKey = getCacheBitmapKey(currentSelectPosition, currentPage);
                    SaveNoteUtils.getInstance(mContext).resetNoteView(mNoteBookView2,cacheBitmapKey[0], cacheBitmapKey[1], SaveNoteUtils.getInstance(mContext).getTaskFileDir());
                }
                prevSavePosition = currentSelectPosition;
                prevPage = currentPage;

                leaveScribbleMode(true, false);
                break;
        }
    };


    @OnClick({R.id.prev_practice, R.id.next_practice , R.id.select_practice,R.id.text_practice_caoGao, R.id.text_practice_clear
                , R.id.img_add_page, R.id.text_hide_caoGao})
    public void onClick(View view) {
        if (mCurrentShowItems && view.getId() != R.id.select_practice) showSelectItem(false);
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
                if (showTaskCommittedTips()) return;
                isCaoGaoShow = !isCaoGaoShow;
                showOrHideCaoGaoLayout(isCaoGaoShow, true);
                break;
            case R.id.text_hide_caoGao:
                showOrHideCaoGaoLayout(false, false);
                break;
            case R.id.text_practice_clear:
                clickClear ();
                break;
            case R.id.img_add_page:
                addPage();
                break;
        }
    }

    /**
     * 清空
     */
    private void clickClear () {
        if (showTaskCommittedTips()) return;
        NoteBookView2 noteBookView2 = mNoteBookView2;
        if (isCaoGaoShow) noteBookView2 = mCaoGaoNoteView;
        clear(noteBookView2);
    }

    private void clear (NoteBookView2 noteBookView2){
        noteBookView2.leaveScribbleMode();
        noteBookView2.clearAll();
        noteBookView2.leaveScribbleMode(true);
    }

    private boolean showTaskCommittedTips () {
        if (isCommited()) {
            ToastUtil.showCustomToast(mContext, "任务已经提交！");
            return true;
        }
        return false;
    }

    /**
     * 练习加页
     */
    private synchronized void addPage () {
        if (mTaskDetailStudentActivity.isHadCommit()){
            ToastUtil.showCustomToast(mContext, "任务已经提交！");
            return;
        }
        if (mAddPageCaoGao == 5) {
            ToastUtil.showCustomToast(mContext, "最多添加5页！");
            return;
        }
        prevPage = currentPage;
        currentPage = mAddPageCaoGao + singlePracticePage;
        mAddPageCaoGao ++;
        SpUtils.putTaskPracticeAddPages(mTaskDetailStudentActivity.mTaskId + "_" + mStageTaskBeans.get(currentSelectPosition).getStageId()
                + "_" + currentSelectPosition, mAddPageCaoGao);
//        pathLists.add(null);
        clickPageIndex();
        autoScrollRecyclerView ((LinearLayoutManager) mTaskPracticePageBar.getLayoutManager(), true);
        setRecyclerViewWidth();
        mTaskPageAdapter.notifyDataSetChanged();
    }

    /**
     * 设置未完成时Page RecyclerView的显示宽度
     */
    private void setRecyclerViewWidth (){
        if (mTaskPracticePageBar == null) {
            LogUtils.w("mTaskPracticePageBar is null.");
            return;
        }
        ViewGroup.LayoutParams layoutParams = mTaskPracticePageBar.getLayoutParams();
        if (singlePracticePage + mAddPageCaoGao > 5) {
            layoutParams.width = 300;
        } else {
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mTaskPracticePageBar.setLayoutParams(layoutParams);
    }

    /*自动滚动PageBar  点击最左或者最右如果还有page，滚动当前到中间*/
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

    /**
     * 显示隐藏草稿纸
     */
    private void showOrHideCaoGaoLayout (boolean isVisibility, boolean isClearCaoGao) {
        if (mTaskDetailStudentActivity.isHadCommit()) return;
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
        if (!checkPrevNextPosition(isNext, currentSelectPosition)) {
            return;
        }
        change(currentSelectPosition);
    }

    private boolean checkPrevNextPosition (boolean isNext, int position) {
        boolean tempFlag = false;
        if (isNext) {
            if (position < practiceTotalCount - 1) {
                position++;
            } else {
                if (TaskDetailStudentActivity.isHandPaintedPattern){
                    leaveScribbleMode(false,false);
                    tempFlag = true;
                }
                ToastUtil.showCustomToast(mContext, "已经是最后一个练习了！");
                View view = ToastUtil.mToast.getView();
                boolean finalTempFlag = tempFlag;
                view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {

                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                        if (finalTempFlag)
                            leaveScribbleMode(true,false);
                    }
                });
                return false;
            }
        } else {
            if (position > 0) {
                position--;
            } else {
                if (TaskDetailStudentActivity.isHandPaintedPattern){
                    leaveScribbleMode(false,false);
                    tempFlag = true;
                }
                ToastUtil.showCustomToast(mContext, "已经是第一个练习了！");
                if (tempFlag)
                    leaveScribbleMode(true,false);
                return false;
            }
        }
        currentSelectPosition = position;
        return true;
    }

    /**
     * 切换到指定的位置
     * @param position
     */
    private synchronized void change(int position) {
        dismissPopupWindow();
        currentPage = 0;
//        mAddPageCaoGao = 0;
        currentSelectPosition = position;
        showOrHideCaoGaoLayout(false, false);
        if (checkCurrentPosition(currentSelectPosition, mStageTaskBeans)){
            setCurrentPracticeInfo();
        }
    }

    /**
     * 题目切换，加载对应的练习和笔记
     */
    private void setCurrentPracticeInfo() {
        if (!isCommited() && prevSavePosition != currentSelectPosition) {
            String[] cacheBitmapKey = getCacheBitmapKey(prevSavePosition, prevPage);
            SaveNoteUtils.getInstance(mContext).saveNoteViewData(mNoteBookView2, SaveNoteUtils.getInstance(mContext).getTaskFileDir(),
                    cacheBitmapKey[0],cacheBitmapKey[1], String.valueOf(mTaskDetailStudentActivity.mTaskId), mStageTaskBeans.get(prevSavePosition).getStageId());
        }
        loadPracticeQuestion ();
    }


    public void saveCurrentPractice () {
        String[] cacheBitmapKey = getCacheBitmapKey(currentSelectPosition, currentPage);
        if (mStageTaskBeans.size() <= 0) return;
        SaveNoteUtils.getInstance(mContext).saveNoteViewData(mNoteBookView2, SaveNoteUtils.getInstance(mContext).getTaskFileDir(),
                cacheBitmapKey[0],cacheBitmapKey[1], String.valueOf(mTaskDetailStudentActivity.mTaskId), mStageTaskBeans.get(currentSelectPosition).getStageId());
    }

    private void loadPracticeQuestion () {
        //"http://" + bucket + AliyunUtil.ANSWER_PIC_HOST + answerContentTreeMap.get("remote");
        LogUtils.d("isHandPaintedPattern = " + TaskDetailStudentActivity.isHandPaintedPattern);
        leaveScribbleMode(false, true);
        mSelectPractice.setText(format(mContext.getString(R.string.str_select_practice) + "(" + (currentSelectPosition + 1) + "/" + practiceTotalCount + ")" ));
        if (isCommited()) {
            mAddPageCaoGao = mLoadAnswer.getCurrentPositionPages(mStageTaskBeans.get(currentSelectPosition)) - singlePracticePage;
            if (mAddPageCaoGao < 0 ) mAddPageCaoGao = 0;
        } else {
            mAddPageCaoGao = SpUtils.getTaskPracticeAddPages(mTaskDetailStudentActivity.mTaskId + "_" + mStageTaskBeans.get(currentSelectPosition).getStageId()
                    + "_" + currentSelectPosition);
        }
        mContentDisplayer.getContentAdapter().deleteDataList(TAG_KEY);
        mContentDisplayer.getContentAdapter().updateDataList(TAG_KEY, mStageTaskBeans.get(currentSelectPosition).getStageContent().get(0)
                        .getValue(), mStageTaskBeans.get(currentSelectPosition).getStageContent().get(0).getFormat() );
        mContentDisplayer.setVisibility(View.VISIBLE);
        mContentDisplayer.toPage(TAG_KEY, currentPage, false, mStatusChangeListener);
        mTaskPageAdapter.notifyDataSetChanged();
    }

    /**
     * 是否显示选择练习选项
     * @param isShow
     */
    private void showSelectItem (boolean isShow) {
        mSelectPractice.setSelected(isShow);
        if (isShow) {
            showPopupWindow();
        } else {
            dismissPopupWindow();
        }
        mCurrentShowItems = isShow;
        LogUtils.i("show select item popup window : " + isShow + "   mCurrentShowItems = " + mCurrentShowItems);
    }


    private void initSelectRecycView () {
        CustomGridLayoutManager customGridLayoutManager = new  CustomGridLayoutManager(mContext, 8);
        customGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        if (practiceTotalCount < 104) customGridLayoutManager.setScrollEnabled(false);
        mSelectRecyclerView.setLayoutManager(customGridLayoutManager);

        mSelectRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int childAdapterPosition = parent.getChildAdapterPosition(view);
                int index = childAdapterPosition % 8;
//                if (index == 0 || index == 2 || index == 5 || index == 7) {
//                    outRect.left = 31;
//                    outRect.right = 30;
//                } else if (index == 1 || index == 3) {
//                    outRect.left = 30;
//                    outRect.right = 30;
//                } else {
//                    outRect.left = 30;
//                    outRect.right = 31;
//                }
                outRect.left = 30;
                outRect.right = 30;
                outRect.top = 10;
                outRect.bottom = 10;
            }
        });


        mSelectRecyclerView.setAdapter(new RecyclerView.Adapter<ViewHolder>() {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_spinner_select_practice, null, false);
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                holder.mTextView.setText(String.valueOf(position + 1));
                setSelectState (holder, position);
                holder.mTextView.setOnClickListener(v -> change(position));
            }

            @Override
            public int getItemCount() {
                return practiceTotalCount;
            }

            private void setSelectState (ViewHolder holder, int position) {
                if (currentSelectPosition == position) {
                    holder.mTextView.setSelected(true);
                    holder.mImageView.setSelected(true);
                } else {
                    holder.mTextView.setSelected(false);
                    holder.mImageView.setSelected(false);
                }
            }
        });
    }

    /**
     *  显示选择练习选项
     */
    private void showPopupWindow () {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.select_practice_popupwindow, null);
//        mPopupWindow = new PopupWindow(mContext);
//        mPopupWindow.setWidth(960);
//        mPopupWindow.setContentView(view);
//        mPopupWindow.setOutsideTouchable(false);
//        mPopupWindow.setTouchable(true);

//        RecyclerView recyclerView = view.findViewById(R.id.select_recycler_view);


//        mSelectRecyclerView.setVisibility(View.VISIBLE);
        mSelectRecyclerView.getAdapter().notifyDataSetChanged();
        layoutSelect.setVisibility(View.VISIBLE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            mPopupWindow.showAsDropDown(mViewLine, 0 , 2);
//            mPopupWindow.setOnDismissListener(() -> {
//                mCurrentShowItems = false;
//                mSelectPractice.setSelected(mCurrentShowItems);
//                LogUtils.d("TaskLog mCurrentShowItems = " + mCurrentShowItems + "   isShowing: " + mPopupWindow.isShowing());
//                leaveScribbleMode(true, false);
//            });
//        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_bg)
        public ImageView mImageView;
        @BindView(R.id.item_spinner_text)
        public TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void dismissPopupWindow () {
//        if (mPopupWindow != null) mPopupWindow.dismiss();
//        mPopupWindow = null;
        if (mCurrentShowItems)
            leaveScribbleMode(true, false);
        layoutSelect.setVisibility(View.GONE);
        mCurrentShowItems = false;
        mSelectPractice.setSelected(mCurrentShowItems);

    }


    private String[] getCacheBitmapKey (int position, int page) {
        if(checkCurrentPosition(position, mStageTaskBeans)){
            int currentId = mStageTaskBeans.get(position).getStageId();
            int taskId = mStageTaskBeans.get(position).getStageAttach();
            String cacheKey = taskId + "_" + currentId + CACHE_KEY + position + "_" + page;
            String bitmapKey = taskId + "_" + currentId + BITMAP_KEY + position + "_" + page;
            return new String[]{cacheKey, bitmapKey};
        }
        LogUtils.w("TaskTest cacheKey is Null, position IndexOfArray Exception.");
        return new String[]{"errorPosition0", "errorPosition1"};
    }


    /**
     *  是否是加页
     * @param page
     * @return
     */
    private boolean isAddPage (int page) {
        return page >= singlePracticePage;
    }

    /**
     * 设置ContentDisPlayer 子ViewScaleType 充满
     */
    private void initDisplayChildScaleType (ImageView.ScaleType scaleType){
        int childCount = mContentDisplayer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (mContentDisplayer.getChildAt(i) instanceof  ImageView) {
                ((ImageView)mContentDisplayer.getChildAt(i)).setScaleType(scaleType);
            }
            if (mContentDisplayer.getChildAt(i) instanceof ContentPdfImageView) {
                ((ContentPdfImageView)mContentDisplayer.getChildAt(i)).setScaleType(scaleType);
            }
        }
    }

}
