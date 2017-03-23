package com.yougy.home.fragment;

import com.yougy.home.Observable.Observer;


/**
 * Created by Administrator on 2016/11/16.
 *  全部  ，本学期笔记的 引用
 */

public class GlobeFragment {

    private static GlobeFragment mInstance ;
    private GlobeFragment(){

    }
    public static synchronized GlobeFragment  getInstance(){
        if (mInstance == null) {
            mInstance = new GlobeFragment();
        }
        return mInstance;
    }
    public  Observer mAllNotes;
    public  Observer mNote ;
    public  Observer mTextBook;
    public  Observer mAllBook ;

}
