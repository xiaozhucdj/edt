package com.yougy.home.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.artifex.mupdfdemo.pdf.task.AsyncTask;
import com.bumptech.glide.Glide;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.yougy.TestImgActivity;
import com.yougy.anwser.AnsweringActivity;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.global.Commons;
import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.NetManager;
import com.yougy.common.manager.PowerManager;
import com.yougy.common.manager.ThreadManager;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.new_network.ApiException;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.protocol.request.NewLoginReq;
import com.yougy.common.service.DownloadService;
import com.yougy.common.service.UploadService;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.DeviceScreensaverUtils;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.fragment.mainFragment.AllCoachBookFragment;
import com.yougy.home.fragment.mainFragment.AllHomeworkFragment;
import com.yougy.home.fragment.mainFragment.AllNotesFragment;
import com.yougy.home.fragment.mainFragment.AllTextBookFragment;
import com.yougy.home.fragment.mainFragment.CoachBookFragment;
import com.yougy.home.fragment.mainFragment.AnswerBookChooseFragment;
import com.yougy.home.fragment.mainFragment.HomeworkFragment;
import com.yougy.home.fragment.mainFragment.NotesFragment;
import com.yougy.home.fragment.mainFragment.ReferenceBooksFragment;
import com.yougy.home.fragment.mainFragment.TaskFragment;
import com.yougy.home.fragment.mainFragment.TextBookFragment;
import com.yougy.init.activity.LoginActivity;
import com.yougy.message.YXClient;
import com.yougy.message.ui.RecentContactListActivity;
import com.yougy.order.LockerActivity;
import com.yougy.setting.ui.SettingMainActivity;
import com.yougy.shop.activity.BookShopActivityDB;
import com.yougy.ui.activity.BuildConfig;
import com.yougy.ui.activity.R;
import com.yougy.update.DownloadManager;
import com.yougy.update.VersionUtils;
import com.yougy.view.dialog.AppUpdateDialog;
import com.yougy.view.dialog.DownProgressDialog;

import org.litepal.tablemanager.Connector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import rx.functions.Action1;

import static com.yougy.common.global.FileContonst.LOCK_SCREEN;
import static com.yougy.common.utils.AliyunUtil.DATABASE_NAME;
import static com.yougy.common.utils.AliyunUtil.JOURNAL_NAME;

//import com.tencent.bugly.crashreport.CrashReport;


