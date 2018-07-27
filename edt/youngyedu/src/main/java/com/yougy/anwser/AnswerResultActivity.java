package com.yougy.anwser;

import android.databinding.DataBindingUtil;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.frank.etude.pageable.PageBtnBarAdapter;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.new_network.RxResultHelper;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.homework.CheckHomeWorkActivity;
import com.yougy.homework.bean.QuestionReplySummary;
import com.yougy.message.ListUtil;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityAnswerResultBinding;
import com.yougy.ui.activity.databinding.ItemAnswerChooseGridviewBinding;
import com.yougy.view.CustomGridLayoutManager;
import com.zhy.autolayout.utils.AutoUtils;

import org.litepal.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by FH on 2017/9/6.
 * 学生点击提交问答后学生查看答案和题干的界面.
 */

public class AnswerResultActivity extends BaseActivity {
    ActivityAnswerResultBinding binding;
    String replyId;
    ParsedQuestionItem parsedQuestionItem;
    String questionType;
    int currentShowReplyPageIndex = 0;
    int currentShowAnalysisPageIndex = 0;
    private List<Content_new> textReplyList = new ArrayList<>();

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_answer_result, null, false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    /**
     * 设置选择题的结果界面
     */
    private void setChooeseResult() {
        List<ParsedQuestionItem.Answer> chooeseAnswerList = parsedQuestionItem.answerList;
        binding.rcvChooeseItem.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(AnswerResultActivity.this).inflate(R.layout.item_answer_choose_gridview, parent, false);
                AutoUtils.auto(view);
                AnswerItemHolder holder = new AnswerItemHolder(view);
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

    public class AnswerItemHolder extends RecyclerView.ViewHolder {
        ItemAnswerChooseGridviewBinding itemBinding;
        ParsedQuestionItem.Answer answer;

        AnswerItemHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }

        public AnswerItemHolder setAnswer(ParsedQuestionItem.Answer answer) {
            this.answer = answer;
            if (answer instanceof ParsedQuestionItem.TextAnswer) {
                itemBinding.textview.setText(((ParsedQuestionItem.TextAnswer) answer).text);
                //选择题选择的结果
                ArrayList<String> checkedAnswerList = new ArrayList<String>();
                LogUtils.e(tag,"text reply list's size is : " + textReplyList.size());
                for (int i = 0; i < textReplyList.size(); i++) {
                    String replyResult = textReplyList.get(i).getValue();
                    LogUtils.e(tag,"text reply result is : " + replyResult);
                    checkedAnswerList.add(replyResult);
                }
                if (ListUtil.conditionalContains(checkedAnswerList, nodeInList -> nodeInList.equals(((ParsedQuestionItem.TextAnswer) answer).text))) {
                    LogUtils.e(tag,"selected true .............. ");
                    itemBinding.checkbox.setSelected(true);
                    itemBinding.textview.setSelected(true);
                } else {
                    LogUtils.e(tag,"selected false .............. ");
                    itemBinding.textview.setSelected(false);
                    itemBinding.checkbox.setSelected(false);
                }
            } else {
                itemBinding.textview.setText("格式错误");
                itemBinding.checkbox.setSelected(false);
            }
            return this;
        }

        private void setChooeseStyle(int size) {
            int rid;
            switch (size) {
                case 2:
                    rid = R.drawable.btn_check_liangdaan;
                    break;
                case 3:
                    rid = R.drawable.btn_check_sandaan;
                    break;
                case 4:
                    rid = R.drawable.btn_check_sidaan;
                    break;
                case 5:
                    rid = R.drawable.btn_check_wudaan;
                    break;
                default:
                    rid = R.drawable.btn_check_liudaan;
                    break;
            }
            itemBinding.checkbox.setBackgroundResource(rid);
        }
    }

