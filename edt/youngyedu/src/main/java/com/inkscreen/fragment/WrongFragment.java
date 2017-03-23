package com.inkscreen.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.inkscreen.MySortActivity;
import com.inkscreen.MyWrongActivity;
import com.inkscreen.adapter.MyWrongAdapter;
import com.inkscreen.model.Event;
import com.inkscreen.model.SerializableMap;
import com.inkscreen.model.WrongInfo;
import com.inkscreen.utils.LeApiApp;
import com.inkscreen.utils.LeApiResult;
import com.inkscreen.utils.LeApiUtils;
import com.inkscreen.utils.StringUtils;
import com.inkscreen.will.utils.widget.MultiListView;
import com.yougy.ui.activity.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by xcz on 2016/11/27.
 */
public class WrongFragment extends BaseFragment {
    LinearLayout lyCentre;
    MultiListView mworngLV;
    MyWrongAdapter myWrongAdapter;
    List<WrongInfo.Ret.Items> mWrongList;
    private static final int PAGE = 1;
    private static final int SIZE = 7;
    EditText CodeTxt;
    Button CodeBtn;
    RadioButton rdBtnLeft, rdBtnMdl, rdBtnRht, rdBtn4, rdBtn5;
    private static int toTal = 0;
    private static final int REQUESTCODE = 1;
    private static final int RESULT_OK = -1;
    private String types = "ALL";
    private String bookCode = null;
    private long sectionCodes;
    TextView textName;
    TextView textWrong;
    private int CurrentPage = 1;
    FrameLayout fmworkView;
    TextView textTishi;
    private SerializableMap serializableMap;
    private View errorView;
    private View layoutContent;
    private View layoutBottom;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View messageLayout = inflater.inflate(R.layout.worngfragment, container, false);
        lyCentre = (LinearLayout) messageLayout.findViewById(R.id.linearcentre_id);
        lyCentre.setOnClickListener(new MyButtonClickListener());
        mworngLV = (MultiListView) messageLayout.findViewById(R.id.wornglv_id);
        CodeTxt = (EditText) messageLayout.findViewById(R.id.verifyCodeTxt);
        textTishi = (TextView) messageLayout.findViewById(R.id.tishi_id);
        CodeTxt.clearFocus();
        fmworkView = (FrameLayout) messageLayout.findViewById(R.id.view_id);
        CodeBtn = (Button) messageLayout.findViewById(R.id.getVerifyCodeBtn);
        rdBtnLeft = (RadioButton) messageLayout.findViewById(R.id.radio_worng_left_id);
        rdBtnMdl = (RadioButton) messageLayout.findViewById(R.id.radio_worng_middle_id);
        rdBtnRht = (RadioButton) messageLayout.findViewById(R.id.radio_worng_right_id);
        rdBtn5 = (RadioButton) messageLayout.findViewById(R.id.radio5_id);
        rdBtn4 = (RadioButton) messageLayout.findViewById(R.id.radio4_id);
        textName = (TextView) messageLayout.findViewById(R.id.name_id);
        textWrong = (TextView) messageLayout.findViewById(R.id.cuoticount_id);


        rdBtnLeft.setOnClickListener(new MyButtonClickListener());
        rdBtnMdl.setOnClickListener(new MyButtonClickListener());
        rdBtnRht.setOnClickListener(new MyButtonClickListener());
        rdBtn4.setOnClickListener(new MyButtonClickListener());
        rdBtn5.setOnClickListener(new MyButtonClickListener());

        CodeBtn.setOnClickListener(new MyButtonClickListener());
        mWrongList = new ArrayList<>();
        myWrongAdapter = new MyWrongAdapter(getActivity());
        myWrongAdapter.setList(new ArrayList<WrongInfo.Ret.Items>());

        myWrongAdapter.SetParentView(mworngLV);
        mworngLV.setAdapter(myWrongAdapter);


        mworngLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


