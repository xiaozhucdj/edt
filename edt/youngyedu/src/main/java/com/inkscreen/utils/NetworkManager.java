package com.inkscreen.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by xcz on 16/11/25
 */
public class NetworkManager {


    private static NetworkManager mInstance;
    public static synchronized NetworkManager getInstance() {
        if(mInstance == null)
            mInstance = new NetworkManager();
        return mInstance;
    }

    //    /************
//     * httpClient
//     */
    private HttpClient httpClient;
//    private static final int REQUEST_TIMEOUT = 3 * 1000;// 设置请求超时10秒钟
//    private static final int SO_TIMEOUT = 5 * 1000; // 设置等待数据超时时间10秒钟
//
//    private HttpClient createHttpClient() {
//
//        HttpParams params = new BasicHttpParams();
//        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//        HttpProtocolParams.setContentCharset(params,
//                HTTP.DEFAULT_CONTENT_CHARSET);
//        HttpProtocolParams.setUseExpectContinue(params, true);
//
//        params.setParameter(CoreProtocolPNames.USER_AGENT,
//                System.getProperty("http.agent"));
//        // 超时时间
//        HttpConnectionParams.setConnectionTimeout(params, REQUEST_TIMEOUT); // 连接
//        HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT); // 数据
//
////        if (proxy) {
////            HttpHost proxy = new HttpHost(proxy_host, proxy_port, "http");
////            params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
////        }
//
//        SchemeRegistry schReg = new SchemeRegistry();
//        schReg.register(new Scheme("http", PlainSocketFactory
//                .getSocketFactory(), 80));
//        schReg.register(new Scheme("https",
//                SSLSocketFactory.getSocketFactory(), 443));
//        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
//                params, schReg);
//
//        return new DefaultHttpClient(conMgr, params);
//    }