    @Override
    protected void init() {
        Parcelable question = getIntent().getParcelableExtra("question");
        replyId = getIntent().getStringExtra("replyId");
        if (question == null) {
            ToastUtil.showCustomToast(this, "题目内容获取失败");
            finish();
            return;
        }
        if (TextUtils.isEmpty(replyId)) {
            ToastUtil.showCustomToast(this, "学生回答获取失败");
            finish();
            return;
        }
        parsedQuestionItem = (ParsedQuestionItem) question;
        List<Content_new> anserList = parsedQuestionItem.answerContentList;
        questionType = (String) parsedQuestionItem.questionContentList.get(0).getExtraData();
    }

    @Override
    protected void initLayout() {
        binding.questionBodyBtn.setOnClickListener(v -> {
            binding.questionBodyBtn.setSelected(true);
            binding.answerAnalysisBtn.setSelected(false);
            showJudgeOrSelect();
            binding.contentDisplayer.getContentAdapter().setPageCountBaseLayerIndex(1);
            refreshViewSafe();
            binding.pageBtnBar.refreshPageBar();
        });
        binding.answerAnalysisBtn.setOnClickListener(v -> {
            binding.questionBodyBtn.setSelected(false);
            binding.answerAnalysisBtn.setSelected(true);
            binding.rcvChooeseItem.setVisibility(View.GONE);
            binding.llChooeseItem.setVisibility(View.GONE);
            binding.contentDisplayer.getContentAdapter().setPageCountBaseLayerIndex(0);
            refreshViewSafe();
            binding.pageBtnBar.refreshPageBar();
        });
        binding.contentDisplayer.setContentAdapter(new WriteableContentDisplayerAdapter() {
            @Override
            public void afterPageCountChanged(String typeKey) {
                binding.pageBtnBar.refreshPageBar();
            }

            @Override
            public void beforeToPage(String fromTypeKey, int fromPageIndex, String toTypeKey, int toPageIndex) {

            }

            @Override
            public void afterToPage(String fromTypeKey, int fromPageIndex, String toTypeKey, int toPageIndex) {

            }
        });
        binding.pageBtnBar.setPageBarAdapter(new PageBtnBarAdapter(this) {
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
            public String getPageText(int index) {
                return String.valueOf(index + 1);
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

    @Override
    protected void loadData() {
        NetWorkManager.queryReply(null, null, replyId).subscribe(replySummaries -> {
            if (replySummaries.size() == 0) {
                ToastUtil.showCustomToast(getApplicationContext(), "获取学生回答失败");
                finish();
                return;
            }
            List<Content_new> contentList = replySummaries.get(0).getParsedContentList();
            textReplyList.clear();
            for (Content_new contentNew : contentList) {
                if (contentNew.getType() == Content_new.Type.TEXT) {
                    textReplyList.add(contentNew);
                }
            }
            binding.contentDisplayer.getContentAdapter().updateDataList("question", 1
                    , ListUtil.conditionalSubList(replySummaries.get(0).getParsedContentList(), nodeInList -> nodeInList.getType() == Content_new.Type.IMG_URL));
            binding.contentDisplayer.getContentAdapter().updateDataList("analysis", 0, parsedQuestionItem.analysisContentList);
            binding.contentDisplayer.getContentAdapter().updateDataList("question", 0, parsedQuestionItem.questionContentList);
            binding.contentDisplayer.getLayer1().setIntercept(true);
            binding.contentDisplayer.getLayer2().setIntercept(true);
            binding.questionBodyBtn.performClick();
        }, throwable -> {
            ToastUtil.showCustomToast(AnswerResultActivity.this, "获取学生回答失败");
            throwable.printStackTrace();
            finish();
        });
    }

    @Override
    protected void refreshView() {
        if (binding.questionBodyBtn.isSelected()) {
            binding.questionTypeTextview.setText("题目类型 : " + questionType);
            binding.contentDisplayer.toPage("question", currentShowReplyPageIndex, false);
        } else if (binding.answerAnalysisBtn.isSelected()) {
            binding.questionTypeTextview.setText("解析");
            binding.contentDisplayer.toPage("analysis", currentShowAnalysisPageIndex, true);
        }
    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
        binding.contentDisplayer.clearCache();
        Runtime.getRuntime().gc();
    }

}
