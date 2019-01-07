package com.yougy.message.attachment;

import org.json.JSONException;
import org.json.JSONObject;

public class TaskRemindAttachment extends CustomAttachment{

    public static final String KEY_TASK_ID = "taskId";
    public static final String KEY_TASK_NAME = "taskName";

    public String taskId;
    public String taskName;

    public TaskRemindAttachment(String clue, double version) {
        super(clue, version);
    }

    public TaskRemindAttachment(String taskId,String taskName){
        super(CustomAttachParser.CLUE_TASK_REMIND,0.1);
        this.taskId = taskId;
        this.taskName = taskName;
    }

    @Override
    protected void parseData(JSONObject data) throws JSONException {
        JSONObject introJsonObj = data.getJSONObject(CustomAttachParser.KEY_INTRO);
        taskId = introJsonObj.getString(KEY_TASK_ID);
        taskName = introJsonObj.getString(KEY_TASK_NAME);
    }

    @Override
    protected JSONObject packData() {
        JSONObject returnJsonObj = new JSONObject();
        JSONObject introJsonObj = new JSONObject();
        try{
            introJsonObj.put(KEY_TASK_ID,taskId);
            introJsonObj.put(KEY_TASK_NAME,taskName);
            returnJsonObj.put(CustomAttachParser.KEY_INTRO,introJsonObj);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return returnJsonObj;
    }
}
