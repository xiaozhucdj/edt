package com.inkscreen.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.inkscreen.MyHaslsudActivity;
import com.inkscreen.MyTobeIsudActivity;
import com.inkscreen.adapter.MyRecordAdapter;
import com.inkscreen.model.RecordInfo;
import com.inkscreen.utils.LeApiApp;
import com.inkscreen.utils.LeApiResult;
import com.inkscreen.utils.LeApiUtils;
import com.inkscreen.will.utils.widget.MultiListView;
import com.yougy.ui.activity.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xcz on 2016/11/27.
 */
public class RecordFragment extends BaseFragment {

    MultiListView mrecordLv;
    MyRecordAdapter myRecordAdapter;
    List<RecordInfo.Ret.Items> mRecordList;
    RadioButton rdBtnLeft, rdBtnMdl, rdBtnRht,rdBtn4,rdBtn5;
    private static final int PAGE = 1;
    private static final int SIZE = 8;
    private static int toTal = 0;
    private  int CurrentPage =1;

    FrameLayout fmworkView;
    private View contentView;
    private View errorView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View messageLayout = inflater.inflate(R.layout.recordfragment,container,false);
        mrecordLv = (MultiListView)messageLayout.findViewById(R.id.workrecordlv_id);
        fmworkView = (FrameLayout)messageLayout.findViewById(R.id.view_id);
        rdBtnLeft = (RadioButton)messageLayout.findViewById(R.id.radio_record_left_id);
        rdBtnMdl = (RadioButton)messageLayout.findViewById(R.id.radio_record_middle_id);
        rdBtnRht = (RadioButton)messageLayout.findViewById(R.id.radio_record_right_id);
        rdBtn5 = (RadioButton)messageLayout.findViewById(R.id.radio5_id);
        rdBtn4 = (RadioButton)messageLayout.findViewById(R.id.radio4_id);
        rdBtnLeft.setOnClickListener(new MyButtonClickListener());
        rdBtnMdl.setOnClickListener(new MyButtonClickListener());
        rdBtnRht.setOnClickListener(new MyButtonClickListener());
        rdBtn4.setOnClickListener(new MyButtonClickListener());
        rdBtn5.setOnClickListener(new MyButtonClickListener());
//        rdBtnLeft.setEnabled(false);
//        rdBtnMdl.setEnabled(false);
//        rdBtnRht.setEnabled(false);
//        rdBtn4.setEnabled(false);
//        rdBtn5.setEnabled(false);

        mRecordList = new ArrayList<>();
        myRecordAdapter = new MyRecordAdapter(getActivity());
        myRecordAdapter.setList(new ArrayList<RecordInfo.Ret.Items>());
        mrecordLv.setAdapter(myRecordAdapter);
        contentView = messageLayout.findViewById(R.id.content_view);
        mrecordLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                String homeWorkId =  mRecordList.get(position).getId();
                intent.putExtra("homeWorkId",homeWorkId);
                intent.putExtra("title", mRecordList.get(position).getHomeWork().getName());

                for(int i=0;i<mRecordList.size();i++){
                    if (i==position){
                        if( mRecordList.get(i).getStatusName().equals("下发")){
                            intent.setClass(getActivity(), MyHaslsudActivity.class);
                        }else {
                            intent.setClass(getActivity(), MyTobeIsudActivity.class);
                        }
                        break;
                    }
                }

