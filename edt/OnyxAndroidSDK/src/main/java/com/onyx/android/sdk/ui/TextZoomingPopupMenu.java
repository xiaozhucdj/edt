package com.onyx.android.sdk.ui;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.sdk.R;


public final class TextZoomingPopupMenu extends LinearLayout {

    public static enum MessageToShown{ZoomFactor,FontSize}
    private TextView mTextViewTittle;
    private TextView mTextSubTittle;

    public TextZoomingPopupMenu(Context context, RelativeLayout parentLayout,int screenWidth,int screenHeight) {
        super(context);

        final LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.textzooming_menu, this, true);
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(screenWidth,
                screenHeight);
        int marginValue=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50,context.getResources().getDisplayMetrics());
        p.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        p.addRule(RelativeLayout.CENTER_HORIZONTAL);
        p.setMargins(0, marginValue, 0, 0);
        parentLayout.addView(this, p);
        setFocusable(false);
        this.setVisibility(View.GONE);
        mTextViewTittle = (TextView) findViewById(R.id.textView_tittle);
        mTextSubTittle = (TextView) findViewById(R.id.textView_subTittle);
    }

    public void showAndUpdate(MessageToShown messageToShown,String subTittle) {
        switch (messageToShown){
            case FontSize:
                mTextViewTittle.setText(R.string.currentFontSize);
                break;
            case ZoomFactor:
                mTextViewTittle.setText(R.string.currentZoomFactor);
                break;
            default:
                break;
        }
       mTextSubTittle.setText(subTittle);
       if (getVisibility() != VISIBLE) {
           setVisibility(View.VISIBLE);
       }
    }

    public void hide() {
        setVisibility(View.GONE);
    }

    public boolean isShow() {
        return (getVisibility() == View.VISIBLE);
    }
}
