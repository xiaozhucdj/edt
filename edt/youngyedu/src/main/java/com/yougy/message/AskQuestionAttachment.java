package com.yougy.message;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by FH on 2017/4/18.
 */

public class AskQuestionAttachment extends CustomAttachment{
    final String KEY_EXAM_ID = "examId";
    final String KEY_ITEM_ID = "itemId";
    final String KEY_FROM = "userId";
    public int examID;
    public int itemId;
    public String from;

    public AskQuestionAttachment(String clue , double version) {
        super(clue , version);
    }

    /**
     * 解析数据
     * @param data
     */
    @Override
    protected void parseData(JSONObject data) throws JSONException{
        JSONObject abstractJsonObj = data.getJSONObject("abstract");
        JSONObject paramJsonObj = data.getJSONObject("param");
        examID = abstractJsonObj.getInt(KEY_EXAM_ID);
        itemId = abstractJsonObj.getInt(KEY_ITEM_ID);
        from = paramJsonObj.getString(KEY_FROM);
    }

    /**
     * 打包数据
     * @return
     */
    @Override
    protected JSONObject packData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_EXAM_ID , examID);
            jsonObject.put(KEY_ITEM_ID , itemId);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
