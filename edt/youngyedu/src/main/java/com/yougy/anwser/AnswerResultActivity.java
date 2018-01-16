package com.yougy.anwser;

import android.databinding.DataBindingUtil;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;

import com.yougy.common.activity.BaseActivity;
import com.yougy.common.new_network.RxResultHelper;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityAnswerResultBinding;

/**
 * Created by FH on 2017/9/6.
 */

public class AnswerResultActivity extends BaseActivity{
    ActivityAnswerResultBinding binding;
    ParsedQuestionItem parsedQuestionItem;
    String questionType;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this) , R.layout.activity_answer_result , null , false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }
    @Override
    protected void init() {
        Parcelable question = getIntent().getParcelableExtra("question");
        if (question == null){
            ToastUtil.showToast(this , "题目内容获取失败");
            finish();
        }
        else {
            parsedQuestionItem = (ParsedQuestionItem) question;
            questionType = (String) parsedQuestionItem.questionContentList.get(0).getExtraData();

        }
    }
    @Override
    protected void initLayout() {
        binding.questionBodyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.questionBodyBtn.setSelected(true);
                binding.answerAnalysisBtn.setSelected(false);
                setQuestionBodyToContent();
            }
        });
        binding.answerAnalysisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.questionBodyBtn.setSelected(false);
                binding.answerAnalysisBtn.setSelected(true);
                setAnalysisToContent();
            }
        });
        binding.contentDisplayer.setmContentAdaper(new ContentDisplayer.ContentAdaper());
    }

    public void setQuestionBodyToContent(){
        binding.questionTypeTextview.setText("题目类型 : " + questionType);
        binding.contentDisplayer.getmContentAdaper().toPage("question" , 0 , false);
    }
    public void setAnalysisToContent(){
        binding.questionTypeTextview.setText("解析");
        if (questionType.equals("选择")){
            binding.contentDisplayer.getmContentAdaper().toPage("analysis" , 0 , true);
        }
        else {
            binding.contentDisplayer.getmContentAdaper().toPage("analysis" , 0 , false);
        }
    }

    @Override
    protected void loadData() {
        binding.contentDisplayer.getmContentAdaper().updateDataList("question" , parsedQuestionItem.questionContentList);
        binding.contentDisplayer.getmContentAdaper().updateDataList("analysis" , parsedQuestionItem.analysisContentList);
        if (questionType.equals("选择")){
            binding.contentDisplayer.getmContentAdaper().setSubText(RxResultHelper.parseAnswerList(parsedQuestionItem.answerContentList));
        }
        binding.questionBodyBtn.performClick();
    }

    @Override
    protected void refreshView() {

    }
    public void back(View view){
        finish();
    }

}
