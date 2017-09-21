package com.yougy.init.activity;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.activity.MainActivity;
import com.yougy.init.bean.Student;
import com.yougy.message.GlideCircleTransform;
import com.yougy.ui.activity.R;
import com.yougy.ui.activity.databinding.ActivityLocalLockBinding;
import com.yougy.view.dialog.ConfirmDialog;
import com.yougy.view.dialog.HintDialog;

/**
 * Created by FH on 2017/6/22.
 */

public class LocalLockActivity extends BaseActivity {
    static public final String NOT_GOTO_HOMEPAGE_ON_ENTER = "not_goto_homepage";
    ActivityLocalLockBinding binding;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(this) , R.layout.activity_local_lock , null , false);
        UIUtils.recursiveAuto(binding.getRoot());
        setContentView(binding.getRoot());
    }

    @Override
    public void init() {

    }

    @Override
    protected void initLayout() {

    }

    @Override
    public void loadData() {
        Student student = SpUtil.getStudent();
        binding.nameTv.setText("姓名 : " + student.getUserRealName());
        binding.schoolTv.setText("学校 : " + student.getSchoolName());
        binding.classTv.setText("班级 : " + student.getClassName());
        binding.numTv.setText("班级 : " + student.getUserNum());
    }

    @Override
    protected void refreshView() {

    }

    public void enter(View view){
        if (TextUtils.isEmpty(binding.localLockEdittext.getText())){
            new HintDialog(getThisActivity() , "密码不能为空").show();
            return;
        }
        Log.v("FH" , "edittext : " + binding.localLockEdittext.getText().toString() + " local : " + SpUtil.getLocalLockPwd());
        if (binding.localLockEdittext.getText().toString().equals(SpUtil.getLocalLockPwd())){
            finish();
            if (!getIntent().getBooleanExtra(NOT_GOTO_HOMEPAGE_ON_ENTER, false)){
                loadIntent(MainActivity.class);
            }
        }
        else {
            new HintDialog(getThisActivity() , "密码不正确").show();
        }
    }

    public void forgetPwd(View view){
        new ConfirmDialog(this, "忘记本机锁密码需要使用乐课账户密码重新登录来重置本机锁密码,是否确定要重置?"
                , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadIntent(LoginActivity.class);
                dialog.dismiss();
                finish();
            }
        }).show();
    }

    @Override
    public void onBackPressed() {
    }
}
