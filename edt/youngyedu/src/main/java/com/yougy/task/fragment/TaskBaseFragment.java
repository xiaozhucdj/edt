package com.yougy.task.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.fragment.BFragment;
import com.yougy.common.utils.LogUtils;
import com.yougy.task.activity.TaskDetailStudentActivity;
import com.yougy.view.NoteBookView2;

import java.util.ArrayList;

import butterknife.Unbinder;

public class TaskBaseFragment extends BFragment {

    public static final String TAG = "TaskBaseFragment";

    protected TaskDetailStudentActivity mTaskDetailStudentActivity;
    protected Context mContext;
    protected View mRootView;
    protected Unbinder mUnbinder;

    public NoteBookView2 mNoteBookView2;
    protected ArrayList<String> pathLists = new ArrayList<>();//path  用'#'连接多页
    protected String mCurrentCacheName = ""; //当前路径是否有本地轨迹文件，判断。
    protected boolean isMultiPage = false;

    protected boolean isHadCommit = false;

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        LogUtils.d("TaskTest onEventMainThread :" + event.getType());
        String type = event.getType();
        if (type.equals(TaskDetailStudentActivity.EVENT_TYPE_LOAD_DATA)) {
            loadData();
        } else if (type.equals(TaskDetailStudentActivity.EVENT_TYPE_SCRIBBLE_MODE)) {
            if (mNoteBookView2 != null && !isHadCommit){
                boolean scribbleMode = (boolean) event.getExtraData();
                leaveScribbleMode(scribbleMode, false);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TaskDetailStudentActivity) mTaskDetailStudentActivity = (TaskDetailStudentActivity) context;
        isHadCommit = mTaskDetailStudentActivity.isHadCommit();
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

    public void leaveScribbleMode (boolean isPen, boolean isClear) {
        if (isHadCommit) return;
        if (mNoteBookView2 == null) return;
        if (isClear) mNoteBookView2.clearAll();
        LogUtils.d("TaskTest isPen = " + isPen +  "   isClear = " + isClear + "  isHandPaintedPattern = " + TaskDetailStudentActivity.isHandPaintedPattern);
        if (TaskDetailStudentActivity.isHandPaintedPattern == isPen) return;
        TaskDetailStudentActivity.isHandPaintedPattern = isPen;
        if (isPen) mNoteBookView2.leaveScribbleMode(isPen);
        else mNoteBookView2.leaveScribbleMode();
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
        if (mNoteBookView2 != null) {
            mNoteBookView2.setIntercept(true);
            mNoteBookView2.setVisibility(View.GONE);
            mNoteBookView2.leaveScribbleMode(false);
        }
    }

    protected View mDataEmptyView;
    protected void showDataEmpty (int visibility) {

    }

}
