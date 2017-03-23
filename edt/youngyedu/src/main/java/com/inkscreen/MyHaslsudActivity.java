package com.inkscreen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.inkscreen.adapter.MyHlsudAdapter;
import com.inkscreen.adapter.MySubjectHasAdapter;
import com.inkscreen.model.ChioceABCDInfo;
import com.inkscreen.model.Subject;
import com.inkscreen.model.TobeInfo;
import com.inkscreen.utils.AndroidUtils;
import com.inkscreen.utils.DeviceInfoUtil;
import com.inkscreen.utils.ImageLoadUtil;
import com.inkscreen.utils.LeApiApp;
import com.inkscreen.utils.LeApiResult;
import com.inkscreen.utils.LeApiUtils;
import com.inkscreen.utils.MyImageGetter;
import com.inkscreen.will.utils.widget.MultiGridView;
import com.yougy.ui.activity.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xcz on 2016/12/12.
 */
public class MyHaslsudActivity extends Activity {
    RadioButton radioTimu, radioMyRspd, radioSureRspd, radioAnalysis;

    LinearLayout linearTimu, linearMyRspd, linearSureRspd, linearAnalysis;

    RelativeLayout answerRL;

    private String homeWorkid, title;

    LinearLayout chioces_LV;
    MyHlsudAdapter myChiocesAdapter;
    List<ChioceABCDInfo> chiocesList;
    List<TobeInfo.Ret.Questions> mTobeList;
    TextView textTigan;
    TextView textVis;
    private int numbers = 0;
    private ImageView imgDati1, imgDati2;

    TextView textMyrespond, textpopTitle, answeredTime;
    TextView htmlSureRspd;
    TextView htmlTextAnalysis;
    Button topBtn, dwonBtn, tiJbutton, shouButton;
    List<Subject> mSubjectList;
    MultiGridView mSubjectGV;
    MySubjectHasAdapter mySubjectAdapter;
    LinearLayout answeredLY;
    PopupWindow myPopwindow;
    TextView workCount, textTitle, textRult;

    LinearLayout popLayout;
    ImageView reportBtn;
    private static final int REQUESTCODE = 1;
    private static final int RESULT_OK = -1;

    TobeInfo.Ret mytobeInfo;
    private int qsNumber = 0;
    //    boolean tag = false;
    Handler handler = new Handler();
    RelativeLayout relShow;
    ImageView imgBack;
    TextView textYeshu;
    ImageButton imgBtnLeft, imgBtnRight;
    FrameLayout fmshowNet;
    ImageView imgNet;
    LinearLayout frameZheZhao;
    FrameLayout loadingFm;
    LinearLayout layoutResult;
    TextView textPopCount;

    private View.OnClickListener nextButtonShowListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showTopAndDown();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myhaslsud);
        homeWorkid = getIntent().getStringExtra("homeWorkId");
        title = getIntent().getStringExtra("title");
        initView();
        initPopview();

        showTopAndDown();

        if (AndroidUtils.isNetworkAvailable(MyHaslsudActivity.this)) {
            fmshowNet.setVisibility(View.GONE);
            loadingFm.setVisibility(View.VISIBLE);
            getRecHomeWorkHaslsudApi();
        } else {
            fmshowNet.setVisibility(View.VISIBLE);
        }
    }

    private void showTopAndDown() {
        relShow.setVisibility(View.VISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                relShow.setVisibility(View.GONE);
            }
        }, 3000);


    }

    private void initView() {
        chiocesList = new ArrayList<>();
        mTobeList = new ArrayList<>();

        textTitle = (TextView) findViewById(R.id.title_id);
        textTitle.setText(title);
        //返回结果
        textRult = (TextView) findViewById(R.id.result_id);
        textYeshu = (TextView) findViewById(R.id.yeshu_id);
        radioTimu = (RadioButton) findViewById(R.id.radio_timu_id);
        radioMyRspd = (RadioButton) findViewById(R.id.radio_my_respond);
        radioSureRspd = (RadioButton) findViewById(R.id.radio_sure_respond);
        radioAnalysis = (RadioButton) findViewById(R.id.radio_analysis);

        imgBtnLeft = (ImageButton) findViewById(R.id.imgbutton_left_id);
        imgBtnRight = (ImageButton) findViewById(R.id.imgbutton_right_id);
        loadingFm = (FrameLayout) findViewById(R.id.loading_id);
        fmshowNet = (FrameLayout) findViewById(R.id.show_id);
        imgNet = (ImageView) findViewById(R.id.imgnet_id);


        frameZheZhao = (LinearLayout) findViewById(R.id.zhezhao_id);
        frameZheZhao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relShow.setVisibility(View.VISIBLE);
                showTopAndDown();
            }
        });


        layoutResult = (LinearLayout) findViewById(R.id.layoutresult_id);
        reportBtn = (ImageView) findViewById(R.id.report_btn_id);
        linearTimu = (LinearLayout) findViewById(R.id.linear_timu_id);
        linearMyRspd = (LinearLayout) findViewById(R.id.linear_my_respond);
        linearMyRspd.setOnClickListener(nextButtonShowListener);

        linearSureRspd = (LinearLayout) findViewById(R.id.linear_sure_respond);
        linearAnalysis = (LinearLayout) findViewById(R.id.linear_analysis);
        relShow = (RelativeLayout) findViewById(R.id.Rel_show_id);
        workCount = (TextView) findViewById(R.id.top_id);
        topBtn = (Button) findViewById(R.id.shang_ti);
        dwonBtn = (Button) findViewById(R.id.xia_ti);
        htmlTextAnalysis = (TextView) findViewById(R.id.textanalysis_id);
        htmlTextAnalysis.setMovementMethod(ScrollingMovementMethod.getInstance());
        htmlTextAnalysis.setOnClickListener(nextButtonShowListener);

        htmlSureRspd = (TextView) findViewById(R.id.html_surerespond);
        htmlSureRspd.setMovementMethod(ScrollingMovementMethod.getInstance());
        htmlSureRspd.setOnClickListener(nextButtonShowListener);


        imgDati1 = (ImageView) findViewById(R.id.img_dati1);
        imgDati2 = (ImageView) findViewById(R.id.img_dati2);
        imgDati1.setOnClickListener(nextButtonShowListener);
        imgDati2.setOnClickListener(nextButtonShowListener);

        textVis = (TextView) findViewById(R.id.textVis_id);
        textTigan = (TextView) findViewById(R.id.tigan_id);
        answerRL = (RelativeLayout) findViewById(R.id.rel_myanswer_id);
        chioces_LV = (LinearLayout) findViewById(R.id.choices_list_id);
        popLayout = (LinearLayout) findViewById(R.id.pop_layout_id);
        popLayout.setOnClickListener(new MyOnClickListener());
        imgBack = (ImageView) findViewById(R.id.cancel_id);
