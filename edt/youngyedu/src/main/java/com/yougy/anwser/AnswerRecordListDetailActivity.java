package com.yougy.anwser;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.frank.etude.pageable.PageBtnBarAdapter;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.OnItemClickListener;
import com.yougy.homework.bean.HomeworkDetail;
import com.yougy.homework.bean.QuestionReplyDetail;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityAnswerRecordListDetailBinding;
import com.yougy.view.CustomGridLayoutManager;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * 学生问答记录集合记录的详情界面（这个页面已经调整为AnswerRecordListDetailActivity，用来展示问答结果列表页面）
 */

public class AnswerRecordListDetailActivity extends BaseActivity {
    ActivityAnswerRecordListDetailBinding binding;

    ParsedQuestionItem parsedQuestionItem;
    int examId;
    private List<Content_new> textReplyList = new ArrayList<>();
    //当前展示的学生答案的页码(从0开始)
    private int currentShowReplyPageIndex = -1;
    //当前展示的解析的页码(从0开始)
    private int currentShowAnalysisPageIndex = -1;
    private ArrayList<Integer> examIdList;
    //当前顶部展示（展示的第几题问答）从0开始。
    private int showHomeWorkPosition = 0;

    @Override
    protected void setContentView() {
        LogUtils.e(tag, "setContentView............");
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_answer_record_list_detail, null, false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
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
                View view = LayoutInflater.from(AnswerRecordListDetailActivity.this).inflate(R.layout.item_answer_choose_gridview, parent, false);
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


    @Override
    protected void init() {

        examIdList = (ArrayList<Integer>) getIntent().getSerializableExtra("itemIdList");

    }

    @Override
    protected void initLayout() {
        binding.pageBtnBar.setPageBarAdapter(new PageBtnBarAdapter(AnswerRecordListDetailActivity.this) {
            @Override
            public int getPageBtnCount() {
                if (binding.questionBodyBtn.isSelected()) {
                    return binding.contentDisplayer.getContentAdapter().getPageCountBaseOnBaseLayer("question");
                } else if (binding.answerAnalysisBtn.isSelected()) {
                    return binding.contentDisplayer.getContentAdapter().getPageCountBaseOnBaseLayer("analysis");
                }
                return 0;
            }

            @Override
            public void onPageBtnClick(View btn, int btnIndex, String textInBtn) {
                if (binding.questionBodyBtn.isSelected()) {
                    currentShowReplyPageIndex = btnIndex;
                    binding.contentDisplayer.toPage("question", currentShowReplyPageIndex, true);
                    //展示客观题reply中的学生答案（ABCD true false）
                    showJudgeOrSelect();
                } else if (binding.answerAnalysisBtn.isSelected()) {
                    currentShowAnalysisPageIndex = btnIndex;
                    binding.contentDisplayer.toPage("analysis", currentShowAnalysisPageIndex, true);
                }
            }
        });
        binding.contentDisplayer.setContentAdapter(new WriteableContentDisplayerAdapter() {
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
                    if (correctBaseLayerIndex(binding.contentDisplayer.getContentAdapter().getLayerPageCount("question", 0)
                            , binding.contentDisplayer.getContentAdapter().getLayerPageCount("question", 1))) {
                        binding.pageBtnBar.refreshPageBar();
                    }
                }
            }
        });

        binding.contentDisplayer.setStatusChangeListener(new WriteableContentDisplayer.StatusChangeListener() {
            @Override
            public void onStatusChanged(WriteableContentDisplayer.LOADING_STATUS newStatus, String typeKey, int pageIndex, WriteableContentDisplayer.ERROR_TYPE errorType, String errorMsg) {

                switch (newStatus) {
                    case LOADING:
                        binding.contentDisplayer.setHintText("加载中");
                        break;
                    case ERROR:
                        binding.contentDisplayer.setHintText(errorMsg);
                        break;
                    case SUCCESS:
                        binding.contentDisplayer.setHintText(null);//设置为null该view会gone
                        break;
                }
            }
        });

