package com.yougy.anwser;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by FH on 2017/8/24.
 */

public class QuestionAnswerContainer extends FrameLayout {
    Context mContext;
    WebView webview;
    TextView textView;
    ImageView imageView;
    TextView clickOrHintlayer;

    boolean needFresh = false;

    OnClickListener mListener = null;
    public QuestionAnswerContainer(@NonNull Context context) {
        super(context);
        mContext = context;
        init();
    }

    public QuestionAnswerContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public QuestionAnswerContainer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init(){
        webview = new WebView(mContext);
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                if (view.getTitle() != null && view.getTitle().contains("找不到网页")){
                    needFresh = true;
                    setHintText("加载失败,点击重试");
                }
                else {
                    needFresh = false;
                    setHintText(null);
                }
            }
        });
        addView(webview, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT));

        textView = new TextView(mContext);
        addView(textView , new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP , 20);
        textView.setTextColor(Color.BLACK);

        imageView = new ImageView(mContext);
        addView(imageView , new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT));

        clickOrHintlayer = new TextView(mContext);
        addView(clickOrHintlayer , new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT));
        clickOrHintlayer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (needFresh){
                    webview.reload();
                }
                else {
                    if (mListener != null){
                        mListener.onClick(v);
                    }
                }
            }
        });
    }

    public void setHintText(String hintText){
        if (TextUtils.isEmpty(hintText)){
            clickOrHintlayer.setBackgroundColor(Color.TRANSPARENT);
            clickOrHintlayer.setText("");
        }
        else {
            clickOrHintlayer.setBackgroundColor(Color.WHITE);
            clickOrHintlayer.setText(hintText);
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        mListener = l;
    }

    public void setHtmlUrl(String url){
        webview.setVisibility(VISIBLE);
        textView.setVisibility(GONE);
        imageView.setVisibility(GONE);
        webview.loadUrl(url);
        needFresh = false;
        setHintText(null);
    }
    public void setText(String text){
        webview.setVisibility(GONE);
        textView.setVisibility(VISIBLE);
        imageView.setVisibility(GONE);
        textView.setText(text);
        needFresh = false;
        setHintText(null);
    }
    public void setImgUrl(String url){
        webview.setVisibility(GONE);
        textView.setVisibility(GONE);
        imageView.setVisibility(VISIBLE);
        Glide.with(mContext).load(url).into(imageView);
        needFresh = false;
        setHintText(null);
    }

    public void setHeight (int height){
        getLayoutParams().height = height;
    }
}
