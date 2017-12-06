package com.yougy.homework.mistake_note;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.yougy.anwser.ParsedQuestionItem;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.ToastUtil;
import com.yougy.homework.FullScreenHintDialog;
import com.yougy.homework.HomeworkBaseActivity;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityMistakeGradeBinding;

import java.util.ArrayList;

import rx.functions.Action1;

/**
 * Created by FH on 2017/11/28.
 * 错题自评界面
 */

public class MistakeGradeActivity extends HomeworkBaseActivity{
    ActivityMistakeGradeBinding binding;
    int currentShowWriteImgPageIndex = 0;
    int currentShowAnswerPageIndex = 0;
    int currentShowAnalysisPageIndex = 0;
    private ParsedQuestionItem questionItem;
    private ArrayList<String> writeImgList;
    private int homeworkId;
    private String bookTitle;
    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this) , R.layout.activity_mistake_grade, null , false);
        setContentView(binding.getRoot());
        binding.writedQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.writedQuestionBtn.setSelected(true);
                binding.analysisBtn.setSelected(false);
                binding.answerBtn.setSelected(false);
                refreshViewSafe();
            }
        });
        binding.analysisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.writedQuestionBtn.setSelected(false);
                binding.analysisBtn.setSelected(true);
                binding.answerBtn.setSelected(false);
                refreshViewSafe();
            }
        });
        binding.answerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.writedQuestionBtn.setSelected(false);
                binding.analysisBtn.setSelected(false);
                binding.answerBtn.setSelected(true);
                refreshViewSafe();
            }
        });
    }

    @Override
    protected void init() {
        questionItem = (ParsedQuestionItem) getIntent().getSerializableExtra("questionItem");
        writeImgList = getIntent().getStringArrayListExtra("writeImgList");
        homeworkId = getIntent().getIntExtra("homeworkId" , -1);
        bookTitle = getIntent().getStringExtra("bookTitle");
    }

    @Override
    protected void initLayout() {

    }

    @Override
    protected void loadData() {
        binding.writedQuestionBtn.setSelected(true);
        refreshViewSafe();
    }

    @Override
    protected void refreshView() {
        if (!TextUtils.isEmpty(bookTitle)){
            binding.subTitleTv.setText(" - " + bookTitle);
        }
        if (binding.writedQuestionBtn.isSelected()){
            if (currentShowWriteImgPageIndex == 0){
                binding.lastPageBtn.setClickable(false);
            }
            else {
                binding.lastPageBtn.setClickable(true);
            }
            if (currentShowWriteImgPageIndex + 1 >= writeImgList.size()){
                binding.nextPageBtn.setClickable(false);
            }
            else {
                binding.nextPageBtn.setClickable(true);
            }
            if (writeImgList == null || writeImgList.size() == 0){
                binding.questionContainer.setText("没有题目数据");
            }
            else {
                binding.questionContainer.setImgUrl(writeImgList.get(currentShowWriteImgPageIndex));
            }
        }
        else if (binding.answerBtn.isSelected()){
            binding.lastPageBtn.setClickable(false);
            binding.nextPageBtn.setClickable(false);
            if (questionItem == null || questionItem.answerList == null
                    || questionItem.answerList.size() == 0){
                binding.questionContainer.setText("没有答案");
            }
            else {
                //答案类型为HTML和IMG的时候支持翻页
                ParsedQuestionItem.Answer answer = questionItem.answerList.get(currentShowAnswerPageIndex);
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
                    if ((currentShowAnswerPageIndex +1) == questionItem.answerList.size()){
                        binding.nextPageBtn.setClickable(false);
                    }
                    else {
                        binding.nextPageBtn.setClickable(true);
                    }
                }
                else {
                    //答案类型为TEXT的时候把所有"正式"的答案拼在一起显示,不支持分页,"混淆"的答案忽略
                    String answerString = "";
                    for (ParsedQuestionItem.Answer tempAnswer: questionItem.answerList) {
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
            if (questionItem== null || questionItem.analysisList == null
                    || questionItem.analysisList.size() == 0){
                binding.questionContainer.setText("没有解析");
                binding.lastPageBtn.setClickable(false);
                binding.nextPageBtn.setClickable(false);
            }
            else {
                ParsedQuestionItem.Analysis analysis = questionItem.analysisList.get(currentShowAnalysisPageIndex);
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
                if ((currentShowAnalysisPageIndex +1) == questionItem.analysisList.size()){
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


    //上一页
    public void lastPage(View view){
        if (binding.writedQuestionBtn.isSelected()) {
            currentShowWriteImgPageIndex--;
        }
        else if (binding.answerBtn.isSelected()) {
            currentShowAnswerPageIndex--;
        }
        else if (binding.analysisBtn.isSelected()) {
            currentShowAnalysisPageIndex--;
        }
        refreshViewSafe();
    }
    //下一页
    public void nextPage(View view){
        if (binding.writedQuestionBtn.isSelected()) {
            currentShowWriteImgPageIndex++;
        }
        else if (binding.answerBtn.isSelected()) {
            currentShowAnswerPageIndex++;
        }
        else if (binding.analysisBtn.isSelected()) {
            currentShowAnalysisPageIndex++;
        }
        refreshViewSafe();
    }
    //正确
    public void onRightBtnClick(View view){
        NetWorkManager.setMistakeLastScore(homeworkId , questionItem.itemId , 100)
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        ToastUtil.showToast(getApplicationContext() , "自评完成");
                        finish();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtil.showToast(getApplicationContext() , "自评失败" + throwable.getMessage());
                    }
                });
        YougyApplicationManager.getRxBus(getApplicationContext()).send("lastScoreChanged:" + questionItem.itemId + ":" + 100);
    }
    //错误
    public void onWrongBtnClick(View view){
        NetWorkManager.setMistakeLastScore(homeworkId , questionItem.itemId , 0)
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        finish();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtil.showToast(getApplicationContext() , "自评失败" + throwable.getMessage());
                    }
                });
        YougyApplicationManager.getRxBus(getApplicationContext()).send("lastScoreChanged:" + questionItem.itemId + ":" + 0);
    }
    //我已学会
    public void onHasLearnedBtnCLick(View view){
        new FullScreenHintDialog(this , "hasLearned")
                .setIconResId(R.drawable.icon_caution_big)
                .setContentText("是否从我的错题本中移除该题?")
                .setBtn1("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NetWorkManager.deleteMistake(homeworkId , questionItem.itemId).subscribe(new Action1<Object>() {
                            @Override
                            public void call(Object o) {
                                ToastUtil.showToast(getApplicationContext() , "已学会");
                                finish();
                                YougyApplicationManager.getRxBus(getApplicationContext()).send("removeMistakeItem:" + questionItem.itemId);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                                ToastUtil.showToast(getApplicationContext() , "删除错题失败" + throwable.getMessage());
                            }
                        });
                    }
                } , true).show();
    }
}
