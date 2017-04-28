package com.yougy.home.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.BatteryManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.global.Commons;
import com.yougy.common.manager.NetManager;
import com.yougy.common.manager.PowerManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.request.AppendNotesRequest;
import com.yougy.common.protocol.request.UpdateNotesRequest;
import com.yougy.common.protocol.response.AppendNotesProtocol;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.UploadService;
import com.yougy.home.bean.DataNoteBean;
import com.yougy.home.bean.NoteInfo;
import com.yougy.home.fragment.GlobeFragment;
import com.yougy.home.fragment.mainFragment.AllCoachBookFragment;
import com.yougy.home.fragment.mainFragment.AllHomeworkFragment;
import com.yougy.home.fragment.mainFragment.AllNotesFragment;
import com.yougy.home.fragment.mainFragment.AllTextBookFragment;
import com.yougy.home.fragment.mainFragment.CoachBookFragment;
import com.yougy.home.fragment.mainFragment.FolderFragment;
import com.yougy.home.fragment.mainFragment.HomeworkFragment;
import com.yougy.home.fragment.mainFragment.NotesFragment;
import com.yougy.home.fragment.mainFragment.ReferenceBooksFragment;
import com.yougy.home.fragment.mainFragment.TextBookFragment;
import com.yougy.home.imple.RefreshBooksListener;
import com.yougy.home.imple.SearchReferenceBooksListener;
import com.yougy.shop.activity.BookShopActivityDB;
import com.yougy.ui.activity.R;
import com.yougy.view.dialog.LoadingProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostStringBuilder;
import com.zhy.http.okhttp.request.RequestCall;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import okhttp3.MediaType;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.onyx.android.sdk.utils.DeviceUtils.getBatteryPecentLevel;


