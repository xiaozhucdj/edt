package com.yougy.common.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.yougy.common.down.DownloadBookListener;
import com.yougy.common.down.NewDownBookInfo;
import com.yougy.common.down.NewDownBookManager;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.eventbus.EventBusConstant;
import com.yougy.common.global.FileContonst;
import com.yougy.common.manager.DialogManager;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.new_network.ApiException;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.DataCacheUtils;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.GsonUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.RefreshUtil;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.bean.NoteInfo;
import com.yougy.init.bean.BookInfo;
import com.yougy.shop.bean.DownloadInfo;
import com.yougy.ui.activity.R;
import com.yougy.view.dialog.DownBookDialog;
import com.yougy.view.dialog.UiPromptDialog;

import org.json.JSONObject;

import java.util.List;

import de.greenrobot.event.EventBus;
import rx.functions.Action1;
import rx.functions.Func1;
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
    public String tag;
    public ViewGroup mRootView;
    protected boolean mIsRefresh;
    //  英语（四年级104030010，三年级，103030023）  ，语文 （四年级 104010002 ，三年级103010007）
//    public String mMediasBookIDs = "103010007 ,103030023,104030010,104010002";
//    public String mMediasBookIDs = "12345";
    public void loadIntent(Class<?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        startActivity(intent);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
        tag = getClass().getName();
        LogUtils.e(tag, "onAttach...........");
        subscription = new CompositeSubscription();
        tapEventEmitter = YoungyApplicationManager.getRxBus(context).toObserverable().publish();
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
        tapEventEmitter = YoungyApplicationManager.getRxBus(context).toObserverable().publish();
        handleEvent();*/
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && mRootView != null) {
            invalidateDelayed(mRootView);
        }

        mHide = hidden;
        LogUtils.e(tag, "mhide is : " + hidden);
        if (subscription != null) {
            subscription.clear();
            subscription = null;
        }
        tapEventEmitter = null;
        if (!hidden) {
            subscription = new CompositeSubscription();
            tapEventEmitter = YoungyApplicationManager.getRxBus(context).toObserverable().publish();
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
            tapEventEmitter = YoungyApplicationManager.getRxBus(context).toObserverable().publish();
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
    //---------------------------------CancelAndDetermine--------------------------------------------

    /**
     * @param titleId 标题
     */
    protected void showCancelAndDetermineDialog(int titleId) {
        if (titleId == R.string.jump_to_net) {//网络重连
            DialogManager.newInstance().showNetConnDialog(getActivity());
            return;
        }
        if (mUiPromptDialog == null) {
            mUiPromptDialog = new UiPromptDialog(getActivity());
            mUiPromptDialog.setListener(this);
        }
        mUiPromptDialog.show();
        mUiPromptDialog.setTag(0);
        mUiPromptDialog.setTitle(titleId);
        mUiPromptDialog.setDialogStyle(false);
    }

    /**
     * @param titleId     标题
     * @param cancleId    取消按钮
     * @param determineId 确定按钮
     */
    protected void showCancelAndDetermineDialog(int titleId, int cancleId, int determineId) {
        if (mUiPromptDialog == null) {
            mUiPromptDialog = new UiPromptDialog(getActivity());
            mUiPromptDialog.setListener(this);
        }
        mUiPromptDialog.show();
        mUiPromptDialog.setTag(0);
        mUiPromptDialog.setTitle(titleId);
        mUiPromptDialog.setDialogStyle(false);
    }


    protected void showTagCancelAndDetermineDialog(int titleId, int tag) {
        if (mUiPromptDialog == null) {
            mUiPromptDialog = new UiPromptDialog(getActivity());
            mUiPromptDialog.setListener(this);
        }
        mUiPromptDialog.show();
        mUiPromptDialog.setTag(tag);
        mUiPromptDialog.setTitle(titleId);
        mUiPromptDialog.setDialogStyle(false);
    }


    /**
     * @param titleId     标题
     * @param cancleId    取消按钮
     * @param determineId 确定按钮
     * @param tag         tag 处理分类
     */
    protected void showTagCancelAndDetermineDialog(int titleId, int cancleId, int determineId, int tag) {
        if (mUiPromptDialog == null) {
            mUiPromptDialog = new UiPromptDialog(getActivity());
            mUiPromptDialog.setListener(this);
        }
        mUiPromptDialog.show();
        mUiPromptDialog.setTag(tag);
        mUiPromptDialog.setTitle(titleId);
        mUiPromptDialog.setCancel(cancleId);
        mUiPromptDialog.setConfirm(determineId);
        mUiPromptDialog.setDialogStyle(false);
    }


    //---------------------------------CenterDetermine--------------------------------------------

    /**
     * @param titleId 标题
     */
    protected void showCenterDetermineDialog(int titleId) {
        if (mUiPromptDialog == null) {
            mUiPromptDialog = new UiPromptDialog(getActivity());
            mUiPromptDialog.setListener(this);
        }
        mUiPromptDialog.show();
        mUiPromptDialog.setTag(0);
        mUiPromptDialog.setTitle(titleId);
        mUiPromptDialog.setDialogStyle(true);
    }

    /**
     * @param titleId 标题
     */
    protected void showCenterDetermineDialog(int titleId, int confirmId) {
        if (mUiPromptDialog == null) {
            mUiPromptDialog = new UiPromptDialog(getActivity());
            mUiPromptDialog.setListener(this);
        }
        mUiPromptDialog.show();
        mUiPromptDialog.setTag(0);
        mUiPromptDialog.setTitle(titleId);
        mUiPromptDialog.setCenterConfirm(confirmId);
        mUiPromptDialog.setDialogStyle(true);
    }

    /**
     * @param titleId 标题
     */
    protected void showTagCenterDetermineDialog(int titleId, int tag) {
        if (mUiPromptDialog == null) {
            mUiPromptDialog = new UiPromptDialog(getActivity());
            mUiPromptDialog.setListener(this);
        }
        mUiPromptDialog.show();
        mUiPromptDialog.setTag(tag);
        mUiPromptDialog.setTitle(titleId);
        mUiPromptDialog.setDialogStyle(true);
    }

    /**
     * @param titleId 标题
     */
    protected void showTagCenterDetermineDialog(int titleId, int confirmId, int tag) {
        if (mUiPromptDialog == null) {
            mUiPromptDialog = new UiPromptDialog(getActivity());
            mUiPromptDialog.setListener(this);
        }
        mUiPromptDialog.show();
        mUiPromptDialog.setTag(tag);
        mUiPromptDialog.setTitle(titleId);
        mUiPromptDialog.setCenterConfirm(confirmId);
        mUiPromptDialog.setDialogStyle(true);
    }


    //---------------------------------dissmiss--------------------------------------------

    protected void dissMissUiPromptDialog() {
        if (mUiPromptDialog != null && mUiPromptDialog.isShowing()) {
            mUiPromptDialog.dismiss();
        }
    }

    //---------------------------------listener--------------------------------------------
    @Override
    public void onUiCancelListener() {
        dissMissUiPromptDialog();
    }

    @Override
    public void onUiDetermineListener() {
        dissMissUiPromptDialog();
    }

    @Override
    public void onUiCenterDetermineListener() {
        dissMissUiPromptDialog();
    }


    private void savebookDownloadKey(int bookId, String key) {
        // 缓存key
        String keys = DataCacheUtils.getBookString(UIUtils.getContext(), FileContonst.DOWN_LOAD_BOOKS_KEY);
        try {
            JSONObject object;
            if (!StringUtils.isEmpty(keys)) {
                object = new JSONObject(keys);
            } else {
                object = new JSONObject();
            }
            object.put(bookId + "", key);
            DataCacheUtils.putBookString(UIUtils.getContext(), FileContonst.DOWN_LOAD_BOOKS_KEY, object.toString());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i("缓存密码出差");
        }
    }

    /**
     * 下载图书的dialog
     */
    protected DownBookDialog mDownDialog;

    /**
     * 下载 图书的 任务
     */

    public void downBookTask(int bookId) {
        if (mDownDialog == null) {
            mDownDialog = new DownBookDialog(getActivity());
        }
        mDownDialog.setListener(new DownBookDialog.DownBookListener() {
            @Override
            public void onCancelListener() {
                cancelDownBook(bookId);
            }

            @Override
            public void onConfirmListener() {
                confirmDownBook(bookId);
            }
        });
        mDownDialog.show();
        mDownDialog.getBtnConfirm().setVisibility(View.VISIBLE);
        mDownDialog.setTitle(UIUtils.getString(R.string.down_book_defult));
        mDownDialog.runConfirmClick();
    }

    /**
     * 取消下载图书
     */
    protected void cancelDownBook(int bookid) {
        mDownDialog.dismiss();
        NewDownBookManager.getInstance().cancel(bookid);
    }

    /**
     * 确认下载图书
     */
    protected void confirmDownBook(int bookId) {
        mDownDialog.getBtnConfirm().setVisibility(View.GONE);
        mDownDialog.setTitle(String.format(getActivity().getString(R.string.down_book_loading), 0 + "%"));
        queryBookDownLoadSyn(bookId);
    }

    /**
     * 查询下载图书 信息
     */
    private void queryBookDownLoadSyn(int bookId) {
        NetWorkManager.downloadBook(SpUtils.getUserId() + "", bookId + "").filter(new Func1<List<DownloadInfo>, Boolean>() {
            @Override
            public Boolean call(List<DownloadInfo> downloadInfos) {
                return downloadInfos != null;
            }
        }).subscribe(new Action1<List<DownloadInfo>>() {
            @Override
            public void call(List<DownloadInfo> downloadInfos) {

                if (downloadInfos != null && downloadInfos.size() > 0) {
                    //下载图书
                    savebookDownloadKey(bookId, downloadInfos.get(0).getAtchEncryptKey());
                    NewDownBookInfo info = new NewDownBookInfo();
                    info.setBookId(bookId);
                    info.setAccessKeyId(downloadInfos.get(0).getAccessKeyId());
                    info.setAccessKeySecret(downloadInfos.get(0).getAccessKeySecret());
                    info.setSecurityToken(downloadInfos.get(0).getSecurityToken());
                    info.setExpiration(downloadInfos.get(0).getExpiration());
                    info.setObjectKey(downloadInfos.get(0).getAtchRemotePath());
                    info.setBucketName(downloadInfos.get(0).getAtchBucket());
                    info.setSaveFilePath(FileUtils.getTextBookFilesDir() + bookId + ".pdf");
//                    System.out.println("to............"+info.toString());
                    downBook(info);
                } else {
                    mDownDialog.setTitle(UIUtils.getContext().getResources().getString(R.string.down_book_error));
                    mDownDialog.getBtnConfirm().setVisibility(View.VISIBLE);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                String msg = UIUtils.getContext().getResources().getString(R.string.down_book_error);
                if (throwable instanceof ApiException) {
                    String errorCode = ((ApiException) throwable).getCode();
                    LogUtils.i("resultCode" + errorCode);
                    if (errorCode.equals("404")) {
                        msg = "没有找到该图书";
                    }
                } else {
                }
                mDownDialog.setTitle(msg);
                mDownDialog.getBtnConfirm().setVisibility(View.VISIBLE);
            }
        });
    }

    private void downBook(NewDownBookInfo info) {

        NewDownBookManager.getInstance().downBookAsy(info, new DownloadBookListener() {
            @Override
            public void onSuccess(int progress) {
//                System.out.println(".........onSuccess...." + progress);
                UIUtils.getMainThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        mDownDialog.setTitle(String.format(getActivity().getString(R.string.down_book_loading), progress + "%"));
                    }
                });
            }

            @Override
            public void onFinish() {
//                System.out.println(".........onFinish");
                UIUtils.getMainThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        mDownDialog.dismiss();
                        //直接进入下载的图书
                        onDownBookFinish();
                    }
                });
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
//                System.out.println(".........onFailure");
                UIUtils.getMainThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        mDownDialog.setTitle(UIUtils.getContext().getResources().getString(R.string.down_book_error));
                        mDownDialog.getBtnConfirm().setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    /**
     * 图书下载完成后的回调
     */
    protected void onDownBookFinish() {

    }



    private Runnable mRefreshRun;

    public void invalidateDelayed(View view) {
        if (mRefreshRun == null) {
            mRefreshRun = new Runnable() {
                @Override
                public void run() {
                    RefreshUtil.invalidate(view);
                }
            };
        }
        UIUtils.getMainThreadHandler().postDelayed(mRefreshRun, 2000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRefreshRun != null) {
            UIUtils.getMainThreadHandler().removeCallbacks(mRefreshRun);
        }
    }
}