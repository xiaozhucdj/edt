package com.yougy.message;

import com.google.gson.Gson;
import com.yougy.shop.bean.BookInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by FH on 2017/4/18.
 */

public class BookRecommandAttachment extends CustomAttachment{
    final String KEY_RECOMMAND_MSG = "recommand_msg";
    final String KEY_BOOK_INFO = "bookinfo";

    public String recommand_msg;
    public BookInfo bookInfo;
    public BookRecommandAttachment() {
        super(CustomAttachParser.CustomAttachmentType.RECOMMAND_BOOK.ordinal());
    }

    public BookRecommandAttachment(String recommand_msg, BookInfo bookInfo) {
        this();
        this.recommand_msg = recommand_msg;
        this.bookInfo = bookInfo;
    }

    /**
     * 解析数据
     * @param data
     */
    @Override
    protected void parseData(JSONObject data) {
        try {
            recommand_msg = data.getString(KEY_RECOMMAND_MSG);
            bookInfo = new Gson().fromJson(data.getJSONObject(KEY_BOOK_INFO).toString() , BookInfo.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打包数据
     * @return
     */
    @Override
    protected JSONObject packData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_RECOMMAND_MSG , recommand_msg);
            jsonObject.put(KEY_BOOK_INFO , new JSONObject(new Gson().toJson(bookInfo)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
