package com.yougy.message.attachment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author: zhang yc
 * @create date: 2018/7/2 11:22  课堂作业
 * @class desc:
 * @modifier:
 * @modify date: 2018/7/2 11:22
 * @modify desc:
 */
public class SeatWorkAttachment extends CustomAttachment{
    final String KEY_EXAM_ID = "examId";
    final String KEY_EXAM_NAME = "examName";
    final String KEY_ISTIMER_WORK = "isTimerWork";

    public String examName;
    public String examId;
    public boolean isTimerWork;

    public SeatWorkAttachment(String clue , double version) {
        super(clue , version);
    }

    public SeatWorkAttachment(String examName, String examId, String mHomewrokId, boolean isTimerWork) {
        super(CustomAttachParser.CLUE_SEAT_HOMEWORK , 0.1);
        this.examName = examName;
        this.examId = examId;
        this.isTimerWork = isTimerWork;
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
        isTimerWork = introJsonObj.getBoolean(KEY_ISTIMER_WORK);
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
            introJsonObj.put(KEY_ISTIMER_WORK, isTimerWork);
            returnJsonObj.put(CustomAttachParser.KEY_INTRO , introJsonObj);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJsonObj;
    }
}
