package com.yougy.task.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.yougy.task.bean.MaterialBean;
import com.yougy.task.bean.StudyDataBean;
import com.yougy.ui.activity.BR;
import com.yougy.ui.activity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2018/6/22.
 */

public class MaterialAdapter extends RecyclerView.Adapter<CommonViewHolder> {

    private Context mContext;
    private List<StudyDataBean> materialBeanLists = new ArrayList<>();

    public MaterialAdapter (Context context) {
        mContext = context;
    }

    public void setData (List<StudyDataBean> materialBeanList) {
        materialBeanLists.clear();
        materialBeanLists.addAll(materialBeanList);
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),R.layout.item_material, parent, false);
        return new CommonViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        ViewDataBinding binding = DataBindingUtil.getBinding(holder.itemView);
        //set data
        binding.setVariable(BR.material,materialBeanLists.get(position));
        binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return materialBeanLists.size();
    }
}
