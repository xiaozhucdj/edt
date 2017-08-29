package com.yougy.message;

import com.google.gson.Gson;
import com.yougy.shop.bean.BookInfo;

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
        recommand_msg = data.getString(KEY_COMMENT);
        bookId = data.getString(KEY_ID);
        bookName = data.getString(KEY_TITLE);
    }

    /**
     * 打包数据
     * @return
     */
    @Override
    protected JSONObject packData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_ID , bookId);
            jsonObject.put(KEY_TITLE , bookName);
            jsonObject.put(KEY_COMMENT, recommand_msg);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
