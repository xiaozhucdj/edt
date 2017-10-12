package com.yougy.common.down;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;

/**
 * Created by Administrator on 2017/8/16.
 */

public interface DownloadBookListener {

    void onSuccess(int progress);
    void onFinish();
    void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException);
}
