package com.yougy.init.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.yougy.common.fragment.BFragment;
import com.yougy.common.manager.NewProtocolManager;
import com.yougy.common.protocol.callback.ClassCallBack;
import com.yougy.common.protocol.request.NewQuerySchoolOrgReq;
import com.yougy.common.protocol.response.NewQuerySchoolOrgRep;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.init.manager.InitManager;
import com.yougy.ui.activity.R;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by jiangliang on 2016/10/14.
 */

public class SelectClassFragment extends BFragment implements View.OnClickListener {
    private static final String TAG = "SelectClassFragment";
    private Spinner mGradeSpinner;
    private Spinner mClassSpinner;
    private Button mLastStep;
    private Button mNextStep;
    private SelectIdentityFragment nextFragment;
    private SelectSchoolFragment lastFragment;

//    private List<ClassInfo.Org> mGradeInfos = new ArrayList<>();

//    private List<ClassInfo.Org> mClassInfos = new ArrayList<>();

//    private HashMap<String, List<ClassInfo.Org>> mOrgMap = new HashMap<>();

//    private ArrayAdapter<ClassInfo.Org> mGradeAdapter;
//    private ArrayAdapter<ClassInfo.Org> mClassAdapter;
//    private ClassInfo.Org gradeInfo;
//    private ClassInfo.Org classInfo;
//    private ClassInfo.Org mGrade;
    private List<NewQuerySchoolOrgRep.SchoolOrg> mGradeInfos = new ArrayList<>();
    private List<NewQuerySchoolOrgRep.SchoolOrg> mClassInfos = new ArrayList<>();
//    private HashMap<String,List<NewQuerySchoolOrgRep.SchoolOrg>> mOrgMap = new HashMap<>();
    private SparseArray<List<NewQuerySchoolOrgRep.SchoolOrg>> mOrgMap = new SparseArray<>();
    private ArrayAdapter<NewQuerySchoolOrgRep.SchoolOrg> mGradeAdapter;
    private ArrayAdapter<NewQuerySchoolOrgRep.SchoolOrg> mClassAdapter;
    private NewQuerySchoolOrgRep.SchoolOrg gradeInfo;
    private NewQuerySchoolOrgRep.SchoolOrg classInfo;
    private NewQuerySchoolOrgRep.SchoolOrg mGrade;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        ProtocolManager.queryClassProtocol(InitManager.getInstance().getSchoolId(), "", ProtocolId.PROTOCOL_ID_QUERYCLASS, new ClassCallBack(context));
        NewQuerySchoolOrgReq schoolOrgReq = new NewQuerySchoolOrgReq();
        schoolOrgReq.setSchoolId(InitManager.getInstance().getSchoolId());
        NewProtocolManager.querySchoolOrg(schoolOrgReq,new ClassCallBack(context));
    }

    @Override
    protected void handleEvent() {
        handleClassEvent();
        super.handleEvent();
    }

    private void handleClassEvent(){
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
//                if (o instanceof ClassInfo){
//                    ClassInfo classInfo = (ClassInfo) o;
//                    ClassInfo.Org org = classInfo.getOrg();
//                    if (org.getCount() > 0) {
//                        mGradeInfos.clear();
//                        mGradeInfos.add(gradeInfo);
//                        mOrgMap.clear();
//                        for (ClassInfo.Org org1 : org.getOrgList()) {
//                            mGradeInfos.addAll(org1.getOrgList());
//                            for (ClassInfo.Org info : org1.getOrgList()) {
//                                mOrgMap.put(info.getOrgId(), info.getOrgList());
//                            }
//                        }
//                        LogUtils.e(TAG, "map is : " + mOrgMap);
//                        mGradeAdapter.notifyDataSetChanged();
//                        mGradeSpinner.setSelection(0);
//                    }
//                }
                if (o instanceof NewQuerySchoolOrgRep){
                    NewQuerySchoolOrgRep orgRep = (NewQuerySchoolOrgRep) o;
                    List<NewQuerySchoolOrgRep.SchoolOrg> data = orgRep.getData();
                    if (null!=data&&data.size()>0){
                        mGradeInfos.clear();
                        mGradeInfos.add(gradeInfo);
                        mOrgMap.clear();
                        for (NewQuerySchoolOrgRep.SchoolOrg org:orgRep.getData()){
                            mGradeInfos.addAll(org.getOrgList());
                            for (NewQuerySchoolOrgRep.SchoolOrg info:org.getOrgList()){
                                mOrgMap.put(info.getOrgId(),info.getOrgList());
                            }
                        }
                        mGradeAdapter.notifyDataSetChanged();
                        mGradeSpinner.setSelection(0);
                    }

                }
            }
        }));
        subscription.add(tapEventEmitter.connect());
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.e(TAG,"onActivityCreated...................");
//        gradeInfo = new ClassInfo.Org();
        gradeInfo = new NewQuerySchoolOrgRep.SchoolOrg();
        gradeInfo.setOrgId(0);
        gradeInfo.setOrgName("选择年级");
        mGradeInfos.add(0, gradeInfo);

        classInfo = new NewQuerySchoolOrgRep.SchoolOrg();
        classInfo.setOrgId(0);
        classInfo.setOrgName("选择班级");
        mClassInfos.add(0, classInfo);

        mGradeAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item, mGradeInfos);
        mGradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGradeSpinner.setAdapter(mGradeAdapter);

        mClassAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item, mClassInfos);
        mClassAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mClassSpinner.setAdapter(mClassAdapter);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.e(TAG,"onCreateView...........................");
        final View rootView = inflater.inflate(R.layout.select_class_layout, container, false);
        mGradeSpinner = (Spinner) rootView.findViewById(R.id.grade_spinner);
        mClassSpinner = (Spinner) rootView.findViewById(R.id.class_spinner);
        mLastStep = (Button) rootView.findViewById(R.id.last_step);
        mNextStep = (Button) rootView.findViewById(R.id.next_step);
        mLastStep.setOnClickListener(this);
        mNextStep.setOnClickListener(this);

        mGradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EpdController.invalidate(rootView, UpdateMode.GC);
                mClassInfos.clear();
                mClassInfos.add(classInfo);
                mGrade = mGradeAdapter.getItem(position);
                if (position != 0) {
//                    List<ClassInfo.Org> orgs = mOrgMap.get(mGrade.getOrgId());
                    List<NewQuerySchoolOrgRep.SchoolOrg> orgs = mOrgMap.get(mGrade.getOrgId());
                    if (orgs != null) {
                        mClassInfos.addAll(orgs);
                    }
                    mClassSpinner.setClickable(true);
                }else{
                    mClassSpinner.setClickable(false);
                }
                InitManager.getInstance().setGradeId(mGrade.getOrgId());
                InitManager.getInstance().setGradeName(mGrade.getOrgName());
                mClassAdapter.notifyDataSetChanged();
                mClassSpinner.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                LogUtils.e(TAG, "onNothingSelected......................");
            }
        });

        mClassSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EpdController.invalidate(rootView, UpdateMode.GC);
                if (position != 0) {
                    mNextStep.setEnabled(true);
//                    ClassInfo.Org org = mClassInfos.get(position);
                    NewQuerySchoolOrgRep.SchoolOrg org = mClassInfos.get(position);
                    SpUtil.saveAccountClass(mGrade.getOrgName()+org.getOrgName());
                    SpUtil.saveGradeName(mGrade.getOrgName());
                    InitManager.getInstance().setClassId(org.getOrgId());
                    InitManager.getInstance().setClassName(org.getOrgName());
                } else {
                    mNextStep.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_step:
                if (nextFragment == null) {
                    nextFragment = new SelectIdentityFragment();
                }
                InitManager.getInstance().nextStep(this, nextFragment, InitManager.TAG_SELECT_IDENTITY);
                break;
            case R.id.last_step:
                if (lastFragment == null) {
                    FragmentManager manager = getFragmentManager();
                    lastFragment = (SelectSchoolFragment) manager.findFragmentByTag(InitManager.TAG_SELECT_SCHOOL);
                }
                InitManager.getInstance().lastStep(this, lastFragment);
                break;
        }
    }

}
