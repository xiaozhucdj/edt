package com.yougy.message;

import android.util.Log;

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by FH on 2017/4/18.
 */

/**
 * 自定义消息解析器
 */
public class CustomAttachParser implements MsgAttachmentParser {
    static String KEY_METHOD = "method";
    static String KEY_PARAM = "param";
    static String KEY_ABSTRACT = "abstract";
    static String KEY_VERSION = "version";
    static String KEY_CLUE = "clue";

    // 根据解析到的消息类型，确定附件对象类型
    @Override
    public MsgAttachment parse(String json) {
        Log.v("FH" , "解析自定义消息 : " + json);
        CustomAttachment attachment = null;
        try {
            JSONObject object = new JSONObject(json);
            String clue = object.getString(KEY_CLUE);
            double version = object.getDouble(KEY_VERSION);
            JSONObject data = null;
            switch (clue){
                case "promoteBook" :
                    attachment = new BookRecommandAttachment(clue , version);
                    data =  object.getJSONObject(KEY_ABSTRACT);
                    break;
                case "askQuestion" :
                    attachment = new AskQuestionAttachment(clue , version);
                    data = object;
                    break;
            }
            if (attachment != null) {
                attachment.fromJson(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return attachment;
    }

    public static String packData(String clue , double version, JSONObject data) {
        JSONObject object = new JSONObject();
        try {
            object.put(KEY_CLUE, clue);
            object.put(KEY_VERSION , version);
            if (data != null) {
                object.put(KEY_ABSTRACT, data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}