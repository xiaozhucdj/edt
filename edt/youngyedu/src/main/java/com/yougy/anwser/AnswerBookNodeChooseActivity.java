package com.yougy.anwser;

import android.content.DialogInterface;
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

import com.yougy.common.activity.BaseActivity;
import com.yougy.common.global.Constant;
import com.yougy.common.manager.DialogManager;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.home.bean.DataCountInBookNode;
import com.yougy.homework.PageableRecyclerView;
import com.yougy.shop.bean.BookInfo;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityBookNodeChooseBinding;
import com.yougy.ui.activity.databinding.ItemBookChapterBinding;
import com.yougy.ui.activity.databinding.ItemBookTopChapterBinding;
import com.yougy.view.dialog.ConfirmDialog;
import com.yougy.view.dialog.HintDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.functions.Action1;

public class AnswerBookNodeChooseActivity extends BaseActivity {
    public static final String KEY_BOOK_ID = "book_id";
    public static final String KEY_HOMEWORK_ID = "homework_id";

    public static final String RETURN_KEY_CHOSEN_NODE_INDEX = "nodeIndex";

    ActivityBookNodeChooseBinding binding;

    int bookId = -1;
    int homeworkId = -1;

    List<BookInfo.BookContentsBean.NodesBean> bookStructureNodeList = new ArrayList<BookInfo.BookContentsBean.NodesBean>();
    private HashMap<String, Integer> questionItemCountMap = new HashMap<String, Integer>();

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this) , R.layout.activity_book_node_choose , null , false);
        setContentView(binding.getRoot());
    }

    @Override
    public void init() {
        bookId = getIntent().getIntExtra(KEY_BOOK_ID , -1);
        homeworkId = getIntent().getIntExtra(KEY_HOMEWORK_ID , -1);
    }

    @Override
    public void loadData() {
       getBookNodesData();
    }

    @Override
    protected void refreshView() {

    }

    @Override
    protected void initLayout(){
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        binding.mainRecyclerview.setLayoutManager(new GridLayoutManager(this , 2) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.mainRecyclerview.setMaxItemNumInOnePage(2);
        binding.mainRecyclerview.setAdapter(new PageableRecyclerView.Adapter<MyHolder>() {
            @Override
            public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ListView listView = new ListView(getThisActivity());
                RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(430, 1060);
                params.leftMargin = 25;
                params.rightMargin = 25;
                listView.setLayoutParams(params);
                listView.setDividerHeight(2);
                listView.setBackgroundResource(R.drawable.shape_rounded_rectangle_black_border);
                ItemBookTopChapterBinding headViewBinding = DataBindingUtil.inflate(LayoutInflater.from(getThisActivity()), R.layout.item_book_top_chapter, null, false);
                listView.addHeaderView(headViewBinding.getRoot());
                listView.setAdapter(new MyAdapter());
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        BookInfo.BookContentsBean.NodesBean node;
                        if (position == 0) {
                            node = ((MyAdapter) ((HeaderViewListAdapter) parent.getAdapter()).getWrappedAdapter()).fatherNode;
                        } else {
                            node = ((MyAdapter) ((HeaderViewListAdapter) parent.getAdapter()).getWrappedAdapter()).nodeList.get(position - 1);
                        }
                        Intent data = new Intent();
                        data.putExtra(RETURN_KEY_CHOSEN_NODE_INDEX , getNodeIndexByNodeId(node.getId()));
                        setResult(RESULT_OK , data);
                        finish();
                    }
                });
                return new MyHolder(listView, headViewBinding);
            }

            @Override
            public void onBindViewHolder(MyHolder holder, int position) {
                BookInfo.BookContentsBean.NodesBean nodes = bookStructureNodeList.get(position);
                String str = nodes.getName();
                Integer questionCount = questionItemCountMap.get(nodes.getId() + "");
                if (questionCount != null) {
                    str = str + "(" + questionCount + ")";
                }
                holder.headViewBinding.textview.setText(str);
                boolean showRedDot;
                showRedDot = false;//暂时都屏蔽红点
                if (showRedDot) {
                    holder.headViewBinding.redDot.setVisibility(View.VISIBLE);
                } else {
                    holder.headViewBinding.redDot.setVisibility(View.GONE);
                }
                ((MyAdapter) ((HeaderViewListAdapter) holder.listview.getAdapter()).getWrappedAdapter()).setFatherNode(bookStructureNodeList.get(position));
            }

            @Override
            public int getItemCount() {
                return bookStructureNodeList.size();
            }
        });
    }

    private int getNodeIndexByNodeId(int nodeId){
        List<BookInfo.BookContentsBean.NodesBean> flatNodeList = new ArrayList<BookInfo.BookContentsBean.NodesBean>();
        flatNode(bookStructureNodeList , flatNodeList);
        for (int i = 0; i < flatNodeList.size(); i++) {
            BookInfo.BookContentsBean.NodesBean node = flatNodeList.get(i);
            if (node.getId() == nodeId){
                return i;
            }
            if (i+1 == flatNodeList.size()){
                return -1;
            }
        }
        return -1;
    }

    private void flatNode(List<BookInfo.BookContentsBean.NodesBean> toFlatList , List<BookInfo.BookContentsBean.NodesBean> resultList){
        if (toFlatList == null || toFlatList.size() == 0){
            return;
        }
        else {
            for (BookInfo.BookContentsBean.NodesBean node : toFlatList) {
                int originSize = resultList.size();
                for (int i = 0; i < resultList.size(); i++) {
                    if (resultList.get(i).getId() > node.getId()){
                        resultList.add(i , node);
                        break;
                    }
                }
                if (resultList.size() == originSize){
                    resultList.add(node);
                }
                flatNode(node.getNodes() , resultList);
            }
        }
    }

    private void getAllQuestionItemInThisBook() {
        if (!NetUtils.isNetConnected()){
            DialogManager.newInstance().showNetConnDialog(getThisActivity());
            return;
        }

        NetWorkManager.getItemCountBaseOnBookNode(homeworkId , Constant.IICODE_01)
                .compose(bindToLifecycle())
                .subscribe(new Action1<List<DataCountInBookNode>>() {
                    @Override
                    public void call(List<DataCountInBookNode> dataCountInBookNodeList) {
                        questionItemCountMap.clear();
                        for (DataCountInBookNode dataCountInBookNode : dataCountInBookNodeList) {
                            questionItemCountMap.put(dataCountInBookNode.getNodeId() + "" , dataCountInBookNode.getCount());
                        }
                        binding.mainRecyclerview.notifyDataSetChanged();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        ToastUtil.showCustomToast(getThisActivity() , "获取问答题目数量失败");
                    }
                });
    }


    /**
     * 获取到书本各个章节
     */
    private void getBookNodesData() {
        NetWorkManager.queryBook(bookId + "" , null)
                .subscribe(new Action1<List<BookInfo>>() {
                    @Override
                    public void call(List<BookInfo> bookInfoList) {
                        if (bookInfoList.size() > 0 && bookInfoList.get(0).getBookContents().getNodes().size() > 0){
                            bookStructureNodeList.clear();
                            bookStructureNodeList.addAll(bookInfoList.get(0).getBookContents().getNodes());
                            binding.mainRecyclerview.notifyDataSetChanged();
                            getAllQuestionItemInThisBook();
                        }
                        else {
                            new HintDialog(getThisActivity(), "获取章节失败").show();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtils.e("FH", "获取章节失败");
                        throwable.printStackTrace();
                        new ConfirmDialog(getThisActivity() , "加载失败，请稍后重试", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                getBookNodesData();
                            }
                        }, "重试").show();
                    }
                });
    }

    private class MyHolder extends RecyclerView.ViewHolder {
        ListView listview;
        ItemBookTopChapterBinding headViewBinding;

        public MyHolder(ListView listview, ItemBookTopChapterBinding headViewBinding) {
            super(listview);
            this.listview = listview;
            this.headViewBinding = headViewBinding;
        }
    }

    private class MyAdapter extends BaseAdapter {
        private BookInfo.BookContentsBean.NodesBean fatherNode;
        private List<BookInfo.BookContentsBean.NodesBean> nodeList = new ArrayList<BookInfo.BookContentsBean.NodesBean>();

        public void setFatherNode(BookInfo.BookContentsBean.NodesBean fatherNode) {
            this.fatherNode = fatherNode;
            nodeList.clear();
            if (fatherNode.getNodes() != null && fatherNode.getNodes().size() != 0) {
                for (BookInfo.BookContentsBean.NodesBean node : fatherNode.getNodes()) {
                    addNode(node);
                }
            }
            notifyDataSetChanged();
        }

        private void addNode(BookInfo.BookContentsBean.NodesBean node) {
            nodeList.add(node);
            if (node.getNodes() != null && node.getNodes().size() != 0) {
                for (BookInfo.BookContentsBean.NodesBean childNode : node.getNodes()) {
                    addNode(childNode);
                }
            }
        }

        public BookInfo.BookContentsBean.NodesBean getFatherNode() {
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
            if (convertView == null) {
                bookChapterBinding = DataBindingUtil.inflate(LayoutInflater.from(getThisActivity()), R.layout.item_book_chapter, parent, false);
                convertView = bookChapterBinding.getRoot();
                convertView.setTag(bookChapterBinding);
            }
            bookChapterBinding = (ItemBookChapterBinding) convertView.getTag();
            BookInfo.BookContentsBean.NodesBean node = nodeList.get(position);
            String str = node.getName();
            Integer questionCount = questionItemCountMap.get(node.getId() + "");
            if (questionCount != null) {
                str = str + "(" + questionCount + ")";
            }
            bookChapterBinding.textview.setText(str);
            boolean showRedDot = false;//暂时都屏蔽红点
            if (showRedDot) {
                bookChapterBinding.redDot.setVisibility(View.VISIBLE);
            } else {
                bookChapterBinding.redDot.setVisibility(View.GONE);
            }
            if (node.getLevel() == 2) {
                convertView.setPadding(20, 0, 0, 0);
                bookChapterBinding.textview.setTextSize(TypedValue.COMPLEX_UNIT_PX, 24);
                bookChapterBinding.textview.setMaxWidth(340);
                bookChapterBinding.getRoot().getLayoutParams().height = 60;
                bookChapterBinding.arrowImg.setVisibility(View.VISIBLE);
            } else if (node.getLevel() == 3) {
                convertView.setPadding(55, 0, 0, 0);
                bookChapterBinding.textview.setTextSize(TypedValue.COMPLEX_UNIT_PX, 20);
                bookChapterBinding.textview.setMaxWidth(320);
                bookChapterBinding.getRoot().getLayoutParams().height = 44;
                bookChapterBinding.arrowImg.setVisibility(View.GONE);
            }
            return convertView;
        }
    }


}
