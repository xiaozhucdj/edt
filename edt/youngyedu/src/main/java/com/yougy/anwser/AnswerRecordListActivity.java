package com.yougy.anwser;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.frank.etude.pageBtnBar.PageBtnBarAdapter;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.homework.bean.HomeworkDetail;
import com.yougy.homework.bean.QuestionReplySummary;
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
    String bookName;
    ArrayList<HomeworkDetail> homeworkDetailList = new ArrayList<HomeworkDetail>();
    ArrayList<Integer> itemIdList = new ArrayList<Integer>();
    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this)
                , R.layout.activity_answer_record_list , null , false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    @Override
    protected void init() {
        ArrayList<Integer> list = (ArrayList<Integer>) getIntent().getSerializableExtra("itemIdList");
        if (list == null || list.size() == 0){
            ToastUtil.showCustomToast(getApplicationContext() , "没有问答");
            finish();
        }
        else {
            itemIdList.addAll(list);
        }
        binding.currentChapterBtn.setText(getIntent().getStringExtra("chapterName"));
        bookName = getIntent().getStringExtra("bookName");
        binding.titleTv.setText(bookName + "问答");
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
                    case "IH51":
                        binding.statusTv.setText("未\n提\n交");
                        binding.statusTv.setBackgroundResource(R.drawable.img_answer_status_bg_red);
                        break;
                    case "IH02"://作答中
                    case "IH03"://未批改
                    case "IH04"://批改中
                    case "IH05"://已批改
                        NetWorkManager.queryReply(homeworkDetail.getExamId() , SpUtils.getUserId() , null)
                                .subscribe(new Action1<List<QuestionReplySummary>>() {
                                    @Override
                                    public void call(List<QuestionReplySummary> replySummaries) {
                                        if (replySummaries.size() == 0){
                                            binding.statusTv.setText("未\n提\n交");
                                            binding.statusTv.setBackgroundResource(R.drawable.img_answer_status_bg_red);
                                        }
                                        else {
                                            QuestionReplySummary replySummary = replySummaries.get(0);
                                            if(replySummary.getReplyStatus().equals("已答完")){
                                                binding.statusTv.setText("批\n改\n中");
                                                binding.statusTv.setBackgroundResource(R.drawable.img_answer_status_bg_red);
                                            }
                                            else {
                                                binding.statusTv.setText("已\n批\n改");
                                                binding.statusTv.setBackgroundResource(R.drawable.img_answer_status_bg_green);
                                            }
                                        }
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        throwable.printStackTrace();
                                    }
                                });
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
                // 用户点击题太快导致按钮还没有初始化 导致binding.pageBtnBar.getCurrentSelectPageIndex() =-1
                HomeworkDetail homeworkDetail ;
                try {
                    homeworkDetail  = homeworkDetailList.get(binding.pageBtnBar.getCurrentSelectPageIndex());
                }catch (Exception e){
                    return;
                }

                Intent newIntent;
                switch (binding.statusTv.getText().toString()){
                    case "作\n答\n中":
                        newIntent = new Intent(getApplicationContext() , AnsweringActivity.class);
                        newIntent.putExtra("itemId" , homeworkDetail.getExamPaper().getPaperContent().get(0).getPaperItem() + "");
                        newIntent.putExtra("from" , homeworkDetail.getExamPaper().getPaperOwner() + "");
                        newIntent.putExtra("examId" , homeworkDetail.getExamId());
                        newIntent.putExtra("startTimeMill" , DateUtils.convertTimeStrToTimeStamp(homeworkDetail.getExamStartTime() , "yyyy-MM-dd HH:mm:ss"));
                        startActivity(newIntent);
                        break;
                    case "未\n批\n改":
                        newIntent = new Intent(getApplicationContext() , AnswerRecordDetailActivity.class);
                        newIntent.putExtra("examId" , homeworkDetail.getExamId());
                        newIntent.putExtra("question" , homeworkDetail.getExamPaper().getPaperContent().get(0).getParsedQuestionItemList().get(0));
                        newIntent.putExtra("status" , "未批改");
                        newIntent.putExtra("bookName" , bookName);
                        newIntent.putExtra("startTime" , homeworkDetail.getExamStartTime());
                        startActivity(newIntent);
                        break;
                    case "批\n改\n中":
                        newIntent = new Intent(getApplicationContext() , AnswerRecordDetailActivity.class);
                        newIntent.putExtra("examId" , homeworkDetail.getExamId());
                        newIntent.putExtra("question" , homeworkDetail.getExamPaper().getPaperContent().get(0).getParsedQuestionItemList().get(0));
                        newIntent.putExtra("status" , "批改中");
                        newIntent.putExtra("bookName" , bookName);
                        newIntent.putExtra("startTime" , homeworkDetail.getExamStartTime());
                        startActivity(newIntent);
                        break;
                    case "已\n批\n改":
                        newIntent = new Intent(getApplicationContext() , AnswerRecordDetailActivity.class);
                        newIntent.putExtra("examId" , homeworkDetail.getExamId());
                        newIntent.putExtra("question" , homeworkDetail.getExamPaper().getPaperContent().get(0).getParsedQuestionItemList().get(0));
                        newIntent.putExtra("status" , "已批改");
                        newIntent.putExtra("bookName" , bookName);
                        newIntent.putExtra("startTime" , homeworkDetail.getExamStartTime());
                        startActivity(newIntent);
                        break;
                    case "未\n提\n交":
                        ToastUtil.showCustomToast(getApplicationContext() , "本次问答您未提交");
                        break;
                }
            }
        });
        binding.contentDisplayer.setScrollEnable(false);
        binding.contentDisplayer.setTextSize(TypedValue.COMPLEX_UNIT_PX , 24);
    }

    @Override
    protected void loadData() {
        String itemIdStr = "[";
        for (int i = 0 ; i < itemIdList.size() ; i++) {
            Integer itemId = itemIdList.get(i);
            itemIdStr = itemIdStr + itemId;
            if (i + 1 < itemIdList.size()){
                itemIdStr = itemIdStr + ",";
            }
        }
        itemIdStr = itemIdStr + "]";
        if (!itemIdStr.equals("[]")){
            NetWorkManager.queryExam(itemIdStr , "{\"order\":\"DESC\"}").subscribe(new Action1<List<HomeworkDetail>>() {
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
    }

    @Override
    protected void refreshView() {

    }

    public void onBack(View view){
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
        binding.contentDisplayer.clearPdfCache();
        Runtime.getRuntime().gc();
    }
}
