package com.yougy.homework;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.yougy.anwser.ContentDisplayer;
import com.yougy.anwser.Content_new;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.new_network.RxResultHelper;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.homework.bean.QuestionReplyDetail;
import com.yougy.homework.bean.QuestionReplySummary;
import com.yougy.message.ListUtil;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityHomeworkResultBinding;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by FH on 2017/9/6.
 * 已批改作业详情
 */

public class CheckedHomeworkDetailActivity extends BaseActivity {
    ActivityHomeworkResultBinding binding;
    QuestionReplySummary currentShow;
    ArrayList<QuestionReplySummary> allReplyList = new ArrayList<QuestionReplySummary>();
    QuestionReplyDetail data;
    int examId;

    int currentShowQuestionPageIndex = 0;
    int currentShowAnalysisPageIndex = 0;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_homework_result, null, false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    @Override
    protected void init() {
        binding.titleTv.setText(getIntent().getStringExtra("examName"));
        examId = getIntent().getIntExtra("examId" , -1);
        currentShow = getIntent().getParcelableExtra("toShow");
        allReplyList = getIntent().getParcelableArrayListExtra("all");
        if (currentShow == null || allReplyList == null || allReplyList.size() == 0 || examId == -1){
            ToastUtil.showCustomToast(getApplicationContext() , "必要数据为空");
            finish();
        }
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
        binding.questionBodyBtn.setSelected(true);
        binding.contentDisplayer.setmContentAdaper(new ContentDisplayer.ContentAdaper(){
            @Override
            public void onPageInfoChanged(String typeKey, int newPageCount, int selectPageIndex) {
                refreshPageChangeBtns();
            }
        });
    }

