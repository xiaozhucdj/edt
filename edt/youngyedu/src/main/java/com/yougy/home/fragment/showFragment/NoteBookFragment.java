package com.yougy.home.fragment.showFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.protocol.request.NewDeleteNoteReq;
import com.yougy.common.protocol.request.NewUpdateNoteReq;
import com.yougy.common.utils.DataCacheUtils;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.bean.Note;
import com.yougy.home.bean.NoteInfo;
import com.yougy.init.bean.BookInfo;
import com.yougy.rx_subscriber.BaseSubscriber;
import com.yougy.ui.activity.R;
import com.yougy.view.controlView.ControlView;
import com.yougy.view.dialog.CreatNoteDialog;
import com.yougy.view.dialog.DeleteDialog;
import com.yougy.view.dialog.LoadingProgressDialog;
import com.yougy.view.showView.MyFrameLayout;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.yougy.common.utils.GsonUtil.fromNotes;


/**
 * Created by jiangliang on 2016/7/14.
 */
public class NoteBookFragment extends BaseFragment implements ControlView.PagerChangerListener {

    private static final String TAG = "NoteBookFragment";
    // 控制点击左右切换View
    // 显示 笔记View
//    private MyFrameLayout myFrameLayout;
    //当前 显示笔记的页码
    private TextView mTvPage;
    private boolean mHide;
    private NoteInfo mUpdateInfo;
    private CreatNoteDialog mNoteUpdataDialog;
    private DeleteDialog mDelteDialog;
    private TextView mTvnotePages;
    private ImageView mBackPageBack;
    private ImageView mPageNext;

    private NoteInfo noteInfo;

    public void setNoteInfo(NoteInfo noteInfo) {
        this.noteInfo = noteInfo;
        LogUtils.e(TAG, "note info is : " + this.noteInfo);
    }


    @Override
    protected void initOtherView() {
        super.initOtherView();
        mStub.setLayoutResource(R.layout.new_note_book);
        mStub.inflate();
        mNotebookIv.setEnabled(false);

        /**
         * 显示底部 页面
         */
        mTvnotePages = mRoot.findViewById(R.id.tv_notePages);
        mTvnotePages.setVisibility(View.VISIBLE);

        mBackPageBack = mRoot.findViewById(R.id.img_pageBack);
        backScription = RxView.clicks(mBackPageBack).throttleFirst(DURATION, TimeUnit.SECONDS).subscribe(getBackSubscriber());

        mPageNext = mRoot.findViewById(R.id.img_pageNext);
        nextScription = RxView.clicks(mPageNext).throttleFirst(DURATION, TimeUnit.SECONDS).subscribe(getNextSubscriber());

        /*****************************设置根布局*******************************/
        mControlView = mRoot.findViewById(R.id.my_frame);
        mTvPage = mRoot.findViewById(R.id.tv_page);
        //设置底部 点击切换页数
        mControlView.setIntercept(false);
        mControlView.setPagerListener(this);
        // 添加 笔记UI
        addLayoutView();

        initScription = getObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(getSubscriber());
        mImgupdataNote.setVisibility(View.VISIBLE);

    }

    /***
     * 更新笔记
     *
     * @param info
     */
    private void updataNoteInfo(NoteInfo info) {
        mControlActivity.mNoteStyle = info.getNoteStyle();
        mControlActivity.mNoteSubject = info.getNoteFitSubjectName();
        mControlActivity.mNotetitle = info.getNoteTitle();
        /**
         * 20161213
         */
        mNote.setNoteStyle(mControlActivity.mNoteStyle);
        setNoteStyle(mControlActivity.mNoteStyle);
    }


    @NonNull
    private Subscriber<Void> getNextSubscriber() {
        return new BaseSubscriber<Void>() {
            @Override
            public void onNext(Void aVoid) {
                LogUtils.e(TAG, "next subscriber...");
                leaveScribbleMode(true);
                saveContent(true);
                nextPage();
                mIsIntercept = false;
            }
        };
    }

    @NonNull
    private Subscriber<Void> getBackSubscriber() {
        return new BaseSubscriber<Void>() {
            @Override
            public void onNext(Void aVoid) {
                LogUtils.e(TAG, "back subscriber...");
                leaveScribbleMode(true);
                if (position > 0) {
                    saveContent(true);
                }
                prePage();
                mIsIntercept = false;
            }
        };
    }

