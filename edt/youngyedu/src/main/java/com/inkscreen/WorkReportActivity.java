package com.inkscreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inkscreen.adapter.MyWorkReportAdapter;
import com.inkscreen.model.Subject;
import com.inkscreen.model.TobeInfo;
import com.inkscreen.will.utils.widget.MultiGridView;
import com.yougy.ui.activity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xcz on 2016/11/18.
 */
public class WorkReportActivity extends Activity {
    MultiGridView mReportGV;
    MyWorkReportAdapter myWorkReportAdapter;
    List<Subject> mWorkList;
    TobeInfo.Ret myTobeInfo;
    TextView textRorrer, textRightRate, textRateTitle, textCount, textTime;
    List<TobeInfo.Ret.Questions> mTobeList;
    String title;
    ImageView imgCancel;
    TextView textRank;
    LinearLayout layoutNodate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workreport);

        Intent intent = this.getIntent();
        myTobeInfo = (TobeInfo.Ret) intent.getSerializableExtra("myTobeInfo");
        title = (String) intent.getStringExtra("title");

        initView();


        if (myTobeInfo != null && myTobeInfo.getQuestions() != null && myTobeInfo.getQuestions().size() > 0) {
            layoutNodate.setVisibility(View.VISIBLE);
        } else {
            layoutNodate.setVisibility(View.GONE);
        }

