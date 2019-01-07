package com.yougy.anwser;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.frank.etude.pageable.PageBtnBarAdapter;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.homework.bean.QuestionReplyDetail;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityAnswerRecordDetailBinding;
import com.yougy.view.CustomGridLayoutManager;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by FH on 2018/3/9.
 * 学生点击提交问答后学生查看答案和题干的界面也复用这个界面
 *
 * 学生问答记录某一条记录的详情界面（这个页面已经调整为AnswerRecordListDetailActivity，用来展示问答结果列表页面）
 */

public class AnswerRecordDetailActivity extends BaseActivity {
    ActivityAnswerRecordDetailBinding binding;
    ParsedQuestionItem parsedQuestionItem;
    int examId;
    private List<Content_new> textReplyList = new ArrayList<>();
    //当前展示的学生答案的页码(从0开始)
    private int currentShowReplyPageIndex = -1;
    //当前展示的解析的页码(从0开始)
    private int currentShowAnalysisPageIndex = -1;

    @Override
    protected void setContentView() {
        LogUtils.e(tag, "setContentView............");
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_answer_record_detail, null, false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    private void showJudgeOrSelect() {
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
                AnswerItemHolder holder = new AnswerItemHolder(view, textReplyList);
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
        }
        if ("已批改".equals(status)) {
            binding.buttomBtn.setVisibility(View.VISIBLE);
            binding.buttomIcon.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(status)) {
            binding.buttomBtn.setVisibility(View.VISIBLE);
            binding.buttomText.setVisibility(View.VISIBLE);
            binding.buttomBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        String bookName = getIntent().getStringExtra("bookName");
        binding.titleTv.setText(bookName == null ? "问答结果" : (bookName + "问答"));

        String startTimeStr = getIntent().getStringExtra("startTime");
        binding.startTimeTv.setText(startTimeStr == null ? "" : "问答开始时间:" + startTimeStr);

        binding.questionTypeTextview.setText(parsedQuestionItem.questionContentList.get(0).getExtraData().toString());
    }

    @Override
    protected void initLayout() {
        binding.pageBtnBar.setPageBarAdapter(new PageBtnBarAdapter(AnswerRecordDetailActivity.this) {
            @Override
            public int getPageBtnCount() {
                if (binding.questionBodyBtn.isSelected()) {
                    return binding.contentDisplayer.getContentAdapter().getPageCountBaseOnBaseLayer("question");
                } else if (binding.answerAnalysisBtn.isSelected()) {
                    return binding.contentDisplayer.getContentAdapter().getPageCountBaseOnBaseLayer("analysis");
                }
                return 0;
            }

            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn) {
                if (binding.questionBodyBtn.isSelected()) {
                    currentShowReplyPageIndex = btnIndex;
                    binding.contentDisplayer.toPage("question", currentShowReplyPageIndex, true);
                    //展示客观题reply中的学生答案（ABCD true false）
                    showJudgeOrSelect();
                } else if (binding.answerAnalysisBtn.isSelected()) {
                    currentShowAnalysisPageIndex = btnIndex;
                    binding.contentDisplayer.toPage("analysis", currentShowAnalysisPageIndex, true);
                }
            }
        });
        binding.contentDisplayer.setContentAdapter(new WriteableContentDisplayerAdapter() {
            @Override
            public void afterPageCountChanged(String typeKey) {
                if ((binding.questionBodyBtn.isSelected() && typeKey.equals("question"))
                        || (binding.answerAnalysisBtn.isSelected() && typeKey.equals("analysis"))) {
                    binding.pageBtnBar.refreshPageBar();
                }
            }

            @Override
            public void beforeToPage(String fromTypeKey, int fromPageIndex, String toTypeKey, int toPageIndex) {

            }

            @Override
            public void afterToPage(String fromTypeKey, int fromPageIndex, String toTypeKey, int toPageIndex) {
                if (toTypeKey.equals("question")) {
                    if (correctBaseLayerIndex(binding.contentDisplayer.getContentAdapter().getLayerPageCount("question", 0)
                            , binding.contentDisplayer.getContentAdapter().getLayerPageCount("question", 1))) {
                        binding.pageBtnBar.refreshPageBar();
                    }
                }
            }
        });
        binding.questionBodyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //题干
                binding.questionBodyBtn.setSelected(true);
                binding.answerAnalysisBtn.setSelected(false);
                showJudgeOrSelect();
                binding.questionTypeTextview.setText("题目类型 : " + parsedQuestionItem.questionContentList.get(0).getExtraData());
                binding.startTimeTv.setVisibility(View.VISIBLE);
                binding.spendTimeTv.setVisibility(View.VISIBLE);
                binding.pageBtnBar.setCurrentSelectPageIndex(currentShowReplyPageIndex);
                correctBaseLayerIndex(binding.contentDisplayer.getContentAdapter().getLayerPageCount("question", 0)
                        , binding.contentDisplayer.getContentAdapter().getLayerPageCount("question", 1));
                binding.pageBtnBar.refreshPageBar();
                if (currentShowReplyPageIndex != -1) {
                    binding.contentDisplayer.toPage("question", currentShowReplyPageIndex, true);
                }
            }
        });
        binding.answerAnalysisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //解答
                binding.questionBodyBtn.setSelected(false);
                binding.answerAnalysisBtn.setSelected(true);
                binding.rcvChooeseItem.setVisibility(View.GONE);
                binding.llChooeseItem.setVisibility(View.GONE);
                binding.questionTypeTextview.setText("解析");
                binding.startTimeTv.setVisibility(View.INVISIBLE);
                binding.spendTimeTv.setVisibility(View.INVISIBLE);
                binding.contentDisplayer.getContentAdapter().setPageCountBaseLayerIndex(0);
                binding.pageBtnBar.setCurrentSelectPageIndex(currentShowAnalysisPageIndex);
                binding.pageBtnBar.refreshPageBar();
                if (currentShowAnalysisPageIndex != -1) {
                    binding.contentDisplayer.toPage("analysis", currentShowAnalysisPageIndex, true);
                }
            }
        });
    }

    @Override
    protected void loadData() {
        NetWorkManager.queryReplyDetail(examId, null, SpUtils.getUserId() + "")
                .subscribe(new Action1<List<QuestionReplyDetail>>() {
                    @Override
                    public void call(List<QuestionReplyDetail> questionReplyDetails) {
                        QuestionReplyDetail questionReplyDetail = questionReplyDetails.get(questionReplyDetails.size() - 1);
                        //显示选择或判断的选项
                        textReplyList.clear();
                        List<Content_new> originContentList = questionReplyDetail.getParsedReplyContentList();
                        List<Content_new> imageContentList = new ArrayList<Content_new>();
                        for (Content_new contentNew : originContentList) {
                            if (contentNew != null) {
                                if (contentNew.getType() == Content_new.Type.TEXT) {
                                    textReplyList.add(contentNew);
                                } else {
                                    imageContentList.add(contentNew);
                                }
                            } else {
                                imageContentList.add(null);
                            }
                        }
                        AnswerRecordDetailActivity.this.showJudgeOrSelect();

                        //显示分数
                        int replyScore = questionReplyDetail.getReplyScore();
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
                        //设置1,2层不可写
                        binding.contentDisplayer.getLayer1().setIntercept(true);
                        binding.contentDisplayer.getLayer2().setIntercept(true);
                        //填充数据
                        binding.contentDisplayer.getContentAdapter().updateDataList("analysis", 0, questionReplyDetail.getParsedQuestionItem().analysisContentList);
                        binding.contentDisplayer.getContentAdapter().updateDataList("question", 0, questionReplyDetail.getParsedQuestionItem().questionContentList);
                        if (questionReplyDetail.getParsedReplyCommentList() != null && questionReplyDetail.getParsedReplyCommentList().size() != 0) {
                            binding.contentDisplayer.getContentAdapter().updateDataList("question", 2, questionReplyDetail.getParsedReplyCommentList());
                        } else {
                            binding.contentDisplayer.getContentAdapter().deleteDataList("question", 2);
                        }
                        binding.contentDisplayer.getContentAdapter().updateDataList("question", 1, imageContentList);

                        //设置用时
                        binding.spendTimeTv.setText("用时 : " + questionReplyDetail.getReplyUseTime());

                        binding.questionBodyBtn.performClick();
                    }
                });
    }

    /**
     * 校正基准层,本来应该以第1层作为基准层,但是考虑到第1层有的时候可能不如第0层页数多,此时需要校正基准层为第0层.
     * 如果校正了基准层为第0层,则返回true,否则返回false.
     */
    private boolean correctBaseLayerIndex(int layer0PageCount, int layer1PageCount) {
        if (layer0PageCount > layer1PageCount) {
            binding.contentDisplayer.getContentAdapter().setPageCountBaseLayerIndex(0);
            return true;
        } else {
            binding.contentDisplayer.getContentAdapter().setPageCountBaseLayerIndex(1);
            return false;
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