    @NonNull
    private rx.Observable<Void> getObservable() {
        return rx.Observable.create(subscriber -> {
            if (!subscriber.isUnsubscribed()) {
                mNotes = DataSupport.where("bookpagenum = ? and name = ?", String.valueOf(-1), fileName).find(Note.class);
                LogUtils.e(TAG, "position is : " + position);
                LogUtils.e(TAG, "mNotes is : " + mNotes.toString());
                convert_list_to_map_with_java(mNotes);
                if (mMapNotes.containsKey(position)) {
                    LogUtils.e(TAG, "contains key......");
                    mMapNotes.get(position).getLines();
                    getLines(position - 1);
                    getLines(position + 1);
                    mNote = mMapNotes.get(position);
                    LogUtils.e(TAG, "doInBackground note's lines'size is : " + mNote.getLines().size());
                } else {
                    LogUtils.e(TAG, "not contains key......");
                    mNote = new Note();
                    //需要设置 pageNum 为了存储到数据库找到对应的角标数据
                    mNote.setPageNum(position);
                    mNote.setName(fileName);
                    mMapNotes.put(position, mNote);
                }
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        });
    }

    private void getLines(int position) {
        if (mMapNotes.containsKey(position) && mMapNotes.get(position).hasNoLines()) {
            mMapNotes.get(position).getLines();
        }
    }

    @NonNull
    private Subscriber<Void> getSubscriber() {
        return new Subscriber<Void>() {
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
                LogUtils.e(TAG, "onCompleted...");
                dialog.dismiss();

            }

            @Override
            public void onError(Throwable e) {
                dialog.dismiss();
                LogUtils.e(TAG, "onError...");
            }

            @Override
            public void onNext(Void aVoid) {
                LogUtils.e(TAG, "onNext...");
                initNoteItem(mNote);
            }
        };
    }


    /**
     * 添加 显示笔记控件
     */
    private ImageView prePageIv;
    private ImageView nextPageIv;
    //    private View mView;
    private boolean flag;

    private void addLayoutView() {
        if (mFrameLayout == null) {
            mFrameLayout = (MyFrameLayout) UIUtils.inflate(R.layout.notebook_item);
            flag = true;
        }
        //每页对应一个 notebookView
        initNoteView();
        if (geteLastNumber() <= position) {
            mTvnotePages.setText(position + 1 + "/" + (position + 1));
            saveLastNumber(position);
        } else {
            mTvnotePages.setText(position + 1 + "/" + (geteLastNumber() + 1));
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 78, 0, 78);
        mFrameLayout.addView(mNoteBookView, params);
        if (flag) {
            mControlView.addView(mFrameLayout, 0);
            flag = false;
        }
    }

    @Override
    protected void initDatas() {
        //TODO:袁野
        if (mControlActivity.mNoteMark > 0) {
            fileName = mControlActivity.mNoteMark + "";
        } else {
            fileName = mControlActivity.mNoteId + "";
        }
        LogUtils.e(TAG, "initDatas file name is : " + fileName);
        super.initDatas();
    }

    /**
     * 集合转换
     */
    public void convert_list_to_map_with_java(List<Note> mNotes) {
        mMapNotes = new LinkedHashMap<>();
        if (mNotes.size() > 0) {
            for (Note note : mNotes) {
                mMapNotes.put(note.getPageNum(), note);
            }
        }
    }

    /**
     * 初始化当前的数据 左右切换设置
     */
    private void initCurrentNote() {
        LogUtils.i("initCurrentNote");
        mFrameLayout.removeAllViews();
        long start = System.currentTimeMillis();
        addLayoutView();
        long end = System.currentTimeMillis();
        LogUtils.e(TAG, "add layout view take time is : " + (end - start));
        if (mMapNotes.containsKey(position)) {
            LogUtils.e(TAG, "notes contains note .....");
            mNote = mMapNotes.get(position);
            getLinesObservable().subscribeOn(Schedulers.io()).subscribe();
            LogUtils.e(TAG, "init current note's lines size is : " + mNote.getLines().size());
        } else {
            LogUtils.e(TAG, "notes not contains note ........");
            mNote = new Note();
            mNote.setPageNum(position);
            mNote.setName(fileName);
            mMapNotes.put(position, mNote);
        }

        start = System.currentTimeMillis();
        initNoteItem(mNote);
        end = System.currentTimeMillis();
        LogUtils.e(TAG, "init note item take times is : " + (end - start));
    }

