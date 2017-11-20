package com.yougy.homework;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityCheckedHomeworkDetailBinding;
import com.yougy.ui.activity.databinding.ItemQuestionGridviewBinding;

/**
 * Created by FH on 2017/11/6.
 */

public class CheckedHomeworkDetailActivity extends HomeworkBaseActivity{
    ActivityCheckedHomeworkDetailBinding binding;
    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this) , R.layout.activity_checked_homework_detail , null , false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initLayout() {
        binding.circleProgressBar.setText("01234567890123");
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
                int i = position % 3;
                if (i == 0){
                    holder.itemBinding.icon.setBackgroundResource(R.drawable.icon_correct);
                }
                else if (i == 1){
                    holder.itemBinding.icon.setBackgroundResource(R.drawable.icon_half_correct);
                }
                else if (i == 2){
                    holder.itemBinding.icon.setBackgroundResource(R.drawable.icon_wrong);
                }
            }

            @Override
            public int getItemCount() {
                return 100;
            }
        });
        binding.mainRecyclerview.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.mainRecyclerview.getRealRcyView()) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {

            }
        });
        binding.mainRecyclerview.notifyDataSetChanged();
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void refreshView() {

    }

    private class MyHolder extends RecyclerView.ViewHolder{
        ItemQuestionGridviewBinding itemBinding;
        public MyHolder(ItemQuestionGridviewBinding binding) {
            super(binding.getRoot());
            this.itemBinding = binding;
        }
    }
}
