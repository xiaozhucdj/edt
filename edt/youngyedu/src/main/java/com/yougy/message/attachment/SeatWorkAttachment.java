package com.yougy.message.attachment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/7/6.
 */

public class SeatWorkAttachment extends CustomAttachment {
    public String examId , examName;
    public boolean isTimeWork;
    public String lifeTime;
    public int teacherId;
    public boolean isStudentCheck;//是否自评
    public String sendDate;//发送日期

    final String KEY_EXAM_ID = "examID";
    final String KEY_EXAM_NAME = "examName";
    final String KEY_IS_TIME_LIMITED_WORK = "isTimeLimitedWork";
    final String KEY_IS_TIME_LIFETIEM = "isLifeTime";
    final String KEY_TEACHERID = "teacherId";
    final String KEY_IS_STUDENTCHECK = "isStudentCheck";
    final String KEY_SEND_DATE = "sendDate";

    public SeatWorkAttachment(String clue , double version) {
        super(clue , version);
    }
    public SeatWorkAttachment(String examId , String examName , boolean isTimeWork, String lifeTime, int teacherId
            , boolean isStudentCheck, String sendDate) {
        super(CustomAttachParser.CLUE_SEND_SEATWORK , 1);
        this.examId = examId;
        this.examName = examName;
        this.isTimeWork = isTimeWork;
        this.lifeTime = lifeTime;
        this.teacherId = teacherId;
        this.isStudentCheck = isStudentCheck;
        this.sendDate = sendDate;
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
        lifeTime = introJsonObj.getString(KEY_IS_TIME_LIFETIEM);
        teacherId = introJsonObj.getInt(KEY_TEACHERID);
        isStudentCheck = introJsonObj.getBoolean(KEY_IS_STUDENTCHECK);
        sendDate = introJsonObj.getString(KEY_SEND_DATE);
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
            introJsonObj.put(KEY_IS_TIME_LIFETIEM , lifeTime);
            introJsonObj.put(KEY_TEACHERID , teacherId);
            introJsonObj.put(KEY_IS_STUDENTCHECK,isStudentCheck);
            returnJsonObj.put(CustomAttachParser.KEY_INTRO , introJsonObj);
            introJsonObj.put(KEY_SEND_DATE , sendDate);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJsonObj;
    }
}
