package com.yougy.init.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.yougy.common.activity.BaseActivity;
import com.yougy.init.fragment.SelectSchoolFragment;
import com.yougy.init.manager.InitManager;
import com.yougy.ui.activity.R;

/**
 * Created by jiangliang on 2016/10/13.
 */

public class InitInfoActivity extends BaseActivity {
    @Override
    protected void init() {

    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.init_layout);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.container,new SelectSchoolFragment(), InitManager.TAG_SELECT_SCHOOL);
        transaction.commit();
        setPressTwiceToExit(true);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void refreshView() {

    }

}
