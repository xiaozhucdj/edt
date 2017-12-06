package com.yougy.homework;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;

import com.yougy.anwser.Content;
import com.yougy.anwser.ParsedQuestionItem;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.new_network.NetWorkManager;
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
 * 单个已批改作业整体概览界面,统计正确率,每道题的对错,等等
 */

public class CheckedHomeworkOverviewActivity extends BaseActivity {
    ActivityHomeworkResultBinding binding;
    QuestionReplySummary currentShow;
    ArrayList<QuestionReplySummary> allReplyList = new ArrayList<QuestionReplySummary>();
    QuestionReplyDetail data;
    int currentShowQuestionPageIndex = 0;
    int currentShowAnswerPageIndex = 0;
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
        refreshViewSafe();
    }

    @Override
    protected void initLayout() {
        binding.questionBodyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.questionBodyBtn.setSelected(true);
                binding.answerBtn.setSelected(false);
                binding.analysisBtn.setSelected(false);
                refreshViewSafe();
            }
        });
        binding.answerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.questionBodyBtn.setSelected(false);
                binding.answerBtn.setSelected(true);
                binding.analysisBtn.setSelected(false);
                refreshViewSafe();
            }
        });
        binding.analysisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.questionBodyBtn.setSelected(false);
                binding.answerBtn.setSelected(false);
                binding.analysisBtn.setSelected(true);
                refreshViewSafe();
            }
        });
        binding.questionBodyBtn.setSelected(true);
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
            if (data.getParsedReplyContentList() == null || data.getParsedReplyContentList().size() == 0){
                binding.questionContainer.setText("没有题目内容");
                binding.lastPageBtn.setClickable(false);
                binding.nextPageBtn.setClickable(false);
            }
            else {
                Content content = data.getParsedReplyContentList().get(currentShowQuestionPageIndex);
                switch (content.getType()){
                    case HTML_URL:
                        binding.questionContainer.setHtmlUrl(content.getValue());
                        break;
                    case IMG_URL:
                        binding.questionContainer.setImgUrl(content.getValue());
                        break;
                    case TEXT:
                        binding.questionContainer.setText(content.getValue());
                        break;
                }
                if (currentShowQuestionPageIndex == 0){
                    binding.lastPageBtn.setClickable(false);
                }
                else {
                    binding.lastPageBtn.setClickable(true);
                }
                if ((currentShowQuestionPageIndex +1) == data.getParsedReplyContentList().size()){
                    binding.nextPageBtn.setClickable(false);
                }
                else {
                    binding.nextPageBtn.setClickable(true);
                }
            }
        }
        else if (binding.answerBtn.isSelected()){
            binding.lastPageBtn.setClickable(false);
            binding.nextPageBtn.setClickable(false);
            if (data.getParsedQuestionItem() == null || data.getParsedQuestionItem().answerList == null
                    || data.getParsedQuestionItem().answerList.size() == 0){
                binding.questionContainer.setText("没有答案");
            }
            else {

                ParsedQuestionItem.Answer answer = data.getParsedQuestionItem().answerList.get(currentShowAnswerPageIndex);
                //答案类型为HTML和IMG的时候支持翻页
                if (answer instanceof ParsedQuestionItem.HtmlAnswer || answer instanceof ParsedQuestionItem.ImgAnswer){
                    if (answer instanceof ParsedQuestionItem.HtmlAnswer){
                        binding.questionContainer.setHtmlUrl(((ParsedQuestionItem.HtmlAnswer) answer).answerUrl);
                    }
                    else if (answer instanceof ParsedQuestionItem.ImgAnswer){
                        binding.questionContainer.setImgUrl(((ParsedQuestionItem.ImgAnswer) answer).imgUrl);
                    }
                    if (currentShowAnswerPageIndex == 0){
                        binding.lastPageBtn.setClickable(false);
                    }
                    else {
                        binding.lastPageBtn.setClickable(true);
                    }
                    if ((currentShowAnswerPageIndex  +1) == data.getParsedQuestionItem().answerList.size()){
                        binding.nextPageBtn.setClickable(false);
                    }
                    else {
                        binding.nextPageBtn.setClickable(true);
                    }
                }
                //答案类型为TEXT的时候把所有"正式"的答案拼在一起显示,不支持分页,"混淆"的答案忽略
                else {
                    String answerString = "";
                    for (ParsedQuestionItem.Answer tempAnswer: data.getParsedQuestionItem().answerList) {
                        if (tempAnswer.answerType.equals("正式")){
                            answerString = answerString + ((ParsedQuestionItem.TextAnswer) tempAnswer).text + "、";
                        }
                    }
                    if (answerString.endsWith("、")){
                        answerString = answerString.substring(0 , answerString.length() - 1);
                    }
                    else {
                        answerString = "没有答案";
                    }
                    binding.questionContainer.setText(answerString);
                    binding.lastPageBtn.setClickable(false);
                    binding.nextPageBtn.setClickable(false);
                }
            }
        }
        else if (binding.analysisBtn.isSelected()){
            if (data.getParsedQuestionItem() == null || data.getParsedQuestionItem().analysisList == null
                    || data.getParsedQuestionItem().analysisList.size() == 0){
                binding.questionContainer.setText("没有解析");
                binding.lastPageBtn.setClickable(false);
                binding.nextPageBtn.setClickable(false);
            }
            else {
                ParsedQuestionItem.Analysis analysis = data.getParsedQuestionItem().analysisList.get(currentShowAnalysisPageIndex);
                if (analysis instanceof ParsedQuestionItem.HtmlAnalysis){
                    binding.questionContainer.setHtmlUrl(((ParsedQuestionItem.HtmlAnalysis) analysis).analysisUrl);
                }
                else if (analysis instanceof ParsedQuestionItem.ImgAnalysis){
                    binding.questionContainer.setImgUrl(((ParsedQuestionItem.ImgAnalysis) analysis).imgUrl);
                }
                else if (analysis instanceof ParsedQuestionItem.TextAnalysis){
                    binding.questionContainer.setText(((ParsedQuestionItem.TextAnalysis) analysis).text);
                }
                if (currentShowAnalysisPageIndex == 0){
                    binding.lastPageBtn.setClickable(false);
                }
                else {
                    binding.lastPageBtn.setClickable(true);
                }
                if ((currentShowAnalysisPageIndex +1) == data.getParsedQuestionItem().analysisList.size()){
                    binding.nextPageBtn.setClickable(false);
                }
                else {
                    binding.nextPageBtn.setClickable(true);
                }
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
        else if (binding.answerBtn.isSelected()) {
            currentShowAnswerPageIndex--;
        }
        else if (binding.analysisBtn.isSelected()) {
            currentShowAnalysisPageIndex--;
        }
        refreshViewSafe();
    }
    public void nextPage(View view){
        if (binding.questionBodyBtn.isSelected()) {
            currentShowQuestionPageIndex++;
        }
        else if (binding.answerBtn.isSelected()) {
            currentShowAnswerPageIndex++;
        }
        else if (binding.analysisBtn.isSelected()) {
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
        currentShowAnswerPageIndex = 0;
        currentShowAnalysisPageIndex = 0;
        binding.questionBodyBtn.setSelected(true);
        binding.answerBtn.setSelected(false);
        binding.analysisBtn.setSelected(false);
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
        currentShowAnswerPageIndex = 0;
        currentShowAnalysisPageIndex = 0;
        binding.questionBodyBtn.setSelected(true);
        binding.answerBtn.setSelected(false);
        binding.analysisBtn.setSelected(false);
        loadData();
    }


}
