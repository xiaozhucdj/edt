package com.yougy.homework;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yougy.anwser.ContentDisplayer;
import com.yougy.anwser.Content_new;
import com.yougy.anwser.ParsedQuestionItem;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.RefreshUtil;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.OnItemClickListener;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.homework.mistake_note.MistakeGradeActivity;
import com.yougy.message.ListUtil;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ItemAnswerChooseGridviewBinding;
import com.yougy.view.CustomGridLayoutManager;
import com.yougy.view.CustomLinearLayoutManager;
import com.yougy.view.NoteBookView2;
import com.yougy.view.dialog.ConfirmDialog;
import com.zhy.autolayout.utils.AutoUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.functions.Action1;

/**
 * Created by cdj
 * 写错题界面
 */
public class WriteErrorHomeWorkActivity extends BaseActivity {

    @BindView(R.id.iv_chooese_tag)
    ImageView ivChooeseTag;
    @BindView(R.id.rcv_all_question_page)
    RecyclerView allQuestionPage;
    @BindView(R.id.rcv_chooese_item)
    RecyclerView rcvChooese;
    @BindView(R.id.ll_chooese_item)
    LinearLayout llChooeseItem;
    @BindView(R.id.content_displayer)
    ContentDisplayer contentDisplayer;
    @BindView(R.id.rl_answer)
    RelativeLayout rlAnswer;
    @BindView(R.id.tv_submit_homework)
    TextView tvSubmitHomeWork;
    @BindView(R.id.tv_add_page)
    TextView tvAddPage;
    @BindView(R.id.tv_clear_write)
    TextView tvClearWrite;

    @BindView(R.id.iv_last_zp_result)
    ImageView ivZpResult;
    @BindView(R.id.tv_last_zp_result)
    TextView tvZpResult;
    @BindView(R.id.tv_caogao_text)
    TextView tvCaogaoText;
    @BindView(R.id.ll_caogao_control)
    LinearLayout llCaogaoControl;
    @BindView(R.id.rl_caogao_box)
    RelativeLayout rlCaogaoBox;
    @BindView(R.id.tv_check_score)
    TextView tvCheckScore;
    @BindView(R.id.iv_check_result)
    ImageView ivCheckResult;
    @BindView(R.id.title_tv)
    TextView titleTv;

    private NoteBookView2 mNbvAnswerBoard;
    //作业草稿纸
    private NoteBookView2 mCaogaoNoteBoard;

    private static final int PAGE_SHOW_SIZE = 5;

    private int questionPageSize = 1;

    //底部页码数偏移量
    private int pageDeviationNum = 0;
    private QuestionPageNumAdapter questionPageNumAdapter;

    //底部某一题多页数据
    private List<Content_new> questionList;
    //如果是选择题，这里存储选择题的结果
    private List<ParsedQuestionItem.Answer> chooeseAnswerList;
    //选择题选择的结果
    private ArrayList<String> checkedAnswerList = new ArrayList<String>();

    //保存当前题目页面分页，默认从0开始
    private int saveQuestionPage = 0;

    //是否添加了手写板
    private boolean isAddAnswerBoard;
    //byte数组集合，（用来保存每一页书写的笔记数据）
    private ArrayList<byte[]> bytesList = new ArrayList<>();
    //存储每一页截屏图片地址
    private ArrayList<String> pathList = new ArrayList<>();
    //byte数组集合，（用来保存每一页草稿的笔记数据）
    private ArrayList<byte[]> cgBytes = new ArrayList<>();

    //是否第一次自动点击进入第一页
    private boolean isFirstComeInQuestion;

