package com.yougy.common.media.file;

import android.content.Context;
import android.view.View;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.ThreadManager;
import com.yougy.common.new_network.ApiException;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.DataCacheUtils;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.SystemUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.init.bean.BookInfo;
import com.yougy.ui.activity.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import rx.functions.Action1;
import rx.functions.Func1;

/**
 * 下载图书 音频 配置文件的 下载器
 */
public class DownFileManager {
    private String TAG = DownFileManager.class.getName();

    private final String FILE_BOOK = "BM04";

    private final String FILE_AUDIO = "BM06";

    private final String FILE_CONFIG = "BM07";

    private final String AUDIO_TAG = "audio_tag";

    private final String AUDIO_CONFIG_TAG = "audioconfig_tag";

    private int mBookId;
    //（默认BM04为图书，BM06为音频zip包，BM07为音频位置文件包）
    private String mType;

    private String mBookStatusCode;

    private String mBookAudio;

    private String mBookAudioConfig;

    private DownFileListener mListener;

    private Context mContext;
    private DownFileDialog mDialog;
    private DownOssBean mOssBean;


    public DownFileManager(Context context, DownFileListener listener) {
        this.mListener = listener;
        mContext = context;
    }


    public void requestDownFile(BookInfo info) {
        requestDownFile(info.getBookId(), info.getBookStatusCode(), info.getBookAudio(), info.getBookAudioConfig());
    }

    public void requestDownFile(int booId, String bookStatusCode, String bookAudio, String bookAudioConfig) {
        mOssBean = null;
        mBookId = booId;
        mBookStatusCode = bookStatusCode;
        mBookAudio = bookAudio;
        mBookAudioConfig = bookAudioConfig;
        //判断语音文件是否有更新：
        LogUtils.e(TAG, "bookStatusCode  bookStatusCode...." + bookStatusCode);
        LogUtils.e(TAG, "图书path" + FileUtils.getBookFileName(booId, FileUtils.bookDir));
        LogUtils.e(TAG, "  图书是否存在." + StringUtils.isEmpty(FileUtils.getBookFileName(booId, FileUtils.bookDir)));

        if (!StringUtils.isEmpty(FileUtils.getBookFileName(booId, FileUtils.bookDir))) {
            String keys = DataCacheUtils.getBookString(UIUtils.getContext(), FileContonst.DOWN_LOAD_BOOKS_KEY);
            if (StringUtils.isEmpty(keys) || !keys.contains(booId + "")) {
                // 本地图书 没秘钥 删除
                LogUtils.e("本地图书 没秘钥 删除");
                FileUtils.deleteFile((FileUtils.getTextBookFilesDir() + booId + ".pdf"));
            }
        }

        if (!StringUtils.isEmpty(bookStatusCode) && FileContonst.SERVER_BOOK_STATU_SCODE.contains(bookStatusCode)) {
            LogUtils.e(TAG, "判断语音文件是否有更新：");

            String localAudio = SpUtils.getMediaString(booId + AUDIO_TAG);
            String localAudioConfig = SpUtils.getMediaString(booId + AUDIO_CONFIG_TAG);

            LogUtils.e(TAG, "------------------XML--START--------------");
            LogUtils.e(TAG, "localAudio：====" + localAudio);
            LogUtils.e(TAG, "localAudioConfig：====" + localAudioConfig);
            LogUtils.e(TAG, "bookAudio：====" + bookAudio);
            LogUtils.e(TAG, "bookAudioConfig：====" + bookAudioConfig);
            LogUtils.e(TAG, "------------------XML---END-------------");

            if (!StringUtils.isEquals(localAudio, bookAudio)) {
                LogUtils.e(TAG, "bookAudio  delete");
                FileUtils.delFileOrFolder(FileUtils.getMediaMp3Path() + mBookId + "/");
            }

            if (!StringUtils.isEquals(localAudioConfig, bookAudioConfig)) {
                LogUtils.e(TAG, "bookAudioConfig  delete");
                FileUtils.delFileOrFolder(FileUtils.getMediaJsonPath() + mBookId + "/");
            }
        }


        if (StringUtils.isEmpty(FileUtils.getBookFileName(booId, FileUtils.bookDir))) {
            mType = FILE_BOOK; //下载图书
            LogUtils.e(TAG, "下载图书：");
            showDownFileDialog();

        } else if (!StringUtils.isEmpty(bookStatusCode) && FileContonst.SERVER_BOOK_STATU_SCODE.contains(bookStatusCode)) {
            if (!FileUtils.exists(FileUtils.getMediaMp3Path() + mBookId + "/") && !StringUtils.isEmpty(mBookAudio)) {
                LogUtils.e(TAG, "下载音频：");
                mType = FILE_AUDIO; //下载音频
                showDownFileDialog();
            } else if (!FileUtils.exists(FileUtils.getMediaJsonPath() + mBookId + "/") && !StringUtils.isEmpty(mBookAudioConfig)) {
                LogUtils.e(TAG, "下载配置文件：");
                mType = FILE_CONFIG; //下载配置文件
                showDownFileDialog();
            } else {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                //进入阅读
                LogUtils.e(TAG, "没有下载的 音频文件 和配置 进入 进入阅读");
                mListener.onDownFileListenerCallBack(mListener.STATE_NORMAL);
            }
        } else {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            LogUtils.e(TAG, " 当前课本  不支持 点读。。进入阅读");
            //进入阅读
            mListener.onDownFileListenerCallBack(mListener.STATE_NORMAL);
        }
    }


