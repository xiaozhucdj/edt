package com.yougy.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.artifex.mupdfdemo.OutlineItem;
import com.yougy.common.utils.LogUtils;
import com.yougy.ui.activity.R;

/**
 * Created by Administrator on 2016/7/1.
 */
public class OutlineAdapter extends BaseAdapter {
    private final OutlineItem mItems[];
    private final LayoutInflater mInflater;
    private final int LINE_HEIGHT= 1;
    private final int LINE_HEIGHT_SECTION = 2;

    private final int MARGIN = 45;
    private final int MARGIN_SECTION = 20;

    private final int ITEM_HIGHT = 50;
    private final int ITEM_SECTION_HIGHT = 60;

    public OutlineAdapter(LayoutInflater inflater, OutlineItem items[]) {
        mInflater = inflater;
        mItems = items;
    }

    public int getCount() {
        // 当获取目录 不存在
        if (mItems == null || mItems.length == 0) {
            return 0;
        }
        return mItems.length;
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_outline_entry, null);
            holder = new ViewHolder();

            holder.tv_title = convertView.findViewById(R.id.tv_title);
            holder.tv_page = convertView.findViewById(R.id.tv_page);
            holder.rl_manger = convertView.findViewById(R.id.rl_manger);
            holder.view_line = convertView.findViewById(R.id.view_line);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        int level = mItems[position].level;

        LogUtils.e(getClass().getName(),mItems[position].toString());
        RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams) holder.rl_manger.getLayoutParams();
        RelativeLayout.LayoutParams lineParams = (RelativeLayout.LayoutParams) holder.view_line.getLayoutParams();

        if (level == 1) {//标题下选择
            lineParams.height = LINE_HEIGHT ;
            holder.view_line.setLayoutParams(lineParams);
            rlParams.height = ITEM_HIGHT ;
            rlParams.leftMargin = MARGIN ;
            holder.rl_manger.setLayoutParams(rlParams);
        } else { // 是标题
            lineParams.height = LINE_HEIGHT_SECTION ;
            holder.view_line.setLayoutParams(lineParams);
            rlParams.height = ITEM_SECTION_HIGHT ;
            rlParams.leftMargin = MARGIN_SECTION ;
            holder.rl_manger.setLayoutParams(rlParams);
        }
        holder.tv_title.setText(mItems[position].title);
        holder.tv_page.setText(String.valueOf(mItems[position].page + 1) + "页");
        return convertView;
    }


    public static class ViewHolder {
        public TextView tv_title;
        public TextView tv_page;
        public RelativeLayout rl_manger;
        public View view_line;
    }
}

