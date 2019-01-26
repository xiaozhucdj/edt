package com.yougy.anwser;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frank.etude.pageable.PageBtnBarAdapter;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.global.Constant;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.home.bean.DataCountInBookNode;
import com.yougy.homework.bean.HomeworkDetail;
import com.yougy.homework.bean.QuestionReplyDetail;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityAnswerDetailListBinding;
import com.yougy.ui.activity.databinding.ItemQuestionChooseBinding;
import com.yougy.view.CustomGridLayoutManager;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 *
 */

public class AnswerRecordListDetailNewActivity extends BaseActivity {
    public static final int REQUEST_CODE = 10101;
    ActivityAnswerDetailListBinding binding;
    int bookId , homeworkId;

    int currentSelectedNodeIndex = -1;
    int currentSelectedQuestionIndex = -1;

    List<DataCountInBookNode> currentBookExamCountInfoList = new ArrayList<DataCountInBookNode>();

    private List<Content_new> textReplyList = new ArrayList<>();

    int currentQuestionSelectedPageIndex = -1;
    int currentAnalysisSelectedPageIndex = -1;

    private ParsedQuestionItem parsedQuestionItem;


    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this) , R.layout.activity_answer_detail_list , null , false);
        setContentView(binding.getRoot());
    }

    @Override
    protected void init() {
        bookId = getIntent().getIntExtra("bookId" , -1);
        homeworkId = getIntent().getIntExtra("homeworkId" , -1);
    }

    @Override
    protected void initLayout() {
        initOnClickListeners();
        initContentDisplayerAndPageBtnBar();
        initQuestionChooseRcv();
        initData();
    }

    private void initData() {
        currentSelectedNodeIndex = SpUtils.getWendaLastNodeIndex();
        currentSelectedQuestionIndex = SpUtils.getWendaLastQuestionIndex();
    }

    /**
     * 初始化上方问题选择recyclerview
     */
    private void initQuestionChooseRcv(){
        binding.questionChooseRcv.setAdapter(new RecyclerView.Adapter<MyHolder>() {
            @NonNull
            @Override
            public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ItemQuestionChooseBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(getThisActivity()), R.layout.item_question_choose, parent, false);
                return new MyHolder(itemBinding);
            }

            @Override
            public void onBindViewHolder(@NonNull MyHolder holder, int position) {
                holder.setPosition(position);

            }

            @Override
            public int getItemCount() {
                return currentBookExamCountInfoList.get(currentSelectedNodeIndex).getCount();
            }
        });
        binding.questionChooseRcv.setLayoutManager(new CustomGridLayoutManager(getThisActivity() , 8){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.questionChooseRcv.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.questionChooseRcv) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                MyHolder holder = (MyHolder) vh;
                currentSelectedQuestionIndex = holder.getMyPosition();
                showAnswer();
                binding.questionChooseRcv.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 初始化题目显示ContentDisplayer和配套的PageBtnBar
     */
    private void initContentDisplayerAndPageBtnBar(){
        //设置手写层都为不可写
        binding.mainContentDisplay.getLayer1().setIntercept(true);
        binding.mainContentDisplay.getLayer2().setIntercept(true);
        binding.mainContentDisplay.setContentAdapter(new WriteableContentDisplayerAdapter() {
            @Override
            public void afterPageCountChanged(String typeKey) {
                if ((binding.questionBodyBtn.isSelected() && typeKey.equals("question"))
                        || (binding.answerAnalysisBtn.isSelected() && typeKey.equals("analysis"))) {
                    binding.pageBtnBar.refreshPageBar();
                }
            }

            @Override
            public void beforeToPage(String fromTypeKey, int fromPageIndex, String toTypeKey, int toPageIndex) {

            }

            @Override
            public void afterToPage(String fromTypeKey, int fromPageIndex, String toTypeKey, int toPageIndex) {
                if (toTypeKey.equals("question")) {
                    if (correctBaseLayerIndex(binding.mainContentDisplay.getContentAdapter().getLayerPageCount("question", 0)
                            , binding.mainContentDisplay.getContentAdapter().getLayerPageCount("question", 1))) {
                        binding.pageBtnBar.refreshPageBar();
                    }
                }
            }
        });

        binding.mainContentDisplay.setStatusChangeListener(new WriteableContentDisplayer.StatusChangeListener() {
            @Override
            public void onStatusChanged(WriteableContentDisplayer.LOADING_STATUS newStatus, String typeKey, int pageIndex, WriteableContentDisplayer.ERROR_TYPE errorType, String errorMsg) {

                switch (newStatus) {
                    case LOADING:
                        binding.mainContentDisplay.setHintText("加载中");
                        break;
                    case ERROR:
                        binding.mainContentDisplay.setHintText(errorMsg);
                        break;
                    case SUCCESS:
                        binding.mainContentDisplay.setHintText(null);//设置为null该view会gone
                        break;
                }
            }
        });

        binding.pageBtnBar.setPageBarAdapter(new PageBtnBarAdapter(this) {
            @Override
            public int getPageBtnCount() {
                if (binding.questionBodyBtn.isSelected()) {
                    return binding.mainContentDisplay.getContentAdapter().getPageCountBaseOnBaseLayer("question");
                } else if (binding.answerAnalysisBtn.isSelected()) {
                    return binding.mainContentDisplay.getContentAdapter().getPageCountBaseOnBaseLayer("analysis");
                }
                return 0;
            }

            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn) {
                if (binding.questionBodyBtn.isSelected()) {
                    currentQuestionSelectedPageIndex = btnIndex;
                    binding.mainContentDisplay.toPage("question", currentQuestionSelectedPageIndex, true);
                    //展示客观题reply中的学生答案（ABCD true false）
                    showJudgeOrSelect();
                    //toPage调用表示错误提示由ContentDisplayer接管,其上层的两个errorHint要隐藏
                    binding.globalErrorHintLayout.setVisibility(View.GONE);
                    binding.questionErrorHintLayout.setVisibility(View.GONE);
                } else if (binding.answerAnalysisBtn.isSelected()) {
                    currentAnalysisSelectedPageIndex  = btnIndex;
                    binding.mainContentDisplay.toPage("analysis", currentAnalysisSelectedPageIndex, true);
                    //toPage调用表示错误提示由ContentDisplayer接管,其上层的两个errorHint要隐藏
                    binding.globalErrorHintLayout.setVisibility(View.GONE);
                    binding.questionErrorHintLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 初始化各个其他按钮点击监听器
     */
    private void initOnClickListeners(){
        //返回
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                binding.questionChooseRcv.setVisibility(View.GONE);
            }
        });
        //刷新
        binding.refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
                binding.questionChooseRcv.setVisibility(View.GONE);
            }
        });
        //上一个章节/问答按钮
        binding.lastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.questionChooseRcv.setVisibility(View.GONE);
                if ("上一个章节".equals(binding.lastBtnText.getText())){
                    currentSelectedNodeIndex--;
                    currentSelectedQuestionIndex = currentBookExamCountInfoList.get(currentSelectedNodeIndex).getCount() - 1;
                    showAnswer();
                }
                else if ("上一个问答".equals(binding.lastBtnText.getText())){
                    currentSelectedQuestionIndex--;
                    showAnswer();
                }
            }
        });
        //下一个章节/问答按钮
        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.questionChooseRcv.setVisibility(View.GONE);
                if ("下一个章节".equals(binding.nextBtnText.getText())){
                    currentSelectedNodeIndex++;
                    currentSelectedQuestionIndex = 0;
                    showAnswer();
                }
                else if ("下一个问答".equals(binding.nextBtnText.getText())){
                    currentSelectedQuestionIndex++;
                    showAnswer();
                }
            }
        });
        //章节名称点击按钮
        binding.questionChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.questionChooseRcv.getVisibility() == View.GONE){
                    binding.questionChooseRcv.setVisibility(View.VISIBLE);
                    binding.questionChooseRcv.getAdapter().notifyDataSetChanged();
                }
                else {
                    binding.questionChooseRcv.setVisibility(View.GONE);
                }
            }
        });
        //进入选书选章节的图标按钮
        binding.toBookStructureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.questionChooseRcv.setVisibility(View.GONE);
                Intent intent = new Intent(getThisActivity() , AnswerBookNodeChooseActivity.class);
                intent.putExtra(AnswerBookNodeChooseActivity.KEY_BOOK_ID , bookId);
                intent.putExtra(AnswerBookNodeChooseActivity.KEY_HOMEWORK_ID , homeworkId);
                startActivityForResult(intent , REQUEST_CODE);
            }
        });
        //题干按钮
        binding.questionBodyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.questionChooseRcv.setVisibility(View.GONE);
                if (!binding.questionBodyBtn.isSelected()){
                    binding.questionBodyBtn.setSelected(true);
                    binding.answerAnalysisBtn.setSelected(false);
                    showJudgeOrSelect();
                    binding.startTimeTv.setVisibility(View.VISIBLE);
                    binding.spendTimeTv.setVisibility(View.VISIBLE);
                    binding.colorBlock.setVisibility(View.VISIBLE);
                    binding.pageBtnBar.setCurrentSelectPageIndex(currentQuestionSelectedPageIndex);
                    correctBaseLayerIndex(binding.mainContentDisplay.getContentAdapter().getLayerPageCount("question", 0)
                            , binding.mainContentDisplay.getContentAdapter().getLayerPageCount("question", 1));
                    binding.pageBtnBar.refreshPageBar();
                    if (currentQuestionSelectedPageIndex != -1) {
                        binding.mainContentDisplay.toPage("question", currentQuestionSelectedPageIndex, true);
                        binding.globalErrorHintLayout.setVisibility(View.GONE);
                        binding.questionErrorHintLayout.setVisibility(View.GONE);
                    }
                }
            }
        });
        //解答按钮
        binding.answerAnalysisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.questionChooseRcv.setVisibility(View.GONE);
                if (!binding.answerAnalysisBtn.isSelected()){
                    binding.questionBodyBtn.setSelected(false);
                    binding.answerAnalysisBtn.setSelected(true);
                    binding.rcvChooeseItem.setVisibility(View.GONE);
                    binding.llChooeseItem.setVisibility(View.GONE);
                    binding.startTimeTv.setVisibility(View.INVISIBLE);
                    binding.spendTimeTv.setVisibility(View.INVISIBLE);
                    binding.colorBlock.setVisibility(View.INVISIBLE);
                    binding.mainContentDisplay.getContentAdapter().setPageCountBaseLayerIndex(0);
                    binding.pageBtnBar.setCurrentSelectPageIndex(currentAnalysisSelectedPageIndex);
                    binding.pageBtnBar.refreshPageBar();
                    if (currentAnalysisSelectedPageIndex != -1) {
                        binding.mainContentDisplay.toPage("analysis", currentAnalysisSelectedPageIndex, true);
                        //toPage调用表示错误提示由ContentDisplayer接管,其上层的两个errorHint要隐藏
                        binding.globalErrorHintLayout.setVisibility(View.GONE);
                        binding.questionErrorHintLayout.setVisibility(View.GONE);
                    }
                }
            }
        });
    }


    @Override
    public void loadData() {
        if(homeworkId == -1 || bookId == -1){
            ToastUtil.showCustomToast(getThisActivity() , "bookId或homeworkId有误");
            finish();
            return;
        }
        getExamCountInBook();
    }

    /**
     * 本方法用于[获得了课本列表后!!!],根据给定条件确定要选中的书和要选中的章节.
     * 现今最新规则是,如果没有给定要选中的书和要选中的章节,则自动选中第一本有问答题目的书的第一个有问答题目的章节.
     * 如果给定了要选中的书和章节,但是数据发现没有这个章节,则数据作废,自动使用上一条规则,确定性的选中书和选中章节.
     * 如果给定了要选中的书和章节没有问题,则使用给定的选中的书和章节
     */
    private void getExamCountInBook(){
        NetWorkManager.getItemCountBaseOnBookNode(homeworkId , Constant.IICODE_01)
                .compose(bindToLifecycle())
                .subscribe(new Action1<List<DataCountInBookNode>>() {
                    @Override
                    public void call(List<DataCountInBookNode> itemCountInBookNodeList) {
                        //事先验证本书是否有章节信息
                        if (itemCountInBookNodeList.size() == 0){
                            //对于没有章节信息的书
                            binding.globalErrorHintLayout.setVisibility(View.VISIBLE);
                            binding.globalErrorHintTv.setText("获取图书问答数量失败");
                            return;
                        }
                        //对于选中章节已知的情况,需要事先检验当前选中章节index是否合法.
                        //如果已知的选中章节index不合法(数组越界),则清空,认定为所有条件不可知,全部为初始状态,然后重新调用自身进行整套逻辑
                        if (currentSelectedNodeIndex >= itemCountInBookNodeList.size()){
                            currentSelectedNodeIndex = -1;
                            currentSelectedQuestionIndex = -1;
                            getExamCountInBook();
                            return;
                        }
                        //如果选中章节已知且合法,则将获取到的章节信息存储,并启动show题目的流程
                        if (currentSelectedNodeIndex != -1){
                            currentBookExamCountInfoList.clear();
                            currentBookExamCountInfoList.addAll(itemCountInBookNodeList);
                            showAnswer();
                        }
                        //选中章节未知
                        else {
                            currentBookExamCountInfoList.clear();
                            currentBookExamCountInfoList.addAll(itemCountInBookNodeList);
                            //检验本书中第一个有题的章节index是什么.
                            int firstNotNullNodeIndex = -1;
                            for (int i = 0; i < itemCountInBookNodeList.size(); i++) {
                                if (itemCountInBookNodeList.get(i).getCount() != 0) {
                                    firstNotNullNodeIndex = i;
                                    break;
                                }
                            }
                            //使用本书中有题的第一个章节作为选中章节.然后进入show题目流程.
                            if (firstNotNullNodeIndex != -1){
                                currentSelectedNodeIndex = firstNotNullNodeIndex;
                                showAnswer();
                            }
                            //如果本书中所有章节都没题,则验证是否还有下一本书
                            else {
                                //如果没有下一本书了,说明所有书里都没题.由于之前保存过第0本书的章节数据
                                //,这里直接设置选中第0本书第0章不选中题目,调用show题目流程来显示无题目的提示.
                                currentSelectedNodeIndex = 0;
                                currentSelectedQuestionIndex = -1;
                                showAnswer();
                                //如果还有下一本书,则继续验证下一本书
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //获取问答题目数量失败.直接报错退出.
                        throwable.printStackTrace();
                        binding.globalErrorHintLayout.setVisibility(View.VISIBLE);
                        binding.globalErrorHintTv.setText("获取问答数量失败,请检查网络");
                    }
                });
    }

    /**
     * 本方法用于在决定了course的书本和书本的node后,显示题目.
     */
    public void showAnswer(){
        SpUtils.setWendaLastNodeIndex(currentSelectedNodeIndex);
        SpUtils.setWendaLastQuestionIndex(currentSelectedQuestionIndex);

        //首先显示章节问答选择栏
        showTopBtnBar();
        //由于showTopBtnBar方法会尝试校正currentSelectedQuestionIndex 为0,如果校正失败,则表示本章没有可显示的题目,此处直接报错结束
        if (currentSelectedQuestionIndex == -1){
            binding.questionErrorHintLayout.setVisibility(View.VISIBLE);
            binding.questionErrorHintTv.setText("本章您没有题目");
            //questionErrorHintLayout show表示错误提示由它接管,它上层的globalErrorHintLayout需要隐藏
            binding.globalErrorHintLayout.setVisibility(View.GONE);
        }
        else {
            NetWorkManager.queryExam(currentBookExamCountInfoList.get(currentSelectedNodeIndex).getExams().get(currentSelectedQuestionIndex) + "" , null)
                    .subscribe(new Action1<List<HomeworkDetail>>() {
                @Override
                public void call(List<HomeworkDetail> homeworkDetails) {
                    HomeworkDetail homeworkDetail = homeworkDetails.get(0);
                    parsedQuestionItem = homeworkDetail.getExamPaper().getPaperContent().get(0).getParsedQuestionItemList().get(0);
                    binding.startTimeTv.setText("问答开始时间 : " + homeworkDetail.getExamStartTime());
                    binding.questionBodyBtn.setText("题干(" + parsedQuestionItem.questionContentList.get(0).getExtraData().toString() + ")");
                    NetWorkManager.queryReplyDetail(currentBookExamCountInfoList.get(currentSelectedNodeIndex).getExams().get(currentSelectedQuestionIndex), null, SpUtils.getUserId() + "")
                            .subscribe(new Action1<List<QuestionReplyDetail>>() {
                                @Override
                                public void call(List<QuestionReplyDetail> questionReplyDetails) {
                                    if (questionReplyDetails.size() > 0) {
                                        QuestionReplyDetail questionReplyDetail = questionReplyDetails.get(questionReplyDetails.size() - 1);
                                        //显示选择或判断的选项
                                        textReplyList.clear();
                                        List<Content_new> originContentList = questionReplyDetail.getParsedReplyContentList();
                                        List<Content_new> imageContentList = new ArrayList<Content_new>();
                                        for (Content_new contentNew : originContentList) {
                                            if (contentNew != null) {
                                                if (contentNew.getType() == Content_new.Type.TEXT) {
                                                    textReplyList.add(contentNew);
                                                } else {
                                                    imageContentList.add(contentNew);
                                                }
                                            } else {
                                                imageContentList.add(null);
                                            }
                                        }
                                        showJudgeOrSelect();

                                        //显示分数
                                        int replyScore = questionReplyDetail.getReplyScore();
                                        switch (replyScore) {
                                            case 100:
                                                binding.bottomIcon.setImageResource(R.drawable.img_zhengque);
                                                break;
                                            case 0:
                                                binding.bottomIcon.setImageResource(R.drawable.img_cuowu);
                                                break;
                                            default:
                                                binding.bottomIcon.setImageResource(R.drawable.img_bandui);
                                                break;
                                        }
                                        //填充数据
                                        binding.mainContentDisplay.getContentAdapter().updateDataList("analysis", 0, questionReplyDetail.getParsedQuestionItem().analysisContentList);
                                        binding.mainContentDisplay.getContentAdapter().updateDataList("question", 0, questionReplyDetail.getParsedQuestionItem().questionContentList);
                                        if (questionReplyDetail.getParsedReplyCommentList() != null && questionReplyDetail.getParsedReplyCommentList().size() != 0) {
                                            binding.mainContentDisplay.getContentAdapter().updateDataList("question", 2, questionReplyDetail.getParsedReplyCommentList());
                                        } else {
                                            binding.mainContentDisplay.getContentAdapter().deleteDataList("question", 2);
                                        }
                                        binding.mainContentDisplay.getContentAdapter().updateDataList("question", 1, imageContentList);

                                        //设置用时
                                        binding.spendTimeTv.setText("用时 : " + questionReplyDetail.getReplyUseTime());
                                        binding.spendTimeTv.setVisibility(View.VISIBLE);

                                        String statusCode = questionReplyDetail.getReplyStatusCode();
                                        if ("IG03".equals(statusCode)) {
                                            binding.statusHintTv.setVisibility(View.VISIBLE);
                                            binding.statusHintTv.setText("批改中");
                                            binding.bottomBtnBar.setVisibility(View.GONE);
                                            binding.statusHintTv.setBackgroundResource(R.drawable.img_wenda);
                                        } else if ("IG04".equals(statusCode)) {
                                            binding.statusHintTv.setVisibility(View.VISIBLE);
                                            binding.bottomBtnBar.setVisibility(View.VISIBLE);
                                            binding.bottomIcon.setVisibility(View.VISIBLE);
                                            binding.statusHintTv.setText("已批改");
                                            binding.statusHintTv.setBackgroundResource(R.drawable.img_bg_green);
                                            binding.bottomBtnBar.setBackgroundResource(R.drawable.bmp_bg_green);
                                        }
                                        binding.bottomText.setVisibility(View.GONE);
                                    } else {
                                        //说明是未提交的问答
                                        binding.mainContentDisplay.getContentAdapter().updateDataList("analysis", 0, parsedQuestionItem.analysisContentList);
                                        binding.mainContentDisplay.getContentAdapter().updateDataList("question", 0, parsedQuestionItem.questionContentList);

                                        binding.mainContentDisplay.getContentAdapter().deleteDataList("question", 1);
                                        binding.mainContentDisplay.getContentAdapter().deleteDataList("question", 2);

                                        binding.spendTimeTv.setText("");
                                        binding.statusHintTv.setVisibility(View.VISIBLE);
                                        binding.statusHintTv.setText("未提交");
                                        binding.bottomBtnBar.setVisibility(View.VISIBLE);
                                        binding.bottomIcon.setVisibility(View.GONE);
                                        binding.bottomText.setText("问答已结束");
                                        binding.bottomText.setVisibility(View.VISIBLE);
                                        binding.statusHintTv.setBackgroundResource(R.drawable.img_wenda);
                                        binding.bottomBtnBar.setBackgroundColor(Color.parseColor("#999999"));
                                    }
                                    currentQuestionSelectedPageIndex = -1;
                                    currentAnalysisSelectedPageIndex = -1;
                                    binding.questionBodyBtn.setSelected(false);
                                    binding.questionBodyBtn.performClick();
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    throwable.printStackTrace();
                                    binding.questionErrorHintLayout.setVisibility(View.VISIBLE);
                                    binding.questionErrorHintTv.setText("获取题目错误");
                                    //questionErrorHintLayout show表示错误提示由它接管,它上层的globalErrorHintLayout需要隐藏
                                    binding.globalErrorHintLayout.setVisibility(View.GONE);
                                }
                            });
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    throwable.printStackTrace();
                    binding.questionErrorHintLayout.setVisibility(View.VISIBLE);
                    binding.questionErrorHintTv.setText("获取题目错误");
                    //questionErrorHintLayout show表示错误提示由它接管,它上层的globalErrorHintLayout需要隐藏
                    binding.globalErrorHintLayout.setVisibility(View.GONE);
                }
            });
        }
    }


    /**
     * 校正基准层,本来应该以第1层作为基准层,但是考虑到第1层有的时候可能不如第0层页数多,此时需要校正基准层为第0层.
     * 如果校正了基准层为第0层,则返回true,否则返回false.
     */
    private boolean correctBaseLayerIndex(int layer0PageCount, int layer1PageCount) {
        if (layer0PageCount > layer1PageCount) {
            binding.mainContentDisplay.getContentAdapter().setPageCountBaseLayerIndex(0);
            return true;
        } else {
            binding.mainContentDisplay.getContentAdapter().setPageCountBaseLayerIndex(1);
            return false;
        }
    }

    private void showJudgeOrSelect() {
        String questionType = (String) parsedQuestionItem.questionContentList.get(0).getExtraData();
        if ("选择".equals(questionType)) {
            binding.rcvChooeseItem.setVisibility(View.VISIBLE);
            binding.llChooeseItem.setVisibility(View.GONE);
            setChooeseResult();
            //刷新当前选择结果的reciv
            if (binding.rcvChooeseItem.getAdapter() != null) {
                binding.rcvChooeseItem.getAdapter().notifyDataSetChanged();
            }
        } else if ("判断".equals(questionType)) {
            binding.rcvChooeseItem.setVisibility(View.GONE);
            binding.llChooeseItem.setVisibility(View.VISIBLE);
            if (textReplyList.size() > 0) {
                String replyResult = textReplyList.get(0).getValue();
                if ("true".equals(replyResult)) {
                    binding.rbRight.setChecked(true);
                    binding.rbError.setChecked(false);
                } else {
                    binding.rbRight.setChecked(false);
                    binding.rbError.setChecked(true);
                }
            }
            binding.rbRight.setClickable(false);
            binding.rbError.setClickable(false);
        } else {
            binding.rcvChooeseItem.setVisibility(View.GONE);
            binding.llChooeseItem.setVisibility(View.GONE);
        }
    }

    /**
     * 设置选择题的结果界面
     */
    private void setChooeseResult() {
        List<ParsedQuestionItem.Answer> chooeseAnswerList = parsedQuestionItem.answerList;

        binding.rcvChooeseItem.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getThisActivity()).inflate(R.layout.item_answer_choose_gridview, parent, false);
                AutoUtils.auto(view);
                AnswerItemHolder holder = new AnswerItemHolder(view, textReplyList);
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
        binding.rcvChooeseItem.setLayoutManager(gridLayoutManager);
    }


    /**
     * 计算并显示章节问答选择栏
     */
    public void showTopBtnBar(){
        DataCountInBookNode currentSelectedNode = currentBookExamCountInfoList.get(currentSelectedNodeIndex);
        //尝试对不合法的选中题目index进行校正,统一校正为第0题.
        if (currentSelectedQuestionIndex < 0 || currentSelectedQuestionIndex >= currentSelectedNode.getCount()){
            if (currentSelectedNode.getCount() != 0){
                currentSelectedQuestionIndex = 0;
            }
            //无法校正的保持-1,表示该章没有可以显示的题.
            else {
                currentSelectedQuestionIndex = -1;
            }
        }
        //计算出是否显示上一个问答,下一个问答,上一个章节,下一个章节.
        boolean showLastQuestion = true, showNextQuestion = true , showLastNode = true, showNextNode = true;
        //首先计算是否显示上一下一问答
        if (currentSelectedQuestionIndex == -1){
            //对于没有可以显示题的章节,不显示上一问答,下一问答
            showLastQuestion = false;
            showNextQuestion = false;
            //设置标题文字,并且标题文字不可点
            binding.questionChooseBtnText.setText(currentSelectedNode.getNodeName()
                    + "(0/0)");
            binding.questionChooseBtn.setClickable(false);
        }
        else {
            //对于第0题,不显示上一问答
            if (currentSelectedQuestionIndex == 0){
                showLastQuestion = false;
            }
            //对于本章最后一题,不显示下一问答
            if (currentSelectedQuestionIndex + 1 >= currentSelectedNode.getCount()){
                showNextQuestion = false;
            }
            //设置标题文字
            binding.questionChooseBtnText.setText(currentSelectedNode.getNodeName()
                    + "(" + (currentSelectedQuestionIndex + 1) + "/" + currentSelectedNode.getCount() + ")");
            binding.questionChooseBtn.setClickable(true);
        }
        //再计算是否显示上一下一章节
        if (currentSelectedNodeIndex == 0){
            showLastNode = false;
        }
        if (currentSelectedNodeIndex + 1 >= currentBookExamCountInfoList.size()){
            showNextNode = false;
        }

        //根据之前计算的结果,具体决定上一下一按钮的显示.
        //对于单个按钮,优先显示上一/下一问答,其次,显示上一/下一章节,如果都无法显示,则隐藏按钮.
        if (showLastQuestion){
            binding.lastBtnText.setText("上一个问答");
            binding.lastBtn.setVisibility(View.VISIBLE);
        } else if (showLastNode) {
            binding.lastBtnText.setText("上一个章节");
            binding.lastBtn.setVisibility(View.VISIBLE);
        }
        else {
            binding.lastBtn.setVisibility(View.GONE);
        }
        if (showNextQuestion){
            binding.nextBtnText.setText("下一个问答");
            binding.nextBtn.setVisibility(View.VISIBLE);
        } else if (showNextNode) {
            binding.nextBtnText.setText("下一个章节");
            binding.nextBtn.setVisibility(View.VISIBLE);
        }
        else {
            binding.nextBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void refreshView() {

    }

    private class MyHolder extends RecyclerView.ViewHolder{
        public int mPosition;
        public ItemQuestionChooseBinding itemBinding;
        public MyHolder(ItemQuestionChooseBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        public MyHolder setPosition(int position) {
            this.mPosition = position;
            itemBinding.numberTv.setText(String.valueOf(position + 1));
            if (currentSelectedQuestionIndex == position){
                itemBinding.getRoot().setSelected(true);
            }
            else {
                itemBinding.getRoot().setSelected(false);
            }
            return this;
        }

        public int getMyPosition() {
            return mPosition;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                currentSelectedNodeIndex = data.getIntExtra(AnswerBookNodeChooseActivity.RETURN_KEY_CHOSEN_NODE_INDEX , -1);
                currentSelectedQuestionIndex = -1;
            }
            loadData();
        }
    }
}