    private void showDownFileDialog() {
        if (mDialog == null) {
            mDialog = new DownFileDialog(mContext);

        }

        mDialog.setListener(new DownFileDialog.ClickListener() {
            @Override
            public void onBtnConfirmListener() {
                //请求网络 获取 下载 地址
                httpGetFileUrl();
            }

            @Override
            public void onBtnCancelListener() {
                //取消下载
                cancelDownFile();
            }
        });

        if (!mDialog.isShowing()) {
            mDialog.show();
        }
        String str = "正在下载";
        switch (mType) {
            case FILE_BOOK:
                str = "正在下载图书,时间较长,耐心等待。";
                break;
            case FILE_AUDIO:
                str = "正在下载音频文件,时间较长,耐心等待。";
                break;
            case FILE_CONFIG:
                str = "正在下载音频配置,时间较长,耐心等待。";
                break;

        }
        mDialog.setTitle(str);
        mDialog.setBtConfirmCallBack();
        mDialog.setBtnConfirmVisibility(View.GONE);
        mDialog.setBtnCancelVisibility(View.VISIBLE);
    }


    private void httpGetFileUrl() {
        mOssBean = null;
        NetWorkManager.downloadFile(mBookId + "", mType).filter(new Func1<List<OssInfoBean>, Boolean>() {
            @Override
            public Boolean call(List<OssInfoBean> ossInfoBeans) {
                return ossInfoBeans != null;
            }
        }).subscribe(new Action1<List<OssInfoBean>>() {
            @Override
            public void call(List<OssInfoBean> ossInfoBeans) {

                if (ossInfoBeans != null && ossInfoBeans.size() > 0) {
                    for (OssInfoBean bean : ossInfoBeans) {
                        if (bean.getAtchSupport().equalsIgnoreCase(SystemUtils.getDeviceModel())) {
                            mOssBean = new DownOssBean();
                            mOssBean.setBookId(mBookId + "");
                            mOssBean.setAccessKeyId(bean.getAccessKeyId());
                            mOssBean.setAccessKeySecret(bean.getAccessKeySecret());
                            mOssBean.setSecurityToken(bean.getSecurityToken());
                            mOssBean.setExpiration(bean.getExpiration());
                            mOssBean.setObjectKey(bean.getAtchRemotePath());
                            mOssBean.setBucketName(bean.getAtchBucket());
                            mOssBean.setType(mType);
                            switch (mType) {
                                case FILE_BOOK:
                                    LogUtils.e("BM04....");
                                    mOssBean.setSaveFilePath(FileUtils.getTextBookFilesDir() + mBookId + ".pdf");
                                    //保存 图书秘钥
                                    saveDrmKey(mBookId, bean.getAtchEncryptKey());
                                    break;
                                case FILE_AUDIO:
                                    LogUtils.e("BM06....");
                                    mOssBean.setSaveFilePath(FileUtils.getMediaMp3Path() + mBookId + ".zip");
                                    break;
                                case FILE_CONFIG:
                                    LogUtils.e("BM07.....");
                                    mOssBean.setSaveFilePath(FileUtils.getMediaJsonPath() + mBookId + ".zip");
                                    break;
                            }
                            break;
                        }
                    }

                    if (mOssBean == null) {
                        LogUtils.e("没有支持的设备型号。。。" + mType);
                        mDialog.dismiss();
                        switch (mType) {
                            case FILE_BOOK:
                                mListener.onDownFileListenerCallBack(mListener.STATE_NO_SUPPORT_BOOK);
                                break;
                            case FILE_AUDIO:
                                mListener.onDownFileListenerCallBack(mListener.STATE_NO_SUPPORT_AUDIO);
                                break;
                            case FILE_CONFIG:
                                mListener.onDownFileListenerCallBack(mListener.STATE_NO_SUPPORT_CONFIG);
                                break;
                        }
                    }

                    if (mOssBean != null) {
                        LogUtils.e("开始进下载。。。。。。");
                        //下载
                        requestOssFile();
                    }
                } else {
                    // 服务器没有资源
                    switch (mType) {
                        case FILE_BOOK:
                            mDialog.dismiss();
                            mListener.onDownFileListenerCallBack(mListener.STATE_SERVER_NO_BOOK_SOURCE);
                            break;
                        case FILE_AUDIO:
                            mDialog.dismiss();
                            mListener.onDownFileListenerCallBack(mListener.STATE_SERVER_NO_AUDIO_SOURCE);
                            break;
                        case FILE_CONFIG:
                            mDialog.dismiss();
                            mListener.onDownFileListenerCallBack(mListener.STATE_SERVER_NO_CONFIG_SOURCE);
                            break;
                    }

                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                String msg = UIUtils.getContext().getResources().getString(R.string.down_book_error);
                if (throwable instanceof ApiException) {
                    String errorCode = ((ApiException) throwable).getCode();
                    LogUtils.i("resultCode" + errorCode);
                    if (errorCode.equals("404")) {
                        msg = "服务器没有上传文件";
                    } else if (errorCode.equals("602")) {
                        msg = "请求参数错误";
                    }
                }
                mDialog.setTitle(msg);
                mDialog.setBtnConfirmVisibility(View.VISIBLE);
            }
        });
    }


    private void cancelDownFile() {
        mDialog.dismiss();
        if (mOssBean != null) {
            DownOssManager.getInstance().cancel(mOssBean.getBookId(), mType);
        }
    }


    private void saveDrmKey(int bookId, String key) {
        // 缓存key
        String keys = DataCacheUtils.getBookString(UIUtils.getContext(), FileContonst.DOWN_LOAD_BOOKS_KEY);
        try {
            JSONObject object;
            if (!StringUtils.isEmpty(keys)) {
                object = new JSONObject(keys);
            } else {
                object = new JSONObject();
            }
            object.put(bookId + "", key);
            DataCacheUtils.putBookString(UIUtils.getContext(), FileContonst.DOWN_LOAD_BOOKS_KEY, object.toString());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i("缓存密码出差");
        }
    }


    private void requestOssFile() {
        DownOssManager.getInstance().downBookAsy(mOssBean, new DownOssistener() {
            @Override
            public void onSuccess(final int progress) {
//                LogUtils.e("下载....progress..." + progress);
                UIUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        String str = "";
                        switch (mType) {
                            case FILE_BOOK:
                                str = String.format(UIUtils.getContext().getString(R.string.down_book_loading), progress + "%");
                                break;
                            case FILE_AUDIO:
                                str = String.format(UIUtils.getContext().getString(R.string.down_audio_loading), progress + "%");
                                break;
                            case FILE_CONFIG:
                                str = String.format(UIUtils.getContext().getString(R.string.down_config_loading), progress + "%");
                                break;
                        }
                        mDialog.setTitle(str);
                    }
                });
            }

            @Override
            public void onFinish() {


                LogUtils.e("下载....onFinish");
                if (mType.equals(FILE_BOOK)) {
                    LogUtils.e("下载....FILE_BOOK");
                    requestDownFile(mBookId, mBookStatusCode, mBookAudio, mBookAudioConfig);
                } else {
                    //解压文件
                    LogUtils.e("....解压文件");
                    try {
                        mDialog.setTitle("正在解压文件，不要操作");
                        mDialog.setBtnCancelVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    LogUtils.e("....解压文件2");
                    ThreadManager.getShortPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            LogUtils.e("解压文件。。。" + mType);
                            String srcPath;
                            String destPath;
                            String key;
                            String value;
                            if (mType.equals(FILE_AUDIO)) {
                                srcPath = FileUtils.getMediaMp3Path() + mBookId + ".zip";
                                destPath = FileUtils.getMediaMp3Path() + mBookId;
                                key = mBookId + AUDIO_TAG;
                                value = mBookAudio;
                            } else {
                                srcPath = FileUtils.getMediaJsonPath() + mBookId + ".zip";
                                destPath = FileUtils.getMediaJsonPath() + mBookId;
                                key = mBookId + AUDIO_CONFIG_TAG;
                                value = mBookAudioConfig;
                            }
                            boolean result = FileUtils.unZip3(srcPath, destPath);
                            //删除原文件
                            FileUtils.deleteFile(srcPath);
                            LogUtils.e("解压结果。。。" + result);
                            UIUtils.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (result) {
                                        SpUtils.putMediaString(key, value);
                                        requestDownFile(mBookId, mBookStatusCode, mBookAudio, mBookAudioConfig);
                                    } else {
                                        //解压失败
                                        mListener.onDownFileListenerCallBack(mType.equals(FILE_AUDIO) ? mListener.STATE_ERROR_AUDIO_ZIP : mListener.STATE_ERROR_CONFIG_ZIP);
                                    }
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                LogUtils.e("下载....onFailure");
                UIUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        String str = "";
                        switch (mType) {
                            case FILE_BOOK:
                                str = UIUtils.getContext().getResources().getString(R.string.down_book_error);
                                break;
                            case FILE_AUDIO:
                                str = UIUtils.getContext().getResources().getString(R.string.down_audio_error);
                                break;
                            case FILE_CONFIG:
                                str = UIUtils.getContext().getResources().getString(R.string.down_config_error);
                                break;
                        }

                        mDialog.setTitle(str);
                        mDialog.setBtnConfirmVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }
}