    private int homeworkId;
    private int lastScore, replyScore;
    private String bookTitle;
    private ParsedQuestionItem parsedQuestionItem;
    private Integer itemWeight;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_write_error_homework);
    }

    @Override
    protected void init() {
        YoungyApplicationManager.NEED_PROMOTION = false;
        homeworkId = getIntent().getIntExtra("HOMEWORKID", -1);
        bookTitle = getIntent().getStringExtra("BOOKTITLE");
        lastScore = getIntent().getIntExtra("LASTSCORE", -1);
        replyScore = getIntent().getIntExtra("REPLYSCORE", -1);
        itemWeight = (Integer) getIntent().getSerializableExtra("REPLYITEMWEIGHT");

        parsedQuestionItem = (ParsedQuestionItem) getIntent().getParcelableExtra("PARSEDQUESTIONITEM");

    }


    @Override
    protected void initLayout() {
        if (!TextUtils.isEmpty(bookTitle)) {
            titleTv.setText(bookTitle);
        }

        //新建写字板，并添加到界面上
        mNbvAnswerBoard = new NoteBookView2(WriteErrorHomeWorkActivity.this);

        mCaogaoNoteBoard = new NoteBookView2(this, 960, 420);
        switch (lastScore) {
            case 0:
                tvZpResult.setText("上次自评结果 : ");
                ivZpResult.setVisibility(View.VISIBLE);
                ivZpResult.setImageResource(R.drawable.img_ziping_cuowu);
                break;
            case 50:
                tvZpResult.setText("上次自评结果 : ");
                ivZpResult.setVisibility(View.VISIBLE);
                ivZpResult.setImageResource(R.drawable.img_ziping_bandui);
                break;
            case 100:
                tvZpResult.setText("上次自评结果 : ");
                ivZpResult.setVisibility(View.VISIBLE);
                ivZpResult.setImageResource(R.drawable.img_ziping_zhengque);
                break;
            case -1:
                tvZpResult.setText("上次自评结果 : 无");
                ivZpResult.setVisibility(View.GONE);
                break;
        }

        if (getIntent().getBooleanExtra("ISDELETED", false)) {
            tvZpResult.setText("上次自评结果 : 已学会");
            ivZpResult.setVisibility(View.GONE);
        }


        ContentDisplayer.ContentAdapter contentAdapter = new ContentDisplayer.ContentAdapter() {

            @Override
            public void onPageInfoChanged(String typeKey, int newPageCount, int selectPageIndex) {
                super.onPageInfoChanged(typeKey, newPageCount, selectPageIndex);

                //获取到最新的页码数后，刷新需要存储数据的集合（笔记，草稿笔记，图片地址），刷新该题的多页角标，展示显示选择页面题目。
                if (newPageCount > questionPageSize) {
                    //需要添加的页码数目。
                    int newAddPageNum = newPageCount - questionPageSize;

                    for (int i = 0; i < newAddPageNum; i++) {
                        bytesList.add(null);
                        pathList.add(null);
                        cgBytes.add(null);
                    }

                    //更新最新的页面数据
                    questionPageSize = newPageCount;
                    questionPageNumAdapter.notifyDataSetChanged();

                }

            }
        };
        contentDisplayer.setContentAdapter(contentAdapter);

        contentDisplayer.setOnLoadingStatusChangedListener(new ContentDisplayer.OnLoadingStatusChangedListener() {
            @Override
            public void onLoadingStatusChanged(ContentDisplayer.LOADING_STATUS loadingStatus) {

                if (questionList == null) {
                    return;
                }

                if (questionList.get(0) == null) {
                    return;
                }

                if ("选择".equals(questionList.get(0).getExtraData()) || "判断".equals(questionList.get(0).getExtraData())) {
                    mNbvAnswerBoard.setVisibility(View.GONE);
                } else {
                    if (loadingStatus == ContentDisplayer.LOADING_STATUS.SUCCESS) {
                        mNbvAnswerBoard.setVisibility(View.VISIBLE);
                    } else {
                        mNbvAnswerBoard.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    protected void loadData() {
        fillData();
    }

    //填充数据
    private void fillData() {
        if (parsedQuestionItem == null) {
            ToastUtil.showCustomToast(getBaseContext(), "该题可能已经被删除");
            return;
        }
        questionList = parsedQuestionItem.questionContentList;
        contentDisplayer.getContentAdapter().updateDataList("question", (ArrayList<Content_new>) questionList);
        if (questionList != null && questionList.size() > 0) {

            questionPageSize = questionList.size();

            //作业中某一题题目、答案切换
            questionPageNumAdapter = new QuestionPageNumAdapter();
            CustomLinearLayoutManager linearLayoutManager = new CustomLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            linearLayoutManager.setScrollHorizontalEnabled(false);
            allQuestionPage.setLayoutManager(linearLayoutManager);
            allQuestionPage.setAdapter(questionPageNumAdapter);

            questionPageNumAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick1(int position) {
//                    ToastUtil.showCustomToast(WriteErrorHomeWorkActivity.this, position + 1 + "页");

                    //离开手绘模式，并刷新界面ui
                    if (mNbvAnswerBoard != null) {
                        mNbvAnswerBoard.leaveScribbleMode(true);
                    }

                    if (mCaogaoNoteBoard != null && mCaogaoNoteBoard.getVisibility() == View.VISIBLE) {
                        mCaogaoNoteBoard.leaveScribbleMode(true);
                    }

                    if (isFirstComeInQuestion) {
                        isFirstComeInQuestion = false;
                    } else {
                        //如果草稿纸打开着，需要先将草稿纸隐藏。用于截图
                        if (llCaogaoControl.getVisibility() == View.VISIBLE) {
                            cgBytes.set(saveQuestionPage, mCaogaoNoteBoard.bitmap2Bytes());

                            tvCaogaoText.setText("草稿纸");
                            mCaogaoNoteBoard.clearAll();
                            llCaogaoControl.setVisibility(View.GONE);
                            mNbvAnswerBoard.setIntercept(false);
                        }
                        //如果 mNbvAnswerBoard是显示的说明是非选择题，需要保持笔记
                        if (mNbvAnswerBoard.getVisibility() == View.VISIBLE) {
                            //保存上一个题目多页数据中的某一页手写笔记。
                            bytesList.set(saveQuestionPage, mNbvAnswerBoard.bitmap2Bytes());
                        }
                        //是否是选择题。都需要截屏保存图片
                        pathList.set(saveQuestionPage, saveBitmapToFile(saveScreenBitmap()));
                    }

                    mNbvAnswerBoard.clearAll();

                    //将本页设置为选中页
                    saveQuestionPage = position;


                    if (position < contentDisplayer.getContentAdapter().getPageCount("question")) {
                        //切换当前题目的分页
                        contentDisplayer.getContentAdapter().toPage("question", position, false);
                        contentDisplayer.setVisibility(View.VISIBLE);
                    } else {
                        //加白纸
                        contentDisplayer.setVisibility(View.GONE);

                    }
                    if (questionList.get(0) != null) {
                        if ("选择".equals(questionList.get(0).getExtraData())) {
                            if (isAddAnswerBoard) {
                                rlAnswer.removeView(mNbvAnswerBoard);
                                isAddAnswerBoard = false;
                            }
                            rcvChooese.setVisibility(View.VISIBLE);
                            llChooeseItem.setVisibility(View.GONE);

                            //选择题不能加页
                            tvAddPage.setVisibility(View.GONE);
                            tvClearWrite.setVisibility(View.GONE);
                            chooeseAnswerList = parsedQuestionItem.answerList;

                            setChooeseResult();

                            //刷新当前选择结果的reciv
                            if (rcvChooese.getAdapter() != null) {
                                rcvChooese.getAdapter().notifyDataSetChanged();
                            }

                        } else if ("判断".equals(questionList.get(0).getExtraData())) {

                            if (isAddAnswerBoard) {
                                rlAnswer.removeView(mNbvAnswerBoard);
                                isAddAnswerBoard = false;
                            }
                            rcvChooese.setVisibility(View.GONE);
                            llChooeseItem.setVisibility(View.VISIBLE);
                            //选择题不能加页
                            tvAddPage.setVisibility(View.GONE);
                            tvClearWrite.setVisibility(View.GONE);


                        } else {
                            if (!isAddAnswerBoard) {
                                RelativeLayout.LayoutParams layer1LayoutParam = new RelativeLayout.LayoutParams(960, 920);
                                rlAnswer.addView(mNbvAnswerBoard, layer1LayoutParam);
                                isAddAnswerBoard = true;
                            }
                            rcvChooese.setVisibility(View.GONE);
                            llChooeseItem.setVisibility(View.GONE);
                            tvAddPage.setVisibility(View.VISIBLE);
                            tvClearWrite.setVisibility(View.VISIBLE);

                            //从之前bytesList中回显之前保存的手写笔记，如果有的话
                            if (bytesList.size() > position) {
                                byte[] tmpBytes = bytesList.get(position);
                                if (tmpBytes != null) {
                                    mNbvAnswerBoard.drawBitmap(BitmapFactory.decodeByteArray(tmpBytes, 0, tmpBytes.length));
                                }
                            }

                        }
                    }
                    questionPageNumAdapter.notifyDataSetChanged();


                    //以下逻辑为调整列表中角标位置。
                    if (questionPageSize <= PAGE_SHOW_SIZE) {
                        return;
                    }

                    if (position <= 2) {
                        pageDeviationNum = 0;
                    } else {
                        pageDeviationNum = (position - 2);
                    }
                    moveToPosition(linearLayoutManager, pageDeviationNum);


                }
            });


            for (int i = 0; i < questionPageSize; i++) {
                bytesList.add(null);
                pathList.add(null);
                cgBytes.add(null);
            }

            isFirstComeInQuestion = true;
            questionPageNumAdapter.onItemClickListener.onItemClick1(0);


            //不是记分题
            if (itemWeight == null) {

                tvCheckScore.setVisibility(View.GONE);
                switch (replyScore) {
                    case -1://说明未批改
                        //异常情况（进入错题笨了，就不可能未批改）
                        break;
                    case 0://判错
                        ivCheckResult.setImageResource(R.drawable.img_cuowu);
                        break;
                    case 50://判半对
                        ivCheckResult.setImageResource(R.drawable.img_bandui);
                        break;
                    case 100://判对
                        ivCheckResult.setImageResource(R.drawable.img_zhengque);

                        break;
                }
            } else {
                tvCheckScore.setVisibility(View.VISIBLE);

                //记分题
                //未批改
                if (replyScore == -1) {
                    //异常情况（进入错题笨了，就不可能未批改）

                } else if (replyScore == 0) {
                    //错误
                    ivCheckResult.setImageResource(R.drawable.img_cuowu);
                    tvCheckScore.setText("（" + replyScore + "分）");
                } else {
                    //满分
                    if (itemWeight == replyScore) {
                        ivCheckResult.setImageResource(R.drawable.img_zhengque);
                        tvCheckScore.setText("（" + replyScore + "分）");
                    } else {
                        //半对
                        ivCheckResult.setImageResource(R.drawable.img_bandui);
                        tvCheckScore.setText("（" + replyScore + "分）");
                    }
                }
            }


        } else {
            ToastUtil.showCustomToast(getBaseContext(), "该题可能已经被删除");
        }
    }


    /**
     * 设置选择题的结果界面
     */
    private void setChooeseResult() {

        //清理掉其他题中的作业结果。
        checkedAnswerList.clear();

        rcvChooese.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(WriteErrorHomeWorkActivity.this).inflate(R.layout.item_answer_choose_gridview, parent, false);
                AutoUtils.auto(view);
                return new AnswerItemHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ParsedQuestionItem.Answer answer = chooeseAnswerList.get(position);
                ((AnswerItemHolder) holder).setAnswer(answer);
            }

            @Override
            public int getItemCount() {
                if (chooeseAnswerList != null) {
                    return chooeseAnswerList.size();
                } else {
                    return 0;
                }
            }
        });
        CustomGridLayoutManager gridLayoutManager = new CustomGridLayoutManager(this, chooeseAnswerList.size());
        gridLayoutManager.setScrollEnabled(false);
        rcvChooese.setLayoutManager(gridLayoutManager);
        rcvChooese.addOnItemTouchListener(new OnRecyclerItemClickListener(rcvChooese) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                ((AnswerItemHolder) vh).reverseCheckbox();
            }
        });

    }


    /**
     * RecyclerView 移动到当前位置，
     *
     * @param manager 设置RecyclerView对应的manager
     * @param n       要跳转的位置
     */
    public static void moveToPosition(LinearLayoutManager manager, int n) {
        manager.scrollToPositionWithOffset(n, 0);
        manager.setStackFromEnd(true);
    }

    @Override
    protected void refreshView() {

    }

    private static long lastClickTime;

    @OnClick({R.id.tv_submit_homework, R.id.tv_clear_write, R.id.tv_add_page, R.id.btn_left, R.id.tv_caogao_text, R.id.tv_dismiss_caogao})
    public void onClick(View view) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 2000) {
            return;
        }
        lastClickTime = time;

        if (mNbvAnswerBoard != null) {
            mNbvAnswerBoard.leaveScribbleMode(true);
        }

        if (mCaogaoNoteBoard != null && mCaogaoNoteBoard.getVisibility() == View.VISIBLE) {
            mCaogaoNoteBoard.leaveScribbleMode(true);
        }

        switch (view.getId()) {

            case R.id.btn_left:
                finish();
                break;
            case R.id.tv_submit_homework:

                submitNum();

                break;
            case R.id.tv_clear_write:

                new ConfirmDialog(WriteErrorHomeWorkActivity.this, "是否清空作答笔迹？",
                        "取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        },
                        "确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mNbvAnswerBoard.clearAll();
                                RefreshUtil.invalidate(rlAnswer);
                            }
                        }).show();

                break;
            case R.id.tv_add_page:
                if (questionPageSize - contentDisplayer.getContentAdapter().getPageCount("question") > 5) {
                    ToastUtil.showCustomToast(this, "最多只能加5张纸");
                    return;
                }

                questionPageSize++;
                bytesList.add(null);
                pathList.add(null);
                cgBytes.add(null);
                questionPageNumAdapter.notifyDataSetChanged();
                questionPageNumAdapter.onItemClickListener.onItemClick1(questionPageSize - 1);

                break;

            case R.id.tv_caogao_text:

                if (tvCaogaoText.getText().toString().startsWith("扔掉")) {
                    tvCaogaoText.setText("草稿纸");

                    cgBytes.set(saveQuestionPage, null);
                    mCaogaoNoteBoard.clearAll();
                    llCaogaoControl.setVisibility(View.GONE);
                    mNbvAnswerBoard.setIntercept(false);

                    if (rlCaogaoBox.getChildCount() > 0) {
                        rlCaogaoBox.removeView(mCaogaoNoteBoard);
                    }

                } else {
                    tvCaogaoText.setText("扔掉\n草稿纸");
                    llCaogaoControl.setVisibility(View.VISIBLE);
                    mNbvAnswerBoard.setIntercept(true);

                    if (rlCaogaoBox.getChildCount() == 0) {
                        rlCaogaoBox.addView(mCaogaoNoteBoard);
                    }
                    //TODO:yuanye 草稿纸在隐藏的时候，暂存时候没有保存，然后再次作答 打开草稿纸 角标越界
                    if (cgBytes != null && cgBytes.size() > 0 && saveQuestionPage <= cgBytes.size()) {
                        byte[] tmpBytes = cgBytes.get(saveQuestionPage);
                        if (tmpBytes != null) {
                            mCaogaoNoteBoard.drawBitmap(BitmapFactory.decodeByteArray(tmpBytes, 0, tmpBytes.length));
                        }
                    }
                }


                break;
            case R.id.tv_dismiss_caogao:
                if (llCaogaoControl.getVisibility() == View.VISIBLE) {
                    tvCaogaoText.setText("草稿纸");
                    cgBytes.set(saveQuestionPage, mCaogaoNoteBoard.bitmap2Bytes());
                    llCaogaoControl.setVisibility(View.GONE);
                    UIUtils.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mNbvAnswerBoard != null) {
                                mNbvAnswerBoard.setIntercept(false);
                            }
                        }
                    }, 600);
                }

                break;

        }
    }

    /**
     * 增加提交次数发送给服务器
     */
    public void submitNum() {
        NetWorkManager.setMistakeExcerpt(homeworkId, getIntent().getIntExtra("REPLYID", -1) + "", "submitNum", (getIntent().getIntExtra("SUBMITNUM", 0) + 1))
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        saveHomeWorkData();
                        gotoMistakeGradeActivity();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtil.showCustomToast(getApplicationContext(), "提交失败，服务器异常，请稍后重试");
                    }
                });
    }


    private void gotoMistakeGradeActivity() {
        //TODO 此处跳转到错题判断界面
        Intent intent = new Intent(getApplicationContext(), MistakeGradeActivity.class);
        intent.putStringArrayListExtra("WRITEIMGLIST", pathList);
        intent.putExtra("PARSEDQUESTIONITEM", parsedQuestionItem);
        intent.putExtra("HOMEWORKID", homeworkId);
        intent.putExtra("BOOKTITLE", bookTitle);
        intent.putExtra("REPLYID", getIntent().getIntExtra("REPLYID", -1));
        startActivity(intent);
        finish();
    }

    /**
     * 保存之前操作题目结果数据
     */
    private void saveHomeWorkData() {

        if (bytesList.size() == 0) {
            return;
        }
        if (pathList.size() == 0) {
            return;
        }

        //刷新最后没有保存的数据
        bytesList.set(saveQuestionPage, mNbvAnswerBoard.bitmap2Bytes());
        pathList.set(saveQuestionPage, saveBitmapToFile(saveScreenBitmap()));
        mNbvAnswerBoard.clearAll();
    }


    /**
     * 获取可操作区域的截图
     *
     * @return 图片的bitmap
     */
    private Bitmap saveScreenBitmap() {
        rlAnswer.setDrawingCacheEnabled(true);
        Bitmap tBitmap = rlAnswer.getDrawingCache();
        // 拷贝图片，否则在setDrawingCacheEnabled(false)以后该图片会被释放掉
        tBitmap = Bitmap.createBitmap(tBitmap);
        rlAnswer.setDrawingCacheEnabled(false);
        return tBitmap;
    }

    public String saveBitmapToFile(Bitmap bitmap) {

        String fileDir = FileUtils.getAppFilesDir() + "/homework_result";
        FileUtils.createDirs(fileDir);


        String bitName = System.currentTimeMillis() + "";
        File f = new File(fileDir, bitName + ".png");
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
        } finally {
            bitmap.recycle();
        }
        return f.getAbsolutePath();
    }


    /*底部页面的adapter相关*/
    class QuestionPageNumViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_page_id)
        TextView mTvPageId;


        public QuestionPageNumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            int margin = UIUtils.dip2px(10);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((UIUtils.getScreenWidth() - UIUtils.dip2px(660) - PAGE_SHOW_SIZE * 2 * margin) / PAGE_SHOW_SIZE, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(margin, 0, margin, 0);
            itemView.setLayoutParams(params);

        }
    }

    class QuestionPageNumAdapter extends RecyclerView.Adapter<QuestionPageNumViewHolder> {

        OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public QuestionPageNumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page_check_homework, parent, false);
            return new QuestionPageNumViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(QuestionPageNumViewHolder holder, final int position) {

            holder.mTvPageId.setText((position + 1) + "");

            if (position == saveQuestionPage) {
                holder.mTvPageId.setBackgroundResource(R.drawable.img_press_question_bg);
                holder.mTvPageId.setTextColor(getResources().getColor(R.color.white));
            } else {
                holder.mTvPageId.setBackgroundResource(R.drawable.img_normal_question_bg);
                holder.mTvPageId.setTextColor(getResources().getColor(R.color.black));
            }

            holder.mTvPageId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick1(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return questionPageSize;
        }
    }


    public class AnswerItemHolder extends RecyclerView.ViewHolder {
        ItemAnswerChooseGridviewBinding itemBinding;
        ParsedQuestionItem.Answer answer;

        public AnswerItemHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }

        public AnswerItemHolder setAnswer(ParsedQuestionItem.Answer answer) {
            this.answer = answer;
            if (answer instanceof ParsedQuestionItem.TextAnswer) {
                itemBinding.textview.setText(((ParsedQuestionItem.TextAnswer) answer).text);
                if (ListUtil.conditionalContains(checkedAnswerList, new ListUtil.ConditionJudger<String>() {
                    @Override
                    public boolean isMatchCondition(String nodeInList) {
                        return nodeInList.equals(((ParsedQuestionItem.TextAnswer) answer).text);
                    }
                })) {
                    itemBinding.checkbox.setSelected(true);
                    itemBinding.textview.setSelected(true);
                } else {
                    itemBinding.checkbox.setSelected(false);
                    itemBinding.textview.setSelected(false);
                }
            } else {
                itemBinding.textview.setText("格式错误");
                itemBinding.checkbox.setSelected(false);
            }
            return this;
        }

        public void reverseCheckbox() {
            if (answer instanceof ParsedQuestionItem.TextAnswer) {
                if (itemBinding.checkbox.isSelected()) {
                    checkedAnswerList.remove(((ParsedQuestionItem.TextAnswer) answer).text);
                } else {
                    String chooeseText = ((ParsedQuestionItem.TextAnswer) answer).text;
                    if (!checkedAnswerList.contains(chooeseText)) {
                        checkedAnswerList.add(chooeseText);
                    }
                }
                rcvChooese.getAdapter().notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNbvAnswerBoard != null) {
            mNbvAnswerBoard.recycle();
        }
        mNbvAnswerBoard = null;

        if (mCaogaoNoteBoard != null) {
            mCaogaoNoteBoard.recycle();
        }
        mCaogaoNoteBoard = null;

        if (questionList != null) {
            questionList.clear();
        }
        questionList = null;

        if (chooeseAnswerList != null) {
            chooeseAnswerList.clear();
        }
        chooeseAnswerList = null;

        if (checkedAnswerList != null) {
            checkedAnswerList.clear();
        }
        checkedAnswerList = null;

        if (bytesList != null) {
            bytesList.clear();
        }
        bytesList = null;


        if (pathList != null) {
            pathList.clear();
        }
        pathList = null;

        if (cgBytes != null) {
            cgBytes.clear();
        }
        cgBytes = null;

        Glide.get(this).clearMemory();
        contentDisplayer.clearPdfCache();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        if (event.getType().equalsIgnoreCase(EventBusConstant.EVENT_ANSWERING_SHOW)) {
            LogUtils.i("type .." + event.getType());
            if (mNbvAnswerBoard != null) {
                mNbvAnswerBoard.leaveScribbleMode();
                mNbvAnswerBoard.setIntercept(true);
            }

            if (mCaogaoNoteBoard != null) {
                mCaogaoNoteBoard.leaveScribbleMode();
                mCaogaoNoteBoard.setIntercept(true);
            }
            BaseEvent baseEvent = new BaseEvent(EventBusConstant.EVENT_ANSWERING_RESULT, "");
            EventBus.getDefault().post(baseEvent);
        } else if (event.getType().equalsIgnoreCase(EventBusConstant.EVENT_ANSWERING_PUASE) || (event.getType().equalsIgnoreCase(EventBusConstant.EVENT_LOCKER_ACTIVITY_PUSE))) {
            LogUtils.i("type .." + event.getType());
            if (mNbvAnswerBoard != null) {
                mNbvAnswerBoard.setIntercept(false);
            }

            if (mCaogaoNoteBoard != null) {
                mCaogaoNoteBoard.setIntercept(false);
            }
        } else if (event.getType().equalsIgnoreCase(EventBusConstant.EVENT_START_ACTIIVTY_ORDER)) {
            LogUtils.i("type .." + event.getType());
            if (mNbvAnswerBoard != null) {
                mNbvAnswerBoard.leaveScribbleMode();
                mNbvAnswerBoard.setIntercept(true);
            }

            if (mCaogaoNoteBoard != null) {
                mCaogaoNoteBoard.leaveScribbleMode();
                mCaogaoNoteBoard.setIntercept(true);
            }
            BaseEvent baseEvent = new BaseEvent(EventBusConstant.EVENT_START_ACTIIVTY_ORDER_RESULT, "");
            EventBus.getDefault().post(baseEvent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        YoungyApplicationManager.NEED_PROMOTION = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNbvAnswerBoard != null) {
            mNbvAnswerBoard.leaveScribbleMode();
        }

        if (mCaogaoNoteBoard != null) {
            mCaogaoNoteBoard.leaveScribbleMode();
        }
    }
}
