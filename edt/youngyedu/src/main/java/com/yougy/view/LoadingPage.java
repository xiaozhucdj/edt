package com.yougy.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yougy.ui.activity.R;

/**
 * Created by Administrator on 2016/10/17.
 * 加载页面数据
 * <p/>
 * 1.多种请求，处理
 * 2.统一管理UI
 */
public class LoadingPage extends FrameLayout implements View.OnClickListener {

    /////////////////////////////////////////////View/////////////////////////////////////////////////
    //静态加载
    private ViewGroup mStaticLoadingLayout;
    private ImageView mImgStaticLoading;
    private TextView mTxtStaticLoading;

    //服务器错误
    private ViewGroup mErrorLayout;
    private ImageView mImgError;
    private TextView mTxtError;

    //服务器返回数据为空
    private ViewGroup mEmptyDataLayout;
    private ImageView mImgEmptyData;
    private TextView mTxtEmptyData;

    //请求超时
    private ViewGroup mConnectTimeoutLayout;
    private ImageView mImgConnectTimeout;
    private TextView mTxtConnectTimeout;

    //设备没有链接wifi
    private ViewGroup mNoNetworkLayout;
    private ImageView mImgNoNetwork;
    private TextView mTxtNoNetwork;

    /////////////////////////////////////////////File  Data/////////////////////////////////////////////////
    /**加载状态*/
    private  LoadingPageState mPageState ;

    private OnLoadPageTxtClickListener mListener;

    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    /////////////////////////////////////////////构造函数/////////////////////////////////////////////////
    public LoadingPage(Context context) {
        super(context);
        init(context, null);
    }

    public LoadingPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoadingPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    //////////////////////////////////////初始化////////////////////////////////////////////

    /**
     * 初始化
     *
     * @param context ：上下文
     * @param attrs   ：自定义属性
     */
    private void init(Context context, AttributeSet attrs) {
        //加载布局
        LayoutInflater.from(getContext()).inflate(R.layout.view_loading_page, LoadingPage.this, true);
        //查找布局
        findIdAndClick();
        //初始化自定义属性值
        initAttrs(context,attrs);

    }

    /**
     * 1.查找布局id
     * 2.设置点击事件
     */
    private void findIdAndClick() {
        //加载页
        mStaticLoadingLayout = findViewById(R.id.loadingPage_staticLoadingState);
        mImgStaticLoading = findViewById(R.id.loadingPage_staticLoadingState_image);
        mTxtStaticLoading = findViewById(R.id.loadingPage_staticLoadingState_text);
        mTxtStaticLoading.setOnClickListener(LoadingPage.this);

        //服务器异常错误
        mErrorLayout = findViewById(R.id.loadingPage_errorState);
        mImgError = findViewById(R.id.loadingPage_errorState_image);
        mTxtError = findViewById(R.id.loadingPage_errorState_text);
        mTxtError.setOnClickListener(LoadingPage.this);

        //服务器返回数据为空
        mEmptyDataLayout = findViewById(R.id.loadingPage_emptyDataState);
        mImgEmptyData = findViewById(R.id.loadingPage_emptyDataState_image);
        mTxtEmptyData = findViewById(R.id.loadingPage_emptyDataState_text);
        mTxtEmptyData.setOnClickListener(this);

        //请求超时
        mConnectTimeoutLayout = findViewById(R.id.loadingPage_connectTimeOutState);
        mImgConnectTimeout = findViewById(R.id.loadingPage_connectTimeOutState_image);
        mTxtConnectTimeout = findViewById(R.id.loadingPage_connectTimeOutState_text);
        mTxtConnectTimeout.setOnClickListener(this);

        //设备没有链接wifi
        mNoNetworkLayout = findViewById(R.id.loadingPage_noNetworkState);
        mImgNoNetwork = findViewById(R.id.loadingPage_noNetworkState_image);
        mTxtNoNetwork = findViewById(R.id.loadingPage_noNetworkState_text);
        mTxtNoNetwork.setOnClickListener(this);
    }