        binding.questionBodyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //题干
                binding.questionBodyBtn.setSelected(true);
                binding.answerAnalysisBtn.setSelected(false);
                showJudgeOrSelect();
                binding.questionTypeTextview.setText("题目类型 : " + parsedQuestionItem.questionContentList.get(0).getExtraData());
                binding.startTimeTv.setVisibility(View.VISIBLE);
                binding.spendTimeTv.setVisibility(View.VISIBLE);
                binding.pageBtnBar.setCurrentSelectPageIndex(currentShowReplyPageIndex);
                correctBaseLayerIndex(binding.contentDisplayer.getContentAdapter().getLayerPageCount("question", 0)
                        , binding.contentDisplayer.getContentAdapter().getLayerPageCount("question", 1));
                binding.pageBtnBar.refreshPageBar();
                if (currentShowReplyPageIndex != -1) {
                    binding.contentDisplayer.toPage("question", currentShowReplyPageIndex, true);
                }
            }
        });
        binding.answerAnalysisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //解答
                binding.questionBodyBtn.setSelected(false);
                binding.answerAnalysisBtn.setSelected(true);
                binding.rcvChooeseItem.setVisibility(View.GONE);
                binding.llChooeseItem.setVisibility(View.GONE);
                binding.questionTypeTextview.setText("解析");
                binding.startTimeTv.setVisibility(View.INVISIBLE);
                binding.spendTimeTv.setVisibility(View.INVISIBLE);
                binding.contentDisplayer.getContentAdapter().setPageCountBaseLayerIndex(0);
                binding.pageBtnBar.setCurrentSelectPageIndex(currentShowAnalysisPageIndex);
                binding.pageBtnBar.refreshPageBar();
                if (currentShowAnalysisPageIndex != -1) {
                    binding.contentDisplayer.toPage("analysis", currentShowAnalysisPageIndex, true);
                }
            }
        });
    }

    @Override
    protected void loadData() {

        if (examIdList == null || examIdList.size() == 0){
            ToastUtil.showCustomToast(getApplicationContext() , "没有问答");
            finish();
            return;
        }

        setTopListData();
        examId = examIdList.get(showHomeWorkPosition);

        if (showHomeWorkPosition == 0) {
            binding.tvLastHomework.setVisibility(View.GONE);
        } else {
            binding.tvLastHomework.setVisibility(View.VISIBLE);
        }
        if (showHomeWorkPosition == examIdList.size() - 1) {
            binding.tvNextHomework.setVisibility(View.GONE);
        } else {
            binding.tvNextHomework.setVisibility(View.VISIBLE);
        }


    }

    /**
     * 校正基准层,本来应该以第1层作为基准层,但是考虑到第1层有的时候可能不如第0层页数多,此时需要校正基准层为第0层.
     * 如果校正了基准层为第0层,则返回true,否则返回false.
     */
    private boolean correctBaseLayerIndex(int layer0PageCount, int layer1PageCount) {
        if (layer0PageCount > layer1PageCount) {
            binding.contentDisplayer.getContentAdapter().setPageCountBaseLayerIndex(0);
            return true;
        } else {
            binding.contentDisplayer.getContentAdapter().setPageCountBaseLayerIndex(1);
            return false;
        }
    }

    @Override
    protected void refreshView() {
    }

    public void back(View view) {
        onBackPressed();
    }

    public void refresh(View view) {
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
        binding.contentDisplayer.clearCache();
        Runtime.getRuntime().gc();
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


            if (position == showHomeWorkPosition) {
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
//                    notifyDataSetChanged();
//                    holder.mTvPageId.setBackgroundResource(R.drawable.img_timu_zhengqu);
//                    holder.mTvPageId.setTextColor(getResources().getColor(R.color.white));
                }
            });
        }

        @Override
        public int getItemCount() {
            return examIdList.size();
        }
    }

    private void setTopListData() {
        binding.tvHomeworkPosition.setText((showHomeWorkPosition + 1) + "/" + examIdList.size());

        HomeWorkPageNumAdapter homeWorkPageNumAdapter = new HomeWorkPageNumAdapter();
        CustomGridLayoutManager gridLayoutManager = new CustomGridLayoutManager(this, 8);
        gridLayoutManager.setScrollEnabled(false);
        binding.rcvAllHomeworkPage.setLayoutManager(gridLayoutManager);
        binding.rcvAllHomeworkPage.setAdapter(homeWorkPageNumAdapter);

        homeWorkPageNumAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick1(int position) {
                showHomeWorkPosition = position;
                onClick(binding.llChooeseHomework);
                loadData();
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_last_homework:
                if (showHomeWorkPosition == 0) {
                    ToastUtil.showCustomToast(this, "已经是第一个问答题");
                    return;
                }
                showHomeWorkPosition--;
                loadData();
                break;
            case R.id.tv_next_homework:
                if (showHomeWorkPosition == examIdList.size() - 1) {
                    ToastUtil.showCustomToast(this, "已经是最后一个问答题");
                    return;
                }
                showHomeWorkPosition++;
                loadData();

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


        }

    }

}
