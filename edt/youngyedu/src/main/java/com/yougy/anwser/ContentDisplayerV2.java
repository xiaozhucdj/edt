package com.yougy.anwser;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
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
import com.bumptech.glide.request.target.Target;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.utils.LogUtils;
import com.yougy.plide.LoadController;
import com.yougy.plide.LoadListener;
import com.yougy.plide.Plide;
import com.yougy.plide.PlideException;
import com.yougy.plide.pipe.Ball;
import com.yougy.plide.pipe.Pipe;
import com.yougy.view.ContentPdfImageView;

import java.util.concurrent.ExecutionException;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by FH on 2018/7/3.
 */

public class ContentDisplayerV2 extends RelativeLayout{
    //状态类
    public enum LOADING_STATUS {
        DOWNLOADING , //下载中
        LOADING , //下载完毕加载中
        ERROR , //错误
        SUCCESS //成功
    }
    //具体错误类型类
    public enum ERROR_TYPE {
        NO_SPECIFIED_CONTENT , //根据给定的条件找不到指定的content显示
        DOWNLOAD_ERROR , //下载失败
        LOAD_ERROR, //加载失败
        USER_CANCLE //用户取消
    }

    //提供数据的adapter
    private ContentDisplayerAdapterV2 mContentAdaper;

    //以下三个为主显示控件,同时只有一个可以显示,其余的隐藏
    //展示网页用的webview
    private WebView webview;
    //展示文字用的textview
    private TextView mainTextView;
    //展示图片用的imageView
    private ImageView picImageView;
    //展示pdf用的imageView
    private ImageView pdfImageView;

    //遮盖在在主显示控件上层的提示文字显示控件,用来在主显示控件无法正常显示时提供提示文字,并提供点击逻辑.
    //次要作用是在scrollEnable为false的时候显示为透明层,遮挡在主显示控件上屏蔽主显示控件的滑动.
    private TextView clickOrHintLayer;

    //屏幕是否可以上下滚动的状态变量
    private boolean scrollEnable = false;

