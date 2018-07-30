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
        int currentShowReplyPageIndex = -1;
        int currentShowAnalysisPageIndex = -1;
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
            questionType = (String) parsedQuestionItem.questionContentList.get(0).getExtraData();
        }

        @Override
        protected void initLayout() {
            binding.questionBodyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.questionBodyBtn.setSelected(true);
                    binding.answerAnalysisBtn.setSelected(false);
                    AnswerResultActivity.this.showJudgeOrSelect();
                    binding.questionTypeTextview.setText("题目类型 : " + questionType);
                    binding.pageBtnBar.setCurrentSelectPageIndex(currentShowReplyPageIndex);
                    correctBaseLayerIndex(binding.contentDisplayer.getContentAdapter().getLayerPageCount("question" , 0)
                            , binding.contentDisplayer.getContentAdapter().getLayerPageCount("question" , 1));
                    binding.pageBtnBar.refreshPageBar();
                    if (currentShowReplyPageIndex != -1){
                        binding.contentDisplayer.toPage("question", currentShowReplyPageIndex, true);
                    }
                }
            });
            binding.answerAnalysisBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.questionBodyBtn.setSelected(false);
                    binding.answerAnalysisBtn.setSelected(true);
                    binding.rcvChooeseItem.setVisibility(View.GONE);
                    binding.llChooeseItem.setVisibility(View.GONE);
                    binding.questionTypeTextview.setText("解析");
                    binding.contentDisplayer.getContentAdapter().setPageCountBaseLayerIndex(0);
                    binding.pageBtnBar.setCurrentSelectPageIndex(currentShowAnalysisPageIndex);
                    binding.pageBtnBar.refreshPageBar();
                    if (currentShowAnalysisPageIndex != -1){
                        binding.contentDisplayer.toPage("analysis", currentShowAnalysisPageIndex, true);
                    }
                }
            });
            binding.contentDisplayer.setContentAdapter(new WriteableContentDisplayerAdapter() {
                @Override
                public void afterPageCountChanged(String typeKey) {
                    if ((binding.questionBodyBtn.isSelected() && typeKey.equals("question"))
                            || (binding.answerAnalysisBtn.isSelected() && typeKey.equals("analysis"))){
                        binding.pageBtnBar.refreshPageBar();
                    }
                }

                @Override
                public void beforeToPage(String fromTypeKey, int fromPageIndex, String toTypeKey, int toPageIndex) {

                }

                @Override
                public void afterToPage(String fromTypeKey, int fromPageIndex, String toTypeKey, int toPageIndex) {
                    if (toTypeKey.equals("question")){
                        if (correctBaseLayerIndex(binding.contentDisplayer.getContentAdapter().getLayerPageCount("question" , 0)
                                , binding.contentDisplayer.getContentAdapter().getLayerPageCount("question" , 1))){
                            binding.pageBtnBar.refreshPageBar();
                        }
                    }
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
            NetWorkManager.queryReply(null, null, replyId).subscribe(new Action1<List<QuestionReplySummary>>() {
                @Override
                public void call(List<QuestionReplySummary> replySummaries) {
                    if (replySummaries.size() == 0) {
                        ToastUtil.showCustomToast(AnswerResultActivity.this.getApplicationContext(), "获取学生回答失败");
                        AnswerResultActivity.this.finish();
                        return;
                    }
                    List<Content_new> contentList = replySummaries.get(0).getParsedContentList();
                    List<Content_new> imgContentList = new ArrayList<Content_new>();
                    textReplyList.clear();
                    for (Content_new contentNew : contentList) {
                        if (contentNew.getType() == Content_new.Type.TEXT) {
                            textReplyList.add(contentNew);
                        }
                        else if (contentNew.getType() == Content_new.Type.IMG_URL) {
                            imgContentList.add(contentNew);
                        }
                    }
                    binding.contentDisplayer.getLayer1().setIntercept(true);
                    binding.contentDisplayer.getLayer2().setIntercept(true);

                    binding.contentDisplayer.getContentAdapter().updateDataList("analysis", 0, parsedQuestionItem.analysisContentList);
                    binding.contentDisplayer.getContentAdapter().updateDataList("question", 0, parsedQuestionItem.questionContentList);
                    binding.contentDisplayer.getContentAdapter().updateDataList("question", 1 , imgContentList);

                    binding.questionBodyBtn.performClick();
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    ToastUtil.showCustomToast(AnswerResultActivity.this, "获取学生回答失败");
                    throwable.printStackTrace();
                    AnswerResultActivity.this.finish();
                }
            });
        }

        /**
         * 校正基准层,本来应该以第1层作为基准层,但是考虑到第1层有的时候可能不如第0层页数多,此时需要校正基准层为第0层.
         * 如果校正了基准层为第0层,则返回true,否则返回false.
         */
        private boolean correctBaseLayerIndex(int layer0PageCount , int layer1PageCount){
            if (layer0PageCount > layer1PageCount){
                binding.contentDisplayer.getContentAdapter().setPageCountBaseLayerIndex(0);
                return true;
            }
            else {
                binding.contentDisplayer.getContentAdapter().setPageCountBaseLayerIndex(1);
                return false;
            }
        }

        @Override
        protected void refreshView() {
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
