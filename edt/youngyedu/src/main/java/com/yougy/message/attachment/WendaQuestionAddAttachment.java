package com.yougy.message.attachment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by FH on 2018/5/9.
 */

public class WendaQuestionAddAttachment extends CustomAttachment {
    final String KEY_BOOK_ID = "bookId";
    final String KEY_CURSOR_ID = "cursorId";
    final String KEY_ITEM_ID = "itemId";

    public String bookId;
    public String cursorId;
    public String itemId;


    public WendaQuestionAddAttachment(String clue , double version) {
        super(clue , version);
    }

    public WendaQuestionAddAttachment(String bookId , String cursorId , String itemId) {
        super(CustomAttachParser.CLUE_WENDA_ADD , 1);
        this.bookId = bookId;
        this.cursorId = cursorId;
        this.itemId = itemId;
    }

    /**
     * 解析数据
     * @param data
     */
    @Override
    protected void parseData(JSONObject data) throws JSONException{
        JSONObject paramJsonObj = data.getJSONObject(CustomAttachParser.KEY_PARAM);
        bookId = paramJsonObj.getString(KEY_BOOK_ID);
        cursorId = paramJsonObj.getString(KEY_CURSOR_ID);
        itemId = paramJsonObj.getString(KEY_ITEM_ID);
    }

    /**
     * 打包数据
     * @return
     */
    @Override
    protected JSONObject packData() {
        JSONObject returnJsonObj = new JSONObject();
        JSONObject paramJsonObj = new JSONObject();
        try {
            paramJsonObj.put(KEY_BOOK_ID , bookId);
            paramJsonObj.put(KEY_CURSOR_ID , cursorId);
            paramJsonObj.put(KEY_ITEM_ID , itemId);

            returnJsonObj.put(CustomAttachParser.KEY_PARAM , paramJsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJsonObj;
    }
}
