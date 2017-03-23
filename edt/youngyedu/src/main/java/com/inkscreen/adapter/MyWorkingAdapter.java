package com.inkscreen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inkscreen.model.WorkInfo;
import com.inkscreen.utils.MyTimeUtils;
import com.inkscreen.will.utils.widget.ArrayListAdapter;
import com.yougy.ui.activity.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xcz on 2016/11/16.
 */
public class MyWorkingAdapter extends ArrayListAdapter<WorkInfo.Ret.Items> {


    public MyWorkingAdapter(Context context) {
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

        final WorkInfo.Ret.Items workInfo = mList.get(position);
        holder.textTitle.setText(workInfo.getHomeWork().getName());

        holder.textTime.setText(MyTimeUtils.getHomeWorkStartTime(workInfo.getHomeWork().getStartTime())+"~"+MyTimeUtils.getHomeWorkDeadLineTime(workInfo.getHomeWork().getDeadline()));



//            SimpleDateFormat formatter = new   SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
//
//            if(DateUtils.getDateDetail(formatter.format(workInfo.getHomeWork().getDeadline()))!=null && DateUtils.getDateDetail(formatter.format(workInfo.getHomeWork().getDeadline())).equals("今天")) {
//
//                holder.textTime.setText("今天"+getdata(workInfo.getHomeWork().getDeadline()));
//            }else{
//                if(DateUtils.getDateDetail(formatter.format(workInfo.getHomeWork().getDeadline()))!=null && DateUtils.getDateDetail(formatter.format(workInfo.getHomeWork().getDeadline())).equals("明天")){
//
//                    holder.textTime.setText("明天"+getdata(workInfo.getHomeWork().getDeadline()));
//                }else {
//
//                    Calendar current = Calendar.getInstance();
//                    current.setTimeInMillis(System.currentTimeMillis());
//                    int nowYear = current.get(Calendar.YEAR);
//
//                    Calendar dead = Calendar.getInstance();
//                    dead.setTimeInMillis(workInfo.getHomeWork().getDeadline());
//                    int deadlineYear = dead.get(Calendar.YEAR);
//
//
//
//                    if (nowYear!=deadlineYear){
//                        holder.textTime.setText(""+getdata1(workInfo.getHomeWork().getStartTime()) + "~" + getdata1(workInfo.getHomeWork().getDeadline()));
//                    } else {
//
//                        holder.textTime.setText(""+getdata2(workInfo.getHomeWork().getStartTime())+"~"+getdata2(workInfo.getHomeWork().getDeadline()));
//                    }
//
//                }
//            }






       // holder.textTime.setText(workInfo.getHomeWork());
        if (workInfo.getHomeWork().getType() == 1){
            holder.textJia.setVisibility(View.GONE);
            holder.textAmount.setText("题目数量"+workInfo.getCompletionCount()+"/"+workInfo.getHomeWork().getQuestionCount());
        }else if (workInfo.getHomeWork().getType() == 2){
            holder.textJia.setVisibility(View.VISIBLE);
            holder.textAmount.setText("专项训练0/" + workInfo.getHomeWork().getQuestionCount());
        }


        return row;
    }

    static class ViewHolder {
        TextView textTitle;
        TextView textTime;
        TextView textAmount;
        TextView textJia;



        public ViewHolder(View row){
            textTitle = (TextView)row.findViewById(R.id.worktitle);
            textTime = (TextView)row.findViewById(R.id.worktime);
            textAmount = (TextView)row.findViewById(R.id.workamount);
            textJia = (TextView)row.findViewById(R.id.jia);

        }
    }


    public static String getdata(long data){

        //mill为你龙类型的时间戳
        Date date = new Date(data);
        String strs ="";
        try {
            //yyyy表示年MM表示月dd表示日
            //yyyy-MM-dd是日期的格式，比如2015-12-12如果你要得到2015年12月12日就换成yyyy年MM月dd日
            SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
            //进行格式化
            strs=sdf.format(date);
            //System.out.println(strs);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return strs;
    }
    public static String getdata1(long data){

        //mill为你龙类型的时间戳
        Date date = new Date(data);
        String strs ="";
        try {
            //yyyy表示年MM表示月dd表示日
            //yyyy-MM-dd是日期的格式，比如2015-12-12如果你要得到2015年12月12日就换成yyyy年MM月dd日
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
            //进行格式化
            strs=sdf.format(date);
            //System.out.println(strs);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return strs;
    }


    public static String getdata2(long data){

        //mill为你龙类型的时间戳
        Date date = new Date(data);
        String strs ="";
        try {
            //yyyy表示年MM表示月dd表示日
            //yyyy-MM-dd是日期的格式，比如2015-12-12如果你要得到2015年12月12日就换成yyyy年MM月dd日
            SimpleDateFormat sdf=new SimpleDateFormat("MM-dd HH:mm");
            //进行格式化
            strs=sdf.format(date);
            //System.out.println(strs);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return strs;
    }

    public static String getdata3(long data){

        //mill为你龙类型的时间戳
        Date date = new Date(data);
        String strs ="";
        try {
            //yyyy表示年MM表示月dd表示日
            //yyyy-MM-dd是日期的格式，比如2015-12-12如果你要得到2015年12月12日就换成yyyy年MM月dd日
            SimpleDateFormat sdf=new SimpleDateFormat("MM-dd");
            //进行格式化
            strs=sdf.format(date);
            //System.out.println(strs);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return strs;
    }

}
