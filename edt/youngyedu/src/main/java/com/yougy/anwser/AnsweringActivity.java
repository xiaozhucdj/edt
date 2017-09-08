package com.yougy.anwser;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.message.EndQuestionAttachment;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityAnsweringBinding;
import com.yougy.view.dialog.HintDialog;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by FH on 2017/3/22.
 * <p>
 * 问答中界面
 */

public class AnsweringActivity extends AnswerBaseActivity {
    ActivityAnsweringBinding binding;
    ParsedQuestionItem parsedQuestionItem;

    String itemId;
    String fromUserId;
    int examId;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this) , R.layout.activity_answering, null , false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    @Override
    public void init() {
        Log.v("FH" , "AnsweringActivity init " + this.toString());
        itemId = getIntent().getStringExtra("itemId");
//        itemId = "73";
        if (TextUtils.isEmpty(itemId)){
            ToastUtil.showToast(this , "item 为空,开始问答失败");
            Log.v("FH" , "item 为空,开始问答失败");
            finish();
        }
        fromUserId = getIntent().getStringExtra("from");
//        from = "10000200";
        if (TextUtils.isEmpty(fromUserId)){
            ToastUtil.showToast(this , "from userId 为空,开始问答失败");
            Log.v("FH" , "from userId 为空,开始问答失败");
            finish();
        }
        examId = getIntent().getIntExtra("examId" , -1);
//        examId = 148;
        if (examId == -1){
            ToastUtil.showToast(this , "examId 为空,开始问答失败");
            Log.v("FH" , "examId 为空,开始问答失败");
            finish();
        }
    }

    @Override
    protected void initLayout() {

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
                                    new HintDialog(AnsweringActivity.this, "老师已经结束本次问答", "确定", new DialogInterface.OnDismissListener() {
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
    public void loadData() {
        NetWorkManager.queryQuestionItemList(fromUserId, null , itemId , null)
                .subscribe(new Action1<List<ParsedQuestionItem>>() {
                    @Override
                    public void call(List<ParsedQuestionItem> parsedQuestionItems) {
                        Log.v("FH" , "call ");
                        if (parsedQuestionItems != null && parsedQuestionItems.size() > 0){
                            parsedQuestionItem = parsedQuestionItems.get(0);
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

    public void commitAnswer(View view){
        Intent intent = new Intent(this , AnswerResultActivity.class);
        intent.putExtra("question" , parsedQuestionItem);
        startActivity(intent);
        finish();
    }
    public void startAnswer(View view){
        binding.startAnswerBtn.setVisibility(View.GONE);
        binding.pageBtnLayout.setVisibility(View.VISIBLE);
    }

    public void addPage(View view){
    }
    public void deletePage(View view){
    }
    public void cancel(View view){
        finish();
    }
    public void lastPage(View view){
    }
    public void nextPage(View view){
    }
    @Override
    protected void refreshView() {
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
    }

    public void back(View view){
        finish();
    }
}
