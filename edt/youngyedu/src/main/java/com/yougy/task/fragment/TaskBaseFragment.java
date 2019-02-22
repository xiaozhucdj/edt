package com.yougy.task.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.yougy.common.fragment.BFragment;
import com.yougy.common.utils.LogUtils;
import com.yougy.task.activity.TaskDetailStudentActivity;
import com.yougy.task.bean.StageTaskBean;
import com.yougy.view.NoteBookView2;

import java.util.ArrayList;
import java.util.List;

import butterknife.Unbinder;

public class TaskBaseFragment extends BFragment {

    public static final String TAG = "TaskBaseFragment";

    protected TaskDetailStudentActivity mTaskDetailStudentActivity;
    protected Context mContext;
    protected View mRootView;
    protected Unbinder mUnbinder;

    public NoteBookView2 mNoteBookView2;

//    public ArrayList<String> getPathLists() {
//        return pathLists;
//    }
//    protected ArrayList<String> pathLists = new ArrayList<>();//path  用'#'连接多页
//    protected String mCurrentCacheName = ""; //当前路径是否有本地轨迹文件，判断。
//    protected boolean isMultiPage = false;

    protected List<StageTaskBean> mStageTaskBeans = new ArrayList<>();

//    protected List<StageTaskBean.StageScene> mStageScenes = new ArrayList<>();
//    protected List<StageTaskBean.StageScene.SceneContent> mSceneContents = new ArrayList<>();

    protected boolean mIsServerFail = true;//服务器请求失败
    protected String mServerFailMsg = "加载中...";//失败信息

    public void setServerFail (boolean isServerFail) {
        mIsServerFail = isServerFail;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TaskDetailStudentActivity) mTaskDetailStudentActivity = (TaskDetailStudentActivity) context;
        mContext = context;
        init();
        Log.i(TAG, "onAttach: ");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        return initView(inflater, container, savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
        Log.i(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) mUnbinder.unbind();
        unInit();
        Log.i(TAG, "onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }

    protected void init (){

    }

    protected View initView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return mRootView;
    }

    protected void initNoteView (NoteBookView2 noteBookView2){
        mNoteBookView2 = noteBookView2;
    }

    protected boolean isLoadSuccess = false;
    public void leaveScribbleMode (boolean isPen, boolean isClear) {
        if (!isLoadSuccess) return;
        if (isClear) mNoteBookView2.clearAll();
        if (isCommited()) return;
        if (mNoteBookView2 == null) return;
        LogUtils.d("TaskTest isPen = " + isPen +  "   isClear = " + isClear + "  isHandPaintedPattern = " + TaskDetailStudentActivity.isHandPaintedPattern);
        if (TaskDetailStudentActivity.isHandPaintedPattern == isPen) return;
        TaskDetailStudentActivity.isHandPaintedPattern = isPen;
        if (isPen) {
            mNoteBookView2.leaveScribbleMode(isPen);
        } else {
            mNoteBookView2.leaveScribbleMode();
        }
        TaskDetailStudentActivity.isIntercept = isPen;
        if (mNoteBookView2 != null) mNoteBookView2.setIntercept(!isPen);
    }


    public void loadData () {
        Log.i(TAG, "loadData: taskFragment");
    }

    protected void handlerRequestSuccess () {

    }

    protected void handlerRequestFail () {

    }

    protected void unInit (){
        mDataEmptyView = null;
        if (mNoteBookView2 != null) {
            mNoteBookView2.setIntercept(true);
            mNoteBookView2.setVisibility(View.GONE);
            mNoteBookView2.leaveScribbleMode(false);
        }
    }

    protected View mDataEmptyView;
    protected void showDataEmpty (int visibility) {

    }


    protected boolean checkCurrentPosition (int position, List arrayList) {
        if (position >= 0 && position < arrayList.size()) {
            return true;
        }
        return false;
    }

    protected boolean isBottomBtnShow () {
        return mTaskDetailStudentActivity.isBottomBtnShow();
    }

    protected void setLayoutParams (View view, int w, int h) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = w;
        layoutParams.height = h;
        view.setLayoutParams(layoutParams);
    }

    protected boolean isCommited (){
        return mTaskDetailStudentActivity.isHadCommit();
    }

}
