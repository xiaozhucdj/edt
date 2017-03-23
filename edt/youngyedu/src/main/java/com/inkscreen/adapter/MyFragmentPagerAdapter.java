package com.inkscreen.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.inkscreen.fragment.BaseFragment;
import com.inkscreen.fragment.RecordFragment;
import com.inkscreen.fragment.WorkFragment;
import com.inkscreen.fragment.WrongFragment;

import java.util.ArrayList;


/**
 * Created by Carson_Ho on 16/7/22.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<BaseFragment> list = new ArrayList<BaseFragment>();

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        list = new ArrayList<BaseFragment>();
        list.add(new WorkFragment());
        list.add(new RecordFragment());
        list.add(new WrongFragment());
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);

        if (obj != null) {

            list.set(position, (BaseFragment) obj);
        }
        return obj;
    }

    @Override
    public Fragment getItem(int position) {

        if (list != null && position < list.size()) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }


    public void reFreshPage(int position) {
        if (list != null && position >= 0 && position < list.size()) {
            list.get(position).refreshFragment(null);
        }
    }
}
