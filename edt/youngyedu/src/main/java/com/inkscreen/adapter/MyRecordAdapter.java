package com.inkscreen.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inkscreen.model.RecordInfo;
import com.inkscreen.utils.MyTimeUtils;
import com.inkscreen.will.utils.widget.ArrayListAdapter;
import com.yougy.ui.activity.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xcz on 2016/11/16.
 */
public class MyRecordAdapter extends ArrayListAdapter<RecordInfo.Ret.Items> {
    private static final String HOMEWORK_STATUS_ISSUED = "ISSUED";

    // private Context mContext;
    public MyRecordAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            row = inflater.inflate(R.layout.working_item, null);

            holder = new ViewHolder(row);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final RecordInfo.Ret.Items workInfo = mList.get(position);
        holder.textTitle.setText(workInfo.getHomeWork().getName());

        holder.textAmount.setVisibility(View.INVISIBLE);

        if (workInfo.getHomeWork().getType() == 1) {
            holder.textJia.setVisibility(View.GONE);

        } else if (workInfo.getHomeWork().getType() == 2) {
            holder.textJia.setVisibility(View.VISIBLE);

        }

        holder.textStatusCode.setVisibility(View.VISIBLE);
        if (HOMEWORK_STATUS_ISSUED.equals(workInfo.getHomeWork().getStatus())) {
            holder.textStatusCode.setText("已下发");
            holder.textAmount.setVisibility(View.VISIBLE);
            if (workInfo.getCompletionRate() > 0 && !TextUtils.isEmpty(workInfo.getRightRateTitle())) {
                holder.textAmount.setText("正确率:" + workInfo.getRightRateTitle());

            } else {
                holder.textAmount.setText("正确率:--");
            }
        } else {
            holder.textStatusCode.setText("待下发");
        }


        holder.textTime.setText("" + MyTimeUtils.getHomeWorkStartTime(workInfo.getHomeWork().getStartTime()) + "~" + MyTimeUtils.getHomeWorkStartTime(workInfo.getHomeWork().getDeadline()));


        //  holder.textTime.setText(""+getdata(workInfo.getHomeWork().getStartTime())+"~"+getdata(workInfo.getHomeWork().getDeadline()));
//        Calendar current = Calendar.getInstance();
//        current.setTimeInMillis(System.currentTimeMillis());
//        int nowYear = current.get(Calendar.YEAR);
//
//        Calendar dead = Calendar.getInstance();
//        dead.setTimeInMillis(workInfo.getHomeWork().getDeadline());
//        int deadlineYear = dead.get(Calendar.YEAR);
//
//        Calendar dead1 = Calendar.getInstance();
//        dead1.setTimeInMillis(workInfo.getHomeWork().getDeadline());
//        int startlineYear = dead.get(Calendar.YEAR);
//        if (nowYear!=deadlineYear){
//            holder.textTime.setText("" + getdata1(workInfo.getHomeWork().getStartTime()) + "~" + getdata1(workInfo.getHomeWork().getDeadline()));
//        } else {
//
//            holder.textTime.setText(""+getdata2(workInfo.getHomeWork().getStartTime())+"~"+getdata2(workInfo.getHomeWork().getDeadline()));
//        }


        return row;
    }

    static class ViewHolder {
        TextView textTitle;
        TextView textTime;
        TextView textAmount;
        TextView textJia;
        TextView textStatusCode;

        public ViewHolder(View row) {
            textTitle = (TextView) row.findViewById(R.id.worktitle);
            textTime = (TextView) row.findViewById(R.id.worktime);
            textAmount = (TextView) row.findViewById(R.id.workamount);
            textJia = (TextView) row.findViewById(R.id.jia);
            textStatusCode = (TextView) row.findViewById(R.id.zhuangtai_id);

        }
    }


    public static String getdata(long data) {

        //mill为你龙类型的时间戳
        Date date = new Date(data);
        String strs = "";
        try {
            //yyyy表示年MM表示月dd表示日
            //yyyy-MM-dd是日期的格式，比如2015-12-12如果你要得到2015年12月12日就换成yyyy年MM月dd日
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //进行格式化
            strs = sdf.format(date);
            //System.out.println(strs);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return strs;
    }


    public static String getdata1(long data) {

        //mill为你龙类型的时间戳
        Date date = new Date(data);
        String strs = "";
        try {
            //yyyy表示年MM表示月dd表示日
            //yyyy-MM-dd是日期的格式，比如2015-12-12如果你要得到2015年12月12日就换成yyyy年MM月dd日
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            //进行格式化
            strs = sdf.format(date);
            //System.out.println(strs);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return strs;
    }


    public static String getdata2(long data) {

        //mill为你龙类型的时间戳
        Date date = new Date(data);
        String strs = "";
        try {
            //yyyy表示年MM表示月dd表示日
            //yyyy-MM-dd是日期的格式，比如2015-12-12如果你要得到2015年12月12日就换成yyyy年MM月dd日
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
            //进行格式化
            strs = sdf.format(date);
            //System.out.println(strs);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return strs;
    }


}