    @Override
    protected void setNoteType() {
        super.setNoteType();
        /**
         * 20161213 笔记样式
         */

        LogUtils.i("start-------------------");
        if (mNote.getNoteStyle() == -1) {
            LogUtils.i("leke_-1");
            setNoteStyle(mControlActivity.mNoteStyle);
        } else {
            LogUtils.i("leke_type");
            LogUtils.i("leke_type==" + mNote.getNoteStyle());
            setNoteStyle(mNote.getNoteStyle());
        }

        if (mRunThread == null) {
            mRunThread = new NoteBookDelayedRun();
        }
        UIUtils.getMainThreadHandler().postDelayed(mRunThread, 500);
    }

    private void setNoteStyle(int type) {
        switch (type) {

            case 0:
                LogUtils.i("0");
                mNoteBookView.setBackgroundColor(UIUtils.getColor(R.color.background_white));
                break;

            case 1:
                LogUtils.i("1");
                mNoteBookView.setBackgroundResource(R.drawable.img_note_line);
                break;
            case 2:
                LogUtils.i("2");
                mNoteBookView.setBackgroundResource(R.drawable.img_note_grid);
                break;
        }
    }

    @NonNull
    private rx.Observable<Void> getLinesObservable() {
        return rx.Observable.create(subscriber -> {
            getLines(position - 1);
            getLines(position + 1);
        });
    }

    /**
     * 后退
     */
    @Override
    public void smartMoveBackwards() {

    }

    /**
     * 前进
     */
    @Override
    public void smartMoveForwards() {
    }

    @Override
    public void onTapMainDocArea() {

    }

