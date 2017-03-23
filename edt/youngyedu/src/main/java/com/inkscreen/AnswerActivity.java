package com.inkscreen;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inkscreen.adapter.MyAnswerSubjectAdapter;
import com.inkscreen.adapter.MyChiocesAdapter;
import com.inkscreen.adapter.MyWorkingAdapter;
import com.inkscreen.model.ChioceABCDInfo;
import com.inkscreen.model.DrawPath;
import com.inkscreen.model.Event;
import com.inkscreen.model.Subject;
import com.inkscreen.model.TaskInfo;
import com.inkscreen.ui.TimerTextView;
import com.inkscreen.ui.WorkSubTitleLayout;
import com.inkscreen.utils.AndroidUtils;
import com.inkscreen.utils.DeviceInfoUtil;
import com.inkscreen.utils.ImageRequest;
import com.inkscreen.utils.LeApiApp;
import com.inkscreen.utils.LeApiResult;
import com.inkscreen.utils.LeApiUtils;
import com.inkscreen.utils.MyImageGetter;
import com.inkscreen.utils.ShowIsudDialog;
import com.inkscreen.utils.StringUtils;
import com.inkscreen.utils.network.RequestManager;
import com.inkscreen.will.utils.widget.MultiGridView;
import com.inkscreen.will.utils.widget.PaintView;
import com.yougy.ui.activity.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import de.greenrobot.event.EventBus;


/**
 * Created by xcz on 2016/11/16.
 */
public class AnswerActivity extends Activity {

    LinearLayout popLayout;
    PopupWindow myPopwindow;
    TextView shouText;
    MyAnswerSubjectAdapter mySubjectAdapter;
    List<Subject> mSubjectList;
    MultiGridView mSubjectGV;
    RadioButton radioBtnTimu, radioBtnDaan;
    private String homeWorkid;
    TextView textTigan;
    TextView textVis, workCount;
    List<ChioceABCDInfo> chiocesList;
    LinearLayout chioces_LV;

    List<TaskInfo.Ret.Questions> mTaskList;
    MyChiocesAdapter myChiocesAdapter;
    Button btnNextAndCommit, btnTop;
    private int numbers = 0;
    private int topandownCount = 1;
    LinearLayout layoutTimubg, layoutDatibg;
    TaskInfo.Ret ret;
    Button tijBtn, plusBtn;
    TimerTextView textRunTime;
    //    TuyaView linePathView;
    LinearLayout linearLayout;
    ImageButton imgsBtn, imgXBtn;
    List<String> list;
    TextView textYema;
    Handler mHander = new Handler();
    List<DrawPath> groupPath;
    List<DrawPath> groupPathRight;
    //List<String> imgList;
    HashMap<String, String> imgMap;
    CountDownLatch threadSignal;
    WorkSubTitleLayout leSubTitleLayout;
    boolean topAndDown = true;
    boolean tijiaoTag = true;
    boolean switchTag = true;
    boolean backTag = true;
    TextView textsectionName;
    FrameLayout fmshowNet;
    ImageView imgNet;
    ProgressBar progressBar;
    LinearLayout layoutSaveLoad;
    long longTime;
    Dialog dialog;
    FrameLayout loadingFm;
    TextView textPrompt;
    TextView textPopCount;
    Runnable countRunnable = new Runnable() {

        @Override
        public void run() {
            //  Looper.prepare();
            paintView.invalidataControler();
            showTimeDialog();
            Log.i("xcz", ">>>>countRunnable");

        }
    };
    Runnable autoSaveRunnable = new Runnable() {

        @Override
        public void run() {
            //  Looper.prepare();
            //作业截止时间前六秒触发自动保存
            paintView.invalidataControler();
            postImgToserver();

        }
    };

    protected PaintView paintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        homeWorkid = getIntent().getStringExtra("homeWorkId");

        intView();
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        linePathView =new TuyaView(this);
        paintView = (PaintView) findViewById(R.id.v_tuya);
//                new TuyaView(this);
//                (TuyaView) findViewById(R.id.v_tuya);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        lp.setMargins(4, 4, 4, 4);
//        linePathView.setLayoutParams(lp);
//        linearLayout.addView(linePathView);
        initPopview();

        if (AndroidUtils.isNetworkAvailable(AnswerActivity.this)) {
            fmshowNet.setVisibility(View.GONE);
            loadingFm.setVisibility(View.VISIBLE);
            linearLayout.post(new Runnable() {
                @Override
                public void run() {
                    getHomeWorkApi();
                }
            });
//            getHomeWorkApi();
        } else {
            fmshowNet.setVisibility(View.VISIBLE);
        }


        list = new ArrayList<>();

        myChiocesAdapter.setChioceActionListener(new MyChiocesAdapter.ActionListener() {
            @Override
            public void RunAction(int position) {
                paintView.invalidataControler();
                ;
                for (int i = 0; i < chiocesList.size(); i++) {
                    if (i == position) {
                        chiocesList.get(i).setChoiceTag(true);
                    } else {
                        chiocesList.get(i).setChoiceTag(false);
                    }
                }
                if (list != null && list.size() > 0) {
                    list.clear();
                }

                if (0 == position) {
                    list.add("A");
                } else if (1 == position) {
                    list.add("B");
                } else if (2 == position) {
                    list.add("C");
                } else if (3 == position) {
                    list.add("D");
                } else if (4 == position) {
                    list.add("E");
                } else if (5 == position) {
                    list.add("F");
                } else if (6 == position) {
                    list.add("G");
                }
                myChiocesAdapter.getList().clear();
                myChiocesAdapter.getList().addAll(chiocesList);
                myChiocesAdapter.notifyDataSetChanged();
//                addCHooseItem();
                myChiocesAdapter.setSelect(position, chioces_LV);
                mTaskList.get(numbers).getStudentHomeworkAnswers().get(0).setContentAscii(list.get(0));
//                Toast.makeText(AnswerActivity.this,"cscscs",Toast.LENGTH_SHORT).show();

//                for (int i=0;i<mTaskList.size();i++){
//                    if (i == numbers){
//                        for (int j = 0; i < chiocesList.size(); j++) {
//                            if (chiocesList.get(j).isChoiceTag()) {
//                                mTaskList.get(i).getStudentHomeworkAnswers().get(0).setContentAscii(list.get(0));
//                                break;
//                            }
//                        }
//                        break;
//                    }
//                }

            }
        });


    }


    private void intView() {

        mTaskList = new ArrayList<>();
        chiocesList = new ArrayList<>();
//        groupPath = new ArrayList<>();
//        groupPathRight = new ArrayList<>();
        // imgList = new ArrayList<>();

//        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
//        progressBar.setVisibility(View.VISIBLE);
        textPrompt = (TextView) findViewById(R.id.prompt_id);
        loadingFm = (FrameLayout) findViewById(R.id.loading_id);
        layoutSaveLoad = (LinearLayout) findViewById(R.id.saveanswer_id);
        leSubTitleLayout = (WorkSubTitleLayout) findViewById(R.id.wsubTitle_id);
        textRunTime = (TimerTextView) findViewById(R.id.runtime_id);
        textYema = (TextView) findViewById(R.id.yeshu_id);
        plusBtn = (Button) findViewById(R.id.plusye_id);

        fmshowNet = (FrameLayout) findViewById(R.id.show_id);
        imgNet = (ImageView) findViewById(R.id.imgnet_id);
        imgsBtn = (ImageButton) findViewById(R.id.imgs_id);
        imgXBtn = (ImageButton) findViewById(R.id.imgx_id);

        workCount = (TextView) findViewById(R.id.top_id);
        linearLayout = (LinearLayout) findViewById(R.id.layout_ty_id);
        layoutDatibg = (LinearLayout) findViewById(R.id.dati_id);
        layoutTimubg = (LinearLayout) findViewById(R.id.layout_id);
        btnNextAndCommit = (Button) findViewById(R.id.commit_id);
        btnTop = (Button) findViewById(R.id.button_top_id);
        textTigan = (TextView) findViewById(R.id.tigan_id);
        textVis = (TextView) findViewById(R.id.textVis_id);
        popLayout = (LinearLayout) findViewById(R.id.pop_layout_id);
        radioBtnTimu = (RadioButton) findViewById(R.id.radio_timu_id);
        radioBtnDaan = (RadioButton) findViewById(R.id.radio_daan_id);
        chioces_LV = (LinearLayout) findViewById(R.id.choices_list_id);
        popLayout.setOnClickListener(new MyTopicClickListener());
        radioBtnTimu.setOnClickListener(new MyTopicClickListener());
        radioBtnDaan.setOnClickListener(new MyTopicClickListener());
        btnNextAndCommit.setOnClickListener(new MyTopicClickListener());
        btnTop.setOnClickListener(new MyTopicClickListener());
        plusBtn.setOnClickListener(new MyTopicClickListener());
        imgsBtn.setOnClickListener(new MyTopicClickListener());
        imgXBtn.setOnClickListener(new MyTopicClickListener());
        imgNet.setOnClickListener(new MyTopicClickListener());
        radioBtnTimu.setChecked(true);
        myChiocesAdapter = new MyChiocesAdapter(AnswerActivity.this);
        myChiocesAdapter.setList(new ArrayList<ChioceABCDInfo>());
        myChiocesAdapter.SetParentView(chioces_LV);
//        chioces_LV.setAdapter(myChiocesAdapter);
        layoutTimubg.setVisibility(View.VISIBLE);
        radioBtnTimu.setTextColor(getResources().getColor(R.color.hui));

        leSubTitleLayout.setOndo(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.invalidataControler();
                ;
                paintView.undo();


            }
        });

        leSubTitleLayout.setOnRedo(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.invalidataControler();
                ;
                paintView.recover();

            }
        });


        leSubTitleLayout.setTitle(null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                backTag = false;
//                postImgToserver();
                paintView.invalidataControler();
                ;
                backEvent();
                //  finish();
            }
        });


        leSubTitleLayout.setOnBi(null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.invalidataControler();
                ;
                paintView.selectPaintStyle(0);
                Toast.makeText(AnswerActivity.this, "已切换笔", Toast.LENGTH_SHORT).show();
            }
        });


        leSubTitleLayout.setOnXp(null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.invalidataControler();
                ;
                paintView.selectPaintStyle(1);
                Toast.makeText(AnswerActivity.this, "已切换橡皮擦", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int popClickPosition;

    private void initPopview() {
        View popupView = getLayoutInflater().inflate(R.layout.answer_pop, null);
        mSubjectGV = (MultiGridView) popupView.findViewById(R.id.subject_id);
        mSubjectList = new ArrayList<>();
        mySubjectAdapter = new MyAnswerSubjectAdapter(this);
        mySubjectAdapter.setList(new ArrayList<Subject>());
        mySubjectAdapter.setItemclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.invalidataControler();
                ;
                if (v.getTag() != null) {
                    switchTag = false;

                    myPopwindow.dismiss();
                    popClickPosition = (int) v.getTag();
                    setLoadworkActionListener(new ActionListener() {
                        @Override
                        public void RunAction() {
                            Log.e("popClickPosition", "popClickPosition=" + popClickPosition);
                            switchAnswer(popClickPosition);
                        }
                    });
                    postImgToserver();
                }
            }
        });
        mSubjectGV.setAdapter(mySubjectAdapter);
        textsectionName = (TextView) popupView.findViewById(R.id.ystime_id);
        tijBtn = (Button) popupView.findViewById(R.id.tijiao_btn_id);
        shouText = (TextView) popupView.findViewById(R.id.shou_id);
        shouText.setOnClickListener(new MyTopicClickListener());
        tijBtn.setOnClickListener(new MyTopicClickListener());
        myPopwindow = new PopupWindow(popupView, 785, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        myPopwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                paintView.invalidataControler();
                ;
                // popLayout.setBackgroundResource(R.drawable.radio_market_selector);
            }
        });
        textPopCount = (TextView) popupView.findViewById(R.id.popcount_id);
        LinearLayout canCelLayout = (LinearLayout) popupView.findViewById(R.id.cancellayout_id);

        canCelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.invalidataControler();
                ;
                myPopwindow.dismiss();
            }
        });

        myPopwindow.setTouchable(true);
        myPopwindow.setOutsideTouchable(true);
        myPopwindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
