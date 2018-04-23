package com.yougy.common.utils;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.yougy.common.bean.AliyunData;
import com.yougy.common.bean.Result;
import com.yougy.common.global.Commons;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.request.AliyunDataUploadReq;
import com.yougy.common.protocol.request.NewUnBindDeviceReq;

import org.litepal.LitePal;
import org.litepal.LitePalDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

/**
 * Created by jiangliang on 2017/4/14.
 */

public class AliyunUtil {
    private OSS oss;
    // 运行sample前需要配置以下字段为有效的值
    private static final String endpoint = Commons.ENDPOINT;
    public static final String ANSWER_PIC_HOST = Commons.ANSWER_PIC_HOST;
    public static String DATABASE_NAME;
    public static String JOURNAL_NAME;
    private static String filePath;
    private String tag = "AliyunUtil";
    private static final String bucketName = Commons.BUCKET_NAME;
    private static String objectKey;

    public AliyunUtil(AliyunData data) {
        DATABASE_NAME = SpUtils.getUserId() + ".db";
        JOURNAL_NAME = SpUtils.getUserId() + ".db-journal";
        LogUtils.e(tag, "accessKeyId : " + data.getAccessKeyId() + ",accessKeySecret : " + data.getAccessKeySecret() + ",securityToken : " + data.getSecurityToken());
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(data.getAccessKeyId(), data.getAccessKeySecret(), data.getSecurityToken());
        filePath = YougyApplicationManager.getContext().getDatabasePath(DATABASE_NAME).getAbsolutePath();
        objectKey = "leke" + File.separator + "appDB" + File.separator + DATABASE_NAME;

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();
        oss = new OSSClient(YougyApplicationManager.getContext(), endpoint, credentialProvider, conf);
    }

    public static void upload() throws Exception{
        Response response = NewProtocolManager.queryAliyunData(new AliyunDataUploadReq());
        if (response.isSuccessful()) {
            String resultJson = response.body().string();
            Result<AliyunData> result = ResultUtils.fromJsonObject(resultJson, AliyunData.class);
            AliyunData data = result.getData();
            AliyunUtil util = new AliyunUtil(data);
            util.method();
        }
//            1000010054
    }

    private void method() throws Exception {
        PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, filePath);
        PutObjectResult putObjectResult = oss.putObject(put);
        LogUtils.e(tag, "upload status code : " + putObjectResult.getStatusCode());
        if (putObjectResult.getStatusCode() == 200) {
            NewUnBindDeviceReq unBindDeviceReq = new NewUnBindDeviceReq();
            unBindDeviceReq.setDeviceId(Commons.UUID);
            unBindDeviceReq.setUserId(SpUtils.getUserId());
            Response response = NewProtocolManager.unbindDevice(unBindDeviceReq);
            if (response.isSuccessful()) {
                SpUtils.clearSP();
            }
        }

    }

    public void asyncUpload() {
        PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, filePath);
        LogUtils.e(tag, "upload ................" + "file path : " + filePath + ",object key : " + objectKey + ",bucket name : " + bucketName);
        oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                LogUtils.e(tag, "onSuccess put object result................");
                SpUtils.changeContent(false);
//                SpUtils.changeInitFlag(false);
//                Connector.resetHelper();
//                deleteDatabase(DATABASE_NAME);
//                deleteDatabase(JOURNAL_NAME);
            }

            @Override
            public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                LogUtils.e(tag, "onFailure ClientException : " + e.getMessage());
                LogUtils.e(tag, "onFailure ServiceException : " + e1.getMessage());
            }
        });
    }


    public void download() {
        GetObjectRequest get = new GetObjectRequest(bucketName, objectKey);
        LogUtils.e(tag, "download..................." + ",object key : " + objectKey + ",bucket name : " + bucketName);
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
                        LogUtils.e(tag, "read length: " + len);
                        fos.write(buffer, 0, len);
                    }
                    LogUtils.e(tag, "download success.");
                    downloadDb = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e(tag, "Exception : " + e.getMessage());
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                SpUtils.changeInitFlag(downloadDb);
                LitePalDB litePalDB = LitePalDB.fromDefault(Integer.toString(SpUtils.getUserId()));
                LitePal.use(litePalDB);
                LitePal.getDatabase();
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                LitePalDB litePalDB = LitePalDB.fromDefault(Integer.toString(SpUtils.getUserId()));
                LitePal.use(litePalDB);
                LitePal.getDatabase();
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                    LogUtils.e(tag, "client excepion : " + clientExcepion.getMessage());
                }
                if (serviceException != null) {
                    // 服务异常
                    LogUtils.e(tag, "ErrorCode : " + serviceException.getErrorCode());
                    LogUtils.e(tag, "RequestId : " + serviceException.getRequestId());
                    LogUtils.e(tag, "HostId : " + serviceException.getHostId());
                    LogUtils.e(tag, "RawMessage : " + serviceException.getRawMessage());
                }
            }
        });
    }


}
