package com.onyx.android.sdk.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.R;
import com.onyx.android.sdk.ui.data.PageStatusInfo;
import com.onyx.android.sdk.ui.util.AlProgressLine;
import com.onyx.android.sdk.ui.util.MyTextView;
import com.onyx.android.sdk.ui.util.StatusBarNavigatorView;

import java.util.Calendar;

/**
 * Created by solskjaer49 on 14-4-21.
 */
public class StatusBar extends LinearLayout{

    private AlProgressLine mProgressLine;
    private MyTextView progressTextView;
    private MyTextView infoTextView;
    private MyTextView batteryView;
    private MyTextView timeView;
    private StatusBarNavigatorView mStatusBarNavigatorView;
    private RelativeLayout statusPageButton;
    private boolean is24HourFormat=false;
    public StatusBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        final LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.status_bar, this, true);
        init();
    }

    public StatusBar(Context context) {
        this(context,null);
    }

    private void init(){
        mStatusBarNavigatorView =(StatusBarNavigatorView)findViewById(R.id.view_navigator);
        progressTextView = (MyTextView)findViewById(R.id.status_page);
        infoTextView=(MyTextView)findViewById(R.id.status_info);
        batteryView=(MyTextView)findViewById(R.id.status_battery);
        timeView = (MyTextView)findViewById(R.id.status_time);
        statusPageButton=(RelativeLayout)findViewById(R.id.status_page_layout);
        mProgressLine = (AlProgressLine)findViewById(R.id.progress_line);
        progressTextView.setTypeface(Typeface.SERIF);
        infoTextView.setTypeface(Typeface.SERIF);
        batteryView.setTypeface(Typeface.SERIF);
        timeView.setTypeface(Typeface.SERIF);
        timeView.setText(onTimeChanged());
    }

    public void updateStatusBar(PageStatusInfo info) {
        updateStatusBarNavigator(info.pageRect, info.viewportRect);
        updateStatusProgressText(info.pageReadStatus);
        updateStatusBarProgressGraphic(info.currentPage, info.totalPage);
        updateStatusBarInfoText(info.currentDocumentTittle);
        updateStatusBarBatteryText(info.batteryStatus);
    }

    public boolean updateStatusBarNavigator(Rect pageRect,Rect viewportRect){
        try{
        mStatusBarNavigatorView.setNavigatorContent(pageRect,viewportRect);
        return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void showStatusBarNavigator() {
        mStatusBarNavigatorView.setVisibility(View.VISIBLE);
    }

    public void hideStatusBarNavigator() {
        mStatusBarNavigatorView.setVisibility(View.INVISIBLE);
    }

    public boolean updateStatusProgressText(String pageReadStatus){
        try{
            progressTextView.setText(pageReadStatus);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatusBarProgressGraphic(int currentPage,int totalPage){
        try{
            mProgressLine.setProgress(currentPage,totalPage);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatusBarInfoText(String currentDocumentTittle){
        try{
            infoTextView.setText(currentDocumentTittle);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStatusBarBatteryText(String batteryStatus){
        try{
            batteryView.setText(batteryStatus);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public void reConfigure(boolean isBatteryPercentageShow, boolean isTimeShow,boolean is24HourFormat,boolean isBatteryGraphicShow) {
        if (isBatteryPercentageShow) {
            batteryView.setVisibility(View.VISIBLE);
        } else {
            batteryView.setVisibility(View.GONE);
        }
        if (isTimeShow) {
            if (this.is24HourFormat!=is24HourFormat){
                this.is24HourFormat=is24HourFormat;
                timeView.setText(onTimeChanged());
            }
            timeView.setVisibility(View.VISIBLE);
        } else {
            timeView.setVisibility(View.GONE);
        }
        if (isBatteryGraphicShow){
            mProgressLine.setShowBatteryGraphic(true);
            mProgressLine.invalidate();
        }else {
            mProgressLine.setShowBatteryGraphic(false);
            mProgressLine.invalidate();
        }
    }

    public void setStatusPageButtonOnClickListener(OnClickListener l){
        statusPageButton.setOnClickListener(l);
    }

    public void hideInfoText(){
        infoTextView.setVisibility(GONE);
    }

    public void showInfoText(){
        infoTextView.setVisibility(VISIBLE);
    }

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            timeView.setText(onTimeChanged());
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        getContext().registerReceiver(mIntentReceiver, filter, null, getHandler());
    }

    @Override
    protected void onDetachedFromWindow() {
        getContext().unregisterReceiver(mIntentReceiver);
        super.onDetachedFromWindow();
    }

    private String onTimeChanged(){
        Calendar ca = Calendar.getInstance();
        if (is24HourFormat){
            return String.format("%tR", ca.getTimeInMillis());
        }
        int AM_PM=ca.get(Calendar.AM_PM);
        if (AM_PM== 1){
            int Hour=ca.get(Calendar.HOUR)==0?12:ca.get(Calendar.HOUR);
            return String.format("%02d:%02d", Hour,ca.get(Calendar.MINUTE))+" PM";
        }
        return String.format("%02d:%02d", ca.get(Calendar.HOUR),ca.get(Calendar.MINUTE))+" AM";
    }
}
