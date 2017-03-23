package com.yougy.home.tab_fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yougy.ui.activity.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jiangliang on 2017/2/6.
 */

public abstract class BaseTabFragment extends Fragment {


    @BindView(R.id.textview)
    TextView textview;

    protected abstract void initContent();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_layout, null);
        ButterKnife.bind(this, view);
        initContent();
        return view;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (getView() != null) {
            getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }
    }
}
