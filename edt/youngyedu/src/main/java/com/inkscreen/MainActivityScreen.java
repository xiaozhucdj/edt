package com.inkscreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.Toast;

import com.inkscreen.adapter.MyFragmentPagerAdapter;
import com.inkscreen.callback.LoginRjCallBack;
import com.inkscreen.model.Event;
import com.inkscreen.ui.LeSubTitleLayout;
import com.inkscreen.utils.AndroidUtils;
import com.inkscreen.will.utils.widget.MyViewPager;
import com.yougy.common.global.Commons;
import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.home.activity.ControlFragmentActivity;
import com.yougy.ui.activity.R;

import de.greenrobot.event.EventBus;


public class MainActivityScreen extends AppCompatActivity {
    private TabLayout mTabLayout;
    private MyViewPager mViewPager;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    //    private TabLayout.Tab one;
//    private TabLayout.Tab two;
    private RadioButton workRD, worngRD, recordRD;
    private static final int WORNGPAGE = 1;
    private static final int WORKPAGE = 0;
    private static final int RECORDPAG = 2;
    LeSubTitleLayout leSubTitleLayout;
    private int mBookId;
    private int mNoteId;
    private int mNoteStyle;
    private String mNotetitle;
    private int mHomewrokId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidUtils.transparentStatusbar(getWindow());
        setContentView(R.layout.activity_main1);
        //初始化视图
//       initViews();

        EventBus.getDefault().register(this);

         if (NetUtils.isNetConnected()){
            //重复绑定会失败，用户可以清空APP数据 ，所以每次都进来登录
            LoginRjCallBack callBack = new LoginRjCallBack(MainActivityScreen.this);
           // callBack.setOnJumpListener((LoginRjCallBack.OnJumpListener) LoginActivity.this);
            ProtocolManager.loginProtocol(Commons.UUID, ProtocolId.PROTOCOL_ID_LOGIN, callBack);
//             Toast.makeText(MainActivityScreen.this, "网络ok", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(MainActivityScreen.this, "请检查网络", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 这里设置接收到2个地方传递过来的 传递来的Bundle 用来跳转到书和笔记的参数
        // 1.作业列表传递
        // 2.点击 书 笔记 （作业 按钮 传参数）
        // 3.作业内部 跳转Activity（无需传bundle）
        // 4.每次进入 都需要 判断下 笔记和课本 Btn状态
        getBundle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.i("yuanye..MainScreen"+ "onResume");
    }
    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.i("yuanye..MainScreen"+ "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.i("yuanye..MainScreen"+ "onRestart");
    }



    public void onEvent(Event<Object> event) {
        if (event.getRequestCode() == 200) {
            initViews();
        }


    }

