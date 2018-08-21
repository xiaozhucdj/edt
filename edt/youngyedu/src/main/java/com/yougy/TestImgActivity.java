package com.yougy;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yougy.ui.activity.R;

/**
 * Created by jiangliang on 2018-2-28.
 */

public class TestImgActivity extends Activity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_img_layout);
    }
}