    /**
     * 根据自定义属性 初始化布局参数
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        //加载页
        CharSequence staticLoadingTxt = "";
        int txtColorStaticLoading = getResources().getColor(R.color.directory_text);
        int staticLoadingImgResId = R.drawable.img_error;

        //服务器异常错误
        CharSequence errorTxt = "";
        int txtColorError = getResources().getColor(R.color.directory_text);
        int errorImgResId = R.drawable.img_error;

        //设备没有链接wifi
        CharSequence noNetworkTxt = "";
        int txtColorNoNetwork = getResources().getColor(R.color.directory_text);
        int noNetworkImgResId = R.drawable.img_error;

        //请求超时
        CharSequence connectTimeOutTxt = "";
        int txtColorConnectTimeOut = getResources().getColor(R.color.directory_text);
        int connectTimeOutImgResId = R.drawable.img_error;

        //服务器返回数据为空
        CharSequence emptyDataTxt = "";
        int txtColorEmptyData = getResources().getColor(R.color.directory_text);
        int emptyDataImgResId = R.drawable.img_error;



        if(null != attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingPage);
            //属性值 ：文字内容
            staticLoadingTxt = typedArray.getString(R.styleable.LoadingPage_textWhenStaticLoading);
            noNetworkTxt = typedArray.getString(R.styleable.LoadingPage_textWhenNoNetwork);
            connectTimeOutTxt = typedArray.getString(R.styleable.LoadingPage_textWhenConnectTimeout);
            emptyDataTxt = typedArray.getString(R.styleable.LoadingPage_textWhenEmptyData);
            errorTxt = typedArray.getString(R.styleable.LoadingPage_textWhenError);

            //属性值 ：颜色
            txtColorStaticLoading = typedArray.getColor(R.styleable.LoadingPage_textColorWhenStaticLoading, txtColorStaticLoading);
            txtColorNoNetwork = typedArray.getColor(R.styleable.LoadingPage_textColorWhenNoNetwork, txtColorNoNetwork);
            txtColorConnectTimeOut = typedArray.getColor(R.styleable.LoadingPage_textColorWhenConnectTimeout, txtColorConnectTimeOut);
            txtColorEmptyData = typedArray.getColor(R.styleable.LoadingPage_textColorWhenEmptyData, txtColorEmptyData);
            txtColorError = typedArray.getColor(R.styleable.LoadingPage_textColorWhenError, txtColorError);

            //属性值 ：图片
            staticLoadingImgResId = typedArray.getResourceId(R.styleable.LoadingPage_imageResIdWhenStaticLoading, android.R.drawable.btn_default);
            noNetworkImgResId = typedArray.getResourceId(R.styleable.LoadingPage_imageResIdWhenNoNetwork, android.R.drawable.btn_default);
            connectTimeOutImgResId = typedArray.getResourceId(R.styleable.LoadingPage_imageResIdWhenConnectTimeout, android.R.drawable.btn_default);
            emptyDataImgResId = typedArray.getResourceId(R.styleable.LoadingPage_imageResIdWhenEmptyData, android.R.drawable.btn_default);
            errorImgResId = typedArray.getResourceId(R.styleable.LoadingPage_imageResIdWhenError, android.R.drawable.btn_default);

            // 回收
            typedArray.recycle();

            //绑定： 图片
            mImgStaticLoading.setImageResource(staticLoadingImgResId);
            mImgStaticLoading.setVisibility(View.GONE);

            mImgNoNetwork.setImageResource(noNetworkImgResId);
            mImgConnectTimeout.setImageResource(connectTimeOutImgResId);
            mImgEmptyData.setImageResource(emptyDataImgResId);
            mImgError.setImageResource(errorImgResId);

            //绑定： 文本内容
            mTxtStaticLoading.setText(staticLoadingTxt);
            mTxtStaticLoading.setVisibility(View.GONE);
            mTxtNoNetwork.setText(noNetworkTxt);
            mTxtConnectTimeout.setText(connectTimeOutTxt);
            mTxtEmptyData.setText(emptyDataTxt);
            mTxtError.setText(errorTxt);

            //绑定： 字体颜色
            mTxtStaticLoading.setTextColor(txtColorStaticLoading);
            mTxtNoNetwork.setTextColor(txtColorNoNetwork);
            mTxtConnectTimeout.setTextColor(txtColorConnectTimeOut);
            mTxtEmptyData.setTextColor(txtColorEmptyData);
            mTxtError.setTextColor(txtColorError);

        }
    }


    /////////////////////////////////////method public set get ////////////////////////////////////////////
    public void setOnLoadPageTxtClickListener(OnLoadPageTxtClickListener listener){
        mListener = listener;
    }

    public LoadingPageState getLoadingPageState(){
        return mPageState;
    }

    public void setLoadingPageState(LoadingPageState pageState){
        mPageState = pageState;
        refreshViewSafe();
    }



    public void setTxtWhenStaticLoading(CharSequence txtWhenStaticLoading){
        mTxtStaticLoading.setText(txtWhenStaticLoading);
    }


    public void setTxtWhenNoNetwork(CharSequence txtWhenNoNetwork){
        mTxtNoNetwork.setText(txtWhenNoNetwork);
    }

    public void setTxtWhenConnectTimeout(CharSequence txtWhenConnectTimeout){
        mTxtConnectTimeout.setText(txtWhenConnectTimeout);
    }

    public void setTxtWhenEmptyData(CharSequence txtWhenEmptyData){
        mTxtEmptyData.setText(txtWhenEmptyData);
    }

    public void setTxtWhenError(CharSequence txtWhenError){
        mTxtError.setText(txtWhenError);
    }

    public void setTxtColorWhenStaticLoading(int color){
        mTxtStaticLoading.setTextColor(color);
    }

    public void setTxtColorWhenNoNetwork(int color){
        mTxtNoNetwork.setTextColor(color);
    }

    public void setTxtColorWhenConnectTimeout(int color){
        mTxtConnectTimeout.setTextColor(color);
    }

    public void setTxtColorWhenEmptyData(int color){
        mTxtEmptyData.setTextColor(color);
    }

    public void setTxtColorWhenError(int color){
        mTxtError.setTextColor(color);
    }

    public void setImageResIdWhenStaticLoading(int resId){
        mImgStaticLoading.setBackgroundResource(resId);
    }



    public void setImageResIdWhenNoNetwork(int resId){
        mImgNoNetwork.setBackgroundResource(resId);
    }

    public void setImageResIdWhenConnectTimeout(int resId){
        mImgConnectTimeout.setBackgroundResource(resId);
    }

    public void setImageResIdWhenEmptyData(int resId){
        mImgEmptyData.setBackgroundResource(resId);
    }

    public void setImageResIdWhenError(int resId){
        mImgError.setBackgroundResource(resId);
    }


    protected void refreshViewSafe(){
        long mainTid = Looper.getMainLooper().getThread().getId();
        long currTid = Thread.currentThread().getId();

        if(currTid == mainTid){
            refreshView();
        }else{
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    refreshView();
                }
            });
        }
    }

    private void refreshView(){
        switch (mPageState) {

            case LOADING_WITH_STATIC:
                mStaticLoadingLayout.setVisibility(View.VISIBLE);
                mNoNetworkLayout.setVisibility(View.GONE);
                mConnectTimeoutLayout.setVisibility(View.GONE);
                mEmptyDataLayout.setVisibility(View.GONE);
                mErrorLayout.setVisibility(View.GONE);

                break;

            case NO_NETWORK:
                mNoNetworkLayout.setVisibility(View.VISIBLE);
                mStaticLoadingLayout.setVisibility(View.GONE);
                mConnectTimeoutLayout.setVisibility(View.GONE);
                mEmptyDataLayout.setVisibility(View.GONE);
                mErrorLayout.setVisibility(View.GONE);
                break;

            case CONNECT_TIME_OUT:
                mConnectTimeoutLayout.setVisibility(View.VISIBLE);
                mNoNetworkLayout.setVisibility(View.GONE);
                mStaticLoadingLayout.setVisibility(View.GONE);
                mEmptyDataLayout.setVisibility(View.GONE);
                mErrorLayout.setVisibility(View.GONE);
                break;

            case EMPTY_DATA:
                mEmptyDataLayout.setVisibility(View.VISIBLE);
                mNoNetworkLayout.setVisibility(View.GONE);
                mConnectTimeoutLayout.setVisibility(View.GONE);
                mStaticLoadingLayout.setVisibility(View.GONE);
                mErrorLayout.setVisibility(View.GONE);
                break;

            case ERROR:
                mErrorLayout.setVisibility(View.VISIBLE);
                mNoNetworkLayout.setVisibility(View.GONE);
                mConnectTimeoutLayout.setVisibility(View.GONE);
                mEmptyDataLayout.setVisibility(View.GONE);
                mStaticLoadingLayout.setVisibility(View.GONE);
                break;

            case IDLE:
                mErrorLayout.setVisibility(View.GONE);
                mNoNetworkLayout.setVisibility(View.GONE);
                mConnectTimeoutLayout.setVisibility(View.GONE);
                mEmptyDataLayout.setVisibility(View.GONE);
                mStaticLoadingLayout.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }


    @Override
    public void onClick(View v) {
        if(null != mListener){
            LoadingPageState state = LoadingPageState.IDLE;
            switch (v.getId()) {
                case R.id.loadingPage_staticLoadingState_text:
                    state =  LoadingPageState.LOADING_WITH_STATIC;
                    break;

                case R.id.loadingPage_noNetworkState_text:
                    state = LoadingPageState.NO_NETWORK;
                    break;

                case R.id.loadingPage_connectTimeOutState_text:
                    state = LoadingPageState.CONNECT_TIME_OUT;
                    break;

                case R.id.loadingPage_emptyDataState_text:
                    state = LoadingPageState.EMPTY_DATA;
                    break;

                case R.id.loadingPage_errorState_text:
                    state = LoadingPageState.ERROR;
                    break;

                default:
                    break;
            }
            mListener.onLoadPageTxtClick(this, state);
        }
    }

///////////////////////////////////////Class //////////////////////////////////////////////////////
    /**枚举  控制 loading状态*/
    public enum LoadingPageState{
        LOADING_WITH_STATIC,

        NO_NETWORK,

        CONNECT_TIME_OUT,

        EMPTY_DATA,

        ERROR,

        IDLE
    }

    /**点击 不同状态的  回调*/
    public interface OnLoadPageTxtClickListener{
        void onLoadPageTxtClick(LoadingPage loadingPage, LoadingPageState pageState);
    }
}
