package com.yougy.homework;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.yougy.anwser.ContentDisplayerAdapterV2;
import com.yougy.anwser.ContentDisplayerV2;
import com.yougy.anwser.Content_new;
import com.yougy.anwser.HomeWorkResultbean;
import com.yougy.anwser.ParsedQuestionItem;
import com.yougy.anwser.STSResultbean;
import com.yougy.anwser.STSbean;
import com.yougy.anwser.TimedTask;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.global.Commons;
import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.new_network.ApiException;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.DataCacheUtils;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.FormatUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.OnClickFastListener;
import com.yougy.common.utils.RefreshUtil;
import com.yougy.common.utils.SharedPreferencesUtil;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.SystemUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.OnItemClickListener;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.homework.bean.HomeworkDetail;
import com.yougy.homework.bean.TeamBean;
import com.yougy.message.ListUtil;
import com.yougy.message.YXClient;
import com.yougy.message.attachment.CollectHomeworkAttachment;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ItemAnswerChooseGridviewBinding;
import com.yougy.view.CustomGridLayoutManager;
import com.yougy.view.CustomLinearLayoutManager;
import com.yougy.view.NoteBookView2;
import com.yougy.view.dialog.ConfirmDialog;
import com.yougy.view.dialog.HintDialog;
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
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.yougy.common.utils.SharedPreferencesUtil.getSpUtil;

