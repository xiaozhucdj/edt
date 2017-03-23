package com.inkscreen.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.inkscreen.AnswerActivity;
import com.inkscreen.adapter.MyWorkingAdapter;
import com.inkscreen.model.Event;
import com.inkscreen.model.WorkInfo;
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

import de.greenrobot.event.EventBus;

/**
 * Created by xcz on 2016/11/27.
 */
public class WorkFragment extends BaseFragment {
    MultiListView mworkLV, mworkreLV;
    MyWorkingAdapter myWorkingAdapter;
    List<WorkInfo.Ret.Items> mWorkList;
    RadioButton rdBtnLeft, rdBtnMdl, rdBtnRht, rdBtn4, rdBtn5;
    Handler mHander = new Handler();

    private static final int PAGE = 1;
    private static final int SIZE = 8;
    private static int toTal = 0;
    private static int CurrentPage = 1;
    FrameLayout fmworkView;
    private View contentView;
    private View errorView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View messageLayout = inflater.inflate(R.layout.workfragment, container, false);
        mworkLV = (MultiListView) messageLayout.findViewById(R.id.worklistview_id);
        contentView = messageLayout.findViewById(R.id.content_view);

        fmworkView = (FrameLayout) messageLayout.findViewById(R.id.view_id);
        rdBtnLeft = (RadioButton) messageLayout.findViewById(R.id.radio_left_id);
        rdBtnMdl = (RadioButton) messageLayout.findViewById(R.id.radio_middle_id);
        rdBtnRht = (RadioButton) messageLayout.findViewById(R.id.radio_right_id);
        rdBtn5 = (RadioButton) messageLayout.findViewById(R.id.radio5_id);
        rdBtn4 = (RadioButton) messageLayout.findViewById(R.id.radio4_id);
        mWorkList = new ArrayList<>();
        myWorkingAdapter = new MyWorkingAdapter(getActivity());
        myWorkingAdapter.setList(new ArrayList<WorkInfo.Ret.Items>());
        mworkLV.setAdapter(myWorkingAdapter);

        initEorrView(messageLayout);
        getDatafromWorkApi(PAGE, SIZE);
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

        mworkLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                String homeWorkId = mWorkList.get(position).getId();
                intent.putExtra("homeWorkId", homeWorkId);
                intent.setClass(getActivity(), AnswerActivity.class);
                startActivity(intent);

            }
        });


        EventBus.getDefault().register(this);
        return messageLayout;
    }

    private void initEorrView(View view) {
        errorView = view.findViewById(R.id.layout_net_error);
        errorView.findViewById(R.id.imgnet_id);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDatafromWorkApi(PAGE, SIZE);
            }
        });
    }

    @Override
    public void refreshFragment(Bundle bundle) {
        super.refreshFragment(bundle);
        getDatafromWorkApi(PAGE, SIZE);
    }

    public void onEvent(Event<Object> event) {
        if (event.getRequestCode() == 1) {

            getDatafromWorkApi(CurrentPage, SIZE);
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }


    private void showErrorView(String msg) {
        if (myWorkingAdapter == null || myWorkingAdapter.getCount() == 0) {
            contentView.setVisibility(View.GONE);
            errorView.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void getDatafromWorkApi(int page, int size) {

        Map<String, String> map = new HashMap<>();
        map.put("page", "" + page);
        map.put("size", "" + size);
        Log.d("xcz", "Url" + LeApiApp.getWorkUrl(page, size));
        LeApiUtils.postString(LeApiApp.getWorkUrl(page, size), map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {


                LeApiResult<WorkInfo> result = new LeApiResult<WorkInfo>(jsonObject, new TypeToken<WorkInfo>() {
                });

                Log.d("xcz", "onResponse" + jsonObject);
                if (result.getResult().getRet_code() == 0) {
                    contentView.setVisibility(View.VISIBLE);
                    errorView.setVisibility(View.GONE);
                    if (null != result.getResult().getRet() && null != result.getResult().getRet().getItems() && result.getResult().getRet().getItems().size() > 0) {
                        fmworkView.setVisibility(View.GONE);
                        mworkLV.setVisibility(View.VISIBLE);
                        mWorkList = result.getResult().getRet().getItems();
                        myWorkingAdapter.getList().clear();
                        myWorkingAdapter.getList().addAll(mWorkList);
                        myWorkingAdapter.notifyDataSetChanged();
                        toTal = result.getResult().getRet().getTotalPage();
                        CurrentPage = result.getResult().getRet().getCurrentPage();


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
                            if (toTal == 1) {
                                rdBtnLeft.setVisibility(View.VISIBLE);
                                if (CurrentPage == 1) {
                                    rdBtnLeft.setChecked(true);
                                    rdBtnLeft.setTextColor(getResources().getColor(R.color.zl_white));
                                }

                            } else if (toTal == 2) {
                                rdBtnLeft.setVisibility(View.VISIBLE);
                                rdBtnMdl.setVisibility(View.VISIBLE);
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
                        mworkLV.setVisibility(View.GONE);
                        myWorkingAdapter.getList().clear();
                        fmworkView.setVisibility(View.VISIBLE);
                    }
                } else {
                    showErrorView(result.getResult().getRet_msg());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("xcz", "volleyError" + volleyError);
                showErrorView("网络异常！请重试");
            }
        }, this);


    }


    class MyButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.radio_left_id:

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
                        getDatafromWorkApi(Integer.parseInt(rdBtnLeft.getText().toString()), SIZE);
                        //   rdBtnLeft.setEnabled(false);
                    }


                    break;
                case R.id.radio_middle_id:


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
                        getDatafromWorkApi(Integer.parseInt(rdBtnMdl.getText().toString()), SIZE);
                        //   rdBtnMdl.setEnabled(false);
                    }


                    break;
                case R.id.radio_right_id:

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
                        getDatafromWorkApi(Integer.parseInt(rdBtnRht.getText().toString()), SIZE);
                        //   rdBtnRht.setEnabled(false);
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
                        getDatafromWorkApi(Integer.parseInt(rdBtn4.getText().toString()), SIZE);
                        //   rdBtn4.setEnabled(false);
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
                        getDatafromWorkApi(Integer.parseInt(rdBtn5.getText().toString()), SIZE);
                        //  rdBtn5.setEnabled(false);
                    }


                    break;
                default:
                    break;
            }

        }
    }
}
