package com.yougy.anwser;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.ToastUtil;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityAnsweringBinding;
import com.yougy.view.NoteBookView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by FH on 2017/3/22.
 * <p>
 * 问答中界面
 */

public class AnsweringActivity extends BaseActivity {
    ActivityAnsweringBinding binding;
    ParsedQuestionItem parsedQuestionItem;
    private NoteBookView mNbvAnswerBoard;


    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_answering, null, false);
        setContentView(binding.getRoot());
    }

    @Override
    public void init() {
        Log.v("FH", "AnsweringActivity init " + this.toString());
        String itemId = getIntent().getStringExtra("itemId");
        itemId = "73";
        if (TextUtils.isEmpty(itemId)) {
            ToastUtil.showToast(this, "item 为空,开始问答失败");
            Log.v("FH", "item 为空,开始问答失败");
            finish();
        }
        String from = getIntent().getStringExtra("from");
        from = "10000200";
        if (TextUtils.isEmpty(from)) {
            ToastUtil.showToast(this, "from userId 为空,开始问答失败");
            Log.v("FH", "from userId 为空,开始问答失败");
            finish();
        }
        NetWorkManager.queryQuestionItemList(from, null, itemId, null)
                .subscribe(new Action1<List<ParsedQuestionItem>>() {
                    @Override
                    public void call(List<ParsedQuestionItem> parsedQuestionItems) {
                        Log.v("FH", "call ");
                        if (parsedQuestionItems != null && parsedQuestionItems.size() > 0) {
                            parsedQuestionItem = parsedQuestionItems.get(0);
                            refreshView();
                        } else {
                            ToastUtil.showToast(getApplicationContext(), "获取到的题目为空,开始问答失败");
                            Log.v("FH", "获取到的题目为空,开始问答失败");
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
        mNbvAnswerBoard = new NoteBookView(this);


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

                ToastUtil.showToast(this,"请开始作答");

                break;
            case R.id.last_page_btn:
                break;
            case R.id.next_page_btn:
                break;
            case R.id.add_page_btn:
                saveResultBitmap();




                break;
            case R.id.delete_current_page_btn:
                break;
            case R.id.cancle_btn:
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

    private void saveResultBitmap() {
        binding.rlAnswer.setDrawingCacheEnabled(true);
        Bitmap tBitmap = binding.rlAnswer.getDrawingCache();
        // 拷贝图片，否则在setDrawingCacheEnabled(false)以后该图片会被释放掉
        tBitmap = tBitmap.createBitmap(tBitmap);
        binding.rlAnswer.setDrawingCacheEnabled(false);
        if (tBitmap != null) {
//            ivResult.setImageBitmap(tBitmap);
            saveBitmapToFile(tBitmap, "adsf");
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
