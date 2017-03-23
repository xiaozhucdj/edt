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
public class MySubjectHasAdapter extends ArrayListAdapter<Subject> {

    private View.OnClickListener itemclick;
    public MySubjectHasAdapter(Context context) {
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
            row = inflater.inflate(R.layout.subject_has_item, null);

            holder = new ViewHolder(row);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final Subject subject = mList.get(position);
            holder.textTitle.setText(subject.getTopic());

        if(subject.getTitelflag() == true) {
            holder.imageView.setVisibility(View.VISIBLE);
            if (!subject.getWriteorblacktag() == true){
                holder.textTitle.setBackgroundResource(R.drawable.black_shape);
            }else {
                holder.textTitle.setBackgroundResource(R.drawable.subject_big_shape);

            }


        }else{
            holder.imageView.setVisibility(View.INVISIBLE);
        }
//
//        if (subject.getResult().equals("WRONG")){
//            holder.imgWrong.setVisibility(View.VISIBLE);
//            holder.imgRight.setVisibility(View.INVISIBLE);
//        }else {
//            holder.imgWrong.setVisibility(View.INVISIBLE);
//            holder.imgRight.setVisibility(View.VISIBLE);
//
//        }


        if (subject.getEmendTag()){
            holder.imgEmend.setVisibility(View.VISIBLE);
        }else {
            holder.imgEmend.setVisibility(View.GONE);
        }


        if (null != subject.getResult() && !subject.getResult().equals("")) {
            if (subject.getResult().indexOf("H") > -1 && subject.getResult().indexOf("W") > -1) {

                holder.imgHalf.setVisibility(View.VISIBLE);
            } else if (null != subject.getResult() && !subject.getResult().equals("") && subject.getResult().equals("INIT")){
                holder.imgRight.setVisibility(View.INVISIBLE);
                holder.imgWrong.setVisibility(View.INVISIBLE);
                holder.imgHalf.setVisibility(View.INVISIBLE);

            }else {
                holder.imgHalf.setVisibility(View.INVISIBLE);

                if (subject.getResult().indexOf("W") > -1) {
                    holder.imgWrong.setVisibility(View.VISIBLE);
                    holder.imgRight.setVisibility(View.INVISIBLE);
                } else {
                    holder.imgWrong.setVisibility(View.INVISIBLE);
                    holder.imgRight.setVisibility(View.VISIBLE);

                }

            }

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
        ImageView imgRight;
        ImageView imgWrong;
        ImageView imgHalf;
        ImageView imgEmend;

        public ViewHolder(View row){
            textTitle = (TextView)row.findViewById(R.id.subjecttitle);
            imageView = (ImageView)row.findViewById(R.id.image_id);
            imgRight = (ImageView)row.findViewById(R.id.right_id);
            imgWrong = (ImageView)row.findViewById(R.id.wrong_id);
            imgHalf = (ImageView)row.findViewById(R.id.halt_id);
            imgEmend = (ImageView)row.findViewById(R.id.emend_id);
        }
    }
}
