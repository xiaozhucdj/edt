package com.yougy.anwser;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.homework.bean.QuestionReplySummary;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityAnswerRecordDetailBinding;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by FH on 2018/3/9.
 * 学生问答记录某一条记录的详情界面
 */

public class AnswerRecordDetailActivity extends BaseActivity{
    ActivityAnswerRecordDetailBinding binding;
    ParsedQuestionItem parsedQuestionItem;
    int examId;

    //当前展示的学生答案的页码(从0开始)
    private int currentShowReplyPageIndex = 0;
    //当前展示的解析的页码(从0开始)
    private int currentShowAnalysisPageIndex = 0;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this) , R.layout.activity_answer_record_detail , null , false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    @Override
    protected void init() {
        examId = getIntent().getIntExtra("examId", -1);
        if (examId == -1 ) {
            ToastUtil.showCustomToast(getApplicationContext(), "exam获取失败!");
            return;
        }
        parsedQuestionItem = getIntent().getParcelableExtra("question");
        String status = getIntent().getStringExtra("status");
        if ("批改中".equals(status)){
            binding.statusHintTv.setVisibility(View.VISIBLE);
        }
        else if ("已批改".equals(status)){
            binding.buttomBtn.setVisibility(View.VISIBLE);
        }
        binding.titleTv.setText(getIntent().getStringExtra("bookName") + "问答");
        binding.startTimeTv.setText("问答开始时间:" + getIntent().getStringExtra("startTime"));
        binding.questionTypeTextview.setText(parsedQuestionItem.questionContentList.get(0).getExtraData().toString());
    }

    @Override
    protected void initLayout() {
        binding.contentDisplayer.setmContentAdaper(new ContentDisplayer.ContentAdaper(){
            @Override
            public void onPageInfoChanged(String typeKey, int newPageCount, int selectPageIndex) {
                if (typeKey.equals("reply")) {
                    currentShowReplyPageIndex = selectPageIndex;
                } else if (typeKey.equals("analysis")) {
                    currentShowAnalysisPageIndex = selectPageIndex;
                }
                refreshPageBtns();
            }
        });
        binding.questionBodyBtn.setSelected(true);
        binding.questionBodyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //题干
                if (!binding.questionBodyBtn.isSelected()) {
                    binding.questionBodyBtn.setSelected(true);
                    binding.answerAnalysisBtn.setSelected(false);
                    binding.contentDisplayer.getmContentAdaper().toPage("reply", currentShowReplyPageIndex, false);
                }
            }
        });
        binding.answerAnalysisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //解答
                if (binding.questionBodyBtn.isSelected()) {
                    binding.questionBodyBtn.setSelected(false);
                    binding.answerAnalysisBtn.setSelected(true);
                    binding.contentDisplayer.getmContentAdaper().toPage("analysis", currentShowAnalysisPageIndex, true);
                }
            }
        });
        binding.lastPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //上一页
                if (binding.questionBodyBtn.isSelected()) {
                    currentShowReplyPageIndex--;
                    binding.contentDisplayer.getmContentAdaper().toPage("reply", currentShowReplyPageIndex, false);
                } else if (binding.answerAnalysisBtn.isSelected()) {
                    currentShowAnalysisPageIndex--;
                    binding.contentDisplayer.getmContentAdaper().toPage("analysis", currentShowAnalysisPageIndex, true);
                }
            }
        });
        binding.nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //下一页
                if (binding.questionBodyBtn.isSelected()) {
                    currentShowReplyPageIndex++;
                    binding.contentDisplayer.getmContentAdaper().toPage("reply", currentShowReplyPageIndex, false);
                } else if (binding.answerAnalysisBtn.isSelected()) {
                    currentShowAnalysisPageIndex++;
                    binding.contentDisplayer.getmContentAdaper().toPage("analysis", currentShowAnalysisPageIndex, true);
                }
            }
        });
    }

    @Override
    protected void loadData() {
        binding.contentDisplayer.getmContentAdaper().updateDataList("analysis" , parsedQuestionItem.analysisContentList);
        NetWorkManager
                .queryReply(examId, SpUtils.getUserId())
//                .queryReply("238")//TODO 测试用,删掉
                .subscribe(new Action1<List<QuestionReplySummary>>() {
                               @Override
                               public void call(List<QuestionReplySummary> studentReplies) {
                                   //可能会有多个回答,取最后一个,并解析
                                   QuestionReplySummary studentReply = studentReplies.get(studentReplies.size() - 1);
                                   studentReply.parsedContent();
                                   if (studentReply.getReplyScore() == 100){
                                       binding.buttomText.setText("正确");
                                       binding.buttomIcon.setImageResource(R.drawable.img_zhengque);
                                   }
                                   else if (studentReply.getReplyScore() == 0){
                                       binding.buttomText.setText("错误");
                                       binding.buttomIcon.setImageResource(R.drawable.img_cuowu);
                                   }
                                   else {
                                       binding.buttomText.setText("50%");
                                       binding.buttomIcon.setImageResource(R.drawable.img_bandui);
                                   }
                                   binding.contentDisplayer.getmContentAdaper().updateDataList("reply"
                                           , (ArrayList<Content_new>) studentReply.getParsedContentList());

                                   currentShowReplyPageIndex = 0;
                                   currentShowAnalysisPageIndex = 0;

                                   binding.contentDisplayer.getmContentAdaper().toPage("reply" , 0 , false);
                                   refreshPageBtns();
                                   binding.spendTimeTv.setText("用时 : " + studentReply.getReplyUseTime());
                               }
                           }
                        , new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                LogUtils.e("FH", "刷新答题情况失败" + throwable.getMessage());
                                throwable.printStackTrace();
                            }
                        });
    }

    public void refreshPageBtns() {
        if (binding.questionBodyBtn.isSelected()) {
            if (currentShowReplyPageIndex == 0) {
                binding.lastPageBtn.setVisibility(View.INVISIBLE);
            } else {
                binding.lastPageBtn.setVisibility(View.VISIBLE);
            }
            if ((currentShowReplyPageIndex + 1) >= binding.contentDisplayer.getmContentAdaper().getPageCount("reply")) {
                binding.nextPageBtn.setVisibility(View.INVISIBLE);
            } else {
                binding.nextPageBtn.setVisibility(View.VISIBLE);
            }
        } else if (binding.answerAnalysisBtn.isSelected()) {
            if (currentShowAnalysisPageIndex == 0) {
                binding.lastPageBtn.setVisibility(View.INVISIBLE);
            } else {
                binding.lastPageBtn.setVisibility(View.VISIBLE);
            }
            if ((currentShowAnalysisPageIndex + 1) >= binding.contentDisplayer.getmContentAdaper().getPageCount("analysis")) {
                binding.nextPageBtn.setVisibility(View.INVISIBLE);
            } else {
                binding.nextPageBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void refreshView() {
    }

    public void back(View view){
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
