package com.yougy.message.attachment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/7/6.
 */

public class SeatWorkAttachment extends CustomAttachment {
    public String examId , examName;
    public boolean isTimeWork;

    final String KEY_EXAM_ID = "examID";
    final String KEY_EXAM_NAME = "examName";
    final String KEY_IS_TIME_LIMITED_WORK = "isTimeLimitedWork";

    public SeatWorkAttachment(String clue , double version) {
        super(clue , version);
    }
    public SeatWorkAttachment(String examId , String examName , boolean isTimeWork) {
        super(CustomAttachParser.CLUE_SEND_SEATWORK , 1);
        this.examId = examId;
        this.examName = examName;
        this.isTimeWork = isTimeWork;
    }

    /**
     * 解析数据
     * @param data
     */
    @Override
    protected void parseData(JSONObject data) throws JSONException {
        JSONObject introJsonObj = data.getJSONObject(CustomAttachParser.KEY_INTRO);
        examId = introJsonObj.getString(KEY_EXAM_ID);
        examName = introJsonObj.getString(KEY_EXAM_NAME);
        isTimeWork = introJsonObj.getBoolean(KEY_IS_TIME_LIMITED_WORK);
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
            introJsonObj.put(KEY_EXAM_NAME , examName);
            introJsonObj.put(KEY_IS_TIME_LIMITED_WORK , isTimeWork);
            returnJsonObj.put(CustomAttachParser.KEY_INTRO , introJsonObj);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJsonObj;
    }
}
