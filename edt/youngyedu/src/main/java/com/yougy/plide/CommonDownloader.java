package com.yougy.plide;

import com.yougy.common.new_network.RxSchedulersHelper;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.plide.pipe.Ball;
import com.yougy.ui.activity.BuildConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by FH on 2018/1/12.
 */

public class CommonDownloader extends Downloader{
    private DownloadRetrofitApi retrofitApi;

    public CommonDownloader() {
        // 配置日志输出，因为Retrofit2不支持输出日志，只能用OkHttp来输出
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request orignaRequest = chain.request();
                Request request = orignaRequest.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .method(orignaRequest.method(), orignaRequest.body())
                        .build();

                return chain.proceed(request);
            }
        };

        builder.readTimeout(10*1000, TimeUnit.MILLISECONDS)
                .connectTimeout(10*1000, TimeUnit.MILLISECONDS)
                .writeTimeout(10*1000, TimeUnit.MILLISECONDS)
//                .addInterceptor(interceptor)
//                .addInterceptor(headerInterceptor)
                .retryOnConnectionFailure(true);

        OkHttpClient mClient = builder.build();


        Retrofit retrofit = new Retrofit.Builder()
                .client(mClient)
                .baseUrl("http://lovewanwan.top")
//                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        retrofitApi = retrofit.create(DownloadRetrofitApi.class);
    }


    public long getRemoteFileSize(String url){
        Result<Long> result = new Result<Long>();
        result.resultCode = -999;
        retrofitApi.downloadFile(url)
                .compose(RxSchedulersHelper.io_main())
                .subscribe(new Action1<ResponseBody>() {
                    @Override
                    public void call(ResponseBody responseBody) {
                        synchronized (result){
                            result.resultCode = 0;
                            result.setData(responseBody.contentLength());
                            result.notify();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        synchronized (result){
                            result.resultCode = -1;
                            result.setData(-1l);
                            result.notify();
                        }
                    }
                });
        synchronized (result){
            if (result.resultCode == -999){
                try {
                    result.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    result.resultCode = -1;
                    result.setData(-1l);
                }
            }
        }
        return result.data;
    }
    @Override
    public void forceDownload(String url, String saveFilePath, DownloadListener downloadListener , Ball ball) throws InterruptedException{
        ball.inserCheckPoint();
        retrofitApi.downloadFile(url).subscribe(new Action1<ResponseBody>() {
            @Override
            public void call(ResponseBody responseBody) {
                downloadListener.onDownloadStart(url , saveFilePath);
                long filesize = responseBody.contentLength();
                if (filesize <= 0){
                    downloadListener.onDownloadStop(url , saveFilePath , -2 , "要下载的文件大小为0");
                }
                File saveFile = new File(saveFilePath);
                if (saveFile.exists()){
                    if (saveFile.length() == filesize){
                        downloadListener.onDownloadFinished(url , saveFilePath , true);
                        return;
                    }
                    else {
                        saveFile.delete();
                        try {
                            saveFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                            saveFile = null;
                        }
                    }
                }
                else {
                    try {
                        saveFile = FileUtils.ifNotExistCreateFile(saveFilePath);
                    } catch (IOException e) {
                        downloadListener.onDownloadStop(url , saveFilePath , -2 , "创建文件失败");
                        e.printStackTrace();
                        return;
                    }
                }
                if (saveFile == null){
                    downloadListener.onDownloadStop(url , saveFilePath , -2 , "创建文件失败");
                    return;
                }
                InputStream inputStream = null;
                OutputStream outputStream = null;
                byte[] fileReader = new byte[4096];
                long fileSizeDownloaded = 0;
                try {
                    inputStream = responseBody.byteStream();
                    outputStream = new FileOutputStream(saveFile);
                    while (true) {
                        ball.inserCheckPoint();
                        int read = inputStream.read(fileReader);
                        if (read == -1) {
                            break;
                        }
                        outputStream.write(fileReader, 0, read);
                        fileSizeDownloaded += read;
                        downloadListener.onDownloadProgressChanged(url, saveFilePath, fileSizeDownloaded * 100 / filesize);
                    }

                    outputStream.flush();
                    downloadListener.onDownloadFinished(url , saveFilePath , false);
                } catch (FileNotFoundException e) {
                    downloadListener.onDownloadStop(url , saveFilePath , -2 , "要写入的文件找不到");
                    e.printStackTrace();
                } catch (IOException e) {
                    downloadListener.onDownloadStop(url , saveFilePath , -2 , "写入文件时发生错误");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    downloadListener.onDownloadStop(url , saveFilePath , -1 , "主动取消下载");
                    e.printStackTrace();
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                downloadListener.onDownloadStop(url , saveFilePath , -2 , "网络错误:" + throwable.getMessage());
            }
        });
    }

    @Override
    public void download(String url, boolean mUseCache , DownloadListener downloadListener , Ball ball) throws InterruptedException {
        LogUtils.e("commonDownloader : start download url=" + url + " useCache=" + mUseCache);
        if (url.startsWith("/")) { //TODO 可替换成正则
            LogUtils.e("commonDownloader : url=" + url + " 是本地地址,直接通知下载完毕");
            downloadListener.onDownloadStart(url, url);
            downloadListener.onDownloadProgressChanged(url, url, 100);
            downloadListener.onDownloadFinished(url, url , true);
        }
        else if (url.startsWith("http://")) { //TODO 可替换成正则
            LogUtils.e("commonDownloader : url=" + url + " 是网络地址");
            if (mUseCache){
                File file = new File(getSavePath(url));
                if (file.exists()){
                    LogUtils.e("commonDownloader : url=" + url + " 由于要求使用缓存,开始验证缓存文件大小是否与服务器上一致");
                    long size = getRemoteFileSize(url);
                    if (size == -1){
                        LogUtils.e("commonDownloader : url=" + url + " 无法获取服务器文件大小,通知错误信息");
                        downloadListener.onDownloadStart(url , getSavePath(url));
                        downloadListener.onDownloadStop(url , getSavePath(url), -2 , "获取要下载的文件大小失败");
                        return;
                    }
                    else if (size == file.length()){
                        LogUtils.e("commonDownloader : url=" + url + " 由于要求使用缓存并且缓存文件大小与服务器一致,直接通知下载完成");
                        downloadListener.onDownloadStart(url , getSavePath(url));
                        downloadListener.onDownloadProgressChanged(url, getSavePath(url), 100);
                        downloadListener.onDownloadFinished(url, getSavePath(url), true);
                        return;
                    }
                    else {
                        LogUtils.e("commonDownloader : url=" + url + " 由于要求使用缓存,但是缓存文件的大小与服务器不一致." +
                                "本地文件大小=" + file.length() + " 服务器文件大小=" + size + ",转入真实下载流程");
                    }
                }
                else {
                    LogUtils.e("commonDownloader : url=" + url + " 由于要求使用缓存,但是缓存文件不存在,转入真实下载流程");
                }
            }
            else {
                LogUtils.e("commonDownloader : url=" + url + " 由于要求不使用缓存,转入真实下载流程");
            }
            forceDownload(url, getSavePath(url), downloadListener, ball);
        }
    }

}
