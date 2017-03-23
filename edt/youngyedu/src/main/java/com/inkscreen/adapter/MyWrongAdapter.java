package com.inkscreen.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inkscreen.model.WrongInfo;
import com.inkscreen.utils.DateUtils;
import com.inkscreen.utils.DeviceInfoUtil;
import com.inkscreen.utils.MyImageGetter;
import com.inkscreen.will.utils.widget.ArrayListAdapter;
import com.yougy.ui.activity.R;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xcz on 2016/11/16.
 */
public class MyWrongAdapter extends ArrayListAdapter<WrongInfo.Ret.Items> {

    Activity activity;
    private View parentView;


    public MyWrongAdapter(Context context) {
        super(context);
        activity= (Activity) context;
    }

    public View SetParentView(View parent) {
        return parentView=parent;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            row = inflater.inflate(R.layout.wrong_item, null);

            holder = new ViewHolder(row);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final WrongInfo.Ret.Items wrongInfo = mList.get(position);

        //holder.textTitle.setHtml(wrongInfo.getQuestion().getContent(), new HtmlHttpImageGetter(holder.textTitle));
        holder.textTitle.setText(FormateSpannedContent(holder.textTitle, wrongInfo.getQuestion().getContent(), DeviceInfoUtil.dip2px(activity, 100), parentView));

        try {
            SimpleDateFormat formatter = new   SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
            Date curDate =  new Date(System.currentTimeMillis());
            String nowDate = formatter.format(curDate);
            String serverDate = formatter.format(wrongInfo.getUpdateAt());
            Date d1 = formatter.parse(nowDate);
            Date d2  = formatter.parse(serverDate);
            if(Math.abs(((d1.getTime() - d2.getTime())/(24*60*60*1000))) <=1){
                //holder.textTime.setText("今天"+MyWorkingAdapter.getdata(wrongInfo.getUpdateAt()));
                holder.textTime.setText("今天");
            }else {
                holder.textTime.setText(getdata(wrongInfo.getUpdateAt()));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        SimpleDateFormat formatter = new   SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");

        if(DateUtils.getDateDetail(formatter.format(wrongInfo.getUpdateAt()))!=null && DateUtils.getDateDetail(formatter.format(wrongInfo.getUpdateAt())).equals("今天")) {

            holder.textTime.setText("今天");
        }else{

                Calendar current = Calendar.getInstance();
                current.setTimeInMillis(System.currentTimeMillis());
                int nowYear = current.get(Calendar.YEAR);

                Calendar dead = Calendar.getInstance();
                dead.setTimeInMillis(wrongInfo.getUpdateAt());
                int deadlineYear = dead.get(Calendar.YEAR);



                if (nowYear!=deadlineYear){
                    holder.textTime.setText(""+getdata((wrongInfo.getUpdateAt())));
                } else {
                    holder.textTime.setText(""+MyWorkingAdapter.getdata3((wrongInfo.getUpdateAt())));
                }
        }






//            holder.textTime.setText(wrongInfo.getWorkTime());
//            holder.textAmount.setText(wrongInfo.getWorkAmount());

        holder.imgClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actionListener!=null){

                    actionListener.RunAction(position);
                }
            }
        });

        return row;
    }

    static class ViewHolder {
        HtmlTextView textTitle;
        TextView textTime;
        ImageView imgClick;


        public ViewHolder(View row){
            textTitle = (HtmlTextView)row.findViewById(R.id.tigan_id);
            textTime = (TextView)row.findViewById(R.id.time_id);
            imgClick = (ImageView)row.findViewById(R.id.imageclick);

        }
    }

    private ActionListener actionListener;

    public void setOnclickActionListener(ActionListener actionListener){
        this.actionListener = actionListener;
    }

    public interface ActionListener{
        public void RunAction(int position);
    }

    public static String getdata(long data){

        //mill为你龙类型的时间戳
        Date date = new Date(data);
        String strs ="";
        try {
            //yyyy表示年MM表示月dd表示日
            //yyyy-MM-dd是日期的格式，比如2015-12-12如果你要得到2015年12月12日就换成yyyy年MM月dd日
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            //进行格式化
            strs=sdf.format(date);
            //System.out.println(strs);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return strs;
    }


//    final Html.ImageGetter imageGetter = new Html.ImageGetter() {
//
//        public Drawable getDrawable(String source) {
//            Drawable drawable = null;
//            URL url;
//            try {
//                url = new URL(source);
//                drawable = Drawable.createFromStream(url.openStream(), "");
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//            drawable.setBounds(200, 200, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//            return drawable;
//        }
//    };


    protected Spanned FormateSpannedContent(TextView view, String content, int borderWidth,View parentView) {
        MyImageGetter getter = new MyImageGetter(view, activity, borderWidth+22,parentView);
        Spanned s = Html.fromHtml(content, getter, null);
        return s;


    }



}
