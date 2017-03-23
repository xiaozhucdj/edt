package com.inkscreen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inkscreen.model.SortInfo;
import com.inkscreen.will.utils.widget.ArrayListAdapter;
import com.yougy.ui.activity.R;


/**
 * Created by xcz on 2016/11/16.
 */
public class MyChidrenAdapter extends ArrayListAdapter<SortInfo.Ret.TEXTBOOK.Section.Children> {

    private boolean childSelect;

    public MyChidrenAdapter(Context context) {
        super(context);
    }

    public void setChildSelect(boolean childSelect) {
        this.childSelect = childSelect;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            row = inflater.inflate(R.layout.soft_item, null);

            holder = new ViewHolder(row);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        SortInfo.Ret.TEXTBOOK.Section.Children workInfo = mList.get(position);

        holder.textBook.setText(workInfo.getName() + "(" + workInfo.getFallibleCount() + ")");

        if (workInfo.isSeTag()) {
            if (childSelect) {
                holder.sectionLayout.setBackgroundColor(mContext.getResources().getColor(R.color.zl_color_bbbbb));

                holder.textBook.setTextColor(mContext.getResources().getColor(R.color.zl_black));
            } else {
                holder.sectionLayout.setBackgroundColor(mContext.getResources().getColor(R.color.zl_black));
                holder.textBook.setTextColor(mContext.getResources().getColor(R.color.zl_white));
            }
        } else {
            holder.sectionLayout.setBackgroundColor(mContext.getResources().getColor(R.color.zl_color_e5));

            holder.textBook.setTextColor(mContext.getResources().getColor(R.color.zl_black));
        }

        return row;
    }

    public void reSetStatus() {
        if (mList != null) {
            for(int index=0;index<mList.size();index++)
            {
                mList.get(index).setSeTag(false);
            }
        }

    }
    static class ViewHolder {
        TextView textBook;
        LinearLayout sectionLayout;


        public ViewHolder(View row) {
            row.findViewById(R.id.count_id).setVisibility(View.GONE);
            textBook = (TextView) row.findViewById(R.id.bookname_id);
            sectionLayout = (LinearLayout) row.findViewById(R.id.section_bg_id);


        }
    }


}
