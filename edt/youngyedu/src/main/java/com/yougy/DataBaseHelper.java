package com.yougy;

/**
 * Created by jiangliang on 2016/12/27.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    public DataBaseHelper(Context context, String name, CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }
    public DataBaseHelper(Context context, String name) {
        this(context, name, VERSION);
    }
    public DataBaseHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("create table user(id int, name varchar(20))");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        System.out.println("update a database");
    }


}