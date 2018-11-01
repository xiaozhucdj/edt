package com.yougy.home.fragment.showFragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.yougy.common.fragment.BFragment;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.activity.ControlFragmentActivity;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.homework.CheckHomeWorkActivity;
import com.yougy.homework.CheckedHomeworkOverviewActivity;
import com.yougy.homework.PageableRecyclerView;
import com.yougy.homework.WriteHomeWorkActivity;
import com.yougy.homework.bean.HomeworkBookDetail;
import com.yougy.homework.bean.HomeworkSummary;
import com.yougy.homework.mistake_note.MistakeListActivity;
import com.yougy.message.YXClient;
import com.yougy.message.attachment.NeedRefreshHomeworkAttachment;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.FragmentExerciseBookBinding;
import com.yougy.ui.activity.databinding.ItemHomeworkListBinding;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

import static com.yougy.common.global.Constant.*;

/**
 * Created by FH on 2016/7/14.
 * 作业本中的作业列表界面
 */
public class ExerciseBookFragment extends BFragment {
    FragmentExerciseBookBinding binding;

    enum STATUS {
        DOING,
        WAIT_FOR_CHECK,
        CHECKED
    }

    private STATUS currentStatus = STATUS.DOING;

    List<HomeworkSummary> doingList = new ArrayList<>();
    List<HomeworkSummary> waitForCheckList = new ArrayList<>();
    List<HomeworkSummary> checkedList = new ArrayList<>();

//    private boolean currentIsHomework = true;//家庭作业    学生端不分

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_exercise_book, container, false);
        UIUtils.recursiveAuto(binding.getRoot());
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        binding.imageRefresh.setOnClickListener(v -> refreshData());

        binding.mainRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.mainRecyclerview.setAdapter(new PageableRecyclerView.Adapter<MyHolder>() {
            @Override
            public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyHolder(DataBindingUtil.inflate(inflater, R.layout.item_homework_list, parent, false));
            }

            @Override
            public void onBindViewHolder(MyHolder holder, int position) {
                HomeworkSummary homeworkSummary;
                switch (currentStatus) {
                    case DOING:
                        homeworkSummary = doingList.get(position);
                        if (!StringUtils.isEmpty(homeworkSummary.getExtra().getLifeTime())) {
                            holder.binding.statusTv.setText("限\n时");
                            holder.binding.statusTv.setBackgroundResource(R.drawable.img_homework_status_bg_red);
                            holder.binding.statusTv.setVisibility(View.VISIBLE);
                            holder.binding.textLifetime.setText("限时：" + homeworkSummary.getExtra().getLifeTime());
                            holder.binding.textLifetime.setVisibility(View.VISIBLE);
                        } else {
                            holder.binding.statusTv.setVisibility(View.GONE);
                            holder.binding.textLifetime.setVisibility(View.GONE);
                        }
                        holder.binding.textRateScore.setVisibility(View.GONE);
                        holder.setData(homeworkSummary);
                        break;
                    case WAIT_FOR_CHECK:
                        homeworkSummary = waitForCheckList.get(position);
                        if (IKCODE_01.equals(homeworkSummary.getExtra().getEval())){
                            holder.binding.statusTv.setText("自\n评");
                            holder.binding.statusTv.setBackgroundResource(R.drawable.img_homework_status_bg_red);
                            holder.binding.statusTv.setVisibility(View.VISIBLE);
                        } else if (isMutualEvaluation(homeworkSummary)) {
                            holder.binding.statusTv.setText("互\n评");
                            holder.binding.statusTv.setBackgroundResource(R.drawable.img_homework_status_bg_red);
                            holder.binding.statusTv.setVisibility(View.VISIBLE);
                        } else {
                            holder.binding.statusTv.setVisibility(View.GONE);
                        }
                        holder.binding.textLifetime.setVisibility(View.GONE);
                        holder.binding.textRateScore.setVisibility(View.GONE);
                        holder.setData(homeworkSummary);
                        break;
                    case CHECKED:
                        homeworkSummary = checkedList.get(position);
                        HomeworkSummary.ExtraBean extraBean = homeworkSummary.getExtra();
                        if (extraBean.getExamTotalPoints() != 0) {
                            //计分作业
                            holder.binding.textRateScore.setText("分数：" + extraBean.getTotalPoints());
                            holder.binding.statusTv.setBackgroundResource(R.drawable.img_homework_status_bg_red);
                            holder.binding.statusTv.setText("计\n分");
                            holder.binding.statusTv.setVisibility(View.VISIBLE);
                        } else {
                            holder.binding.statusTv.setVisibility(View.GONE);
                            holder.binding.textRateScore.setText("正确率：" + extraBean.getCorrectCount() + "/" + extraBean.getItemCount());
                        }
                        holder.binding.textLifetime.setVisibility(View.GONE);
                        holder.binding.textRateScore.setVisibility(View.VISIBLE);
                        holder.setData(homeworkSummary);
                        break;
                }
            }

            @Override
            public int getItemCount() {
                switch (currentStatus) {
                    case DOING:
                        return doingList.size();
                    case WAIT_FOR_CHECK:
                        return waitForCheckList.size();
                    case CHECKED:
                        return checkedList.size();
                }
                return 0;
            }
        });
        binding.mainRecyclerview.setMaxItemNumInOnePage(7);
        binding.mainRecyclerview.notifyDataSetChanged();
        binding.mainRecyclerview.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.mainRecyclerview.getRealRcyView()) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                Intent intent;
                MyHolder holder = (MyHolder) vh;
                HomeworkSummary homeworkSummary = holder.getData();
                switch (currentStatus) {
                    case CHECKED:
                        if (IHCODE_05.equals(holder.getData().getExtra().getStatusCode())) {
                            intent = new Intent(getActivity(), CheckedHomeworkOverviewActivity.class);
                            intent.putExtra("examId", holder.getData().getExam());
                            intent.putExtra("examName", holder.getData().getExtra().getName());
                            if (holder.getData().getExtra().getExamTotalPoints() != 0) {
                                //是否为计分作业
                                intent.putExtra("isScoring", true);
                                intent.putExtra("getExamTotalPoints", holder.getData().getExtra().getExamTotalPoints());
                            }
                            intent.putExtra("getItemCount", holder.getData().getExtra().getItemCount());
                            startActivity(intent);
                        }
//                        else if (IHCODE_51.equals(holder.getData().getExtra().getStatusCode())){
//                            ToastUtil.showCustomToast(getActivity() , "本次作业您未提交,无法查看");
//                        }
                        break;
                    case WAIT_FOR_CHECK:
                        if (IKCODE_01.equals(homeworkSummary.getExtra().getEval())){
                            intent = new Intent(getActivity(), CheckHomeWorkActivity.class);
                            intent.putExtra("isStudentCheck", 1);
                            intent.putExtra("examId", homeworkSummary.getExam());
                            intent.putExtra("teacherID", ((MyHolder) vh).getData().getExtra().getExamSponsor());
                            startActivity(intent);
                        }
                        else if (isMutualEvaluation(homeworkSummary)) {
                            intent = new Intent(getActivity(), CheckHomeWorkActivity.class);
                            intent.putExtra("isStudentCheck", 2);
                            intent.putExtra("examId", homeworkSummary.getExam());
                            intent.putExtra("teacherID", ((MyHolder) vh).getData().getExtra().getExamSponsor());
                            startActivity(intent);
                        } else {
                            ToastUtil.showCustomToast(getActivity(),"作业不是自评或者互评作业！不能查看！  status Code: "
                                    + homeworkSummary.getExtra().getStatusCode() + "   type Code :" + homeworkSummary.getExtra().getTypeCode());
                        }
                        break;
                    case DOING:
                        intent = new Intent(getActivity(), WriteHomeWorkActivity.class);
                        HomeworkSummary.ExtraBean extraBean = homeworkSummary.getExtra();
                        intent.putExtra("examId", homeworkSummary.getExam() + "");
                        intent.putExtra("mHomewrokId", mControlActivity.mHomewrokId);
                        intent.putExtra("examName", extraBean.getName());
                        //传参是否定时作业
                        if (!StringUtils.isEmpty(extraBean.getLifeTime())) {
                            intent.putExtra("isTimerWork", true);
                            intent.putExtra("lifeTime", extraBean.getLifeTime());
                        }
                        String typeCode = extraBean.getTypeCode();
                        if (IICODE_02.equals(typeCode)) {//课堂作业
                            intent.putExtra("isOnClass", true);
                        } else {
                            intent.putExtra("isOnClass", false);
                        }
                        //isStudentCheck  0   默认不传  1 自评   2 互评
                        LogUtils.d("isStudentCheck：：typeCode： " +  typeCode);
                        if (IKCODE_01.equals(homeworkSummary.getExtra().getEval())) {
                            intent.putExtra("isStudentCheck", 1);
                        } else if (isMutualEvaluation(homeworkSummary))  {
                            intent.putExtra("isStudentCheck", 2);
                        }
                        intent.putExtra("teacherID", extraBean.getExamSponsor());
                        startActivity(intent);
                        break;
                }
            }
        });
        binding.doingHomeworkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStatus = STATUS.DOING;
                binding.mainRecyclerview.setCurrentPage(0);
                binding.mainRecyclerview.notifyDataSetChanged();
                binding.doingHomeworkBtn.setSelected(true);
                binding.waitForCheckBtn.setSelected(false);
                binding.hasCheckedBtn.setSelected(false);
                refreshData();
            }
        });
        binding.waitForCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStatus = STATUS.WAIT_FOR_CHECK;
                binding.mainRecyclerview.setCurrentPage(0);
                binding.mainRecyclerview.notifyDataSetChanged();
                binding.doingHomeworkBtn.setSelected(false);
                binding.waitForCheckBtn.setSelected(true);
                binding.hasCheckedBtn.setSelected(false);
                refreshData();
            }
        });
        binding.hasCheckedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStatus = STATUS.CHECKED;
                binding.mainRecyclerview.setCurrentPage(0);
                binding.mainRecyclerview.notifyDataSetChanged();
                binding.doingHomeworkBtn.setSelected(false);
                binding.waitForCheckBtn.setSelected(false);
                binding.hasCheckedBtn.setSelected(true);
                refreshData();
            }
        });

        binding.mistakesBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MistakeListActivity.class);
                intent.putExtra("homeworkId", mControlActivity.mHomewrokId);
                intent.putExtra("bookId", mControlActivity.mBookId);
                startActivity(intent);
            }
        });
        binding.switch2bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 切换到课本逻辑
                if (mControlActivity.mBookId <= 0) {
                    ToastUtil.showCustomToast(getActivity(), "没有图书");
                } else if (!StringUtils.isEmpty(FileUtils.getBookFileName(mControlActivity.mBookId, FileUtils.bookDir))) {
                    mControlActivity.switch2TextBookFragment();
                } else {
                    if (NetUtils.isNetConnected()) {
                        downBookTask(mControlActivity.mBookId);
                    } else {
                        showCancelAndDetermineDialog(R.string.jump_to_net);
                    }

                }
            }
        });
        binding.switch2noteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 切换到笔记逻辑
                if (mControlActivity.mNoteId > 0) {
                    mControlActivity.switch2NoteBookFragment();
                } else {
                    ToastUtil.showCustomToast(getActivity(), "没有笔记");
                }
            }
        });
        binding.doingHomeworkBtn.setSelected(true);
        YXClient.getInstance().with(getActivity()).addOnNewCommandCustomMsgListener(new YXClient.OnMessageListener() {
            @Override
            public void onNewMessage(IMMessage message) {
                if (message.getAttachment() instanceof NeedRefreshHomeworkAttachment) {
                    refreshData();
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    protected void handleEvent() {
        super.handleEvent();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            refreshData();
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }


    private void refreshData() {
        binding.switch2homeworkBtn.setEnabled(false);
        if (mControlActivity.mNoteId <= 0) {
            binding.switch2noteBtn.setEnabled(false);
        }

        if (mControlActivity.mBookId <= 0) {
            binding.switch2bookBtn.setEnabled(false);
        }
        // 不显示IH01未开始的作业
        String statusCode = null;
        String examTypeCode = StringUtils.smartCombineStrings("[" , "]" , "\"" , "\"" , "," , IICODE_02 , IICODE_03);
        switch (currentStatus) {
            case DOING:
                statusCode = StringUtils.smartCombineStrings("[" , "]" , "\"" , "\"" , "," , IHCODE_02 , IHCODE_51);
                break;
            case WAIT_FOR_CHECK:
                statusCode = StringUtils.smartCombineStrings("[" , "]" , "\"" , "\"" , "," , IHCODE_03 , IHCODE_04);
                break;
            case CHECKED:
                statusCode = IHCODE_05;
                break;
        }
        NetWorkManager.queryHomeworkBookDetail_New(mControlActivity.mHomewrokId, examTypeCode, statusCode)
                .subscribe(new Action1<List<HomeworkBookDetail>>() {
                    @Override
                    public void call(List<HomeworkBookDetail> homeworkBookDetails) {
                        LogUtils.d("homeworkBookDetails size = " + homeworkBookDetails.size());
                        if (homeworkBookDetails.size() > 0) {
                            List<HomeworkSummary> homeworkSummaryList = homeworkBookDetails.get(0).getHomeworkContent();
                            switch (currentStatus) {
                                case DOING:
                                    doingList.clear();
                                    doingList.addAll(homeworkSummaryList);
                                    if (doingList.size() == 0) {
                                        binding.emptyHintLayout.setVisibility(View.VISIBLE);
                                        binding.emptyHintTv.setText("您已经完成所有作业啦! 继续努力!");
                                    } else {
                                        binding.emptyHintLayout.setVisibility(View.GONE);
                                    }
                                    break;
                                case WAIT_FOR_CHECK:
                                    waitForCheckList.clear();
                                    int size = homeworkBookDetails.get(0).getHomeworkRemarks().size();
                                    for (int i = 0; i < size; i++) {
                                        HomeworkSummary homeworkSummary = new HomeworkSummary();
                                        HomeworkSummary.ExtraBean extraBean = homeworkBookDetails.get(0).getHomeworkRemarks().get(i);
                                        homeworkSummary.setExtra(extraBean);
                                        waitForCheckList.add(homeworkSummary);
                                    }
                                    if (waitForCheckList.size() == 0) {
                                        binding.emptyHintLayout.setVisibility(View.VISIBLE);
                                        binding.emptyHintTv.setText("您还没有待批改的作业哦");
                                    } else {
                                        binding.emptyHintLayout.setVisibility(View.GONE);
                                    }
                                    break;
                                case CHECKED:
                                    checkedList.clear();
                                    checkedList.addAll(homeworkSummaryList);
                                    if (checkedList.size() == 0) {
                                        binding.emptyHintLayout.setVisibility(View.VISIBLE);
                                        binding.emptyHintTv.setText("您还没有已批改的作业哦");
                                    } else {
                                        binding.emptyHintLayout.setVisibility(View.GONE);
                                    }
                                    break;
                            }
                        }
                        binding.mainRecyclerview.setCurrentPage(0);
                        binding.mainRecyclerview.notifyDataSetChanged();
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    LogUtils.d("homeworkBookDetails throwable " + throwable.getMessage());
                });
    }


    private class MyHolder extends RecyclerView.ViewHolder {
        private ItemHomeworkListBinding binding;
        private HomeworkSummary data;

        public MyHolder(ItemHomeworkListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(HomeworkSummary data) {
            this.data = data;
            binding.homeworkNameTv.setText(data.getExtra().getName());
            String startTime = data.getExtra().getStartTime();
            String endTime = data.getExtra().getEndTime();
            if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
                if (startTime.length() >= 16) {
                    startTime = startTime.substring(0, 16);
                }
                if (endTime.length() >= 16) {
                    endTime = endTime.substring(0, 16);
                }
                binding.timeTv.setText("时间:" + startTime + "~" + endTime);
                binding.timeTv.setVisibility(View.VISIBLE);
            } else if (!StringUtils.isEmpty(data.getExtra().getStartTime())) {
                if (data.getExtra().getStartTime().length() < 16) {
                    binding.timeTv.setText("时间:" + data.getExtra().getStartTime());
                } else {
                    binding.timeTv.setText("时间:" + data.getExtra().getStartTime().substring(0, 16));
                }
                binding.timeTv.setVisibility(View.VISIBLE);
            } else {
                binding.timeTv.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(data.getExtra().getTeam())){
                binding.groupNameTv.setVisibility(View.VISIBLE);
                binding.groupNameTv.setText("组名 : " + data.getExtra().getTeam());
            }
            else {
                binding.groupNameTv.setVisibility(View.GONE);
            }
        }

        public HomeworkSummary getData() {
            return data;
        }

        public ItemHomeworkListBinding getBinding() {
            return binding;
        }
    }

    /**
     * 是否互评
     * @return  true 互评
     */
    private boolean isMutualEvaluation (HomeworkSummary uncheckedHomeworkSummary) {
        if (IKCODE_02.equals(uncheckedHomeworkSummary.getExtra().getEval())
                || IJCODE_03.equals(uncheckedHomeworkSummary.getExtra().getEval())) {
            return true;
        }
        return false;
    }

    //TODO:袁野
    public ControlFragmentActivity mControlActivity;

    public void setActivity(ControlFragmentActivity activity) {
        mControlActivity = activity;
    }

    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
        Intent intent = new Intent("android.intent.action.WIFI_ENABLE");
        startActivity(intent);
        dissMissUiPromptDialog();
    }

    @Override
    public void onUiCancelListener() {
        super.onUiCancelListener();
        dissMissUiPromptDialog();
    }

    @Override
    protected void onDownBookFinish() {
        super.onDownBookFinish();
        mControlActivity.switch2TextBookFragment();
    }

    public void onBackListener() {
        getActivity().finish();
    }

}
