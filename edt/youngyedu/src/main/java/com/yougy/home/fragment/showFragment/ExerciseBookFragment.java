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

import com.yougy.common.fragment.BFragment;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.activity.ControlFragmentActivity;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.homework.PageableRecyclerView;
import com.yougy.homework.mistake_note.BookStructureActivity;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.FragmentExerciseBookBinding;
import com.yougy.ui.activity.databinding.ItemHomeworkListBinding;

import java.util.ArrayList;


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

    ArrayList doingList = new ArrayList(){
        {
            for (int i = 0 ; i < 45 ; i++){
                add(new Object());
            }
        }
    };
    ArrayList waitForCheckList = new ArrayList(){
        {
            for (int i = 0 ; i < 25 ; i++){
                add(new Object());
            }
        }
    };
    ArrayList checkedList = new ArrayList(){
        {
            for (int i = 0 ; i < 35 ; i++){
                add(new Object());
            }
        }
    };

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
//                        holder.binding.homeworkNameTv.setText("作业中作业" + position);
                        break;
                    case WAIT_FOR_CHECK:
                        holder.binding.statusTv.setText("待\n批\n改");
                        holder.binding.statusTv.setBackgroundResource(R.drawable.img_homework_status_bg_gray);
//                        holder.binding.homeworkNameTv.setText("待批改作业" + position);
                        break;
                    case CHECKED:
                        holder.binding.statusTv.setText("已\n批\n改");
                        holder.binding.statusTv.setBackgroundResource(R.drawable.img_homework_status_bg_gray);
//                        holder.binding.homeworkNameTv.setText("已批改作业" + position);
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
                //TODO 列表点击
            }
        });
        binding.doingHomeworkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStatus = STATUS.DOING;
                binding.mainRecyclerview.setCurrentPage(1);
                binding.mainRecyclerview.notifyDataSetChanged();
            }
        });
        binding.waitForCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStatus = STATUS.WAIT_FOR_CHECK;
                binding.mainRecyclerview.setCurrentPage(1);
                binding.mainRecyclerview.notifyDataSetChanged();
            }
        });
        binding.hasCheckedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStatus = STATUS.CHECKED;
                binding.mainRecyclerview.setCurrentPage(1);
                binding.mainRecyclerview.notifyDataSetChanged();
            }
        });
        binding.mistakesBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity() , BookStructureActivity.class));
            }
        });
        binding.switch2bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 切换到课本逻辑
            }
        });
        binding.switch2noteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 切换到笔记逻辑
            }
        });
        binding.switch2homeworkBtn.setSelected(true);
        binding.doingHomeworkBtn.setSelected(true);
        return binding.getRoot();
    }

    private class MyHolder extends RecyclerView.ViewHolder{
        ItemHomeworkListBinding binding;
        public MyHolder(ItemHomeworkListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }







    //TODO:袁野
    public ControlFragmentActivity mControlActivity;
    public void setActivity(ControlFragmentActivity activity) {
        mControlActivity = activity;
    }
}
