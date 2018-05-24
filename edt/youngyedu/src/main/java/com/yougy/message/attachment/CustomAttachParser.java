package com.yougy.message.attachment;

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

    final static String CLUE_PROMOTE_BOOK = "promoteBook";
    final static String CLUE_ASK_QUESTION = "askQuestion";
    final static String CLUE_END_QUESTION = "endQuestion";
    final static String CLUE_WENDA_ADD = "wendaAdd";
    final static String CLUE_OVERALLLOCK = "overallLock";
    final static String CLUE_OVERALLUNLOCK = "overallUnlock";
    final static String CLUE_RETRY_ASK_QUESTION = "retryAskQuestion";
    final static String CLUE_SEND_REPLY = "sendReply";

    // 根据解析到的消息类型，确定附件对象类型
    @Override
    public MsgAttachment parse(String json) {
        LogUtils.e("FH", "解析自定义消息 : " + json);
        CustomAttachment attachment = null;
        try {
            JSONObject object = new JSONObject(json).getJSONObject(KEY_CONTENT);
            String clue = object.getString(KEY_CLUE);
            double version = object.getDouble(KEY_VERSION);
            switch (clue){
                case CLUE_PROMOTE_BOOK:
                    attachment = new BookRecommandAttachment(clue , version);
                    break;
                case CLUE_ASK_QUESTION:
                    attachment = new AskQuestionAttachment(clue , version);
                    break;
                case CLUE_END_QUESTION:
                    attachment = new EndQuestionAttachment(clue , version);
                    break;
                case CLUE_WENDA_ADD:
                    attachment = new WendaQuestionAddAttachment(clue , version);
                    break;
                case CLUE_OVERALLLOCK:
                    attachment = new OverallLockAttachment(clue , version);
                    break;
                case CLUE_OVERALLUNLOCK:
                    attachment = new OverallUnlockAttachment(clue , version);
                    break;
                case CLUE_RETRY_ASK_QUESTION:
                    attachment = new RetryAskQuestionAttachment(clue , version);
                    break;
            }
            if (attachment != null) {
                attachment.fromJson(object);
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