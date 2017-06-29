package com.yougy.home.activity;

import android.app.Activity;
import android.os.Bundle;

import com.onyx.android.sdk.ui.compat.AppCompatImageViewCollection;
import com.yougy.ui.activity.R;

/**
 * Created by Administrator on 2017/6/29.
 */

public class TestColoor extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testcolor);
//        initConfig();
    }

    private void initConfig() {
        // this make all imageView align to even position, just need to initialize once as it is static var
        // suggest init in your CustomApplication
        AppCompatImageViewCollection.setAlignView(true);
        //tip:
        // and make sure that all the res-drawable-image can't zoom in/out
        // the ImageView dp match px of image, ref layout/activity_main

        // the special image translated by tool on line : http://oa.o-in.me:9056/login
    }
}
