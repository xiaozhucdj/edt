package com.yougy.shop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yougy.shop.bean.CategoryInfo;

import java.util.List;

/**
 * Created by jiangliang on 2017/3/20.
 */

public class StageAdapter extends BaseAdapter {

    private List<CategoryInfo> infos;

    public StageAdapter(List<CategoryInfo> infos){
        this.infos = infos;
    }

    @Override
    public int getCount() {
        return infos.size();
    }

    @Override
    public CategoryInfo getItem(int position) {
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        TextView text;

        if (convertView == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        } else {
            view = convertView;
        }
        text = (TextView) view;
        text.setText(infos.get(position).getCategoryDisplay());
        return view;
    }
}
