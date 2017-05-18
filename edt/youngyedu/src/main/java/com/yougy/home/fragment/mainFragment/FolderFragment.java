package com.yougy.home.fragment.mainFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.download.DownloadListener;
import com.yougy.common.fragment.BFragment;
import com.yougy.common.manager.DownloadManager;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.nohttp.DownInfo;
import com.yougy.common.utils.FileUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.home.activity.MainActivity;
import com.yougy.home.imple.RefreshBooksListener;
import com.yougy.ui.activity.R;
import com.zhy.http.okhttp.callback.Callback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by Administrator on 2016/7/12.
 * 文件夹
 */
public class FolderFragment extends BFragment implements View.OnClickListener {
    private ViewGroup mRootView;
    private Button mBtnLogin;
    private Button mBtnFileHas;
    private ArrayList<String> fiels;
    private Button mBtnDeleteFile;
    private Button mBtnBreakpoint;
    private Button mBtnCount;
    private Button mBtnDrable;
    private ImageView mImgDrable;
    private Button mBtnSize;
    private TextView mTvSize;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_folder, null);
        mBtnLogin = (Button) mRootView.findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(this);

        mBtnFileHas = (Button) mRootView.findViewById(R.id.btn_fileHas);
        mBtnFileHas.setOnClickListener(this);

        mBtnBreakpoint = (Button) mRootView.findViewById(R.id.btn_breakpoint);

        fiels = new ArrayList<>();
        fiels.add("10001.pdf");
        fiels.add("10002.pdf");
        fiels.add("10003.pdf");

        mBtnDeleteFile = (Button) mRootView.findViewById(R.id.btn_deleteFile);
        mBtnDeleteFile.setOnClickListener(this);

        mBtnBreakpoint = (Button) mRootView.findViewById(R.id.btn_breakpoint);
        mBtnBreakpoint.setOnClickListener(this);

        mBtnCount = (Button) mRootView.findViewById(R.id.btn_todocount);
        mBtnCount.setOnClickListener(this);

        mBtnDrable = (Button) mRootView.findViewById(R.id.btn_testDrable);
        mBtnDrable.setOnClickListener(this);
        mImgDrable = (ImageView) mRootView.findViewById(R.id.img);

        mBtnSize = (Button) mRootView.findViewById(R.id.btn_size);
        mBtnSize.setOnClickListener(this);


        mTvSize = (TextView) mRootView.findViewById(R.id.tv_size);
        return mRootView;

    }


    @Override
    public void onHiddenChanged(boolean hidden) {

        super.onHiddenChanged(hidden);

        if (!hidden) {
            LogUtils.i("当前---文件夹");
            setRefreshListener();
        }
    }

    private void setRefreshListener() {
        SearchImple imple = new SearchImple();
        ((MainActivity) getActivity()).setRefreshListener(imple);
    }

    class SearchImple implements RefreshBooksListener {
        @Override
        public void onRefreshClickListener() {
//            loadData();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                testDownFiles();
                break;
            case R.id.btn_fileHas:

                for (String name : fiels) {
                    String str = FileUtils.getTextBookFilesDir() + name;
                    LogUtils.i("判断文件夹是否存在 " + name + "是否成功 == " + FileUtils.exists(str));
                }
                break;

            case R.id.btn_deleteFile:

                for (String name : fiels) {
                    String str = FileUtils.getTextBookFilesDir() + name;
                    LogUtils.i("删除全部文件 " + name + "是否成功 == " + FileUtils.delFileOrFolder(str));
                }
                break;

            case R.id.btn_breakpoint:
                DownloadManager.cancel();
                break;

            case R.id.btn_todocount:
                ProtocolManager.getHomework_todo_count(SpUtil.getAccountId(), 1, new Callback() {
                    @Override
                    public Object parseNetworkResponse(Response response, int id) throws Exception {
                        LogUtils.i("  response.body().string() ==" + response.body().string());
                        return null;
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        LogUtils.i("onError..........................");
                    }

                    @Override
                    public void onResponse(Object response, int id) {

                    }
                });
                break;

            case R.id.btn_testDrable:
//                mImgDrable.setImageDrawable(UIUtils.getDrawable(R.drawable.drabke_e));
                break;
            case R.id.btn_size:
                testSize();
                break;
        }
    }


    private void testDownFiles() {
        /**
         *
         * @param url         下载地址。
         * @param fileFolder  保存的文件夹。
         * @param filename    文件名。
         * @param isRange     是否断点续传下载。
         * @param isDeleteOld 如果发现存在同名文件，是否删除后重新下载，如果不删除，则直接下载成功
         * @param what 请求标识
         */

        String str1 = "http://192.168.12.2:8080/leke_platform/pupload/preview20160930115152.pdf";
        String str2 = "http://192.168.12.2:8080/leke_platform/dupload/download20160930115204.pdf";
        List<DownInfo> downInfos = new ArrayList<>();
        DownInfo info1 = new DownInfo(str1, FileUtils.getTextBookFilesDir(), "10001.pdf", true, false, 1);
        DownInfo info2 = new DownInfo(str2, FileUtils.getTextBookFilesDir(), "10002.pdf", true, false, 2);

        downInfos.add(info1);
        downInfos.add(info2);

        DownloadManager.downloadFile(downInfos, new DownloadListener() {
            @Override
            public void onDownloadError(int what, Exception exception) {
                LogUtils.i("  onDownloadError     what ........" + what);
                DownloadManager.cancel();
            }

            @Override
            public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
                LogUtils.i("  onStart     what ........" + what);
            }

            @Override
            public void onProgress(int what, int progress, long fileCount) {
                LogUtils.i("  onProgress     what ........" + what + "....progress" + progress);

            }

            @Override
            public void onFinish(int what, String filePath) {
                LogUtils.i("  onFinish     what ........" + what + "....filePath" + filePath);
                // 下载完成后 回调 onFinish ，request.isFinished() 函数不能立刻反应下来 所以延迟3S 后在查询结果
                if (DownloadManager.isFinish()) {
                    LogUtils.i("  onFinish     全部下载完成");
                    UIUtils.showToastSafe("全部下载完成", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onCancel(int what) {
                LogUtils.i("  onCancel     what ........" + what);
                DownloadManager.cancel();
                UIUtils.showToastSafe("下载失败", Toast.LENGTH_LONG);
            }
        });
    }

    private void testSize() {
        int w = UIUtils.getScreenHeight();
        int h = UIUtils.getScreenWidth();
        LogUtils.i("getScreenHeight==" + w);
        LogUtils.i("getScreenWidth==" + h);
        mTvSize.setText("w==" + w + ",h==" + h);
    }
}
