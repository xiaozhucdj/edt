package com.yougy.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.yougy.home.bean.PaintDrawStateInfo;
import com.yougy.init.bean.AccountInfo;
import com.yougy.init.bean.UserInfo;
import com.yougy.ui.activity.R;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by jiangliang on 2016/6/30.
 */
public class SpUtil {
    private static final String LABEL_LOCATION = "label_location";
    private static final String LOCATION_X = "locationX";
    private static final String LOCATION_Y = "locationY";
    private static final String LABEL_CONTENT = "label_content";
    private static final String USER_FILE = "user";
    public static final String USER_ID = "user_id";
    private static final String USER_NAME = "user_name";
    private static final String USER_NUMBER = "user_number";
    private static final String USER_INFO = "user_info";

    private static final String INIT_FILE = "info_init";
    private static final String INIT_SCHOOL = "province";

    private static final String ACCOUNT_FILE = "acount";
    private static final String ACCOUNT_SCHOOL = "school";
    private static final String ACCOUNT_CLASS = "class";
    private static final String ACCOUNT_NAME = "name";
    private static final String ACCOUNT_NUMBER = "number";
    private static final String ACCOUNT_ID = "id";
    /**
     * 年级
     */
    private static final String GRADE_NAME = "gradeName";
    /**
     * 当前学期学科
     */
    private static final String SUBJECT_NAMES = "subjectNames";
    private static final String HISTORY_RECORD = "history_record";
    public static void saveLableLocation(int x, int y) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(LABEL_LOCATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(LOCATION_X, x);
        editor.putInt(LOCATION_Y, y);
        editor.apply();
    }

