package com.yougy.home.fragment.mainFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.yougy.common.fragment.BFragment;
import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.AppendNotesCallBack;
import com.yougy.common.protocol.callback.NoteBookCallBack;
import com.yougy.common.protocol.request.AppendNotesRequest;
import com.yougy.common.protocol.response.QueryNoteProtocol;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.Observable.Observer;
import com.yougy.home.activity.ControlFragmentActivity;
import com.yougy.home.activity.MainActivity;
import com.yougy.home.adapter.NotesAdapter;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.home.bean.CacheJsonInfo;
import com.yougy.home.bean.DataNoteBean;
import com.yougy.home.bean.NoteInfo;
import com.yougy.home.imple.RefreshBooksListener;
import com.yougy.ui.activity.R;
import com.yougy.view.CustomGridLayoutManager;
import com.yougy.view.DividerGridItemDecoration;
import com.yougy.view.dialog.CreatNoteDialog;
import com.yougy.view.dialog.LoadingProgressDialog;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by Administrator on 2016/7/12.
 * 笔记
 */
public class NotesFragment extends BFragment implements View.OnClickListener, Observer {//, BookMarksDialog.DialogClickFinsihListener {

    private static final String TAG = "NotesFragment";
    ////////////////////////////data///////////////////////////////////////

    /**
     * 适配器数据 ，用来刷新集合 。
     */
    private List<NoteInfo> mNotes = new ArrayList<>();
    /**
     * 数据总和用来支配 分页 。
     */
    private List<NoteInfo> mCountInfos = new ArrayList<>();
    /**
     * 服务器 返回数据  。
     */
    private List<NoteInfo> mServerInfos = new ArrayList<>();

    /***
     * 一页数据个数
     */
    private static final int COUNT_PER_PAGE = FileContonst.PAGE_COUNTS;

    /***
     * 当前翻页的角标
     */
    private int mPagerIndex;
    /**
     * 计算有多少个按钮
     */
    private int mCounts;

    /**
     * 添加笔记数据
     */
    private NoteInfo mCreatInfo;

    private int mDelteIndex = 1;

