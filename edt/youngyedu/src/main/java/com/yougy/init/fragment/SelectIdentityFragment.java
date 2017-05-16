package com.yougy.init.fragment;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.ui.view.DisableScrollLinearManager;
import com.yougy.common.fragment.BFragment;
import com.yougy.common.fragment.UserCallBack;
import com.yougy.common.global.Commons;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.BindCallBack;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.init.adapter.IdentityAdapter;
import com.yougy.init.bean.BindInfo;
import com.yougy.init.bean.UserInfo;
import com.yougy.init.manager.InitManager;
import com.yougy.ui.activity.R;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by jiangliang on 2016/10/14.
 */

public class SelectIdentityFragment extends BFragment implements View.OnClickListener {
    private static final String TAG = "SelectIdentityFragment";
    private RecyclerView mIdentityRecycler;
    private LinearLayout mPageLayout;
    private Button mLastStep;
    private int count;
    private List<UserInfo.User> infos = new ArrayList<>();
    private List<UserInfo.User> pageInfos = new ArrayList<>();
    private IdentityAdapter adapter;
    private static final int COUNT_PER_PAGE = 16;
    private List<Button> btns = new ArrayList<>();


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ProtocolManager.queryUserProtocol(InitManager.getInstance().getClassId(), "", ProtocolId.PROTOCOL_ID_QUERYUSER, new UserCallBack(context));
    }

    @Override
    protected void handleEvent() {
        handleUserEvent();
        handleBindEvent();
        super.handleEvent();
    }

    private void handleUserEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof UserInfo) {
                    UserInfo userInfo = (UserInfo) o;
                    if (userInfo.getCount() > 0) {
                        infos.clear();
                        pageInfos.clear();
                        infos.addAll(userInfo.getUsers());
                        for (int i = 0; i < COUNT_PER_PAGE - userInfo.getCount() % COUNT_PER_PAGE; i++) {
                            UserInfo.User user = new UserInfo.User();
                            user.setUserName("");
                            user.setUserNumber("");
                            user.setBind(true);
                            infos.add(user);
                        }
                        pageInfos.addAll(infos.subList(0, COUNT_PER_PAGE));
                        freshList();
                        mPageLayout.removeAllViews();
                        generateBtn();
                    }
                }
            }
        }));
    }

    private void handleBindEvent() {

        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof BindInfo) {
                    if (nextFragment == null) {
                        nextFragment = new StartUseFragment();
                    }
                    InitManager.getInstance().nextStep(SelectIdentityFragment.this, nextFragment, InitManager.TAG_START_USE);
                    mPopWindown.dismiss();
                }
            }
        }));
    }
    private View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.e(TAG, "onCreateView.....................");
        rootView = inflater.inflate(R.layout.select_identity_layout, container, false);
        mIdentityRecycler = (RecyclerView) rootView.findViewById(R.id.identity_recycler);
        mPageLayout = (LinearLayout) rootView.findViewById(R.id.page_layout);
        mLastStep = (Button) rootView.findViewById(R.id.last_step);

//        mIdentityRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        mIdentityRecycler.setLayoutManager(new DisableScrollLinearManager(context));
        mLastStep.setOnClickListener(this);
        return rootView;
    }

    private void freshList() {
        adapter = new IdentityAdapter(pageInfos);
        adapter.setOnItemClickListener(new IdentityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                user = pageInfos.get(position);
                InitManager.getInstance().setStudentName(user.getUserRealName());
                InitManager.getInstance().setStudentNumber(user.getUserNumber());
                InitManager.getInstance().setStudentId(user.getUserId());
                showComfirmPopWindow();
            }
        });
//        adapter = new PIdentityAdapter(pageInfos);
        mIdentityRecycler.setAdapter(adapter);
