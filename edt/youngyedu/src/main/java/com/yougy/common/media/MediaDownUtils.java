package com.yougy.common.media;

import android.content.Context;
import android.view.View;

import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.download.DownloadListener;
import com.yougy.common.manager.DownloadManager;
import com.yougy.common.manager.ThreadManager;
import com.yougy.common.nohttp.DownInfo;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.ui.activity.R;
import com.yougy.view.dialog.DownBookDialog;

import java.util.ArrayList;
import java.util.List;


public class MediaDownUtils implements DownBookDialog.DownBookListener {

    private String url1 = "http://espo.oss-cn-shanghai.aliyuncs.com/test/103030023.zip";
    private String url2 = "http://espo.oss-cn-shanghai.aliyuncs.com/test/103010007.zip";
    private List<String> mUrlList = new ArrayList<>();
    private DownBookDialog mProReadDialog;
    private MediaDownUtilsListener mMediaDownUtilsListener;
    private String mMediaID;
    private String mDownUrl;

    public MediaDownUtils() {
        mUrlList.add(url1);
        mUrlList.add(url2);
    }

    public interface MediaDownUtilsListener {
        void onMediaCancelListener();

        void onMediaDownFinishListener();
    }

    public void setMediaDownUtilsListener(MediaDownUtilsListener mediaDownUtilsListener) {
        mMediaDownUtilsListener = mediaDownUtilsListener;
    }


    public void downMediaZipFile(Context context, String mediaId) {
        mMediaID = mediaId;
        if (mMediaDownUtilsListener == null) {
            UIUtils.showToastSafe("setMediaDownUtilsListener 没有调用");
            return;
        }

        for (String url : mUrlList) {
            if (url.contains(mMediaID)) {
                mDownUrl = url;
                break;
            }
        }
        if (mProReadDialog == null) {
            mProReadDialog = new DownBookDialog(context);
            mProReadDialog.setListener(this);
        }
        mProReadDialog.show();
        mProReadDialog.getBtnConfirm().setVisibility(View.VISIBLE);
        mProReadDialog.runConfirmClick();
        mProReadDialog.setTitle(UIUtils.getString(R.string.down_book_defult));
    }

    @Override
    public void onCancelListener() {
        mProReadDialog.dismiss();
        DownloadManager.cancel();
        if (mMediaDownUtilsListener != null) {
            mMediaDownUtilsListener.onMediaCancelListener();
        }
    }

    @Override
    public void onConfirmListener() {
        mProReadDialog.getBtnConfirm().setVisibility(View.GONE);
        mProReadDialog.setTitle("准备下载语音包,请等待...");
        //下载文件
        List<DownInfo> mFiles = new ArrayList<>();
        DownInfo info = new DownInfo(mDownUrl, FileUtils.getMediaFilesDir(), mMediaID, true, false, 10);
        info.setBookName(mMediaID);
        mFiles.add(info);
        downBook(mFiles);
    }

    /***
     * 文件下载，下载位置 ，FileUtils.getTextBookFilesDir()
     */
    private void downBook(List<DownInfo> mFiles) {
        DownloadManager.downloadFile(mFiles, new DownloadListener() {
            @Override
            public void onDownloadError(int what, Exception exception) {
                LogUtils.i("  onDownloadError     what ........" + what);
                DownloadManager.cancel();
                mProReadDialog.setTitle(UIUtils.getString(R.string.down_media_error));
                mProReadDialog.getBtnConfirm().setVisibility(View.VISIBLE);
                mProReadDialog.getBtnConfirm().setEnabled(true);
            }

            @Override
            public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
                LogUtils.i("  onStart     what ........" + what);
            }

            @Override
            public void onProgress(int what, int progress, long fileCount) {
                mProReadDialog.setTitle(String.format(UIUtils.getString(R.string.down_media_loading), progress + "%"));
            }

            @Override
            public void onFinish(int what, String filePath) {
                if (DownloadManager.isFinish()) {
                    mProReadDialog.setTitle("正在安装，请等待.");
                    if (FileUtils.exists(FileUtils.getMediaFilesDir() + mMediaID + ".zip")) {
                        ThreadManager.getShortPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                LogUtils.e("开始解压文件");
                                boolean isSuccess = FileUtils.unZip3(FileUtils.getMediaFilesDir() + mMediaID + ".zip", FileUtils.getMediaFilesDir() + mMediaID);
                                if (isSuccess) {
                                    LogUtils.e("完成解压文件");
                                    FileUtils.deleteFile(FileUtils.getMediaFilesDir() + mMediaID + ".zip");
                                    UIUtils.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mProReadDialog.dismiss();
                                            if (mMediaDownUtilsListener != null) {
                                                mMediaDownUtilsListener.onMediaDownFinishListener();
                                            }
                                        }
                                    }) ;
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancel(int what) {
                mProReadDialog.dismiss();
                mProReadDialog = null;
            }
        });
    }
}
