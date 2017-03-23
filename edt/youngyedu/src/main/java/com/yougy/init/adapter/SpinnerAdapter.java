package com.yougy.init.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yougy.init.bean.SpinnerInfo;

import java.util.List;

/**
 * Created by jiangliang on 2016/10/14.
 */

public class SpinnerAdapter extends BaseAdapter {

    private List<SpinnerInfo> infos;
    private Context context;
    public SpinnerAdapter(Context context,List<SpinnerInfo> infos){
        this.infos = infos;
        this.context = context;
    }

    @Override
    public int getCount() {
        return infos.size();
    }

    @Override
    public SpinnerInfo getItem(int position) {
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = new TextView(context);
        }
        TextView tv = (TextView) convertView;
        tv.setWidth(100);
        tv.setHeight(50);
        tv.setGravity(Gravity.CENTER);
        tv.setText(infos.get(position).getName());
        return tv;
    }
}