/**
 * V1
 * Created by Administrator on 2016/8/24.
 * <p/>
 * 作业
 * 笔记
 * 课外书
 * 辅导书
 * 课本
 * fragment 切换
 * <p/>
 * V2
 * Created by Administrator on 2016/9/18.
 * 增加需求:个人信息  ，书城......
 * * Created by Administrator on 2016/10/28.
 * 增加需求:侧边栏 区分 全部 可当前学期 ，和按钮的动态变化
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private CoachBookFragment mCoachBookFragment;
    private HomeworkFragment mHomeworkFragment;
    private AnswerBookChooseFragment mAnswerBookChooseFragment;
    private NotesFragment mNotesFragment;
    private ReferenceBooksFragment mReferenceBooksFragment;
    private TextBookFragment mTextBookFragment;
    private TaskFragment mTaskFragment;

    private AllTextBookFragment mAllTextBookFragment;
    private AllCoachBookFragment mAllCoachBookFragment;
    private AllNotesFragment mAllNotesFragment;
    private AllHomeworkFragment mAllHomeworkFragment;

    private TextView mTvAnswer;
    private TextView mTvHomework;
    private TextView mTvNotes;
    private TextView mTvReferenceBooks;
    private TextView mTvCoachBook;
    private TextView mTvTextBook;

    private Button mBtnTask;
    private Button mBtnBookStore;
    private Button mBtnCurrentBook;
    private Button mBtnAllBook;
    private Button mBtnMsg;

    //    private WifiStatusChangedReceiver receiver = new WifiStatusChangedReceiver();
//    private IntentFilter filter;

    private ImageButton mImgBtnShowRight;
    private TextView mTvClassName;
    private TextView mTvUserName;
    private Button mBtnSearChBook;
    private Button mBtnAccout;
    private FrameLayout mFlRight;
    /********************************
     * 侧边栏目 UI变化 显示的参数
     ***************/
    //判断是本学期 ，还是全部 ，true 全部，false 本学期
    private boolean mIsAll = false;
    // Fragment 切换条目的状态选项 ，可以判断 侧边栏目UI 状态
    private FragmentDisplayOption mDisplayOption;
    private Button mBtnRefresh;
    private ImageView mImgWSysWifi;
    private ImageView mImgWSysPower;
    private TextView mTvSysPower;
    private TextView mTvSysTime;
    private Button mBtnSysSeeting;
    private View mRootView;
    private ImageView mIvMsg;


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int totalUnreadMsgCount = msg.what;
            if (totalUnreadMsgCount != 0) {
                mBtnMsg.setText("我的消息    未读" + totalUnreadMsgCount);
                mIvMsg.setVisibility(View.VISIBLE);
//                unreadMsgCountTextview1.setText("" + totalUnreadMsgCount);
            } else {
                mBtnMsg.setText("我的消息");
                mIvMsg.setVisibility(View.GONE);
            }
        }
    };
    private ImageView imgSextIcon;
    private long mLastTime;
    private TextView testVersion;
    private boolean isStScreensaver;
    private AppUpdateDialog mAppUpdateDialog;
    private Button btn_deviceSize;
    private DeviceYxMsgErrorDialog mDeviceYxMsgErrorDialog;


    private static long lastCheckTimeMill = 0;
    private static CheckRunnable checkRunnable = new CheckRunnable();
    private boolean initializationNeedNetFinished = false;

    @Override
    protected void onResume() {
        super.onResume();
        long timeTolastCheck = System.currentTimeMillis() - lastCheckTimeMill;
        LogUtils.e("FH", "主界面onResume验证是否被解绑-------距离上一次验证是否被解绑过了" + (timeTolastCheck/1000) + "秒");
        if (System.currentTimeMillis() - lastCheckTimeMill > 60*60*1000){
//        if (System.currentTimeMillis() - lastCheckTimeMill > 5*1000){
            LogUtils.e("FH", "主界面onResume验证是否被解绑-------距离上一次验证解绑时间过长,启动验证是否解绑流程");
            YoungyApplicationManager.getMainThreadHandler().removeCallbacks(checkRunnable);
            YoungyApplicationManager.getMainThreadHandler().post(checkRunnable.setActivity(this));
        }
        else {
            LogUtils.e("FH", "主界面onResume验证是否被解绑-------距离上一次验证解绑时间在可允许范围内,本次不验证是否解绑");
        }
    }

    /***************************************************************************/

    @Override
    protected void init() {
        mIsCheckStartNet = false;
        setPressTwiceToExit(true);
        YXClient.getInstance().with(this).addOnRecentContactListChangeListener(new YXClient.OnThingsChangedListener<List<RecentContact>>() {
            @Override
            public void onThingChanged(List<RecentContact> thing, int type) {
                ArrayList<RecentContact> recentContactList = new ArrayList<RecentContact>();
                recentContactList.addAll(YXClient.getInstance().getRecentContactList());
                int totalUnreadCount = 0;
                for (RecentContact recentContact : recentContactList) {
                    totalUnreadCount += YXClient.getInstance().getUnreadMsgCount(recentContact.getContactId(), recentContact.getSessionType());
                }
                mHandler.sendEmptyMessage(totalUnreadCount);
            }
        });
    }

    private void removeFragments() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(mCoachBookFragment);
        fragmentTransaction.remove(mHomeworkFragment);
        fragmentTransaction.remove(mAnswerBookChooseFragment);
        fragmentTransaction.remove(mNotesFragment);
        fragmentTransaction.remove(mReferenceBooksFragment);
        fragmentTransaction.remove(mTextBookFragment);
        fragmentTransaction.remove(mTaskFragment);
        fragmentTransaction.remove(mAllTextBookFragment);
        fragmentTransaction.remove(mAllCoachBookFragment);
        fragmentTransaction.remove(mAllNotesFragment);
        fragmentTransaction.remove(mAllHomeworkFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        Glide.get(this).clearMemory();
        mCoachBookFragment = null;
        mHomeworkFragment = null;
        mAnswerBookChooseFragment = null;
        mNotesFragment = null;
        mReferenceBooksFragment = null;
        mTextBookFragment = null;
        mTaskFragment = null;

        mAllTextBookFragment = null;
        mAllCoachBookFragment = null;
        mAllNotesFragment = null;
        mAllHomeworkFragment = null;

        super.onDestroy();
    }

    @Override
    protected void initLayout() {
        mRootView = UIUtils.inflate(R.layout.activity_main_ui);
        setContentView(mRootView);

        mTvAnswer = (TextView) findViewById(R.id.tv_answer);
        mTvAnswer.setOnClickListener(this);

        mTvHomework = (TextView) findViewById(R.id.tv_homework);
        mTvHomework.setOnClickListener(this);

        mTvNotes = (TextView) findViewById(R.id.tv_notes);
        mTvNotes.setOnClickListener(this);

        mTvReferenceBooks = (TextView) findViewById(R.id.tv_reference_books);
        mTvReferenceBooks.setOnClickListener(this);

        mTvCoachBook = (TextView) findViewById(R.id.tv_coach_book);
        mTvCoachBook.setOnClickListener(this);

        mTvTextBook = (TextView) findViewById(R.id.tv_text_book);
        mTvTextBook.setOnClickListener(this);

        mBtnTask = findViewById(R.id.btn_task);
        mBtnTask.setOnClickListener(this);
        //有侧边栏显示按钮
        mImgBtnShowRight = (ImageButton) this.findViewById(R.id.imgBtn_showRight);
        mImgBtnShowRight.setOnClickListener(this);
        // 右边侧边栏显示内容
        mFlRight = (FrameLayout) this.findViewById(R.id.fl_right);
        mFlRight.setOnClickListener(this);
        //班级
        mTvClassName = (TextView) this.findViewById(R.id.tv_className);
        mTvClassName.setText(SpUtils.getAccountClass());
        //学生名字
        mTvUserName = (TextView) this.findViewById(R.id.tv_userName);
        mTvUserName.setText(SpUtils.getAccountName());
        //当前学期使用的书
        mBtnCurrentBook = (Button) this.findViewById(R.id.btn_currentBook);
        mBtnCurrentBook.setOnClickListener(this);
        //全部用书
        mBtnAllBook = (Button) this.findViewById(R.id.btn_allBook);
        mBtnAllBook.setOnClickListener(this);
        //搜索课外书
        mBtnSearChBook = (Button) this.findViewById(R.id.btn_serchBook);
        mBtnSearChBook.setOnClickListener(this);
        // 商城
        mBtnBookStore = (Button) this.findViewById(R.id.btn_bookStore);
        mBtnBookStore.setOnClickListener(this);
        //消息中心
        mBtnMsg = (Button) this.findViewById(R.id.btn_msg);
        mBtnMsg.setOnClickListener(this);
        //账号设置
        mBtnAccout = (Button) this.findViewById(R.id.btn_account);
        mBtnAccout.setOnClickListener(this);

        mIvMsg = (ImageView) findViewById(R.id.iv_home_msg);

        mImgWSysWifi = (ImageView) this.findViewById(R.id.img_wifi);
        mImgWSysWifi.setOnClickListener(this);
        mImgWSysPower = (ImageView) this.findViewById(R.id.img_electricity);
        mTvSysPower = (TextView) this.findViewById(R.id.tv_power);
        mTvSysTime = (TextView) this.findViewById(R.id.tv_time);
        mBtnSysSeeting = (Button) this.findViewById(R.id.btn_sysSeeting);
        mBtnSysSeeting.setOnClickListener(this);
        //初始化fragment
        initFragment();
        mBtnRefresh = (Button) this.findViewById(R.id.btn_refresh);
        mBtnRefresh.setOnClickListener(this);

        findViewById(R.id.btn_upload).setOnClickListener(this);
        findViewById(R.id.btn_download).setOnClickListener(this);
        findViewById(R.id.btn_test_img).setOnClickListener(this);
        findViewById(R.id.btn_check_update).setOnClickListener(this);

        imgSextIcon = (ImageView) this.findViewById(R.id.img_sex_icon);
        testVersion = (TextView) this.findViewById(R.id.test_version);
        btn_deviceSize = (Button) findViewById(R.id.btn_deviceSize);
        btn_deviceSize.setOnClickListener(this);
    }

    @Override
    protected void loadData() {
        btn_deviceSize.setVisibility(View.VISIBLE);
        if (BuildConfig.DEBUG) {
//            testVersion.setVisibility(View.VISIBLE);
//            testVersion.setText(UIUtils.getString(R.string.app_name));
//            btn_deviceSize.setVisibility(View.VISIBLE);
        }

        String sex = SpUtils.getSex();
        if ("男".equalsIgnoreCase(sex)) {
            imgSextIcon.setImageDrawable(UIUtils.getDrawable(R.drawable.icon_avatar_student_male_120px));
        } else {
            imgSextIcon.setImageDrawable(UIUtils.getDrawable(R.drawable.icon_avatar_student_famale_122px));
        }
        mTvTextBook.callOnClick();
        setSysPower(PowerManager.getInstance().getlevelPercent(), PowerManager.getInstance().getBatteryStatus());

//        CrashReport.setUserId(SpUtils.getUserId()+":"+ Commons.UUID);
    }

    @Override
    protected void refreshView() {
    }

    @Override
    public void onClick(View v) {

   /*     long currentTime = System.currentTimeMillis();
        if (mLastTime > 0 && SystemUtils.getDeviceModel().equalsIgnoreCase("PL107")) {
            if (currentTime - mLastTime < 1000) {
                UIUtils.showToastSafe("操作过快");
                return;
            }
        }
        mLastTime = currentTime;*/

        int clickedViewId = v.getId();
        setSysTime();

        if (v.getId() == R.id.imgBtn_showRight) {
            mFlRight.setVisibility(mFlRight.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        } else {
            mFlRight.setVisibility(View.GONE);
        }

        switch (clickedViewId) {
            case R.id.btn_upload:
                startService(new Intent(this, UploadService.class));
                break;
            case R.id.btn_download:
                startService(new Intent(this, DownloadService.class));
                break;
            case R.id.btn_check_update:
                getServerVersion();
                break;
            case R.id.btn_test_img:
                startActivity(new Intent(this, TestImgActivity.class));
                // TODO: 2018/3/8
                Intent newIntent = new Intent(getApplicationContext(), AnsweringActivity.class);
                startActivity(newIntent);

                break;
            case R.id.tv_answer:
                refreshTabBtnState(clickedViewId);
                bringFragmentToFrontInner(FragmentDisplayOption.ANSWER_FRAGMENT);
//                EpdController.invalidate(mRootView, UpdateMode.GC);
//                startActivity(new Intent(this, AnsweringActivity.class));
                break;

            case R.id.tv_homework:
//                XSharedPref.putString(this, "loadApp", "student");
                refreshTabBtnState(clickedViewId);
                bringFragmentToFrontInner(mIsAll == true ? FragmentDisplayOption.ALL_HOMEWORK_FRAGMENT : FragmentDisplayOption.HOMEWORK_FRAGMENT);
//                EpdController.invalidate(mRootView, UpdateMode.GC);
                break;

            case R.id.tv_notes:
//                XSharedPref.putString(this, "loadApp", "");
                refreshTabBtnState(clickedViewId);
                bringFragmentToFrontInner(mIsAll == true ? FragmentDisplayOption.ALL_NOTES_FRAGMENT : FragmentDisplayOption.NOTES_FRAGMENT);
//                EpdController.invalidate(mRootView, UpdateMode.GC);
                break;
            case R.id.tv_reference_books:
                refreshTabBtnState(clickedViewId);
                bringFragmentToFrontInner(FragmentDisplayOption.REFERENCE_BOOKS_FRAGMENT);
//                EpdController.invalidate(mRootView, UpdateMode.GC);
                break;

            case R.id.tv_coach_book:
                refreshTabBtnState(clickedViewId);
                bringFragmentToFrontInner(mIsAll == true ? FragmentDisplayOption.ALL_COACH_BOOK_FRAGMENT : FragmentDisplayOption.COACH_BOOK_FRAGMENT);
//                EpdController.invalidate(mRootView, UpdateMode.GC);
                break;

            case R.id.tv_text_book:
                refreshTabBtnState(clickedViewId);
                bringFragmentToFrontInner(mIsAll == true ? FragmentDisplayOption.ALL_TEXT_BOOK_FRAGMENT : FragmentDisplayOption.TEXT_BOOK_FRAGMENT);
//                EpdController.invalidate(mRootView, UpdateMode.GC);
                break;
            case R.id.btn_task:
                refreshTabBtnState(clickedViewId);
                bringFragmentToFrontInner(FragmentDisplayOption.TASK_FRAGMENT);
                break;
            case R.id.imgBtn_showRight:
//                mFlRight.setVisibility(View.VISIBLE);
//                EpdController.invalidate(mRootView, UpdateMode.GC);
                ArrayList<RecentContact> recentContactList = new ArrayList<RecentContact>();
                recentContactList.addAll(YXClient.getInstance().getRecentContactList());
                int totalUnreadCount = 0;
                for (RecentContact recentContact : recentContactList) {
                    totalUnreadCount += YXClient.getInstance().getUnreadMsgCount(recentContact.getContactId(), recentContact.getSessionType());
                }
                if (totalUnreadCount != 0) {
                    mBtnMsg.setText("我的消息    未读" + totalUnreadCount);
                } else {
                    mBtnMsg.setText("我的消息");
                }
                break;

            case R.id.fl_right:
                mFlRight.setVisibility(View.GONE);
//                EpdController.invalidate(mRootView, UpdateMode.GC);
                break;

            case R.id.btn_currentBook:
                LogUtils.i("当前学期");
                mIsAll = false;
                if (mDisplayOption == FragmentDisplayOption.ALL_TEXT_BOOK_FRAGMENT) {
                    bringFragmentToFrontInner(FragmentDisplayOption.TEXT_BOOK_FRAGMENT);

                } else if (mDisplayOption == FragmentDisplayOption.ALL_COACH_BOOK_FRAGMENT) {
                    bringFragmentToFrontInner(FragmentDisplayOption.COACH_BOOK_FRAGMENT);

                } else if (mDisplayOption == FragmentDisplayOption.ALL_NOTES_FRAGMENT) {
                    bringFragmentToFrontInner(FragmentDisplayOption.NOTES_FRAGMENT);

                } else if (mDisplayOption == FragmentDisplayOption.ALL_HOMEWORK_FRAGMENT) {
                    bringFragmentToFrontInner(FragmentDisplayOption.HOMEWORK_FRAGMENT);
                }

                break;

            case R.id.btn_allBook:
                LogUtils.i("全部");
                mIsAll = true;
                if (mDisplayOption == FragmentDisplayOption.TEXT_BOOK_FRAGMENT) {
                    bringFragmentToFrontInner(FragmentDisplayOption.ALL_TEXT_BOOK_FRAGMENT);

                } else if (mDisplayOption == FragmentDisplayOption.COACH_BOOK_FRAGMENT) {
                    bringFragmentToFrontInner(FragmentDisplayOption.ALL_COACH_BOOK_FRAGMENT);

                } else if (mDisplayOption == FragmentDisplayOption.NOTES_FRAGMENT) {
                    bringFragmentToFrontInner(FragmentDisplayOption.ALL_NOTES_FRAGMENT);

                } else if (mDisplayOption == FragmentDisplayOption.HOMEWORK_FRAGMENT) {
                    bringFragmentToFrontInner(FragmentDisplayOption.ALL_HOMEWORK_FRAGMENT);
                }
                break;
            case R.id.btn_serchBook:
                LogUtils.i("搜索课外书");
                BaseEvent baseEvent = new BaseEvent(EventBusConstant.serch_reference, "");
                EventBus.getDefault().post(baseEvent);
                break;
            case R.id.btn_bookStore:
                LogUtils.e(getClass().getName(), "书城");
                if (NetUtils.isNetConnected()) {
                    loadIntent(BookShopActivityDB.class);
                } else {
                    showCancelAndDetermineDialog(R.string.jump_to_net);
                }
                break;
            case R.id.btn_msg:
                changeSystemConfigIntegerValue(getApplicationContext() , "close_wifi_delay" , -1);
//                LogUtils.e(getClass().getName(), "我的消息");
                gotoMyMessage();
                break;
            case R.id.btn_account:
                LogUtils.i("账号设置");

                if (NetUtils.isNetConnected()) {
                    loadIntent(SettingMainActivity.class);
                } else {
                    showCancelAndDetermineDialog(R.string.jump_to_net);
                }

                break;
            case R.id.btn_refresh:
                LogUtils.i("刷新列表");
                if (NetUtils.isNetConnected()) {
                    postEvent();
                } else {
                    showCancelAndDetermineDialog(R.string.jump_to_net);
                }
                break;

            case R.id.img_wifi:
//                boolean isConnected = NetManager.getInstance().isWifiConnected(this);
                boolean isConnected = false;
                NetManager.getInstance().changeWiFi(this, !isConnected);
                mImgWSysWifi.setImageDrawable(UIUtils.getDrawable(isConnected ? R.drawable.img_wifi_1 : R.drawable.img_wifi_0));
                break;

            case R.id.btn_sysSeeting:
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.onyx.android.settings", "com.onyx.android.libsetting.view.activity.DeviceMainSettingActivity"));
                startActivity(intent);
                break;

            case R.id.btn_deviceSize:
//                UIUtils.showToastSafe("W--"+UIUtils.getScreenWidth() +"H--"+UIUtils.getScreenHeight());
                if (mDeviceYxMsgErrorDialog == null) {
                    mDeviceYxMsgErrorDialog = new DeviceYxMsgErrorDialog(this);
                }

                mDeviceYxMsgErrorDialog.show();

            default:
                break;
        }
    }

    private void gotoMyMessage() {
        if (!NetUtils.isNetConnected()) {
            showCancelAndDetermineDialog(R.string.jump_to_net);
            return;
        }
        loadIntent(RecentContactListActivity.class);
    }

    private void postEvent() {
        String type = "";
        switch (mDisplayOption) {
            case TEXT_BOOK_FRAGMENT:
                type = EventBusConstant.current_text_book;
                break;

            case ALL_TEXT_BOOK_FRAGMENT:
                type = EventBusConstant.all_text_book;
                break;

            case COACH_BOOK_FRAGMENT:
                type = EventBusConstant.current_coach_book;
                break;

            case ALL_COACH_BOOK_FRAGMENT:
                type = EventBusConstant.all_coach_book;
                break;

            case REFERENCE_BOOKS_FRAGMENT:
                type = EventBusConstant.current_reference_book;
                break;

            case NOTES_FRAGMENT:
                type = EventBusConstant.current_note;
                break;
            case ALL_NOTES_FRAGMENT:
                type = EventBusConstant.all_notes;
                break;
            case HOMEWORK_FRAGMENT:
                type = EventBusConstant.current_home_work;
                break;
            case ALL_HOMEWORK_FRAGMENT:
                type = EventBusConstant.all_home_work;
                break;

            case ANSWER_FRAGMENT:
                type = EventBusConstant.answer_event;
                break;
        }

        LogUtils.i("刷新" + type);
        BaseEvent baseEvent = new BaseEvent(type, "");
        EventBus.getDefault().post(baseEvent);
    }


    /***
     * 切换fragment
     */
    private void bringFragmentToFrontInner(FragmentDisplayOption fragmentDisplayOption) {
        mDisplayOption = fragmentDisplayOption;
        Fragment whichToFront = null;
        Fragment whichToBack1 = null;
        Fragment whichToBack2 = null;
        Fragment whichToBack3 = null;
        Fragment whichToBack4 = null;
        Fragment whichToBack5 = null;
        Fragment whichToBack6 = null;
        Fragment whichToBack7 = null;
        Fragment whichToBack8 = null;
        Fragment whichToBack9 = null;
        Fragment whichToBack10 = null;

        switch (fragmentDisplayOption) {
            case TEXT_BOOK_FRAGMENT:
                //显示 本学期 课本
                whichToFront = mTextBookFragment;
                whichToBack1 = mAllTextBookFragment;
                whichToBack2 = mCoachBookFragment;
                whichToBack3 = mAllCoachBookFragment;
                whichToBack4 = mReferenceBooksFragment;
                whichToBack5 = mNotesFragment;
                whichToBack6 = mAllNotesFragment;
                whichToBack7 = mHomeworkFragment;
                whichToBack8 = mAllHomeworkFragment;
                whichToBack9 = mAnswerBookChooseFragment;
                whichToBack10 = mTaskFragment;
                break;
            case ALL_TEXT_BOOK_FRAGMENT:
                //显示 全部 课本
                whichToFront = mAllTextBookFragment;
                whichToBack1 = mTextBookFragment;
                whichToBack2 = mCoachBookFragment;
                whichToBack3 = mAllCoachBookFragment;
                whichToBack4 = mReferenceBooksFragment;
                whichToBack5 = mNotesFragment;
                whichToBack6 = mAllNotesFragment;
                whichToBack7 = mHomeworkFragment;
                whichToBack8 = mAllHomeworkFragment;
                whichToBack9 = mAnswerBookChooseFragment;
                whichToBack10 = mTaskFragment;
                break;

            case COACH_BOOK_FRAGMENT:
                //显示 辅导书
                whichToFront = mCoachBookFragment;
                whichToBack1 = mTextBookFragment;
                whichToBack2 = mAllTextBookFragment;
                whichToBack3 = mAllCoachBookFragment;
                whichToBack4 = mReferenceBooksFragment;
                whichToBack5 = mNotesFragment;
                whichToBack6 = mAllNotesFragment;
                whichToBack7 = mHomeworkFragment;
                whichToBack8 = mAllHomeworkFragment;
                whichToBack9 = mAnswerBookChooseFragment;
                whichToBack10 = mTaskFragment;
                break;

            case ALL_COACH_BOOK_FRAGMENT:
                //全部辅导书
                whichToFront = mAllCoachBookFragment;
                whichToBack1 = mTextBookFragment;
                whichToBack2 = mAllTextBookFragment;
                whichToBack3 = mCoachBookFragment;
                whichToBack4 = mReferenceBooksFragment;
                whichToBack5 = mNotesFragment;
                whichToBack6 = mAllNotesFragment;
                whichToBack7 = mHomeworkFragment;
                whichToBack8 = mAllHomeworkFragment;
                whichToBack9 = mAnswerBookChooseFragment;
                whichToBack10 = mTaskFragment;
                break;

            case REFERENCE_BOOKS_FRAGMENT:
                //课外书
                whichToFront = mReferenceBooksFragment;
                whichToBack1 = mTextBookFragment;
                whichToBack2 = mAllTextBookFragment;
                whichToBack3 = mCoachBookFragment;
                whichToBack4 = mAllCoachBookFragment;
                whichToBack5 = mNotesFragment;
                whichToBack6 = mAllNotesFragment;
                whichToBack7 = mHomeworkFragment;
                whichToBack8 = mAllHomeworkFragment;
                whichToBack9 = mAnswerBookChooseFragment;
                whichToBack10 = mTaskFragment;
                break;

            case NOTES_FRAGMENT:
                //笔记
                whichToFront = mNotesFragment;
                whichToBack1 = mTextBookFragment;
                whichToBack2 = mAllTextBookFragment;
                whichToBack3 = mCoachBookFragment;
                whichToBack4 = mAllCoachBookFragment;
                whichToBack5 = mReferenceBooksFragment;
                whichToBack6 = mAllNotesFragment;
                whichToBack7 = mHomeworkFragment;
                whichToBack8 = mAllHomeworkFragment;
                whichToBack9 = mAnswerBookChooseFragment;
                whichToBack10 = mTaskFragment;
                break;

            case ALL_NOTES_FRAGMENT:

                //全部笔记
                whichToFront = mAllNotesFragment;
                whichToBack1 = mTextBookFragment;
                whichToBack2 = mAllTextBookFragment;
                whichToBack3 = mCoachBookFragment;
                whichToBack4 = mAllCoachBookFragment;
                whichToBack5 = mReferenceBooksFragment;
                whichToBack6 = mNotesFragment;
                whichToBack7 = mHomeworkFragment;
                whichToBack8 = mAllHomeworkFragment;
                whichToBack9 = mAnswerBookChooseFragment;
                whichToBack10 = mTaskFragment;
                break;
            case HOMEWORK_FRAGMENT:
                //作业
                whichToFront = mHomeworkFragment;
                whichToBack1 = mTextBookFragment;
                whichToBack2 = mAllTextBookFragment;
                whichToBack3 = mCoachBookFragment;
                whichToBack4 = mAllCoachBookFragment;
                whichToBack5 = mReferenceBooksFragment;
                whichToBack6 = mNotesFragment;
                whichToBack7 = mAllNotesFragment;
                whichToBack8 = mAllHomeworkFragment;
                whichToBack9 = mAnswerBookChooseFragment;
                whichToBack10 = mTaskFragment;
                break;
            case ALL_HOMEWORK_FRAGMENT:
                //全部作业

                whichToFront = mAllHomeworkFragment;
                whichToBack1 = mTextBookFragment;
                whichToBack2 = mAllTextBookFragment;
                whichToBack3 = mCoachBookFragment;
                whichToBack4 = mAllCoachBookFragment;
                whichToBack5 = mReferenceBooksFragment;
                whichToBack6 = mNotesFragment;
                whichToBack7 = mAllNotesFragment;
                whichToBack8 = mHomeworkFragment;
                whichToBack9 = mAnswerBookChooseFragment;
                whichToBack10 = mTaskFragment;
                break;

            case ANSWER_FRAGMENT:
                //问答
                whichToFront = mAnswerBookChooseFragment;
                whichToBack1 = mTextBookFragment;
                whichToBack2 = mAllTextBookFragment;
                whichToBack3 = mCoachBookFragment;
                whichToBack4 = mAllCoachBookFragment;
                whichToBack5 = mReferenceBooksFragment;
                whichToBack6 = mNotesFragment;
                whichToBack7 = mAllNotesFragment;
                whichToBack8 = mHomeworkFragment;
                whichToBack9 = mAllHomeworkFragment;
                whichToBack10 = mTaskFragment;
                break;
            case TASK_FRAGMENT:
                whichToFront = mTaskFragment;
                whichToBack1 = mTextBookFragment;
                whichToBack2 = mAllTextBookFragment;
                whichToBack3 = mCoachBookFragment;
                whichToBack4 = mAllCoachBookFragment;
                whichToBack5 = mReferenceBooksFragment;
                whichToBack6 = mNotesFragment;
                whichToBack7 = mAllNotesFragment;
                whichToBack8 = mHomeworkFragment;
                whichToBack9 = mAllHomeworkFragment;
                whichToBack10 = mAnswerBookChooseFragment;
                break;
            default:
                break;
        }

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .show(whichToFront)
                .hide(whichToBack1)
                .hide(whichToBack2)
                .hide(whichToBack3)
                .hide(whichToBack4)
                .hide(whichToBack5)
                .hide(whichToBack6)
                .hide(whichToBack7)
                .hide(whichToBack8)
                .hide(whichToBack9)
                .hide(whichToBack10)
                .commitAllowingStateLoss();


        //设置右边侧边栏条目内容
        refreshRightBtnState();
    }

    /**
     * 刷新侧边栏目
     */
    private void refreshRightBtnState() {
        mBtnCurrentBook.setText("");
        mBtnCurrentBook.setVisibility(View.VISIBLE);
        mBtnCurrentBook.setSelected(false);
        mBtnCurrentBook.setEnabled(true);

        mBtnAllBook.setText("");
        mBtnAllBook.setVisibility(View.VISIBLE);
        mBtnAllBook.setSelected(false);
        mBtnAllBook.setEnabled(true);

        mBtnSearChBook.setVisibility(View.GONE);
        mBtnSearChBook.setSelected(false);

        mBtnCurrentBook.setVisibility(View.VISIBLE);
        mBtnAllBook.setVisibility(View.VISIBLE);

        switch (mDisplayOption) {
            case TEXT_BOOK_FRAGMENT:
                mBtnCurrentBook.setText("本学期课本");
                mBtnAllBook.setText("全部课本");
                mBtnCurrentBook.setSelected(true);
                mBtnCurrentBook.setEnabled(false);
                break;

            case ALL_TEXT_BOOK_FRAGMENT:
                mBtnCurrentBook.setText("本学期课本");
                mBtnAllBook.setText("全部课本");
                mBtnAllBook.setSelected(true);
                mBtnAllBook.setEnabled(false);
                break;

            case COACH_BOOK_FRAGMENT:
                mBtnCurrentBook.setText("本学期辅导书");
                mBtnAllBook.setText("全部辅导书");
                mBtnCurrentBook.setSelected(true);
                mBtnCurrentBook.setEnabled(false);
                break;

            case ALL_COACH_BOOK_FRAGMENT:
                mBtnCurrentBook.setText("本学期辅导书");
                mBtnAllBook.setText("全部辅导书");
                mBtnAllBook.setSelected(true);
                mBtnAllBook.setEnabled(false);
                break;

            case REFERENCE_BOOKS_FRAGMENT:
                mBtnCurrentBook.setVisibility(View.GONE);
                mBtnAllBook.setVisibility(View.GONE);
                mBtnSearChBook.setVisibility(View.VISIBLE);
                mBtnSearChBook.setSelected(true);
                break;

            case NOTES_FRAGMENT:
                mBtnCurrentBook.setText("本学期笔记");
                mBtnAllBook.setText("全部笔记");
                mBtnCurrentBook.setSelected(true);
                mBtnCurrentBook.setEnabled(false);
                break;
            case ALL_NOTES_FRAGMENT:
                mBtnCurrentBook.setText("本学期笔记");
                mBtnAllBook.setText("全部笔记");
                mBtnAllBook.setSelected(true);
                mBtnAllBook.setEnabled(false);
                break;
            case HOMEWORK_FRAGMENT:
                mBtnCurrentBook.setText("本学期作业");
                mBtnAllBook.setText("全部作业");
                mBtnCurrentBook.setSelected(true);
                mBtnCurrentBook.setEnabled(false);
                break;
            case ALL_HOMEWORK_FRAGMENT:
                mBtnCurrentBook.setText("本学期作业");
                mBtnAllBook.setText("全部作业");
                mBtnAllBook.setSelected(true);
                mBtnAllBook.setEnabled(false);
                break;
            case ANSWER_FRAGMENT:
                mBtnCurrentBook.setVisibility(View.GONE);
                mBtnAllBook.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 初始化10个Fragment
     */
    private void initFragment() {
        //课本
        mTextBookFragment = new TextBookFragment();
        mAllTextBookFragment = new AllTextBookFragment();
        //辅导书
        mCoachBookFragment = new CoachBookFragment();
        mAllCoachBookFragment = new AllCoachBookFragment();
        //课外书
        mReferenceBooksFragment = new ReferenceBooksFragment();
        // 笔记
        mNotesFragment = new NotesFragment();
        mAllNotesFragment = new AllNotesFragment();
        mTaskFragment = new TaskFragment();

        //作业
        mHomeworkFragment = new HomeworkFragment();
        mAllHomeworkFragment = new AllHomeworkFragment();
        //问答
        mAnswerBookChooseFragment = new AnswerBookChooseFragment();
        android.support.v4.app.FragmentManager mChildFragmentManager = getSupportFragmentManager();
        mChildFragmentManager.beginTransaction()
                /***
                 *  替换 布局到fragment
                 */
                // 课本
                .add(R.id.fl_content_layout, mTextBookFragment).add(R.id.fl_content_layout, mAllTextBookFragment)
                // 辅导书
                .add(R.id.fl_content_layout, mCoachBookFragment).add(R.id.fl_content_layout, mAllCoachBookFragment)
                //课外书
                .add(R.id.fl_content_layout, mReferenceBooksFragment)
                //笔记
                .add(R.id.fl_content_layout, mNotesFragment).add(R.id.fl_content_layout, mAllNotesFragment)
                //作业
                .add(R.id.fl_content_layout, mHomeworkFragment).add(R.id.fl_content_layout, mAllHomeworkFragment)
                //问答
                .add(R.id.fl_content_layout, mAnswerBookChooseFragment)
                //任务
                .add(R.id.fl_content_layout,mTaskFragment)

                /***
                 * hide 全部fragment
                 */
                // 课本
                .hide(mTextBookFragment).hide(mAllTextBookFragment)
                // 辅导书
                .hide(mCoachBookFragment).hide(mAllCoachBookFragment)
                .hide(mReferenceBooksFragment)
                //笔记
                .hide(mNotesFragment).hide(mAllNotesFragment)
                //作业
                .hide(mHomeworkFragment).hide(mAllHomeworkFragment)
                //问答
                .hide(mAnswerBookChooseFragment)
                //任务
                .hide(mTaskFragment)
                //提交事务
                .commitAllowingStateLoss();

        /**
         * 保存到全局的位置 为了删除 和修改 笔记使用
         */
    }

    /**
     * 设置 title分类 按下是否显示下划线 已经颜色的变化
     */
    private void refreshTabBtnState(int clickedViewId) {

//        mRlTextBook = (ViewGroup) findViewById(R.id.rl_text_book);
//        mRlTextBook.setOnClickListener(this);

        boolean isCoachBookFragment = clickedViewId == R.id.tv_coach_book;
        boolean isHomeworkFragment = clickedViewId == R.id.tv_homework;
        boolean isAnswerFragment = clickedViewId == R.id.tv_answer;
        boolean isNotesFragment = clickedViewId == R.id.tv_notes;
        boolean isReferenceBooksFragment = clickedViewId == R.id.tv_reference_books;
        boolean isTextBookFragment = clickedViewId == R.id.tv_text_book;

        int hideView = View.INVISIBLE;
        int showView = View.VISIBLE;

        //isCoachBookFragment
        mTvCoachBook.setSelected(isCoachBookFragment);
//        mViewCoachBook.setVisibility(isCoachBookFragment == true ? showView : hideView);
        //isAnswerFragment
        mTvAnswer.setSelected(isAnswerFragment);
//        mViewAnswer.setVisibility(isAnswerFragment == true ? showView : hideView);
        //isHomeworkFragment
        mTvHomework.setSelected(isHomeworkFragment);
//        mViewHomework.setVisibility(isHomeworkFragment == true ? showView : hideView);
        //isNotesFragment
        mTvNotes.setSelected(isNotesFragment);
//        mViewNotes.setVisibility(isNotesFragment == true ? showView : hideView);
        //isReferenceBooksFragment
        mTvReferenceBooks.setSelected(isReferenceBooksFragment);
//        mViewReferenceBooks.setVisibility(isReferenceBooksFragment == true ? showView : hideView);
        //isTextBookFragment
        mTvTextBook.setSelected(isTextBookFragment);
//        mViewTextBook.setVisibility(isTextBookFragment == true ? showView : hideView);
    }

    public enum FragmentDisplayOption {
        /**
         * 课本
         */
        TEXT_BOOK_FRAGMENT,
        /**
         * 全部课本
         */
        ALL_TEXT_BOOK_FRAGMENT,
        /**
         * 辅导书
         */
        COACH_BOOK_FRAGMENT,
        /**
         * 全部辅导书
         */
        ALL_COACH_BOOK_FRAGMENT,
        /**
         * 课外书
         */
        REFERENCE_BOOKS_FRAGMENT,
        /**
         * 笔记
         */
        NOTES_FRAGMENT,
        /**
         * 全部笔记
         */
        ALL_NOTES_FRAGMENT,
        /**
         * 作业
         */
        HOMEWORK_FRAGMENT,
        /**
         * 全部作业
         */
        ALL_HOMEWORK_FRAGMENT,
        /**
         * 问答
         */
        ANSWER_FRAGMENT,
        /**
         * 任务
         */
        TASK_FRAGMENT
    }

//    key是 "close_wifi_delay",  永不关闭传值:-1
    public static boolean changeSystemConfigIntegerValue(Context context, String dataKey, int value) {
        try {
            return Settings.System.putInt(context.getContentResolver(), dataKey, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!NetUtils.isNetConnected()) {
            if (NetUtils.isNetAvailable()) {
                NetManager.getInstance().changeWiFi(this, true);
            }
        }

//        changeSystemConfigIntegerValue(this,"close_wifi_delay" ,-1) ;




        if (!isStScreensaver) {
            ThreadManager.getSinglePool().execute(new Runnable() {
                @Override
                public void run() {
                    DeviceScreensaverUtils.setScreensaver();
//                    DeviceScreensaverUtils.setDeviceBg();
                    isStScreensaver = true;
                    FileUtils.writeProperties(FileUtils.getSDCardPath() + "leke_init", FileContonst.LOAD_APP_STUDENT + "," + SpUtils.getVersion());
                }
            });
        }
        initSysIcon();
        if (SpUtils.getOrder().contains(LOCK_SCREEN) && SpUtils.getOrder().contains(DateUtils.getCalendarString())) {
            Intent newIntent = new Intent(getApplicationContext(), LockerActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(newIntent);
        }
    }

    private void setSysWifi() {
        if (NetManager.getInstance().isWifiConnected(this)) {
            int level = NetManager.getInstance().getConnectionInfoRssi(this);
            // 这个方法。得到的值是一个0到-100的区间值，是一个int型数据，其中0到-50表示信号最好，-50到-70表示信号偏差，
            //小于-70表示最差，有可能连接不上或者掉线，一般Wifi已断则值为-200。
            if (level <= 0 && level >= -50) {
                mImgWSysWifi.setImageDrawable(UIUtils.getDrawable(R.drawable.img_wifi_0));
            } else if (level < -50 && level >= -70) {
                mImgWSysWifi.setImageDrawable(UIUtils.getDrawable(R.drawable.img_wifi_4));
            } else if (level < -70 && level >= -80) {
                mImgWSysWifi.setImageDrawable(UIUtils.getDrawable(R.drawable.img_wifi_3));
            } else if (level < -80 && level >= -100) {
                mImgWSysWifi.setImageDrawable(UIUtils.getDrawable(R.drawable.img_wifi_2));
            }
            if (YoungyApplicationManager.isWifiAvailable() && SpUtils.isContentChanged()) {
                LogUtils.e(tag, "setSysWifi upload................");
                startService(new Intent(this, UploadService.class));
            }
        } else {
            mImgWSysWifi.setImageDrawable(UIUtils.getDrawable(R.drawable.img_wifi_1));
        }
    }

    private void setSysTime() {
        mTvSysTime.setText(DateUtils.getTimeHHMMString());

    }

    private void initSysIcon() {
        setSysWifi();
        setSysTime();
    }

    private void setSysPower(int level, int state) {

        mTvSysPower.setText(level + "%");
        if (level == 0) {
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING ? R.drawable.ic_battery_charge_0_black_03 : R.drawable.ic_battery_0_black_03));

        } else if (level > 0 && level <= 10) {
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING ? R.drawable.ic_battery_charge_10_black_03 : R.drawable.ic_battery_10_black_03));
        } else if (level > 10 && level <= 20) {
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING ? R.drawable.ic_battery_charge_20_black_03 : R.drawable.ic_battery_20_black_03));
        } else if (level > 20 && level <= 30) {
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING ? R.drawable.ic_battery_charge_30_black_03 : R.drawable.ic_battery_30_black_03));
        } else if (level > 30 && level <= 40) {
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING ? R.drawable.ic_battery_charge_40_black_03 : R.drawable.ic_battery_40_black_03));
        } else if (level > 40 && level <= 50) {
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING ? R.drawable.ic_battery_charge_50_black_03 : R.drawable.ic_battery_50_black_03));
        } else if (level > 50 && level <= 60) {
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING ? R.drawable.ic_battery_charge_60_black_03 : R.drawable.ic_battery_60_black_03));
        } else if (level > 60 && level <= 70) {
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING ? R.drawable.ic_battery_charge_70_black_03 : R.drawable.ic_battery_70_black_03));
        } else if (level > 70 && level <= 80) {
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING ? R.drawable.ic_battery_charge_80_black_03 : R.drawable.ic_battery_80_black_03));
        } else if (level > 80 && level < 100) {
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING ? R.drawable.ic_battery_charge_90_black_03 : R.drawable.ic_battery_90_black_03));
        } else if (level == 100) {
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(R.drawable.ic_battery_100_black_03));
        }

    }


    @Override
    protected void setContentView() {
        mRootView = UIUtils.inflate(R.layout.activity_main_ui);
        setContentView(mRootView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            removeFragments();
        }
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        if (event == null)
            return;

        setSysTime();
        if (EventBusConstant.EVENT_WIIF.equals(event.getType())) {
            LogUtils.i("event ...wiif");
            LogUtils.i("event...ressa..." + NetManager.getInstance().getConnectionInfoRssi(UIUtils.getContext()));
            LogUtils.i("event...isWifiConnected..." + NetManager.getInstance().isWifiConnected(UIUtils.getContext()));
            setSysWifi();
        } else if (EventBusConstant.EVENTBUS_POWER.equals(event.getType())) {
            LogUtils.i("event ...power");
//            LogUtils.i("event...lever..." + PowerManager.getInstance().getlevelPercent());
//            LogUtils.i("event...status..." + PowerManager.getInstance().getBatteryStatus());
            setSysPower(PowerManager.getInstance().getlevelPercent(), PowerManager.getInstance().getBatteryStatus());
        } else if (EventBusConstant.need_refresh.equalsIgnoreCase(event.getType())) {
            postEvent();
        }
    }

    @Override
    public void onUiDetermineListener() {
        super.onUiDetermineListener();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }


    //=================================升级相关代码 开始=============================================================
    private int lastProgress;

    //升级接口 m=getAppVersion&id=student   用id来判断学生端、教师端 http://ocghxr9lf.bkt.clouddn.com/sample-debug.apk
    private void getServerVersion() {
        NetWorkManager.getVersion()
                .compose(bindToLifecycle())
                .subscribe(version -> {
                    int serverVersion = TextUtils.isEmpty(version.getAppVersion()) ? -1 : Integer.parseInt(version.getAppVersion());
                    int localVersion = VersionUtils.getVersionCode(MainActivity.this);
                    LogUtils.i("袁野 localVersion ==" + localVersion);
                    final String url = version.getAppUrl();
                    if (serverVersion > localVersion && !TextUtils.isEmpty(url)) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mAppUpdateDialog == null) {
                                    mAppUpdateDialog = new AppUpdateDialog(getThisActivity());
                                    mAppUpdateDialog.setCliclListener(new AppUpdateDialog.AppUpdateClicklListener() {
                                        @Override
                                        public void cancelListener() {
                                            mAppUpdateDialog.dismiss();
                                        }

                                        @Override
                                        public void confirmListener() {
                                            SpUtils.setVersion("" + serverVersion);
                                            FileUtils.writeProperties(FileUtils.getSDCardPath() + "leke_init", FileContonst.LOAD_APP_STUDENT + "," + SpUtils.getVersion());
                                            mAppUpdateDialog.dismiss();
                                            doDownLoad(MainActivity.this, url);
                                        }
                                    });
                                }
                                mAppUpdateDialog.show();
                                mAppUpdateDialog.setContent(version.getUpdaMsg().replaceAll("#", "\n"));
                                int forceVersion = TextUtils.isEmpty(version.getForceVersion()) ? -1 : Integer.parseInt(version.getForceVersion());
                                if (localVersion < forceVersion) {
                                    mAppUpdateDialog.isShowcCancel(false);
                                }
                            }
                        });
                    } else {
                        ToastUtil.showCustomToast(MainActivity.this, "当前版本 : " + VersionUtils.getVersionName() + " ,已经是最新版本了");
                    }
                }, throwable -> {
                    ToastUtil.showCustomToast(MainActivity.this, "检测版本失败，请稍后重试");
                });

    }

    private void doDownLoad(final Context mContext, final String downloadUrl) {
        final DownProgressDialog downProgressDialog = new DownProgressDialog(mContext);

//        downProgressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        downProgressDialog.show();
        downProgressDialog.setDownProgress("0%");


        // 删除下载的apk文件
        doDeleteDownApk();
        DownloadManager.getInstance().cancelAll();
        DownloadManager.downloadId = DownloadManager.getInstance().add(DownloadManager.getDownLoadRequest(mContext, downloadUrl, new DownloadStatusListenerV1() {
            @Override
            public void onDownloadComplete(DownloadRequest downloadRequest) {
                LogUtils.e("FH", "下载完成,开始安装");
                // 更新进度条显示
                downProgressDialog.setDownProgress("100%");
                downProgressDialog.dismiss();

                // 下载完成，执行安装逻辑
                doInstallApk(mContext);
                // 退出App
                finishAll();
            }

            @Override
            public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {
                downProgressDialog.setDownProgress("更新失败，重新更新下载");
                LogUtils.e("FH", "更新失败: errorCode=" + errorCode + " errorMsg=" + errorMessage);
                // TODO: 2017/4/25
                downProgressDialog.dismiss();
                doDownLoad(mContext, downloadUrl);
            }

            @Override
            public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {
                if (lastProgress != progress) {
                    LogUtils.e("FH", "下载进度变化------" + progress + "%");
                    lastProgress = progress;
                    String content = downloadedBytes * 100 / totalBytes + "%";
                    downProgressDialog.setDownProgress(content);
                }
            }
        }));
    }

    /**
     * 删除下载的apk文件
     */
    private static void doDeleteDownApk() {
        File file = new File(DownloadManager.getApkPath());
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 执行安装apk文件
     */
    private static void doInstallApk(Context mContext) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(DownloadManager.getApkPath())),
                "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    //=================================升级相关代码 结束=============================================================

    private static class CheckRunnable implements Runnable{
        MainActivity activity;
        public CheckRunnable setActivity(MainActivity activity) {
            this.activity = activity;
            return this;
        }

        @Override
        public void run() {
            if (!NetUtils.isNetConnected()) {
                LogUtils.e("FH", "主界面onResume验证是否被解绑-------结果:没有连接网络,尝试打开网络,并在30秒之后再次检查");
                NetManager.getInstance().changeWiFi(YoungyApplicationManager.getInstance(), true);
                YoungyApplicationManager.getMainThreadHandler().removeCallbacks(this);
                YoungyApplicationManager.getMainThreadHandler().postDelayed(this, 30000);
            } else {
                NewLoginReq loginReq = new NewLoginReq();
                loginReq.setDeviceId(Commons.UUID);
                NetWorkManager.getInstance(true).login(loginReq)
                        .subscribe(students -> {
                            LogUtils.e("FH", "主界面onResume验证是否被解绑-------结果:没有被解绑,走正常流程");
                            if (!activity.initializationNeedNetFinished){
                                downloadDb();
                                getUnreadMsg();
                                activity.initializationNeedNetFinished = true;
                            }
                            lastCheckTimeMill = System.currentTimeMillis();
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                                if (throwable instanceof ApiException
                                        && ("401".equals(((ApiException) throwable).getCode()))) {
                                    LogUtils.e("FH", "主界面onResume验证是否被解绑-------结果:可能被解绑了,先logout后跳转到login界面");
                                    ToastUtil.showCustomToast(activity, "您的设备可能已经被解绑了,请重新登录绑定设备!");
                                    //登录失败,可能是在pc端被解绑了.
                                    //删除本端的缓存数据并且跳转到login界面
                                    SpUtils.clearSP();
                                    SpUtils.changeInitFlag(false);
                                    Connector.resetHelper();
                                    activity.deleteDatabase(DATABASE_NAME);
                                    activity.deleteDatabase(JOURNAL_NAME);
                                    FileUtils.writeProperties(FileUtils.getSDCardPath() + "leke_init", FileContonst.LOAD_APP_RESET + "," + SpUtils.getVersion());
                                    YXClient.getInstance().logout();
                                    ThreadManager.getSinglePool().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            DeviceScreensaverUtils.setScreensaver();
                                        }
                                    });
                                    activity.startActivity(new Intent(activity , LoginActivity.class));
                                    activity.finish();
                                } else {
                                    LogUtils.e("FH", "主界面onResume验证是否被解绑-------结果:未知,查询是否解绑接口调不通,尝试进行初始化,是否解绑延期至下次进入主界面时做");
                                    if (!activity.initializationNeedNetFinished){
                                        downloadDb();
                                        getUnreadMsg();
                                        activity.initializationNeedNetFinished = true;
                                    }
                                }
                            }
                        });
            }
        }

        public void downloadDb(){
            File file = new File(activity.getDatabasePath(SpUtils.getUserId() + ".db").getAbsolutePath());
            if (!file.exists()) {
                activity.startService(new Intent(activity, DownloadService.class));
            }
        }
        public void getUnreadMsg(){
            YXClient.getInstance().checkIfNotLoginThenDoIt(activity, new RequestCallback() {
                @Override
                public void onSuccess(Object param) {
                    new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] params) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            int totalUnreadMsgCount = 0;
                            for (RecentContact recentContact : YXClient.getInstance().getRecentContactList()) {
                                totalUnreadMsgCount += YXClient.getUnreadMsgCount(recentContact.getContactId(), recentContact.getSessionType());
                            }
                            if (totalUnreadMsgCount != 0) {
                                activity.mIvMsg.setVisibility(View.VISIBLE);
//                            unreadMsgCountTextview1.setText("" + totalUnreadMsgCount);
                            } else {
                                activity.mIvMsg.setVisibility(View.GONE);
                            }
                        }
                    }.execute((Object[]) null);
                }

                @Override
                public void onFailed(int code) {
                    ToastUtil.showCustomToast(activity , "连接消息服务器失败!");
                }

                @Override
                public void onException(Throwable exception) {

                }
            });
        }
    }

    @Override
    public void finish() {
        super.finish();
        YoungyApplicationManager.getMainThreadHandler().removeCallbacks(checkRunnable);
        checkRunnable.activity = null;
    }


}

