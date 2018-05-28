package com.yougy.message.attachment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by FH on 2017/4/18.
 */

public class EndQuestionAttachment extends CustomAttachment{
    final String KEY_EXAM_ID = "examId";
    public int examID;

    public EndQuestionAttachment(String clue , double version) {
        super(clue , version);
    }

    /**
     * 解析数据
     * @param data
     */
    @Override
    protected void parseData(JSONObject data) throws JSONException{
//        JSONObject introJsonObj = data.getJSONObject(CustomAttachParser.KEY_INTRO);
        JSONObject paramJsonObj = data.getJSONObject(CustomAttachParser.KEY_PARAM);
        examID = paramJsonObj.getInt(KEY_EXAM_ID);
    }

    /**
     * 打包数据
     * @return
     */
    @Override
    protected JSONObject packData() {
        JSONObject returnJsonObj = new JSONObject();
        JSONObject paramJsonObj = new JSONObject();
        JSONObject introJsonObj = new JSONObject();
        try {
            paramJsonObj.put(KEY_EXAM_ID , examID);
            returnJsonObj.put(CustomAttachParser.KEY_INTRO , introJsonObj);
            returnJsonObj.put(CustomAttachParser.KEY_PARAM , paramJsonObj);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJsonObj;
    }
}
