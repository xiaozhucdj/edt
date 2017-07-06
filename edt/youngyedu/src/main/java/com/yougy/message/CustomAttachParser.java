package com.yougy.message;

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

    static String KEY_DATA = "data";
    static String KEY_TYPE = "type";

    enum CustomAttachmentType {
        RECOMMAND_BOOK
    }

    // 根据解析到的消息类型，确定附件对象类型
    @Override
    public MsgAttachment parse(String json) {
        CustomAttachment attachment = null;
        try {
            JSONObject object = new JSONObject(json);
            int type = object.getInt("type");
            JSONObject data = object.getJSONObject(KEY_DATA);
            if (type == CustomAttachmentType.RECOMMAND_BOOK.ordinal()){
                attachment = new BookRecommandAttachment();
            }
            if (attachment != null) {
                attachment.fromJson(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return attachment;
    }

    public static String packData(int type, JSONObject data) {
        JSONObject object = new JSONObject();
        try {
            object.put(KEY_TYPE, type);
            if (data != null) {
                object.put(KEY_DATA, data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}