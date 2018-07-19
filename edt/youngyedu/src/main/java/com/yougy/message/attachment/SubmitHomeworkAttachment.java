package com.yougy.message.attachment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author: zhang yc
 * @create date: 2018/7/17 16:06
 * @class desc: 学生提交作业  发送消息给教师  刷新作业结果列表
 * @modifier:
 * @modify date: 2018/7/17 16:06
 * @modify desc:
 */
public class SubmitHomeworkAttachment extends CustomAttachment{

    public int studentId;
    public String studentName;
    public int examId;

    private final String KEY_STUDENT_ID = "student_id";
    private final String KEY_STUDENT_NAME = "student_name";
    private final String KEY_EXAM_ID = "key_exam_id";

    public SubmitHomeworkAttachment(String clue , double version) {
        super(clue , version);
    }
    public SubmitHomeworkAttachment(int studentId , String studentName, int examId) {
        super(CustomAttachParser.CLUE_SUBMITE_HOMEWORK , 1);
        this.studentId = studentId;
        this.studentName = studentName;
        this.examId = examId;
    }
    @Override
    protected void parseData(JSONObject data) throws JSONException {
        JSONObject introJsonObj = data.getJSONObject(CustomAttachParser.KEY_INTRO);
        studentId = introJsonObj.getInt(KEY_STUDENT_ID);
        studentName = introJsonObj.getString(KEY_STUDENT_NAME);
        examId = introJsonObj.getInt(KEY_EXAM_ID);
    }

    @Override
    protected JSONObject packData() {
        JSONObject returnJsonObj = new JSONObject();
        JSONObject introJsonObj = new JSONObject();
        try {
            introJsonObj.put(KEY_STUDENT_ID , studentId);
            introJsonObj.put(KEY_STUDENT_NAME , studentName);
            introJsonObj.put(KEY_EXAM_ID , examId);
            returnJsonObj.put(CustomAttachParser.KEY_INTRO , introJsonObj);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJsonObj;
    }
}
