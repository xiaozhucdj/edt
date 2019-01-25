package com.yougy.task.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.task.activity.SaveNoteUtils;
import com.yougy.task.bean.StageTaskBean;
import com.yougy.ui.activity.R;
import com.yougy.view.NoteBookView2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

public class SignatureFragment extends TaskBaseFragment {

    @BindView(R.id.signature_noteView)
    NoteBookView2 mNoteBookViewSignature;


    private int stageId;

    @Override
    protected View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.activity_signature,container , false);
        mUnbinder = ButterKnife.bind(this, mRootView);
        mNoteBookViewSignature.setIntercept(true);
        return super.initView(inflater, container, savedInstanceState);
    }

    @Override
    protected void init() {

    }

    @Override
    public void loadData() {
        super.loadData();
        NetWorkManager.queryStageTask(String.valueOf(mTaskDetailStudentActivity.dramaId), "SR04").subscribe(stageTaskBeans -> {
            Log.i(TAG, "call: " + stageTaskBeans.size());
            if (stageTaskBeans.size() > 0) {
                stageId = stageTaskBeans.get(0).getStageId();
            }
        }, throwable -> LogUtils.e("TaskTest sign error :" + throwable.getMessage()));
    }

    @OnClick({R.id.btn_submit, R.id.btn_cancel})
    public void onClick (View view){
        switch (view.getId()) {
            case R.id.btn_submit:
                saveSignatureBitmap();
                mTaskDetailStudentActivity.signatureSubmit();
                break;
            case R.id.btn_cancel:
                mTaskDetailStudentActivity.signatureCancel();
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        UIUtils.postDelayed(() -> {
            mNoteBookViewSignature.leaveScribbleMode(true);
            mNoteBookViewSignature.setIntercept(false);
        }, 10);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveSignatureBitmap ();
        mNoteBookViewSignature.setIntercept(true);
        mNoteBookViewSignature.leaveScribbleMode();
    }


    public static final String CACHE_KEY = "_task_sign_cache_";
    public static final String BITMAP_KEY = "_task_sign_bmp_";
    /**
     * 保存签字bitmap
     */
    private void saveSignatureBitmap () {
        String cacheKey = mTaskDetailStudentActivity.dramaId + "_" + stageId + CACHE_KEY ;
        String bitmapKey = mTaskDetailStudentActivity.dramaId + "_" + stageId + BITMAP_KEY ;
        SaveNoteUtils.getInstance(mContext).saveNoteViewData(mNoteBookViewSignature,
                SaveNoteUtils.getInstance(mContext).getTaskFileDir(),
                cacheKey, bitmapKey , String.valueOf(mTaskDetailStudentActivity.dramaId), stageId);
    }
}
