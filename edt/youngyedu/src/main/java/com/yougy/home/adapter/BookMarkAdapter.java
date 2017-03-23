package com.yougy.home.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.yougy.home.bean.BookMarkInfo;
import com.yougy.ui.activity.R;

import java.util.List;

/**
 * Created by Administrator on 2016/7/12.
 */
public class BookMarkAdapter extends BaseAdapter {

    private Context mContext;
    private List<BookMarkInfo> mInfos;

    public BookMarkAdapter(List<BookMarkInfo> infos, Context context) {
        this.mInfos =infos;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.adapter_book_marks, null);
            holder = new ViewHolder();
            holder.mTvCreatTime = (TextView) convertView.findViewById(R.id.tv_creat_time);
            holder.mTvMarkNmae = (TextView) convertView.findViewById(R.id.tv_mark_name);
            holder.mTvMarkPage = (TextView) convertView.findViewById(R.id.tv_mark_page);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTvCreatTime.setText(mInfos.get(position).getCreatTime());
        holder.mTvMarkNmae.setText(mInfos.get(position).getMarkName());
        holder.mTvMarkPage.setText(mInfos.get(position).getNumber() + "页");
        return convertView;
    }

    static class ViewHolder {
        TextView mTvCreatTime;
        TextView mTvMarkNmae;
        TextView mTvMarkPage;
    }
}