    @Override
    protected void loadData() {
        NetWorkManager.queryReplyDetail(examId , currentShow.getReplyItem() , SpUtils.getUserId() + "")
                .subscribe(new Action1<List<QuestionReplyDetail>>() {
                    @Override
                    public void call(List<QuestionReplyDetail> questionReplyDetails) {
                        if (questionReplyDetails.size() == 0){
                            ToastUtil.showCustomToast(getApplicationContext() , "获取不到数据");
                            return;
                        }
                        data = questionReplyDetails.get(0);
                        String questionType = (String) data.getParsedQuestionItem().questionContentList.get(0).getExtraData();
                        binding.contentDisplayer.getmContentAdaper()
                                .updateDataList("reply" , ListUtil.conditionalSubList(data.getParsedReplyContentList(), new ListUtil.ConditionJudger<Content_new>() {
                                    @Override
                                    public boolean isMatchCondition(Content_new nodeInList) {
                                        return nodeInList.getType() != Content_new.Type.TEXT;
                                    }
                                }));
                        binding.contentDisplayer.getmContentAdaper().updateDataList("analysis"
                                , data.getParsedQuestionItem().analysisContentList);
                        binding.contentDisplayer.getmContentAdaper().updateDataList("question"
                                , data.getParsedQuestionItem().questionContentList);
                        if (questionType.equals("选择")){
                            binding.contentDisplayer.getmContentAdaper()
                                    .setSubText("答案 : " + RxResultHelper.parseAnswerList(data.getParsedQuestionItem().answerContentList));
                        }
                        int questionIndex = ListUtil.conditionalIndexOf(allReplyList, new ListUtil.ConditionJudger<QuestionReplySummary>() {
                            @Override
                            public boolean isMatchCondition(QuestionReplySummary nodeInList) {
                                return nodeInList.getReplyId() == currentShow.getReplyId();
                            }
                        });
                        binding.questionNumTv.setText("第" + (questionIndex+1) + "题");
                        switch (currentShow.getReplyScore()){
                            case 0:
                                binding.scoreIconImv.setImageResource(R.drawable.icon_wrong_1);
                                break;
                            case 100:
                                binding.scoreIconImv.setImageResource(R.drawable.icon_correct_1);
                                break;
                            default:
                                binding.scoreIconImv.setImageResource(R.drawable.icon_half_correct_1);
                                break;
                        }
                        if (data.getParsedReplyCommentList().size() == 0){
                            binding.showCommentBtn.setVisibility(View.GONE);
                        }
                        else {
                            binding.showCommentBtn.setVisibility(View.VISIBLE);
                        }

                        currentShowQuestionPageIndex = 0;
                        currentShowAnalysisPageIndex = 0;
                        refreshViewSafe();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    protected void refreshView() {
        if (binding.questionBodyBtn.isSelected()) {
            if (binding.contentDisplayer.getmContentAdaper().getPageCount("reply") != 0){
                binding.contentDisplayer.getmContentAdaper().toPage("reply" , currentShowQuestionPageIndex , false);
            }
            else {
                binding.contentDisplayer.getmContentAdaper().toPage("question" , currentShowQuestionPageIndex , false);
            }
        }
        else if (binding.answerAnalysisBtn.isSelected()) {
            binding.contentDisplayer.getmContentAdaper().toPage("analysis" , currentShowAnalysisPageIndex , true);
        }
        refreshQuestionChangeBtns();
    }

    public void refreshPageChangeBtns(){
        int currentSelectPageIndex = binding.contentDisplayer.getmContentAdaper().getCurrentSelectPageIndex();
        if (currentSelectPageIndex == 0){
            binding.lastPageBtn.setVisibility(View.GONE);
        }
        else {
            binding.lastPageBtn.setVisibility(View.VISIBLE);
        }

        if (binding.questionBodyBtn.isSelected()){
            if ((currentSelectPageIndex + 1) >= binding.contentDisplayer.getmContentAdaper().getPageCount("reply")){
                binding.nextPageBtn.setVisibility(View.GONE);
            }
            else {
                binding.nextPageBtn.setVisibility(View.VISIBLE);
            }
        }
        else if (binding.answerAnalysisBtn.isSelected()){
            if ((currentSelectPageIndex + 1) >= binding.contentDisplayer.getmContentAdaper().getPageCount("analysis")){
                binding.nextPageBtn.setVisibility(View.GONE);
            }
            else {
                binding.nextPageBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    public void back(View view) {
        finish();
    }

    public void lastPage(View view){
        int lastPageIndex = binding.contentDisplayer.getmContentAdaper().getCurrentSelectPageIndex() - 1;
        if (lastPageIndex >= 0){
            if (binding.questionBodyBtn.isSelected()) {
                currentShowQuestionPageIndex = lastPageIndex;
                if (binding.contentDisplayer.getmContentAdaper().getPageCount("reply") != 0){
                    binding.contentDisplayer.getmContentAdaper().toPage("reply" , lastPageIndex , false);
                }
                else {
                    binding.contentDisplayer.getmContentAdaper().toPage("question" , lastPageIndex , false);
                }
            }
            else if (binding.answerAnalysisBtn.isSelected()) {
                currentShowAnalysisPageIndex = lastPageIndex;
                binding.contentDisplayer.getmContentAdaper().toPage("analysis" , lastPageIndex, true);
            }
        }
    }
    public void nextPage(View view){
        int nextPageIndex = binding.contentDisplayer.getmContentAdaper().getCurrentSelectPageIndex() + 1;
        if (binding.questionBodyBtn.isSelected()) {
            if (nextPageIndex < binding.contentDisplayer.getmContentAdaper().getPageCount("reply")){
                currentShowQuestionPageIndex = nextPageIndex;
                if (binding.contentDisplayer.getmContentAdaper().getPageCount("reply") != 0){
                    binding.contentDisplayer.getmContentAdaper().toPage("reply" , nextPageIndex, false);
                }
                else {
                    binding.contentDisplayer.getmContentAdaper().toPage("question" , nextPageIndex, false);
                }
            }
        }
        else if (binding.answerAnalysisBtn.isSelected()) {
            if (nextPageIndex < binding.contentDisplayer.getmContentAdaper().getPageCount("analysis")){
                currentShowAnalysisPageIndex = nextPageIndex;
                binding.contentDisplayer.getmContentAdaper().toPage("analysis" , nextPageIndex , true);
            }
        }
    }
    public void lastQuestion(View view){
        int questionIndex = ListUtil.conditionalIndexOf(allReplyList, new ListUtil.ConditionJudger<QuestionReplySummary>() {
            @Override
            public boolean isMatchCondition(QuestionReplySummary nodeInList) {
                return nodeInList.getReplyId() == currentShow.getReplyId();
            }
        });
        if (questionIndex != 0){
            currentShow = allReplyList.get(questionIndex - 1);
            binding.questionBodyBtn.setSelected(true);
            binding.answerAnalysisBtn.setSelected(false);
            binding.commentDialog.setVisibility(View.GONE);
            loadData();
        }
        else {
            ToastUtil.showCustomToast(getApplicationContext() , "已经是第一题了");
        }
    }
    public void nextQuestion(View view){
        int questionIndex = ListUtil.conditionalIndexOf(allReplyList, new ListUtil.ConditionJudger<QuestionReplySummary>() {
            @Override
            public boolean isMatchCondition(QuestionReplySummary nodeInList) {
                return nodeInList.getReplyId() == currentShow.getReplyId();
            }
        });
        if (questionIndex+1 < allReplyList.size()){
            currentShow = allReplyList.get(questionIndex + 1);
            binding.questionBodyBtn.setSelected(true);
            binding.answerAnalysisBtn.setSelected(false);
            binding.commentDialog.setVisibility(View.GONE);
            loadData();
        }
        else {
            ToastUtil.showCustomToast(getApplicationContext() , "已经是最后一题了");
        }
    }
    public void refreshQuestionChangeBtns(){
        int questionIndex = ListUtil.conditionalIndexOf(allReplyList, new ListUtil.ConditionJudger<QuestionReplySummary>() {
            @Override
            public boolean isMatchCondition(QuestionReplySummary nodeInList) {
                return nodeInList.getReplyId() == currentShow.getReplyId();
            }
        });
        if (questionIndex != 0){
            binding.lastQuestionBtn.setText("上一题");
            binding.lastQuestionBtn.setBackgroundResource(R.drawable.bmp_bg_blue);
        }
        else {
            binding.lastQuestionBtn.setText("");
            binding.lastQuestionBtn.setBackgroundColor(getResources().getColor(R.color.gray_666666));
        }

        if (questionIndex+1 < allReplyList.size()){
            binding.nextQuestionBtn.setText("下一题");
            binding.nextQuestionBtn.setBackgroundResource(R.drawable.bmp_bg_blue);
        }
        else {
            binding.nextQuestionBtn.setText("");
            binding.nextQuestionBtn.setBackgroundColor(getResources().getColor(R.color.gray_666666));
        }
    }
    public void showComment(View view){
        String commentStr = "";
        for (String comment : data.getParsedReplyCommentList()) {
            if (!TextUtils.isEmpty(comment)){
                commentStr+=comment;
            }
        }
        if (!TextUtils.isEmpty(commentStr)){
            binding.commentTv.setText(commentStr);
            binding.commentDialog.setVisibility(View.VISIBLE);
        }
    }

    public void dismissComment(View view){
        binding.commentDialog.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        allReplyList.clear();
        allReplyList = null;
        Glide.get(this).clearMemory();
        binding.contentDisplayer.clearPdfCache();
        Runtime.getRuntime().gc();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
