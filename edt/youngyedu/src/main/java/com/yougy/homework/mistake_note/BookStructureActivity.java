package com.yougy.homework.mistake_note;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.homework.HomeworkBaseActivity;
import com.yougy.homework.PageableRecyclerView;
import com.yougy.homework.bean.HomeworkBookDetail;
import com.yougy.homework.bean.MistakeSummary;
import com.yougy.message.ListUtil;
import com.yougy.shop.bean.BookInfo;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityMistakeNoteBookStructureBinding;
import com.yougy.ui.activity.databinding.ItemBookChapterBinding;
import com.yougy.ui.activity.databinding.ItemBookTopChapterBinding;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by FH on 2017/10/19.
 * 错题本中的图书章节列表,并显示章节下是否有错题
 */

public class BookStructureActivity extends HomeworkBaseActivity {
    ActivityMistakeNoteBookStructureBinding binding;
    List<BookInfo.BookContentsBean.NodesBean> bookStructureNodeList = new ArrayList<BookInfo.BookContentsBean.NodesBean>();
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
//                if (o instanceof String
//                        && (((String) o).startsWith("removeMistakeItem:") || ((String) o).startsWith("lastScoreChanged"))){
//                    getMistakes();
//                }
            }
        }));
        super.handleEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMistakes();
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
                ItemBookTopChapterBinding headViewBinding = DataBindingUtil.inflate(LayoutInflater.from(BookStructureActivity.this) , R.layout.item_book_top_chapter , null , false);
                listView.addHeaderView(headViewBinding.getRoot());
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
                return new MyHolder(listView , headViewBinding);
            }

            @Override
            public void onBindViewHolder(MyHolder holder, int position) {
                holder.headViewBinding.textview.setText(bookStructureNodeList.get(position).getName());
                if (ListUtil.conditionalContains(mistakeList, new ListUtil.ConditionJudger<MistakeSummary>() {
                    @Override
                    public boolean isMatchCondition(MistakeSummary nodeInList) {
                        return nodeInList.getExtra().getCursor() == bookStructureNodeList.get(position).getId();
                    }
                })){
                    holder.headViewBinding.redDot.setVisibility(View.VISIBLE);
                }
                else {
                    holder.headViewBinding.redDot.setVisibility(View.GONE);
                }
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
            ToastUtil.showCustomToast(getApplicationContext() , "bookId 为空");
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

    private void getMistakes(){
        if (homeworkId == -1){
            ToastUtil.showCustomToast(getApplicationContext() , "homeworkId 为空");
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
                                //被标记为"我已学会"的错题不算作错题,排除
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


    private class MyHolder extends RecyclerView.ViewHolder{
        ListView listview;
        ItemBookTopChapterBinding headViewBinding;
        public MyHolder(ListView listview , ItemBookTopChapterBinding headViewBinding) {
            super(listview);
            this.listview = listview;
            this.headViewBinding = headViewBinding;
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
                bookChapterBinding.textview.setMaxWidth(340);
                bookChapterBinding.getRoot().getLayoutParams().height = 60;
                bookChapterBinding.arrowImg.setVisibility(View.VISIBLE);
            }
            else if (node.getLevel() == 3){
                convertView.setPadding(55 , 0 , 0 , 0);
                bookChapterBinding.textview.setTextSize(TypedValue.COMPLEX_UNIT_PX , 20);
                bookChapterBinding.textview.setMaxWidth(320);
                bookChapterBinding.getRoot().getLayoutParams().height = 44;
                bookChapterBinding.arrowImg.setVisibility(View.GONE);
            }
            return convertView;
        }
    }
}
