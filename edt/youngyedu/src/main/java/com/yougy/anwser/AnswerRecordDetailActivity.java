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
import com.yougy.homework.bean.QuestionReplyDetail;
import com.yougy.homework.bean.QuestionReplySummary;
import com.yougy.message.ListUtil;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityAnswerRecordDetailBinding;

import org.litepal.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by FH on 2018/3/9.
 * 学生问答记录某一条记录的详情界面
 */

public class AnswerRecordDetailActivity extends BaseActivity {
    ActivityAnswerRecordDetailBinding binding;
    ParsedQuestionItem parsedQuestionItem;
    int examId;

    //当前展示的学生答案的页码(从0开始)
    private int currentShowReplyPageIndex = 0;
    //当前展示的解析的页码(从0开始)
    private int currentShowAnalysisPageIndex = 0;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_answer_record_detail, null, false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    @Override
    protected void init() {
        examId = getIntent().getIntExtra("examId", -1);
        if (examId == -1) {
            ToastUtil.showCustomToast(getApplicationContext(), "exam获取失败!");
            return;
        }
        parsedQuestionItem = getIntent().getParcelableExtra("question");
        String status = getIntent().getStringExtra("status");
        if ("批改中".equals(status)) {
            binding.statusHintTv.setVisibility(View.VISIBLE);
        } else if ("已批改".equals(status)) {
            binding.buttomBtn.setVisibility(View.VISIBLE);
        }
        LogUtils.e(tag, "init question : " + parsedQuestionItem.questionContentList);
        LogUtils.e(tag, "init analysis : " + parsedQuestionItem.analysisContentList);
        binding.titleTv.setText(getIntent().getStringExtra("bookName") + "问答");
        binding.startTimeTv.setText("问答开始时间:" + getIntent().getStringExtra("startTime"));
        binding.questionTypeTextview.setText(parsedQuestionItem.questionContentList.get(0).getExtraData().toString());
    }

    @Override
    protected void initLayout() {
        binding.contentDisplayer.setContentAdapter(new WriteableContentDisplayerAdapter() {
            @Override
            public void afterPageCountChanged(String typeKey) {
                refreshPageBtns();
            }

            @Override
            public void beforeToPage(String fromTypeKey, int fromPageIndex, String toTypeKey, int toPageIndex) {

            }

            @Override
            public void afterToPage(String fromTypeKey, int fromPageIndex, String toTypeKey, int toPageIndex) {

            }
        });
        binding.questionBodyBtn.setSelected(true);
        binding.questionBodyBtn.setOnClickListener(v -> {
            //题干
            if (!binding.questionBodyBtn.isSelected()) {
                binding.contentDisplayer.getContentAdapter().setPageCountBaseLayerIndex(1);
                binding.questionBodyBtn.setSelected(true);
                binding.answerAnalysisBtn.setSelected(false);
                binding.contentDisplayer.toPage("question", currentShowReplyPageIndex, false);
            }
        });
        binding.answerAnalysisBtn.setOnClickListener(v -> {
            //解答
            if (binding.questionBodyBtn.isSelected()) {
                binding.contentDisplayer.getContentAdapter().setPageCountBaseLayerIndex(0);
                binding.questionBodyBtn.setSelected(false);
                binding.answerAnalysisBtn.setSelected(true);
                binding.contentDisplayer.toPage("analysis", currentShowAnalysisPageIndex, true);
            }
        });
        binding.lastPageBtn.setOnClickListener(v -> {
            //上一页
            if (binding.questionBodyBtn.isSelected()) {
                currentShowReplyPageIndex--;
                binding.contentDisplayer.toPage("question", currentShowReplyPageIndex, false);
            } else if (binding.answerAnalysisBtn.isSelected()) {
                currentShowAnalysisPageIndex--;
                binding.contentDisplayer.toPage("analysis", currentShowAnalysisPageIndex, true);
            }
        });
        binding.nextPageBtn.setOnClickListener(v -> {
            //下一页
            if (binding.questionBodyBtn.isSelected()) {
                currentShowReplyPageIndex++;
                binding.contentDisplayer.toPage("question", currentShowReplyPageIndex, false);
            } else if (binding.answerAnalysisBtn.isSelected()) {
                currentShowAnalysisPageIndex++;
                binding.contentDisplayer.toPage("analysis", currentShowAnalysisPageIndex, true);
            }
        });
    }

    @Override
    protected void loadData() {
        NetWorkManager.queryReplyDetail(examId, null, SpUtils.getUserId() + "")
                .subscribe(questionReplyDetails -> {
                    QuestionReplyDetail questionReplyDetail = questionReplyDetails.get(questionReplyDetails.size() - 1);
                    int replyScore = questionReplyDetail.getReplyScore();
                    binding.buttomText.setText("");
                    switch (replyScore) {
                        case 100:
                            binding.buttomIcon.setImageResource(R.drawable.img_zhengque);
                            break;
                        case 0:
                            binding.buttomIcon.setImageResource(R.drawable.img_cuowu);
                            break;
                        default:
                            binding.buttomIcon.setImageResource(R.drawable.img_bandui);
                            break;
                    }
                    binding.contentDisplayer.getContentAdapter().updateDataList("analysis", 0, questionReplyDetail.getParsedQuestionItem().analysisContentList);
                    binding.contentDisplayer.getContentAdapter().updateDataList("question", 0, questionReplyDetail.getParsedQuestionItem().questionContentList);

                    if (questionReplyDetail.getParsedReplyCommentList() != null && questionReplyDetail.getParsedReplyCommentList().size() != 0) {
                        binding.contentDisplayer.getContentAdapter().updateDataList("question", 2, questionReplyDetail.getParsedReplyCommentList());
                    } else {
                        binding.contentDisplayer.getContentAdapter().deleteDataList("question", 2);
                    }

                    binding.contentDisplayer.getContentAdapter().updateDataList("question", 1, questionReplyDetail.getParsedReplyContentList());
                    currentShowReplyPageIndex = 0;
                    currentShowAnalysisPageIndex = 0;
                    binding.contentDisplayer.toPage("question", 0, false);
                    refreshPageBtns();
                    binding.spendTimeTv.setText("用时 : " + questionReplyDetail.getReplyUseTime());
                    binding.contentDisplayer.getLayer1().setIntercept(true);
                    binding.contentDisplayer.getLayer2().setIntercept(true);
                });
    }

    public void refreshPageBtns() {
        if (binding.questionBodyBtn.isSelected()) {
            if (currentShowReplyPageIndex == 0) {
                binding.lastPageBtn.setVisibility(View.INVISIBLE);
            } else {
                binding.lastPageBtn.setVisibility(View.VISIBLE);
            }
            if ((currentShowReplyPageIndex + 1) >= binding.contentDisplayer.getContentAdapter().getPageCountBaseOnBaseLayer("reply")) {
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
            if ((currentShowAnalysisPageIndex + 1) >= binding.contentDisplayer.getContentAdapter().getPageCountBaseOnBaseLayer("analysis")) {
                binding.nextPageBtn.setVisibility(View.INVISIBLE);
            } else {
                binding.nextPageBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void refreshView() {
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
        binding.contentDisplayer.clearCache();
        Runtime.getRuntime().gc();
    }
}
