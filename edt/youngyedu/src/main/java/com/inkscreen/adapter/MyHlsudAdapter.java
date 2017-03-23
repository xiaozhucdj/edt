package com.inkscreen.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inkscreen.model.ChioceABCDInfo;
import com.inkscreen.utils.DeviceInfoUtil;
import com.inkscreen.utils.MyImageGetter;
import com.inkscreen.will.utils.widget.ArrayListAdapter;
import com.yougy.ui.activity.R;


/**已下发作业
 * Created by xcz on 2016/11/16.
 */
public class MyHlsudAdapter extends ArrayListAdapter<ChioceABCDInfo> {
    Activity activity;
   // private Context mContext;
    private View parentView;
    public MyHlsudAdapter(Context context) {
        super(context);
       // mContext = context;
        activity = (Activity) context;
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
            row = inflater.inflate(R.layout.haslsud_item, null);

            holder = new ViewHolder(row);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final ChioceABCDInfo workInfo = mList.get(position);




        holder.textchioces.setText(FormateSpannedContent(holder.textchioces,workInfo.getChoiceContent(), DeviceInfoUtil.dip2px(activity,150),holder.relChioce));
        holder.textBook.setText(workInfo.getChoiceName());
        if (workInfo.isRightTag()){
            holder.myChioce.setVisibility(View.VISIBLE);

            holder.relChioce.setBackgroundResource(R.drawable.choose_bg_c);
        }else {
            holder.myChioce.setVisibility(View.GONE);
            holder.relChioce.setBackgroundResource(R.drawable.choose_bg);

        }


        if (workInfo.isChoiceTag()){
            holder.imgRight.setVisibility(View.VISIBLE);

        }else {
            holder.imgRight.setVisibility(View.GONE);
        }

//        holder.relChioce.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(actionListener!=null){
//
//                    actionListener.RunAction(position);
//                }
//            }
//        });



        return row;
    }

    static class ViewHolder {
        TextView textchioces;
        TextView textBook;
        TextView myChioce;
        LinearLayout relChioce;
        ImageView imgRight;



        public ViewHolder(View row){
            textchioces = (TextView)row.findViewById(R.id.chioces_text_id);
            textBook = (TextView)row.findViewById(R.id.choice_name_id);
            myChioce = (TextView)row.findViewById(R.id.myChioce_id);
            relChioce = (LinearLayout)row.findViewById(R.id.rel_chioce_id);
            imgRight = (ImageView)row.findViewById(R.id.img_right_id);

        }
    }

    private ActionListener actionListener;

    public void setChioceActionListener(ActionListener actionListener){
        this.actionListener = actionListener;
    }

    public interface ActionListener{
        public void RunAction(int position);
    }

    protected Spanned FormateSpannedContent(TextView view, String content, int borderWidth,View parentView) {
        MyImageGetter getter = new MyImageGetter(view, activity, borderWidth+22,parentView);
        Spanned s = Html.fromHtml(content, getter, null );
        return s;


    }
}
