package com.yougy.homework;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.yougy.anwser.ParsedQuestionItem;
import com.yougy.anwser.QuestionAnswerContainer;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.new_network.NetWorkManager;
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
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by FH on 2017/10/19.
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
    @BindView(R.id.question_container)
    QuestionAnswerContainer questionContainer;
    @BindView(R.id.rl_answer)
    RelativeLayout rlAnswer;


    private NoteBookView2 mNbvAnswerBoard;

    private static final int PAGE_SHOW_SIZE = 5;

    private int homeWorkPageSize = 17;
    private int questionPageSize = 3;

    //底部页码数偏移量
    private int pageDeviationNum = 0;
    private QuestionPageNumAdapter questionPageNumAdapter;

    //当前顶部展示的页数（展示的第几题）从0开始。
    private int showHomeWorkPosition = 0;
    private HomeWorkPageNumAdapter homeWorkPageNumAdapter;

    //顶部作业题目展示数据
    List<com.yougy.homework.bean.HomeworkDetail.ExamPaper.ExamPaperContent> examPaperContentList;
    //底部某一题多页数据
    private List<ParsedQuestionItem.Question> questionList;
    //如果是选择题，这里存储选择题的结果
    private List<ParsedQuestionItem.Answer> chooeseAnswerList;
    //选择题选择的结果
    private ArrayList<String> checkedAnswerList = new ArrayList<String>();

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_write_homework);
    }

    @Override
    protected void init() {

    }


    @Override
    protected void initLayout() {
        //新建写字板，并添加到界面上
        mNbvAnswerBoard = new NoteBookView2(this);

    }

    @Override
    protected void loadData() {
        NetWorkManager.queryHomeworkDetail(524).subscribe(new Action1<List<HomeworkDetail>>() {
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
                ToastUtil.showToast(WriteHomeWorkActivity.this, position + 1 + "题");

                EpdController.leaveScribbleMode(mNbvAnswerBoard);
                mNbvAnswerBoard.invalidate();

                showHomeWorkPosition = position;

                com.yougy.homework.bean.HomeworkDetail.ExamPaper.ExamPaperContent examPaperContent = examPaperContentList.get(position);
                ParsedQuestionItem parsedQuestionItem = examPaperContent.getParsedQuestionItemList().get(0);
                questionList = parsedQuestionItem.questionList;


                questionPageSize = questionList.size();

                //取题目的第一页纸展示
                ParsedQuestionItem.Question question = questionList.get(0);
                if (question instanceof ParsedQuestionItem.HtmlQuestion) {
                    questionContainer.setHtmlUrl(((ParsedQuestionItem.HtmlQuestion) question).htmlUrl);
                } else if (question instanceof ParsedQuestionItem.TextQuestion) {
                    questionContainer.setText(((ParsedQuestionItem.TextQuestion) question).text);
                } else if (question instanceof ParsedQuestionItem.ImgQuestion) {
                    questionContainer.setImgUrl(((ParsedQuestionItem.ImgQuestion) question).imgUrl);
                }
                questionContainer.setVisibility(View.VISIBLE);
                questionPageNumAdapter.notifyDataSetChanged();

                if ("选择".equals(question.questionType)) {
                    rlAnswer.removeView(mNbvAnswerBoard);
                    rcvChooese.setVisibility(View.VISIBLE);
                    chooeseAnswerList = parsedQuestionItem.answerList;

                    setChooeseResult();

                } else {
                    rlAnswer.addView(mNbvAnswerBoard);
                    rcvChooese.setVisibility(View.GONE);
                }


//                HomeWorkPageNumViewHolder holder = (HomeWorkPageNumViewHolder) allHomeWorkPage.findViewHolderForAdapterPosition(position);
//                homeWorkPageNumAdapter.notifyDataSetChanged();
//                holder.mTvPageId.setBackgroundResource(R.drawable.img_timu_zhengqu);
//                holder.mTvPageId.setTextColor(getResources().getColor(R.color.white));

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
                ToastUtil.showToast(WriteHomeWorkActivity.this, position + 1 + "页");

                EpdController.leaveScribbleMode(mNbvAnswerBoard);
                mNbvAnswerBoard.invalidate();

                if (position < questionList.size()) {
                    //切换当前题目的分页
                    ParsedQuestionItem.Question question = questionList.get(position);
                    if (question instanceof ParsedQuestionItem.HtmlQuestion) {
                        questionContainer.setHtmlUrl(((ParsedQuestionItem.HtmlQuestion) question).htmlUrl);
                    } else if (question instanceof ParsedQuestionItem.TextQuestion) {
                        questionContainer.setText(((ParsedQuestionItem.TextQuestion) question).text);
                    } else if (question instanceof ParsedQuestionItem.ImgQuestion) {
                        questionContainer.setImgUrl(((ParsedQuestionItem.ImgQuestion) question).imgUrl);
                    }
                    questionContainer.setVisibility(View.VISIBLE);
                } else {
                    //加白纸
                    questionContainer.setVisibility(View.GONE);

                }


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

        if (examPaperContentList != null && examPaperContentList.size() > 0) {

            homeWorkPageNumAdapter.onItemClickListener.onItemClick1(0);
            if (questionList != null && questionList.size() > 0) {
                questionPageNumAdapter.onItemClickListener.onItemClick1(0);
            }
        }
        //触发一下点击事件。默认隐藏所有题目
        onClick(findViewById(R.id.ll_chooese_homework));
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
                View view = LayoutInflater.from(WriteHomeWorkActivity.this).inflate(R.layout.item_answer_choose_gridview, parent, false);
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


    @OnClick({R.id.tv_last_homework, R.id.tv_next_homework, R.id.tv_save_homework, R.id.tv_submit_homework, R.id.tv_clear_write, R.id.tv_add_page, R.id.ll_chooese_homework})
    public void onClick(View view) {
        EpdController.leaveScribbleMode(mNbvAnswerBoard);
        mNbvAnswerBoard.invalidate();

        switch (view.getId()) {

            case R.id.tv_last_homework:
                if (showHomeWorkPosition > 0) {
                    showHomeWorkPosition--;
                    homeWorkPageNumAdapter.onItemClickListener.onItemClick1(showHomeWorkPosition);
                } else {
                    ToastUtil.showToast(this, "已经是第一题了");
                }
                break;
            case R.id.tv_next_homework:
                if (showHomeWorkPosition < homeWorkPageSize - 1) {
                    showHomeWorkPosition++;
                    homeWorkPageNumAdapter.onItemClickListener.onItemClick1(showHomeWorkPosition);
                } else {
                    ToastUtil.showToast(this, "已经是最后一题了");
                }
                break;
            case R.id.tv_save_homework:
                break;
            case R.id.tv_submit_homework:
                break;
            case R.id.tv_clear_write:
                mNbvAnswerBoard.clearAll();
                break;
            case R.id.tv_add_page:
                questionPageSize++;
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
        }
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


            holder.mTvPageId.setBackgroundResource(R.drawable.img_timu_chooese);
            holder.mTvPageId.setTextColor(getResources().getColor(R.color.black));

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

            int tag = 2;
            tag = position % 2;

            if (holder.mTvPageId.getTag() != null) {
                tag = (int) holder.mTvPageId.getTag();
            }
            switch (tag) {
                default:
                case 0://错误
                    holder.mTvPageId.setBackgroundResource(R.drawable.img_normal_question_bg);
                    holder.mTvPageId.setTextColor(getResources().getColor(R.color.black));
                    break;
                case 1://选中
                    holder.mTvPageId.setBackgroundResource(R.drawable.img_press_question_bg);
                    holder.mTvPageId.setTextColor(getResources().getColor(R.color.white));
                    break;
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
                } else {
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
                    checkedAnswerList.add(((ParsedQuestionItem.TextAnswer) answer).text);
                }
                rcvChooese.getAdapter().notifyDataSetChanged();
            }
        }
    }

}
