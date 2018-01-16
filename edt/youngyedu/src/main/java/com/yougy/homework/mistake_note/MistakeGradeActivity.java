package com.yougy.homework.mistake_note;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.yougy.anwser.ContentDisplayer;
import com.yougy.anwser.Content_new;
import com.yougy.anwser.ParsedQuestionItem;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.new_network.RxResultHelper;
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
    int currentShowAnalysisPageIndex = 0;
    private ParsedQuestionItem questionItem;
    private String questionType;
    private ArrayList<Content_new> writeContentList = new ArrayList<Content_new>();
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
                binding.answerAnalysisBtn.setSelected(false);
                refreshViewSafe();
            }
        });
        binding.answerAnalysisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.writedQuestionBtn.setSelected(false);
                binding.answerAnalysisBtn.setSelected(true);
                refreshViewSafe();
            }
        });
        binding.contentDisplayer.setmContentAdaper(new ContentDisplayer.ContentAdaper());
    }

    @Override
    protected void init() {
        questionItem = (ParsedQuestionItem) getIntent().getParcelableExtra("questionItem");
        ArrayList<String> contentList = getIntent().getStringArrayListExtra("writeImgList");
        for (String url : contentList) {
            if (!TextUtils.isEmpty(url)){
                writeContentList.add(new Content_new(Content_new.Type.IMG_URL , 0 , url , null));
            }
        }
        homeworkId = getIntent().getIntExtra("homeworkId" , -1);
        bookTitle = getIntent().getStringExtra("bookTitle");
        questionType = (String) questionItem.questionContentList.get(0).getExtraData();

    }

    @Override
    protected void initLayout() {

    }

    @Override
    protected void loadData() {
        binding.writedQuestionBtn.setSelected(true);
        binding.contentDisplayer.getmContentAdaper().updateDataList("question" , writeContentList);
        binding.contentDisplayer.getmContentAdaper().updateDataList("analysis" , questionItem.analysisContentList);
        if (questionType.equals("选择")){
            binding.contentDisplayer.getmContentAdaper().setSubText(RxResultHelper.parseAnswerList(questionItem.answerContentList));
        }
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
            if (currentShowWriteImgPageIndex + 1 >= writeContentList.size()){
                binding.nextPageBtn.setClickable(false);
            }
            else {
                binding.nextPageBtn.setClickable(true);
            }
            binding.contentDisplayer.getmContentAdaper().toPage("question" , currentShowWriteImgPageIndex , false);
        }
        else if (binding.answerAnalysisBtn.isSelected()){
            if (questionType.equals("选择")){
                binding.contentDisplayer.getmContentAdaper().toPage("analysis" , currentShowAnalysisPageIndex , true);
            }
            else {
                binding.contentDisplayer.getmContentAdaper().toPage("analysis" , currentShowAnalysisPageIndex , false);
            }
            if (currentShowAnalysisPageIndex == 0) {
                binding.lastPageBtn.setClickable(false);
            } else {
                binding.lastPageBtn.setClickable(true);
            }
            if ((currentShowAnalysisPageIndex + 1) == questionItem.analysisList.size()) {
                binding.nextPageBtn.setClickable(false);
            } else {
                binding.nextPageBtn.setClickable(true);
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
        else if (binding.answerAnalysisBtn.isSelected()) {
            currentShowAnalysisPageIndex--;
        }
        refreshViewSafe();
    }
    //下一页
    public void nextPage(View view){
        if (binding.writedQuestionBtn.isSelected()) {
            currentShowWriteImgPageIndex++;
        }
        else if (binding.answerAnalysisBtn.isSelected()) {
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
