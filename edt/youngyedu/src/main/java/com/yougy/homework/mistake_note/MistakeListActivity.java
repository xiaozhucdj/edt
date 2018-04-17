package com.yougy.homework.mistake_note;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.frank.etude.pageBtnBar.PageBtnBarAdapter;
import com.yougy.anwser.ContentDisplayer;
import com.yougy.anwser.ParsedQuestionItem;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.homework.HomeworkBaseActivity;
import com.yougy.homework.WriteErrorHomeWorkActivity;
import com.yougy.homework.bean.HomeworkBookDetail;
import com.yougy.homework.bean.MistakeSummary;
import com.yougy.shop.bean.BookInfo;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityMistakeListBinding;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by FH on 2017/11/3.
 * 错题列表界面
 */

public class MistakeListActivity extends HomeworkBaseActivity{
    ActivityMistakeListBinding binding;
    ArrayList<MistakeSummary> mistakeSummaryList = new ArrayList<MistakeSummary>();
    ArrayList<ParsedQuestionItem> questionList = new ArrayList<ParsedQuestionItem>();
    BookInfo.BookContentsBean.NodesBean topNode , currentNode;
    ArrayList<BookInfo.BookContentsBean.NodesBean> nodeTree = new ArrayList<BookInfo.BookContentsBean.NodesBean>();
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
//                //自评界面我已学会按钮点击后,会发消息通知本界面移除错题
//                if (o instanceof String && ((String) o).startsWith("removeMistakeItem:")){
//                    String[] tempStrs = ((String) o).split(":");
//                    if (tempStrs.length == 2){
//                        int removeItemId = Integer.parseInt(tempStrs[1]);
//                        ListUtil.conditionalRemove(mistakeSummaryList, new ListUtil.ConditionJudger<MistakeSummary>() {
//                            @Override
//                            public boolean isMatchCondition(MistakeSummary nodeInList) {
//                                return nodeInList.getItem() == removeItemId;
//                            }
//                        });
//                        loadData();
//                    }
//                }
//                //自评界面自评按钮点击后,会通知本界面更新原来的上次自评结果字段
//                if (o instanceof String && ((String) o).startsWith("lastScoreChanged:")){
//                    String[] tempStrs = ((String) o).split(":");
//                    if (tempStrs.length == 3){
//                        int itemId = Integer.parseInt(tempStrs[1]);
//                        int lastScore = Integer.parseInt(tempStrs[2]);
//                        for (MistakeSummary mistakeSummary :
//                                mistakeSummaryList) {
//                            if (mistakeSummary.getItem() == itemId){
//                                mistakeSummary.getExtra().setLastScore(lastScore);
//                            }
//                        }
//                        loadData();
//                    }
//                }
            }
        }));
        super.handleEvent();
    }

    @Override
    protected void init() {
        //标记本activity在onstop后仍然接收其他界面发送的RxBus消息
        setNeedRecieveEventAfterOnStop(true);
        topNode = getIntent().getParcelableExtra("topNode");
        currentNode = getIntent().getParcelableExtra("currentNode");
        homeworkId = getIntent().getIntExtra("homeworkId" , -1);
        //找到当前章节的层级结构并显示
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

    private boolean findCurrentNode(BookInfo.BookContentsBean.NodesBean from , BookInfo.BookContentsBean.NodesBean tofind){
        if (from.getId() == tofind.getId()){
            nodeTree.add(from);
            return true;
        }
        else {
            if (from.getNodes() == null || from.getNodes().size() == 0){
                return false;
            }
            else {
                for (BookInfo.BookContentsBean.NodesBean child : from.getNodes()){
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
        binding.pageBtnBar.setPageBarAdapter(new PageBtnBarAdapter(getApplicationContext()) {
            @Override
            public int getPageBtnCount() {
                return questionList.size();
            }

            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn) {
                ParsedQuestionItem item = questionList.get(btnIndex);
                refreshItem(item);
            }
        });
        binding.contentDisplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParsedQuestionItem clickItem = questionList.get(binding.pageBtnBar.getCurrentSelectPageIndex());

                Intent intent = new Intent(getApplicationContext() , WriteErrorHomeWorkActivity.class);
                intent.putExtra("QUESTION_ITEMID" , clickItem.itemId);
                intent.putExtra("HOMEWORKID" , homeworkId);
                intent.putExtra("BOOKTITLE" , getIntent().getStringExtra("bookTitle"));
                for (MistakeSummary mistakeSummary : mistakeSummaryList) {
                    if (mistakeSummary.getItem() == Integer.parseInt(clickItem.itemId)){
                        intent.putExtra("LASTSCORE" , mistakeSummary.getExtra().getLastScore());
                    }
                }
                startActivity(intent);
            }
        });
        binding.contentDisplayer.setmContentAdaper(new ContentDisplayer.ContentAdaper(){
            @Override
            public void onPageInfoChanged(String typeKey, int newPageCount, int selectPageIndex) {
                super.onPageInfoChanged(typeKey, newPageCount, selectPageIndex);
            }
        });
    }

    public void refreshItem(ParsedQuestionItem item){
        binding.contentDisplayer.getmContentAdaper().updateDataList("question" , item.questionContentList);
        String subTextStr = "        题目类型 : " + item.questionContentList.get(0).getExtraData();
        for (MistakeSummary mistakeSummary : mistakeSummaryList) {
            if (item.itemId.equals("" + mistakeSummary.getItem())){
                subTextStr = subTextStr + "         来自于 : " + mistakeSummary.getExtra().getName();
                break;
            }
        }
        binding.contentDisplayer.getmContentAdaper().setSubText(subTextStr);
        binding.contentDisplayer.getmContentAdaper().toPage("question" , 0 , true);
    }


    @Override
    protected void loadData() {
    }

    @Override
    protected void refreshView() {
    }

    private void refreshUI(){
        if (homeworkId == -1){
            ToastUtil.showToast(getApplicationContext() , "homeworkId 为空");
            finish();
            return;
        }
        NetWorkManager.queryHomeworkBookDetail(homeworkId)
                .subscribe(new Action1<List<HomeworkBookDetail>>() {
                    @Override
                    public void call(List<HomeworkBookDetail> homeworkBookDetails) {
                        mistakeSummaryList.clear();
                        if (homeworkBookDetails != null && homeworkBookDetails.size() != 0
                                && homeworkBookDetails.get(0).getHomeworkExcerpt() != null
                                && homeworkBookDetails.get(0).getHomeworkExcerpt().size() != 0){
                            for (MistakeSummary mistakeSummary : homeworkBookDetails.get(0).getHomeworkExcerpt()) {
                                //被标记为"我已学会"的错题不算作错题,排除
                                if (!mistakeSummary.getExtra().isDeleted()){
                                    if (mistakeSummary.getExtra().getCursor() == currentNode.getId()) {
                                        mistakeSummaryList.add(mistakeSummary);
                                    }
                                }
                            }
                        }
                        if (mistakeSummaryList == null || mistakeSummaryList.size() == 0){
                            binding.noResultTextview.setVisibility(View.VISIBLE);
                            binding.pageBtnBar.setVisibility(View.GONE);
                            binding.contentDisplayer.setVisibility(View.GONE);
                            return;
                        }
                        binding.noResultTextview.setVisibility(View.GONE);
                        binding.contentDisplayer.setVisibility(View.VISIBLE);
                        binding.pageBtnBar.setVisibility(View.VISIBLE);
                        //根据传进来的错题的id的list拼接请求查询错题详情的list
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
                                binding.pageBtnBar.refreshPageBar();
                                ParsedQuestionItem item = questionList.get(binding.pageBtnBar.getCurrentSelectPageIndex());
                                refreshItem(item);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
        binding.contentDisplayer.clearPdfCache();
        Runtime.getRuntime().gc();
    }
}
