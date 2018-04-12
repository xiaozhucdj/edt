package com.yougy.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.yougy.home.bean.PaintDrawStateInfo;
import com.yougy.init.bean.Student;
import com.yougy.ui.activity.R;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by jiangliang on 2016/6/30.
 */
public class SpUtils {
    private static final String LABEL_LOCATION = "label_location";
    private static final String LOCATION_X = "locationX";
    private static final String LOCATION_Y = "locationY";
    private static final String LABEL_CONTENT = "label_content";
    private static final String USER_FILE = "user";

    private static final String STUDENT_ID = "student_id";
    private static final String STUDENT_NAME = "student_name";
    private static final String STUDENT_SCHOOL = "student_school";
    private static final String STUDENT_SUBJECT = "student_subject";
    private static final String STUDENT_CODE = "student_code";

    private static final String SP_MSG_UNREAD_COUNT_FILE_NAME = "message_unread_count";
    private static final String SP_DIALOG_NOT_SHOW_AGAIN_TAG_FILE_NAME = "dialog_not_show_again_tag";

    private static final SharedPreferences unReadMsgSp = UIUtils.getContext()
            .getSharedPreferences(SP_MSG_UNREAD_COUNT_FILE_NAME , Context.MODE_PRIVATE);

    /**
     * 年级
     */
    private static final String GRADE_NAME = "gradeName";

    private static final String USER_GENDER = "user_gender";

    private static final String CLASS_NAME = "class_name";
    private static final String CLASS_ID = "class_Id";
    private static final String REAL_NAME = "real_name";
    private static String GRADE_DISPLAY = "gradeDisplay" ;

    private static final String LOCAL_LOCK_PWD = "LOCAL_LOCK_PWD";

    /**
     * 当前学期学科
     */
    private static final String SUBJECT_NAMES = "subjectNames";
    private static final String HISTORY_RECORD = "history_record";

    private static final String UUID = "UUID";

    private static final SharedPreferences sp = UIUtils.getContext().getSharedPreferences(USER_FILE,Context.MODE_PRIVATE);

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

    public static void saveStudent(Student student){
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(USER_FILE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(STUDENT_ID,student.getUserId());
        LogUtils.e("SpUtils","saveStudent user id : " + student.getUserId());
        editor.putString(STUDENT_NAME,student.getUserName());
        editor.putString(STUDENT_CODE,student.getUserNum());
        editor.putString(STUDENT_SCHOOL,student.getSchoolName());
        editor.putString(STUDENT_SUBJECT,student.getSubjectNames());
        editor.putString(GRADE_NAME , student.getGradeName());
        editor.putString(CLASS_NAME , student.getClassName());
        editor.putString(REAL_NAME , student.getUserRealName());
        editor.putString(GRADE_DISPLAY , student.getGradeDisplay());
        editor.putString(SUBJECT_NAMES , student.getSubjectNames());
        editor.putString(CLASS_ID , student.getClassId());
        editor.putString(USER_GENDER , student.getUserGender());
        editor.apply();
    }

    public static Student getStudent(){
        Student student = new Student();
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(USER_FILE,Context.MODE_PRIVATE);
        student.setUserId(sp.getInt(STUDENT_ID,-1));
        LogUtils.e("SpUtils","getStudent user id : " + sp.getInt(STUDENT_ID,-1));
        student.setUserName(sp.getString(STUDENT_NAME,""));
        student.setUserNum(sp.getString(STUDENT_CODE,"-1"));
        student.setSchoolName(sp.getString(STUDENT_SCHOOL,""));
        student.setSubjectNames(sp.getString(STUDENT_SUBJECT,""));
        student.setGradeDisplay(sp.getString(GRADE_DISPLAY ,""));
        student.setClassName(sp.getString(CLASS_NAME,""));
        student.setUserRealName(sp.getString(REAL_NAME , ""));
        student.setGradeName(sp.getString(GRADE_NAME,""));
        student.setSubjectNames(sp.getString(SUBJECT_NAMES , ""));
        student.setClassId(sp.getString(CLASS_ID , ""));
        student.setUserGender(sp.getString(USER_GENDER , ""));
        return student;
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
    public static void putHistoryRecord(List<String> values) {
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

    public static void clearHistoryRecord() {
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
        } else {
            for (String str : valus) {
                infos.add(str);
            }
        }
        return infos;
    }

    public static int getUserId() {
        return getStudent().getUserId();
    }

    public static String getAccountClass() {
        return getStudent().getClassName();
    }

    public static String getAccountName() {
        return getStudent().getUserRealName();
    }

    public static String getAccountNumber() {
        return getStudent().getUserNum();
    }

    public static int getAccountId() {
        return getStudent().getUserId();
    }


    public static void clearSP() {
        sp.edit().clear().apply();
        UIUtils.getContext().getSharedPreferences(LABEL_LOCATION , Context.MODE_PRIVATE).edit().clear().apply();
        UIUtils.getContext().getSharedPreferences(HISTORY_RECORD , Context.MODE_PRIVATE).edit().clear().apply();
        UIUtils.getContext().getSharedPreferences(CONTENT_CHANGED , Context.MODE_PRIVATE).edit().clear().apply();
        UIUtils.getContext().getSharedPreferences(INIT_DOWN , Context.MODE_PRIVATE).edit().clear().apply();
        UIUtils.getContext().getSharedPreferences(HISTORY_RECORD , Context.MODE_PRIVATE).edit().clear().apply();
        UIUtils.getContext().getSharedPreferences(UUID , Context.MODE_PRIVATE).edit().clear().apply();
        UIUtils.getContext().getSharedPreferences(SP_MSG_UNREAD_COUNT_FILE_NAME , Context.MODE_PRIVATE).edit().clear().apply();
    }

    /**
     * 获取年级
     *
     * @return
     */
    public static String getGradeName() {
        return getStudent().getGradeName();
    }

    /**
     * 获取当前学期学科
     *
     * @return
     */
    public static String getSubjectNames() {
        return getStudent().getSubjectNames();
    }

    private static final String CONTENT_CHANGED = "content_changed";
    private static final String FLAG = "flag";

    public static void changeContent(boolean flag) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(CONTENT_CHANGED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(FLAG, flag);
        editor.apply();
    }

    public static boolean isContentChanged() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(CONTENT_CHANGED, Context.MODE_PRIVATE);
        return sp.getBoolean(FLAG, false);
    }

    private static final String INIT_DOWN = "init_down";
    private static final String FIRST_FLAG = "first_flag";

    public static void changeInitFlag(boolean flag) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(INIT_DOWN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(FIRST_FLAG, flag);
        editor.apply();
    }

    public static boolean isInit() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(INIT_DOWN, Context.MODE_PRIVATE);
        return sp.getBoolean(FIRST_FLAG, false);
    }


