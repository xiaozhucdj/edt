package com.inkscreen.utils;

import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.inkscreen.LeController;
import com.inkscreen.utils.network.RequestManager;


import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by xcz on 2016/11/24.
 */
public class LeApiUtils {


    private static RequestManager mInstance = RequestManager.getInstance();
    public static final String TAG = "LeApiUtils";
    /**
     * append base parameter
     *
     * @param hostName
     * @param uriBuilder
     * @return final api url
     */
    public static String finalUrl(String hostName, Uri.Builder uriBuilder) {
        if (uriBuilder.toString().startsWith("http")) {
            Log.i("xcz",">>>>"+uriBuilder.toString());
            return uriBuilder.toString();
        }
        return (StringUtils.isEmptyStr(hostName) ? LeApi.HOST_NAME : hostName) + uriBuilder.toString();
    }


    public static String getParamsJsonString(Map<String, String> params) {
//        JSONObject jsonObject = new JSONObject();
//        if (params != null && !params.isEmpty()) {
//            try {
//                for (Map.Entry<String, String> entry : params.entrySet()) {
//                    jsonObject.put(entry.getKey(), entry.getValue());
//                }
//                return jsonObject.toString();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }


        if (params != null && params.size() > 0) {
            // String paramsEncoding = ConfigNetwork.ENCODE;
            // StringBuilder encodedParams = new StringBuilder();
            // try {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            NameValuePair pair;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                list.add(pair);
                // encodedParams.append(entry.getKey());
                // encodedParams.append('=');
                // encodedParams.append(entry.getValue());
                // encodedParams.append('&');

            }
            return URLEncodedUtils.format(list, HTTP.UTF_8);
        }

        return null;
    }


    public static void postString(String url, Map<String, String> map, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Object tag) {
        actionSting(Request.Method.POST, url, getParamsJsonString(map), listener, errorListener, tag);
    }

    public static void actionSting(int method, String url, String postStr, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Object tag) {
        if ( !AndroidUtils.isNetworkAvailable(LeController.appContext)){
            if(errorListener != null){
                errorListener.onErrorResponse(new NetworkError());
            }
            return;
        }
        JsonObjectRequest request = getJsonStringRequest(method, url, postStr, listener, errorListener);
        mInstance.addToRequestQueue(request, tag);
    }

    public static void post(String url, Map<String, String> params, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        action(Request.Method.POST, url, params, listener, errorListener);
    }

    public static void action(int method, String url, Map<String, String> params, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        if (!AndroidUtils.isNetworkAvailable(LeController.appContext)){
            if(errorListener != null){
                errorListener.onErrorResponse(new NetworkError());
            }
            return;
        }
        JsonObjectRequest request = getJsonRequest(method, url, params, listener, errorListener);
        mInstance.addToRequestQueue(request);
    }


    private static JsonObjectRequest getJsonRequest(final int method, final String url, final Map<String, String> params, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {

        JsonObjectRequest request = new JsonObjectRequest(method,url, method != Request.Method.GET ? new JSONObject(Collections.emptyMap()) : null,listener, errorListener


        ) {
            @Override
            public String getBodyContentType() {
                if ((method == Method.PUT || method == Method.POST) && params != null && params.size() > 0) {

                    return LeApi.FORM_CONTENT_TYPE;
                }
                return super.getBodyContentType();
            }

            @Override
            public byte[] getBody() {
               // if (params != null && params.size() > 0 && (method == Method.PUT || method == Method.POST)) {
              //  Log.i("xcz","AAAAAAAAAAAAAAAAAAAAAAAAAA"+encodeParameters(params, getParamsEncoding()).toString());
                    //return encodeParameters(params, getParamsEncoding());

                try {
                    return paramstoString(params).getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //  }
                return null;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return LeApiUtils.getHeaders();
            }
        };

            Log.d(TAG, "call url:" + url);
            Log.d(TAG, "call method:" + method);
            showParams(params);

        return request;
    }

    private static byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
               // encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
               // encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }





    private static String paramstoString(Map<String, String> params) {
        if (params != null && params.size() > 0) {
            // String paramsEncoding = ConfigNetwork.ENCODE;
            // StringBuilder encodedParams = new StringBuilder();
            // try {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            NameValuePair pair;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                list.add(pair);
                // encodedParams.append(entry.getKey());
                // encodedParams.append('=');
                // encodedParams.append(entry.getValue());
                // encodedParams.append('&');

            }
            return URLEncodedUtils.format(list, HTTP.UTF_8);
        }
        return null;
    }

    private static JsonObjectRequest getJsonStringRequest(final int method, final String url, final String postStr, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {

        JsonObjectRequest request = new JsonObjectRequest(method,
                url, method != Request.Method.GET ? new JSONObject(Collections.emptyMap()) : null,
                listener, errorListener
        ) {
            @Override
            public String getBodyContentType() {
                return LeApi.FORM_CONTENT_TYPE;

            }

            @Override
            public byte[] getBody() {
                //if (postStr != null && (method == Method.PUT || method == Method.POST)) {

                try {
                    if(postStr!=null) {
                        Log.i("xcz", "wwwwwwwwwwwwwwwwwwwwwww" + postStr.getBytes("utf-8"));
                        return postStr.getBytes("utf-8");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

//                try {
//                    return postStr.getBytes("utf-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }

                //  }
                return null;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
              return LeApiUtils.getHeaders();
            }
        };

            Log.d(TAG, "call url:" + url);
            Log.d(TAG, "call method:" + method);
            Log.d(TAG, "call postStr:" + postStr);



       // LeController.addApiUsed(url);
        return request;
    }

    public static void dequeue(Object tag) {
        mInstance.cancelPendingRequests(tag);
    }


    public static Map<String, String> getHeaders() {
        Map<String, String> map = new HashMap<String, String>();
        if (AndroidUtils.getInstance().getPrefs("token", null) != null) {
            map.put("S_T", AndroidUtils.getInstance().getPrefs("token", null));// 回话token
        }
        map.put("SUBJECT", "math");
        map.put("DEVICE_TYPE", "ANDROID");
        map.put("APP", "MATH_STUDENT");
        map.put("VERSION", "1.0.1");
        return map;
    }

    private static void showParams(Map<String, String> params) {
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Log.d(TAG, "call key=" + key + " value=" + params.get(key));
            }
        }
    }

}
