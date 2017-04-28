package com.yougy.init.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.yougy.common.fragment.BFragment;
import com.yougy.common.manager.ProtocolManager;
import com.yougy.common.protocol.ProtocolId;
import com.yougy.common.protocol.callback.AreaCallBack;
import com.yougy.common.protocol.callback.SchoolCallBack;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.SpUtil;
import com.yougy.init.bean.AreaInfo;
import com.yougy.init.bean.SchoolInfo;
import com.yougy.init.manager.InitManager;
import com.yougy.ui.activity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by jiangliang on 2016/10/14.
 */

public class SelectSchoolFragment extends BFragment implements View.OnClickListener {
    private final String TAG = "SelectSchoolFragment";
    private Spinner mProvinceSpinner;
    private Spinner mCitySpinner;
    private Spinner mAreaSpinner;
    private Spinner mSchoolSpinner;
    private Button mNextStep;
    private SelectClassFragment nextFragment;


    private List<SchoolInfo.School> mSchoolInfos = new ArrayList<>();
    private List<AreaInfo.Area> mProvinceInfos = new ArrayList<>();
    private List<AreaInfo.Area> mCityInfos = new ArrayList<>();
    private List<AreaInfo.Area> mAreaInfos = new ArrayList<>();

    private ArrayAdapter<AreaInfo.Area> mProvinceAdapter;
    private ArrayAdapter<AreaInfo.Area> mCityAdapter;
    private ArrayAdapter<AreaInfo.Area> mAreaAdapter;
    private ArrayAdapter<SchoolInfo.School> mSchoolAdapter;

    private HashMap<String, List<AreaInfo.Area>> mAreaMap = new HashMap<>();


    private SchoolInfo.School schoolInfo;
    private AreaInfo.Area provinceInfo;
    private AreaInfo.Area cityInfo;
    private AreaInfo.Area areaInfo;

    private AreaInfo.Area selectedArea;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        LogUtils.e(TAG, "context is null ? " + (context == null));
        LogUtils.e(TAG, "onAttach.................activity");
        ProtocolManager.queryAreaProtocol(-1, "", -1, -1, ProtocolId.PROTOCOL_ID_QUERYAREA, new AreaCallBack(context));
    }

    @Override
    protected void handleEvent() {
        handleAreaEvent();
        handleSchoolEvent();
        super.handleEvent();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtils.e(TAG, "onViewCreated.........................");
        schoolInfo = new SchoolInfo.School();
        schoolInfo.setSchoolId(Integer.toString(0));
        schoolInfo.setSchoolName("选择学校");
        mSchoolInfos.add(0, schoolInfo);
        mSchoolAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item, mSchoolInfos);
        mSchoolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSchoolSpinner.setAdapter(mSchoolAdapter);

        provinceInfo = new AreaInfo.Area();
        provinceInfo.setAreaId(Integer.toString(0));
        provinceInfo.setAreaName("选择省份");
        mProvinceInfos.add(0, provinceInfo);
        mProvinceAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item, mProvinceInfos);
        mProvinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mProvinceSpinner.setAdapter(mProvinceAdapter);

        cityInfo = new AreaInfo.Area();
        cityInfo.setAreaId(Integer.toString(0));
        cityInfo.setAreaName("选择市");
        mCityInfos.add(0, cityInfo);
        mCityAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item, mCityInfos);
        mCityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCitySpinner.setAdapter(mCityAdapter);

        areaInfo = new AreaInfo.Area();
        areaInfo.setAreaId(Integer.toString(0));
        areaInfo.setAreaName("选择地区");
        mAreaInfos.add(0, areaInfo);
        mAreaAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item, mAreaInfos);
        mAreaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAreaSpinner.setAdapter(mAreaAdapter);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_school_layout, container, false);
        mProvinceSpinner = (Spinner) view.findViewById(R.id.province_spinner);
        mCitySpinner = (Spinner) view.findViewById(R.id.city_spinner);
        mAreaSpinner = (Spinner) view.findViewById(R.id.area_spinner);
        mSchoolSpinner = (Spinner) view.findViewById(R.id.school_spinner);
        mNextStep = (Button) view.findViewById(R.id.next_step);
        mNextStep.setOnClickListener(this);

        mProvinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.e(TAG, "province position is : " + position);
