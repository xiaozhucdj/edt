package com.yougy.homework.mistake_note;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.homework.HomeworkBaseActivity;
import com.yougy.homework.PageableRecyclerView;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityMistakeListBinding;
import com.yougy.ui.activity.databinding.ItemMistakeListBinding;

/**
 * Created by FH on 2017/11/3.
 */

public class MistakeListActivity extends HomeworkBaseActivity{
    ActivityMistakeListBinding binding;
    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.activity_mistake_list , null , false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initLayout() {
        binding.mainRecyclerview.setMaxItemNumInOnePage(4);
        binding.mainRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext() , LinearLayoutManager.VERTICAL , false));
        binding.mainRecyclerview.setAdapter(new PageableRecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyHolder(DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()) , R.layout.item_mistake_list , parent , false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 10;
            }
        });
        binding.mainRecyclerview.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.mainRecyclerview.getRealRcyView()) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                //TODO 列表点击事件
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

    public void back(View view){
        onBackPressed();
    }
    private class MyHolder extends RecyclerView.ViewHolder{
        ItemMistakeListBinding binding;
        public MyHolder(ItemMistakeListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
