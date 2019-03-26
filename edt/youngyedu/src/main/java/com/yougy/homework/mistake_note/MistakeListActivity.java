package com.yougy.homework.mistake_note;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.frank.etude.pageable.PageBtnBarAdapter;
import com.google.gson.internal.LinkedTreeMap;
import com.yougy.anwser.Content_new;
import com.yougy.anwser.ParsedQuestionItem;
import com.yougy.anwser.WriteableContentDisplayer;
import com.yougy.anwser.WriteableContentDisplayerAdapter;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.OnItemClickListener;
import com.yougy.homework.HomeworkBaseActivity;
import com.yougy.homework.WriteErrorHomeWorkActivity;
import com.yougy.homework.bean.MistakeSummary;
import com.yougy.homework.bean.QuestionReplyDetail;
import com.yougy.homework.bean.ReplyCommented;
import com.yougy.message.ListUtil;
import com.yougy.shop.bean.BookInfo;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityMistakeListBinding;
import com.yougy.ui.activity.databinding.ItemAnswerChooseGridviewBinding;
import com.yougy.view.CustomGridLayoutManager;
import com.yougy.view.dialog.HintDialog;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * Created by FH on 2017/11/3.
 * 错题列表界面
 */

public class MistakeListActivity extends HomeworkBaseActivity {
    ActivityMistakeListBinding binding;
    BookInfo.BookContentsBean.NodesBean topNode, currentNode;
    ArrayList<BookInfo.BookContentsBean.NodesBean> nodeTree = new ArrayList<BookInfo.BookContentsBean.NodesBean>();
    private int homeworkId, bookId;
    //当前学生作业中的所有错题数据集合（提出了我已学会的），20190125 功能调整，我已学会不移除，在ui上提示我已学会。
    private List<QuestionReplyDetail> mQuestionReplyDetails = new ArrayList<>();
    //模拟一共有多少题
    private int pageSize = 0;
    //当前展示的题目编号（展示的第几题）从0开始。
    private int currentShowQuestionIndex = 0;
    //当前展示学生的题目
    private QuestionReplyDetail questionReplyDetail;
    //当前展示的学生答案的页码(从0开始)
    private int currentShowReplyPageIndex = 0;
    //学生作业客观题结果存放集合（ABCD ture false）
    private List<Content_new> textReplyList = new ArrayList<>();
    //存放教师批注
    private List<Content_new> textCommentList = new ArrayList<>();
    private HomeWorkPageNumAdapter homeWorkPageNumAdapter;

