package com.yougy.plide;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.reader.ReaderContract;
import com.onyx.reader.ReaderPresenter;
import com.yougy.common.media.MediaBean;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.plide.pipe.Ball;
import com.yougy.plide.pipe.Pipe;

import java.util.HashMap;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.yougy.plide.MyLog.lv;

/**
 * Created by FH on 2018/1/12.
 */

public class LoadController implements ReaderContract.ReaderView{
    private static final String DLDirPath = FileUtils.getAppFilesDir() + "Plide/";
    private static HashMap<String , LoadController> loadControllerTempStorage = new HashMap<String , LoadController>();

    private static String getHashString(Object o){
        return Integer.toHexString(System.identityHashCode(o));
    }
    protected static LoadController getLoadController(ImageView imageView){
        LoadController toReturnControlloer = loadControllerTempStorage.get(getHashString(imageView));
        if (toReturnControlloer == null){
            toReturnControlloer = new LoadController(imageView);
            loadControllerTempStorage.put(getHashString(imageView) , toReturnControlloer);
        }
        return toReturnControlloer;
    }

    protected static LoadController popLoadController(ImageView imageView){
        LoadController loadController = loadControllerTempStorage.remove(getHashString(imageView));
        return loadController;
    }

    private PDF_STATUS currentStatus = PDF_STATUS.EMPTY;
    private Pipe downloadPipe;
    private Pipe loadPdfPipe;
    private Pipe toPagePipe;
    private Downloader downloader = new CommonDownloader();
    private ReaderPresenter mReaderPresenter;
    private ImageView mImgView;
    private LoadListener mLoadListener;
    private String mSavePath;
    private String mUrl;
    private Context mContext;
    private final Object currentStatusLock = new Object();
    private final Object mReaderPresentLock = new Object();

    protected ReaderPresenter getReaderPresenter() {
        synchronized (mReaderPresentLock){
            //TODO 不可靠的单例模式,今后改正
            if (mReaderPresenter == null) {
                mReaderPresenter = new ReaderPresenter(this);
            }
            return mReaderPresenter;
        }
    }

    protected boolean isReaderPresenterAbandoned(){
        return mReaderPresenter == null;
    }

    protected void abandonReaderPresenter(){
        synchronized (mReaderPresentLock){
            mReaderPresenter = null;
        }
    }

    private LoadController(ImageView imageView) {
        mImgView = imageView;
        currentStatus = PDF_STATUS.EMPTY;
        downloadPipe = new Pipe();
        loadPdfPipe = new Pipe();
        toPagePipe = new Pipe();
    }

    public enum PDF_STATUS{
        EMPTY , DOWNLOADING , LOADING , LOADED , ERROR
    }

    public PDF_STATUS getCurrentStatus() {
        return currentStatus;
    }

    private void setCurrentStatus(PDF_STATUS status , String savePath){
        synchronized (currentStatusLock){
            currentStatus = status;
            if (savePath != null){
                mSavePath = savePath;
            }
        }
    }

