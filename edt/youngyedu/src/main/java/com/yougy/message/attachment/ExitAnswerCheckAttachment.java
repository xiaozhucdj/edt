package com.yougy.message.attachment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by FH on 2019/2/18.
 */

public class ExitAnswerCheckAttachment extends CustomAttachment{
    public ExitAnswerCheckAttachment(String clue , double version) {
        super(clue , version);
    }

    public ExitAnswerCheckAttachment() {
        super(CustomAttachParser.CLUE_EXIT_ANSWER_CHECK , 0.1);
    }

    /**
     * 解析数据
     * @param data
     */
    @Override
    protected void parseData(JSONObject data) throws JSONException{
        JSONObject introJsonObj = data.getJSONObject(CustomAttachParser.KEY_INTRO);
        //以后如果有要传入的参数,放在introJsonObj里
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
            returnJsonObj.put(CustomAttachParser.KEY_INTRO , introJsonObj);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJsonObj;
    }
}
