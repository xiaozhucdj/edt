package com.inkscreen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inkscreen.model.LeaveInfo;
import com.inkscreen.will.utils.widget.ArrayListAdapter;
import com.yougy.ui.activity.R;


/**
 * Created by xcz on 2016/11/16.
 */
public class MyLeaveAdapter extends ArrayListAdapter<LeaveInfo> {


    public MyLeaveAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            row = inflater.inflate(R.layout.leave_item, null);

            holder = new ViewHolder(row);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final LeaveInfo leaveInfo = mList.get(position);
            holder.textTitle.setText(leaveInfo.getPctAge());

            return row;
    }

    static class ViewHolder {
        TextView textTitle;
        ImageView imageView;

        public ViewHolder(View row){
            textTitle = (TextView)row.findViewById(R.id.percentage_id);
          //  imageView = (ImageView)row.findViewById(R.id.image_id);
        }
    }
}
