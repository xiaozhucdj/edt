package com.yougy.message.attachment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by FH on 2017/4/18.
 */

public class HomeworkRemindAttachment extends CustomAttachment{
    final String KEY_EXAM_ID = "examId";
    final String KEY_EXAM_NAME = "examName";

    public String examName;
    public String examId;

    public HomeworkRemindAttachment(String clue , double version) {
        super(clue , version);
    }

    public HomeworkRemindAttachment(String examName, String examId) {
        super(CustomAttachParser.CLUE_HOMEWORK_REMIND , 0.1);
        this.examName = examName;
        this.examId = examId;
    }

    /**
     * 解析数据
     * @param data
     */
    @Override
    protected void parseData(JSONObject data) throws JSONException{
        JSONObject introJsonObj = data.getJSONObject(CustomAttachParser.KEY_INTRO);
        examName = introJsonObj.getString(KEY_EXAM_NAME);
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
            introJsonObj.put(KEY_EXAM_NAME , examName);
            introJsonObj.put(KEY_EXAM_ID , examId);
            returnJsonObj.put(CustomAttachParser.KEY_INTRO , introJsonObj);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJsonObj;
    }
}
