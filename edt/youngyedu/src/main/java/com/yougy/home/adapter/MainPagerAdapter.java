package com.yougy.home.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.yougy.common.utils.LogUtils;
import com.yougy.home.fragment.mainFragment.CoachBookFragment;
import com.yougy.home.fragment.mainFragment.FolderFragment;
import com.yougy.home.fragment.mainFragment.HomeworkFragment;
import com.yougy.home.fragment.mainFragment.NotesFragment;
import com.yougy.home.fragment.mainFragment.ReferenceBooksFragment;
import com.yougy.home.fragment.mainFragment.TextBookFragment;

/**
 * Created by jiangliang on 2017/2/6.
 */

public class MainPagerAdapter extends FragmentPagerAdapter {

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        LogUtils.e(this.getClass().getName(), "getItem()--->" + position);
        switch (position) {
            case 0:
                return new TextBookFragment();
            case 1:
                return new CoachBookFragment();
            case 2:
                return new ReferenceBooksFragment();
            case 3:
                return new NotesFragment();
            case 4:
                return new HomeworkFragment();
            case 5:
                return new FolderFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 6;
    }
}