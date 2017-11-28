package com.yougy.homework.mistake_note;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;

import com.yougy.homework.HomeworkBaseActivity;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityMistakePractiseBinding;

/**
 * Created by FH on 2017/11/28.
 * 错题练习界面
 */

public class MistakePractiseActivity extends HomeworkBaseActivity{
    ActivityMistakePractiseBinding binding;
    int currentShowQuestionPageIndex = 0;
    int currentShowAnswerPageIndex = 0;
    int currentShowAnalysisPageIndex = 0;
    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this) , R.layout.activity_mistake_practise , null , false);
        setContentView(binding.getRoot());
        binding.questionBodyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.questionBodyBtn.setSelected(true);
                binding.analysisBtn.setSelected(false);
                binding.answerBtn.setSelected(false);
                refreshViewSafe();
            }
        });
        binding.analysisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.questionBodyBtn.setSelected(false);
                binding.analysisBtn.setSelected(true);
                binding.answerBtn.setSelected(false);
                refreshViewSafe();
            }
        });
        binding.answerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.questionBodyBtn.setSelected(false);
                binding.analysisBtn.setSelected(false);
                binding.answerBtn.setSelected(true);
                refreshViewSafe();
            }
        });
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initLayout() {

    }

    @Override
    protected void loadData() {
        binding.questionBodyBtn.setSelected(true);
    }

    @Override
    protected void refreshView() {
        if (binding.questionBodyBtn.isSelected()){

        }
        else if (binding.analysisBtn.isSelected()){

        }
        else if (binding.answerBtn.isSelected()){

        }
    }

    public void back(View view) {
        finish();
    }


    //上一页
    public void lastPage(View view){

    }
    //下一页
    public void nextPage(View view){

    }
    //正确
    public void onRightBtnClick(View view){

    }
    //错误
    public void onWrongBtnClick(View view){

    }
    //我已学会
    public void onHasLearnedBtnCLick(View view){

    }
}
