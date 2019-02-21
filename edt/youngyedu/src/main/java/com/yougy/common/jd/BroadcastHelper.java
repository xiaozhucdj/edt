package com.yougy.common.jd;

import android.content.Context;
import android.content.Intent;

import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;

public class BroadcastHelper {


    private final static String YOUNGY_ACCOUNT_BIND_ACTION = "com.youngy.ACTION_ACCOUNT_BIND";
    public final static String YOUNGY_ACCOUNT_UNBIND_ACTION = "com.youngy.ACTION_ACCOUNT_UNBIND";




    public static void bindJdReader(Context context ,JdReaderBindBean bean) {
        LogUtils.e("bindJdReader .."+bean.toString());
        Intent intent = new Intent();
        intent.setAction(YOUNGY_ACCOUNT_BIND_ACTION);
        intent.putExtra("data" ,GsonUtil.toJson(bean)) ;
        context. sendBroadcast(intent);
    }


    public static  void unBindJdReader(Context context) {
        Intent intent = new Intent();
        intent.setAction(YOUNGY_ACCOUNT_UNBIND_ACTION);
        context.sendBroadcast(intent);
    }
}
