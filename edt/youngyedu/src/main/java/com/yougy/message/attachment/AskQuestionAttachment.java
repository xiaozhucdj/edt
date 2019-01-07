package com.yougy.message.attachment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by FH on 2017/4/18.
 */

public class AskQuestionAttachment extends CustomAttachment{
    final String KEY_EXAM_ID = "examId";
    final String KEY_ITEM_ID = "itemId";
    final String KEY_FROM = "examSponsor";
    final String KEY_QUESTION = "question";
    final String KEY_QUESTION_TYPE = "questionType";

    public int examID;
    public int itemId;
    public String from;
    public String questionType;

    public AskQuestionAttachment(String clue , double version) {
        super(clue , version);
    }

    /**
     * 解析数据
     * @param data
     */
    @Override
    protected void parseData(JSONObject data) throws JSONException{
        JSONObject introJsonObj = data.getJSONObject(CustomAttachParser.KEY_INTRO);
        examID = introJsonObj.getInt(KEY_EXAM_ID);
        itemId = introJsonObj.getInt(KEY_ITEM_ID);
        from = introJsonObj.getString(KEY_FROM);
        questionType = introJsonObj.getJSONArray(KEY_QUESTION).getJSONObject(0).getString(KEY_QUESTION_TYPE);
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
            introJsonObj.put(KEY_EXAM_ID , examID);
            introJsonObj.put(KEY_ITEM_ID , itemId);
            introJsonObj.put(KEY_FROM , from);

            JSONObject tempObj = new JSONObject();
            tempObj.put(KEY_QUESTION_TYPE , questionType);
            JSONArray tempArray = new JSONArray();
            tempArray.put(tempObj);
            introJsonObj.put(KEY_QUESTION , tempArray);

            returnJsonObj.put(CustomAttachParser.KEY_INTRO , introJsonObj);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJsonObj;
    }

    @Override
    public String toString() {
        return "AskQuestionAttachment{" +
                "examID=" + examID +
                ", itemId=" + itemId +
                ", from='" + from + '\'' +
                ", questionType='" + questionType + '\'' +
                '}';
    }
}