/**
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
    @BindView(R.id.ll_chooese_item)
    LinearLayout llChooeseItem;
    @BindView(R.id.content_displayer)
    ContentDisplayerV2 contentDisplayer;
    @BindView(R.id.lines_bg_imv)
    ImageView lineImgview;
    @BindView(R.id.rl_answer)
    RelativeLayout rlAnswer;
    @BindView(R.id.tv_submit_homework)
    TextView tvSubmitHomeWork;
    @BindView(R.id.tv_title_time)
    TextView tvSubmitTime;
    @BindView(R.id.tv_title_timing)
    TextView tvTiming;
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
    @BindView(R.id.next_homework_icon)
    ImageView nextQuestionIcon;
    @BindView(R.id.next_homework_text)
    TextView nextQuestionText;
    @BindView(R.id.last_homework_icon)
    ImageView lastQuestionIcon;
    @BindView(R.id.last_homework_text)
    TextView lastQuestionText;
    @BindView(R.id.tv_last_homework)
    LinearLayout lastQuestionBtn;
    @BindView(R.id.tv_next_homework)
    LinearLayout nextQuestionBtn;
    @BindView(R.id.tv_save_homework)
    TextView tvSaveHomework;
    @BindView(R.id.rb_error)
    RadioButton rbError;
    @BindView(R.id.rb_right)
    RadioButton rbRight;
    @BindView(R.id.rg_judge)
    RadioGroup rgJudge;
    @BindView(R.id.tv_homework_position)
    TextView tvHomeWorkPosition;
    @BindView(R.id.image_refresh)
    ImageView imageRefresh;
    @BindView(R.id.btn_left)
    ImageView mImageViewBack;

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
    //判断题判断的结果
    private ArrayList<String> judgeAnswerList = new ArrayList<String>();
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
    //isStudentCheck  0   默认 不传  1 自评   2 互评
    private int isStudentCheck;
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

    //某一题的分页中选中页码用来设置选择背景色。
    private int chooesePoint = 0;

    private Boolean btnLocked = false;

    private boolean isTimerWork = false; //是否定时作业
    private long timeSpace = -1;  // 定时时间
    private long startTime = 0;// 开始时间
    private long residueTime; //剩余时间
    private boolean isAutoSubmit = false; //是否到时间自动提交
    private TimedTask timingTask;//定时作业定时器

    private int teacherId;//教师Id
    private boolean mIsOnClass = false;//是否课堂作业
    private boolean mIsSubmit = false;//是否点击提交课堂作业
    private YXClient.OnMessageListener receiverMsg;
    private boolean isUpload = false;

    //是否用户手动提交了问答（用来做手动提交时，老师强制收取到时的学生结果为空）
    private boolean isUpByUser;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_write_homework);
    }

    @Override
    protected void init() {
        examId = getIntent().getStringExtra("examId");
        isStudentCheck = getIntent().getIntExtra("isStudentCheck", 0);
        if (TextUtils.isEmpty(examId)) {
            ToastUtil.showCustomToast(getBaseContext(), "作业id为空");
            mIsFinish = true;
            finish();
        }

        examName = getIntent().getStringExtra("examName");
        tvTitle.setText(examName);

        isTimerWork = getIntent().getBooleanExtra("isTimerWork", false);
        if (isTimerWork && !StringUtils.isEmpty(getIntent().getStringExtra("lifeTime"))) {
            timeSpace = FormatUtils.timeStrToLongMisencod(getIntent().getStringExtra("lifeTime"));
            if (timeSpace <= 0) {
                LogUtils.e("lifeTime is 0, ERROR return.");
                finish();
                return;
            }
            judgeWorkIsEnd();
        }
        teacherId = getIntent().getIntExtra("teacherID", 0);
        mIsOnClass = getIntent().getBooleanExtra("isOnClass", false);
        if (mIsOnClass) {
            tvSaveHomework.setVisibility(View.GONE);
            mImageViewBack.setVisibility(View.GONE);
        }
        LogUtils.d("homework isTimerWork = " + isTimerWork);
        if (SystemUtils.getDeviceModel().contains(FileContonst.DEVICE_TYPE_PL107)) {
            tvSubmitTime.setVisibility(View.INVISIBLE);
            tvTiming.setVisibility(View.INVISIBLE);
        } else {
            if (isTimerWork) {
                tvSubmitTime.setVisibility(View.INVISIBLE);
                tvTiming.setVisibility(View.VISIBLE);
            } else {
                tvSubmitTime.setVisibility(View.VISIBLE);
                tvTiming.setVisibility(View.INVISIBLE);
            }
        }
        initReceiveHomeworkMsg();
    }

    /**
     * 收作业消息
     */
    private void initReceiveHomeworkMsg() {
        receiverMsg = new YXClient.OnMessageListener() {
            @Override
            public void onNewMessage(IMMessage message) {
                if (message.getAttachment() instanceof CollectHomeworkAttachment) {
                    CollectHomeworkAttachment collectHomeworkAttachment = (CollectHomeworkAttachment) message.getAttachment();
                    LogUtils.d("examId = " + examId + "   collectHomeworkAttachment.examId = " + collectHomeworkAttachment.examId);
                    if (examId.equals(collectHomeworkAttachment.examId)) {
                        LogUtils.w("teacher receive homework , auto submit.");
                        //如果学生已经手动点击了提交，这时如果收到老师的强制收取消息，则不再执行提交逻辑。
                        if (isUpByUser) {
                            return;
                        }
                        WriteHomeWorkActivity.this.autoSubmitHomeWork();
                    } else {
                        LogUtils.w("current examId is not receive examId. not submit.");
                    }
                }
            }
        };
        YXClient.getInstance().with(getApplication()).addOnNewCommandCustomMsgListener(receiverMsg);

    }

    /**
     * 移除消息
     */
    private void removeReceiveHomeworkMsg() {
        if (receiverMsg != null) {
            YXClient.getInstance().with(getApplication()).removeOnNewCommandCustomMsgListener(receiverMsg);
        }
    }

    /**
     * 定时作业到时间后，进入后直接提交    作业进入  目前是不能返回的 不存在退出再次进入的情况
     */
    private Runnable mAutoSubmit = () -> autoSubmitHomeWork();
    /**
     * 手写模式开启    OnCreate 先拦截 手绘   onResume  延迟200ms取消拦截
     */
    private Runnable mInterceptAnswerBoard = new Runnable() {
        @Override
        public void run() {
            if (mNbvAnswerBoard != null) {
                mNbvAnswerBoard.setIntercept(false);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (isAutoSubmit) {
            YoungyApplicationManager.getMainThreadHandler().postDelayed(mAutoSubmit, 1500);
        } else {
            YoungyApplicationManager.getMainThreadHandler().postDelayed(mInterceptAnswerBoard, 200);
        }
    }

    /**
     * 判断定时作业是否到时间  未到时间继续 到时间自动提交作业
     */
    private void judgeWorkIsEnd() {
        SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getSpUtil();
        startTime = sharedPreferencesUtil.getLong("startWorkTime" + "_" + examId, 0);
        long currentTime = System.currentTimeMillis();
        LogUtils.d("timerWork init startTime :" + startTime);
        if (startTime == 0 || (currentTime - startTime > 0 && currentTime - startTime < timeSpace)) {
            if (startTime == 0) {//记录开始时间
                startTime = System.currentTimeMillis();
                sharedPreferencesUtil.putLong("startWorkTime" + "_" + examId, startTime);
                residueTime = timeSpace;
            } else {
                residueTime = timeSpace - (currentTime - startTime);
            }
            //继续作业
            LogUtils.d("timerWork init continue ....");
            startTimerTask();
        } else {
            //作业到时间  自动提交
            LogUtils.d("timerWork init submit ....");
            isAutoSubmit = true;
        }
    }

    /**
     * 开始计时任务
     */
    private synchronized void startTimerTask() {
        timingTask = new TimedTask(TimedTask.TYPE.IMMEDIATELY_AND_CIRCULATION, 1000)
                .start(new Action1<Integer>() {
                    @Override
                    public void call(Integer times) {
                        if (residueTime < 1000) {
                            residueTime = 0;
                            autoSubmitHomeWork();
                            timingTask.stop();
                        } else {
                            residueTime -= 1000;
                        }
                        refreshTime(residueTime);
                    }
                });
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    /**
     * 时间到了自动提交任务
     */
    private void autoSubmitHomeWork() {
        //是否要提示：
//        ToastUtil.showCustomToast(getApplicationContext(), "定时时间到，作业将自动提交！");
        mNbvAnswerBoard.setIntercept(true);
        invalidate();
        saveLastHomeWorkData(showHomeWorkPosition, false);
        getUpLoadInfo();
    }

    @Override
    protected void initLayout() {
        //新建写字板，并添加到界面上
        /*findViewById(R.id.ll_content_displayer).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                findViewById(R.id.ll_content_displayer).getViewTreeObserver().removeGlobalOnLayoutListener(this);

            }
        });*/

        mNbvAnswerBoard = new NoteBookView2(WriteHomeWorkActivity.this);

        mCaogaoNoteBoard = new NoteBookView2(this, 960, 420);
        mNbvAnswerBoard.setIntercept(true);
//        findViewById(R.id.img_btn_right).setVisibility(View.GONE);

        /*mNbvAnswerBoard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (llCaogaoControl.getVisibility() == View.VISIBLE) {
                    onClick(findViewById(R.id.tv_caogao_text));
                }
                return false;
            }
        });*/

        ContentDisplayerAdapterV2 contentAdapter = new ContentDisplayerAdapterV2() {
            @Override
            public void afterPageCountChanged(String typeKey) {
                int newPageCount = getPageCount(typeKey);


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


                }
            }

        };
        contentDisplayer.setContentAdapter(contentAdapter);

        contentDisplayer.setLoadingStatusListener(new ContentDisplayerV2.StatusChangeListener() {
            @Override
            public void onStatusChanged(ContentDisplayerV2.LOADING_STATUS newStatus, String typeKey,
                                        int pageIndex, String url, ContentDisplayerV2.ERROR_TYPE errorType, String errorMsg) {
                if (questionList == null) {
                    return;
                }

                switch (newStatus) {
                    case DOWNLOADING:
                        contentDisplayer.setHintText("下载中");
                        break;
                    case LOADING:
                        contentDisplayer.setHintText("加载中");
                        break;
                    case ERROR:
                        contentDisplayer.setHintText("加载错误，请刷新");
                        break;
                    case SUCCESS:
                        contentDisplayer.setHintText(null);//设置为null该view会gone
                        break;
                }


                if ("选择".equals(questionList.get(0).getExtraData()) || "判断".equals(questionList.get(0).getExtraData())) {
                    mNbvAnswerBoard.setVisibility(View.GONE);
                } else {
                    if (newStatus == ContentDisplayerV2.LOADING_STATUS.SUCCESS) {

                        UIUtils.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mNbvAnswerBoard != null) {
                                    mNbvAnswerBoard.setVisibility(View.VISIBLE);
                                    onClick(tvTitle);
                                }
                            }
                        }, 600);
                    } else {
                        mNbvAnswerBoard.setVisibility(View.GONE);
                    }
                }
            }
        });
    }


    @Override
    protected void loadData() {

        showNoNetDialog();

        NetWorkManager.queryExam(examId, null).subscribe(new Action1<List<HomeworkDetail>>() {
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

                if (mNbvAnswerBoard != null) {
                    mNbvAnswerBoard.leaveScribbleMode(true);
                }

                if (mCaogaoNoteBoard != null && mCaogaoNoteBoard.getVisibility() == View.VISIBLE) {
                    mCaogaoNoteBoard.leaveScribbleMode(true);
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

                        if (allHomeWorkPage.getVisibility() == View.VISIBLE) {
                            allHomeWorkPage.setVisibility(View.GONE);
                            ivChooeseTag.setImageResource(R.drawable.img_timu_down);
                            UIUtils.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mNbvAnswerBoard != null) {
                                        mNbvAnswerBoard.setIntercept(false);
                                    }
                                }
                            }, 600);
                        }

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
                    ToastUtil.showCustomToast(getBaseContext(), "该题题目可能已经删除");
                    return;
                }

                parsedQuestionItem = parsedQuestionItemList.get(0);
                questionList = parsedQuestionItem.questionContentList;

                //判断是否之前有笔记
                questionPageSize = bytesList.size() >= questionList.size() ? bytesList.size() : questionList.size();

                //切换题目时，先丢掉之前添加的写字板
                if (isAddAnswerBoard) {
                    rlAnswer.removeView(mNbvAnswerBoard);
                    isAddAnswerBoard = false;
                }

                if (questionList != null && questionList.size() > 0) {
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
                }
                contentDisplayer.getContentAdapter().updateDataList("question", questionList);

                //取题目的第一页纸展示
                if (questionList != null && questionList.size() > 0) {
                    isFirstComeInQuestion = true;
                    if (btnLocked == false) {
                        btnLocked = true;
                        clickPageBtn(0);
                        btnLocked = false;
                    }
                }

                String useTime = SharedPreferencesUtil.getSpUtil().getString(examId + "_" + showHomeWorkPosition + "_use_time", "");

                long alreadyUseTime = 0;
                if (!TextUtils.isEmpty(useTime)) {
                    alreadyUseTime = DateUtils.transformToTime_second(useTime) * 1000;
                }

                startTimeMill = System.currentTimeMillis() - alreadyUseTime;
                startClock();

                homeWorkPageNumAdapter.notifyDataSetChanged();
                refreshLastAndNextQuestionBtns();
                if (examPaperContent.getPaperItemWeight() == null) {
                    tvHomeWorkPosition.setText((showHomeWorkPosition + 1) + "/" + homeWorkPageSize);
                } else {
                    tvHomeWorkPosition.setText((showHomeWorkPosition + 1) + "/" + homeWorkPageSize + "(" + examPaperContent.getPaperItemWeight() + "分)");
                }
            }
        });


        //作业中某一题题目、答案切换
        questionPageNumAdapter = new QuestionPageNumAdapter();
        CustomLinearLayoutManager linearLayoutManager = new CustomLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager.setScrollHorizontalEnabled(false);
        allQuestionPage.setLayoutManager(linearLayoutManager);
        allQuestionPage.setAdapter(questionPageNumAdapter);

        questionPageNumAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick1(int position) {
                if (btnLocked == false) {
                    btnLocked = true;
                    clickPageBtn(position);
                    btnLocked = false;
                }
            }
        });

        if (examPaperContentList != null && examPaperContentList.size() > 0) {

            isFirstComeInHomeWork = true;
            homeWorkPageNumAdapter.onItemClickListener.onItemClick1(showHomeWorkPosition);

        }

        if (allHomeWorkPage.getVisibility() == View.VISIBLE) {
            //触发一下点击事件。默认隐藏所有题目
            onClick(findViewById(R.id.ll_chooese_homework));
        }
    }

    public void clickPageBtn(int position) {

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
                llCaogaoControl.setVisibility(View.GONE);
                mNbvAnswerBoard.setIntercept(false);
            }
            mCaogaoNoteBoard.clearAll();

            //如果 mNbvAnswerBoard是显示的说明是非选择题，需要保持笔记
            if (mNbvAnswerBoard.getVisibility() == View.VISIBLE) {
                //保存上一个题目多页数据中的某一页手写笔记。
                byte[] tmpBytes1 = mNbvAnswerBoard.bitmap2Bytes();
                bytesList.set(saveQuestionPage, tmpBytes1);
            }
            //是否是选择题。都需要截屏保存图片
            pathList.set(saveQuestionPage, saveBitmapToFile(mNbvAnswerBoard.getBitmap(), examId + "_" + showHomeWorkPosition + "_" + saveQuestionPage));
        }

        mNbvAnswerBoard.clearAll();

        chooesePoint = position;

        //将本页设置为选中页
        saveQuestionPage = position;

        if (position < contentDisplayer.getContentAdapter().getPageCount("question")) {
            //切换当前题目的分页
            ContentDisplayerV2.StatusChangeListener specificListener = null;
            if (questionList.get(0) != null
                    && ("问答".equals(questionList.get(0).getExtraData()))) {
                specificListener = new ContentDisplayerV2.StatusChangeListener() {
                    @Override
                    public void onStatusChanged(ContentDisplayerV2.LOADING_STATUS newStatus, String typeKey, int pageIndex, String url, ContentDisplayerV2.ERROR_TYPE errorType, String errorMsg) {
                        if (newStatus == ContentDisplayerV2.LOADING_STATUS.SUCCESS) {
                            if (position + 1 == contentDisplayer.getContentAdapter().getPageCount("question")) {
                                Bitmap bitmap = ((BitmapDrawable) contentDisplayer.getPdfImageView().getDrawable()).getBitmap();
                                for (int y = bitmap.getHeight() - 1; y >= 0; y--) {
                                    for (int x = 0; x < bitmap.getWidth(); x++) {
                                        if (bitmap.getPixel(x, y) != -1) {
                                            lineImgview.setVisibility(View.VISIBLE);
                                            lineImgview.setPadding(30, y + 1, 0, 0);
                                            return;
                                        }
                                    }
                                }
                            } else {
                                lineImgview.setVisibility(View.GONE);
                            }
                        }
                    }
                };
            } else {
                lineImgview.setVisibility(View.GONE);
            }
            contentDisplayer.toPage("question", position, false, specificListener);
            contentDisplayer.setVisibility(View.VISIBLE);
        } else {
            //加白纸
            contentDisplayer.setVisibility(View.GONE);
            //新需求:在问答题加白纸上需要显示横线背景
            if (questionList.get(0) != null
                    && ("问答".equals(questionList.get(0).getExtraData()))) {
                lineImgview.setVisibility(View.VISIBLE);
                lineImgview.setPadding(30, 0, 0, 0);
            } else {
                lineImgview.setVisibility(View.GONE);
            }
        }
        if (questionList.get(0) != null) {
            if ("选择".equals(questionList.get(0).getExtraData())) {
                if (isAddAnswerBoard) {
                    rlAnswer.removeView(mNbvAnswerBoard);
                    isAddAnswerBoard = false;
                }
                rcvChooese.setVisibility(View.VISIBLE);
                llChooeseItem.setVisibility(View.GONE);
                chooeseAnswerList = parsedQuestionItem.answerList;
                //选择题不能加页
                tvAddPage.setVisibility(View.GONE);
                tvClearWrite.setVisibility(View.GONE);

                setChooeseResult();

                //刷新当前选择结果的reciv
                if (rcvChooese.getAdapter() != null) {
                    rcvChooese.getAdapter().notifyDataSetChanged();
                }
//                if (saveQuestionPage == 0) {
//                    rcvChooese.setVisibility(View.VISIBLE);
//                } else {
//                    rcvChooese.setVisibility(View.GONE);
//                }

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

                //从之前judgeAnswerList中回显之前保存的判断结果，如果有的话
                if (judgeAnswerList.size() != 0) {
                    String judgeResult = judgeAnswerList.get(0);
                    if ("true".equals(judgeResult)) {
                        rbRight.setChecked(true);
                        rbError.setChecked(false);
                    } else {
                        rbRight.setChecked(false);
                        rbError.setChecked(true);
                    }
                } else {
                    rbRight.setChecked(false);
                    rbError.setChecked(false);
                    rgJudge.clearCheck();
                }

//                if (saveQuestionPage == 0) {
//                    llChooeseItem.setVisibility(View.VISIBLE);
//                } else {
//                    llChooeseItem.setVisibility(View.GONE);
//                }

            } else {
                if (!isAddAnswerBoard) {
                    RelativeLayout.LayoutParams layer1LayoutParam = new RelativeLayout.LayoutParams(960, 920);
                    rlAnswer.addView(mNbvAnswerBoard, layer1LayoutParam);
                    isAddAnswerBoard = true;
                }
                llChooeseItem.setVisibility(View.GONE);
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
        moveToPosition((LinearLayoutManager) allQuestionPage.getLayoutManager(), pageDeviationNum);
    }


    private void startClock() {

        if (SystemUtils.getDeviceModel().contains(FileContonst.DEVICE_TYPE_PL107)) {
            return;
        }
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
        tvSubmitTime.setText("时间：" + DateUtils.converLongTimeToString(spentTimeMill));
    }

    private void refreshTime(long time) {
        tvTiming.setText("时间：" + DateUtils.converLongTimeToString(time));
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
                if (mNbvAnswerBoard != null) {
                    mNbvAnswerBoard.leaveScribbleMode(true);
                }

                if (mCaogaoNoteBoard != null && mCaogaoNoteBoard.getVisibility() == View.VISIBLE) {
                    mCaogaoNoteBoard.leaveScribbleMode(true);
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

    private boolean mIsFinish;

    @Override
    public void onBackPressed() {
        if (mIsOnClass && !mIsSubmit) {
            // 课堂作业，只能点击提交返回   == 待需求确认
            return;
        }
        if (mNbvAnswerBoard != null) {
            mNbvAnswerBoard.leaveScribbleMode(true);
        }

        if (mCaogaoNoteBoard != null && mCaogaoNoteBoard.getVisibility() == View.VISIBLE) {
            mCaogaoNoteBoard.leaveScribbleMode(true);
        }
        super.onBackPressed();
    }

    private static long lastClickTime;

    @OnClick({R.id.tv_dismiss_caogao, R.id.tv_caogao_text, R.id.btn_left, R.id.tv_last_homework, R.id.tv_next_homework, R.id.tv_save_homework,
            R.id.tv_submit_homework, R.id.tv_clear_write, R.id.tv_add_page, R.id.ll_chooese_homework, R.id.rb_error, R.id.rb_right, R.id.tv_title, R.id.image_refresh})
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

            case R.id.tv_title:
                break;
            case R.id.btn_left:
                mIsFinish = true;
                finish();
                break;

            case R.id.tv_last_homework:
                if (showHomeWorkPosition > 0) {
                    showHomeWorkPosition--;
                    COMEIN_HOMEWORK_PAGE_MODE = 1;
                    chooesePoint = 0;
                    homeWorkPageNumAdapter.onItemClickListener.onItemClick1(showHomeWorkPosition);
                } else {
                    ToastUtil.showCustomToast(this, "已经是第一题了");
                }
                break;
            case R.id.tv_next_homework:
                if (showHomeWorkPosition < homeWorkPageSize - 1) {
                    showHomeWorkPosition++;
                    COMEIN_HOMEWORK_PAGE_MODE = 2;
                    chooesePoint = 0;
                    homeWorkPageNumAdapter.onItemClickListener.onItemClick1(showHomeWorkPosition);
                } else {
                    ToastUtil.showCustomToast(this, "已经是最后一题了");
                }
                break;
            case R.id.tv_save_homework:
                //暂存，默认触发调转到下一题（如果有），然后打开暂存成功弹窗
                if (showHomeWorkPosition < homeWorkPageSize - 1) {
                    showHomeWorkPosition++;
                    COMEIN_HOMEWORK_PAGE_MODE = 2;
                    if (homeWorkPageNumAdapter != null && homeWorkPageNumAdapter.onItemClickListener != null) {
                        homeWorkPageNumAdapter.onItemClickListener.onItemClick1(showHomeWorkPosition);
                    }
                } else {
                    //如果已经是最后一题，那么不在跳转。直接打开暂存成功弹窗
                    saveLastHomeWorkData(showHomeWorkPosition, false);
                }
                // 这里需要跳转到暂存成功的弹窗界面
                if (mNbvAnswerBoard != null) {
                    mNbvAnswerBoard.setVisibility(View.GONE);
                }
                FullScreenHintDialog fullScreenHintDialog = new FullScreenHintDialog(this, "");
                fullScreenHintDialog.setIconResId(R.drawable.icon_correct).setContentText("暂存成功").setBtn1("继续作答", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fullScreenHintDialog.dismiss();

                        UIUtils.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mNbvAnswerBoard != null) {
                                    mNbvAnswerBoard.setVisibility(View.VISIBLE);
                                    WriteHomeWorkActivity.this.onClick(tvTitle);
                                }
                            }
                        }, 600);

                    }
                }, false).setBtn2("返回作业", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fullScreenHintDialog.dismiss();

//                        YoungyApplicationManager.getRxBus(getBaseContext()).send("refreshHomeworkList");
                        mIsFinish = true;
                        onBackPressed();
                    }
                }, false).setShowNoMoreAgainHint(false).show();
                fullScreenHintDialog.setBtn1Style(R.drawable.bind_confirm_btn_bg, R.color.white);


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
                    saveLastHomeWorkData(showHomeWorkPosition, false);
