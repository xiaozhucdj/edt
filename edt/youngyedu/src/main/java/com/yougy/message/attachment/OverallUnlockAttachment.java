package com.yougy.message.attachment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by FH on 2018/5/9.
 */

public class OverallUnlockAttachment extends CustomAttachment{
    public OverallUnlockAttachment(String clue , double version) {
        super(clue , version);
    }

    /**
     * 解析数据
     * @param data
     */
    @Override
    protected void parseData(JSONObject data) throws JSONException{
    }

    /**
     * 打包数据
     * @return
     */
    @Override
    protected JSONObject packData() {
        JSONObject returnJsonObj = new JSONObject();
        return returnJsonObj;
    }
}
