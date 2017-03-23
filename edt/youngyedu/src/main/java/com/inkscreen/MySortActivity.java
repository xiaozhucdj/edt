package com.inkscreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.inkscreen.adapter.MyChidrenAdapter;
import com.inkscreen.adapter.MyChidrenAdapter1;
import com.inkscreen.adapter.MySectionAdapter;
import com.inkscreen.adapter.MySoft_LeftAdapter;
import com.inkscreen.model.Event;
import com.inkscreen.model.SerializableMap;
import com.inkscreen.model.SortInfo;
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
 * Created by xcz on 2016/12/5.
 */
public class MySortActivity extends Activity {
    private String bookcode = null;

    private long sectionCode = -1;
    private String type = null;
    private ImageView SureBtn, calBtn;
    MultiListView left_LV, section_LV, children_LV, children1_LV;
    MySoft_LeftAdapter mySoft_leftAdapter;
    MySectionAdapter mySectionAdapter;
    MyChidrenAdapter myChidrenAdapter;
    MyChidrenAdapter1 myChidrenAdapter1;
    List<SortInfo.Ret.TEXTBOOK> mleftList;
    List<SortInfo.Ret.TEXTBOOK.Section> msectionList;
    List<SortInfo.Ret.TEXTBOOK.Section.Children> mChildrenList;
    List<SortInfo.Ret.TEXTBOOK.Section.Children.Children1> mChildrenList1;
    String name = "全部";

    Map<String, Object> myMap;

