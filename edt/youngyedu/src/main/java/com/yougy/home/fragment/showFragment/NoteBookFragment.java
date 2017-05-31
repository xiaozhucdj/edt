package com.yougy.home.fragment.showFragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.protocol.callback.NewDelteNoteCallBack;
import com.yougy.common.protocol.callback.NewUpdaNoteCallBack;
import com.yougy.common.protocol.request.NewDeleteNoteReq;
import com.yougy.common.protocol.request.NewUpdateNoteReq;
import com.yougy.common.protocol.response.NewDeleteNoteRep;
import com.yougy.common.protocol.response.NewUpdateNoteRep;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.Observable.Observable;
import com.yougy.home.Observable.Observer;
import com.yougy.home.bean.Note;
import com.yougy.home.bean.NoteInfo;
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

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by jiangliang on 2016/7/14.
 */
public class NoteBookFragment extends BaseFragment implements ControlView.PagerChangerListener, Observable {

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
    private Subscription mUpdateSub;

    public void setNoteInfo(NoteInfo noteInfo) {
        this.noteInfo = noteInfo;
        Log.e(TAG, "note info is : " + this.noteInfo);
    }

    private void uploadNote() {

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
        mTvnotePages = (TextView) mRoot.findViewById(R.id.tv_notePages);
        mTvnotePages.setVisibility(View.VISIBLE);

        mBackPageBack = (ImageView) mRoot.findViewById(R.id.img_pageBack);
        backScription = RxView.clicks(mBackPageBack).throttleFirst(DURATION, TimeUnit.SECONDS).subscribe(getBackSubscriber());

        mPageNext = (ImageView) mRoot.findViewById(R.id.img_pageNext);
        nextScription = RxView.clicks(mPageNext).throttleFirst(DURATION, TimeUnit.SECONDS).subscribe(getNextSubscriber());

        /*****************************设置根布局*******************************/
        mControlView = (ControlView) mRoot.findViewById(R.id.my_frame);
        mTvPage = (TextView) mRoot.findViewById(R.id.tv_page);
        //设置底部 点击切换页数
        mControlView.setIntercept(false);
        mControlView.setPagerListener(this);
        // 添加 笔记UI
        addLayoutView();

        initScription = getObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(getSubscriber());
        mImgupdataNote.setVisibility(View.VISIBLE);

    }

    @Override
    protected void handleEvent() {
        handleRemoveEvent();
        handleUpdateEvent();
        super.handleEvent();
    }

