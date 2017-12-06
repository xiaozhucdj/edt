package com.yougy.homework.mistake_note;

import android.content.Intent;
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
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.homework.HomeworkBaseActivity;
import com.yougy.homework.PageableRecyclerView;
import com.yougy.homework.bean.BookStructureNode;
import com.yougy.homework.bean.HomeworkBookDetail;
import com.yougy.homework.bean.MistakeSummary;
import com.yougy.message.ListUtil;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityMistakeNoteBookStructureBinding;
import com.yougy.ui.activity.databinding.ItemBookChapterBinding;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by FH on 2017/10/19.
 * 错题本中的图书章节列表,并显示章节下是否有错题
 */

public class BookStructureActivity extends HomeworkBaseActivity {
    ActivityMistakeNoteBookStructureBinding binding;
    List<BookStructureNode> bookStructureNodeList = new ArrayList<BookStructureNode>();
    List<MistakeSummary> mistakeList = new ArrayList<MistakeSummary>();
    int bookId;
    int homeworkId;
    String bookTitle;
    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this)
                , R.layout.activity_mistake_note_book_structure , null , false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }
    @Override
    protected void init() {
        setNeedRecieveEventAfterOnStop(true);
        bookId = getIntent().getIntExtra("bookId" , -1);
        homeworkId = getIntent().getIntExtra("homeworkId" , -1);
    }

    @Override
    protected void handleEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof String
                        && (((String) o).startsWith("removeMistakeItem:") || ((String) o).startsWith("lastScoreChanged"))){
                    getMistakes();
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
                        BookStructureNode node;
                        if (position == 0){
                            node = ((MyAdapter) ((HeaderViewListAdapter) parent.getAdapter()).getWrappedAdapter()).fatherNode;
                        }
                        else {
                            node = ((MyAdapter) ((HeaderViewListAdapter) parent.getAdapter()).getWrappedAdapter()).nodeList.get(position - 1);
                        }
                        ArrayList<MistakeSummary> subMistakeList = ListUtil.conditionalSubList(mistakeList, new ListUtil.ConditionJudger<MistakeSummary>() {
                            @Override
                            public boolean isMatchCondition(MistakeSummary nodeInList) {
                                return nodeInList.getExtra().getCursor() == node.getId();
                            }
                        });
                        Intent intent = new Intent(getApplicationContext() , MistakeListActivity.class);
                        intent.putExtra("topNode" , ((MyAdapter) ((HeaderViewListAdapter) parent.getAdapter()).getWrappedAdapter()).fatherNode);
                        intent.putExtra("currentNode" , node);
                        intent.putExtra("homeworkId" , homeworkId);
                        intent.putExtra("bookTitle" , bookTitle);
                        if (subMistakeList.size() > 0){
                            intent.putParcelableArrayListExtra("mistakeList" , subMistakeList);
                        }
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
    }
    @Override
    protected void loadData() {
        if (bookId == -1){
            ToastUtil.showToast(getApplicationContext() , "bookId 为空");
            finish();
            return;
        }
        bookStructureNodeList.clear();
        NetWorkManager.queryBook(bookId)
                .subscribe(new Action1<List<Object>>() {
                    @Override
                    public void call(List<Object> objects) {
                        if (objects.size() > 0){
                            getMistakes();
                            bookTitle = (String) ((LinkedTreeMap) objects.get(0)).get("bookTitle");
                            List<LinkedTreeMap> nodeList =(List<LinkedTreeMap>)((LinkedTreeMap) ((LinkedTreeMap) objects.get(0)).get("bookContents")).get("nodes");
                            bookStructureNodeList.addAll(parseNode(nodeList));
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

    private void getMistakes(){
        if (homeworkId == -1){
            ToastUtil.showToast(getApplicationContext() , "homeworkId 为空");
            finish();
            return;
        }
        NetWorkManager.queryHomeworkBookDetail(homeworkId)
                .subscribe(new Action1<List<HomeworkBookDetail>>() {
                    @Override
                    public void call(List<HomeworkBookDetail> homeworkBookDetails) {
                        if (homeworkBookDetails != null && homeworkBookDetails.size() != 0
                                && homeworkBookDetails.get(0).getHomeworkExcerpt() != null
                                && homeworkBookDetails.get(0).getHomeworkExcerpt().size() != 0){
                            mistakeList.clear();
                            for (MistakeSummary mistakeSummary : homeworkBookDetails.get(0).getHomeworkExcerpt()) {
                                if (!mistakeSummary.getExtra().isDeleted()){
                                 mistakeList.add(mistakeSummary);
                                }
                            }
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

    @Override
    protected void refreshView() {

    }

    public void back(View view){
        onBackPressed();
    }

    private List<BookStructureNode> parseNode(List<LinkedTreeMap> list){
        List<BookStructureNode> returnList = new ArrayList<BookStructureNode>();
        for (LinkedTreeMap linkedTreeMap : list) {
            BookStructureNode node = new BookStructureNode();
            node.setId((int)((double) linkedTreeMap.get("id")));
            node.setLevel((int)((double) linkedTreeMap.get("level")));
            node.setName((String) linkedTreeMap.get("name"));
            if (linkedTreeMap.get("nodes") != null){
                node.setNodes(parseNode((List<LinkedTreeMap>)linkedTreeMap.get("nodes")));
            }
            returnList.add(node);
        }
        return returnList;
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
        private BookStructureNode fatherNode;
        private List<BookStructureNode> nodeList = new ArrayList<BookStructureNode>();
        public void setFatherNode(BookStructureNode fatherNode){
            this.fatherNode = fatherNode;
            nodeList.clear();
            if (fatherNode .getNodes()!= null && fatherNode .getNodes().size() != 0){
                for (BookStructureNode node : fatherNode.getNodes()) {
                    addNode(node);
                }
            }
            notifyDataSetChanged();
        }

        private void addNode(BookStructureNode node){
            nodeList.add(node);
            if (node.getNodes() != null && node.getNodes().size() != 0){
                for (BookStructureNode childNode : node.getNodes()) {
                    addNode(childNode);
                }
            }
        }

        public BookStructureNode getFatherNode(){
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
            BookStructureNode node = nodeList.get(position);
            bookChapterBinding.textview.setText(node.getName());
            if (ListUtil.conditionalContains(mistakeList, new ListUtil.ConditionJudger<MistakeSummary>() {
                @Override
                public boolean isMatchCondition(MistakeSummary nodeInList) {
                    return nodeInList.getExtra().getCursor() == node.getId();
                }
            })){
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
