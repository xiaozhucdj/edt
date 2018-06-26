package com.yougy.task.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.common.fragment.BFragment;
import com.yougy.common.utils.LogUtils;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.task.TaskDetailActivity;
import com.yougy.task.adapter.CommonViewHolder;
import com.yougy.task.adapter.MaterialAdapter;
import com.yougy.task.adapter.TaskAdapter;
import com.yougy.task.bean.MaterialBean;
import com.yougy.task.bean.StudyDataBean;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.FragmentTabTaskMaterialsBinding;
import com.yougy.view.CustomLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhang yc
 * @create date: 2018/6/22 10:58
 * @class desc:  任务学习资料
 * @modifier: 
 * @modify date: 2018/6/22 10:58
 * @modify desc: 
 */
public class FragmentTaskMaterials extends BFragment{

    FragmentTabTaskMaterialsBinding binding;

    private MaterialAdapter materialAdapter;

    private final int COUNT_PER_PAGE = 9;

    private List<StudyDataBean> allMaterialBeanList = new ArrayList<>();
    private List<StudyDataBean> currentMaterialBeanList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tab_task_materials, container, false);
        initView();
        loadData();
        return binding.getRoot();
    }


    private void initView() {
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.adaper_divider_img_normal));
        binding.recyclerViewMaterials.addItemDecoration(divider);

        CustomLinearLayoutManager layout = new CustomLinearLayoutManager(getActivity());
        layout.setScrollHorizontalEnabled(false);
        binding.recyclerViewMaterials.setLayoutManager(layout);

        materialAdapter = new MaterialAdapter(getActivity());
        binding.recyclerViewMaterials.setAdapter(materialAdapter);

        binding.recyclerViewMaterials.addOnItemTouchListener(new OnRecyclerItemClickListener(binding.recyclerViewMaterials) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                itemClick(vh);
            }
        });
    }

    private void itemClick(RecyclerView.ViewHolder vh) {
        LogUtils.d("zhangyc Material Name:" + currentMaterialBeanList.get(vh.getAdapterPosition()));
    }


    private void loadData () {
        List<StudyDataBean> studyDataBeanList = ((TaskDetailActivity) getActivity()).getStudyDataBeanList();
//        currentMaterialBeanList.add(new MaterialBean(10, "资料1"));
        currentMaterialBeanList.addAll(studyDataBeanList);
        materialAdapter.setData(currentMaterialBeanList);
        materialAdapter.notifyDataSetChanged();
    }


}
