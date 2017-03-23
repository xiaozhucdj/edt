package com.inkscreen.adapter;

import android.app.Activity;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inkscreen.model.ChioceABCDInfo;
import com.inkscreen.utils.DeviceInfoUtil;
import com.inkscreen.utils.MyImageGetter;
import com.inkscreen.will.utils.widget.ArrayListAdapter;
import com.yougy.ui.activity.R;

/**
 * Created by xcz on 2016/11/16.
 */
public class MyChiocesAdapter extends ArrayListAdapter<ChioceABCDInfo> {
    Activity activity;
    private View parentView;

    // private Context mContext;
    public MyChiocesAdapter(Activity context) {
        super(context);
        // mContext = context;
        activity = context;
    }

    public View SetParentView(View parent) {
        return parentView = parent;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            row = inflater.inflate(R.layout.chioce_item, null);

            holder = new ViewHolder(row);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ChioceABCDInfo workInfo = mList.get(position);



        holder.textchioces.setText(FormateSpannedContent(holder.textchioces, workInfo.getChoiceContent(), DeviceInfoUtil.dip2px(activity, 100),  holder.relChioce));
//        holder.textchioces.setHtml(workInfo.getChoiceContent(), new HtmlHttpImageGetter(holder.textchioces,"",true));
        holder.textBook.setText(workInfo.getChoiceName());
        if (workInfo.isChoiceTag()) {
            holder.myChioce.setVisibility(View.VISIBLE);

            holder.relChioce.setBackgroundResource(R.drawable.choose_bg_c);
        } else {
            holder.myChioce.setVisibility(View.INVISIBLE);

            holder.relChioce.setBackgroundResource(R.drawable.choose_bg);

        }

        holder.relChioce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionListener != null) {

                    actionListener.RunAction(position);
                }
            }
        });


        return row;
    }

    public void setSelect(int position, LinearLayout parentView) {

        if (parentView == null) {
            return;
        }
        int count = parentView.getChildCount();
        for (int index = 0; index < count; index++) {

            View child = parentView.getChildAt(index);
            if (child == null) {
                continue;
            }
            if (index == position) {
                child.findViewById(R.id.myChioce_id).setVisibility(View.VISIBLE);

                child.findViewById(R.id.rel_chioce_id).setBackgroundResource(R.drawable.choose_bg_c);
            } else {
                child.findViewById(R.id.myChioce_id).setVisibility(View.INVISIBLE);

                child.findViewById(R.id.rel_chioce_id).setBackgroundResource(R.drawable.choose_bg);
            }
        }
    }

    static class ViewHolder {
        TextView textchioces;
        TextView textBook;
        TextView myChioce;
        LinearLayout relChioce;


        public ViewHolder(View row) {
            textchioces = (TextView) row.findViewById(R.id.chioces_text_id);
            textBook = (TextView) row.findViewById(R.id.choice_name_id);
            myChioce = (TextView) row.findViewById(R.id.myChioce_id);
            relChioce = (LinearLayout) row.findViewById(R.id.rel_chioce_id);
        }
    }

    private ActionListener actionListener;

    public void setChioceActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public interface ActionListener {
        public void RunAction(int position);
    }



    protected Spanned FormateSpannedContent(TextView view, String content, int borderWidth, View parentView) {
        MyImageGetter getter = new MyImageGetter(view, activity, borderWidth + 22, parentView);
        Spanned s = Html.fromHtml(content, getter, null);
        return s;


    }
}