    public synchronized HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = getNewHttpClient();
        }
        return httpClient;
    }

    public void shutdownHttpClient() {
        try {
            if (httpClient != null && httpClient.getConnectionManager() != null) {
                httpClient.getConnectionManager().shutdown();
                httpClient = null;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    public String doGet(String url){
        String content = null;

        try {
            HttpClient httpClient = getHttpClient();
            HttpGet request = new HttpGet(url);
//            if(NetworkHelper.supportWap){
//                HttpHost mobileProxy = null;
//                if (AppData.isCmwap) {
//                    mobileProxy = new HttpHost("10.0.0.172", 80, "http");
//                } else if (AppData.isCtwap) {
//                    mobileProxy = new HttpHost("10.0.0.200", 80, "http");
//                }
//                httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, mobileProxy);
//            }

            request.addHeader("Cache-Control", "no-cache");
            request.addHeader("Cache-Control", "no-store");
            request.addHeader("Accept-Encoding", "gzip");

            HttpResponse response;
            response = httpClient.execute(request);

//				int code = response.getStatusLine().getStatusCode();
//				Log.v(AppApplication.tag, AppApplication.tag + ":code:" + code);
            if (response != null) {
                HttpEntity httpEntity = response.getEntity();
                if (httpEntity != null) {
                    if (httpEntity.getContentEncoding() != null &&
                            httpEntity.getContentEncoding().getValue() != null &&
                            httpEntity.getContentEncoding().getValue().contains("gzip")) {
                        InputStream is = null;
                        BufferedReader reader = null;
                        try{
                            is = httpEntity.getContent();
                            StringBuilder strb = new StringBuilder();
                            is = new GZIPInputStream(is);
                            reader = new BufferedReader(new InputStreamReader(is));
                            String line;
                            while((line = reader.readLine()) != null)
                            {
                                strb.append(line);
                            }
                            if(strb != null && strb.length() > 0){
                                content =  strb.toString();
                            }
                        }catch (Exception e) {
                            // TODO: handle exception
                        }finally{
                            try {
                                if (reader != null) {
                                    reader.close();
                                }
                            } catch (Exception e) {
                                // deliberately empty
                            }
                            try{
                                if(is != null){
                                    is.close();
                                }
                            }catch (Exception e) {
                                // TODO: handle exception
                            }
                        }
                    }else{
                        content = EntityUtils.toString(httpEntity, "utf-8");
                    }
                }
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }




    public static class SSLSocketFactoryEx extends SSLSocketFactory {

        SSLContext sslContext = SSLContext.getInstance("TLS");

        public SSLSocketFactoryEx(KeyStore truststore)
                throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain, String authType)
                        throws java.security.cert.CertificateException {

                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain, String authType)
                        throws java.security.cert.CertificateException {

                }
            };

            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port,
                                   boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port,
                    autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }


    //第二步：编写新的HttpClient  getNewHttpClient来代替原有DefaultHttpClient，代码如下：
    public static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public String doPost(String url, final String requestBody, boolean json){
        String content = null;

        try {
            HttpClient httpClient = getHttpClient();
            HttpPost httppost = new HttpPost(url);
            StringEntity entity = new StringEntity(requestBody, "utf-8");//new StringEntity(requestBody, "text/xml", "ISO-8859-1");

            httppost.setEntity(entity);

            if(json){
                httppost.setHeader("Accept", "application/json");
                httppost.setHeader("Content-type", "application/json;charset=UTF-8");
                httppost.setHeader("query-type", "text");
            }

            HttpResponse response = httpClient.execute(httppost);
            if(response != null && response.getEntity() != null){
                content = EntityUtils.toString(response.getEntity(), "UTF-8");
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    public String doPut(String url, final String requestBody, boolean json){
        String content = null;

        try {
            HttpClient httpClient = getHttpClient();
            HttpPost httpput = new HttpPost(url);
            StringEntity entity = new StringEntity(requestBody, "utf-8");//new StringEntity(requestBody, "text/xml", "ISO-8859-1");

            httpput.setEntity(entity);

            if(json){
                httpput.setHeader("Accept", "application/json");
                httpput.setHeader("Content-type", "application/json;charset=UTF-8");
            }

            HttpResponse response = httpClient.execute(httpput);
            if(response != null && response.getEntity() != null){
                content = EntityUtils.toString(response.getEntity(), "UTF-8");
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }



//    public static boolean proxy = false;
//    // public static String proxy_host = "15.105.253.226";
//    public static String proxy_host = "web-proxy.cce.hp.com";
//    public static int proxy_port = 8080;
//    private static final int REQUEST_TIMEOUT = 3 * 1000;// 设置请求超时10秒钟
//    private static final int SO_TIMEOUT = 5 * 1000; // 设置等待数据超时时间10秒钟

    public byte[] getInputStreamData(String url, boolean setTimeout) {
//        if (proxy) {
//            System.setProperty("http.proxyHost", proxy_host);
//            System.setProperty("http.proxyPort", "" + proxy_port);
//        }
//
//        if (AndroidUtils.isCmwap(this)) {
//            System.setProperty("http.proxyHost", "10.0.0.172");
//            System.setProperty("http.proxyPort", "80");
//        }else if(AndroidUtils.isCtwap(this)){
//            System.setProperty("http.proxyHost", "10.0.0.200");
//            System.setProperty("http.proxyPort", "80");
//        }

//		HttpHost mobileProxy = null;
//		if (AndroidUtils.isCmwap(this)) {
//			mobileProxy = new HttpHost("10.0.0.172", 80, "http");
//		} else if (AndroidUtils.isCtwap(this)) {
//			mobileProxy = new HttpHost("10.0.0.200", 80, "http");
//		}
//		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, mobileProxy);

        InputStream stream = null;
        URL imageUrl;
        byte[] bs = null;
        try {
            imageUrl = new URL(url);
            try {
//				stream = imageUrl.openStream();

                if(setTimeout){
                    URLConnection con = imageUrl.openConnection();
                    con.setConnectTimeout(3000);
                    con.setReadTimeout(3000);
                    stream = con.getInputStream();
                }else{
                    stream = imageUrl.openStream();
                }

                bs = inputStreamToByte(stream);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//        if (proxy) {
//            System.setProperty("http.proxyHost", "");
//            System.setProperty("http.proxyPort", "");
//        }
        return bs;
    }

    public byte[] inputStreamToByte(InputStream iStrm) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = iStrm.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }
}