    /***
     * 需要设置一下 对事件的拦截
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        LogUtils.e(TAG, "onclick............");
        super.onClick(v);
        switch (v.getId()) {
            case R.id.img_updataNote:
                if (mNoteUpdataDialog == null) {
                    mNoteUpdataDialog = new CreatNoteDialog(getActivity());
                }

                mNoteUpdataDialog.setClickListener(new CreatNoteDialog.NoteFragmentDialogClickListener() {
                    @Override
                    public void onCreatListener() {
                        String noteTitle = mNoteUpdataDialog.getEditName();
                        String subject = mNoteUpdataDialog.getStrSubject();
                        if (StringUtils.isEmpty(subject)) {
                            subject = "无";
                        }
                        if (StringUtils.isEmpty(noteTitle) && SpUtils.getAccountId() == mControlActivity.mNoteCreator) {
                            showCenterDetermineDialog(R.string.note_name_null);
                            return;
                        }

                        mUpdateInfo = new NoteInfo();
                        mUpdateInfo.setNoteId(mControlActivity.mNoteId);
                        //标题 ,后台创建笔记不可以修改名称
                        mUpdateInfo.setNoteTitle(SpUtils.getAccountId() == mControlActivity.mNoteCreator ? noteTitle : mControlActivity.mNotetitle);
                        //学科 ，后台创建笔记不可以修改学科
                        mUpdateInfo.setNoteFitSubjectName(SpUtils.getAccountId() == mControlActivity.mNoteCreator ? subject : mControlActivity.mNoteSubject);
                        //笔记样式
                        mUpdateInfo.setNoteStyleOption(mNoteUpdataDialog.getNoteOptionStyle());
                        mUpdateInfo.setNoteMark(mControlActivity.mNoteMark);

                        if (NetUtils.isNetConnected() && mControlActivity.mNoteId > 0) { //上传服务器已经有的笔记
                            updataNoteProtocol();
                        } else { //当前没有网络 或者笔记ID
                            //修改本地添加笔记
                            updateInfoForLocal();
                        }
                        if (mNoteUpdataDialog.isShowing()) {
                            mNoteUpdataDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelListener() {
                        if (mNoteUpdataDialog.isShowing()) {
                            mNoteUpdataDialog.dismiss();
                        }
                    }
                });
                mNoteUpdataDialog.show();
                if (SpUtils.getAccountId() != mControlActivity.mNoteCreator) {
                    mNoteUpdataDialog.setEditNameEnable(false);
                    mNoteUpdataDialog.setRecyclerViewGone();
                }
                mNoteUpdataDialog.setNoteName(mControlActivity.mNotetitle);
                mNoteUpdataDialog.setBtnName("保存");
                mNoteUpdataDialog.setTitle("修改笔记");
                break;

            case R.id.img_deleteNote:

                if (SpUtils.getAccountId() == mControlActivity.mNoteCreator) {
                    if (mDelteDialog == null) {
                        mDelteDialog = new DeleteDialog(getActivity());
                        mDelteDialog.setSureListener(() -> {
                            //删除本地数据
                            if (mControlActivity.mNoteId < 0) {
                                String addStr = DataCacheUtils.getString(getActivity(), NewProtocolManager.OffLineId.OFF_LINE_ADD);
                                //判断离线添加是否有数据 并且外面传来的参数是离线添加的数据
                                if (!StringUtils.isEmpty(addStr) && mControlActivity.mNoteMark != -1) {
                                    // 获取到离线数据
                                    List<NoteInfo> tabNoteModels = fromNotes(addStr);
                                    //遍历数据  删除对应的数据bean
                                    for (NoteInfo model : tabNoteModels) {
                                        if (model.getNoteMark() == mControlActivity.mNoteMark) {
                                            //删除本地数据
                                            tabNoteModels.remove(model);
                                            //重新放到ADD 缓存
                                            DataCacheUtils.putString(getActivity(), NewProtocolManager.OffLineId.OFF_LINE_ADD, GsonUtil.toJson(tabNoteModels));
                                            // 通知相关Fragment
                                            BaseEvent baseEvent = new BaseEvent(EventBusConstant.delete_note, model);
                                            EventBus.getDefault().post(baseEvent);
                                            getActivity().finish();
                                            break;
                                        }
                                    }

                                }
                                return;
                            }
                            if (!NetUtils.isNetConnected()) { //当前笔记都是服务器存着的ID的 所以在没网的时候是不允许用户删除的 否则会 业务逻辑更加复杂。
                                showCancelAndDetermineDialog(R.string.jump_to_net);
                                return;
                            }
                            delteNoteProtocol();
                        });
                    }
                    mDelteDialog.show();
                }
                break;
        }
    }


    private void prePage() {
        if (position > 0) {
            mUndoIv.setEnabled(false);
            mRedoIv.setEnabled(false);
            position--;
            initCurrentNote();
        }
    }

    private void nextPage() {
        mUndoIv.setEnabled(false);
        mRedoIv.setEnabled(false);
        position++;
        initCurrentNote();
    }


    //协议删除笔记

    private void delteNoteProtocol() {
        NewDeleteNoteReq req = new NewDeleteNoteReq();
        req.setUserId(SpUtils.getAccountId());
        req.setNoteId(mControlActivity.mNoteId);
        NetWorkManager.deleteNote(req)
                .subscribe(o -> {
                    String cutterStr = DataCacheUtils.getString(getActivity(), NewProtocolManager.NewCacheId.CODE_CURRENT_NOTE);
                    if (!StringUtils.isEmpty(cutterStr)) {
                        List<NoteInfo> cutters = GsonUtil.fromNotes(cutterStr);
                        for (NoteInfo model : cutters) {
                            if (model.getNoteId() == mControlActivity.mNoteId) {
                                cutters.remove(model);
                                DataCacheUtils.putString(getActivity(), NewProtocolManager.NewCacheId.CODE_CURRENT_NOTE, GsonUtil.toJson(cutters));
                                break;
                            }
                        }
                    }

                    //删除缓存中全部笔记 包含的 bean
                    String allStr = DataCacheUtils.getString(getActivity(), NewProtocolManager.NewCacheId.ALL_CODE_NOTE);
                    if (!StringUtils.isEmpty(allStr)) {
                        List<NoteInfo> alls = fromNotes(allStr);
                        for (NoteInfo model : alls) {
                            if (model.getNoteId() == mControlActivity.mNoteId) {
                                alls.remove(model);
                                DataCacheUtils.putString(getActivity(), NewProtocolManager.NewCacheId.ALL_CODE_NOTE, GsonUtil.toJson(alls));
                                break;
                            }

                        }
                    }
                    //通知fragmentUI
                    NoteInfo model = new NoteInfo();
                    model.setNoteId(mControlActivity.mNoteId);
                    model.setNoteMark(mControlActivity.mNoteMark);
                    BaseEvent baseEvent = new BaseEvent(EventBusConstant.delete_note, model);
                    EventBus.getDefault().post(baseEvent);
                    getActivity().finish();
                }, throwable -> {
                    showCenterDetermineDialog(R.string.delete_note_fail);
                });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }


    /**
     * 按键翻页
     */
    @Override
    public void prevPageForKey() {
        super.prevPageForKey();
        if (!mHide && mBackPageBack != null) {
            mBackPageBack.callOnClick();
        }
    }

