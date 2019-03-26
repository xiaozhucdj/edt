package com.yougy.homework;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.homework.bean.HomeworkSummarySumInfo;
import com.yougy.homework.bean.QuestionReplySummary;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityCheckedHomeworkDetailBinding;
import com.yougy.ui.activity.databinding.ItemQuestionGridview2Binding;
import com.yougy.ui.activity.databinding.ItemQuestionGridviewBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by FH on 2017/11/6.
 * <p>
 * 单个已批改作业整体概览界面,统计正确率,每道题的对错,等等
 * <p>
 * modify by zhangyc on 2018/0704  添加计分作业显示逻辑
 */

public class CheckedHomeworkOverviewActivity extends HomeworkBaseActivity {
    ActivityCheckedHomeworkDetailBinding binding;
    ArrayList<QuestionReplySummary> replyList = new ArrayList<QuestionReplySummary>();
    HashMap<Integer, Integer> commentatorValidMap = new HashMap<Integer, Integer>();
    ArrayList<HashMap<Integer, Integer>> scoreMapList = new ArrayList<HashMap<Integer, Integer>>();

    int examId;
    String examName;

    private boolean isScoring = false;// 是否计分作业
    private int examTotalPoints;//exam 总分
    private double scoreAvg;//平均正确率
    private int itemCount;//总题数
    private int correctCount;//正确的题目
    private int currentSelectCommentIndex = 0;
    private int isStudentCheck = 0;
    private double score;//总得分

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_checked_homework_detail, null, false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    @Override
    protected void init() {
        examId = getIntent().getIntExtra("examId", -1);
        if (examId == -1) {
            ToastUtil.showCustomToast(getApplicationContext(), "examId 为空");
            finish();
        }
        examName = getIntent().getStringExtra("examName");
        isScoring = getIntent().getBooleanExtra("isScoring", false);
        examTotalPoints = getIntent().getIntExtra("getExamTotalPoints", 0);
        itemCount = getIntent().getIntExtra("getItemCount", 0);
        isStudentCheck = getIntent().getIntExtra("isStudentCheck", 0);
        binding.titleTv.setText(examName);
    }

