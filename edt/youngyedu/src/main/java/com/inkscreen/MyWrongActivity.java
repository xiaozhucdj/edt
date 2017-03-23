package com.inkscreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.inkscreen.adapter.MyWrongDetailAdapter;
import com.inkscreen.model.ChioceABCDInfo;
import com.inkscreen.model.WrongInfo;
import com.inkscreen.utils.DeviceInfoUtil;
import com.inkscreen.utils.ImageLoadUtil;
import com.inkscreen.utils.MyImageGetter;
import com.yougy.ui.activity.R;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xcz on 2016/12/8.
 */
public class MyWrongActivity extends Activity {

    private WrongInfo.Ret.Items items;
    RadioButton radioTimu, radioMyRspd, radioSureRspd, radioAnalysis;
    LinearLayout linearTimu, linearMyRspd, linearSureRspd, linearAnalysis;
    ImageView cancelImg;
    TextView htmlTextTiGan, htmlTextAnalysis;

    HtmlTextView textMyrespond;
    LinearLayout chioces_LV;
    MyWrongDetailAdapter myChiocesAdapter;
    List<ChioceABCDInfo> chiocesList;
    TextView textLianXi, textCuoTi, textRenShu;
    LinearLayout anserPage;
    ImageView aswerImg;
    TextView textSureRspd;
    View lineView;
    View lineAnswer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mywrong);
        Intent intent = this.getIntent();
        items = (WrongInfo.Ret.Items) intent.getSerializableExtra("item");
        initView();


    }

    private void initView() {
        chiocesList = new ArrayList<>();


        lineAnswer = (View) findViewById(R.id.line_id);
        lineView = (View) findViewById(R.id.lineView_id);
        textLianXi = (TextView) findViewById(R.id.textlianxi_id);
        textCuoTi = (TextView) findViewById(R.id.textcuoti_id);
        textRenShu = (TextView) findViewById(R.id.textrenshu_id);
        aswerImg = (ImageView) findViewById(R.id.answerimg_id);

        anserPage = (LinearLayout) findViewById(R.id.answerpag_id);
        chioces_LV = (LinearLayout) findViewById(R.id.choices_list_id);
        htmlTextAnalysis = (TextView) findViewById(R.id.textanalysis_id);
        htmlTextAnalysis.setMovementMethod(ScrollingMovementMethod.getInstance());

        textSureRspd = (TextView) findViewById(R.id.text_surerespond);

        textSureRspd.setMovementMethod(ScrollingMovementMethod.getInstance());
        textMyrespond = (HtmlTextView) findViewById(R.id.text_myrespond_id);
        htmlTextTiGan = (TextView) findViewById(R.id.tigan_id);
        cancelImg = (ImageView) findViewById(R.id.cancel_id);

        radioTimu = (RadioButton) findViewById(R.id.radio_timu_id);
        radioMyRspd = (RadioButton) findViewById(R.id.radio_my_respond);
        radioSureRspd = (RadioButton) findViewById(R.id.radio_sure_respond);
        radioAnalysis = (RadioButton) findViewById(R.id.radio_analysis);

        linearTimu = (LinearLayout) findViewById(R.id.linear_timu_id);
        linearMyRspd = (LinearLayout) findViewById(R.id.linear_my_respond);
        linearSureRspd = (LinearLayout) findViewById(R.id.linear_sure_respond);
        linearAnalysis = (LinearLayout) findViewById(R.id.linear_analysis);

        radioTimu.setOnClickListener(new MyOnClickListener());
        radioMyRspd.setOnClickListener(new MyOnClickListener());
        radioSureRspd.setOnClickListener(new MyOnClickListener());
        radioAnalysis.setOnClickListener(new MyOnClickListener());

        linearTimu.setOnClickListener(new MyOnClickListener());
        linearMyRspd.setOnClickListener(new MyOnClickListener());
        linearSureRspd.setOnClickListener(new MyOnClickListener());
        linearAnalysis.setOnClickListener(new MyOnClickListener());
        radioTimu.setChecked(true);
        linearTimu.setVisibility(View.VISIBLE);
        radioTimu.setTextColor(getResources().getColor(R.color.hui));
        cancelImg.setOnClickListener(new MyOnClickListener());

        lineAnswer.setVisibility(View.VISIBLE);

//        htmlTextTiGan.setHtml(items.getQuestion().getContent(), new HtmlHttpImageGetter(htmlTextTiGan));

        htmlTextTiGan.setText(FormateSpannedContent(htmlTextTiGan, items.getQuestion().getContent(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));

        StringBuffer stringBuffer1 = new StringBuffer();
//        for(int i=0;i<items.getLatestAnswer().size();i++){
//            stringBuffer1.append((i+1)+":");
//            stringBuffer1.append(items.getLatestAnswer().get(i));
//        }
//        textMyrespond.setHtml(stringBuffer1.toString(), new HtmlHttpImageGetter(textMyrespond));
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < items.getQuestion().getAnswers().size(); i++) {
            if (items.getQuestion().getAnswers().size() > 1) {
                stringBuffer.append((i + 1) + "," + items.getQuestion().getAnswers().get(i).getContent() + "<br>");
            } else {
                stringBuffer.append(items.getQuestion().getAnswers().get(i).getContent() + "<br>");
            }

        }
        textSureRspd.setText(FormateSpannedContent(textSureRspd, stringBuffer.toString(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
//        textSureRspd.setHtml(stringBuffer.toString(), new HtmlHttpImageGetter(textSureRspd));
        htmlTextAnalysis.setText(FormateSpannedContent(htmlTextAnalysis, items.getQuestion().getAnalysis(), DeviceInfoUtil.dip2px(getBaseContext(), 40)));
//        htmlTextAnalysis.setHtml(items.getQuestion().getAnalysis(), new HtmlHttpImageGetter(htmlTextAnalysis));

        textLianXi.setText(items.getExerciseNum());
        textCuoTi.setText(items.getMistakeNum());
        textRenShu.setText(items.getMistakePeople());

        if (!TextUtils.isEmpty(items.getLatestAnswerImg())) {
            ImageLoadUtil.getInstance().loadImageActivity(MyWrongActivity.this,
                    items.getLatestAnswerImg(), R.drawable.image_fail, aswerImg);
        }
        if (items.getQuestion().getType().equals("SINGLE_CHOICE")) {
            lineView.setVisibility(View.VISIBLE);

            if (items.getLatestAnswer() != null && items.getLatestAnswer().size() > 0) {
                textMyrespond.setHtml(items.getLatestAnswer().get(0), new HtmlHttpImageGetter(textMyrespond));
            }

        } else {
            lineView.setVisibility(View.GONE);
            textMyrespond.setHtml("", new HtmlHttpImageGetter(textMyrespond));
        }


        myChiocesAdapter = new MyWrongDetailAdapter(MyWrongActivity.this);
        myChiocesAdapter.setList(new ArrayList<ChioceABCDInfo>());
        myChiocesAdapter.SetParentView(chioces_LV);

        // chioces_LV.setAdapter(myChiocesAdapter);


        if (items.getQuestion().getType().equals("SINGLE_CHOICE")) {
            chioces_LV.setVisibility(View.VISIBLE);
            for (int j = 0; j < items.getQuestion().getChoices().size(); j++) {

                ChioceABCDInfo chioceABCDInfo = new ChioceABCDInfo();
                chioceABCDInfo.setChoiceContent(items.getQuestion().getChoices().get(j));
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
            myChiocesAdapter.getList().clear();
            myChiocesAdapter.getList().addAll(chiocesList);
            addCHooseItem();
            myChiocesAdapter.notifyDataSetChanged();
        } else {
            chioces_LV.setVisibility(View.INVISIBLE);

        }

    }

    class MyOnClickListener implements View.OnClickListener {


        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.radio_timu_id:

                    linearTimu.setVisibility(View.VISIBLE);
                    linearMyRspd.setVisibility(View.GONE);
                    linearSureRspd.setVisibility(View.GONE);
                    linearAnalysis.setVisibility(View.GONE);
                    anserPage.setVisibility(View.GONE);
                    lineAnswer.setVisibility(View.VISIBLE);


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
                    anserPage.setVisibility(View.VISIBLE);
                    lineAnswer.setVisibility(View.GONE);

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
                    anserPage.setVisibility(View.GONE);
                    lineAnswer.setVisibility(View.GONE);

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
                    anserPage.setVisibility(View.GONE);
                    lineAnswer.setVisibility(View.GONE);

                    radioTimu.setTextColor(getResources().getColor(R.color.zl_black));
                    radioMyRspd.setTextColor(getResources().getColor(R.color.zl_black));
                    radioSureRspd.setTextColor(getResources().getColor(R.color.zl_black));
                    radioAnalysis.setTextColor(getResources().getColor(R.color.hui));

                    break;

                case R.id.cancel_id:
                    finish();
                    break;

                default:
                    break;

            }

        }
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

//    private void setTimutrue(){
//        radioTimu.setTextColor(getResources().getColor(R.color.hui));
//        radioMyRspd.setTextColor(getResources().getColor(R.color.zl_black));
//        radioSureRspd.setTextColor(getResources().getColor(R.color.zl_black));
//        radioAnalysis.setTextColor(getResources().getColor(R.color.zl_black));
//
//    }

}
