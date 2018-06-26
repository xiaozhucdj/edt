package com.yougy.task;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.View;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.yougy.anwser.AnsweringActivity;
import com.yougy.anwser.STSbean;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.global.Commons;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.view.dialog.ConfirmDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lenovo on 2018/6/19.
 */

public class Utils {
    private static long lastClickTime = 0;

    public static String saveBitmapToFile (Bitmap bitmap, String fileDir) {
        FileUtils.createDirs(fileDir);
        File f = new File(fileDir, "test111.png");
        FileOutputStream fOut = null;
        try {
            f.createNewFile();
            fOut = new FileOutputStream(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return f.getAbsolutePath();
    }

    public static Bitmap saveCurrentScreenBitmap (View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap tBitmap = view.getDrawingCache();
        // 拷贝图片，否则在setDrawingCacheEnabled(false)以后该图片会被释放掉
        tBitmap = tBitmap.createBitmap(tBitmap);
        view.setDrawingCacheEnabled(false);
        return tBitmap;
    }

    /**
     * @param stSbean
     * @return   Al  OOS  实例
     */
    public static OSS  getAliOOS (STSbean stSbean) {
        OSSCredentialProvider credentialProvider = new OSSFederationCredentialProvider() {
            @Override
            public OSSFederationToken getFederationToken() {
                return new OSSFederationToken(stSbean.getAccessKeyId(), stSbean.getAccessKeySecret(), stSbean.getSecurityToken(), stSbean.getExpiration());
            }
        };
        //该配置类如果不设置，会有默认配置，具体可看该类
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();
        return new OSSClient(YoungyApplicationManager.getContext(), Commons.ENDPOINT, credentialProvider, conf);
    }

    /**
     * @param baseActivity
     * @param title
     * @param confirmText
     * @param cancelText
     * @param callBack
     * @return  创建确认对话框
     */
    public static ConfirmDialog createConfirmDialog (BaseActivity baseActivity, String title,
                                                   String confirmText, String cancelText, ConfirmDialogCallBack callBack) {
       return new ConfirmDialog(baseActivity, title,confirmText,
               (dialog, which) -> {
                   dialog.dismiss();
                   callBack.confirm();
               },cancelText,
               (dialog, which) -> {
                   dialog.dismiss();
                   callBack.cancel();
               });
    }

    public static boolean isFastClick () {
        if (System.currentTimeMillis() - lastClickTime < 500) {
            LogUtils.w(" isFastClick .");
            return true;
        }
        lastClickTime = System.currentTimeMillis();
        return false;
    }

}
