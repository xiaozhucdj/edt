package com.inkscreen.utils.network;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by xcz on 2016/11/24.
 */
public class RequestManager {

    private static RequestManager mInstance;
    private RequestQueue mRequestQueue;

    private final String TAG = "Volley";
    private Context mContext;

    public static synchronized RequestManager getInstance() {
        if(mInstance == null)
            mInstance = new RequestManager();
        return mInstance;
    }

    public void init(Context context){
        mContext = context;
    }


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        return mRequestQueue;
    }

//    public ImageLoader getImageLoader() {
//        getRequestQueue();
//        if (mImageLoader == null) {
//            mImageLoader = new ImageLoader(this.mRequestQueue,
//                    new LruBitmapCache());
//        }
//        return this.mImageLoader;
//    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        FakeX509TrustManager.allowAllSSL();
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req, Object tag) {
        // set the default tag if tag is empty
        req.setTag(tag == null ? TAG : tag);
        FakeX509TrustManager.allowAllSSL();
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        FakeX509TrustManager.allowAllSSL();
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req, Object tag, int retryTime) {
        // set the default tag if tag is empty
        req.setTag(tag == null ? TAG : tag);
        if (retryTime > 1){
            req.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, retryTime, 1.0f));
        }
        FakeX509TrustManager.allowAllSSL();
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
