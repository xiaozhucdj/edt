package com.yougy.task.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frank.etude.pageable.PageBtnBarAdapterV2;
import com.frank.etude.pageable.PageBtnBarV2;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.task.activity.TaskDetailStudentActivity;
import com.yougy.ui.activity.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TaskContentBaseFragment extends TaskBaseFragment {

    @BindView(R.id.content_main)
    ConstraintLayout mConstraintLayout;
    @BindView(R.id.text_task_content)
    TextView mTextViewContent;
    @BindView(R.id.content_pageBar)
    PageBtnBarV2 mPageBtnBarV2;

    private int mCurrentPage;
    private int mPageCount;//页数
    private int mTextMaxLines;
    private int mLines;
    private String mTaskContent = "任务内容为空！";//任务内容不能为空。



    private ConstraintSet applyConstraintSet = new ConstraintSet();

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        LogUtils.d(TAG + " TaskTest onEventMainThread :" + event.getType());
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
        mRootView = inflater.inflate(R.layout.fragment_task_content, container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);
        applyConstraintSet.clone(mConstraintLayout);
        return mRootView;
    }

    @Override
    public void loadData() {
        Log.i(TAG, "loadData: ");
        mStageTaskBeans.clear();
        mStageTaskBeans.addAll(mTaskDetailStudentActivity.getStageTaskBeans());
        if (mStageTaskBeans.size() > 0) {
            if (mStageTaskBeans.get(0).getStageContent() != null)
                mTaskContent = mStageTaskBeans.get(0).getStageContent().get(0).getValue();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mTextMaxLines = mTextViewContent.getMaxLines();
        }
        setCurrentContent ();
        initPageBar ();
    }

    private void initPageBar () {
        LogUtils.d("initPageBar mPageCount =" + mPageCount);
        if (mPageCount == 0) {
            mPageBtnBarV2.setVisibility(View.GONE);
            return;
        }

        if (!mTaskDetailStudentActivity.isBottomBtnShow()) {
            applyConstraintSet.setMargin(R.id.content_pageBar, ConstraintSet.TOP, 961);
            applyConstraintSet.applyTo(mConstraintLayout);

            ViewGroup.LayoutParams layoutParams = mTextViewContent.getLayoutParams();
            layoutParams.width = 900;
            layoutParams.height = 1080;
            mTextViewContent.setLayoutParams(layoutParams);
        }

        mPageBtnBarV2.setPageBarAdapter(new PageBtnBarAdapterV2(mContext) {
            @Override
            public int getPageBtnCount() {
                return mPageCount;
            }

            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn, int lastSelectPageBtnIndex) {
                mCurrentPage = btnIndex ;
                setCurrentContent();
            }

            @Override
            public void onNoPageToShow() {

            }
        });
        mPageBtnBarV2.selectPageBtn(0, false);
    }

    private void setCurrentContent () {
        if (StringUtils.isEmpty(mTaskContent)) {
            LogUtils.w("current content string is Null, server error, return.");
            return;
        }
//        mTextViewContent.setText(mTaskContent);
        setCurrentShowContent();
    }

    private String[] getContentLinesContent (int widthPix, int textSizePix) {
        if (textSizePix <= 0) {
            throw new IllegalArgumentException("IllegalArgumentException, textSize is 0, params error, return.");
        }
        int linesNum = widthPix / textSizePix;
        String[] strings = mTaskContent.split("\r\n");
        mLines = 0;
        for (String str: strings) {
            mLines +=  str.length() / linesNum;
            if (str.length() % linesNum != 0) mLines++ ;
        }

        mPageCount = mLines / mTextMaxLines;
        if (mLines % mTextMaxLines != 0) mPageCount ++;
        LogUtils.d("mPageCount = " + mPageCount + "   mLines = " + mLines);
        int tempStart = 0;
        String[] contentPerLine = new String[mLines];

        for (int i = 0; i < strings.length; i++) {
            strings[i] = strings[i] + "\r\n";
            LogUtils.d("strings[i] = " + strings[i] + "  i = " + i + "   mPageCount" );
            int line = strings[i].length() / linesNum;
            if (strings[i].length() % linesNum != 0) line ++;
            for (int j = 0; j < line; j++) {
                if (j == line - 1) {
                    contentPerLine[tempStart] = strings[i].substring(j * linesNum, strings[i].length());
                } else {
                    contentPerLine[tempStart] = strings[i].substring(j * linesNum, (j + 1) * linesNum);
                }
                tempStart ++;
            }
        }
        return contentPerLine;
    }

    private void setCurrentShowContent () {
        String[] content = getContentLinesContent(860, 24);
        if (mLines < mTextMaxLines) {
            mTextViewContent.setText(mTaskContent);
        } else {
            //分页
            String currentShowStr = "";
            for (int j = mCurrentPage * mTextMaxLines; j < (mCurrentPage + 1) *mTextMaxLines && j < mLines; j++) {
                currentShowStr = currentShowStr + content[j];
            }
            mTextViewContent.setText(currentShowStr);

        }
    }

}
