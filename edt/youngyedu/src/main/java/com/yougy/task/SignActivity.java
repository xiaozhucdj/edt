package com.yougy.task;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;

import com.yougy.common.utils.UIUtils;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivitySignBinding;

import butterknife.OnClick;

/**
 * @author: zhang yc
 * @create date: 2018/6/21 17:52
 * @class desc:  家长签字界面
 * @modifier: 
 * @modify date: 2018/6/21 17:52
 * @modify desc: 
 */
public class SignActivity extends TaskBaseActivity {

    private ActivitySignBinding binding;

    @Override
    protected void yxMsgObserverCall(Object o) {

    }

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_sign, null, false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    @Override
    protected void initLayout() {

    }

    @Override
    protected void refreshView() {

    }

    @OnClick({R.id.sign_close, R.id.sign_commit})
    public void onClick (View view) {
        switch (view.getId()) {
            case R.id.sign_close:
                finish();
                break;
            case R.id.sign_commit:
                //提交
                break;
        }
    }
}
