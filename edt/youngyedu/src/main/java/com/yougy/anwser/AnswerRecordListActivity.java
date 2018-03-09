package com.yougy.anwser;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.frank.etude.pageBtnBar.PageBtnBarAdapter;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.homework.bean.HomeworkDetail;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityAnswerRecordListBinding;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by FH on 2018/3/8.
 * 问答记录列表
 */

public class AnswerRecordListActivity extends AnswerBaseActivity{
    ActivityAnswerRecordListBinding binding;
    int bookId,cursorId;
    ArrayList<HomeworkDetail> homeworkDetailList = new ArrayList<HomeworkDetail>();
    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this)
                , R.layout.activity_answer_record_list , null , false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    @Override
    protected void init() {
        bookId = getIntent().getIntExtra("bookId" , -1);
        cursorId = getIntent().getIntExtra("cursor" , -1);
        binding.currentChapterBtn.setText(getIntent().getStringExtra("chapterName"));
        binding.titleTv.setText(getIntent().getStringExtra("bookName" + "问答"));
    }

    @Override
    protected void initLayout() {
        binding.pageBtnBar.setPageBarAdapter(new PageBtnBarAdapter(this) {
            @Override
            public int getPageBtnCount() {
                return homeworkDetailList.size();
            }

            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn) {
                HomeworkDetail homeworkDetail = homeworkDetailList.get(btnIndex);
                ParsedQuestionItem parsedQuestionItem = homeworkDetail.getExamPaper().getPaperContent().get(0).getParsedQuestionItemList().get(0);

                binding.contentDisplayer.getmContentAdaper().updateDataList("question"
                        ,parsedQuestionItem.questionContentList);
                String subText = "        题型 : " + parsedQuestionItem.questionContentList.get(0).getExtraData()
                        + "        提问时间 : " + homeworkDetail.getExamCreateTime();
                binding.contentDisplayer.getmContentAdaper().setSubText(subText);
                binding.contentDisplayer.getmContentAdaper().toPage("question" , 0 , true);
                switch (homeworkDetail.getExamStatusCode()){
                    case "IH01":
                        binding.statusTv.setText("未\n开\n始");
                        binding.statusTv.setBackgroundResource(R.drawable.img_answer_status_bg_red);
                        break;
                    case "IH02":
                        binding.statusTv.setText("作\n答\n中");
                        binding.statusTv.setBackgroundResource(R.drawable.img_answer_status_bg_red);
                        break;
                    case "IH03":
                        binding.statusTv.setText("未\n批\n改");
                        binding.statusTv.setBackgroundResource(R.drawable.img_answer_status_bg_red);
                        break;
                    case "IH04":
                        binding.statusTv.setText("批\n改\n中");
                        binding.statusTv.setBackgroundResource(R.drawable.img_answer_status_bg_red);
                        break;
                    case "IH05":
                        binding.statusTv.setText("已\n批\n改");
                        binding.statusTv.setBackgroundResource(R.drawable.img_answer_status_bg_green);
                        break;
                    case "IH51":
                        binding.statusTv.setText("未\n提\n交");
                        binding.statusTv.setBackgroundResource(R.drawable.img_answer_status_bg_red);
                        break;
                }
            }
        });
        binding.contentDisplayer.setmContentAdaper(new ContentDisplayer.ContentAdaper(){
            @Override
            public void onPageInfoChanged(String typeKey, int newPageCount, int selectPageIndex) {

            }
        });
        binding.contentDisplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeworkDetail homeworkDetail = homeworkDetailList.get(binding.pageBtnBar.getCurrentSelectPageIndex());
                Intent newIntent;
                switch (homeworkDetail.getExamStatus()){
                    case "作答中":
                        newIntent = new Intent(getApplicationContext() , AnsweringActivity.class);
                        newIntent.putExtra("itemId" , homeworkDetail.getExamPaper().getPaperContent().get(0).getPaperItem() + "");
                        newIntent.putExtra("from" , homeworkDetail.getExamPaper().getPaperOwner() + "");
                        newIntent.putExtra("examId" , homeworkDetail.getExamId());
                        newIntent.putExtra("startTimeMill" , DateUtils.convertTimeStrToTimeStamp(homeworkDetail.getExamStartTime() , "yyyy-MM-dd HH:mm:ss"));
                        startActivity(newIntent);
                        break;
                    case "未批改":
                    case "批改中":
                        newIntent = new Intent(getApplicationContext() , AnswerRecordDetailActivity.class);
                        newIntent.putExtra("examId" , homeworkDetail.getExamId());
                        newIntent.putExtra("question" , homeworkDetail.getExamPaper().getPaperContent().get(0).getParsedQuestionItemList().get(0));
                        startActivity(newIntent);
                        break;
                    case "已批改":
                        newIntent = new Intent(getApplicationContext() , AnswerRecordDetailActivity.class);
                        newIntent.putExtra("examId" , homeworkDetail.getExamId());
                        newIntent.putExtra("question" , homeworkDetail.getExamPaper().getPaperContent().get(0).getParsedQuestionItemList().get(0));
                        startActivity(newIntent);
                        break;
                }
            }
        });
        binding.contentDisplayer.setScrollEnable(false);
        binding.contentDisplayer.setTextSize(TypedValue.COMPLEX_UNIT_PX , 24);
    }

    @Override
    protected void loadData() {
        NetWorkManager.queryAnswer(SpUtil.getStudent().getClassId() , bookId + "" , cursorId).subscribe(new Action1<List<HomeworkDetail>>() {
            @Override
            public void call(List<HomeworkDetail> homeworkDetails) {
                homeworkDetailList.clear();
                homeworkDetailList.addAll(homeworkDetails);
                binding.pageBtnBar.refreshPageBar();
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

    }

    public void onBack(View view){
        onBackPressed();
    }
}
