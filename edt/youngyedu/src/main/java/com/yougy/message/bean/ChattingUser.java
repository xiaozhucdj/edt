package com.yougy.message.bean;

import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;

/**
 * Created by FH on 2017/7/10.
 */

public class ChattingUser {
    private String id;
    private ArrayList<IMMessage> last20Message = new ArrayList<IMMessage>();

    public String getId() {
        return id;
    }

    public ChattingUser setId(String id) {
        this.id = id;
        return this;
    }

    public ArrayList<IMMessage> getLast20Message() {
        return last20Message;
    }

}
