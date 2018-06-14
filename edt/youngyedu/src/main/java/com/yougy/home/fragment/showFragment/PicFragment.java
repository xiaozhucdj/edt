package com.yougy.home.fragment.showFragment;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yougy.common.fragment.BFragment;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.rx.RxBus;
import com.yougy.common.utils.ImageLoader;
import com.yougy.home.bean.Photograph;
import com.yougy.ui.activity.PicBinding;
import com.yougy.ui.activity.R;

/**
 * Created by jiangliang on 2017/5/12.
 */

public class PicFragment extends BFragment {
    private Photograph photo;
    private PicBinding binding;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.pic_fragment,container,false);
        binding.setBinding(this);
        return binding.getRoot();
    }

    public void setPhotoGraph(Photograph photo){
        this.photo = photo;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bitmap bitmap = ImageLoader.loadBmpFromBytes(photo.getBytes());
        binding.picIg.setImageBitmap(bitmap);
    }

    public void shrink(View view){
        removeFragment();
    }

    private void removeFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(this);
        ft.commit();
    }

    public void delete(View view){
        removeFragment();
        RxBus rxBus = YoungyApplicationManager.getRxBus(getContext());
        rxBus.send(photo);
    }


}
