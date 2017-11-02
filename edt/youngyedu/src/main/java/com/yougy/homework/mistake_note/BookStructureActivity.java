package com.yougy.homework.mistake_note;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yougy.common.utils.UIUtils;
import com.yougy.homework.HomeworkBaseActivity;
import com.yougy.homework.PageableRecyclerView;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityMistakeNoteBookStructureBinding;

/**
 * Created by FH on 2017/10/19.
 */

public class BookStructureActivity extends HomeworkBaseActivity {
    ActivityMistakeNoteBookStructureBinding binding;
    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this)
                , R.layout.activity_mistake_note_book_structure , null , false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }
    @Override
    protected void init() {

    }
    @Override
    protected void initLayout() {
        binding.mainRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext() , LinearLayoutManager.HORIZONTAL , false));
        binding.mainRecyclerview.setMaxItemNumInOnePage(2);
        binding.mainRecyclerview.setAdapter(new PageableRecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ListView listView = new ListView(getApplicationContext());
                RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(50 , 950);
                listView.setLayoutParams(params);
                listView.setBackgroundColor(Color.BLACK);
                return new MyHolder(listView);
            }
            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            }
            @Override
            public int getItemCount() {
                return 10;
            }
        });
    }
    @Override
    protected void loadData() {

    }
    @Override
    protected void refreshView() {

    }
    private class MyHolder extends RecyclerView.ViewHolder{
        public MyHolder(View itemView) {
            super(itemView);
        }
    }
}
