package com.yougy.common.manager;

import android.util.SparseIntArray;

import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.download.DownloadListener;
import com.yolanda.nohttp.download.DownloadRequest;
import com.yougy.common.nohttp.CallServer;
import com.yougy.common.nohttp.DownInfo;
import com.yougy.common.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/19.
 *  文件下载
 */
public class DownloadManager {

    private  static  List<DownloadRequest > mDownloadRequests = new ArrayList<>() ;
    private static List<DownInfo> mDownInfos = new ArrayList<>();
    private static SparseIntArray mIndexs = new SparseIntArray();
    /***
     *
     * @param downInfos 下载列表
     * @param downloadListener 下载回调
     */
    public static void downloadFile(List<DownInfo> downInfos ,DownloadListener downloadListener) {
        //取消之前的请求
        cancel();
        mDownloadRequests.clear();
        mDownInfos.clear();
        mIndexs.clear();
        mDownInfos .addAll(downInfos );
        for (DownInfo info : downInfos) {
            //如果使用断点续传的话，一定要指定文件名喔。
            // url 下载地址。
            // fileFolder 保存的文件夹。
            // fileName 文件名。
            // isRange 是否断点续传下载。
            // isDeleteOld 如果发现存在同名文件，是否删除后重新下载，如果不删除，则直接下载成功。
            DownloadRequest request = NoHttp.createDownloadRequest(info.getUrl(), info.getFileFolder(), info.getFilename(), info.isRange(), info.isDeleteOld());
            mDownloadRequests.add(request) ;
            // what 区分下载。
            // downloadRequest 下载请求对象。
            // downloadListener 下载监听。
            CallServer.getDownloadInstance().add(info.getWhat(), request, downloadListener);
            mIndexs.put(info.getWhat(),info.getIndex()) ;
        }
    }

    public static int  getIndex(int what){
        int index = 0;
        if (mIndexs!=null && mIndexs.size()>0){
            index = mIndexs.get(what) ;
        }
        return  index;
    }
    /**
     * 取消下载
     */
    public static void cancel() {
        if (mDownloadRequests!=null && mDownloadRequests.size()>0){
            for (DownloadRequest request:mDownloadRequests){
                request.cancel();
            }
        }
    }
    /**判断下载是否全部下载完成完成*/
    public  static  boolean isFinish(){
        boolean result = true;
        if (mDownInfos!=null && mDownInfos.size()>0){
            for (DownInfo info:mDownInfos){
                if (!info.isexists()){
                    result = false ;
                    LogUtils.i("没有下载完成"+info.getWhat());
                    break;
                }
            }
        }else{
            return  false ;
        }
        return  result;
    }

}