    private SerializableMap serializableMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mysort);
        left_LV = (MultiListView) findViewById(R.id.list_left_id);
        section_LV = (MultiListView) findViewById(R.id.section_id);
        children_LV = (MultiListView) findViewById(R.id.chiren_id);
        children1_LV = (MultiListView) findViewById(R.id.chiren1_id);
        SureBtn = (ImageView) findViewById(R.id.sure_id);
        calBtn = (ImageView) findViewById(R.id.cancel_id);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (null != (SerializableMap) bundle.get("seria")) {
                serializableMap = (SerializableMap) bundle.get("seria");
//                if (serializableMap.getMap().get("list_left_id")!=null){
//                    Log.i("xcz", "serializableMap:" + serializableMap.getMap().get("list_left_id"));
//                }
            }


        }

        mleftList = new ArrayList<>();
        msectionList = new ArrayList<>();
        mChildrenList = new ArrayList<>();
        mChildrenList1 = new ArrayList<>();
        mySoft_leftAdapter = new MySoft_LeftAdapter(MySortActivity.this);
        mySectionAdapter = new MySectionAdapter(MySortActivity.this);
        myChidrenAdapter = new MyChidrenAdapter(MySortActivity.this);
        myChidrenAdapter1 = new MyChidrenAdapter1(MySortActivity.this);

        mySoft_leftAdapter.setList(new ArrayList<SortInfo.Ret.TEXTBOOK>());
        mySectionAdapter.setList(new ArrayList<SortInfo.Ret.TEXTBOOK.Section>());
        myChidrenAdapter.setList(new ArrayList<SortInfo.Ret.TEXTBOOK.Section.Children>());
        myChidrenAdapter1.setList(new ArrayList<SortInfo.Ret.TEXTBOOK.Section.Children.Children1>());
        left_LV.setAdapter(mySoft_leftAdapter);
        section_LV.setAdapter(mySectionAdapter);
        children_LV.setAdapter(myChidrenAdapter);
        children1_LV.setAdapter(myChidrenAdapter1);


        left_LV.setOnItemClickListener(new MyOnItemClickLister());
        section_LV.setOnItemClickListener(new MyOnItemClickLister());
        children_LV.setOnItemClickListener(new MyOnItemClickLister());
        children1_LV.setOnItemClickListener(new MyOnItemClickLister());
        if (serializableMap != null) {
            myMap = serializableMap.getMap();
        } else {
            myMap = new HashMap<String, Object>();
        }
        getDatafromWorngApi();
        SureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                if (null != type) {
                    bundle.putString("type", type);
                }
                if (null != bookcode) {
                    bundle.putString("bookCode", bookcode);
                }

                if (sectionCode != -1) {
                    bundle.putLong("sectionCode", sectionCode);
                }

                if (!StringUtils.isEmptyStr(name)) {

                    bundle.putString("name", name);
                }

                if (myMap != null) {
                    SerializableMap map = new SerializableMap();
                    map.setMap(myMap);
                    bundle.putSerializable("seria", map);
                }
                intent.putExtras(bundle);
                MySortActivity.this.setResult(RESULT_OK, intent);

                EventBus.getDefault().post(new Event<Bundle>(2,bundle));
                finish();


            }
        });
        calBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    public void getDatafromWorngApi() {
        Map<String, String> map = new HashMap<>();
        LeApiUtils.postString(LeApiApp.getMySortUrl(), map, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                LeApiResult<SortInfo> result = new LeApiResult<SortInfo>(jsonObject, new TypeToken<SortInfo>() {
                });

                if (result.getResult().getRet_code() == 0) {


                    String[] strings = result.getResult().getRet().getALL();
                    String others = result.getResult().getRet().getOTHER();
                    int sum = 0;
                    for (int i = 0; i < 1; i++) {
                        sum += Integer.parseInt(strings[i]);

                    }
                    mleftList = result.getResult().getRet().getTEXTBOOK();

                    for (int j = 0; j < 2; j++) {
                        SortInfo.Ret.TEXTBOOK sortInfo = new SortInfo.Ret.TEXTBOOK();
                        if (j == 0) {
                            sortInfo.setCount(String.valueOf(sum));
                            sortInfo.setName("全部错题");
                            sortInfo.setCode(1L);
                            mleftList.add(0, sortInfo);
                        }
                        if (j == 1) {
                            sortInfo.setCount(String.valueOf(others));
                            sortInfo.setName("其他");
                            sortInfo.setCode(2L);
                            mleftList.add(sortInfo);
                        }

                    }


                    if (serializableMap != null) {
                        if (serializableMap.getMap().get("list_left_id") != null) {
                            long leftId = (Long) serializableMap.getMap().get("list_left_id");
                            for (int i = 0; i < mleftList.size(); i++) {

                                if (leftId == mleftList.get(i).getCode()) {
                                    if (leftId == 1) {
                                        type = "ALL";
                                        bookcode = null;
                                        name = "全部";
                                    } else if (leftId == 2) {
                                        type = "OTHER";
                                        bookcode = null;
                                        name = "其他";
                                    } else {
                                        type = "TEXTBOOK";
                                        bookcode = String.valueOf(leftId);
                                    }
                                    mleftList.get(i).setSeTag(true);
                                    mySoft_leftAdapter.getList().clear();
                                    mySoft_leftAdapter.getList().addAll(mleftList);
                                    mySoft_leftAdapter.notifyDataSetChanged();
                                    if (serializableMap.getMap().get("section_id") != null) {

                                        for (int j = 0; j < mleftList.get(i).getSections().size(); j++) {

                                            if ((long) serializableMap.getMap().get("section_id") == mleftList.get(i).getSections().get(j).getCode()) {
                                                mySoft_leftAdapter.setChildSelect(true);
                                                msectionList.clear();
                                                msectionList.addAll(mleftList.get(i).getSections());
                                                msectionList.get(j).setSeTag(true);
                                                mySectionAdapter.getList().clear();
                                                mySectionAdapter.getList().addAll(mleftList.get(i).getSections());
                                                mySectionAdapter.notifyDataSetChanged();
                                                sectionCode = mleftList.get(i).getSections().get(j).getCode();
                                                name = mleftList.get(i).getSections().get(j).getName();
                                                if (serializableMap.getMap().get("chiren_id") != null) {

                                                    for (int k = 0; k < mleftList.get(i).getSections().get(j).getChildren().size(); k++) {
                                                        if ((long) serializableMap.getMap().get("chiren_id") == mleftList.get(i).getSections().get(j).getChildren().get(k).getCode()) {

                                                            mySectionAdapter.setChildSelect(true);
                                                            myChidrenAdapter.getList().clear();
                                                            mChildrenList.clear();
                                                            mChildrenList.addAll(mleftList.get(i).getSections().get(j).getChildren());
                                                            mChildrenList.get(k).setSeTag(true);
                                                            myChidrenAdapter.getList().addAll(mleftList.get(i).getSections().get(j).getChildren());
                                                            myChidrenAdapter.notifyDataSetChanged();
                                                            sectionCode = mleftList.get(i).getSections().get(j).getChildren().get(k).getCode();
                                                            name = mleftList.get(i).getSections().get(j).getChildren().get(k).getName();
                                                            if (serializableMap.getMap().get("chiren1_id") != null) {
                                                                for (int m = 0; m < mleftList.get(i).getSections().get(j).getChildren().get(k).getChildren().size(); m++) {
                                                                    if ((long) serializableMap.getMap().get("chiren1_id") == mleftList.get(i).getSections().get(j).getChildren().get(k).getChildren().get(m).getCode()) {
                                                                        myChidrenAdapter.setChildSelect(true);
                                                                        myChidrenAdapter1.getList().clear();
                                                                        mChildrenList1.clear();
                                                                        mChildrenList1.addAll(mleftList.get(i).getSections().get(j).getChildren().get(k).getChildren());
                                                                        mChildrenList1.get(m).setSeTag(true);
                                                                        myChidrenAdapter1.getList().addAll(mChildrenList1);
                                                                        myChidrenAdapter1.notifyDataSetChanged();
                                                                        sectionCode = mleftList.get(i).getSections().get(j).getChildren().get(k).getChildren().get(m).getCode();
                                                                        name = mleftList.get(i).getSections().get(j).getChildren().get(k).getChildren().get(m).getName();

                                                                    } else {
                                                                        mleftList.get(i).getSections().get(j).getChildren().get(k).getChildren().get(m).setSeTag(false);
                                                                    }
                                                                }
                                                            }
                                                        } else {
                                                            mleftList.get(i).getSections().get(j).getChildren().get(k).setSeTag(false);
                                                        }
                                                    }
                                                }
                                            } else {
                                                mleftList.get(i).getSections().get(j).setSeTag(false);
                                            }
                                        }
                                    }

                                } else {
                                    mleftList.get(i).setSeTag(false);
                                }
                            }
                        } else {
                            mleftList.get(0).setSeTag(true);
                            mySoft_leftAdapter.getList().clear();
                            mySoft_leftAdapter.getList().addAll(mleftList);
                            mySoft_leftAdapter.notifyDataSetChanged();
                        }
                    } else {
                        mleftList.get(0).setSeTag(true);
                        mySoft_leftAdapter.getList().clear();
                        mySoft_leftAdapter.getList().addAll(mleftList);
                        mySoft_leftAdapter.notifyDataSetChanged();
                    }


                }


                Log.d("xcz", "onResponse" + jsonObject);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("xcz", "volleyError" + volleyError);
            }
        }, this);


    }


    class MyOnItemClickLister implements AdapterView.OnItemClickListener {


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            switch (parent.getId()) {
                case R.id.list_left_id:
                    resetAdapter();
                    for (int i = 0; i < mleftList.size(); i++) {
//                        mleftList.get(i).setSeTag(false);
                        if (i == position) {
                            if (mleftList.get(position).getSections() != null && (position != 0 || position != mleftList.size() - 1)) {
                                msectionList.clear();
                                msectionList.addAll( mleftList.get(position).getSections());
                            } else {
                                if (msectionList != null && msectionList.size() > 0) {
                                    msectionList.clear();
                                }

                            }

                            if (position == 0) {
                                type = "ALL";
                                bookcode = null;
                                myMap.put("list_left_id", 1L);
                                name = "全部";
                            } else if (position == mleftList.size() - 1) {
                                type = "OTHER";
                                bookcode = null;
                                myMap.put("list_left_id", 2L);
                                name = "其他";
                            } else {
                                type = "TEXTBOOK";
                                bookcode = String.valueOf(mleftList.get(position).getCode());
                                myMap.put("list_left_id", mleftList.get(position).getCode());
                                name = mleftList.get(i).getName();
                            }

                            mleftList.get(i).setSeTag(true);


                        } else {
                            mleftList.get(i).setSeTag(false);

                        }
                    }

                    mySoft_leftAdapter.getList().clear();
                    mySoft_leftAdapter.getList().addAll(mleftList);
                    mySoft_leftAdapter.notifyDataSetChanged();


                    mySectionAdapter.getList().clear();
                    mySectionAdapter.getList().addAll(msectionList);
                    mySectionAdapter.reSetStatus();
                    mySectionAdapter.notifyDataSetChanged();

                    myChidrenAdapter.getList().clear();
                    // myChidrenAdapter.getList().addAll(null);
                    myChidrenAdapter.notifyDataSetChanged();
                    myChidrenAdapter1.getList().clear();
                    // myChidrenAdapter.getList().addAll(null);
                    myChidrenAdapter1.notifyDataSetChanged();


                    sectionCode=-1;
                    myMap.remove("section_id");
                    myMap.remove("chiren_id");

                    break;

                case R.id.section_id:
                    mySoft_leftAdapter.setChildSelect(true);
                    mySectionAdapter.setChildSelect(false);
                    myChidrenAdapter.setChildSelect(false);
                    myChidrenAdapter1.setChildSelect(false);
                    for (int i = 0; i < msectionList.size(); i++) {
//                        msectionList.get(i).setSeTag(false);
                        if (i == position) {
                            if (msectionList.get(position).getChildren() != null) {
                                mChildrenList.clear();
                                mChildrenList.addAll(msectionList.get(position).getChildren());
                            }

                            //    type = null;
                            sectionCode = msectionList.get(position).getCode();


                            msectionList.get(i).setSeTag(true);
                            name = msectionList.get(i).getName();
                            myMap.put("section_id", sectionCode);
                        } else {

                            msectionList.get(i).setSeTag(false);
                        }
                    }
                    mySoft_leftAdapter.notifyDataSetChanged();
                    mySectionAdapter.getList().clear();
                    mySectionAdapter.getList().addAll(msectionList);
                    mySectionAdapter.notifyDataSetChanged();


                    myChidrenAdapter.getList().clear();
                    myChidrenAdapter.getList().addAll(mChildrenList);
                    myChidrenAdapter.reSetStatus();
                    myChidrenAdapter.notifyDataSetChanged();

                    myChidrenAdapter1.getList().clear();
                    // myChidrenAdapter.getList().addAll(null);
                    myChidrenAdapter1.notifyDataSetChanged();
                    myMap.remove("chiren_id");
                    break;

                case R.id.chiren_id:
                    mySectionAdapter.setChildSelect(true);
                    myChidrenAdapter.setChildSelect(false);
                    myChidrenAdapter1.setChildSelect(false);
                    for (int i = 0; i < mChildrenList.size(); i++) {
//                        mChildrenList.get(i).setSeTag(false);
                        if (i == position) {
                            if (mChildrenList.get(position).getChildren() != null) {
                                mChildrenList1.clear();
                                mChildrenList1 .addAll(mChildrenList.get(position).getChildren());
                            }

                            // type = null;
                            sectionCode = mChildrenList.get(position).getCode();
                            mChildrenList.get(i).setSeTag(true);
                            name = mChildrenList.get(i).getName();
                            myMap.put("chiren_id", sectionCode);
                        } else {
                            mChildrenList.get(i).setSeTag(false);
                        }
                    }
                    mySectionAdapter.notifyDataSetChanged();
                    myChidrenAdapter.getList().clear();
                    myChidrenAdapter.getList().addAll(mChildrenList);
                    myChidrenAdapter.notifyDataSetChanged();


                    myChidrenAdapter1.getList().clear();
                    myChidrenAdapter1.getList().addAll(mChildrenList1);
                    myChidrenAdapter1.reSetStatus();
                    myChidrenAdapter1.notifyDataSetChanged();

                    break;
                case R.id.chiren1_id:

                    myChidrenAdapter.setChildSelect(true);
                    myChidrenAdapter1.setChildSelect(false);
                    for (int i = 0; i < mChildrenList1.size(); i++) {
//                        mChildrenList1.get(i).setSeTag(false);
                        if (i == position) {
//                            if(mChildrenList.get(position).getChildren() !=null ){
//                                mChildrenList1 = mChildrenList.get(position).getChildren();
//                            }

                            // type = null;
                            sectionCode = mChildrenList1.get(position).getCode();
                            mChildrenList1.get(i).setSeTag(true);
                            name = mChildrenList1.get(i).getName();
                            myMap.put("chiren1_id", sectionCode);
                        } else {
                            mChildrenList1.get(i).setSeTag(false);
                        }
                    }
                    myChidrenAdapter.notifyDataSetChanged();
                    myChidrenAdapter1.getList().clear();
                    myChidrenAdapter1.getList().addAll(mChildrenList1);
                    myChidrenAdapter1.notifyDataSetChanged();

                    break;

                default:
                    break;
            }

        }
    }


    private void resetAdapter() {
        mySoft_leftAdapter.setChildSelect(false);
        mySectionAdapter.setChildSelect(false);
        myChidrenAdapter.setChildSelect(false);
        myChidrenAdapter1.setChildSelect(false);
    }
}
