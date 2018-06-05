package com.yougy.anwser;

import android.databinding.DataBindingUtil;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.new_network.RxResultHelper;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.homework.bean.QuestionReplySummary;
import com.yougy.message.ListUtil;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityAnswerResultBinding;

import java.util.List;

import rx.functions.Action1;

/**
 * Created by FH on 2017/9/6.
 * 学生点击提交问答后学生查看答案和题干的界面.
 */

public class AnswerResultActivity extends BaseActivity{
    ActivityAnswerResultBinding binding;
    String replyId;
    ParsedQuestionItem parsedQuestionItem;
    String questionType;
    int currentShowReplyPageIndex = 0;
    int currentShowAnalysisPageIndex = 0;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this) , R.layout.activity_answer_result , null , false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }
    @Override
    protected void init() {
        Parcelable question = getIntent().getParcelableExtra("question");
        replyId = getIntent().getStringExtra("replyId");
        if (question == null){
            ToastUtil.showCustomToast(this , "题目内容获取失败");
            finish();
            return;
        }
        if (TextUtils.isEmpty(replyId)){
            ToastUtil.showCustomToast(this , "学生回答获取失败");
            finish();
            return;
        }
        parsedQuestionItem = (ParsedQuestionItem) question;
        questionType = (String) parsedQuestionItem.questionContentList.get(0).getExtraData();
    }
    @Override
    protected void initLayout() {
        binding.questionBodyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.questionBodyBtn.setSelected(true);
                binding.answerAnalysisBtn.setSelected(false);
                refreshViewSafe();
            }
        });
        binding.answerAnalysisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.questionBodyBtn.setSelected(false);
                binding.answerAnalysisBtn.setSelected(true);
                refreshViewSafe();
            }
        });
        binding.contentDisplayer.setmContentAdaper(new ContentDisplayer.ContentAdaper(){
            @Override
            public void onPageInfoChanged(String typeKey, int newPageCount, int selectPageIndex) {
                binding.pageBtnBar.setCurrentSelectPageIndex(selectPageIndex);
                binding.pageBtnBar.refreshPageBar();
            }
        });
        binding.pageBtnBar.setPageBarAdapter(new PageBtnBar.PageBarAdapter() {
            @Override
            public int getPageBtnCount() {
                if (binding.questionBodyBtn.isSelected()){
                    return binding.contentDisplayer.getmContentAdaper().getPageCount("reply");
                }
                else if (binding.answerAnalysisBtn.isSelected()){
                    return binding.contentDisplayer.getmContentAdaper().getPageCount("analysis");
                }
                return 0;
            }

            @Override
            public String getPageText(int index) {
                return String.valueOf(index + 1);
            }
        });
        binding.pageBtnBar.setOnPageBtnClickListener(new PageBtnBar.OnPageBtnClickListener() {
            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn) {
                if (binding.questionBodyBtn.isSelected()){
                    currentShowReplyPageIndex = btnIndex;
                    refreshView();
                }
                else if (binding.answerAnalysisBtn.isSelected()){
                    currentShowAnalysisPageIndex = btnIndex;
                    refreshView();
                }
            }
        });
    }

    @Override
    protected void loadData() {
        NetWorkManager.queryReply(null , null , replyId).subscribe(new Action1<List<QuestionReplySummary>>() {
            @Override
            public void call(List<QuestionReplySummary> replySummaries) {
                if (replySummaries.size() == 0){
                    ToastUtil.showCustomToast(getApplicationContext() , "获取学生回答失败");
                    finish();
                    return;
                }
                binding.contentDisplayer.getmContentAdaper().updateDataList("reply"
                        , ListUtil.conditionalSubList(replySummaries.get(0).getParsedContentList(), new ListUtil.ConditionJudger<Content_new>() {
                    @Override
                    public boolean isMatchCondition(Content_new nodeInList) {
                        return nodeInList.getType() == Content_new.Type.IMG_URL;
                    }
                }));
                binding.contentDisplayer.getmContentAdaper().updateDataList("analysis" , parsedQuestionItem.analysisContentList);
                if (questionType.equals("选择")){
                    binding.contentDisplayer.getmContentAdaper().setSubText("答案 : " + RxResultHelper.parseAnswerList(parsedQuestionItem.answerContentList));
                }
                binding.questionBodyBtn.performClick();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                ToastUtil.showCustomToast(AnswerResultActivity.this , "获取学生回答失败");
                throwable.printStackTrace();
                finish();
            }
        });
    }

    @Override
    protected void refreshView() {
        if (binding.questionBodyBtn.isSelected()){
            binding.questionTypeTextview.setText("题目类型 : " + questionType);
            binding.contentDisplayer.getmContentAdaper().toPage("reply" , currentShowReplyPageIndex, false);
        }
        else if (binding.answerAnalysisBtn.isSelected()){
            binding.questionTypeTextview.setText("解析");
            if (questionType.equals("选择")){
                binding.contentDisplayer.getmContentAdaper().toPage("analysis" , currentShowAnalysisPageIndex , true);
            }
            else {
                binding.contentDisplayer.getmContentAdaper().toPage("analysis" , currentShowAnalysisPageIndex , false);
            }
        }
    }
    public void back(View view){
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
        binding.contentDisplayer.clearPdfCache();
        Runtime.getRuntime().gc();
    }

}
