package com.inkscreen.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by xcz on 16-12-14.
 */
public class ImageRequest extends Request<String> {
    private final Response.Listener<JSONObject> mListener;
    private final Bitmap bitmap;
    private final String boundary;
    private final String endBoundary;
    private final String SEQ = "\r\n";
    private String fileField;
    private String fileName;
    private String fileType;
    private Map<String, String> params = new HashMap<String, String>();
    private ByteArrayOutputStream bos;
    public ImageRequest(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Bitmap bitmap) {
        this(url, listener, errorListener, bitmap, "headImg", "headImg", "image/jpeg");
    }

    public ImageRequest(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, Bitmap bitmap, String fileField, String fileName, String fileType) {
        super(Method.POST, url, errorListener);
        //Log.d(ZLApiUtils.TAG,"image upload url:"+url);
        mListener = listener;
        this.bitmap = bitmap;
        boundary = "-----------------" + UUID.randomUUID();
        endBoundary = SEQ + "--" + boundary + "--" + SEQ;
        this.fileField = fileField;
        this.fileName = fileName;
        this.fileType = fileType;
        buildMultipartEntity();
    }

    private void buildMultipartEntity() {
        bos = new ByteArrayOutputStream();
        try {
            String boundaryMessage = getBoundaryMessage();
            bos.write(boundaryMessage.getBytes());
            bos.write(bitmap2Bytes(bitmap, Bitmap.CompressFormat.JPEG));


            bos.write(endBoundary.getBytes());
            //Log.d(ZLApiUtils.TAG,"image upload body:"+new String(bos.toByteArray()));
        } catch (IOException e) {
            Log.e("ImageLoadding", e.getMessage());
        }

    }

    String getBoundaryMessage() {
        StringBuffer res = new StringBuffer("--").append(boundary).append(SEQ);
        Iterator<String> keys = params.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = params.get(key);
            res.append("Content-Disposition: form-data; name=\"").append(key).append("\"" + SEQ)
                    .append(SEQ).append(value).append(SEQ)
                    .append("--").append(boundary).append(SEQ);
        }
        res.append("Content-Disposition: form-data; name=\"").append(fileField).append("\"; filename=\"").append(fileName).append("\"" + SEQ)
                .append("Content-Type: ").append(fileType).append(SEQ + SEQ);
        return res.toString();
    }

    @Override
    public String getBodyContentType() {
        return LeApi.MULTIPART_CONTENT_TYPE + "; boundary=" + boundary;
    }


    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            if(bos!=null){
                return bos.toByteArray();
            }
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> header = LeApiUtils.getHeaders();
        if(bos!=null){
            header.put("Content-Length",bos.toByteArray().length+"");
        }
        return header;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }


    @Override
    protected void deliverResponse(String response) {
        if (mListener != null) {
            try {
                mListener.onResponse(new JSONObject(response));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    public String getFileField() {
        return fileField;
    }

    public void setFileField(String fileField) {
        this.fileField = fileField;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
    public static byte[] bitmap2Bytes(Bitmap bm,Bitmap.CompressFormat format) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(format, 100, baos);
        return baos.toByteArray();
    }

}