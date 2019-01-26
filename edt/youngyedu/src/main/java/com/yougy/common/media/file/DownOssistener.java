package com.yougy.common.media.file;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;

public interface DownOssistener {
    void onSuccess(int progress);
    void onFinish();
    void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException);
}