    ///////////////////////////////View////////////////////////////////////
    private ViewGroup mRootView;
    private RecyclerView mRecyclerView;
    private NotesAdapter mNotesAdapter;
    private CreatNoteDialog mNoteDialog;
    private boolean mIsFist;
    private LinearLayout mLlPager;
    private NoteBookCallBack mNoteCallBack;
    private Subscription mSub;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_notes, null);
        initNotes();
        mLlPager = (LinearLayout) mRootView.findViewById(R.id.ll_page);
        return mRootView;
    }

    /**
     * 初始化 书列表
     */
    private void initNotes() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_View);
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(UIUtils.getContext()));
        CustomGridLayoutManager layout = new CustomGridLayoutManager(getActivity(), FileContonst.PAGE_LINES);
        layout.setScrollEnabled(false);
        mRecyclerView.setLayoutManager(layout);
        mNotesAdapter = new NotesAdapter(getActivity(), mNotes);
        mRecyclerView.setAdapter(mNotesAdapter);
        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                noteItemClick(vh.getAdapterPosition());
            }
        });
        notifyDataSetChanged();
    }

    private void notifyDataSetChanged(){
        mNotesAdapter.notifyDataSetChanged();
        EpdController.invalidate(mRootView, UpdateMode.GC);
    }

    private void noteItemClick(int position){
        NoteInfo info = mNotes.get(position);
        if (info.isAddView()) {
            //TODO:弹出框
            if (mNoteDialog == null) {
                mNoteDialog = new CreatNoteDialog(getActivity());
            }
            mNoteDialog.setClickListener(new CreatNoteDialog.NoteFragmentDialogClickListener() {
                @Override
                public void onCreatListener() {
                    //TODO:添加笔记
                    String noteTitle = mNoteDialog.getEditName();
                    String subject = mNoteDialog.getStrSubject();
                    if (StringUtils.isEmpty(subject)) {
                        subject = "无";
                    }

                    if (StringUtils.isEmpty(noteTitle)) {
                        UIUtils.showToastSafe("亲 你还没有 创建笔记 昵称", Toast.LENGTH_SHORT);
                        return;
                    } else {
                        mCreatInfo = new NoteInfo();
                        //设置 笔记名字
                        mCreatInfo.setNoteTitle(noteTitle);
                        //笔记所有者
                        mCreatInfo.setNoteAuthor(Integer.parseInt(SpUtil.getAccountId()));
                        // 笔记创建者
                        mCreatInfo.setNoteCreator(Integer.parseInt(SpUtil.getAccountId()));
                        //设置 笔记类型 ,内部转换对应的int 后期修改
                        mCreatInfo.setNoteStyleOption(mNoteDialog.getNoteOptionStyle());
                        UIUtils.showToastSafe(mCreatInfo.getNoteType() + "", 1);
                        //笔记年级
                        mCreatInfo.setNoteFitGradeName(SpUtil.getGradeName());
                        //笔记学科
                        mCreatInfo.setNoteFitSubjectName(subject);
                        //独立笔记
                        mCreatInfo.setNoteType(2);
                        //根据时间戳设置为内部笔记
                        mCreatInfo.setNoteMark(System.currentTimeMillis());
                        //添加笔记
                        creatNoteInfoProtocol();
                    }
                    mNoteDialog.dismiss();
                }

                @Override
                public void onCancelListener() {
                    if (mNoteDialog.isShowing()) {
                        mNoteDialog.dismiss();
                    }
                }
            });
            mNoteDialog.show();
        } else {
            LogUtils.i("noteinfo ....." + info.toString());
            Bundle extras = new Bundle();
            //课本进入
            extras.putString(FileContonst.JUMP_FRAGMENT, FileContonst.JUMP_NOTE);
            //笔记创建者
            extras.putInt(FileContonst.NOTE_CREATOR, info.getNoteCreator());
            //笔记id
            extras.putInt(FileContonst.NOTE_ID, info.getNoteId());
            //图书id
            extras.putInt(FileContonst.BOOK_ID, info.getBookId());
            //分类码
            extras.putInt(FileContonst.CATEGORY_ID, info.getBookCategory());
            //笔记标题
            extras.putString(FileContonst.NOTE_TITLE, info.getNoteTitle());
            //笔记类型
            extras.putInt(FileContonst.NOTE_Style, info.getNoteStyle());
            //笔记学科
            extras.putString(FileContonst.NOTE_SUBJECT_NAME, info.getNoteFitSubjectName());

            extras.putInt(FileContonst.NOTE_SUBJECT_ID, info.getNoteFitSubjectId());

            extras.putSerializable(FileContonst.NOTE_OBJECT, info);
            //内部ID
            extras.putLong(FileContonst.NOTE_MARK, info.getNoteMark());
            //作业ID
            extras.putInt(FileContonst.HOME_WROK_ID, info.getNoteFitHomeworkId()) ;
            loadIntentWithExtras(ControlFragmentActivity.class, extras);
        }
    }

    /**
     * 创建笔记按钮
     */
    private NoteInfo mAddInfo;

    /***
     * \
     * 创建添加的笔记
     *
     * @return
     */
    private NoteInfo addCreatNoteItem() {
        if (mAddInfo == null) {
            mAddInfo = new NoteInfo();
            mAddInfo.setAddView(true);
        }
        return mAddInfo;
    }

    @Override
    protected void handleEvent() {

        handleNoteBookEvent();
        handleAppendNoteEvent();

        super.handleEvent();
    }

    private void appendNote(int noteId) {
        // 移除+号
        mServerInfos.remove(mServerInfos.size() - 1);
        //设置到生成笔记的id
        //添加到全部集合
        mCreatInfo.setNoteId(noteId);
        mServerInfos.add(mCreatInfo);
        // 添加+号到集合尾部
        mServerInfos.add(addCreatNoteItem());
        //刷新数据 计算分页 以及设置集合数据
        refresh();
        //当前分页大于1 ，需要收到设置到分页尾部。
        if (mCounts != 1) {
            page(mCounts);
        }
        //设置添加了笔记
        FileContonst.globeIsAdd = true;
    }

    private void handleAppendNoteEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof Integer) {
                    int noteId = (int) o;
                    LogUtils.e(TAG,"handleAppendNoteEvent..................");
                    appendNote(noteId);
                }
            }
        }));
    }

    private void handleNoteBookEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof QueryNoteProtocol && !mHide && mNoteCallBack != null) {
                    QueryNoteProtocol data = (QueryNoteProtocol) o;
                    List<NoteInfo> notes = data.getData().get(0).getNoteList();
                    mServerInfos.clear();
                    mServerInfos.addAll(notes);
                    mServerInfos.add(addCreatNoteItem());
                    refresh();
                }else if (o instanceof String && !mHide && StringUtils.isEquals((String) o,ProtocolId.PROTOCOL_ID_NOTE+"")){
                    LogUtils.i("yuanye...请求服务器 加载出错 ---NotesFragment");
                    mSub = getNotesObserver().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(getSubscriber());
                }
            }
        }));
    }

    public void loadIntentWithExtras(Class<? extends Activity> cls, Bundle extras) {
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtras(extras);
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mIsFist = true;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (mIsFist && !hidden && mServerInfos.size() == 0) {
            loadData();
        }
        if (!hidden) {
            LogUtils.i("当前--本学期 笔记");
            setRefreshListener();
        }
    }

    private void setRefreshListener() {
        SearchImple imple = new SearchImple();
        ((MainActivity) getActivity()).setRefreshListener(imple);
    }

    private class SearchImple implements RefreshBooksListener {
        @Override
        public void onRefreshClickListener() {
            loadData();
        }
    }


    private void loadData() {
        getNotes();
    }

    /***
     * 获取服务器笔记列表
     */
    private void getNotes() {
        if (YougyApplicationManager.isWifiAvailable()) {
            mNoteCallBack = new NoteBookCallBack(getActivity(),ProtocolId.PROTOCOL_ID_NOTE);
            mNoteCallBack.setTermIndex(0);
            ProtocolManager.queryNotesProtocol(Integer.parseInt(SpUtil.getAccountId()), 0, 2, ProtocolId.PROTOCOL_ID_NOTE, mNoteCallBack);
            LogUtils.e(TAG, "query notes from server...");
        } else {
            LogUtils.e(TAG, "query notes from database...");
            mSub = getNotesObserver().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(getSubscriber());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSub != null) {
            mSub.unsubscribe();
        }
    }


    private Observable<List<NoteInfo>> getNotesObserver() {
        return Observable.create(new Observable.OnSubscribe<List<NoteInfo>>() {
            @Override
            public void call(Subscriber<? super List<NoteInfo>> subscriber) {
                //缓存JSON
                List<CacheJsonInfo> infos = DataSupport.where("cacheID = ? ", ProtocolId.PROTOCOL_ID_NOTE+"").find(CacheJsonInfo.class);
                //离线数据
                List<NoteInfo> offLines = DataSupport.findAll(NoteInfo.class);
                if (infos != null && infos.size() > 0) {

                    QueryNoteProtocol protocol = GsonUtil.fromJson(infos.get(0).getCacheJSON(), QueryNoteProtocol.class);
                    if (protocol.getData() != null && protocol.getData().get(0) != null && protocol.getData().get(0).getNoteList().size() > 0) {
                        List<NoteInfo> noteCaches = protocol.getData().get(0).getNoteList();
                        if (offLines != null && offLines.size() > 0) {
                            //添加离线笔记
                            for (NoteInfo noteInfo : offLines) {
                                if (noteInfo.getNoteId() == -1) {
                                    LogUtils.i("发现 离线笔记 ");
                                    LogUtils.i("...4");
                                    noteCaches.add(noteInfo);
                                }
                            }
                        }
                        subscriber.onNext(noteCaches);
                    }

                } else if (offLines != null && offLines.size() > 0) {
                    //没有缓存的JSON 那么可以肯定 都是离线创建的笔记
                    subscriber.onNext(offLines);
                }
                subscriber.onCompleted();
            }
        });
    }

    private Subscriber<List<NoteInfo>> getSubscriber() {
        return new Subscriber<List<NoteInfo>>() {
            LoadingProgressDialog dialog;

            @Override
            public void onStart() {
                super.onStart();
                dialog = new LoadingProgressDialog(getActivity());
                dialog.show();
                dialog.setTitle("数据加载中...");
            }

            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted...");
                dialog.dismiss();
                if (mServerInfos!=null && mServerInfos.size()<0){
                    mServerInfos.add(addCreatNoteItem());
                    refresh();
                }

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError...");
                dialog.dismiss();
            }

            @Override
            public void onNext(List<NoteInfo> noteInfos) {
                mServerInfos.clear();
                mServerInfos.addAll(noteInfos);
                mServerInfos.add(addCreatNoteItem());
                refresh();
            }
        };
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.page_btn:
                mDelteIndex = (int) v.getTag();
                page((int) v.getTag());
                break;
        }
    }

    private void page(int index) {
        if (index == mPagerIndex) {
            return;
        }

        //还原上个按钮状态
        mLlPager.getChildAt(mPagerIndex - 1).setSelected(false);
        mPagerIndex = index;
        //设置当前按钮状态
        mLlPager.getChildAt(mPagerIndex - 1).setSelected(true);

        //设置page页数数据
        mNotes.clear();

        if ((mPagerIndex - 1) * COUNT_PER_PAGE + COUNT_PER_PAGE > mCountInfos.size()) { // 不是 正数被
            mNotes.addAll(mCountInfos.subList((mPagerIndex - 1) * COUNT_PER_PAGE, mCountInfos.size()));
        } else {
            mNotes.addAll(mCountInfos.subList((mPagerIndex - 1) * COUNT_PER_PAGE, (mPagerIndex - 1) * COUNT_PER_PAGE + COUNT_PER_PAGE)); //正数被
        }
        notifyDataSetChanged();
    }

    /////////////////////////////////testData///////////////////////////////////


    /***
     * 初始化 测试数据
     */
    private void initTestData() {

        int index = 1000;
        for (int i = 0; i < 40; i++) {
            NoteInfo info1 = new NoteInfo();
            //笔记ID
            info1.setNoteId(index + i);
            //笔记名字
            info1.setNoteTitle("笔记数学");
            //笔记创建者
            info1.setNoteCreator(index + i);
            //用户年级
            info1.setNoteFitGradeName("一年级");
            //用户学科
            info1.setNoteFitSubjectName("数学");
            //对应数的id
            info1.setBookId(1001);
            // 图书编码 ，数学 语文 其他
            info1.setBookCategory(index + i);
            mServerInfos.add(info1);
        }
        // 刷新数据
        mServerInfos.add(addCreatNoteItem());
        refresh();

    }

    /***
     * 刷新 内容 ，笔记标题
     */
    private void refresh() {
        //添加刷新按钮
        mCountInfos.clear();
        //总数添加标题
        mCountInfos.addAll(mServerInfos);
        /**初始化角标*/
        initPages();
    }

    /**
     * 初始化翻页角标
     */
    private void initPages() {
        mCounts = 0;
        int quotient = mCountInfos.size() / COUNT_PER_PAGE;
        int remainder = mCountInfos.size() % COUNT_PER_PAGE;
        if (quotient == 0) {
            if (remainder == 0) {
                //没有数据
                mCounts = 0;
            } else {
                //不足16个item
                mCounts = 1;
            }
        }
        if (quotient != 0) {
            if (remainder == 0) {
                //没有数据
                mCounts = quotient; //.正好是16的倍数
            } else {
                //不足16个item
                mCounts = quotient + 1; // 不足16个 +1
            }
        }

        //设置显示按钮
        addBtnCounts(mCounts);
        mNotes.clear();
        if (mCountInfos.size() > COUNT_PER_PAGE) { // 大于1页
            LogUtils.i("initPages1..");
            mNotes.addAll(mCountInfos.subList(0, COUNT_PER_PAGE));
        } else {
            LogUtils.i("initPages2.."); //小于1页
            mNotes.addAll(mCountInfos.subList(0, mCountInfos.size()));
        }
        notifyDataSetChanged();
    }

    /***
     * 添加按钮
     *
     * @param counts
     */
    private void addBtnCounts(int counts) {
        //删除之前的按钮
        mLlPager.removeAllViews();
        for (int index = 1; index <= counts; index++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 20;
            LogUtils.e(TAG, "getActivity is null ? " + (getActivity() == null));
            View pageLayout = View.inflate(getActivity(), R.layout.page_item, null);
            final Button pageBtn = (Button) pageLayout.findViewById(R.id.page_btn);
            if (index == 1) {
                mPagerIndex = 1;
                pageBtn.setSelected(true);
            }
            pageBtn.setTag(index);
            pageBtn.setText(Integer.toString(index));
            pageBtn.setOnClickListener(this);
            mLlPager.addView(pageBtn, params);
        }
    }


    /**
     * 添加笔记
     */
    private void creatNoteInfoProtocol() {
        if (NetUtils.isNetConnected()) {
            AppendNotesRequest request = new AppendNotesRequest();
            request.setUserId(Integer.parseInt(SpUtil.getAccountId()));
            request.setCount(1);
            List<NoteInfo> infos = new ArrayList<>();
            infos.add(mCreatInfo);
            DataNoteBean bean = new DataNoteBean();
            bean.setCount(infos.size());
            bean.setNoteList(infos);
            List<DataNoteBean> data = new ArrayList<>();
            data.add(bean);
            request.setData(data);
            ProtocolManager.appendNotesProtocol(request, ProtocolId.PROTOCOL_ID_APPEND_NOTES, new AppendNotesCallBack(getActivity(), request));
        } else {
            //保存离线笔记  ，后期 上传到服务器 再次打开APP 无网络需要追加到列表
            mCreatInfo.save();
            appendNote(-1);
        }
    }

    /***
     * 修改笔记
     *
     * @param noteId    笔记id
     * @param noteStyle 笔记样式
     * @param subject   学科
     * @param noteTile  标题
     */

    @Override
    public void updataNote(long noteId, int noteStyle, String subject, String noteTile) {
        LogUtils.i("更新笔记");
        if (mServerInfos == null || mServerInfos.size() < 0) {
            return;
        }

        for (NoteInfo info : mServerInfos) {
            if (info.getNoteId() == noteId || info.getNoteMark() == noteId) {
                if (!StringUtils.isEmpty(noteTile)) {
                    info.setNoteTitle(noteTile);
                }
                info.setNoteStyle(noteStyle);
                notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * 删除笔记
     *
     * @param noteId 笔记id
     */
    @Override
    public void removeNote(int noteId) {
        LogUtils.i("删除笔记");
        if (mServerInfos == null || mServerInfos.size() < 0) {
            return;
        }
        for (NoteInfo info : mServerInfos) {
            if (info.getNoteId() == noteId) {
                LogUtils.i("测试成功了");
                mServerInfos.remove(info);
                //刷新数据
                refresh();
                //设置删除后翻页角标
                //删除之前 5个 ，当前位置4
                //当前5个 当前位置5  ，5只有1item 删除后=4 那么当前位置==4
                page(mCounts - mDelteIndex > 0 ? mDelteIndex : mCounts);
                if (mCounts < mDelteIndex) {
                    mDelteIndex = mCounts;
                }
                break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerView = null;
        if (mNotesAdapter != null) {
            mNotesAdapter = null;
        }
    }
}