package com.inkscreen;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.inkscreen.adapter.MyChiocesAdapter;
import com.inkscreen.adapter.MySubjectAdapter;
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
import com.inkscreen.utils.StringUtils;
import com.inkscreen.will.utils.widget.MultiGridView;
import com.yougy.ui.activity.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xcz on 2016/12/9.
 */
public class MyTobeIsudActivity extends Activity {

    private String homeWorkid, title;
    RadioButton radioBtnTimu, radioBtnDaan;
    LinearLayout  layoutDatibg;
    List<TobeInfo.Ret.Questions> mTobeList;
    TextView textTigan;
//    TextView textVis;
    LinearLayout chioces_LV;
    List<ChioceABCDInfo> chiocesList;
    MyChiocesAdapter myChiocesAdapter;
    private int numbers = 0;
    private ImageView imgDati1, imgDati2;
    MultiGridView mSubjectGV;
    MySubjectAdapter mySubjectAdapter;
    List<Subject> mSubjectList;
    TextView shouText, textTitle, textpopTitle, answeredTime;
    PopupWindow myPopwindow;
    LinearLayout popLayout;
    Button topBtn, dwonBtn, tiJbutton, shouButton;
    RelativeLayout relShow;
    LinearLayout answeredLY;
    Handler handler = new Handler();
    TextView workCount;
    //    private int topandownCount = 1;
    ImageView imgCancel;
    TextView textYeshu;
    ImageButton imgBtnLeft, imgBtnRight;
    FrameLayout fmshowNet;
    ImageView imgNet;
    FrameLayout frameZheZhao;
    ScrollView myscrollView;
    FrameLayout loadingFm;
    ImageView imgWatch;
    TextView textPopCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tobeisud);
        homeWorkid = getIntent().getStringExtra("homeWorkId");
        title = getIntent().getStringExtra("title");

        initView();
        initPopview();

        showTopAndDown();
        if (AndroidUtils.isNetworkAvailable(MyTobeIsudActivity.this)) {
            fmshowNet.setVisibility(View.GONE);
            loadingFm.setVisibility(View.VISIBLE);
            getRecHomeWorkApi();
        } else {
            fmshowNet.setVisibility(View.VISIBLE);
        }


    }

    private void initView() {
        chiocesList = new ArrayList<>();
        mTobeList = new ArrayList<>();
        radioBtnTimu = (RadioButton) findViewById(R.id.radio_timu_id);
        radioBtnDaan = (RadioButton) findViewById(R.id.radio_daan_id);
        textYeshu = (TextView) findViewById(R.id.yeshu_id);

        layoutDatibg = (LinearLayout) findViewById(R.id.dati_id);
//        layoutTimubg = (LinearLayout) findViewById(R.id.layout_id);
        frameZheZhao = (FrameLayout) findViewById(R.id.zhezhao_id);
//        frameZheZhao.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                relShow.setVisibility(View.VISIBLE);
//
//                showTopAndDown();
//            }
//        });


        imgWatch = (ImageView) findViewById(R.id.watch_id);
        loadingFm = (FrameLayout) findViewById(R.id.loading_id);
        myscrollView = (ScrollView) findViewById(R.id.scrollView_id);

        fmshowNet = (FrameLayout) findViewById(R.id.show_id);
        imgNet = (ImageView) findViewById(R.id.imgnet_id);

//        textVis = (TextView) findViewById(R.id.textVis_id);
        textTigan = (TextView) findViewById(R.id.tigan_id);
        imgDati1 = (ImageView) findViewById(R.id.img_dati1);
        imgDati2 = (ImageView) findViewById(R.id.img_dati2);
        imgBtnLeft = (ImageButton) findViewById(R.id.imgbutton_left_id);
        imgBtnRight = (ImageButton) findViewById(R.id.imgbutton_right_id);
        topBtn = (Button) findViewById(R.id.shang_ti);
        dwonBtn = (Button) findViewById(R.id.xia_ti);
        topBtn.setOnClickListener(new MyTopicClickListener());
        dwonBtn.setOnClickListener(new MyTopicClickListener());
        chioces_LV = (LinearLayout) findViewById(R.id.choices_list_id);
        relShow = (RelativeLayout) findViewById(R.id.Rel_show_id);
        textTitle = (TextView) findViewById(R.id.title);
        answeredTime = (TextView) findViewById(R.id.answered_time_id);
        imgCancel = (ImageView) findViewById(R.id.cancel_id);

        radioBtnTimu.setOnClickListener(new MyTopicClickListener());
        radioBtnDaan.setOnClickListener(new MyTopicClickListener());
        imgCancel.setOnClickListener(new MyTopicClickListener());
        imgBtnLeft.setOnClickListener(new MyTopicClickListener());
        imgBtnRight.setOnClickListener(new MyTopicClickListener());
        imgNet.setOnClickListener(new MyTopicClickListener());
        radioBtnTimu.setChecked(true);
        radioBtnTimu.setTextColor(getResources().getColor(R.color.hui));
        textTitle.setText(title);
        workCount = (TextView) findViewById(R.id.top_id);


        popLayout = (LinearLayout) findViewById(R.id.pop_layout_id);
        popLayout.setOnClickListener(new MyTopicClickListener());
        myChiocesAdapter = new MyChiocesAdapter(MyTobeIsudActivity.this);
        myChiocesAdapter.setList(new ArrayList<ChioceABCDInfo>());
        myChiocesAdapter.SetParentView(chioces_LV);
        // chioces_LV.setAdapter(myChiocesAdapter);
        myscrollView.setVisibility(View.VISIBLE);


        myscrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        relShow.setVisibility(View.VISIBLE);
                        showTopAndDown();
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:

                }
                return false;
            }
        });

    }


    class MyTopicClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.imgnet_id:
                    if (AndroidUtils.isNetworkAvailable(MyTobeIsudActivity.this)) {
                        fmshowNet.setVisibility(View.GONE);
                        getRecHomeWorkApi();
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
                    if (textYeshu.getText().equals("2/2") && mTobeList.get(numbers).getStudentHomeworkQuestion().getAnswerImgs() != null && mTobeList.get(numbers).getStudentHomeworkQuestion().getAnswerImgs().get(0) != null) {
                        textYeshu.setText("1/2");
                        imgDati1.setVisibility(View.VISIBLE);
                        imgDati2.setVisibility(View.GONE);
                        ImageLoadUtil.getInstance().loadImageActivity(MyTobeIsudActivity.this,
                                mTobeList.get(numbers).getStudentHomeworkQuestion().getAnswerImgs().get(0), R.drawable.image_fail, imgDati1);
                    }

                    break;
                case R.id.imgbutton_right_id:
                    if (textYeshu.getText().equals("1/2") && mTobeList.get(numbers).getStudentHomeworkQuestion().getAnswerImgs() != null && mTobeList.get(numbers).getStudentHomeworkQuestion().getAnswerImgs() != null && mTobeList.get(numbers).getStudentHomeworkQuestion().getAnswerImgs().get(1) != null) {
                        textYeshu.setText("2/2");
                        imgDati1.setVisibility(View.GONE);
                        imgDati2.setVisibility(View.VISIBLE);
                        ImageLoadUtil.getInstance().loadImageActivity(MyTobeIsudActivity.this,
                                mTobeList.get(numbers).getStudentHomeworkQuestion().getAnswerImgs().get(1), R.drawable.image_fail, imgDati2);
                    }


                    break;


                case R.id.radio_timu_id:
                    radioBtnTimu.setChecked(true);
                    radioBtnDaan.setChecked(false);


                    myscrollView.setVisibility(View.VISIBLE);
                    layoutDatibg.setVisibility(View.GONE);
                    radioBtnTimu.setTextColor(getResources().getColor(R.color.hui));
                    radioBtnDaan.setTextColor(getResources().getColor(R.color.zl_black));


                    break;
                case R.id.radio_daan_id:
                    radioBtnTimu.setChecked(false);
                    radioBtnDaan.setChecked(true);
                    myscrollView.setVisibility(View.GONE);
                    layoutDatibg.setVisibility(View.VISIBLE);

                    radioBtnTimu.setTextColor(getResources().getColor(R.color.zl_black));
                    radioBtnDaan.setTextColor(getResources().getColor(R.color.hui));
                    break;


                case R.id.xia_ti:
                    if (numbers < mTobeList.size() - 1) {
                        numbers += 1;
                        topWork();
                    }
                    break;


                case R.id.shang_ti:

//                    topBtn.setTextColor(getResources().getColor(R.color.zl_white));
                    if (numbers > 0) {
                        numbers -= 1;
                        topWork();
                    }
//                    topBtn.setTextColor(getResources().getColor(R.color.zl_black));
                    break;
                case R.id.shou_id:
                    myPopwindow.dismiss();
                    break;
                case R.id.cancel_id:

                    finish();
                    break;

                default:
                    break;
            }
        }
    }


    private void initPopview() {
        View popupView = getLayoutInflater().inflate(R.layout.answer_pop, null);
        mSubjectGV = (MultiGridView) popupView.findViewById(R.id.subject_id);
        mSubjectList = new ArrayList<>();
        mySubjectAdapter = new MySubjectAdapter(this);
        mySubjectAdapter.setItemclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v.getTag() != null) {
                    myPopwindow.dismiss();
                    int position = (int) v.getTag();
                    numbers = position;
//                    topandownCount = position + 1;
                    topWork();

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
        shouButton.setOnClickListener(new MyTopicClickListener());

        myPopwindow = new PopupWindow(popupView, 785, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        tiJbutton.setVisibility(View.INVISIBLE);
//        answeredLY.setVisibility(View.INVISIBLE);
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
//
//                myPopwindow.dismiss();
//                numbers = position + 1;
//                topandownCount = position+2;
//                topWork();
//
//            }
//        });

    }


    private void topWork() {
        if (numbers >= 0 && numbers < mTobeList.size()) {
            // refreshPop();
//            topandownCount=numbers+1;
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
            for (int i = 0; i < mTobeList.size(); i++) {
                if (i == numbers) {
//                    if (topandownCount > 0)
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
                        if (chiocesList != null && chiocesList.size() > 0) {
                            chiocesList.clear();
                        }

                        if (mTobeList.get(i).isCorrectQuestion()) {
                            textTigan.setText(FormateSpannedContent(textTigan, "[订正题]" + mTobeList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                        } else {
                            textTigan.setText(FormateSpannedContent(textTigan, mTobeList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                        }
//                        textVis.setVisibility(View.VISIBLE);

                        for (int j = 0; j < mTobeList.get(i).getChoices().length; j++) {
                            String myAnswer = mTobeList.get(i).getStudentHomeworkAnswers().get(0).getContent();
                            ChioceABCDInfo chioceABCDInfo = new ChioceABCDInfo();
                            chioceABCDInfo.setChoiceContent(mTobeList.get(i).getChoices()[j]);
                            if (j == 0) {
                                chioceABCDInfo.setChoiceName("A");
                                if (myAnswer.equals("A")) {
                                    chioceABCDInfo.setChoiceTag(true);
                                } else {
                                    chioceABCDInfo.setChoiceTag(false);
                                }
                            } else if (j == 1) {
                                chioceABCDInfo.setChoiceName("B");
                                if (myAnswer.equals("B")) {
                                    chioceABCDInfo.setChoiceTag(true);
                                } else {
                                    chioceABCDInfo.setChoiceTag(false);
                                }

                            } else if (j == 2) {
                                chioceABCDInfo.setChoiceName("C");
                                if (myAnswer.equals("C")) {
                                    chioceABCDInfo.setChoiceTag(true);
                                } else {
                                    chioceABCDInfo.setChoiceTag(false);
                                }
                            } else if (j == 3) {
                                chioceABCDInfo.setChoiceName("D");
                                if (myAnswer.equals("D")) {
                                    chioceABCDInfo.setChoiceTag(true);
                                } else {
                                    chioceABCDInfo.setChoiceTag(false);
                                }
                            } else if (j == 4) {
                                chioceABCDInfo.setChoiceName("E");
                                if (myAnswer.equals("E")) {
                                    chioceABCDInfo.setChoiceTag(true);
                                } else {
                                    chioceABCDInfo.setChoiceTag(false);
                                }
                            } else if (j == 5) {
                                chioceABCDInfo.setChoiceName("F");
                                if (myAnswer.equals("F")) {
                                    chioceABCDInfo.setChoiceTag(true);
                                } else {
                                    chioceABCDInfo.setChoiceTag(false);
                                }
                            }
                            chiocesList.add(chioceABCDInfo);
                        }

//                        if(numbers==0){
//                            ++numbers;
//                        }
//                        textVis.setVisibility(View.VISIBLE);
                        chioces_LV.setVisibility(View.VISIBLE);
                        myChiocesAdapter.getList().clear();
                        myChiocesAdapter.getList().addAll(chiocesList);
                        addCHooseItem();
                        myChiocesAdapter.notifyDataSetChanged();
                        imgDati1.setVisibility(View.GONE);
                        imgDati2.setVisibility(View.GONE);

                        if (mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs() != null && mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs().size() > 0) {

                            if (mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs().size() == 2) {
                                textYeshu.setText("1/2");
                                imgDati1.setVisibility(View.VISIBLE);
                                ImageLoadUtil.getInstance().loadImageActivity(MyTobeIsudActivity.this,
                                        mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs().get(0), R.drawable.image_fail, imgDati1);
                            } else {

                                if (mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs().size() == 1) {
                                    textYeshu.setText("1/1");
                                    imgDati1.setVisibility(View.VISIBLE);
                                    ImageLoadUtil.getInstance().loadImageActivity(MyTobeIsudActivity.this,
                                            mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs().get(0), R.drawable.image_fail, imgDati1);

                                } else {
                                    textYeshu.setText("1/1");
                                }

                            }


                        } else {
                            textYeshu.setText("1/1");
                        }

                    } else {
                        if (mTobeList.get(i).isCorrectQuestion()) {
                            textTigan.setText(FormateSpannedContent(textTigan, "[订正题]" + mTobeList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                        } else {
                            textTigan.setText(FormateSpannedContent(textTigan, mTobeList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                        }
//                        textVis.setVisibility(View.GONE);
                        chioces_LV.setVisibility(View.GONE);
//                        if(numbers==0){
//                            ++numbers;
//                        }
                        imgDati1.setVisibility(View.GONE);
                        imgDati2.setVisibility(View.GONE);

                        if (mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs() != null && mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs().size() > 0) {

                            if (mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs().size() == 2) {
                                textYeshu.setText("1/2");
                                imgDati1.setVisibility(View.VISIBLE);
                                ImageLoadUtil.getInstance().loadImageActivity(MyTobeIsudActivity.this,
                                        mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs().get(0), R.drawable.image_fail, imgDati1);
                            } else {

                                if (mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs().size() == 1) {
                                    textYeshu.setText("1/1");
                                    imgDati1.setVisibility(View.VISIBLE);
                                    ImageLoadUtil.getInstance().loadImageActivity(MyTobeIsudActivity.this,
                                            mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs().get(0), R.drawable.image_fail, imgDati1);

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


    private void getRecHomeWorkApi() {

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
                    if (result.getResult().getRet().getStudentHomework().getHomeworkTime() == 0) {
                        answeredTime.setText("用时:--");
                    } else {
                        answeredTime.setText("用时:" + (result.getResult().getRet().getStudentHomework().getHomeworkTime() % 3600 / 60) + "'" + (result.getResult().getRet().getStudentHomework().getHomeworkTime() % 60) + "''");
                    }
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
//                    workCount.setText(topandownCount + "/" + mTobeList.size());
                    textPopCount.setText((numbers + 1) + "/" + (dzindex > 0 ? workindex + "+" + dzindex : workindex));

                    refreshPop();
                    for (int i = 0; i < mTobeList.size(); i++) {
                        if (mTobeList.get(i).getType().equals("SINGLE_CHOICE")) {
                            //answeredTime.setText(mTobeList.get(i).get);

                            if (mTobeList.get(i).isCorrectQuestion()) {
//                                textTigan.setText("[订正题]"+mTobeList.get(i).getContent(), new HtmlHttpImageGetter(textTigan));
                                textTigan.setText(FormateSpannedContent(textTigan, "[订正题]" + mTobeList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                            } else {
                                textTigan.setText(FormateSpannedContent(textTigan, mTobeList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                            }

//                            textVis.setVisibility(View.VISIBLE);

                            for (int j = 0; j < mTobeList.get(i).getChoices().length; j++) {
                                String myAnswer = mTobeList.get(i).getStudentHomeworkAnswers().get(0).getContent();

                                ChioceABCDInfo chioceABCDInfo = new ChioceABCDInfo();
                                chioceABCDInfo.setChoiceContent(mTobeList.get(i).getChoices()[j]);
                                if (j == 0) {
                                    chioceABCDInfo.setChoiceName("A");
                                    if (myAnswer.equals("A")) {
                                        chioceABCDInfo.setChoiceTag(true);
                                    } else {
                                        chioceABCDInfo.setChoiceTag(false);
                                    }
                                } else if (j == 1) {
                                    chioceABCDInfo.setChoiceName("B");
                                    if (myAnswer.equals("B")) {
                                        chioceABCDInfo.setChoiceTag(true);
                                    } else {
                                        chioceABCDInfo.setChoiceTag(false);
                                    }

                                } else if (j == 2) {
                                    chioceABCDInfo.setChoiceName("C");
                                    if (myAnswer.equals("C")) {
                                        chioceABCDInfo.setChoiceTag(true);
                                    } else {
                                        chioceABCDInfo.setChoiceTag(false);
                                    }
                                } else if (j == 3) {
                                    chioceABCDInfo.setChoiceName("D");
                                    if (myAnswer.equals("D")) {
                                        chioceABCDInfo.setChoiceTag(true);
                                    } else {
                                        chioceABCDInfo.setChoiceTag(false);
                                    }
                                } else if (j == 4) {
                                    chioceABCDInfo.setChoiceName("E");
                                    if (myAnswer.equals("E")) {
                                        chioceABCDInfo.setChoiceTag(true);
                                    } else {
                                        chioceABCDInfo.setChoiceTag(false);
                                    }
                                } else if (j == 5) {
                                    chioceABCDInfo.setChoiceName("F");
                                    if (myAnswer.equals("F")) {
                                        chioceABCDInfo.setChoiceTag(true);
                                    } else {
                                        chioceABCDInfo.setChoiceTag(false);
                                    }
                                }
                                chiocesList.add(chioceABCDInfo);
                            }
//                            textVis.setVisibility(View.VISIBLE);
                            chioces_LV.setVisibility(View.VISIBLE);
//                            numbers++;
                            myChiocesAdapter.getList().clear();
                            myChiocesAdapter.getList().addAll(chiocesList);
                            addCHooseItem();
                            myChiocesAdapter.notifyDataSetChanged();
                            imgDati1.setVisibility(View.GONE);
                            imgDati2.setVisibility(View.GONE);
                            if (mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs() != null && mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs().size() > 0) {

                                if (mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs().size() == 2) {
                                    textYeshu.setText("1/2");
                                    imgDati1.setVisibility(View.VISIBLE);
                                    ImageLoadUtil.getInstance().loadImageActivity(MyTobeIsudActivity.this,
                                            mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs().get(0), R.drawable.image_fail, imgDati1);
                                } else {

                                    if (mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs().size() == 1) {
                                        textYeshu.setText("1/1");
                                        imgDati1.setVisibility(View.VISIBLE);
                                        ImageLoadUtil.getInstance().loadImageActivity(MyTobeIsudActivity.this,
                                                mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs().get(0), R.drawable.image_fail, imgDati1);

                                    } else {
                                        textYeshu.setText("1/1");
                                    }

                                }


                            } else {
                                textYeshu.setText("1/1");

                            }


                        } else {
//                            numbers++;

                            if (mTobeList.get(i).isCorrectQuestion()) {
//                                textTigan.setText("[订正题]"+mTobeList.get(i).getContent(), new HtmlHttpImageGetter(textTigan));
                                textTigan.setText(FormateSpannedContent(textTigan, "[订正题]" + mTobeList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                            } else {
                                textTigan.setText(FormateSpannedContent(textTigan, mTobeList.get(i).getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
                            }

//                            ImageLoaderManager.loadImageActivity(MyTobeIsudActivity.this,
//                                    mTobeList.get(i).getStudentHomeworkQuestion().getSolvingImg(), R.drawable.imgerror, imgDati);
//                            textVis.setVisibility(View.GONE);
                            chioces_LV.setVisibility(View.GONE);
                            imgDati1.setVisibility(View.GONE);
                            imgDati2.setVisibility(View.GONE);

                            if (mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs() != null && mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs().size() > 0) {

                                if (mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs().size() == 2) {
                                    textYeshu.setText("1/2");
                                    imgDati1.setVisibility(View.VISIBLE);
                                    ImageLoadUtil.getInstance().loadImageActivity(MyTobeIsudActivity.this,
                                            mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs().get(0), R.drawable.image_fail, imgDati1);
                                } else {

                                    if (mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs().size() == 1) {
                                        textYeshu.setText("1/1");
                                        imgDati1.setVisibility(View.VISIBLE);
                                        ImageLoadUtil.getInstance().loadImageActivity(MyTobeIsudActivity.this,
                                                mTobeList.get(i).getStudentHomeworkQuestion().getAnswerImgs().get(0), R.drawable.image_fail, imgDati1);

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
                    imgWatch.setVisibility(View.VISIBLE);
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


    private void showTopAndDown() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                relShow.setVisibility(View.GONE);
            }
        }, 3000);

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

            if (mTobeList.get(i).getType().equals("SINGLE_CHOICE")) {
                //选择题
                if (!StringUtils.isEmptyStr(mTobeList.get(i).getStudentHomeworkAnswers().get(0).getContentAscii())) {
                    subject.setDoflag(true);
                }

            } else {
                //解答题&填空题
                if (mTobeList.get(i).getStudentHomeworkQuestion().getHandWriting() != null && mTobeList.get(i).getStudentHomeworkQuestion().getHandWriting().size() > 0) {
                    subject.setDoflag(true);
                } else {
                    subject.setDoflag(false);
                }
            }


            if (i == numbers) {
                subject.setTitelflag(true);
                subject.setWriteorblacktag(true);
            } else {
                subject.setTitelflag(false);
            }

            mSubjectList.add(subject);
        }

        mySubjectAdapter.getList().clear();
        mySubjectAdapter.getList().addAll(mSubjectList);
        mySubjectAdapter.notifyDataSetChanged();

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


    @Override
    protected void onDestroy() {
        super.onDestroy();


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
        myscrollView.setVisibility(View.VISIBLE);
        layoutDatibg.setVisibility(View.GONE);
    }

}