    //用于存放互评人员名称，包括老师名字（如果有老师名字，在第一位）
    private ArrayList<String> checkerNames = new ArrayList<>();
    private int showCheckPeoplePosition = 0;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.activity_mistake_list, null, false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    @Override
    protected void handleEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
            }
        }));
        super.handleEvent();
    }

    @Override
    protected void init() {
        //标记本activity在onstop后仍然接收其他界面发送的RxBus消息
        setNeedRecieveEventAfterOnStop(true);
        topNode = getIntent().getParcelableExtra("topNode");
        currentNode = getIntent().getParcelableExtra("currentNode");
        homeworkId = getIntent().getIntExtra("homeworkId", -1);
        bookId = getIntent().getIntExtra("bookId", -1);

        if (currentNode != null) {
            binding.tvTitle.setText(currentNode.getName());
        } else {
            binding.tvTitle.setText("错题本");
        }


        /*//找到当前章节的层级结构并显示
        if (findCurrentNode(topNode, currentNode)) {
            String currentPostionText = "";
            for (int i = nodeTree.size() - 1; i >= 0; i--) {
                currentPostionText = currentPostionText + nodeTree.get(i).getName();
                if (i != 0) {
                    currentPostionText = currentPostionText + "  >>  ";
                }
            }
            binding.currentPositionTextview.setText(currentPostionText);
        }*/


        binding.wcdContentDisplayer.setContentAdapter(new WriteableContentDisplayerAdapter() {
            @Override
            public void afterPageCountChanged(String typeKey) {

                binding.pageBtnBar.refreshPageBar();
            }

            @Override
            public void beforeToPage(String fromTypeKey, int fromPageIndex, String toTypeKey, int toPageIndex) {

            }

            @Override
            public void afterToPage(String fromTypeKey, int fromPageIndex, String toTypeKey, int toPageIndex) {

                int layer0Size = binding.wcdContentDisplayer.getContentAdapter().getLayerPageCount("question", 0);
                int layer1Size = binding.wcdContentDisplayer.getContentAdapter().getLayerPageCount("question", 1);
                //根据第0层和第1层集合大小调整基准层。
                if (layer0Size > layer1Size && binding.wcdContentDisplayer.getContentAdapter().getPageCountBaseLayerIndex() != 0) {
                    binding.wcdContentDisplayer.getContentAdapter().setPageCountBaseLayerIndex(0);

                    binding.pageBtnBar.refreshPageBar();
                }


                //展示客观题reply中的学生答案（ABCD true false）
                String questionType = (String) questionReplyDetail.getParsedQuestionItem().questionContentList.get(0).getExtraData();
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
        });

        binding.pageBtnBar.setPageBarAdapter(new PageBtnBarAdapter(getApplicationContext()) {
            @Override
            public int getPageBtnCount() {
                return binding.wcdContentDisplayer.getContentAdapter().getPageCountBaseOnBaseLayer("question");
            }

            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn) {
                currentShowReplyPageIndex = btnIndex;
                binding.wcdContentDisplayer.toPage("question", currentShowReplyPageIndex, true);
            }

        });

        binding.wcdContentDisplayer.setStatusChangeListener(new WriteableContentDisplayer.StatusChangeListener() {
            @Override
            public void onStatusChanged(WriteableContentDisplayer.LOADING_STATUS newStatus, String typeKey, int pageIndex, WriteableContentDisplayer.ERROR_TYPE errorType, String errorMsg) {

                switch (newStatus) {
                    case LOADING:
                        binding.wcdContentDisplayer.setHintText("加载中");
                        break;
                    case ERROR:
                        binding.wcdContentDisplayer.setHintText(errorMsg);
                        break;
                    case SUCCESS:
                        binding.wcdContentDisplayer.setHintText(null);//设置为null该view会gone
                        break;
                }
            }
        });
    }

    private boolean findCurrentNode(BookInfo.BookContentsBean.NodesBean from, BookInfo.BookContentsBean.NodesBean tofind) {
        if (from.getId() == tofind.getId()) {
            nodeTree.add(from);
            return true;
        } else {
            if (from.getNodes() == null || from.getNodes().size() == 0) {
                return false;
            } else {
                for (BookInfo.BookContentsBean.NodesBean child : from.getNodes()) {
                    if (findCurrentNode(child, tofind)) {
                        nodeTree.add(from);
                        return true;
                    }
                }
                return false;
            }
        }
    }

    @Override
    protected void initLayout() {

        binding.setMyOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myOnClick(view);
            }
        });
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void refreshView() {
    }


    public void myOnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_node:

                if (bookId == -1 || bookId == 0) {
                    ToastUtil.showCustomToast(getApplicationContext(), "该学科还没有教材");
                    return;
                }

                Intent intent = new Intent(MistakeListActivity.this, BookStructureActivity.class);
                intent.putExtra("homeworkId", homeworkId);
                intent.putExtra("bookId", bookId);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_last_homework:

                //不提交批改数据，直接跳转到上一题
                if (currentShowQuestionIndex > 0) {
                    currentShowQuestionIndex--;
                    binding.wcdContentDisplayer.getLayer2().clearAll();

                    homeWorkPageNumAdapter.onItemClickListener.onItemClick1(currentShowQuestionIndex);

                } else {
                    ToastUtil.showCustomToast(getBaseContext(), "已经是第一题了");
                }
                break;
            case R.id.tv_next_homework:

                if (currentShowQuestionIndex < pageSize - 1) {

                    currentShowQuestionIndex++;
                    binding.wcdContentDisplayer.getLayer2().clearAll();

                    homeWorkPageNumAdapter.onItemClickListener.onItemClick1(currentShowQuestionIndex);
                } else {
                    ToastUtil.showCustomToast(getBaseContext(), "已经是最后一题了");
                }
                break;
            case R.id.ll_chooese_homework:
                if (binding.rcvAllHomeworkPage.getVisibility() == View.GONE) {

                    binding.rcvAllHomeworkPage.setVisibility(View.VISIBLE);
                    binding.ivChooeseTag.setImageResource(R.drawable.img_timu_up);
                } else {
                    binding.rcvAllHomeworkPage.setVisibility(View.GONE);
                    binding.ivChooeseTag.setImageResource(R.drawable.img_timu_down);
                }


                break;
            case R.id.ll_control_bottom:

                intent = new Intent(getApplicationContext(), WriteErrorHomeWorkActivity.class);
                intent.putExtra("HOMEWORKID", homeworkId);
                intent.putExtra("BOOKTITLE", questionReplyDetail.getHomeworkExcerpt().getCursorName());

                intent.putExtra("PARSEDQUESTIONITEM", questionReplyDetail.getParsedQuestionItem());
                intent.putExtra("LASTSCORE", questionReplyDetail.getHomeworkExcerpt().getExtra().getLastScore());
                intent.putExtra("SUBMITNUM", questionReplyDetail.getHomeworkExcerpt().getExtra().getSubmitNum());
                intent.putExtra("ISDELETED", questionReplyDetail.getHomeworkExcerpt().getExtra().isDeleted());
                intent.putExtra("REPLYID", questionReplyDetail.getReplyId());

                intent.putExtra("REPLYITEMWEIGHT", questionReplyDetail.getReplyItemWeight());
                intent.putExtra("REPLYSCORE", questionReplyDetail.getReplyScore());

                startActivity(intent);

                break;
            case R.id.show_comment_btn:
                binding.commentDialog.setVisibility(View.VISIBLE);
                break;
            case R.id.image_refresh:
                refreshUI();
                break;
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.tv_comment_cancle:
                binding.commentDialog.setVisibility(View.GONE);
                break;
            case R.id.iv_last_people:
                if (showCheckPeoplePosition > 0) {
                    showCheckPeoplePosition--;
                    setOtherCheckNames();
                    refreshQuestion();
                } else {
                    ToastUtil.showCustomToast(this, "已经是第一个批改结果");
                }
                break;
            case R.id.iv_next_people:

                if (showCheckPeoplePosition < (checkerNames.size() - 1)) {
                    showCheckPeoplePosition++;
                    setOtherCheckNames();
                    refreshQuestion();
                } else {
                    ToastUtil.showCustomToast(this, "已经是最后一个批改结果");
                }
                break;
        }
    }


    private void refreshUI() {
        if (homeworkId == -1) {
            ToastUtil.showCustomToast(getApplicationContext(), "homeworkId 为空");
            finish();
            return;
        }

        NetWorkManager.queryHomeworkExcerptWithReply(homeworkId, currentNode != null ? currentNode.getId() : null)
                .compose(bindToLifecycle())
                .subscribe(new Action1<List<QuestionReplyDetail>>() {
                    @Override
                    public void call(List<QuestionReplyDetail> questionReplyDetails) {
                        mQuestionReplyDetails.clear();

                        for (int i = 0; i < questionReplyDetails.size(); i++) {

                            QuestionReplyDetail questionReplyDetail = questionReplyDetails.get(i);
                            MistakeSummary homeworkExcerpt = questionReplyDetail.getHomeworkExcerpt();
//                            //被标记为"我已学会"的错题不算作错题,排除
//                            if (!homeworkExcerpt.getExtra().isDeleted()) {
                            mQuestionReplyDetails.add(questionReplyDetail);
//                            }
                        }
                        questionReplyDetails.clear();

                        pageSize = mQuestionReplyDetails.size();

                        if (pageSize == 0) {
                            binding.noResultTextview.setVisibility(View.VISIBLE);
                            binding.questionLayout.setVisibility(View.GONE);
                            binding.llControlTop.setVisibility(View.GONE);
                        } else {
                            binding.noResultTextview.setVisibility(View.GONE);
                            binding.questionLayout.setVisibility(View.VISIBLE);
                            binding.llControlTop.setVisibility(View.VISIBLE);

                            setHomeWorkNumberView();
                            questionReplyDetail = mQuestionReplyDetails.get(currentShowQuestionIndex);

                            setIsHasOtherCheckData();
                            refreshQuestion();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        new HintDialog(getBaseContext(), "获取数据失败", "返回", new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                finish();
                            }
                        }).show();
                    }
                });


    }

    //互评作业时，顶部批改人名字
    private void setOtherCheckNames() {
        binding.tvResult.setText(checkerNames.get(showCheckPeoplePosition));
    }


    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mQuestionReplyDetails != null) {
            mQuestionReplyDetails.clear();
        }
        binding.wcdContentDisplayer.getLayer2().recycle();
        mQuestionReplyDetails = null;
        Glide.get(this).clearMemory();
        binding.wcdContentDisplayer.clearCache();
        Runtime.getRuntime().gc();


    }

    /**
     * add by FH
     * 刷新上一题下一题按钮的UI,如果已经是第一题或者最后一题了,就置灰按钮
     */
    public void refreshLastAndNextQuestionBtns() {

        binding.tvHomeworkPosition.setText("(" + (currentShowQuestionIndex + 1) + "/" + pageSize + ")");

        if (currentShowQuestionIndex > 0) {
            binding.lastHomeworkText.setVisibility(View.VISIBLE);
            binding.lastHomeworkIcon.setVisibility(View.VISIBLE);
        } else {
            binding.lastHomeworkText.setVisibility(View.GONE);
            binding.lastHomeworkIcon.setVisibility(View.GONE);
        }
        if (currentShowQuestionIndex < pageSize - 1) {
            binding.nextHomeworkIcon.setVisibility(View.VISIBLE);
            binding.nextHomeworkText.setVisibility(View.VISIBLE);
        } else {
            binding.nextHomeworkIcon.setVisibility(View.GONE);
            binding.nextHomeworkText.setVisibility(View.GONE);
        }
    }

    private void refreshQuestion() {

        if (binding.commentDialog.getVisibility() == View.VISIBLE) {
            binding.commentDialog.setVisibility(View.GONE);
        }

        binding.pageBtnBar.setCurrentSelectPageIndex(-1);
        currentShowReplyPageIndex = 0;

        Integer itemWeight = questionReplyDetail.getReplyItemWeight();
        //不是记分题
        if (itemWeight == null) {

            binding.tvCheckScore.setVisibility(View.GONE);
            int replyScore = questionReplyDetail.getReplyScore();
            switch (replyScore) {
                case -1://说明未批改
                    //异常情况（进入错题笨了，就不可能未批改）
                    break;
                case 0://判错
                    binding.ivCheckResult.setImageResource(R.drawable.img_cuowu);
                    break;
                case 50://判半对
                    binding.ivCheckResult.setImageResource(R.drawable.img_bandui);
                    break;
                case 100://判对
                    binding.ivCheckResult.setImageResource(R.drawable.img_zhengque);

                    break;
            }
        } else {
            binding.tvCheckScore.setVisibility(View.VISIBLE);

            //记分题
            int replyScore = questionReplyDetail.getReplyScore();
            //未批改
            if (replyScore == -1) {
                //异常情况（进入错题笨了，就不可能未批改）

            } else if (replyScore == 0) {
                //错误
                binding.ivCheckResult.setImageResource(R.drawable.img_cuowu);
                binding.tvCheckScore.setText("（" + replyScore + "分）");
            } else {
                //满分
                if (itemWeight == replyScore) {
                    binding.ivCheckResult.setImageResource(R.drawable.img_zhengque);
                    binding.tvCheckScore.setText("（" + replyScore + "分）");
                } else {
                    //半对
                    binding.ivCheckResult.setImageResource(R.drawable.img_bandui);
                    binding.tvCheckScore.setText("（" + replyScore + "分）");
                }
            }
        }
        String mistakeFromName = questionReplyDetail.getHomeworkExcerpt().getExtra().getName();
        binding.tvMistakeFrom.setText("来源于：" + (TextUtils.isEmpty(mistakeFromName) ? "问答" : mistakeFromName));
        binding.tvTitle.setText(questionReplyDetail.getHomeworkExcerpt().getCursorName());

        binding.wcdContentDisplayer.getContentAdapter().setPageCountBaseLayerIndex(1);
        binding.wcdContentDisplayer.getLayer1().setIntercept(true);
        binding.wcdContentDisplayer.getLayer2().setIntercept(true);

        /***********填充所有需要展示的3层数据资源 start***************/

        //拆分出学生答案中的轨迹图片和TEXT（因为客观题有ABCD ture false）
        List<Content_new> imgReplyList = new ArrayList<>();
        //先清空集合数据（避免其他题目数据传入）
        textReplyList.clear();


        List<Content_new> content_news = questionReplyDetail.getParsedReplyContentList();

        int needSaveSize = content_news.size();

        for (int i = 0; i < needSaveSize; i++) {

            Content_new content_new = content_news.get(i);

            if (content_new != null) {
                if (content_new.getType() == Content_new.Type.IMG_URL) {
                    imgReplyList.add(content_new);
                } else if (content_new.getType() == Content_new.Type.TEXT) {
                    textReplyList.add(content_new);
                }
            } else {
                imgReplyList.add(null);
            }
        }


        //拆分出教师批改结果里的批注和批改轨迹
        List<Content_new> imgCommentList = new ArrayList<>();
        //先清空集合数据（避免其他题目数据传入）
        textCommentList.clear();

        List<Content_new> replyCommentList = null;
        List<LinkedTreeMap> replyCommetnList = questionReplyDetail.getReplyComment();
        if (replyCommetnList != null && replyCommetnList.size() > 0) {

            List<ReplyCommented> replyCommentedList = questionReplyDetail.getReplyCommented();
            if (replyCommentedList != null && replyCommentedList.size() > 0) {

                //有老师，有学生批改，说明是互评
                if (showCheckPeoplePosition == 0) {//点击的是教师批改
                    replyCommentList = questionReplyDetail.getParsedReplyCommentList();
                } else {
                    replyCommentList = questionReplyDetail.getReplyCommented().get(showCheckPeoplePosition - 1).parse().getParsedReplyCommentList();
                }

            } else {
                //只有教师批改，说明是老师批改
                replyCommentList = questionReplyDetail.getParsedReplyCommentList();
            }

        } else {
            List<ReplyCommented> replyCommentedList = questionReplyDetail.getReplyCommented();
            if (replyCommentedList != null && replyCommentedList.size() > 0) {

                //只有学生批改，说明是互评，或者自评
                replyCommentList = questionReplyDetail.getReplyCommented().get(showCheckPeoplePosition).parse().getParsedReplyCommentList();
            } else {
                //没有教师和学生批改，异常数据，错题本中的题目必须已经批改了

            }
        }

        if (replyCommentList != null) {
            for (int i = 0; i < replyCommentList.size(); i++) {
                Content_new content_new = replyCommentList.get(i);
                if (content_new != null) {
                    if (content_new.getType() == Content_new.Type.IMG_URL) {
                        imgCommentList.add(content_new);
                    } else if (content_new.getType() == Content_new.Type.TEXT) {
                        textCommentList.add(content_new);
                    }
                } else {
                    imgCommentList.add(null);
                }
            }
        }

        //根据获取的结果，是否展示出显示批注按钮
        if (textCommentList.size() > 0) {
            String commentStr = "";
            for (int i = 0; i < textCommentList.size(); i++) {
                Content_new textComment = textCommentList.get(i);

                String value = textComment.getValue();
                if (!TextUtils.isEmpty(value)) {
                    commentStr += value;
                }
            }

            if (!TextUtils.isEmpty(commentStr)) {
                binding.commentTv.setText(commentStr);
                binding.showCommentBtn.setVisibility(View.VISIBLE);
            } else {
                binding.showCommentBtn.setVisibility(View.GONE);
            }
        } else {
            binding.showCommentBtn.setVisibility(View.GONE);
        }


        binding.wcdContentDisplayer.getContentAdapter().updateDataList("analysis", 0, questionReplyDetail.getParsedQuestionItem().analysisContentList);
        if (imgCommentList.size() != 0) {
            binding.wcdContentDisplayer.getContentAdapter().updateDataList("question", 2, imgCommentList);
        } else {
            binding.wcdContentDisplayer.getContentAdapter().deleteDataList("question", 2);
        }
        if (imgReplyList.size() == 0) {
            binding.wcdContentDisplayer.getContentAdapter().setPageCountBaseLayerIndex(0);
            binding.wcdContentDisplayer.getContentAdapter().deleteDataList("question", 1);
            binding.wcdContentDisplayer.getContentAdapter().updateDataList("question", 0, questionReplyDetail.getParsedQuestionItem().questionContentList);
            int newPageCount = binding.wcdContentDisplayer.getContentAdapter().getPageCountBaseOnBaseLayer("question");
        } else {
            binding.wcdContentDisplayer.getContentAdapter().updateDataList("question", 0, questionReplyDetail.getParsedQuestionItem().questionContentList);
            binding.wcdContentDisplayer.getContentAdapter().updateDataList("question", 1, imgReplyList);
        }

        /***********填充所有需要展示的3层数据资源 end***************/

        //是否展示我已学会ui
        if (questionReplyDetail.getHomeworkExcerpt().getExtra().isDeleted()) {
            binding.tvXuehui.setVisibility(View.VISIBLE);
        } else {
            binding.tvXuehui.setVisibility(View.GONE);
        }

        refreshLastAndNextQuestionBtns();
    }


    /**
     * 设置选择题的结果界面
     */
    private void setChooeseResult() {

        //清理掉其他题中的作业结果。
//        checkedAnswerList.clear();
        List<ParsedQuestionItem.Answer> chooeseAnswerList = questionReplyDetail.getParsedQuestionItem().answerList;


        binding.rcvChooeseItem.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(MistakeListActivity.this).inflate(R.layout.item_answer_choose_gridview, parent, false);
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
        binding.rcvChooeseItem.setLayoutManager(gridLayoutManager);

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


                //选择题选择的结果
                ArrayList<String> checkedAnswerList = new ArrayList<String>();

                for (int i = 0; i < textReplyList.size(); i++) {

                    String replyResult = textReplyList.get(i).getValue();
                    checkedAnswerList.add(replyResult);
                }

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

    private void setHomeWorkNumberView() {

        homeWorkPageNumAdapter = new HomeWorkPageNumAdapter();
        CustomGridLayoutManager gridLayoutManager = new CustomGridLayoutManager(this, 8);
        gridLayoutManager.setScrollEnabled(false);
        binding.rcvAllHomeworkPage.setLayoutManager(gridLayoutManager);
        binding.rcvAllHomeworkPage.setAdapter(homeWorkPageNumAdapter);

        homeWorkPageNumAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick1(int position) {

                currentShowQuestionIndex = position;

                if (binding.rcvAllHomeworkPage.getVisibility() == View.VISIBLE) {
                    binding.rcvAllHomeworkPage.setVisibility(View.GONE);
                    binding.ivChooeseTag.setImageResource(R.drawable.img_timu_down);
                }

                questionReplyDetail = mQuestionReplyDetails.get(currentShowQuestionIndex);

                setIsHasOtherCheckData();
                refreshQuestion();

                homeWorkPageNumAdapter.notifyDataSetChanged();

            }
        });

    }

    private void setIsHasOtherCheckData() {
        checkerNames.clear();
        //1.查看是否有教师批改结果
        List<LinkedTreeMap> replyCommetnList = questionReplyDetail.getReplyComment();
        if (replyCommetnList != null && replyCommetnList.size() > 0) {

            List<ReplyCommented> replyCommentedList = questionReplyDetail.getReplyCommented();
            if (replyCommentedList != null && replyCommentedList.size() > 0) {

                //有老师，有学生批改，说明是互评
                binding.rlOtherCheckBar.setVisibility(View.VISIBLE);

                checkerNames.add("批改结果（教师）");
                for (ReplyCommented replyCommented : replyCommentedList) {
                    checkerNames.add("批改结果（" + replyCommented.getReplyCommentatorName() + "）");
                }
                setOtherCheckNames();
            } else {
                //只有教师批改，说明是老师批改
                binding.rlOtherCheckBar.setVisibility(View.GONE);
            }

        } else {
            List<ReplyCommented> replyCommentedList = questionReplyDetail.getReplyCommented();
            if (replyCommentedList != null && replyCommentedList.size() > 0) {

                //只有学生批改，说明是互评，或者自评
                binding.rlOtherCheckBar.setVisibility(View.VISIBLE);
                for (ReplyCommented replyCommented : replyCommentedList) {
                    checkerNames.add("批改结果（" + replyCommented.getReplyCommentatorName() + "）");
                }
                setOtherCheckNames();
            } else {
                //没有教师和学生批改，异常数据，错题本中的题目必须已经批改了
                binding.rlOtherCheckBar.setVisibility(View.GONE);
            }
        }
    }

    /*顶部页面的adapter相关*/
    class HomeWorkPageNumViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_page_id)
        TextView mTvPageId;


        public HomeWorkPageNumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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
            if (position == currentShowQuestionIndex) {
                holder.mTvPageId.setBackgroundResource(R.drawable.img_timu_cuowu);
                holder.mTvPageId.setTextColor(getResources().getColor(R.color.white));
            } else {
                holder.mTvPageId.setBackgroundResource(R.drawable.img_timu_chooese);
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
            return pageSize;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (binding.wcdContentDisplayer != null) {
            binding.wcdContentDisplayer.leaveScribbleMode();
        }
    }

}