    /**
     * 按键翻页
     */
    @Override
    public void nextPageForKey() {
        super.nextPageForKey();
        if (!mHide && mPageNext != null) {
            mPageNext.callOnClick();
        }
    }


    /**
     * 更新笔记
     */
    private void updataNoteProtocol() {
        NewUpdateNoteReq req = new NewUpdateNoteReq();
        req.setUserId(SpUtils.getAccountId());
        List<NoteInfo> infos = new ArrayList<>();
        infos.add(mUpdateInfo);
        req.setData(infos);
        NetWorkManager.updateNote(req).compose(((BaseActivity) context).bindToLifecycle())
                .subscribe(o -> {
                    //更新缓存数据 ,说明
                    if (mControlActivity.mNoteMark <= 0) { //服务器独立创建的笔记
                        //更新课本
                        String cutterBookStr = DataCacheUtils.getString(getActivity(), NewProtocolManager.NewCacheId.CODE_CURRENT_BOOK);
                        String allCutterBookStr = DataCacheUtils.getString(getActivity(), NewProtocolManager.NewCacheId.ALL_CODE_CURRENT_BOOK);
                        if (!StringUtils.isEmpty(cutterBookStr)) {
                            List<BookInfo> cutterS = GsonUtil.fromBooks(cutterBookStr);
                            for (BookInfo model : cutterS) {
                                if (model.getBookFitNoteId() == mControlActivity.mNoteId) {
                                    model.setNoteStyle(mUpdateInfo.getNoteStyle());
                                    DataCacheUtils.putString(getActivity(), NewProtocolManager.NewCacheId.CODE_CURRENT_BOOK, GsonUtil.toJson(cutterS));
                                    break;
                                }
                            }
                        }
                        if (!StringUtils.isEmpty(allCutterBookStr)) {
                            List<BookInfo> allCutterS = GsonUtil.fromBooks(allCutterBookStr);
                            for (BookInfo model : allCutterS) {
                                if (model.getBookFitNoteId() == mControlActivity.mNoteId) {
                                    model.setNoteStyle(mUpdateInfo.getNoteStyle());
                                    DataCacheUtils.putString(getActivity(), NewProtocolManager.NewCacheId.ALL_CODE_CURRENT_BOOK, GsonUtil.toJson(allCutterS));
                                    break;
                                }
                            }
                        }
                    }
                    //更新笔记
                    String cutterStr = DataCacheUtils.getString(getActivity(), NewProtocolManager.NewCacheId.CODE_CURRENT_NOTE);
                    String allCutterStr = DataCacheUtils.getString(getActivity(), NewProtocolManager.NewCacheId.ALL_CODE_NOTE);
                    //当前笔记 替换和修改缓存
                    if (!StringUtils.isEmpty(cutterStr)) {
                        List<NoteInfo> cutterS = GsonUtil.fromNotes(cutterStr);
                        for (NoteInfo model : cutterS) {
                            if (model.getNoteId() == mControlActivity.mNoteId) {
                                if (mControlActivity.mNoteMark <= 0) {
                                    model.setNoteStyle(mUpdateInfo.getNoteStyle());
                                } else {
                                    model.setNoteTitle(mUpdateInfo.getNoteTitle());
                                    model.setNoteStyle(mUpdateInfo.getNoteStyle());
                                    model.setNoteFitSubjectName(mUpdateInfo.getNoteFitSubjectName());
                                }
                                DataCacheUtils.putString(getActivity(), NewProtocolManager.NewCacheId.CODE_CURRENT_NOTE, GsonUtil.toJson(cutterS));
                                break;
                            }
                        }
                    }
                    //全部笔记
                    if (!StringUtils.isEmpty(allCutterStr)) {
                        List<NoteInfo> allCutterS = GsonUtil.fromNotes(allCutterStr);
                        for (NoteInfo model : allCutterS) {
                            if (model.getNoteId() == mControlActivity.mNoteId) {

                                if (mControlActivity.mNoteMark <= 0) {
                                    model.setNoteStyle(mUpdateInfo.getNoteStyle());
                                } else {
                                    model.setNoteTitle(mUpdateInfo.getNoteTitle());
                                    model.setNoteStyle(mUpdateInfo.getNoteStyle());
                                    model.setNoteFitSubjectName(mUpdateInfo.getNoteFitSubjectName());
                                }
                                DataCacheUtils.putString(getActivity(), NewProtocolManager.NewCacheId.ALL_CODE_NOTE, GsonUtil.toJson(allCutterS));
                                break;
                            }
                        }
                    }
                    //更新当前内存数据
                    updataNoteInfo(mUpdateInfo);
                    BaseEvent baseEvent = new BaseEvent(EventBusConstant.alter_note, mUpdateInfo);
                    EventBus.getDefault().post(baseEvent);
                }, throwable -> showCenterDetermineDialog(R.string.updata_note_fail));


    }