    @Override
    protected void initLayout() {
        if (isScoring) {
            binding.mainRecyclerview.setMaxItemNumInOnePage(24);
        } else {
            binding.mainRecyclerview.setMaxItemNumInOnePage(36);
        }
        binding.mainRecyclerview.setLayoutManager(new GridLayoutManager(getApplicationContext(), 6) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.mainRecyclerview.setAdapter(new PageableRecyclerView.Adapter<MyHolder>() {
            @Override
            public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                //计分
                ItemQuestionGridviewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.item_question_gridview, parent, false);
                //不计分
                ItemQuestionGridview2Binding binding2 = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.item_question_gridview2, parent, false);
                if (isScoring) {
                    return new MyHolder(binding);
                }
                return new MyHolder(binding2);
            }

            @Override
            public void onBindViewHolder(MyHolder holder, int position) {
                if (isScoring) {
                    holder.itemBinding.textview.setText("" + (position + 1));
                } else {
                    holder.itemBinding2.textview.setText("" + (position + 1));
                }
                Integer currentCommentator = ((Integer) commentatorValidMap.keySet().toArray()[currentSelectCommentIndex]);
                Integer score = scoreMapList.get(position).get(currentCommentator);
                if (score == null) {
                    score = scoreMapList.get(position).get(1);
                }
                holder.setData(score, replyList.get(position).getReplyItemWeight());
            }

            @Override
            public int getItemCount() {
                return scoreMapList.size();
            }
        });
        binding.mainRecyclerview.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.mainRecyclerview.getRealRcyView()) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                MyHolder holder = (MyHolder) vh;
                Intent intent = new Intent(CheckedHomeworkOverviewActivity.this, CheckHomeWorkActivity.class);
                intent.putExtra("examId", examId);
                intent.putExtra("toShowPosition", holder.getPosition());
                intent.putExtra("isCheckOver", true);
                intent.putExtra("isStudentLook", true);
                intent.putExtra("isStudentCheck", isStudentCheck);
                intent.putExtra("HOMEWORKID", getIntent().getIntExtra("HOMEWORKID", -1));
                intent.putExtra("replyCreator", (long) SpUtils.getUserId());
                intent.putExtra("replyCommentator",
                        ((Integer) commentatorValidMap.keySet().toArray()[currentSelectCommentIndex]).toString());
                startActivity(intent);

            }
        });
    }

    @Override
    protected void loadData() {
        NetWorkManager.queryReply(examId, SpUtils.getUserId(), null)
                .subscribe(new Action1<List<QuestionReplySummary>>() {
                    @Override
                    public void call(List<QuestionReplySummary> replySummaries) {
                        replyList.clear();
                        replyList.addAll(replySummaries);
                        commentatorValidMap.clear();
                        scoreMapList.clear();
                        //scoreList的结构是:不同的人对一份作业的批改分数的列表再组成一个列表
                        //批改分数列表的首个元素是批改者的id.
                        //形如于{{批改者id1 , 题1批改结果1,题2批改结果1...} , {批改者id2 , 题1批改结果2,题2批改结果2...} ...}
                        for (int i = 0; i < replyList.size(); i++) {
                            QuestionReplySummary replySummary = replyList.get(i);
                            //学生批改结果(互评,含分数)
                            List<QuestionReplySummary.ReplyCommentedBean> replyCommented = replySummary.getReplyCommented();
                            //教师批改结果(不含分数)
                            List<Object> replyComment = replySummary.getReplyComment();
                            //教师批改的分数,或者客观题自动批改的分数,或者如果没有自动批也没有老师评,就是学生批改的分数,如果学生也没评,就是-1.
                            int tempScore = replySummary.getReplyScore();
                            int tempCommentator = replySummary.getReplyCommentator();
                            HashMap<Integer, Integer> replyScoreMap = new HashMap<Integer, Integer>();
                            for (int j = 0; j < replyCommented.size(); j++) {
                                QuestionReplySummary.ReplyCommentedBean replyCommentedBean = replyCommented.get(j);
                                if (replyCommentedBean.getReplyCommentator() > 1) {
                                    if (replyCommentedBean.getReplyScore() >= 0) {
                                        replyScoreMap.put(replyCommentedBean.getReplyCommentator(), replyCommentedBean.getReplyScore());
                                        if (commentatorValidMap.get(replyCommentedBean.getReplyCommentator()) == null) {
                                            commentatorValidMap.put(replyCommentedBean.getReplyCommentator(), 1);
                                        }
                                    } else {
                                        commentatorValidMap.put(replyCommentedBean.getReplyCommentator(), -1);
                                    }
                                }
                            }
                            if (tempScore >= 0) {
                                if (replyComment.size() > 0) {
                                    replyScoreMap.put(tempCommentator, tempScore);
                                    commentatorValidMap.put(tempCommentator, 0);
                                } else {
                                    replyScoreMap.put(1, tempScore);
                                    commentatorValidMap.put(1, 1);
                                }
                            }
                            scoreMapList.add(replyScoreMap);
                        }

                        Iterator<Integer> keyIterator = commentatorValidMap.keySet().iterator();
                        while (keyIterator.hasNext()) {
                            Integer commentator = keyIterator.next();
                            if (commentatorValidMap.get(commentator) == 0) {
                                boolean allChecked = true;
                                for (int i = 0; i < scoreMapList.size(); i++) {
                                    Integer score = scoreMapList.get(i).get(commentator);
                                    if (score == null || score == -1) {
                                        score = scoreMapList.get(i).get(1);
                                        if (score == null || score == -1) {
                                            allChecked = false;
                                            break;
                                        }
                                    }
                                }
                                if (!allChecked) {
                                    keyIterator.remove();
//                                    commentatorValidMap.remove(commentator);
                                }
                            } else if (commentatorValidMap.get(commentator) == -1) {
                                keyIterator.remove();
//                                commentatorValidMap.remove(commentator);
                            }
                        }
                        if (commentatorValidMap.size() > 1) {
                            commentatorValidMap.remove(1);
                        }

                        refreshCommentChooseBar();
                        binding.mainRecyclerview.notifyDataSetChanged();
                        binding.questionNumTv.setText("习题数量 : " + replyList.size());
                        long allUseTime = 0;
                        for (QuestionReplySummary questionReplySummary : replyList) {
                            long detailUseTime = DateUtils.transformToTime_second(questionReplySummary.getReplyUseTime());
                            allUseTime += detailUseTime;
                        }
                        binding.timeTv.setText(DateUtils.converLongTimeToString(allUseTime * 1000));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        ToastUtil.showCustomToast(getApplicationContext(), "获取每题分数数据失败");
                        finish();
                    }
                });
        NetWorkManager.sumReplyStudent(examId, SpUtils.getUserId())
                .subscribe(new Action1<List<HomeworkSummarySumInfo>>() {
                    @Override
                    public void call(List<HomeworkSummarySumInfo> homeworkSummarySumInfos) {
                        if (homeworkSummarySumInfos == null || homeworkSummarySumInfos.size() == 0) {
                            ToastUtil.showCustomToast(getApplicationContext(), "获取总分失败,查不到数据");
                        } else {
                            scoreAvg = homeworkSummarySumInfos.get(0).getScoreAvg();
                            score = homeworkSummarySumInfos.get(0).getScore();
                            correctCount = homeworkSummarySumInfos.get(0).getCorrectCount();
                            refreshCircleProgressBar();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        ToastUtil.showCustomToast(getApplicationContext(), "获取总分失败");
                        finish();
                    }
                });
    }


    /**
     * 刷新批改结果选择条
     */
    private void refreshCommentChooseBar() {
        if (currentSelectCommentIndex + 1 > commentatorValidMap.keySet().size()) {
            currentSelectCommentIndex = commentatorValidMap.keySet().size() - 1;
        }
        if (currentSelectCommentIndex <= 0) {
            binding.lastCommentBtn.setEnabled(false);
        } else {
            binding.lastCommentBtn.setEnabled(true);
        }
        if (currentSelectCommentIndex + 1 == commentatorValidMap.keySet().size()) {
            binding.nextCommentBtn.setEnabled(false);
        } else {
            binding.nextCommentBtn.setEnabled(true);
        }
        if (commentatorValidMap.keySet().size() == 0) {
            binding.commentNameTextview.setText("无批改结果");
        } else if (commentatorValidMap.keySet().size() == 1) {
            binding.commentNameTextview.setText("批改结果");
        } else {
            binding.commentNameTextview.setText("批改结果" + (currentSelectCommentIndex + 1));
        }
    }

    /**
     * 更新圆形进度条的文字
     */
    private void refreshCircleProgressBar() {
        if (isScoring) {
            binding.textScoreTitle.setText("分数");
            if (examTotalPoints <= 0) {
                binding.circleProgressBar.setProgress(0);
            } else {
                binding.circleProgressBar.setProgress(((int) (score * 100 / examTotalPoints)));
            }
            binding.circleProgressBar.setIsDrawCenterText(false);
            binding.textScore.setText(score + "分");
        } else { //不计分作业
            binding.textScoreTitle.setText("正确率");
            if (isStudentCheck == 2) {//互评
                binding.circleProgressBar.setProgress(((int) scoreAvg));
                binding.textScore.setText((int) scoreAvg + "%");
            } else {
                binding.circleProgressBar.setProgress(correctCount * 100 / itemCount);
                binding.textScore.setText(correctCount + "/" + itemCount);
            }
            binding.circleProgressBar.setIsDrawCenterText(false);
        }
    }

    @Override
    protected void refreshView() {
    }

    public void back(View view) {
        finish();
    }

    @OnClick({R.id.image_refresh, R.id.last_comment_btn, R.id.next_comment_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_refresh:
                loadData();
                break;
            case R.id.last_comment_btn:
                currentSelectCommentIndex--;
                refreshCommentChooseBar();
                binding.mainRecyclerview.notifyDataSetChanged();
                break;
            case R.id.next_comment_btn:
                currentSelectCommentIndex++;
                refreshCommentChooseBar();
                binding.mainRecyclerview.notifyDataSetChanged();
                break;
        }
    }

    private class MyHolder extends RecyclerView.ViewHolder {
        private ItemQuestionGridviewBinding itemBinding;
        private ItemQuestionGridview2Binding itemBinding2;

        public MyHolder(ItemQuestionGridviewBinding binding) {
            super(binding.getRoot());
            this.itemBinding = binding;//计分
        }

        public MyHolder(ItemQuestionGridview2Binding binding) {
            super(binding.getRoot());
            this.itemBinding2 = binding;
        }

        public MyHolder setData(int score, int replyItemWeight) {
            if (isScoring) {
                if (score == 0) {
                    itemBinding.icon.setBackgroundResource(R.drawable.img_fenzhi_cuowu);
                } else {
                    if (score == replyItemWeight) {
                        itemBinding.icon.setBackgroundResource(R.drawable.img_fenzhi_zhengque);
                    } else {
                        itemBinding.icon.setBackgroundResource(R.drawable.img_fenzhi_bandui);
                    }
                }
            } else {
                if (score == 0) {
                    itemBinding2.icon.setBackgroundResource(R.drawable.img_cuowu_2);
                } else if (score == 100) {
                    itemBinding2.icon.setBackgroundResource(R.drawable.img_zhengque_2);
                } else {
                    itemBinding2.icon.setBackgroundResource(R.drawable.img_bandui_2);
                }
            }

            if (isScoring) {//计分作业
                itemBinding.scoreText.setText(score + "分");
            }
            return this;
        }
    }

}