        myWrongAdapter.setOnclickActionListener(new MyWrongAdapter.ActionListener() {
            @Override
            public void RunAction(int position) {
                WrongInfo.Ret.Items items = mWrongList.get(position);
                Intent intent = new Intent();
                intent.setClass(getActivity(), MyWrongActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", items);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            }
        });

        getDatafromWorngApi("ALL", PAGE, SIZE, null, null, -1);
        initEorrView(messageLayout);

        EventBus.getDefault().register(this);
        return messageLayout;
    }

    private void initEorrView(View view) {
        layoutContent = view.findViewById(R.id.worng_layout);
        layoutBottom = view.findViewById(R.id.layout_bottom);
        errorView = view.findViewById(R.id.layout_net_error);
        errorView.findViewById(R.id.imgnet_id);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDatafromWorngApi("ALL", PAGE, SIZE, null, null, -1);
            }
        });
    }

    @Override
    public void refreshFragment(Bundle bundle) {
        super.refreshFragment(bundle);
        types = "ALL";
        bookCode = null;
        sectionCodes = -1;
        serializableMap=null;
        if (CodeTxt != null) {
            CodeTxt.setText("");
        }
        if (textName != null) {
            textName.setText("全部");
        }
        getDatafromWorngApi(types, PAGE, SIZE, null, null, -1);
    }

    class MyButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.linearcentre_id:
                    lyCentre.setBackgroundResource(R.drawable.btn_market_selector);

                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    intent.setClass(getActivity(), MySortActivity.class);
                    if (serializableMap != null) {
                        bundle.putSerializable("seria", serializableMap);

                    }
                    intent.putExtras(bundle);
                    startActivityForResult(intent, REQUESTCODE);

                    break;
                case R.id.getVerifyCodeBtn:
                    closeKeybord();

//                        if (CodeTxt.getText().toString().equals("全部")) {
//                            getDatafromWorngApi("ALL", PAGE, SIZE, null, null, -1);
//                        } else {
//                            getDatafromWorngApi(types, PAGE, SIZE, CodeTxt.getText().toString(), bookCode, -1);
//                        }
                    getDatafromWorngApi(types, PAGE, SIZE, CodeTxt.getText().toString(), bookCode, sectionCodes);


                    break;

                case R.id.radio_worng_left_id:


                    if (Integer.parseInt(rdBtnLeft.getText().toString()) <= toTal) {
//                        rdBtnLeft.setChecked(true);
//                        rdBtnMdl.setChecked(false);
//                        rdBtnRht.setChecked(false);
//                        rdBtn4.setChecked(false);
//                        rdBtn5.setChecked(false);
                        rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_white));
                        rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtn5.setTextColor(getResources().getColor(R.color.zl_black));
                        getDatafromWorngApi(types, Integer.parseInt(rdBtnLeft.getText().toString()), SIZE, CodeTxt.getText().toString(),  bookCode, sectionCodes);
//                        rdBtnLeft.setEnabled(false);
                    }

                    break;
                case R.id.radio_worng_middle_id:


                    if (Integer.parseInt(rdBtnMdl.getText().toString()) <= toTal) {
//                        rdBtnLeft.setChecked(false);
//                        rdBtnMdl.setChecked(true);
//                        rdBtnRht.setChecked(false);
//                        rdBtn4.setChecked(false);
//                        rdBtn5.setChecked(false);
                        rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_white));
                        rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtn5.setTextColor(getResources().getColor(R.color.zl_black));
                        getDatafromWorngApi(types, Integer.parseInt(rdBtnMdl.getText().toString()), SIZE, CodeTxt.getText().toString(), bookCode, sectionCodes);
