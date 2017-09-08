package com.yougy.anwser;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.message.EndQuestionAttachment;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityAnsweringBinding;
import com.yougy.view.dialog.HintDialog;
import com.yougy.view.NoteBookView2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
    private NoteBookView2 mNbvAnswerBoard;

    //图片地址的集合（用来保存截图生长的图片路径）
    private ArrayList<String> picPathList = new ArrayList<>();
    //byte数组集合，（用来保存每一页书写的笔记数据）
    private ArrayList<byte[]> bytesList = new ArrayList<>();
    //当前页，默认从1开始
    private int position = 1;

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

    @Override
    protected void initLayout() {


        //新建写字板，并添加到界面上
        mNbvAnswerBoard = new NoteBookView2(this);


    }

    @Override
    public void loadData() {


    }


    public void onClick(View view) {
        EpdController.leaveScribbleMode(mNbvAnswerBoard);
        switch (view.getId()) {
            case R.id.start_answer_btn:
                binding.startAnswerBtn.setVisibility(View.GONE);
                binding.pageBtnLayout.setVisibility(View.VISIBLE);
                binding.rlAnswer.addView(mNbvAnswerBoard);

                bytesList.add(mNbvAnswerBoard.bitmap2Bytes());
                ToastUtil.showToast(this, "请开始作答");

                break;
            case R.id.last_page_btn:
                /*
                上一页 ，下一页逻辑
                1.保存当期页面数据到集合中
                2.清理当前页面数据
                3.将上一页，下一页数据从集合中取出，并回复到页面
                */


                bytesList.set(position - 1, mNbvAnswerBoard.bitmap2Bytes());

                if (position == 1) {
                    ToastUtil.showToast(this, "已经是第一页了");
                    return;
                }
                mNbvAnswerBoard.clearAll();

                position--;
                binding.pageNumTv.setText(position + "/" + bytesList.size());


                byte[] tmpBytes = bytesList.get(position - 1);
                mNbvAnswerBoard.drawBitmap(BitmapFactory.decodeByteArray(tmpBytes, 0, tmpBytes.length));
                if (position == 1) {
                    binding.questionContainer.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.next_page_btn:

                binding.questionContainer.setVisibility(View.GONE);
                bytesList.set(position - 1, mNbvAnswerBoard.bitmap2Bytes());
                if (position == bytesList.size()) {
                    ToastUtil.showToast(this, "已经是最后一页了");
                    return;
                }
                mNbvAnswerBoard.clearAll();
                position++;
                binding.pageNumTv.setText(position + "/" + bytesList.size());


                tmpBytes = bytesList.get(position - 1);
                mNbvAnswerBoard.drawBitmap(BitmapFactory.decodeByteArray(tmpBytes, 0, tmpBytes.length));

                break;
            case R.id.add_page_btn:
                binding.questionContainer.setVisibility(View.GONE);

                bytesList.set(position - 1, mNbvAnswerBoard.bitmap2Bytes());
                mNbvAnswerBoard.clearAll();
                position++;
                bytesList.add(mNbvAnswerBoard.bitmap2Bytes());

                binding.pageNumTv.setText(position + "/" + bytesList.size());


                break;
            case R.id.delete_current_page_btn:
                if (position == 1) {
                    ToastUtil.showToast(this, "第一页不能删除");
                    return;
                }
                mNbvAnswerBoard.clearAll();
                bytesList.remove(position - 1);
                position--;

                binding.pageNumTv.setText(position + "/" + bytesList.size());


                tmpBytes = bytesList.get(position - 1);
                mNbvAnswerBoard.drawBitmap(BitmapFactory.decodeByteArray(tmpBytes, 0, tmpBytes.length));


                break;
            case R.id.cancle_btn:
                mNbvAnswerBoard.clearAll();
                ToastUtil.showToast(this, "清理成功");
                break;
            case R.id.commit_answer_btn:
                Intent intent = new Intent(this, AnswerResultActivity.class);
                intent.putExtra("question", parsedQuestionItem);
                startActivity(intent);
                finish();
                break;


        }

    }


    @Override
    protected void refreshView() {
        ParsedQuestionItem.Question question = parsedQuestionItem.questionList.get(0);
        binding.questionTypeTextview.setText("题目类型 : " + question.questionType);
        if (question instanceof ParsedQuestionItem.HtmlQuestion) {
            binding.questionContainer.setHtmlUrl(((ParsedQuestionItem.HtmlQuestion) question).htmlUrl);
        } else if (question instanceof ParsedQuestionItem.TextQuestion) {
            binding.questionContainer.setText(((ParsedQuestionItem.TextQuestion) question).text);
        } else if (question instanceof ParsedQuestionItem.ImgQuestion) {
            binding.questionContainer.setImgUrl(((ParsedQuestionItem.ImgQuestion) question).imgUrl);
        }
    }

    public void back(View view) {
        finish();
    }

    private void saveResultBitmap(String fileName) {
        binding.rlAnswer.setDrawingCacheEnabled(true);
        Bitmap tBitmap = binding.rlAnswer.getDrawingCache();
        // 拷贝图片，否则在setDrawingCacheEnabled(false)以后该图片会被释放掉
        tBitmap = tBitmap.createBitmap(tBitmap);
        binding.rlAnswer.setDrawingCacheEnabled(false);
        if (tBitmap != null) {
//            ivResult.setImageBitmap(tBitmap);
            saveBitmapToFile(tBitmap, fileName);
            Toast.makeText(this, "获取成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "获取失败", Toast.LENGTH_SHORT).show();
        }
    }


    public void saveBitmapToFile(Bitmap bitmap, String bitName) {
        File f = new File("/sdcard/" + bitName + ".png");
        FileOutputStream fOut = null;
        try {
            f.createNewFile();
            fOut = new FileOutputStream(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
