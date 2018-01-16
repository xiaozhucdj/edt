package com.yougy.anwser;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.yougy.common.utils.UIUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by FH on 2017/8/24.
 */

public class ContentDisplayer extends RelativeLayout {
    //提供数据的adapter
    private ContentAdaper mContentAdaper;

    //以下三个为主显示控件,同时只有一个可以显示,其余的隐藏
    //展示网页用的webview
    private WebView webview;
    //展示文字用的textview
    private TextView mainTextView;
    //展示图片用的imageView
    private ImageView imageView;

    //次显示控件
    private TextView subTextview;

    //遮盖在在主显示控件上层的提示文字显示控件,用来在主显示控件无法正常显示时提供提示文字,并提供点击逻辑.
    //次要作用是在scrollEnable为false的时候显示为透明层,遮挡在主显示控件上屏蔽主显示控件的滑动.
    private TextView clickOrHintlayer;

    private OnClickListener mListener = null;

    private boolean needRefresh = false;

    private boolean scrollEnable = false;
    public ContentDisplayer(@NonNull Context context) {
        this(context , null);
    }
    public ContentDisplayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ContentDisplayer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 获取为本ContentDisplayer提供数据的adapter,可能为空
     * @return
     */
    public ContentAdaper getmContentAdaper() {
        return mContentAdaper;
    }

    /**
     * 设置为本ContentDisplayer提供数据的adapter
     * @param adaper
     */
    public void setmContentAdaper(ContentAdaper adaper) {
        if (mContentAdaper != null){
            mContentAdaper.setmContentDisplayer(null);
        }
        mContentAdaper = adaper;
        adaper.setmContentDisplayer(this);
    }

