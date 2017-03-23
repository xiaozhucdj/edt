package com.onyx.android.sdk.data.util;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.data.sys.OnyxSysCenter;
import com.onyx.android.sdk.device.IMX6Factory;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by yxhuang on 16-7-29.
 */
public class IgnoreTextEnhanceUtil {

    private static final String KEY_TEXT_ENHANCE = "sys.text_enhance";
    public static final String USER_IGNORE_TEXT_ENHANCE_APPS = "usr_ignore_text_enhance_apps";
    public static final String CONFIG_IGNORE_TEXT_ENHANCE_APPS = "config_ignore_text_enhance_apps";


    private static Map<String, Map<String, AppConfig>> allIgnoreMap; // key is packageName

    /**
     *  Query an application is in record.
     * @param context
     * @param packageName
     * @return
     */
    public static boolean queryIgnoreApp(Context context, String packageName){
        if (TextUtils.isEmpty(packageName)){
            return false;
        }

        if (allIgnoreMap == null) {
            init(context);
        }

        if (allIgnoreMap.isEmpty()){
            return false;
        }

        if (queryIgnoreApp(packageName, USER_IGNORE_TEXT_ENHANCE_APPS)) {
            return true;
        }

        if (queryIgnoreApp(packageName, CONFIG_IGNORE_TEXT_ENHANCE_APPS)){
            return true;
        }

        return false;
    }

    private static boolean queryIgnoreApp(String packageName, String ignoreType){
        Map<String, AppConfig> innerMap = allIgnoreMap.get(ignoreType);
        if (CollectionUtils.isNullOrEmpty(innerMap)){
            return false;
        }
        if (innerMap.containsKey(packageName)){
            return true;
        }
        return false;
    }

    /**
     *  Add an application into ignore text enhance record.
     * @param context
     * @param packageName
     */
    public static void addIgnoreApp(Context context, String packageName){
        addIgnoreApp(context,packageName, null);
    }

    /**
     * Add an application into ignore text enhance record.
     * @param context
     * @param packageName
     * @param appConfig
     */
    public static void addIgnoreApp(Context context, String packageName, AppConfig appConfig){
        if (TextUtils.isEmpty(packageName)){
            return;
        }

        if (allIgnoreMap == null) {
            init(context);
        }

        if (appConfig == null){
            appConfig = new AppConfig();
            Map<String, Config> configMap = new HashMap<>();
            configMap.put(packageName, new Config());
            appConfig.setActivitiesMap(configMap);
        }

        Map<String, AppConfig> appConfigMap = allIgnoreMap.get(USER_IGNORE_TEXT_ENHANCE_APPS);
        if (!CollectionUtils.isNullOrEmpty(allIgnoreMap)){
            Set<String> packageNameSet = appConfigMap.keySet();
            if (packageNameSet.contains(packageName)){
                return;
            }
        } else {
            appConfigMap = new HashMap<>();
        }
        appConfigMap.put(packageName, appConfig);
        allIgnoreMap.put(USER_IGNORE_TEXT_ENHANCE_APPS, appConfigMap);

        String record = JSON.toJSONString(allIgnoreMap);
        saveToRecord(context, record);
    }

    /**
     * Delete an application from ignore text enhance record.
     * @param context
     * @param packageName
     */
    public static void deleteIgnoreApp(Context context, String packageName){
        if (!queryIgnoreApp(context, packageName)){
            return;
        }

        Map<String, AppConfig> map = allIgnoreMap.get(USER_IGNORE_TEXT_ENHANCE_APPS);
        if (CollectionUtils.isNullOrEmpty(map)){
            return;
        }

        map.remove(packageName);
        allIgnoreMap.put(USER_IGNORE_TEXT_ENHANCE_APPS, map);

        String record = JSON.toJSONString(allIgnoreMap);
        saveToRecord(context, record);
    }


    /**
     *  Get all ignore text enhance applications.
     * @param context
     * @return
     */
    public static Map<String, Map<String, AppConfig>> getAllIgnoreMap(Context context){
        if (allIgnoreMap != null){
            return allIgnoreMap;
        }

        return null;
    }


    /**
     * Get all ignore text enhance application's name set.
     * @param context
     * @return
     */
    public static Set<String> getAllIgnoreAppsPackageNameSet(Context context){
        if (allIgnoreMap == null){
            init(context);
        }

        Set<String> set = new HashSet<>();
        Map<String, AppConfig> userIgnoreMap = allIgnoreMap.get(USER_IGNORE_TEXT_ENHANCE_APPS);
        if (!CollectionUtils.isNullOrEmpty(userIgnoreMap)){
            set.addAll(userIgnoreMap.keySet());
        }

        HashMap<String, AppConfig> configHashMap = getConfigIgnoreAppsMap(context);
        if (!CollectionUtils.isNullOrEmpty(configHashMap)){
            Set<String> keySet = configHashMap.keySet();
            set.addAll(keySet);
        }

        return set;
    }

    public static HashMap<String, AppConfig> getConfigIgnoreAppsMap(Context context){
        IMX6Factory.IMX6Controller controller = IMX6Factory.IMX6Controller.createController();
        return controller.getConfigIgnoreApps(context);
    }

    private static void init(Context context){
        String record = OnyxSysCenter.getStringValue(context, KEY_TEXT_ENHANCE);
        allIgnoreMap = new HashMap<>();

        if (!TextUtils.isEmpty(record)){
            Map<String, String> outMap = JSON.parseObject(record, new TypeReference<Map<String, String>>(){});
            Set outSet = outMap.entrySet();
            Iterator outIterator = outSet.iterator();
            while (outIterator.hasNext()){
                Map.Entry outEntry = (Map.Entry) outIterator.next();
                String ignoreType = (String) outEntry.getKey();
                String innerData = (String) outEntry.getValue();

                Map<String, AppConfig> ignoreInnerMap = new HashMap<>();
                Map<String, String> innerMap = JSON.parseObject(innerData, new TypeReference<Map<String, String>>(){});
                Set innerSet = innerMap.entrySet();
                Iterator innerIterator = innerSet.iterator();
                while (innerIterator.hasNext()){
                    Map.Entry innerEntry = (Map.Entry) innerIterator.next();
                    String packageName = (String) innerEntry.getKey();
                    String appConfigString = (String) innerEntry.getValue();
                    AppConfig appConfig = JSON.parseObject(appConfigString, AppConfig.class);
                    ignoreInnerMap.put(packageName, appConfig);
                }

                allIgnoreMap.put(ignoreType, ignoreInnerMap);
            }
        } else {
            HashMap<String, AppConfig> configHashMap = getConfigIgnoreAppsMap(context);
            if (!CollectionUtils.isNullOrEmpty(configHashMap)){
                allIgnoreMap.put(CONFIG_IGNORE_TEXT_ENHANCE_APPS, configHashMap);
            }
        }
    }

    private static void saveToRecord(Context context, String record){
        OnyxSysCenter.setStringValue(context, KEY_TEXT_ENHANCE, record);
    }

}
