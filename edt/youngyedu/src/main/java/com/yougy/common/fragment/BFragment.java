package com.yougy.common.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YougyApplicationManager;
import com.yougy.common.utils.DataCacheUtils;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.home.bean.NoteInfo;
import com.yougy.init.bean.BookInfo;
import com.yougy.view.dialog.UiPromptDialog;

import java.util.List;

import de.greenrobot.event.EventBus;
import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;

import static com.yougy.common.utils.GsonUtil.fromNotes;

/**
 * Created by jiangliang on 2016/12/12.
 */

public abstract class BFragment extends Fragment implements UiPromptDialog.Listener {
    public boolean mHide;
    protected CompositeSubscription subscription;
    protected ConnectableObservable<Object> tapEventEmitter;
    protected Context context;
    private String tag;

    protected boolean mIsRefresh;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        tag = getClass().getName();
        LogUtils.e(tag, "onAttach...........");
        subscription = new CompositeSubscription();
        tapEventEmitter = YougyApplicationManager.getRxBus(context).toObserverable().publish();
        handleEvent();
        EventBus.getDefault().register(this);
    }

    protected void handleEvent() {
        subscription.add(tapEventEmitter.connect());
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.e(tag, "onResume...........");
      /*  subscription = new CompositeSubscription();
        tapEventEmitter = YougyApplicationManager.getRxBus(context).toObserverable().publish();
        handleEvent();*/
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mHide = hidden;
        LogUtils.e(tag, "mhide is : " + hidden);
        if (subscription != null) {
            subscription.clear();
            subscription = null;
        }
        tapEventEmitter = null;
        if (!hidden) {
            subscription = new CompositeSubscription();
            tapEventEmitter = YougyApplicationManager.getRxBus(context).toObserverable().publish();
            handleEvent();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.e(tag, "onDestroyView............");
        if (subscription != null) {
            subscription.clear();
        }
        subscription = null;
        tapEventEmitter = null;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        LogUtils.e(tag, "setMenuVisibility...");
        if (getView() != null) {
            getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
        }
        mHide = menuVisible;
        if (menuVisible && context != null) {
            subscription = new CompositeSubscription();
            tapEventEmitter = YougyApplicationManager.getRxBus(context).toObserverable().publish();
            handleEvent();
        } else {
            if (subscription != null) {
                subscription.clear();
                subscription = null;
            }
            tapEventEmitter = null;
        }
    }


    public void onEventMainThread(BaseEvent event) {
        if (event == null)
            return;
        if (event.getType().equalsIgnoreCase(EventBusConstant.need_refresh)) {
            mIsRefresh = true;
        }
    }

    protected List<BookInfo> getCacheBooks(String key) {
        List<BookInfo> books = null;
        String json = DataCacheUtils.getString(getActivity(), key);
        LogUtils.i("json..." + json);
        if (!StringUtils.isEmpty(json)) {
            books = GsonUtil.fromBooks(json);
        }
        return books;
    }

    /**
     * 获取缓存笔记的JSON+离线的JSON
     */
    protected List<NoteInfo> getCacheNotes(String key) {
        List<NoteInfo> notes = null;
        String json = DataCacheUtils.getString(getActivity(), key);
        if (!StringUtils.isEmpty(json)) {
            notes = fromNotes(json);
        }
        // 离线添加的笔记
        String offLineAddStr = DataCacheUtils.getString(getActivity(), NewProtocolManager.OffLineId.OFF_LINE_ADD);
        if (!StringUtils.isEmpty(offLineAddStr)) {
            if (notes != null) {
                notes.addAll(fromNotes(offLineAddStr));
            } else {
                notes = fromNotes(offLineAddStr);
                LogUtils.i("缓存没有 服务器的数据，当前显示的结果是 离线添加的笔记");
            }
        }
        return notes;
    }

    protected UiPromptDialog mUiPromptDialog;

    protected void showCancelAndDetermineDialog(String title) {

        if (mUiPromptDialog == null) {
            mUiPromptDialog = new UiPromptDialog(getActivity());
            mUiPromptDialog.setListener(this);
        }
        mUiPromptDialog.show();
        mUiPromptDialog.setTitle(title);
        mUiPromptDialog.setDialogStyle(false);
    }

    /**显示UI提示的对话框*/
    protected void showCancelAndDetermineDialog(int resID) {
        if (mUiPromptDialog == null) {
            mUiPromptDialog = new UiPromptDialog(getActivity());
            mUiPromptDialog.setListener(this);
        }
        mUiPromptDialog.show();
        mUiPromptDialog.setTitle(resID);
        mUiPromptDialog.setDialogStyle(false);
    }

    protected void showCenterDetermineDialog(String title) {

        if (mUiPromptDialog == null) {
            mUiPromptDialog = new UiPromptDialog(getActivity());
            mUiPromptDialog.setListener(this);
        }
        mUiPromptDialog.show();
        mUiPromptDialog.setTitle(title);
        mUiPromptDialog.setDialogStyle(true);
    }

    /**显示UI提示的对话框*/
    protected void showCenterDetermineDialog(int resID) {
        if (mUiPromptDialog == null) {
            mUiPromptDialog = new UiPromptDialog(getActivity());
            mUiPromptDialog.setListener(this);
        }
        mUiPromptDialog.show();
        mUiPromptDialog.setTitle(resID);
        mUiPromptDialog.setDialogStyle(true);
    }

    protected void dissMissUiPromptDialog( ) {
        if (mUiPromptDialog != null && mUiPromptDialog.isShowing()) {
            mUiPromptDialog.dismiss();
        }
    }

    @Override
    public void onUiCancelListener() {
    }

    @Override
    public void onUiDetermineListener() {
    }

    @Override
    public void onUiCenterDetermineListener() {

    }
}
