package com.yougy.task.activity;


import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.google.gson.Gson;
import com.yougy.anwser.STSResultbean;
import com.yougy.anwser.STSbean;
import com.yougy.common.activity.BaseActivity;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.global.Commons;
import com.yougy.common.manager.DialogManager;
import com.yougy.common.manager.YoungyApplicationManager;
import com.yougy.common.new_network.ApiException;
import com.yougy.common.new_network.NetWorkManager;
import com.yougy.common.utils.DateUtils;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.NetUtils;
import com.yougy.common.utils.SpUtils;
import com.yougy.common.utils.ToastUtil;
import com.yougy.common.utils.UIUtils;
import com.yougy.message.attachment.TaskRemindAttachment;
import com.yougy.task.bean.StageTaskBean;
import com.yougy.task.bean.SubmitTaskBean;
import com.yougy.task.fragment.MaterialsBaseFragment;
import com.yougy.task.fragment.PracticeBaseFragment;
import com.yougy.task.fragment.SignatureFragment;
import com.yougy.task.fragment.TaskContentBaseFragment;
import com.yougy.task.fragment.TaskBaseFragment;
import com.yougy.ui.activity.R;
import com.yougy.view.dialog.LoadingProgressDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TaskDetailStudentActivity extends BaseActivity {

    @BindView(R.id.task_student_details)
    ConstraintLayout mConstraintLayout;
    @BindView(R.id.image_back)
    ImageView mImageBack;
    @BindView(R.id.image_refresh)
    ImageView mImageRefresh;
    @BindView(R.id.top_title)
    TextView mTopTitle;
    @BindView(R.id.tab_content)
    TextView mTabContent;
    @BindView(R.id.tab_materials)
    TextView mTabMaterials;
    @BindView(R.id.tab_practice)
    TextView mTabPractice;
    @BindView(R.id.task_tab)
    LinearLayout mTaskTab;
    @BindView(R.id.task_content)
    FrameLayout mTaskContent;
    @BindView(R.id.top_title_layout)
    View mTopLayout;
    @BindView(R.id.btn_confirm)
    TextView mTextFinish;
    @BindView(R.id.frame_signature)
    FrameLayout mFrameSignature;

    public int dramaId = 160;
    public int mTaskId ;

    public static boolean isIntercept = false;//是否可写状态
    public static boolean isHandPaintedPattern = false;//是否手绘模式

    public static final String EVENT_TYPE_SCRIBBLE_MODE = "task_scribbleMode";
    public static final String EVENT_TYPE_LOAD_DATA = "event_type_load_data";
    public static final String EVENT_TYPE_LOAD_DATA_FAIL = "event_type_load_data_fail";
    public static final String EVENT_TYPE_COMMIT_STATE = "event_type_commit_state";


    private String currentTitleStr = "任务详情";
    /*是否家长签字任务*/
    private boolean isSignatureTask = false;
    /*是否已经签字*/
    private boolean hadSignature = false;
    /*是否已提交*/
    private boolean isHadCommit = false;


    private TaskContentBaseFragment mTaskContentFragment;
    private PracticeBaseFragment mPracticeFragment;
    private MaterialsBaseFragment mMaterialsFragment;
    private SignatureFragment mSignatureFragment;
    private FragmentManager mFragmentManager;


    public static final String TAB_CONTENT = "tab_content";
    public static final String TAB_MATERIALS = "tab_materials";
    public static final String TAB_PRACTICE = "tab_practice";
    public String currentTab = "";

    private Unbinder mUnbinder;
//    private boolean isNetConnected = true;
    public boolean isLoading = false;

    private List<StageTaskBean> mStageTaskBeans = new ArrayList<>();

    public List<StageTaskBean> getStageTaskBeans () {
        return mStageTaskBeans;
    }

//    @Override
//    public void onEventMainThread(BaseEvent event) {
//        super.onEventMainThread(event);
//        if (event.getType().equals(EventBusConstant.EVENT_WIIF)) {
//            boolean currentNetState = NetUtils.isNetConnected();
//            if (currentNetState && !isNetConnected) {
//                loadData();
//            }
//            isNetConnected = currentNetState;
//        }
//    }

    @Override
    protected void setContentView() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_task_student_details, null);
        setContentView(view);
        mUnbinder = ButterKnife.bind(R.layout.top_title_bar, mTopLayout);
    }

    @Override
    public void init() {
//        mTask = (Task) getIntent().getSerializableExtra("taskBean");
        currentTitleStr = getIntent().getStringExtra(TaskRemindAttachment.KEY_TASK_NAME);
        mTaskId = getIntent().getIntExtra(TaskRemindAttachment.KEY_TASK_ID, 0);
        dramaId = getIntent().getIntExtra(TaskRemindAttachment.KEY_TASK_ID_DEST, 0);
        mTopTitle.setText(currentTitleStr);
        // SV01 进行中   SV02 已完成   SV03 已检查
        isHadCommit = ("SV02").equals(getIntent().getStringExtra("ContentStatusCode"));
        isSignatureTask = getIntent().getBooleanExtra("isSign", false);
        LogUtils.e(tag,"dramaId : " + dramaId);
        initContentFragment();
    }

    @Override
    protected void initLayout() {
        if (isHadCommit) {
            if (isSignatureTask) {
                if (hadSignature) {
                    mTextFinish.setVisibility(View.GONE);
                } else {
                    mTextFinish.setText(getStrText(R.string.parent_sign));
                }
            } else {
                mTextFinish.setVisibility(View.GONE);
            }
        }
    }

    public boolean isBottomBtnShow () {
        return mTextFinish.getVisibility() == View.VISIBLE;
    }

    private void initContentFragment() {
        mFragmentManager = getSupportFragmentManager();
        mTaskContentFragment = new TaskContentBaseFragment();
        mPracticeFragment = new PracticeBaseFragment();
        mMaterialsFragment = new MaterialsBaseFragment();
        if (isSignatureTask) mSignatureFragment = new SignatureFragment();
        showContentFragment(mTaskContentFragment, TAB_CONTENT, false);
    }


    @Override
    public void loadData() {
        Log.i(TaskBaseFragment.TAG, "loadData: activity : "  + currentTab);
        isLoading = true;
        if (isSignatureTask) {
            NetWorkManager.queryStageTask(String.valueOf(dramaId), "SR04", SpUtils.getUserId())
                    .subscribe(stageTaskBeans -> {
                        isLoading = false;
                        LogUtils.d("TaskTest  hadSignature :" + stageTaskBeans.size());
                        if (stageTaskBeans.size() > 0) {
                            StageTaskBean stageTaskBean = stageTaskBeans.get(0);
                            if (stageTaskBean.getStageScene().size() > 0 ) {
                                LogUtils.d("TaskTest hadSignature : " + stageTaskBean.toString());
                                hadSignature = true;
                            }
                        }
                        initLayout();
                        NetWorkManager.queryStageTask(String.valueOf(dramaId), getStageTypeCode(), SpUtils.getUserId())
                                .subscribe(stageTaskBeans2 -> {
                                            isLoading = false;
                                            mStageTaskBeans.clear();
                                            mStageTaskBeans.addAll(stageTaskBeans2);
                                            if (mCurrentFragment != null) {
                                                mCurrentFragment.setServerFail(false);
                                            }
                                            EventBus.getDefault().post(new BaseEvent(EVENT_TYPE_LOAD_DATA));
                                        },
                                        throwable -> {
                                            isLoading = false;
                                            mStageTaskBeans.clear();
                                            if (throwable!=null) {
                                                String errorMsg = throwable.getMessage();
                                                if (!NetUtils.isNetConnected()) errorMsg = "网络连接不正常，请检查您的网络!";
                                                EventBus.getDefault().post(new BaseEvent(EVENT_TYPE_LOAD_DATA_FAIL, errorMsg));
                                                LogUtils.e("server reply error:" + throwable.getMessage());
                                            }
                                        });
                    }, throwable -> {
                        isLoading = false;
                        hadSignature = false;
                            });
        } else {
            NetWorkManager.queryStageTask(String.valueOf(dramaId), getStageTypeCode(), SpUtils.getUserId())
                    .subscribe(stageTaskBeans2 -> {
                                isLoading = false;
                                mStageTaskBeans.clear();
                                mStageTaskBeans.addAll(stageTaskBeans2);
                                if (mCurrentFragment != null) {
                                    mCurrentFragment.setServerFail(false);
                                }
                                EventBus.getDefault().post(new BaseEvent(EVENT_TYPE_LOAD_DATA));
                            },
                            throwable -> {
                                isLoading = false;
                                mStageTaskBeans.clear();
                                if (throwable!=null) {
                                    String errorMsg = throwable.getMessage();
                                    if (!NetUtils.isNetConnected()) errorMsg = "网络连接不正常，请检查您的网络!";
                                    EventBus.getDefault().post(new BaseEvent(EVENT_TYPE_LOAD_DATA_FAIL, errorMsg));
                                    LogUtils.e("server reply error:" + throwable.getMessage());
                                }
                            });
        }

    }

    @Override
    protected void refreshView() {

    }

    @OnClick({R.id.image_back, R.id.image_refresh, R.id.tab_content, R.id.tab_materials, R.id.tab_practice , R.id.btn_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                back();
                break;
            case R.id.image_refresh:
                loadData();
                break;
            case R.id.tab_content:
                showContentFragment(mTaskContentFragment, TAB_CONTENT, true);
                break;
            case R.id.tab_materials:
                showContentFragment(mMaterialsFragment, TAB_MATERIALS, true);
                break;
            case R.id.tab_practice:
                showContentFragment(mPracticeFragment, TAB_PRACTICE, true);
                break;
            case R.id.btn_confirm:
                finishedTask ();
                break;
        }
    }

    private void back() {
        if (isHadCommit || getString(R.string.parent_sign).equals(mTextFinish.getText().toString())) {//已经提交
            if (isSignatureTask && !hadSignature) {
                DialogManager.newInstance().showSubmitConfirmDialog(this, getString(R.string.unable_sign_temporary),
                        R.string.now_sign,
                        R.string.temporary_no_sign, new DialogManager.DialogCallBack() {
                            @Override
                            public void confirm() {
                                replaceFragment(mSignatureFragment, R.id.frame_signature, null);
                            }

                            @Override
                            public void cancel() {
                                super.cancel();
                                setResult(10000);
                                finish();
                            }
                        });
            } else {
                setResult(10000);
                finish();
            }
        } else { //未提交返回
            finish();
        }
    }

    private TaskBaseFragment mCurrentFragment;
    private void showContentFragment(TaskBaseFragment fragment, String tag, boolean isLoadData) {
        if (currentTab.equals(TAB_PRACTICE) && !tag.equals(TAB_PRACTICE)) {
            mPracticeFragment.dismissPopupWindow();
        }
        if (tag.equals(currentTab)) {
            LogUtils.d("current tab = " + currentTab + "  tag = " + tag);
            return;
        }
        LogUtils.d("task current show Fragment : " + tag + "   currentTab = " + currentTab);
        currentTab = tag;
        mCurrentFragment= fragment;
        setTabSelectState();
        replaceFragment(fragment, R.id.task_content, tag);
        if (isLoadData) {
            mStageTaskBeans.clear();
            loadData();
        }
    }

    private void replaceFragment (TaskBaseFragment fragment, int containerId, String tag) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (tag != null) fragmentTransaction.replace(containerId, fragment, tag);
        else fragmentTransaction.replace(containerId, fragment);
        if (containerId == R.id.frame_signature)
            mFrameSignature.setVisibility(View.VISIBLE);
        fragmentTransaction.commit();
    }

    private void removeFragment (int containerId) {
        Fragment fragmentById = mFragmentManager.findFragmentById(containerId);
        if (fragmentById != null) {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.remove(fragmentById);
            fragmentTransaction.commit();
        }
    }

    private void setTabSelectState () {
        mTabContent.setSelected(false);
        mTabMaterials.setSelected(false);
        mTabPractice.setSelected(false);
        switch (currentTab) {
            case TAB_CONTENT:
                mTabContent.setSelected(true);
                break;
            case TAB_MATERIALS:
                mTabMaterials.setSelected(true);
                break;
            case TAB_PRACTICE:
                mTabPractice.setSelected(true);
                break;
        }
    }

    protected void finishedTask () {
        if (currentTab.equals(TAB_PRACTICE)){
            mPracticeFragment.dismissPopupWindow();
        }
        LogUtils.d("TaskTest finishedTask, isHadCommit  = " + isHadCommit
                    + "   isSignatureTask = " + isSignatureTask + "  hadSignature = " + hadSignature);
        if (getString(R.string.parent_sign).equals(mTextFinish.getText().toString()) && isSignatureTask) {
            replaceFragment(mSignatureFragment, R.id.frame_signature, null);
            return;
        }
//        if (isHadCommit && isSignatureTask) {
//            replaceFragment(mSignatureFragment, R.id.frame_signature, null);
//            return;
//        }
        showDialog (getString(R.string.is_finished_task), R.string.confirm, R.string.cancel);
    }

    private void showDialog (String title, int confirm , int cancel) {
        EventBus.getDefault().post(new BaseEvent(EVENT_TYPE_SCRIBBLE_MODE, false));
        DialogManager.newInstance().showSubmitConfirmDialog(this, title, confirm, cancel
                , new DialogManager.DialogCallBack(){

                    @Override
                    public void confirm() {
                        if (currentTab.equals(TAB_PRACTICE)) {
                            mPracticeFragment.saveCurrentPractice();
                        }
                        oosUpload();
                    }

                    @Override
                    public void cancel() {
                        super.cancel();
                        EventBus.getDefault().post(new BaseEvent(EVENT_TYPE_SCRIBBLE_MODE, true));
                        UIUtils.postDelayed(() -> invalidate(), 100);
                    }
                });
    }
    LoadingProgressDialog loadingProgressDialog;
    private void oosUpload () {
        if (loadingProgressDialog == null) {
            loadingProgressDialog = new LoadingProgressDialog(TaskDetailStudentActivity.this);
            loadingProgressDialog.show();
            if (getString(R.string.parent_sign).equals(mTextFinish.getText().toString())) {
                loadingProgressDialog.setTitle("签字提交中...");
            } else {
                loadingProgressDialog.setTitle("任务提交中...");
            }

        }
        NetWorkManager.uploadTaskPracticeOOS(SpUtils.getUserId())
                .subscribe(stSbean -> {
                    if (stSbean == null) {
                        ToastUtil.showCustomToast(TaskDetailStudentActivity.this.getApplicationContext(), "获取上传信息失败");
                    } else {
                        LogUtils.d("TaskTest upload oos success.");
                        TaskDetailStudentActivity.this.uploadPic(stSbean);
                    }
                }, throwable -> {
                    if (loadingProgressDialog != null) {
                        loadingProgressDialog.dismiss();
                        loadingProgressDialog = null;
                    }
                    ToastUtil.showCustomToast(TaskDetailStudentActivity.this.getApplicationContext(), "获取上传信息失败");
                    LogUtils.e("TaskTest 获取上传信息失败!");} );
    }

    private void uploadPic (STSbean stSbean) {
        LogUtils.d("TaskTest submit server.");

        File file = new File(SaveNoteUtils.getInstance(getApplicationContext()).getTaskFileDir() + "/" + mTaskId);
        if (!file.exists() || file.listFiles().length == 0) {
            LogUtils.e("TaskTest not exits note.");
            NetWorkManager.updateHomeworkContent(SpUtils.getUserId(), mTaskId, "SV02")
                    .subscribe(o -> {
                        if (!isHadCommit && isSignatureTask) {
                            isHadCommit = true;
                            mTextFinish.setText(R.string.parent_sign);
                            EventBus.getDefault().post(new BaseEvent(EVENT_TYPE_COMMIT_STATE, true));
                        } else {
                            //上传后清理掉本地图片文件
                            setResult(10000);
                            TaskDetailStudentActivity.this.finish();
                        }
                        if (loadingProgressDialog != null) {
                            loadingProgressDialog.dismiss();
                            loadingProgressDialog = null;
                        }
                        ToastUtil.showCustomToast(TaskDetailStudentActivity.this.getBaseContext(), "提交完毕");
                    }, throwable -> {
                        if (loadingProgressDialog != null) {
                            loadingProgressDialog.dismiss();
                            loadingProgressDialog = null;
                        }
                        ToastUtil.showCustomToast(TaskDetailStudentActivity.this.getBaseContext(), "提交失败，请重试！");
                    });

//                boolean mkdirs = file.mkdirs();
//                File fi = new File(file.getAbsolutePath() +"/" + mTaskId + "/" + mTaskId + "_" + dramaId
//                            + "_empty.png");
//                boolean mkdir = fi.mkdir();
//                LogUtils.d("TaskTest empty mkdirs." + mkdirs + "  mkdir = " + mkdir);
        } else {//存在笔记要提交

            String endpoint = Commons.ENDPOINT;

            LogUtils.d("TaskTest uploadPic  :" + stSbean.getAccessKeyId() );

            OSSCredentialProvider credentialProvider = new OSSFederationCredentialProvider() {
                @Override
                public OSSFederationToken getFederationToken() {
                    return new OSSFederationToken(stSbean.getAccessKeyId(), stSbean.getAccessKeySecret(), stSbean.getSecurityToken(), stSbean.getExpiration());
                }
            };
            //该配置类如果不设置，会有默认配置，具体可看该类
            ClientConfiguration conf = new ClientConfiguration();
            conf.setConnectionTimeout(15 * 10000); // 连接超时，默认15秒
            conf.setSocketTimeout(15 * 10000); // socket超时，默认15秒
            conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
            conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
            OSSLog.enableLog();
            OSS oss = new OSSClient(YoungyApplicationManager.getContext(), endpoint, credentialProvider, conf);


            SubmitTaskBean submitTaskBean = new SubmitTaskBean();
            Observable.create(subscriber -> {
                File[] files = file.listFiles();
                List<File> fileLists = Arrays.asList(files);
                LogUtils.d("TaskTest path :" + file.getAbsolutePath() + "  size = " + fileLists.size() + "files length = " + files.length);
                ArrayList<SubmitTaskBean.SubmitTask> submitTasks = new ArrayList<>();
                for (int i = 0; i < fileLists.size(); i++) {
                    File f1 = fileLists.get(i);
                    File[] files1 = f1.listFiles();
                    List<File> files2 = Arrays.asList(files1);

                    ArrayList<STSResultbean> picContent = new ArrayList<>();
                    for (File file1 : files2) {
                        String picPath = file1.getAbsolutePath();
                        String picName = picPath.substring(picPath.lastIndexOf("/"));
                        LogUtils.d("TaskTest picName = " + picName);
                        PutObjectRequest put = new PutObjectRequest(stSbean.getBucketName(), stSbean.getPath() + picName, picPath);
                        try {
                            oss.putObject(put);
                            STSResultbean stsResultbean = new STSResultbean();
                            stsResultbean.setBucket(stSbean.getBucketName());
                            stsResultbean.setRemote(stSbean.getPath() + picName);
                            stsResultbean.setSize(file1.length());
                            picContent.add(stsResultbean);
                        } catch (ClientException e) {
                            e.printStackTrace();
                            Log.e("TaskTest UPLOAD OOS", "call: error message:  " + e.getMessage());
                        } catch (ServiceException e) {
                            e.printStackTrace();
                            Log.e("TaskTest UPLOAD OOS", "call: error Code:  " + e.getErrorCode() + " message:" + e.getRawMessage());
                        }
                    }
//                int size = picContent.size();
                    int stageId = 0;
                    if (picContent.size() > 0) {
                        String remote = picContent.get(0).getRemote();
                        LogUtils.d("TaskTest remote :" + remote);//138201/10000002607/2019/569_4274_task_practice_bitmap_0_0.png
                        String[] strings = remote.split("_");
                        if (strings.length > 1) {
                            stageId = Integer.parseInt(strings[1]);
                        }
                        SubmitTaskBean.SubmitTask submitTask = new SubmitTaskBean.SubmitTask();
                        submitTask.setPicContent(picContent);
                        LogUtils.d(  "TaskTest stageId = " + stageId);
                        submitTask.setPerformId(mTaskId);
                        submitTask.setStageId(stageId);
                        submitTask.setSceneCreateTime(DateUtils.getCalendarAndTimeString());
                        submitTasks.add(submitTask);
                    }
                    LogUtils.d("TaskTest submitTasks size = "  + submitTasks.size());
                }
                submitTaskBean.setSubmitTasks(submitTasks);
                subscriber.onNext(new Object());//将执行结果返回
                subscriber.onCompleted();//结束异步任务

            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Object>() {

                        @Override
                        public void onStart() {
                            super.onStart();
//                            if (loadingProgressDialog == null) {
//                                loadingProgressDialog = new LoadingProgressDialog(TaskDetailStudentActivity.this);
//                                loadingProgressDialog.show();
//                                loadingProgressDialog.setTitle("任务提交中...");
//                            }
                        }

                        @Override
                        public void onCompleted() {
                            LogUtils.d("TaskTest submitToServer.");
//                            if (loadingProgressDialog != null) {
//                                loadingProgressDialog.dismiss();
//                                loadingProgressDialog = null;
//                            }
                            TaskDetailStudentActivity.this.submitToServer(submitTaskBean);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("OOS", "onError: oos upload fail. " + e.getMessage());
                            if (loadingProgressDialog != null) {
                                loadingProgressDialog.dismiss();
                                loadingProgressDialog = null;
                            }
                        }

                        @Override
                        public void onNext(Object o) {
//                            if (loadingProgressDialog != null) {
//                                loadingProgressDialog.dismiss();
//                                loadingProgressDialog = null;
//                            }
                        }
                    });
        }


    }

    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    private File mCurrentFile;
    private void submitToServer (SubmitTaskBean submitTaskBean) {
        String toJson = new Gson().toJson(submitTaskBean.getSubmitTasks());
        LogUtils.d("TaskTest  json : " + toJson);
        NetWorkManager.submitTaskPracticeServer(SpUtils.getUserId(),toJson)
                .subscribe(submitReplyBeans -> {
                    LogUtils.d(" size = " + submitReplyBeans.size());
                    mCurrentFile = new File(SaveNoteUtils.getInstance(getApplicationContext()).getTaskFileDir() + "/" + dramaId);
                    if (!getString(R.string.parent_sign).equals(mTextFinish.getText().toString()) && isSignatureTask) {
//                        isHadCommit = true;
                        mTextFinish.setText(R.string.parent_sign);
//                        deleteDirWihtFile(file);
//                        loadData();
                        EventBus.getDefault().post(new BaseEvent(EVENT_TYPE_COMMIT_STATE, true));
                    } else {
                        //上传后清理掉本地图片文件
                        deleteDirWihtFile(mCurrentFile);
                        setResult(10000);
                        TaskDetailStudentActivity.this.finish();
                    }
                    if (loadingProgressDialog != null) {
                        loadingProgressDialog.dismiss();
                        loadingProgressDialog = null;
                    }
                    ToastUtil.showCustomToast(TaskDetailStudentActivity.this.getBaseContext(), "提交完毕");
                }, throwable -> {
                    if (loadingProgressDialog != null) {
                        loadingProgressDialog.dismiss();
                        loadingProgressDialog = null;
                    }
                    if (throwable instanceof ApiException) {
                        String errorCode = ((ApiException) throwable).getCode();
                        if (errorCode.equals("400")) {
//                            setResult(10000);
//                            TaskDetailStudentActivity.this.finish();
                            ToastUtil.showCustomToast(getBaseContext(), "提交失败！");
                        }
                    } else {
                        ToastUtil.showCustomToast(getBaseContext(), "提交失败，请重试！");
                    }
                });
    }

    public String getStageTypeCode () {
        String srId = "SR01";
        switch (currentTab) {
            case TAB_CONTENT:
                srId = "SR01";
                break;
            case TAB_MATERIALS:
                srId = "SR02";
                break;
            case TAB_PRACTICE:
                srId = "SR03";
                break;
        }
        return srId;
    }

    public void signatureCancel (){
        LogUtils.d("signatureCancel");
        removeFragment(R.id.frame_signature);
        mFrameSignature.setVisibility(View.GONE);
        UIUtils.postDelayed(this::invalidate, 300);
    }

    public void signatureSubmit () {
        LogUtils.d("TaskTest signatureSubmit");
        //提交 家长签字
        removeFragment(R.id.frame_signature);
        mFrameSignature.setVisibility(View.GONE);
        oosUpload();
    }


    public boolean isHadCommit() {
        return isHadCommit;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteDirWihtFile(mCurrentFile);
        if (mUnbinder != null)  mUnbinder.unbind();
    }
}
