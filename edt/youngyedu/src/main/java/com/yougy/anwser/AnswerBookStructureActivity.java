package com.yougy.anwser;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.homework.PageableRecyclerView;
import com.yougy.homework.bean.HomeworkDetail;
import com.yougy.shop.bean.BookInfo;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityAnswerBookStructureBinding;
import com.yougy.ui.activity.databinding.ItemBookChapterBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by FH on 2018/03/08.
 * 问答记录中的图书章节列表,并显示章节下是否有问答记录
 */

public class AnswerBookStructureActivity extends AnswerBaseActivity {
    ActivityAnswerBookStructureBinding binding;
    List<BookInfo.BookContentsBean.NodesBean> bookStructureNodeList = new ArrayList<BookInfo.BookContentsBean.NodesBean>();
    HashMap<String , Integer> examNumSumMap = new HashMap<String, Integer>();
    int bookId;
    String bookName;
    String bookTitle;
    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this)
                , R.layout.activity_answer_book_structure , null , false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }
    @Override
    protected void init() {
        setNeedRecieveEventAfterOnStop(true);
        bookId = getIntent().getIntExtra("bookId" , -1);
        bookName = getIntent().getStringExtra("bookName");
    }

    @Override
    protected void handleEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof String
                        && (((String) o).startsWith("refresh:"))){
                    getAnswers();
                }
            }
        }));
        super.handleEvent();
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
                listView.setDividerHeight(2);
                listView.setBackgroundResource(R.drawable.shape_rounded_rectangle_black_border);
                TextView headView = new TextView(getApplicationContext());
                ListView.LayoutParams headviewParam = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);
                headView.setTextSize(TypedValue.COMPLEX_UNIT_SP , 25);
                headView.setPadding(20 , 20 , 20 , 20);
                headView.setLayoutParams(headviewParam);
                listView.addHeaderView(headView);
                listView.setAdapter(new MyAdapter());
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        BookInfo.BookContentsBean.NodesBean node;
                        if (position == 0){
                            node = ((MyAdapter) ((HeaderViewListAdapter) parent.getAdapter()).getWrappedAdapter()).fatherNode;
                        }
                        else {
                            node = ((MyAdapter) ((HeaderViewListAdapter) parent.getAdapter()).getWrappedAdapter()).nodeList.get(position - 1);
                        }
                        Intent intent = new Intent(getApplicationContext() , AnswerRecordListActivity.class);
                        intent.putExtra("bookId" , bookId);
                        intent.putExtra("bookName" , bookName);
                        intent.putExtra("cursor" , node.getId());
                        intent.putExtra("chapterName" , node.getName());
                        startActivity(intent);
                    }
                });
                return new MyHolder(listView , headView);
            }

            @Override
            public void onBindViewHolder(MyHolder holder, int position) {
                holder.headView.setText(bookStructureNodeList.get(position).getName());
                ((MyAdapter) ((HeaderViewListAdapter) holder.listview.getAdapter()).getWrappedAdapter()).setFatherNode(bookStructureNodeList.get(position));
            }

            @Override
            public int getItemCount() {
                return bookStructureNodeList.size();
            }
        });
        binding.mainRecyclerview.notifyDataSetChanged();
        binding.titleTv.setText(bookName + "问答");
    }
    @Override
    protected void loadData() {
        if (bookId == -1){
            ToastUtil.showToast(getApplicationContext() , "bookId 为空");
            finish();
            return;
        }
        bookStructureNodeList.clear();
        //先获取图书章节信息
        NetWorkManager.queryBook(bookId + "" , null)
                .subscribe(new Action1<List<BookInfo>>() {
                    @Override
                    public void call(List<BookInfo> bookInfoList) {
                        if (bookInfoList.size() > 0){
                            //再获取错题列表
                            getAnswers();
                            bookTitle = bookInfoList.get(0).getBookTitle();
                            bookStructureNodeList.addAll(bookInfoList.get(0).getBookContents().getNodes());
                            binding.mainRecyclerview.notifyDataSetChanged();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    private void getAnswers(){
        NetWorkManager.queryAnswer(SpUtils.getStudent().getClassId() + "" , bookId+ "" , null)
                .subscribe(new Action1<List<HomeworkDetail>>() {
                    @Override
                    public void call(List<HomeworkDetail> homeworkBookDetails) {
                        examNumSumMap.clear();
                        for (HomeworkDetail homeworkDetail : homeworkBookDetails) {
                            List<HomeworkDetail.ExamPostscriptBean> examPostscriptList = homeworkDetail.getExamPostscript();
                            if (examPostscriptList.size() > 0){
                                String cursorID = examPostscriptList.get(0).getCursor();
                                if (!TextUtils.isEmpty(cursorID)){
                                    Integer count = examNumSumMap.get(cursorID);
                                    if (count ==  null){
                                        examNumSumMap.put(cursorID , 1);
                                    }
                                    else {
                                        examNumSumMap.put(cursorID , count + 1);
                                    }
                                }
                            }
                        }
                        binding.mainRecyclerview.notifyDataSetChanged();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    protected void refreshView() {

    }

    public void back(View view){
        onBackPressed();
    }

    private class MyHolder extends RecyclerView.ViewHolder{
        ListView listview;
        TextView headView;
        public MyHolder(ListView listview , TextView headView) {
            super(listview);
            this.listview = listview;
            this.headView = headView;
        }
    }

    private class MyAdapter extends BaseAdapter {
        private BookInfo.BookContentsBean.NodesBean fatherNode;
        private List<BookInfo.BookContentsBean.NodesBean> nodeList = new ArrayList<BookInfo.BookContentsBean.NodesBean>();
        public void setFatherNode(BookInfo.BookContentsBean.NodesBean fatherNode){
            this.fatherNode = fatherNode;
            nodeList.clear();
            if (fatherNode .getNodes()!= null && fatherNode .getNodes().size() != 0){
                for (BookInfo.BookContentsBean.NodesBean node : fatherNode.getNodes()) {
                    addNode(node);
                }
            }
            notifyDataSetChanged();
        }

        private void addNode(BookInfo.BookContentsBean.NodesBean node){
            nodeList.add(node);
            if (node.getNodes() != null && node.getNodes().size() != 0){
                for (BookInfo.BookContentsBean.NodesBean childNode : node.getNodes()) {
                    addNode(childNode);
                }
            }
        }

        public BookInfo.BookContentsBean.NodesBean getFatherNode(){
            return fatherNode;
        }
        @Override
        public int getCount() {
            return nodeList.size();
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
            BookInfo.BookContentsBean.NodesBean node = nodeList.get(position);
            bookChapterBinding.textview.setText(node.getName());
            Integer examCountInNode = examNumSumMap.get(String.valueOf(node.getId()));
            if (examCountInNode != null && examCountInNode > 0){
                bookChapterBinding.redDot.setVisibility(View.VISIBLE);
            }
            else {
                bookChapterBinding.redDot.setVisibility(View.GONE);
            }
            if (node.getLevel() == 2){
                convertView.setPadding(20 , 0 , 0 , 0);
                bookChapterBinding.textview.setTextSize(TypedValue.COMPLEX_UNIT_PX , 24);
                bookChapterBinding.getRoot().getLayoutParams().height = 60;
                bookChapterBinding.arrowImg.setVisibility(View.VISIBLE);
            }
            else if (node.getLevel() == 3){
                convertView.setPadding(75 , 0 , 0 , 0);
                bookChapterBinding.textview.setTextSize(TypedValue.COMPLEX_UNIT_PX , 20);
                bookChapterBinding.getRoot().getLayoutParams().height = 45;
                bookChapterBinding.arrowImg.setVisibility(View.GONE);
            }
            return convertView;
        }
    }
}
