package com.yougy.message;

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by FH on 2017/4/18.
 */

/**
 * 自定义消息附件的基类
 */
public abstract class CustomAttachment implements MsgAttachment {

    // 自定义消息附件的类型，根据该字段区分不同的自定义消息
    protected String clue;

    protected double version;


    CustomAttachment(String clue , double version) {
        this.clue = clue;
        this.version = version;
    }

    // 解析附件内容。
    public void fromJson(JSONObject data) throws JSONException{
        if (data != null) {
            parseData(data);
        }
    }

    // 实现 MsgAttachment 的接口，封装公用字段，然后调用子类的封装函数。
    @Override
    public String toJson(boolean send) {
        return CustomAttachParser.packData(clue , version , packData());
    }

    // 子类的解析和封装接口。
    protected abstract void parseData(JSONObject data) throws JSONException;
    protected abstract JSONObject packData();
}