    //默认的clickOrHintLayer层的点击监听器,如果没有设置自定义的点击监听器,则默认使用本监听器.
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //默认点击监听器逻辑是直接刷新当前内容
            refresh();
        }
    };

    //默认的状态监听器,负责处理错误和提示性文字.
    //如果没有设置自定义的状态监听器,则默认使用本监听器.
    private StatusChangeListener mStatusChangeListener = new StatusChangeListener() {
        @Override
        public void onStatusChanged(LOADING_STATUS newStatus, String typeKey
                , int pageIndex, String url, ERROR_TYPE errorType, String errorMsg) {
            //do nothing
        }
    };

    //管道类,处理显示content的请求.
    private Pipe mPipe = new Pipe();

    //当前显示的数据的typeKey
    private String currentShowTypeKey;
    //当前显示的页index
    private int currentShowPageIndex;
    //当前使用的指定状态变化监听器
    private StatusChangeListener currentSpecificListener;

    /**
     * 构造函数
     * @param context
     */
    public ContentDisplayerV2(@NonNull Context context) {
        this(context , null);
    }
    public ContentDisplayerV2(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ContentDisplayerV2(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 获取为本ContentDisplayer提供数据的adapter,可能为空
     * @return
     */
    public ContentDisplayerAdapterV2 getContentAdapter() {
        return mContentAdaper;
    }
    /**
     * 设置为本ContentDisplayer提供数据的adapter
     * @param adaper
     */
    public void setContentAdapter(ContentDisplayerAdapterV2 adaper) {
        //ContentDisplayer和adapter双向绑定,设定新的adapter之前,如果有旧的adapter,需要解除双向绑定
        if (mContentAdaper != null){
            mContentAdaper.setContentDisplayer(null);
        }
        mContentAdaper = adaper;
        adaper.setContentDisplayer(this);
    }

    /**
     * 为本ContentDisplayer设置加载状态变化监听器
     * 如果不设置,则使用默认的状态监听器处理状态变化
     *
     * @param listener 不能为null,为空则设置无效
     */
    public void setLoadingStatusListener(StatusChangeListener listener){
        if (listener != null){
            mStatusChangeListener = listener;
        }
    }

    /**
     * 获取当前显示的TypeKey
     * @return
     */
    public String getCurrentShowTypeKey() {
        return currentShowTypeKey;
    }

    /**
     * 获取当前显示的页Index
     * @return
     */
    public int getCurrentShowPageIndex() {
        return currentShowPageIndex;
    }

    /**
     * 清除pdf缓存,在退出的时候调一下
     */
    public void clearPdfCache(){
        if (pdfImageView != null){
            Plide.clearCache(pdfImageView);
        }
    }


    /**
     * 核心方法
     * 切换显示内容至指定的typeKey下的指定pageIndex.
     * 切换进度会在整体的listener里通知,如果想要针对每个toPage请求单独监听进度,也可以单独设置specificListener
     *
     * 线程安全的方法!
     * 所有的toPage请求都会被加入toPage执行管道中顺序执行,后来的toPage会尝试中断前面所有的toPage请求.中断成功或者前一个请求执行完之后才会执行下一个请求.
     *
     * 本方法toPage的pageIndex可以是任意值,对于每个toPage请求,ContentDisplayer会尝试展开所有未展开的pdf content来获取正确的页码总数.
     * 如果全部展开后还仍然无法满足toPage 的pageIndex的需求,则会报NO_SPECIFIED_CONTENT的ERROR.
     * 例如开始ContentList里有3个pdf,每个pdf分别有4页,并且还都未展开.此时的总页数应该是3页.
     * 现在需要topage到第6页
     * ContentDisplayer会首先展开第一个pdf,发现第一个pdf的页数不足以提供第6页的数据.
     * 然后再展开第二个pdf,发现第二个pdf可以提供第6页的值,然后toPage到正确的第6页,也就是toPage的第2个pdf的第2页.
     *
     *
     * @param typeKey 需要切换至的的page所在的数据源的typeKey
     * @param pageIndex 需要切换至的page所在数据源的pageIndex
     * @param useCache 切换时是否需要使用缓存,如果不使用缓存则每次都会从网络上下载最新的数据
     * @param specificListener 用来对每个toPage请求做单独的监听.
     *                         可以为null,如果不为空,则本topage请求除了会在整体的StatusChangeListener上通知,也会在设定的specificListener通知.
     */
    public void toPage(String typeKey , int pageIndex , boolean useCache , StatusChangeListener specificListener){
        //adapter为空时,不建立新的toPage请求.
        if (mContentAdaper == null){
            LogUtils.e("没有绑定ContentDisplayerAdaterV2,无法toPage");
            return;
        }
        //建立新的toPage 请求
        mPipe.push(new Ball(true){
            @Override
            public void run() throws InterruptedException {
                //循环查询,直到查到为止,或者确认查不到为止,会break这个循环
                while (true){
                    inserCheckPoint();
                    //存储本次toPage参数
                    currentShowPageIndex = pageIndex;
                    currentShowTypeKey = typeKey;
                    currentSpecificListener = specificListener;
                    //向adapter查询实际需要toPage的信息情况
                    ContentDisplayerAdapterV2.PageInfoQueryResult pageInfoQueryResult
                            = mContentAdaper.queryPageInfo(typeKey , pageIndex);
                    if (!pageInfoQueryResult.needQueryAgain){
                        //如果adapter告诉我们查询完这次不需要再次查询了,并且查询的content有值,说明已经查出正确的toPage信息了.
                        //这时直接进入showContent流程.
                        if (pageInfoQueryResult.content != null){
                            inserCheckPoint();
                            showContent(pageInfoQueryResult.content , pageInfoQueryResult.subPageIndex
                                    , useCache , this , typeKey , pageIndex , specificListener);
                            break;
                        }
                        //如果adapter告诉我们查询完这次不需要再次查询了,但是查询到的content无值,说明toPage请求的pageIndex无法满足.
                        //则通知listener错误NO_SPECIFIED_CONTENT
                        else {
                            inserCheckPoint();
                            callOnStatusChangedListener(specificListener , LOADING_STATUS.ERROR , typeKey , pageIndex , null , ERROR_TYPE.NO_SPECIFIED_CONTENT , "没有查到要显示的内容");
                            break;
                        }
                    }
                    else {
                        //如果adapter告诉我们查询完这次还需要继续查询,则说明adapter认为还有未展开的pdf,不缺定是否能够满足toPage的pageIndex的需求.
                        //需要先展开pdf,然后再看看能否满足需求.
                        inserCheckPoint();
                        //同步查询pdf的总页数
                        int pdfPageCount = getContentSubpageCount_sync(pageInfoQueryResult.content , useCache);
                        if (!pageInfoQueryResult.content.getValue().endsWith("##")){
                            pageInfoQueryResult.content.setValue(pageInfoQueryResult.content.getValue() + "**" + pdfPageCount + "##");
                            //由于pdf展开导致总页码数发生变化,需要通知afterPageCountChanged
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mContentAdaper != null){
                                        mContentAdaper.afterPageCountChanged(typeKey);
                                    }
                                }
                            });

                        }
                    }
                }
            }
        });
    }

    /**
     * 根据上次的查询请求刷新ContentDisplayer
     */
    public void refresh(){
        toPage(getCurrentShowTypeKey() , getCurrentShowPageIndex() , false , currentSpecificListener);
    }

    /**
     * 设置hintText层的文字
     * @param hintText 为null时,hintText层GONE
     *                 为""时,hintText层为不透明白底无字
     *                 为文字时,hintText层为白底文字
     */
    public void setHintText(String hintText){
        if (hintText == null){
            if (scrollEnable){
                clickOrHintLayer.setText("");
                clickOrHintLayer.setVisibility(GONE);
            }
            else {
                clickOrHintLayer.setBackgroundColor(Color.TRANSPARENT);
                clickOrHintLayer.setText("");
            }
        }
        else {
            clickOrHintLayer.setVisibility(VISIBLE);
            clickOrHintLayer.setBackgroundColor(Color.WHITE);
            clickOrHintLayer.setText(hintText);
        }
    }

    /**
     * 设置点击监听器,如果设置了点击监听器,只有在设置scrollEnable后才能起作用.
     * @param l
     */
    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        mOnClickListener = l;
    }

    /**
     * 设置是否支持滚动(展示html的时候)
     * @param scrollEnable
     * @return
     */
    public ContentDisplayerV2 setScrollEnable(boolean scrollEnable) {
        this.scrollEnable = scrollEnable;
        return this;
    }

    /**
     * 初始化方法,顺序实例化5层显示控件
     */
    private void init(){
        webview = new WebView(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
        addView(webview, params);

        mainTextView = new TextView(getContext());
        mainTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP , 20);
        mainTextView.setTextColor(Color.BLACK);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mainTextView, params);

        picImageView = new ImageView(getContext());
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
        addView(picImageView , params);

        pdfImageView = new ContentPdfImageView(getContext());
        pdfImageView.setScaleType(ImageView.ScaleType.MATRIX);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
        addView(pdfImageView , params);

        clickOrHintLayer = new TextView(getContext());
        clickOrHintLayer.setTextSize(TypedValue.COMPLEX_UNIT_SP , 20);
        mainTextView.setTextColor(Color.BLACK);
        clickOrHintLayer.setGravity(Gravity.CENTER);
        clickOrHintLayer.setOnClickListener(mOnClickListener);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
        addView(clickOrHintLayer, params);
    }

    /**
     * 最终展示content内容的方法.根据展示的content的类型不同,调用不同的子方法展示内容.
     * @param content  需要展示的content
     * @param subPageIndex 当需要展示的content内部也有页码值时,subPageIndex代表内部的页码值.
     * @param useCache 展示时是否使用缓存,不使用缓存时每次都会从网络上下载最新的数据.
     * @param ball 发起showContent命令的toPage请求的任务ball,可以用来插入中断检查点
     * @param typeKey 发起showContent命令的原始typeKey参数
     * @param originPageIndex 发起showContent命令的原始originPageIndex参数
     * @param specificListener 发起showContent命令的原始specificListener参数,可能为null
     */
    private void showContent(Content_new content , int subPageIndex , boolean useCache , Ball ball
            , String typeKey , int originPageIndex , StatusChangeListener specificListener){
        String url;
        try {
            //根据不同的content类型,调用不同的子方法
            switch (content.getType()){
                case IMG_URL:
                    url = content.getValue();
                    setImgUrl(url , useCache , ball , typeKey , originPageIndex, specificListener);
                    break;
                case TEXT:
                    url = content.getValue();
                    setMainText(url , typeKey , originPageIndex, specificListener);
                    break;
                case HTML_URL:
                    url = content.getValue();
                    setHtmlUrl(url , useCache , ball , typeKey , originPageIndex, specificListener);
                    break;
                case PDF:
                    setPdf(content , subPageIndex , useCache , ball , typeKey , originPageIndex , specificListener);
                    break;
            }
        } catch (InterruptedException e) {
            //子方法报告中断,通知 USER_CANCLE的ERROR
            e.printStackTrace();
            callOnStatusChangedListener(specificListener , LOADING_STATUS.ERROR , typeKey , originPageIndex , content.getValue() , ERROR_TYPE.USER_CANCLE , "请求被用户中断");
        }
    }

    /**
     * 显示html的子方法
     * @param url 要显示的html的url
     * @param useCache 展示时是否使用缓存,不使用缓存时每次都会从网络上下载最新的数据.
     * @param ball 发起showContent命令的toPage请求的任务ball,可以用来插入中断检查点
     * @param typeKey 发起showContent命令的原始typeKey参数
     * @param originPageIndex 发起showContent命令的原始originPageIndex参数
     * @param specificListener 发起showContent命令的原始specificListener参数,可能为null
     * @throws InterruptedException 检测到中断,可能是其他toPage请求中断了当前toPage请求.
     */
    private void setHtmlUrl(String url , boolean useCache , Ball ball , String typeKey
            , int originPageIndex , StatusChangeListener specificListener) throws InterruptedException {
        callOnStatusChangedListener(specificListener , LOADING_STATUS.DOWNLOADING , typeKey , originPageIndex , url , null , null);
        //同步状态值,-999就是还未执行完
        int[] result = new int[]{-999};
        webview.stopLoading();
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                if (view.getTitle() != null && view.getTitle().contains("找不到网页")){
                    //加载失败
                    callOnStatusChangedListener(specificListener , LOADING_STATUS.ERROR , typeKey , originPageIndex , url , ERROR_TYPE.DOWNLOAD_ERROR, "加载网页失败");
                    synchronized (result){
                        //-1表示失败
                        result[0] = -1;
                        //通知加载完成
                        result.notify();
                    }
                }
                else {
                    //加载成功
                    callOnStatusChangedListener(specificListener , LOADING_STATUS.SUCCESS , typeKey , originPageIndex , url , null , null);
                    webview.setVisibility(VISIBLE);
                    mainTextView.setVisibility(GONE);
                    picImageView.setVisibility(GONE);
                    pdfImageView.setVisibility(GONE);
                    synchronized (result){
                        //0表示成功
                        result[0] = 0;
                        //通知加载完成
                        result.notify();
                    }
                }
            }
        });
        webview.loadUrl(url);
        synchronized (result){
            if (result[0] == -999){
                try {
                    result.wait();
                }
                catch (InterruptedException e){
                    //检测到被中断时需要停止loading
                    webview.stopLoading();
                    //然后继续向上层throw InterruptedException
                    throw e;
                }
            }
        }
    }
    /**
     * 显示纯文字的子方法
     * @param text 要显示的text文字
     * @param typeKey 发起showContent命令的原始typeKey参数
     * @param originPageIndex 发起showContent命令的原始originPageIndex参数
     * @param specificListener 发起showContent命令的原始specificListener参数,可能为null
     * @throws InterruptedException 检测到中断,可能是其他toPage请求中断了当前toPage请求.
     */
    private void setMainText(String text , String typeKey , int originPageIndex
            , StatusChangeListener specificListener){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //由于展示纯文字无需网络请求,直接通知loading和success
                callOnStatusChangedListener(specificListener , LOADING_STATUS.LOADING , typeKey , originPageIndex , text , null , null);
                webview.setVisibility(GONE);
                mainTextView.setVisibility(VISIBLE);
                picImageView.setVisibility(GONE);
                pdfImageView.setVisibility(GONE);
                mainTextView.setText(text);
                callOnStatusChangedListener(specificListener , LOADING_STATUS.SUCCESS , typeKey , originPageIndex , text , null , null);
            }
        });
    }

    /**
     * 显示图片的子方法
     * @param url 要显示的图片的url
     * @param useCache 展示时是否使用缓存,不使用缓存时每次都会从网络上下载最新的数据.
     * @param ball 发起showContent命令的toPage请求的任务ball,可以用来插入中断检查点
     * @param typeKey 发起showContent命令的原始typeKey参数
     * @param originPageIndex 发起showContent命令的原始originPageIndex参数
     * @param specificListener 发起showContent命令的原始specificListener参数,可能为null
     * @throws InterruptedException 检测到中断,可能是其他toPage请求中断了当前toPage请求.
     */
    private void setImgUrl(String url , boolean useCache , Ball ball , String typeKey
            , int originPageIndex , StatusChangeListener specificListener) throws InterruptedException {
        //通知DOWNLOADING
        callOnStatusChangedListener(specificListener , LOADING_STATUS.DOWNLOADING , typeKey , originPageIndex , url , null , null);
        GlideDrawable drawable = null;
        //下载图片
        try {
            drawable = Glide.with(BaseActivity.getCurrentActivity())
                    .load(url).skipMemoryCache(useCache ? false : true)
                    .diskCacheStrategy(useCache ? DiskCacheStrategy.ALL : DiskCacheStrategy.NONE)
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            //下载失败,通知失败
            callOnStatusChangedListener(specificListener , LOADING_STATUS.ERROR, typeKey, originPageIndex, url, ERROR_TYPE.DOWNLOAD_ERROR, "下载图片时失败:" + e.getMessage());
        }
        ball.inserCheckPoint();
        //下载成功的逻辑
        if (drawable != null){
            final GlideDrawable finalDrawable = drawable;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callOnStatusChangedListener(specificListener , LOADING_STATUS.LOADING, typeKey , originPageIndex , url , null , null);
                    webview.setVisibility(GONE);
                    mainTextView.setVisibility(GONE);
                    picImageView.setVisibility(VISIBLE);
                    pdfImageView.setVisibility(GONE);
                    picImageView.setImageDrawable(finalDrawable);
                    callOnStatusChangedListener(specificListener , LOADING_STATUS.SUCCESS , typeKey , originPageIndex , url , null , null);
                }
            });
        }
    }


    /**
     * 显示pdf的子方法
     *
     * pdf如果是未展开的,加载的时候会展开并且把总页码以**页数##的格式拼在content的value后面
     * @param content 要显示的pdf的content
     * @param subPageIndex 要展示的pdf的内部页index
     * @param useCache 展示时是否使用缓存,不使用缓存时每次都会从网络上下载最新的数据.
     * @param ball 发起showContent命令的toPage请求的任务ball,可以用来插入中断检查点
     * @param typeKey 发起showContent命令的原始typeKey参数
     * @param originPageIndex 发起showContent命令的原始originPageIndex参数
     * @param specificListener 发起showContent命令的原始specificListener参数,可能为null
     * @throws InterruptedException 检测到中断,可能是其他toPage请求中断了当前toPage请求.
     */
    private void setPdf(Content_new  content , int subPageIndex , boolean useCache , Ball ball
            , String typeKey , int originPageIndex , StatusChangeListener specificListener) throws InterruptedException {
        String url;
        //获取正确的不带页码的pdf的url地址
        if (content.getValue().endsWith("##")){
            url = content.getValue().substring(0 , content.getValue().lastIndexOf("**"));
        }
        else {
            url = content.getValue();
        }
        try {
            //同步状态变量,-999表示还未加载完成
            int result[] = new int[]{-999};
            //开始加载pdf
            LoadController loadController = Plide.with(getContext())
                    .load(url)
                    .useCache(useCache)
                    .setLoadListener(new LoadListener() {
                @Override
                public void onLoadStatusChanged(LoadController.PDF_STATUS newStatus, float downloadProgress , int totalPage) {
                    switch (newStatus){
                        case DOWNLOADING:
                            //通知DOWNLOADING
                            callOnStatusChangedListener(specificListener , LOADING_STATUS.DOWNLOADING , typeKey , originPageIndex , url , null , null);
                            break;
                        case ERROR:
                            //根据下载进度区分是通知listener下载错误还是加载错误
                            if (downloadProgress == 100){
                                callOnStatusChangedListener(specificListener , LOADING_STATUS.ERROR , typeKey , originPageIndex , url , ERROR_TYPE.LOAD_ERROR, "加载pdf文档错误");
                            }
                            else {
                                callOnStatusChangedListener(specificListener , LOADING_STATUS.ERROR , typeKey , originPageIndex , url , ERROR_TYPE.DOWNLOAD_ERROR , "下载pdf文档错误");
                            }
                            //通知错误,-1是加载pdf遇到错误
                            synchronized (result){
                                result[0] = -1;
                                result.notify();
                            }
                            break;
                        case LOADING:
                            //通知LOADING
                            callOnStatusChangedListener(specificListener , LOADING_STATUS.LOADING , typeKey , originPageIndex , url , null , null);
                            break;
                        case LOADED:
                            synchronized (result){
                                //由于opendocummen成功和pdf topage成功时都会走到这里.所以用状态量做区分
                                if (result[0] == -999){
                                    //如果-999代表是opendocummen成功,通知继续进行pdf,topage
                                    result[0] = 0;
                                    result.notify();
                                    //更新页码
                                    content.setValue(url + "**" + totalPage + "##");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mContentAdaper != null){
                                                mContentAdaper.afterPageCountChanged(typeKey);
                                            }
                                        }
                                    });
                                }
                                else {
                                    //如果不是-999代表是pdf topage成功,直接通知listener加载完成
                                    webview.setVisibility(GONE);
                                    mainTextView.setVisibility(GONE);
                                    picImageView.setVisibility(GONE);
                                    pdfImageView.setVisibility(VISIBLE);
                                    callOnStatusChangedListener(specificListener , LOADING_STATUS.SUCCESS , typeKey , originPageIndex , url , null , null);
                                }
                            }
                            break;
                        case EMPTY:
                            //未知错误
                            callOnStatusChangedListener(specificListener , LOADING_STATUS.ERROR , typeKey , originPageIndex , url , ERROR_TYPE.LOAD_ERROR, "未知错误:Plide加载器空闲");
                            synchronized (result){
                                result[0] = -1;
                                result.notify();
                            }
                            break;
                    }
                }
            }).into(pdfImageView , false);
            //等待opendocument完成后再继续pdf topage.
            synchronized (result){
                if (result[0] == -999){
                    result.wait();
                }
            }
            if (result[0] == 0){
                ball.inserCheckPoint();
                loadController.toPage(subPageIndex);
            }
        } catch (PlideException e) {
            e.printStackTrace();
            callOnStatusChangedListener(specificListener , LOADING_STATUS.ERROR , typeKey , originPageIndex , url , ERROR_TYPE.LOAD_ERROR, "加载pdf文档错误");
        }
    }

    /**
     * 在主线程中通知listener状态发生变化
     * @param specificListener 为null时只会通知通用的listener,如果规定了特定specificListener ,则通知通用的listener之后也通知specificListener一下
     * @param newStatus 变化后的新状态
     * @param typeKey 导致变化的toPage请求的typeKey
     * @param pageIndex 导致变化的toPage请求的pageIndex
     * @param url 导致变化的toPage请求的url
     * @param errorType 如果newStatus是error,这里要传错误的类型,否则传null
     * @param errorMsg 如果newStatus是error,要传错误信息,否则传null
     */
    private void callOnStatusChangedListener(StatusChangeListener specificListener , LOADING_STATUS newStatus , String typeKey
            , int pageIndex , String url , ERROR_TYPE errorType , String errorMsg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(specificListener != null){
                    specificListener.onStatusChanged(newStatus, typeKey, pageIndex, url, errorType, errorMsg);
                }
                mStatusChangeListener.onStatusChanged(newStatus, typeKey, pageIndex, url, errorType, errorMsg);
            }
        });
    }

    /**
     * 同步方法!会导致调用的线程等待,不要在主线程里调用!
     *
     * 获取content的内部页总数,获取成功之前会使线程等待,成功之后才会返回值.
     *
     * 获取的页数会以**页数##的格式拼在content的value后面
     *
     * @param content 要获取页数的content
     * @param useCache 获取页数时是否采用缓存,如果不采用缓存每次都会从网络下载新的数据
     * @return content包含的页总数.
     * pdf才会返回其内部的页码总数,其他的类型都会返回1.
     * 不合法的content会返回-1.
     * 获取失败会返回-1.
     * 已展开的pdf类型的content会直接返回之前解析好的页总数
     */
    private int getContentSubpageCount_sync(Content_new content , boolean useCache){
        //不合法的content会返回-1.
        if (content == null){
            return -1;
        }
        //pdf才会返回其内部的页码总数,其他的类型都会返回1.
        if (content.getType() != Content_new.Type.PDF){
            return 1;
        }
        //已展开的pdf类型的content会直接返回之前解析好的页总数
        if (content.getValue().endsWith("##")){
            return Integer.parseInt(content.getValue().substring(
                    content.getValue().lastIndexOf("**") + 2, content.getValue().lastIndexOf("##")));
        }
        try {
            //同步状态量-999代表未完成获取
            int totalCountPageCount[] = new int[]{-999};
            Plide.with(getContext())
                    .load(content.getValue())
                    .useCache(useCache)
                    .setLoadListener(new LoadListener() {
                @Override
                public void onLoadStatusChanged(LoadController.PDF_STATUS newStatus, float downloadProgress, int totalPage) {
                    if (newStatus == LoadController.PDF_STATUS.ERROR){
                        synchronized (totalCountPageCount){
                            //-1表示获取失败
                            totalCountPageCount[0] = -1;
                            totalCountPageCount.notify();
                        }
                    }
                    if (newStatus == LoadController.PDF_STATUS.LOADED){
                        synchronized (totalCountPageCount){
                            //获取成功直接把获取到的页码值作为状态量返回
                            totalCountPageCount[0] = totalPage;
                            totalCountPageCount.notify();
                        }
                    }
                }
            }).into(pdfImageView , false);
            //获取完成之前等待
            synchronized (totalCountPageCount){
                if (totalCountPageCount[0] == -999){
                    totalCountPageCount.wait();
                }
            }
            //直接把状态量返回,成功是页码值,获取失败是-1
            return totalCountPageCount[0];
        } catch (PlideException e) {
            e.printStackTrace();
            return -1;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 在主线程中执行代码
     * @param runnable
     */
    protected static void runOnUiThread(Runnable runnable){
        Observable.empty()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
            @Override
            public void onCompleted() {
                runnable.run();
            }
            @Override
            public void onError(Throwable e) {}
            @Override
            public void onNext(Object o) {}
        });
    }

    /**
     * 状态变化监听器
     */
    public interface StatusChangeListener {
        /**
         * toPage导致WCD状态发生变化的回调,可以用来检测toPage进度
         *
         * 本回调都会在主线程中被调用!
         *
         * @param newStatus 变化后的新状态
         * @param typeKey 发生变化时的typeKey
         * @param url 发生变化时的url
         * @param pageIndex 发生变化时的pageIndex
         * @param errorType 如果有错误,错误类型是啥,无错误,传null
         * @param errorMsg 如果有错误,错误描述是啥,无错误,传null
         */
        public void onStatusChanged(LOADING_STATUS newStatus, String typeKey
                , int pageIndex, String url, ERROR_TYPE errorType, String errorMsg);
    }

}
