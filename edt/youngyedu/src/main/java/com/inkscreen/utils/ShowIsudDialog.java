package com.inkscreen.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.inkscreen.MyHaslsudActivity;
import com.inkscreen.model.Event;
import com.yougy.ui.activity.R;

import de.greenrobot.event.EventBus;

/**
 * Created by xcz on 2017/1/9.
 */
public class ShowIsudDialog {

    public static void showTimeDialog(final Activity context,final String title, final String homeWorkId,final int code,final String ret_msg) {

        final Dialog dialog = new Dialog(context, R.style.MyCustomDialog);
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialoghasisud, null);
        dialog.setContentView(contentView);
        dialog.setCancelable(false);
        Button sureBtn = (Button)contentView.findViewById(R.id.sureBtn);
        TextView textView = (TextView)contentView.findViewById(R.id.linetime_id);
        TextView textBewrite = (TextView)(TextView)contentView.findViewById(R.id.miaoshu_id);


        if (code == 1854){
            textView.setText("提交成功");
            textBewrite.setText(""+ret_msg);

        }else if(code == 1856 || code == 1855){
            textView.setText("作业已被提交");
            textBewrite.setText(""+ret_msg);
        }

        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (code == 1854){
                    Intent intent = new Intent();
                    intent.putExtra("homeWorkId",homeWorkId);
                    intent.putExtra("title", title);
                    intent.setClass(context, MyHaslsudActivity.class);
                    context.startActivity(intent);
                    EventBus.getDefault().post(new Event<>(1));
                }else if (code == 1856 || code == 1855){
                    EventBus.getDefault().post(new Event<>(1));
                }

                context.finish();

            }
        });

        dialog.show();
    }




}