    public static void saveUUID(String uuid) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(UUID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(UUID, uuid);
        editor.apply();
    }

    public static String getUUID() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences(UUID, Context.MODE_PRIVATE);
        return sp.getString(UUID, null);
    }

    public static String getLocalLockPwd(){
        return rot13(sp.getString(LOCAL_LOCK_PWD , ""));
    }
    public static void setLocalLockPwd(String pwd){
        pwd = rot13(pwd);
        sp.edit().putString(LOCAL_LOCK_PWD , pwd).apply();
    }

    public static int getUnreadMsgCount (String ssid){
        Log.v("FH" , "getUnreadMsgCount ssid=" + ssid);
        if (!TextUtils.isEmpty(ssid)){
            return unReadMsgSp.getInt(ssid , 0);
        }
        return 0;
    }

    public static void clearUnreadMsgCount (String ssid){
        Log.v("FH" , "clearUnreadMsgCount ssid=" + ssid);
        unReadMsgSp.edit().remove(ssid).apply();
    }

    public static void addUnreadMsgCount (String ssid){
        Log.v("FH" , "addUnreadMsgCount ssid=" + ssid);
        int current = getUnreadMsgCount(ssid);
        unReadMsgSp.edit().putInt(ssid , ++current).apply();
    }

    public static void clearAllUnreadMsgCount (){
        Log.v("FH" , "clearAllUnreadMsgCount");
        unReadMsgSp.edit().clear().apply();
    }

    public static boolean isThisDialogNotShowAgain(String tag){
        if (TextUtils.isEmpty(tag)){
            return false;
        }
        SharedPreferences sp = UIUtils.getContext()
                .getSharedPreferences(SP_DIALOG_NOT_SHOW_AGAIN_TAG_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(tag , false);
    }

    public static void setNotSHowAgainDialogTag(String tag , boolean notShow){
        if (!TextUtils.isEmpty(tag)){
            UIUtils.getContext().getSharedPreferences(SP_DIALOG_NOT_SHOW_AGAIN_TAG_FILE_NAME, Context.MODE_PRIVATE)
                    .edit().putBoolean(tag , notShow).apply();
        }
    }

    public static String rot13 (String str){
        if (TextUtils.isEmpty(str)){
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0 ; i < str.length() ; i++){
            char c = str.charAt(i);
            if(c >= 'a' && c <= 'z'){
                c = (char) ('a' + (c - 'a' + 13) % 26);
            }
            else if(c >= 'A' && c <= 'Z'){
                c = (char) ('A' + (c - 'A' + 13) % 26);
            }
            stringBuffer.append(c);
        }
        return stringBuffer.toString();
    }

    /**
     * 返回当前用户的account
     * TODO 本方法仅用于调试,正式版需要换成返回当前用户名的正式方法
     * @return
     */
    public static String justForTest(){
        return "test10";
    }

    public static String getSex() {
        return sp.getString(USER_GENDER, "");
    }
}