//            imgDati1.setOnClickListener(new MyOnClickListener());
//            imgDati2.setOnClickListener(new MyOnClickListener());
        imgBtnLeft.setOnClickListener(new MyOnClickListener());
        imgBtnRight.setOnClickListener(new MyOnClickListener());
        reportBtn.setOnClickListener(new MyOnClickListener());
        topBtn.setOnClickListener(new MyOnClickListener());
        dwonBtn.setOnClickListener(new MyOnClickListener());
        radioTimu.setOnClickListener(new MyOnClickListener());
        radioMyRspd.setOnClickListener(new MyOnClickListener());
        radioSureRspd.setOnClickListener(new MyOnClickListener());
        radioAnalysis.setOnClickListener(new MyOnClickListener());
        imgBack.setOnClickListener(new MyOnClickListener());
        linearTimu.setOnClickListener(new MyOnClickListener());
//        linearMyRspd.setOnClickListener(new MyOnClickListener());
//        linearSureRspd.setOnClickListener(new MyOnClickListener());
//        linearAnalysis.setOnClickListener(new MyOnClickListener());

        imgNet.setOnClickListener(new MyOnClickListener());

        myChiocesAdapter = new MyHlsudAdapter(MyHaslsudActivity.this);
        myChiocesAdapter.setList(new ArrayList<ChioceABCDInfo>());
        myChiocesAdapter.SetParentView(chioces_LV);
        //  chioces_LV.setAdapter(myChiocesAdapter);

        radioTimu.setChecked(true);
        radioTimu.setTextColor(getResources().getColor(R.color.hui));
        linearTimu.setVisibility(View.VISIBLE);
        answerRL.setVisibility(View.GONE);

