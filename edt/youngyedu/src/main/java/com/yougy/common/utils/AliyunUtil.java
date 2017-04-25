package com.yougy.common.utils;

import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.yougy.common.manager.YougyApplicationManager;

import org.litepal.tablemanager.Connector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.litepal.LitePal.deleteDatabase;

/**
 * Created by jiangliang on 2017/4/14.
 */

public class AliyunUtil {
    private OSS oss;
    // 运行sample前需要配置以下字段为有效的值
    private static final String endpoint = "http://oss-cn-shanghai.aliyuncs.com";
    private static final String accessKeyId = "LTAI9VRGbs9MnZZi";
    private static final String accessKeySecret = "TGrEQznAlSWTMIDfS3JffCDgjd85ml";
    public static final String DATABASE_NAME = "leke.db";
    public static final String JOURNAL_NAME = "leke.db-journal";
    private static String filePath;

    private static final String bucketName = "espo";
    private static String objectKey;

    private AliyunUtil() {
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessKeyId, accessKeySecret);
        filePath = YougyApplicationManager.getContext().getDatabasePath(DATABASE_NAME).getAbsolutePath();
        objectKey = "leke" + File.separator + SpUtil.getAccountInfo().getId() + File.separator + DATABASE_NAME;
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();
        oss = new OSSClient(YougyApplicationManager.getContext(), endpoint, credentialProvider, conf);
    }

    private static volatile AliyunUtil instance;

    public static AliyunUtil getInstance() {
        if (instance == null) {
            synchronized (AliyunUtil.class) {
                if (instance == null) {
                    instance = new AliyunUtil();
                }
            }
        }
        return instance;
    }


    public void upload() {
        LogUtils.e("AliyunUtil", "upload ................");
        PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, filePath);
        oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                LogUtils.e("AliyunUtil", "onSuccess put object result................");
                SpUtil.changeContent(false);
                SpUtil.changeInitFlag(false);
                Connector.resetHelper();
                deleteDatabase(DATABASE_NAME);
                deleteDatabase(JOURNAL_NAME);
            }

            @Override
            public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                LogUtils.e("AliyunUtil", "onFailure ClientException : " + e.getMessage());
                LogUtils.e("AliyunUtil", "onFailure ServiceException : " + e.getMessage());
            }
        });
        SpUtil.clearAccount();
    }


    public void download() {
        LogUtils.e("AliyunUtil", "download...................");
        GetObjectRequest get = new GetObjectRequest(bucketName, objectKey);
        oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                InputStream inputStream = result.getObjectContent();
                boolean downloadDb = false;
                byte[] buffer = new byte[2048];
                int len;
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(filePath);
                    while ((len = inputStream.read(buffer)) != -1) {
                        // 处理下载的数据
                        Log.e("asyncGetObjectSample", "read length: " + len);
                        fos.write(buffer, 0, len);
                    }
                    Log.e("asyncGetObjectSample", "download success.");
                    downloadDb = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("asyncGetObjectSample", "Exception : " + e.getMessage());
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                SpUtil.changeInitFlag(downloadDb);
                if (!downloadDb) {
                    Connector.getDatabase();
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }


}