//
//        mSubjectGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,   int position, long id) {
//
//                switchTag = false;
//
//                myPopwindow.dismiss();
//                popClickPosition=position;
//                postImgToserver();
//
//                setLoadworkActionListener(new ActionListener() {
//                    @Override
//                    public void RunAction() {
//                        switchAnswer(popClickPosition);
//                    }
//                });
//
//
//            }
//        });


    }

    //
    private void switchAnswer(int position) {
        if (position >= 1) {
            btnTop.setVisibility(View.VISIBLE);
        }

        if (position == 1) {
            btnTop.setVisibility(View.INVISIBLE);
        }

        if ((position + 1) == mTaskList.size()) {
            btnNextAndCommit.setText("提交作业");

        }
        layoutTimubg.setVisibility(View.VISIBLE);
        layoutDatibg.setVisibility(View.GONE);
        numbers = position + 1;
        topandownCount = position + 2;
        topWork();

    }


    private void refreshPop() {
        //刷新popWindow题目
        if (mSubjectList != null && mSubjectList.size() > 0) {
            mSubjectList.clear();

        }
        int m = 1;
        int w = 1;
        for (int i = 0; i < mTaskList.size(); i++) {

            Subject subject = new Subject();
            subject.setTopic("" + m);
            if (mTaskList.get(i).isCorrectQuestion()) {
                subject.setTopic("" + w);
                subject.setEmendTag(true);
                w++;
            } else {
                subject.setTopic("" + m);
                subject.setEmendTag(false);
                m++;
            }

            if (i == numbers) {

                if (mTaskList.get(i).getType().equals("SINGLE_CHOICE")) {
                    if (!StringUtils.isEmptyStr(mTaskList.get(i).getStudentHomeworkAnswers().get(0).getContentAscii())) {
                        subject.setDoflag(true);
                    } else {
                        subject.setDoflag(false);
                    }

                } else {
                    if ((mTaskList.get(i).getLeftPath() != null && mTaskList.get(i).getLeftPath().size() > 0) ||
                            (mTaskList.get(i).getRightPath() != null && mTaskList.get(i).getRightPath().size() > 0)
                            || (mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting() != null
                            && mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting().size() > 0) || (paintView.savePath != null && paintView.savePath.size() > 0)) {
                        subject.setDoflag(true);
                    } else {
                        subject.setDoflag(false);
                    }

                }


            } else {
                if (mTaskList.get(i).getType().equals("SINGLE_CHOICE")) {
                    //选择题
                    if (!StringUtils.isEmptyStr(mTaskList.get(i).getStudentHomeworkAnswers().get(0).getContentAscii())) {
                        subject.setDoflag(true);
                    } else {
                        subject.setDoflag(false);
                    }

                } else {
                    //解答题&填空题
                    if (mTaskList.get(i).getLeftPath() != null && mTaskList.get(i).getLeftPath().size() > 0 || mTaskList.get(i).getRightPath() != null && mTaskList.get(i).getRightPath().size() > 0 || mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting().size() > 0) {
                        subject.setDoflag(true);
                    } else {
                        subject.setDoflag(false);
                    }
                }
            }


            if (i == numbers) {
                subject.setTitelflag(true);
            } else {

                subject.setTitelflag(false);
            }

//            m++;
            mSubjectList.add(subject);
        }

        mySubjectAdapter.getList().clear();
        mySubjectAdapter.getList().addAll(mSubjectList);
        mySubjectAdapter.notifyDataSetChanged();

    }


    private void getHomeWorkApi() {

        Map<String, String> map = new HashMap<>();
        map.put("stuHkId", homeWorkid);

        LeApiUtils.postString(LeApiApp.getHomeWorkUrl(), map, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if (isFinishing()) {
                    return;
                }
                LeApiResult<TaskInfo> result = new LeApiResult<TaskInfo>(jsonObject, new TypeToken<TaskInfo>() {
                });

                if (result.getCode() == 0 && result.getResult() != null) {


                    ret = result.getResult().getRet();

                    longTime = Long.parseLong(ret.getDeadline());

                    mHander.postDelayed(countRunnable, longTime);
                    mHander.postDelayed(autoSaveRunnable, longTime - 8000);

                    if (ret.getStudentHomework().getHomeworkTime() != 0) {

                        textRunTime.setTime(ret.getStudentHomework().getHomeworkTime());
                        textRunTime.startTimer();
                    } else {
                        textRunTime.startTimer();
                    }

                    mTaskList = result.getResult().getRet().getQuestions();
                    //循环加订正题

                    int workindex = 0;
                    int dzindex = 0;
                    for (int dindex = 0; dindex < mTaskList.size(); dindex++) {

                        if (mTaskList.get(dindex).isCorrectQuestion()) {
                            dzindex++;

                        } else {
                            workindex++;
                        }


                    }

                    workCount.setText(topandownCount + "/" + (dzindex > 0 ? workindex + "+" + dzindex : workindex));
                    textPopCount.setText(topandownCount + "/" + (dzindex > 0 ? workindex + "+" + dzindex : workindex));
                    //workCount.setText(topandownCount + "/" + workindex+"+"+dzindex);
                    textsectionName.setText(ret.getStudentHomework().getHomework().getName());
                    btnTop.setVisibility(View.INVISIBLE);
                    //  refreshPop();
                    for (int i = 0; i < mTaskList.size(); i++) {
                        if (mTaskList.get(i).getType().equals("SINGLE_CHOICE")) {


                            if (mTaskList.get(i).isCorrectQuestion()) {

                                textTigan.setText(FormateSpannedContent(textTigan, "[订正题]" + mTaskList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
//                                textTigan.setHtml("[订正题]" + mTaskList.get(i).getContent(), new HtmlHttpImageGetter(textTigan,"",true));
                            } else {
//                                textTigan.setHtml(mTaskList.get(i).getContent(), new HtmlHttpImageGetter(textTigan,"",true));
                                textTigan.setText(FormateSpannedContent(textTigan, mTaskList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                            }


                            textVis.setVisibility(View.VISIBLE);

                            for (int j = 0; j < mTaskList.get(i).getChoices().length; j++) {

                                ChioceABCDInfo chioceABCDInfo = new ChioceABCDInfo();
                                chioceABCDInfo.setChoiceContent(mTaskList.get(i).getChoices()[j]);
                                if (j == 0) {
                                    chioceABCDInfo.setChoiceName("A");

                                } else if (j == 1) {
                                    chioceABCDInfo.setChoiceName("B");


                                } else if (j == 2) {
                                    chioceABCDInfo.setChoiceName("C");

                                } else if (j == 3) {
                                    chioceABCDInfo.setChoiceName("D");

                                } else if (j == 4) {
                                    chioceABCDInfo.setChoiceName("E");

                                } else if (j == 5) {
                                    chioceABCDInfo.setChoiceName("F");

                                }
                                chiocesList.add(chioceABCDInfo);
                            }

                            for (int m = 0; m < chiocesList.size(); m++) {
                                //ChioceABCDInfo chioceABCDInfo = new ChioceABCDInfo();
                                if (chiocesList.get(m).getChoiceName() != null && chiocesList.get(m).getChoiceName().equals(mTaskList.get(i).getStudentHomeworkAnswers().get(0).getContentAscii())) {
                                    chiocesList.get(m).setChoiceTag(true);
                                } else {
                                    chiocesList.get(m).setChoiceTag(false);
                                }


                            }

                            // textVis.setVisibility(View.VISIBLE);
                            chioces_LV.setVisibility(View.VISIBLE);
                            // numbers++;
                            myChiocesAdapter.getList().clear();
                            myChiocesAdapter.getList().addAll(chiocesList);
                            addCHooseItem();
                            myChiocesAdapter.notifyDataSetChanged();


                        } else {
                            // numbers++;
                            if (mTaskList.get(i).isCorrectQuestion()) {
                                textTigan.setText(FormateSpannedContent(textTigan, "[订正题]" + mTaskList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
//                                textTigan.setHtml("[订正题]" + mTaskList.get(i).getContent(), new HtmlHttpImageGetter(textTigan,"",true));
                            } else {
//                                textTigan.setHtml(mTaskList.get(i).getContent(), new HtmlHttpImageGetter(textTigan,"",true));
                                textTigan.setText(FormateSpannedContent(textTigan, mTaskList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                            }
                            // textTigan.setHtml(mTaskList.get(i).getContent(), new HtmlHttpImageGetter(textTigan,"",true));
                            textVis.setVisibility(View.GONE);
                            chioces_LV.setVisibility(View.GONE);
                        }

                        if (mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds().size() <= 0) {

                            mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds().add("1");

                        }

                        if (mTaskList.get(numbers).getLeftPath() != null && mTaskList.get(numbers).getLeftPath().size() > 0 || mTaskList.get(numbers).getRightPath() != null && mTaskList.get(numbers).getRightPath().size() > 0) {


                            if (mTaskList.get(numbers).getLeftPath().size() + mTaskList.get(numbers).getRightPath().size() <= 1) {
                                textYema.setText("1/1");
                                plusBtn.setText("加一页");
                            } else {
                                textYema.setText("1/2");
                                plusBtn.setText("删一页");
                            }
                            if (mTaskList.get(numbers).getLeftPath() != null && mTaskList.get(numbers).getLeftPath().size() > 0) {


                                paintView.initPaht(mTaskList.get(numbers).getLeftPath());


                            } else {

                                paintView.initPaht(mTaskList.get(numbers).getRightPath());

                            }


                        } else if (mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting().size() > 0) {

                            List<DrawPath> mleftList = new ArrayList<DrawPath>();
                            List<DrawPath> mrightList = new ArrayList<DrawPath>();

                            if (!StringUtils.isEmptyStr(mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting().get(0))) {

                                mleftList = new Gson().fromJson(mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting().get(0), new TypeToken<List<DrawPath>>() {
                                }.getType());

                                mTaskList.get(numbers).setLeftPath(mleftList);

                            }

                            if (mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting().size() > 1) {
                                if (!StringUtils.isEmptyStr(mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting().get(1))) {

                                    mrightList = new Gson().fromJson(mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting().get(1), new TypeToken<List<DrawPath>>() {
                                    }.getType());

                                }

                                mTaskList.get(numbers).setRightPath(mrightList);

                            }

                            if (mleftList.size() > 0 && mrightList.size() > 0) {
                                textYema.setText("1/2");
                                plusBtn.setText("删一页");

                            } else {
                                textYema.setText("1/1");
                                plusBtn.setText("加一页");

                            }
                            if (mleftList.size() > 0) {

                                List<DrawPath> drawPaths = new ArrayList<>();
                                DrawPath drawPath;

                                for (int k = 0; k < mleftList.size(); k++) {
                                    drawPath = new DrawPath();
                                    drawPath.path = new Path();
                                    drawPath.setTag(mleftList.get(k).getTag());
                                    drawPath.setPoints(mleftList.get(k).points);
                                    drawPath.init();
                                    drawPaths.add(drawPath);
                                }

                                paintView.initPaht(drawPaths);


                            } else {

                                List<DrawPath> drawPaths = new ArrayList<>();
                                DrawPath drawPath;

                                for (int k = 0; k < mrightList.size(); k++) {
                                    drawPath = new DrawPath();
                                    drawPath.path = new Path();
                                    drawPath.setTag(mleftList.get(k).getTag());
                                    drawPath.setPoints(mrightList.get(k).points);
                                    drawPath.init();
                                    drawPaths.add(drawPath);
                                }

                                paintView.initPaht(drawPaths);

                            }

                        } else {
                            textYema.setText("1/1");
                            plusBtn.setText("加一页");

                        }

                        break;

                    }
                    refreshPop();
                    loadingFm.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (isFinishing()) {
                    return;
                }
                fmshowNet.setVisibility(View.VISIBLE);
                loadingFm.setVisibility(View.GONE);
            }
        }, this);

    }


    class MyTopicClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            paintView.invalidataControler();
            ;
            switch (v.getId()) {
                case R.id.imgnet_id:

                    if (AndroidUtils.isNetworkAvailable(AnswerActivity.this)) {
                        fmshowNet.setVisibility(View.GONE);
                        loadingFm.setVisibility(View.VISIBLE);
                        getHomeWorkApi();
                    } else {
                        fmshowNet.setVisibility(View.VISIBLE);
                    }

                    break;
                case R.id.pop_layout_id:
                    //   popLayout.setBackgroundResource(R.drawable.bg_num);
                    refreshPop();
                    myPopwindow.showAsDropDown(popLayout, -312, -popLayout.getHeight());

//                    showTimeDialog();
                    break;


                case R.id.imgs_id:

                    if (textYema.getText().toString().equals("2/2")) {

                        //textYema.setText("1/2");
                        setPagingBtnEnable(false);
                        postImgBtnRightToserver();
                    }

                    break;
                case R.id.imgx_id:
                    if (textYema.getText().toString().equals("1/2")) {

                        //textYema.setText("2/2");
                        setPagingBtnEnable(false);
                        postImgBtnLeftToserver();

                    }


                    break;
                case R.id.plusye_id:
                    if (textYema.getText().toString().equals("1/1")) {
                        // textYema.setText("2/2");

                        if (mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds().size() == 1) {

                            mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds().add("2");

                        }
                        postImgBtnLeftToserver();

                    } else {
                        showDeletDialog();
                    }

                    break;

                case R.id.shou_id:
                    myPopwindow.dismiss();
                    //  popLayout.setBackgroundResource(R.drawable.radio_market_selector);
                    break;


                case R.id.radio_timu_id:
                    radioBtnTimu.setChecked(true);
                    radioBtnDaan.setChecked(false);


                    layoutTimubg.setVisibility(View.VISIBLE);
                    layoutDatibg.setVisibility(View.GONE);

                    radioBtnTimu.setTextColor(getResources().getColor(R.color.hui));
                    radioBtnDaan.setTextColor(getResources().getColor(R.color.zl_black));
                    break;
                case R.id.radio_daan_id:
                    radioBtnTimu.setChecked(false);
                    radioBtnDaan.setChecked(true);
                    layoutTimubg.setVisibility(View.GONE);
                    layoutDatibg.setVisibility(View.VISIBLE);

                    radioBtnTimu.setTextColor(getResources().getColor(R.color.zl_black));
                    radioBtnDaan.setTextColor(getResources().getColor(R.color.hui));

                    break;
                case R.id.commit_id:
                    radioBtnTimu.setChecked(true);
                    radioBtnDaan.setChecked(false);
                    if (!topAndDown) {
                        topAndDown = true;
                    }

                    if (btnNextAndCommit.getText().equals("提交作业")) {

                        showDialog();
                    } else {
                        postImgToserver();
                    }


                    break;

                case R.id.button_top_id:

                    //存上一页model

//                    layoutTimubg.setVisibility(View.VISIBLE);
//                    layoutDatibg.setVisibility(View.GONE);

                    if (topAndDown) {
                        topAndDown = false;
                    }
                    postImgToserver();


                    break;

                case R.id.tijiao_btn_id:
//                    tijiaoTag = false;
//                    postImgToserver();
                    myPopwindow.dismiss();
                    //   popLayout.setBackgroundResource(R.drawable.radio_market_selector);


                    showDialog();
                    break;

                default:
                    break;
            }
        }
    }

    private void setPagingBtnEnable(boolean isEnable) {
        imgsBtn.setEnabled(isEnable);
        imgXBtn.setEnabled(isEnable);
        ;
    }

    //提交第一页图片
    private void postImgBtnLeftToserver() {

//        Resources res = getResources();
//        Bitmap    bmp = BitmapFactory.decodeResource(res, R.drawable.bi_img_btn_normal);


        boolean leftBpathTag = true;
        if (mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().size() > 0) {
            if (!StringUtils.isEmptyStr(mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(0))) {

                if (paintView.savePath != null) {
                    leftBpathTag = !mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(0).equals(new Gson().toJson(paintView.savePath));
                }

            }
        }

        if (paintView.savePath != null && paintView.savePath.size() > 0 && leftBpathTag) {

            textPrompt.setText("正在保存答案");
            layoutSaveLoad.setVisibility(View.VISIBLE);
            ImageRequest request = new ImageRequest(LeApiApp.getPostImgUrl(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.i("xcz", "postImgBtnLeftToserver");
                            layoutSaveLoad.setVisibility(View.GONE);
                            try {

                                if (response.getInt("ret_code") == 0) {
                                    if (!StringUtils.isEmptyStr(response.getJSONObject("ret").getString("id"))) {

//                                        textYema.setText("2/2");
//                                        plusBtn.setText("删本页");
                                        mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds().set(0, response.getJSONObject("ret").getString("id"));

                                        if (paintView.savePath != null && paintView.savePath.size() > 0) {
                                            List<DrawPath> savePath = new ArrayList<>();
                                            savePath.addAll(paintView.savePath);
                                            // groupPath.addAll(savePath);

                                            mTaskList.get(numbers).setLeftPath(savePath);

                                        }

                                        if (paintView.savePath != null && paintView.savePath.size() > 0) {
                                            paintView.redo();
                                        }
                                        if (mTaskList.get(numbers).getRightPath() != null && mTaskList.get(numbers).getRightPath().size() > 0) {

                                            // linePathView.initPaht(mTaskList.get(numbers).getRightPath());
                                            List<DrawPath> drawPaths = new ArrayList<>();
                                            DrawPath drawPath;

                                            for (int k = 0; k < mTaskList.get(numbers).getRightPath().size(); k++) {
                                                drawPath = new DrawPath();
                                                drawPath.path = new Path();
                                                drawPath.setTag(mTaskList.get(numbers).getRightPath().get(k).getTag());
                                                drawPath.setPoints(mTaskList.get(numbers).getRightPath().get(k).points);
                                                drawPath.init();
                                                drawPaths.add(drawPath);
                                            }

                                            paintView.initPaht(drawPaths);


                                        } else {
                                            if (mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().size() > 1) {
                                                if (!StringUtils.isEmptyStr(mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(1))) {


                                                    List<DrawPath> mrightRight;
                                                    mrightRight = new Gson().fromJson(mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(1), new TypeToken<List<DrawPath>>() {
                                                    }.getType());

                                                    if (mrightRight.size() > 0) {
                                                        List<DrawPath> drawPaths = new ArrayList<>();
                                                        DrawPath drawPath;

                                                        for (int k = 0; k < mrightRight.size(); k++) {
                                                            drawPath = new DrawPath();
                                                            drawPath.path = new Path();
                                                            drawPath.setTag(mrightRight.get(k).getTag());
                                                            drawPath.setPoints(mrightRight.get(k).points);
                                                            drawPath.init();
                                                            drawPaths.add(drawPath);
                                                        }

                                                        paintView.initPaht(drawPaths);
                                                    }
                                                }
                                            }
                                        }
                                        textYema.setText("2/2");
                                        plusBtn.setText("删本页");
                                    }

                                } else {
                                    layoutSaveLoad.setVisibility(View.GONE);
                                    Toast.makeText(AnswerActivity.this, "" + response.getString("ret_msg"), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            layoutSaveLoad.setVisibility(View.GONE);
                            textYema.setText("2/2");
                            plusBtn.setText("删本页");
                            setPagingBtnEnable(true);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    textYema.setText("2/2");
                    plusBtn.setText("删本页");
                    setPagingBtnEnable(true);
                    layoutSaveLoad.setVisibility(View.GONE);
                    Toast.makeText(AnswerActivity.this, "网络链接异常,请检查你的网络", Toast.LENGTH_LONG).show();
                }
            }, paintView.mBitmap, "image", "image.jpg", "image/jpeg");
            request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, 1.0f));
            RequestManager.getInstance().addToRequestQueue(request, AnswerActivity.this);
        } else {

//            textYema.setText("2/2");
//            plusBtn.setText("删本页");
//            setPagingBtnEnable(true);
            if (paintView.savePath != null && paintView.savePath.size() > 0) {
                paintView.redo();
            }
            if (mTaskList.get(numbers).getRightPath() != null && mTaskList.get(numbers).getRightPath().size() > 0) {

                //          linePathView.initPaht(mTaskList.get(numbers).getRightPath());

                if (mTaskList.get(numbers).getRightPath() != null && mTaskList.get(numbers).getRightPath().size() > 0) {

                    List<DrawPath> drawPaths = new ArrayList<>();
                    DrawPath drawPath;

                    for (int k = 0; k < mTaskList.get(numbers).getRightPath().size(); k++) {
                        drawPath = new DrawPath();
                        drawPath.path = new Path();
                        drawPath.setTag(mTaskList.get(numbers).getRightPath().get(k).getTag());
                        drawPath.setPoints(mTaskList.get(numbers).getRightPath().get(k).points);
                        drawPath.init();
                        drawPaths.add(drawPath);
                    }

                    paintView.initPaht(drawPaths);


                }


            } else {
                if (mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().size() > 1) {
                    if (!StringUtils.isEmptyStr(mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(1))) {


                        List<DrawPath> mrightRight;
                        mrightRight = new Gson().fromJson(mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(1), new TypeToken<List<DrawPath>>() {
                        }.getType());

                        if (mrightRight.size() > 0) {
                            List<DrawPath> drawPaths = new ArrayList<>();
                            DrawPath drawPath;

                            for (int k = 0; k < mrightRight.size(); k++) {
                                drawPath = new DrawPath();
                                drawPath.path = new Path();
                                drawPath.setTag(mrightRight.get(k).getTag());
                                drawPath.setPoints(mrightRight.get(k).points);
                                drawPath.init();
                                drawPaths.add(drawPath);
                            }

                            paintView.initPaht(drawPaths);
                        }
                    }
                }
            }
            textYema.setText("2/2");
            plusBtn.setText("删本页");
            setPagingBtnEnable(true);
        }

    }


    //提交第二页图片
    private void postImgBtnRightToserver() {


        boolean rightBpathTag = true;
        if (mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().size() > 1) {
            if (!StringUtils.isEmptyStr(mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(1))) {

                if (paintView.savePath != null) {
                    rightBpathTag = !mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(1).equals(new Gson().toJson(paintView.savePath));
                }

            }
        }


        if (paintView.savePath != null && paintView.savePath.size() > 0 && rightBpathTag) {
            textPrompt.setText("正在保存答案");
            layoutSaveLoad.setVisibility(View.VISIBLE);
            ImageRequest request = new ImageRequest(LeApiApp.getPostImgUrl(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("xcz", "postImgBtnRightToserver");
                            layoutSaveLoad.setVisibility(View.GONE);
                            try {

                                if (response.getInt("ret_code") == 0) {
                                    if (!StringUtils.isEmptyStr(response.getJSONObject("ret").getString("id"))) {

                                        mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds().set(1, response.getJSONObject("ret").getString("id"));

                                        if (paintView.savePath != null && paintView.savePath.size() > 0) {
                                            List<DrawPath> savePath = new ArrayList<>();
                                            savePath.addAll(paintView.savePath);
//                                        groupPathRight.addAll(savePath);
                                            mTaskList.get(numbers).setRightPath(savePath);
                                        }

                                        if (paintView.savePath != null && paintView.savePath.size() > 0) {
                                            paintView.redo();
                                        }

                                        if (mTaskList.get(numbers).getLeftPath() != null && mTaskList.get(numbers).getLeftPath().size() > 0) {

                                            // linePathView.initPaht(mTaskList.get(numbers).getLeftPath());


                                            List<DrawPath> drawPaths = new ArrayList<>();
                                            DrawPath drawPath;

                                            for (int k = 0; k < mTaskList.get(numbers).getLeftPath().size(); k++) {
                                                drawPath = new DrawPath();
                                                drawPath.path = new Path();
                                                drawPath.setTag(mTaskList.get(numbers).getLeftPath().get(k).getTag());
                                                drawPath.setPoints(mTaskList.get(numbers).getLeftPath().get(k).points);
                                                drawPath.init();
                                                drawPaths.add(drawPath);
                                            }

                                            paintView.initPaht(drawPaths);

                                        } else {

                                            if (mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().size() > 0) {
                                                if (!StringUtils.isEmptyStr(mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(0))) {

                                                    List<DrawPath> mrightLeft;
                                                    mrightLeft = new Gson().fromJson(mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(0), new TypeToken<List<DrawPath>>() {
                                                    }.getType());
                                                    if (mrightLeft.size() > 0) {
                                                        List<DrawPath> drawPaths = new ArrayList<>();
                                                        DrawPath drawPath;

                                                        for (int k = 0; k < mrightLeft.size(); k++) {
                                                            drawPath = new DrawPath();
                                                            drawPath.path = new Path();
                                                            drawPath.setTag(mrightLeft.get(k).getTag());
                                                            drawPath.setPoints(mrightLeft.get(k).points);
                                                            drawPath.init();
                                                            drawPaths.add(drawPath);
                                                        }

                                                        paintView.initPaht(drawPaths);

                                                    }

                                                }
                                            }

                                        }

                                    }
                                    textYema.setText("1/2");
                                } else {
                                    layoutSaveLoad.setVisibility(View.GONE);
                                    int retcode = response.getInt("ret_code");
                                    String retMsg = response.getString("ret_msg");
                                    if (retcode == 1854) {
                                        //已下发

                                        ShowIsudDialog.showTimeDialog(AnswerActivity.this, ret.getStudentHomework().getHomework().getName(), ret.getStudentHomework().getId(), retcode, retMsg);

                                    } else if (retcode == 1856 || retcode == 1855) {
                                        //已提交

                                        ShowIsudDialog.showTimeDialog(AnswerActivity.this, ret.getStudentHomework().getHomework().getName(), ret.getStudentHomework().getId(), retcode, retMsg);

                                    } else {
                                        Toast.makeText(AnswerActivity.this, retMsg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            layoutSaveLoad.setVisibility(View.GONE);
                            setPagingBtnEnable(true);
                            textYema.setText("1/2");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    setPagingBtnEnable(true);
                    textYema.setText("1/2");
                    layoutSaveLoad.setVisibility(View.GONE);
                    Toast.makeText(AnswerActivity.this, "网络链接异常,请检查你的网络", Toast.LENGTH_LONG).show();
                }
            }, paintView.mBitmap, "image", "image.jpg", "image/jpeg");
            request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, 1.0f));
            RequestManager.getInstance().addToRequestQueue(request, AnswerActivity.this);
        } else {
//            setPagingBtnEnable(false);
//            textYema.setText("1/2");

            if (paintView.savePath != null && paintView.savePath.size() > 0) {
                paintView.redo();
            }

            if (mTaskList.get(numbers).getLeftPath() != null && mTaskList.get(numbers).getLeftPath().size() > 0) {

                //linePathView.initPaht(mTaskList.get(numbers).getLeftPath());

                List<DrawPath> drawPaths = new ArrayList<>();
                DrawPath drawPath;

                for (int k = 0; k < mTaskList.get(numbers).getLeftPath().size(); k++) {
                    drawPath = new DrawPath();
                    drawPath.path = new Path();
                    drawPath.setTag(mTaskList.get(numbers).getLeftPath().get(k).getTag());
                    drawPath.setPoints(mTaskList.get(numbers).getLeftPath().get(k).points);
                    drawPath.init();
                    drawPaths.add(drawPath);
                }

                paintView.initPaht(drawPaths);


            } else {

                if (mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().size() > 0) {
                    if (!StringUtils.isEmptyStr(mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(0))) {

                        List<DrawPath> mrightLeft;
                        mrightLeft = new Gson().fromJson(mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(0), new TypeToken<List<DrawPath>>() {
                        }.getType());
                        if (mrightLeft.size() > 0) {
                            List<DrawPath> drawPaths = new ArrayList<>();
                            DrawPath drawPath;

                            for (int k = 0; k < mrightLeft.size(); k++) {
                                drawPath = new DrawPath();
                                drawPath.path = new Path();
                                drawPath.setTag(mrightLeft.get(k).getTag());
                                drawPath.setPoints(mrightLeft.get(k).points);
                                drawPath.init();
                                drawPaths.add(drawPath);
                            }

                            paintView.initPaht(drawPaths);

                        }

                    }
                }

            }

            setPagingBtnEnable(true);
            textYema.setText("1/2");
        }
    }


    private void postAnswerToServer(List<List<DrawPath>> Path) {

        //上传服务器
        Map<String, String> map = new HashMap<>();
        map.put("stuHkId", ret.getStudentHomework().getId());
        map.put("homeworkId", ret.getStudentHomework().getHomeworkId());

        if (numbers >= 0 && numbers < ret.getQuestions().size()) {

            map.put("questionId", ret.getQuestions().get(numbers).getId());
            map.put("stuHkQuestionId", ret.getQuestions().get(numbers).getStudentHomeworkQuestion().getId());

        }
        map.put("asciimathAnswers", new Gson().toJson(list));
        map.put("mathmlAnswers", new Gson().toJson(list));
        map.put("time", "" + textRunTime.getTime());
        if (mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds().size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String imgId : mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds()) {
                if (imgId.equals("1") || imgId.equals("2")) {
                    continue;
                }
                stringBuilder.append(imgId).append(",");
            }
            if (stringBuilder.lastIndexOf(",") >= 0) {
                stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
            }
            map.put("images", stringBuilder.toString());
        }

        map.put("completionRate", "50");
        if (Path != null && Path.size() > 0) {
            if (Path.size() > 1) {

                List<String> handWritings = new ArrayList<String>(2);
                handWritings.add(new Gson().toJson(Path.get(0)));
                handWritings.add(new Gson().toJson(Path.get(1)));
                map.put("handWriting", new Gson().toJson(handWritings));
            } else {
                List<String> handWritings = new ArrayList<String>(1);
                handWritings.add(new Gson().toJson(Path.get(0)));

                map.put("handWriting", new Gson().toJson(handWritings));
            }

        }
        map.put("type", "1");

        textPrompt.setText("正在保存答案");
        layoutSaveLoad.setVisibility(View.VISIBLE);
        LeApiUtils.postString(LeApiApp.getPostWorkUrl(), map, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    layoutSaveLoad.setVisibility(View.GONE);
                    if (jsonObject.getInt("ret_code") == 0) {

                        //  Toast.makeText(AnswerActivity.this,"vvvv",Toast.LENGTH_SHORT).show();
                        if (list != null && list.size() > 0) {
                            list.clear();
                        }
                        //返回保存答案
                        if (!switchTag) {
                            switchTag = true;
                            if (actionSwitchListener != null) {
                                actionSwitchListener.RunAction();
                            }

                        } else {
                            if (!backTag) {

                                finish();
                            } else {
                                if (!tijiaoTag) {
                                    //showDialog();
                                    commitHomeWork();
                                    tijiaoTag = true;
                                } else {
                                    if (topAndDown) {
                                        nextWork();
                                    } else {
                                        topWork();
                                    }
                                }
                            }

                        }
                    } else {
                        if (jsonObject.getInt("ret_code") == 1854) {
                            //已下发

                            ShowIsudDialog.showTimeDialog(AnswerActivity.this, ret.getStudentHomework().getHomework().getName(), ret.getStudentHomework().getId(), 1854, jsonObject.getString("ret_msg"));

                        } else if (jsonObject.getInt("ret_code") == 1856 || jsonObject.getInt("ret_code") == 1855) {
                            //已提交

                            ShowIsudDialog.showTimeDialog(AnswerActivity.this, ret.getStudentHomework().getHomework().getName(), ret.getStudentHomework().getId(), 1856, jsonObject.getString("ret_msg"));

                        } else {
                            if (!backTag) {
                                showBackNetFailDialog();
                            } else {
                                Toast.makeText(AnswerActivity.this, "" + jsonObject.getString("ret_msg"), Toast.LENGTH_SHORT).show();

                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    backTag = true;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                layoutSaveLoad.setVisibility(View.GONE);
                if (!backTag) {
                    showBackNetFailDialog();
                } else {
                    Toast.makeText(AnswerActivity.this, "网络链接异常,请检查你的网络", Toast.LENGTH_LONG).show();
                }
            }
        }, this);

    }


    /**
     * 画板是否有修改
     *
     * @return
     */
    private boolean hasWritingChange() {
        boolean leftBpathTag = false;
        boolean rightBpathTag = false;
        if (mTaskList == null || numbers >= mTaskList.size()) {
            return false;
        }
        if (textYema.getText().equals("1/2") || textYema.getText().equals("1/1")) {
            if (mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().size() > 0 && !StringUtils.isEmptyStr(mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(0))) {
                leftBpathTag = !mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(0).equals(new Gson().toJson(paintView.savePath));
            } else {
                if (paintView.savePath != null && paintView.savePath.size() > 0) {

                    leftBpathTag = true;
                }
            }

            if (mTaskList.get(numbers).getRightPath() != null && mTaskList.get(numbers).getRightPath().size() > 0 && mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().size() > 1) {
                rightBpathTag = !mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(1).equals(new Gson().toJson(mTaskList.get(numbers).getRightPath()));
            }

        } else {

            if (mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().size() > 1 && !StringUtils.isEmptyStr(mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(1))) {
                rightBpathTag = !mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(1).equals(new Gson().toJson(paintView.savePath));
            } else {
                if (paintView.savePath != null && paintView.savePath.size() > 0) {

                    rightBpathTag = true;
                }
            }

            if (mTaskList.get(numbers).getLeftPath() != null && mTaskList.get(numbers).getLeftPath().size() > 0 && mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().size() > 0) {
                leftBpathTag = !mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(0).equals(new Gson().toJson(mTaskList.get(numbers).getLeftPath()));

            }
        }
        return (paintView.savePath != null && paintView.savePath.size() > 0 && (leftBpathTag || rightBpathTag));
    }

    private void postImgToserver() {
        //保存答案loading
        if (hasWritingChange()) {
            textPrompt.setText("正在保存答案");
            layoutSaveLoad.setVisibility(View.VISIBLE);
            // Toast.makeText(AnswerActivity.this, "bukong", Toast.LENGTH_SHORT).show();
            ImageRequest request = new ImageRequest(LeApiApp.getPostImgUrl(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                if (response.getInt("ret_code") == 0) {


                                    if (!StringUtils.isEmptyStr(response.getJSONObject("ret").getString("id"))) {


                                        List<List<DrawPath>> postGroup = new ArrayList<>();

                                        if (textYema.getText().equals("2/2")) {
                                            mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds().set(1, response.getJSONObject("ret").getString("id"));

                                            if (paintView.savePath != null && paintView.savePath.size() > 0) {
                                                List<DrawPath> savePath = new ArrayList<>();
                                                savePath.addAll(paintView.savePath);
//                                                if (groupPathRight!=null && groupPathRight.size()>0){
//                                                    groupPathRight.clear();
//                                                }
//
//                                                groupPathRight.addAll(savePath);

                                                mTaskList.get(numbers).setRightPath(savePath);

                                            }

                                        } else {
                                            mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds().set(0, response.getJSONObject("ret").getString("id"));

                                            if (paintView.savePath != null && paintView.savePath.size() > 0) {
                                                List<DrawPath> savePath = new ArrayList<>();
                                                savePath.addAll(paintView.savePath);
                                                mTaskList.get(numbers).setLeftPath(savePath);
                                            }
                                        }
                                        //如果没做过题getHandWriting为空
                                        List<String> leftList = new ArrayList<>();

                                        if (mTaskList.get(numbers).getLeftPath() != null && mTaskList.get(numbers).getLeftPath().size() > 0) {
                                            postGroup.add(mTaskList.get(numbers).getLeftPath());
                                            if (mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().size() > 0) {
                                                mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().set(0, new Gson().toJson(mTaskList.get(numbers).getLeftPath()));

                                            } else {


                                                leftList.add(new Gson().toJson(mTaskList.get(numbers).getLeftPath()));
                                                //   mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().add(new Gson().toJson(mTaskList.get(numbers).getLeftPath()));
                                                mTaskList.get(numbers).getStudentHomeworkQuestion().setHandWriting(leftList);
                                            }
                                        }
                                        if (mTaskList.get(numbers).getRightPath() != null && mTaskList.get(numbers).getRightPath().size() > 0) {
                                            postGroup.add(mTaskList.get(numbers).getRightPath());
                                            if (mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().size() > 1) {
                                                mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().set(1, new Gson().toJson(mTaskList.get(numbers).getRightPath()));
                                            } else {


                                                leftList.add(new Gson().toJson(mTaskList.get(numbers).getRightPath()));
                                                mTaskList.get(numbers).getStudentHomeworkQuestion().setHandWriting(leftList);
                                                //  mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().add(new Gson().toJson(mTaskList.get(numbers).getRightPath()));
                                            }

                                        }

                                        postAnswerToServer(postGroup);

                                    }
                                } else {

                                    layoutSaveLoad.setVisibility(View.GONE);
                                    if (!backTag) {
                                        showBackNetFailDialog();
                                    } else {
                                        Toast.makeText(AnswerActivity.this, "" + response.getString("ret_msg"), Toast.LENGTH_LONG).show();

                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                backTag = true;
                            }
                            layoutSaveLoad.setVisibility(View.GONE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    layoutSaveLoad.setVisibility(View.GONE);
                    if (!backTag) {
                        showBackNetFailDialog();
                    } else {
                        Toast.makeText(AnswerActivity.this, "网络链接异常,请检查你的网络", Toast.LENGTH_LONG).show();
                    }
                }
            }, paintView.mBitmap, "image", "image.jpg", "image/jpeg");
            request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, 1.0f));
            RequestManager.getInstance().addToRequestQueue(request, AnswerActivity.this);
//            paintView.saveToSDCard();
        } else {


            if (list != null && list.size() > 0) {

                Log.e("popwindow", " list.size() > 0 ");
                List<List<DrawPath>> postGroup = new ArrayList<>();
                postAnswerToServer(postGroup);

            } else {
                Log.e("popwindow", " switchTag is  " + switchTag);
                if (!switchTag) {
                    switchTag = true;
                    if (actionSwitchListener != null) {
                        actionSwitchListener.RunAction();
                    }


                } else {
                    if (!backTag) {

                        finish();
                    } else {
                        if (!tijiaoTag) {
                            //showDialog();
                            commitHomeWork();
                            tijiaoTag = true;
                        } else {
                            if (topAndDown) {
                                nextWork();
                            } else {
                                topWork();
                            }
                        }
                    }

                }

            }
        }


    }

    private void showDialog() {
        paintView.invalidataControler();
        ;
        int notDoCount = 0;
        for (int i = 0; i < mTaskList.size(); i++) {
            if (mTaskList.get(i).getType().equals("SINGLE_CHOICE")) {

                if (StringUtils.isEmptyStr(mTaskList.get(i).getStudentHomeworkAnswers().get(0).getContentAscii())) {

                    notDoCount++;
                }
            } else {

                if (i == numbers) {
                    if (mTaskList.get(i).getRightPath() != null
                            && mTaskList.get(i).getRightPath().size() == 0 && mTaskList.get(i).getLeftPath() != null
                            && mTaskList.get(i).getLeftPath().size() == 0 && paintView.savePath != null && paintView.savePath.size() == 0) {
                        notDoCount++;

                    }
                } else {
                    if (mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting() == null || mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting().size() == 0) {
                        notDoCount++;

                    }
                }
            }
        }
        if (notDoCount == 0) {
            //全做直接提交
            tijiaoTag = false;
            postImgToserver();
            return;
        }
        final Dialog dialog = new Dialog(this, R.style.MyCustomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog, null);
        dialog.setContentView(contentView);
        dialog.setCancelable(false);

        TextView textNotDo = (TextView) contentView.findViewById(R.id.nodo_id);
        Button buttonTJ = (Button) contentView.findViewById(R.id.tj_id);
        Button buttonHF = (Button) contentView.findViewById(R.id.fh_id);
        if (notDoCount != mTaskList.size()) {
            textNotDo.setText("您还有" + notDoCount + "题没回答");
            buttonTJ.setVisibility(View.VISIBLE);
        } else {

            buttonTJ.setVisibility(View.GONE);
            textNotDo.setText("您一题未做不能提交");

        }

        buttonHF.setOnClickListener(new View.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(View v) {
                                            paintView.invalidataControler();
                                            ;
                                            dialog.dismiss();
                                        }
                                    }

        );

        buttonTJ.setOnClickListener(new View.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(View v) {
                                            paintView.invalidataControler();
                                            ;
                                            tijiaoTag = false;
                                            postImgToserver();
                                            dialog.dismiss();

                                        }
                                    }

        );
        dialog.show();
    }

    private void showDeletDialog() {
        paintView.invalidataControler();
        ;
        final Dialog dialog = new Dialog(this, R.style.MyCustomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.deletedialog, null);
        dialog.setContentView(contentView);
        dialog.setCancelable(false);
        Button sureBtn = (Button) contentView.findViewById(R.id.sure_id);
        Button cancelBtn = (Button) contentView.findViewById(R.id.cancel_id);
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.invalidataControler();
                ;
                dialog.dismiss();

                if (textYema.getText().equals("2/2")) {

                    if (mTaskList.get(numbers) != null && mTaskList.get(numbers).getRightPath().size() > 0) {
                        mTaskList.get(numbers).getRightPath().clear();
                    }
                    if (mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().size() > 1) {
                        mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().remove(1);
                    }

                    if (mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds().size() > 1) {

                        mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds().remove(1);
                    }

                    if (paintView.savePath != null && paintView.savePath.size() > 0) {
                        paintView.redo();
                    }


                    if (mTaskList.get(numbers).getLeftPath() != null && mTaskList.get(numbers).getLeftPath().size() > 0) {

                        // paintView.initPaht(mTaskList.get(numbers).getLeftPath());
                        List<DrawPath> drawPaths = new ArrayList<>();
                        DrawPath drawPath;

                        for (int k = 0; k < mTaskList.get(numbers).getLeftPath().size(); k++) {
                            drawPath = new DrawPath();
                            drawPath.path = new Path();
                            drawPath.setTag(mTaskList.get(numbers).getLeftPath().get(k).getTag());
                            drawPath.setPoints(mTaskList.get(numbers).getLeftPath().get(k).points);
                            drawPath.init();
                            drawPaths.add(drawPath);
                        }

                        paintView.initPaht(drawPaths);


                    } else {

                        if (mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().size() > 0) {
                            if (!StringUtils.isEmptyStr(mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(0))) {

                                List<DrawPath> mrightLeft;
                                mrightLeft = new Gson().fromJson(mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(0), new TypeToken<List<DrawPath>>() {
                                }.getType());
                                if (mrightLeft.size() > 0) {
                                    List<DrawPath> drawPaths = new ArrayList<>();
                                    DrawPath drawPath;

                                    for (int k = 0; k < mrightLeft.size(); k++) {
                                        drawPath = new DrawPath();
                                        drawPath.path = new Path();
                                        drawPath.setTag(mrightLeft.get(k).getTag());
                                        drawPath.setPoints(mrightLeft.get(k).points);
                                        drawPath.init();
                                        drawPaths.add(drawPath);
                                    }

                                    paintView.initPaht(drawPaths);

                                }
                            }

                        }

                    }


                } else {


                    if (mTaskList.get(numbers) != null && mTaskList.get(numbers).getLeftPath().size() > 0) {
                        mTaskList.get(numbers).getLeftPath().clear();
                    }
                    if (mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().size() > 0) {
                        mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().remove(0);
                    }

                    if (mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds().size() > 0) {

                        mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds().remove(0);
                    }

                    if (paintView.savePath != null && paintView.savePath.size() > 0) {
                        paintView.redo();
                    }

                    if (mTaskList.get(numbers).getRightPath() != null && mTaskList.get(numbers).getRightPath().size() > 0) {

                        // paintView.initPaht(mTaskList.get(numbers).getRightPath());


                        List<DrawPath> drawPaths = new ArrayList<>();
                        DrawPath drawPath;

                        for (int k = 0; k < mTaskList.get(numbers).getRightPath().size(); k++) {
                            drawPath = new DrawPath();
                            drawPath.path = new Path();
                            drawPath.setTag(mTaskList.get(numbers).getRightPath().get(k).getTag());
                            drawPath.setPoints(mTaskList.get(numbers).getRightPath().get(k).points);
                            drawPath.init();
                            drawPaths.add(drawPath);
                        }
                        paintView.initPaht(drawPaths);

                        mTaskList.get(numbers).setLeftPath(mTaskList.get(numbers).getRightPath());
                        mTaskList.get(numbers).getRightPath().clear();


                    } else {

                        if (mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().size() > 1) {
                            if (!StringUtils.isEmptyStr(mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(1))) {

                                List<DrawPath> mrightRight;
                                mrightRight = new Gson().fromJson(mTaskList.get(numbers).getStudentHomeworkQuestion().getHandWriting().get(1), new TypeToken<List<DrawPath>>() {
                                }.getType());
                                if (mrightRight.size() > 0) {
                                    List<DrawPath> drawPaths = new ArrayList<>();
                                    DrawPath drawPath;

                                    for (int k = 0; k < mrightRight.size(); k++) {
                                        drawPath = new DrawPath();
                                        drawPath.path = new Path();
                                        drawPath.setTag(mrightRight.get(k).getTag());
                                        drawPath.setPoints(mrightRight.get(k).points);
                                        drawPath.init();
                                        drawPaths.add(drawPath);
                                    }
                                    paintView.initPaht(drawPaths);
                                }
                            }
                        }
                    }
                }
                textYema.setText("1/1");
                plusBtn.setText("加一页");

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.invalidataControler();
                ;
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void nextWork() {
        if (numbers < mTaskList.size() - 1) {
            ++numbers;

            if (numbers > 0) {
                btnTop.setVisibility(View.VISIBLE);
            }

            if (numbers == mTaskList.size() - 1) {
                btnNextAndCommit.setText("提交作业");

            }

            radioBtnTimu.setChecked(true);
            radioBtnDaan.setChecked(false);

            for (int i = 0; i < mTaskList.size(); i++) {
                if (i == numbers) {
                    if (topandownCount < mTaskList.size()) {

                        //循环加订正题

                        int workindex = 0;
                        int dzindex = 0;
                        for (int dindex = 0; dindex < mTaskList.size(); dindex++) {

                            if (mTaskList.get(dindex).isCorrectQuestion()) {
                                dzindex++;

                            } else {
                                workindex++;
                            }


                        }
                        int count = ++topandownCount;
                        if (mTaskList.get(numbers).isCorrectQuestion()) {

                            workCount.setText(mSubjectList.get(numbers).getTopic() + "/" + (dzindex > 0 ? workindex + "+" + dzindex : workindex));
                            textPopCount.setText(mSubjectList.get(numbers).getTopic() + "/" + (dzindex > 0 ? workindex + "+" + dzindex : workindex));
                        } else {
                            workCount.setText(count + "/" + (dzindex > 0 ? workindex + "+" + dzindex : workindex));
                            textPopCount.setText(count + "/" + (dzindex > 0 ? workindex + "+" + dzindex : workindex));
                        }

                        //  workCount.setText((++topandownCount) + "/" + mTaskList.size());
                    }
                    if (mTaskList.get(i).getType().equals("SINGLE_CHOICE")) {
                        if (chiocesList != null && chiocesList.size() > 0) {
                            chiocesList.clear();
                        }
                        if (mTaskList.get(i).isCorrectQuestion()) {
                            textRunTime.stopTimer();
                            textTigan.setText(FormateSpannedContent(textTigan, "[订正题]" + mTaskList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
//                            textTigan.setHtml("[订正题]" + mTaskList.get(i).getContent(), new HtmlHttpImageGetter(textTigan,"",true));
                        } else {
                            textRunTime.startTimer();
                            textTigan.setText(FormateSpannedContent(textTigan, mTaskList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
//                            textTigan.setHtml(mTaskList.get(i).getContent(), new HtmlHttpImageGetter(textTigan,"",true));
                        }
                        // textTigan.setHtml(mTaskList.get(i).getContent(), new HtmlHttpImageGetter(textTigan,"",true));
                        textVis.setVisibility(View.VISIBLE);

                        for (int j = 0; j < mTaskList.get(i).getChoices().length; j++) {

                            ChioceABCDInfo chioceABCDInfo = new ChioceABCDInfo();
                            chioceABCDInfo.setChoiceContent(mTaskList.get(i).getChoices()[j]);
                            if (j == 0) {
                                chioceABCDInfo.setChoiceName("A");

                            } else if (j == 1) {
                                chioceABCDInfo.setChoiceName("B");


                            } else if (j == 2) {
                                chioceABCDInfo.setChoiceName("C");

                            } else if (j == 3) {
                                chioceABCDInfo.setChoiceName("D");

                            } else if (j == 4) {
                                chioceABCDInfo.setChoiceName("E");

                            } else if (j == 5) {
                                chioceABCDInfo.setChoiceName("F");

                            }
                            chiocesList.add(chioceABCDInfo);
                        }
                        for (int m = 0; m < chiocesList.size(); m++) {
                            //ChioceABCDInfo chioceABCDInfo = new ChioceABCDInfo();
                            if (chiocesList.get(m).getChoiceName() != null && chiocesList.get(m).getChoiceName().equals(mTaskList.get(i).getStudentHomeworkAnswers().get(0).getContentAscii())) {
                                chiocesList.get(m).setChoiceTag(true);
                            } else {
                                chiocesList.get(m).setChoiceTag(false);
                            }
                        }
                        // numbers++;
                        textVis.setVisibility(View.VISIBLE);
                        chioces_LV.setVisibility(View.VISIBLE);
                        myChiocesAdapter.getList().clear();
                        myChiocesAdapter.getList().addAll(chiocesList);
                        addCHooseItem();
                        myChiocesAdapter.notifyDataSetChanged();
                    } else {
                        //  numbers++;
                        if (mTaskList.get(i).isCorrectQuestion()) {
                            textRunTime.stopTimer();
                            textTigan.setText(FormateSpannedContent(textTigan, "[订正题]" + mTaskList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
//                            textTigan.setHtml("[订正题]" + mTaskList.get(i).getContent(), new HtmlHttpImageGetter(textTigan,"",true));
                        } else {
                            textRunTime.startTimer();
                            textTigan.setText(FormateSpannedContent(textTigan, mTaskList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
//                            textTigan.setHtml(mTaskList.get(i).getContent(), new HtmlHttpImageGetter(textTigan,"",true));
                        }
                        //textTigan.setHtml(mTaskList.get(i).getContent(), new HtmlHttpImageGetter(textTigan,"",true));
                        textVis.setVisibility(View.GONE);
                        chioces_LV.setVisibility(View.GONE);
                    }


                    setTimutrue();
                    layoutTimubg.setVisibility(View.VISIBLE);
                    layoutDatibg.setVisibility(View.GONE);

                    //画下一题

                    if (paintView.savePath != null && paintView.savePath.size() > 0) {
                        paintView.redo();

                    }


                    if (mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds().size() <= 0) {

                        mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds().add("1");

                    }

                    if (mTaskList.get(numbers).getLeftPath() != null && mTaskList.get(numbers).getLeftPath().size() > 0 || mTaskList.get(numbers).getRightPath() != null && mTaskList.get(numbers).getRightPath().size() > 0) {


//                    if (mTaskList.get(numbers).getLeftPath().size() + mTaskList.get(numbers).getRightPath().size() == 1) {
//                        textYema.setText("1/1");
//                        plusBtn.setText("加一页");
//                    } else {
//                        textYema.setText("1/2");
//                        plusBtn.setText("删一页");
//                    }

                        if (mTaskList.get(numbers).getLeftPath().size() > 0 && mTaskList.get(numbers).getRightPath().size() > 0) {
                            textYema.setText("1/2");
                            plusBtn.setText("删一页");

                        } else {
                            textYema.setText("1/1");
                            plusBtn.setText("加一页");

                        }

                        if (mTaskList.get(numbers).getLeftPath() != null && mTaskList.get(numbers).getLeftPath().size() > 0) {


                            //linePathView.initPaht(mTaskList.get(numbers).getLeftPath());
                            List<DrawPath> drawPaths = new ArrayList<>();
                            DrawPath drawPath;

                            for (int k = 0; k < mTaskList.get(numbers).getLeftPath().size(); k++) {
                                drawPath = new DrawPath();
                                drawPath.path = new Path();
                                drawPath.setTag(mTaskList.get(numbers).getLeftPath().get(k).getTag());
                                drawPath.setPoints(mTaskList.get(numbers).getLeftPath().get(k).points);
                                drawPath.init();
                                drawPaths.add(drawPath);
                            }

                            paintView.initPaht(drawPaths);


                        } else {

                            // linePathView.initPaht(mTaskList.get(numbers).getRightPath());

                            List<DrawPath> drawPaths = new ArrayList<>();
                            DrawPath drawPath;

                            for (int k = 0; k < mTaskList.get(numbers).getRightPath().size(); k++) {
                                drawPath = new DrawPath();
                                drawPath.path = new Path();
                                drawPath.setTag(mTaskList.get(numbers).getRightPath().get(k).getTag());
                                drawPath.setPoints(mTaskList.get(numbers).getRightPath().get(k).points);
                                drawPath.init();
                                drawPaths.add(drawPath);
                            }

                            paintView.initPaht(drawPaths);


                        }


                    } else if (mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting().size() > 0) {

                        List<DrawPath> mleftList = new ArrayList<DrawPath>();
                        List<DrawPath> mrightList = new ArrayList<DrawPath>();

                        if (!StringUtils.isEmptyStr(mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting().get(0))) {

                            mleftList = new Gson().fromJson(mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting().get(0), new TypeToken<List<DrawPath>>() {
                            }.getType());

                            mTaskList.get(numbers).setLeftPath(mleftList);

                        }

                        if (mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting().size() > 1) {
                            if (!StringUtils.isEmptyStr(mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting().get(1))) {

                                mrightList = new Gson().fromJson(mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting().get(1), new TypeToken<List<DrawPath>>() {
                                }.getType());


                                mTaskList.get(numbers).setRightPath(mrightList);
                            }
                        }


//                    if (mleftList.size() + mrightList.size() == 1) {
//                        textYema.setText("1/1");
//                        plusBtn.setText("加一页");
//                    } else {
//                        textYema.setText("1/2");
//                        plusBtn.setText("删一页");
//                    }


                        if (mleftList.size() > 0 && mrightList.size() > 0) {
                            textYema.setText("1/2");
                            plusBtn.setText("删一页");

                        } else {
                            textYema.setText("1/1");
                            plusBtn.setText("加一页");

                        }


                        if (mleftList.size() > 0) {

                            List<DrawPath> drawPaths = new ArrayList<>();
                            DrawPath drawPath;

                            for (int k = 0; k < mleftList.size(); k++) {
                                drawPath = new DrawPath();
                                drawPath.path = new Path();
                                drawPath.setTag(mleftList.get(k).getTag());
                                drawPath.setPoints(mleftList.get(k).points);
                                drawPath.init();
                                drawPaths.add(drawPath);
                            }

                            paintView.initPaht(drawPaths);


                        } else {

                            List<DrawPath> drawPaths = new ArrayList<>();
                            DrawPath drawPath;

                            for (int k = 0; k < mrightList.size(); k++) {
                                drawPath = new DrawPath();
                                drawPath.path = new Path();
                                drawPath.setTag(mrightList.get(k).getTag());
                                drawPath.setPoints(mrightList.get(k).points);
                                drawPath.init();
                                drawPaths.add(drawPath);
                            }

                            paintView.initPaht(drawPaths);

                        }

                    } else {
                        textYema.setText("1/1");
                        plusBtn.setText("加一页");
                    }
                    break;
                }
            }
        }
        //  refreshPop();

        //提交作业提示哪些题未做

    }


    private void topWork() {
        if (numbers > 0) {
            --numbers;

            if (numbers == 0) {
                btnTop.setVisibility(View.INVISIBLE);
            }

            if (btnNextAndCommit.getText().equals("提交作业") && numbers != mTaskList.size() - 1) {
                btnNextAndCommit.setText("下一题");
            }

            radioBtnTimu.setChecked(true);
            radioBtnDaan.setChecked(false);

            // refreshPop();
            for (int i = 0; i < mTaskList.size(); i++) {
                if (i == numbers) {
                    if (topandownCount > 1) {

                        //循环加订正题

                        int workindex = 0;
                        int dzindex = 0;
                        for (int dindex = 0; dindex < mTaskList.size(); dindex++) {

                            if (mTaskList.get(dindex).isCorrectQuestion()) {
                                dzindex++;

                            } else {
                                workindex++;
                            }


                        }
                        int count = --topandownCount;
                        if (mTaskList.get(numbers).isCorrectQuestion()) {

                            workCount.setText(mSubjectList.get(numbers).getTopic() + "/" + (dzindex > 0 ? workindex + "+" + dzindex : workindex));
                            textPopCount.setText(mSubjectList.get(numbers).getTopic() + "/" + (dzindex > 0 ? workindex + "+" + dzindex : workindex));
                        } else {
                            workCount.setText(count + "/" + (dzindex > 0 ? workindex + "+" + dzindex : workindex));

                            textPopCount.setText(count + "/" + (dzindex > 0 ? workindex + "+" + dzindex : workindex));
                        }
                        //  workCount.setText(--topandownCount + "/" + (dzindex > 0 ? workindex + "+" + dzindex : workindex));
                        // workCount.setText((--topandownCount) + "/" + mTaskList.size());
                    }


                    if (mTaskList.get(i).getType().equals("SINGLE_CHOICE")) {
                        if (chiocesList != null && chiocesList.size() > 0) {
                            chiocesList.clear();
                        }
                        if (mTaskList.get(i).isCorrectQuestion()) {
                            textRunTime.stopTimer();
                            textTigan.setText(FormateSpannedContent(textTigan, "[订正题]" + mTaskList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
//                            textTigan.setHtml("[订正题]" + mTaskList.get(i).getContent(), new HtmlHttpImageGetter(textTigan,"",true));
                        } else {
                            textRunTime.startTimer();
                            textTigan.setText(FormateSpannedContent(textTigan, mTaskList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
//                            textTigan.setHtml(mTaskList.get(i).getContent(), new HtmlHttpImageGetter(textTigan,"",true));
                        }
                        // textTigan.setHtml(mTaskList.get(i).getContent(), new HtmlHttpImageGetter(textTigan,"",true));
                        textVis.setVisibility(View.VISIBLE);

                        for (int j = 0; j < mTaskList.get(i).getChoices().length; j++) {

                            ChioceABCDInfo chioceABCDInfo = new ChioceABCDInfo();
                            chioceABCDInfo.setChoiceContent(mTaskList.get(i).getChoices()[j]);
                            if (j == 0) {
                                chioceABCDInfo.setChoiceName("A");

                            } else if (j == 1) {
                                chioceABCDInfo.setChoiceName("B");


                            } else if (j == 2) {
                                chioceABCDInfo.setChoiceName("C");

                            } else if (j == 3) {
                                chioceABCDInfo.setChoiceName("D");

                            } else if (j == 4) {
                                chioceABCDInfo.setChoiceName("E");

                            } else if (j == 5) {
                                chioceABCDInfo.setChoiceName("F");

                            }
                            chiocesList.add(chioceABCDInfo);
                        }

                        for (int m = 0; m < chiocesList.size(); m++) {
                            //ChioceABCDInfo chioceABCDInfo = new ChioceABCDInfo();
                            if (chiocesList.get(m).getChoiceName() != null && chiocesList.get(m).getChoiceName().equals(mTaskList.get(i).getStudentHomeworkAnswers().get(0).getContentAscii())) {
                                chiocesList.get(m).setChoiceTag(true);
                            } else {
                                chiocesList.get(m).setChoiceTag(false);
                            }
                        }
                        textVis.setVisibility(View.VISIBLE);
                        chioces_LV.setVisibility(View.VISIBLE);
                        myChiocesAdapter.getList().clear();
                        myChiocesAdapter.getList().addAll(chiocesList);
                        addCHooseItem();
                        myChiocesAdapter.notifyDataSetChanged();
                    } else {
                        if (mTaskList.get(i).isCorrectQuestion()) {
                            textRunTime.stopTimer();
                            textTigan.setText(FormateSpannedContent(textTigan, "[订正题]" + mTaskList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
//                            textTigan.setHtml("[订正题]" + mTaskList.get(i).getContent(), new HtmlHttpImageGetter(textTigan,"",true));
                        } else {
                            textRunTime.startTimer();
                            textTigan.setText(FormateSpannedContent(textTigan, mTaskList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
//                            textTigan.setHtml(mTaskList.get(i).getContent(), new HtmlHttpImageGetter(textTigan,"",true));
                        }

                        //  textTigan.setHtml(mTaskList.get(i).getContent(), new HtmlHttpImageGetter(textTigan,"",true));
                        textVis.setVisibility(View.GONE);
                        chioces_LV.setVisibility(View.GONE);
                    }
                    layoutTimubg.setVisibility(View.VISIBLE);
                    layoutDatibg.setVisibility(View.GONE);

                    setTimutrue();
                    //画上一题

                    if (paintView.savePath != null && paintView.savePath.size() > 0) {
                        paintView.redo();

                    }

                    if (mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds() != null && mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds().size() <= 0) {

                        mTaskList.get(numbers).getStudentHomeworkQuestion().getAnswerImgIds().add("1");

                    }

                    if (mTaskList.get(numbers).getLeftPath() != null && mTaskList.get(numbers).getLeftPath().size() > 0 || mTaskList.get(numbers).getRightPath() != null && mTaskList.get(numbers).getRightPath().size() > 0) {


//                        if (mTaskList.get(numbers).getLeftPath().size() + mTaskList.get(numbers).getRightPath().size() == 1) {
//                            textYema.setText("1/1");
//                            plusBtn.setText("加一页");
//                        } else {
//                            textYema.setText("1/2");
//                            plusBtn.setText("删一页");
//                        }

                        if (mTaskList.get(numbers).getLeftPath().size() > 0 && mTaskList.get(numbers).getRightPath().size() > 0) {
                            textYema.setText("1/2");
                            plusBtn.setText("删一页");

                        } else {
                            textYema.setText("1/1");
                            plusBtn.setText("加一页");

                        }


                        if (mTaskList.get(numbers).getLeftPath() != null && mTaskList.get(numbers).getLeftPath().size() > 0) {


                            //  linePathView.initPaht(mTaskList.get(numbers).getLeftPath());

                            List<DrawPath> drawPaths = new ArrayList<>();
                            DrawPath drawPath;

                            for (int k = 0; k < mTaskList.get(numbers).getLeftPath().size(); k++) {
                                drawPath = new DrawPath();
                                drawPath.path = new Path();
                                drawPath.setTag(mTaskList.get(numbers).getLeftPath().get(k).getTag());
                                drawPath.setPoints(mTaskList.get(numbers).getLeftPath().get(k).points);
                                drawPath.init();
                                drawPaths.add(drawPath);
                            }

                            paintView.initPaht(drawPaths);
                        } else {

                            paintView.initPaht(mTaskList.get(numbers).getRightPath());

                            List<DrawPath> drawPaths = new ArrayList<>();
                            DrawPath drawPath;

                            for (int k = 0; k < mTaskList.get(numbers).getRightPath().size(); k++) {
                                drawPath = new DrawPath();
                                drawPath.path = new Path();
                                drawPath.setTag(mTaskList.get(numbers).getRightPath().get(k).getTag());
                                drawPath.setPoints(mTaskList.get(numbers).getRightPath().get(k).points);
                                drawPath.init();
                                drawPaths.add(drawPath);
                            }

                            paintView.initPaht(drawPaths);
                        }


                    } else if (mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting().size() > 0) {

                        List<DrawPath> mleftList = new ArrayList<DrawPath>();
                        List<DrawPath> mrightList = new ArrayList<DrawPath>();

                        if (!StringUtils.isEmptyStr(mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting().get(0))) {

                            mleftList = new Gson().fromJson(mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting().get(0), new TypeToken<List<DrawPath>>() {
                            }.getType());

                            mTaskList.get(numbers).setLeftPath(mleftList);

                        }

                        if (mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting() != null && mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting().size() > 1) {
                            if (!StringUtils.isEmptyStr(mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting().get(1))) {

                                mrightList = new Gson().fromJson(mTaskList.get(i).getStudentHomeworkQuestion().getHandWriting().get(1), new TypeToken<List<DrawPath>>() {
                                }.getType());

                                mTaskList.get(numbers).setLeftPath(mrightList);

                            }
                        }


                        if (mleftList.size() > 0 && mrightList.size() > 0) {
                            textYema.setText("1/2");
                            plusBtn.setText("删一页");
                        } else {
                            textYema.setText("1/1");
                            plusBtn.setText("加一页");
                        }
                        if (mleftList.size() > 0) {

                            List<DrawPath> drawPaths = new ArrayList<>();
                            DrawPath drawPath;

                            for (int k = 0; k < mleftList.size(); k++) {
                                drawPath = new DrawPath();
                                drawPath.path = new Path();
                                drawPath.setTag(mleftList.get(k).getTag());
                                drawPath.setPoints(mleftList.get(k).points);
                                drawPath.init();
                                drawPaths.add(drawPath);
                            }

                            paintView.initPaht(drawPaths);


                        } else {

                            List<DrawPath> drawPaths = new ArrayList<>();
                            DrawPath drawPath;

                            for (int k = 0; k < mrightList.size(); k++) {
                                drawPath = new DrawPath();
                                drawPath.path = new Path();
                                drawPath.setTag(mrightList.get(k).getTag());
                                drawPath.setPoints(mrightList.get(k).points);
                                drawPath.init();
                                drawPaths.add(drawPath);
                            }

                            paintView.initPaht(drawPaths);

                        }

                    } else {
                        textYema.setText("1/1");
                        plusBtn.setText("加一页");
                    }

                    break;
                }

            }

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        textRunTime.stopTimer();
        if (mHander != null) {
            mHander.removeCallbacks(countRunnable);
            mHander.removeCallbacks(autoSaveRunnable);
        }


    }


    public void commitHomeWork() {
        Map<String, String> map = new HashMap<>();
        map.put("hkId", ret.getStudentHomework().getHomework().getId());
        textPrompt.setText("正在提交答案...");
        layoutSaveLoad.setVisibility(View.VISIBLE);
        LeApiUtils.postString(LeApiApp.getCommitUrl(), map, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                layoutSaveLoad.setVisibility(View.GONE);
                try {
                    if (jsonObject.getInt("ret_code") == 0) {
                        if (mHander != null) {
                            mHander.removeCallbacks(countRunnable);
                            mHander.removeCallbacks(autoSaveRunnable);
                        }
                        layoutSaveLoad.setVisibility(View.GONE);
                        EventBus.getDefault().post(new Event<>(1));
                        finish();

                    } else if (jsonObject.getInt("ret_code") == 1854) {
                        //已下发
                        if (mHander != null) {
                            mHander.removeCallbacks(countRunnable);
                            mHander.removeCallbacks(autoSaveRunnable);
                        }
                        layoutSaveLoad.setVisibility(View.GONE);
                        ShowIsudDialog.showTimeDialog(AnswerActivity.this, ret.getStudentHomework().getHomework().getName(), ret.getStudentHomework().getId(), 1854, jsonObject.getString("ret_msg"));
                    } else if (jsonObject.getInt("ret_code") == 1856 || jsonObject.getInt("ret_code") == 1855) {
                        //已提交
                        if (mHander != null) {
                            mHander.removeCallbacks(countRunnable);
                            mHander.removeCallbacks(autoSaveRunnable);
                        }
                        layoutSaveLoad.setVisibility(View.GONE);
                        ShowIsudDialog.showTimeDialog(AnswerActivity.this, ret.getStudentHomework().getHomework().getName(), ret.getStudentHomework().getId(), 1856, jsonObject.getString("ret_msg"));
                    } else {
                        layoutSaveLoad.setVisibility(View.GONE);
                        Toast.makeText(AnswerActivity.this, jsonObject.getString("ret_msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    layoutSaveLoad.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                layoutSaveLoad.setVisibility(View.GONE);
                Toast.makeText(AnswerActivity.this, "网络链接异常,请检查你的网络", Toast.LENGTH_LONG).show();
            }
        }, this);

    }

    private void showTimeDialog() {
        if (mHander != null) {
            mHander.removeCallbacks(countRunnable);
        }
        layoutSaveLoad.setVisibility(View.GONE);
        dialog = new Dialog(this, R.style.MyCustomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialogtime, null);
        dialog.setContentView(contentView);
        dialog.setCancelable(false);
//        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                tijiaoTag = false;
//                postImgToserver();
//            }
//        });
        TextView textNotDo = (TextView) contentView.findViewById(R.id.linetime_id);
        textNotDo.setText("当前作业已截止" + "(" + MyWorkingAdapter.getdata1(ret.getStudentHomework().getHomework().getDeadline()) + ")");

        Button buttonHF = (Button) contentView.findViewById(R.id.sureBtn);
        buttonHF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                tijiaoTag = false;
//                postImgToserver();
                // finish();
                paintView.invalidataControler();
                ;
                //Toast.makeText(AnswerActivity.this,"back",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                EventBus.getDefault().post(new Event<>(1));
                finish();

            }
        });

        dialog.show();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (dialog != null && dialog.isShowing()) {
//                dialog.dismiss();
//                tijiaoTag = false;
//                postImgToserver();
            } else {
//                backTag = false;
//                postImgToserver();
                // finish();
                backEvent();
            }


            return false;

        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void backEvent() {
        paintView.invalidataControler();
        ;
        if (hasWritingChange() || (list != null && list.size() > 0)) {
            //
            showBackDialog();
        } else {
            finish();
        }
    }

    private void showBackDialog() {
        paintView.invalidataControler();
        ;
        final Dialog backdialog = new Dialog(this, R.style.MyCustomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog, null);
        backdialog.setContentView(contentView);
        backdialog.setCancelable(false);

        TextView textTitle = (TextView) contentView.findViewById(R.id.tv_dialog_title);
        TextView textNotDo = (TextView) contentView.findViewById(R.id.nodo_id);
        Button buttonTJ = (Button) contentView.findViewById(R.id.tj_id);
        Button buttonHF = (Button) contentView.findViewById(R.id.fh_id);
        textTitle.setText("作业未完成，确定退出作业？");
        buttonTJ.setText("取消");
        buttonHF.setText("确定");
        textNotDo.setText("已做题目将自动上传保存");
        buttonHF.setOnClickListener(new View.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(View v) {

                                            backdialog.dismiss();
                                            backTag = false;
                                            postImgToserver();
                                        }
                                    }

        );

        buttonTJ.setOnClickListener(new View.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(View v) {
                                            backTag = true;
                                            backdialog.dismiss();

                                        }
                                    }

        );
        backdialog.show();
    }

    private void showBackNetFailDialog() {
        paintView.invalidataControler();
        ;
        final Dialog backFailDialog = new Dialog(this, R.style.MyCustomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog, null);
        backFailDialog.setContentView(contentView);
        backFailDialog.setCancelable(false);

        TextView textTitle = (TextView) contentView.findViewById(R.id.tv_dialog_title);
        TextView textNotDo = (TextView) contentView.findViewById(R.id.nodo_id);
        Button buttonTJ = (Button) contentView.findViewById(R.id.tj_id);
        Button buttonHF = (Button) contentView.findViewById(R.id.fh_id);
        textTitle.setText("上传失败，请检查网络连接");
        buttonTJ.setText("确定退出");
        buttonHF.setText("重试");
        textNotDo.setText("如果继续退出，答案可能会丢失");
        buttonHF.setOnClickListener(new View.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(View v) {

                                            backFailDialog.dismiss();
                                            backTag = false;
                                            postImgToserver();
                                        }
                                    }

        );

        buttonTJ.setOnClickListener(new View.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(View v) {
                                            backFailDialog.dismiss();
                                            backTag = true;
                                            finish();
                                        }
                                    }

        );
        backFailDialog.show();
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
        radioBtnTimu.setChecked(true);
        radioBtnDaan.setChecked(false);
        radioBtnTimu.setTextColor(getResources().getColor(R.color.hui));
        radioBtnDaan.setTextColor(getResources().getColor(R.color.zl_black));

    }


    private ActionListener actionSwitchListener;

    public void setLoadworkActionListener(ActionListener actionListener) {
        this.actionSwitchListener = actionListener;
    }

    public interface ActionListener {
        public void RunAction();
    }

}
