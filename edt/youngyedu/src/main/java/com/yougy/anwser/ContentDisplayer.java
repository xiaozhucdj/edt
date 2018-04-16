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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.utils.UIUtils;
import com.yougy.plide.LoadController;
import com.yougy.plide.LoadListener;
import com.yougy.plide.Plide;
import com.yougy.plide.PlideException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by FH on 2017/8/24.
 */

public class ContentDisplayer extends RelativeLayout {
    public enum LOADING_STATUS {
        LOADING , ERROR , SUCCESS
    }

    //提供数据的adapter
    private ContentAdaper mContentAdaper;

    //以下三个为主显示控件,同时只有一个可以显示,其余的隐藏
    //展示网页用的webview
    private WebView webview;
    //展示文字用的textview
    private TextView mainTextView;
    //展示图片用的imageView
    private ImageView picImageView;
    //展示pdf用的imageView
    private ImageView pdfImageView;
    //次显示控件
    private TextView subTextview;

    //遮盖在在主显示控件上层的提示文字显示控件,用来在主显示控件无法正常显示时提供提示文字,并提供点击逻辑.
    //次要作用是在scrollEnable为false的时候显示为透明层,遮挡在主显示控件上屏蔽主显示控件的滑动.
    private TextView clickOrHintlayer;

    private OnClickListener mOnClickListener = null;

    private boolean needRefresh = false;

    private boolean scrollEnable = false;

