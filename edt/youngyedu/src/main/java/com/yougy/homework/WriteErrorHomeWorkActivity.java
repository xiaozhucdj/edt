package com.yougy.homework;

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

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.yougy.anwser.ParsedQuestionItem;
import com.yougy.anwser.QuestionAnswerContainer;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.SharedPreferencesUtil;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

import static com.yougy.common.utils.SharedPreferencesUtil.getSpUtil;


public class WriteErrorHomeWorkActivity extends BaseActivity {

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
    @BindView(R.id.tv_submit_homework)
    TextView tvSubmitHomeWork;


    private NoteBookView2 mNbvAnswerBoard;

    private static final int PAGE_SHOW_SIZE = 5;

    private int homeWorkPageSize = 17;
    private int questionPageSize = 1;

    //底部页码数偏移量
    private int pageDeviationNum = 0;
    private QuestionPageNumAdapter questionPageNumAdapter;

    //当前顶部展示的页数（展示的第几题）从0开始。
    private int showHomeWorkPosition = 0;
//    private HomeWorkPageNumAdapter homeWorkPageNumAdapter;

    //顶部作业题目展示数据
    List<HomeworkDetail.ExamPaper.ExamPaperContent> examPaperContentList;
    //底部某一题多页数据
    private List<ParsedQuestionItem.Question> questionList;
    //如果是选择题，这里存储选择题的结果
    private List<ParsedQuestionItem.Answer> chooeseAnswerList;
    //选择题选择的结果
    private ArrayList<String> checkedAnswerList = new ArrayList<String>();
    //作业中某一题对返回结果解析后拿到的结果对象
    private ParsedQuestionItem parsedQuestionItem;


    //保存当前题目结果的页面从0开始
    private int saveHomeWorkPage;

    //保存当前题目页面分页，默认从0开始
    private int saveQuestionPage = 0;

