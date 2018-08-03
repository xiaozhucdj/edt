package com.yougy.message.attachment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by FH on 2017/4/18.
 */

public class HomeworkRemindAttachment extends CustomAttachment{
    final String KEY_EXAM_ID = "examId";
    final String KEY_EXAM_NAME = "examName";
    final String KEY_EXAM_TIMEWORK = "isTimerWork";
    final String KEY_EXAM_LIFTTIME = "lifetime";
    final String KEY_EXAM_TEACHERID = "teacherId";
    final String KEY_EXAM_TYPECODE = "typeCode";
    final String KEY_IS_STUDENTCHECK = "isStudentCheck";

    public String examName;
    public String examId;
    public boolean isTimeWork;
    public String lifeTime;
    public int teacherId;
    public String examOccasion;
    public boolean isStudentCheck;//是否自评

    public HomeworkRemindAttachment(String clue , double version) {
        super(clue , version);
    }

    public HomeworkRemindAttachment(String examName, String examId, boolean isTimeWork, String lifeTime, int teacherId
            ,String typeCode, boolean isStudentCheck) {
        super(CustomAttachParser.CLUE_HOMEWORK_REMIND , 0.1);
        this.examName = examName;
        this.examId = examId;
        this.isTimeWork = isTimeWork;
        this.lifeTime = lifeTime;
        this.teacherId = teacherId;
        this.examOccasion = typeCode;
        this.isStudentCheck = isStudentCheck;
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
        isTimeWork = introJsonObj.getBoolean(KEY_EXAM_TIMEWORK);
        lifeTime = introJsonObj.getString(KEY_EXAM_LIFTTIME);
        teacherId = introJsonObj.getInt(KEY_EXAM_TEACHERID);
        examOccasion = introJsonObj.getString(KEY_EXAM_TYPECODE);
        isStudentCheck = introJsonObj.getBoolean(KEY_IS_STUDENTCHECK);
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
            introJsonObj.put(KEY_EXAM_TIMEWORK, isTimeWork);
            introJsonObj.put(KEY_EXAM_LIFTTIME, lifeTime);
            introJsonObj.put(KEY_EXAM_TEACHERID, teacherId);
            introJsonObj.put(KEY_EXAM_TYPECODE, examOccasion);
            introJsonObj.put(KEY_IS_STUDENTCHECK,isStudentCheck);
            returnJsonObj.put(CustomAttachParser.KEY_INTRO , introJsonObj);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJsonObj;
    }
}