                startActivity(intent);
            }
        });


        getDatafromRecordApi(PAGE,SIZE);

        initEorrView(messageLayout);
        return messageLayout;
    }
    private void initEorrView(View view) {
        errorView = view.findViewById(R.id.layout_net_error);
        errorView.findViewById(R.id.imgnet_id);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDatafromRecordApi(PAGE,SIZE);
            }
        });
    }
    @Override
    public void refreshFragment(Bundle bundle) {
        super.refreshFragment(bundle);
        getDatafromRecordApi(PAGE,SIZE);
    }

    @Override
    public void onResume() {
        super.onResume();

    }
    private void showErrorView(String msg) {
        if (myRecordAdapter == null || myRecordAdapter.getCount() == 0) {
            contentView.setVisibility(View.GONE);
            errorView.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }
    private  void getDatafromRecordApi(int page,int size){
        Map<String, String> map = new HashMap<>();
        map.put("page", ""+page);
        map.put("size", ""+size);
        Log.d("xcz", "Url" + LeApiApp.getRecordUrl(page, size));
        LeApiUtils.post(LeApiApp.getRecordUrl(page, size), map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {


                LeApiResult<RecordInfo> result = new LeApiResult<RecordInfo>(jsonObject, new TypeToken<RecordInfo>() {
                });

                Log.d("xcz", "onResponse" + jsonObject);
                if (result.getResult().getRet_code()==0) {
                    contentView.setVisibility(View.VISIBLE);
                    errorView.setVisibility(View.GONE);
                    if (result.getResult().getRet() != null && result.getResult().getRet().getItems()!=null && result.getResult().getRet().getItems().size() > 0) {
                        fmworkView.setVisibility(View.GONE);
                        CurrentPage = result.getResult().getRet().getCurrentPage();
                        mRecordList = result.getResult().getRet().getItems();
                        myRecordAdapter.getList().clear();
                        myRecordAdapter.getList().addAll(mRecordList);
                        myRecordAdapter.notifyDataSetChanged();
                        toTal = result.getResult().getRet().getTotalPage();




                        if (toTal>4){
                            rdBtnLeft.setVisibility(View.VISIBLE);
                            rdBtnMdl.setVisibility(View.VISIBLE);
                            rdBtnRht.setVisibility(View.VISIBLE);
                            rdBtn4.setVisibility(View.VISIBLE);
                            rdBtn5.setVisibility(View.VISIBLE);

                            if (CurrentPage <=4){
                                rdBtnLeft.setText("1");
                                rdBtnMdl.setText("2");
                                rdBtnRht.setText("3");
                                rdBtn4.setText("4");
                                rdBtn5.setText("5");

                                if (CurrentPage == 1){
                                    rdBtnLeft.setChecked(true);
                                    rdBtnMdl.setChecked(false);
                                    rdBtnRht.setChecked(false);
                                    rdBtn4.setChecked(false);
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_white));
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtn5.setTextColor(getResources().getColor(R.color.zl_black));
                                }else if (CurrentPage == 2){
                                    rdBtnLeft.setChecked(false);
                                    rdBtnMdl.setChecked(true);
                                    rdBtnRht.setChecked(false);
                                    rdBtn4.setChecked(false);
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_white));
                                    rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtn5.setTextColor(getResources().getColor(R.color.zl_black));
                                }else if (CurrentPage == 3){
                                    rdBtnLeft.setChecked(false);
                                    rdBtnMdl.setChecked(false);
                                    rdBtnRht.setChecked(true);
                                    rdBtn4.setChecked(false);
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnRht.setTextColor(getResources().getColor(R.color.zl_white));
                                    rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtn5.setTextColor(getResources().getColor(R.color.zl_black));
                                }else if (CurrentPage == 4){

                                    rdBtnLeft.setChecked(false);
                                    rdBtnMdl.setChecked(false);
                                    rdBtnRht.setChecked(false);
                                    rdBtn4.setChecked(true);

                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtn4.setTextColor(getResources().getColor(R.color.zl_white));
                                    rdBtn5.setTextColor(getResources().getColor(R.color.zl_black));

                                }

                            }else if (CurrentPage + 2 >6 && CurrentPage==toTal){
                                rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                                rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                                rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                                rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));
                                rdBtn5.setTextColor(getResources().getColor(R.color.zl_white));

                                rdBtnLeft.setChecked(false);
                                rdBtnMdl.setChecked(false);
                                rdBtnRht.setChecked(false);
                                rdBtn4.setChecked(false);
                                rdBtn5.setChecked(true);

                            }else if (CurrentPage + 2 >6 && CurrentPage+2<=toTal){

                                rdBtnLeft.setText(String.valueOf(result.getResult().getRet().getCurrentPage() - 2));
                                rdBtnMdl.setText(String.valueOf(result.getResult().getRet().getCurrentPage() - 1));
                                rdBtnRht.setText(String.valueOf(result.getResult().getRet().getCurrentPage()));
                                rdBtn4.setText(String.valueOf(result.getResult().getRet().getCurrentPage() + 1));
                                rdBtn5.setText(String.valueOf(result.getResult().getRet().getCurrentPage() + 2));
                                rdBtnLeft.setChecked(false);
                                rdBtnMdl.setChecked(false);
                                rdBtnRht.setChecked(true);
                                rdBtn4.setChecked(false);
                                rdBtn5.setChecked(false);
                                rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                                rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                                rdBtnRht.setTextColor(getResources().getColor(R.color.zl_white));
                                rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));
                                rdBtn5.setTextColor(getResources().getColor(R.color.zl_black));

                            }else if (CurrentPage + 2 >6 && CurrentPage+1==toTal){

                                rdBtnLeft.setText(String.valueOf(result.getResult().getRet().getCurrentPage()-3));
                                rdBtnMdl.setText(String.valueOf(result.getResult().getRet().getCurrentPage() -2));
                                rdBtnRht.setText(String.valueOf(result.getResult().getRet().getCurrentPage()-1));
                                rdBtn4.setText(String.valueOf(result.getResult().getRet().getCurrentPage()));
                                rdBtn5.setText(String.valueOf(result.getResult().getRet().getCurrentPage() + 1));
                                rdBtnLeft.setChecked(false);
                                rdBtnMdl.setChecked(false);
                                rdBtnRht.setChecked(false);
                                rdBtn4.setChecked(true);
                                rdBtn5.setChecked(false);
                                rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                                rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                                rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                                rdBtn4.setTextColor(getResources().getColor(R.color.zl_white));
                                rdBtn5.setTextColor(getResources().getColor(R.color.zl_black));
                            }



