package com.yougy.home.fragment.showFragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.yougy.common.fragment.BFragment;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.DateUtils;
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
import com.yougy.homework.mistake_note.BookStructureActivity;
import com.yougy.message.YXClient;
import com.yougy.message.attachment.NeedRefreshHomeworkAttachment;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.FragmentExerciseBookBinding;
import com.yougy.ui.activity.databinding.ItemHomeworkListBinding;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

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
                switch (currentStatus) {
                    case DOING:
                        HomeworkSummary doingHomeworkSummary = doingList.get(position);
                        if (!StringUtils.isEmpty(doingHomeworkSummary.getExtra().getLifeTime())) {
                            holder.binding.statusTv.setText("定\n\n时");
                            holder.binding.statusTv.setBackgroundResource(R.drawable.img_homework_status_bg_blue);
                            holder.binding.textLifetime.setText("计时：" + doingHomeworkSummary.getExtra().getLifeTime());
                            holder.binding.textLifetime.setVisibility(View.VISIBLE);
                        }
                        holder.setData(doingHomeworkSummary);
                        break;
                    case WAIT_FOR_CHECK:
                        HomeworkSummary uncheckedHomeworkSummary = waitForCheckList.get(position);
                        if ("II54".equals(uncheckedHomeworkSummary.getExtra().getTypeCode())){
                            holder.binding.statusTv.setText("自\n\n评");
                            holder.binding.statusTv.setBackgroundResource(R.drawable.img_homework_status_bg_blue);
                        } else if ("II55".equals(uncheckedHomeworkSummary.getExtra().getStatusCode())) {
                            holder.binding.statusTv.setText("互\n\n评");
                            holder.binding.statusTv.setBackgroundResource(R.drawable.img_homework_status_bg_blue);
                        }
                        else {
                            holder.binding.statusTv.setVisibility(View.GONE);
                        }
                        holder.setData(waitForCheckList.get(position));
                        break;
                    case CHECKED:
                        HomeworkSummary checkedHomeworkSummary = checkedList.get(position);
                        HomeworkSummary.ExtraBean extraBean = checkedHomeworkSummary.getExtra();
                        if (extraBean.getExamTotalPoints() != 0) {
                            //计分作业
                            holder.binding.textRateScore.setText("分数：" + extraBean.getTotalPoints());
                            holder.binding.statusTv.setBackgroundResource(R.drawable.img_homework_status_bg_gray);
                            holder.binding.statusTv.setText("计\n\n分");
                        } else {
                            holder.binding.statusTv.setVisibility(View.GONE);
                            holder.binding.textRateScore.setText("正确率：" + (int)(extraBean.getAccuracy() * 100) + "%");
                        }
                        holder.binding.textRateScore.setVisibility(View.VISIBLE);
                        holder.setData(checkedHomeworkSummary);
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
                switch (currentStatus) {
                    case CHECKED:
                        if (holder.getData().getExtra().getStatusCode().equals("IH51")){
                            ToastUtil.showCustomToast(getActivity() , "本次作业您未提交,无法查看");
                        }
                        else if (holder.getData().getExtra().getStatusCode().equals("IH05")){
                            intent = new Intent(getActivity(), CheckedHomeworkOverviewActivity.class);
                            intent.putExtra("examId", holder.getData().getExam());
                            intent.putExtra("examName", holder.getData().getExtra().getName());
                            if (holder.getData().getExtra().getExamTotalPoints() != 0) {
                                //是否为计分作业
                                intent.putExtra("isScoring", true);
                            }
                            intent.putExtra("getExamTotalPoints", holder.getData().getExtra().getExamTotalPoints());
                            intent.putExtra("getTotalPoints", holder.getData().getExtra().getTotalPoints());
                            intent.putExtra("getAccuracy", holder.getData().getExtra().getAccuracy());
                            startActivity(intent);
                        }
                        break;
                    case WAIT_FOR_CHECK:
                        HomeworkSummary uncheckedHomeworkSummary = holder.getData();
                        if ("IH52".equals(uncheckedHomeworkSummary.getExtra().getStatusCode())){
                            intent = new Intent(getActivity() , CheckHomeWorkActivity.class);
                            intent.putExtra("examId" , uncheckedHomeworkSummary.getExam());
                            startActivity(intent);
                        }
                        break;
                    case DOING:
                        intent = new Intent(getActivity(), WriteHomeWorkActivity.class);
                        intent.putExtra("examId", ((MyHolder) vh).getData().getExam() + "");
                        intent.putExtra("mHomewrokId", mControlActivity.mHomewrokId);
                        intent.putExtra("examName", ((MyHolder) vh).getData().getExtra().getName());
                        //传参是否定时作业
                        if (!StringUtils.isEmpty(((MyHolder) vh).getData().getExtra().getLifeTime())) {
                            intent.putExtra("isTimerWork", true);
                            intent.putExtra("lifeTime", ((MyHolder) vh).getData().getExtra().getLifeTime());
                        }
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
                Intent intent = new Intent(getActivity(), BookStructureActivity.class);
                intent.putExtra("homeworkId", mControlActivity.mHomewrokId);
                intent.putExtra("bookId", mControlActivity.mBookId);
                startActivity(intent);
            }
        });
        binding.switch2bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 切换到课本逻辑
                if (mControlActivity.mBookId <= 0){
                    ToastUtil.showCustomToast(getActivity() , "没有图书");
                }
                else if ( !StringUtils.isEmpty(FileUtils.getBookFileName(mControlActivity.mBookId , FileUtils.bookDir))) {
                        mControlActivity.switch2TextBookFragment();
                }else{
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
                if (mControlActivity.mNoteId>0){
                    mControlActivity.switch2NoteBookFragment();
                }
                else {
                    ToastUtil.showCustomToast(getActivity() , "没有笔记");
                }
            }
        });
        binding.doingHomeworkBtn.setSelected(true);
        YXClient.getInstance().with(getActivity()).addOnNewCommandCustomMsgListener(new YXClient.OnMessageListener() {
            @Override
            public void onNewMessage(IMMessage message) {
                if (message.getAttachment() instanceof NeedRefreshHomeworkAttachment){
                    refreshData();
                }
            }
        });

        //课堂作业和家庭作业切换
       /* binding.toggleHomework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接设置刷新适配器数据
                if (currentIsHomework) {
                    binding.toggleHomework.setText("进入家庭作业");
                    currentIsHomework = false;
                } else {
                    binding.toggleHomework.setText("进入课堂作业");
                    currentIsHomework = true;
                }
                refreshData();
            }
        });*/
        return binding.getRoot();
    }

    @Override
    protected void handleEvent() {
//        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
//            @Override
//            public void call(Object o) {
//                if (o instanceof String || o.equals("refreshHomeworkList")){
//                    refreshData();
//                }
//            }
//        }));
        super.handleEvent();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden){
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
        if (mControlActivity.mNoteId<=0){
            binding.switch2noteBtn.setEnabled(false);
        }

        if (mControlActivity.mBookId <= 0){
            binding.switch2bookBtn.setEnabled(false);
        }
  // 待服务器调试   正确率显示   数据刷新IH 参数
        String statusCode = "IH01";
        switch (currentStatus) {
            case DOING:
                statusCode = "[\"IH01\",\"IH02\"]";
                break;
            case WAIT_FOR_CHECK:
                statusCode = "[\"IH03\",\"IH04\",\"IH52\"]";
                break;
            case CHECKED:
                statusCode = "[\"IH05\",\"IH51\"]";
                break;
        }
        String examTypeCode = "[\"II02\",\"II03\",\"II54\",\"II55\",\"II56\",\"II57\",\"II58\",\"II59\"]";
        NetWorkManager.queryHomeworkBookDetail_New(mControlActivity.mHomewrokId, examTypeCode,statusCode)
                .subscribe(new Action1<List<HomeworkBookDetail>>() {
                    @Override
                    public void call(List<HomeworkBookDetail> homeworkBookDetails) {
                        LogUtils.d("homeworkBookDetails size = " + homeworkBookDetails.size());
                    if (homeworkBookDetails.size() > 0) {
                        List<HomeworkSummary> homeworkSummaryList = homeworkBookDetails.get(0).getHomeworkContent();
                        switch (currentStatus) {
                            case DOING:
                                doingList.clear();
                                for (HomeworkSummary homeworkSummary : homeworkSummaryList) {
                                    if ("IH01".equals(homeworkSummary.getExtra().getStatusCode())) {
                                        //如果作业开始时间已经早于现在的时间,说明作业已经开始了,
                                        //但是如果此时这个作业的状态还是IH01未开始,则调一次刷新接口刷新整个作业本,这样这个作业的状态就可以更正了.
                                        long startTime = DateUtils.convertTimeStrToTimeStamp(homeworkSummary.getExtra().getStartTime() , "yyyy-MM-dd HH:mm:ss");
                                        long currentTime = System.currentTimeMillis();
                                        if (startTime < currentTime){
                                            LogUtils.e("ERROR retry, 考试状态不对,刷新考试列表.");
                                            ToastUtil.showCustomToast(getActivity() , "发现有考试状态不对,刷新考试列表");
                                            refreshData();
                                            return;
                                        }
                                    }
                                }
                                doingList.addAll(homeworkSummaryList);
                                if (doingList.size() == 0){
                                    binding.emptyHintLayout.setVisibility(View.VISIBLE);
                                    binding.emptyHintTv.setText("您已经完成所有作业啦! 继续努力!");
                                }
                                else {
                                    binding.emptyHintLayout.setVisibility(View.GONE);
                                }
                                break;
                            case WAIT_FOR_CHECK:
                                waitForCheckList.clear();
                                waitForCheckList.addAll(homeworkSummaryList);
                                if (waitForCheckList.size() == 0){
                                    binding.emptyHintLayout.setVisibility(View.VISIBLE);
                                    binding.emptyHintTv.setText("您还没有待批改的作业哦");
                                }
                                else {
                                    binding.emptyHintLayout.setVisibility(View.GONE);
                                }
                                break;
                            case CHECKED:
                                checkedList.clear();
                                checkedList.addAll(homeworkSummaryList);
                                if (checkedList.size() == 0){
                                    binding.emptyHintLayout.setVisibility(View.VISIBLE);
                                    binding.emptyHintTv.setText("您还没有已批改的作业哦");
                                }
                                else {
                                    binding.emptyHintLayout.setVisibility(View.GONE);
                                }
                                break;
                        }

                    }

                    binding.mainRecyclerview.setCurrentPage(0);
                    binding.mainRecyclerview.notifyDataSetChanged();

                        /*if (homeworkBookDetails.size() > 0) {
                            checkedList.clear();
                            waitForCheckList.clear();
                            doingList.clear();
                            //作业状态码:
                            //未开始(IH01),作答中(IH02),未批改(IH03),批改中(IH04),已批改(IH05),未提交(IH51)
                            List<HomeworkSummary> homeworkSummaryList = homeworkBookDetails.get(0).getHomeworkContent();
                            for (HomeworkSummary homeworkSummary : homeworkSummaryList) {
                                String statusCode = homeworkSummary.getExtra().getStatusCode();
                                if ("IH01".equals(statusCode)) {
                                    //如果作业开始时间已经早于现在的时间,说明作业已经开始了,
                                    //但是如果此时这个作业的状态还是IH01未开始,则调一次刷新接口刷新整个作业本,这样这个作业的状态就可以更正了.
                                    long startTime = DateUtils.convertTimeStrToTimeStamp(homeworkSummary.getExtra().getStartTime(), "yyyy-MM-dd HH:mm:ss");
                                    long endTime = DateUtils.convertTimeStrToTimeStamp(homeworkSummary.getExtra().getEndTime(), "yyyy-MM-dd HH:mm:ss");
                                    long currentTime = System.currentTimeMillis();
                                    if (startTime < currentTime) {
                                        LogUtils.e("FH", "发现有作业状态不对,刷新作业列表" +
                                                " 作业id: " + homeworkSummary.getExam()
                                                + " 作业状态 : " + statusCode
                                                + " startTime : " + homeworkSummary.getExtra().getStartTime()
                                                + " endTime : " + homeworkSummary.getExtra().getEndTime()
                                                + "currentTime" + System.currentTimeMillis());
                                        ToastUtil.showCustomToast(ExerciseBookFragment.this.getActivity(), "发现有考试状态不对,刷新考试列表");
                                        NetWorkManager.refreshHomeworkBook(mControlActivity.mHomewrokId)
                                                .subscribe(new Action1<Object>() {
                                                    @Override
                                                    public void call(Object o) {
                                                        //刷新成功后再次调用查询接口查询新的作业本数据
                                                        refreshData();
                                                    }
                                                }, new Action1<Throwable>() {
                                                    @Override
                                                    public void call(Throwable throwable) {
                                                        throwable.printStackTrace();
                                                    }
                                                });
                                        return;
                                    }
                                } else if ("IH02".equals(statusCode)) {//作答中
                                    doingList.add(homeworkSummary);
                                } else if ("IH03".equals(statusCode) || "IH04".equals(statusCode) //未批改,批改中都算待批改
                                        || "IH52".equals(statusCode)) {//自评中也算待批改
                                    waitForCheckList.add(homeworkSummary);
                                } else if ("IH05".equals(statusCode) || "IH51".equals(statusCode)) {//已批改,未提交都算已批改
                                    checkedList.add(homeworkSummary);
                                }
                            }
                        }
                        binding.mainRecyclerview.setCurrentPage(0);
                        binding.mainRecyclerview.notifyDataSetChanged();
                        switch (currentStatus) {
                            case CHECKED:
                                if (checkedList.size() == 0) {
                                    binding.emptyHintLayout.setVisibility(View.VISIBLE);
                                    binding.emptyHintTv.setText("您还没有已批改的作业哦");
                                } else {
                                    binding.emptyHintLayout.setVisibility(View.GONE);
                                }
                                break;
                            case DOING:
                                if (doingList.size() == 0) {
                                    binding.emptyHintLayout.setVisibility(View.VISIBLE);
                                    binding.emptyHintTv.setText("您已经完成所有作业啦! 继续努力!");
                                } else {
                                    binding.emptyHintLayout.setVisibility(View.GONE);
                                }
                                break;
                            case WAIT_FOR_CHECK:
                                if (waitForCheckList.size() == 0) {
                                    binding.emptyHintLayout.setVisibility(View.VISIBLE);
                                    binding.emptyHintTv.setText("您还没有待批改的作业哦");
                                } else {
                                    binding.emptyHintLayout.setVisibility(View.GONE);
                                }
                                break;
                        }*/
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        LogUtils.d("homeworkBookDetails throwable " + throwable.getMessage());
                    }
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
            binding.timeTv.setText("时间:" + data.getExtra().getStartTime()+ "~" + data.getExtra().getEndTime());
        }

        public HomeworkSummary getData() {
            return data;
        }

        public ItemHomeworkListBinding getBinding() {
            return binding;
        }
    }

    //TODO:袁野
    public ControlFragmentActivity mControlActivity;

    public void setActivity(ControlFragmentActivity activity) {
        mControlActivity = activity;
    }

 /*   public void aaaa() {
        int id1 = mControlActivity.mHomewrokId;
        int id2 = mControlActivity.mNoteId;
        int id3 = mControlActivity.mBookId;
    }
*/

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
}
