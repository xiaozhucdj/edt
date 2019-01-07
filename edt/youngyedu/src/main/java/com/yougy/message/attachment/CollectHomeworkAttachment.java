package com.yougy.message.attachment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author: zhang yc
 * @create date: 2018/7/12 15:10 收作业
 * @class desc:
 * @modifier:
 * @modify date: 2018/7/12 15:10
 * @modify desc:
 */
public class CollectHomeworkAttachment extends CustomAttachment {
    public String examId, examName;
    final String KEY_EXAM_ID = "examID";
    final String KEY_EXAM_NAME = "examName";

    protected CollectHomeworkAttachment(String clue, double version) {
        super(clue, version);
    }

    public CollectHomeworkAttachment(String examId, String examName) {
        super(CustomAttachParser.CLUE_END_SEATWORK , 1);
        this.examId = examId;
        this.examName = examName;
    }

    @Override
    protected void parseData(JSONObject data) throws JSONException {
        JSONObject introJsonObj = data.getJSONObject(CustomAttachParser.KEY_INTRO);
        examId = introJsonObj.getString(KEY_EXAM_ID);
        examName = introJsonObj.getString(KEY_EXAM_NAME);
    }

    @Override
    protected JSONObject packData() {
        JSONObject returnJsonObj = new JSONObject();
        JSONObject introJsonObj = new JSONObject();
        try {
            introJsonObj.put(KEY_EXAM_ID , examId);
            introJsonObj.put(KEY_EXAM_NAME , examName);
            returnJsonObj.put(CustomAttachParser.KEY_INTRO , introJsonObj);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJsonObj;
    }
}
