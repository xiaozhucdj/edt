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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import com.frank.etude.pageable.PageBtnBarAdapterV2;
import com.frank.etude.pageable.PageBtnBarV2;
import com.yougy.common.utils.LogUtils;
import com.yougy.task.activity.MaterialActivity;
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

    private List<String> testDatas = new ArrayList<>();
    private List<String> currentDatas = new ArrayList<>();

    private int currentPage = 0;//当前显示页
    private int PAGE_COUNT = 9;
    private int mTotalMaterials = 10 * 9 + 5;//总共的资料数

    @Override
    protected View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        for (int i = 0; i < mTotalMaterials; i++) {
            testDatas.add("Materials" + i);
        }
        calculateCurrentLists(0);
        mRootView = inflater.inflate(R.layout.fragment_task_materials, container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);
        initRecyclerView ();
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
        mPageBtnBarV2.selectPageBtn(0, false);
    }

    @Override
    protected void initNoteView(NoteBookView2 noteBookView2) {
        mCurrentCacheName = "_material_stu_";
    }

    @Override
    public void loadData() {
        super.loadData();
        Log.i(TAG, "loadData: ");
//        showDataEmpty(View.VISIBLE);
    }

    @Override
    protected void showDataEmpty(int visibility) {
        super.showDataEmpty(visibility);
        if (mDataEmptyView == null) {
            mDataEmptyView = mTaskMaterialViewStub.inflate();
            TextView text = mDataEmptyView.findViewById(R.id.text_empty);
            text.setText(mContext.getString(R.string.str_task_materials_empty));
        } else {
            mDataEmptyView.setVisibility(visibility);
        }
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
            holder.mTextView.setText(currentDatas.get(position));
            holder.itemView.setOnClickListener(v -> handlerClickItem(holder, position));
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

    /**
     * recyclerView Item Click
     * @param viewHolder
     * @param position
     */
    private void handlerClickItem (ViewHolder viewHolder, int position) {
        Toast.makeText(mContext, "click position = " + position, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(mContext, MaterialActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void calculateCurrentLists (int page) {
        currentPage = page;
        int size = testDatas.size();
        if (size > PAGE_COUNT) {
            if ((currentPage + 1) * PAGE_COUNT > size) {
                size =  size - currentPage  * PAGE_COUNT;
            } else {
                size =  PAGE_COUNT;
            }
        }
        currentDatas.clear();
        currentDatas.addAll(testDatas.subList(currentPage * PAGE_COUNT , currentPage * PAGE_COUNT + size));
    }

}