//        mIdentityRecycler.addOnItemTouchListener(new OnRecyclerItemClickListener(mIdentityRecycler){
//
//            @Override
//            public void onItemClick(RecyclerView.ViewHolder vh) {
//                   identityItemClick(vh.getAdapterPosition());
//            }
//        });
    }
    private void identityItemClick(int position){
        user = pageInfos.get(position);
        InitManager.getInstance().setStudentName(user.getUserRealName());
        InitManager.getInstance().setStudentNumber(user.getUserNumber());
        InitManager.getInstance().setStudentId(user.getUserId());
        showComfirmPopWindow();
    }

    private void generateBtn() {
        int count = infos.size() % COUNT_PER_PAGE == 0 ? infos.size() / COUNT_PER_PAGE : infos.size() / COUNT_PER_PAGE + 1;
        for (int index = 1; index <= count; index++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 20;
            View pageLayout = View.inflate(context, R.layout.page_item, null);
            final Button pageBtn = (Button) pageLayout.findViewById(R.id.page_btn);
            if (index == 1) {
                pageBtn.setSelected(true);
            }
            pageBtn.setText(Integer.toString(index));
            btns.add(pageBtn);
            final int page = index - 1;
            pageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (Button btn : btns) {
                        btn.setSelected(false);
                    }
                    pageBtn.setSelected(true);
                    pageInfos.clear();
                    int start = page * COUNT_PER_PAGE;
                    int end = (page + 1) * COUNT_PER_PAGE;
                    if (end > infos.size()) {
                        end = infos.size();
                    }
                    pageInfos.addAll(infos.subList(start, end));
                    adapter.notifyDataSetChanged();
                }
            });
            mPageLayout.addView(pageBtn, params);
        }
    }

    private PopupWindow mPopWindown;
    private TextView schoolTv;
    private TextView classTv;
    private TextView nameTv;
    private TextView numberTv;
    private TextView reconfirmTv;
    private int clickCount = 0;
    private StartUseFragment nextFragment;
    private UserInfo.User user;

    private void showComfirmPopWindow() {
        LogUtils.e(TAG, "show pop window..................");
        if (mPopWindown == null) {
            mPopWindown = new PopupWindow();
            View view = View.inflate(context, R.layout.dialog_confirm_identity, null);
            schoolTv = (TextView) view.findViewById(R.id.info_school);
            classTv = (TextView) view.findViewById(R.id.info_class);
            nameTv = (TextView) view.findViewById(R.id.info_name);
            numberTv = (TextView) view.findViewById(R.id.info_number);
            reconfirmTv = (TextView) view.findViewById(R.id.info_reconfirm);
            Button confirmBtn = (Button) view.findViewById(R.id.identity_confirm);
            Button selectErrorBtn = (Button) view.findViewById(R.id.select_error);
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickCount == 0) {
                        clickCount++;
                        reconfirmTv.setVisibility(View.VISIBLE);
                        EpdController.invalidate(rootView, UpdateMode.GC);
                    } else {
                        SpUtil.saveAccountId(user.getUserId());
                        SpUtil.saveAccountName(user.getUserRealName());
                        SpUtil.saveAccountNumber(user.getUserNumber());
                        String uuid = Commons.UUID;
                        ProtocolManager.deviceBindProtocol(user.getUserId(), uuid, ProtocolId.PROTOCOL_ID_DEVICEBIND, new BindCallBack(context));
                    }
                }
            });
            selectErrorBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickCount = 0;
                    mPopWindown.dismiss();
                    reconfirmTv.setVisibility(View.GONE);
                }
            });
            mPopWindown.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopWindown.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopWindown.setContentView(view);
            mPopWindown.setFocusable(true);
            mPopWindown.setOutsideTouchable(true);
            mPopWindown.setBackgroundDrawable(new BitmapDrawable());
            mPopWindown.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            mPopWindown.setTouchInterceptor(new View.OnTouchListener() {
                public boolean onTouch(View view, MotionEvent event) {
                    return false;
                }
            });
        }
        schoolTv.setText(String.format(getString(R.string.info_school), InitManager.getInstance().getSchoolName()));
        classTv.setText(String.format(getString(R.string.info_class), InitManager.getInstance().getGradeName() + InitManager.getInstance().getClassName()));
        nameTv.setText(String.format(getString(R.string.info_name), InitManager.getInstance().getStudentName()));
        numberTv.setText(String.format(getString(R.string.info_number), InitManager.getInstance().getStudentNumber()));
        mPopWindown.showAtLocation(((Activity) context).getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }


    @Override
    public void onClick(View v) {
        FragmentManager manager = getFragmentManager();
        Fragment lastFragment = manager.findFragmentByTag(InitManager.TAG_SELECT_CLASS);
        InitManager.getInstance().lastStep(this, lastFragment);
    }
}
