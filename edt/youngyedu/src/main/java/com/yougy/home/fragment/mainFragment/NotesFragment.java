package com.yougy.home.fragment.mainFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.fragment.BFragment;
import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.protocol.callback.BaseCallBack;
import com.yougy.common.protocol.callback.NewAppendNotesCallBack;
import com.yougy.common.protocol.callback.NewNoteBookCallBack;
import com.yougy.common.protocol.request.NewInserAllNoteReq;
import com.yougy.common.protocol.request.NewQueryNoteReq;
import com.yougy.common.protocol.request.NewUpdateNoteReq;
import com.yougy.common.protocol.response.NewInserAllNoteRep;
import com.yougy.common.protocol.response.NewQueryNoteRep;
import com.yougy.common.protocol.response.NewUpdateNoteRep;
import com.yougy.common.utils.DataCacheUtils;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.activity.ControlFragmentActivity;
import com.yougy.home.adapter.NotesAdapter;
import com.yougy.home.adapter.OnRecyclerItemClickListener;
import com.yougy.home.bean.NoteInfo;
import com.yougy.ui.activity.R;
import com.yougy.view.CustomGridLayoutManager;
import com.yougy.view.dialog.CreatNoteDialog;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Response;
import rx.functions.Action1;


/**
 * Created by Administrator on 2016/7/12.
 * 笔记
 */
public class NotesFragment extends BFragment implements View.OnClickListener {//, BookMarksDialog.DialogClickFinsihListener {

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
    //    private Subscription mSub;
    private NewNoteBookCallBack mNewNoteBookCallBack;
    private ViewGroup mLoadingNull;
    private String mAddStr;
    private String mUpdataStr;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_notes, null);
        initNotes();
        mLlPager = (LinearLayout) mRootView.findViewById(R.id.ll_page);
        mLoadingNull = (ViewGroup) mRootView.findViewById(R.id.loading_null);
        return mRootView;
    }

    /**
     * 初始化 书列表
     */
    private void initNotes() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
