package com.yougy.message.attachment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by FH on 2018/5/9.
 */

public class WendaQuestionAddAttachment extends CustomAttachment {
    final String KEY_BOOK_ID = "bookId";
    final String KEY_CURSOR = "cursor";
    final String KEY_ITEM_IDS = "itemIds";

    public String bookId;
    public String cursorId;
    public ArrayList<String> itemIdList = new ArrayList<String>();


    public WendaQuestionAddAttachment(String clue , double version) {
        super(clue , version);
    }

    public WendaQuestionAddAttachment(String bookId, String cursorId, ArrayList<String> itemIdList) {
        super(CustomAttachParser.CLUE_NOTIFY_PAD_FOR_INTERLOCUTION, 0.1);
        this.bookId = bookId;
        this.cursorId = cursorId;
        this.itemIdList = itemIdList;
    }

    /**
     * 解析数据
     * @param data
     */
    @Override
    protected void parseData(JSONObject data) throws JSONException{
        JSONObject introJsonObj = data.getJSONObject(CustomAttachParser.KEY_INTRO);
        bookId = introJsonObj.getString(KEY_BOOK_ID);
        cursorId = introJsonObj.getString(KEY_CURSOR);
        JSONArray itemIdJsonArray = introJsonObj.getJSONArray(KEY_ITEM_IDS);
        for (int i = 0; i < itemIdJsonArray.length(); i++) {
            itemIdList.add((String) itemIdJsonArray.get(0));
        }
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
            introJsonObj.put(KEY_BOOK_ID , bookId);
            introJsonObj.put(KEY_CURSOR, cursorId);
            JSONArray itemIdListJsonArray = new JSONArray(itemIdList);
            introJsonObj.put(KEY_ITEM_IDS, itemIdListJsonArray);
            returnJsonObj.put(CustomAttachParser.KEY_INTRO , introJsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnJsonObj;
    }
}
