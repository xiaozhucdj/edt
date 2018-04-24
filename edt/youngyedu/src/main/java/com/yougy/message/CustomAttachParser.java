package com.yougy.message;

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;
import com.yougy.common.utils.LogUtils;

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
    static String KEY_INTRO = "intro";
    static String KEY_VERSION = "version";
    static String KEY_CLUE = "clue";
    static String KEY_CONTENT = "content";

    // 根据解析到的消息类型，确定附件对象类型
    @Override
    public MsgAttachment parse(String json) {
        LogUtils.e("FH", "解析自定义消息 : " + json);
        CustomAttachment attachment = null;
        try {
            JSONObject object = new JSONObject(json).getJSONObject(KEY_CONTENT);
            String clue = object.getString(KEY_CLUE);
            double version = object.getDouble(KEY_VERSION);
            JSONObject data = null;
            switch (clue){
                case "promoteBook" :
                    attachment = new BookRecommandAttachment(clue , version);
                    data =  object;
                    break;
                case "askQuestion" :
                    attachment = new AskQuestionAttachment(clue , version);
                    data = object;
                    break;
                case "endQuestion" :
                    attachment = new EndQuestionAttachment(clue , version);
                    data = object;
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
        JSONObject returnObj = new JSONObject();
        try {
            data.put(KEY_CLUE, clue);
            data.put(KEY_VERSION , version);
            returnObj.put(KEY_CONTENT , data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnObj.toString();
    }
}