package com.yougy.shop.bean;

import java.util.List;

public class RemoveRequestObj {
    private Integer userId;
    private List<BookIdObj> data;

    public RemoveRequestObj(Integer userId, List<BookIdObj> data) {
        this.userId = userId;
        this.data = data;
    }
}