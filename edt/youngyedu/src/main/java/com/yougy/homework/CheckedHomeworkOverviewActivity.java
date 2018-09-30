package com.yougy.homework;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.List;

import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by FH on 2017/11/6.
 * <p>
 * 单个已批改作业整体概览界面,统计正确率,每道题的对错,等等
 *
 * modify by zhangyc on 2018/0704  添加计分作业显示逻辑
 */

public class CheckedHomeworkOverviewActivity extends HomeworkBaseActivity {
    ActivityCheckedHomeworkDetailBinding binding;
    ArrayList<QuestionReplySummary> replyList = new ArrayList<QuestionReplySummary>();
    int examId;
    String examName;

    private boolean isScoring = false;// 是否计分作业
    private int examTotalPoints ;//exam 总分
    private int totalScore ;//得分
    private int itemCount;//总题数
    private int correctCount;//正确的题目

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
        itemCount = getIntent().getIntExtra("getItemCount", 0 );
        binding.titleTv.setText(examName);
    }

    @Override
    protected void initLayout() {
        if (isScoring) {
            binding.mainRecyclerview.setMaxItemNumInOnePage(30);
        } else {
            binding.mainRecyclerview.setMaxItemNumInOnePage(42);
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
                ItemQuestionGridviewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()),R.layout.item_question_gridview, parent, false);
                //不计分
                ItemQuestionGridview2Binding binding2 = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()),R.layout.item_question_gridview2, parent, false);
                if (isScoring) {
                    return new MyHolder(binding);
                }
                return  new MyHolder(binding2);
            }

            @Override
            public void onBindViewHolder(MyHolder holder, int position) {
                if (isScoring) {
                    holder.itemBinding.textview.setText("" + (position + 1));
                } else {
                    holder.itemBinding2.textview.setText("" + (position + 1));
                }
                holder.setData(replyList.get(position));
            }

            @Override
            public int getItemCount() {
                return replyList.size();
            }
        });
        binding.mainRecyclerview.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.mainRecyclerview.getRealRcyView()) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                MyHolder holder = (MyHolder) vh;
                Intent /*intent = new Intent(CheckedHomeworkOverviewActivity.this, CheckedHomeworkDetailActivity.class);
                intent.putExtra("examName", examName);
                intent.putExtra("toShow", holder.getData());
                intent.putExtra("examId", examId);
                intent.putParcelableArrayListExtra("all", replyList);
                intent.putExtra("isScoring", isScoring);
                startActivity(intent);*/

                intent = new Intent(CheckedHomeworkOverviewActivity.this , CheckHomeWorkActivity.class);
                intent.putExtra("examId" , examId);
                intent.putExtra("toShowPosition", holder.getPosition());
                intent.putExtra("isCheckOver", true);
                intent.putExtra("isStudentLook", true);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void loadData() {
        NetWorkManager.queryReplySummary(examId, SpUtils.getUserId())
                .subscribe(new Action1<List<QuestionReplySummary>>() {
                    @Override
                    public void call(List<QuestionReplySummary> replySummaries) {
                        replyList.clear();
                        replyList.addAll(replySummaries);
                        binding.mainRecyclerview.notifyDataSetChanged();
                        binding.questionNumTv.setText("习题数量 : " + replyList.size());
                        long allUseTime = 0;
                        for (QuestionReplySummary questionReplySummary : replyList) {
                            long detailUseTime = DateUtils.transformToTime(questionReplySummary.getReplyUseTime());
                            allUseTime += detailUseTime;
                        }
                        binding.timeTv.setText(DateUtils.converLongTimeToString(allUseTime * 1000));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        ToastUtil.showCustomToast(getApplicationContext() , "获取每题分数数据失败");
                    }
                });
        NetWorkManager.sumReplyStudent(examId , SpUtils.getUserId())
                .subscribe(new Action1<List<HomeworkSummarySumInfo>>() {
                    @Override
                    public void call(List<HomeworkSummarySumInfo> homeworkSummarySumInfos) {
                        if (homeworkSummarySumInfos == null || homeworkSummarySumInfos.size() == 0){
                            ToastUtil.showCustomToast(getApplicationContext() , "获取总分失败,查不到数据");
                        }
                        else {
                            totalScore = homeworkSummarySumInfos.get(0).getScore();
                            correctCount = homeworkSummarySumInfos.get(0).getCorrectCount();
                            refreshCircleProgressBar();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        ToastUtil.showCustomToast(getApplicationContext() , "获取总分失败");
                    }
                });
    }


    /**
     * 更新圆形进度条的文字
     */
    private void refreshCircleProgressBar() {
        if (isScoring) {
            binding.textScoreTitle.setText("分数");
            binding.circleProgressBar.setProgress(totalScore * 100 / examTotalPoints);
            binding.circleProgressBar.setIsDrawCenterText(false);
            binding.textScore.setText(totalScore + "分");
        } else { //不计分作业
            binding.textScoreTitle.setText("正确率");
            binding.circleProgressBar.setProgress((int) (correctCount * 100 / itemCount));
            binding.textScore.setText(correctCount + "/" + itemCount);
            binding.circleProgressBar.setIsDrawCenterText(false);
        }

    }

    @Override
    protected void refreshView() {
    }

    public void back(View view) {
        finish();
    }

    @OnClick({R.id.image_refresh})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_refresh:
                loadData();
                break;
        }
    }

    private class MyHolder extends RecyclerView.ViewHolder {
        private ItemQuestionGridviewBinding itemBinding;
        private ItemQuestionGridview2Binding itemBinding2;
        private QuestionReplySummary data;

        public MyHolder(ItemQuestionGridviewBinding binding) {
            super(binding.getRoot());
            this.itemBinding = binding;//计分
        }

        public MyHolder(ItemQuestionGridview2Binding binding) {
            super(binding.getRoot());
            this.itemBinding2 = binding;
        }

        public MyHolder setData(QuestionReplySummary data) {
            this.data = data;
            if (isScoring) {
                if (data.getReplyScore() == 0) {
                    itemBinding.icon.setBackgroundResource(R.drawable.img_fenzhi_cuowu);
                }
                else {
                    if (data.getReplyScore() == data.getReplyItemWeight()) {
                        itemBinding.icon.setBackgroundResource(R.drawable.img_fenzhi_zhengque);
                    } else {
                        itemBinding.icon.setBackgroundResource(R.drawable.img_fenzhi_bandui);
                    }
                }
            } else {
                if (data.getReplyScore() == 0) {
                    itemBinding2.icon.setBackgroundResource(R.drawable.img_cuowu_2);
                } else if (data.getReplyScore() == 100) {
                    itemBinding2.icon.setBackgroundResource(R.drawable.img_zhengque_2);
                } else {
                    itemBinding2.icon.setBackgroundResource(R.drawable.img_bandui_2);
                }
            }

            if (isScoring) {//计分作业
                itemBinding.scoreText.setText(data.getReplyScore() + "分");
            }
            return this;
        }
        public QuestionReplySummary getData() {
            return data;
        }
    }
}
