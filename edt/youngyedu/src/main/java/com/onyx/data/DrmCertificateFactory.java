package com.onyx.data;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.onyx.android.sdk.reader.api.ReaderDrmCertificateFactory;
import com.onyx.android.sdk.utils.FileUtils;
import com.yougy.common.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by joy on 4/26/17.
 */

public class DrmCertificateFactory implements ReaderDrmCertificateFactory {
    private  Context context;
    private String mKeyStart = "-----BEGIN PUBLIC KEY-----\n";
    private String mKeyEnd = "\n-----END PUBLIC KEY-----";
    private String mPublicKey ;

    public DrmCertificateFactory(Context context) {
        this.context = context;
    }

    @Override
    public String getDeviceId() {
        try {
            WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo();
            String address = info.getMacAddress().toLowerCase();
            return address;
        } catch (Throwable tr) {
            return "";
        }
    }

    public String getDrmCertificate() {
        if (!StringUtils.isEmpty(getPublicKey())){
           return mKeyStart+getPublicKey()+mKeyEnd;
        }
      return  null ;
    }

    private String getPublicKey(){
        return mPublicKey ;
    }

    public void setKey(String key){
        this.mPublicKey = key ;
    }



    private String readContentOfFile(File fileForRead) {
        FileInputStream in = null;
        InputStreamReader reader = null;
        BufferedReader breader = null;
        try {
            in = new FileInputStream(fileForRead);
            reader = new InputStreamReader(in, "utf-8");
            breader = new BufferedReader(reader);

            String ls = System.getProperty("line.separator");

            StringBuilder sb = new StringBuilder();
            boolean firstLine = true;
            String line;
            while ((line = breader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                } else {
                    sb.append(ls);
                }
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtils.closeQuietly(breader);
            FileUtils.closeQuietly(reader);
            FileUtils.closeQuietly(in);
        }
        return null;
    }
}