//                        rdBtnMdl.setEnabled(false);
                    }


                    break;
                case R.id.radio_worng_right_id:

                    if (Integer.parseInt(rdBtnRht.getText().toString()) <= toTal) {
//                        rdBtnLeft.setChecked(false);
//                        rdBtnMdl.setChecked(false);
//                        rdBtnRht.setChecked(true);
//                        rdBtn4.setChecked(false);
//                        rdBtn5.setChecked(false);
                        rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtnRht.setTextColor(getResources().getColor(R.color.zl_white));
                        rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtn5.setTextColor(getResources().getColor(R.color.zl_black));
                        getDatafromWorngApi(types, Integer.parseInt(rdBtnRht.getText().toString()), SIZE, CodeTxt.getText().toString(),  bookCode, sectionCodes);
//                        rdBtnRht.setEnabled(false);
                    }


                    break;
                case R.id.radio4_id:
                    if (Integer.parseInt(rdBtn4.getText().toString()) <= toTal) {
//                        rdBtnLeft.setChecked(false);
//                        rdBtnMdl.setChecked(false);
//                        rdBtnRht.setChecked(false);
//                        rdBtn4.setChecked(true);
//                        rdBtn5.setChecked(false);
                        rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtn4.setTextColor(getResources().getColor(R.color.zl_white));
                        rdBtn5.setTextColor(getResources().getColor(R.color.zl_black));
                        getDatafromWorngApi(types, Integer.parseInt(rdBtn4.getText().toString()), SIZE, CodeTxt.getText().toString(),  bookCode, sectionCodes);
//                        rdBtn4.setEnabled(false);
                    }


                    break;

                case R.id.radio5_id:

                    if (Integer.parseInt(rdBtn5.getText().toString()) <= toTal) {
//                        rdBtnLeft.setChecked(false);
//                        rdBtnMdl.setChecked(false);
//                        rdBtnRht.setChecked(false);
//                        rdBtn4.setChecked(false);
//                        rdBtn5.setChecked(true);
                        rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));
                        rdBtn5.setTextColor(getResources().getColor(R.color.zl_white));
                        getDatafromWorngApi(types, Integer.parseInt(rdBtn5.getText().toString()), SIZE, CodeTxt.getText().toString(),  bookCode, sectionCodes);
//                        rdBtn5.setEnabled(false);
                    }


                    break;
                default:
                    break;
            }

        }
    }