    private void handleUpdateEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof NewUpdateNoteRep) {
                    NewUpdateNoteRep req = (NewUpdateNoteRep) o;
                    if (req!=null && req.getCode() == NewProtocolManager.NewCodeResult.CODE_SUCCESS){
                        updataNoteInfo(mUpdateInfo);
                    }else{
                       UIUtils.showToastSafe("修改笔记失败",Toast.LENGTH_LONG);
                    }
                }
            }
        }));
        subscription.add(tapEventEmitter.connect());
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

        long id ;
        if (mControlActivity.mNoteId> 0){
            id = mControlActivity.mNoteId;
        }else{
            id =  mControlActivity.mNoteMark;
        }


        notifyChange(id , mControlActivity.mNoteStyle,
                mControlActivity.mNoteSubject, mControlActivity.mNotetitle);

    }

    private void handleRemoveEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof NewDeleteNoteRep ) {
                    NewDeleteNoteRep rep  = (NewDeleteNoteRep) o;
                    if (rep!=null && rep.getCode() == NewProtocolManager.NewCodeResult.CODE_SUCCESS){
                        notifyDelete(mControlActivity.mNoteId);
                        getActivity().finish() ;
                    }else{
                        UIUtils.showToastSafe("删除笔记失败",Toast.LENGTH_LONG);
                    }
                }
            }
        }));
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
        return rx.Observable.create(new rx.Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    mNotes = DataSupport.where("bookpagenum = ? and name = ?", String.valueOf(-1), fileName).find(Note.class);
                    LogUtils.e(TAG, "position is : " + position);
                    LogUtils.e(TAG, "mNotes is : " + mNotes.toString());
                    convert_list_to_map_with_java(mNotes);
                    if (mMapNotes.containsKey(position)) {
                        LogUtils.e(TAG, "contains key......");
                        mMapNotes.get(position).getLines();
//                    mMapNotes.get(position).getBitmap();
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
                Log.e(TAG, "onCompleted...");
                dialog.dismiss();

            }

            @Override
            public void onError(Throwable e) {
                dialog.dismiss();
                Log.e(TAG, "onError...");
            }

            @Override
            public void onNext(Void aVoid) {
                Log.e(TAG, "onNext...");
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
//       mNoteBookView.setBackgroundResource(R.drawable.biji_canvas_bg);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 60, 0, 111);
        mFrameLayout.addView(mNoteBookView, params);
        if (flag) {
            mControlView.addView(mFrameLayout, 0);
            flag = false;
        }
    }

    @Override
    protected void initDatas() {
        //TODO:袁野
        if (mControlActivity.mNoteMark >0) {
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
            LogUtils.i("leke_type=="+mNote.getNoteStyle());
            setNoteStyle(mNote.getNoteStyle());
        }
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
                mNoteBookView.setBackgroundResource(R.drawable.imgz_note_grid);
                break;
        }
    }

    @NonNull
    private rx.Observable<Void> getLinesObservable() {
        return rx.Observable.create(new rx.Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                Log.e(TAG, "pre position is : " + position);
                getLines(position - 1);
                Log.e(TAG, "after position is : " + position);
                getLines(position + 1);
                Log.e(TAG, "third position is : " + position);
            }
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
        //mIsIntercept 字段是判断 点击 手势 画笔 ，截图 对事件的拦截 是否交给操作左右切换
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

                        if (StringUtils.isEmpty(noteTitle) && SpUtil.getAccountId() == mControlActivity.mNoteCreator) {
                            UIUtils.showToastSafe("请填写笔记昵称", Toast.LENGTH_SHORT);
                            return;
                        } else {
                            if (NetUtils.isNetConnected() && mControlActivity.mNoteId > 0) {
                                mUpdateInfo = new NoteInfo();
                                mUpdateInfo.setNoteId(mControlActivity.mNoteId);
                                //标题
                                mUpdateInfo.setNoteTitle(SpUtil.getAccountId() == mControlActivity.mNoteCreator ? noteTitle : mControlActivity.mNotetitle);
                                //学科
                                mUpdateInfo.setNoteFitSubjectName(SpUtil.getAccountId() == mControlActivity.mNoteCreator ? subject : mControlActivity.mNoteSubject);
                                //笔记样式
                                mUpdateInfo.setNoteStyleOption(mNoteUpdataDialog.getNoteOptionStyle());
                                updataNoteProtocol();
                            } else {
                                //修改本地添加笔记
                                updateInfoForLocal();
                            }
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
//                mNoteUpdataDialog.setNoteTypeGone();
                if (SpUtil.getAccountId() != mControlActivity.mNoteCreator) {
                    mNoteUpdataDialog.setEditNameEnable(false);
                    mNoteUpdataDialog.setRecyclerViewGone();
                }
                mNoteUpdataDialog.setNoteName(mControlActivity.mNotetitle);
                mNoteUpdataDialog.setBtnName("保存");
                mNoteUpdataDialog.setTitle("修改笔记");

                break;

            case R.id.img_deleteNote:
                if (!NetUtils.isNetConnected()) {
                    UIUtils.showToastSafe(R.string.net_not_connection, Toast.LENGTH_SHORT);
                    return;
                }

                if (mControlActivity.mNoteId < 0) {
                    return;
                }

                if (SpUtil.getAccountId() == mControlActivity.mNoteCreator) {
                    if (mDelteDialog == null) {
                        mDelteDialog = new DeleteDialog(getActivity());
                        mDelteDialog.setSureListener(new DeleteDialog.SureListener() {
                            @Override
                            public void onSureClick() {
                                delteNoteProtocol();
                            }
                        });
                    }
                    mDelteDialog.show();
                }
                break;
        }
//        mControlView.setIntercept(mIsIntercept);
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

    /***
     * 观察者
     */
    private List<Observer> mObservers = new ArrayList<>();

    @Override
    public void addObserver(Observer observer) {
        mObservers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {

    }

    @Override
    public void notifyChange(long noteId, int noteStyle, String subject, String noteTile) {
        for (Observer observer : mObservers) {
            observer.updataNote(noteId, noteStyle, subject, noteTile);
        }
    }

    @Override
    public void notifyDelete(int noteId) {
        for (Observer observer : mObservers) {
            observer.removeNote(noteId);
        }
    }

    //协议删除笔记

    private void delteNoteProtocol() {
      /*  RemoveNotesRequest request = new RemoveNotesRequest();
        request.setUserId(SpUtil.getAccountId());
        request.setCount(1);
        NoteInfo info = new NoteInfo();
        info.setNoteId(mControlActivity.mNoteId);
        List<NoteInfo> infos = new ArrayList<>();
        infos.add(info);
        DataNoteBean bean = new DataNoteBean();
        bean.setCount(infos.size());
        bean.setNoteList(infos);
        List<DataNoteBean> data = new ArrayList<>();
        data.add(bean);
        request.setData(data);
        ProtocolManager.removeNotesProtocol(request, ProtocolId.PROTOCOL_ID_REMOVE_NOTES, new DelteNoteCallBack(getActivity(), request));*/
        NewDeleteNoteReq req = new NewDeleteNoteReq();
        req.setUserId(SpUtil.getAccountId());
        req.setNoteId(mControlActivity.mNoteId);
        NewProtocolManager.deleteNote(req ,new NewDelteNoteCallBack(getActivity(),req));
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
        NewUpdateNoteReq req = new NewUpdateNoteReq() ;
        List<NoteInfo> infos = new ArrayList<>() ;
        infos.add(mUpdateInfo) ;
        req.setData(infos);
        NewProtocolManager.updateNote(req , new NewUpdaNoteCallBack(getActivity() ,req) );
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
    /***
     * 更新本地数据
     */
    private void updateInfoForLocal() {
        rx.Observable<NoteInfo> observable = rx.Observable.create(new rx.Observable.OnSubscribe<NoteInfo>() {
            @Override
            public void call(Subscriber<? super NoteInfo> subscriber) {

                if (mControlActivity.mNoteId > 0) {
                    //修改服务器创建离线笔记
                    LogUtils.i("修改服务器创建离线笔记");
                    List<NoteInfo> infos = DataSupport.where("noteId = ? ", mControlActivity.mNoteId + "").find(NoteInfo.class);
                    //查询数据库
                    if (infos != null && infos.size() > 0) {
                        LogUtils.i("本地有ID 更新数据库");
                        NoteInfo info = infos.get(0);
                        ContentValues values = new ContentValues();
                        if (!StringUtils.isEmpty(mNoteUpdataDialog.getEditName())) {
                            values.put("noteTitle", mNoteUpdataDialog.getEditName());
                            info.setNoteTitle(  mNoteUpdataDialog.getEditName());
                        }

                        if (!StringUtils.isEmpty(mNoteUpdataDialog.getStrSubject())) {
                            values.put("noteFitSubjectName", mNoteUpdataDialog.getStrSubject());
                            info.setNoteFitSubjectName( mNoteUpdataDialog.getStrSubject());
                        }
                        values.put("noteStyle", info.setNoteStyleOption(mNoteUpdataDialog.getNoteOptionStyle()) );
                        DataSupport.updateAll(NoteInfo.class, values, "noteId = ? ", info.getNoteId() + "");
                        subscriber.onNext(info);
                    } else {
                        LogUtils.i("本地有ID 创建一条数据");
                        NoteInfo info = new NoteInfo();
                        info.setNoteId(mControlActivity.mNoteId);
                        if (!StringUtils.isEmpty(mNoteUpdataDialog.getEditName())) {
                            info.setNoteTitle(mNoteUpdataDialog.getEditName());
                        }

                        if (!StringUtils.isEmpty(mNoteUpdataDialog.getStrSubject())) {
                            info.setNoteFitSubjectName(mNoteUpdataDialog.getStrSubject());
                        }
                        info.setNoteStyleOption(mNoteUpdataDialog.getNoteOptionStyle());
                        info.save();
                        subscriber.onNext(info);
                    }

                } else {
                    //修改离线添加笔记,相当更新信息
                    LogUtils.i("查询数据库 离线添加的笔记是否在数据库中存在");
                    List<NoteInfo> infos = DataSupport.where("noteMark = ? ", mControlActivity.mNoteMark + "").find(NoteInfo.class);
                    if (infos != null && infos.size() > 0) {
                        LogUtils.i("离线创建 更新数据库");
                        NoteInfo info = infos.get(0);
                        ContentValues values = new ContentValues();

                        if (!StringUtils.isEmpty(mNoteUpdataDialog.getEditName())) {
                            values.put("noteTitle", mNoteUpdataDialog.getEditName());
                            info.setNoteTitle( mNoteUpdataDialog.getEditName());
                        }

                        if (!StringUtils.isEmpty(mNoteUpdataDialog.getStrSubject())) {
                            values.put("noteFitSubjectName", mNoteUpdataDialog.getStrSubject());
                            info.setNoteTitle( mNoteUpdataDialog.getStrSubject());
                        }
                        values.put("noteStyle", info.setNoteStyleOption(mNoteUpdataDialog.getNoteOptionStyle()) );
                        DataSupport.updateAll(NoteInfo.class, values, "noteMark = ? ", info.getNoteMark() + "");
                        subscriber.onNext(info);
                    }
                }
                subscriber.onCompleted();
            }
        });

        Subscriber<NoteInfo> subscriber = new Subscriber<NoteInfo>() {
            LoadingProgressDialog dialog;

            @Override
            public void onStart() {
                super.onStart();
                dialog = new LoadingProgressDialog(getActivity());
                dialog.show();
                dialog.setTitle("请求...");
            }

            @Override
            public void onCompleted() {
                LogUtils.e(TAG, "onCompleted...");
                dialog.dismiss();


            }

            public void onError(Throwable e) {
                dialog.dismiss();
            }

            @Override
            public void onNext(NoteInfo noteInfo) {
                updataNoteInfo(noteInfo);
            }
        };

        mUpdateSub = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUpdateSub != null) {
            initScription.unsubscribe();
        }
    }
}