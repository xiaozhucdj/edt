package com.yougy.task.fragment;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import com.frank.etude.pageable.PageBtnBarAdapterV2;
import com.frank.etude.pageable.PageBtnBarV2;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.task.activity.MaterialActivity2;
import com.yougy.task.activity.TaskDetailStudentActivity;
import com.yougy.task.bean.StageTaskBean;
import com.yougy.ui.activity.R;
import com.yougy.view.NoteBookView2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MaterialsBaseFragment extends TaskBaseFragment {

    @BindView(R.id.material_constrain_layout)
    ConstraintLayout mConstraintLayout;
    @BindView(R.id.task_material_viewStub)
    ViewStub mTaskMaterialViewStub;
    @BindView(R.id.page_recycler_view)
    RecyclerView mPageableRecyclerView;
    @BindView(R.id.pageBar_materials)
    PageBtnBarV2 mPageBtnBarV2;

    private MyRecyclerAdapter mMyRecyclerAdapter;

    private int currentPage = 0;//当前显示页
    private int PAGE_COUNT = 9;
    private int mTotalMaterials = 0;//总共的资料数
    private List<StageTaskBean> currentDatas = new ArrayList<>();


    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        LogUtils.d("TaskTest onEventMainThread :" + event.getType());
        String type = event.getType();
        if (type.equals(TaskDetailStudentActivity.EVENT_TYPE_LOAD_DATA)) {
            mIsServerFail = false;
            loadData();
        } else if (type.equals(TaskDetailStudentActivity.EVENT_TYPE_LOAD_DATA_FAIL)){
            mIsServerFail = true;
//            mServerFailMsg = (String) event.getExtraData();
            mServerFailMsg = "服务器请求失败！";
            loadData();
        }
    }

    @Override
    protected View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_task_materials, container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);
        initRecyclerView();
        initPageBar ();
        return mRootView;
    }

    private void initRecyclerView (){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3, LinearLayoutManager.VERTICAL, false){
            @Override
            public boolean canScrollVertically() {
                return false;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        mPageableRecyclerView.setLayoutManager(gridLayoutManager);
        mPageableRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = 45;
                outRect.right = 45;
                if (mTaskDetailStudentActivity.isBottomBtnShow()) {
                    outRect.bottom = 81;
                } else {
                    outRect.bottom = 73;
                }
            }
        });
        mMyRecyclerAdapter = new MyRecyclerAdapter();
        mPageableRecyclerView.setAdapter(mMyRecyclerAdapter);

        if (!mTaskDetailStudentActivity.isBottomBtnShow()) {
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(mConstraintLayout);
            constraintSet.setMargin(R.id.pageBar_materials, ConstraintSet.TOP, 53);
            constraintSet.applyTo(mConstraintLayout);
        }
    }

    private void initPageBar () {
        mPageBtnBarV2.setPageBarAdapter(new PageBtnBarAdapterV2(mContext) {
            @Override
            public int getPageBtnCount() {
                return (mTotalMaterials  - 1) / PAGE_COUNT  + 1;
            }

            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn, int lastSelectPageBtnIndex) {
                LogUtils.d("btnIndex = " + btnIndex);
                calculateCurrentLists (btnIndex);
                mMyRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNoPageToShow() {

            }
        });
    }

    @Override
    protected void initNoteView(NoteBookView2 noteBookView2) {

    }

    @Override
    public void loadData() {
        super.loadData();
        Log.i(TAG, "loadData: ");
        if (mIsServerFail) {
            handlerRequestFail();
        } else {
            mStageTaskBeans.clear();
            mStageTaskBeans.addAll(mTaskDetailStudentActivity.getStageTaskBeans());
            calculateCurrentLists(0);
            handlerRequestSuccess();
        }
    }

    @Override
    protected void handlerRequestSuccess() {
        super.handlerRequestSuccess();
        if (mStageTaskBeans.size() == 0) {
            showDataEmpty(View.VISIBLE);
        } else {
            showDataEmpty(View.GONE);
            mMyRecyclerAdapter.notifyDataSetChanged();
            mPageBtnBarV2.selectPageBtn(0, false);
        }
    }

    @Override
    protected void handlerRequestFail() {
        super.handlerRequestFail();
        showDataEmpty(View.VISIBLE);
    }

    @Override
    protected void showDataEmpty(int visibility) {
        super.showDataEmpty(visibility);
        String textStr = mContext.getString(R.string.str_task_materials_empty);
        if (mTaskDetailStudentActivity.isLoading) return;
        if (mIsServerFail) {
            if (!TextUtils.isEmpty(mServerFailMsg))
                textStr = mServerFailMsg;
        }
        if (mDataEmptyView == null) {
            mDataEmptyView = mTaskMaterialViewStub.inflate();
        }
        TextView text = mDataEmptyView.findViewById(R.id.text_empty);
        text.setText(textStr);
        mDataEmptyView.setVisibility(visibility);
    }

    @Override
    protected void unInit() {
        super.unInit();
        if (mUnbinder != null) mUnbinder.unbind();
    }



    public class MyRecyclerAdapter extends RecyclerView.Adapter<ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_task_materials, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.mTextView.setText(currentDatas.get(position).getStageContent().get(0).getAtchName());
            holder.itemView.setOnClickListener(v -> handlerClickItem(position));
        }

        @Override
        public int getItemCount() {
            LogUtils.d("getItemCount size = " + currentDatas.size());
            return currentDatas.size();
        }

    }

    private Unbinder mUnbinder;
    public class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        @BindView(R.id.item_text_materials)
        TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mUnbinder = ButterKnife.bind(this, itemView);
        }
    }


    public static StageTaskBean sStageTaskBean;
    /**
     * recyclerView Item Click
     * @param position
     */
    private void handlerClickItem (int position) {
        sStageTaskBean = currentDatas.get(position);
        boolean flag = true;
        if (sStageTaskBean.getStageContent().size() > 0 ) {
            String stageFormat = sStageTaskBean.getStageContent().get(0).getFormat();
            if (stageFormat.contains("pdf") || stageFormat.contains("PDF")  || stageFormat.contains("PNG")
                    || stageFormat.contains("png") || stageFormat.contains("jpg")
                    || stageFormat.contains("JPG") || stageFormat.contains("bmp") || stageFormat.contains("BMP")
                    || stageFormat.contains("jpeg") || stageFormat.contains("JPEG")||stageFormat.contains("txt")) {
                flag = false;
            }
        }
        if (flag) {
            ToastUtil.showCustomToast(mContext, "资料格式不支持！");
            return;
        }
        Intent intent = new Intent(mTaskDetailStudentActivity, MaterialActivity2.class);
        intent.putExtra("isHadComplete", mTaskDetailStudentActivity.isHadCommit());
        intent.putExtra("mTaskId", mTaskDetailStudentActivity.mTaskId);
        intent.putExtra("mCurrentPosition", position );
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mTaskDetailStudentActivity.startActivity(intent);
    }

    private void calculateCurrentLists (int page) {
        currentPage = page;
        int size = mStageTaskBeans.size();
        if (size > PAGE_COUNT) {
            if ((currentPage + 1) * PAGE_COUNT > size) {
                size =  size - currentPage  * PAGE_COUNT;
            } else {
                size =  PAGE_COUNT;
            }
        }
        currentDatas.clear();
        LogUtils.d("calculateCurrentLists stage size = " + mStageTaskBeans.size() + "  currentPage = " + currentPage);
        currentDatas.addAll(mStageTaskBeans.subList(currentPage * PAGE_COUNT , currentPage * PAGE_COUNT + size));
        LogUtils.d("calculateCurrentLists currentDatas size = " + currentDatas.size());
    }

}