    public static int getLocationX() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(LABEL_LOCATION, Context.MODE_PRIVATE);
        return sp.getInt(LOCATION_X, -1);
    }

    public static int getLocationY() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(LABEL_LOCATION, Context.MODE_PRIVATE);
        return sp.getInt(LOCATION_Y, -1);
    }

    public static void saveLabelContent(String content) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(LABEL_LOCATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(LABEL_CONTENT, content);
        editor.apply();
    }

    public static void clearLabel() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(LABEL_LOCATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    public static String getLabelContent() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(LABEL_LOCATION, Context.MODE_PRIVATE);
        return sp.getString(LABEL_CONTENT, "");
    }

    /**
     * 保存
     */
    public static void putPaintDrawStates(PaintDrawStateInfo info) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(LABEL_LOCATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(info.getPAN_SIZE(), info.getPanSize());
        editor.putInt(info.getPAN_COLOR(), info.getPanColor());
        editor.putInt(info.getPAN_ALPH_PROGRESS(), info.getPanAlphProgress());
        editor.putInt(info.getPAN_SIZE_PROGRESS(), info.getPanSizeProgress());
        editor.apply();
    }

    /**
     * 刷新 查询 下XML 文件
     */
    public static void readPaintDrawStates(PaintDrawStateInfo info) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(LABEL_LOCATION, Context.MODE_PRIVATE);
        if (info != null) {
            info.setPanSize(sp.getFloat(info.getPAN_SIZE(), -1));
            System.out.println("sp.getInt(info.getPAN_COLOR() ==" + sp.getInt(info.getPAN_COLOR(), -1));
            info.setPanColor(sp.getInt(info.getPAN_COLOR(), -1));
            info.setPanAlphProgress(sp.getInt(info.getPAN_ALPH_PROGRESS(), -1));
            info.setPanSizeProgress(sp.getInt(info.getPAN_SIZE_PROGRESS(), -1));
        }
    }

    /***
     * 保存 String
     *
     * @param key
     * @param value
     */
    public static void putString(String key, String value) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(LABEL_LOCATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(String key) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(LABEL_LOCATION, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }


    /***
     * @param values
     */
    public static void putHistoryRecord( List<String> values) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(HISTORY_RECORD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        LinkedHashSet<String> infos = null;
        if (values != null && values.size() > 0) {
            infos = new LinkedHashSet<>();
            for (String str : values) {
                infos.add(str);
            }
        }
        editor.putStringSet(HISTORY_RECORD, infos);
        editor.apply();
    }

    public static void clearHistoryRecord(){
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(HISTORY_RECORD, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    public static List<String> getHistoryRecord() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(HISTORY_RECORD, Context.MODE_PRIVATE);
        Set<String> valus = sp.getStringSet(HISTORY_RECORD, null);
        List<String> infos = new ArrayList<>();
        if (valus == null || valus.size() < 0) {
            infos.add(UIUtils.getContext().getResources().getString(R.string.no_history_record));
        }else {
            for (String str : valus) {
                infos.add(str);
            }
        }
        return infos;
    }

    public static int getUserId() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(USER_FILE, Context.MODE_PRIVATE);
        return sp.getInt(USER_ID, 0);
    }

    public static void saveUserId(int userId) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(USER_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(USER_ID, userId);
        editor.apply();
    }

    public static void saveUser(UserInfo.User user) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(USER_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(USER_ID, user.getUserId());
        editor.putString(USER_NAME, user.getUserName());
        editor.putString(USER_NUMBER, user.getUserNumber());
        editor.apply();
    }

    public static UserInfo.User getUser() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(USER_FILE, Context.MODE_PRIVATE);
        String userId = sp.getString(USER_ID, "");
        String userName = sp.getString(USER_NAME, "");
        String userNumber = sp.getString(USER_NUMBER, "");
        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userNumber)) {
            UserInfo.User user = new UserInfo.User();
            user.setUserId(userId);
            user.setUserName(userName);
            user.setUserNumber(userNumber);
            return user;
        }
        return null;
    }

    public static void saveAccountSchool(String school) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(ACCOUNT_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(ACCOUNT_SCHOOL, school);
        editor.apply();
    }

    public static String getAccountSchool() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(ACCOUNT_FILE, Context.MODE_PRIVATE);
        return sp.getString(ACCOUNT_SCHOOL, "");
    }

    public static void saveAccountClass(String classStr) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(ACCOUNT_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(ACCOUNT_CLASS, classStr);
        editor.apply();
    }

    public static String getAccountClass() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(ACCOUNT_FILE, Context.MODE_PRIVATE);
        return sp.getString(ACCOUNT_CLASS, "");
    }

    public static void saveAccountName(String name) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(ACCOUNT_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(ACCOUNT_NAME, name);
        editor.apply();
    }

    public static String getAccountName() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(ACCOUNT_FILE, Context.MODE_PRIVATE);
        return sp.getString(ACCOUNT_NAME, "");
    }

    public static void saveAccountNumber(String number) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(ACCOUNT_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(ACCOUNT_NUMBER, number);
        editor.apply();
    }

    public static String getAccountNumber() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(ACCOUNT_FILE, Context.MODE_PRIVATE);
        return sp.getString(ACCOUNT_NUMBER, "");
    }

    public static void saveAccountId(String id) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(ACCOUNT_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(ACCOUNT_ID, id);
        editor.apply();
    }

    public static String getAccountId() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(ACCOUNT_FILE, Context.MODE_PRIVATE);
        return sp.getString(ACCOUNT_ID, "-1");
    }

    public static AccountInfo getAccountInfo() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(ACCOUNT_FILE, Context.MODE_PRIVATE);
        String schoolName = sp.getString(ACCOUNT_SCHOOL, "");
        String className = sp.getString(ACCOUNT_CLASS, "");
        String studentName = sp.getString(ACCOUNT_NAME, "");
        String studentNumber = sp.getString(ACCOUNT_NUMBER, "");
        String id = sp.getString(ACCOUNT_ID, "");
        AccountInfo info = null;
        if (!TextUtils.isEmpty(schoolName) && !TextUtils.isEmpty(className) && !TextUtils.isEmpty(studentName) && !TextUtils.isEmpty(studentNumber) && !TextUtils.isEmpty(id)) {
            info = new AccountInfo();
            info.setSchoolName(schoolName);
            info.setClassName(className);
            info.setStudentName(studentName);
            info.setStudentNumber(studentNumber);
            info.setId(id);
        }
        return info;
    }

    public static void clearAccount() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(ACCOUNT_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * 设置年级
     *
     * @param gradeName
     */
    public static void saveGradeName(String gradeName) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(ACCOUNT_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(GRADE_NAME, gradeName);
        editor.apply();
    }

    /**
     * 获取年级
     *
     * @return
     */
    public static String getGradeName() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(ACCOUNT_FILE, Context.MODE_PRIVATE);
        return sp.getString(GRADE_NAME, "");
    }

    /**
     * 设置当前学期学科
     *
     * @param gradeName
     */
    public static void saveSubjectNames(String gradeName) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(ACCOUNT_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SUBJECT_NAMES, gradeName);
        editor.apply();
    }

    /**
     * 获取当前学期学科
     *
     * @return
     */
    public static String getSubjectNames() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(ACCOUNT_FILE, Context.MODE_PRIVATE);
        return sp.getString(SUBJECT_NAMES, "");
    }

    private static final String AREA = "area";
    private static final String AREA_ID = "area_id";

    public static void saveSelectAreaID(String areaId){
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(AREA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(AREA_ID, areaId);
        editor.apply();
    }
    public static String getSelectAreaId(){
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(ACCOUNT_FILE, Context.MODE_PRIVATE);
        return sp.getString(SUBJECT_NAMES, "");
    }

    private static final String CONTENT_CHANGED = "content_changed";
    private static final String FLAG = "flag";
    public static void changeContent(boolean flag){
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(CONTENT_CHANGED,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(FLAG,flag);
        editor.apply();
    }

    public static boolean isContentChanged(){
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(CONTENT_CHANGED,Context.MODE_PRIVATE);
        return sp.getBoolean(FLAG,false);
    }

    private static final String INIT_DOWN = "init_down";
    private static final String FIRST_FLAG = "first_flag";

    public static void changeInitFlag(boolean flag){
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(INIT_DOWN,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(FIRST_FLAG,flag);
        editor.apply();
    }

    public static boolean isInit(){
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(INIT_DOWN,Context.MODE_PRIVATE);
        return sp.getBoolean(FIRST_FLAG,false);
    }
}