    private void init(){
        subTextview = new TextView(getContext());
        subTextview.setId(UIUtils.generateViewId());
        subTextview.setTextSize(TypedValue.COMPLEX_UNIT_SP , 20);
        subTextview.setTextColor(Color.BLACK);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(subTextview , params);

        webview = new WebView(getContext());
        webview.setId(UIUtils.generateViewId());
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                if (view.getTitle() != null && view.getTitle().contains("找不到网页")){
                    setHintText("加载失败,点击重试");
                    needRefresh = true;
                }
                else {
                    setHintText(null);
                }
            }
        });
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(BELOW , subTextview.getId());
        addView(webview, params);

        mainTextView = new TextView(getContext());
        mainTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP , 20);
        mainTextView.setTextColor(Color.BLACK);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(BELOW , subTextview.getId());
        addView(mainTextView, params);

        imageView = new ImageView(getContext());
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(BELOW , subTextview.getId());
        addView(imageView, params);

        clickOrHintlayer = new TextView(getContext());
        clickOrHintlayer.setTextSize(TypedValue.COMPLEX_UNIT_SP , 20);
        mainTextView.setTextColor(Color.BLACK);
        clickOrHintlayer.setGravity(Gravity.CENTER);
        clickOrHintlayer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (needRefresh){
                    mContentAdaper.refresh();
                }
                else if (mListener != null){
                    mListener.onClick(v);
                }
            }
        });
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(BELOW , subTextview.getId());
        addView(clickOrHintlayer, params);
    }

    public void setTextSize(int unit , int size){
        clickOrHintlayer.setTextSize(unit, size);
        mainTextView.setTextSize(unit , size);
        subTextview.setTextSize(unit , size);
    }
    public void setHintText(String hintText){
        if (TextUtils.isEmpty(hintText)){
            if (scrollEnable){
                clickOrHintlayer.setText("");
                clickOrHintlayer.setVisibility(GONE);
            }
            else {
                clickOrHintlayer.setBackgroundColor(Color.TRANSPARENT);
                clickOrHintlayer.setText("");
            }
        }
        else {
            clickOrHintlayer.setVisibility(VISIBLE);
            clickOrHintlayer.setBackgroundColor(Color.WHITE);
            clickOrHintlayer.setText(hintText);
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        mListener = l;
    }

    public ContentDisplayer setScrollEnable(boolean scrollEnable) {
        this.scrollEnable = scrollEnable;
        return this;
    }

    private void setHtmlUrl(String url){
        webview.setVisibility(VISIBLE);
        mainTextView.setVisibility(GONE);
        imageView.setVisibility(GONE);
        webview.loadUrl(url);
        needRefresh = false;
        setHintText("正在加载....");
    }
    private void setMainText(String text){
        webview.setVisibility(GONE);
        mainTextView.setVisibility(VISIBLE);
        imageView.setVisibility(GONE);
        mainTextView.setText(text);
        needRefresh = false;
        setHintText(null);
    }
    private void setImgUrl(String url){
        webview.setVisibility(GONE);
        mainTextView.setVisibility(GONE);
        imageView.setVisibility(VISIBLE);
        Glide.with(getContext()).load(url).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                e.printStackTrace();
                Log.v("FH" , "getImg exception : " + e.getMessage() + "url : " + url);
                setHintText("题目图片加载失败:" + e.getMessage() + ",点击重新加载...");
                needRefresh = true;
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        }).into(imageView);
        needRefresh = false;
        setHintText(null);
    }

    private void setSubText(String text){
        if (TextUtils.isEmpty(text)){
            subTextview.setVisibility(GONE);
        }
        else {
            subTextview.setVisibility(VISIBLE);
            subTextview.setText(text);
        }
    }

    public void setHeight (int height){
        getLayoutParams().height = height;
    }


    public static class ContentAdaper {
        private ContentDisplayer mContentDisplayer;

        private HashMap<String , ArrayList<Content_new>> dataMap = new HashMap<String, ArrayList<Content_new>>();

        private String subText;
        private String currentShowTypeKey;
        private int currentShowPosition;
        private boolean isSubTextShow;


        public void setmContentDisplayer(ContentDisplayer mContentDisplayer) {
            this.mContentDisplayer = mContentDisplayer;
        }

        public void addData(String typeKey , Content_new data){
            ArrayList<Content_new> dataList = dataMap.get(typeKey);
            if (dataList == null){
                dataList = new ArrayList<Content_new>();
                dataMap.put(typeKey , dataList);
            }
            dataList.add(data);
        }
        public void deleteData(String typeKey , Content_new data){
            ArrayList<Content_new> dataList = dataMap.get(typeKey);
            if (dataList != null){
                dataList.remove(data);
            }
        }
        public void updateData(String typeKey , Content_new data , Comparator<Content_new> comparator){
            ArrayList<Content_new> dataList = dataMap.get(typeKey);
            if (dataList == null){
                dataList = new ArrayList<Content_new>();
                dataMap.put(typeKey , dataList);
            }
            else {
                for (int i = 0; i < dataList.size();) {
                    Content_new originData = dataList.get(i);
                    if (comparator.compare(originData , data) == 0){
                        dataList.remove(i);
                        dataList.add(i , originData);
                        return;
                    }
                    else {
                        i++;
                    }
                }
            }
        }

        public Content_new getData(String typeKey , int positionInList){
            ArrayList<Content_new> dataList = dataMap.get(typeKey);
            if (dataList != null){
                return dataList.get(positionInList);
            }
            return null;
        }


        public void deleteDataList(String typeKey){
            dataMap.remove(typeKey);
        }
        public void updateDataList(String typeKey , ArrayList<Content_new> dataList){
            dataMap.put(typeKey , dataList);
        }

        public ArrayList<Content_new> getDataList(String typeKey){
            return dataMap.get(typeKey);
        }

        public ArrayList<String> getKeyList(){
            ArrayList<String> keyList = new ArrayList<String>();
            for (Iterator<String> iterator = dataMap.keySet().iterator(); iterator.hasNext() ; ){
                String key = iterator.next();
                if (!TextUtils.isEmpty(key)){
                    keyList.add(key);
                }
            }
            return keyList;
        }

        public void setSubText(String subText){
            this.subText = subText;
        }

        public String getSubText(){
            return subText;
        }

        public void toPage(String typeKey , int pageIndex , boolean showSubText){
            if (mContentDisplayer != null){
                ArrayList<Content_new> dataList = dataMap.get(typeKey);
                if (dataList != null){
                    Content_new data = dataList.get(pageIndex);
                    if (data != null){
                        currentShowTypeKey = typeKey;
                        currentShowPosition = pageIndex;
                        showContent(data);
                    }
                }
                isSubTextShow = showSubText;
                if (showSubText && !TextUtils.isEmpty(subText)){
                    mContentDisplayer.subTextview.setVisibility(VISIBLE);
                    mContentDisplayer.subTextview.setText(subText);
                }
                else {
                    mContentDisplayer.subTextview.setVisibility(GONE);
                }
            }
        }

        public int getPageCount(String typeKey){
            ArrayList<Content_new> dataList = dataMap.get(typeKey);
            if (dataList != null){
                return dataList.size();
            }
            return 0;
        }

        public int getCurrentShowPageIndex(){
            return currentShowPosition;
        }

        public String getCurrentShowTypeKey(){
            return currentShowTypeKey;
        }


        public void refresh(){
            toPage(currentShowTypeKey , currentShowPosition , isSubTextShow);
        }

        private void showContent(Content_new content){
            switch (content.getType()){
                case HTML_URL:
                    mContentDisplayer.setHtmlUrl(content.getValue());
                    break;
                case IMG_URL:
                    mContentDisplayer.setImgUrl(content.getValue());
                    break;
                case TEXT:
                    mContentDisplayer.setMainText(content.getValue());
                    break;
            }
        }
    }

}
