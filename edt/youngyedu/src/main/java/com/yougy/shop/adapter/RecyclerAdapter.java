package com.yougy.shop.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yougy.common.utils.LogUtils;
import com.yougy.shop.bean.CategoryInfo;

import java.util.List;

/**
 * Created by jiangliang on 2017/5/22.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder> {
    private List<CategoryInfo> infos;

    public RecyclerAdapter(List<CategoryInfo> infos) {
        this.infos = infos;
        LogUtils.e(getClass().getName(), "infos' size : " + infos.size());
    }

    public CategoryInfo getItem(int position) {
        return infos.get(position);
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return RecyclerHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {
        holder.bindView(infos.get(position));
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    static class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView textView;

        RecyclerHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }

        void bindView(CategoryInfo info) {
            textView.setTextSize(20);
            textView.setText(info.getCategoryDisplay());
        }

        static RecyclerHolder create(ViewGroup parent) {
            return new RecyclerHolder(new TextView(parent.getContext()));
        }
    }

}
