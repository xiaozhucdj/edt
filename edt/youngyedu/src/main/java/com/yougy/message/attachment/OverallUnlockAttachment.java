package com.yougy.message.attachment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by FH on 2018/5/9.
 */

public class OverallUnlockAttachment extends CustomAttachment{
    final String KEY_TIME = "time";

    public String time;

    public OverallUnlockAttachment(String clue , double version) {
        super(clue , version);
    }
    public OverallUnlockAttachment(String time) {
        super(CustomAttachParser.CLUE_OVERALLUNLOCK, 1);
        this.time = time;
    }
    /**
     * 解析数据
     * @param data
     */
    @Override
    protected void parseData(JSONObject data) throws JSONException{
        JSONObject introJsonObj = data.getJSONObject(CustomAttachParser.KEY_INTRO);
        time = introJsonObj.getString(KEY_TIME);
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
            introJsonObj.put(KEY_TIME , time);
            returnJsonObj.put(CustomAttachParser.KEY_INTRO , introJsonObj);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJsonObj;
    }
}
