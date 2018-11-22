package com.yougy.task;


import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.yougy.common.activity.BaseActivity;
import com.yougy.common.eventbus.BaseEvent;
import com.yougy.common.manager.DialogManager;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.UIUtils;
import com.yougy.task.fragment.MaterialsBaseFragment;
import com.yougy.task.fragment.PracticeBaseFragment;
import com.yougy.task.fragment.SignatureFragment;
import com.yougy.task.fragment.TaskContentBaseFragment;
import com.yougy.task.fragment.TaskBaseFragment;
import com.yougy.ui.activity.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.greenrobot.event.EventBus;

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

    public static boolean isIntercept = false;//是否可写状态
    public static boolean isHandPaintedPattern = false;//是否手绘模式

    public static final String EVENT_TYPE_SCRIBBLE_MODE = "task_scribbleMode";
    public static final String EVENT_TYPE_LOAD_DATA = "event_type_load_data";

    private String currentTitleStr = "任务详情";
    /*是否家长签字任务*/
    private boolean isSignatureTask = true;
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

    @Override
    protected void setContentView() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_task_student_details, null);
        setContentView(view);
        mUnbinder = ButterKnife.bind(R.layout.top_title_bar, mTopLayout);
    }

    @Override
    public void init() {
        mTopTitle.setText(currentTitleStr);
        initContentFragment();
    }

    @Override
    protected void initLayout() {
        if (isHadCommit) {
            if (isSignatureTask) {
                if (hadSignature) {
                    mTextFinish.setVisibility(View.GONE);
                } else {
                    mTextFinish.setText("家长签字");
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
        showContentFragment(mTaskContentFragment, TAB_CONTENT);
    }


    @Override
    public void loadData() {
        Log.i(TaskBaseFragment.TAG, "loadData: activity : "  + currentTab);
        EventBus.getDefault().post(new BaseEvent(EVENT_TYPE_LOAD_DATA));
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
                showContentFragment(mTaskContentFragment, TAB_CONTENT);
                break;
            case R.id.tab_materials:
                showContentFragment(mMaterialsFragment, TAB_MATERIALS);
                break;
            case R.id.tab_practice:
                showContentFragment(mPracticeFragment, TAB_PRACTICE);
                break;
            case R.id.btn_confirm:
                finishedTask ();
                break;
        }
    }

    private void back() {
        if (isHadCommit) {//已经提交
            if (isSignatureTask) {
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
                                saveTaskToBitmap ();
                                finish();
                            }
                        });
            } else {
                finish();
            }
        } else { //未提交返回
            saveTaskToBitmap ();
            finish();
        }
    }

    private void showContentFragment(TaskBaseFragment fragment, String tag) {
        if (currentTab == TAB_PRACTICE && tag != TAB_PRACTICE) {
            mPracticeFragment.dismissPopupWindow();
        }
        if (tag.equals(currentTab)) {
            LogUtils.d("current tab = " + currentTab + "  tag = " + tag);
            return;
        }
        LogUtils.d("task current show Fragment : " + tag + "   currentTab = " + currentTab);
        currentTab = tag;
        setTabSelectState();
        replaceFragment(fragment, R.id.task_content, tag);
    }

    private void replaceFragment (TaskBaseFragment fragment, int containerId, String tag) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (tag != null) fragmentTransaction.replace(containerId, fragment, tag);
        else fragmentTransaction.replace(containerId, fragment);
        if (containerId == R.id.frame_signature)
            mFrameSignature.setVisibility(View.VISIBLE);
        fragmentTransaction.commit();
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
        if (isHadCommit) {
            if (!isSignatureTask || hadSignature) {
                finish();
            } else {
                replaceFragment(mSignatureFragment, R.id.frame_signature, null);
            }
            return;
        }
        showDialog (getString(R.string.is_finished_task), R.string.confirm, R.string.cancel);
    }

    private void showDialog (String title, int confirm , int cancel) {
        EventBus.getDefault().post(new BaseEvent(EVENT_TYPE_SCRIBBLE_MODE, false));
        DialogManager.newInstance().showSubmitConfirmDialog(this, title, confirm, cancel
                , new DialogManager.DialogCallBack(){

                    @Override
                    public void confirm() {
                        if (isSignatureTask) {
                            isHadCommit = true;
                            mTextFinish.setText(R.string.parent_sign);
                            UIUtils.postDelayed(() -> invalidate(), 100);
                        } else {
                            finish();
                        }
                    }

                    @Override
                    public void cancel() {
                        super.cancel();
                        EventBus.getDefault().post(new BaseEvent(EVENT_TYPE_SCRIBBLE_MODE, true));
                        UIUtils.postDelayed(() -> invalidate(), 100);
                    }
                });
    }

    /**
     * 保存到本地 bitmap
     */
    private void saveTaskToBitmap () {


    }


    public void signatureCancel (){
        mFrameSignature.setVisibility(View.GONE);
        UIUtils.postDelayed(this::invalidate, 300);
    }

    public void signatureSubmit () {
        //提交 家长签字
        signatureCancel();
        finish();
    }


    public boolean isHadCommit() {
        return isHadCommit;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null)  mUnbinder.unbind();
    }
}
