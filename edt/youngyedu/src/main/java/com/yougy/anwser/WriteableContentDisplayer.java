package com.yougy.anwser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.target.Target;
import com.yougy.common.utils.LogUtils;
import com.yougy.plide.pipe.Ball;
import com.yougy.plide.pipe.Pipe;
import com.yougy.view.NoteBookView2;

import java.util.concurrent.ExecutionException;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Administrator on 2018/6/28.
 */

public class WriteableContentDisplayer extends RelativeLayout{
    //状态类
    public enum LOADING_STATUS {
        LOADING , //加载中
        ERROR , //错误
        SUCCESS //成功
    }

    //具体错误类型类
    public enum ERROR_TYPE {
        NO_SPECIFIED_CONTENT , //根据给定的条件找不到指定的内容
        LOAD_ERROR, //加载失败
        USER_CANCLE //用户取消加载
    }

    //默认的加载状态监听器
    private StatusChangeListener mStatusChangeListener = new StatusChangeListener() {
        @Override
        public void onStatusChanged(LOADING_STATUS newStatus, String typeKey, int pageIndex, ERROR_TYPE errorType, String errorMsg) {
            // do nothing..
        }
    };
    //以下是实际显示内容的几层控件,分第0层第1层第2层第3层,一共4层,数字大的层覆盖在数字小的层之上
    //第0层,显示题目的层.
    private ContentDisplayerV2 layer0;
    //第1层和第2层,都是手写层,只能显示图片和手写
    private NoteBookView2 layer1 , layer2;
    //第3层是提示文字层,在最上面.只能显示文字.
    //本控件内不会自动设置这一层的数据,但会提供api给使用者,使用者须自行维护本层的逻辑.默认是GONE的.
    private TextView hintLayer;

    //控件依赖的adapter适配器
    private WriteableContentDisplayerAdapter mAdapter;
    //用户规定序列化多个topage请求的管道类
    private Pipe toPagePipe = new Pipe();
    //第一层和第二层的缓存数据.
    //该缓存数据会在layer1和layer2数据发生变化时,自动recycle之前旧的数据.且在clearCache()中也会recycle这些缓存
    //除此之外不会自动回收,需要用户自己维护.
    private Bitmap layer1Bitmap , layer2Bitmap;
    //当前显示的typeKey,默认是null
    protected String currentTypeKey = null;
    //当前显示的pageIndex,默认是-1
    protected int currentPageIndex = -1;
    //当前是否使用了cache,默认是false,本值用在refresh上
    protected boolean currentIfUseCache = false;
    /**
     * 构造函数
     */
    public WriteableContentDisplayer(Context context) {
        this(context , null);
    }
    public WriteableContentDisplayer(Context context, AttributeSet attrs) {
        this(context, attrs , 0);
    }
    public WriteableContentDisplayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 获取第0层的实例(pdf显示层)
     * @return
     */
    public ContentDisplayerV2 getLayer0() {
        return layer0;
    }
    /**
     * 获取第1层的实例(下层手写层)
     * @return
     */
    public NoteBookView2 getLayer1() {
        return layer1;
    }
    /**
     * 获取第2层的实例(上层手写层)
     * @return
     */
    public NoteBookView2 getLayer2() {
        return layer2;
    }

    /**
     * 获取第3层提示文字层的实例
     * @return
     */
    public TextView getHintLayer(){
        return hintLayer;
    }

    /**
     * 设置adapter
     * @param adapter 为null时设置无效
     */
    public void setContentAdapter(WriteableContentDisplayerAdapter adapter){
        if  (adapter == null){
            LogUtils.e("不能把WriteableContentDisplayerAdapter 设置为null");
            return;
        }
        //adapter和WCD是双向绑定的,因此设置新adapter的时候需要先剪断之前adapter对本WCD的绑定
        if (this.mAdapter != null){
            this.mAdapter.setWriteableContentDisplayer(null);
        }
        this.mAdapter = adapter;
        //adapter和WCD双向绑定
        this.mAdapter.setWriteableContentDisplayer(this);
        this.layer0.setContentAdapter(mAdapter.getLayer0Adapter());
    }


    /**
     * 获取当前WCD绑定的adapter
     * @return
     */
    public WriteableContentDisplayerAdapter getContentAdapter() {
        return mAdapter;
    }

