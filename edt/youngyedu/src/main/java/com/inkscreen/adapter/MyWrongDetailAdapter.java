package com.inkscreen.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inkscreen.model.ChioceABCDInfo;
import com.inkscreen.utils.DeviceInfoUtil;
import com.inkscreen.utils.MyImageGetter;
import com.inkscreen.will.utils.widget.ArrayListAdapter;
import com.yougy.ui.activity.R;


/**
 * Created by xcz on 2016/11/16.
 */
public class MyWrongDetailAdapter extends ArrayListAdapter<ChioceABCDInfo> {

    Activity activity;
    private View parentView;
    public MyWrongDetailAdapter(Context context) {
        super(context);
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
            row = inflater.inflate(R.layout.chiocedetail_item, null);

            holder = new ViewHolder(row);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final ChioceABCDInfo workInfo = mList.get(position);
        holder.textchioces.setText(FormateSpannedContent( holder.textchioces,workInfo.getChoiceContent(), DeviceInfoUtil.dip2px(activity, 100),parentView));

        holder.textBook.setText(workInfo.getChoiceName());
//        if (workInfo.isChoiceTag()){
//            holder.myChioce.setVisibility(View.VISIBLE);
//
//        }else {
//            holder.myChioce.setVisibility(View.INVISIBLE);
//        }

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
        RelativeLayout relChioce;



        public ViewHolder(View row){
            textchioces = (TextView)row.findViewById(R.id.chioces_text_id);
            textBook = (TextView)row.findViewById(R.id.choice_name_id);
            myChioce = (TextView)row.findViewById(R.id.myChioce_id);
            //relChioce = (RelativeLayout)row.findViewById(R.id.rel_chioce_id);
        }
    }
//
//    private ActionListener actionListener;
//
//    public void setChioceActionListener(ActionListener actionListener){
//        this.actionListener = actionListener;
//    }
//
//    public interface ActionListener{
//        public void RunAction(int position);
//    }
protected Spanned FormateSpannedContent(TextView view, String content, int borderWidth,View parentView) {
    MyImageGetter getter = new MyImageGetter(view, activity, borderWidth+22,parentView);
    Spanned s = Html.fromHtml(content, getter, null);
    return s;


}

}
