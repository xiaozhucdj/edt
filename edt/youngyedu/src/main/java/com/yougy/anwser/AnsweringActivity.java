package com.yougy.anwser;

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
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.global.Commons;
import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.new_network.ApiException;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.RefreshUtil;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.SystemUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.OnItemClickListener;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.message.ListUtil;
import com.yougy.message.YXClient;
import com.yougy.message.attachment.EndQuestionAttachment;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityAnsweringBinding;
import com.yougy.ui.activity.databinding.ItemAnswerChooseGridviewBinding;
import com.yougy.view.CustomGridLayoutManager;
import com.yougy.view.CustomLinearLayoutManager;
import com.yougy.view.NoteBookView2;
import com.yougy.view.dialog.ConfirmDialog;
import com.yougy.view.dialog.LoadingProgressDialog;
import com.zhy.autolayout.utils.AutoUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.yougy.common.eventbus.EventBusConstant.EVENT_ANSWERING_RESULT;
import static com.yougy.common.eventbus.EventBusConstant.EVENT_LOCKER_ACTIVITY_PUSE;

/**
 * Created by FH on 2017/3/22.
 * <p>
 * 问答中界面
 */

public class AnsweringActivity extends AnswerBaseActivity {


    public RandomAccessFile mFile;
    public static ArrayList<Integer> handledExamIdList = new ArrayList<Integer>();
    ActivityAnsweringBinding binding;
    private NoteBookView2 mNbvAnswerBoard;
    //作业草稿纸
    private NoteBookView2 mCaogaoNoteBoard;

    long startTimeMill = -1;
    private TimedTask timedTask;

    //图片地址的集合（用来保存截图生长的图片路径）
    private ArrayList<String> pathList = new ArrayList<>();
    //byte数组集合，（用来保存每一页书写的笔记数据）
    private ArrayList<byte[]> bytesList = new ArrayList<>();

    //图片上传成功后，统计上传数据到集合中，方便将该信息提交到服务器
    private ArrayList<STSResultbean> stsResultbeanArrayList = new ArrayList<>();

    String itemId;
    String fromUserId;
    public int examId;


    private ParsedQuestionItem questionItem;

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
    //byte数组集合，（用来保存每一页草稿的笔记数据）
    private ArrayList<byte[]> cgBytes = new ArrayList<>();
    //是否第一次自动点击进入第一页
    private boolean isFirstComeInQuestion;

    //是否添加了手写板
    private boolean isAddAnswerBoard;

    //保存当前题目页面分页，默认从0开始
    private int saveQuestionPage = 0;

    //是否用户手动提交了问答（用来做手动提交时，老师强制收取到时的学生结果为空）
    private boolean isUpByUser;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_answering, null, false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    @Override
    public void init() {
        LogUtils.e("FH", "AnsweringActivity init " + this.toString());
        itemId = getIntent().getStringExtra("itemId");
//        itemId = "2499";//填空
//        itemId = "189";//选择
        if (TextUtils.isEmpty(itemId)) {
            ToastUtil.showCustomToast(this, "item 为空,开始问答失败");
            LogUtils.e("FH", "item 为空,开始问答失败");
            myFinish();
            return;
        }
        fromUserId = getIntent().getStringExtra("from");
//        fromUserId = "10001037";//填空
//        fromUserId = "10000239";//选择
        if (TextUtils.isEmpty(fromUserId)) {
            ToastUtil.showCustomToast(this, "from userId 为空,开始问答失败");
            LogUtils.e("FH", "from userId 为空,开始问答失败");
            myFinish();
            return;
        }
        examId = getIntent().getIntExtra("examId", -1);
//        examId = 1235;//填空
//        examId = 772;//选择
        if (examId == -1) {
            ToastUtil.showCustomToast(this, "examId 为空,开始问答失败");
            LogUtils.e("FH", "examId 为空,开始问答失败");
            myFinish();
            return;
        }
        startTimeMill = getIntent().getLongExtra("startTimeMill", -1);
        if (startTimeMill == -1) {
            startTimeMill = System.currentTimeMillis();
        }
    }

