package com.yougy.homework.mistake_note;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.anwser.ParsedQuestionItem;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.homework.HomeworkBaseActivity;
import com.yougy.homework.PageableRecyclerView;
import com.yougy.homework.WriteErrorHomeWorkActivity;
import com.yougy.homework.bean.BookStructureNode;
import com.yougy.homework.bean.MistakeSummary;
import com.yougy.message.ListUtil;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityMistakeListBinding;
import com.yougy.ui.activity.databinding.ItemMistakeListBinding;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by FH on 2017/11/3.
 */

public class MistakeListActivity extends HomeworkBaseActivity{
    ActivityMistakeListBinding binding;
    ArrayList<MistakeSummary> mistakeSummaryList;
    ArrayList<ParsedQuestionItem> questionList = new ArrayList<ParsedQuestionItem>();
    BookStructureNode topNode , currentNode;
    ArrayList<BookStructureNode> nodeTree = new ArrayList<BookStructureNode>();
    int homeworkId;
    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.activity_mistake_list , null , false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    @Override
    protected void handleEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof String && ((String) o).startsWith("removeMistakeItem:")){
                    String[] tempStrs = ((String) o).split(":");
                    if (tempStrs.length == 2){
                        int removeItemId = Integer.parseInt(tempStrs[1]);
                        ListUtil.conditionalRemove(mistakeSummaryList, new ListUtil.ConditionJudger<MistakeSummary>() {
                            @Override
                            public boolean isMatchCondition(MistakeSummary nodeInList) {
                                return nodeInList.getItem() == removeItemId;
                            }
                        });
                        loadData();
                    }
                }
                else if (o instanceof String && ((String) o).startsWith("lastScoreChanged:")){
                    String[] tempStrs = ((String) o).split(":");
                    if (tempStrs.length == 3){
                        int itemId = Integer.parseInt(tempStrs[1]);
                        int lastScore = Integer.parseInt(tempStrs[2]);
                        for (MistakeSummary mistakeSummary :
                                mistakeSummaryList) {
                            if (mistakeSummary.getItem() == itemId){
                                mistakeSummary.getExtra().setLastScore(lastScore);
                            }
                        }
                        loadData();
                    }
                }
            }
        }));
        super.handleEvent();
    }

    @Override
    protected void init() {
        setNeedRecieveEventAfterOnStop(true);
        mistakeSummaryList = getIntent().getParcelableArrayListExtra("mistakeList");
        topNode = getIntent().getParcelableExtra("topNode");
        currentNode = getIntent().getParcelableExtra("currentNode");
        homeworkId = getIntent().getIntExtra("homeworkId" , -1);
        if (findCurrentNode(topNode , currentNode)){
            String currentPostionText = "";
            for (int i = nodeTree.size() - 1 ; i >=0 ; i--){
                currentPostionText = currentPostionText + nodeTree.get(i).getName();
                if (i != 0){
                    currentPostionText = currentPostionText + "  >>  ";
                }
            }
            binding.currentPositionTextview.setText(currentPostionText);
        }
    }

    private boolean findCurrentNode(BookStructureNode from , BookStructureNode tofind){
        if (from.getId() == tofind.getId()){
            nodeTree.add(from);
            return true;
        }
        else {
            if (from.getNodes() == null || from.getNodes().size() == 0){
                return false;
            }
            else {
                for (BookStructureNode child : from.getNodes()){
                    if (findCurrentNode(child , tofind)){
                        nodeTree.add(from);
                        return true;
                    }
                }
                return false;
            }
        }
    }

    @Override
    protected void initLayout() {
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.mainRecyclerview.setMaxItemNumInOnePage(4);
        binding.mainRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext() , LinearLayoutManager.VERTICAL , false));
        binding.mainRecyclerview.setAdapter(new PageableRecyclerView.Adapter<MyHolder>() {
            @Override
            public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyHolder(DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()) , R.layout.item_mistake_list , parent , false));
            }

            @Override
            public void onBindViewHolder(MyHolder holder, int position) {
                ParsedQuestionItem parsedQuestionItem = questionList.get(position);
                holder.setQuestionItem(parsedQuestionItem);
            }

            @Override
            public int getItemCount() {
                return questionList.size();
            }
        });
        binding.mainRecyclerview.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.mainRecyclerview.getRealRcyView()) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                Intent intent = new Intent(getApplicationContext() , WriteErrorHomeWorkActivity.class);
                intent.putExtra("QUESTION_ITEMID" , ((MyHolder) vh).questionItem.itemId);
                intent.putExtra("HOMEWORKID" , homeworkId);
                intent.putExtra("BOOKTITLE" , getIntent().getStringExtra("bookTitle"));
                for (MistakeSummary mistakeSummary : mistakeSummaryList) {
                    if (mistakeSummary.getItem() == Integer.parseInt(((MyHolder) vh).questionItem.itemId)){
                        intent.putExtra("LASTSCORE" , mistakeSummary.getExtra().getLastScore());
                    }
                }
                startActivity(intent);
            }
        });
        binding.mainRecyclerview.notifyDataSetChanged();
    }

    @Override
    protected void loadData() {
        if (mistakeSummaryList == null || mistakeSummaryList.size() == 0){
            binding.noResultTextview.setVisibility(View.VISIBLE);
            binding.mainRecyclerview.setVisibility(View.GONE);
            return;
        }
        binding.noResultTextview.setVisibility(View.GONE);
        binding.mainRecyclerview.setVisibility(View.VISIBLE);
        String itemIdStr = "";
        for (int i = 0; i < mistakeSummaryList.size() ; i++) {
            if (i == 0){
                itemIdStr = itemIdStr + "[";
            }
            itemIdStr = itemIdStr + mistakeSummaryList.get(i).getItem();
            if(i == mistakeSummaryList.size() - 1){
                itemIdStr = itemIdStr + "]";
            }
            else {
                itemIdStr = itemIdStr + ",";
            }
        }
        NetWorkManager.queryQuestionItemList(null , null , itemIdStr , null).subscribe(new Action1<List<ParsedQuestionItem>>() {
            @Override
            public void call(List<ParsedQuestionItem> parsedQuestionItems) {
                questionList.clear();
                questionList.addAll(parsedQuestionItems);
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

    private class MyHolder extends RecyclerView.ViewHolder{
        private ItemMistakeListBinding binding;
        private ParsedQuestionItem questionItem;
        public MyHolder(ItemMistakeListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setQuestionItem(ParsedQuestionItem item){
            questionItem = item;
            if (questionItem.questionList.get(0) instanceof ParsedQuestionItem.HtmlQuestion){
                binding.questionContainer.setHtmlUrl(((ParsedQuestionItem.HtmlQuestion) questionItem.questionList.get(0)).htmlUrl);
            }
            else if (questionItem.questionList.get(0) instanceof ParsedQuestionItem.TextQuestion){
                binding.questionContainer.setText(((ParsedQuestionItem.TextQuestion) questionItem.questionList.get(0)).text);
            }
            else if (questionItem.questionList.get(0) instanceof ParsedQuestionItem.ImgQuestion){
                binding.questionContainer.setImgUrl(((ParsedQuestionItem.ImgQuestion) questionItem.questionList.get(0)).imgUrl);
            }
            for (MistakeSummary mistakeSummary : mistakeSummaryList) {
                if (questionItem.itemId.equals("" + mistakeSummary.getItem())){
                    binding.fromTextview.setText("来自于 : " + mistakeSummary.getExtra().getName());
                    break;
                }
            }
        }
    }
}
