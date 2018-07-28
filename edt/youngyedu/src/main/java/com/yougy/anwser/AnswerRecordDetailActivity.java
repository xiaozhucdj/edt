package com.yougy.anwser;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.yougy.ui.activity.databinding.ItemAnswerChooseGridviewBinding;
import com.yougy.view.CustomGridLayoutManager;
import com.zhy.autolayout.utils.AutoUtils;

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
    private List<Content_new> textReplyList = new ArrayList<>();
    //当前展示的学生答案的页码(从0开始)
    private int currentShowReplyPageIndex = 0;
    //当前展示的解析的页码(从0开始)
    private int currentShowAnalysisPageIndex = 0;

    @Override
    protected void setContentView() {
        LogUtils.e(tag,"setContentView............");
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_answer_record_detail, null, false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    private void showJudgeOrSelect(){
        String questionType = (String) parsedQuestionItem.questionContentList.get(0).getExtraData();
        if ("选择".equals(questionType)) {
            binding.rcvChooeseItem.setVisibility(View.VISIBLE);
            binding.llChooeseItem.setVisibility(View.GONE);
            setChooeseResult();
            //刷新当前选择结果的reciv
            if (binding.rcvChooeseItem.getAdapter() != null) {
                binding.rcvChooeseItem.getAdapter().notifyDataSetChanged();
            }
        } else if ("判断".equals(questionType)) {
            binding.rcvChooeseItem.setVisibility(View.GONE);
            binding.llChooeseItem.setVisibility(View.VISIBLE);
            if (textReplyList.size() > 0) {
                String replyResult = textReplyList.get(0).getValue();
                if ("true".equals(replyResult)) {
                    binding.rbRight.setChecked(true);
                    binding.rbError.setChecked(false);
                } else {
                    binding.rbRight.setChecked(false);
                    binding.rbError.setChecked(true);
                }
            }
            binding.rbRight.setClickable(false);
            binding.rbError.setClickable(false);
        } else {
            binding.rcvChooeseItem.setVisibility(View.GONE);
            binding.llChooeseItem.setVisibility(View.GONE);
        }
    }

    /**
     * 设置选择题的结果界面
     */
    private void setChooeseResult() {
        List<ParsedQuestionItem.Answer> chooeseAnswerList = parsedQuestionItem.answerList;

        binding.rcvChooeseItem.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(AnswerRecordDetailActivity.this).inflate(R.layout.item_answer_choose_gridview, parent, false);
                AutoUtils.auto(view);
                AnswerItemHolder holder = new AnswerItemHolder(view,textReplyList);
                holder.setChooeseStyle(chooeseAnswerList.size());
                return holder;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ParsedQuestionItem.Answer answer = chooeseAnswerList.get(position);
                ((AnswerItemHolder) holder).setAnswer(answer);
            }

            @Override
            public int getItemCount() {
                if (chooeseAnswerList != null) {
                    return chooeseAnswerList.size();
                } else {
                    return 0;
                }
            }
        });
        CustomGridLayoutManager gridLayoutManager = new CustomGridLayoutManager(this, chooeseAnswerList.size());
        gridLayoutManager.setScrollEnabled(false);
        binding.rcvChooeseItem.setLayoutManager(gridLayoutManager);
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
                binding.questionTypeTextview.setText("题目类型 : " + parsedQuestionItem.questionContentList.get(0).getExtraData());
                binding.startTimeTv.setVisibility(View.VISIBLE);
                binding.spendTimeTv.setVisibility(View.VISIBLE);
                binding.rcvChooeseItem.setVisibility(View.VISIBLE);
                binding.llChooeseItem.setVisibility(View.VISIBLE);
                binding.contentDisplayer.getContentAdapter().setPageCountBaseLayerIndex(1);
                binding.questionBodyBtn.setSelected(true);
                binding.answerAnalysisBtn.setSelected(false);
                binding.contentDisplayer.toPage("question", currentShowReplyPageIndex, false);
            }
        });
        binding.answerAnalysisBtn.setOnClickListener(v -> {
            //解答
            if (binding.questionBodyBtn.isSelected()) {
                binding.questionTypeTextview.setText("解析");
                binding.startTimeTv.setVisibility(View.GONE);
                binding.spendTimeTv.setVisibility(View.GONE);
                binding.rcvChooeseItem.setVisibility(View.GONE);
                binding.llChooeseItem.setVisibility(View.GONE);
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
                    textReplyList.clear();
                    List<Content_new> contentList = questionReplyDetail.getParsedReplyContentList();
                    for (Content_new contentNew : contentList) {
                        LogUtils.e(tag,"content new type is : " + contentNew.getType()+",value is : " + contentNew.getValue());
                        if (contentNew.getType() == Content_new.Type.TEXT) {
                            textReplyList.add(contentNew);
                        }
                    }
                    showJudgeOrSelect();
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
