package com.inkscreen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inkscreen.model.Subject;
import com.inkscreen.will.utils.widget.ArrayListAdapter;
import com.yougy.ui.activity.R;


/**
 * Created by xcz on 2016/11/16.
 */
public class MyWorkReportAdapter extends ArrayListAdapter<Subject> {

    private View.OnClickListener itemclick;

    public MyWorkReportAdapter(Context context) {
        super(context);
    }

    public void setItemclick(View.OnClickListener itemclick) {
        this.itemclick = itemclick;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            row = inflater.inflate(R.layout.work_report_item, null);

            holder = new ViewHolder(row);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Subject workInfo = mList.get(position);
        holder.textTitle.setText(workInfo.getTopic());
//            if (workInfo.getResult().equals("WRONG")){
//                holder.imgWrong.setVisibility(View.VISIBLE);
//                holder.imgRight.setVisibility(View.INVISIBLE);
//            }else {
//                holder.imgWrong.setVisibility(View.INVISIBLE);
//                holder.imgRight.setVisibility(View.VISIBLE);
//
//            }


        if (null != workInfo.getResult() && !workInfo.getResult().equals("")) {
            if (workInfo.getResult().indexOf("H") > -1 && workInfo.getResult().indexOf("W") > -1) {

                holder.imgHalf.setVisibility(View.VISIBLE);
            } else if (null != workInfo.getResult() && !workInfo.getResult().equals("") && workInfo.getResult().equals("INIT")) {
                holder.imgRight.setVisibility(View.INVISIBLE);
                holder.imgWrong.setVisibility(View.INVISIBLE);
                holder.imgHalf.setVisibility(View.INVISIBLE);

            } else {
                holder.imgHalf.setVisibility(View.INVISIBLE);

                if (workInfo.getResult().indexOf("W") > -1) {
                    holder.imgWrong.setVisibility(View.VISIBLE);
                    holder.imgRight.setVisibility(View.INVISIBLE);
                } else {
                    holder.imgWrong.setVisibility(View.INVISIBLE);
                    holder.imgRight.setVisibility(View.VISIBLE);

                }

            }

        }


        if (workInfo.getEmendTag()) {
            holder.imgEmend.setVisibility(View.VISIBLE);
        } else {
            holder.imgEmend.setVisibility(View.GONE);
        }

        holder.textTitle.setTag(position);
        if (itemclick != null) {
            holder.textTitle.setOnClickListener(itemclick);
        }


//            holder.textTime.setText(workInfo.getWorkTime());
//            holder.textAmount.setText(workInfo.getWorkAmount());
        return row;
    }


    static class ViewHolder {
        TextView textTitle;
        ImageView imgRight;
        ImageView imgWrong;
        ImageView imgHalf;
        ImageView imgEmend;


        public ViewHolder(View row) {
            textTitle = (TextView) row.findViewById(R.id.report_id);
            imgRight = (ImageView) row.findViewById(R.id.right_id);
            imgWrong = (ImageView) row.findViewById(R.id.wrong_id);
            imgHalf = (ImageView) row.findViewById(R.id.halt_id);
            imgEmend = (ImageView) row.findViewById(R.id.emend_id);

        }
    }
}
