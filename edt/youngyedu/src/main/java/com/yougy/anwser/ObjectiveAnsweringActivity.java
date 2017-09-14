package com.yougy.anwser;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.message.EndQuestionAttachment;
import com.yougy.message.ListUtil;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityObjectiveAnsweringBinding;
import com.yougy.ui.activity.databinding.ItemAnswerChooseGridviewBinding;
import com.yougy.view.dialog.HintDialog;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by FH on 2017/9/7.
 * 选择题答题界面
 */

public class ObjectiveAnsweringActivity extends AnswerBaseActivity{
    ActivityObjectiveAnsweringBinding binding;
    ParsedQuestionItem parsedQuestionItem;
    String itemId;
    String fromUserId;
    int examId;
    ArrayList<String> checkedAnswerList = new ArrayList<String>();

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this) , R.layout.activity_objective_answering , null , false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }
    @Override
    protected void init() {
        Log.v("FH" , "AnsweringActivity init " + this.toString());
        itemId = getIntent().getStringExtra("itemId");
        itemId = "115";
        if (TextUtils.isEmpty(itemId)){
            ToastUtil.showToast(this , "item 为空,开始问答失败");
            Log.v("FH" , "item 为空,开始问答失败");
            finish();
        }
        fromUserId = getIntent().getStringExtra("from");
        fromUserId = "10000207";
        if (TextUtils.isEmpty(fromUserId)){
            ToastUtil.showToast(this , "from userId 为空,开始问答失败");
            Log.v("FH" , "from userId 为空,开始问答失败");
            finish();
        }
        examId = getIntent().getIntExtra("examId" , -1);
        examId = 148;
        if (examId == -1){
            ToastUtil.showToast(this , "examId 为空,开始问答失败");
            Log.v("FH" , "examId 为空,开始问答失败");
            finish();
        }
    }

    @Override
    protected void handleEvent() {
        super.handleEvent();
        tapEventEmitter.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if (o instanceof IMMessage){
                            if (((IMMessage) o).getAttachment() instanceof EndQuestionAttachment){
                                if (((EndQuestionAttachment) ((IMMessage) o).getAttachment()).examID == examId){
                                    new HintDialog(ObjectiveAnsweringActivity.this, "老师已经结束本次问答", "确定", new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    }).show();
                                }
                            }
                        }
                    }
                });
    }

    @Override
    protected void initLayout() {
        binding.answerContainer.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(ObjectiveAnsweringActivity.this).inflate(R.layout.item_answer_choose_gridview , parent , false);
                AutoUtils.auto(view);
                return new AnswerItemHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ParsedQuestionItem.Answer answer = parsedQuestionItem.answerList.get(position);
                ((AnswerItemHolder) holder).setAnswer(answer);
            }

            @Override
            public int getItemCount() {
                if (parsedQuestionItem != null){
                    return parsedQuestionItem.answerList.size();
                }
                else {
                    return 0;
                }
            }
        });
        binding.answerContainer.setLayoutManager(new GridLayoutManager(this , 4));
        binding.answerContainer.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.answerContainer) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                ((AnswerItemHolder) vh).reverseCheckbox();
            }
        });
    }

    @Override
    protected void loadData() {
        NetWorkManager.queryQuestionItemList(fromUserId, null , itemId , null)
                .subscribe(new Action1<List<ParsedQuestionItem>>() {
                    @Override
                    public void call(List<ParsedQuestionItem> parsedQuestionItems) {
                        Log.v("FH" , "call ");
                        if (parsedQuestionItems != null && parsedQuestionItems.size() > 0){
                            parsedQuestionItem = parsedQuestionItems.get(0);
                            if (parsedQuestionItem.questionList.size() <= 0){
                                ToastUtil.showToast(getApplicationContext() , "题干内容为空");
                                return;
                            }
                            else if (!parsedQuestionItem.questionList.get(0).questionType.equals("选择")){
                                ToastUtil.showToast(getApplicationContext() , "题目类型错误,题目不为选择题");
                                return;
                            }
                            refreshView();
                        }
                        else {
                            ToastUtil.showToast(getApplicationContext() , "获取到的题目为空,开始问答失败");
                            Log.v("FH" , "获取到的题目为空,开始问答失败");
                            finish();
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
        if (parsedQuestionItem.questionList.size() <= 0){
            ToastUtil.showToast(getApplicationContext() , "题干内容为空");
            return;
        }
        ParsedQuestionItem.Question question = parsedQuestionItem.questionList.get(0);
        binding.questionTypeTextview.setText("题目类型 : " + question.questionType);
        if (question instanceof ParsedQuestionItem.HtmlQuestion){
            binding.questionContainer.setHtmlUrl(((ParsedQuestionItem.HtmlQuestion) question).htmlUrl);
        }
        else if (question instanceof ParsedQuestionItem.TextQuestion){
            binding.questionContainer.setText(((ParsedQuestionItem.TextQuestion) question).text);
        }
        else if (question instanceof ParsedQuestionItem.ImgQuestion){
            binding.questionContainer.setImgUrl(((ParsedQuestionItem.ImgQuestion) question).imgUrl);
        }

        binding.answerContainer.getAdapter().notifyDataSetChanged();
    }

    public void commitAnswer(View view){
        Intent intent = new Intent(this , AnswerResultActivity.class);
        intent.putExtra("question" , parsedQuestionItem);
        startActivity(intent);
        finish();
    }
    public void startAnswer(View view){
        binding.startAnswerBtn.setVisibility(View.GONE);
        binding.answerContainer.setVisibility(View.VISIBLE);
    }

    public void back(View view){
        finish();
    }

    public class AnswerItemHolder extends RecyclerView.ViewHolder{
        ItemAnswerChooseGridviewBinding itemBinding;
        ParsedQuestionItem.Answer answer;
        public AnswerItemHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }

        public AnswerItemHolder setAnswer(ParsedQuestionItem.Answer answer) {
            this.answer = answer;
            if (answer instanceof ParsedQuestionItem.TextAnswer){
                itemBinding.textview.setText(((ParsedQuestionItem.TextAnswer) answer).text);
                if (ListUtil.conditionalContains(checkedAnswerList, new ListUtil.ConditionJudger<String>() {
                    @Override
                    public boolean isMatchCondition(String nodeInList) {
                        return nodeInList.equals(((ParsedQuestionItem.TextAnswer) answer).text);
                    }
                })){
                    itemBinding.checkbox.setSelected(true);
                }
                else {
                    itemBinding.checkbox.setSelected(false);
                }
            }
            else {
                itemBinding.textview.setText("格式错误");
                itemBinding.checkbox.setSelected(false);
            }
            return this;
        }

        public void reverseCheckbox(){
            if (answer instanceof ParsedQuestionItem.TextAnswer){
                if (itemBinding.checkbox.isSelected()){
                    checkedAnswerList.remove(((ParsedQuestionItem.TextAnswer) answer).text);
                }
                else {
                    checkedAnswerList.add(((ParsedQuestionItem.TextAnswer) answer).text);
                }
                binding.answerContainer.getAdapter().notifyDataSetChanged();
            }
        }
    }

}
