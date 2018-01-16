package com.yougy.homework;

import android.databinding.DataBindingUtil;
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
    int currentShowQuestionPageIndex = 0;
    int currentShowAnalysisPageIndex = 0;
    int examId;

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
        ContentDisplayer.ContentAdaper contentAdaper = new ContentDisplayer.ContentAdaper();
        binding.questionDisplayer.setmContentAdaper(contentAdaper);
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
        String questionType = (String) data.getParsedQuestionItem().questionContentList.get(0).getExtraData();
        binding.questionDisplayer.getmContentAdaper()
                .updateDataList("reply" , ListUtil.conditionalSubList(data.getParsedReplyContentList(), new ListUtil.ConditionJudger<Content_new>() {
            @Override
            public boolean isMatchCondition(Content_new nodeInList) {
                return nodeInList.getType() != Content_new.Type.TEXT;
            }
        }));
        binding.questionDisplayer.getmContentAdaper().updateDataList("analysis"
                , data.getParsedQuestionItem().analysisContentList);
        if (questionType.equals("选择")){
            binding.questionDisplayer.getmContentAdaper()
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
        if (binding.questionBodyBtn.isSelected()){
            binding.questionDisplayer.getmContentAdaper().toPage("reply" , currentShowQuestionPageIndex , false);
            if (currentShowQuestionPageIndex == 0){
                binding.lastPageBtn.setClickable(false);
            }
            else {
                binding.lastPageBtn.setClickable(true);
            }
            if ((currentShowQuestionPageIndex +1) == binding.questionDisplayer.getmContentAdaper().getDataList("reply").size()){
                binding.nextPageBtn.setClickable(false);
            }
            else {
                binding.nextPageBtn.setClickable(true);
            }
        }
        else if (binding.answerAnalysisBtn.isSelected()){
            if (questionType.equals("选择")){
                binding.questionDisplayer.getmContentAdaper().toPage("analysis" , currentShowAnalysisPageIndex , true);
            }
            else {
                binding.questionDisplayer.getmContentAdaper().toPage("analysis" , currentShowAnalysisPageIndex , false);
            }
            if (currentShowAnalysisPageIndex == 0){
                binding.lastPageBtn.setClickable(false);
            }
            else {
                binding.lastPageBtn.setClickable(true);
            }
            if ((currentShowAnalysisPageIndex +1) == binding.questionDisplayer.getmContentAdaper().getDataList("analysis").size()){
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
        if (binding.questionBodyBtn.isSelected()) {
            currentShowQuestionPageIndex--;
        }
        else if (binding.answerAnalysisBtn.isSelected()) {
            currentShowAnalysisPageIndex--;
        }
        refreshViewSafe();
    }
    public void nextPage(View view){
        if (binding.questionBodyBtn.isSelected()) {
            currentShowQuestionPageIndex++;
        }
        else if (binding.answerAnalysisBtn.isSelected()) {
            currentShowAnalysisPageIndex++;
        }
        refreshViewSafe();
    }
    public void lastQuestion(View view){
        int questionIndex = ListUtil.conditionalIndexOf(allReplyList, new ListUtil.ConditionJudger<QuestionReplySummary>() {
            @Override
            public boolean isMatchCondition(QuestionReplySummary nodeInList) {
                return nodeInList.getReplyId() == currentShow.getReplyId();
            }
        });
        currentShow = allReplyList.get(questionIndex - 1);
        currentShowQuestionPageIndex = 0;
        currentShowAnalysisPageIndex = 0;
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
        currentShowQuestionPageIndex = 0;
        currentShowAnalysisPageIndex = 0;
        binding.questionBodyBtn.setSelected(true);
        binding.answerAnalysisBtn.setSelected(false);
        loadData();
    }


}