    private void saveLastNumber(int number) {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences("note_last_number", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(fileName, number);
        editor.apply();
    }

    private int geteLastNumber() {
        SharedPreferences sp = UIUtils.getContext().getSharedPreferences("note_last_number", Context.MODE_PRIVATE);
        return sp.getInt(fileName, 0);
    }


    /**
     * 没有网络 或者 ID <0 离线添加的笔记
     * 更新
     */
    private void updateInfoForLocal() {
        if (mControlActivity.mNoteId > 0) { // 服务器存着的ID 更新
            String updataStr = DataCacheUtils.getString(getActivity(), NewProtocolManager.OffLineId.OFF_LINE_UPDATA);
            if (!StringUtils.isEmpty(updataStr)) {

                List<NoteInfo> cutterS = GsonUtil.fromNotes(updataStr);
                for (NoteInfo model : cutterS) {
                    if (model.getNoteId() == mControlActivity.mNoteId) {
                        if (mControlActivity.mNoteMark <= 0) {
                            model.setNoteStyle(mUpdateInfo.getNoteStyle());
                        } else {
                            model.setNoteTitle(mUpdateInfo.getNoteTitle());
                            model.setNoteStyle(mUpdateInfo.getNoteStyle());
                            model.setNoteFitSubjectName(mUpdateInfo.getNoteFitSubjectName());
                        }
                        DataCacheUtils.putString(getActivity(), NewProtocolManager.OffLineId.OFF_LINE_UPDATA, GsonUtil.toJson(cutterS));
                        break;
                    }
                }
            } else {
                List<NoteInfo> cutterS = new ArrayList<>();
                cutterS.add(mUpdateInfo);
                DataCacheUtils.putString(getActivity(), NewProtocolManager.OffLineId.OFF_LINE_UPDATA, GsonUtil.toJson(cutterS));
            }


            //更新笔记
            String cutterStr = DataCacheUtils.getString(getActivity(), NewProtocolManager.NewCacheId.CODE_CURRENT_NOTE);
            String allCutterStr = DataCacheUtils.getString(getActivity(), NewProtocolManager.NewCacheId.ALL_CODE_NOTE);
            //当前笔记 替换和修改缓存
            if (!StringUtils.isEmpty(cutterStr)) {
                List<NoteInfo> cutterS = GsonUtil.fromNotes(cutterStr);
                for (NoteInfo model : cutterS) {
                    if (model.getNoteId() == mControlActivity.mNoteId) {
                        if (mControlActivity.mNoteMark <= 0) {
                            model.setNoteStyle(mUpdateInfo.getNoteStyle());
                        } else {
                            model.setNoteTitle(mUpdateInfo.getNoteTitle());
                            model.setNoteStyle(mUpdateInfo.getNoteStyle());
                            model.setNoteFitSubjectName(mUpdateInfo.getNoteFitSubjectName());
                        }
                        DataCacheUtils.putString(getActivity(), NewProtocolManager.NewCacheId.CODE_CURRENT_NOTE, GsonUtil.toJson(cutterS));
                        break;
                    }
                }
            }
            //全部笔记
            if (!StringUtils.isEmpty(allCutterStr)) {
                List<NoteInfo> allCutterS = GsonUtil.fromNotes(allCutterStr);
                for (NoteInfo model : allCutterS) {
                    if (model.getNoteId() == mControlActivity.mNoteId) {
                        if (mControlActivity.mNoteMark <= 0) {
                            model.setNoteStyle(mUpdateInfo.getNoteStyle());
                        } else {
                            model.setNoteTitle(mUpdateInfo.getNoteTitle());
                            model.setNoteStyle(mUpdateInfo.getNoteStyle());
                            model.setNoteFitSubjectName(mUpdateInfo.getNoteFitSubjectName());
                        }
                        DataCacheUtils.putString(getActivity(), NewProtocolManager.NewCacheId.ALL_CODE_NOTE, GsonUtil.toJson(allCutterS));
                        break;
                    }
                }
            }
        } else { //本地更新
            String offLineStr = DataCacheUtils.getString(getActivity(), NewProtocolManager.OffLineId.OFF_LINE_ADD);
            if (!StringUtils.isEmpty(offLineStr)) {
                List<NoteInfo> notes = GsonUtil.fromNotes(offLineStr);
                for (NoteInfo model : notes) {
                    if (model.getNoteMark() == mControlActivity.mNoteMark) {
                        model.setNoteStyle(mUpdateInfo.getNoteStyle());
                        model.setNoteTitle(mUpdateInfo.getNoteTitle());
                        model.setNoteFitSubjectName(mUpdateInfo.getNoteFitSubjectName());
                        DataCacheUtils.putString(getActivity(), NewProtocolManager.OffLineId.OFF_LINE_ADD, GsonUtil.toJson(notes));
                        break;
                    }
                }
            }
        }
        updataNoteInfo(mUpdateInfo);
        BaseEvent baseEvent = new BaseEvent(EventBusConstant.alter_note, mUpdateInfo);
        EventBus.getDefault().post(baseEvent);
    }


    private NoteBookDelayedRun mRunThread;

    private class NoteBookDelayedRun implements Runnable {
        @Override
        public void run() {
            if (mNoteBookView != null) {
                mNoteBookView.setIntercept(false);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mNoteBookView != null) {
            mNoteBookView.recycle();
        }

        if (mRunThread == null) {
            mRunThread = new NoteBookDelayedRun();
        }

        mRunThread = null;
        Runtime.getRuntime().gc();
    }

    @Override
    public void onUiDetermineListener() {
        Intent intent = new Intent("android.intent.action.WIFI_ENABLE");
        startActivity(intent);
    }


    @Override
    public void onEventMainThread(BaseEvent event) {
        super.onEventMainThread(event);
        if (event.getType().equalsIgnoreCase(EventBusConstant.EVENT_ANSWERING_SHOW)) {
            LogUtils.i("type .." + event.getType());
            if (mNoteBookView != null) {
                mNoteBookView.leaveScribbleMode();
                mNoteBookView.setIntercept(true);
            }
            BaseEvent baseEvent = new BaseEvent(EventBusConstant.EVENT_ANSWERING_RESULT, "");
            EventBus.getDefault().post(baseEvent);
        } else if (event.getType().equalsIgnoreCase(EventBusConstant.EVENT_ANSWERING_PUASE) || (event.getType().equalsIgnoreCase(EventBusConstant.EVENT_LOCKER_ACTIVITY_PUSE))) {
            LogUtils.i("type .." + event.getType());
            if (mNoteBookView != null) {
                mNoteBookView.setIntercept(false);
            }
        } else if (event.getType().equalsIgnoreCase(EventBusConstant.EVENT_START_ACTIIVTY_ORDER)) {
            LogUtils.i("type .." + event.getType());
            if (mNoteBookView != null) {
                mNoteBookView.leaveScribbleMode();
                mNoteBookView.setIntercept(true);
            }
            BaseEvent baseEvent = new BaseEvent(EventBusConstant.EVENT_START_ACTIIVTY_ORDER_RESULT, "");
            EventBus.getDefault().post(baseEvent);
        }
    }
}