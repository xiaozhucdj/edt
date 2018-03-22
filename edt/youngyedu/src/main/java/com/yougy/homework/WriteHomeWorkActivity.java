package com.yougy.homework;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.google.gson.Gson;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.yougy.anwser.ContentDisplayer;
import com.yougy.anwser.Content_new;
import com.yougy.anwser.HomeWorkResultbean;
import com.yougy.anwser.ParsedQuestionItem;
import com.yougy.anwser.STSResultbean;
import com.yougy.anwser.STSbean;
import com.yougy.anwser.TimedTask;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.DataCacheUtils;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.SharedPreferencesUtil;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.OnItemClickListener;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.homework.bean.HomeworkDetail;
import com.yougy.message.ListUtil;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ItemAnswerChooseGridviewBinding;
import com.yougy.view.CustomGridLayoutManager;
import com.yougy.view.CustomLinearLayoutManager;
import com.yougy.view.NoteBookView2;
import com.yougy.view.dialog.LoadingProgressDialog;
import com.zhy.autolayout.utils.AutoUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.yougy.common.utils.SharedPreferencesUtil.getSpUtil;

/**
 * Created by cdj
 * 写作业界面
 */
public class WriteHomeWorkActivity extends BaseActivity {

    @BindView(R.id.iv_chooese_tag)
    ImageView ivChooeseTag;
    @BindView(R.id.rcv_all_homework_page)
    RecyclerView allHomeWorkPage;
    @BindView(R.id.rcv_all_question_page)
    RecyclerView allQuestionPage;
    @BindView(R.id.rcv_chooese_item)
    RecyclerView rcvChooese;
    @BindView(R.id.content_displayer)
    ContentDisplayer contentDisplayer;
    @BindView(R.id.rl_answer)
    RelativeLayout rlAnswer;
    @BindView(R.id.tv_submit_homework)
    TextView tvSubmitHomeWork;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_add_page)
    TextView tvAddPage;
    @BindView(R.id.tv_clear_write)
    TextView tvClearWrite;
    @BindView(R.id.tv_caogao_text)
    TextView tvCaogaoText;
    @BindView(R.id.ll_caogao_control)
    LinearLayout llCaogaoControl;
    @BindView(R.id.rl_caogao_box)
    RelativeLayout rlCaogaoBox;

    //作业回答手写板
    private NoteBookView2 mNbvAnswerBoard;
    //作业草稿纸
    private NoteBookView2 mCaogaoNoteBoard;

    private static final int PAGE_SHOW_SIZE = 5;

    private int homeWorkPageSize = 17;
    private int questionPageSize = 1;

    //底部页码数偏移量
    private int pageDeviationNum = 0;
    private QuestionPageNumAdapter questionPageNumAdapter;

    //当前顶部展示的页数（展示的第几题）从0开始。
    private int showHomeWorkPosition = 0;
    private HomeWorkPageNumAdapter homeWorkPageNumAdapter;

    //顶部作业题目展示数据
    List<com.yougy.homework.bean.HomeworkDetail.ExamPaper.ExamPaperContent> examPaperContentList;
    //底部某一题多页数据
    private List<Content_new> questionList;
    //如果是选择题，这里存储选择题的可选结果
    private List<ParsedQuestionItem.Answer> chooeseAnswerList;
    //选择题选择的结果
    private ArrayList<String> checkedAnswerList = new ArrayList<String>();
    //作业中某一题对返回结果解析后拿到的结果对象
    private ParsedQuestionItem parsedQuestionItem;

    private static int COMEIN_HOMEWORK_PAGE_MODE = 0;//0：点击角标进入，1：上一页进入，2：下一页进入
    //单题用时时间记录的定时器
    private TimedTask timedTask;
    //单题用时开始时间
    private long startTimeMill;

    //保存当前题目结果的页面从0开始
    private int saveHomeWorkPage;

    //保存当前题目页面分页，默认从0开始
    private int saveQuestionPage = 0;

    private String examName;
    private String examId = "550";
    //是否添加了手写板
    private boolean isAddAnswerBoard;
    //byte数组集合，（用来保存每一页书写的笔记数据）
    private ArrayList<byte[]> bytesList = new ArrayList<>();
    //存储每一页截屏图片地址
    private ArrayList<String> pathList = new ArrayList<>();

    //byte数组集合，（用来保存每一页草稿的笔记数据）
    private ArrayList<byte[]> cgBytes = new ArrayList<>();

    //是否第一次自动点击进入某一题
    private boolean isFirstComeInHomeWork;
    //是否第一次自动点击进入某一题的第一页
    private boolean isFirstComeInQuestion;
    //作业中某一题所有结果（图片，文本），统计上传数据到集合中，方便将该信息提交到服务器
    ArrayList<HomeWorkResultbean> homeWorkResultbeanList = new ArrayList<>();

    //某一题的分页中选中页码用来设置选择背景色。
    private int chooesePoint = 0;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_write_homework);
    }

    @Override
    protected void init() {

        examId = getIntent().getStringExtra("examId");

        if (TextUtils.isEmpty(examId)) {
            ToastUtil.showToast(getBaseContext(), "作业id为空");
            finish();
        }

        examName = getIntent().getStringExtra("examName");
        tvTitle.setText(examName);
    }


    int screenWidth;
    int screenHeight;
    int lastX;
    int lastY;

    int left;
    int top;
    int right;
    int bottom;

    @Override
    protected void initLayout() {
        //新建写字板，并添加到界面上
        mNbvAnswerBoard = new NoteBookView2(this);
        mCaogaoNoteBoard = new NoteBookView2(this);
        findViewById(R.id.img_btn_right).setVisibility(View.GONE);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels - 50;


        llCaogaoControl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        EpdController.leaveScribbleMode(mNbvAnswerBoard);
                        mNbvAnswerBoard.invalidate();

                        if (mCaogaoNoteBoard.getVisibility() == View.VISIBLE) {
                            EpdController.leaveScribbleMode(mCaogaoNoteBoard);
                            mCaogaoNoteBoard.invalidate();
                        }

                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;

                        left = v.getLeft() + dx;
                        top = v.getTop() + dy;
                        right = v.getRight() + dx;
                        bottom = v.getBottom() + dy;
                        if (left < 0) {
                            left = 0;
                            right = left + v.getWidth();
                        }
                        if (right > screenWidth) {
                            right = screenWidth;
                            left = right - v.getWidth();
                        }
                        if (top < 0) {
                            top = 0;
                            bottom = top + v.getHeight();
                        }
                        if (bottom > screenHeight) {
                            bottom = screenHeight;
                            top = bottom - v.getHeight();
                        }
                        v.layout(left, top, right, bottom);
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        /*v.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                v.layout(left, top, right, bottom);
                            }
                        }, 100);*/

                        break;
                }
                return false;
            }
        });

        ContentDisplayer.ContentAdaper contentAdaper = new ContentDisplayer.ContentAdaper() {
            @Override
            public void onPageInfoChanged(String typeKey, int newPageCount, int selectPageIndex) {
                super.onPageInfoChanged(typeKey, newPageCount, selectPageIndex);

                //获取到最新的页码数后，刷新需要存储数据的集合（笔记，草稿笔记，图片地址），刷新该题的多页角标，展示显示选择页面题目。
                if (newPageCount > questionPageSize) {
                    //需要添加的页码数目。
                    int newAddPageNum = newPageCount - questionPageSize;

                    for (int i = 0; i < newAddPageNum; i++) {
                        bytesList.add(null);
                    }
                    for (int i = 0; i < newAddPageNum; i++) {
                        cgBytes.add(null);
                    }
                    for (int i = 0; i < newAddPageNum; i++) {
                        pathList.add(null);
                    }

                    //更新最新的页面数据
                    questionPageSize = newPageCount;
                    questionPageNumAdapter.notifyDataSetChanged();
                    if (selectPageIndex == 0) {
                        isFirstComeInQuestion = true;
                    }

                }
            }
        };
        contentDisplayer.setmContentAdaper(contentAdaper);
    }

    @Override
    protected void loadData() {

        NetWorkManager.queryHomeworkDetail(Integer.parseInt(examId)).subscribe(new Action1<List<HomeworkDetail>>() {
            @Override
            public void call(List<HomeworkDetail> homeworkDetails) {
                com.yougy.homework.bean.HomeworkDetail.ExamPaper examPaper = homeworkDetails.get(0).getExamPaper();

                examPaperContentList = examPaper.getPaperContent();

                homeWorkPageSize = examPaperContentList.size();

                fillData();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        });


    }

    //填充数据
    private void fillData() {
        //作业题目切换
        homeWorkPageNumAdapter = new HomeWorkPageNumAdapter();
        CustomGridLayoutManager gridLayoutManager = new CustomGridLayoutManager(this, 8);
        gridLayoutManager.setScrollEnabled(false);
//        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        allHomeWorkPage.setLayoutManager(gridLayoutManager);
        allHomeWorkPage.setAdapter(homeWorkPageNumAdapter);

        homeWorkPageNumAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick1(int position) {

                EpdController.leaveScribbleMode(mNbvAnswerBoard);
                mNbvAnswerBoard.invalidate();
                /*llCaogaoControl.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        llCaogaoControl.layout(left, top, right, bottom);
                    }
                }, 30);*/

                if (mCaogaoNoteBoard.getVisibility() == View.VISIBLE) {
                    EpdController.leaveScribbleMode(mCaogaoNoteBoard);
                    mCaogaoNoteBoard.invalidate();
                }

                //存储之前一题的结果
                //两种情况（一种直接点击，此时showHomeWorkPosition为之前页码位。另一种上一页或下一页点击，此时showHomeWorkPosition为当前展示页码位，获取之前页面位需+1或-1）

                if (isFirstComeInHomeWork) {
                    isFirstComeInHomeWork = false;

                } else {
                    if (COMEIN_HOMEWORK_PAGE_MODE == 0) {

                        //点击的是同一页。不处理
                        if (position == showHomeWorkPosition) {
                            return;
                        }
                        chooesePoint = 0;
                        saveHomeWorkPage = showHomeWorkPosition;
                        onClick(findViewById(R.id.ll_chooese_homework));

                    } else if (COMEIN_HOMEWORK_PAGE_MODE == 1) {
                        saveHomeWorkPage = position + 1;

                    } else {
                        saveHomeWorkPage = position - 1;
                    }

                    COMEIN_HOMEWORK_PAGE_MODE = 0;


                    //切换题目是触发一下保存上一题结果数据
                    saveLastHomeWorkData(saveHomeWorkPage);
                }

                showHomeWorkPosition = position;

                getShowHomeWorkData(showHomeWorkPosition);

                com.yougy.homework.bean.HomeworkDetail.ExamPaper.ExamPaperContent examPaperContent = examPaperContentList.get(position);
                List<ParsedQuestionItem> parsedQuestionItemList = examPaperContent.getParsedQuestionItemList();
                if (parsedQuestionItemList == null || parsedQuestionItemList.size() == 0) {
                    ToastUtil.showToast(getBaseContext(), "该题题目可能已经删除");
                    return;
                }

                parsedQuestionItem = parsedQuestionItemList.get(0);
                questionList = parsedQuestionItem.questionContentList;
                contentDisplayer.getmContentAdaper().updateDataList("question", (ArrayList<Content_new>) questionList);


                //判断是否之前有笔记
                questionPageSize = bytesList.size() >= questionList.size() ? bytesList.size() : questionList.size();

                //切换题目时，先丢掉之前添加的写字板
                if (isAddAnswerBoard) {
                    rlAnswer.removeView(mNbvAnswerBoard);
                    isAddAnswerBoard = false;
                }


                //取题目的第一页纸展示
                if (questionList != null && questionList.size() > 0) {
                    //这里先添加第一题题目的分页手写笔记，方便后期修改

                    if (bytesList.size() > 0) {

                    } else {
                        for (int i = 0; i < questionPageSize; i++) {
                            bytesList.add(null);
                        }
                    }
                    if (cgBytes.size() > 0) {
                    } else {
                        for (int i = 0; i < questionPageSize; i++) {
                            cgBytes.add(null);
                        }
                    }
                    if (pathList.size() > 0) {

                    } else {
                        for (int i = 0; i < questionPageSize; i++) {
                            pathList.add(null);
                        }
                    }
                    isFirstComeInQuestion = true;
                    questionPageNumAdapter.onItemClickListener.onItemClick1(0);
                }

                startTimeMill = System.currentTimeMillis();
                startClock();

                homeWorkPageNumAdapter.notifyDataSetChanged();
            }
        });


        //作业中某一题题目、答案切换
        questionPageNumAdapter = new QuestionPageNumAdapter();
        CustomLinearLayoutManager linearLayoutManager = new CustomLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager.setScrollEnabled(false);
        allQuestionPage.setLayoutManager(linearLayoutManager);
        allQuestionPage.setAdapter(questionPageNumAdapter);

        questionPageNumAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick1(int position) {

                //离开手绘模式，并刷新界面ui
                EpdController.leaveScribbleMode(mNbvAnswerBoard);
                mNbvAnswerBoard.invalidate();
               /* llCaogaoControl.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        llCaogaoControl.layout(left, top, right, bottom);
                    }
                }, 30);
*/
                if (mCaogaoNoteBoard.getVisibility() == View.VISIBLE) {
                    EpdController.leaveScribbleMode(mCaogaoNoteBoard);
                    mCaogaoNoteBoard.invalidate();
                }

                if (isFirstComeInQuestion) {
                    isFirstComeInQuestion = false;
                } else {

                    //如果草稿纸打开着，需要先将草稿纸隐藏。用于截图
                    if (llCaogaoControl.getVisibility() == View.VISIBLE) {
                        cgBytes.set(saveQuestionPage, mCaogaoNoteBoard.bitmap2Bytes());

                        tvCaogaoText.setText("草稿纸");
                        mCaogaoNoteBoard.clear();
                        llCaogaoControl.setVisibility(View.GONE);
                    }

                    //如果 mNbvAnswerBoard是显示的说明是非选择题，需要保持笔记
                    if (mNbvAnswerBoard.getVisibility() == View.VISIBLE) {
                        //保存上一个题目多页数据中的某一页手写笔记。
                        byte[] tmpBytes1 = mNbvAnswerBoard.bitmap2Bytes();
                        bytesList.set(saveQuestionPage, tmpBytes1);
                    }
                    //是否是选择题。都需要截屏保存图片
                    pathList.set(saveQuestionPage, saveBitmapToFile(saveScreenBitmap(), examId + "_" + showHomeWorkPosition + "_" + saveQuestionPage));
                }

                mNbvAnswerBoard.clearAll();


                chooesePoint = position;


                //将本页设置为选中页
                saveQuestionPage = position;

                if (position < contentDisplayer.getmContentAdaper().getPageCount("question")) {
                    //切换当前题目的分页
                    contentDisplayer.getmContentAdaper().toPage("question", position, false);
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
                        chooeseAnswerList = parsedQuestionItem.answerList;
                        //选择题不能加页
                        tvAddPage.setVisibility(View.GONE);
                        tvClearWrite.setVisibility(View.GONE);

                        setChooeseResult();

                        //刷新当前选择结果的reciv
                        if (rcvChooese.getAdapter() != null) {
                            rcvChooese.getAdapter().notifyDataSetChanged();
                        }

                    } else {
                        if (!isAddAnswerBoard) {
                            rlAnswer.addView(mNbvAnswerBoard);
                            isAddAnswerBoard = true;
                        }
                        rcvChooese.setVisibility(View.GONE);
                        tvAddPage.setVisibility(View.VISIBLE);
                        tvClearWrite.setVisibility(View.VISIBLE);

                        //从之前bytesList中回显之前保存的手写笔记，如果有的话
                        if (bytesList.size() > position) {
                            byte[] tmpBytes = bytesList.get(position);
                            if (tmpBytes != null) {
                                mNbvAnswerBoard.drawBitmap(BitmapFactory.decodeByteArray(tmpBytes, 0, tmpBytes.length));
                            }
                        }
                       /* //从之前cgBytes中回显之前保存的草稿手写笔记，如果有的话
                        if (cgBytes.size() > position) {
                            byte[] tmpBytes = cgBytes.get(position);
                            if (tmpBytes != null) {
                                mCaogaoNoteBoard.drawBitmap(BitmapFactory.decodeByteArray(tmpBytes, 0, tmpBytes.length));
                            }
                        }*/
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

        left = llCaogaoControl.getLeft();
        top = llCaogaoControl.getTop();
        right = llCaogaoControl.getRight();
        bottom = llCaogaoControl.getBottom();

        if (examPaperContentList != null && examPaperContentList.size() > 0) {

            isFirstComeInHomeWork = true;
            homeWorkPageNumAdapter.onItemClickListener.onItemClick1(0);

        }
        //触发一下点击事件。默认隐藏所有题目
        onClick(findViewById(R.id.ll_chooese_homework));
    }


    private void startClock() {
        timedTask = new TimedTask(TimedTask.TYPE.IMMEDIATELY_AND_CIRCULATION, 1000)
                .start(new Action1<Integer>() {
                    @Override
                    public void call(Integer times) {
                        refreshTime();
                    }
                });
    }

    private void refreshTime() {
        long spentTimeMill = System.currentTimeMillis() - startTimeMill;

        tvSubmitHomeWork.setText("提交（时间 " + DateUtils.converLongTimeToString(spentTimeMill) + ")");
    }


    /**
     * 设置选择题的结果界面
     */
    private void setChooeseResult() {

        //清理掉其他题中的作业结果。
//        checkedAnswerList.clear();

        rcvChooese.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(WriteHomeWorkActivity.this).inflate(R.layout.item_answer_choose_gridview, parent, false);
                AutoUtils.auto(view);
                AnswerItemHolder holder = new AnswerItemHolder(view);

                holder.setChooeseStyle(chooeseAnswerList.size());

                return holder;
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
                //离开手绘模式，并刷新界面ui
                EpdController.leaveScribbleMode(mNbvAnswerBoard);
                mNbvAnswerBoard.invalidate();
                /*llCaogaoControl.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        llCaogaoControl.layout(left, top, right, bottom);
                    }
                }, 30);*/

                if (mCaogaoNoteBoard.getVisibility() == View.VISIBLE) {
                    EpdController.leaveScribbleMode(mCaogaoNoteBoard);
                    mCaogaoNoteBoard.invalidate();
                }

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

    @Override
    public void onBackPressed() {
        EpdController.leaveScribbleMode(mNbvAnswerBoard);
        if (mCaogaoNoteBoard.getVisibility() == View.VISIBLE) {
            EpdController.leaveScribbleMode(mCaogaoNoteBoard);
        }
        super.onBackPressed();
    }

    @OnClick({R.id.tv_dismiss_caogao, R.id.tv_caogao_text, R.id.btn_left, R.id.tv_last_homework, R.id.tv_next_homework, R.id.tv_save_homework, R.id.tv_submit_homework, R.id.tv_clear_write, R.id.tv_add_page, R.id.ll_chooese_homework})
    public void onClick(View view) {
        EpdController.leaveScribbleMode(mNbvAnswerBoard);
        mNbvAnswerBoard.invalidate();
        /*llCaogaoControl.postDelayed(new Runnable() {
            @Override
            public void run() {
                llCaogaoControl.layout(left, top, right, bottom);
            }
        }, 30);*/

        if (mCaogaoNoteBoard.getVisibility() == View.VISIBLE) {
            EpdController.leaveScribbleMode(mCaogaoNoteBoard);
            mCaogaoNoteBoard.invalidate();
        }

        switch (view.getId()) {

            case R.id.btn_left:
                finish();
                break;

            case R.id.tv_last_homework:
                if (showHomeWorkPosition > 0) {
                    showHomeWorkPosition--;
                    COMEIN_HOMEWORK_PAGE_MODE = 1;
                    chooesePoint = 0;
                    homeWorkPageNumAdapter.onItemClickListener.onItemClick1(showHomeWorkPosition);
                } else {
                    ToastUtil.showToast(this, "已经是第一题了");
                }
                break;
            case R.id.tv_next_homework:
                if (showHomeWorkPosition < homeWorkPageSize - 1) {
                    showHomeWorkPosition++;
                    COMEIN_HOMEWORK_PAGE_MODE = 2;
                    chooesePoint = 0;
                    homeWorkPageNumAdapter.onItemClickListener.onItemClick1(showHomeWorkPosition);
                } else {
                    ToastUtil.showToast(this, "已经是最后一题了");
                }
                break;
            case R.id.tv_save_homework:
                //暂存，默认触发调转到下一题（如果有），然后打开暂存成功弹窗
                if (showHomeWorkPosition < homeWorkPageSize - 1) {
                    showHomeWorkPosition++;
                    COMEIN_HOMEWORK_PAGE_MODE = 2;
                    homeWorkPageNumAdapter.onItemClickListener.onItemClick1(showHomeWorkPosition);
                } else {
                    //如果已经是最后一题，那么不在跳转。直接打开暂存成功弹窗
                }
                // 这里需要跳转到暂存成功的弹窗界面
                FullScreenHintDialog fullScreenHintDialog = new FullScreenHintDialog(this, "");
                fullScreenHintDialog.setIconResId(R.drawable.icon_correct).setContentText("暂存成功").setBtn1("继续作答", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fullScreenHintDialog.dismiss();
                    }
                }, false).setBtn2("返回作业", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fullScreenHintDialog.dismiss();

//                        YougyApplicationManager.getRxBus(getBaseContext()).send("refreshHomeworkList");
                        onBackPressed();
                    }
                }, false).setShowNoMoreAgainHint(false).show();


                break;
            case R.id.tv_submit_homework:

                //提交，默认触发调转到下一题（如果有），然后打未完成提示
                if (showHomeWorkPosition < homeWorkPageSize - 1) {
                    showHomeWorkPosition++;
                    COMEIN_HOMEWORK_PAGE_MODE = 2;
                    homeWorkPageNumAdapter.onItemClickListener.onItemClick1(showHomeWorkPosition);


                } else {
                    //如果已经是最后一题，直接提交
                    //前提是先保存最后一题结果到本地存储
                    saveLastHomeWorkData(showHomeWorkPosition);
//                    getUpLoadInfo();
                }

                //打开未完成提示
                fullScreenHintDialog = new FullScreenHintDialog(this, "");
                fullScreenHintDialog.setIconResId(R.drawable.aa).setContentText("是否提交作业").setBtn1("检查作业", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fullScreenHintDialog.dismiss();
                    }
                }, false).setBtn2("确认提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 去提交
                        getUpLoadInfo();

                    }
                }, false).setShowNoMoreAgainHint(false).show();


                break;
            case R.id.tv_clear_write:
                mNbvAnswerBoard.clearAll();
                break;
            case R.id.tv_add_page:
                questionPageSize++;
                bytesList.add(null);
                pathList.add(null);
                cgBytes.add(null);
                questionPageNumAdapter.notifyDataSetChanged();
                questionPageNumAdapter.onItemClickListener.onItemClick1(questionPageSize - 1);

                break;
            case R.id.ll_chooese_homework:
                if (allHomeWorkPage.getVisibility() == View.GONE) {
                    allHomeWorkPage.setVisibility(View.VISIBLE);
                    ivChooeseTag.setImageResource(R.drawable.img_timu_down);
                } else {
                    allHomeWorkPage.setVisibility(View.GONE);
                    ivChooeseTag.setImageResource(R.drawable.img_timu_up);
                }

                break;
            case R.id.tv_caogao_text:

                if (tvCaogaoText.getText().toString().startsWith("扔掉")) {
                    tvCaogaoText.setText("草稿纸");

                    cgBytes.set(saveQuestionPage, null);
                    mCaogaoNoteBoard.clearAll();
                    llCaogaoControl.setVisibility(View.GONE);

                    if (rlCaogaoBox.getChildCount() > 0) {
                        rlCaogaoBox.removeView(mCaogaoNoteBoard);
                    }

                } else {
                    tvCaogaoText.setText("扔掉\n草稿纸");
                    llCaogaoControl.setVisibility(View.VISIBLE);

                    if (rlCaogaoBox.getChildCount() == 0) {
                        rlCaogaoBox.addView(mCaogaoNoteBoard);
                    }

                    byte[] tmpBytes = cgBytes.get(saveQuestionPage);
                    if (tmpBytes != null) {
                        mCaogaoNoteBoard.drawBitmap(BitmapFactory.decodeByteArray(tmpBytes, 0, tmpBytes.length));
                    }

                }

                break;
            case R.id.tv_dismiss_caogao:

                if (llCaogaoControl.getVisibility() == View.VISIBLE) {
                    tvCaogaoText.setText("草稿纸");
                    cgBytes.set(saveQuestionPage, mCaogaoNoteBoard.bitmap2Bytes());
                    llCaogaoControl.setVisibility(View.GONE);
                }
                break;
        }
    }

    /**
     * 保存之前操作题目结果数据
     */
    private void saveLastHomeWorkData(int position) {

        //如果草稿纸打开着，需要先将草稿纸隐藏。用于截图
        if (llCaogaoControl.getVisibility() == View.VISIBLE) {
            tvCaogaoText.setText("草稿纸");
            cgBytes.set(saveQuestionPage, mCaogaoNoteBoard.bitmap2Bytes());
            mCaogaoNoteBoard.clear();
            llCaogaoControl.setVisibility(View.GONE);
        }


        //刷新最后没有保存的数据
        //如果 mNbvAnswerBoard是显示的说明是非选择题，需要保持笔记
        if (mNbvAnswerBoard.getVisibility() == View.VISIBLE) {
            bytesList.set(saveQuestionPage, mNbvAnswerBoard.bitmap2Bytes());
        }
        //是否是选择题。都需要截屏保存图片
        pathList.set(saveQuestionPage, saveBitmapToFile(saveScreenBitmap(), examId + "_" + position + "_" + saveQuestionPage));
        mNbvAnswerBoard.clearAll();


        //保存手写笔记，用于回显（1，暂存时，2，题目切换时）
        DataCacheUtils.putObject(this, examId + "_" + position + "_bytes_list", bytesList);
        DataCacheUtils.putObject(this, examId + "_" + position + "_caogao_bytes_list", cgBytes);
        //保存待上传图片，用于上传
        getSpUtil().setDataList(examId + "_" + position + "_path_list", pathList);
        getSpUtil().setDataList(examId + "_" + position + "_chooese_list", checkedAnswerList);

        String textInfo = tvSubmitHomeWork.getText().toString();
        if (textInfo.contains("(") && textInfo.contains(")")) {
            getSpUtil().putString(examId + "_" + position + "_use_time", textInfo.substring(textInfo.indexOf("(") + 2, textInfo.lastIndexOf(")")));
        }

        //本题所有数据保存完毕
        saveQuestionPage = 0;

        //以上存储了3中数据，1：保存手写笔记集合，2：保存每页图片集合，3：如果是选择题，保存选择题结果字符串。
        //存储成功之后，将内存中的数据全部清空。

        for (int i = 0; i < bytesList.size(); i++) {
            byte[] tmp = bytesList.get(i);
            tmp = null;
        }
        bytesList.clear();
        cgBytes.clear();
        pathList.clear();
        checkedAnswerList.clear();

    }


    /**
     * 得到之前保存的数据，用于回显
     */
    private void getShowHomeWorkData(int position) {

        //回显之前存储在sp中的手写笔记数据（如果有）

        List<byte[]> tmpBytesList = (List<byte[]>) DataCacheUtils.getObject(this, examId + "_" + position + "_bytes_list");
        if (tmpBytesList != null && tmpBytesList.size() > 0) {
            bytesList.addAll(tmpBytesList);
        }
        List<byte[]> cgBytesList = (List<byte[]>) DataCacheUtils.getObject(this, examId + "_" + position + "_caogao_bytes_list");
        if (cgBytesList != null && cgBytesList.size() > 0) {
            cgBytes.addAll(cgBytesList);
        }
        List<String> tmpPathList = getSpUtil().getDataList(examId + "_" + position + "_path_list");
        if (tmpPathList != null && tmpPathList.size() > 0) {
            pathList.addAll(tmpPathList);
        }
        //回显之前存储在sp中的选择结果数据（如果有）
        List<String> tmpCheckedAnswerList = getSpUtil().getDataList(examId + "_" + position + "_chooese_list");
        if (tmpCheckedAnswerList != null && tmpCheckedAnswerList.size() > 0) {
            checkedAnswerList.addAll(tmpCheckedAnswerList);

        }

    }


    /**
     * 获取oss上传所需信息
     */
    private void getUpLoadInfo() {
        NetWorkManager.queryReplyRequest(SpUtil.getUserId() + "")
                .subscribe(new Action1<STSbean>() {
                    @Override
                    public void call(STSbean stSbean) {
                        Log.v("FH", "call ");
                        if (stSbean != null) {
                            upLoadPic(stSbean);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), "获取上传信息失败");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });

    }

    /**
     * 上传图片，使用同步方法上传
     *
     * @param stSbean
     */
    public void upLoadPic(STSbean stSbean) {


        String endpoint = "http://oss-cn-shanghai.aliyuncs.com";


        OSSCredentialProvider credentialProvider = new OSSFederationCredentialProvider() {
            @Override
            public OSSFederationToken getFederationToken() {
                return new OSSFederationToken(stSbean.getAccessKeyId(), stSbean.getAccessKeySecret(), stSbean.getSecurityToken(), stSbean.getExpiration());
            }
        };


        //该配置类如果不设置，会有默认配置，具体可看该类
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();
        OSS oss = new OSSClient(YougyApplicationManager.getContext(), endpoint, credentialProvider, conf);


        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {


                for (int i = 0; i < homeWorkPageSize; i++) {

                    List<String> tmpPathList = getSpUtil().getDataList(examId + "_" + i + "_path_list");
                    if (tmpPathList != null && tmpPathList.size() > 0) {

                        //作业中某一题图片上传成功后，统计上传数据到集合中，方便将该信息提交到服务器
                        ArrayList<STSResultbean> stsResultbeanArrayList = new ArrayList<>();

                        for (int j = 0; j < tmpPathList.size(); j++) {

                            String picPath = tmpPathList.get(j);

                            if (TextUtils.isEmpty(picPath)) {
                                continue;
                            }


                            String picName = picPath.substring(picPath.lastIndexOf("/"));


                            // 构造上传请求
                            PutObjectRequest put = new PutObjectRequest(stSbean.getBucketName(), stSbean.getPath() + picName, picPath);
                            try {
                                PutObjectResult putResult = oss.putObject(put);
                                Log.d("PutObject", "UploadSuccess");
                                Log.d("ETag", putResult.getETag());
                                Log.d("RequestId", putResult.getRequestId());
                            } catch (ClientException e) {
                                // 本地异常如网络异常等
                                e.printStackTrace();
                            } catch (ServiceException e) {
                                // 服务异常
                                Log.e("RequestId", e.getRequestId());
                                Log.e("ErrorCode", e.getErrorCode());
                                Log.e("HostId", e.getHostId());
                                Log.e("RawMessage", e.getRawMessage());
                            }

                            STSResultbean stsResultbean = new STSResultbean();
                            stsResultbean.setBucket(stSbean.getBucketName());
                            stsResultbean.setRemote(stSbean.getPath() + picName);
                            File picFile = new File(picPath);
                            stsResultbean.setSize(picFile.length());
                            stsResultbeanArrayList.add(stsResultbean);
                            //上传后清理掉本地图片文件
                            picFile.delete();

                        }
                        tmpPathList.clear();
                        List<String> tmpCheckedAnswerList = getSpUtil().getDataList(examId + "_" + i + "_chooese_list");
                        String useTime = SharedPreferencesUtil.getSpUtil().getString(examId + "_" + i + "_use_time", "");

                        HomeWorkResultbean homeWorkResultbean = new HomeWorkResultbean();
                        homeWorkResultbean.setExamId(Integer.parseInt(examId));
                        int itemId = examPaperContentList.get(i).getPaperItem();
                        homeWorkResultbean.setItemId(itemId);

                        homeWorkResultbean.setPicContent(stsResultbeanArrayList);
                        homeWorkResultbean.setUseTime(useTime);
                        homeWorkResultbean.setTxtContent(tmpCheckedAnswerList);
                        homeWorkResultbeanList.add(homeWorkResultbean);


                        //清理掉缓存书写笔记，图片地址存 ,选择结果
                        DataCacheUtils.reomve(getBaseContext(), examId + "_" + i + "_bytes_list");
                        DataCacheUtils.reomve(getBaseContext(), examId + "_" + i + "_caogao_bytes_list");
                        SharedPreferencesUtil.getSpUtil().remove(examId + "_" + i + "_path_list");
                        SharedPreferencesUtil.getSpUtil().remove(examId + "_" + i + "_chooese_list");
                        SharedPreferencesUtil.getSpUtil().remove(examId + "_" + i + "_use_time");
                    } else {

                        //后台定义没有做的题目 任然上传，结果数据为空
                        HomeWorkResultbean homeWorkResultbean = new HomeWorkResultbean();
                        homeWorkResultbean.setExamId(Integer.parseInt(examId));
                        int itemId = examPaperContentList.get(i).getPaperItem();
                        homeWorkResultbean.setItemId(itemId);

                        homeWorkResultbeanList.add(homeWorkResultbean);

                    }

                }


                subscriber.onNext(new Object());//将执行结果返回
                subscriber.onCompleted();//结束异步任务
            }
        })
                .subscribeOn(Schedulers.io())//异步任务在IO线程执行
                .observeOn(AndroidSchedulers.mainThread())//执行结果在主线程运行
                .subscribe(new Subscriber<Object>() {
                    LoadingProgressDialog loadingProgressDialog;

                    @Override
                    public void onStart() {
                        super.onStart();
                        if (loadingProgressDialog == null) {
                            loadingProgressDialog = new LoadingProgressDialog(WriteHomeWorkActivity.this);
                            loadingProgressDialog.show();
                            loadingProgressDialog.setTitle("答案上传中...");
                        }

                    }

                    @Override
                    public void onCompleted() {
                        if (loadingProgressDialog != null) {
                            loadingProgressDialog.dismiss();
                            loadingProgressDialog = null;
                        }


                        writeInfoToS();

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (loadingProgressDialog != null) {
                            loadingProgressDialog.dismiss();
                            loadingProgressDialog = null;
                        }
                    }

                    @Override
                    public void onNext(Object o) {
                    }
                });

    }


    /**
     * 将上传信息提交给服务器
     */
    private void writeInfoToS() {

        String content = new Gson().toJson(homeWorkResultbeanList);

        NetWorkManager.postReply(SpUtil.getUserId() + "", content)
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        timedTask.stop();


                        NetWorkManager.refreshHomeworkBook(getIntent().getIntExtra("mHomewrokId", 0)).subscribe(new Action1<Object>() {
                            @Override
                            public void call(Object o) {

                                ToastUtil.showToast(getBaseContext(), "上传信息提交给服务器完毕");
//                                YougyApplicationManager.getRxBus(getBaseContext()).send("refreshHomeworkList");
                                onBackPressed();
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


    /**
     * 获取可操作区域的截图
     *
     * @return 图片的bitmap
     */
    private Bitmap saveScreenBitmap() {
        rlAnswer.setDrawingCacheEnabled(true);
        Bitmap tBitmap = rlAnswer.getDrawingCache();
        // 拷贝图片，否则在setDrawingCacheEnabled(false)以后该图片会被释放掉
        tBitmap = tBitmap.createBitmap(tBitmap);
        rlAnswer.setDrawingCacheEnabled(false);
        return tBitmap;
    }

    public String saveBitmapToFile(Bitmap bitmap, String bitName) {

        String fileDir = FileUtils.getAppFilesDir() + "/homework_result";
        FileUtils.createDirs(fileDir);


//        String bitName = System.currentTimeMillis() + "";
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


    /*顶部页面的adapter相关*/
    class HomeWorkPageNumViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_page_id)
        TextView mTvPageId;


        public HomeWorkPageNumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


//            int margin = UIUtils.dip2px(10);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) ((UIUtils.getScreenWidth() - PAGE_SHOW_SIZE * 2 * margin) / PAGE_SHOW_SIZE), ViewGroup.LayoutParams.MATCH_PARENT);
//            params.setMargins(margin, 0, margin, 0);
//            itemView.setLayoutParams(params);

        }


    }

    class HomeWorkPageNumAdapter extends RecyclerView.Adapter<HomeWorkPageNumViewHolder> {

        OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public HomeWorkPageNumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page_check_homework, parent, false);
            return new HomeWorkPageNumViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(HomeWorkPageNumViewHolder holder, final int position) {


            holder.mTvPageId.setText((position + 1) + "");


            if (position == showHomeWorkPosition) {
                holder.mTvPageId.setBackgroundResource(R.drawable.img_timu_cuowu);
                holder.mTvPageId.setTextColor(getResources().getColor(R.color.white));
            } else {
                holder.mTvPageId.setBackgroundResource(R.drawable.img_timu_chooese);
                holder.mTvPageId.setTextColor(getResources().getColor(R.color.black));
            }


           /* //判断题目回答状态
            int tag = 2;
            tag = position % 4;

            if (holder.mTvPageId.getTag() != null) {
                tag = (int) holder.mTvPageId.getTag();
            }
            switch (tag) {
                default:
                case 0://未批改
                    holder.mTvPageId.setBackgroundResource(R.drawable.img_timu_normer);
                    holder.mTvPageId.setTextColor(getResources().getColor(R.color.white));
                    break;
                case 1://正确
                    holder.mTvPageId.setBackgroundResource(R.drawable.img_timu_zhengqu);
                    holder.mTvPageId.setTextColor(getResources().getColor(R.color.white));
                    break;
                case 2://错误
                    holder.mTvPageId.setBackgroundResource(R.drawable.img_timu_cuowu);
                    holder.mTvPageId.setTextColor(getResources().getColor(R.color.white));
                    break;
                case 3://选中
                    holder.mTvPageId.setBackgroundResource(R.drawable.img_timu_chooese);
                    holder.mTvPageId.setTextColor(getResources().getColor(R.color.black));
                    break;

            }*/

            holder.mTvPageId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick1(position);
//                    notifyDataSetChanged();
//                    holder.mTvPageId.setBackgroundResource(R.drawable.img_timu_zhengqu);
//                    holder.mTvPageId.setTextColor(getResources().getColor(R.color.white));
                }
            });
        }

        @Override
        public int getItemCount() {
            return homeWorkPageSize;
        }
    }

    /*底部页面的adapter相关*/
    class QuestionPageNumViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_page_id)
        TextView mTvPageId;


        public QuestionPageNumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            int margin = UIUtils.dip2px(10);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) ((UIUtils.getScreenWidth() - UIUtils.dip2px(660) - PAGE_SHOW_SIZE * 2 * margin) / PAGE_SHOW_SIZE), ViewGroup.LayoutParams.MATCH_PARENT);
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


            if (position == chooesePoint) {
                holder.mTvPageId.setBackgroundResource(R.drawable.img_press_question_bg);
                holder.mTvPageId.setTextColor(getResources().getColor(R.color.white));
            } else {
                holder.mTvPageId.setBackgroundResource(R.drawable.img_normal_question_bg);
                holder.mTvPageId.setTextColor(getResources().getColor(R.color.black));
            }



            /*int tag = 0;

            if (holder.mTvPageId.getTag() != null) {
                tag = (int) holder.mTvPageId.getTag();
            }
            switch (tag) {
                default:
                case 0://错误

                    break;
                case 1://选中

                    break;
            }*/

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
                    itemBinding.textview.setSelected(false);
                    itemBinding.checkbox.setSelected(false);
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


        public void setChooeseStyle(int size) {
            int rid;
            switch (size) {
                case 2:
                    rid = R.drawable.btn_check_liangdaan;
                    break;
                case 3:
                    rid = R.drawable.btn_check_sandaan;
                    break;
                case 4:
                    rid = R.drawable.btn_check_sidaan;
                    break;
                case 5:
                    rid = R.drawable.btn_check_wudaan;
                    break;
                default:
                    rid = R.drawable.btn_check_liudaan;
                    break;
            }
            itemBinding.checkbox.setBackgroundResource(rid);
        }

    }

}