//                        rdBtnLeft.setChecked(false);
//                        rdBtnMdl.setChecked(false);
//                        rdBtnRht.setChecked(false);
//                        rdBtn4.setChecked(true);
//                        rdBtn5.setChecked(false);

                        }else {
                            if (toTal == 1){
                                rdBtnLeft.setVisibility(View.VISIBLE);
                                if (CurrentPage == 1){
                                    rdBtnLeft.setChecked(true);
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_white));
                                }

                            }else if (toTal == 2){
                                rdBtnLeft.setVisibility(View.VISIBLE);
                                rdBtnMdl.setVisibility(View.VISIBLE);
                                if (CurrentPage == 1){
                                    rdBtnLeft.setChecked(true);
                                    rdBtnMdl.setChecked(false);
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_white));
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));

                                }else if (CurrentPage == 2){
                                    rdBtnLeft.setChecked(false);
                                    rdBtnMdl.setChecked(true);

                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_white));
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                                }

                            }else if (toTal == 3){
                                rdBtnLeft.setVisibility(View.VISIBLE);
                                rdBtnMdl.setVisibility(View.VISIBLE);
                                rdBtnRht.setVisibility(View.VISIBLE);
                                if (CurrentPage == 1){
                                    rdBtnLeft.setChecked(true);
                                    rdBtnMdl.setChecked(false);
                                    rdBtnRht.setChecked(false);
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_white));
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));

                                }else if (CurrentPage == 2){
                                    rdBtnLeft.setChecked(false);
                                    rdBtnMdl.setChecked(true);
                                    rdBtnRht.setChecked(false);
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_white));
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                                }else if (CurrentPage == 3){
                                    rdBtnLeft.setChecked(false);
                                    rdBtnMdl.setChecked(false);
                                    rdBtnRht.setChecked(true);
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnRht.setTextColor(getResources().getColor(R.color.zl_white));

                                }

                            }else if (toTal == 4){
                                rdBtnLeft.setVisibility(View.VISIBLE);
                                rdBtnMdl.setVisibility(View.VISIBLE);
                                rdBtnRht.setVisibility(View.VISIBLE);
                                rdBtn4.setVisibility(View.VISIBLE);
                                if (CurrentPage == 1){
                                    rdBtnLeft.setChecked(true);
                                    rdBtnMdl.setChecked(false);
                                    rdBtnRht.setChecked(false);
                                    rdBtn4.setChecked(false);

                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_white));
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));

                                }else if (CurrentPage == 2){
                                    rdBtnLeft.setChecked(false);
                                    rdBtnMdl.setChecked(true);
                                    rdBtnRht.setChecked(false);
                                    rdBtn4.setChecked(false);
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_white));
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));
                                }else if (CurrentPage == 3){
                                    rdBtnLeft.setChecked(false);
                                    rdBtnMdl.setChecked(false);
                                    rdBtnRht.setChecked(true);
                                    rdBtn4.setChecked(false);
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnRht.setTextColor(getResources().getColor(R.color.zl_white));
                                    rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));

                                }else if (CurrentPage == 4){
                                    rdBtnLeft.setChecked(false);
                                    rdBtnMdl.setChecked(false);
                                    rdBtnRht.setChecked(false);
                                    rdBtn4.setChecked(true);
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtn4.setTextColor(getResources().getColor(R.color.zl_white));

                                }

                            }

                        }


                    }else {
                        fmworkView.setVisibility(View.VISIBLE);
                    }

                }else
                {
                    showErrorView(result.getResult().getRet_msg());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("xcz", "volleyError" + volleyError);
                showErrorView("网络异常！请重试");
            }
        });


    }




    class MyButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.radio_record_left_id:


                    if(Integer.parseInt(rdBtnLeft.getText().toString()) <= toTal){
                        rdBtnLeft.setChecked(true);
                        rdBtnMdl.setChecked(false);
                        rdBtnRht.setChecked(false);
                        rdBtn4.setChecked(false);
                        rdBtn5.setChecked(false);
                        rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_white));
                        rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtn5.setTextColor(getResources().getColor(R.color.zl_black));
                        getDatafromRecordApi(Integer.parseInt(rdBtnLeft.getText().toString()), SIZE);
