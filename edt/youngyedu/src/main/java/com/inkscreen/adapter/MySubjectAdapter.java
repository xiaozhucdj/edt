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
public class MySubjectAdapter extends ArrayListAdapter<Subject> {

    private View.OnClickListener itemclick;
    Context mContext;
    public MySubjectAdapter(Context context) {
        super(context);
        mContext = context;
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
            row = inflater.inflate(R.layout.subject_item, null);

            holder = new ViewHolder(row);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final Subject subject = mList.get(position);
            holder.textTitle.setText(subject.getTopic());

        if(subject.getTitelflag() == true) {
            holder.imageView.setVisibility(View.VISIBLE);
//            if (!subject.getWriteorblacktag() == true){
//                holder.textTitle.setBackgroundResource(R.drawable.black_shape);
//            }else {
//                holder.textTitle.setBackgroundResource(R.drawable.subject_big_shape);
//
//            }

          //  holder.textTitle.setTextColor(mContext.getResources().getColor(R.color.zl_white));
        }else{
            holder.imageView.setVisibility(View.INVISIBLE);

          //  holder.textTitle.setTextColor(mContext.getResources().getColor(R.color.zl_black));
        }

        if (subject.getDoflag()){
            holder.textTitle.setTextColor(mContext.getResources().getColor(R.color.zl_white));
            holder.textTitle.setBackgroundResource(R.drawable.black_shape);
        }else {
            holder.textTitle.setBackgroundResource(R.drawable.subject_big_shape);
            holder.textTitle.setTextColor(mContext.getResources().getColor(R.color.zl_black));
        }

        if (subject.getEmendTag()){
            holder.imgEmend.setVisibility(View.VISIBLE);
        }else {
            holder.imgEmend.setVisibility(View.GONE);
        }
        holder.textTitle.setTag(position);
        if(itemclick!=null)
        {
            holder.textTitle.setOnClickListener(itemclick);
        }
            return row;
    }

    static class ViewHolder {
        TextView textTitle;
        ImageView imageView;
        ImageView imgEmend;

        public ViewHolder(View row){
            textTitle = (TextView)row.findViewById(R.id.subjecttitle);
            imageView = (ImageView)row.findViewById(R.id.image_id);
            imgEmend = (ImageView)row.findViewById(R.id.emend_id);
        }
    }
}