    private OnLoadingStatusChangedListener mOnLoadingStatusChangedListener;

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
                    callOnLoadingStatusChangedListener(LOADING_STATUS.ERROR);
                }
                else {
                    setHintText(null);
                    callOnLoadingStatusChangedListener(LOADING_STATUS.SUCCESS);
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

        picImageView = new ImageView(getContext());
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(BELOW , subTextview.getId());
        addView(picImageView , params);

        pdfImageView = new ImageView(getContext());
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(BELOW , subTextview.getId());
        addView(pdfImageView , params);

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
                else if (mOnClickListener != null){
                    mOnClickListener.onClick(v);
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
        mOnClickListener = l;
    }

    public void setOnLoadingStatusChangedListener(OnLoadingStatusChangedListener mOnLoadingStatusChangedListener){
        this.mOnLoadingStatusChangedListener = mOnLoadingStatusChangedListener;
    }

    public ContentDisplayer setScrollEnable(boolean scrollEnable) {
        this.scrollEnable = scrollEnable;
        return this;
    }

    private void setHtmlUrl(String url){
        webview.setVisibility(VISIBLE);
        mainTextView.setVisibility(GONE);
        picImageView.setVisibility(GONE);
        pdfImageView.setVisibility(GONE);
        webview.loadUrl(url);
        needRefresh = false;
        setHintText("正在加载网页....");
    }
    private void setMainText(String text){
        webview.setVisibility(GONE);
        mainTextView.setVisibility(VISIBLE);
        picImageView.setVisibility(GONE);
        pdfImageView.setVisibility(GONE);
        mainTextView.setText(text);
        needRefresh = false;
        setHintText(null);
        callOnLoadingStatusChangedListener(LOADING_STATUS.SUCCESS);
    }
    private void setImgUrl(String url , boolean useCache){
        webview.setVisibility(GONE);
        mainTextView.setVisibility(GONE);
        picImageView.setVisibility(VISIBLE);
        pdfImageView.setVisibility(GONE);

        if (picImageView!=null){
            Glide.clear(picImageView);
        }
        if (useCache){

            Glide.with(BaseActivity.getCurrentActivity())
                    .load(url)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            e.printStackTrace();
                            Log.v("FH" , "getImg exception : " + e.getMessage() + "url : " + url);
                            setHintText("题目图片加载失败:" + e.getMessage() + ",点击重新加载...");
                            callOnLoadingStatusChangedListener(LOADING_STATUS.ERROR);
                            needRefresh = true;
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            callOnLoadingStatusChangedListener(LOADING_STATUS.SUCCESS);
                            return false;
                        }
                    }).into(picImageView);
        }
        else {

            Glide.with(BaseActivity.getCurrentActivity())
                    .load(url)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            e.printStackTrace();
                            Log.v("FH" , "getImg exception : " + e.getMessage() + "url : " + url);
                            setHintText("题目图片加载失败:" + e.getMessage() + ",点击重新加载...");
                            callOnLoadingStatusChangedListener(LOADING_STATUS.ERROR);
                            needRefresh = true;
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            callOnLoadingStatusChangedListener(LOADING_STATUS.SUCCESS);
                            return false;
                        }
                    }).into(picImageView);
        }
        needRefresh = false;
        setHintText(null);
    }

    private void setPdf(Content_new content , int contentIndex , int subPageIndex , String typeKey){
        webview.setVisibility(GONE);
        mainTextView.setVisibility(GONE);
        picImageView.setVisibility(GONE);
        pdfImageView.setVisibility(VISIBLE);
        try {
            String url = content.getValue();
            if (url.endsWith("##")){
                url = url.substring(0 , url.lastIndexOf("**"));
            }
            Plide.with(getContext()).load(url).setLoadListener(new LoadListener() {
                @Override
                public void onLoadStatusChanged(LoadController.PDF_STATUS newStatus, float downloadProgress , int totalPage) {
                    Log.v("FH" , "onLoadStatusChanged newStatus = " + newStatus + " downloadProgress = " + downloadProgress
                            + " totalPage = " + totalPage
                            + " threadId = " + Thread.currentThread().getId());
                    if (newStatus == LoadController.PDF_STATUS.ERROR){
                        setHintText("题目pdf加载失败"  + ",点击重新加载...");
                        needRefresh = true;
                        callOnLoadingStatusChangedListener(LOADING_STATUS.ERROR);
                    }
                    else if (newStatus == LoadController.PDF_STATUS.LOADED){
                        if (!content.getValue().endsWith("##")){
                            content.setValue(content.getValue() + "**" + totalPage + "##");
                            if (typeKey.equals(mContentAdaper.getCurrentShowTypeKey())){
                                mContentAdaper.onPageInfoChanged(typeKey , mContentAdaper.getPageCount(typeKey) , mContentAdaper.getCurrentSelectPageIndex());
                            }
                        }
                        else {
                            String value = content.getValue();
                            String contentPageStr = value.substring(value.lastIndexOf("**") + 2 , value.lastIndexOf("##"));
                            try {
                                if (Integer.parseInt(contentPageStr) != totalPage){
                                    content.setValue(value.substring(0 , value.lastIndexOf("**") - 1) + "**" + totalPage + "##");
                                    if (typeKey.equals(mContentAdaper.getCurrentShowTypeKey())) {
                                        mContentAdaper.onPageInfoChanged(typeKey, mContentAdaper.getPageCount(typeKey), mContentAdaper.getCurrentSelectPageIndex());
                                    }
                                }
                            }
                            catch (NumberFormatException e){
                                content.setValue(value.substring(0 , value.lastIndexOf("**") - 1));
                                if (typeKey.equals(mContentAdaper.getCurrentShowTypeKey())) {
                                    mContentAdaper.onPageInfoChanged(typeKey, mContentAdaper.getPageCount(typeKey), mContentAdaper.getCurrentSelectPageIndex());
                                }
                                callOnLoadingStatusChangedListener(LOADING_STATUS.ERROR);
                            }
                        }
                        setHintText(null);
                        callOnLoadingStatusChangedListener(LOADING_STATUS.SUCCESS);
                    }
                    else if (newStatus == LoadController.PDF_STATUS.DOWNLOADING){
                        setHintText("正在下载pdf....");
                    }
                    else if (newStatus == LoadController.PDF_STATUS.LOADING){
                        setHintText("正在加载pdf....");
                    }
                    else if (newStatus == LoadController.PDF_STATUS.EMPTY){
                        setHintText("无pdf加载请求...");
                    }
                }
            }).into(pdfImageView).toPage(subPageIndex);
        } catch (PlideException e) {
            e.printStackTrace();
            setHintText("题目pdf加载失败:" + e.getMessage() + ",点击重新加载...");
            needRefresh = true;
        }
        needRefresh = false;
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
        private String currentShowTypeKey = null;
        private int currentShowContentIndex = -1;
        private int currentShowSubPageIndex = -1;
        private boolean isSubTextShow;

        public void onPageInfoChanged(String typeKey , int newPageCount , int selectPageIndex){

        }

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
            if (typeKey.equals(getCurrentShowTypeKey())){
                onPageInfoChanged(typeKey , getPageCount(typeKey) , getCurrentSelectPageIndex());
            }
        }
        public void deleteData(String typeKey , Content_new data){
            ArrayList<Content_new> dataList = dataMap.get(typeKey);
            if (dataList != null){
                int toRemoveIndex = dataList.indexOf(data);
                if (toRemoveIndex != -1){
                    dataList.remove(data);
                    if (typeKey.equals(getCurrentShowTypeKey())){
                        if (toRemoveIndex == getCurrentShowContentIndex()){
                            setCurrentShowInfo(typeKey , -1 , -1);
                            toPage(typeKey , -1 , false);
                        }
                        else if (toRemoveIndex < getCurrentShowContentIndex()){
                            setCurrentShowInfo(typeKey , getCurrentShowContentIndex() - 1 , getCurrentShowSubPageIndex());
                        }
                        onPageInfoChanged(typeKey , getPageCount(typeKey) , getCurrentSelectPageIndex());
                    }
                }
            }
        }
        public void updateData(String typeKey , Content_new data , Comparator<Content_new> comparator){
            ArrayList<Content_new> dataList = dataMap.get(typeKey);
            if (dataList == null){
                dataList = new ArrayList<Content_new>();
                dataList.add(data);
                dataMap.put(typeKey , dataList);
                if (typeKey.equals(getCurrentShowTypeKey())){
                    onPageInfoChanged(typeKey , getPageCount(typeKey) , getCurrentShowContentIndex());
                }
            }
            else {
                for (int i = 0; i < dataList.size();) {
                    Content_new originData = dataList.get(i);
                    if (comparator.compare(originData , data) == 0){
                        dataList.remove(i);
                        dataList.add(i , originData);
                        if (typeKey.equals(getCurrentShowTypeKey())){
                            if (i == getCurrentShowContentIndex()){
                                setCurrentShowInfo(typeKey , -1 , -1);
                                toPage(typeKey , -1 , false);
                            }
                            onPageInfoChanged(typeKey , getPageCount(typeKey) , getCurrentSelectPageIndex());
                        }
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
            if (typeKey.equals(getCurrentShowTypeKey())){
                setCurrentShowInfo(null , -1 , -1);
                toPage(null , -1 , false);
                onPageInfoChanged(typeKey , 0 , -1);
            }
        }
        public void updateDataList(String typeKey , ArrayList<Content_new> dataList){
            dataMap.put(typeKey , dataList);
            if (typeKey.equals(getCurrentShowTypeKey())){
                setCurrentShowInfo(typeKey , -1 , -1);
                toPage(typeKey, -1 , false);
                onPageInfoChanged(typeKey , getPageCount(typeKey) , -1);
            }
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
            toPage(typeKey, pageIndex, showSubText , true);
        }

        public void toPage(String typeKey , int pageIndex , boolean showSubText , boolean useCache){
            mContentDisplayer.callOnLoadingStatusChangedListener(LOADING_STATUS.LOADING);
            if (typeKey == null || pageIndex == -1){
                mContentDisplayer.setMainText("没有内容");
                mContentDisplayer.callOnLoadingStatusChangedListener(LOADING_STATUS.ERROR);
                return;
            }
            if (mContentDisplayer != null){
                isSubTextShow = showSubText;
                mContentDisplayer.setSubText(showSubText ? subText : null);

                ArrayList<Content_new> dataList = dataMap.get(typeKey);
                if (dataList != null){
                    for (int i = 0 ; i < dataList.size() ; i ++) {
                        Content_new content = dataList.get(i);
                        if (content.getValue().endsWith("##")){
                            String contentValue = content.getValue();
                            String contentPageStr = contentValue.substring(contentValue.lastIndexOf("**") + 2 , contentValue.lastIndexOf("##"));
                            int subPageIndex = 0;
                            for (int j = 0; j < Integer.parseInt(contentPageStr) ; j++){
                                pageIndex--;
                                if (pageIndex == -1){
                                    setCurrentShowInfo(typeKey , i , subPageIndex);
                                    onPageInfoChanged(typeKey , getPageCount(typeKey) , getCurrentSelectPageIndex());
                                    showContent(typeKey , i , subPageIndex , useCache);
                                    return;
                                }
                                else {
                                    subPageIndex++;
                                }
                            }
                        }
                        else {
                            pageIndex--;
                            if (pageIndex == -1){
                                setCurrentShowInfo(typeKey , i , 0);
                                onPageInfoChanged(typeKey , getPageCount(typeKey) , getCurrentSelectPageIndex());
                                showContent(typeKey , i , 0 , useCache);
                                return;
                            }
                        }
                    }
                }
            }
        }

        public int getPageCount(String typeKey){
            ArrayList<Content_new> dataList = dataMap.get(typeKey);
            int pageCount = 0;
            if (dataList != null){
                for (Content_new content : dataList) {
                    String contentValue = content.getValue();
                    if (contentValue.endsWith("##")){
                        String contentPageStr = contentValue.substring(contentValue.lastIndexOf("**") + 2 , contentValue.lastIndexOf("##"));
                        try {
                            pageCount = pageCount + Integer.parseInt(contentPageStr);
                        }
                        catch (NumberFormatException e){
                            e.printStackTrace();
                            pageCount = pageCount + 1;
                        }
                    }
                    else {
                        pageCount = pageCount + 1;
                    }
                }
            }
            return pageCount;
        }

        public void setCurrentShowInfo(String typeKey , int contentIndex , int subPageIndex){
            this.currentShowTypeKey = typeKey;
            this.currentShowContentIndex = contentIndex;
            this.currentShowSubPageIndex = subPageIndex;
        }

        public int getCurrentShowContentIndex(){
            return currentShowContentIndex;
        }

        public String getCurrentShowTypeKey(){
            return currentShowTypeKey;
        }
        public int getCurrentShowSubPageIndex(){
            return currentShowSubPageIndex;
        }

        public int getCurrentSelectPageIndex(){
            ArrayList<Content_new> dataList = dataMap.get(currentShowTypeKey);
            if (dataList != null){
                int pageIndex = 0;
                for (int i = 0 ; i < currentShowContentIndex ; i++) {
                    Content_new content = dataList.get(i);
                    if (content.getValue().endsWith("##")) {
                        String contentValue = content.getValue();
                        String contentPageStr = contentValue.substring(contentValue.lastIndexOf("**") + 2, contentValue.lastIndexOf("##"));
                        pageIndex = pageIndex + Integer.parseInt(contentPageStr);
                    }
                    else {
                        pageIndex++;
                    }
                }
                pageIndex = pageIndex + currentShowSubPageIndex;
                return pageIndex;
            }
            return -1;
        }

        public void refresh(){
            toPage(getCurrentShowTypeKey() , getCurrentSelectPageIndex() , isSubTextShow);
        }

        private void showContent(String typeKey , int contentIndex , int subPageIndex , boolean useCache){
            Content_new content = dataMap.get(typeKey).get(contentIndex);
            switch (content.getType()){
                case HTML_URL:
                    mContentDisplayer.setHtmlUrl(content.getValue());
                    break;
                case IMG_URL:
                    mContentDisplayer.setImgUrl(content.getValue() , useCache);
                    break;
                case TEXT:
                    mContentDisplayer.setMainText(content.getValue());
                    break;
                case PDF:
                    mContentDisplayer.setPdf(content , contentIndex , subPageIndex, typeKey);
                    break;
            }
        }
    }

    public void callOnLoadingStatusChangedListener(LOADING_STATUS loadingStatus){
        if (mOnLoadingStatusChangedListener != null){
            mOnLoadingStatusChangedListener.onLoadingStatusChanged(loadingStatus);
        }
    }
    public interface OnLoadingStatusChangedListener{
        public void onLoadingStatusChanged(LOADING_STATUS loadingStatus);
    }

}
