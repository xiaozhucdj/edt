package com.yougy.message.attachment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by FH on 2017/4/18.
 */

public class ReplyAttachment extends CustomAttachment{
    final String KEY_REPLY_ID = "replyId";
    final String KEY_EXAM_ID = "examId";

    String replyId;
    String examId;

    public ReplyAttachment(String clue , double version) {
        super(clue , version);
    }

    public ReplyAttachment(String replyId, String examId) {
        super(CustomAttachParser.CLUE_SEND_REPLY , 0.1);
        this.replyId = replyId;
        this.examId = examId;
    }

    /**
     * 解析数据
     * @param data
     */
    @Override
    protected void parseData(JSONObject data) throws JSONException{
        JSONObject introJsonObj = data.getJSONObject(CustomAttachParser.KEY_INTRO);
        replyId = introJsonObj.getString(KEY_REPLY_ID);
        examId = introJsonObj.getString(KEY_EXAM_ID);
    }

    /**
     * 打包数据
     * @return
     */
    @Override
    protected JSONObject packData() {
        JSONObject returnJsonObj = new JSONObject();
        JSONObject introJsonObj = new JSONObject();
        try {
            introJsonObj.put(KEY_REPLY_ID , replyId);
            introJsonObj.put(KEY_EXAM_ID , examId);
            returnJsonObj.put(CustomAttachParser.KEY_INTRO , introJsonObj);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJsonObj;
    }
}
