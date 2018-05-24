package com.yougy.message.attachment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by FH on 2017/4/18.
 */

public class RetryAskQuestionAttachment extends CustomAttachment{
    final String KEY_EXAM_ID = "examId";
    final String KEY_ITEM_ID = "itemId";
    final String KEY_QUESTION_TYPE = "questionType";
    final String KEY_USER_ID = "userId";

    public int examId;
    public int itemId;
    public int userId;
    public String questionType;

    public RetryAskQuestionAttachment(String clue , double version) {
        super(clue , version);
    }

    public RetryAskQuestionAttachment(int examId, int itemId, int userId, String questionType) {
        super(CustomAttachParser.CLUE_RETRY_ASK_QUESTION , 0.1);
        this.examId = examId;
        this.itemId = itemId;
        this.userId = userId;
        this.questionType = questionType;
    }

    /**
     * 解析数据
     * @param data
     */
    @Override
    protected void parseData(JSONObject data) throws JSONException{
        JSONObject introJsonObj = data.getJSONObject(CustomAttachParser.KEY_INTRO);
        examId = introJsonObj.getInt(KEY_EXAM_ID);
        itemId = introJsonObj.getInt(KEY_ITEM_ID);
        userId = introJsonObj.getInt(KEY_USER_ID);
        questionType = introJsonObj.getString(KEY_QUESTION_TYPE);
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
            introJsonObj.put(KEY_EXAM_ID , examId);
            introJsonObj.put(KEY_ITEM_ID , itemId);
            introJsonObj.put(KEY_USER_ID , KEY_USER_ID);
            introJsonObj.put(KEY_QUESTION_TYPE , questionType);

            returnJsonObj.put(CustomAttachParser.KEY_INTRO , introJsonObj);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJsonObj;
    }
}
