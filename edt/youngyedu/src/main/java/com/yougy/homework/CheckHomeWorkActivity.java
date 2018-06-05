package com.yougy.homework;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
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
import com.bumptech.glide.Glide;
import com.yougy.anwser.ContentDisplayer;
import com.yougy.anwser.Content_new;
import com.yougy.anwser.STSResultbean;
import com.yougy.anwser.STSbean;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.global.Commons;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.new_network.RxResultHelper;
import com.yougy.common.popupwindow.PopupMenuManager;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.RecycleUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.homework.bean.QuestionReplyDetail;
import com.yougy.message.ListUtil;
import com.yougy.ui.activity.R;
import com.yougy.view.CustomLinearLayoutManager;
import com.yougy.view.NoteBookView2;
import com.yougy.view.dialog.ConfirmDialog;
import com.yougy.view.dialog.HintDialog;
import com.yougy.view.dialog.LoadingProgressDialog;

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

import static com.yougy.anwser.Content_new.Type.IMG_URL;


/**
 * Created by Administrator on 2017/10/16.
 * 批改作业界面
 */

public class CheckHomeWorkActivity extends BaseActivity {

    private int examId, studentId;
    private String studentName;
    @BindView(R.id.rcv_all_question_page)
    RecyclerView mAllQuestionPageView;
    @BindView(R.id.tv_title)
    TextView titleTextview;
    @BindView(R.id.question_body_btn)
    TextView questionBodyBtn;
    @BindView(R.id.analysis_btn)
    TextView analysisBtn;
    @BindView(R.id.rl_answer)
    RelativeLayout rlAnswer;
    @BindView(R.id.last_page_btn)
    ImageView lastPageBtn;
    @BindView(R.id.next_page_btn)
    ImageView nextPageBtn;
    @BindView(R.id.img_btn_right)
    ImageButton btnRight;


    @BindView(R.id.content_displayer)
    ContentDisplayer contentDisplayer;


    @BindView(R.id.ll_homework_check_option)
    LinearLayout llHomeWorkCheckOption;
    @BindView(R.id.ll_check_again)
    RelativeLayout llCheckAgain;
    @BindView(R.id.iv_check_result)
    ImageView ivCheckResult;
    @BindView(R.id.tv_check_result)
    TextView tvCheckResult;
    @BindView(R.id.iv_check_change)
    TextView ivCheckChange;
    @BindView(R.id.tv_next_homework)
    LinearLayout nextHomeworkBtn;
    @BindView(R.id.tv_last_homework)
    LinearLayout lastHomeworkBtn;
    @BindView(R.id.last_homework_text)
    TextView lastHomeworkText;
    @BindView(R.id.last_homework_icon)
    ImageView lastHomeworkIcon;
    @BindView(R.id.next_homework_text)
    TextView nextHomeworkText;
    @BindView(R.id.next_homework_icon)
    ImageView nextHomeworkIcon;

    private CustomLinearLayoutManager linearLayoutManager;

    private NoteBookView2 mNbvAnswerBoard;

    //模拟一共有多少页
    int pageSize = 0;
    //每页展示页码数
    private static final int PAGE_SHOW_SIZE = 10;
    //当前展示的题目编号（展示的第几题）从0开始。
    private int currentShowQuestionIndex = 0;
    //当前展示的学生答案的页码(从0开始)(没有学生答案展示题目本身)
    private int currentShowReplyPageIndex = 0;
    //当前展示的解析的页码(从0开始)
    private int currentShowAnalysisPageIndex = 0;


    //页码数偏移量
    private int pageDeviationNum = 0;
    private PageNumAdapter pageNumAdapter;


    //byte数组集合，（用来保存每一页书写的笔记数据）
    private ArrayList<byte[]> bytesList = new ArrayList<>();
    //存储每一页截屏图片地址
    private ArrayList<String> pathList = new ArrayList<>();
    //作业中某一题图片上传成功后，统计上传数据到集合中，方便将该信息提交到服务器
    ArrayList<STSResultbean> stsResultbeanArrayList = new ArrayList<>();

