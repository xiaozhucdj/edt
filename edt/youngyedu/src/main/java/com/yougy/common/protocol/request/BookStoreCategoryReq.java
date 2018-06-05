package com.yougy.common.protocol.request;

/**
 * Created by jiangliang on 2018-5-7.
 */

public class BookStoreCategoryReq extends NewBaseReq {

    private int depth = -1;

    private int categoryId = -1;

    private int categoryParent = -1;

    public BookStoreCategoryReq() {
//        m = "queryBookCategory";
        m = "queryBookCategoryPlus";
        address = "bookStore";
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getCategoryParent() {
        return categoryParent;
    }

    public void setCategoryParent(int categoryParent) {
        this.categoryParent = categoryParent;
    }
}

