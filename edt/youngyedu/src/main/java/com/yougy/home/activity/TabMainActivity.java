package com.yougy.home.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yougy.common.activity.BaseActivity;
import com.yougy.common.global.Commons;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.request.AppendNotesRequest;
import com.yougy.common.protocol.request.UpdateNotesRequest;
import com.yougy.common.protocol.response.AppendNotesProtocol;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.adapter.MainPagerAdapter;
import com.yougy.home.bean.DataNoteBean;
import com.yougy.home.bean.NoteInfo;
import com.yougy.home.imple.RefreshBooksListener;
import com.yougy.home.imple.SearchReferenceBooksListener;
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

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

/**
 * Created by jiangliang on 2017/2/6.
 */

public class TabMainActivity extends BaseActivity {


    @BindView(R.id.main_tablayout)
    TabLayout mTabLayout;
    @BindView(R.id.main_container)
    RelativeLayout mContainer;
    @BindView(R.id.imgBtn_showRight)
    ImageButton mImgBtnShowRight;
    @BindView(R.id.rl_title)
    RelativeLayout mRlTitle;
    @BindView(R.id.tv_className)
    TextView mTvClassName;
    @BindView(R.id.tv_userName)
    TextView mTvUserName;
    @BindView(R.id.btn_currentBook)
    Button mBtnCurrentBook;
    @BindView(R.id.btn_allBook)
    Button mBtnAllBook;
    @BindView(R.id.btn_serchBook)
    Button mBtnSerchBook;
    @BindView(R.id.btn_bookStore)
    Button mBtnBookStore;
    @BindView(R.id.btn_msg)
    Button mBtnMsg;
    @BindView(R.id.btn_account)
    Button mBtnAccount;
    @BindView(R.id.btn_refresh)
    Button mBtnRefresh;
    @BindView(R.id.fl_right)
    FrameLayout mFlRight;

    private MainPagerAdapter mAdapter;

    @BindArray(R.array.titles)
    String[] mTitles;

    private Subscription mSub;
    private List<NoteInfo> mAddInfos;
    private List<NoteInfo> mUpDataInfos;

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
    protected void init() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.tab_main_layout);
    }

    @Override
    protected void initLayout() {

        ButterKnife.bind(this);
        mTvClassName.setText(SpUtil.getAccountClass());
        mTvUserName.setText(SpUtil.getAccountName());
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment fragment = (Fragment) mAdapter.instantiateItem(mContainer, position);
                mAdapter.setPrimaryItem(mContainer, position, fragment);
                mAdapter.finishUpdate(mContainer);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        initTab();

        if (NetUtils.isNetConnected()) {
            mSub = getObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(getSubscriber());
        }
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void refreshView() {

    }

    private void initTab() {
        for (int i = 0; i < 6; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.tab_title_layout, null);
            TextView textView = (TextView) view.findViewById(R.id.textview);
            textView.setText(mTitles[i]);
            TabLayout.Tab tab = mTabLayout.newTab();
            tab.setCustomView(textView);
            mTabLayout.addTab(tab, i == 0);
        }
    }


    @OnClick({R.id.imgBtn_showRight, R.id.btn_currentBook, R.id.btn_allBook, R.id.btn_serchBook, R.id.btn_bookStore, R.id.btn_msg, R.id.btn_account, R.id.btn_refresh, R.id.fl_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtn_showRight:
                mFlRight.setVisibility(View.VISIBLE);
                break;
            case R.id.fl_right:
                mFlRight.setVisibility(View.GONE);
                break;
            case R.id.btn_currentBook:
                break;
            case R.id.btn_allBook:
                break;
            case R.id.btn_serchBook:
                mFlRight.setVisibility(View.GONE);
                if (mSearchListener != null) {
                    mSearchListener.onSearchClickListener();
                }
                break;
            case R.id.btn_bookStore:
                break;
            case R.id.btn_msg:
                break;
            case R.id.btn_account:
                if (NetUtils.isNetConnected()) {
                    loadIntent(AccountSetActivity.class);
                } else {
                    UIUtils.showToastSafe(R.string.net_not_connection, Toast.LENGTH_SHORT);
                }
                break;
            case R.id.btn_refresh:
                if (NetUtils.isNetConnected()) {
                    if (mRefreshListener != null) {
                        mRefreshListener.onRefreshClickListener();
                    }
                } else {
                    UIUtils.showToastSafe(R.string.net_not_connection, Toast.LENGTH_SHORT);
                }
                break;
        }
    }

    private Observable<Object> getObservable() {
        LogUtils.i("yuanye 111111111");
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                LogUtils.i("yuanye 000000000");
                List<NoteInfo> infos = DataSupport.findAll(NoteInfo.class);
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

    // 上传 离线添加笔记 列表
    private void addRequestNotes() {
        AppendNotesRequest request = new AppendNotesRequest();
        request.setUserId(SpUtil.getAccountId());
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
        request.setUserId(SpUtil.getAccountId());
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

    private Subscriber<Object> getSubscriber() {
        return new Subscriber<Object>() {
            LoadingProgressDialog dialog;

            @Override
            public void onStart() {
                super.onStart();
                dialog = new LoadingProgressDialog(TabMainActivity.this);
                dialog.show();
                dialog.setTitle("请求...");
            }

            @Override
            public void onCompleted() {
                LogUtils.e(TAG, "onCompleted...");
                dialog.dismiss();
//                mRlTextBook.callOnClick();
            }

            @Override
            public void onError(Throwable e) {
                dialog.dismiss();
//                mRlTextBook.callOnClick();
            }

            @Override
            public void onNext(Object o) {
            }
        };
    }

}