    /**
     * 设置状态变化监听器
     * @param mStatusChangeListener 为null时设置无效
     */
    public void setStatusChangeListener(StatusChangeListener mStatusChangeListener) {
        if (mStatusChangeListener != null){
            this.mStatusChangeListener = mStatusChangeListener;
        }
    }

    /**
     * 获取当前显示的pageIndex,默认是-1
     * @return
     */
    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    /**
     * 获取当前显示的typeKey,默认是null
     * @return
     */
    public String getCurrentTypeKey() {
        return currentTypeKey;
    }

    /**
     * 核心方法,切换到指定typeKey和pageIndex的内容.
     * 本方法的逻辑是:会根据给定的typeKey的pageIndex在adapter的3层数据源中查找层分别要显示的content,然后分别展示在3层layer上.
     * 如果页数基准层查找不到对应的content,会直接结束并且在listener中报NO_SPECIFIED_CONTENT的ERROR
     * 如果非页数基准层找不到对应的content,layer0会显示白板.手写层会clear之前的内容.
     * 如果在加载的过程中3层中的任意一层出现了加载错误,会直接在listener中报LOAD_ERROR.
     *
     * 每次调用toPage时都会保存这一次的toPage的typeKey,pageIndex和useCache,以便refresh的时候用.
     *
     * 注意----->【不管toPage是否成功,currentTypeKey,currentPageIndex,currentIfUseCache都会更新】
     *
     * 关于线程安全的说明:
     * toPage请求是以队列的形式提交执行的,具体行为是:
     * 当多个toPage请求依次快速发生时,请求并不会立即执行,而是以发起的先后顺序进入toPage请求执行管道
     * 后来的toPage请求会尝试取消之前的所有toPage请求.
     * 取消的方式分为两种,对正在执行中的toPage请求,会命令它在下一个中断点时中断执行.对于还未执行的toPage请求,会直接把它从toPage队列中移除.
     * 这样能保证同时只执行一个toPage请求,并且后发起的toPage请求会后执行.换句话说,保证了本控件是线程安全的.
     *
     *
     * @param typeKey 需要显示的数据所在数据源的typeKey
     * @param pageIndex 需要显示的数据所在数据源的pageIndex
     * @param useCache 根据数据源的数据下载数据时是否采用缓存.
     *                 如果为true,可以使用之前下载过的数据做展示,显示速度会更快,但是有可能网络源数据发生变化时,由于使用了缓存中的数据而导致显示错误.
     *                 如果为false,每次都重新下载网络源数据的数据进行展示,速度会比使用缓存更慢,但是会保证永远使用网络的最新数据.
     */
    public void toPage(String typeKey, int pageIndex, boolean useCache) {
        //保存之前的toPage参数,以传入beforeToPage
        final String fromTypeKey = getCurrentTypeKey();
        final int fromPageIndex = getCurrentPageIndex();
        //更新新的toPage参数
        currentTypeKey = typeKey;
        currentPageIndex = pageIndex;
        currentIfUseCache = useCache;
        //检测mAdapter非空
        if (mAdapter == null){
            LogUtils.e("toPage失败,没有绑定WriteableContentDisplayerAdapter");
        }
        //构建toPage请求,并push进toPage请求管道
        toPagePipe.push(new Ball(true){
            @Override
            public void run() throws InterruptedException {
                inserCheckPoint();
                //线程同步状态量
                boolean[] needWait = new boolean[]{true , true , true , true};
                //在主线程里执行beforeToPage.在beforeToPage的过程中用到的needWait[0]来表示同步状态.
                //needWait[0]=true代表beforeToPage还未执行完,还需要等待.
                //needWait[0]=false代表beforeToPage执行完毕,可以继续执行.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("执行beforeToPage--开始 typeKey=" + typeKey + " pageIndex=" + pageIndex + " useCache=" + useCache);
                        mAdapter.beforeToPage(fromTypeKey , fromPageIndex , typeKey , pageIndex);
                        LogUtils.e("执行beforeToPage--结束 typeKey=" + typeKey + " pageIndex=" + pageIndex + " useCache=" + useCache);
                        synchronized (needWait){
                            needWait[0] = false;
                            needWait.notify();
                        }
                    }
                });
                //然后等待beforeToPage执行完后继续toPage管道线程.
                synchronized (needWait){
                    while (needWait[0] == true){
                        try {
                            needWait.wait();
                        }
                        catch (InterruptedException e){

                        }
                    }
                }
                inserCheckPoint();

                //进行toPage的主逻辑
                if (pageIndex == -1 || pageIndex >= mAdapter.getPageCountBaseOnBaseLayer(typeKey)){
                    //判断如果页数基准层没有找到合适的内容,直接报NO_SPECIFIED_CONTENT的ERROR
                    callOnStatusChangedListener(LOADING_STATUS.ERROR , typeKey , pageIndex , ERROR_TYPE.NO_SPECIFIED_CONTENT , "没有查到要显示的内容");
                }
                else {
                    //分别加载layer0,layer1,layer2的内容
                    needWait[0] = true;//true表示layer0的内容加载逻辑未执行完,还需等待,为false表示layer0的内容已经加载完(或加载失败),无需再等待
                    needWait[1] = true;//true表示layer1的内容加载逻辑未执行完,还需等待,为false表示layer1的内容已经加载完(或加载失败),无需再等待
                    needWait[2] = true;//true表示layer2的内容加载逻辑未执行完,还需等待,为false表示layer2的内容已经加载完(或加载失败),无需再等待
                    needWait[3] = true;//整体toPage请求是否成功
                    //分别异步加载3个layer层
                    LogUtils.e("FH!!!! 开始加载三个图层");
                    showLayer0Content(typeKey , pageIndex , useCache , needWait);
                    showLayer1Content(typeKey , pageIndex , useCache , needWait);
                    showLayer2Content(typeKey , pageIndex , useCache , needWait);
                    //如果3层全部报告无需等待,则toPage管道线程继续,否则toPage管道线程一直等待.
                    synchronized (needWait) {
                        while (needWait[0] != false || needWait[1] != false || needWait[2] != false) {
                            try {
                                needWait.wait();
                            }
                            catch (InterruptedException e){

                            }
                        }
                    }
                    LogUtils.e("FH!!!! 三个图层加载完毕");
                    inserCheckPoint();
                    if (needWait[3] == true){
                        //如果3层全部成功,则提示listener成功.
                        callOnStatusChangedListener(LOADING_STATUS.SUCCESS , typeKey , pageIndex , null , null);
                    }
                    else {
                        //如果3层有一层失败,则提示listener失败.
                        callOnStatusChangedListener(LOADING_STATUS.ERROR , typeKey , pageIndex , ERROR_TYPE.LOAD_ERROR , "加载失败");
                    }
                }

                inserCheckPoint();
                //在主线程里执行afterToPage.在afterToPage的过程中用到的needWait[0]来表示同步状态.
                //needWait[0]=true代表afterToPage还未执行完,还需要等待.
                //needWait[0]=false代表afterToPage执行完毕,可以继续执行.
                needWait[0] = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("执行afterToPage--开始 typeKey=" + typeKey + " pageIndex=" + pageIndex + " useCache=" + useCache);
                        mAdapter.afterToPage(fromTypeKey , fromPageIndex , typeKey , pageIndex);
                        LogUtils.e("执行afterToPage--开始 typeKey=" + typeKey + " pageIndex=" + pageIndex + " useCache=" + useCache);
                        synchronized (needWait){
                            needWait[0] = false;
                            needWait.notify();
                        }
                    }
                });
                //然后等待afterToPag执行完后继续toPage管道线程.
                synchronized (needWait){
                    while (needWait[0] == true){
                        try {
                            needWait.wait();
                        }
                        catch (InterruptedException e){

                        }
                    }
                }
            }

            @Override
            public void onCancelled() {

            }
        });
    }

    /**
     * 按照上一次的toPage请求参数再做一次toPage,即刷新操作.
     * 可用于topage 报error之后的刷新逻辑.
     */
    public void refresh(){
        toPage(currentTypeKey , currentPageIndex , currentIfUseCache);
    }

    /**
     * 设置hint层的文字
     * @param hintText 为null时,如果scrollEnable,hintText层GONE,如果scrollDisable,hintText层透明(以便屏蔽scroll)----总之就是不会遮挡下面的内容
     *                 为""时,hintText层为不透明白底无字
     *                 为文字时,hintText层为白底文字
     */
    public void setHintText(String hintText){
        LogUtils.e("FH-----setHintText = " + hintText);
        if (hintText == null){
            hintLayer.setVisibility(GONE);
        }
        else {
            hintLayer.setVisibility(VISIBLE);
            hintLayer.setBackgroundColor(Color.WHITE);
            hintLayer.setText(hintText);
        }
    }

    /**
     * 清除缓存,在退出的时候调一下
     */
    public void clearCache() {
        layer0.clearPdfCache();
        layer1.recycle();
        layer2.recycle();

        if (layer1Bitmap != null && !layer1Bitmap.isRecycled()){
            layer1Bitmap.recycle();
        }
        if (layer2Bitmap != null && !layer2Bitmap.isRecycled()){
            layer2Bitmap.recycle();
        }

    }

    /**
     * 初始化4层控件
     */
    private void init(){
        layer0 = new ContentDisplayerV2(getContext());
        layer0.setLoadingStatusListener(new ContentDisplayerV2.StatusChangeListener() {
            @Override
            public void onStatusChanged(ContentDisplayerV2.LOADING_STATUS newStatus, String typeKey, int pageIndex, String url, ContentDisplayerV2.ERROR_TYPE errorType, String errorMsg) {

            }
        });
        LayoutParams contentDisplayerLayoutParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
        addView(layer0, contentDisplayerLayoutParam);

        layer1 = new NoteBookView2(getContext());
        LayoutParams layer1LayoutParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
        addView(layer1 , layer1LayoutParam);

        layer2 = new NoteBookView2(getContext());
        LayoutParams layer2LayoutParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
        addView(layer2, layer2LayoutParam);

        hintLayer = new TextView(getContext());
        hintLayer.setTextColor(Color.BLACK);
        hintLayer.setGravity(CENTER_IN_PARENT);
        LayoutParams hintLayerLayoutParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
        addView(hintLayer , hintLayerLayoutParam);
    }

    /**
     * 加载第0层的内容
     * @param typeKey 要加载的typeKey
     * @param pageIndex 要加载的pageIndex
     * @param useCache 是否使用cache缓存
     * @param needWait 线程同步状态量
     */
    private void showLayer0Content(String typeKey , int pageIndex , boolean useCache, boolean[] needWait){
        layer0.toPage(typeKey, pageIndex, useCache, new ContentDisplayerV2.StatusChangeListener() {
            @Override
            public void onStatusChanged(ContentDisplayerV2.LOADING_STATUS newStatus, String typeKey
                    , int pageIndex, String url, ContentDisplayerV2.ERROR_TYPE errorType, String errorMsg) {
                LogUtils.e("FH-----layer0 onStatusChanged newStatus=" + newStatus + " typeKey=" + typeKey
                 + " pageIndex=" + pageIndex + " url=" + url + " errorType=" + errorType + " errorMsg=" + errorMsg);
                switch (newStatus){
                    case SUCCESS:
                        //加载成功通知第0层加载完毕
                        synchronized (needWait){
                            layer0.setHintText(null);
                            needWait[0] = false;
                            needWait.notify();
                        }
                        break;
                    case ERROR:
                        //加载失败第0层置为白屏
                        layer0.setHintText("");
                        if (errorType == ContentDisplayerV2.ERROR_TYPE.NO_SPECIFIED_CONTENT
                                && mAdapter.getPageCountBaseLayerIndex() != 0){
                            //如果不是基准层,且没有内容可以加载,整体加载不算失败,通知第0层加载完毕
                            synchronized (needWait){
                                needWait[0] = false;
                                needWait.notify();
                            }
                        }
                        else {
                            //如果是基准层,或者出现其他错误,则整体算失败.needWait[3]=false,意思是加载失败,然后通知第0层加载完毕
                            synchronized (needWait){
                                needWait[0] = false;
                                needWait[3] = false;
                                needWait.notify();
                            }
                        }
                        break;
                }
            }
        });
    }
    private void showLayer1Content(String typeKey , int pageIndex , boolean useCache, boolean[] needWait){
        //尝试查找要显示的content.
        Content_new content = mAdapter.getContent(typeKey , pageIndex , 1);
        if (content == null){
            //查找不到content的情况下
            if (mAdapter.getPageCountBaseLayerIndex() != 1){
                //如果不是基准层,则清空layer1,显示透明,然后通知layer1加载完成
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layer1.clearAll();
                        if (layer1Bitmap!= null && !layer1Bitmap.isRecycled()){
                            layer1Bitmap.recycle();
                        }
                        LogUtils.e("FH-----layer1 没有可显示的内容,由于不是基准层,clear layer1! typeKey=" + typeKey + " pageIndex=" + pageIndex
                                + " useCache=" + useCache);
                        synchronized (needWait){
                            needWait[1] = false;
                            needWait.notify();
                        }
                    }
                });
            }
            else {
                //如果是基准层,则整体加载失败,并通知layer1加载完成
                LogUtils.e("FH-----layer1 出错 没有可显示的内容,并且layer1是基准层 无法显示 typeKey=" + typeKey + " pageIndex=" + pageIndex
                        + " useCache=" + useCache);
                synchronized (needWait){
                    needWait[1] = false;
                    needWait[3] = false;
                    needWait.notify();
                }
            }
        }
        else {
            //如果能查找到要显示的content
            new Thread(){
                @Override
                public void run() {
                    LogUtils.e("FH!!!!showlayer1Content start!");
                    try {
                        //尝试下载content内的数据,Glide的get是同步方法,只要返回就是成功,错误会报异常
                        GlideBitmapDrawable glideBitmapDrawable = (GlideBitmapDrawable) Glide.with(getContext())
                                .load(content.getValue())
                                .skipMemoryCache(useCache ? false : true)
                                .diskCacheStrategy(useCache ? DiskCacheStrategy.ALL : DiskCacheStrategy.NONE)
                                .into(Target.SIZE_ORIGINAL , Target.SIZE_ORIGINAL)
                                .get();
                        LogUtils.e("FH-----layer1 下载完毕 typeKey=" + typeKey + " pageIndex=" + pageIndex
                                + " useCache=" + useCache);
                        //下载成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //清空原来的数据
                                layer1.clearAll();
                                //画新数据
                                layer1.drawBitmap(glideBitmapDrawable.getBitmap());
                                //回收旧bitmap内存
                                if (layer1Bitmap != null && !layer1Bitmap.isRecycled()){
                                    layer1Bitmap.recycle();
                                }
                                //存新数据以供以后回收
                                layer1Bitmap = glideBitmapDrawable.getBitmap();
                                //通知加载完成
                                synchronized (needWait){
                                    LogUtils.e("FH-----layer1 加载完毕 typeKey=" + typeKey + " pageIndex=" + pageIndex
                                            + " useCache=" + useCache);
                                    needWait[1] = false;
                                    needWait.notify();
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        //下载出错逻辑,通知整体加载失败,通知layer1层加载完成.
                        e.printStackTrace();
                        synchronized (needWait){
                            LogUtils.e("FH-----layer1 出错! " + e.getMessage() + " typeKey=" + typeKey + " pageIndex=" + pageIndex
                                    + " useCache=" + useCache);
                            needWait[1] = false;
                            needWait[3] = false;
                            needWait.notify();
                        }
                    } catch (ExecutionException e) {
                        //下载出错逻辑,通知整体加载失败,通知layer1层加载完成.
                        e.printStackTrace();
                        synchronized (needWait){
                            LogUtils.e("FH-----layer1 出错! " + e.getMessage() + " typeKey=" + typeKey + " pageIndex=" + pageIndex
                                    + " useCache=" + useCache);
                            needWait[1] = false;
                            needWait[3] = false;
                            needWait.notify();
                        }
                    }
                    LogUtils.e("FH!!!!showlayer1Content end!");
                }
            }.start();
        }
    }


    private void showLayer2Content(String typeKey , int pageIndex , boolean useCache, boolean[] needWait){
        //尝试查找要显示的content.
        Content_new content = mAdapter.getContent(typeKey , pageIndex , 2);
        if (content == null){
            //查找不到content的情况下
            if (mAdapter.getPageCountBaseLayerIndex() != 2){
                //如果不是基准层,则清空layer2,显示透明,然后通知layer2加载完成
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layer2.clearAll();
                        if (layer2Bitmap!= null && !layer2Bitmap.isRecycled()){
                            layer2Bitmap.recycle();
                        }
                        LogUtils.e("FH-----layer2 没有可显示的内容,由于不是基准层,clear layer2! typeKey=" + typeKey + " pageIndex=" + pageIndex
                                + " useCache=" + useCache);
                        synchronized (needWait){
                            needWait[2] = false;
                            needWait.notify();
                        }
                    }
                });
            }
            else {
                //如果是基准层,则整体加载失败,并通知layer2加载完成
                LogUtils.e("FH-----layer2 出错 没有可显示的内容,并且layer2是基准层 无法显示 typeKey=" + typeKey + " pageIndex=" + pageIndex
                        + " useCache=" + useCache);
                synchronized (needWait){
                    needWait[2] = false;
                    needWait[3] = false;
                    needWait.notify();
                }
            }
        }
        else {
            //如果能查找到要显示的content
            new Thread(){
                @Override
                public void run() {
                    LogUtils.e("FH!!!!showlayer2Content start!");
                    try {
                        //尝试下载content内的数据,Glide的get是同步方法,只要返回就是成功,错误会报异常
                        GlideBitmapDrawable glideBitmapDrawable = (GlideBitmapDrawable) Glide.with(getContext())
                                .load(content.getValue())
                                .skipMemoryCache(useCache ? false : true)
                                .diskCacheStrategy(useCache ? DiskCacheStrategy.ALL : DiskCacheStrategy.NONE)
                                .into(Target.SIZE_ORIGINAL , Target.SIZE_ORIGINAL)
                                .get();
                        LogUtils.e("FH-----layer2 下载完毕 typeKey=" + typeKey + " pageIndex=" + pageIndex
                                + " useCache=" + useCache);
                        //下载成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //清空原来的数据
                                layer2.clearAll();
                                //画新数据
                                layer2.drawBitmap(glideBitmapDrawable.getBitmap());
                                //回收旧bitmap内存
                                if (layer2Bitmap != null && !layer2Bitmap.isRecycled()){
                                    layer2Bitmap.recycle();
                                }
                                //存新数据以供以后回收
                                layer2Bitmap = glideBitmapDrawable.getBitmap();
                                //通知加载完成
                                synchronized (needWait){
                                    LogUtils.e("FH-----layer2 加载完毕 typeKey=" + typeKey + " pageIndex=" + pageIndex
                                            + " useCache=" + useCache);
                                    needWait[2] = false;
                                    needWait.notify();
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        //下载出错逻辑,通知整体加载失败,通知layer2层加载完成.
                        e.printStackTrace();
                        synchronized (needWait){
                            LogUtils.e("FH-----layer2 出错! " + e.getMessage() + " typeKey=" + typeKey + " pageIndex=" + pageIndex
                                    + " useCache=" + useCache);
                            needWait[2] = false;
                            needWait[3] = false;
                            needWait.notify();
                        }
                    } catch (ExecutionException e) {
                        //下载出错逻辑,通知整体加载失败,通知layer2层加载完成.
                        e.printStackTrace();
                        synchronized (needWait){
                            LogUtils.e("FH-----layer2 出错! " + e.getMessage() + " typeKey=" + typeKey + " pageIndex=" + pageIndex
                                    + " useCache=" + useCache);
                            needWait[2] = false;
                            needWait[3] = false;
                            needWait.notify();
                        }
                    }
                    LogUtils.e("FH!!!!showlayer2Content end!");
                }
            }.start();
        }
    }


    /**
     * 在主线程里回调statusChangeListener
     * @param newStatus 变化后的新状态
     * @param typeKey 发生变化时的typeKey
     * @param pageIndex 发生变化时的pageIndex
     * @param errorType 如果有错误,错误类型是啥,无错误,传null
     * @param errorMsg 如果有错误,错误描述是啥,无错误,传null
     */
    private void callOnStatusChangedListener(LOADING_STATUS newStatus , String typeKey
            , int pageIndex , ERROR_TYPE errorType , String errorMsg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatusChangeListener.onStatusChanged(newStatus, typeKey, pageIndex, errorType, errorMsg);
            }
        });
    }

    /**
     * 工具方法,在主线程里执行代码
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
         * @param pageIndex 发生变化时的pageIndex
         * @param errorType 如果有错误,错误类型是啥,无错误,传null
         * @param errorMsg 如果有错误,错误描述是啥,无错误,传null
         */
        void onStatusChanged(LOADING_STATUS newStatus, String typeKey
                , int pageIndex, ERROR_TYPE errorType, String errorMsg);
    }
}
