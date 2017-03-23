package com.inkscreen.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xcz 2016/11/29
 */
public class LeApiResult<T> {
    private T result;
    private int code = -1;

    public LeApiResult() {
        super();
        // TODO Auto-generated constructor stub
    }
  /*  public ZLApiResult(JSONObject jsonObject,TypeToken<T> typeToken) {
        this(jsonObject,1,typeToken);
    }*/

    public LeApiResult(JSONObject jsonObject, TypeToken<T> typeToken) {
        super();
        try {
            code = jsonObject.getInt("ret_code");
            if (code == 0) {
                if (!jsonObject.isNull("ret_token")) {
                    String token = jsonObject.getString("ret_token");
                    AndroidUtils.getInstance().putPrefs("token", token);
                    Log.i("xcz", "token:" + token);
                } else if (!jsonObject.isNull("ret")) {

                    if (!jsonObject.getJSONObject("ret").isNull("token")) {
                        String token = jsonObject.getJSONObject("ret").getString("token");
                        AndroidUtils.getInstance().putPrefs("token", token);

                    }

                }
            }
            Gson gson = new Gson();
            result = gson.fromJson(jsonObject.toString(), typeToken.getType());

        } catch (Exception e) {
            e.printStackTrace();
            Gson gson = new Gson();
            result = gson.fromJson(jsonObject.toString(), typeToken.getType());
        }
    }


    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public static void main(String[] args) throws JSONException {

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