//final String type,final int loadPage


    private void getDatafromWorngApi(String type, int page, int size, String key, String textbookCode, long sectionCode) {

        Map<String, String> map = new HashMap<>();
        map.put("type", "" + type);
        map.put("page", "" + page);
        map.put("size", "" + size);
        if (null != key && !TextUtils.isEmpty(key.trim())) {
            map.put("key", key);
        }
        if (null != textbookCode && !StringUtils.isEmptyStr(textbookCode)) {
            map.put("textbookCode", textbookCode);
        }


        if (sectionCode != -1) {
            map.put("sectionCode", "" + sectionCode);

        }


        Log.e("getDatafromWorngApi", "request_params_map->" + map.toString());
        LeApiUtils.postString(LeApiApp.getWorngUrl(type, page, size), map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.i("xcz", "" + jsonObject);
                LeApiResult<WrongInfo> result = new LeApiResult<WrongInfo>(jsonObject, new TypeToken<WrongInfo>() {
                });

                if (result.getCode() == 0) {
                    layoutContent.setVisibility(View.VISIBLE);
                    layoutBottom.setVisibility(View.VISIBLE);
                    errorView.setVisibility(View.GONE);
                    if (result.getResult() != null && result.getResult().getRet() != null && result.getResult().getRet().getItems().size() > 0) {
                        mworngLV.setVisibility(View.VISIBLE);
                        fmworkView.setVisibility(View.GONE);
                        CurrentPage = result.getResult().getRet().getCurrentPage();
                        textWrong.setVisibility(View.VISIBLE);
                        textWrong.setText("共计" + result.getResult().getRet().getTotal() + "题");
                        mWrongList = result.getResult().getRet().getItems();
                        myWrongAdapter.getList().clear();
                        myWrongAdapter.getList().addAll(mWrongList);
                        myWrongAdapter.notifyDataSetChanged();
                        toTal = result.getResult().getRet().getTotalPage();

                        Log.i("xcz", "xxxx" + CurrentPage + "CCCCCC" + toTal);
                        if (toTal > 4) {
                            rdBtnLeft.setVisibility(View.VISIBLE);
                            rdBtnMdl.setVisibility(View.VISIBLE);
                            rdBtnRht.setVisibility(View.VISIBLE);
                            rdBtn4.setVisibility(View.VISIBLE);
                            rdBtn5.setVisibility(View.VISIBLE);

                            if (CurrentPage <= 4) {
                                rdBtnLeft.setText("1");
                                rdBtnMdl.setText("2");
                                rdBtnRht.setText("3");
                                rdBtn4.setText("4");
                                rdBtn5.setText("5");

                                if (CurrentPage == 1) {
                                    rdBtnLeft.setChecked(true);
                                    rdBtnMdl.setChecked(false);
                                    rdBtnRht.setChecked(false);
                                    rdBtn4.setChecked(false);
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_white));
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtn5.setTextColor(getResources().getColor(R.color.zl_black));
                                } else if (CurrentPage == 2) {
                                    rdBtnLeft.setChecked(false);
                                    rdBtnMdl.setChecked(true);
                                    rdBtnRht.setChecked(false);
                                    rdBtn4.setChecked(false);
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_white));
                                    rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtn5.setTextColor(getResources().getColor(R.color.zl_black));
                                } else if (CurrentPage == 3) {
                                    rdBtnLeft.setChecked(false);
                                    rdBtnMdl.setChecked(false);
                                    rdBtnRht.setChecked(true);
                                    rdBtn4.setChecked(false);
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnRht.setTextColor(getResources().getColor(R.color.zl_white));
                                    rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtn5.setTextColor(getResources().getColor(R.color.zl_black));
                                } else if (CurrentPage == 4) {

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

                            } else if (CurrentPage + 2 > 6 && CurrentPage == toTal) {
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

                            } else if (CurrentPage + 2 > 6 && CurrentPage + 2 <= toTal) {

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

                            } else if (CurrentPage + 2 > 6 && CurrentPage + 1 == toTal) {

                                rdBtnLeft.setText(String.valueOf(result.getResult().getRet().getCurrentPage() - 3));
                                rdBtnMdl.setText(String.valueOf(result.getResult().getRet().getCurrentPage() - 2));
                                rdBtnRht.setText(String.valueOf(result.getResult().getRet().getCurrentPage() - 1));
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

                        } else {
                            rdBtnLeft.setText("1");
                            rdBtnMdl.setText("2");
                            rdBtnRht.setText("3");
                            rdBtn4.setText("4");
                            if (toTal == 1) {
                                rdBtnLeft.setVisibility(View.VISIBLE);
                                rdBtnMdl.setVisibility(View.GONE);
                                rdBtnRht.setVisibility(View.GONE);
                                rdBtn4.setVisibility(View.GONE);
                                rdBtn5.setVisibility(View.GONE);

                                if (CurrentPage == 1) {
                                    rdBtnLeft.setChecked(true);
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_white));
                                }

                            } else if (toTal == 2) {
                                rdBtnLeft.setVisibility(View.VISIBLE);
                                rdBtnMdl.setVisibility(View.VISIBLE);
                                rdBtnRht.setVisibility(View.GONE);
                                rdBtn4.setVisibility(View.GONE);
                                rdBtn5.setVisibility(View.GONE);
                                if (CurrentPage == 1) {
                                    rdBtnLeft.setChecked(true);
                                    rdBtnMdl.setChecked(false);
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_white));
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));

                                } else if (CurrentPage == 2) {
                                    rdBtnLeft.setChecked(false);
                                    rdBtnMdl.setChecked(true);

                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_white));
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                                }

                            } else if (toTal == 3) {
                                rdBtnLeft.setVisibility(View.VISIBLE);
                                rdBtnMdl.setVisibility(View.VISIBLE);
                                rdBtnRht.setVisibility(View.VISIBLE);
                                rdBtn4.setVisibility(View.GONE);
                                rdBtn5.setVisibility(View.GONE);
                                if (CurrentPage == 1) {
                                    rdBtnLeft.setChecked(true);
                                    rdBtnMdl.setChecked(false);
                                    rdBtnRht.setChecked(false);
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_white));
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));

                                } else if (CurrentPage == 2) {
                                    rdBtnLeft.setChecked(false);
                                    rdBtnMdl.setChecked(true);
                                    rdBtnRht.setChecked(false);
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_white));
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                                } else if (CurrentPage == 3) {
                                    rdBtnLeft.setChecked(false);
                                    rdBtnMdl.setChecked(false);
                                    rdBtnRht.setChecked(true);
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnRht.setTextColor(getResources().getColor(R.color.zl_white));

                                }

                            } else if (toTal == 4) {
                                rdBtnLeft.setVisibility(View.VISIBLE);
                                rdBtnMdl.setVisibility(View.VISIBLE);
                                rdBtnRht.setVisibility(View.VISIBLE);
                                rdBtn4.setVisibility(View.VISIBLE);
                                rdBtn5.setVisibility(View.GONE);
                                if (CurrentPage == 1) {
                                    rdBtnLeft.setChecked(true);
                                    rdBtnMdl.setChecked(false);
                                    rdBtnRht.setChecked(false);
                                    rdBtn4.setChecked(false);

                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_white));
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));

                                } else if (CurrentPage == 2) {
                                    rdBtnLeft.setChecked(false);
                                    rdBtnMdl.setChecked(true);
                                    rdBtnRht.setChecked(false);
                                    rdBtn4.setChecked(false);
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_white));
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnRht.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));
                                } else if (CurrentPage == 3) {
                                    rdBtnLeft.setChecked(false);
                                    rdBtnMdl.setChecked(false);
                                    rdBtnRht.setChecked(true);
                                    rdBtn4.setChecked(false);
                                    rdBtnMdl.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_black));
                                    rdBtnRht.setTextColor(getResources().getColor(R.color.zl_white));
                                    rdBtn4.setTextColor(getResources().getColor(R.color.zl_black));

                                } else if (CurrentPage == 4) {
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


                    } else {
                        textWrong.setVisibility(View.GONE);
                        fmworkView.setVisibility(View.VISIBLE);
                        mworngLV.setVisibility(View.GONE);
                        layoutBottom.setVisibility(View.GONE);
                        if (!StringUtils.isEmptyStr(CodeTxt.getText().toString())) {
                            textTishi.setText("没有搜到和“" + CodeTxt.getText().toString() + "”相关的错题");
                        }

                        //Toast.makeText(getActivity(),"没搜到~~",Toast.LENGTH_SHORT).show();

                    }
                } else {
                    showErrorView(result.getResult().getRet_msg());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                showErrorView("网络异常！请重试");
            }
        }, this);


    }

    private void showErrorView(String msg) {
        if (myWrongAdapter == null || myWrongAdapter.getCount() == 0) {
            layoutContent.setVisibility(View.GONE);
            layoutBottom.setVisibility(View.GONE);
            errorView.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }
    public void onEvent(Event<Object> event) {
        if (event.getRequestCode() == 2) {

            if(event.getTarget()!=null&&event.getTarget() instanceof Bundle)
            {
                Bundle bundle= (Bundle) event.getTarget();
                bookCode = bundle.getString("bookCode", null);
                types = bundle.getString("type", "All");
                sectionCodes = bundle.getLong("sectionCode", -1);
                Log.i("xcz", "ddddddd" + bookCode + "sasasasa" + types + "fffff" + sectionCodes);
                textName.setText(bundle.getString("name"));
                serializableMap = (SerializableMap) bundle.get("seria");

                getDatafromWorngApi(types, PAGE, SIZE, null, bookCode, sectionCodes);
            }

        }


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE) {
            switch (resultCode) {
                case RESULT_OK:
//                    Bundle bundle = new Bundle();
//                    bundle = data.getExtras();
//                    bookCode = bundle.getString("bookCode", null);
//                    types = bundle.getString("type", "All");
//                    sectionCodes = bundle.getLong("sectionCode", -1);
//                    Log.i("xcz", "ddddddd" + bookCode + "sasasasa" + types + "fffff" + sectionCodes);
//                    textName.setText(bundle.getString("name"));
//                    serializableMap = (SerializableMap) bundle.get("seria");
//
//                    getDatafromWorngApi(types, PAGE, SIZE, null, bookCode, sectionCodes);

                    break;
                default:
                    break;
            }
        }


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeKeybord();
        EventBus.getDefault().unregister(this);
    }


    private void closeKeybord() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(CodeTxt.getWindowToken(), 0);
    }


    @Override
    public void onResume() {
        super.onResume();


    }

}
