package com.inkscreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.inkscreen.model.UserInfo;
import com.inkscreen.utils.AndroidUtils;
import com.inkscreen.utils.LeApiApp;
import com.inkscreen.utils.LeApiResult;
import com.inkscreen.utils.LeApiUtils;
import com.yougy.ui.activity.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xcz on 2016/11/28.
 */
public class LoginActivity extends Activity{
    EditText editText1,editText2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginlayout);
        editText1 = (EditText)findViewById(R.id.edit1);
         editText2 = (EditText)findViewById(R.id.edit2);
        Button button1= (Button)findViewById(R.id.button_id1);
        Button button2= (Button)findViewById(R.id.button_id2);
        editText1.setText("韩韩");
        editText2.setText("111222");

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Map<String, String> map = new HashMap<>();
            map.put("username",editText1.getText().toString());
            map.put("password",  editText2.getText().toString());
                Log.d("xcz", "cccccccccccccccccccc" + LeApiApp.getLoginlUrl("" + editText1.getText().toString(), "" + editText2.getText().toString()));
            LeApiUtils.postString(LeApiApp.getLoginlUrl("" + editText1.getText().toString(), "" + editText2.getText().toString()), map, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {


                    LeApiResult<UserInfo> result = new LeApiResult<UserInfo>(jsonObject, new TypeToken<UserInfo>() {
                    });
                    if (result.getResult() != null) {
                        Toast.makeText(LoginActivity.this, "success", Toast.LENGTH_LONG).show();
                        Log.d("xcz", "" + result.toString());
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, MainActivityScreen.class);
                        startActivity(intent);


                    }

                    Log.d("xcz", "token" + AndroidUtils.getInstance().getPrefs("token", ""));

                    Log.d("xcz", "aaaaaaaaaaaaaaaaaaaaa" + jsonObject);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.d("xcz", "bbbbbbbbbbbbb" + volleyError);
                }
            }, this);

//                Intent intent = new Intent();
//                intent.setClass(LoginActivity.this,MainActivityScreen.class);
//                startActivity(intent);

            }
        });


//
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (NetUtils.isNetConnected()){
//                    //重复绑定会失败，用户可以清空APP数据 ，所以每次都进来登录
//                    LoginRjCallBack callBack = new LoginRjCallBack(LoginActivity.this);
//                   // callBack.setOnJumpListener((LoginRjCallBack.OnJumpListener) LoginActivity.this);
//                    ProtocolManager.loginProtocol(Commons.UUID, ProtocolId.PROTOCOL_ID_LOGIN, callBack);
//                }else{
//                    Toast.makeText(LoginActivity.this,"请检查网络",Toast.LENGTH_LONG).show();
//                }
//
//
//            }
//        });



    }
}