/**
 * V1
 * Created by Administrator on 2016/8/24.
 * <p/>
 * 显示 文件夹
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
    private ViewGroup mRlFolder;
    private ViewGroup mRHomework;
    private ViewGroup mRlNotes;
    private ViewGroup mRlReferenceBooks;
    private ViewGroup mRlCoachBook;
    private ViewGroup mRlTextBook;
    private CoachBookFragment mCoachBookFragment;
    private HomeworkFragment mHomeworkFragment;
    private FolderFragment mFolderFragment;
    private NotesFragment mNotesFragment;
    private ReferenceBooksFragment mReferenceBooksFragment;
    private TextBookFragment mTextBookFragment;


    private AllTextBookFragment mAllTextBookFragment;
    private AllCoachBookFragment mAllCoachBookFragment;
    private AllNotesFragment mAllNotesFragment;
    private AllHomeworkFragment mAllHomeworkFragment;

    private TextView mTvFolder;
    private View mViewFolder;
    private TextView mTvHomework;
    private View mViewHomework;
    private TextView mTvNotes;
    private View mViewNotes;
    private TextView mTvReferenceBooks;
    private View mViewReferenceBooks;
    private TextView mTvCoachBook;
    private View mViewCoachBook;
    private TextView mTvTextBook;
    private View mViewTextBook;
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
    private List<NoteInfo> mAddInfos;
    private List<NoteInfo> mUpDataInfos;
    private Subscription mSub;
    private ImageView mImgWSysWifi;
    private ImageView mImgWSysPower;
    private TextView mTvSysPower;
    private TextView mTvSysTime;
    private Button mBtnSysSeeting;

//    private ImageButton mImgBtnRefresh;

    /***************************************************************************/

    @Override
    protected void init() {
        setPressTwiceToExit(true);
    }

    private void removeFragments() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(mCoachBookFragment);
        fragmentTransaction.remove(mHomeworkFragment);
        fragmentTransaction.remove(mFolderFragment);
        fragmentTransaction.remove(mNotesFragment);
        fragmentTransaction.remove(mReferenceBooksFragment);
        fragmentTransaction.remove(mTextBookFragment);
        fragmentTransaction.remove(mAllTextBookFragment);
        fragmentTransaction.remove(mAllCoachBookFragment);
        fragmentTransaction.remove(mAllNotesFragment);
        fragmentTransaction.remove(mAllHomeworkFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        NetManager.getInstance().unregisterReceiver(this);
        PowerManager.getInstance().unregisterReceiver(this);
        GlobeFragment.getInstance().mAllNotes = null;
        GlobeFragment.getInstance().mNote = null;
        GlobeFragment.getInstance().mAllBook = null;
        GlobeFragment.getInstance().mTextBook = null;
        Glide.get(this).clearMemory();
        mCoachBookFragment = null;
        mHomeworkFragment = null;
        mFolderFragment = null;
        mNotesFragment = null;
        mReferenceBooksFragment = null;
        mTextBookFragment = null;


        mAllTextBookFragment = null;
        mAllCoachBookFragment = null;
        mAllNotesFragment = null;
        mAllHomeworkFragment = null;

        if (YougyApplicationManager.isWifiAvailable() && SpUtil.isContentChanged()) {
            startService(new Intent(this, UploadService.class));
        }

        if (mSub != null) {
            mSub.unsubscribe();
        }
        if (mRefreshListener != null) {
            mRefreshListener = null;
        }

        if (mSearchListener != null) {
            mSearchListener = null;
        }

        if (serachWeakReference != null) {
            serachWeakReference = null;
        }

        if (refreshWeakReference != null) {
            refreshWeakReference = null;
        }

        super.onDestroy();
    }

    @Override
    protected void initLayout() {

        setContentView(R.layout.activity_main_ui);
        EventBus.getDefault().register(this);
        NetManager.getInstance().registerReceiver(this);
        PowerManager.getInstance().registerReceiver(this);

        getBatteryPecentLevel(this);
        mRlFolder = (ViewGroup) findViewById(R.id.rl_folder);
        mRlFolder.setOnClickListener(this);
        //TODO:文件夹还为实现 ，暂时关闭点击切换Fragment 功能
        mRlFolder.setEnabled(false);
        mTvFolder = (TextView) findViewById(R.id.tv_folder);
        mViewFolder = findViewById(R.id.view_folder);

        mRHomework = (ViewGroup) findViewById(R.id.rl_homework);
        mRHomework.setOnClickListener(this);
        mRHomework.setEnabled(false);

        mTvHomework = (TextView) findViewById(R.id.tv_homework);
        mViewHomework = findViewById(R.id.view_homework);

        mRlNotes = (ViewGroup) findViewById(R.id.rl_notes);
        mRlNotes.setOnClickListener(this);
        mTvNotes = (TextView) findViewById(R.id.tv_notes);
        mViewNotes = findViewById(R.id.view_notes);

        mRlReferenceBooks = (ViewGroup) findViewById(R.id.rl_reference_books);
        mRlReferenceBooks.setOnClickListener(this);
        mTvReferenceBooks = (TextView) findViewById(R.id.tv_reference_books);
        mViewReferenceBooks = findViewById(R.id.view_reference_books);

        mRlCoachBook = (ViewGroup) findViewById(R.id.rl_coach_book);
        mRlCoachBook.setOnClickListener(this);
        mTvCoachBook = (TextView) findViewById(R.id.tv_coach_book);
        mViewCoachBook = findViewById(R.id.view_coach_book);

        mRlTextBook = (ViewGroup) findViewById(R.id.rl_text_book);
        mRlTextBook.setOnClickListener(this);
        mTvTextBook = (TextView) findViewById(R.id.tv_text_book);
        mViewTextBook = findViewById(R.id.view_text_book);

        //有侧边栏显示按钮
        mImgBtnShowRight = (ImageButton) this.findViewById(R.id.imgBtn_showRight);
        mImgBtnShowRight.setOnClickListener(this);
        // 右边侧边栏显示内容
        mFlRight = (FrameLayout) this.findViewById(R.id.fl_right);
        mFlRight.setOnClickListener(this);
        //班级
        mTvClassName = (TextView) this.findViewById(R.id.tv_className);
        mTvClassName.setText(SpUtil.getAccountClass());
        //学生名字
        mTvUserName = (TextView) this.findViewById(R.id.tv_userName);
        mTvUserName.setText(SpUtil.getAccountName());
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
//        mBtnBookStore.setOnClickListener(this);
        //消息中心
        mBtnMsg = (Button) this.findViewById(R.id.btn_msg);
        mBtnMsg.setOnClickListener(this);
        //账号设置
        mBtnAccout = (Button) this.findViewById(R.id.btn_account);
        mBtnAccout.setOnClickListener(this);

        mImgWSysWifi = (ImageView) this.findViewById(R.id.img_wifi);
        mImgWSysWifi.setOnClickListener(this);
        mImgWSysPower = (ImageView) this.findViewById(R.id.img_electricity);
        mTvSysPower = (TextView) this.findViewById(R.id.tv_power);
        mTvSysTime = (TextView) this.findViewById(R.id.tv_time);
        mBtnSysSeeting = (Button) this.findViewById(R.id.btn_sysSeeting);
        mBtnSysSeeting.setOnClickListener(this);
        //初始化fragment
        initFragment();
        GlobeFragment.getInstance().mAllNotes = mAllNotesFragment;
        GlobeFragment.getInstance().mNote = mNotesFragment;
        GlobeFragment.getInstance().mTextBook = mTextBookFragment;
        GlobeFragment.getInstance().mAllBook = mAllTextBookFragment;
        mBtnRefresh = (Button) this.findViewById(R.id.btn_refresh);
        mBtnRefresh.setOnClickListener(this);
        checkUpdateNote();
    }


    @Override
    protected void loadData() {
        LogUtils.i(" screenWidth =" + UIUtils.getScreenWidth());
        LogUtils.i(" getScreenHeight=" + UIUtils.getScreenHeight());
    }

    @Override
    protected void refreshView() {
    }

    @Override
    public void onClick(View v) {
        int clickedViewId = v.getId();
        setSysTime();
        switch (clickedViewId) {
            case R.id.rl_folder:
                refreshTabBtnState(clickedViewId);
                bringFragmentToFrontInner(FragmentDisplayOption.FOLDER_FRAGMENT);
                break;

            case R.id.rl_homework:
//                XSharedPref.putString(this, "loadApp", "student");
                refreshTabBtnState(clickedViewId);
                bringFragmentToFrontInner(mIsAll == true ? FragmentDisplayOption.ALL_HOMEWORK_FRAGMENT : FragmentDisplayOption.HOMEWORK_FRAGMENT);
                break;

            case R.id.rl_notes:
//                XSharedPref.putString(this, "loadApp", "");
                refreshTabBtnState(clickedViewId);
                bringFragmentToFrontInner(mIsAll == true ? FragmentDisplayOption.ALL_NOTES_FRAGMENT : FragmentDisplayOption.NOTES_FRAGMENT);
                break;

            case R.id.rl_reference_books:
                refreshTabBtnState(clickedViewId);
                bringFragmentToFrontInner(FragmentDisplayOption.REFERENCE_BOOKS_FRAGMENT);
                break;

            case R.id.rl_coach_book:
                refreshTabBtnState(clickedViewId);
                bringFragmentToFrontInner(mIsAll == true ? FragmentDisplayOption.ALL_COACH_BOOK_FRAGMENT : FragmentDisplayOption.COACH_BOOK_FRAGMENT);
                break;

            case R.id.rl_text_book:
                refreshTabBtnState(clickedViewId);
                bringFragmentToFrontInner(mIsAll == true ? FragmentDisplayOption.ALL_TEXT_BOOK_FRAGMENT : FragmentDisplayOption.TEXT_BOOK_FRAGMENT);
                break;

            case R.id.imgBtn_showRight:
                mFlRight.setVisibility(View.VISIBLE);
                break;

            case R.id.fl_right:
                mFlRight.setVisibility(View.GONE);
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
                mFlRight.setVisibility(View.GONE);
                if (mSearchListener != null) {
                    mSearchListener.onSearchClickListener();
                }
                break;

            case R.id.btn_bookStore:
                LogUtils.i("书城");
                if (NetUtils.isNetConnected()) {
                    loadIntent(BookShopActivityDB.class);
                } else {
                    UIUtils.showToastSafe(R.string.net_not_connection, Toast.LENGTH_SHORT);
                }
                break;

            case R.id.btn_msg:
                LogUtils.i("消息中心");
                break;

            case R.id.btn_account:
                LogUtils.i("账号设置");

                if (NetUtils.isNetConnected()) {
                    loadIntent(AccountSetActivity.class);
                } else {
                    UIUtils.showToastSafe(R.string.net_not_connection, Toast.LENGTH_SHORT);
                }


                break;
            case R.id.imgBtn_refresh:
    /*            LogUtils.i("刷新列表");
                if (NetUtils.isNetConnected()) {
                    if (mRefreshListener != null) {
                        mRefreshListener.onRefreshClickListener();
                    }
                } else {
                    UIUtils.showToastSafe(R.string.net_not_connection, Toast.LENGTH_SHORT);
                }*/
                break;

            case R.id.btn_refresh:
                LogUtils.i("刷新列表");
                if (NetUtils.isNetConnected()) {
                    if (mRefreshListener != null) {
                        mRefreshListener.onRefreshClickListener();
                    }
                } else {
                    UIUtils.showToastSafe(R.string.net_not_connection, Toast.LENGTH_SHORT);
                }
                break;

            case R.id.img_wifi:
                boolean isConnected = NetManager.getInstance().isWifiConnected(this);
                NetManager.getInstance().changeWiFi(this, !isConnected);
                mImgWSysWifi.setImageDrawable(UIUtils.getDrawable(isConnected == true? R.drawable.img_wifi_1:R.drawable.img_wifi_0));
                break;

            case R.id.btn_sysSeeting:
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.onyx.android.settings","com.onyx.android.libsetting.view.activity.DeviceMainSettingActivity"));
                startActivity(intent);
                break;
        }
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
                whichToBack9 = mFolderFragment;
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
                whichToBack9 = mFolderFragment;
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
                whichToBack9 = mFolderFragment;
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
                whichToBack9 = mFolderFragment;
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
                whichToBack9 = mFolderFragment;
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
                whichToBack9 = mFolderFragment;
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
                whichToBack9 = mFolderFragment;
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
                whichToBack9 = mFolderFragment;
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
                whichToBack9 = mFolderFragment;
                break;

            case FOLDER_FRAGMENT:
                whichToFront = mFolderFragment;
                whichToBack1 = mTextBookFragment;
                whichToBack2 = mAllTextBookFragment;
                whichToBack3 = mCoachBookFragment;
                whichToBack4 = mAllCoachBookFragment;
                whichToBack5 = mReferenceBooksFragment;
                whichToBack6 = mNotesFragment;
                whichToBack7 = mAllNotesFragment;
                whichToBack8 = mHomeworkFragment;
                whichToBack9 = mAllHomeworkFragment;
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
            case FOLDER_FRAGMENT:
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


        //作业
        mHomeworkFragment = new HomeworkFragment();
        mAllHomeworkFragment = new AllHomeworkFragment();
        //文件夹
        mFolderFragment = new FolderFragment();
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
                //文件夹
                .add(R.id.fl_content_layout, mFolderFragment)

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
                //文件夹
                .hide(mFolderFragment)
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

        mRlTextBook = (ViewGroup) findViewById(R.id.rl_text_book);
        mRlTextBook.setOnClickListener(this);

        boolean isCoachBookFragment = clickedViewId == R.id.rl_coach_book;
        boolean isHomeworkFragment = clickedViewId == R.id.rl_homework;
        boolean isFolderFragment = clickedViewId == R.id.rl_folder;
        boolean isNotesFragment = clickedViewId == R.id.rl_notes;
        boolean isReferenceBooksFragment = clickedViewId == R.id.rl_reference_books;
        boolean isTextBookFragment = clickedViewId == R.id.rl_text_book;

        int hideView = View.INVISIBLE;
        int showView = View.VISIBLE;

        //isCoachBookFragment
        mTvCoachBook.setSelected(isCoachBookFragment);
        mViewCoachBook.setVisibility(isCoachBookFragment == true ? showView : hideView);
        //isFolderFragment
        mTvFolder.setSelected(isFolderFragment);
        mViewFolder.setVisibility(isFolderFragment == true ? showView : hideView);
        //isHomeworkFragment
        mTvHomework.setSelected(isHomeworkFragment);
        mViewHomework.setVisibility(isHomeworkFragment == true ? showView : hideView);
        //isNotesFragment
        mTvNotes.setSelected(isNotesFragment);
        mViewNotes.setVisibility(isNotesFragment == true ? showView : hideView);
        //isReferenceBooksFragment
        mTvReferenceBooks.setSelected(isReferenceBooksFragment);
        mViewReferenceBooks.setVisibility(isReferenceBooksFragment == true ? showView : hideView);
        //isTextBookFragment
        mTvTextBook.setSelected(isTextBookFragment);
        mViewTextBook.setVisibility(isTextBookFragment == true ? showView : hideView);
    }

    private enum FragmentDisplayOption {
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
         * 文件夹
         */
        FOLDER_FRAGMENT
    }


    public SearchReferenceBooksListener mSearchListener;
    private WeakReference<SearchReferenceBooksListener> serachWeakReference;


    public void setSearchListener(SearchReferenceBooksListener listener) {
        serachWeakReference = new WeakReference<>(listener);
        mSearchListener = listener;
    }

    public RefreshBooksListener mRefreshListener;
    private WeakReference<RefreshBooksListener> refreshWeakReference;


    public void setRefreshListener(RefreshBooksListener listener) {
        refreshWeakReference = new WeakReference<>(listener);
        mRefreshListener = listener;
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.i("mian....onStart");
        //显示系统的 WIIF ,TIME ,POWER
        initSysIcon();
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
//        setSysPower(DeviceUtils.getBatteryPecentLevel(this), BatteryManager.BATTERY_STATUS_NOT_CHARGING);
    }

    private void setSysPower(int level,int state) {

        mTvSysPower.setText(level + "%");
        if (level== 0){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING?R.drawable.ic_battery_charge_0_black_03:R.drawable.ic_battery_0_black_03  ));

        }else if(level>0 && level<=10 ){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING?R.drawable.ic_battery_charge_10_black_03:R.drawable.ic_battery_10_black_03  ));
        }
        else if(level>10 && level<=20 ){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING?R.drawable.ic_battery_charge_20_black_03:R.drawable.ic_battery_20_black_03  ));
        }  else if(level>20 && level<=30 ){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING?R.drawable.ic_battery_charge_30_black_03:R.drawable.ic_battery_30_black_03  ));
        }
        else if(level>30 && level<=40 ){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING?R.drawable.ic_battery_charge_40_black_03:R.drawable.ic_battery_40_black_03  ));
        }

        else if(level>40 && level<=50 ){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING?R.drawable.ic_battery_charge_50_black_03:R.drawable.ic_battery_50_black_03  ));
        }
        else if(level>50 && level<=60 ){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING?R.drawable.ic_battery_charge_60_black_03:R.drawable.ic_battery_60_black_03  ));
        }
        else if(level>60 && level<=70){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING?R.drawable.ic_battery_charge_70_black_03:R.drawable.ic_battery_70_black_03  ));
        }

        else if(level>70 && level<=80 ){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING?R.drawable.ic_battery_charge_80_black_03:R.drawable.ic_battery_80_black_03  ));
        }

        else if(level>80&& level<100 ){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(state == BatteryManager.BATTERY_STATUS_CHARGING?R.drawable.ic_battery_charge_90_black_03:R.drawable.ic_battery_90_black_03  ));
        }

        else if(level==100){
            mImgWSysPower.setImageDrawable(UIUtils.getDrawable(R.drawable.ic_battery_100_black_03 ));
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            removeFragments();
        }