    @Override
    protected void handleEvent() {
        super.handleEvent();
        tapEventEmitter.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        if (o instanceof IMMessage) {
                            if (((IMMessage) o).getAttachment() instanceof EndQuestionAttachment) {


                                if (((EndQuestionAttachment) ((IMMessage) o).getAttachment()).examID == examId) {

                                    if (mNbvAnswerBoard != null) {
                                        mNbvAnswerBoard.leaveScribbleMode(true);
                                        mNbvAnswerBoard.setIntercept(true);
                                    }

                                    if (mCaogaoNoteBoard != null) {
                                        mCaogaoNoteBoard.leaveScribbleMode(true);
                                        mCaogaoNoteBoard.setIntercept(true);
                                    }


                                    /*new HintDialog(AnsweringActivity.this, "老师已经结束本次问答", "确定", new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            if (timedTask != null) {
                                                timedTask.stop();
                                            }
                                            dialog.dismiss();
                                            myFinish();
                                        }
                                    }).show();*/

                                    //如果学生已经手动点击了提交，这时如果收到老师的强制收取消息，则不再执行提交逻辑。
                                    if (isUpByUser) {
                                        return;
                                    }
                                    //  这里因为要做问答自评互评功能，这里需要当老师结束问答时，强制提交学生问答结果。 （这里有个问题，选择判断题，学生提交时会判断，但是当前自动提交时不能判断）
                                    saveHomeWorkData();
                                    getUpLoadInfo();

                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void loadData() {
        showNoNetDialog();
        bytesList.clear();
        pathList.clear();
        cgBytes.clear();
        NetWorkManager.queryQuestionItemList(null, null, itemId, null)
                .subscribe(new Action1<List<ParsedQuestionItem>>() {
                    @Override
                    public void call(List<ParsedQuestionItem> parsedQuestionItems) {
                        if (parsedQuestionItems != null && parsedQuestionItems.size() > 0) {
                            questionItem = parsedQuestionItems.get(0);
                            fillData();
                        } else {
                            ToastUtil.showCustomToast(getApplicationContext(), "获取到的题目为空,开始问答失败");
                            LogUtils.e("FH", "获取到的题目为空,开始问答失败");
                            myFinish();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
        startClock();
    }

    @Override
    protected void initLayout() {
        binding.startTimeTv.setText("开始时间 : " + DateUtils.convertTimeMillisToStr(startTimeMill, "yyyy-MM-dd HH:mm"));

        //新建写字板，并添加到界面上


        /*binding.rlAnswer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.rlAnswer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mNbvAnswerBoard = new NoteBookView2(AnsweringActivity.this, 960, 920);

            }
        });*/

        mNbvAnswerBoard = new NoteBookView2(AnsweringActivity.this);

        mCaogaoNoteBoard = new NoteBookView2(this, 960, 420);

        binding.contentDisplayer.setContentAdapter(new ContentDisplayer.ContentAdapter() {
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
        });

        binding.contentDisplayer.setOnLoadingStatusChangedListener(new ContentDisplayer.OnLoadingStatusChangedListener() {
            @Override
            public void onLoadingStatusChanged(ContentDisplayer.LOADING_STATUS loadingStatus) {

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
    public void onBackPressed() {

    }

    public String saveBitmapToFile1(Bitmap bitmap) {

        String fileDir = FileUtils.getAppFilesDir() + "/answer_result";
        FileUtils.createDirs(fileDir);


        File f = new File(fileDir, "test111.png");
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
        return f.getAbsolutePath();
    }

    private static long lastClickTime;
    public void onClick(View view) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
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
                myFinish();
                break;
            case R.id.commit_answer_btn:
                //防止快速多次点击
                binding.commitAnswerBtn.setClickable(false);

                if ("选择".equals(questionList.get(0).getExtraData())) {
                    if (checkedAnswerList.size() == 0) {
                        ToastUtil.showCustomToast(this, "请先选择结果后再提交");
                        binding.commitAnswerBtn.setClickable(true);
                        return;
                    }
                }
                if ("判断".equals(questionList.get(0).getExtraData())) {
                    if (checkedAnswerList.size() == 0) {
                        ToastUtil.showCustomToast(this, "请先判断后再提交");
                        binding.commitAnswerBtn.setClickable(true);
                        return;
                    }
                }

                isUpByUser = true;
                saveHomeWorkData();
                getUpLoadInfo();

                break;
            case R.id.tv_clear_write:

                mNbvAnswerBoard.clearAll();
//                saveBitmapToFile1(mNbvAnswerBoard.getBitmap());

                break;
            case R.id.tv_add_page:
                binding.tvAddPage.setEnabled(false);
                if (questionPageSize - binding.contentDisplayer.getContentAdapter().getPageCount("question") > 5) {
                    ToastUtil.showCustomToast(this, "最多只能加5张纸");
                    binding.tvAddPage.setEnabled(true);
                    return;
                }

                questionPageSize++;
                bytesList.add(null);
                pathList.add(null);
                cgBytes.add(null);
                questionPageNumAdapter.notifyDataSetChanged();
                questionPageNumAdapter.onItemClickListener.onItemClick1(questionPageSize - 1);
                binding.tvAddPage.setEnabled(true);
                break;
            case R.id.tv_caogao_text:

                if (binding.tvCaogaoText.getText().toString().startsWith("扔掉")) {
                    binding.tvCaogaoText.setText("草稿纸");

                    cgBytes.set(saveQuestionPage, null);
                    mCaogaoNoteBoard.clearAll();
                    binding.llCaogaoControl.setVisibility(View.GONE);
                    mNbvAnswerBoard.setIntercept(false);

                    if (binding.rlCaogaoBox.getChildCount() > 0) {
                        binding.rlCaogaoBox.removeView(mCaogaoNoteBoard);
                    }

                } else {
                    binding.tvCaogaoText.setText("扔掉\n草稿纸");
                    binding.llCaogaoControl.setVisibility(View.VISIBLE);
                    mNbvAnswerBoard.setIntercept(true);

                    if (binding.rlCaogaoBox.getChildCount() == 0) {
                        binding.rlCaogaoBox.addView(mCaogaoNoteBoard);
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
                if (binding.llCaogaoControl.getVisibility() == View.VISIBLE) {
                    binding.tvCaogaoText.setText("草稿纸");
                    cgBytes.set(saveQuestionPage, mCaogaoNoteBoard.bitmap2Bytes());
                    binding.llCaogaoControl.setVisibility(View.GONE);
                    mNbvAnswerBoard.setIntercept(false);
                }

                break;

            case R.id.rb_right:
                if (checkedAnswerList.size() == 0) {
                    checkedAnswerList.add("true");
                } else {
                    checkedAnswerList.set(0, "true");
                }
                break;

            case R.id.rb_error:
                if (checkedAnswerList.size() == 0) {
                    checkedAnswerList.add("false");
                } else {
                    checkedAnswerList.set(0, "false");
                }
                break;
            case R.id.image_refresh:
                loadData();
                break;
        }
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
        pathList.set(saveQuestionPage, saveBitmapToFile(mNbvAnswerBoard.getBitmap()));
        mNbvAnswerBoard.clearAll();
    }

    //填充数据
    private void fillData() {

        if (questionItem == null) {
            ToastUtil.showCustomToast(getBaseContext(), "该题可能已经被删除");
            return;
        }
        questionList = questionItem.questionContentList;
        binding.questionTypeTextview.setText("题目类型 : " + questionItem.questionContentList.get(0).getExtraData());

        binding.contentDisplayer.getContentAdapter().updateDataList("question", (ArrayList<Content_new>) questionList);
        if (questionList != null && questionList.size() > 0) {

            questionPageSize = questionList.size();

            //作业中某一题题目、答案切换
            questionPageNumAdapter = new QuestionPageNumAdapter();
            CustomLinearLayoutManager linearLayoutManager = new CustomLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            linearLayoutManager.setScrollHorizontalEnabled(false);
            binding.rcvAllQuestionPage.setLayoutManager(linearLayoutManager);
            binding.rcvAllQuestionPage.setAdapter(questionPageNumAdapter);

            questionPageNumAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick1(int position) {
//                    ToastUtil.showCustomToast(AnsweringActivity.this, position + 1 + "页");
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
                        if (binding.llCaogaoControl.getVisibility() == View.VISIBLE) {
                            cgBytes.set(saveQuestionPage, mCaogaoNoteBoard.bitmap2Bytes());

                            binding.tvCaogaoText.setText("草稿纸");
                            mCaogaoNoteBoard.clearAll();
                            binding.llCaogaoControl.setVisibility(View.GONE);
                            mNbvAnswerBoard.setIntercept(false);
                        }

                        //如果 mNbvAnswerBoard是显示的说明是非选择题，需要保持笔记
                        if (mNbvAnswerBoard.getVisibility() == View.VISIBLE) {
                            //保存上一个题目多页数据中的某一页手写笔记。
                            bytesList.set(saveQuestionPage, mNbvAnswerBoard.bitmap2Bytes());
                        }

                        //是否是选择题。都需要截屏保存图片
                        pathList.set(saveQuestionPage, saveBitmapToFile(mNbvAnswerBoard.getBitmap()));
                    }

                    mNbvAnswerBoard.clearAll();

                    //将本页设置为选中页
                    saveQuestionPage = position;


                    if (position < binding.contentDisplayer.getContentAdapter().getPageCount("question")) {
                        //切换当前题目的分页
                        binding.contentDisplayer.getContentAdapter().toPage("question", position, false);
                        binding.contentDisplayer.setVisibility(View.VISIBLE);
                    } else {
                        //加白纸
                        binding.contentDisplayer.setVisibility(View.GONE);

                    }
                    if (questionList.get(0) != null) {
                        if ("选择".equals(questionList.get(0).getExtraData())) {
                            if (isAddAnswerBoard) {
                                binding.rlAnswer.removeView(mNbvAnswerBoard);
                                isAddAnswerBoard = false;
                            }
                            binding.rcvChooeseItem.setVisibility(View.VISIBLE);
                            binding.llChooeseItem.setVisibility(View.GONE);
                            //选择题不能加页
                            binding.tvAddPage.setVisibility(View.GONE);
                            binding.tvClearWrite.setVisibility(View.GONE);
                            chooeseAnswerList = questionItem.answerList;
                            setChooeseResult();

                            //刷新当前选择结果的reciv
                            if (binding.rcvChooeseItem.getAdapter() != null) {
                                binding.rcvChooeseItem.getAdapter().notifyDataSetChanged();
                            }
//                            if (saveQuestionPage == 0) {
//                                binding.rcvChooeseItem.setVisibility(View.VISIBLE);
//                            } else {
//                                binding.rcvChooeseItem.setVisibility(View.GONE);
//                            }
                        } else if ("判断".equals(questionList.get(0).getExtraData())) {
                            if (isAddAnswerBoard) {
                                binding.rlAnswer.removeView(mNbvAnswerBoard);
                                isAddAnswerBoard = false;
                            }

                            binding.rcvChooeseItem.setVisibility(View.GONE);
                            binding.llChooeseItem.setVisibility(View.VISIBLE);
                            //选择题不能加页
                            binding.tvAddPage.setVisibility(View.GONE);
                            binding.tvClearWrite.setVisibility(View.GONE);

//                            if (saveQuestionPage == 0) {
//                                binding.llChooeseItem.setVisibility(View.VISIBLE);
//                            } else {
//                                binding.llChooeseItem.setVisibility(View.GONE);
//                            }

                        } else {
                            if (!isAddAnswerBoard) {
                                RelativeLayout.LayoutParams layer1LayoutParam = new RelativeLayout.LayoutParams(960, 920);
                                binding.rlAnswer.addView(mNbvAnswerBoard, layer1LayoutParam);
                                isAddAnswerBoard = true;
                            }
                            binding.rcvChooeseItem.setVisibility(View.GONE);
                            binding.llChooeseItem.setVisibility(View.GONE);
                            binding.tvAddPage.setVisibility(View.VISIBLE);
                            binding.tvClearWrite.setVisibility(View.VISIBLE);

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
        } else {
            ToastUtil.showCustomToast(getBaseContext(), "该题可能已经被删除");
        }
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


    /**
     * 设置选择题的结果界面
     */
    private void setChooeseResult() {
        //清理掉其他题中的作业结果。
//        checkedAnswerList.clear();

        binding.rcvChooeseItem.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(AnsweringActivity.this).inflate(R.layout.item_answer_choose_gridview, parent, false);
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
        binding.rcvChooeseItem.setLayoutManager(gridLayoutManager);
        binding.rcvChooeseItem.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.rcvChooeseItem) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                ((AnswerItemHolder) vh).reverseCheckbox();
            }
        });

    }

    @Override
    protected void refreshView() {
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
        binding.spendTimeTv.setText("已用时间 : " + DateUtils.converLongTimeToString(spentTimeMill));
    }

    public void back(View view) {
        if (mNbvAnswerBoard != null) {
            mNbvAnswerBoard.leaveScribbleMode(true);
        }
        if (mCaogaoNoteBoard != null && mCaogaoNoteBoard.getVisibility() == View.VISIBLE) {
            mCaogaoNoteBoard.leaveScribbleMode(true);
        }
        ToastUtil.showCustomToast(this, "请完成作答");
        // TODO: 2017/9/13 这里先保留关闭页面，做测试使用
        myFinish();
    }

    private void saveResultBitmap(String fileName) {
        binding.rlAnswer.setDrawingCacheEnabled(true);
        Bitmap tBitmap = binding.rlAnswer.getDrawingCache();
        // 拷贝图片，否则在setDrawingCacheEnabled(false)以后该图片会被释放掉
        tBitmap = Bitmap.createBitmap(tBitmap);
        binding.rlAnswer.setDrawingCacheEnabled(false);
        if (tBitmap != null) {
//            ivResult.setImageBitmap(tBitmap);
            saveBitmapToFile(tBitmap);
            UIUtils.showToastSafe("获取成功");
        } else {
            UIUtils.showToastSafe("获取失败");
        }
    }

    private Bitmap saveScreenBitmap() {
        binding.rlAnswer.setDrawingCacheEnabled(true);
        Bitmap tBitmap = binding.rlAnswer.getDrawingCache();
        // 拷贝图片，否则在setDrawingCacheEnabled(false)以后该图片会被释放掉
        tBitmap = Bitmap.createBitmap(tBitmap);
        binding.rlAnswer.setDrawingCacheEnabled(false);
        return tBitmap;
    }


    public String saveBitmapToFile(Bitmap bitmap) {

        String fileDir = FileUtils.getAppFilesDir() + "/answer_result";
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
        }
        return f.getAbsolutePath();
    }


    /**
     * 获取oss上传所需信息
     */
    private void getUpLoadInfo() {
        if (showNoNetDialog()) {
            binding.commitAnswerBtn.setClickable(true);
            return;
        }
        NetWorkManager.queryReplyRequest(SpUtils.getUserId() + "")
                .subscribe(new Action1<STSbean>() {
                    @Override
                    public void call(STSbean stSbean) {
                        if (stSbean != null) {
                            upLoadPic(stSbean);
                        } else {
                            new ConfirmDialog(AnsweringActivity.this, "获取上传信息失败!",
                                    "退出",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            myFinish();
                                            binding.commitAnswerBtn.setClickable(true);
                                        }
                                    },
                                    "重试",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            getUpLoadInfo();
                                        }
                                    }).show();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        new ConfirmDialog(AnsweringActivity.this, "获取上传信息失败!",
                                "退出",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        myFinish();
                                        binding.commitAnswerBtn.setClickable(true);
                                    }
                                },
                                "重试",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        getUpLoadInfo();
                                    }
                                }).show();
                    }
                });
    }


    /**
     * 上传图片，使用同步方法上传
     *
     * @param stSbean
     */
    public void upLoadPic(STSbean stSbean) {
        if (showNoNetDialog()) {
            binding.commitAnswerBtn.setClickable(true);
            return;
        }

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


        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {

                for (int i = 0; i < pathList.size(); i++) {

                    String picPath = pathList.get(i);

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
                        stsResultbean.setRemote("");
                        stsResultbean.setSize(0);
                        stsResultbeanArrayList.add(stsResultbean);
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
                            loadingProgressDialog = new LoadingProgressDialog(AnsweringActivity.this);
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
                        e.printStackTrace();

                        if (loadingProgressDialog != null) {
                            loadingProgressDialog.dismiss();
                            loadingProgressDialog = null;
                        }
                        new ConfirmDialog(AnsweringActivity.this, "上传答案失败",
                                "退出",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        myFinish();
                                        binding.commitAnswerBtn.setClickable(true);
                                    }
                                },
                                "重试",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        upLoadPic(stSbean);
                                    }
                                }).show();
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
        if (showNoNetDialog()) {
            binding.commitAnswerBtn.setClickable(true);
            return;
        }

        String picContent = new Gson().toJson(stsResultbeanArrayList);
        String txtContent = new Gson().toJson(checkedAnswerList);

        NetWorkManager.postReply(SpUtils.getUserId() + "", itemId, examId + "", picContent, txtContent, DateUtils.converLongTimeToString(System.currentTimeMillis() - startTimeMill), DateUtils.converLongTimeToString(System.currentTimeMillis()))
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {

                        double d = (double) ((LinkedTreeMap) ((ArrayList) o).get(0)).get("replyId");
                        YXClient.getInstance().sendReply(fromUserId, SessionTypeEnum.P2P, String.valueOf((int) d), examId + "", new RequestCallback<Void>() {
                            @Override
                            public void onSuccess(Void param) {
                                ToastUtil.showCustomToast(getApplicationContext(), "提交成功");
                            }

                            @Override
                            public void onFailed(int code) {
                                ToastUtil.showCustomToast(getApplicationContext(), "提交成功,通知教师失败 : " + code);
                            }

                            @Override
                            public void onException(Throwable exception) {
                                exception.printStackTrace();
                                ToastUtil.showCustomToast(getApplicationContext(), "提交成功,通知教师失败 : " + exception.getMessage());
                            }
                        });
                        if (timedTask != null) {
                            timedTask.stop();
                        }
                        Intent intent = new Intent(AnsweringActivity.this, AnswerRecordDetailActivity.class);
                        intent.putExtra("question", questionItem);
                        intent.putExtra("examId", examId);
                        startActivity(intent);
                        myFinish();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (throwable instanceof ApiException) {
                            if (((ApiException) throwable).getCode().equals("400")) {
                                LogUtils.e("FH" , "问答提交被拒绝,可能是之前已经提交过该问答");
                                //FIXME 这里会有问题
                                //理论上这里被拒绝可能有两种情况,一是之前已经提交成功,但是pad端没有收到成功的返回消息,这时点击重试,就会走到这里,这种情况下,pad端没有走过之前提交成功的逻辑.
                                //二是,按得太快发了两次请求,第一次成功了,并且已经走了上面提交成功的逻辑,第二次提交直接走了这里.
                                //对于第一种情况,我们应该手动转到和上面提交成功一样的逻辑.而对于第二种情况,我们应该直接提示成功,跳过上面提交成功的逻辑(因为之前已经走了一次了)
                                //而现在的情况是,我们无法区分是第一种情况还是第二种情况导致走到这里,所以统一只采用对应第二种情况的处理办法,直接提示成功.
                                ToastUtil.showCustomToast(getApplicationContext(), "提交成功");
                                if (timedTask != null) {
                                    timedTask.stop();
                                }
                                Intent intent = new Intent(AnsweringActivity.this, AnswerRecordDetailActivity.class);
                                intent.putExtra("question", questionItem);
                                intent.putExtra("examId", examId);
                                startActivity(intent);
                                myFinish();

//                                new HintDialog(getApplicationContext(), "问答提交被拒绝,可能是问答已经结束或者之前已经提交过该问答", "退出", new DialogInterface.OnDismissListener() {
//                                    @Override
//                                    public void onDismiss(DialogInterface dialog) {
//                                        dialog.dismiss();
//                                        myFinish();
//                                    }
//                                }).show();
                            }
                        } else {
                            new ConfirmDialog(AnsweringActivity.this, "提交失败,请重试!",
                                    "退出",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            myFinish();
                                            binding.commitAnswerBtn.setClickable(true);
                                        }
                                    },
                                    "重试",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            writeInfoToS();
                                        }
                                    }).show();
                        }
                        throwable.printStackTrace();
                    }
                });
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
                binding.rcvChooeseItem.getAdapter().notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNbvAnswerBoard != null) {
            mNbvAnswerBoard.recycle();
        }

        if (mCaogaoNoteBoard != null) {
            mCaogaoNoteBoard.recycle();
        }

        if (timedTask != null) {
            timedTask.stop();
            timedTask = null;
        }

        if (pathList != null) {
            pathList.clear();
        }
        pathList = null;

        if (bytesList != null) {
            bytesList.clear();
        }
        bytesList = null;

        if (stsResultbeanArrayList != null) {
            stsResultbeanArrayList.clear();
        }
        stsResultbeanArrayList = null;

        if (questionList != null) {
            questionList.clear();
        }
        questionList = null;

        if (chooeseAnswerList != null) {
            chooeseAnswerList.clear();
        }
        chooeseAnswerList = null;


        if (cgBytes != null) {
            cgBytes.clear();
        }
        cgBytes = null;

        if (checkedAnswerList != null) {
            checkedAnswerList.clear();
        }
        checkedAnswerList = null;

        Glide.get(this).clearMemory();
        binding.contentDisplayer.clearPdfCache();
        Runtime.getRuntime().gc();
    }

    @Override
    protected void onStart() {
        super.onStart();
        BaseEvent baseEvent = new BaseEvent(EventBusConstant.EVENT_ANSWERING_SHOW, "");
        EventBus.getDefault().post(baseEvent);
    }

    private boolean mEventResult = false;

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        if (event.getType().equalsIgnoreCase(EVENT_ANSWERING_RESULT) && !mEventResult) {
            LogUtils.i("type .." + event.getType());
            mEventResult = true;
            UIUtils.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mCaogaoNoteBoard != null) {
                        mCaogaoNoteBoard.leaveScribbleMode();
                        mCaogaoNoteBoard.setIntercept(false);
                    }

                    if (mNbvAnswerBoard != null) {
                        mNbvAnswerBoard.leaveScribbleMode();
                        mNbvAnswerBoard.setIntercept(false);
                    }
                    LogUtils.i("type .." + "111111111111111111111");
                    RefreshUtil.invalidate(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0));
                }
            }, 3000);
        }
        if (event.getType().equalsIgnoreCase(EVENT_LOCKER_ACTIVITY_PUSE)) {
            if (mCaogaoNoteBoard != null) {
                mCaogaoNoteBoard.setIntercept(false);
            }

            if (mNbvAnswerBoard != null) {
                mNbvAnswerBoard.setIntercept(false);
            }
        }
        if (event.getType().equalsIgnoreCase(EventBusConstant.EVENT_START_ACTIIVTY_ORDER)) {
            if (mCaogaoNoteBoard != null) {
                mCaogaoNoteBoard.setIntercept(true);
            }

            if (mNbvAnswerBoard != null) {
                mNbvAnswerBoard.setIntercept(true);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void myFinish() {
        finish();
        if (mCaogaoNoteBoard != null) {
            mCaogaoNoteBoard.leaveScribbleMode();
            mCaogaoNoteBoard.setIntercept(true);
        }

        if (mNbvAnswerBoard != null) {
            mNbvAnswerBoard.leaveScribbleMode();
            mNbvAnswerBoard.setIntercept(true);
        }
        BaseEvent baseEvent = new BaseEvent(EventBusConstant.EVENT_ANSWERING_PUASE, "");
        EventBus.getDefault().post(baseEvent);
    }
}
