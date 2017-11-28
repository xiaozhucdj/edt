package com.yougy.home.fragment.showFragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.common.fragment.BFragment;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.activity.ControlFragmentActivity;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.homework.CheckedHomeworkDetailActivity;
import com.yougy.homework.FullScreenHintDialog;
import com.yougy.homework.PageableRecyclerView;
import com.yougy.homework.bean.HomeworkBookDetail;
import com.yougy.homework.bean.HomeworkSummary;
import com.yougy.homework.WriteHomeWorkActivity;
import com.yougy.homework.mistake_note.BookStructureActivity;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.FragmentExerciseBookBinding;
import com.yougy.ui.activity.databinding.ItemHomeworkListBinding;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;


/**
 * Created by FH on 2016/7/14.
 */
public class ExerciseBookFragment extends BFragment {
    FragmentExerciseBookBinding binding;
    enum STATUS {
        DOING,
        WAIT_FOR_CHECK,
        CHECKED
    }
    private STATUS currentStatus = STATUS.DOING;

    ArrayList<HomeworkSummary> doingList = new ArrayList<HomeworkSummary>();
    ArrayList<HomeworkSummary> waitForCheckList = new ArrayList<HomeworkSummary>();
    ArrayList<HomeworkSummary> checkedList = new ArrayList<HomeworkSummary>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()) , R.layout.fragment_exercise_book, container , false);
        UIUtils.recursiveAuto(binding.getRoot());
        binding.mainRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity() , LinearLayoutManager.VERTICAL , false));
        binding.mainRecyclerview.setAdapter(new PageableRecyclerView.Adapter<MyHolder>() {
            @Override
            public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    return new MyHolder(DataBindingUtil.inflate(inflater , R.layout.item_homework_list , parent , false));
            }

            @Override
            public void onBindViewHolder(MyHolder holder, int position) {
                switch (currentStatus){
                    case DOING:
                        holder.binding.statusTv.setText("作\n业\n中");
                        holder.binding.statusTv.setBackgroundResource(R.drawable.img_homework_status_bg_blue);
                        holder.setData(doingList.get(position));
                        break;
                    case WAIT_FOR_CHECK:
                        holder.binding.statusTv.setText("待\n批\n改");
                        holder.binding.statusTv.setBackgroundResource(R.drawable.img_homework_status_bg_gray);
                        holder.setData(waitForCheckList.get(position));
                        break;
                    case CHECKED:
                        holder.binding.statusTv.setText("已\n批\n改");
                        holder.binding.statusTv.setBackgroundResource(R.drawable.img_homework_status_bg_gray);
                        holder.setData(checkedList.get(position));
                        break;
                }
            }
            @Override
            public int getItemCount() {
                switch (currentStatus){
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
        binding.mainRecyclerview.setMaxItemNumInOnePage(6);
        binding.mainRecyclerview.notifyDataSetChanged();
        binding.mainRecyclerview.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.mainRecyclerview.getRealRcyView()) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                switch (currentStatus){
                    case CHECKED:
                        Intent intent = new Intent(getActivity() , CheckedHomeworkDetailActivity.class);
                        intent.putExtra("examId" , ((MyHolder) vh).getData().getExam());
                        intent.putExtra("examName" , ((MyHolder) vh).getData().getExtra().getName());
                        startActivity(intent);
                        break;
                    case WAIT_FOR_CHECK:
//                        Intent intent111 = new Intent(Settings.ACTION_SETTINGS);
//                        startActivity(intent111);
                        //TODO 待批改项点击
                        break;
                    case DOING:
                        //TODO 作业中项点击
                        break;
                }
            }
        });
        binding.doingHomeworkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStatus = STATUS.DOING;
                binding.mainRecyclerview.setCurrentPage(1);
                binding.mainRecyclerview.notifyDataSetChanged();
                binding.doingHomeworkBtn.setSelected(true);
                binding.waitForCheckBtn.setSelected(false);
                binding.hasCheckedBtn.setSelected(false);
            }
        });
        binding.waitForCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStatus = STATUS.WAIT_FOR_CHECK;
                binding.mainRecyclerview.setCurrentPage(1);
                binding.mainRecyclerview.notifyDataSetChanged();
                binding.doingHomeworkBtn.setSelected(false);
                binding.waitForCheckBtn.setSelected(true);
                binding.hasCheckedBtn.setSelected(false);
            }
        });
        binding.hasCheckedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStatus = STATUS.CHECKED;
                binding.mainRecyclerview.setCurrentPage(1);
                binding.mainRecyclerview.notifyDataSetChanged();
                binding.doingHomeworkBtn.setSelected(false);
                binding.waitForCheckBtn.setSelected(false);
                binding.hasCheckedBtn.setSelected(true);
            }
        });
        binding.mistakesBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity() , BookStructureActivity.class);
                intent.putExtra("homeworkId" , mControlActivity.mHomewrokId);
                intent.putExtra("bookId" , mControlActivity.mBookId);
                startActivity(intent);
            }
        });
        binding.switch2bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 切换到课本逻辑
                startActivity(new Intent(getActivity(),WriteHomeWorkActivity.class));
            }
        });
        binding.switch2noteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 切换到笔记逻辑
                new FullScreenHintDialog(getActivity() , "test")
                        .setIconResId(R.drawable.icon_caution_big)
                        .setContentText("是否提交作业?")
                        .setBtn1("检查作业", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ToastUtil.showToast(getContext() , "检查作业");
                                dialog.dismiss();
                            }
                        } , false)
                        .setBtn2("确认提交", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ToastUtil.showToast(getContext() , "确认提交");
                                dialog.dismiss();
                            }
                        } , true)
                        .show();
            }
        });
        binding.doingHomeworkBtn.setSelected(true);
        refreshData();
        return binding.getRoot();
    }

    private void refreshData(){
        Log.v("FH" , "mHomeworkid = " + mControlActivity.mHomewrokId);
        NetWorkManager.queryHomeworkBookDetail(mControlActivity.mHomewrokId)
                .subscribe(new Action1<List<HomeworkBookDetail>>() {
                    @Override
                    public void call(List<HomeworkBookDetail> homeworkBookDetails) {
                        checkedList.clear();
                        waitForCheckList.clear();
                        doingList.clear();
                        if (homeworkBookDetails.size() > 0){
                            parseData(homeworkBookDetails.get(0));
                        }
                        binding.mainRecyclerview.setCurrentPage(1);
                        binding.mainRecyclerview.notifyDataSetChanged();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    private void parseData(HomeworkBookDetail homeworkBookDetail){
        //作业状态码:
        //未开始(IH01),作答中(IH02),未批改(IH03),批改中(IH04),已批改(IH05),未提交(IH51)
        List<HomeworkSummary> homeworkSummaryList = homeworkBookDetail.getHomeworkContent();
        for (HomeworkSummary homeworkSummary : homeworkSummaryList) {
            String statusCode = homeworkSummary.getExtra().getStatusCode();
            if (statusCode.equals("IH01")){

            }
            else if (statusCode.equals("IH02")){//作答中
                doingList.add(homeworkSummary);
            }
            else if (statusCode.equals("IH03") || statusCode.equals("IH04")){//未批改,批改中
                waitForCheckList.add(homeworkSummary);
            }
            else if (statusCode.equals("IH05")){//已批改
                checkedList.add(homeworkSummary);
            }
        }
    }

    private class MyHolder extends RecyclerView.ViewHolder{
        private ItemHomeworkListBinding binding;
        private HomeworkSummary data;
        public MyHolder(ItemHomeworkListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(HomeworkSummary data){
            this.data = data;
            binding.homeworkNameTv.setText(data.getExtra().getName());
        }
        public HomeworkSummary getData() {
            return data;
        }

        public ItemHomeworkListBinding getBinding() {
            return binding;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void back(View view){
        getActivity().finish();
    }







    //TODO:袁野
    public ControlFragmentActivity mControlActivity;
    public void setActivity(ControlFragmentActivity activity) {
        mControlActivity = activity;
    }

    public void aaaa() {
        int id1 = mControlActivity.mHomewrokId;
        int id2 = mControlActivity.mNoteId;
        int id3 = mControlActivity.mBookId;
    }
}
