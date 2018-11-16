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
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.StringUtils;
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
    private String mTaskContent = "   sad睡觉的啦几点啦是大方可怕开发怕是肯定怕是快递发浦发卡皮肤科啊疯狂泡咖啡怕送滴哦菩萨道发票是咖啡泼阿斯顿啊发破伤风" +
            "!\r\n   sad睡觉的啦几点啦。\r\n  是大方可怕开发怕是肯定怕是快递发浦发卡皮肤科啊疯狂泡咖啡怕送滴哦菩萨道发票是咖啡泼阿斯顿啊发破伤风" +
            "sad睡觉的啦几点啦是大！方可怕开发怕是肯定怕是快递发浦发卡皮肤科啊疯狂泡咖啡怕送滴哦菩萨道发票。\r\n   是咖啡泼阿斯顿啊发破伤风" +
            "sad睡觉的啦几点啦是大方可怕开。发怕是肯定怕是快递发浦发卡皮肤科啊疯狂泡咖啡怕送滴哦菩萨道发票是咖啡泼阿斯顿啊发破伤风" +
            "sad睡觉的啦几点啦是大方可怕开发，怕是肯定怕是快递发浦发卡皮肤科啊疯狂泡\r\n  咖啡怕送滴哦菩萨道发票是咖啡泼阿斯顿啊发破伤风" +
            "sad睡觉的啦几点啦是大方可怕，开发怕是肯定怕是快递发浦发卡皮。肤科啊疯狂泡咖\r\n   啡怕送滴哦菩萨道发票是咖啡泼阿斯顿啊发破伤风" +
            "sad睡觉的啦几点啦是大方可怕开发怕是肯定怕是快递发浦发卡皮肤科？啊疯狂泡咖啡怕送\r\n  滴哦菩萨道发票是咖啡泼阿斯顿啊发破伤风!"+
            "!\r\n   sad睡觉的啦几点啦。\r\n  是大方可怕开发怕是肯定怕是快递发浦发卡皮肤科啊疯狂泡咖啡怕送滴哦菩萨道发票是咖啡泼阿斯顿啊发破伤风" +
            "sad睡觉的啦几点啦是大！方可怕开发怕是肯定怕是快递发浦发卡皮肤科啊疯狂泡咖啡怕送滴哦菩萨道发票。\r\n   是咖啡泼阿斯顿啊发破伤风" +
            "sad睡觉的啦几点啦是大方可怕开。发怕是肯定怕是快递发浦发卡皮肤科啊疯狂泡咖啡怕送滴哦菩萨道发票是咖啡泼阿斯顿啊发破伤风" +
            "sad睡觉的啦几点啦是大方可怕开发，怕是肯定怕是快递发浦发卡皮肤科啊疯狂泡\r\n  咖啡怕送滴哦菩萨道发票是咖啡泼阿斯顿啊发破伤风" +
            "sad睡觉的啦几点啦是大方可怕，开发怕是肯定怕是快递发浦发卡皮。肤科啊疯狂泡咖\r\n   啡怕送滴哦菩萨道发票是咖啡泼阿斯顿啊发破伤风" +
            "sad睡觉的啦几点啦是大方可怕开发怕是肯定怕是快递发浦发卡皮肤科？啊疯狂泡咖啡怕送\r\n  滴哦菩萨道发票是咖啡泼阿斯顿啊发破伤风!"+
            "!\r\n   sad睡觉的啦几点啦。\r\n  是大方可怕开发怕是肯定怕是快递发浦发卡皮肤科啊疯狂泡咖啡怕送滴哦菩萨道发票是咖啡泼阿斯顿啊发破伤风" +
            "sad睡觉的啦几点啦是大！方可怕开发怕是肯定怕是快递发浦发卡皮肤科啊疯狂泡咖啡怕送滴哦菩萨道发票。\r\n   是咖啡泼阿斯顿啊发破伤风" +
            "sad睡觉的啦几点啦是大方可怕开。发怕是肯定怕是快递发浦发卡皮肤科啊疯狂泡咖啡怕送滴哦菩萨道发票是咖啡泼阿斯顿啊发破伤风" +
            "sad睡觉的啦几点啦是大方可怕开发，怕是肯定怕是快递发浦发卡皮肤科啊疯狂泡\r\n  咖啡怕送滴哦菩萨道发票是咖啡泼阿斯顿啊发破伤风" +
            "sad睡觉的啦几点啦是大方可怕，开发怕是肯定怕是快递发浦发卡皮。肤科啊疯狂泡咖\r\n   啡怕送滴哦菩萨道发票是咖啡泼阿斯顿啊发破伤风" +
            "sad睡觉的啦几点啦是大方可怕开发怕是肯定怕是快递发浦发卡皮肤科？啊疯狂泡咖啡怕送\r\n  滴哦菩萨道发票是咖啡泼阿斯顿啊发破伤风!" +
            "!\r\n   sad睡觉的啦几点啦。\r\n  是大方可怕开发怕是肯定怕是快递发浦发卡皮肤科啊疯狂泡咖啡怕送滴哦菩萨道发票是咖啡泼阿斯顿啊发破伤风" +
            "sad睡觉的啦几点啦是大！方可怕开发怕是肯定怕是快递发浦发卡皮肤科啊疯狂泡咖啡怕送滴哦菩萨道发票。\r\n   是咖啡泼阿斯顿啊发破伤风" +
            "sad睡觉的啦几点啦是大方可怕开。发怕是肯定怕是快递发浦发卡皮肤科啊疯狂泡咖啡怕送滴哦菩萨道发票是咖啡泼阿斯顿啊发破伤风" +
            "sad睡觉的啦几点啦是大方可怕开发，怕是肯定怕是快递发浦发卡皮肤科啊疯狂泡\r\n  咖啡怕送滴哦菩萨道发票是咖啡泼阿斯顿啊发破伤风" +
            "sad睡觉的啦几点啦是大方可怕，开发怕是肯定怕是快递发浦发卡皮。肤科啊疯狂泡咖\r\n   啡怕送滴哦菩萨道发票是咖啡泼阿斯顿啊发破伤风" +
            "sad睡觉的啦几点啦是大方可怕开发怕是肯定怕是快递发浦发卡皮肤科？啊疯狂泡咖啡怕送\r\n  滴哦菩萨道发票是咖啡泼阿斯顿啊发破伤风!";



    private ConstraintSet applyConstraintSet = new ConstraintSet();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_task_content, container, false);
        mUnbinder = ButterKnife.bind(this, mRootView);
        applyConstraintSet.clone(mConstraintLayout);
        mTextMaxLines = mTextViewContent.getMaxLines();
        setCurrentContent ();
        initPageBar ();
        return mRootView;
    }

    @Override
    public void loadData() {
        Log.i(TAG, "loadData: ");
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
