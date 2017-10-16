package com.yougy.home.fragment.showFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.common.fragment.BFragment;
import com.yougy.home.activity.ControlFragmentActivity;
import com.yougy.ui.activity.R;


/**
 * Created by jiangliang on 2016/7/14.
 */
public class ExerciseBookFragment extends BFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.activity_homewrok, container, false);
        return viewRoot;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    public int getExerciseId() {
        return mControlActivity.mHomewrokId;
    }

    //TODO:袁野
    public ControlFragmentActivity mControlActivity;

    public void setActivity(ControlFragmentActivity activity) {
        mControlActivity = activity;
    }
}