//                    getUpLoadInfo();
                }

                //打开未完成提示
                fullScreenHintDialog = new FullScreenHintDialog(this, "");
                fullScreenHintDialog.setIconResId(R.drawable.icon_correct).setContentText("是否提交作业").setBtn1("检查作业", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fullScreenHintDialog.dismiss();
                    }
                }, false).setBtn2("确认提交", new OnClickFastListener() {

                    @Override
                    public void onFastClick(DialogInterface dialogInterface, int i) {
                        fullScreenHintDialog.dismiss();
                        // 去提交,学生手动点击提交按钮后,屏蔽老师的自动收取消息,防止多次提交的错误.今后只能手动提交(只对本次作业生效)
                        isUpByUser = true;
                        getUpLoadInfo();
                    }
                }, false).setShowNoMoreAgainHint(false).show();
                fullScreenHintDialog.setBtn1Style(R.drawable.bind_confirm_btn_bg, R.color.white);


                break;
            case R.id.tv_clear_write:

                new ConfirmDialog(WriteHomeWorkActivity.this, "是否清空作答笔迹？",
                        "确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                UIUtils.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mNbvAnswerBoard.clearAll();
                                        RefreshUtil.invalidate(rlAnswer);
                                    }
                                }, 600);
                            }
                        },
                        "取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case R.id.tv_add_page:
                if (btnLocked == false) {
                    btnLocked = true;
                    if (questionPageSize - contentDisplayer.getContentAdapter().getPageCount("question") >= 5) {
                        ToastUtil.showCustomToast(this, "最多只能加5张纸");
                        btnLocked = false;
                        return;
                    }
                    questionPageSize++;
                    bytesList.add(null);
                    pathList.add(null);
                    cgBytes.add(null);
                    questionPageNumAdapter.notifyDataSetChanged();
                    clickPageBtn(questionPageSize - 1);
                    btnLocked = false;
                }
                break;
            case R.id.ll_chooese_homework:
                if (allHomeWorkPage.getVisibility() == View.GONE) {
                    allHomeWorkPage.setVisibility(View.VISIBLE);
                    ivChooeseTag.setImageResource(R.drawable.img_timu_up);
                    mNbvAnswerBoard.setIntercept(true);
                } else {
                    allHomeWorkPage.setVisibility(View.GONE);
                    ivChooeseTag.setImageResource(R.drawable.img_timu_down);

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
                    //TDO
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

            case R.id.rb_right:
                if (judgeAnswerList.size() == 0) {
                    judgeAnswerList.add("true");
                } else {
                    judgeAnswerList.set(0, "true");
                }
                break;
            case R.id.rb_error:
                if (judgeAnswerList.size() == 0) {
                    judgeAnswerList.add("false");
                } else {
                    judgeAnswerList.set(0, "false");
                }
                break;

            case R.id.image_refresh:
                saveLastHomeWorkData(showHomeWorkPosition, false);
                loadData();
                break;
        }
    }


    /**
     * add by FH
     * 刷新上一题下一题按钮的UI,如果已经是第一题或者最后一题了,就置灰按钮
     */
    public void refreshLastAndNextQuestionBtns() {

        if (showHomeWorkPosition > 0) {
            lastQuestionBtn.setVisibility(View.VISIBLE);
            lastQuestionText.setTextColor(Color.WHITE);
            lastQuestionIcon.setImageResource(R.drawable.img_timu_last);
        } else {
            lastQuestionBtn.setVisibility(View.GONE);
            lastQuestionText.setTextColor(Color.WHITE);
            lastQuestionIcon.setImageResource(R.drawable.img_timu_last);
        }
        if (showHomeWorkPosition < homeWorkPageSize - 1) {
            nextQuestionBtn.setVisibility(View.VISIBLE);
            nextQuestionText.setTextColor(Color.WHITE);
            nextQuestionIcon.setImageResource(R.drawable.img_timu_next);
        } else {
            nextQuestionBtn.setVisibility(View.GONE);
            nextQuestionText.setTextColor(Color.WHITE);
            nextQuestionIcon.setImageResource(R.drawable.img_timu_next);
        }
    }

    private void saveLastHomeWorkData(int position, boolean clear) {
        if (bytesList.size() == 0) {
            return;
        }

        //如果草稿纸打开着，需要先将草稿纸隐藏。用于截图
        if (llCaogaoControl.getVisibility() == View.VISIBLE) {
            tvCaogaoText.setText("草稿纸");
            cgBytes.set(saveQuestionPage, mCaogaoNoteBoard.bitmap2Bytes());
            llCaogaoControl.setVisibility(View.GONE);
            mNbvAnswerBoard.setIntercept(false);
        }
        mCaogaoNoteBoard.clearAll();


        //刷新最后没有保存的数据
        //如果 mNbvAnswerBoard是显示的说明是非选择题，需要保持笔记
        if (mNbvAnswerBoard.getVisibility() == View.VISIBLE) {
            bytesList.set(saveQuestionPage, mNbvAnswerBoard.bitmap2Bytes());
        }
        //是否是选择题。都需要截屏保存图片
        pathList.set(saveQuestionPage, saveBitmapToFile(mNbvAnswerBoard.getBitmap(), examId + "_" + position + "_" + saveQuestionPage));
        if (clear) {
            mNbvAnswerBoard.clearAll();
        }


        //保存手写笔记，用于回显（1，暂存时，2，题目切换时）
        DataCacheUtils.putObject(WriteHomeWorkActivity.this, examId + "_" + position + "_bytes_list", bytesList);
        DataCacheUtils.putObject(WriteHomeWorkActivity.this, examId + "_" + position + "_caogao_bytes_list", cgBytes);
        //保存待上传图片，用于上传
        getSpUtil().setDataList(examId + "_" + position + "_path_list", pathList);
        getSpUtil().setDataList(examId + "_" + position + "_chooese_list", checkedAnswerList);
        getSpUtil().setDataList(examId + "_" + position + "_judge_list", judgeAnswerList);


        if (SystemUtils.getDeviceModel().contains(FileContonst.DEVICE_TYPE_PL107)) {
            refreshTime();
        }

        String textInfo = tvSubmitTime.getText().toString();
        if (textInfo.startsWith("时间：")) {
            getSpUtil().putString(examId + "_" + position + "_use_time", textInfo.substring(3));
        }

        if (clear) {
            //本题所有数据保存完毕
            saveQuestionPage = 0;

            //以上存储了3中数据，1：保存手写笔记集合，2：保存每页图片集合，3：如果是选择题，保存选择题结果字符串。
            //存储成功之后，将内存中的数据全部清空。

            for (int i = 0; i < bytesList.size(); i++) {
                byte[] tmp = bytesList.get(i);
                tmp = null;
            }

            bytesList.clear();
//            cgBytes.clear();
            pathList.clear();
            checkedAnswerList.clear();
            judgeAnswerList.clear();
        }

        if (SystemUtils.getDeviceModel().contains(FileContonst.DEVICE_TYPE_PL107)) {
            tvSubmitHomeWork.setText("提交答案");
        }
    }

    /**
     * 保存之前操作题目结果数据
     */
    private void saveLastHomeWorkData(int position) {
        saveLastHomeWorkData(position, true);
    }


    /**
     * 得到之前保存的数据，用于回显
     */
    private void getShowHomeWorkData(int position) {

        bytesList.clear();
        cgBytes.clear();
        pathList.clear();
        checkedAnswerList.clear();
        judgeAnswerList.clear();

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
        //回显之前存储在sp中的判断结果数据（如果有）
        List<String> tmpJudgeAnswerList = getSpUtil().getDataList(examId + "_" + position + "_judge_list");
        if (tmpJudgeAnswerList != null && tmpJudgeAnswerList.size() > 0) {
            judgeAnswerList.addAll(tmpJudgeAnswerList);
        }

    }


    /**
     * 获取oss上传所需信息
     */
    private synchronized void getUpLoadInfo() {

        if (isUpload) {
            return;
        }
        isUpload = true;

        showNoNetDialog();
        NetWorkManager.queryReplyRequest(SpUtils.getUserId() + "")
                .subscribe(new Action1<STSbean>() {
                    @Override
                    public void call(STSbean stSbean) {
                        if (stSbean != null) {
                            upLoadPic(stSbean);
                        } else {
                            ToastUtil.showCustomToast(getApplicationContext(), "获取上传信息失败");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        isUpload = false;
                    }
                });

    }

    /**
     * 上传图片，使用同步方法上传
     *
     * @param stSbean
     */
    public void upLoadPic(STSbean stSbean) {

        String endpoint = Commons.ENDPOINT;


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
        OSS oss = new OSSClient(YoungyApplicationManager.getContext(), endpoint, credentialProvider, conf);

        //作业中某一题所有结果（图片，文本），统计上传数据到集合中，方便将该信息提交到服务器
        ArrayList<HomeWorkResultbean> homeWorkResultbeanList = new ArrayList<>();
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


                            if (!TextUtils.isEmpty(picPath) && picPath.contains("/")) {

                                String picName = picPath.substring(picPath.lastIndexOf("/"));


                                // 构造上传请求
                                PutObjectRequest put = new PutObjectRequest(stSbean.getBucketName(), stSbean.getPath() + picName, picPath);
                                try {
                                    PutObjectResult putResult = oss.putObject(put);
                                    LogUtils.e("PutObject", "UploadSuccess");
                                    LogUtils.e("ETag", putResult.getETag());
                                    LogUtils.e("RequestId", putResult.getRequestId());
                                } catch (ClientException e) {
                                    // 本地异常如网络异常等
                                    e.printStackTrace();
                                    isUpload = false;
                                } catch (ServiceException e) {
                                    // 服务异常
                                    LogUtils.e("RequestId", e.getRequestId());
                                    LogUtils.e("ErrorCode", e.getErrorCode());
                                    LogUtils.e("HostId", e.getHostId());
                                    LogUtils.e("RawMessage", e.getRawMessage());
                                    isUpload = false;
                                }

                                STSResultbean stsResultbean = new STSResultbean();
                                stsResultbean.setBucket(stSbean.getBucketName());
                                stsResultbean.setRemote(stSbean.getPath() + picName);
                                File picFile = new File(picPath);
                                stsResultbean.setSize(picFile.length());
                                stsResultbeanArrayList.add(stsResultbean);
                                //上传后清理掉本地图片文件
                                picFile.delete();
                            } else {
                                STSResultbean stsResultbean = new STSResultbean();
                                stsResultbean.setBucket(stSbean.getBucketName());
                                stsResultbean.setRemote("");
                                stsResultbean.setSize(0);
                                stsResultbeanArrayList.add(stsResultbean);
                            }

                        }
                        tmpPathList.clear();
                        List<String> tmpCheckedAnswerList = getSpUtil().getDataList(examId + "_" + i + "_chooese_list");
                        List<String> tmpJudgeAnswerList = getSpUtil().getDataList(examId + "_" + i + "_judge_list");
                        String useTime = SharedPreferencesUtil.getSpUtil().getString(examId + "_" + i + "_use_time", "");

                        HomeWorkResultbean homeWorkResultbean = new HomeWorkResultbean();
                        homeWorkResultbean.setExamId(Integer.parseInt(examId));
                        int itemId = examPaperContentList.get(i).getPaperItem();
                        homeWorkResultbean.setItemId(itemId);

                        //postReply 接口 有新增字段 replyCommentator 如果是自评作业传学生自己，老师批改的传教师id ，互评0 by后台马国东定义
//                        if (isStudentCheck == 0) {
//                            homeWorkResultbean.setReplyCommentator(teacherId);
//                        } else if (isStudentCheck == 1) {
//                            homeWorkResultbean.setReplyCommentator(SpUtils.getUserId());
//                        } else {
//                            homeWorkResultbean.setReplyCommentator(0);
//                        }
                        homeWorkResultbean.setPicContent(stsResultbeanArrayList);
                        homeWorkResultbean.setUseTime(useTime);
                        homeWorkResultbean.setTxtContent(tmpJudgeAnswerList.size() > 0 ? tmpJudgeAnswerList : tmpCheckedAnswerList);
                        homeWorkResultbean.setReplyCreateTime(DateUtils.getCalendarAndTimeString());
                        homeWorkResultbeanList.add(homeWorkResultbean);


                        //清理掉缓存书写笔记，图片地址存 ,选择结果
                        DataCacheUtils.reomve(getBaseContext(), examId + "_" + i + "_bytes_list");
                        DataCacheUtils.reomve(getBaseContext(), examId + "_" + i + "_caogao_bytes_list");
                        SharedPreferencesUtil.getSpUtil().remove(examId + "_" + i + "_path_list");
                        SharedPreferencesUtil.getSpUtil().remove(examId + "_" + i + "_chooese_list");
                        SharedPreferencesUtil.getSpUtil().remove(examId + "_" + i + "_judge_list");
                        SharedPreferencesUtil.getSpUtil().remove(examId + "_" + i + "_use_time");
                    } else {

                        //后台定义没有做的题目 任然上传，结果数据为空
                        HomeWorkResultbean homeWorkResultbean = new HomeWorkResultbean();
                        homeWorkResultbean.setExamId(Integer.parseInt(examId));
                        int itemId = examPaperContentList.get(i).getPaperItem();
                        homeWorkResultbean.setItemId(itemId);
                        homeWorkResultbean.setReplyCreateTime(DateUtils.getCalendarAndTimeString());
                        //postReply 接口 有新增字段 replyCommentator 如果是自评作业传学生自己，老师批改的传教师id ，互评0 by后台马国东定义
//                        if (isStudentCheck == 0) {
//                            homeWorkResultbean.setReplyCommentator(teacherId);
//                        } else if (isStudentCheck == 1) {
//                            homeWorkResultbean.setReplyCommentator(SpUtils.getUserId());
//                        } else {
//                            homeWorkResultbean.setReplyCommentator(0);
//                        }
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
                            loadingProgressDialog.setTitle(R.string.loading_text);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        if (loadingProgressDialog != null) {
                            loadingProgressDialog.dismiss();
                            loadingProgressDialog = null;
                        }
                        writeInfoToS(homeWorkResultbeanList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        isUpload = false;
                        if (loadingProgressDialog != null) {
                            loadingProgressDialog.dismiss();
                            loadingProgressDialog = null;
                        }
                        new HintDialog(WriteHomeWorkActivity.this, "作业轨迹上传oss失败，请退出后重新操作", "确定", new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                finish();
                            }
                        }).show();
                    }

                    @Override
                    public void onNext(Object o) {
                        if (loadingProgressDialog != null) {
                            loadingProgressDialog.dismiss();
                            loadingProgressDialog = null;
                        }
                    }
                });
    }


    /**
     * 将上传信息提交给服务器
     */
    private void writeInfoToS(ArrayList<HomeWorkResultbean> homeWorkResultbeanList) {
        String content = new Gson().toJson(homeWorkResultbeanList);

        if (homeWorkResultbeanList != null) {
            homeWorkResultbeanList.clear();
        }
        homeWorkResultbeanList = null;

        NetWorkManager.postReply(SpUtils.getUserId() + "", content)
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if (timedTask != null) {
                            timedTask.stop();
                        }
                        if (timingTask != null) {
                            timingTask.stop();
                        }
                        ToastUtil.showCustomToast(getBaseContext(), "提交完毕");
                        YoungyApplicationManager.removeHomeworkRemind(examId);
                        SpUtils.setHomeworkOrTaskFinished("homework_" + examId);
                        //发送消息
                        if (teacherId != 0) {
                            sendFinishMsgToTeacher();
                        }
                        mIsSubmit = true;

                        //国东添加接口：互评作业分配接口
                        if (isStudentCheck == 2) {
                            NetWorkManager.allocationMutualHomework(examId)
                                    .subscribe(new Action1<Object>() {
                                        @Override
                                        public void call(Object o) {

                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            throwable.printStackTrace();
                                            ToastUtil.showCustomToast(getBaseContext(), "allocationMutualHomework接口错误");
                                        }
                                    });
                        }
                        onBackPressed();

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        isUpload = false;
                        throwable.printStackTrace();
                        if (throwable instanceof ApiException) {
                            String errorCode = ((ApiException) throwable).getCode();
                            if (errorCode.equals("400")) {
                                //FIXME 这里会有问题
                                //理论上这里被拒绝可能有两种情况,一是之前已经提交成功,但是pad端没有收到成功的返回消息,这时点击重试,就会走到这里,这种情况下,pad端没有走过之前提交成功的逻辑.
                                //二是,按得太快发了两次请求,第一次成功了,并且已经走了上面提交成功的逻辑,第二次提交直接走了这里.
                                //对于第一种情况,我们应该手动转到和上面提交成功一样的逻辑.而对于第二种情况,我们应该直接提示成功,跳过上面提交成功的逻辑(因为之前已经走了一次了)
                                //而现在的情况是,我们无法区分是第一种情况还是第二种情况导致走到这里,所以统一只采用对应第二种情况的处理办法,直接提示成功.
                                HintDialog hintDialog = new HintDialog(WriteHomeWorkActivity.this, "该作业已经完成提交", "确定", new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        finish();
                                    }
                                });
                                hintDialog.show();
                            }
                        } else {
                            ToastUtil.showCustomToast(getBaseContext(), "提交失败，请重试");
                        }
                    }
                });
    }


    //完成作业后发送消息给教师端
    private void sendFinishMsgToTeacher() {

        NetWorkManager.querySchoolTeamByStudentAndExam(SpUtils.getUserId() + "", examId)
                .subscribe(new Action1<TeamBean>() {
                    @Override
                    public void call(TeamBean teamBean) {
                        if (teamBean != null) {

                            YXClient.getInstance().sendSubmitHomeworkMsg(Integer.parseInt(examId)
                                    , SessionTypeEnum.P2P
                                    , SpUtils.getAccountId()
                                    , SpUtils.getAccountName()
                                    , teacherId
                                    , teamBean.getTeamId()
                                    , new RequestCallback<Void>() {

                                        @Override
                                        public void onSuccess(Void param) {
                                            LogUtils.d("提交消息通知教师成功！");
                                        }

                                        @Override
                                        public void onFailed(int code) {
                                            LogUtils.d("提交消息通知教师失败！ code = " + code);
                                        }

                                        @Override
                                        public void onException(Throwable exception) {
                                            LogUtils.d("提交消息通知教师异常！ " + exception.getMessage());
                                        }
                                    });

                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
//                        throwable.printStackTrace();
                        //如果不是分组作业这里获取的话服务器返回400，
                        YXClient.getInstance().sendSubmitHomeworkMsg(Integer.parseInt(examId)
                                , SessionTypeEnum.P2P
                                , SpUtils.getAccountId()
                                , SpUtils.getAccountName()
                                , teacherId
                                , 0
                                , new RequestCallback<Void>() {

                                    @Override
                                    public void onSuccess(Void param) {
                                        LogUtils.d("提交消息通知教师成功！");
                                    }

                                    @Override
                                    public void onFailed(int code) {
                                        LogUtils.d("提交消息通知教师失败！ code = " + code);
                                    }

                                    @Override
                                    public void onException(Throwable exception) {
                                        LogUtils.d("提交消息通知教师异常！ " + exception.getMessage());
                                    }
                                });
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
        tBitmap = Bitmap.createBitmap(tBitmap);
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
//            bitmap.recycle();
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


            if (position == chooesePoint) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YoungyApplicationManager.getMainThreadHandler().removeCallbacks(mAutoSubmit);
        YoungyApplicationManager.getMainThreadHandler().removeCallbacks(mInterceptAnswerBoard);
        removeReceiveHomeworkMsg();
        if (examPaperContentList != null) {
            examPaperContentList.clear();
        }
        examPaperContentList = null;

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
        if (judgeAnswerList != null) {
            judgeAnswerList.clear();
        }
        judgeAnswerList = null;

        if (timedTask != null) {
            timedTask.stop();
        }
        timedTask = null;

        if (timingTask != null) {
            timingTask.stop();
        }
        timingTask = null;

        if (bytesList != null) {
            pathList.clear();
        }
        pathList = null;


        if (cgBytes != null) {
            cgBytes.clear();
        }
        cgBytes = null;

        if (mNbvAnswerBoard != null) {
            mNbvAnswerBoard.recycle();
        }
        mNbvAnswerBoard = null;


        if (mCaogaoNoteBoard != null) {
            mCaogaoNoteBoard.recycle();
        }
        mCaogaoNoteBoard = null;

        Glide.get(this).clearMemory();
        contentDisplayer.clearPdfCache();
        Runtime.getRuntime().gc();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mIsFinish) {
            //tvSaveHomework.callOnClick();
        }
        if (isTimerWork) {
            saveLastHomeWorkData(showHomeWorkPosition, false);
        }

        if (mNbvAnswerBoard != null) {
            mNbvAnswerBoard.leaveScribbleMode();
        }
        if (mCaogaoNoteBoard != null) {
            mCaogaoNoteBoard.leaveScribbleMode();
        }
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
                mNbvAnswerBoard.leaveScribbleMode();
                mNbvAnswerBoard.setIntercept(false);
            }

            if (mCaogaoNoteBoard != null) {
                mCaogaoNoteBoard.leaveScribbleMode();
                mCaogaoNoteBoard.setIntercept(false);
            }
        }
        if (event.getType().equalsIgnoreCase(EventBusConstant.EVENT_START_ACTIIVTY_ORDER)) {
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

    public String getExam_id() {
        return examId;
    }

}
