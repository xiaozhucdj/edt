package com.yougy.homework.mistake_note;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yougy.common.utils.UIUtils;
import com.yougy.homework.HomeworkBaseActivity;
import com.yougy.homework.PageableRecyclerView;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityMistakeNoteBookStructureBinding;
import com.yougy.ui.activity.databinding.ItemBookChapterBinding;

import java.util.ArrayList;

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
        binding.mainRecyclerview.setLayoutManager(new GridLayoutManager(getApplicationContext() , 2){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.mainRecyclerview.setMaxItemNumInOnePage(2);
        binding.mainRecyclerview.setAdapter(new PageableRecyclerView.Adapter<MyHolder>() {
            @Override
            public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ListView listView = new ListView(getApplicationContext());
                RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(430 , 1020);
                params.leftMargin = 25;
                params.rightMargin = 25;
                params.topMargin = 30;
                listView.setLayoutParams(params);
                listView.setBackgroundResource(R.drawable.shape_rounded_rectangle_black_border);
                TextView headView = new TextView(getApplicationContext());
                ListView.LayoutParams headviewParam = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);
                headView.setText("heyheyhey111111!");
                headView.setTextSize(TypedValue.COMPLEX_UNIT_SP , 25);
                headView.setPadding(20 , 20 , 20 , 20);
                headView.setLayoutParams(headviewParam);
                listView.addHeaderView(headView);
                listView.setAdapter(new MyAdapter());
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        ((MyAdapter) parent.getAdapter()).dataList.get(position);
                        //TODO 章节点击事件
                        loadIntent(MistakeListActivity.class);
                    }
                });
                return new MyHolder(listView);
            }

            @Override
            public void onBindViewHolder(MyHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 10;
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
        ListView listview;
        public MyHolder(View itemView) {
            super(itemView);
        }
    }

    private class MyAdapter extends BaseAdapter {
        ArrayList<Object> dataList = new ArrayList<Object>();
        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemBookChapterBinding bookChapterBinding;
            if (convertView == null){
                bookChapterBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()) , R.layout.item_book_chapter , parent, false);
                convertView = bookChapterBinding.getRoot();
                convertView.setTag(bookChapterBinding);
            }
            bookChapterBinding = (ItemBookChapterBinding) convertView.getTag();
            bookChapterBinding.textview.setText("heyheyhey!");
            if (position%2 == 0){
                convertView.setPadding(20 , 0 , 0 , 0);
                bookChapterBinding.textview.setTextSize(TypedValue.COMPLEX_UNIT_PX , 24);
                bookChapterBinding.getRoot().getLayoutParams().height = 60;
                bookChapterBinding.arrowImg.setVisibility(View.VISIBLE);
            }
            else {
                convertView.setPadding(75 , 0 , 0 , 0);
                bookChapterBinding.textview.setTextSize(TypedValue.COMPLEX_UNIT_PX , 20);
                bookChapterBinding.getRoot().getLayoutParams().height = 45;
                bookChapterBinding.arrowImg.setVisibility(View.GONE);
            }
            return convertView;
        }
    }
}
