package com.inkscreen;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inkscreen.adapter.MySubjectAdapter;
import com.inkscreen.model.Subject;
import com.inkscreen.will.utils.widget.MultiGridView;
import com.inkscreen.will.utils.widget.TuyaView;
import com.yougy.ui.activity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xcz on 2016/11/16.
 */
public class TopicActivity extends Activity {
    MultiGridView mSubjectGV;
    MySubjectAdapter mySubjectAdapter;
    List<Subject> mSubjectList;
    TextView textTop,textNext;
    TuyaView linePathView;
    TextView textclean,textsave,readtext;
    LinearLayout linearLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        textTop = (TextView)findViewById(R.id.top_id);
        textNext = (TextView)findViewById(R.id.next_work);
        readtext = (TextView)findViewById(R.id.read_id);
        mSubjectGV = (MultiGridView) findViewById(R.id.subject_id);
         linearLayout = (LinearLayout)findViewById(R.id.layout_id);
        //linePathView = (TuyaView) findViewById(R.id.view);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        linePathView = new TuyaView(this, dm.widthPixels, dm.heightPixels);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        lp.setMargins(10, 10, 10, 10);
//        linePathView.setLayoutParams(lp);
//        linearLayout.addView(linePathView);
        textclean = (TextView)findViewById(R.id.clear1);
        textsave = (TextView)findViewById(R.id.save1);

        mSubjectList = new ArrayList<>();
        mySubjectAdapter = new MySubjectAdapter(this);
        mySubjectAdapter.setList(new ArrayList<Subject>());
        mSubjectGV.setAdapter(mySubjectAdapter);
        textTop.setOnClickListener(new MyTopicClickListener());
        textNext.setOnClickListener(new MyTopicClickListener());
        mSubjectGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mSubjectList != null && mSubjectList.size() > 0) {
                    for (int i = 0; i < mSubjectList.size(); i++) {
                        if (position == i) {
                            mSubjectList.get(i).setTitelflag(true);
                        } else {
                            mSubjectList.get(i).setTitelflag(false);
                        }
                        mySubjectAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        textclean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linePathView.redo();
            }
        });

        textsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (linePathView.getTouched()) {
//                    try {
//                        linePathView.save("/sdcard/qm.png", true, 10);
//                        //setResult(100);
//                        finish();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//
//                    Toast.makeText(TopicActivity.this, "您没有签名~", Toast.LENGTH_SHORT).show();
//                }
 //               linePathView.undo();
                try {
                    linePathView.saveToSDCard();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        readtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  linePathView.read();
            }
        });

      //  linePathView.setBackColor(getResources().getColor(R.color.zl_highlight));
        LoadDataAsyncTask asyncTask = new LoadDataAsyncTask();
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    class MyTopicClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.top_id:
                    if (mSubjectList != null && mSubjectList.size() > 0) {
                        for (int i = 0; i < mSubjectList.size(); i++) {
                            if (mSubjectList.get(i).getTitelflag()  == true  && i!=0) {
                                mSubjectList.get(i).setTitelflag(false);
                                mSubjectList.get(i-1).setTitelflag(true);
                                break;
                            } else {
                             //   mSubjectList.get(i).setTitelflag(false);
                            }
                        }
                        mySubjectAdapter.notifyDataSetChanged();
                    }
                    break;

                case R.id.next_work:

                    if (mSubjectList != null && mSubjectList.size() > 0) {

                        int m = -1;
                        for (int i = 0; i < mSubjectList.size(); i++) {
                            if (mSubjectList.get(i).getTitelflag() == true && i!=mSubjectList.size()-1){
                                mSubjectList.get(i).setTitelflag(false);
                                m = i+1;
                                break;
                            }
                        }
                        for (int j =0;j< mSubjectList.size();j++){
                            if (m == j){
                                mSubjectList.get(j).setTitelflag(true);
                                m = -1;
                                break;
                            }
                        }
                        mySubjectAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    }
    private class LoadDataAsyncTask extends AsyncTask<Integer, Integer, List<Subject>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Subject> doInBackground(Integer... params) {
            for (int i = 0; i < 12; i++) {

                Subject subject = new Subject();
                subject.setTopic("" + i);
                if (i == 0) {
                    subject.setTitelflag(true);
                } else {
                    subject.setTitelflag(false);
                }
                mSubjectList.add(subject);
            }
            return mSubjectList;
        }

        @Override
        protected void onPostExecute(List<Subject> subjects) {
            super.onPostExecute(subjects);
            mySubjectAdapter.getList().clear();
            mySubjectAdapter.getList().addAll(subjects);
            mySubjectAdapter.notifyDataSetChanged();

        }
    }
}
