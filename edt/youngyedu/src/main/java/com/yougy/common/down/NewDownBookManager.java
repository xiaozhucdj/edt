package com.yougy.common.down;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/8/16.
 */

public class NewDownBookManager {

    private OSSCredentialProvider mMredetialProvider;
    private ClientConfiguration mConf;
    private OSSClient mOss;

    private static NewDownBookManager instance;
    private OSSAsyncTask<GetObjectResult> mTask;
    private NewDownBookInfo mInfo;

    private NewDownBookManager() {

    }

    public synchronized static NewDownBookManager getInstance() {
        if (instance == null) {
            instance = new NewDownBookManager();
        }
        return instance;
    }

    private void initCredetialProvider(String ak, String sk, String token, String expiration) {
        mMredetialProvider = new OSSFederationCredentialProvider() {
            @Override
            public OSSFederationToken getFederationToken() {
//                    String ak = jsonObjs.getString("accessKeyId");
//                    String sk = jsonObjs.getString("accessKeySecret");
//                    String token = jsonObjs.getString("securityToken");
//                    String expiration = jsonObjs.getString("expiration");
                return new OSSFederationToken(ak, sk, token, expiration);
            }
        };
    }

    private void initClientConfiguration() {
        mConf = new ClientConfiguration();
        mConf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        mConf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        mConf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        mConf.setMaxErrorRetry(2);
        OSSLog.enableLog();
    }

    private void initOSSClient(String endpoint) {
        mOss = new OSSClient(YoungyApplicationManager.getContext(), endpoint, mMredetialProvider, mConf);
    }

    private void init(NewDownBookInfo info) {

        if (mConf == null) {
            initClientConfiguration();
        }
        initCredetialProvider(info.getAccessKeyId(), info.getAccessKeySecret(), info.getSecurityToken(), info.getExpiration());
        initOSSClient(info.getEndpoint());
    }

    private DownloadBookListener mListener;

    public void downBookAsy(NewDownBookInfo info, DownloadBookListener listener) {
        mInfo = info ;
        mListener = listener;
        init(info);
        GetObjectRequest get = new GetObjectRequest(info.getBucketName(), info.getObjectKey());

        mTask = mOss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                // 请求成功
                InputStream inputStream = result.getObjectContent();
                byte[] buffer = new byte[2048];

                FileOutputStream fos = null;
                long length = result.getContentLength();
                if (length == 0){
                    FileUtils.deleteFile(info.getSaveFilePath()) ;
                    mListener.onFailure(request, null, null);
                    return;
                }
                double rate = (double) 100 / length;  //最大进度转化为100
                int total = 0;
                int len;
                try {
//                    System.out.println("yuanye ... save path "+info.getSaveFilePath());

                    fos = new FileOutputStream(info.getSaveFilePath());
                    while ((len = inputStream.read(buffer)) != -1) {
                        // 处理下载的数据
                       LogUtils.e("asyncGetObjectSample", "read length: " + len);
                        fos.write(buffer, 0, len);
                        total += len;

                        listener.onSuccess((int) (total * rate));
                    }
                    listener.onFinish();
                } catch (IOException e) {
                    FileUtils.deleteFile(info.getSaveFilePath()) ;
                    mListener.onFailure(request, null, null);
                    e.printStackTrace();
                } finally {
//                    System.out.println(".........finally");
                    if (fos != null) {
                        try {
                            inputStream.close();
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                   LogUtils.e("ErrorCode", serviceException.getErrorCode());
                   LogUtils.e("RequestId", serviceException.getRequestId());
                   LogUtils.e("HostId", serviceException.getHostId());
                   LogUtils.e("RawMessage", serviceException.getRawMessage());
                }

                FileUtils.deleteFile(info.getSaveFilePath()) ;
                mListener.onFailure(request, clientExcepion, serviceException);
            }
        });
    }

    public void cancel(int bookid) {
        if (mTask != null) {
            if (mInfo!=null && !StringUtils.isEmpty(mInfo.getSaveFilePath()) && bookid == mInfo.getBookId()){
                FileUtils.deleteFile(mInfo.getSaveFilePath()) ;
            }
            mTask.cancel();
        }
    }
}
