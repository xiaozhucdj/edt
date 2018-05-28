package com.yougy.message.attachment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by FH on 2017/4/18.
 */

public class BookRecommandAttachment extends CustomAttachment{
    final String KEY_COMMENT = "comment";
    final String KEY_TITLE = "title";
    final String KEY_ID = "id";

    public String bookId;
    public String bookName;
    public String recommand_msg;

    public BookRecommandAttachment(String clue , double version) {
        super(clue , version);
    }

    /**
     * 解析数据
     * @param data
     */
    @Override
    protected void parseData(JSONObject data) throws JSONException{
        JSONObject introJsonObj = data.getJSONObject(CustomAttachParser.KEY_INTRO);
        JSONObject paramJsonObj = data.getJSONObject(CustomAttachParser.KEY_PARAM);
        recommand_msg = introJsonObj.getString(KEY_COMMENT);
        bookId = introJsonObj.getString(KEY_ID);
        bookName = introJsonObj.getString(KEY_TITLE);
    }

    /**
     * 打包数据
     * @return
     */
    @Override
    protected JSONObject packData() {
        JSONObject returnJsonObj = new JSONObject();
        JSONObject paramJsonObj = new JSONObject();
        JSONObject introJsonObj = new JSONObject();
        try {
            introJsonObj.put(KEY_ID , bookId);
            introJsonObj.put(KEY_TITLE , bookName);
            introJsonObj.put(KEY_COMMENT, recommand_msg);

            returnJsonObj.put(CustomAttachParser.KEY_INTRO , introJsonObj);
            returnJsonObj.put(CustomAttachParser.KEY_PARAM , paramJsonObj);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJsonObj;
    }
}