    private int examId = 550;
    //是否添加了手写板
    private boolean isAddAnswerBoard;
    //byte数组集合，（用来保存每一页书写的笔记数据）
    private ArrayList<byte[]> bytesList = new ArrayList<>();
    //存储每一页截屏图片地址
    private ArrayList<String> pathList = new ArrayList<>();


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_write_error_homework);
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
        NetWorkManager.queryHomeworkDetail(examId).subscribe(new Action1<List<HomeworkDetail>>() {
            @Override
            public void call(List<HomeworkDetail> homeworkDetails) {
                HomeworkDetail.ExamPaper examPaper = homeworkDetails.get(0).getExamPaper();

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


        //作业中某一题题目、答案切换
        questionPageNumAdapter = new QuestionPageNumAdapter();
        CustomLinearLayoutManager linearLayoutManager = new CustomLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager.setScrollEnabled(false);
        allQuestionPage.setLayoutManager(linearLayoutManager);
        allQuestionPage.setAdapter(questionPageNumAdapter);

        questionPageNumAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick1(int position) {
                ToastUtil.showToast(WriteErrorHomeWorkActivity.this, position + 1 + "页");

                //离开手绘模式，并刷新界面ui
                EpdController.leaveScribbleMode(mNbvAnswerBoard);
                mNbvAnswerBoard.invalidate();


                //保存上一个题目多页数据中的某一页手写笔记。
                bytesList.set(saveQuestionPage, mNbvAnswerBoard.bitmap2Bytes());
                pathList.set(saveQuestionPage, saveBitmapToFile(saveScreenBitmap()));

                mNbvAnswerBoard.clearAll();

                //将本页设置为选中页
                saveQuestionPage = position;

                //从之前bytesList中回显之前保存的手写笔记，如果有的话
                if (bytesList.size() > position) {
                    byte[] tmpBytes = bytesList.get(position);
                    if (tmpBytes != null) {
                        mNbvAnswerBoard.drawBitmap(BitmapFactory.decodeByteArray(tmpBytes, 0, tmpBytes.length));
                    }
                }


                ParsedQuestionItem.Question question = null;
                if (position < questionList.size()) {
                    //切换当前题目的分页
                    question = questionList.get(position);
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
                if (question != null) {
                    if ("选择".equals(question.questionType)) {
                        if (isAddAnswerBoard) {
                            rlAnswer.removeView(mNbvAnswerBoard);
                            isAddAnswerBoard = false;
                        }
                        rcvChooese.setVisibility(View.VISIBLE);
                        chooeseAnswerList = parsedQuestionItem.answerList;

                        setChooeseResult();

                    } else {
                        if (!isAddAnswerBoard) {
                            rlAnswer.addView(mNbvAnswerBoard);
                            isAddAnswerBoard = true;
                        }
                        rcvChooese.setVisibility(View.GONE);
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

        questionPageNumAdapter.onItemClickListener.onItemClick1(0);
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


    @OnClick({ R.id.tv_submit_homework, R.id.tv_clear_write, R.id.tv_add_page})
    public void onClick(View view) {
        EpdController.leaveScribbleMode(mNbvAnswerBoard);
        mNbvAnswerBoard.invalidate();

        switch (view.getId()) {

            /*case R.id.tv_last_homework:
                if (showHomeWorkPosition > 0) {
                    showHomeWorkPosition--;
                    COMEIN_HOMEWORK_PAGE_MODE = 1;
                    homeWorkPageNumAdapter.onItemClickListener.onItemClick1(showHomeWorkPosition);
                } else {
                    ToastUtil.showToast(this, "已经是第一题了");
                }
                break;
            case R.id.tv_next_homework:
                if (showHomeWorkPosition < homeWorkPageSize - 1) {
                    showHomeWorkPosition++;
                    COMEIN_HOMEWORK_PAGE_MODE = 2;
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

                break;*/
            case R.id.tv_submit_homework:
                saveHomeWorkData();
                break;
            case R.id.tv_clear_write:
                mNbvAnswerBoard.clearAll();
                break;
            case R.id.tv_add_page:
                questionPageSize++;
                bytesList.add(null);
                pathList.add(null);
                questionPageNumAdapter.notifyDataSetChanged();
                questionPageNumAdapter.onItemClickListener.onItemClick1(questionPageSize - 1);

                break;
           /* case R.id.ll_chooese_homework:
                if (allHomeWorkPage.getVisibility() == View.GONE) {
                    allHomeWorkPage.setVisibility(View.VISIBLE);
                    ivChooeseTag.setImageResource(R.drawable.img_timu_down);
                } else {
                    allHomeWorkPage.setVisibility(View.GONE);
                    ivChooeseTag.setImageResource(R.drawable.img_timu_up);
                }

                break;*/
        }
    }

    /**
     * 保存之前操作题目结果数据
     */
    private void saveHomeWorkData() {
        //刷新最后没有保存的数据
        bytesList.set(saveQuestionPage, mNbvAnswerBoard.bitmap2Bytes());
        pathList.set(saveQuestionPage, saveBitmapToFile(saveScreenBitmap()));
        mNbvAnswerBoard.clearAll();


        //保存手写笔记，用于回显（1，暂存时，2，题目切换时）
        SharedPreferencesUtil.getSpUtil().setDataList(examId + "_" + saveHomeWorkPage + "_bytes_list", bytesList);
        //保存待上传图片，用于上传
        SharedPreferencesUtil.getSpUtil().setDataList(examId + "_" + saveHomeWorkPage + "_path_list", pathList);
        SharedPreferencesUtil.getSpUtil().setDataList(examId + "_" + saveHomeWorkPage + "_chooese_list", checkedAnswerList);

        String chooeseResult = "";
        if (questionList != null && "选择".equals(questionList.get(0).questionType)) {

            for (int i = 0; i < checkedAnswerList.size(); i++) {
                chooeseResult = chooeseResult + "," + checkedAnswerList.get(i);
            }

            if (!TextUtils.isEmpty(chooeseResult)) {
                chooeseResult = chooeseResult.substring(1);
            }

            //选择题需要存储选择结果
            getSpUtil().putString(examId + "_" + saveHomeWorkPage + "_chooese_result", chooeseResult);
        }

        //以上存储了3中数据，1：保存手写笔记集合，2：保存每页图片集合，3：如果是选择题，保存选择题结果字符串。
        //存储成功之后，将内存中的数据全部清空。

        for (int i = 0; i < bytesList.size(); i++) {
            byte[] tmp = bytesList.get(i);
            tmp = null;
        }
        bytesList.clear();
        pathList.clear();
        checkedAnswerList.clear();
        chooeseResult = "";

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
