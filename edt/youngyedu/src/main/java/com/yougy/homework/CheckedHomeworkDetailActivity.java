package com.yougy.homework;

import android.databinding.DataBindingUtil;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.yougy.anwser.ContentDisplayer;
import com.yougy.anwser.Content_new;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.new_network.RxResultHelper;
import com.yougy.common.utils.SpUtil;
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
 * 已批改作业详情列表
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
            ToastUtil.showToast(getApplicationContext() , "必要数据为空");
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
        NetWorkManager.queryReplyDetail(examId , currentShow.getReplyItem() , SpUtil.getUserId() + "")
                .subscribe(new Action1<List<QuestionReplyDetail>>() {
                    @Override
                    public void call(List<QuestionReplyDetail> questionReplyDetails) {
                        if (questionReplyDetails.size() == 0){
                            ToastUtil.showToast(getApplicationContext() , "获取不到数据");
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
                        binding.questionNumTv.setText("第" + (questionIndex + 1) + "题");
                        if (questionIndex == 0){
                            binding.lastQuestionBtn.setClickable(false);
                        }
                        else {
                            binding.lastQuestionBtn.setClickable(true);
                        }
                        if (questionIndex + 1 == allReplyList.size()){
                            binding.nextQuestionBtn.setClickable(false);
                        }
                        else {
                            binding.nextQuestionBtn.setClickable(true);
                        }
                        switch (currentShow.getReplyScore()){
                            case 0:
                                binding.rightOrWrongIcon.setImageResource(R.drawable.img_ziping_cuowu);
                                break;
                            case 100:
                                binding.rightOrWrongIcon.setImageResource(R.drawable.img_ziping_zhengque);
                                break;
                            default:
                                binding.rightOrWrongIcon.setImageResource(R.drawable.img_ziping_bandui);
                                break;
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
            binding.contentDisplayer.getmContentAdaper().toPage("reply" , currentShowQuestionPageIndex , false);
        }
        else if (binding.answerAnalysisBtn.isSelected()) {
            binding.contentDisplayer.getmContentAdaper().toPage("analysis" , currentShowAnalysisPageIndex , true);
        }
    }

    public void refreshPageChangeBtns(){
        int currentSelectPageIndex = binding.contentDisplayer.getmContentAdaper().getCurrentSelectPageIndex();
        if (currentSelectPageIndex == 0){
            binding.lastPageBtn.setClickable(false);
        }
        else {
            binding.lastPageBtn.setClickable(true);
        }

        if (binding.questionBodyBtn.isSelected()){
            if ((currentSelectPageIndex + 1) >= binding.contentDisplayer.getmContentAdaper().getPageCount("reply")){
                binding.nextPageBtn.setClickable(false);
            }
            else {
                binding.nextPageBtn.setClickable(true);
            }
        }
        else if (binding.answerAnalysisBtn.isSelected()){
            if ((currentSelectPageIndex + 1) >= binding.contentDisplayer.getmContentAdaper().getPageCount("analysis")){
                binding.nextPageBtn.setClickable(false);
            }
            else {
                binding.nextPageBtn.setClickable(true);
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
                binding.contentDisplayer.getmContentAdaper().toPage("reply" , lastPageIndex , false);
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
                binding.contentDisplayer.getmContentAdaper().toPage("reply" , nextPageIndex, false);
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
        currentShow = allReplyList.get(questionIndex - 1);
        binding.questionBodyBtn.setSelected(true);
        binding.answerAnalysisBtn.setSelected(false);
        loadData();
    }
    public void nextQuestion(View view){
        int questionIndex = ListUtil.conditionalIndexOf(allReplyList, new ListUtil.ConditionJudger<QuestionReplySummary>() {
            @Override
            public boolean isMatchCondition(QuestionReplySummary nodeInList) {
                return nodeInList.getReplyId() == currentShow.getReplyId();
            }
        });
        currentShow = allReplyList.get(questionIndex + 1);
        binding.questionBodyBtn.setSelected(true);
        binding.answerAnalysisBtn.setSelected(false);
        loadData();
    }


}
