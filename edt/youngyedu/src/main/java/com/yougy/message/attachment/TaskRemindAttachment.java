package com.yougy.message.attachment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class TaskRemindAttachment extends CustomAttachment{

    public static final String KEY_TASK_ID = "taskId";
    public static final String KEY_TASK_NAME = "taskName";
    public static final String IS_SIGN = "isSign";
    public static final String SCENE_STATUS_CODE = "sceneStatusCode";
    public static final String KEY_DRAMA_ID = "dramaId";

    public int taskId;
    public int dramaId;
    public String taskName;
    public boolean isSign;
    public String sceneStatusCode;

    public TaskRemindAttachment(String clue, double version) {
        super(clue, version);
    }

    public TaskRemindAttachment(int taskId,int dramaId,String taskName,boolean isSign,String sceneStatusCode){
        super(CustomAttachParser.CLUE_TASK_REMIND,0.1);
        this.taskId = taskId;
        this.dramaId = dramaId;
        this.taskName = taskName;
        this.isSign = isSign;
        this.sceneStatusCode = sceneStatusCode;
    }

    @Override
    protected void parseData(JSONObject data) throws JSONException {
        JSONObject introJsonObj = data.getJSONObject(CustomAttachParser.KEY_INTRO);
        taskId = introJsonObj.getInt(KEY_TASK_ID);
        dramaId = introJsonObj.getInt(KEY_DRAMA_ID);
        taskName = introJsonObj.getString(KEY_TASK_NAME);
        try {
            isSign = introJsonObj.getBoolean(IS_SIGN);
            sceneStatusCode = introJsonObj.getString(SCENE_STATUS_CODE);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject packData() {
        JSONObject returnJsonObj = new JSONObject();
        JSONObject introJsonObj = new JSONObject();
        try{
            introJsonObj.put(KEY_TASK_ID,taskId);
            introJsonObj.put(KEY_DRAMA_ID,dramaId);
            introJsonObj.put(KEY_TASK_NAME,taskName);
            introJsonObj.put(IS_SIGN,isSign);
            introJsonObj.put(SCENE_STATUS_CODE,sceneStatusCode);
            returnJsonObj.put(CustomAttachParser.KEY_INTRO,introJsonObj);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return returnJsonObj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskRemindAttachment that = (TaskRemindAttachment) o;
        return taskId == that.taskId &&
                dramaId == that.dramaId &&
                isSign == that.isSign &&
                Objects.equals(taskName, that.taskName) &&
                Objects.equals(sceneStatusCode, that.sceneStatusCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, dramaId, taskName, isSign, sceneStatusCode);
    }
}