//        chioces_LV.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_MOVE:
//                        return true;
//                    default:
//                        break;
//                }
//                return true;
//            }
//        });

    }


    private void getRecHomeWorkHaslsudApi() {

        Map<String, String> map = new HashMap<>();
        map.put("stuHkId", homeWorkid);

        LeApiUtils.postString(LeApiApp.getHomeWorkUrl(), map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (isFinishing()) {
                    return;
                }
                LeApiResult<TobeInfo> result = new LeApiResult<TobeInfo>(jsonObject, new TypeToken<TobeInfo>() {
                });

                if (result.getCode() == 0 && result.getResult() != null) {
                    topBtn.setEnabled(false);
                    topBtn.setTextColor(getResources().getColor(R.color.zl_user_point_text));

                    mytobeInfo = result.getResult().getRet();
                    mTobeList = result.getResult().getRet().getQuestions();
                    //循环加订正题

                    int workindex = 0;
                    int dzindex = 0;
                    for (int dindex = 0; dindex < mTobeList.size(); dindex++) {

                        if (mTobeList.get(dindex).isCorrectQuestion()) {
                            dzindex++;

                        } else {
                            workindex++;
                        }

                    }

                    workCount.setText((numbers + 1) + "/" + (dzindex > 0 ? workindex + "+" + dzindex : workindex));
                    textPopCount.setText((numbers + 1) + "/" + (dzindex > 0 ? workindex + "+" + dzindex : workindex));

                    refreshPop();
                    for (int i = 0; i < mTobeList.size(); i++) {
                        if (mTobeList.get(i).getType().equals("SINGLE_CHOICE")) {
                            htmlSureRspd.setVisibility(View.VISIBLE);

                            htmlSureRspd.setText(mTobeList.get(i).getAnswers().get(0).getContent());

                            if (mTobeList.get(i).isCorrectQuestion()) {
                                textTigan.setText(FormateSpannedContent(textTigan, "[订正题]" + mTobeList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                            } else {
                                textTigan.setText(FormateSpannedContent(textTigan, mTobeList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                            }


                            textVis.setVisibility(View.VISIBLE);

                            if ("WRONG".equals(mTobeList.get(i).getStudentHomeworkQuestion().getResult())) {
                                textRult.setText("回答错误");
                                layoutResult.setVisibility(View.VISIBLE);

                            } else if ("RIGHT".equals(mTobeList.get(i).getStudentHomeworkQuestion().getResult())) {
                                textRult.setText("回答正确");
                                layoutResult.setVisibility(View.VISIBLE);
                            } else {
                                layoutResult.setVisibility(View.GONE);

                            }


                            for (int j = 0; j < mTobeList.get(i).getChoices().length; j++) {
                                String myAnswer = mTobeList.get(i).getAnswers().get(0).getContent();
                                String rightAnswer = mTobeList.get(i).getStudentHomeworkAnswers().get(0).getContent();

                                ChioceABCDInfo chioceABCDInfo = new ChioceABCDInfo();
                                chioceABCDInfo.setChoiceContent(mTobeList.get(i).getChoices()[j]);
                                if (j == 0) {
                                    chioceABCDInfo.setChoiceName("A");
                                    if (myAnswer.equals("A")) {
                                        chioceABCDInfo.setChoiceTag(true);
                                    } else {
                                        chioceABCDInfo.setChoiceTag(false);

                                    }

                                    if (rightAnswer.equals("A")) {
                                        chioceABCDInfo.setRightTag(true);
                                    } else {
                                        chioceABCDInfo.setRightTag(false);
                                    }
                                } else if (j == 1) {
                                    chioceABCDInfo.setChoiceName("B");
                                    if (myAnswer.equals("B")) {
                                        chioceABCDInfo.setChoiceTag(true);
                                    } else {
                                        chioceABCDInfo.setChoiceTag(false);

                                    }

                                    if (rightAnswer.equals("B")) {
                                        chioceABCDInfo.setRightTag(true);
                                    } else {
                                        chioceABCDInfo.setRightTag(false);
                                    }

                                } else if (j == 2) {
                                    chioceABCDInfo.setChoiceName("C");
                                    if (myAnswer.equals("C")) {
                                        chioceABCDInfo.setChoiceTag(true);
                                    } else {
                                        chioceABCDInfo.setChoiceTag(false);

                                    }

                                    if (rightAnswer.equals("C")) {
                                        chioceABCDInfo.setRightTag(true);
                                    } else {
                                        chioceABCDInfo.setRightTag(false);
                                    }
                                } else if (j == 3) {
                                    chioceABCDInfo.setChoiceName("D");
                                    if (myAnswer.equals("D")) {
                                        chioceABCDInfo.setChoiceTag(true);
                                    } else {
                                        chioceABCDInfo.setChoiceTag(false);

                                    }

                                    if (rightAnswer.equals("D")) {
                                        chioceABCDInfo.setRightTag(true);
                                    } else {
                                        chioceABCDInfo.setRightTag(false);
                                    }
                                } else if (j == 4) {
                                    chioceABCDInfo.setChoiceName("E");
                                    if (myAnswer.equals("E")) {
                                        chioceABCDInfo.setChoiceTag(true);
                                    } else {
                                        chioceABCDInfo.setChoiceTag(false);

                                    }

                                    if (rightAnswer.equals("E")) {
                                        chioceABCDInfo.setRightTag(true);
                                    } else {
                                        chioceABCDInfo.setRightTag(false);
                                    }
                                } else if (j == 5) {
                                    chioceABCDInfo.setChoiceName("F");
                                    if (myAnswer.equals("F")) {
                                        chioceABCDInfo.setChoiceTag(true);
                                    } else {
                                        chioceABCDInfo.setChoiceTag(false);

                                    }

                                    if (rightAnswer.equals("F")) {
                                        chioceABCDInfo.setRightTag(true);
                                    } else {
                                        chioceABCDInfo.setRightTag(false);
                                    }
                                }
                                chiocesList.add(chioceABCDInfo);
                            }
                            textVis.setVisibility(View.VISIBLE);
                            chioces_LV.setVisibility(View.VISIBLE);

                            //numbers++;
                            myChiocesAdapter.getList().clear();
                            myChiocesAdapter.getList().addAll(chiocesList);
                            addCHooseItem();
                            myChiocesAdapter.notifyDataSetChanged();
//                            ImageLoaderManager.loadImageActivity(MyHaslsudActivity.this,
//                                    mTobeList.get(i).getStudentHomeworkQuestion().getSolvingImg(), R.drawable.ic_launcher, imgDati);


                            htmlTextAnalysis.setText(FormateSpannedContent(htmlTextAnalysis, mTobeList.get(i).getAnalysis(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                            imgDati1.setVisibility(View.GONE);
                            imgDati2.setVisibility(View.GONE);
                            List<String> answerImgs = mTobeList.get(i).getStudentHomeworkQuestion().getNotationAnswerImgs();
                            if (answerImgs == null || answerImgs.size() == 0) {
                                answerImgs = mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs();
                            }
                            if (answerImgs != null && answerImgs.size() > 0) {

                                if (answerImgs.size() == 2) {
                                    textYeshu.setText("1/2");
                                    imgDati1.setVisibility(View.VISIBLE);

                                    ImageLoadUtil.getInstance().loadImageActivity(MyHaslsudActivity.this,
                                            answerImgs.get(0), R.drawable.image_fail, imgDati1);
                                } else {

                                    if (answerImgs.size() == 1) {
                                        textYeshu.setText("1/1");
                                        imgDati1.setVisibility(View.VISIBLE);
                                        ImageLoadUtil.getInstance().loadImageActivity(MyHaslsudActivity.this,
                                                answerImgs.get(0), R.drawable.image_fail, imgDati1);

                                    } else {
                                        textYeshu.setText("1/1");
                                    }

                                }


                            } else {

                                textYeshu.setText("1/1");
                            }

                        } else {
                            //  numbers++;
                            //htmlTextAnalysis.setHtml(mTobeList.get(i).getAnalysis(), new HtmlHttpImageGetter(htmlTextAnalysis));
                            htmlTextAnalysis.setText(FormateSpannedContent(htmlTextAnalysis, mTobeList.get(i).getAnalysis(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                            if (mTobeList.get(i).isCorrectQuestion()) {
                                textTigan.setText(FormateSpannedContent(textTigan, "[订正题]" + mTobeList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                            } else {
                                textTigan.setText(FormateSpannedContent(textTigan, mTobeList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                            }

                            //textTigan.setHtml(mTobeList.get(i).getContent(), new HtmlHttpImageGetter(textTigan));
                            if ("WRONG".equals(mTobeList.get(i).getStudentHomeworkQuestion().getResult())) {
                                layoutResult.setVisibility(View.VISIBLE);

                                if (mTobeList.get(i).getStudentHomeworkQuestion().getRightRate() == 0 || TextUtils.isEmpty(mTobeList.get(i).getStudentHomeworkQuestion().getRightRateTitle())) {
                                    textRult.setText("回答错误");
                                } else {
                                    textRult.setText(mTobeList.get(i).getStudentHomeworkQuestion().getRightRateTitle() + "正确");
                                }
                            } else if ("RIGHT".equals(mTobeList.get(i).getStudentHomeworkQuestion().getResult())) {

                                layoutResult.setVisibility(View.VISIBLE);
                                textRult.setText("回答正确");
                            } else {
                                //隱藏
                                layoutResult.setVisibility(View.GONE);
                            }
//


                            StringBuffer stringBuffer = new StringBuffer();
//                            for (int k = 0; k < mTobeList.get(i).getAnswers().size(); k++) {
//                                stringBuffer.append((k + 1) + ":" + mTobeList.get(i).getAnswers().get(k).getContent() + "<br>");
//                            }

                            for (int k = 0; k < mTobeList.get(i).getAnswers().size(); k++) {
                                if (mTobeList.get(i).getAnswers().size() > 1) {
                                    stringBuffer.append((k + 1) + "," + mTobeList.get(i).getAnswers().get(k).getContent() + "<br>");
                                } else {
                                    stringBuffer.append(mTobeList.get(i).getAnswers().get(k).getContent() + "<br>");

                                }
                            }
                            htmlSureRspd.setText(FormateSpannedContent(htmlSureRspd, stringBuffer.toString(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
//                            htmlSureRspd.setHtml(stringBuffer.toString(), new HtmlHttpImageGetter(htmlSureRspd));
                            Log.i("bbb", mTobeList.get(i).getStudentHomeworkQuestion().getSolvingImg());
//                            ImageLoaderManager.loadImageActivity(MyHaslsudActivity.this,
//                                    mTobeList.get(i).getStudentHomeworkQuestion().getSolvingImg(), R.drawable.ic_launcher, imgDati);
                            textVis.setVisibility(View.GONE);
                            chioces_LV.setVisibility(View.GONE);
                            htmlSureRspd.setVisibility(View.VISIBLE);
                            imgDati1.setVisibility(View.GONE);
                            imgDati2.setVisibility(View.GONE);
                            List<String> answerImgs = mTobeList.get(i).getStudentHomeworkQuestion().getNotationAnswerImgs();
                            if (answerImgs == null || answerImgs.size() == 0) {
                                answerImgs = mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs();
                            }
                            if (answerImgs != null && answerImgs.size() > 0) {

                                if (answerImgs.size() == 2) {
                                    textYeshu.setText("1/2");
                                    imgDati1.setVisibility(View.VISIBLE);
                                    ImageLoadUtil.getInstance().loadImageActivity(MyHaslsudActivity.this,
                                            answerImgs.get(0), R.drawable.image_fail, imgDati1);
                                } else {

                                    if (answerImgs.size() == 1) {
                                        textYeshu.setText("1/1");
                                        imgDati1.setVisibility(View.VISIBLE);
                                        ImageLoadUtil.getInstance().loadImageActivity(MyHaslsudActivity.this,
                                                answerImgs.get(0), R.drawable.image_fail, imgDati1);

                                    } else {
                                        textYeshu.setText("1/1");
                                    }

                                }


                            } else {

                                textYeshu.setText("1/1");
                            }


                        }

                        break;

                    }
                    loadingFm.setVisibility(View.GONE);
                    reportBtn.setVisibility(View.VISIBLE);
                }


            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (isFinishing()) {
                    return;
                }
                loadingFm.setVisibility(View.GONE);
                fmshowNet.setVisibility(View.VISIBLE);
            }
        }, this);


    }


    private void initPopview() {
        View popupView = getLayoutInflater().inflate(R.layout.answer_pop, null);
        mSubjectGV = (MultiGridView) popupView.findViewById(R.id.subject_id);
        mSubjectList = new ArrayList<>();
        mySubjectAdapter = new MySubjectHasAdapter(this);
        mySubjectAdapter.setItemclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v.getTag() != null) {
                    myPopwindow.dismiss();
                    numbers = (int) v.getTag();
//                    tag = true;
                    getnextAndResutqs();
                }
            }
        });
        mySubjectAdapter.setList(new ArrayList<Subject>());
        mSubjectGV.setAdapter(mySubjectAdapter);
        tiJbutton = (Button) popupView.findViewById(R.id.tijiao_btn_id);
        shouButton = (Button) popupView.findViewById(R.id.shou_id);
        answeredLY = (LinearLayout) popupView.findViewById(R.id.answered_id);
        textpopTitle = (TextView) popupView.findViewById(R.id.ystime_id);
        textpopTitle.setText(title);
        shouButton.setOnClickListener(new MyOnClickListener());

        myPopwindow = new PopupWindow(popupView, 785, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        tiJbutton.setVisibility(View.INVISIBLE);
        answeredLY.setVisibility(View.INVISIBLE);
        myPopwindow.setTouchable(true);
        myPopwindow.setOutsideTouchable(true);
        myPopwindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));

        textPopCount = (TextView) popupView.findViewById(R.id.popcount_id);
        LinearLayout canCelLayout = (LinearLayout) popupView.findViewById(R.id.cancellayout_id);

        canCelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myPopwindow.dismiss();
            }
        });

//        mSubjectGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                myPopwindow.dismiss();
//                numbers = position;
//                topandownCount = position;
//                tag = true;
//                getnextAndResutqs();
//                // refreshPop();
//
//            }
//        });

    }

    private void refreshPop() {
        //刷新popWindow题目
        if (mSubjectList != null && mSubjectList.size() > 0) {
            mSubjectList.clear();

        }
        int m = 1;
        int w = 1;
        for (int i = 0; i < mTobeList.size(); i++) {

            Subject subject = new Subject();

            if (mTobeList.get(i).isCorrectQuestion()) {
                subject.setTopic("" + w);
                subject.setEmendTag(true);
                w++;
            } else {
                subject.setTopic("" + m);
                subject.setEmendTag(false);
                m++;
            }
            if (i == numbers) {
                subject.setTitelflag(true);
                subject.setWriteorblacktag(true);
            } else {
                subject.setWriteorblacktag(false);

                subject.setTitelflag(false);
            }


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


//            m++;
            mSubjectList.add(subject);
        }

        mySubjectAdapter.getList().clear();
        mySubjectAdapter.getList().addAll(mSubjectList);
        mySubjectAdapter.notifyDataSetChanged();

    }


    class MyOnClickListener implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            List<String> answerImgs;
            switch (v.getId()) {

                case R.id.imgnet_id:

                    if (AndroidUtils.isNetworkAvailable(MyHaslsudActivity.this)) {
                        fmshowNet.setVisibility(View.GONE);
                        getRecHomeWorkHaslsudApi();
                    } else {
                        fmshowNet.setVisibility(View.VISIBLE);
                    }

                    break;


                case R.id.pop_layout_id:
                    // initPopview();
                    refreshPop();
                    myPopwindow.showAsDropDown(popLayout, -312, -popLayout.getHeight());

                    break;


                case R.id.imgbutton_left_id:
                    answerImgs = mTobeList.get(numbers).getStudentHomeworkQuestion().getNotationAnswerImgs();
                    if (answerImgs == null || answerImgs.size() == 0) {
                        answerImgs = mTobeList.get(numbers).getStudentHomeworkQuestion().getAnswerImgs();
                    }
                    if (textYeshu.getText().equals("2/2") && answerImgs != null && answerImgs.size() > 0 && answerImgs.get(0) != null) {
                        textYeshu.setText("1/2");
                        imgDati1.setVisibility(View.VISIBLE);
                        imgDati2.setVisibility(View.GONE);
                        ImageLoadUtil.getInstance().loadImageActivity(MyHaslsudActivity.this,
                                answerImgs.get(0), R.drawable.image_fail, imgDati1);
                    }

                    break;
                case R.id.imgbutton_right_id:
                    answerImgs = mTobeList.get(numbers).getStudentHomeworkQuestion().getNotationAnswerImgs();
                    if (answerImgs == null || answerImgs.size() == 0) {
                        answerImgs = mTobeList.get(numbers).getStudentHomeworkQuestion().getAnswerImgs();
                    }
                    if (textYeshu.getText().equals("1/2") && answerImgs != null && answerImgs.size() > 1 && answerImgs.get(1) != null) {
                        textYeshu.setText("2/2");
                        imgDati1.setVisibility(View.GONE);
                        imgDati2.setVisibility(View.VISIBLE);
                        ImageLoadUtil.getInstance().loadImageActivity(MyHaslsudActivity.this,
                                answerImgs.get(1), R.drawable.image_fail, imgDati2);
                    }

                    break;

                case R.id.report_btn_id:

                    Intent intent = new Intent();
                    intent.setClass(MyHaslsudActivity.this, WorkReportActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("myTobeInfo", mytobeInfo);

                    bundle.putString("title", title);

                    intent.putExtras(bundle);

                    startActivityForResult(intent, REQUESTCODE);
                    break;

                case R.id.radio_timu_id:

                    linearTimu.setVisibility(View.VISIBLE);
                    linearMyRspd.setVisibility(View.GONE);
                    linearSureRspd.setVisibility(View.GONE);
                    linearAnalysis.setVisibility(View.GONE);
                    answerRL.setVisibility(View.GONE);

                    radioTimu.setTextColor(getResources().getColor(R.color.hui));
                    radioMyRspd.setTextColor(getResources().getColor(R.color.zl_black));
                    radioSureRspd.setTextColor(getResources().getColor(R.color.zl_black));
                    radioAnalysis.setTextColor(getResources().getColor(R.color.zl_black));
                    break;
                case R.id.radio_my_respond:

                    linearTimu.setVisibility(View.GONE);
                    linearMyRspd.setVisibility(View.VISIBLE);
                    linearSureRspd.setVisibility(View.GONE);
                    linearAnalysis.setVisibility(View.GONE);
                    answerRL.setVisibility(View.VISIBLE);

                    radioTimu.setTextColor(getResources().getColor(R.color.zl_black));
                    radioMyRspd.setTextColor(getResources().getColor(R.color.hui));
                    radioSureRspd.setTextColor(getResources().getColor(R.color.zl_black));
                    radioAnalysis.setTextColor(getResources().getColor(R.color.zl_black));
                    break;
                case R.id.radio_sure_respond:

                    linearTimu.setVisibility(View.GONE);
                    linearMyRspd.setVisibility(View.GONE);
                    linearSureRspd.setVisibility(View.VISIBLE);
                    linearAnalysis.setVisibility(View.GONE);
                    answerRL.setVisibility(View.GONE);


                    radioTimu.setTextColor(getResources().getColor(R.color.zl_black));
                    radioMyRspd.setTextColor(getResources().getColor(R.color.zl_black));
                    radioSureRspd.setTextColor(getResources().getColor(R.color.hui));
                    radioAnalysis.setTextColor(getResources().getColor(R.color.zl_black));
                    break;
                case R.id.radio_analysis:


                    linearTimu.setVisibility(View.GONE);
                    linearMyRspd.setVisibility(View.GONE);
                    linearSureRspd.setVisibility(View.GONE);
                    linearAnalysis.setVisibility(View.VISIBLE);
                    answerRL.setVisibility(View.GONE);

                    radioTimu.setTextColor(getResources().getColor(R.color.zl_black));
                    radioMyRspd.setTextColor(getResources().getColor(R.color.zl_black));
                    radioSureRspd.setTextColor(getResources().getColor(R.color.zl_black));
                    radioAnalysis.setTextColor(getResources().getColor(R.color.hui));
                    break;


                case R.id.xia_ti:
                    if (numbers < mTobeList.size() - 1) {
                        numbers += 1;
                        getnextAndResutqs();
                    }

                    break;
                case R.id.shang_ti:
                    if (numbers > 0) {
                        numbers -= 1;
                        getnextAndResutqs();
                    }

                    break;

                case R.id.cancel_id:
                    finish();
                    break;

                case R.id.shou_id:
                    myPopwindow.dismiss();
                    break;

                default:
                    break;

            }

        }
    }

    private void getnextAndResutqs() {

        if (numbers < mTobeList.size() && numbers >= 0) {


            if (numbers == mTobeList.size() - 1) {
                dwonBtn.setEnabled(false);
                dwonBtn.setTextColor(getResources().getColor(R.color.zl_user_point_text));
            } else {
                dwonBtn.setEnabled(true);
                dwonBtn.setTextColor(getResources().getColor(R.color.zl_black));
            }
            if (numbers == 0) {
                topBtn.setEnabled(false);
                topBtn.setTextColor(getResources().getColor(R.color.zl_user_point_text));
            } else {
                topBtn.setEnabled(true);
                topBtn.setTextColor(getResources().getColor(R.color.zl_black));
            }

            Log.i("xcz", "aqqqqqqqqqqqqqqqqqqqqq" + numbers);
            for (int i = 0; i < mTobeList.size(); i++) {
                if (i == numbers) {
                    // Log.i("xcz",">>>>:"+numbers+"<<<<<<:"+(++topandownCount));
//                    if (topandownCount < mTobeList.size())
                    {

                        //循环加订正题

                        int workindex = 0;
                        int dzindex = 0;
                        for (int dindex = 0; dindex < mTobeList.size(); dindex++) {

                            if (mTobeList.get(dindex).isCorrectQuestion()) {
                                dzindex++;

                            } else {
                                workindex++;
                            }

                        }

                        int count = numbers + 1;
                        if (mTobeList.get(numbers).isCorrectQuestion()) {

                            workCount.setText(mSubjectList.get(numbers).getTopic() + "/" + (dzindex > 0 ? workindex + "+" + dzindex : workindex));
                            textPopCount.setText(mSubjectList.get(numbers).getTopic() + "/" + (dzindex > 0 ? workindex + "+" + dzindex : workindex));
                        } else {
                            workCount.setText(count + "/" + (dzindex > 0 ? workindex + "+" + dzindex : workindex));
                            textPopCount.setText(count + "/" + (dzindex > 0 ? workindex + "+" + dzindex : workindex));
                        }
                    }

                    if (mTobeList.get(i).getType().equals("SINGLE_CHOICE")) {

                        htmlSureRspd.setVisibility(View.VISIBLE);
                        htmlSureRspd.setText(mTobeList.get(i).getAnswers().get(0).getContent());
                        if (chiocesList != null && chiocesList.size() > 0) {
                            chiocesList.clear();
                        }


                        if (mTobeList.get(i).isCorrectQuestion()) {
                            textTigan.setText(FormateSpannedContent(textTigan, "[订正题]" + mTobeList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                        } else {
                            textTigan.setText(FormateSpannedContent(textTigan, mTobeList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                        }
                        //textTigan.setHtml(mTobeList.get(i).getContent(), new HtmlHttpImageGetter(textTigan));
                        textVis.setVisibility(View.VISIBLE);
//                        if (mTobeList.get(i).getStudentHomeworkAnswers().get(0).getResult().equals("WRONG")) {
//                            textRult.setText("回答错误");
//                        } else {
//                            textRult.setText("回答正确");
//                        }


                        if ("WRONG".equals(mTobeList.get(i).getStudentHomeworkQuestion().getResult())) {
                            textRult.setText("回答错误");
                            layoutResult.setVisibility(View.VISIBLE);

                        } else if ("RIGHT".equals(mTobeList.get(i).getStudentHomeworkQuestion().getResult())) {
                            textRult.setText("回答正确");
                            layoutResult.setVisibility(View.VISIBLE);
                        } else {
                            layoutResult.setVisibility(View.GONE);

                        }

                        for (int j = 0; j < mTobeList.get(i).getChoices().length; j++) {
                            String myAnswer = mTobeList.get(i).getAnswers().get(0).getContent();
                            String rightAnswer = mTobeList.get(i).getStudentHomeworkAnswers().get(0).getContent();

                            ChioceABCDInfo chioceABCDInfo = new ChioceABCDInfo();
                            chioceABCDInfo.setChoiceContent(mTobeList.get(i).getChoices()[j]);
                            if (j == 0) {
                                chioceABCDInfo.setChoiceName("A");
                                if (myAnswer.equals("A")) {
                                    chioceABCDInfo.setChoiceTag(true);
                                } else {
                                    chioceABCDInfo.setChoiceTag(false);

                                }

                                if (rightAnswer.equals("A")) {
                                    chioceABCDInfo.setRightTag(true);
                                } else {
                                    chioceABCDInfo.setRightTag(false);
                                }
                            } else if (j == 1) {
                                chioceABCDInfo.setChoiceName("B");
                                if (myAnswer.equals("B")) {
                                    chioceABCDInfo.setChoiceTag(true);
                                } else {
                                    chioceABCDInfo.setChoiceTag(false);

                                }

                                if (rightAnswer.equals("B")) {
                                    chioceABCDInfo.setRightTag(true);
                                } else {
                                    chioceABCDInfo.setRightTag(false);
                                }

                            } else if (j == 2) {
                                chioceABCDInfo.setChoiceName("C");
                                if (myAnswer.equals("C")) {
                                    chioceABCDInfo.setChoiceTag(true);
                                } else {
                                    chioceABCDInfo.setChoiceTag(false);

                                }

                                if (rightAnswer.equals("C")) {
                                    chioceABCDInfo.setRightTag(true);
                                } else {
                                    chioceABCDInfo.setRightTag(false);
                                }
                            } else if (j == 3) {
                                chioceABCDInfo.setChoiceName("D");
                                if (myAnswer.equals("D")) {
                                    chioceABCDInfo.setChoiceTag(true);
                                } else {
                                    chioceABCDInfo.setChoiceTag(false);

                                }

                                if (rightAnswer.equals("D")) {
                                    chioceABCDInfo.setRightTag(true);
                                } else {
                                    chioceABCDInfo.setRightTag(false);
                                }
                            } else if (j == 4) {
                                chioceABCDInfo.setChoiceName("E");
                                if (myAnswer.equals("E")) {
                                    chioceABCDInfo.setChoiceTag(true);
                                } else {
                                    chioceABCDInfo.setChoiceTag(false);

                                }

                                if (rightAnswer.equals("E")) {
                                    chioceABCDInfo.setRightTag(true);
                                } else {
                                    chioceABCDInfo.setRightTag(false);
                                }
                            } else if (j == 5) {
                                chioceABCDInfo.setChoiceName("F");
                                if (myAnswer.equals("F")) {
                                    chioceABCDInfo.setChoiceTag(true);
                                } else {
                                    chioceABCDInfo.setChoiceTag(false);

                                }

                                if (rightAnswer.equals("F")) {
                                    chioceABCDInfo.setRightTag(true);
                                } else {
                                    chioceABCDInfo.setRightTag(false);
                                }
                            }
                            chiocesList.add(chioceABCDInfo);
                        }


                        textVis.setVisibility(View.VISIBLE);
                        chioces_LV.setVisibility(View.VISIBLE);
                        myChiocesAdapter.getList().clear();
                        myChiocesAdapter.getList().addAll(chiocesList);
                        addCHooseItem();
                        myChiocesAdapter.notifyDataSetChanged();
//                        ImageLoaderManager.loadImageActivity(MyHaslsudActivity.this,
//                                mTobeList.get(i).getStudentHomeworkQuestion().getSolvingImg(), R.drawable.ic_launcher, imgDati);
                        // htmlTextAnalysis.setHtml(mTobeList.get(i).getAnalysis(), new HtmlHttpImageGetter(htmlTextAnalysis));
                        htmlTextAnalysis.setText(FormateSpannedContent(htmlTextAnalysis, mTobeList.get(i).getAnalysis(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                        imgDati1.setVisibility(View.GONE);
                        imgDati2.setVisibility(View.GONE);
                        List<String> answerImgs = mTobeList.get(i).getStudentHomeworkQuestion().getNotationAnswerImgs();
                        if (answerImgs == null || answerImgs.size() == 0) {
                            answerImgs = mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs();
                        }
                        if (answerImgs != null && answerImgs.size() > 0) {

                            if (answerImgs.size() == 2) {
                                textYeshu.setText("1/2");
                                imgDati1.setVisibility(View.VISIBLE);
                                ImageLoadUtil.getInstance().loadImageActivity(MyHaslsudActivity.this,
                                        answerImgs.get(0), R.drawable.image_fail, imgDati1);
                            } else {

                                if (answerImgs.size() == 1) {
                                    textYeshu.setText("1/1");
                                    imgDati1.setVisibility(View.VISIBLE);
                                    ImageLoadUtil.getInstance().loadImageActivity(MyHaslsudActivity.this,
                                            answerImgs.get(0), R.drawable.image_fail, imgDati1);

                                } else {
                                    textYeshu.setText("1/1");
                                }

                            }


                        } else {

                            textYeshu.setText("1/1");
                        }

                    } else {
//                        if (numbers<mTobeList.size()-1){
//                            numbers++;
//                        }
                        htmlSureRspd.setVisibility(View.VISIBLE);

                        if (mTobeList.get(i).isCorrectQuestion()) {
                            textTigan.setText(FormateSpannedContent(textTigan, "[订正题]" + mTobeList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                        } else {
                            textTigan.setText(FormateSpannedContent(textTigan, mTobeList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                        }
                        //textTigan.setHtml(mTobeList.get(i).getContent(), new HtmlHttpImageGetter(textTigan));
                        textVis.setVisibility(View.GONE);
                        chioces_LV.setVisibility(View.GONE);

                        //htmlTextAnalysis.setHtml(mTobeList.get(i).getAnalysis(), new HtmlHttpImageGetter(htmlTextAnalysis));
                        htmlTextAnalysis.setText(FormateSpannedContent(htmlTextAnalysis, mTobeList.get(i).getAnalysis(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                        StringBuffer stringBuffer = new StringBuffer();
                        for (int k = 0; k < mTobeList.get(i).getAnswers().size(); k++) {
                            if (mTobeList.get(i).getAnswers().size() > 1) {
                                stringBuffer.append((k + 1) + "," + mTobeList.get(i).getAnswers().get(k).getContent() + "<br>");
                            } else {
                                stringBuffer.append(mTobeList.get(i).getAnswers().get(k).getContent() + "<br>");

                            }
                        }
                        htmlSureRspd.setText(FormateSpannedContent(htmlSureRspd, stringBuffer.toString(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
//                        htmlSureRspd.setHtml(stringBuffer.toString(), new HtmlHttpImageGetter(htmlSureRspd));

                        if ("WRONG".equals(mTobeList.get(i).getStudentHomeworkQuestion().getResult())) {
                            layoutResult.setVisibility(View.VISIBLE);

                            if (mTobeList.get(i).getStudentHomeworkQuestion().getRightRate() == 0 || TextUtils.isEmpty(mTobeList.get(i).getStudentHomeworkQuestion().getRightRateTitle())) {
                                textRult.setText("回答错误");
                            } else {
                                textRult.setText(mTobeList.get(i).getStudentHomeworkQuestion().getRightRateTitle() + "正确");
                            }
                        } else if ("RIGHT".equals(mTobeList.get(i).getStudentHomeworkQuestion().getResult())) {

                            layoutResult.setVisibility(View.VISIBLE);
                            textRult.setText("回答正确");
                        } else {
                            //隱藏
                            layoutResult.setVisibility(View.GONE);
                        }

                        imgDati1.setVisibility(View.GONE);
                        imgDati2.setVisibility(View.GONE);
                        List<String> answerImgs = mTobeList.get(i).getStudentHomeworkQuestion().getNotationAnswerImgs();
                        if (answerImgs == null || answerImgs.size() == 0) {
                            answerImgs = mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs();
                        }
                        if (answerImgs != null && answerImgs.size() > 0) {

                            if (answerImgs.size() == 2) {
                                textYeshu.setText("1/2");
                                imgDati1.setVisibility(View.VISIBLE);
                                ImageLoadUtil.getInstance().loadImageActivity(MyHaslsudActivity.this,
                                        answerImgs.get(0), R.drawable.image_fail, imgDati1);
                            } else {

                                if (answerImgs.size() == 1) {
                                    textYeshu.setText("1/1");
                                    imgDati1.setVisibility(View.VISIBLE);
                                    ImageLoadUtil.getInstance().loadImageActivity(MyHaslsudActivity.this,
                                            answerImgs.get(0), R.drawable.image_fail, imgDati1);

                                } else {
                                    textYeshu.setText("1/1");
                                }

                            }


                        } else {

                            textYeshu.setText("1/1");
                        }
                    }
                    setTimutrue();
                    break;
                }

            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE) {
            switch (resultCode) {
                case RESULT_OK:

                    Bundle bundle = data.getExtras();
                    numbers = bundle.getInt("position", 0);
                    getnextAndResutqs();
                    //  refreshPop();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                relShow.setVisibility(View.VISIBLE);
                showTopAndDown();
                break;

        }

        return super.onTouchEvent(event);
    }


    private void addCHooseItem() {
        if (chioces_LV != null) {
            chioces_LV.removeAllViews();
        }
        if (myChiocesAdapter != null && myChiocesAdapter.getCount() > 0) {
            for (int i = 0; i < myChiocesAdapter.getCount(); i++) {
                chioces_LV.addView(myChiocesAdapter.getView(i, null, null));
            }
        }
    }

    protected Spanned FormateSpannedContent(TextView view, String content, int borderWidth) {
        MyImageGetter getter = new MyImageGetter(view, this, borderWidth + 22);
        Spanned s = Html.fromHtml(content, getter, null);
        return s;


    }

    private void setTimutrue() {
        radioTimu.setChecked(true);
        radioMyRspd.setChecked(false);
        radioSureRspd.setChecked(false);
        radioAnalysis.setChecked(false);
        radioTimu.setTextColor(getResources().getColor(R.color.hui));
        radioMyRspd.setTextColor(getResources().getColor(R.color.zl_black));
        radioSureRspd.setTextColor(getResources().getColor(R.color.zl_black));
        radioAnalysis.setTextColor(getResources().getColor(R.color.zl_black));

        linearTimu.setVisibility(View.VISIBLE);
        linearMyRspd.setVisibility(View.GONE);
        linearSureRspd.setVisibility(View.GONE);
        linearAnalysis.setVisibility(View.GONE);

    }
}
