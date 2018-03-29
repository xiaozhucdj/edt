package com.yougy.homework;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.homework.bean.QuestionReplySummary;
import com.yougy.message.SizeUtil;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityCheckedHomeworkDetailBinding;
import com.yougy.ui.activity.databinding.ItemQuestionGridviewBinding;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by FH on 2017/11/6.
 *
 * 单个已批改作业整体概览界面,统计正确率,每道题的对错,等等
 */

public class CheckedHomeworkOverviewActivity extends HomeworkBaseActivity{
    ActivityCheckedHomeworkDetailBinding binding;
    ArrayList<QuestionReplySummary> replyList = new ArrayList<QuestionReplySummary>();
    int examId;
    String examName;
    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this) , R.layout.activity_checked_homework_detail , null , false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    @Override
    protected void init() {
        examId = getIntent().getIntExtra("examId" , -1);
        if (examId == -1){
            ToastUtil.showToast(getApplicationContext() , "examId 为空");
            finish();
        }
        examName = getIntent().getStringExtra("examName");
        binding.titleTv.setText(examName);
    }

    @Override
    protected void initLayout() {
        binding.mainRecyclerview.setMaxItemNumInOnePage(36);
        binding.mainRecyclerview.setLayoutManager(new GridLayoutManager(getApplicationContext() , 6){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.mainRecyclerview.setAdapter(new PageableRecyclerView.Adapter<MyHolder>() {
            @Override
            public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyHolder(DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()) , R.layout.item_question_gridview , parent , false));
            }

            @Override
            public void onBindViewHolder(MyHolder holder, int position) {
                holder.itemBinding.textview.setText("" + (position + 1));
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
                Intent intent = new Intent(CheckedHomeworkOverviewActivity.this , CheckedHomeworkDetailActivity.class);
                intent.putExtra("examName" , examName);
                intent.putExtra("toShow" , holder.getData());
                intent.putExtra("examId" , examId);
                intent.putParcelableArrayListExtra("all" , replyList);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void loadData() {
        NetWorkManager.queryReplySummary(examId , SpUtils.getUserId())
                .subscribe(new Action1<List<QuestionReplySummary>>() {
                    @Override
                    public void call(List<QuestionReplySummary> replySummaries) {
                        replyList.clear();
                        replyList.addAll(replySummaries);
                        binding.mainRecyclerview.notifyDataSetChanged();
                        binding.questionNumTv.setText("习题数量 : " + replyList.size());
                        refreshCircleProgressBar();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }


    /**
     * 更新圆形进度条的文字
     */
    private void refreshCircleProgressBar(){
        float f = 0;
        for (QuestionReplySummary reply : replyList) {
            if (reply.getReplyScore() == 100){
                f++;
            }
        }
        f = f*100/replyList.size();
        binding.circleProgressBar.setProgress(Integer.valueOf(SizeUtil.doScale(f , 0)));
        binding.circleProgressBar.setText(SizeUtil.doScale(f , 0) + "%");
    }
    @Override
    protected void refreshView() {}

    public void back(View view){
        finish();
    }
    private class MyHolder extends RecyclerView.ViewHolder{
        private ItemQuestionGridviewBinding itemBinding;
        private QuestionReplySummary data;
        public MyHolder(ItemQuestionGridviewBinding binding) {
            super(binding.getRoot());
            this.itemBinding = binding;
        }

        public MyHolder setData(QuestionReplySummary data) {
            this.data = data;
            if (data.getReplyScore() == 0){
                itemBinding.icon.setBackgroundResource(R.drawable.icon_wrong);
            }
            else if (data.getReplyScore() == 100){
                itemBinding.icon.setBackgroundResource(R.drawable.icon_correct);
            }
            else {
                itemBinding.icon.setBackgroundResource(R.drawable.icon_half_correct);
            }
            return this;
        }

        public ItemQuestionGridviewBinding getItemBinding() {
            return itemBinding;
        }

        public QuestionReplySummary getData() {
            return data;
        }
    }
}
