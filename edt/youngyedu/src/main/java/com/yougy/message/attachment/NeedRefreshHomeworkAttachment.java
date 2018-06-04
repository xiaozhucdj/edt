package com.yougy.message.attachment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by FH on 2017/4/18.
 */

public class NeedRefreshHomeworkAttachment extends CustomAttachment{
    final String KEY_EXAM_ID = "examId";

    public String examId;

    public NeedRefreshHomeworkAttachment(String clue , double version) {
        super(clue , version);
    }

    public NeedRefreshHomeworkAttachment(String examId) {
        super(CustomAttachParser.CLUE_NEED_REFRESH_HOMEWORK , 0.1);
        this.examId = examId;
    }

    /**
     * 解析数据
     * @param data
     */
    @Override
    protected void parseData(JSONObject data) throws JSONException{
        JSONObject introJsonObj = data.getJSONObject(CustomAttachParser.KEY_INTRO);
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
            introJsonObj.put(KEY_EXAM_ID , examId);
            returnJsonObj.put(CustomAttachParser.KEY_INTRO , introJsonObj);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJsonObj;
    }
}