    //当前展示要批改的学生作业
    private QuestionReplyDetail questionReplyDetail;
    //当前学生作业中的所有题目数据集合
    private List<QuestionReplyDetail> mQuestionReplyDetails;

    //该题教师给出的评分 默认-1。3个状态0，50,100
    private int score = -1;

    //上一题，下一题的状态 上一题：1，下一题：2.
    private int commitType = 0;

    //获取给学生评判的分数。其中-1表示还没判分，数值用来判断展示已批改还是未批改。选择自动判断也是通过该数值。
    private List<Integer> replyScoreList = new ArrayList<>();

    @Override
    public void init() {
        studentId = SpUtils.getUserId();
        examId = getIntent().getIntExtra("examId", -1);
        studentName = SpUtils.getAccountName();
        titleTextview.setText(studentName);
    }

    @Override
    protected void initLayout() {
        //新建写字板，并添加到界面上
        rlAnswer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rlAnswer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mNbvAnswerBoard = new NoteBookView2(CheckHomeWorkActivity.this, rlAnswer.getMeasuredWidth(), rlAnswer.getMeasuredHeight());
                rlAnswer.addView(mNbvAnswerBoard);
            }
        });
        contentDisplayer.setmContentAdaper(new ContentDisplayer.ContentAdaper() {
            @Override
            public void onPageInfoChanged(String typeKey, int newPageCount, int selectPageIndex) {
                if (typeKey.equals("reply")) {
                    currentShowReplyPageIndex = selectPageIndex;
                } else if (typeKey.equals("analysis")) {
                    currentShowAnalysisPageIndex = selectPageIndex;
                }
                refreshPageBtns();
            }
        });

        contentDisplayer.setOnLoadingStatusChangedListener(new ContentDisplayer.OnLoadingStatusChangedListener() {
            @Override
            public void onLoadingStatusChanged(ContentDisplayer.LOADING_STATUS loadingStatus) {
                if (questionBodyBtn.isSelected()) {
                    int replyScore = -1;
                    if (replyScoreList.size() != 0) {
                        replyScore = replyScoreList.get(currentShowQuestionIndex);
                    }
                    if (replyScore == -1) {//没批
                        if (loadingStatus == ContentDisplayer.LOADING_STATUS.SUCCESS) {
                            mNbvAnswerBoard.setVisibility(View.VISIBLE);
                        } else {
                            mNbvAnswerBoard.setVisibility(View.GONE);
                        }

                    } else {//批了
                        mNbvAnswerBoard.setVisibility(View.GONE);
                    }
                } else {
                    mNbvAnswerBoard.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_check_homework);
    }


    @Override
    public void loadData() {

        showNetDialog();
        NetWorkManager.queryReplyDetail(examId, null, String.valueOf(studentId))
                .subscribe(new Action1<List<QuestionReplyDetail>>() {
                    @Override
                    public void call(List<QuestionReplyDetail> questionReplyDetails) {
                        mQuestionReplyDetails = questionReplyDetails;
                        pageSize = mQuestionReplyDetails.size();

                        if (pageSize == 0) {
                            new HintDialog(getBaseContext(), "改作业题获取结果为0！", "返回", new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    back();
                                }
                            }).show();
                            return;
                        }

                        setPageNumberView();
                        questionReplyDetail = mQuestionReplyDetails.get(currentShowQuestionIndex);
                        for (int i = 0; i < pageSize; i++) {
                            replyScoreList.add(mQuestionReplyDetails.get(i).getReplyScore());
                        }

                        refreshQuestion();
                        refreshLastAndNextQuestionBtns();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        ToastUtil.showCustomToast(getApplicationContext(), "获取数据失败");
                    }
                });

        btnRight.setVisibility(View.VISIBLE);
        btnRight.setImageResource(R.drawable.icon_gengduo);

    }

    @Override
    protected void refreshView() {

    }

    private void refreshQuestion() {
        String questionType = (String) questionReplyDetail.getParsedQuestionItem().questionContentList.get(0).getExtraData();
        ArrayList<Content_new> replyList;
        if ("选择".equals(questionType) || "判断".equals(questionType)) {
            replyList = ListUtil.conditionalSubList(questionReplyDetail.getParsedReplyContentList(), new ListUtil.ConditionJudger<Content_new>() {
                @Override
                public boolean isMatchCondition(Content_new nodeInList) {
                    return nodeInList.getType() != Content_new.Type.TEXT;
                }
            });
            contentDisplayer.getmContentAdaper().setSubText(RxResultHelper.parseAnswerList(questionReplyDetail.getParsedQuestionItem().answerContentList));
        } else {
            replyList = (ArrayList<Content_new>) questionReplyDetail.getParsedReplyContentList();
            contentDisplayer.getmContentAdaper().setSubText(null);
        }
        contentDisplayer.getmContentAdaper().updateDataList("reply", replyList);
        contentDisplayer.getmContentAdaper().updateDataList("analysis", questionReplyDetail.getParsedQuestionItem().analysisContentList);
        contentDisplayer.getmContentAdaper().updateDataList("question", questionReplyDetail.getParsedQuestionItem().questionContentList);

        currentShowReplyPageIndex = 0;
        currentShowAnalysisPageIndex = 0;

        questionBodyBtn.setSelected(false);
        onClick(findViewById(R.id.question_body_btn));

        int replyScore = replyScoreList.get(currentShowQuestionIndex);
        switch (replyScore) {
            case -1://说明未批改
                llHomeWorkCheckOption.setVisibility(View.VISIBLE);
                llCheckAgain.setVisibility(View.GONE);
                mNbvAnswerBoard.setVisibility(View.VISIBLE);

                break;
            case 0://判错
                llHomeWorkCheckOption.setVisibility(View.GONE);
                llCheckAgain.setVisibility(View.VISIBLE);
                ivCheckResult.setImageResource(R.drawable.img_cuowu);
//                tvCheckResult.setText("错误");
                tvCheckResult.setText("");
                mNbvAnswerBoard.setVisibility(View.GONE);

                if ("选择".equals(questionType) || "判断".equals(questionType)) {
                    ivCheckChange.setVisibility(View.GONE);
                } else {
                    ivCheckChange.setVisibility(View.VISIBLE);
                }

                break;
            case 50://判半对
                llHomeWorkCheckOption.setVisibility(View.GONE);
                llCheckAgain.setVisibility(View.VISIBLE);
                ivCheckResult.setImageResource(R.drawable.img_bandui);
//                tvCheckResult.setText("50%");
                tvCheckResult.setText("");
                mNbvAnswerBoard.setVisibility(View.GONE);

                if ("选择".equals(questionType) || "判断".equals(questionType)) {
                    ivCheckChange.setVisibility(View.GONE);
                } else {
                    ivCheckChange.setVisibility(View.VISIBLE);
                }

                break;
            case 100://判对
                llHomeWorkCheckOption.setVisibility(View.GONE);
                llCheckAgain.setVisibility(View.VISIBLE);
                ivCheckResult.setImageResource(R.drawable.img_zhengque);
                tvCheckResult.setText("");
//                tvCheckResult.setText("正确");
                mNbvAnswerBoard.setVisibility(View.GONE);

                if ("选择".equals(questionType) || "判断".equals(questionType)) {
                    ivCheckChange.setVisibility(View.GONE);
                } else {
                    ivCheckChange.setVisibility(View.VISIBLE);
                }

                break;
        }


        //初始化保存的笔记，图片地址数据。方便之后的覆盖填充
        List<Content_new> content_news = questionReplyDetail.getParsedReplyContentList();
        int needSaveSize = content_news.size();

        for (int i = 0; i < needSaveSize; i++) {

            Content_new content_new = content_news.get(i);

            if (content_new.getType() == IMG_URL) {

                String picPath = content_new.getValue();
                String picName = picPath.substring(picPath.lastIndexOf("/") + 1);
                pathList.add(picName);
            }

            bytesList.add(null);
        }
    }

    public void refreshPageChangeBtns(String tag) {
        int currentSelectPageIndex = contentDisplayer.getmContentAdaper().getCurrentSelectPageIndex();
        if (currentSelectPageIndex == 0 || currentSelectPageIndex == -1) {
            lastPageBtn.setVisibility(View.INVISIBLE);
        } else {
            lastPageBtn.setVisibility(View.VISIBLE);
        }

        if ((currentSelectPageIndex + 1) >= contentDisplayer.getmContentAdaper().getPageCount(tag)) {
            nextPageBtn.setVisibility(View.INVISIBLE);
        } else {
            nextPageBtn.setVisibility(View.VISIBLE);
        }
    }


    private void setPageNumberView() {
        pageNumAdapter = new PageNumAdapter();
        linearLayoutManager = new CustomLinearLayoutManager(this);
        linearLayoutManager.setScrollHorizontalEnabled(false);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mAllQuestionPageView.setLayoutManager(linearLayoutManager);
        mAllQuestionPageView.setAdapter(pageNumAdapter);

        pageNumAdapter.setOnItemClickListener(new OnItemClickListenerForAnswerList() {
            @Override
            public void onItemClick1(int position) {

                if (mNbvAnswerBoard != null) {
                    mNbvAnswerBoard.leaveScribbleMode(true);
                }

                changeHomeWorkCorner(position);
                refreshLastAndNextQuestionBtns();
            }

            @Override
            public void onItemClick2(int position) {

            }

            @Override
            public void onItemClick3(int position) {

            }
        });

    }

    @OnClick(R.id.btn_left)
    public void back() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (mNbvAnswerBoard != null) {
            mNbvAnswerBoard.leaveScribbleMode(true);
        }
        super.onBackPressed();
    }

    @OnClick({R.id.tv_last_homework, R.id.tv_next_homework, R.id.tv_homework_error, R.id.tv_homework_half_right, R.id.tv_homework_right,
            R.id.question_body_btn, R.id.analysis_btn, R.id.last_page_btn, R.id.next_page_btn,
            R.id.img_btn_right, R.id.iv_check_change})
    public void onClick(View view) {

        if (mNbvAnswerBoard != null) {
            mNbvAnswerBoard.leaveScribbleMode(true);
        }
        mNbvAnswerBoard.invalidate();
        switch (view.getId()) {
            case R.id.tv_last_homework:

                if (llHomeWorkCheckOption.getVisibility() == View.VISIBLE) {

                    if (score == -1) {
                        ToastUtil.showCustomToast(this, "请先对该题判对错");
                        return;
                    }
                    //保存没触发前的界面数据,并提交批改数据到服务器
                    saveCheckData();

                    //设置分数集合中的批改分数，返回上一题时，能够查看到之前是批改后的数据
                    replyScoreList.set(currentShowQuestionIndex, score);

                    commitType = 1;
                    getUpLoadInfo();
                } else {

                    //不提交批改数据，直接跳转到上一题
                    if (currentShowQuestionIndex > 0) {
                        currentShowQuestionIndex--;
                        pageNumAdapter.onItemClickListener.onItemClick1(currentShowQuestionIndex);

                    } else {
                        ToastUtil.showCustomToast(getBaseContext(), "已经是第一题了");
                    }
                }

                break;
            case R.id.tv_next_homework:
                if (llHomeWorkCheckOption.getVisibility() == View.VISIBLE) {

                    if (score == -1) {
                        ToastUtil.showCustomToast(this, "请先对该题判对错");
                        return;
                    }

                    //保存没触发前的界面数据
                    saveCheckData();

                    //设置分数集合中的批改分数，返回上一题时，能够查看到之前是批改后的数据
                    replyScoreList.set(currentShowQuestionIndex, score);
                    commitType = 2;
                    getUpLoadInfo();
                } else {
                    if (currentShowQuestionIndex < pageSize - 1) {

                        currentShowQuestionIndex++;
                        pageNumAdapter.onItemClickListener.onItemClick1(currentShowQuestionIndex);

                    } else {
                        new ConfirmDialog(this, "批改完成，请选择其他学生继续批改！", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

//                                back();

                                NetWorkManager.closeHomework(examId, null, studentId + "")
                                        .subscribe(new Action1<Object>() {
                                            @Override
                                            public void call(Object o) {
                                                ToastUtil.showCustomToast(getBaseContext(), "该学生批改完毕");
                                                back();

                                            }
                                        }, new Action1<Throwable>() {
                                            @Override
                                            public void call(Throwable throwable) {
                                                throwable.printStackTrace();
                                                ToastUtil.showCustomToast(getBaseContext(), "该学生批改完毕提交错误了");
                                            }
                                        });

                            }
                        }, "确定").show();

                    }
                }
                break;
            case R.id.tv_homework_error:
                score = 0;
                ToastUtil.showCustomToast(this, "该题判为错误");

                break;
            case R.id.tv_homework_half_right:
                score = 50;
                ToastUtil.showCustomToast(this, "该题判为部分正确");
                break;
            case R.id.tv_homework_right:
                score = 100;
                ToastUtil.showCustomToast(this, "该题判为正确");
                break;


            case R.id.question_body_btn:
                //学生答案
                if (!questionBodyBtn.isSelected()) {
                    questionBodyBtn.setSelected(true);
                    if (llHomeWorkCheckOption.getVisibility() == View.VISIBLE) {
                        mNbvAnswerBoard.setVisibility(View.VISIBLE);
                    } else {
                        mNbvAnswerBoard.setVisibility(View.GONE);
                    }

                    analysisBtn.setSelected(false);
                    if (contentDisplayer.getmContentAdaper().getPageCount("reply") == 0) {
                        contentDisplayer.getmContentAdaper().toPage("question", currentShowReplyPageIndex, false, false);
                        refreshPageChangeBtns("question");
                    } else {
                        contentDisplayer.getmContentAdaper().toPage("reply", currentShowReplyPageIndex, false, false);
                        refreshPageChangeBtns("reply");
                    }
                    getShowCheckDate();
                }
                break;
            case R.id.analysis_btn:
                //保存没触发前的界面数据
                saveCheckData();
                //解析
                if (questionBodyBtn.isSelected()) {
                    questionBodyBtn.setSelected(false);
                    mNbvAnswerBoard.setVisibility(View.GONE);
                    analysisBtn.setSelected(true);
                    contentDisplayer.getmContentAdaper().toPage("analysis", currentShowAnalysisPageIndex, true);
                    refreshPageChangeBtns("analysis");
                }
                break;
            case R.id.last_page_btn:
                //上一页
                if (questionBodyBtn.isSelected()) {

                    //保存没触发前的界面数据
                    saveCheckData();

                    currentShowReplyPageIndex--;
                    if (contentDisplayer.getmContentAdaper().getPageCount("reply") == 0) {
                        contentDisplayer.getmContentAdaper().toPage("question", currentShowReplyPageIndex, false, false);
                        refreshPageChangeBtns("question");
                    } else {
                        contentDisplayer.getmContentAdaper().toPage("reply", currentShowReplyPageIndex, false, false);
                        refreshPageChangeBtns("reply");
                    }
                    getShowCheckDate();
                } else if (analysisBtn.isSelected()) {
                    currentShowAnalysisPageIndex--;
                    contentDisplayer.getmContentAdaper().toPage("analysis", currentShowAnalysisPageIndex, true);
                    refreshPageChangeBtns("analysis");
                }
                break;
            case R.id.next_page_btn:
                //下一页
                if (questionBodyBtn.isSelected()) {
                    //保存没触发前的界面数据
                    saveCheckData();

                    currentShowReplyPageIndex++;
                    if (contentDisplayer.getmContentAdaper().getPageCount("reply") == 0) {
                        contentDisplayer.getmContentAdaper().toPage("question", currentShowReplyPageIndex, false, false);
                        refreshPageChangeBtns("question");
                    } else {
                        contentDisplayer.getmContentAdaper().toPage("reply", currentShowReplyPageIndex, false, false);
                        refreshPageChangeBtns("reply");
                    }
                    getShowCheckDate();

                } else if (analysisBtn.isSelected()) {
                    currentShowAnalysisPageIndex++;
                    contentDisplayer.getmContentAdaper().toPage("analysis", currentShowAnalysisPageIndex, true);
                    refreshPageChangeBtns("analysis");
                }
                break;
            case R.id.img_btn_right:
                //点击显示或隐藏统计数据

                if (PopupMenuManager.isShow()) {
                    PopupMenuManager.dismiss();
                } else {

                    int rightCount = 0;
                    int wrongCount = 0;
                    for (Integer i : replyScoreList) {
                        if (i == 100) {
                            rightCount++;
                        } else if (i == 0 || i == 50) {
                            wrongCount++;
                        }
                    }


                    long allUseTime = 0;
                    for (QuestionReplyDetail detail : mQuestionReplyDetails) {
                        long detailUseTime = DateUtils.transformToTime(detail.getReplyUseTime());
                        allUseTime += detailUseTime;
                    }
                    PopupMenuManager.initPupopWindow(this, btnRight, "正确：" + rightCount, "错误：" + wrongCount, "用时：" + DateUtils.converLongTimeToString(allUseTime * 1000), "正确率：" + rightCount * 100 / pageSize + "%");
                }
                break;


            case R.id.iv_check_change:

                //点击从新对该题进行批改
                llHomeWorkCheckOption.setVisibility(View.VISIBLE);
                llCheckAgain.setVisibility(View.GONE);
                mNbvAnswerBoard.setVisibility(View.VISIBLE);
                //TODO: yuanye : start   解决 重新批改后无法手写
                questionReplyDetail = mQuestionReplyDetails.get(currentShowQuestionIndex);
                questionReplyDetail.setReplyScore(-1);
                replyScoreList.clear();
                for (int i = 0; i < pageSize; i++) {
                    replyScoreList.add(mQuestionReplyDetails.get(i).getReplyScore());
                }

                //TODO: yuanye : end
                break;

        }
    }

    public void refreshPageBtns() {
        if (questionBodyBtn.isSelected()) {
            if (currentShowReplyPageIndex == 0) {
                lastPageBtn.setClickable(false);
            } else {
                lastPageBtn.setClickable(true);
            }
            if ((currentShowReplyPageIndex + 1) >= contentDisplayer.getmContentAdaper().getPageCount("reply")) {
                nextPageBtn.setClickable(false);
            } else {
                nextPageBtn.setClickable(true);
            }
        } else if (analysisBtn.isSelected()) {
            if (currentShowAnalysisPageIndex == 0) {
                lastPageBtn.setClickable(false);
            } else {
                lastPageBtn.setClickable(true);
            }
            if ((currentShowAnalysisPageIndex + 1) >= contentDisplayer.getmContentAdaper().getPageCount("analysis")) {
                nextPageBtn.setClickable(false);
            } else {
                nextPageBtn.setClickable(true);
            }
        }
    }


    private void saveCheckData() {
        if (bytesList.size() == 0) {
            return;
        }
        if (pathList.size() == 0) {
            return;
        }
        //保存笔记
        bytesList.set(currentShowReplyPageIndex, mNbvAnswerBoard.bitmap2Bytes());
        //保存图片
//        pathList.set(currentShowReplyPageIndex, saveBitmapToFile(saveScreenBitmap(), examId + "_" + questionReplyDetail.getReplyId() + "_" + currentShowReplyPageIndex));
        String fileName = pathList.get(currentShowReplyPageIndex);
        if (fileName.contains("/")) {
            fileName = fileName.substring(fileName.lastIndexOf("/"));
        }
        String filePath = saveBitmapToFile(saveScreenBitmap(), fileName);
        pathList.set(currentShowReplyPageIndex, filePath);
        //清除当前页面笔记
        mNbvAnswerBoard.clearAll();

    }

    //展示之前保存的笔记数据
    private void getShowCheckDate() {
        if (bytesList.size() > currentShowReplyPageIndex && currentShowReplyPageIndex >= 0) {
            byte[] tmpBytes = bytesList.get(currentShowReplyPageIndex);
            if (tmpBytes != null) {
                mNbvAnswerBoard.drawBitmap(BitmapFactory.decodeByteArray(tmpBytes, 0, tmpBytes.length));
            }
        }
    }


    /**
     * 截取手写板上的笔记包括底部题目
     *
     * @return 截取的图片bitmap
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

        String fileDir = FileUtils.getAppFilesDir() + "/teacher_check_homework_result";
        FileUtils.createDirs(fileDir);


        File f = new File(fileDir, bitName);
        FileOutputStream fOut = null;
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            fOut = new FileOutputStream(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, fOut);
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
        return f.getAbsolutePath();
    }


    class PageNumViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_page_id)
        TextView mTvPageId;


        public PageNumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


            int margin = UIUtils.dip2px(10);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) ((UIUtils.getScreenWidth() - PAGE_SHOW_SIZE * 2 * margin) / PAGE_SHOW_SIZE), ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(margin, margin * 2, margin, 0);
            itemView.setLayoutParams(params);

        }


    }

    class PageNumAdapter extends RecyclerView.Adapter<PageNumViewHolder> {


        OnItemClickListenerForAnswerList onItemClickListener;

        public void setOnItemClickListener(OnItemClickListenerForAnswerList onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }


        @Override
        public PageNumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page_check_homework, parent, false);
            return new PageNumViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PageNumViewHolder holder, final int position) {


            holder.mTvPageId.setText((position + 1) + "");


            //判断题目回答状态
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

            }


            holder.mTvPageId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.mTvPageId.setTag(3);
                    onItemClickListener.onItemClick1(position);
                    pageNumAdapter.notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return pageSize;
        }
    }


    /**
     * 获取oss上传所需信息
     */
    private void getUpLoadInfo() {

        showNetDialog();

        NetWorkManager.postCommentRequest(questionReplyDetail.getReplyId() + "")
                .subscribe(new Action1<STSbean>() {
                    @Override
                    public void call(STSbean stSbean) {
                        LogUtils.e("FH", "call ");
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
        OSS oss = new OSSClient(getBaseContext(), endpoint, credentialProvider, conf);


        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {

                for (int j = 0; j < pathList.size(); j++) {

                    String picPath = pathList.get(j);

                    if (picPath.contains("/")) {


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
                        } catch (ServiceException e) {
                            // 服务异常
                            LogUtils.e("RequestId", e.getRequestId());
                            LogUtils.e("ErrorCode", e.getErrorCode());
                            LogUtils.e("HostId", e.getHostId());
                            LogUtils.e("RawMessage", e.getRawMessage());
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
                        stsResultbean.setRemote(null);
                        stsResultbean.setSize(0);
                        stsResultbeanArrayList.add(stsResultbean);
                    }

                }
                pathList.clear();


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
                            loadingProgressDialog = new LoadingProgressDialog(CheckHomeWorkActivity.this);
                            loadingProgressDialog.show();
                            loadingProgressDialog.setTitle("批改上传中...");
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

//        String content = new Gson().toJson(stsResultbeanArrayList);
        //教师批改直接使用oss覆盖上传，不需要上传content了
        String content = "";

        NetWorkManager.postComment(questionReplyDetail.getReplyId() + "", score + "", content, SpUtils.getUserId() + "")
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {

                        //  清理相关数据，并跳转到下一题进行批改
                        score = -1;
                        stsResultbeanArrayList.clear();
                        bytesList.clear();
                        pathList.clear();

                        if (commitType == 1) {

                            if (currentShowQuestionIndex > 0) {


                                currentShowQuestionIndex--;
                                pageNumAdapter.onItemClickListener.onItemClick1(currentShowQuestionIndex);

                            } else {
                                ToastUtil.showCustomToast(getBaseContext(), "已经是第一题了");
                            }

                        } else if (commitType == 2) {
                            if (currentShowQuestionIndex < pageSize - 1) {

                                currentShowQuestionIndex++;
                                pageNumAdapter.onItemClickListener.onItemClick1(currentShowQuestionIndex);

                            } else {

                                new ConfirmDialog(CheckHomeWorkActivity.this, "批改完成，请选择其他学生继续批改！", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

//                                        back();
                                        NetWorkManager.closeHomework(examId, null, studentId + "")
                                                .subscribe(new Action1<Object>() {
                                                    @Override
                                                    public void call(Object o) {
                                                        ToastUtil.showCustomToast(getBaseContext(), "该学生批改完毕");
                                                        back();

                                                    }
                                                }, new Action1<Throwable>() {
                                                    @Override
                                                    public void call(Throwable throwable) {
                                                        throwable.printStackTrace();
                                                        ToastUtil.showCustomToast(getBaseContext(), "该学生批改完毕提交错误了");
                                                    }
                                                });

                                    }
                                }, "确定").show();

                            }
                        }

                        commitType = 0;


                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        ToastUtil.showCustomToast(getBaseContext(), "提交错误");
                    }
                });

    }

    //提交完毕后刷新底部题目角标
    private void changeHomeWorkCorner(int position) {
        currentShowQuestionIndex = position;

        score = -1;
        stsResultbeanArrayList.clear();
        bytesList.clear();
        pathList.clear();
        mNbvAnswerBoard.clearAll();
        questionReplyDetail = mQuestionReplyDetails.get(currentShowQuestionIndex);
        refreshQuestion();


        //以下逻辑为调整列表中角标位置。
        if (pageSize <= PAGE_SHOW_SIZE) {
            return;
        }

        if (position <= 5) {
            pageDeviationNum = 0;
        } else {
            pageDeviationNum = (position + 1) - (PAGE_SHOW_SIZE / 2) - 1;
        }
        RecycleUtils.moveToPosition(linearLayoutManager, pageDeviationNum);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bytesList != null) {
            bytesList.clear();
        }
        if (pathList != null) {
            pathList.clear();
        }
        if (stsResultbeanArrayList != null) {
            stsResultbeanArrayList.clear();
        }

        if (mQuestionReplyDetails != null) {
            mQuestionReplyDetails.clear();
        }

        if (replyScoreList != null) {
            replyScoreList.clear();
        }


        mNbvAnswerBoard.recycle();
        mNbvAnswerBoard = null;
        bytesList = null;
        pathList = null;
        stsResultbeanArrayList = null;
        mQuestionReplyDetails = null;
        replyScoreList = null;
        Glide.get(this).clearMemory();
        contentDisplayer.clearPdfCache();
        Runtime.getRuntime().gc();
    }

    /**
     * add by FH
     * 刷新上一题下一题按钮的UI,如果已经是第一题或者最后一题了,就置灰按钮
     */
    public void refreshLastAndNextQuestionBtns() {

        titleTextview.setText(studentName + "(" + (currentShowQuestionIndex + 1) + "/" + pageSize + ")");

        if (currentShowQuestionIndex > 0) {
            lastHomeworkBtn.setBackgroundResource(R.drawable.bmp_bg_blue);
            lastHomeworkText.setVisibility(View.VISIBLE);
            lastHomeworkIcon.setVisibility(View.VISIBLE);
        } else {
            lastHomeworkBtn.setBackgroundColor(getResources().getColor(R.color.gray_999999));
            lastHomeworkText.setVisibility(View.GONE);
            lastHomeworkIcon.setVisibility(View.GONE);
        }
        if (currentShowQuestionIndex < pageSize - 1) {
//            nextHomeworkBtn.setBackgroundResource(R.drawable.bmp_bg_blue);
//            nextHomeworkText.setVisibility(View.VISIBLE);
            nextHomeworkIcon.setVisibility(View.VISIBLE);
            nextHomeworkText.setText("下一题");
        } else {
//            nextHomeworkBtn.setBackgroundColor(getResources().getColor(R.color.gray_999999));
//            nextHomeworkText.setVisibility(View.GONE);
            nextHomeworkIcon.setVisibility(View.GONE);
            nextHomeworkText.setText("完成批改");
        }
    }


}