    private void callLoadListener(float downloadProgress) {
        Observable.just(111).subscribeOn(Schedulers.immediate()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                try {
                    if (mLoadListener != null) {
                        mLoadListener.onLoadStatusChanged(currentStatus, downloadProgress, getReaderPresenter().getPages());
                    }
                } catch (NullPointerException e) {
                    // 此处忽略
                }
            }
        });
    }

    protected LoadController doLoadMainLogic(String url , LoadListener loadListener , Context context , boolean forceBaseWidth){
          LogUtils.e("FHHHH" , "doLoadMainLogic url = " + url + " loadListener " + loadListener + " context " + context + " threadId " + Thread.currentThread().getId());
          LogUtils.e("FHHHH" , "-----------------------------!!!!!!!!!!!!!!---------------setImgview Null!!!!!!!");
        this.mContext = context;
        mUrl = url;
        if (loadListener != null){
            mLoadListener = loadListener;
        }
        download(url , new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String savePath) {
                  LogUtils.e("FHHHH" , "onDownloadStart url = " + url + " savePath " + savePath + " threadId " + Thread.currentThread().getId());
                setCurrentStatus(PDF_STATUS.DOWNLOADING , null);
                callLoadListener(0);
            }

            @Override
            public void onDownloadProgressChanged(String url, String savePath, float progress) {
                  LogUtils.e("FHHHH" , "onDownloadProgressChanged url = " + url + " savePath " + savePath + " progress "  + progress
                        + " threadId " + Thread.currentThread().getId());
                callLoadListener(progress);
            }

            @Override
            public void onDownloadStop(String url, String savePath, int errorCode, String reason) {
                  LogUtils.e("FHHHH" , "onDownloadStop url = " + url + " savePath " + savePath + " errorCode " + errorCode + " reason " + reason
                        + " threadId " + Thread.currentThread().getId());
                if (errorCode == -1){
                    setCurrentStatus(PDF_STATUS.EMPTY , null);
                    callLoadListener(0);
                }
                else if (errorCode == -2){
                    setCurrentStatus(PDF_STATUS.ERROR , null);
                    callLoadListener(0);
                }
            }

            @Override
            public void onDownloadFinished(String url, String savePath , boolean noNeedToDownload) {
                  LogUtils.e("FHHHH" , "onDownloadFinished url = " + url + " savePath " + savePath + " noNeedToDownload " + noNeedToDownload
                        + " threadId " + Thread.currentThread().getId());
                synchronized (currentStatusLock){
                    if (mSavePath != null && mSavePath.equals(savePath) && noNeedToDownload){
                          LogUtils.e("FHHHH" , "onDownloadFinished 需要下载的文件内容没有变化,并且已经加载了该pdf,不再发起load请求..."
                                + "url = " + url + " savePath " + savePath + " noNeedToDownload " + noNeedToDownload
                                + " threadId " + Thread.currentThread().getId());
                        setCurrentStatus(PDF_STATUS.LOADED , getReaderPresenter().getReader().getDocumentPath());
                        currentStatusLock.notify();
                        callLoadListener(100);
                        return;
                    }
                }
                loadPdfPipe.push(new Ball(false){
                    @Override
                    public void run() throws InterruptedException {
                          LogUtils.e("FHHHH" , "run--load pdf savePath " + savePath
                                + " threadId" + Thread.currentThread().getId());
                        setCurrentStatus(PDF_STATUS.LOADING , null);
                        callLoadListener(100);
                        getReaderPresenter().close();
                        Runtime.getRuntime().gc();
                        abandonReaderPresenter();

                        getReaderPresenter().openDocument(savePath , null);
                          LogUtils.e("FHHHH" , "load命令完成,等待回调中断 savePath " + savePath
                                + " threadId" + Thread.currentThread().getId());
                        while (true){
                            Thread.sleep(1000*999);
                        }
                    }

                    @Override
                    public void onCancelled() {

                    }
                });
            }
        });
        return this;
    }

    public void toPage(int pageIndex){
        String currentUrl = mUrl;
        toPagePipe.push(new Ball(true){
            @Override
            public void run() throws InterruptedException {
                synchronized (currentStatusLock){
                      LogUtils.e("FHHHHH" , "topage run--- pageIndex " + pageIndex
                            + " currentStatus " + currentStatus + " mSavePath " + mSavePath
                            + " currentUrl " + currentUrl
                            + " threadId " + Thread.currentThread().getId());
                    while (currentStatus != PDF_STATUS.LOADED
                            || TextUtils.isEmpty(mSavePath)
                            || !mSavePath.equals(getSavePath(currentUrl))){
                          LogUtils.e("FHHHH" , "toPage run---wait pageIndex " + pageIndex
                                + " threadId : " + Thread.currentThread().getId());
                        currentStatusLock.wait();
                          LogUtils.e("FHHHH" , "toPage run---wait end pageIndex " + pageIndex
                                + " threadId " + Thread.currentThread().getId());
                    }
                      LogUtils.e("FHHHH" , "toPage run---正式开始 pageIndex " + pageIndex
                            + " threadId " + Thread.currentThread().getId());
                    setCurrentStatus(PDF_STATUS.LOADING , null);
                    callLoadListener(100);
                    getReaderPresenter().gotoPage(pageIndex);
                      LogUtils.e("FHHHH" , "topage run---gotoPage命令完成 , 等待完成中断... pageIndex " + pageIndex
                            + " threadId " + Thread.currentThread().getId());
                    while (true){
                        Thread.sleep(1000*999);
                    }
                }
            }

            @Override
            public void onCancelled() {

            }
        });
    }

    private static String getSavePath(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (url.startsWith("/")) { //TODO 可替换成正则
                return url;
            } else if (url.startsWith("http://")) { //TODO 可替换成正则
                String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
                if (!TextUtils.isEmpty(fileName)) {
                    return DLDirPath + fileName;
                }
            }
        }
        return null;
    }

    private void download(String url, DownloadListener downloadListener) {
        if (url.startsWith("/")) { //TODO 可替换成正则
            downloadPipe.push(new Ball(true) {
                @Override
                public void run() throws InterruptedException {
                    downloadListener.onDownloadStart(url, url);
                    downloadListener.onDownloadProgressChanged(url, url, 100);
                    downloadListener.onDownloadFinished(url, url , true);
                }

                @Override
                public void onCancelled() {

                }
            });
        }
        else if (url.startsWith("http://")) { //TODO 可替换成正则
            downloadPipe.push(new Ball(true) {
                @Override
                public void run() throws InterruptedException {
                    downloader.forceDownload(url, getSavePath(url), downloadListener, this);
                }

                @Override
                public void onCancelled() {

                }
            });
        }
    }

    @Override
    public Context getViewContext() {
        return mContext;
    }

    @Override
    public void updatePage(int page, Bitmap bitmap,MediaBean bean) {
        lv("updatePage page=" + page + "  bitmap=" + bitmap + " threadId : " + Thread.currentThread().getId());
        lv("____________________________________!!!!!!!!!!!!!!!!!!!_______________________________" + bitmap
                + " recycle " + bitmap.isRecycled());
        mImgView.setImageBitmap(bitmap);
        toPagePipe.cancleCurrentBall();
        synchronized (currentStatusLock){
            setCurrentStatus(PDF_STATUS.LOADED , null);
            currentStatusLock.notify();
        }
        callLoadListener(100);
    }



    @Override
    public View getContentView() {
        lv("getContentView");
        return mImgView;
    }

    @Override
    public void showThrowable(Throwable throwable) {
        throwable.printStackTrace();
        lv("showThrowable " + throwable);
        setCurrentStatus(PDF_STATUS.ERROR , null);
        callLoadListener(100);
    }

    @Override
    public void openDocumentFinsh() {
        lv("openDocumentFinsh!! total page=" + getReaderPresenter().getPages()
                + "  Doc Path=" + getReaderPresenter().getReader().getDocumentPath());
        loadPdfPipe.cancleCurrentBall();
        synchronized (currentStatusLock){
            setCurrentStatus(PDF_STATUS.LOADED , getReaderPresenter().getReader().getDocumentPath());
            currentStatusLock.notify();
        }
        callLoadListener(100);
    }

    @Override
    public void updateDirectory(ReaderDocumentTableOfContent content) {
        lv("updateDirectory " + content);
    }
}
