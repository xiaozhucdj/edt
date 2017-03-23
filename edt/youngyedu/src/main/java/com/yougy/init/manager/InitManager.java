package com.yougy.init.manager;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.yougy.ui.activity.R;

/**
 * Created by jiangliang on 2016/10/14.
 */

public class InitManager {
    private static InitManager instance;
    private String mSchoolId;
    private String mClassId;
    private String mSchoolName;
    private String mClassName;
    private String mStudentName;
    private String mStudentNumber;
    private String mStudentId;
    private String mGradeId;
    private String mGradeName;

    public static final String TAG_SELECT_SCHOOL = "select_school";
    public static final String TAG_SELECT_CLASS = "select_class";
    public static final String TAG_SELECT_IDENTITY = "select_identity";
    public static final String TAG_START_USE = "start_use";

    private InitManager() {

    }

    public static InitManager getInstance() {
        if (instance == null) {
            instance = new InitManager();
        }
        return instance;
    }


    public void nextStep(Fragment currentFragment, Fragment nextFragment, String tag) {
        FragmentManager manager = currentFragment.getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.hide(currentFragment);
        transaction.add(R.id.container, nextFragment, tag);
//        if (manager.findFragmentByTag(tag) == null) {
//            transaction.add(R.id.container, nextFragment, tag);
//            LogUtils.e("InitManager","add fragment.....");
//        } else {
//            transaction.show(nextFragment);
//            LogUtils.e("InitManager","show fragment...");
//        }
        transaction.commit();
    }

    public void lastStep(Fragment currentFragment, Fragment lastFragment) {
        FragmentManager manager = currentFragment.getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.hide(currentFragment);
        transaction.remove(currentFragment);
        transaction.show(lastFragment);
        transaction.commit();
    }

    public void setGradeId(String gradeId) {
        mGradeId = gradeId;
    }

    public String getGradeId() {
        return mGradeId;
    }

    public void setGradeName(String gradeName) {
        mGradeName = gradeName;
    }

    public String getGradeName() {
        return mGradeName;
    }

    public void setSchoolId(String schoolId) {
        mSchoolId = schoolId;
    }

    public String getSchoolId() {
        return mSchoolId;
    }

    public void setSchoolName(String school) {
        mSchoolName = school;
    }

    public String getSchoolName() {
        return mSchoolName;
    }

    public void setClassId(String classId) {
        mClassId = classId;
    }

    public String getClassId() {
        return mClassId;
    }

    public void setClassName(String mClass) {
        this.mClassName = mClass;
    }

    public String getClassName() {
        return mClassName;
    }

    public void setStudentName(String studentName) {
        mStudentName = studentName;
    }

    public String getStudentName() {
        return mStudentName;
    }

    public void setStudentNumber(String studentNumber) {
        mStudentNumber = studentNumber;
    }

    public String getStudentNumber() {
        return mStudentNumber;
    }

    public String getStudentId() {
        return mStudentId;
    }

    public void setStudentId(String mSutdentId) {
        this.mStudentId = mSutdentId;
    }

    public String getClassInfo() {
        return mGradeName + mClassName;
    }

}
