package com.yougy.anwser;

import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.yougy.common.activity.BaseActivity;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityAnswerResultBinding;

import java.io.Serializable;

/**
 * Created by FH on 2017/9/6.
 */

public class AnswerResultActivity extends BaseActivity{
    ActivityAnswerResultBinding binding;
    ParsedQuestionItem parsedQuestionItem;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this) , R.layout.activity_answer_result , null , false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }
    @Override
    protected void init() {
        Serializable question = getIntent().getSerializableExtra("question");
        if (question == null){
            ToastUtil.showToast(this , "题目内容获取失败");
            finish();
        }
        else {
            parsedQuestionItem = (ParsedQuestionItem) question;
        }
    }
    @Override
    protected void initLayout() {
        binding.questionBodyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.questionBodyBtn.setSelected(true);
                binding.answerBtn.setSelected(false);
                binding.analysisBtn.setSelected(false);
                setQuestionBodyToContent();
            }
        });
        binding.answerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.questionBodyBtn.setSelected(false);
                binding.answerBtn.setSelected(true);
                binding.analysisBtn.setSelected(false);
                setAnswerToContent();
            }
        });
        binding.analysisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.questionBodyBtn.setSelected(false);
                binding.answerBtn.setSelected(false);
                binding.analysisBtn.setSelected(true);
                setAnalysisToContent();
            }
        });
    }

    public void setQuestionBodyToContent(){
        ParsedQuestionItem.Question question = parsedQuestionItem.questionList.get(0);
        binding.questionTypeTextview.setText("题目类型 : " + question.questionType);
        if (question instanceof ParsedQuestionItem.HtmlQuestion){
            binding.questionContainer.setHtmlUrl(((ParsedQuestionItem.HtmlQuestion) question).htmlUrl);
        }
        else if (question instanceof ParsedQuestionItem.TextQuestion){
            binding.questionContainer.setText(((ParsedQuestionItem.TextQuestion) question).text);
        }
        else if (question instanceof ParsedQuestionItem.ImgQuestion){
            binding.questionContainer.setImgUrl(((ParsedQuestionItem.ImgQuestion) question).imgUrl);
        }
    }
    public void setAnswerToContent(){
        binding.questionTypeTextview.setText("答案");
        if (parsedQuestionItem.answerList.size() == 0){
            binding.questionContainer.setText("没有答案");
            return;
        }
        ParsedQuestionItem.Answer answer = parsedQuestionItem.answerList.get(0);
        if (answer instanceof ParsedQuestionItem.HtmlAnswer){
            binding.questionContainer.setHtmlUrl(((ParsedQuestionItem.HtmlAnswer) answer).answerUrl);
        }
        else if (answer instanceof ParsedQuestionItem.TextAnswer){
            String answerText = "";
            for (int i = 0 ; i < parsedQuestionItem.answerList.size() ; i++){
                ParsedQuestionItem.Answer tempAnswer = parsedQuestionItem.answerList.get(i);
                if (tempAnswer instanceof ParsedQuestionItem.TextAnswer && tempAnswer.answerType.equals("正式")){
                    answerText = answerText + ((ParsedQuestionItem.TextAnswer) tempAnswer).text + "、";
                }
            }
            if (TextUtils.isEmpty(answerText)){
                binding.questionContainer.setText("没有答案");
            }
            else {
                binding.questionContainer.setText(answerText.substring(0 , answerText.length() - 1));
            }
        }
        else if (answer instanceof ParsedQuestionItem.ImgAnswer){
            binding.questionContainer.setImgUrl(((ParsedQuestionItem.ImgAnswer) answer).imgUrl);
        }
    }
    public void setAnalysisToContent(){
        binding.questionTypeTextview.setText("解析");
        if (parsedQuestionItem.analysisList.size() == 0){
            binding.questionContainer.setText("没有解析");
            return;
        }
        ParsedQuestionItem.Analysis analysis = parsedQuestionItem.analysisList.get(0);
        if (analysis instanceof ParsedQuestionItem.HtmlAnalysis){
            binding.questionContainer.setHtmlUrl(((ParsedQuestionItem.HtmlAnalysis) analysis).analysisUrl);
        }
        else if (analysis instanceof ParsedQuestionItem.TextAnalysis){
            binding.questionContainer.setText(((ParsedQuestionItem.TextAnalysis) analysis).text);
        }
        else if (analysis instanceof ParsedQuestionItem.ImgAnalysis){
            binding.questionContainer.setImgUrl(((ParsedQuestionItem.ImgAnalysis) analysis).imgUrl);
        }
    }

    @Override
    protected void loadData() {
        binding.questionBodyBtn.performClick();
    }

    @Override
    protected void refreshView() {

    }
    public void back(View view){
        finish();
    }

}
