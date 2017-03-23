package com.onyx.android.sdk.data.util;


import java.util.Map;

/**
 *  Inner class
 */
public class AppConfig {
    private Map<String, Config> activitiesMap;
    private int dpi;

    public AppConfig(){

    }

    public Map<String, Config> getActivitiesMap() {
        return activitiesMap;
    }

    public void setActivitiesMap(Map<String, Config> activitiesMap) {
        this.activitiesMap = activitiesMap;
    }

    public int getDpi() {
        return dpi;
    }

    public void setDpi(int dpi) {
        this.dpi = dpi;
    }
}