//        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(UIUtils.getContext()));
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.adaper_divider_img_normal));
        mRecyclerView.addItemDecoration(divider);

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

    private void notifyDataSetChanged() {
        LogUtils.i("notes ..." + mNotes.size());
        mNotesAdapter.notifyDataSetChanged();
        EpdController.invalidate(mRootView, UpdateMode.GC);
    }

    private void noteItemClick(int position) {
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
                        showCenterDetermineDialog(R.string.note_name_null);
                        return;
                    } else {
                        mCreatInfo = new NoteInfo();
                        //设置 笔记名字
                        mCreatInfo.setNoteTitle(noteTitle);
                        //笔记所有者
                        mCreatInfo.setNoteAuthor(SpUtils.getAccountId());
                        // 笔记创建者
                        mCreatInfo.setNoteCreator(SpUtils.getAccountId());
                        //设置 笔记类型 ,内部转换对应的int 后期修改
                        mCreatInfo.setNoteStyleOption(mNoteDialog.getNoteOptionStyle());
//                        UIUtils.showToastSafe(mCreatInfo.getNoteType() + "", 1);
                        //笔记年级
                        mCreatInfo.setNoteFitGradeName(SpUtils.getGradeName());
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
            extras.putInt(FileContonst.HOME_WROK_ID, info.getNoteFitHomeworkId());
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
        mServerInfos.remove(0);
        //设置到生成笔记的id
        //添加到全部集合
        mCreatInfo.setNoteId(noteId);
        mServerInfos.add(mCreatInfo);
        // 添加+号到集合尾部
        mServerInfos.add(0,addCreatNoteItem());
        //刷新数据 计算分页 以及设置集合数据
        refresh();
        //当前分页大于1 ，需要收到设置到分页尾部。
        if (mCounts != 1) {
            page(mCounts);
        }
        //设置添加了笔记
        BaseEvent baseEvent = new BaseEvent(EventBusConstant.add_note, mCreatInfo);
        EventBus.getDefault().post(baseEvent);
    }

    private void handleAppendNoteEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof NewInserAllNoteRep) {
                    LogUtils.e(TAG, "handleAppendNoteEvent..................");
                    NewInserAllNoteRep rep = (NewInserAllNoteRep) o;
                    if (rep.getCode() == NewProtocolManager.NewCodeResult.CODE_SUCCESS) {
                        //缓存中 当前 全部 笔记添加 ，以防止无网络的时候使用上次缓存数据
                        appendNote(rep.getData().get(0).getNoteId());
                        addCacheData(NewProtocolManager.NewCacheId.CODE_CURRENT_NOTE, mCreatInfo);
                        addCacheData(NewProtocolManager.NewCacheId.ALL_CODE_NOTE, mCreatInfo);

                    } else {
                        showCenterDetermineDialog(R.string.add_note_fail);
                    }
                }
            }
        }));
    }

    private void handleNoteBookEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof NewQueryNoteRep && !mHide && mNewNoteBookCallBack != null) {
                    NewQueryNoteRep data = (NewQueryNoteRep) o;
                    if (data != null && data.getCode() == NewProtocolManager.NewCodeResult.CODE_SUCCESS) {
                        if (data.getData() != null && data.getData().size() > 0) {
                            List<NoteInfo> notes = data.getData();
                            mServerInfos.clear();
                            mServerInfos.addAll(notes);
                        }
                        //添加addItem
                        if (!mServerInfos.contains(addCreatNoteItem())) {
                            mServerInfos.add(0,addCreatNoteItem());
                        }
                        refresh();
                    }
                } else if (o instanceof String && !mHide && StringUtils.isEquals((String) o, NewProtocolManager.NewCacheId.CODE_CURRENT_NOTE + "")) {
                    LogUtils.i("使用缓存数据");

                    List<NoteInfo> infos = getCacheNotes(NewProtocolManager.NewCacheId.CODE_CURRENT_NOTE);
                    mServerInfos.clear();
                    if (infos != null) {
                        mServerInfos.addAll(infos);
                    }
                    mServerInfos.add(0,addCreatNoteItem());

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
    }


    private void loadData() {
        if (NetUtils.isNetConnected()) {
            mAddStr = DataCacheUtils.getString(UIUtils.getContext(), NewProtocolManager.OffLineId.OFF_LINE_ADD);
            mUpdataStr = DataCacheUtils.getString(UIUtils.getContext(), NewProtocolManager.OffLineId.OFF_LINE_UPDATA);
            if (!StringUtils.isEmpty(mAddStr)) {
                LogUtils.i("网络请求 离线添加笔记");
                requestOffLineAddNote();
            } else if (!StringUtils.isEmpty(mUpdataStr)) {
                LogUtils.i("网络请求 离线更新笔记");
                requestOffLineUpdataNote();
            } else {
                LogUtils.i("获取本学期笔记列表");
                getNotes();
            }
        } else {
            getNotes();
        }
    }

    /**
     * 更新离线修改的笔记
     */
    private void requestOffLineUpdataNote() {
        NewUpdateNoteReq req = new NewUpdateNoteReq();
        req.setUserId(SpUtils.getAccountId());
        req.setData(GsonUtil.fromNotes(mUpdataStr));
        NewProtocolManager.updateNote(req, new BaseCallBack<NewUpdateNoteRep>(getActivity()) {

            @Override
            public NewUpdateNoteRep parseNetworkResponse(Response response, int id) throws Exception {
                String str = response.body().string();
//                System.out.println("response json ...." + str);
                return GsonUtil.fromJson(str, NewUpdateNoteRep.class);
            }

            @Override
            public void onResponse(NewUpdateNoteRep response, int id) {
                mUpdataStr = "";
                DataCacheUtils.putString(getActivity(), NewProtocolManager.OffLineId.OFF_LINE_UPDATA, "");
                getNotes();
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                getNotes();
            }
        });
    }

    /**
     * 离线上传笔记
     */
    private void requestOffLineAddNote() {
        NewInserAllNoteReq req = new NewInserAllNoteReq();
        req.setUserId(SpUtils.getAccountId());
        req.setData(GsonUtil.fromNotes(mAddStr));
        NewProtocolManager.inserAllNote(req, new BaseCallBack<NewInserAllNoteRep>(getActivity()) {
            @Override
            public NewInserAllNoteRep parseNetworkResponse(Response response, int id) throws Exception {
                String json = response.body().string();
                LogUtils.i("respons add notes json ==" + json);
                return GsonUtil.fromJson(json, NewInserAllNoteRep.class);
            }

            @Override
            public void onResponse(NewInserAllNoteRep response, int id) {
                //判断是否有离线修改的笔记
                if (response.getCode() == NewProtocolManager.NewCodeResult.CODE_SUCCESS) {
                    mAddStr = "";
                    DataCacheUtils.putString(getActivity(), NewProtocolManager.OffLineId.OFF_LINE_ADD, "");
                    if (!StringUtils.isEmpty(mUpdataStr)) {
                        LogUtils.i("网络请求 离线更新笔记");
                        requestOffLineUpdataNote();
                    } else {
                        LogUtils.i("获取本学期笔记列表");
                        getNotes();
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                //判断是否有离线修改的笔记
                if (!StringUtils.isEmpty(mUpdataStr)) {
                    LogUtils.i("网络请求 离线更新笔记");
                    requestOffLineUpdataNote();
                } else {
                    LogUtils.i("获取本学期笔记列表");
                    getNotes();
                }
            }
        });
    }

    /***
     * 获取服务器笔记列表
     */
    private void getNotes() {
        if (NetUtils.isNetConnected()) {
            NewQueryNoteReq req = new NewQueryNoteReq();
            //设置学生ID
            req.setUserId(SpUtils.getAccountId());
            //设置缓存数据ID的key
            req.setCacheId(Integer.parseInt(NewProtocolManager.NewCacheId.CODE_CURRENT_NOTE));
            //设置年级
            req.setNoteFitGradeName(SpUtils.getGradeName());
            mNewNoteBookCallBack = new NewNoteBookCallBack(getActivity(), req);
            NewProtocolManager.queryNote(req, mNewNoteBookCallBack);

        } else {
            LogUtils.e(TAG, "query notes from database...");
            List<NoteInfo> infos = getCacheNotes(NewProtocolManager.NewCacheId.CODE_CURRENT_NOTE);
            mServerInfos.clear();
            if (infos != null) {
                mServerInfos.addAll(infos);
            }
            mServerInfos.add(0,addCreatNoteItem());
            refresh();
        }
    }


    @Override
    public void onClick(View v) {
        mDelteIndex = (int) v.getTag();
        page((int) v.getTag());

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
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            params.leftMargin = 20;
//            LogUtils.e(TAG, "getActivity is null ? " + (getActivity() == null));
//            View pageLayout = View.inflate(getActivity(), R.layout.page_item, null);
//            final Button pageBtn = (Button) pageLayout.findViewById(R.id.page_btn);
            TextView pageBtn = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.new_page_item, mLlPager, false);
            if (index == 1) {
                mPagerIndex = 1;
                pageBtn.setSelected(true);
            }
            pageBtn.setTag(index);
            pageBtn.setText(Integer.toString(index));
            pageBtn.setOnClickListener(this);
            mLlPager.addView(pageBtn);
        }
    }


    /**
     * 添加笔记
     */
    private void creatNoteInfoProtocol() {
        if (NetUtils.isNetConnected()) {
            NewInserAllNoteReq req = new NewInserAllNoteReq();
            req.setUserId(SpUtils.getAccountId());
            List<NoteInfo> infos = new ArrayList<>();
            infos.add(mCreatInfo);
            req.setData(infos);
            NewProtocolManager.inserAllNote(req, new NewAppendNotesCallBack(getActivity(), req));
        } else {
            //添加离线笔记
            addCacheData(NewProtocolManager.OffLineId.OFF_LINE_ADD, mCreatInfo);
            appendNote(-1);
        }
    }

    private void addCacheData(String key, NoteInfo noteModel) {
        // 缓存离线笔记
        String str = DataCacheUtils.getString(getActivity(), key);
        List<NoteInfo> notes;
        if (!StringUtils.isEmpty(str)) {
            notes = GsonUtil.fromNotes(str);
        } else {
            notes = new ArrayList<>();
        }
        notes.add(noteModel);
        DataCacheUtils.putString(getActivity(), key, GsonUtil.toJson(notes));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerView = null;
        if (mNotesAdapter != null) {
            mNotesAdapter = null;
        }
    }


    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        if (event.getType().equalsIgnoreCase(EventBusConstant.current_note)) {
            LogUtils.i("type .." + EventBusConstant.current_note);
            loadData();
        } else if (event.getType().equalsIgnoreCase(EventBusConstant.delete_note)) {
            LogUtils.i("type .." + EventBusConstant.delete_note);
            NoteInfo noteInfo = (NoteInfo) event.getExtraData();
            if (mServerInfos != null && mServerInfos.size() > 0) {
                //笔记内部ID 和 服务器ID 怕出相同的值 理论上不会出现相同的值
                if (noteInfo.getNoteId() > 0) {
                    for (NoteInfo noteModel : mServerInfos) {
                        if (noteModel.getNoteId() == noteInfo.getNoteId()) {
                            mServerInfos.remove(noteModel);
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
                } else { // 本地离线笔记
                    for (NoteInfo noteModel : mServerInfos) {
                        if (noteModel.getNoteMark() == noteInfo.getNoteMark()) {
                            mServerInfos.remove(noteModel);
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
            }
        } else if (event.getType().equalsIgnoreCase(EventBusConstant.alter_note)) {
            LogUtils.i("type .." + EventBusConstant.alter_note);

            NoteInfo noteInfo = (NoteInfo) event.getExtraData();
            // 服务器有ID的笔记 ,并且是服务器建设的笔记
            if (mServerInfos != null && mServerInfos.size() > 0) {
                if (noteInfo.getNoteId() > 0 && noteInfo.getNoteMark() <= 0) {
                    for (NoteInfo noteModel : mServerInfos) {
                        if (noteModel.getNoteId() == noteInfo.getNoteId()) {
                            noteModel.setNoteStyle(noteInfo.getNoteStyle());
                            break;
                        }
                    }
                } else { // 非服务器创建的笔记
                    for (NoteInfo noteModel : mServerInfos) {
                        if (noteModel.getNoteMark() == noteInfo.getNoteMark()) {
                            noteModel.setNoteStyle(noteInfo.getNoteStyle());
                            noteModel.setNoteFitSubjectName(noteInfo.getNoteFitSubjectName());
                            if (!noteModel.getNoteTitle().equalsIgnoreCase(noteInfo.getNoteTitle())) {
                                noteModel.setNoteTitle(noteInfo.getNoteTitle());
                                mNotesAdapter.notifyDataSetChanged();
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onUiCenterDetermineListener() {
        super.onUiCenterDetermineListener();
        dissMissUiPromptDialog();
    }
}