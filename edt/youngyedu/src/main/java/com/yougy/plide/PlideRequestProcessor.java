package com.yougy.plide;

import android.content.Context;


import com.yougy.common.utils.LogUtils;
import com.yougy.plide.pipe.PlideReaderPresenter;

import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Administrator on 2018/7/9.
 */

public class PlideRequestProcessor {
    private ArrayList<PlideRequest> requestList = new ArrayList<PlideRequest>();
    private MyThread mThread;
    private boolean beenRecycled = false;
    private Downloader downloader = new CommonDownloader();
    private Context mContext;
    private PlideReaderPresenter presenter;

    private long validTimeStamp = 0;
    private PlideRequest lastFinishedRequest = null;
    private PlideRequest lastFailedRequest = null;

    public PlideRequestProcessor(Context context) {
        mThread = new MyThread();
        mThread.start();
        mContext = context;
        presenter = new PlideReaderPresenter(mContext);
    }

    public void push(PlideRequest request){
        synchronized (requestList){
            LogUtils.e("push request" + request);
            if (beenRecycled){
                LogUtils.e("push request" + request + "本processor已经被recycle了,本次push的request会直接被取消");
                request.onCancelled();
                request.setProcessor(null);
                return;
            }
            for (int i = 0 ; i < requestList.size() ; ) {
                PlideRequest requestInList = requestList.get(i);
                if (request != null && requestInList.getTimeStamp() > request.getTimeStamp()){
                    LogUtils.e("push request " + request + "找到该request应该所在的位置,将它插入该位置" + i);
                    requestList.add(i , request);
                    requestInList = request;
                    request = null;
                }
                LogUtils.e("push request " + request + "检查request列表,检查request " + requestInList);
                long rootRequestTimeStamp = getRootRequestTimeStamp(requestInList);
                LogUtils.e("push request " + request + "检查request列表,检查request " + requestInList + "获取到它的rootTimeStamp是" + rootRequestTimeStamp);
                if (rootRequestTimeStamp < validTimeStamp){
                    LogUtils.e("push request " + request + "检查request列表,检查request " + requestInList
                            + "它的rootTimeStamp" + rootRequestTimeStamp + "已经少于现在的validTimeStamp" + validTimeStamp + ",取消该请求,并且移出requstList");
                    requestList.remove(requestInList);
                    requestInList.onCancelled();
                    requestInList.setProcessor(null);
                    continue;
                }
                else if (rootRequestTimeStamp == validTimeStamp){
                    if (!(requestInList instanceof PlideToPageRequest)
                            || i == 0
                            || !(requestList.get(i-1) instanceof PlideToPageRequest)
                            || getRootRequestTimeStamp(requestList.get(i-1)) != rootRequestTimeStamp){
                        LogUtils.e("push request " + request + "检查request列表,检查request " + requestInList
                                + "它的rootTimeStamp" + rootRequestTimeStamp + "已经与现在的validTimeStamp" + validTimeStamp + "相同,并且前一个request为:"
                                + (i==0?"没有这个request":requestList.get(i).toString() + "不应该被其取消")
                                + ",检查下一个request");
                        i++;
                    }
                    else {
                        LogUtils.e("push request " + request + "检查request列表,检查request " + requestInList
                                + "它的rootTimeStamp" + rootRequestTimeStamp + "已经与现在的validTimeStamp" + validTimeStamp + "相同,前一个request"
                                + requestList.get(i).toString() + "需要被其取消,取消前一个request,然后检查下一个request");
                        PlideRequest toDeleteRequest = requestList.remove(i-1);
                        toDeleteRequest.onCancelled();
                        toDeleteRequest.setProcessor(null);
                        //TODO 今后有可能可以加入取消正在执行的其他toPage请求
                    }
                }
                else {
                    LogUtils.e("push request " + request + "检查request列表,检查request " + requestInList
                            + "它的rootTimeStamp" + rootRequestTimeStamp + "大于现在的validTimeStamp" + validTimeStamp + ",更新validTimeStamp,并且移出该请求之前的所有其他请求.然后继续检查下一个request");
                    validTimeStamp = rootRequestTimeStamp;
                    lastFinishedRequest = null;
                    lastFailedRequest = null;
                    while (true) {
                        PlideRequest toDeleteRequest = requestList.get(0);
                        if (toDeleteRequest == requestInList){
                            break;
                        }
                        requestList.remove(toDeleteRequest);
                        i--;
                        toDeleteRequest.onCancelled();
                        toDeleteRequest.setProcessor(null);
                    }
                    i++;
                    continue;
                }
            }
            if (request != null){
                LogUtils.e("push request " + request + "没有找到该request应该所在的位置");
                long rootRequestTimeStamp = getRootRequestTimeStamp(request);
                LogUtils.e("push request " + request + "获取它的rootTimeStamp" + rootRequestTimeStamp);
                if (rootRequestTimeStamp < validTimeStamp){
                    request.onCancelled();
                    request.setProcessor(null);
                    LogUtils.e("push request " + request + "它的rootTimeStamp" + rootRequestTimeStamp + "小于validTimeStamp" + validTimeStamp
                            + "直接将它取消,不插入requestList");
                }
                else if (rootRequestTimeStamp == validTimeStamp){
                    if (requestList.size() != 0
                            && (request instanceof PlideToPageRequest)
                            && (requestList.get(requestList.size()-1) instanceof PlideToPageRequest)
                            && rootRequestTimeStamp == getRootRequestTimeStamp(requestList.get(requestList.size()-1))){
                        LogUtils.e("push request " + request + "它的rootTimeStamp" + rootRequestTimeStamp + "与validTimeStamp" + validTimeStamp
                                + "相同,并且现在队尾的那个request需要被其取消,取消它");
                        PlideRequest toDeleteRequest = requestList.remove(requestList.size()-1);
                        toDeleteRequest.onCancelled();
                        toDeleteRequest.setProcessor(null);
                    }
                    LogUtils.e("push request " + request + "它的rootTimeStamp" + rootRequestTimeStamp + "与validTimeStamp" + validTimeStamp
                            + "相同,将它插入队尾");
                    requestList.add(request);
                }
                else {
                    LogUtils.e("push request " + request + "它的rootTimeStamp" + rootRequestTimeStamp + "大于validTimeStamp" + validTimeStamp
                            + ",将它插入队尾,并且清空并取消它之前的所有request");
                    requestList.add(request);
                    LogUtils.e("push request " + request + "由于更新了validTimeStamp,清空lastFinishedRequest和lastFailedRequest");
                    validTimeStamp = rootRequestTimeStamp;
                    lastFinishedRequest = null;
                    lastFailedRequest = null;
                    while (true) {
                        PlideRequest toDeleteRequest = requestList.get(0);
                        if (toDeleteRequest == request){
                            break;
                        }
                        requestList.remove(toDeleteRequest);
                        toDeleteRequest.onCancelled();
                        toDeleteRequest.setProcessor(null);
                    }
                }
            }
            requestList.notify();
        }
    }