//        mImgBtnRefresh.setEnabled(false);
    }

    /***
     * 检查离线笔记是否需要同步到服务器
     */
    private void checkUpdateNote() {
        if (NetUtils.isNetConnected()) {
            mSub = getObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(getSubscriber());
        } else {
            //设置当前的课本为第一个显示fragment的
            mRlTextBook.callOnClick();
        }
    }


    private Observable<Object> getObservable() {
        LogUtils.i("yuanye 111111111");
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                LogUtils.i("yuanye 000000000");
                List<NoteInfo> infos = DataSupport.findAll(NoteInfo.class);
//                YougyApplicationManager.closeDb();
                if (infos != null && infos.size() > 0) {
                    mAddInfos = new ArrayList<>();
                    mUpDataInfos = new ArrayList<>();
                    for (NoteInfo info : infos) {
                        if (info.getNoteId() == -1) {
                            mAddInfos.add(info);
                            LogUtils.i("yuanye 2222222");
                        } else {
                            mUpDataInfos.add(info);
                            LogUtils.i("yuanye 3333");
                        }
                    }

                    if (mAddInfos.size() > 0) {
                        // 上传离线创建的笔记
                        LogUtils.i("yuanye 4444");
                        addRequestNotes();
                    } else if (mUpDataInfos.size() > 0) {
                        //更新离线修改的笔记
                        LogUtils.i("yuanye 7777777777");
                        updaRequestNotes();
                    }
                }
                subscriber.onCompleted();
            }
        });
    }


    private Subscriber<Object> getSubscriber() {
        return new Subscriber<Object>() {
            LoadingProgressDialog dialog;

            @Override
            public void onStart() {
                super.onStart();
                dialog = new LoadingProgressDialog(MainActivity.this);
                dialog.show();
                dialog.setTitle("请求...");
            }

            @Override
            public void onCompleted() {
                dialog.dismiss();
                mRlTextBook.callOnClick();
            }

            @Override
            public void onError(Throwable e) {
                dialog.dismiss();
                mRlTextBook.callOnClick();
            }

            @Override
            public void onNext(Object o) {
            }
        };
    }

    // 上传 离线添加笔记 列表
    private void addRequestNotes() {
        AppendNotesRequest request = new AppendNotesRequest();
        request.setUserId(Integer.parseInt(SpUtil.getAccountId()));
        request.setCount(1);
        DataNoteBean bean = new DataNoteBean();
        bean.setCount(mAddInfos.size());
        bean.setNoteList(mAddInfos);
        List<DataNoteBean> data = new ArrayList<>();
        data.add(bean);
        request.setData(data);
        Response response = request(Commons.URL_APPEND_NOTES, GsonUtil.toJson(request), 1);
        if (response != null) {
            try {
                String json = response.body().string();
                LogUtils.i(" 离线添加数据到服务器 返回JSON==" + json);
                AppendNotesProtocol protocol = GsonUtil.fromJson(json, AppendNotesProtocol.class);
                if (protocol.getCode() == ProtocolId.RET_SUCCESS) {
                    //删除数据库
                    DataSupport.deleteAll(NoteInfo.class, "noteId = ?", "-1");
                    //上传修改数据
                    if (mUpDataInfos.size() > 0) {
                        LogUtils.i("yuanye 555555");
                        updaRequestNotes();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 上传 离线 修改 笔记 标题 样式 学科等信息
     */
    private void updaRequestNotes() {
        UpdateNotesRequest request = new UpdateNotesRequest();
        request.setUserId(Integer.parseInt(SpUtil.getAccountId()));
        request.setCount(1);

        DataNoteBean bean = new DataNoteBean();
        bean.setCount(mUpDataInfos.size());
        bean.setNoteList(mUpDataInfos);

        List<DataNoteBean> data = new ArrayList<>();
        data.add(bean);
        request.setData(data);

        Response response = request(Commons.URL_UPDATE_NOTES, GsonUtil.toJson(request), 2);
        if (response != null) {
            try {
                String json = response.body().string();
                LogUtils.i(" 离线 修改数据到服务器 返回JSON==" + json);
                AppendNotesProtocol protocol = GsonUtil.fromJson(json, AppendNotesProtocol.class);
                if (protocol.getCode() == ProtocolId.RET_SUCCESS) {
                    //删除数据库
                    LogUtils.i("yuanye 6666");
                    DataSupport.deleteAll(NoteInfo.class);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private Response request(String url, String json, int id) {

        LogUtils.i("请求地址.......url..." + url);
        LogUtils.i("请求数据.......json..." + json);
        LogUtils.i("请求id.........id..." + id);

        //防止用户的多次请求
        OkHttpUtils.getInstance().cancelTag(url);
        //设置请求String
        PostStringBuilder builder = OkHttpUtils.postString();
        //设置地址
        builder.url(url);
        //设置请求内容
        builder.content(json);
        //设置类型
        builder.mediaType(MediaType.parse("application/json; charset=utf-8"));
        //设置tag,为了 打标志 取消请求
        builder.tag(url);
        //根据协议设置ID在回调函数 统一处理
        builder.id(id);
        RequestCall call = builder.build();
        // 执行请求，
        try {
            return call.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onEventMainThread(BaseEvent event) {
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
            LogUtils.i("event...lever..." + PowerManager.getInstance().getlevelPercent());
            LogUtils.i("event...status..." + PowerManager.getInstance().getBatteryStatus());
            setSysPower(PowerManager.getInstance().getlevelPercent(), PowerManager.getInstance().getBatteryStatus());
        }
    }
}