//                        rdBtnLeft.setEnabled(false);
                    }

                    break;
                case R.id.radio_record_middle_id:


                    if(Integer.parseInt(rdBtnMdl.getText().toString()) <= toTal){
                        rdBtnMdl.setChecked(true);
                        rdBtnLeft.setChecked(false);
                        rdBtnRht.setChecked(false);
                        rdBtn4.setChecked(false);
                        rdBtn5.setChecked(false);
                        rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_white));
                        rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtn5.setTextColor(getResources().getColor(R.color.zl_black));
                        getDatafromRecordApi(Integer.parseInt(rdBtnMdl.getText().toString()), SIZE);
//                        rdBtnMdl.setEnabled(false);
                    }


                    break;
                case R.id.radio_record_right_id:

                    if(Integer.parseInt(rdBtnRht.getText().toString()) <= toTal){
                        rdBtnRht.setChecked(true);
                        rdBtnLeft.setChecked(false);
                        rdBtnMdl.setChecked(false);
                        rdBtn4.setChecked(false);
                        rdBtn5.setChecked(false);
                        rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtnRht.setTextColor(getResources().getColor(R.color.zl_white));
                        rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtn5.setTextColor(getResources().getColor(R.color.zl_black));
                        getDatafromRecordApi(Integer.parseInt(rdBtnRht.getText().toString()), SIZE);
//                        rdBtnRht.setEnabled(false);
                    }


                    break;
                case R.id.radio4_id:
                    if(Integer.parseInt(rdBtn4.getText().toString()) <= toTal){
                        rdBtn4.setChecked(true);
                        rdBtnLeft.setChecked(false);
                        rdBtnMdl.setChecked(false);
                        rdBtnRht.setChecked(false);
                        rdBtn5.setChecked(false);
                        rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtn4.setTextColor(getResources().getColor(R.color.zl_white));
                        rdBtn5.setTextColor(getResources().getColor(R.color.zl_black));
                        getDatafromRecordApi(Integer.parseInt(rdBtn4.getText().toString()), SIZE);
//                        rdBtn4.setEnabled(false);
                    }



                    break;

                case R.id.radio5_id:

                    if(Integer.parseInt(rdBtn5.getText().toString()) <= toTal){
                        rdBtn5.setChecked(true);
                        rdBtnLeft.setChecked(false);
                        rdBtnMdl.setChecked(false);
                        rdBtnRht.setChecked(false);
                        rdBtn4.setChecked(false);

                        rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtn5.setTextColor(getResources().getColor(R.color.zl_white));
                        getDatafromRecordApi(Integer.parseInt(rdBtn5.getText().toString()), SIZE);
//                        rdBtn5.setEnabled(false);
                    }


                    break;
                default:
                    break;
            }

        }
    }
}
