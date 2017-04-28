package com.yougy.init.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.download.DownloadListener;
import com.yougy.common.fragment.BFragment;
import com.yougy.common.global.Commons;
import com.yougy.common.manager.DownloadManager;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.nohttp.DownInfo;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.BookShelfCallBack;
import com.yougy.common.protocol.callback.StartCallBack;
import com.yougy.common.protocol.response.BookShelfProtocol;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.activity.MainActivity;
import com.yougy.init.bean.BookInfo;
import com.yougy.init.bean.UserInfo;
import com.yougy.init.manager.InitManager;
import com.yougy.ui.activity.R;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by jiangliang on 2016/10/14.
 */

public class StartUseFragment extends BFragment implements View.OnClickListener {

    private TextView mSchoolTv;
    private TextView mClassTv;
    private TextView mStudentNameTv;
    private TextView mStudentNumberTv;
    private Button mStartUseBtn;
    private LinearLayout mLinearLayout;
    private List<DownInfo> mFiles = new ArrayList<>();
    private RelativeLayout mRlDown;
    private TextView mTvProgress;

    private static final String TAG = "StartUseFragment";

    @Override
    protected void handleEvent() {
        handleStartEvent();
        handleBookShelfEvent();
        super.handleEvent();
    }

    private void handleStartEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof UserInfo.User) {
                    Log.e(TAG,"handleStartEvent...");
                    UserInfo.User user = (UserInfo.User) o;
                    SpUtil.saveAccountId(user.getUserId());
                    SpUtil.saveAccountSchool(user.getSchoolName());
                    SpUtil.saveAccountClass(user.getClassName());
                    SpUtil.saveAccountNumber(user.getUserNumber());
                    SpUtil.saveGradeName(user.getGradeName());
                    SpUtil.saveAccountName(user.getUserRealName());
                    SpUtil.saveSubjectNames(user.getSubjectNames());
                    startActivity(new Intent(context, MainActivity.class));
                    getActivity().finish();
                }
            }
        }));
    }

    private void handleBookShelfEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof BookShelfProtocol) {
                    Log.e(TAG,"handleBookShelfEvent...");
                    BookShelfProtocol bookShelf = (BookShelfProtocol) o;
                    if (bookShelf.getBookList() != null && bookShelf.getBookList().size() > 0) {
                        // 初始化 ，下载PDF
                        mLinearLayout.setVisibility(View.GONE);
                        mRlDown.setVisibility(View.VISIBLE);
                        mFiles.clear();

                        /***遍历数据  适配 Bean下载*/
                        int index = 1;
                        for (BookInfo bookInfo : bookShelf.getBookList()) {
                            //判断本地是否有此书
                            String filePath = FileUtils.getTextBookFilesDir() + bookInfo.getBookId() + ".pdf";
                            if (!FileUtils.exists(filePath)) {
                                DownInfo info = new DownInfo(bookInfo.getBookDownload(), FileUtils.getTextBookFilesDir(), bookInfo.getBookId() + ".pdf", true, false, bookInfo.getBookId());
                                info.setBookName(bookInfo.getBookTitle());
                                info.setIndex(index++);
                                mFiles.add(info);
                            }
                        }

                        if (mFiles.size() > 0) {
                            //下载
                            downFiles();
                        } else {
                            //书全部下载完成后 还需要我们手动去调用一下 绑定 登录  性质不一样 需要我们重新登录接口。
                            //作业可能还需要获取token
                            ProtocolManager.loginProtocol(Commons.UUID, ProtocolId.PROTOCOL_ID_LOGIN, new StartCallBack(getActivity()));
                        }
                    } else {
                        //书全部下载完成后 还需要我们手动去调用一下 绑定 登录  性质不一样 需要我们重新登录接口。
                        //作业可能还需要获取token
                        ProtocolManager.loginProtocol(Commons.UUID, ProtocolId.PROTOCOL_ID_LOGIN, new StartCallBack(getActivity()));
                    }
                }
            }
        }));
        subscription.add(tapEventEmitter.connect());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_use, container, false);
        mSchoolTv = (TextView) view.findViewById(R.id.info_school);
        mClassTv = (TextView) view.findViewById(R.id.info_class);
        mStudentNameTv = (TextView) view.findViewById(R.id.info_name);
        mStudentNumberTv = (TextView) view.findViewById(R.id.info_number);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.linear_layout);
        mStartUseBtn = (Button) view.findViewById(R.id.start_use);
        mStartUseBtn.setOnClickListener(this);

        mSchoolTv.setText(String.format(getString(R.string.info_school), InitManager.getInstance().getSchoolName()));
        mClassTv.setText(String.format(getString(R.string.info_class), InitManager.getInstance().getClassName()));
        mStudentNameTv.setText(String.format(getString(R.string.info_name), InitManager.getInstance().getStudentName()));
        mStudentNumberTv.setText(String.format(getString(R.string.info_number), InitManager.getInstance().getStudentNumber()));
        mRlDown = (RelativeLayout) view.findViewById(R.id.rl_down);

        mTvProgress = (TextView) view.findViewById(R.id.tv_progress);
        return view;
    }

    @Override
    public void onClick(View v) {
        // 获取订单列表 ，得到下载书条目
        ProtocolManager.bookShelfProtocol(Integer.parseInt(SpUtil.getAccountId()), -1, -1, "", ProtocolId.PROTOCOL_ID_BOOK_SHELF, new BookShelfCallBack(getActivity()));
    }

    /***
     * 文件下载，下载位置 ，FileUtils.getTextBookFilesDir()
     */
    private void downFiles() {
        DownloadManager.downloadFile(mFiles, new DownloadListener() {
            @Override
            public void onDownloadError(int what, Exception exception) {
                LogUtils.e("  onDownloadError     what ........" + what);
                DownloadManager.cancel();
//                mLinearLayout.setVisibility(View.VISIBLE);
//                mRlDown.setVisibility(View.GONE);
                ProtocolManager.loginProtocol(Commons.UUID, ProtocolId.PROTOCOL_ID_LOGIN, new StartCallBack(getActivity()));
            }

            @Override
            public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
                LogUtils.e("  onStart     what ........" + what);
            }

            @Override
            public void onProgress(int what, int progress, long fileCount) {
                LogUtils.e("  onProgress     what ........" + what + "....progress" + progress);
                mTvProgress.setText(String.format(getString(R.string.down_file_progress), mFiles.size(), DownloadManager.getIndex(what)) + "%" + progress);
            }

            @Override
            public void onFinish(int what, String filePath) {
                LogUtils.e("  onFinish     what ........" + what + "....filePath" + filePath);
                // 下载完成后 回调 onFinish ，request.isFinished() 函数不能立刻反应下来 所以延迟3S 后在查询结果
                if (DownloadManager.isFinish()) {
                    LogUtils.e("  onFinish     全部下载完成");
                    UIUtils.showToastSafe("全部下载完成", Toast.LENGTH_LONG);
                    //书全部下载完成后 还需要我们手动去调用一下 绑定 登录  性质不一样 需要我们重新登录接口。
                    //作业可能还需要获取token
                    ProtocolManager.loginProtocol(Commons.UUID, ProtocolId.PROTOCOL_ID_LOGIN, new StartCallBack(getActivity()));
                }
            }

            @Override
            public void onCancel(int what) {
                LogUtils.i("  onCancel     what ........" + what);
                UIUtils.showToastSafe("下载失败", Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DownloadManager.cancel();
    }
}