//                EpdController.invalidate(parent, UpdateMode.GC);
//                EpdController.setViewDefaultUpdateMode(parent, UpdateMode.GC);
                mCityInfos.clear();
                mCityInfos.add(cityInfo);
                if (position != 0) {
                    mCityInfos.addAll(mAreaMap.get(mProvinceAdapter.getItem(position).getAreaId()));
                    mCityAdapter.notifyDataSetChanged();
                    mCitySpinner.setClickable(true);
                } else {
                    mCitySpinner.setClickable(false);
                    mAreaSpinner.setClickable(false);
                    mSchoolSpinner.setClickable(false);
                    mNextStep.setEnabled(false);
                }
                mSchoolSpinner.setSelection(0);
                mCitySpinner.setSelection(0);
                mAreaSpinner.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                LogUtils.e(TAG, "onNothingSelected...............");
            }
        });

        mCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.e(TAG, "city position is : " + position);
                mAreaInfos.clear();
                mAreaInfos.add(areaInfo);
                if (position != 0) {
                    mAreaInfos.addAll(mAreaMap.get(mCityAdapter.getItem(position).getAreaId()));
                    mAreaAdapter.notifyDataSetChanged();
                    mAreaSpinner.setClickable(true);
                } else {
                    mAreaSpinner.setClickable(false);
                    mSchoolSpinner.setClickable(false);
                    mNextStep.setEnabled(false);
                }
                mAreaSpinner.setSelection(0);
                mSchoolSpinner.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mAreaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.e(TAG, "area position is : " + position);
                if (position != 0) {
                    selectedArea = mAreaAdapter.getItem(position);
                    SpUtil.saveSelectAreaID(selectedArea.getAreaId());
                    ProtocolManager.querySchoolProtocol(selectedArea.getAreaId(), "", ProtocolId.PROTOCOL_ID_QUERYSCHOOL, new SchoolCallBack(context));
                    mSchoolSpinner.setClickable(true);
                } else {
                    mSchoolSpinner.setClickable(false);
                    mNextStep.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSchoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.e(TAG, "school position is : " + position);
                if (position != 0) {
                    mNextStep.setEnabled(true);
                    SpUtil.saveAccountSchool(mSchoolInfos.get(position).getSchoolName());
                    InitManager.getInstance().setSchoolId(mSchoolInfos.get(position).getSchoolId());
                    InitManager.getInstance().setSchoolName(mSchoolInfos.get(position).getSchoolName());
                } else {
                    mNextStep.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        if (nextFragment == null) {
            nextFragment = new SelectClassFragment();
        }
        InitManager.getInstance().nextStep(this, nextFragment, InitManager.TAG_SELECT_CLASS);
    }

    private void handleSchoolEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                Log.e(TAG, "handleSchoolEvent...");
                if (o instanceof SchoolInfo) {
                    Log.e(TAG, "handleSchoolEvent...");
                    SchoolInfo info = (SchoolInfo) o;
                    List<SchoolInfo.School> schools = info.getSchoolList();
                    mSchoolInfos.clear();
                    mSchoolInfos.add(schoolInfo);
                    if (schools != null && schools.size() > 0) {
                        mSchoolInfos.addAll(schools);
                    }
                    mSchoolAdapter.notifyDataSetChanged();
                }
            }
        }));
    }

    private void handleAreaEvent() {
        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                Log.e(TAG, "handleAreaEvent...");
                if (o instanceof AreaInfo.Area) {
                    mProvinceInfos.clear();
                    mProvinceInfos.add(provinceInfo);
                    AreaInfo.Area area = (AreaInfo.Area) o;
                    for (AreaInfo.Area info : area.getAreaList()) {
                        AreaInfo.Area spinnerInfo = new AreaInfo.Area();
                        spinnerInfo.setAreaId(info.getAreaId());
                        spinnerInfo.setAreaName(info.getAreaName());
                        mProvinceInfos.add(spinnerInfo);
                    }
                    mProvinceAdapter.notifyDataSetChanged();
                    traverse(area);
                }
            }
        }));
    }

    private void traverse(AreaInfo.Area area) {
        List<AreaInfo.Area> areas = area.getAreaList();
        if (areas != null) {
            mAreaMap.put(area.getAreaId(), areas);
            for (AreaInfo.Area area1 : areas) {
                traverse(area1);
            }
        }
    }

}
