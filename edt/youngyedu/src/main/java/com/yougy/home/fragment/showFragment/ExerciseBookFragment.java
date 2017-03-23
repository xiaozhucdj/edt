package com.yougy.home.fragment.showFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inkscreen.MainActivityScreen;
import com.yougy.common.fragment.BFragment;
import com.yougy.ui.activity.R;


/**
 * Created by jiangliang on 2016/7/14.
 */
public class ExerciseBookFragment extends BFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.activity_homewrok, container, false);

        viewRoot.findViewById(R.id.btn_back) .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return viewRoot ;
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getActivity(), MainActivityScreen.class);
        startActivity(intent);
    }
}