    private void initViews() {
        leSubTitleLayout = (LeSubTitleLayout) findViewById(R.id.appSubTitleLayout);
        workRD = (RadioButton) findViewById(R.id.radio_zuoyeid);
        recordRD = (RadioButton) findViewById(R.id.radio_recordid);
        worngRD = (RadioButton) findViewById(R.id.radio_cuotiid);

        workRD.setOnClickListener(new MyWorkClickListener());
        recordRD.setOnClickListener(new MyWorkClickListener());
        worngRD.setOnClickListener(new MyWorkClickListener());
        //使用适配器将ViewPager与Fragment绑定在一起
        mViewPager = (MyViewPager) findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(2);
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(myFragmentPagerAdapter);
        //将TabLayout与ViewPager绑定在一起
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        //指定Tab的位置
//        one = mTabLayout.getTabAt(0);
//        two = mTabLayout.getTabAt(1);

        //设置Tab的图标
//        one.setIcon(R.mipmap.ic_launcher);
//        two.setIcon(R.mipmap.ic_launcher);
        mViewPager.setPagingEnabled(false);

        leSubTitleLayout.setTitle("", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        leSubTitleLayout.setOncBook("", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBookId>0 && FileUtils.exists(FileUtils.getTextBookFilesDir() + mBookId + ".pdf")){
                    jumpActivityControl(FileContonst.JUMP_TEXT_BOOK);
                }else{
                    Toast.makeText(MainActivityScreen.this, "当前书还未下载", Toast.LENGTH_LONG).show();
                }
            }
        });


        leSubTitleLayout.setOncPan("",new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (mNoteId>0 ){
                    jumpActivityControl(FileContonst.JUMP_NOTE);
                }else{
                    Toast.makeText(MainActivityScreen.this, "没有对应的笔记", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    class MyWorkClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            closeKeybord();
            switch (v.getId()) {
                case R.id.radio_zuoyeid:
//                    worngtext.setBackgroundResource(R.drawable.btn_market_selector);
//                    worktext.setBackgroundResource(R.drawable.btn_market_press_selector);
//                    worktext.getPaint().setFakeBoldText(true);
//                    worngtext.getPaint().setFakeBoldText(false);
//                    worktext.setTextColor(getResources().getColor(R.color.zl_white));
//                    worngtext.setTextColor(getResources().getColor(R.color.zl_black));
                    workRD.setChecked(true);
                    recordRD.setChecked(false);
                    worngRD.setChecked(false);
                    workRD.setTextColor(getResources().getColor(R.color.zl_white));
                    recordRD.setTextColor(getResources().getColor(R.color.zl_black));
                    worngRD.setTextColor(getResources().getColor(R.color.zl_black));
                    mViewPager.setCurrentItem(WORKPAGE);
                    //closeBoard(MainActivityScreen.this);
//                    showNet();
//
//                    if(actionLoadListener!=null){
//                        showNet();
//                        if (AndroidUtils.isNetworkAvailable(MainActivityScreen.this)){
//                            actionLoadListener.RunAction();
//                        }
//
//                    }
                    if (myFragmentPagerAdapter != null) {
                        myFragmentPagerAdapter.reFreshPage(mViewPager.getCurrentItem());
                    }
                    // closeBoard(MainActivityScreen.this);
                    break;

                case R.id.radio_recordid:
//                    worktext.setBackgroundResource(R.drawable.btn_market_selector);
//                    worngtext.setBackgroundResource(R.drawable.btn_market_press_selector);
//                    worngtext.getPaint().setFakeBoldText(true);
//                    worktext.getPaint().setFakeBoldText(false);
//                    worngtext.setTextColor(getResources().getColor(R.color.zl_white));
//                    worktext.setTextColor(getResources().getColor(R.color.zl_black));


                    workRD.setChecked(false);
                    recordRD.setChecked(true);
                    worngRD.setChecked(false);
                    workRD.setTextColor(getResources().getColor(R.color.zl_black));
                    recordRD.setTextColor(getResources().getColor(R.color.zl_white));
                    worngRD.setTextColor(getResources().getColor(R.color.zl_black));
                    mViewPager.setCurrentItem(WORNGPAGE);
                    // closeBoard(MainActivityScreen.this);
                    if (myFragmentPagerAdapter != null) {
                        myFragmentPagerAdapter.reFreshPage(mViewPager.getCurrentItem());
                    }

                    break;

                case R.id.radio_cuotiid:
//                    worktext.setBackgroundResource(R.drawable.btn_market_selector);
//                    worngtext.setBackgroundResource(R.drawable.btn_market_press_selector);
//                    worngtext.getPaint().setFakeBoldText(true);
//                    worktext.getPaint().setFakeBoldText(false);
//                    worngtext.setTextColor(getResources().getColor(R.color.zl_white));
//                    worktext.setTextColor(getResources().getColor(R.color.zl_black));

                    workRD.setChecked(false);
                    recordRD.setChecked(false);
                    worngRD.setChecked(true);
                    workRD.setTextColor(getResources().getColor(R.color.zl_black));
                    recordRD.setTextColor(getResources().getColor(R.color.zl_black));
                    worngRD.setTextColor(getResources().getColor(R.color.zl_white));
                    mViewPager.setCurrentItem(RECORDPAG);
                    // closeBoard(MainActivityScreen.this);
                    if (myFragmentPagerAdapter != null) {
                        myFragmentPagerAdapter.reFreshPage(mViewPager.getCurrentItem());
                    }
                    //  closeBoard(MainActivityScreen.this);
                    break;
                default:
                    break;
            }

        }
    }

    private void closeKeybord() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    /***
     * 功能：跳转 书和笔记的 activity
     * 跳转结果：可以跳转到 书 阅读页面
     * 完善：临时测试 待 数据完善 修改 ，可以正确跳转 笔记和书
     * * @param tag :目标
     */
    private void jumpActivityControl(String tag) {
        Bundle extras = new Bundle();
        //进入地址
        extras.putString(FileContonst.JUMP_FRAGMENT, tag);
        LogUtils.i("yuanye ....mJump  test2 =="+tag);
        extras.putInt(FileContonst.BOOK_ID, mBookId);
        extras.putInt(FileContonst.NOTE_ID, mNoteId);
        extras.putInt(FileContonst.HOME_WROK_ID, mHomewrokId);

        extras.putInt(FileContonst.NOTE_Style, mNoteStyle);
        extras.putString(FileContonst.NOTE_TITLE, mNotetitle);

        Intent intent = new Intent(MainActivityScreen.this, ControlFragmentActivity.class);
        intent.putExtras(extras);
        startActivity(intent);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void  getBundle(){
        LogUtils.i("yuanye..MainScreen"+ "onStart");

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            mBookId = bundle.getInt(FileContonst.BOOK_ID, -1);
            mNoteId = bundle.getInt(FileContonst.NOTE_ID, -1);
            LogUtils.i("yuanye....BOOK_ID == "+mBookId);
            LogUtils.i("yuanye....mNoteId == "+mNoteId);
            mNoteStyle = bundle.getInt(FileContonst.NOTE_Style, 0);
            mNotetitle = bundle.getString(FileContonst.NOTE_TITLE, "");
            mHomewrokId= bundle.getInt(FileContonst.HOME_WROK_ID, -1);
        }
    }
}