    public void recycle(){
        synchronized (requestList){
            LogUtils.e("recycle!取消所有列表中的request,取消正在执行的request,并且标记本processor为recycled,以便取消今后的所有request");
            beenRecycled = true;
            PlideRequest request;
            while (requestList.size() > 0){
                request = requestList.remove(0);
                request.onCancelled();
                request.setProcessor(null);
            }
            mThread.cancelCurrentBall();
            mThread = null;
        }
    }

    public void cancleCurrentBall(){
        mThread.cancelCurrentBall();
    }


    protected Downloader getDownloader() {
        return downloader;
    }

    public PlideReaderPresenter getPresenter() {
        if (presenter == null){
            presenter = new PlideReaderPresenter(mContext);
        }
        return presenter;
    }

    public void setPresenter(PlideReaderPresenter presenter) {
        this.presenter = presenter;
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

    private long getRootRequestTimeStamp (PlideRequest request){
        if (request instanceof PlideToPageRequest){
            return request.mPreRequest.mPreRequest.getTimeStamp();
        }
        else if (request instanceof PlideOpenDocumentRequest){
            return request.mPreRequest.getTimeStamp();
        }
        else {
            return request.getTimeStamp();
        }
    }

    private class MyThread extends Thread{
        private PlideRequest currentRequest;

        public void cancelCurrentBall(){
            try {
                currentRequest.cancel();
                mThread.interrupt();
            }
            catch (NullPointerException e){
                //此处的空指针可以忽略
            }
        }

        @Override
        public void run() {
            while (true){
                synchronized (requestList){
                    try {
                        if (requestList.size() == 0){
                            LogUtils.e("PlideRequestProcessor寻找可执行的request过程中发现requestList中没有数据,进行等待");
                            requestList.wait();
                            LogUtils.e("PlideRequestProcessor寻找可执行的request过程中发现requestList中没有数据而进行的等待被唤醒,重新寻找可执行的request");
                            continue;
                        }
                        else {
                            PlideRequest plideRequest = requestList.get(0);
                            LogUtils.e("PlideRequestProcessor寻找可执行的request过程中发现队首的request为" + plideRequest + ",他的前置request为" + plideRequest.mPreRequest);
                            if (plideRequest.mPreRequest == null || plideRequest.mPreRequest.equals(lastFinishedRequest)){
                                LogUtils.e("PlideRequestProcessor寻找可执行的request过程中发现队首的request:" + plideRequest + "的前置request为null或与现在执行完的request:" + lastFinishedRequest + "一致,达到执行条件,因此开始执行request");
                                requestList.remove(plideRequest);
                                currentRequest = plideRequest;
                            }
                            else if (plideRequest.mPreRequest.equals(lastFailedRequest) ||
                                        (plideRequest.mPreRequest.mPreRequest != null && plideRequest.mPreRequest.mPreRequest.equals(lastFailedRequest))){
                                LogUtils.e("PlideRequestProcessor寻找可执行的request过程中发现队首的request:" + plideRequest + "的前置request或前置的前置request与已经失败的request:" + lastFinishedRequest + "一致,本request已经不可能执行,移出request队列并取消执行,然后重新尝试查找可执行的request");
                                requestList.remove(plideRequest);
                                plideRequest.onCancelled();
                                plideRequest.setProcessor(null);
                                continue;
                            }
                            else {
                                LogUtils.e("PlideRequestProcessor寻找可执行的request过程中发现队首的request:" + plideRequest + "的前置request不为null且与现在执行完的request:" + lastFinishedRequest + "不一致,没有达到执行条件,因此进行等待");
                                requestList.wait();
                                LogUtils.e("PlideRequestProcessor寻找可执行的request过程中发现队首的request:" + plideRequest + "不符合执行条件而进行的等待被唤起,重新尝试查找可执行的request");
                                continue;
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        if (beenRecycled){
                            LogUtils.e("PlideRequestProcessor等待可执行的request过程中被中断,由于beenRecycled为true,因此结束PlideRequestProcessor的生命周期");
                            return;
                        }
                        else {
                            LogUtils.e("PlideRequestProcessor等待可执行的request过程中被中断,由于beenRecycled为false,因此不结束PlideRequestProcessor的生命周期,重新寻找可执行的request");
                            interrupted();
                            continue;
                        }
                    }
                }
                if (beenRecycled){
                    LogUtils.e("PlideRequestProcessor执行request" + currentRequest + "之前自我检测processor已经被回收,因此结束procissor的生命周期");
                    return;
                }
                try {
                    if (currentRequest.isCanceld()){
                        LogUtils.e("PlideRequestProcessor执行request" + currentRequest + "之前检测该request已经被取消,因此取消执行");
                        currentRequest.onCancelled();
                        currentRequest.setProcessor(null);
                        interrupted();
                    }
                    else {
                        LogUtils.e("PlideRequestProcessor开始执行request" + currentRequest);
                        currentRequest.run();
                        LogUtils.e("PlideRequestProcessor执行request" + currentRequest + "完毕");
                        synchronized (requestList) {
                            if (currentRequest instanceof PlideDownloadRequest){
                                if (validTimeStamp == currentRequest.getTimeStamp()){
                                    LogUtils.e("PlideRequestProcessor执行request" + currentRequest + "完毕后,由于该request是关键request,因此更新finishRequest变量为" + currentRequest);
                                    lastFinishedRequest = currentRequest;
                                }
                            }
                            else if (currentRequest instanceof PlideOpenDocumentRequest) {
                                if (validTimeStamp == currentRequest.mPreRequest.getTimeStamp()){
                                    LogUtils.e("PlideRequestProcessor执行request" + currentRequest + "完毕后,由于该request是关键request,因此更新finishRequest变量为" + currentRequest);
                                    lastFinishedRequest = currentRequest;
                                }
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    currentRequest.onCancelled();
                    if (beenRecycled){
                        LogUtils.e("PlideRequestProcessor执行request" + currentRequest + "中被中断,由于beenRecycled为true,因此结束PlideRequestProcessor的生命周期");
                        currentRequest.setProcessor(null);
                        currentRequest = null;
                        return;
                    }
                    else {
                        LogUtils.e("PlideRequestProcessor执行request" + currentRequest + "中被中断,由于beenRecycled为false,可能是想要结束当前request去执行其他的request,因此PlideRequestProcessor的生命周期继续");
                        synchronized (requestList){
                            if (currentRequest instanceof PlideDownloadRequest){
                                if (validTimeStamp == currentRequest.getTimeStamp()){
                                    LogUtils.e("PlideRequestProcessor执行request" + currentRequest + "被中断后,由于该request是关键request,因此更新lastFailedRequest变量为" + currentRequest);
                                    lastFailedRequest = currentRequest;
                                }
                            }
                            else if (currentRequest instanceof PlideOpenDocumentRequest) {
                                if (validTimeStamp == currentRequest.mPreRequest.getTimeStamp()){
                                    LogUtils.e("PlideRequestProcessor执行request" + currentRequest + "被中断后,由于该request是关键request,因此更新lastFailedRequest变量为" + currentRequest);
                                    lastFailedRequest = currentRequest;
                                }
                            }
                        }
                        interrupted();
                    }
                }
                catch (PlideRunTimeException e){
                    e.printStackTrace();
                    LogUtils.e("PlideRequestProcessor执行request" + currentRequest + "失败.");
                    synchronized (requestList){
                        if (currentRequest instanceof PlideDownloadRequest){
                            if (validTimeStamp == currentRequest.getTimeStamp()){
                                LogUtils.e("PlideRequestProcessor执行request" + currentRequest + "失败后,由于该request是关键request,因此更新lastFailedRequest变量为" + currentRequest);
                                lastFailedRequest = currentRequest;
                            }
                        }
                        else if (currentRequest instanceof PlideOpenDocumentRequest) {
                            if (validTimeStamp == currentRequest.mPreRequest.getTimeStamp()){
                                LogUtils.e("PlideRequestProcessor执行request" + currentRequest + "失败后,由于该request是关键request,因此更新lastFailedRequest变量为" + currentRequest);
                                lastFailedRequest = currentRequest;
                            }
                        }
                    }
                }
                finally {
                    currentRequest.setProcessor(null);
                    currentRequest = null;
                }
            }
        }
    }
}