//        LoadDataAsyncTask asyncTask = new LoadDataAsyncTask();
//        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void initView() {

        mTobeList = new ArrayList<>();
        layoutNodate = (LinearLayout) findViewById(R.id.nodate_id);
        mReportGV = (MultiGridView) findViewById(R.id.workrep_id);
        textRorrer = (TextView) findViewById(R.id.properLV_id);
        textRightRate = (TextView) findViewById(R.id.rightrate_id);
        textRateTitle = (TextView) findViewById(R.id.rate_title_id);
        textCount = (TextView) findViewById(R.id.count_id);
        textTime = (TextView) findViewById(R.id.rate_time_id);
        imgCancel = (ImageView) findViewById(R.id.cancel_id);
        textRank = (TextView) findViewById(R.id.rank_id);

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mWorkList = new ArrayList<>();
        myWorkReportAdapter = new MyWorkReportAdapter(this);
        myWorkReportAdapter.setList(new ArrayList<Subject>());
        myWorkReportAdapter.setItemclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getTag()!=null)
                {
                    int position= (int) v.getTag();
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    intent.putExtras(bundle);
                    WorkReportActivity.this.setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        mReportGV.setAdapter(myWorkReportAdapter);
        if (myTobeInfo != null) {
            if (myTobeInfo.getStudentHomework().getRank() == 0) {
                textRank.setText("班级第" + "--" + "名");
            } else {
                textRank.setText("班级第" + myTobeInfo.getStudentHomework().getRank() + "名");
            }


            if (myTobeInfo.getStudentHomework().getCompletionRate() <= 0 || TextUtils.isEmpty(myTobeInfo.getStudentHomework().getRightRateTitle())) {

                textRightRate.setText("--");
            } else {
                textRightRate.setText(myTobeInfo.getStudentHomework().getRightRateTitle());
            }

//            textTime.setText("用时:"+myTobeInfo.getStudentHomework().getHomeworkTime());
            if (myTobeInfo.getStudentHomework().getHomeworkTime() == 0) {
                textTime.setText("用时 --");
            } else {
                textTime.setText("用时  " + (myTobeInfo.getStudentHomework().getHomeworkTime() % 3600 / 60) + "'" + (myTobeInfo.getStudentHomework().getHomeworkTime() % 60) + "''");
            }


            mTobeList = myTobeInfo.getQuestions();
            int w = 1;
            for (int i = 0; i < mTobeList.size(); i++) {
                Subject subject = new Subject();

                if (mTobeList.get(i).isCorrectQuestion()) {
                    subject.setTopic("" + w);
                    subject.setEmendTag(true);
                    w++;
                } else {
                    subject.setTopic("" + (i + 1));
                    subject.setEmendTag(false);

                }


//                StringBuffer stringBuffer = new StringBuffer();
//                for (int j=0;j<mTobeList.get(i).getStudentHomeworkAnswers().size();j++){
//
//                    stringBuffer.append(mTobeList.get(i).getStudentHomeworkAnswers().get(j).getResult());
//                }
//                subject.setResult(stringBuffer.toString());
//                if (!mTobeList.get(i).getType().equals("QUESTION_ANSWERING")){
//                    StringBuffer stringBuffer = new StringBuffer();
//                    for (int j=0;j<mTobeList.get(i).getStudentHomeworkAnswers().size();j++){
//
//                        stringBuffer.append(mTobeList.get(i).getStudentHomeworkAnswers().get(j).getResult());
//
//                    }
//                    subject.setResult(stringBuffer.toString());
//                }else {

//                    if (null != mTobeList.get(i).getStudentHomeworkQuestion().getRightRate()) {
//
//                        if (mTobeList.get(i).getStudentHomeworkQuestion().getRightRate() == 0) {
//                            // textRult.setText("WRONG");
//                            subject.setResult("WRONG");
//                        } else if (mTobeList.get(i).getStudentHomeworkQuestion().getRightRate() == 100) {
//                            //textRult.setText("RIGHT");
//                            subject.setResult("RIGHT");
//                        } else {
//                            //textRult.setText(mTobeList.get(i).getStudentHomeworkQuestion().getRightRate()+"%正确");
//                            subject.setResult("WRONGRIGHT");
//                        }
//                    }


                //               }


                if (mTobeList.get(i).getType().equals("SINGLE_CHOICE")) {

                    if ("WRONG".equals(mTobeList.get(i).getStudentHomeworkQuestion().getResult())) {
                        subject.setResult("WRONG");
                    } else if ("RIGHT".equals(mTobeList.get(i).getStudentHomeworkQuestion().getResult())) {
                        subject.setResult("RIGHT");
                    } else {
                        subject.setResult("INIT");

                    }
                } else {

                    if ("WRONG".equals(mTobeList.get(i).getStudentHomeworkQuestion().getResult())) {


                        if (mTobeList.get(i).getStudentHomeworkQuestion().getRightRate() == 0 || TextUtils.isEmpty(mTobeList.get(i).getStudentHomeworkQuestion().getRightRateTitle())) {
                            subject.setResult("WRONG");
                        } else {
                            //textRult.setText(TextUtils.isEmpty(mTobeList.get(i).getStudentHomeworkQuestion().getRightRateTitle() )+ "正确");

                            subject.setResult("WRONGRIGHT");
                        }
                    } else if ("RIGHT".equals(mTobeList.get(i).getStudentHomeworkQuestion().getResult())) {

                        subject.setResult("RIGHT");
                    } else {
                        //隱藏
                        // layoutResult.setVisibility(View.GONE);
                        subject.setResult("INIT");
                    }


                }


                mWorkList.add(subject);

            }


            textRateTitle.setText(title);
            //textCount.setText("题目总数:" + mTobeList.size());
            int m = 0;
            for (int i = 0; i < mTobeList.size(); i++) {
                if (!mTobeList.get(i).isCorrectQuestion()) {
                    m++;
                }
            }
            textCount.setText("题目总数 " + m);

            myWorkReportAdapter.getList().clear();
            myWorkReportAdapter.getList().addAll(mWorkList);
            myWorkReportAdapter.notifyDataSetChanged();

        }


//        mReportGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                Intent intent = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putInt("position", position);
//                intent.putExtras(bundle);
//                WorkReportActivity.this.setResult(RESULT_OK, intent);
//                finish();
//
//            }
//        });
    }


//    private class LoadDataAsyncTask extends AsyncTask<Integer, Integer, List<Subject>> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected List<Subject> doInBackground(Integer... params) {
//            for (int i = 0; i < 12; i++) {
//
//                Subject subject = new Subject();
//                subject.setTopic("" + i);
//
//                mWorkList.add(subject);
//            }
//            return mWorkList;
//        }
//
//        @Override
//        protected void onPostExecute(List<Subject> subjects) {
//            super.onPostExecute(subjects);
//
//
//        }
//    }


}
