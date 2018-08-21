package com.yougy.homework.mistake_note;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.frank.etude.pageable.PageBtnBarAdapter;
import com.yougy.anwser.ContentDisplayer;
import com.yougy.anwser.Content_new;
import com.yougy.anwser.ParsedQuestionItem;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.new_network.RxResultHelper;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.homework.FullScreenHintDialog;
import com.yougy.homework.HomeworkBaseActivity;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityMistakeGradeBinding;

import java.util.ArrayList;

import rx.functions.Action1;

/**
 * Created by FH on 2017/11/28.
 * 错题自评界面
 */

public class MistakeGradeActivity extends HomeworkBaseActivity {
    ActivityMistakeGradeBinding binding;
    int currentShowWriteImgPageIndex = 0;
    int currentShowAnalysisPageIndex = 0;
    private ParsedQuestionItem questionItem;
    private String questionType;
    private ArrayList<Content_new> writeContentList = new ArrayList<Content_new>();
    private int homeworkId;
    private String bookTitle;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_mistake_grade, null, false);
        setContentView(binding.getRoot());
        binding.writedQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.writedQuestionBtn.setSelected(true);
                binding.answerAnalysisBtn.setSelected(false);
                refreshViewSafe();
            }
        });
        binding.answerAnalysisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.writedQuestionBtn.setSelected(false);
                binding.answerAnalysisBtn.setSelected(true);
                refreshViewSafe();
            }
        });
        binding.contentDisplayer.setContentAdapter(new ContentDisplayer.ContentAdapter() {
            @Override
            public void onPageInfoChanged(String typeKey, int newPageCount, int selectPageIndex) {
                LogUtils.e("FH", "===================onPageInfoChanged");
                binding.pageBtnBar.refreshPageBar();
            }
        });


        binding.pageBtnBar.setPageBarAdapter(new PageBtnBarAdapter(getApplicationContext()) {
            @Override
            public int getPageBtnCount() {
                if (binding.writedQuestionBtn.isSelected()) {
                    return binding.contentDisplayer.getContentAdapter().getPageCount("question");
                } else if (binding.answerAnalysisBtn.isSelected()) {
                    return binding.contentDisplayer.getContentAdapter().getPageCount("analysis");
                }
                return 0;
            }

            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn) {

                if (binding.writedQuestionBtn.isSelected()) {
                    currentShowWriteImgPageIndex = btnIndex;
                    binding.contentDisplayer.getContentAdapter().toPage("question", btnIndex, false);
                } else if (binding.answerAnalysisBtn.isSelected()) {
                    currentShowAnalysisPageIndex = btnIndex;
                    binding.contentDisplayer.getContentAdapter().toPage("analysis", btnIndex, true);
                }
            }

        });

    }

    @Override
    protected void init() {
        questionItem = getIntent().getParcelableExtra("questionItem");
        ArrayList<String> contentList = getIntent().getStringArrayListExtra("writeImgList");
        for (String url : contentList) {
            if (!TextUtils.isEmpty(url)) {
                writeContentList.add(new Content_new(Content_new.Type.IMG_URL, 0, url, null));
            }
        }
        homeworkId = getIntent().getIntExtra("homeworkId", -1);
        bookTitle = getIntent().getStringExtra("bookTitle");
        questionType = (String) questionItem.questionContentList.get(0).getExtraData();
    }

    @Override
    protected void initLayout() {

    }

    @Override
    protected void loadData() {
        binding.writedQuestionBtn.setSelected(true);
        binding.contentDisplayer.getContentAdapter().updateDataList("question", writeContentList);
        binding.contentDisplayer.getContentAdapter().updateDataList("analysis", questionItem.analysisContentList);
        if (questionType.equals("选择")) {
            binding.contentDisplayer.getContentAdapter().setSubText(RxResultHelper.parseAnswerList(questionItem.answerContentList));
        }
        if (!TextUtils.isEmpty(bookTitle)) {
            binding.subTitleTv.setText(" - " + bookTitle);
        }
        refreshViewSafe();
    }

    @Override
    protected void refreshView() {
        if (binding.writedQuestionBtn.isSelected()) {
            binding.contentDisplayer.getContentAdapter().toPage("question", currentShowWriteImgPageIndex, false);
        } else if (binding.answerAnalysisBtn.isSelected()) {
            binding.contentDisplayer.getContentAdapter().toPage("analysis", currentShowAnalysisPageIndex, true);
        }
    }

    public void back(View view) {
        finish();
    }


    //正确
    public void onRightBtnClick(View view) {
        NetWorkManager.setMistakeLastScore(homeworkId, questionItem.itemId, 100)
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        ToastUtil.showCustomToast(getApplicationContext(), "自评完成");
                        finish();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtil.showCustomToast(getApplicationContext(), "自评失败");
                    }
                });
//        YoungyApplicationManager.getRxBus(getApplicationContext()).send("lastScoreChanged:" + questionItem.itemId + ":" + 100);
    }

    //错误
    public void onWrongBtnClick(View view) {
        NetWorkManager.setMistakeLastScore(homeworkId, questionItem.itemId, 0)
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        ToastUtil.showCustomToast(getApplicationContext(), "自评完成");
                        finish();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtil.showCustomToast(getApplicationContext(), "自评失败");
                    }
                });
//        YoungyApplicationManager.getRxBus(getApplicationContext()).send("lastScoreChanged:" + questionItem.itemId + ":" + 0);
    }

    //我已学会
    public void onHasLearnedBtnCLick(View view) {
        if (!showNoNetDialog()) {
            new FullScreenHintDialog(this, "hasLearned")
                    .setIconResId(R.drawable.icon_caution_big)
                    .setContentText("是否从我的错题本中移除该题?")
                    .setBtn1("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NetWorkManager.deleteMistake(homeworkId, questionItem.itemId).subscribe(new Action1<Object>() {
                                @Override
                                public void call(Object o) {
                                    ToastUtil.showCustomToast(getApplicationContext(), "已学会");
                                    finish();
//                                YoungyApplicationManager.getRxBus(getApplicationContext()).send("removeMistakeItem:" + questionItem.itemId);
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    throwable.printStackTrace();
                                    ToastUtil.showCustomToast(getApplicationContext(), "删除错题失败");
                                }
                            });
                        }
                    }, true)
                    .setBtn2("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, false).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
        binding.contentDisplayer.clearPdfCache();
        Runtime.getRuntime().gc();
    }
}
