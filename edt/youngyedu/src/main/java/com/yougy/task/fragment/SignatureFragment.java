package com.yougy.task.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.common.utils.UIUtils;
import com.yougy.task.activity.SaveNoteUtils;
import com.yougy.ui.activity.R;
import com.yougy.view.NoteBookView2;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignatureFragment extends TaskBaseFragment {

    @BindView(R.id.signature_noteView)
    NoteBookView2 mNoteBookViewSignature;

    private String mCurrentCacheName = "";


    @Override
    protected View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.activity_signature,container , false);
        mUnbinder = ButterKnife.bind(this, mRootView);
        mCurrentCacheName = "_signature_stu_";
        mNoteBookViewSignature.setIntercept(true);
        return super.initView(inflater, container, savedInstanceState);
    }

    @Override
    protected void init() {

    }


    @OnClick({R.id.btn_submit, R.id.btn_cancel})
    public void onClick (View view){
        switch (view.getId()) {
            case R.id.btn_submit:
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

    /**
     * 提交签名  Server   //todo  提交成功 finish   提交失败  提示，暂存。重试.
     */
    private void submitSignature () {

    }

    /**
     * 保存签字bitmap
     */
    private void saveSignatureBitmap () {
        ArrayList<String> pathLists = new ArrayList<>();
        SaveNoteUtils.getInstance(mContext).saveNoteViewData(mNoteBookViewSignature, SaveNoteUtils.TASK_FILE_DIR,
                mCurrentCacheName, mCurrentCacheName,
                true, 0,  pathLists, false);
    }
}
