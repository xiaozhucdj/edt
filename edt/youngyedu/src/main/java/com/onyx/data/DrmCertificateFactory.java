package com.onyx.data;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.onyx.android.sdk.reader.api.ReaderDrmCertificateFactory;
import com.yougy.common.utils.StringUtils;

/**
 * Created by joy on 4/26/17.
 */

public class DrmCertificateFactory implements ReaderDrmCertificateFactory {
    private Context context;
    private String mKeyStart = "-----BEGIN PUBLIC KEY-----\n";
    private String mKeyEnd = "\n-----END PUBLIC KEY-----";
    private String mPublicKey;

    public DrmCertificateFactory(Context context) {
        this.context = context;
    }

    @Override
    public String getDeviceId() {
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo();
            String address = info.getMacAddress().toLowerCase();
            return address;
        } catch (Throwable tr) {
            return "";
        }
    }

    private String getDefaultCertificate() {
        if (!StringUtils.isEmpty(getPublicKey())) {
            return mKeyStart + getPublicKey() + mKeyEnd;
        }
        return null;
    }

    private String getPublicKey() {
        return mPublicKey;
    }

    public void setKey(String key) {
        this.mPublicKey = key;
    }


    public String getDrmCertificate() {
        return getDefaultCertificate();
    }
}
